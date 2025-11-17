package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalTime
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PetWalkerTimePicker(
    modifier: Modifier = Modifier,
    currentTime: LocalTime? = null,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    isError: Boolean = false,
    errorString: String? = null
) {
    val pickerState = rememberTimePickerState(
        initialHour = currentTime?.hour ?: 0,
        initialMinute = currentTime?.minute ?: 0
    )
    LaunchedEffect(pickerState.hour, pickerState.minute) {
        onTimeChanged(
            pickerState.hour, pickerState.minute
        )
    }
    Column(modifier = modifier) {
        TimePicker(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            state = pickerState,
            colors = TimePickerDefaults.colors(
                containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else Color.Unspecified,
                periodSelectorBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
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