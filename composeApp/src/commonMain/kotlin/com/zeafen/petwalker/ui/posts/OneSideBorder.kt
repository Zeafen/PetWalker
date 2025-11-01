package com.zeafen.petwalker.ui.posts

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.onSideBorder(
    color: Color,
    strokeWidth: Dp
) = composed {
    val density = LocalDensity.current
    Modifier.drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = with(density){ strokeWidth.toPx() }
        )
    }
}