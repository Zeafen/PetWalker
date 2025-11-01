package com.zeafen.petwalker.data.helpers

import java.text.DecimalFormat

actual fun Float.format(placeAfterDot: Int): String {
    return DecimalFormat("#.##").format(this)
}