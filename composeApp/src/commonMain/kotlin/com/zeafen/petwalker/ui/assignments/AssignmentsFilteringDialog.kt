package com.zeafen.petwalker.ui.assignments

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import com.zeafen.petwalker.ui.standard.elements.FiltersDialogBody
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_period_end_label
import petwalker.composeapp.generated.resources.date_period_start_label
import petwalker.composeapp.generated.resources.filters_label
import petwalker.composeapp.generated.resources.max_distance_option_label
import petwalker.composeapp.generated.resources.services_label

@Composable
fun AssignmentsFilteringDialog(
    onClearClick: () -> Unit,
    onDoneClick: (
        maxDistance: Float?,
        timeFrom: Long?,
        timeTo: Long?,
        services: List<ServiceType>?
    ) -> Unit,
    onDismissRequest: () -> Unit
) {
    var distanceOption by remember {
        mutableStateOf(
            1 to FilterOption(
                name = Res.string.max_distance_option_label,
                value = FilteringTypes.FloatType()
            )
        )
    }
    var timeFromOption by remember {
        mutableStateOf(
            2 to FilterOption(
                name = Res.string.date_period_start_label,
                value = FilteringTypes.DateOption()
            )
        )
    }
    var timeToOption by remember {
        mutableStateOf(
            3 to FilterOption(
                name = Res.string.date_period_end_label,
                value = FilteringTypes.DateOption()
            )
        )
    }
    var servicesOption by remember {
        mutableStateOf(
            4 to FilterOption(
                name = Res.string.services_label,
                value = FilteringTypes.ListingType(
                    availableOptions = ServiceType.entries.toList(),
                    optionContent = {
                        Text(
                            text = stringResource((it as ServiceType).displayName),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
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
                        if (distanceOption.second.enabled)
                            (distanceOption.second.value as FilteringTypes.FloatType).num.toFloatOrNull()
                        else null,
                        if (timeFromOption.second.enabled)
                            (timeFromOption.second.value as FilteringTypes.DateOption).date
                        else null,
                        if (timeToOption.second.enabled)
                            (timeToOption.second.value as FilteringTypes.DateOption).date
                        else null,
                        if (servicesOption.second.enabled)
                            (servicesOption.second.value as FilteringTypes.ListingType).selectedOptions as List<ServiceType>
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
                    distanceOption,
                    servicesOption,
                    timeFromOption,
                    timeToOption
                ),
                onFilteringOptionChanged = { index, option ->
                    when (index) {
                        distanceOption.first -> {
                            distanceOption = index to option
                        }

                        servicesOption.first -> {
                            servicesOption = index to option
                        }

                        timeFromOption.first -> {
                            timeFromOption = index to option
                        }

                        timeToOption.first -> {
                            timeToOption = index to option
                        }
                    }
                }
            )
        }
    }
}