package com.zeafen.petwalker.presentation.standard.filtering

import androidx.compose.runtime.Composable

sealed interface FilteringTypes {
    data class ListingType(
        val availableOptions: List<Any>,
        val selectedOptions: List<Any> = emptyList(),
        val singleSelection: Boolean = false,
        val optionContent: @Composable (Any) -> Unit
    ) : FilteringTypes

    data class TextType(
        val text: String = ""
    ) : FilteringTypes

    data class IntType(
        val num: String = "",
        val canBeConverted: Boolean = false
    ) : FilteringTypes

    data class FloatType(
        val num: String = "",
        val canBeConverted: Boolean = false
    ) : FilteringTypes

    data class BooleanType(
        val selected: Boolean = false
    ) : FilteringTypes

    data class DateOption(
        val date: Long? = null
    ) : FilteringTypes
}