package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.standard.filtering.FilterOption
import com.zeafen.petwalker.presentation.standard.filtering.FilteringTypes
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.clear_btn_txt
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.float_input_hint
import petwalker.composeapp.generated.resources.int_input_hint
import petwalker.composeapp.generated.resources.nan_error_txt
import petwalker.composeapp.generated.resources.positive_label
import petwalker.composeapp.generated.resources.search_field_hint

@Composable
fun PetWalkerDialogHeader(
    modifier: Modifier = Modifier,
    title: String,
    onClearFiltersClick: () -> Unit,
    onDoneFiltersClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PetWalkerChip(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            label = stringResource(Res.string.clear_btn_txt),
            onClick = onClearFiltersClick,
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
        Text(
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = 4.dp),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        PetWalkerChip(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            label = stringResource(Res.string.done_btn_txt),
            onClick = onDoneFiltersClick,
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersDialogBody(
    modifier: Modifier = Modifier,
    filteringOptions: Map<Int, FilterOption>,
    onFilteringOptionChanged: (Int, FilterOption) -> Unit
) {
    Column(modifier = modifier) {
        filteringOptions.forEach { (index, option) ->
            FilteringOptionHeader(
                header = stringResource(option.name),
                enabled = option.enabled,
                onEnabledChanged = { onFilteringOptionChanged(index, option.copy(enabled = it)) }
            )
            FilteringOptionBody(
                filteringOption = index to option,
                onFilteringOptionChanged = onFilteringOptionChanged
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilteringOptionHeader(
    modifier: Modifier = Modifier,
    header: String,
    enabled: Boolean,
    onEnabledChanged: (Boolean) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = enabled,
            onCheckedChange = onEnabledChanged,
        )
        Text(
            text = header,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilteringOptionBody(
    modifier: Modifier = Modifier,
    filteringOption: Pair<Int, FilterOption>,
    onFilteringOptionChanged: (Int, FilterOption) -> Unit
) {
    val (index, option) = filteringOption
    when (option.value) {
        is FilteringTypes.BooleanType -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = option.value.selected,
                    onCheckedChange = {
                        onFilteringOptionChanged(
                            index,
                            option.copy(value = option.value.copy(selected = it))
                        )
                    }
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.positive_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is FilteringTypes.FloatType -> {
            PetWalkerTextInput(
                modifier = modifier,
                value = option.value.num,
                onValueChanged = {
                    onFilteringOptionChanged(
                        index,
                        option.copy(
                            value = option.value.copy(
                                num = it,
                                canBeConverted = it.toFloatOrNull() != null
                            )
                        )
                    )
                },
                isError = option.enabled && !option.value.canBeConverted,
                supportingText = if (option.enabled && !option.value.canBeConverted)
                    stringResource(Res.string.nan_error_txt)
                else null,
                hint = stringResource(Res.string.float_input_hint),
                keyboardType = KeyboardType.Decimal
            )
        }

        is FilteringTypes.IntType -> {
            PetWalkerTextInput(
                modifier = modifier,
                value = option.value.num,
                onValueChanged = {
                    onFilteringOptionChanged(
                        index,
                        option.copy(
                            value = option.value.copy(
                                num = it,
                                canBeConverted = it.toIntOrNull() != null
                            )
                        )
                    )
                },
                isError = option.enabled && !option.value.canBeConverted,
                supportingText = if (option.enabled && !option.value.canBeConverted)
                    stringResource(Res.string.nan_error_txt)
                else null,
                hint = stringResource(Res.string.int_input_hint),
                keyboardType = KeyboardType.Number
            )
        }

        is FilteringTypes.ListingType -> {
            OptionsSelectedInput(
                modifier = modifier,
                availableOptions = option.value.availableOptions,
                selectedOptions = option.value.selectedOptions,
                hint = stringResource(Res.string.search_field_hint),
                onOptionSelected = { selected ->
                    onFilteringOptionChanged(
                        index,
                        if (option.value.singleSelection) option.copy(
                            value = option.value.copy(
                                selectedOptions = listOf(selected)
                            )
                        )
                        else
                            option.copy(
                                value = option.value.copy(
                                    selectedOptions = option.value.selectedOptions.toMutableList()
                                        .apply {
                                            add(selected)
                                            distinct()
                                        }
                                )
                            )
                    )
                },
                onOptionDeleted = { deleted ->
                    onFilteringOptionChanged(
                        index,
                        option.copy(
                            value = option.value.copy(
                                selectedOptions = option.value.selectedOptions.toMutableList()
                                    .apply {
                                        remove(deleted)
                                    }
                            )
                        )
                    )
                },
                optionContent = { value -> option.value.optionContent(value) }
            )
        }

        is FilteringTypes.TextType -> {
            PetWalkerTextInput(
                modifier = modifier,
                value = option.value.text,
                onValueChanged = {
                    onFilteringOptionChanged(
                        index,
                        option.copy(
                            value = option.value.copy(
                                text = it
                            )
                        )
                    )
                },
                keyboardType = KeyboardType.Decimal
            )

        }

        is FilteringTypes.DateOption -> {
            val state = rememberDatePickerState(
                option.value.date,
                initialDisplayMode = DisplayMode.Input
            )
            LaunchedEffect(state.selectedDateMillis) {
                onFilteringOptionChanged(
                    index,
                    option.copy(
                        value = option.value.copy(
                            date = state.selectedDateMillis
                        )
                    )
                )
            }
            DatePicker(
                modifier = modifier,
                state = state,
                title = null,
            )
        }
    }
}
