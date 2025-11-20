package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintsStats
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.ComplaintModel
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiState
import com.zeafen.petwalker.ui.complaints.ComplaintCard
import com.zeafen.petwalker.ui.complaints.ComplaintFilteringDialog
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.active_count_txt
import petwalker.composeapp.generated.resources.complaints_count_txt
import petwalker.composeapp.generated.resources.complaints_tab_display_name
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_filter
import petwalker.composeapp.generated.resources.solved_count_txt

@Composable
fun WalkerComplaintsTab(
    modifier: Modifier = Modifier,
    state: WalkerDetailsPageUiState,
    onEvent: (WalkerDetailsPageUiEvent) -> Unit,
    onGoToAssignmentClick: (String) -> Unit,
    onAddComplaintClick: () -> Unit,
    onDeleteComplaintClick: (String) -> Unit,
    onEditComplaintClick: (String) -> Unit
) {
    var openFiltersDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier) {
        when (state.walkerComplaintsStats) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.3f),
                strokeWidth = 4.dp
            )

            is APIResult.Error ->
                ErrorInfoHint(
                    modifier = Modifier
                        .fillMaxWidth(),
                    errorInfo = "${
                        stringResource(state.walkerComplaintsStats.info.infoResource())
                    }: ${state.walkerComplaintsStats.additionalInfo}",
                    onReloadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerComplaintsStats) }
                )

            is APIResult.Succeed -> {
                ComplaintsStatisticsSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    complaintsStats = state.walkerComplaintsStats.data!!
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = { openFiltersDialog = true }) {
                Icon(
                    modifier = Modifier
                        .size(32.dp),
                    painter = painterResource(Res.drawable.ic_filter),
                    contentDescription = "Filters"
                )
            }
            PetWalkerButton(
                text = stringResource(Res.string.complaints_tab_display_name),
                trailingIcon = painterResource(Res.drawable.ic_add),
                onClick = onAddComplaintClick
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp),
            thickness = 4.dp
        )
        ComplaintsList(
            complaints = state.walkerComplaints,
            onGoToAssignmentClick = onGoToAssignmentClick,
            onLoadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerComplaints(state.selectedComplaintsPage)) },
            onDeleteComplaintClick = onDeleteComplaintClick,
            onEditComplaintClick = onEditComplaintClick
        )
        if (openFiltersDialog)
            ComplaintFilteringDialog(
                onDismissRequest = { openFiltersDialog = false },
                onDoneClick = { topic, status, period ->
                    onEvent(
                        WalkerDetailsPageUiEvent.SetWalkerComplaintsFilters(
                            topic,
                            status,
                            period
                        )
                    )
                },
                onClearClick = {
                    onEvent(
                        WalkerDetailsPageUiEvent.SetWalkerComplaintsFilters(
                            null,
                            null,
                            null
                        )
                    )
                }
            )
    }
}

@Composable
fun ComplaintsList(
    modifier: Modifier = Modifier,
    complaints: APIResult<PagedResult<ComplaintModel>, Error>,
    onLoadPage: (Int) -> Unit,
    onGoToAssignmentClick: (String) -> Unit,
    onDeleteComplaintClick: (String) -> Unit,
    onEditComplaintClick: (String) -> Unit,
) {
    when (complaints) {
        is APIResult.Downloading -> CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            strokeWidth = 4.dp
        )

        is APIResult.Error ->
            ErrorInfoHint(
                modifier = Modifier
                    .fillMaxWidth(),
                errorInfo = "${
                    stringResource(complaints.info.infoResource())
                }: ${complaints.additionalInfo}",
                onReloadPage = { onLoadPage(0) }
            )


        is APIResult.Succeed -> {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Adaptive(minSize = 250.dp)
            ) {
                items(complaints.data!!.result, key = { it.id }) { complaint ->
                    ComplaintCard(
                        modifier = Modifier
                            .padding(4.dp),
                        complaint = complaint,
                        onSeeAssignmentClick = onGoToAssignmentClick,
                        onEditComplaintClick = { onEditComplaintClick(complaint.id) },
                        onDeleteComplaintClick = { onDeleteComplaintClick(complaint.id) },
                    )
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    PageSelectionRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        totalPages = complaints.data.totalPages,
                        currentPage = complaints.data.currentPage,
                        onPageClick = onLoadPage
                    )
                }
            }
        }
    }
}


@Composable
fun ComplaintsStatisticsSheet(
    modifier: Modifier = Modifier,
    complaintsStats: ComplaintsStats
) {
    Column(modifier = modifier) {
        Column {
            Text(
                stringResource(Res.string.complaints_count_txt, complaintsStats.totalCount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                stringResource(Res.string.active_count_txt, complaintsStats.activeCount),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                stringResource(Res.string.solved_count_txt, complaintsStats.solvedCount),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        Spacer(Modifier.height(12.dp))
        complaintsStats.topicsPercentage.keys.sortedDescending().forEach { topic ->
            StatProgress(
                option = stringResource(topic.displayName),
                percentage = complaintsStats.topicsPercentage[topic]!!
            )
        }
    }
}