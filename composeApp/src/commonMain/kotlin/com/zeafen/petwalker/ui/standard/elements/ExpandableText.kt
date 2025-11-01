package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.more_label
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun PetWalkerExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    maxLinesWrapped: Int = 2,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    wrappedOverflow: TextOverflow = TextOverflow.Ellipsis,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    val measurer = rememberTextMeasurer()
    var expandText by remember {
        mutableStateOf(false)
    }
    Layout(
        content = {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .layoutId("text"),
                content = {
                    AnimatedContent(
                        targetState = expandText
                    ) { expandReview ->
                        when {
                            expandReview -> {
                                Text(
                                    text = text,
                                    style = style,
                                    textAlign = textAlign,
                                    fontWeight = fontWeight
                                )
                            }

                            !expandReview -> {
                                Text(
                                    text = text,
                                    style = style,
                                    textAlign = textAlign,
                                    maxLines = maxLinesWrapped,
                                    overflow = wrappedOverflow
                                )
                            }
                        }
                    }
                }
            )
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .layoutId("expand_btn"),
                content = {
                    PetWalkerLinkTextButton(
                        text = stringResource(Res.string.more_label),
                        onClick = { expandText = !expandText }
                    )
                }
            )
        },
        modifier = modifier
            .clipToBounds()
    ) { measurables, constraints ->
        val textPlaceable = measurables.firstOrNull { it.layoutId == "text" }
            ?.measure(constraints.copy(minWidth = 0))
        var expandBtnPlaceable: Placeable? = null


        if (measurer.measure(
                text = text, style = style,
                constraints = constraints.copy(minWidth = 0)
            ).lineCount > 2
        )
            expandBtnPlaceable = measurables.firstOrNull { it.layoutId == "expand_btn" }
                ?.measure(constraints.copy(minWidth = 0))

        val btnXOffset = max(
            constraints.maxWidth - (expandBtnPlaceable?.width ?: 0),
            0
        )
        val btnYOffset =
            ((textPlaceable?.height ?: 0) + OFFSET.toPx()).roundToInt()

        val totalHeight = btnYOffset + (expandBtnPlaceable?.height ?: 0)

        layout(constraints.maxWidth, totalHeight) {
            textPlaceable?.placeRelative(0, 0)
            expandBtnPlaceable?.placeRelative(btnXOffset, btnYOffset)
        }
    }
}

private val OFFSET = 2.dp
