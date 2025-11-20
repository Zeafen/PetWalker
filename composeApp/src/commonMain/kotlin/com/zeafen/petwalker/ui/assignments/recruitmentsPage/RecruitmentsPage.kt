package com.zeafen.petwalker.ui.assignments.recruitmentsPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.RecruitmentModel
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsDirection
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsPageUiEvent
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsPageUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.confirm_delete_pet_label
import petwalker.composeapp.generated.resources.ic_filter
import petwalker.composeapp.generated.resources.loading_label
import petwalker.composeapp.generated.resources.recruitments_page_header
import petwalker.composeapp.generated.resources.success_label
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun RecruitmentsPage(
    modifier: Modifier = Modifier,
    state: RecruitmentsPageUiState,
    onSeeWalkerClick: (String) -> Unit,
    onSeeAssignmentClick: (String) -> Unit,
    onEvent: (RecruitmentsPageUiEvent) -> Unit
) {
    var openFiltersDialog by remember {
        mutableStateOf(false)
    }
    var selectedRecId by remember {
        mutableStateOf<String?>(null)
    }
    var openConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }
    val pagerState = rememberPagerState {
        RecruitmentsDirection.entries.size
    }
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }

    LaunchedEffect(state.recruitmentsDirectionIndex) {
        pagerState.animateScrollToPage(state.recruitmentsDirectionIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            onEvent(RecruitmentsPageUiEvent.SetLoadType(pagerState.currentPage == 0))
    }
    LaunchedEffect(state.recruitmentRequestResult) {
        popupContent = when (state.recruitmentRequestResult) {
            is APIResult.Downloading -> Res.string.loading_label
            is APIResult.Error<*> -> state.recruitmentRequestResult.info.infoResource()
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
                        stringResource(Res.string.recruitments_page_header),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = { openFiltersDialog = true }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_filter),
                            contentDescription = "Open filters"
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
        ) {
            ScrollableTabRow(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                selectedTabIndex = state.recruitmentsDirectionIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                RecruitmentsDirection.entries.forEachIndexed { index, recruitmentsDirection ->
                    Tab(
                        selected = index == state.recruitmentsDirectionIndex,
                        text = {
                            Text(
                                text = stringResource(recruitmentsDirection.displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = { onEvent(RecruitmentsPageUiEvent.SetLoadType(index == 0)) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            Spacer(Modifier.height(12.dp))
            HorizontalPager(
                state = pagerState,
            ) { page ->
                PullToRefreshBox(
                    modifier = Modifier
                        .padding(4.dp),
                    isRefreshing = if (page == 0)
                        state.incomingRecruitments is APIResult.Downloading
                    else state.outcomingRecruitments is APIResult.Downloading,
                    onRefresh = {
                        onEvent(RecruitmentsPageUiEvent.LoadOwnRecruitments(outComing = page == 1))
                    }
                ) {
                    RecruitmentsList(
                        recruitments = if (page == 0) state.incomingRecruitments else state.outcomingRecruitments,
                        onLoadPage = {
                            onEvent(
                                if (page == 0) RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                    it,
                                    false
                                )
                                else RecruitmentsPageUiEvent.LoadOwnRecruitments(it, true)
                            )
                        },
                        onDeclineClick = { onEvent(RecruitmentsPageUiEvent.DeclineRecruitment(it)) },
                        onAcceptClick = { onEvent(RecruitmentsPageUiEvent.AcceptsRecruitment(it)) },
                        onSeeWalkerClick = onSeeWalkerClick,
                        onSeeAssignmentClick = onSeeAssignmentClick,
                        onDeleteRecruitment = { id ->
                            selectedRecId = id
                            openConfirmDeleteDialog = true
                        },
                        lastSelectedPage = if (page == 0)
                            state.lastSelectedIncomingPage
                        else state.lastSelectedOutcomingPage
                    )
                }
            }

        }
    }
    if (openFiltersDialog)
        RecruitmentsFilteringDialog(
            onDismissRequest = { openFiltersDialog = false },
            onClearClick = { onEvent(RecruitmentsPageUiEvent.SetFilters()) },
            onDoneClick = { group, state, from, until ->
                onEvent(
                    RecruitmentsPageUiEvent.SetFilters(
                        group,
                        state,
                        from?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        },
                        until?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        }
                    )
                )
            }
        )
    if (openConfirmDeleteDialog && selectedRecId != null)
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_delete_pet_label),
            onConfirm = {
                onEvent(RecruitmentsPageUiEvent.DeleteRecruitment(selectedRecId!!))
                openConfirmDeleteDialog = false
                selectedRecId = null
            },
            onDismissRequest = {
                openConfirmDeleteDialog = false
                selectedRecId = null
            }
        )
    if (popupContent != null)
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { popupContent = null },
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = stringResource(popupContent!!),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
}

@Composable
fun RecruitmentsList(
    modifier: Modifier = Modifier,
    recruitments: APIResult<PagedResult<RecruitmentModel>, Error>,
    onLoadPage: (page: Int) -> Unit,
    lastSelectedPage: Int,
    onSeeWalkerClick: (String) -> Unit,
    onSeeAssignmentClick: (String) -> Unit,
    onAcceptClick: (String) -> Unit,
    onDeclineClick: (String) -> Unit,
    onDeleteRecruitment: (id: String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        when (recruitments) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.4f),
                strokeWidth = 4.dp
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(recruitments.info.infoResource())
                }: ${recruitments.additionalInfo}",
                onReloadPage = { onLoadPage(lastSelectedPage) }
            )

            is APIResult.Succeed -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(240.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    items(
                        items = recruitments.data!!.result,
                        key = { recruitment -> recruitment.id }
                    ) { recruitment ->
                        RecruitmentCard(
                            modifier = Modifier
                                .padding(12.dp),
                            recruitment = recruitment,
                            onSeeWalkerClick = onSeeWalkerClick,
                            onSeeAssignmentClick = onSeeAssignmentClick,
                            onAcceptClick = { onAcceptClick(recruitment.id) },
                            onDeclineClick = { onDeclineClick(recruitment.id) },
                            onDeleteClick = if (recruitment.outcoming) {
                                { onDeleteRecruitment(recruitment.id) }
                            } else null
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        PageSelectionRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            totalPages = recruitments.data.totalPages,
                            currentPage = recruitments.data.currentPage,
                            onPageClick = { onLoadPage(it) }
                        )
                    }
                }
            }
        }
    }
}