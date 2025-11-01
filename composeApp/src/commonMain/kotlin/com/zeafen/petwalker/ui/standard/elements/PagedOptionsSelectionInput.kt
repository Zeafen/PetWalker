package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import org.jetbrains.compose.resources.stringResource


@Composable
fun <T> PagedOptionsSelectedInput(
    modifier: Modifier = Modifier,
    selectedOptions: List<T>,
    availableOptions: APIResult<PagedResult<T>, Error>,
    onOptionDeleted: (T) -> Unit,
    onOptionSelected: (T) -> Unit,
    onAvailableOptionsPageSelected: (Int) -> Unit,
    hint: String,
    label: String? = null,
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
                    .padding(horizontal = 8.dp, vertical = 4.dp)
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
                    .padding(horizontal = 8.dp, vertical = 4.dp),
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
            when (availableOptions) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .size(128.dp),
                )

                is APIResult.Error -> ErrorInfoHint(
                    errorInfo = "${
                        stringResource(availableOptions.info.infoResource())
                    }: ${availableOptions.additionalInfo}",
                    onReloadPage = { onAvailableOptionsPageSelected(0) }
                )

                is APIResult.Succeed -> {
                    PagedExpandableOptionsList(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()),
                        availableOptions = availableOptions.data!!.result,
                        currentPage = availableOptions.data.currentPage,
                        maxPages = availableOptions.data.totalPages,
                        onPageSelected = onAvailableOptionsPageSelected,
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> PagedExpandableOptionsList(
    modifier: Modifier = Modifier,
    availableOptions: List<T>,
    currentPage: Int,
    maxPages: Int,
    onPageSelected: (Int) -> Unit,
    isOptionSelected: (T) -> Boolean,
    onOptionSelected: (T, Boolean) -> Unit,
    optionContent: @Composable (T) -> Unit
) {
    FlowRow(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalArrangement = Arrangement.Center
    ) {
        availableOptions.forEach { option ->
            val selected = isOptionSelected(option)
            AssistChip(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = { onOptionSelected(option, !selected) },
                label = { optionContent(option) }
            )
        }

        PageSelectionRow(
            modifier = Modifier.fillMaxWidth(),
            currentPage = currentPage,
            totalPages = maxPages,
            onPageClick = onPageSelected
        )
    }
}