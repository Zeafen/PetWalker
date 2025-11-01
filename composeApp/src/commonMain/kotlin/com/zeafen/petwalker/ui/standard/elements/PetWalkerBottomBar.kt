package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.standard.navigation.NavStop
import com.zeafen.petwalker.presentation.standard.navigation.NavigationRoutes
import org.jetbrains.compose.resources.painterResource
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetWalkerBottomBar(
    modifier: Modifier = Modifier,
    centerButton: @Composable () -> Unit,
    navStops: List<NavStop>? = null,
    onNavStopClick: ((NavigationRoutes) -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
) {
    val localDensity = LocalDensity.current
    var centerBtnRadius = remember { with(localDensity) { 32.dp.toPx() } }
    Surface {
        Layout(
            content = {
                if (!navStops.isNullOrEmpty()) {
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("actionsL")
                    ) {
                        CompositionLocalProvider(
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    content = {
                                        navStops.subList(
                                            0,
                                            (navStops.size / 2f).roundToInt()
                                        ).forEach {
                                            OutlinedIconButton(
                                                onClick = { onNavStopClick?.invoke(it.route) }
                                            ) {
                                                Icon(
                                                    painter = painterResource(it.iconRes),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                    Box(
                        Modifier
                            .wrapContentSize()
                            .layoutId("actionsR")
                    ) {
                        CompositionLocalProvider(
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    content = {
                                        navStops.subList(
                                            (navStops.size / 2f).roundToInt(),
                                            navStops.size
                                        ).forEach {
                                            OutlinedIconButton(
                                                onClick = { onNavStopClick?.invoke(it.route) }
                                            ) {
                                                Icon(
                                                    painter = painterResource(it.iconRes),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                }


                Box(
                    Modifier
                        .wrapContentSize()
                        .layoutId("centerBtn")
                ) {
                    CompositionLocalProvider(
                        content = centerButton
                    )
                }
            },
            modifier = Modifier
                .windowInsetsPadding(windowInsets)
                .clipToBounds().graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawBehind {
                    drawRoundRect(
                        containerColor,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        size = Size(
                            size.width / 2 - centerBtnRadius.roundToInt(),
                            size.height
                        )
                    )
                    drawRoundRect(
                        containerColor,
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        topLeft = Offset(
                            size.width / 2f + centerBtnRadius,
                            0f
                        ),
                        size = Size(
                            size.width / 2 - centerBtnRadius.roundToInt(),
                            size.height
                        )
                    )

                    drawCircle(
                        color = Color.Black,
                        radius = centerBtnRadius + 8.dp.toPx(),
                        center = Offset(size.width / 2f, size.height / 2f),
                        blendMode = BlendMode.Clear
                    )
                }
        ) { measurables, constraints ->
            val centerBtnPlaceable = measurables.firstOrNull { it.layoutId == "centerBtn" }
                ?.measure(constraints.copy(minWidth = 0))?.also {
                    val newRadius = it.width / 2f
                    if (centerBtnRadius != newRadius)
                        centerBtnRadius = newRadius
                }

            val actionsTabsMaxWidth = (constraints.maxWidth - (centerBtnPlaceable?.width
                ?: 0)) / 2f - STANDARD_PADDING.toPx() - CENTER_BTN_PADDING.toPx()

            val actionsRPlaceable = measurables.firstOrNull { it.layoutId == "actionsR" }
                ?.measure(
                    constraints.copy(
                        maxWidth = actionsTabsMaxWidth.roundToInt(),
                        minWidth = actionsTabsMaxWidth.roundToInt() - 1
                    )
                )
            val actionsLPlaceable = measurables.firstOrNull { it.layoutId == "actionsL" }
                ?.measure(
                    constraints.copy(
                        maxWidth = actionsTabsMaxWidth.roundToInt(),
                        minWidth = actionsTabsMaxWidth.roundToInt() - 1
                    )
                )

            val centerBtnLineHeight = max(
                40.dp.toPx(),
                centerBtnPlaceable?.height?.toFloat() ?: 40.dp.toPx()
            ).roundToInt()

            val centerBtnY = (centerBtnLineHeight - (centerBtnPlaceable?.height
                ?: 0)) / 2 + STANDARD_PADDING.toPx().roundToInt()
            val centerBtnX =
                (constraints.maxWidth - (centerBtnPlaceable?.width ?: 0)) / 2

            val actionsRY = (centerBtnLineHeight - (actionsRPlaceable?.height
                ?: 0)) / 2 + STANDARD_PADDING.toPx().roundToInt()
            val actionsRX = centerBtnX + (centerBtnPlaceable?.width ?: 0) +
                    CENTER_BTN_PADDING.toPx().roundToInt()

            val actionsLY = (centerBtnLineHeight - (actionsLPlaceable?.height
                ?: 0)) / 2 + STANDARD_PADDING.toPx().roundToInt()
            val actionsLX = STANDARD_PADDING.toPx().roundToInt()

            val appBarHeight = centerBtnLineHeight + STANDARD_PADDING.toPx().roundToInt() * 2
            layout(constraints.maxWidth, appBarHeight) {
                centerBtnPlaceable?.placeRelative(centerBtnX, centerBtnY)
                actionsRPlaceable?.placeRelative(actionsRX, actionsRY)
                actionsLPlaceable?.placeRelative(actionsLX, actionsLY)
            }
        }
    }
}

internal val STANDARD_PADDING = 4.dp
internal val CENTER_BTN_PADDING = 12.dp