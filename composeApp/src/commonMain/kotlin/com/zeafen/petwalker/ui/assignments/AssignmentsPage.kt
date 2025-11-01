package com.zeafen.petwalker.ui.assignments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsUiState
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerOrderingTab
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignments_page_header
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_check
import petwalker.composeapp.generated.resources.ic_filter
import petwalker.composeapp.generated.resources.ic_more_vert
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.owner_option_display_name
import petwalker.composeapp.generated.resources.search_field_hint
import petwalker.composeapp.generated.resources.walker_details_pages_header
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AssignmentsPage(
    modifier: Modifier = Modifier,
    state: AssignmentsUiState,
    onEvent: (AssignmentsUiEvent) -> Unit,
    onAssignmentClick: (String) -> Unit,
    onAddAssignmentClick: () -> Unit,
    onEditAssignmentClick: (id: String) -> Unit
) {
    var openFiltersDialog by remember {
        mutableStateOf(false)
    }
    var openSelectGroupMenu by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.assignments_page_header),
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
                    if (state.loadOwn) {
                        Box {
                            IconButton(
                                onClick = { openSelectGroupMenu = true }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_more_vert),
                                    contentDescription = "Select group"
                                )
                            }

                            DropdownMenu(
                                expanded = openSelectGroupMenu,
                                onDismissRequest = { openSelectGroupMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.owner_option_display_name),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    leadingIcon = {
                                        if (state.loadAsOwner)
                                            Icon(
                                                painter = painterResource(Res.drawable.ic_check),
                                                contentDescription = null
                                            )
                                    },
                                    onClick = { onEvent(AssignmentsUiEvent.SetOwnLoadGroup(true)) }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(Res.string.walker_details_pages_header),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    },
                                    leadingIcon = {
                                        if (!state.loadAsOwner)
                                            Icon(
                                                painter = painterResource(Res.drawable.ic_check),
                                                contentDescription = null
                                            )
                                    },
                                    onClick = { onEvent(AssignmentsUiEvent.SetOwnLoadGroup(false)) }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAssignmentClick,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = "Go to beginning"
                )
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
            PetWalkerTextInput(
                modifier = Modifier
                    .padding(
                        horizontal = 12.dp, vertical = 8.dp
                    )
                    .heightIn(max = 128.dp),
                singleLine = true,
                value = state.searchTitle,
                hint = stringResource(Res.string.search_field_hint),
                onValueChanged = { onEvent(AssignmentsUiEvent.SetSearchTitle(it)) },
                leadingIcon = painterResource(Res.drawable.ic_search)
            )
            PetWalkerOrderingTab(
                availableOrderings = AssignmentsOrdering.entries.toList(),
                selectedOrdering = state.ordering,
                onOrderingChanged = { onEvent(AssignmentsUiEvent.SetOrdering(it)) },
                ascending = state.ascending,
                orderingLabel = { stringResource(it.displayName) }
            )
            when (state.assignments) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.4f),
                    strokeWidth = 4.dp
                )

                is APIResult.Error -> ErrorInfoHint(
                    errorInfo = "${
                        stringResource(state.assignments.info.infoResource())
                    }: ${state.assignments.additionalInfo}",
                    onReloadPage = { onEvent(AssignmentsUiEvent.LoadAssignments()) }
                )

                is APIResult.Succeed -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(240.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        items(
                            items = state.assignments.data!!.result,
                            key = { assignment -> assignment.id }
                        ) { assignment ->
                            AssignmentCard(
                                modifier = Modifier.padding(12.dp),
                                assignment = assignment,
                                onClick = { onAssignmentClick(assignment.id) },
                                onEditClick = if (state.loadOwn && state.loadAsOwner) {
                                    { onEditAssignmentClick(assignment.id) }
                                } else null
                            )
                        }
                        item(span = StaggeredGridItemSpan.FullLine) {
                            PageSelectionRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                totalPages = state.assignments.data.totalPages,
                                currentPage = state.assignments.data.currentPage,
                                onPageClick = { onEvent(AssignmentsUiEvent.LoadAssignments(it)) }
                            )
                        }
                    }
                }
            }

        }
    }
    if (openFiltersDialog)
        AssignmentsFilteringDialog(
            onClearClick = { onEvent(AssignmentsUiEvent.ClearFilters) },
            onDismissRequest = { openFiltersDialog = false },
            onDoneClick = { maxDistance, from, until, services ->
                onEvent(
                    AssignmentsUiEvent.SetFilters(
                        maxDistance,
                        from?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        },
                        until?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                        },
                        services
                    )
                )
            }
        )
}