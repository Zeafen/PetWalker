package com.zeafen.petwalker.presentation.auth

sealed interface ForgotPasswordUiEvent {
    data class EnterEmail(val newEmail: String): ForgotPasswordUiEvent
    data class EnterCode(val newCode: String): ForgotPasswordUiEvent
    data class EnterPassword(val newPassword: String): ForgotPasswordUiEvent
    data class EnterRepeatPassword(val repeatPassword: String): ForgotPasswordUiEvent
    data object SendCode: ForgotPasswordUiEvent
    data object ConfirmCode: ForgotPasswordUiEvent
    data object ChangePassword: ForgotPasswordUiEvent
    data object ClearResult: ForgotPasswordUiEvent
}