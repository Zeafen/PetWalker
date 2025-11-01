package com.zeafen.petwalker.presentation.auth

sealed interface AuthUiEvent {
    data class EnterLogin(val newLogin: String) : AuthUiEvent
    data class EnterEmail(val newEmail: String) : AuthUiEvent
    data class EnterPhone(val newPhone: String) : AuthUiEvent
    data class EnterPassword(val newPassword: String) : AuthUiEvent
    data class EnterRepeatPassword(val repeatPassword: String) : AuthUiEvent
    data class EnterFirstName(val newFirstName: String) : AuthUiEvent
    data class EnterLastName(val newLastName: String) : AuthUiEvent
    data object ConfirmSignIn : AuthUiEvent

    data object Authorize: AuthUiEvent
    data object ConfirmSignUp : AuthUiEvent

    data object ClearResult: AuthUiEvent
}