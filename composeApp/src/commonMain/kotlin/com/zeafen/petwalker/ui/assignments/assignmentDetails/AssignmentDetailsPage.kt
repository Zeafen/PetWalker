package com.zeafen.petwalker.ui.assignments.assignmentDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsTabs
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiState
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageTabs
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import com.zeafen.petwalker.ui.walkers.walkerDetails.WalkerInfoTab
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.assignment_details_pages_header
import petwalker.composeapp.generated.resources.close_btn_txt
import petwalker.composeapp.generated.resources.complete_btn_txt
import petwalker.composeapp.generated.resources.confirm_change_status_label
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.pending_status_display_name
import petwalker.composeapp.generated.resources.search_field_hint
import petwalker.composeapp.generated.resources.start_btn_txt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailsPage(
    modifier: Modifier = Modifier,
    state: AssignmentDetailsUiState,
    onEvent: (AssignmentDetailsUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onGoToChannelClick: (assignmentId: String) -> Unit,
    onLeaveReviewClick: (assignmentID: String) -> Unit
) {
    val pagerState = rememberPagerState {
        WalkerDetailsPageTabs.entries.size
    }
    var openStatusSelectionMenu by remember {
        mutableStateOf(false)
    }
    var popupContent = remember(state.assignmentWalker) {
        state.filesLoadingError?.infoResource()
    }
    var selectedStatus by remember {
        mutableStateOf<AssignmentState?>(null)
    }
    var openConfirmChangeStatusDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            onEvent(AssignmentDetailsUiEvent.SetSelectedTab(pagerState.currentPage))
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.assignment_details_pages_header),
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
                    if (state.owns)
                        Box {
                            IconButton(
                                onClick = { openStatusSelectionMenu = !openStatusSelectionMenu }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_online),
                                    contentDescription = "Change assignment status"
                                )
                            }
                            DropdownMenu(
                                expanded = openStatusSelectionMenu,
                                onDismissRequest = { openStatusSelectionMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.search_field_hint),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        selectedStatus = AssignmentState.Searching
                                        openStatusSelectionMenu = false
                                        openConfirmChangeStatusDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.start_btn_txt),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        selectedStatus = AssignmentState.In_Progress
                                        openStatusSelectionMenu = false
                                        openConfirmChangeStatusDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.complete_btn_txt),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        selectedStatus = AssignmentState.Completed
                                        openStatusSelectionMenu = false
                                        openConfirmChangeStatusDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.close_btn_txt),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    onClick = {
                                        selectedStatus = AssignmentState.Closed
                                        openStatusSelectionMenu = false
                                        openConfirmChangeStatusDialog = true
                                    }
                                )
                            }
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
                    .fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                selectedTabIndex = state.selectedTabIndex,
                tabs = {
                    AssignmentDetailsTabs.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = index == state.selectedTabIndex,
                            text = {
                                Text(
                                    text = stringResource(tab.displayName),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = { onEvent(AssignmentDetailsUiEvent.SetSelectedTab(index)) },
                        )
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState
            ) { index ->
                when (AssignmentDetailsTabs.entries[index]) {
                    AssignmentDetailsTabs.Info -> PullToRefreshBox(
                        modifier = Modifier
                            .padding(4.dp),
                        isRefreshing = state.assignment is APIResult.Downloading,
                        onRefresh = {
                            state.selectedAssignmentId?.let {
                                onEvent(
                                    AssignmentDetailsUiEvent.LoadAssignment(it)
                                )
                            }
                        }
                    ) {
                        AssignmentInfoTab(
                            modifier = Modifier
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState()),
                            state = state,
                            onEvent = onEvent
                        )
                    }

                    AssignmentDetailsTabs.Pets -> PullToRefreshBox(
                        modifier = Modifier
                            .padding(4.dp),
                        isRefreshing = state.assignmentPets is APIResult.Downloading,
                        onRefresh = { onEvent(AssignmentDetailsUiEvent.LoadPets(state.lastSelectedPetPage)) }
                    ) {
                        AssignmentPetsTab(
                            modifier = Modifier
                                .fillMaxHeight(),
                            state = state,
                            onEvent = onEvent
                        )
                    }

                    AssignmentDetailsTabs.WalkerInfo -> {
                        PullToRefreshBox(
                            modifier = Modifier
                                .padding(4.dp),
                            isRefreshing = state.assignmentWalker is APIResult.Downloading,
                            onRefresh = { onEvent(AssignmentDetailsUiEvent.LoadWalker) }
                        ) {
                            when (state.assignmentWalker) {
                                is APIResult.Downloading -> CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth()
                                        .fillMaxWidth(0.4f),
                                    strokeWidth = 4.dp
                                )

                                is APIResult.Error -> ErrorInfoHint(
                                    errorInfo = "${
                                        stringResource(state.assignmentWalker.info.infoResource())
                                    }: ${state.assignmentWalker.additionalInfo}",
                                    onReloadPage = { onEvent(AssignmentDetailsUiEvent.LoadWalker) }
                                )

                                is APIResult.Succeed -> WalkerInfoTab(
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    walker = state.assignmentWalker.data!!,
                                    canReviewWalker = state.owns,
                                    distance = state.distanceToAssignment,
                                    onLeaveReviewClick = {
                                        state.selectedAssignmentId?.let {
                                            onLeaveReviewClick(
                                                it
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    AssignmentDetailsTabs.Channel ->
                        PullToRefreshBox(
                            modifier = Modifier
                                .padding(4.dp),
                            isRefreshing = state.assignmentChannel is APIResult.Downloading,
                            onRefresh = { onEvent(AssignmentDetailsUiEvent.LoadChannel) }
                        ) {
                            AssignmentChannelTab(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                state = state,
                                onEvent = onEvent,
                                onGoToChannelClick = onGoToChannelClick
                            )
                        }
                }
            }
        }
    }
    if (openConfirmChangeStatusDialog && selectedStatus != null)
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(
                Res.string.confirm_change_status_label,
                stringResource(selectedStatus!!.displayName)
            ),
            onConfirm = {
                onEvent(AssignmentDetailsUiEvent.SetStatus(selectedStatus!!))
                openConfirmChangeStatusDialog = false
                selectedStatus = null
            },
            onDismissRequest = {
                openConfirmChangeStatusDialog = false
                selectedStatus = null
            }
        )

    if (popupContent != null) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { popupContent = null }
        ) {
            Text(
                text = stringResource(popupContent!!),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}