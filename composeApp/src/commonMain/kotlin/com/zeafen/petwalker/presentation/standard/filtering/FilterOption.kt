package com.zeafen.petwalker.presentation.standard.filtering

import org.jetbrains.compose.resources.StringResource

data class FilterOption(
    val name: StringResource,
    val enabled: Boolean = false,
    val value: FilteringTypes
)
