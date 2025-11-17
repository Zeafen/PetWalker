package com.zeafen.petwalker.presentation.posts.postConfigure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.countWords
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PostsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.least_words_count_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PostConfigureViewModel(
    private val postsRepository: PostsRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<PostConfigureUiState> = MutableStateFlow(
        PostConfigureUiState()
    )
    val state: StateFlow<PostConfigureUiState> =
        _state.asStateFlow()

    init {
        _state
            .distinctUntilChangedBy { it.postTitle }
            .onEach { value ->
                _state.update {
                    it.copy(
                        titleValidation = when {
                            value.postTitle.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.postTitle.length > 300 -> ValidationInfo(
                                false,
                                Res.string.incorrect_length_max_error,
                                listOf(300)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        _state.distinctUntilChangedBy { it.attachments.isEmpty() }
            .onEach { value ->
                _state.update {
                    it.copy(textNeeded = value.attachments.isEmpty())
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChanged { old, new ->
                old.textNeeded == new.textNeeded
                        && old.postText == new.postText
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        textValidation = when {
                            value.textNeeded && value.postText.countWords() < 5 -> ValidationInfo(
                                false, Res.string.least_words_count_error_txt, listOf(5)
                            )

                            value.textNeeded && value.postText.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private val inputMutex = Mutex()

    @OptIn(ExperimentalUuidApi::class)
    fun onEvent(event: PostConfigureUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PostConfigureUiEvent.AddAttachment -> {
                    if (event.fileInfo.path != null) {
                        val attachment = if (state.value.selectedPostId != null) {
                            if (state.value.attachmentEditingResult is APIResult.Downloading)
                                return@launch
                            _state.update {
                                it.copy(attachmentEditingResult = APIResult.Downloading())
                            }

                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(attachmentEditingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            val result = postsRepository.postAttachments(
                                state.value.selectedPostId!!,
                                listOf(event.fileInfo)
                            )
                            _state.update {
                                it.copy(
                                    attachmentEditingResult = if (result is APIResult.Error) APIResult.Error(
                                        result.info
                                    )
                                    else APIResult.Succeed()
                                )
                            }
                            if (result is APIResult.Succeed)
                                result.data?.firstOrNull()
                            else null
                        } else Attachment(
                            Uuid.random().toString(),
                            when {
                                event.fileInfo.mediaType.contains("image") -> AttachmentType.Image
                                event.fileInfo.mediaType.contains("video") -> AttachmentType.Video
                                event.fileInfo.mediaType.contains("audio") -> AttachmentType.Audio
                                else -> AttachmentType.Document
                            },
                            event.fileInfo.displayName,
                            event.fileInfo.path
                        )
                        attachment?.let { added ->

                            val newList = (state.value.attachments + attachment).distinct()
                            val newFiles =
                                (state.value.attachmentFiles + (attachment.id to event.fileInfo)).filterKeys { it in newList.map { it.id } }
                            _state.update {
                                if (state.value.selectedPostId != null)
                                    it.copy(attachments = newList)
                                else
                                    it.copy(
                                        attachments = newList,
                                        attachmentFiles = newFiles
                                    )
                            }
                        }
                    }

                }

                PostConfigureUiEvent.PublishData -> {
                    if (state.value.postLoadingResult is APIResult.Downloading)
                        return@launch
                    inputMutex.withLock {
                        _state.update {
                            it.copy(postLoadingResult = APIResult.Downloading())
                        }
                    }

                    if (!state.value.canPublish) {
                        _state.update {
                            it.copy(postLoadingResult = APIResult.Error(NetworkError.CONFLICT))
                        }
                        return@launch
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(postLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val selectedPostId = state.value.selectedPostId

                    when {
                        selectedPostId != null -> {
                            val result = postsRepository.updatePost(
                                selectedPostId,
                                state.value.postTitle,
                                state.value.postText,
                                state.value.postType!!
                            )

                            _state.update {
                                it.copy(postLoadingResult = result)
                            }
                        }

                        state.value.selectedPostId == null -> {
                            val result = postsRepository.postPost(
                                state.value.postType!!,
                                state.value.postTitle,
                                state.value.postText,
                                state.value.attachments.mapNotNull {
                                    state.value.attachmentFiles[it.id]
                                }
                            )

                            if (result is APIResult.Error)
                                _state.update {
                                    it.copy(
                                        postLoadingResult = APIResult.Error(result.info),
                                        selectedPostId = null
                                    )
                                }
                            else
                                _state.update {
                                    it.copy(
                                        postLoadingResult = APIResult.Succeed(),
                                        selectedPostId = (result as APIResult.Succeed).data?.id
                                    )
                                }
                        }
                    }
                }

                is PostConfigureUiEvent.RemoveAttachment -> {
                    state.value.attachments.firstOrNull { it.id == event.id }?.let { removed ->
                        val postId = state.value.selectedPostId
                        if (postId != null) {
                            _state.update { it.copy(attachmentEditingResult = APIResult.Downloading()) }

                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(attachmentEditingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            val result = postsRepository.deleteAttachment(
                                postId,
                                event.id
                            )
                            _state.update {
                                it.copy(attachmentEditingResult = APIResult.Succeed())
                            }
                            if (result is APIResult.Error)
                                return@launch
                        }

                        _state.update {
                            it.copy(
                                attachments = it.attachments - removed
                            )
                        }
                    }
                }

                is PostConfigureUiEvent.SetPostText -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(postText = event.text)
                        }
                    }
                }

                is PostConfigureUiEvent.SetPostTitle -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(postTitle = event.title)
                        }
                    }
                }

                is PostConfigureUiEvent.SetPostType -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(postType = event.type)
                        }
                    }
                }

                is PostConfigureUiEvent.SetSelectedPostId -> {
                    _state.update {
                        it.copy(
                            postLoadingResult = APIResult.Downloading(),
                            selectedPostId = event.id
                        )
                    }

                    if (event.id != null) {
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(postLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val post = postsRepository.getPost(event.id)
                        if (post is APIResult.Error) {
                            _state.update {
                                it.copy(postLoadingResult = APIResult.Error(post.info))
                            }
                            return@launch
                        }

                        inputMutex.withLock {
                            _state.update {
                                it.copy(
                                    postLoadingResult = APIResult.Succeed(),
                                    postTitle = (post as APIResult.Succeed).data!!.topic,
                                    postText = post.data!!.body ?: "",
                                    postType = post.data.type,
                                    attachments = post.data.attachments,
                                )
                            }
                        }

                    } else _state.update {
                        it.copy(
                            postLoadingResult = APIResult.Succeed(),
                            postTitle = "",
                            postText = "",
                            postType = null,
                            attachments = emptyList()
                        )
                    }
                }

                is PostConfigureUiEvent.SetSelectedTab -> {
                    _state.update {
                        it.copy(
                            selectedTabIndex =
                                if (event.index in PostConfigureTabs.entries.indices)
                                    event.index
                                else 0
                        )
                    }
                }

                PostConfigureUiEvent.ClearResult -> {
                    inputMutex.withLock {

                        if (state.value.postLoadingResult is APIResult.Downloading) {
                            return@launch
                        }
                        _state.update {
                            it.copy(
                                postLoadingResult = null
                            )
                        }
                    }
                }
            }
        }
    }
}