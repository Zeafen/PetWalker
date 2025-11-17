package com.zeafen.petwalker.presentation.posts.postsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.posts.Post
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PostsPageViewModel(
    private val postsRepository: PostsRepository,
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val downloadManager: PetWalkerDownloadManager
) : ViewModel() {

    private val _state: MutableStateFlow<PostsPageUiState> = MutableStateFlow(PostsPageUiState())
    val state: StateFlow<PostsPageUiState> =
        _state.asStateFlow()

    init {
        onEvent(PostsPageUiEvent.LoadPosts())
    }

    val inputMutex = Mutex()
    var postsLoadingJob: Job? = null
    fun onEvent(event: PostsPageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PostsPageUiEvent.LoadAttachment -> {
                    _state.update {
                        it.copy(
                            fileLoadingError =
                                downloadManager.queryDownload(event.ref, event.name)
                        )
                    }
                }

                is PostsPageUiEvent.LoadPosts -> {
                    if (postsLoadingJob?.isActive == true)
                        postsLoadingJob?.cancel()

                    postsLoadingJob = launch {
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(posts = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val posts = postsRepository.getPosts(
                            event.page,
                            15,
                            state.value.searchTopic,
                            state.value.selectedType,
                            state.value.selectedPeriod,
                            state.value.ordering,
                            state.value.ordering?.let { state.value.ascending }
                        )

                        if (posts is APIResult.Error) {
                            _state.update {
                                it.copy(posts = APIResult.Error(posts.info))
                            }
                            return@launch
                        }

                        val models = (posts as APIResult.Succeed).data!!.result.map {
                            async {
                                getModelForPost(it)
                            }
                        }
                        _state.update {
                            it.copy(
                                posts = APIResult.Succeed(
                                    PagedResult(
                                        models.awaitAll().mapNotNull { it },
                                        posts.data!!.currentPage,
                                        posts.data.totalPages,
                                        posts.data.pageSize
                                    )
                                )
                            )
                        }
                    }
                }

                is PostsPageUiEvent.SetFilters -> {
                    _state.update {
                        it.copy(
                            selectedType = event.type,
                            selectedPeriod = event.period
                        )
                    }
                    onEvent(PostsPageUiEvent.LoadPosts())
                }

                is PostsPageUiEvent.SetOrdering -> {
                    _state.update {
                        it.copy(
                            ascending = if (it.ordering == event.ordering) !it.ascending else true,
                            ordering = event.ordering
                        )
                    }
                }

                is PostsPageUiEvent.SetSearchTopic -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(searchTopic = event.topic)
                        }
                    }
                    onEvent(PostsPageUiEvent.LoadPosts())
                }
            }
        }
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

