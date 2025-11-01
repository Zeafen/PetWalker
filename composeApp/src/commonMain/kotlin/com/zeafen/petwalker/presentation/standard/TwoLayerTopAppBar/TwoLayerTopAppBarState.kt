package com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class TwoLayerTopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) {
    companion object {
        val Saver: Saver<TwoLayerTopAppBarState, *> = mapSaver(
            save = {
                mapOf(
                    OffsetLimitKey to it.heightOffsetLimit,
                    HeightOffsetKey to it.heightOffset,
                    ContentOffsetKey to it.contentOffset
                )
            },
            restore = {
                TwoLayerTopAppBarState(
                    it[OffsetLimitKey] as Float,
                    it[HeightOffsetKey] as Float,
                    it[ContentOffsetKey] as Float
                )
            }
        )
        private const val OffsetLimitKey = "two-layer-offset-limit"
        private const val HeightOffsetKey = "two-layer-offset-height"
        private const val ContentOffsetKey = "two-layer-offset-content"
    }

    var heightOffsetLimit by mutableFloatStateOf(initialHeightOffsetLimit)

    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(value) {
            _heightOffset.floatValue = value.coerceIn(
                minimumValue = heightOffsetLimit,
                maximumValue = 1f
            )
        }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)

    var contentOffset by mutableFloatStateOf(initialContentOffset)

    val collapsedFraction: Float
        get() {
            return if (heightOffsetLimit != 0f)
                heightOffset / heightOffsetLimit
            else 0f
        }
}

@Composable
internal fun rememberTwoLayerTopAppBarState(
    initialHeightOffsetLimit: Float = 0f,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f
) = rememberSaveable(saver = TwoLayerTopAppBarState.Saver) {
    TwoLayerTopAppBarState(
        initialHeightOffsetLimit,
        initialHeightOffset,
        initialContentOffset
    )
}