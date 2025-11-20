package com.zeafen.petwalker.ui.channel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.channel.ChannelDetailsUiEvent
import com.zeafen.petwalker.presentation.channel.ChannelDetailsUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.channel_tab_display_name
import petwalker.composeapp.generated.resources.confirm_delete_message_label
import petwalker.composeapp.generated.resources.ic_arrow_up
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.loading_label
import petwalker.composeapp.generated.resources.max_amount_reached_error_txt
import petwalker.composeapp.generated.resources.success_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailsPage(
    modifier: Modifier = Modifier,
    state: ChannelDetailsUiState,
    onEvent: (ChannelDetailsUiEvent) -> Unit,
    onBackClick: () -> Unit
) {

    val lazyListState =
        rememberLazyListState()
    val scope = rememberCoroutineScope()

    var prevOffset by remember {
        mutableIntStateOf(0)
    }
    var showGoUp by remember {
        mutableStateOf(false)
    }
    var selectedMessageId by remember {
        mutableStateOf<String?>(null)
    }

    var popupContent by remember(state.fileLoadingError) {
        mutableStateOf<StringResource?>(state.fileLoadingError?.infoResource())
    }
    val documentPicker = rememberDocumentPicker { fileInfo ->
        fileInfo?.let {
            onEvent(ChannelDetailsUiEvent.AddAttachment(fileInfo))
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect {
                showGoUp = it < prevOffset
                prevOffset = it
            }
    }

    LaunchedEffect(state.sendingMessageResult) {
        popupContent = when (state.sendingMessageResult) {
            is APIResult.Downloading -> Res.string.loading_label
            is APIResult.Error<*> -> state.sendingMessageResult.info.infoResource()
            is APIResult.Succeed<*> -> Res.string.success_label
            null -> null
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.channel_tab_display_name),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_go_back),
                            contentDescription = "Go back"
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showGoUp,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        onEvent(ChannelDetailsUiEvent.LoadMessages())
                        scope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    },
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_up),
                        contentDescription = "Go to beginning"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .consumeWindowInsets(WindowInsets.systemBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = lazyListState,
                reverseLayout = true
            ) {
                when {
                    state.areMessagesLoading && !state.isLoadingDownwards -> item {
                        CircularProgressIndicator(
                            Modifier
                                .size(80.dp)
                                .padding(top = 24.dp),
                            strokeWidth = 4.dp
                        )
                    }

                    state.messagesLoadingError != null && !state.isLoadingDownwards -> item {
                        ErrorInfoHint(errorInfo = stringResource(state.messagesLoadingError.infoResource())) {
                            onEvent(ChannelDetailsUiEvent.LoadMessages(state.currentMessagesPageComb.first - 1))
                        }
                    }
                }

                items(
                    state.messages.size,
                    key = { index -> state.messages[index].id }
                ) { index ->
                    val message = state.messages[index]
                    if (index >= state.messages.size - 1 && !state.maxMessagesPageReached && !state.areMessagesLoading)
                        onEvent(ChannelDetailsUiEvent.LoadMessages(state.currentMessagesPageComb.second + 1))
                    else if (state.messages[index].id == state.messages.first().id
                        && state.currentMessagesPageComb.first > 1
                        && !state.areMessagesLoading
                    )
                        onEvent(ChannelDetailsUiEvent.LoadMessages(state.currentMessagesPageComb.first - 1))
                    MessageCard(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                            .wrapContentWidth(if (message.isOwn) Alignment.End else Alignment.Start)
                            .fillMaxWidth(0.8f),
                        message = message,
                        onLoadAttachment = { ref, name ->
                            onEvent(ChannelDetailsUiEvent.LoadAttachment(ref, name))
                        },
                        onDeleteMessageClick = { selectedMessageId = message.id },
                        onEditMessageClick = {
                            onEvent(
                                ChannelDetailsUiEvent.SetEditedMessage(
                                    message
                                )
                            )
                        }
                    )
                }

                when {
                    state.areMessagesLoading && state.isLoadingDownwards -> item {
                        CircularProgressIndicator(
                            Modifier
                                .size(80.dp)
                                .padding(top = 24.dp),
                            strokeWidth = 4.dp
                        )
                    }

                    state.messagesLoadingError != null && state.isLoadingDownwards -> item {
                        ErrorInfoHint(errorInfo = stringResource(state.messagesLoadingError.infoResource())) {
                            onEvent(ChannelDetailsUiEvent.LoadMessages(state.currentMessagesPageComb.first - 1))
                        }
                    }
                }
            }
            CommentaryDataInputField(
                inputString = state.messageString,
                onInputChanged = { onEvent(ChannelDetailsUiEvent.SetMessageString(it)) },
                attachments = state.selectedAttachmentUris,
                onRemoveAttachment = { onEvent(ChannelDetailsUiEvent.RemoveAttachment(it)) },
                onAddAttachmentClick = {
                    if (state.canAddAttachment)
                        documentPicker.launch(
                            ExtensionGroups.Documents.exts
                                    + ExtensionGroups.Image.exts
                                    + ExtensionGroups.Audio.exts
                                    + ExtensionGroups.Video.exts
                        )
                    else
                        popupContent = Res.string.max_amount_reached_error_txt
                },
                onSendClick = {
                    if (state.canSend.isValid)
                        onEvent(ChannelDetailsUiEvent.SendMessage)
                    else state.canSend.errorResId?.let {
                        popupContent = state.canSend.errorResId
                    }
                },
                isEditingMessage = state.selectedMessageId != null,
                onCancelEditingClick = { onEvent(ChannelDetailsUiEvent.SetEditedMessage(null)) }
            )

        }
    }

    if (selectedMessageId != null)
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_delete_message_label),
            onConfirm = {
                onEvent(ChannelDetailsUiEvent.DeleteMessage(selectedMessageId!!))
                selectedMessageId = null
            },
            onDismissRequest = { selectedMessageId = null }
        )
    if (popupContent != null)
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { popupContent = null }
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = when {
                    popupContent == state.canSend.errorResId -> stringResource(
                        state.canSend.errorResId!!,
                        *state.canSend.formatArgs.toTypedArray()
                    )

                    else -> stringResource(popupContent!!)
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        }
}