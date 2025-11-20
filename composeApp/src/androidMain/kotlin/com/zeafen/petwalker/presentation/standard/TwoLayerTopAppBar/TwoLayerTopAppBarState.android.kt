package com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal actual fun rememberTwoLayerTopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) = rememberSaveable(saver = TwoLayerTopAppBarState.Saver) {
    TwoLayerTopAppBarState(
        initialHeightOffsetLimit,
        initialHeightOffset,
        initialContentOffset
    )
}