package com.zeafen.petwalker.domain.models

import org.jetbrains.compose.resources.StringResource

data class ValidationInfo(
    val isValid: Boolean,
    val errorResId: StringResource?,
    val formatArgs: List<Any>
)
