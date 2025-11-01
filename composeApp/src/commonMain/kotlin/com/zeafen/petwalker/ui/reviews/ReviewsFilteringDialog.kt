package com.zeafen.petwalker.ui.reviews

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import com.zeafen.petwalker.ui.standard.elements.FiltersDialogBody
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_period_label
import petwalker.composeapp.generated.resources.filters_label
import petwalker.composeapp.generated.resources.positive_label

@Composable
fun ReviewsFilteringDialog(
    onDismissRequest: () -> Unit,
    onClearClick: () -> Unit,
    onDoneClick: (
        positive: Boolean?,
        period: DatePeriods?
    ) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        var positiveOption by rememberSaveable {
            mutableStateOf(
                1 to FilterOption(
                    name = Res.string.positive_label,
                    value = FilteringTypes.BooleanType()
                )
            )
        }
        var periodOption by rememberSaveable {
            mutableStateOf(
                2 to FilterOption(
                    name = Res.string.date_period_label,
                    value = FilteringTypes.ListingType(
                        singleSelection = true,
                        availableOptions = DatePeriods.entries.toList(),
                        optionContent = {
                            Text(
                                text = stringResource((it as DatePeriods).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                )
            )
        }
        Column {
            PetWalkerDialogHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                title = stringResource(Res.string.filters_label),
                onClearFiltersClick = {
                    onClearClick()
                    onDismissRequest()
                },
                onDoneFiltersClick = {
                    onDoneClick(
                        if (positiveOption.second.enabled) (positiveOption.second.value as FilteringTypes.BooleanType).selected else null,
                        if (periodOption.second.enabled)
                            (positiveOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.first() as DatePeriods
                        else null
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
                    positiveOption,
                    periodOption
                ),
                onFilteringOptionChanged = { index, option ->
                    when (index) {
                        positiveOption.first -> {
                            positiveOption = index to option
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