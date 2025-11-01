package com.zeafen.petwalker.data.helpers

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun Float.format(placeAfterDot: Int): String {
    val formatter = NSNumberFormatter()
        .apply {
            allowsFloats = true
            numberStyle = NSNumberFormatterDecimalStyle
            minimumFractionDigits = placeAfterDot.toULong()
            maximumFractionDigits = placeAfterDot.toULong()
        }
    return formatter.stringFromNumber(NSNumber(float = this)) ?: this.toString()
}