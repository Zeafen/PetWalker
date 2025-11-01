package com.zeafen.petwalker.presentation.standard.shapes

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class MessageBubbleShape(
    private val cornerRadius: Dp = 12.dp,
    private val tipWidth: Dp = 16.dp,
    private val isOwn: Boolean = false,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tipSizePx = with(density) { tipWidth.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = if (isOwn) 0f else tipSizePx,
                    right = if (isOwn) size.width - tipSizePx else size.width,
                    top = 0f,
                    bottom = size.height,
                    topLeftCornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    topRightCornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    bottomRightCornerRadius = if (isOwn) CornerRadius(
                        0f,
                        0f
                    ) else CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    bottomLeftCornerRadius = if (isOwn) CornerRadius(
                        cornerRadiusPx,
                        cornerRadiusPx
                    ) else CornerRadius(0f, 0f),
                )
            )
            moveTo(
                if (isOwn) size.width - tipSizePx else tipSizePx,
                size.height - cornerRadiusPx*1.5f
            )
            lineTo(
                if (isOwn) size.width - tipSizePx else tipSizePx,
                size.height
            )
            lineTo(
                if (isOwn) size.width else 0f,
                size.height
            )
            lineTo(
                if (isOwn) size.width else 0f,
                size.height - cornerRadiusPx/4f
            )
            arcTo(
                rect = Rect(
                    left = if (isOwn) size.width - tipSizePx else -tipSizePx,
                    bottom = size.height - cornerRadiusPx / 4f,
                    right = if (isOwn) size.width + tipSizePx else tipSizePx,
                    top = size.height - cornerRadiusPx * 1.5f
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = if(isOwn) 90f else -90f,
                forceMoveTo = false,
            )
        }

        return Outline.Generic(path)
    }
}