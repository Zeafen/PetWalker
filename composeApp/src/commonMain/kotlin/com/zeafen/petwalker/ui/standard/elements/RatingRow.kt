package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.standard.shapes.StarShape

/**
 * Composes view-only stars rating row
 * @param modifier Modifier applied to row
 * @param currentRating selected rating value
 * @param maxRating maximum rating value
 * @param starSize defines star element size
 * @param starColor defines star element color
 * @param itemsPadding star items spacing
 */
@Composable
fun RatingRow(
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    currentRating: Float,
    starColor: Color = MaterialTheme.colorScheme.tertiary,
    starSize: Dp = 32.dp,
    itemsPadding: PaddingValues = PaddingValues(horizontal = 4.dp),
) {
    val floatingIndex = (currentRating * 10).toInt().div(10)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxRating) {
            Box(
                modifier = Modifier
                    .padding(itemsPadding)
                    .size(starSize)
                    .border(BorderStroke(1.dp, starColor), shape = StarShape())
                    .padding(1.dp)
                    .clip(StarShape())
                    .background(
                        brush = Brush.horizontalGradient(
                            colorStops =
                                if (currentRating > it && it != floatingIndex)
                                    arrayOf(
                                        1.0f to starColor,
                                        1.0f to Color.Transparent
                                    )
                                else if (it == floatingIndex)
                                    arrayOf(
                                        currentRating - floatingIndex to starColor,
                                        currentRating - floatingIndex to Color.Transparent
                                    )
                                else
                                    arrayOf(
                                        0f to starColor,
                                        0f to Color.Transparent
                                    )
                        ),
                        shape = StarShape()
                    )
            )
        }
    }
}

/**
 * Composes selectable stars rating row
 * @param modifier Modifier applied to row
 * @param currentRating selected rating value
 * @param maxRating maximum rating value
 * @param starSize defines star element size
 * @param starColor defines star element color
 * @param itemsPadding star items spacing
 * @param onStarClick called when users click on a certain star button
 */
@Composable
fun RatingRow(
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starColor: Color = MaterialTheme.colorScheme.tertiary,
    starSize: Dp = 48.dp,
    itemsPadding: PaddingValues = PaddingValues(horizontal = 4.dp),
    onStarClick: (rating: Int) -> Unit,
    currentRating: Int
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxRating) {
            Button(
                modifier = Modifier
                    .padding(itemsPadding)
                    .size(starSize),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentRating > it) starColor
                    else Color.Transparent,
                ),
                border = BorderStroke(1.dp, starColor),
                onClick = { onStarClick(it + 1) },
                shape = StarShape(),
                content = {}
            )
        }
    }
}
