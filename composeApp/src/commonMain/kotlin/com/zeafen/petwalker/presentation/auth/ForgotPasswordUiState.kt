package com.zeafen.petwalker.presentation.auth

import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.forgot_password_page_slogan_first
import petwalker.composeapp.generated.resources.forgot_password_page_slogan_second
import petwalker.composeapp.generated.resources.forgot_password_page_slogan_third

data class ForgotPasswordUiState(
    val email: String = "",
    val emailValid: ValidationInfo = ValidationInfo(false, null, emptyList()),
    val code: String = "",
    val password: String = "",
    val passwordValid: ValidationInfo = ValidationInfo(true, null, emptyList()),
    val repeatPassword: String = "",
    val passwordsMatch: Boolean = false,
    val canChangePassword: Boolean = false,
    val stage: ForgotPasswordStage = ForgotPasswordStage.SendCode,
    val result: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null
)

enum class ForgotPasswordStage(val slogan: StringResource) {
    SendCode(Res.string.forgot_password_page_slogan_first),
    ConfirmCode(Res.string.forgot_password_page_slogan_second),
    ChangePassword(Res.string.forgot_password_page_slogan_third)
}