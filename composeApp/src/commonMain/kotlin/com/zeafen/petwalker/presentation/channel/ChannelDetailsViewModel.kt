package com.zeafen.petwalker.presentation.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ChannelsRepository
import kotlinx.coroutines.Job
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
import petwalker.composeapp.generated.resources.closed_label
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ChannelDetailsViewModel(
    private val channelsRepository: ChannelsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val downloadManager: PetWalkerDownloadManager
) : ViewModel() {

    private val _state: MutableStateFlow<ChannelDetailsUiState> =
        MutableStateFlow(ChannelDetailsUiState())
    val state: StateFlow<ChannelDetailsUiState> =
        _state.asStateFlow()

    init {
        _state
            .distinctUntilChanged { old, new ->
                old.selectedAttachmentUris.size == new.selectedAttachmentUris.size
                        && old.messageString == new.messageString
                        && old.channel == new.channel
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canSend =
                            if (value.selectedAttachmentUris.isNotEmpty() || value.messageString.isNotBlank())
                                ValidationInfo(true, null, emptyList())
                            else if (value.channel is APIResult.Succeed && value.channel.data?.isClosed == true)
                                ValidationInfo(false, Res.string.closed_label, emptyList())
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
                old.currentMessagesPageComb == new.currentMessagesPageComb
                        && old.maxMessagesPages == new.maxMessagesPages
            }
            .onEach { value ->
                _state.update {
                    it.copy(maxMessagesPageReached = value.maxMessagesPages == value.currentMessagesPageComb.second)
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChangedBy { it.selectedAttachmentUris.size }
            .onEach { value ->
                _state.update {
                    it.copy(canAddAttachment = value.selectedAttachmentUris.size < 5)
                }
            }
            .launchIn(viewModelScope)
    }

    private val inputMutex = Mutex()
    private var messagesLoadingJob: Job? = null
    private var sendingMessageJob: Job? = null
    fun onEvent(event: ChannelDetailsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ChannelDetailsUiEvent.AddAttachment -> {
                    if (!state.value.canAddAttachment)
                        return@launch
                    val attachment = Attachment(
                        Uuid.random().toString(),
                        AttachmentType.Document,
                        event.fileInfo.displayName,
                        event.fileInfo.toString()
                    )
                    val newList = (state.value.selectedAttachmentUris + attachment).distinct()
                    val newFiles =
                        (state.value.selectedAttachmentFiles + (attachment.id to event.fileInfo)).filterKeys { it in newList.map { it.id } }
                    _state.update {
                        it.copy(
                            selectedAttachmentUris = newList,
                            selectedAttachmentFiles = newFiles
                        )
                    }
                }

                is ChannelDetailsUiEvent.LoadAttachment -> {
                    _state.update {
                        it.copy(
                            fileLoadingError =
                                downloadManager.queryDownload(event.ref, event.name)
                        )
                    }
                }

                is ChannelDetailsUiEvent.LoadChannel -> {
                    _state.update {
                        it.copy(
                            channel = APIResult.Downloading(),
                            selectedAssignmentId = event.assignmentId
                        )
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(channel = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val channel = channelsRepository.getAssignmentChannel(
                        event.assignmentId
                    )
                    _state.update {
                        it.copy(
                            channel = channel,
                            selectedChannelId = if (channel is APIResult.Succeed) channel.data?.id else null
                        )
                    }
                    onEvent(ChannelDetailsUiEvent.LoadMessages())
                }

                is ChannelDetailsUiEvent.LoadMessages -> {
                    if (messagesLoadingJob?.isActive == true)
                        messagesLoadingJob?.cancel()
                    messagesLoadingJob = launch {
                        val currentComb = when {
                            event.page < state.value.currentMessagesPageComb.first - 1 ->
                                event.page.coerceAtLeast(1) to (event.page + 1).coerceAtLeast(1)

                            event.page == state.value.currentMessagesPageComb.first - 1 ->
                                event.page to state.value.currentMessagesPageComb.first

                            event.page == state.value.currentMessagesPageComb.second + 1 ->
                                state.value.currentMessagesPageComb.second to event.page

                            event.page > state.value.currentMessagesPageComb.second + 1 ->
                                (event.page - 1).coerceAtLeast(1) to event.page.coerceAtLeast(1)

                            else -> 1 to 1
                        }
                        _state.update {
                            it.copy(
                                isMessagesLoading = true,
                                isLoadingDownwards = it.currentMessagesPageComb.second == currentComb.first,
                                messagesLoadingError = null
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(
                                    messagesLoadingError = NetworkError.UNAUTHORIZED,
                                    isMessagesLoading = false
                                )
                            }
                        }

                        val selectedChannelId = state.value.selectedChannelId
                        if (selectedChannelId == null) {
                            _state.update {
                                it.copy(
                                    messagesLoadingError = NetworkError.NOT_FOUND,
                                    isMessagesLoading = false
                                )
                            }
                            return@launch
                        }

                        val messages = channelsRepository.getChannelMessages(
                            selectedChannelId!!,
                            event.page,
                            15
                        )
                        if (messages is APIResult.Error) {
                            _state.update {
                                it.copy(
                                    isMessagesLoading = false,
                                    messagesLoadingError = messages.info
                                )
                            }
                            return@launch
                        } else if (messages is APIResult.Succeed) {
                            val maxPages = messages.data!!.totalPages
                            val newMessages = when {
                                currentComb.second == state.value.currentMessagesPageComb.first ->
                                    messages.data.result + state.value.messages.take(15)

                                currentComb.first == state.value.currentMessagesPageComb.second ->
                                    state.value.messages.takeLast(15) + messages.data.result

                                else -> messages.data.result
                            }

                            _state.update {
                                it.copy(
                                    currentMessagesPageComb = currentComb,
                                    messages = newMessages,
                                    isLoadingDownwards = false,
                                    isMessagesLoading = false,
                                    maxMessagesPages = maxPages
                                )
                            }
                        }
                    }
                }

                is ChannelDetailsUiEvent.RemoveAttachment -> {
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

                ChannelDetailsUiEvent.SendMessage -> {
                    inputMutex.withLock {
                        if (sendingMessageJob?.isActive != true)
                            sendingMessageJob = launch {
                                if (!state.value.canSend.isValid)
                                    return@launch

                                val token = authDataStore.authDataStoreFlow.first().token
                                if (token == null) {
                                    _state.update {
                                        it.copy(
                                            sendingMessageResult = APIResult.Error(NetworkError.NOT_FOUND)
                                        )
                                    }
                                    return@launch
                                }

                                val selectedChannelId = state.value.selectedChannelId
                                if (selectedChannelId == null) {
                                    _state.update {
                                        it.copy(
                                            sendingMessageResult = APIResult.Error(NetworkError.NOT_FOUND)
                                        )
                                    }
                                    return@launch
                                }


                                val result = channelsRepository.postMessage(
                                    selectedChannelId,
                                    state.value.messageString,
                                    state.value.selectedAttachmentFiles.mapNotNull { it.value })

                                _state.update {
                                    it.copy(
                                        sendingMessageResult = result
                                    )
                                }
                            }
                    }
                }

                is ChannelDetailsUiEvent.SetMessageString -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(messageString = event.string)
                        }
                    }
                }
            }
        }
    }
}