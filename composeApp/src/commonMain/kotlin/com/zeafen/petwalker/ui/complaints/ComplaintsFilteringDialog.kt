package com.zeafen.petwalker.ui.complaints

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import com.zeafen.petwalker.ui.standard.elements.FiltersDialogBody
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_period_label
import petwalker.composeapp.generated.resources.filters_label
import petwalker.composeapp.generated.resources.status_label
import petwalker.composeapp.generated.resources.topic_label

@Composable
fun ComplaintFilteringDialog(
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
    onDoneClick: (
        topic: ComplaintTopic?,
        status: ComplaintStatus?,
        period: DatePeriods?
    ) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        var topicOption by remember {
            mutableStateOf(
                1 to FilterOption(
                    name = Res.string.topic_label,
                    value = FilteringTypes.ListingType(
                        singleSelection = true,
                        availableOptions = ComplaintTopic.entries.toList(),
                        optionContent = { option ->
                            Text(
                                text = stringResource((option as ComplaintTopic).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                )
            )
        }
        var statusOption by remember {
            mutableStateOf(
                2 to FilterOption(
                    name = Res.string.status_label,
                    value = FilteringTypes.ListingType(
                        singleSelection = true,
                        availableOptions = ComplaintStatus.entries.toList(),
                        optionContent = { option ->
                            Text(
                                text = stringResource((option as ComplaintStatus).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                )
            )
        }
        var periodOption by remember {
            mutableStateOf(
                3 to FilterOption(
                    name = Res.string.date_period_label ,
                    value = FilteringTypes.ListingType(
                        singleSelection = true,
                        availableOptions = DatePeriods.entries.toList(),
                        optionContent = { option ->
                            Text(
                                text = stringResource((option as DatePeriods).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                )
            )
        }

        Column {
            PetWalkerDialogHeader(
                title = stringResource(Res.string.filters_label),
                onClearFiltersClick = {
                    onClearClick()
                    onDismissRequest()
                },
                onDoneFiltersClick = {
                    onDoneClick(
                        if (topicOption.second.enabled)
                            (topicOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.first() as ComplaintTopic
                        else null,
                        if (statusOption.second.enabled)
                            (statusOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.first() as ComplaintStatus
                        else null,
                        if (periodOption.second.enabled)
                            (periodOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.first() as DatePeriods
                        else null,
                    )
                    onDismissRequest()
                }
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp),
                thickness = 4.dp
            )
            FiltersDialogBody(
                filteringOptions = mapOf(
                    topicOption,
                    statusOption,
                    periodOption
                ),
                onFilteringOptionChanged = { index, option ->
                    when (index) {
                        topicOption.first -> {
                            topicOption = index to option
                        }

                        statusOption.first -> {
                            statusOption = index to option
                        }

                        periodOption.first -> {
                            periodOption = index to option
                        }
                    }
                }
            )
        }
    }
}