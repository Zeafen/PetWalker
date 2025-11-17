package com.zeafen.petwalker.presentation.posts.postDetailsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.posts.Commentary
import com.zeafen.petwalker.domain.models.api.posts.Post
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.CommentaryModel
import com.zeafen.petwalker.domain.models.ui.PostModel
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PostsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.error_info_content
import petwalker.composeapp.generated.resources.loading_error_txt
import petwalker.composeapp.generated.resources.success_label
import petwalker.composeapp.generated.resources.unauthorized_error_txt

class PostDetailsViewModel(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val downloadManager: PetWalkerDownloadManager,
) : ViewModel() {

    private val _state: MutableStateFlow<PostDetailsUiState> =
        MutableStateFlow(PostDetailsUiState())

    val state: StateFlow<PostDetailsUiState> =
        _state.asStateFlow()

    init {
        _state
            .distinctUntilChanged { old, new ->
                old.selectedAttachmentUris.size == new.selectedAttachmentUris.size
                        && old.commentString == new.commentString
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canSend =
                            if (value.selectedAttachmentUris.isNotEmpty() || value.commentString.isNotBlank())
                                ValidationInfo(true, null, emptyList())
                            else ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChanged { old, new ->
                old.currentCommentariesPageComb == new.currentCommentariesPageComb
                        && old.maxCommentariesPages == new.maxCommentariesPages
            }
            .onEach { value ->
                _state.update {
                    it.copy(maxCommentariesPageReached = value.maxCommentariesPages == value.currentCommentariesPageComb.second)
                }
            }
            .launchIn(viewModelScope)
    }

    private val inputMutex = Mutex()
    private var commentariesLoadingJob: Job? = null
    private var sendingCommentaryJob: Job? = null
    fun onEvent(event: PostDetailsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PostDetailsUiEvent.AddAttachment -> {

                    val attachment = Attachment(
                        "",
                        AttachmentType.Document,
                        event.fileInfo.displayName,
                        event.fileInfo.path.toString()
                    )


                    val newList = (state.value.selectedAttachmentUris + attachment).distinct()
                    val newFiles =
                        (state.value.attachmentFiles + (attachment.id to event.fileInfo)).filterKeys { it in newList.map { it.id } }
                    _state.update {
                        it.copy(
                            selectedAttachmentUris = newList,
                            attachmentFiles = newFiles
                        )
                    }
                }

                is PostDetailsUiEvent.LoadAttachment -> {
                    _state.update {
                        it.copy(
                            fileLoadingResult =
                                downloadManager.queryDownload(event.ref, event.name)
                        )
                    }
                }

                is PostDetailsUiEvent.LoadCommentaries -> {
                    if (commentariesLoadingJob?.isActive == true)
                        commentariesLoadingJob?.cancel()

                    commentariesLoadingJob = launch {
                        val currentComb = when {
                            event.page < state.value.currentCommentariesPageComb.first - 1 ->
                                event.page.coerceAtLeast(1) to (event.page + 1).coerceAtLeast(1)

                            event.page == state.value.currentCommentariesPageComb.first - 1 ->
                                event.page to state.value.currentCommentariesPageComb.first

                            event.page == state.value.currentCommentariesPageComb.second + 1 ->
                                state.value.currentCommentariesPageComb.second to event.page

                            event.page > state.value.currentCommentariesPageComb.second + 1 ->
                                (event.page - 1).coerceAtLeast(1) to event.page.coerceAtLeast(1)

                            else -> 1 to 1
                        }
                        _state.update {
                            it.copy(
                                isCommentariesLoading = true,
                                isLoadingDownwards = it.currentCommentariesPageComb.second == currentComb.first,
                                commentariesLoadingError = null
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    commentariesLoadingError = NetworkError.UNAUTHORIZED,
                                    isCommentariesLoading = false
                                )
                            }
                            return@launch
                        }

                        val selectedPostId = state.value.selectedPostId
                        if (selectedPostId == null) {
                            _state.update {
                                it.copy(
                                    commentariesLoadingError = NetworkError.NOT_FOUND,
                                    isCommentariesLoading = false
                                )
                            }
                            return@launch
                        }

                        val commentaries = postsRepository.getPostCommentaries(
                            selectedPostId!!,
                            event.page,
                            15
                        )
                        if (commentaries is APIResult.Error) {
                            _state.update {
                                it.copy(
                                    commentariesLoadingError = commentaries.info,
                                    isCommentariesLoading = false
                                )
                            }
                            return@launch
                        }

                        val models = (commentaries as APIResult.Succeed).data!!.result.map {
                            async {
                                val childNodes = it.childCommentaries.map { child ->
                                    async { getModelForCommentary(child) }
                                }
                                getModelForCommentary(it).copy(
                                    childCommentaries = childNodes.awaitAll()
                                )
                            }
                        }
                        val maxPages = commentaries.data!!.totalPages

                        val newCommentaries = when {
                            currentComb.second == state.value.currentCommentariesPageComb.first ->
                                models.awaitAll() + state.value.commentaries.take(15)

                            currentComb.first == state.value.currentCommentariesPageComb.second ->
                                state.value.commentaries.takeLast(15) + models.awaitAll()

                            else -> models.awaitAll()
                        }
                        _state.update {
                            it.copy(
                                currentCommentariesPageComb = currentComb,
                                commentaries = newCommentaries,
                                isCommentariesLoading = false,
                                maxCommentariesPages = maxPages
                            )
                        }
                    }
                }

                is PostDetailsUiEvent.LoadPost -> {
                    _state.update {
                        it.copy(
                            post = APIResult.Downloading(),
                            selectedPostId = event.id
                        )
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(post = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val post = postsRepository.getPost(event.id)
                    if (post is APIResult.Error) {
                        _state.update {
                            it.copy(post = APIResult.Error(post.info))
                        }
                        return@launch
                    }

                    val model =
                        getModelForPost((post as APIResult.Succeed).data!!)
                    _state.update {
                        it.copy(post = APIResult.Succeed(model))
                    }

                    onEvent(PostDetailsUiEvent.LoadCommentaries())
                }

                is PostDetailsUiEvent.RemoveAttachment -> {
                    val minusItem =
                        state.value.selectedAttachmentUris.firstOrNull { attachment -> attachment.reference == event.ref }
                    _state.update { state ->
                        state.copy(
                            selectedAttachmentUris = minusItem?.let {
                                state.selectedAttachmentUris.minus(it)
                            } ?: state.selectedAttachmentUris
                        )
                    }
                }

                PostDetailsUiEvent.SendCommentary -> {
                    inputMutex.withLock {
                        if (sendingCommentaryJob?.isActive != true)
                            sendingCommentaryJob = launch {
                                if (!state.value.canSend.isValid)
                                    return@launch

                                val token = authDataStore.authDataStoreFlow.first().token
                                if (token == null || token.accessToken.isBlank()) {
                                    _state.update {
                                        it.copy(
                                            sendingCommentaryResult = ValidationInfo(
                                                false,
                                                Res.string.unauthorized_error_txt,
                                                emptyList()
                                            )
                                        )
                                    }
                                    return@launch
                                }

                                val selectedPostId = state.value.selectedPostId
                                if (selectedPostId == null) {
                                    _state.update {
                                        it.copy(
                                            sendingCommentaryResult = ValidationInfo(
                                                false,
                                                Res.string.loading_error_txt,
                                                emptyList()
                                            )
                                        )
                                    }
                                    return@launch
                                }

                                val files = state.value.selectedAttachmentUris.mapNotNull {
                                    state.value.attachmentFiles[it.id]
                                }


                                val result = state.value.selectedParentCommentary?.let {
                                    postsRepository.postCommentaryResponse(
                                        selectedPostId!!,
                                        it.id,
                                        state.value.commentString,
                                        files
                                    )
                                }
                                    ?: postsRepository.postCommentary(
                                        selectedPostId!!,
                                        state.value.commentString,
                                        files
                                    )
                                _state.update {
                                    it.copy(
                                        sendingCommentaryResult =
                                            if (result is APIResult.Error)
                                                ValidationInfo(
                                                    false,
                                                    Res.string.error_info_content,
                                                    listOf(result.info)
                                                )
                                            else ValidationInfo(
                                                true,
                                                Res.string.success_label,
                                                emptyList()
                                            )
                                    )
                                }
                            }
                    }
                }

                is PostDetailsUiEvent.SetCommentaryString -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(commentString = event.string)
                        }
                    }
                }

                is PostDetailsUiEvent.SetRespondingCommentary -> {
                    _state.update {
                        it.copy(selectedParentCommentary = event.commentary)
                    }
                }
            }
        }
    }

    private suspend fun getModelForCommentary(
        commentary: Commentary
    ): CommentaryModel {
        val senderRes = usersRepository.getWalker(commentary.userId)
        val sender = if (senderRes is APIResult.Succeed) senderRes.data else null
        return CommentaryModel(
            commentary.id,
            commentary.postId,
            sender?.let { "${it.firstName} ${it.lastName}" } ?: "",
            sender?.imageUrl,
            commentary.body,
            commentary.attachments,
            commentary.dateSent,
            commentary.dateEdited,
            commentary.childCommentariesCount,
            emptyList()
        )
    }

    private suspend fun getModelForPost(
        post: Post
    ): PostModel? {
        val senderRes = usersRepository.getWalker(post.userId)
        val sender = (if (senderRes is APIResult.Succeed) senderRes.data else null) ?: return null
        return PostModel(
            post.id,
            "${sender.firstName} ${sender.lastName}",
            sender.imageUrl,
            post.topic,
            post.type,
            post.body,
            post.attachments,
            post.commentsCount,
            post.dateSent,
            post.dateEdited
        )
    }
}