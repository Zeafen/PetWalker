package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.foundation.background
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageTabs
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.loading_label
import petwalker.composeapp.generated.resources.success_label
import petwalker.composeapp.generated.resources.walker_details_pages_header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkerDetailsPage(
    modifier: Modifier = Modifier,
    state: WalkerDetailsPageUiState,
    onEvent: (WalkerDetailsPageUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onGoToAssignmentClick: (String) -> Unit,
    onAddComplaintClick: () -> Unit
) {
    var openRecruitingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val pagerState = rememberPagerState {
        WalkerDetailsPageTabs.entries.size
    }
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            onEvent(WalkerDetailsPageUiEvent.SetSelectedTab(pagerState.currentPage))
    }
    LaunchedEffect(state.recruitingResult) {
        popupContent = when (state.recruitingResult) {
            is APIResult.Downloading -> Res.string.loading_label
            is APIResult.Error<*> -> state.recruitingResult.info.infoResource()
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
                        stringResource(Res.string.walker_details_pages_header),
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
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedTabIndex = state.selectedTabIndex,
                tabs = {
                    WalkerDetailsPageTabs.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == state.selectedTabIndex,
                            text = {
                                Text(
                                    text = stringResource(tab.displayName),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = { onEvent(WalkerDetailsPageUiEvent.SetSelectedTab(index)) },
                        )
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
            HorizontalPager(
                modifier = Modifier
                    .weight(1f),
                state = pagerState
            ) { index ->
                when (WalkerDetailsPageTabs.entries[index]) {
                    WalkerDetailsPageTabs.Info -> {
                        PullToRefreshBox(
                            isRefreshing = state.walker is APIResult.Downloading,
                            onRefresh = {
                                state.selectedWalkerId?.let {
                                    onEvent(
                                        WalkerDetailsPageUiEvent.LoadWalker(it)
                                    )
                                }
                            }
                        ) {
                            when (state.walker) {
                                is APIResult.Downloading -> CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth()
                                        .size(128.dp),
                                    strokeWidth = 4.dp
                                )

                                is APIResult.Error -> ErrorInfoHint(
                                    errorInfo = "${
                                        stringResource(state.walker.info.infoResource())
                                    }: ${state.walker.additionalInfo}",
                                    onReloadPage = {
                                        state.selectedWalkerId?.let {
                                            onEvent(
                                                WalkerDetailsPageUiEvent.LoadWalker(it)
                                            )
                                        }
                                    }
                                )

                                is APIResult.Succeed -> {
                                    WalkerInfoTab(
                                        walker = state.walker.data!!,
                                        distance = state.distance,
                                        onRecruitClick = {
                                            onEvent(WalkerDetailsPageUiEvent.LoadAvailableAssignments())
                                            openRecruitingDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    WalkerDetailsPageTabs.Reviews -> {
                        PullToRefreshBox(
                            isRefreshing = state.walkerReviews is APIResult.Downloading,
                            onRefresh = {
                                onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews(state.selectedReviewsPage))
                            }
                        ) {
                            WalkerReviewsTab(
                                state = state,
                                onGoToAssignmentClick = onGoToAssignmentClick,
                                onEvent = onEvent
                            )
                        }
                    }

                    WalkerDetailsPageTabs.Complaints -> {
                        PullToRefreshBox(
                            isRefreshing = state.walkerComplaintsStats is APIResult.Downloading,
                            onRefresh = {
                                onEvent(WalkerDetailsPageUiEvent.LoadWalkerComplaints(state.selectedComplaintsPage))
                            }
                        ) {
                            WalkerComplaintsTab(
                                state = state,
                                onEvent = onEvent,
                                onGoToAssignmentClick = onGoToAssignmentClick,
                                onAddComplaintClick = onAddComplaintClick
                            )
                        }
                    }

                    WalkerDetailsPageTabs.AssignmentsHistory -> {
                        PullToRefreshBox(
                            isRefreshing = state.walkerAssignments is APIResult.Downloading,
                            onRefresh = {
                                onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment(state.selectedAssignmentsPage))
                            }
                        ) {
                            WalkerAssignmentsTab(
                                state = state,
                                onEvent = onEvent,
                                onAssignmentClick = onGoToAssignmentClick
                            )
                        }
                    }
                }
            }
            if (openRecruitingDialog)
                RecruitingDialog(
                    availableAssignments = state.availableAssignments,
                    onDismissRequest = { openRecruitingDialog = false },
                    onLoadAssignments = {
                        onEvent(
                            WalkerDetailsPageUiEvent.LoadAvailableAssignments(
                                it
                            )
                        )
                    },
                    onDoneRecruiting = {
                        onEvent(WalkerDetailsPageUiEvent.RecruitWalker(it))
                        openRecruitingDialog = false
                    }
                )
            if(popupContent != null)
                Popup(
                    alignment = Alignment.BottomCenter,
                    onDismissRequest = { popupContent = null }
                ) {
                    Text(
                        text = stringResource(popupContent!!),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
        }
    }
}