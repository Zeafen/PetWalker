package com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberTwoLayerTopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) = remember {
    TwoLayerTopAppBarState(
        initialHeightOffsetLimit,
        initialHeightOffset,
        initialContentOffset
    )
}