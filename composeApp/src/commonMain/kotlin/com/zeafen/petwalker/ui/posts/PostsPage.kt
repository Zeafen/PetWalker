package com.zeafen.petwalker.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.domain.models.api.filtering.PostOrdering
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.posts.postsList.PostsPageUiEvent
import com.zeafen.petwalker.presentation.posts.postsList.PostsPageUiState
import com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar.rememberTwoLayerTopAppBarScrollBehavior
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerOrderingTab
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.posts_page_header
import petwalker.composeapp.generated.resources.topic_input_hint
import petwalker.composeapp.generated.resources.topic_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsPage(
    modifier: Modifier = Modifier,
    state: PostsPageUiState,
    onEvent: (PostsPageUiEvent) -> Unit,
    onAddPostClick: () -> Unit,
    onGoToPostClick: (id: String) -> Unit,
) {
    var popupContent = remember(state.fileLoadingError) {
        state.fileLoadingError?.infoResource()
    }
    val scrollBehavior = rememberTwoLayerTopAppBarScrollBehavior()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(Res.string.posts_page_header),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = onAddPostClick
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add post"
                        )
                    }
                },
                additionalContent = {
                    Column {
                        PetWalkerTextInput(
                            value = state.searchTopic,
                            onValueChanged = { onEvent(PostsPageUiEvent.SetSearchTopic(it)) },
                            label = stringResource(Res.string.topic_label),
                            hint = stringResource(Res.string.topic_input_hint),
                            leadingIcon = painterResource(Res.drawable.ic_search)
                        )
                        Spacer(Modifier.height(8.dp))
                        PetWalkerOrderingTab(
                            availableOrderings = PostOrdering.entries.toList(),
                            selectedOrdering = state.ordering,
                            ascending = state.ascending,
                            onOrderingChanged = { onEvent(PostsPageUiEvent.SetOrdering(it)) },
                            orderingLabel = { stringResource(it.displayName) }
                        )
                    }
                },
                scrollBehaviour = scrollBehavior
            )
        },
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
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            when (state.posts) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.4f),
                    strokeWidth = 4.dp
                )

                is APIResult.Error -> ErrorInfoHint(
                    errorInfo = "${
                        stringResource(state.posts.info.infoResource())
                    }: ${state.posts.additionalInfo}",
                    onReloadPage = { onEvent(PostsPageUiEvent.LoadPosts()) }
                )

                is APIResult.Succeed -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(
                            items = state.posts.data!!.result,
                            key = { walker -> walker.id }
                        ) { post ->
                            PostCard(
                                modifier = Modifier.padding(vertical = 12.dp),
                                post = post,
                                onGoToPostClick = onGoToPostClick,
                                onLoadAttachment = { ref, name ->
                                    onEvent(
                                        PostsPageUiEvent.LoadAttachment(
                                            ref,
                                            name
                                        )
                                    )
                                },
                            )
                        }
                        item {
                            PageSelectionRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                totalPages = state.posts.data.totalPages,
                                currentPage = state.posts.data.currentPage,
                                onPageClick = { onEvent(PostsPageUiEvent.LoadPosts(it)) }
                            )
                        }
                    }
                }
            }
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