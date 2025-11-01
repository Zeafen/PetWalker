package com.zeafen.petwalker.presentation.auth

import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult

data class AuthUiState(
    val firstName: String = "",
    val lastName: String = "",
    val login: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val email: String = "",
    val phone: String = "",
    val passwordValid: ValidationInfo = ValidationInfo(true, null, emptyList()),
    val passwordsMatch: Boolean = true,
    val emailValid: ValidationInfo = ValidationInfo(true, null, emptyList()),
    val phoneValid: ValidationInfo = ValidationInfo(true, null, emptyList()),
    val canSignIn: Boolean = false,
    val canSignUp: Boolean = false,
    val result: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null,
)
