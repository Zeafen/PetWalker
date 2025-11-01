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

class CommentaryBubbleShape(
    private val cornerRadius: Dp = 16.dp,
    private val tipSize: Dp = 12.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tipSizePx = with(density) { tipSize.toPx() }
        val cornerRadiusPx = with(density) { cornerRadius.toPx() }
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = tipSizePx,
                    top = 0f,
                    bottom = size.height,
                    right = size.width,
                    topRightCornerRadius = CornerRadius(
                        cornerRadiusPx,
                        cornerRadiusPx
                    ),
                    bottomLeftCornerRadius = CornerRadius(
                        cornerRadiusPx,
                        cornerRadiusPx
                    ),
                    bottomRightCornerRadius = CornerRadius(
                        cornerRadiusPx,
                        cornerRadiusPx
                    ),
                )
            )
            moveTo(
                0f,
                0f
            )
            lineTo(tipSizePx, 0f)
            arcTo(
                Rect(
                    left = -tipSizePx,
                    top = 0f,
                    bottom = tipSizePx * 2,
                    right = tipSizePx
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -90f,
                forceMoveTo = false
            )
            close()
        }

        return Outline.Generic(path)
    }

}