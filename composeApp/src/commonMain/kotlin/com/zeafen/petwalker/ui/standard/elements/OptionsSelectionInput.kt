package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_arrow_up
import petwalker.composeapp.generated.resources.ic_clear
import petwalker.composeapp.generated.resources.ic_search

@Composable
fun <T> OptionsSelectedInput(
    modifier: Modifier = Modifier,
    selectedOptions: List<T>,
    availableOptions: List<T>,
    onOptionDeleted: (T) -> Unit,
    onOptionSelected: (T) -> Unit,
    label: String? = null,
    hint: String,
    supportingText: String? = null,
    optionContent: @Composable RowScope.(T) -> Unit,
    expandedOptionContent: @Composable RowScope.(T) -> Unit = optionContent,
) {
    var openAvailableOptions by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        label?.let {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(
                modifier = Modifier
                    .height(4.dp)
            )
        }
        SelectedOptionsInputField(
            modifier = if (selectedOptions.isNotEmpty())
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            else
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        openAvailableOptions = !openAvailableOptions
                    }
                    .padding(horizontal = 8.dp, vertical = 8.dp),
            openAvailableOptions = openAvailableOptions,
            openAvailableOptionsChanged = { openAvailableOptions = it },
            selectedOptions = selectedOptions,
            onOptionDeleted = onOptionDeleted,
            hint = hint,
            optionContent = optionContent
        )
        supportingText?.let {
            Text(
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp),
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        AnimatedVisibility(
            visible = openAvailableOptions
        ) {
            ExpandableOptionsList(
                availableOptions = availableOptions,
                isOptionSelected = { selectedOptions.contains(it) },
                onOptionSelected = { option, selected ->
                    if (selected)
                        onOptionSelected(option)
                    else onOptionDeleted(option)
                }
            ) {
                Row {
                    expandedOptionContent(it)
                }
            }
        }
    }
}

@Composable
fun <T> SelectedOptionsInputField(
    modifier: Modifier = Modifier,
    openAvailableOptions: Boolean,
    openAvailableOptionsChanged: (Boolean) -> Unit,
    selectedOptions: List<T>,
    onOptionDeleted: (T) -> Unit,
    hint: String,
    optionContent: @Composable RowScope.(T) -> Unit
) {
    val rotation by animateFloatAsState(
        if (openAvailableOptions) 0f else 180f
    )
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(rememberScrollState())
        ) {
            selectedOptions.ifEmpty {
                HintWithIcon(
                    modifier = Modifier,
                    hint = hint,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    leadingIcon = painterResource(Res.drawable.ic_search)
                )
            }
            selectedOptions.forEach {
                AssistChip(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = { onOptionDeleted(it) },
                    label = { optionContent(it) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_clear),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        IconButton(
            onClick = {
                openAvailableOptionsChanged(!openAvailableOptions)
            }
        ) {
            Icon(
                modifier = Modifier
                    .rotate(rotation),
                painter = painterResource(Res.drawable.ic_arrow_up),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> ExpandableOptionsList(
    modifier: Modifier = Modifier,
    availableOptions: List<T>,
    isOptionSelected: (T) -> Boolean,
    onOptionSelected: (T, Boolean) -> Unit,
    optionContent: @Composable (T) -> Unit
) {
    FlowRow(modifier = modifier) {
        availableOptions.forEach { option ->
            val selected = isOptionSelected(option)
            AssistChip(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                ),
                onClick = { onOptionSelected(option, !selected) },
                label = { optionContent(option) }
            )
        }
    }
}
