package com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

class TwoLayerTopAppBarScrollBehaviour(
    val state: TwoLayerTopAppBarState,
    val flingAnimationSpec: DecayAnimationSpec<Float>?
) {
    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val prevHeightOffset = state.heightOffset
            state.heightOffset += available.y
            return if (prevHeightOffset == state.heightOffset)
                Offset.Zero
            else available.copy(x = 0f)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            state.contentOffset += consumed.y
            if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                if (consumed.y == 0f && available.y > 0f) {
                    state.contentOffset = 0f
                }
            }
            state.heightOffset += consumed.y
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            snapToolbar(state)
            return superConsumed +
                    flingToolbar(state, available.y, flingAnimationSpec)
        }
    }
}

private suspend fun flingToolbar(
    state: TwoLayerTopAppBarState,
    initialVelocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
): Velocity {
    var remainingVelocity = initialVelocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(initialVelocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = initialVelocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) {
                    cancelAnimation()
                }
            }
    }
    return Velocity(0f, remainingVelocity)
}

private suspend fun snapToolbar(state: TwoLayerTopAppBarState) {
    // In case the app bar motion was stopped in a state where it's partially visible, snap it to
    // the nearest state.
    if (state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(
            initialValue = state.heightOffset
        ).animateTo(
            targetValue = if (state.collapsedFraction < 0.4f) 0f else state.heightOffsetLimit,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy)
        ) {
            state.heightOffset = value
        }
    }
}

@Composable
fun rememberTwoLayerTopAppBarScrollBehavior() = TwoLayerTopAppBarScrollBehaviour(
    state = rememberTwoLayerTopAppBarState(
        initialHeightOffsetLimit = 0f
    ),
    flingAnimationSpec = rememberSplineBasedDecay()
)