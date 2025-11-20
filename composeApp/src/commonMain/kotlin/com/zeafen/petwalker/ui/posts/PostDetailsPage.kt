package com.zeafen.petwalker.ui.posts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.posts.postDetailsPage.PostDetailsUiEvent
import com.zeafen.petwalker.presentation.posts.postDetailsPage.PostDetailsUiState
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.commentary_input_string
import petwalker.composeapp.generated.resources.empty_label
import petwalker.composeapp.generated.resources.ic_arrow_up
import petwalker.composeapp.generated.resources.ic_attach
import petwalker.composeapp.generated.resources.ic_clear
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_send
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.post_info_page_screen_header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsPage(
    modifier: Modifier = Modifier,
    state: PostDetailsUiState,
    onEvent: (PostDetailsUiEvent) -> Unit,
    onGoToCommentaryRoot: (id: String) -> Unit,
    onBackClick: () -> Unit
) {
    var popupContent by remember(state.fileLoadingResult) {
        mutableStateOf(state.fileLoadingResult?.infoResource())
    }
    val documentPicker =
        rememberDocumentPicker { fileInfo ->
            if (fileInfo != null)
                onEvent(PostDetailsUiEvent.AddAttachment(fileInfo))
        }

    val lazyListState =
        rememberLazyListState()
    val scope = rememberCoroutineScope()

    var prevOffset by remember {
        mutableIntStateOf(0)
    }
    var showGoUp by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect {
                showGoUp = it < prevOffset
                prevOffset = it
            }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.post_info_page_screen_header),
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
                        onEvent(PostDetailsUiEvent.LoadCommentaries())
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
                state = lazyListState
            ) {
                if (state.currentCommentariesPageComb.first == 1)
                    item {
                        when (state.post) {
                            is APIResult.Downloading -> CircularProgressIndicator(
                                Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth()
                                    .fillMaxWidth(0.4f)
                                    .padding(top = 24.dp),
                                strokeWidth = 4.dp
                            )

                            is APIResult.Error -> ErrorInfoHint(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentWidth()
                                    .fillMaxWidth(0.7f),
                                errorInfo = "${
                                    stringResource(state.post.info.infoResource())
                                }: ${state.post.additionalInfo}",
                                onReloadPage = {
                                    state.selectedPostId?.let {
                                        onEvent(
                                            PostDetailsUiEvent.LoadPost(it)
                                        )
                                    }
                                }
                            )

                            is APIResult.Succeed -> {
                                PostCard(
                                    modifier = Modifier
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    post = state.post.data!!,
                                    onCommentsClick = {
                                        if (state.commentaries.size > 1)
                                            scope.launch {
                                                lazyListState.scrollToItem(1)
                                            }
                                    },
                                    onLoadAttachment = { ref, name ->
                                        onEvent(PostDetailsUiEvent.LoadAttachment(ref, name))
                                    }
                                )
                            }
                        }
                    }


                if (state.isCommentariesLoading && !state.isLoadingDownwards)
                    item {
                        CircularProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .fillMaxWidth(0.4f)
                                .padding(top = 24.dp),
                            strokeWidth = 4.dp
                        )
                    }

                items(
                    state.commentaries.size,
                    key = { index -> state.commentaries[index].id }
                ) { index ->
                    val commentary = state.commentaries[index]
                    if (index >= state.commentaries.size - 1 && !state.maxCommentariesPageReached && !state.isCommentariesLoading)
                        onEvent(PostDetailsUiEvent.LoadCommentaries(state.currentCommentariesPageComb.second + 1))
                    else if (state.commentaries[index].id == state.commentaries.first().id
                        && state.currentCommentariesPageComb.first > 1
                        && !state.isCommentariesLoading
                    )
                        onEvent(PostDetailsUiEvent.LoadCommentaries(state.currentCommentariesPageComb.first - 1))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .horizontalScroll(rememberScrollState())
                            .onSideBorder(
                                Color.LightGray,
                                strokeWidth = 12.dp
                            )
                            .padding(start = 12.dp)
                    ) {
                        CommentaryCell(
                            modifier = Modifier
                                .widthIn(max = 360.dp),
                            commentary = commentary,
                            onLoadAttachment = { ref, name ->
                                onEvent(PostDetailsUiEvent.LoadAttachment(ref, name))
                            },
                            onOpenCommentaryRoot = onGoToCommentaryRoot
                        )

                        commentary.childCommentaries.forEach { child ->
                            CommentaryCell(
                                modifier = Modifier
                                    .padding(start = 120.dp, top = 12.dp)
                                    .widthIn(max = 360.dp),
                                commentary = child,
                                onLoadAttachment = { ref, name ->
                                    onEvent(PostDetailsUiEvent.LoadAttachment(ref, name))
                                },
                                onOpenCommentaryRoot = onGoToCommentaryRoot
                            )
                        }
                    }
                }

                if (state.isCommentariesLoading && state.isLoadingDownwards)
                    item {
                        CircularProgressIndicator(
                            Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .fillMaxWidth(0.4f)
                                .padding(top = 24.dp),
                            strokeWidth = 4.dp
                        )
                    }
            }


            AnimatedVisibility(
                visible = state.selectedParentCommentary != null
            ) {
                if (state.selectedParentCommentary != null)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PetWalkerAsyncImage(
                            asyncImageModifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            imageUrl = state.selectedParentCommentary.senderImageUrl
                        )
                        Text(
                            text = state.selectedParentCommentary.text
                                ?: stringResource(Res.string.empty_label),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = { onEvent(PostDetailsUiEvent.SetRespondingCommentary(null)) }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_clear),
                                contentDescription = "Remove responding message"
                            )
                        }
                    }
            }
            CommentaryDataInputField(
                inputString = state.commentString,
                onInputChanged = { onEvent(PostDetailsUiEvent.SetCommentaryString(it)) },
                attachments = state.selectedAttachmentUris,
                onRemoveAttachment = { onEvent(PostDetailsUiEvent.RemoveAttachment(it)) },
                onAddAttachmentClick = {
                    documentPicker.launch(
                        ExtensionGroups.Documents.exts +
                                ExtensionGroups.Image.exts +
                                ExtensionGroups.Audio.exts +
                                ExtensionGroups.Video.exts
                    )
                },
                onSendClick = {
                    if (state.canSend.isValid)
                        onEvent(PostDetailsUiEvent.SendCommentary)
                    else state.canSend.errorResId?.let {
                        popupContent = it
                    }
                },
            )
        }
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
                    text = stringResource(popupContent!!),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
    }
}

@Composable
fun CommentaryDataInputField(
    modifier: Modifier = Modifier,
    inputString: String,
    onInputChanged: (String) -> Unit,
    attachments: List<Attachment>,
    onRemoveAttachment: (ref: String) -> Unit,
    onAddAttachmentClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
        ) {
            items(attachments) { attachment ->
                AttachmentCell(
                    attachment = attachment,
                    onRemoveClick = { onRemoveAttachment(attachment.reference) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 8.dp)
        ) {
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f),
                value = inputString,
                hint = stringResource(Res.string.commentary_input_string),
                onValueChanged = onInputChanged,
                leadingIcon = painterResource(Res.drawable.ic_text)
            )
            IconButton(
                onClick = onAddAttachmentClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_attach),
                    contentDescription = "Attach file"
                )
            }
            FilledIconButton(
                onClick = onSendClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "Attach file"
                )
            }
        }
    }
}