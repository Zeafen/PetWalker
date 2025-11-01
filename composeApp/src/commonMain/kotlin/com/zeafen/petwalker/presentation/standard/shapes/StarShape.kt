package com.zeafen.petwalker.presentation.standard.shapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class StarShape(
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width * 0.1f, size.height)
            lineTo(size.width * 0.25f, size.height * 0.625f)
            lineTo(0f, size.height * 0.375f)
            lineTo(size.width * 0.35f, size.height * 0.375f)
            lineTo(size.width * 0.5f, 0f)
            lineTo(size.width * 0.65f, size.height * 0.375f)
            lineTo(size.width, size.height * 0.375f)
            lineTo(size.width * 0.75f, size.height * 0.625f)
            lineTo(size.width * 0.9f, size.height)
            lineTo(size.width * 0.5f, size.height * 0.8f)
            lineTo(size.width * 0.1f, size.height)
            close()
        }

        return Outline.Generic(path)
    }
}