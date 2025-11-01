package com.zeafen.petwalker.data.helpers

actual fun Float.format(placeAfterDot: Int): String {
    return String.format("%.${placeAfterDot}f", this)
}