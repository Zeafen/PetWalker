package com.zeafen.petwalker.ui.standard.effects

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

fun Modifier.newsDot(dotColor: Color) = composed {
    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
        .drawWithCache {
            onDrawWithContent {
                this.drawContent()

                val dotSize = (minOf(size.width, size.height) / 6f).coerceAtMost(64.dp.toPx())
                drawCircle(
                    color = Color.Black,
                    radius = dotSize,
                    center = Offset(size.width - dotSize, dotSize),
                    blendMode = BlendMode.Clear
                )
                drawCircle(
                    color = dotColor,
                    radius = dotSize * 0.8f,
                    center = Offset(size.width - dotSize, dotSize)
                )
            }
        }
}