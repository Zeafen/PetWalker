package com.zeafen.petwalker.ui.posts.postConfigure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureTabs
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureUiEvent
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureUiState
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_send
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.post_info_page_screen_header
import petwalker.composeapp.generated.resources.type_label

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostConfigurePage(
    modifier: Modifier = Modifier,
    state: PostConfigureUiState,
    onEvent: (PostConfigureUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val documentPicker =
        rememberDocumentPicker { fileInfo ->
            if (fileInfo != null)
                onEvent(PostConfigureUiEvent.AddAttachment(fileInfo))
        }
    val pagerState = rememberPagerState {
        PostConfigureTabs.entries.size
    }
    val imageAttachments = remember(state.attachments) {
        state.attachments.filter { it.type == AttachmentType.Image }
    }
    val attachments = remember(state.attachments) {
        state.attachments.filter { it.type != AttachmentType.Image }
    }
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }


    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            onEvent(PostConfigureUiEvent.SetSelectedTab(pagerState.currentPage))
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
                actions = {
                    IconButton(
                        onClick = {
                            if (state.canPublish)
                                onEvent(PostConfigureUiEvent.PublishData)
                            else {
                                popupContent = Res.string.conflict_error
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = "Go back"
                        )
                    }
                }
            )
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
                .verticalScroll(rememberScrollState())
        ) {
            ScrollableTabRow(
                modifier = Modifier
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedTabIndex = state.selectedTabIndex,
                tabs = {
                    PostConfigureTabs.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == state.selectedTabIndex,
                            text = {
                                Text(
                                    text = stringResource(tab.displayName),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = { onEvent(PostConfigureUiEvent.SetSelectedTab(index)) },
                        )
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            OptionsSelectedInput(
                selectedOptions = state.postType?.let { listOf(it) } ?: emptyList(),
                availableOptions = PostType.entries.toList(),
                label = stringResource(Res.string.type_label),
                hint = stringResource(Res.string.option_selection_hint),
                onOptionSelected = { onEvent(PostConfigureUiEvent.SetPostType(it)) },
                onOptionDeleted = { onEvent(PostConfigureUiEvent.SetPostType(null)) },
                optionContent = {
                    Text(
                        text = stringResource(it.displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )

            Spacer(Modifier.height(24.dp))
            HorizontalPager(
                state = pagerState
            ) { index ->
                when (PostConfigureTabs.entries[index]) {
                    PostConfigureTabs.InfoInput -> {
                        PostInfoConfigureTab(
                            modifier = Modifier
                                .padding(4.dp),
                            postTitle = state.postTitle,
                            titleValidation = state.titleValidation,
                            onTitleChanged = { onEvent(PostConfigureUiEvent.SetPostTitle(it)) },
                            postText = state.postText,
                            textValidation = state.textValidation,
                            onTextChanged = { onEvent(PostConfigureUiEvent.SetPostText(it)) }
                        )
                    }

                    PostConfigureTabs.Images -> {
                        PostAttachmentsConfigureTab(
                            modifier = Modifier
                                .heightIn(max = 400.dp)
                                .padding(4.dp),
                            images = imageAttachments,
                            onAttachmentRemoved = { onEvent(PostConfigureUiEvent.RemoveAttachment(it)) },
                            onAddAttachmentClick = { documentPicker.launch(ExtensionGroups.Image.exts) }
                        )
                    }

                    PostConfigureTabs.Attachments -> {
                        PostAttachmentsConfigureTab(
                            modifier = Modifier
                                .heightIn(max = 400.dp)
                                .padding(4.dp),
                            images = attachments,
                            onAttachmentRemoved = { onEvent(PostConfigureUiEvent.RemoveAttachment(it)) },
                            onAddAttachmentClick = {
                                documentPicker.launch(
                                    ExtensionGroups.Documents.exts +
                                            ExtensionGroups.Image.exts +
                                            ExtensionGroups.Audio.exts +
                                            ExtensionGroups.Video.exts
                                )
                            }
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                thickness = 4.dp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
            ) {
                PetWalkerButton(
                    text = stringResource(Res.string.cancel_btn_txt),
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = onBackClick
                )
                Spacer(Modifier.width(16.dp))
                PetWalkerButton(
                    text = stringResource(Res.string.done_btn_txt),
                    onClick = { onEvent(PostConfigureUiEvent.PublishData) }
                )
            }
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