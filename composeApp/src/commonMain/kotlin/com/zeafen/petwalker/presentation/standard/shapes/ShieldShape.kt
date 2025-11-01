package com.zeafen.petwalker.presentation.standard.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

object ShieldShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.5f, size.height)
            arcTo(
                Rect(
                    topLeft = Offset(0f, -size.height * 0.6f),
                    bottomRight = Offset(size.width, size.height)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            arcTo(
                Rect(
                    topLeft = Offset(size.width * 0.5f, -size.height * 0.2f),
                    bottomRight = Offset(size.width * 1.5f, size.height * 0.2f)
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            arcTo(
                Rect(
                    topLeft = Offset(-size.width * 0.5f, -size.height * 0.2f),
                    bottomRight = Offset(size.width * 0.5f, size.height * 0.2f)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
            arcTo(
                Rect(
                    topLeft = Offset(0f, -size.height * 0.6f),
                    bottomRight = Offset(size.width, size.height)
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )
        }

        return Outline.Generic(path)
    }
}