package com.zeafen.petwalker.ui.walkers

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
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import com.zeafen.petwalker.ui.standard.elements.FiltersDialogBody
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.complaints_count_label
import petwalker.composeapp.generated.resources.filters_label
import petwalker.composeapp.generated.resources.online_label
import petwalker.composeapp.generated.resources.services_label
import petwalker.composeapp.generated.resources.status_label

@Composable
fun WalkersFilteringDialog(
    onDismissRequest: () -> Unit,
    onClearFiltersClick: () -> Unit,
    onDoneFiltersClick: (
        services: List<ServiceType>?,
        maxComplaintsCount: Int?,
        accountStatus: AccountStatus?,
        showOnline: Boolean?
    ) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        var serviceOption by rememberSaveable {
            mutableStateOf(
                1 to FilterOption(
                    value = FilteringTypes.ListingType(
                        availableOptions = ServiceType.entries,
                        optionContent = { option ->
                            Text(
                                stringResource((option as ServiceType).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    ),
                    name = Res.string.services_label
                ),
            )
        }
        var maxComplaintsOption by rememberSaveable {
            mutableStateOf(
                2 to FilterOption(
                    value = FilteringTypes.IntType(),
                    name = Res.string.complaints_count_label
                ),
            )
        }
        var statusOption by rememberSaveable {
            mutableStateOf(
                3 to FilterOption(
                    value = FilteringTypes.ListingType(
                        availableOptions = AccountStatus.entries,
                        singleSelection = true,
                        optionContent = { option ->
                            Text(
                                stringResource((option as AccountStatus).displayName),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    ),
                    name = Res.string.status_label
                )
            )
        }
        var onlineOption by rememberSaveable {
            mutableStateOf(
                4 to FilterOption(
                    value = FilteringTypes.BooleanType(),
                    name = Res.string.online_label
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
                    onClearFiltersClick()
                    onDismissRequest()
                },
                onDoneFiltersClick = {
                    onDoneFiltersClick(
                        if (serviceOption.second.enabled)
                            (serviceOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.map { it as ServiceType }
                        else null,
                        if (maxComplaintsOption.second.enabled)
                            (maxComplaintsOption.second.value as FilteringTypes.IntType).num.toIntOrNull()
                        else null,
                        if (statusOption.second.enabled)
                            (statusOption.second.value as FilteringTypes.ListingType)
                                .selectedOptions.first() as AccountStatus
                        else null,
                        if (onlineOption.second.enabled)
                            (onlineOption.second.value as FilteringTypes.BooleanType).selected
                        else null
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
                    serviceOption,
                    maxComplaintsOption,
                    statusOption,
                    onlineOption
                ),
                onFilteringOptionChanged = { index, option ->
                    when {
                        serviceOption.first == index -> {
                            serviceOption = index to option
                        }

                        maxComplaintsOption.first == index -> {
                            maxComplaintsOption = index to option
                        }

                        statusOption.first == index -> {
                            statusOption = index to option
                        }

                        onlineOption.first == index -> {
                            onlineOption = index to option
                        }

                    }
                }
            )
        }
    }
}

