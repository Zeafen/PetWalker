package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentsStats
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.presentation.walkers.walkerDetails.AssignmentsStatsGenerationOption
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiState
import com.zeafen.petwalker.ui.assignments.AssignmentCard
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerOrderingTab
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_period_label
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.search_field_hint

@Composable
fun WalkerAssignmentsTab(
    modifier: Modifier = Modifier,
    state: WalkerDetailsPageUiState,
    onEvent: (WalkerDetailsPageUiEvent) -> Unit,
    onAssignmentClick: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        val deviceConfig =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

        PetWalkerTextInput(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp, vertical = 8.dp
                ),
            value = state.searchAssignmentTitle,
            hint = stringResource(Res.string.search_field_hint),
            onValueChanged = { onEvent(WalkerDetailsPageUiEvent.SetSearchAssignmentTitle(it)) },
            leadingIcon = painterResource(Res.drawable.ic_search)
        )
        PetWalkerOrderingTab(
            availableOrderings = AssignmentsOrdering.entries.toList(),
            selectedOrdering = state.assignmentOrdering,
            onOrderingChanged = { onEvent(WalkerDetailsPageUiEvent.SetAssignmentOrdering(it)) },
            ascending = state.assignmentAscending,
            orderingLabel = { stringResource(it.displayName) }
        )

        when (state.walkerAssignments) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.3f),
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(state.walkerAssignments.info.infoResource())
                }: ${state.walkerAssignments.additionalInfo}",
                onReloadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment()) }
            )

            is APIResult.Succeed -> {
                LazyVerticalStaggeredGrid(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    columns = StaggeredGridCells.Adaptive(minSize = 250.dp)
                ) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        when (state.walkerAssignmentStats) {
                            is APIResult.Downloading -> CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth()
                                    .size(64.dp),
                                strokeWidth = 4.dp
                            )

                            is APIResult.Error -> ErrorInfoHint(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                errorInfo = "${
                                    stringResource(state.walkerAssignmentStats.info.infoResource())
                                }: ${state.walkerAssignmentStats.additionalInfo}",
                                onReloadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews()) }
                            )

                            is APIResult.Succeed -> {
                                AssignmentsStatisticsSheet(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .wrapContentWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(horizontal = 8.dp, vertical = 12.dp),
                                    assignmentsStats = state.walkerAssignmentStats.data!!,
                                    onChangeSelectedPeriod = {
                                        it?.let {
                                            onEvent(
                                                WalkerDetailsPageUiEvent.LoadAssignmentsAssignmentsStats(
                                                    it
                                                )
                                            )
                                        }
                                    },
                                    selectedDatePeriod = state.assignmentsStatsDatePeriod
                                )
                            }
                        }
                    }
                    items(
                        items = state.walkerAssignments.data!!.result,
                        key = { assignment -> assignment.id }
                    ) { assignment ->
                        AssignmentCard(
                            modifier = Modifier.padding(12.dp),
                            assignment = assignment,
                            onClick = { onAssignmentClick(assignment.id) }
                        )
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        PageSelectionRow(
                            modifier = Modifier
                                .fillMaxWidth(),
                            totalPages = state.walkerAssignments.data.totalPages,
                            currentPage = state.walkerAssignments.data.currentPage,
                            onPageClick = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment(it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssignmentsStatisticsSheet(
    modifier: Modifier = Modifier,
    assignmentsStats: AssignmentsStats,
    selectedDatePeriod: DatePeriods? = DatePeriods.All,
    onChangeSelectedPeriod: (DatePeriods?) -> Unit
) {
    var selectedOption by remember {
        mutableStateOf(AssignmentsStatsGenerationOption.Amount)
    }
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    Column(modifier = modifier) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OptionsSelectedInput(
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .padding(8.dp),
                selectedOptions = listOf(selectedDatePeriod),
                availableOptions = DatePeriods.entries,
                onOptionSelected = onChangeSelectedPeriod,
                onOptionDeleted = {},
                label = stringResource(Res.string.date_period_label),
                hint = "",
                optionContent = {
                    Text(
                        stringResource((it as DatePeriods).displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )

            OptionsSelectedInput(
                modifier = Modifier
                    .widthIn(max = 200.dp)
                    .padding(8.dp),
                selectedOptions = listOf(selectedOption),
                availableOptions = AssignmentsStatsGenerationOption.entries,
                onOptionSelected = { selectedOption = it },
                onOptionDeleted = {},
                label = stringResource(Res.string.option_selection_hint),
                hint = "",
                optionContent = {
                    Text(
                        stringResource(it.displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            ServiceType.entries.forEach {
                val animatedHeightFraction by animateFloatAsState(
                    targetValue = when (selectedOption) {
                        AssignmentsStatsGenerationOption.Amount -> assignmentsStats.countsMap.getOrElse(
                            it
                        ) { 0f } / 100f

                        AssignmentsStatsGenerationOption.TotalIncome -> assignmentsStats.incomesMap.getOrElse(
                            it
                        ) { 0f } / assignmentsStats.incomesMap.maxBy { it.value }.value

                        AssignmentsStatsGenerationOption.AverageIncome -> assignmentsStats.avgIncomesMap.getOrElse(
                            it
                        ) { 0f } / assignmentsStats.avgIncomesMap.maxBy { it.value }.value
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when(selectedOption){
                            AssignmentsStatsGenerationOption.Amount -> (assignmentsStats.countsMap.getOrElse(it) { 0f })
                                .format(2)
                            AssignmentsStatsGenerationOption.TotalIncome -> (assignmentsStats.incomesMap.getOrElse(it) { 0f })
                                .format(2)
                            AssignmentsStatsGenerationOption.AverageIncome -> (assignmentsStats.avgIncomesMap.getOrElse(it) { 0f })
                                .format(2)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Box(
                        modifier = Modifier
                            .width(
                                when (deviceConfig) {
                                    DeviceConfiguration.MOBILE_PORTRAIT -> 16.dp
                                    in listOf(
                                        DeviceConfiguration.MOBILE_LANDSCAPE,
                                        DeviceConfiguration.TABLET_PORTRAIT
                                    ) -> 32.dp

                                    else -> 48.dp
                                }
                            )
                            .heightIn(
                                max =
                                    when (deviceConfig) {
                                        in listOf(
                                            DeviceConfiguration.MOBILE_PORTRAIT,
                                            DeviceConfiguration.MOBILE_LANDSCAPE
                                        ) -> 200.dp

                                        DeviceConfiguration.TABLET_PORTRAIT -> 300.dp

                                        else -> 400.dp
                                    }
                            )
                            .fillMaxHeight(animatedHeightFraction)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 4.dp, topEnd = 4.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        stringResource((it).displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}