package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PetWalkerDatePicker(
    modifier: Modifier = Modifier,
    currentDate: LocalDateTime? = null,
    onDateChanged: (LocalDateTime?) -> Unit,
    isError: Boolean = false,
    errorString: String? = null
) {
    val pickerState = rememberDatePickerState(
        currentDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
            ?: 0L,
        initialDisplayMode = DisplayMode.Input
    )
    LaunchedEffect(pickerState.selectedDateMillis) {
        onDateChanged(
            pickerState.selectedDateMillis?.let {
                Instant.fromEpochMilliseconds(it).toLocalDateTime(
                    TimeZone.currentSystemDefault()
                )
            }
        )
    }
    Column(modifier = modifier) {
        DatePicker(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            state = pickerState,
            title = null,
            colors = DatePickerDefaults.colors(
                containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else Color.Unspecified,
                todayDateBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        )
        AnimatedVisibility(
            visible = isError
        ) {
            errorString?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}