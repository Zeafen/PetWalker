package com.zeafen.petwalker.ui.assignments.recruitmentsPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsLoadGroup
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import com.zeafen.petwalker.ui.standard.elements.FiltersDialogBody
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.category_selection_label
import petwalker.composeapp.generated.resources.date_period_end_label
import petwalker.composeapp.generated.resources.date_period_start_label
import petwalker.composeapp.generated.resources.filters_label
import petwalker.composeapp.generated.resources.state_selection_label

@Composable
fun RecruitmentsFilteringDialog(
    onDismissRequest: () -> Unit,
    onDoneClick: (
        loadGroup: RecruitmentsLoadGroup,
        state: RecruitmentState?,
        dateFrom: Long?,
        dateUntil: Long?
    ) -> Unit,
    onClearClick: () -> Unit
) {
    var loadGroupOption by remember {
        mutableStateOf(
            0 to FilterOption(
                Res.string.category_selection_label,
                value = FilteringTypes.ListingType(
                    availableOptions = RecruitmentsLoadGroup.entries.toList(),
                    singleSelection = true
                ) {
                    Text(
                        text = stringResource((it as RecruitmentsLoadGroup).displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        )
    }
    var stateOption by remember {
        mutableStateOf(
            1 to FilterOption(
                Res.string.state_selection_label,
                value = FilteringTypes.ListingType(
                    availableOptions = RecruitmentState.entries.toList(),
                    singleSelection = true
                ) {
                    Text(
                        text = stringResource((it as RecruitmentState).displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        )
    }
    var dateFromOption by rememberSaveable {
        mutableStateOf(
            2 to FilterOption(
                name = Res.string.date_period_start_label,
                value = FilteringTypes.DateOption()
            )
        )
    }
    var dateToOption by rememberSaveable {
        mutableStateOf(
            3 to FilterOption(
                name = Res.string.date_period_end_label,
                value = FilteringTypes.DateOption()
            )
        )
    }


    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            PetWalkerDialogHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                onClearFiltersClick = {
                    onClearClick()
                    onDismissRequest()
                },
                title = stringResource(Res.string.filters_label),
                onDoneFiltersClick = {
                    onDoneClick(
                        if (loadGroupOption.second.enabled) (loadGroupOption.second.value as FilteringTypes.ListingType).selectedOptions.first() as RecruitmentsLoadGroup
                        else RecruitmentsLoadGroup.All,
                        if (stateOption.second.enabled) (stateOption.second.value as FilteringTypes.ListingType).selectedOptions.first() as RecruitmentState
                        else null,
                        if (dateFromOption.second.enabled)
                            (dateFromOption.second.value as FilteringTypes.DateOption).date
                        else null,
                        if (dateToOption.second.enabled)
                            (dateToOption.second.value as FilteringTypes.DateOption).date
                        else null,
                    )
                    onDismissRequest()
                }
            )

            HorizontalDivider()
            FiltersDialogBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .clip(
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .verticalScroll(rememberScrollState()),
                filteringOptions = mapOf(
                    loadGroupOption,
                    stateOption,
                    dateFromOption,
                    dateToOption
                ),
                onFilteringOptionChanged = { index, option ->
                    when (index) {
                        loadGroupOption.first -> {
                            loadGroupOption = index to option
                        }

                        stateOption.first -> {
                            stateOption = index to option
                        }

                        dateFromOption.first -> {
                            dateFromOption = index to option
                        }

                        dateToOption.first -> {
                            dateToOption = index to option
                        }
                    }
                }
            )
        }
    }
}