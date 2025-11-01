package com.zeafen.petwalker.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.isValidEmail
import com.zeafen.petwalker.data.helpers.isValidPassword
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.invalid_email_format_error_txt

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ForgotPasswordUiState> =
        MutableStateFlow(ForgotPasswordUiState())

    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    init {
        _state.distinctUntilChangedBy { it.email }
            .onEach { state ->
                val isValid = state.email.isValidEmail()
                _state.update {
                    it.copy(
                        emailValid = ValidationInfo(
                            isValid,
                            if (isValid) null else Res.string.invalid_email_format_error_txt,
                            emptyList()
                        )
                    )
                }
            }
            .launchIn(viewModelScope)

        _state.distinctUntilChangedBy { it.password }
            .onEach { state ->
                _state.update {
                    it.copy(
                        passwordValid = state.password.isValidPassword()
                    )
                }
            }
            .launchIn(viewModelScope)

        _state.distinctUntilChangedBy { it.repeatPassword }
            .onEach { state ->
                _state.update {
                    it.copy(
                        passwordsMatch = state.password == state.repeatPassword
                    )
                }
            }
            .launchIn(viewModelScope)

        _state.distinctUntilChanged { old, new ->
            old.passwordValid == new.passwordValid && old.passwordsMatch == new.passwordsMatch
                    && old.emailValid == new.emailValid && (old.code.isNotBlank() == new.code.isNotBlank())
        }
            .onEach { state ->
                _state.update {
                    it.copy(
                        canChangePassword = state.emailValid.isValid && state.code.isNotBlank()
                                && state.passwordValid.isValid && state.passwordsMatch
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private val dataInputMutex = Mutex()
    fun onEvent(event: ForgotPasswordUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ForgotPasswordUiEvent.EnterCode -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(code = event.newCode)
                        }
                    }
                }

                is ForgotPasswordUiEvent.EnterEmail -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(email = event.newEmail)
                        }
                    }
                }

                is ForgotPasswordUiEvent.EnterPassword -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(password = event.newPassword)
                        }
                    }
                }

                is ForgotPasswordUiEvent.EnterRepeatPassword -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(repeatPassword = event.repeatPassword)
                        }
                    }
                }

                ForgotPasswordUiEvent.SendCode -> {
                    //checking if user can send confirmation code
                    if (!state.value.emailValid.isValid) {
                        _state.update {
                            it.copy(
                                result = APIResult.Error(NetworkError.NOT_FOUND)
                            )
                        }
                        return@launch
                    }

                    //sending confirmation code
                    val result = authRepository.getEmailCode(state.value.email)
                    if (result is APIResult.Error) {
                        _state.update {
                            it.copy(result = result)
                        }
                        return@launch
                    }

                    //updating current stage
                    _state.update {
                        it.copy(
                            stage = ForgotPasswordStage.ConfirmCode
                        )
                    }
                }

                ForgotPasswordUiEvent.ConfirmCode -> {
                    if (state.value.code.isBlank()) {
                        _state.update {
                            it.copy(
                                result = APIResult.Error(NetworkError.UNKNOWN)
                            )
                        }
                        return@launch
                    }

                    //validating confirmation code
                    val result =
                        authRepository.checkConfirmationCode(state.value.email, state.value.code)
                    if (result is APIResult.Error) {
                        _state.update {
                            it.copy(result = result)
                        }
                        return@launch
                    }

                    //updating current stage
                    _state.update {
                        it.copy(
                            stage = ForgotPasswordStage.ChangePassword
                        )
                    }
                }

                ForgotPasswordUiEvent.ChangePassword -> {
                    //checking if user can send confirmation code
                    if (!state.value.canChangePassword) {
                        _state.update {
                            it.copy(
                                result = APIResult.Error(NetworkError.UNKNOWN)
                            )
                        }
                        return@launch
                    }

                    //updating current stage
                    _state.update {
                        it.copy(
                            result = authRepository.updatePassword(
                                state.value.email,
                                state.value.code,
                                state.value.password
                            )
                        )
                    }
                }

                ForgotPasswordUiEvent.ClearResult -> _state.update {
                    it.copy(
                        result = null
                    )
                }
            }
        }
    }
}