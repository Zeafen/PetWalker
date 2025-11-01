package com.zeafen.petwalker.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.isValidEmail
import com.zeafen.petwalker.data.helpers.isValidPassword
import com.zeafen.petwalker.data.helpers.isValidPhoneNumber
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.auth.AuthRequest
import com.zeafen.petwalker.domain.models.api.auth.RegisterRequest
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.AuthRepository
import com.zeafen.petwalker.domain.services.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.invalid_email_format_error_txt
import petwalker.composeapp.generated.resources.invalid_phone_format_error_txt

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())

    val state: StateFlow<AuthUiState> = _state
        .asStateFlow()

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

        _state.distinctUntilChangedBy { it.phone }
            .onEach { state ->
                val isValid = state.phone.isValidPhoneNumber()
                _state.update {
                    it.copy(
                        phoneValid = ValidationInfo(
                            isValid,
                            if (isValid) null else Res.string.invalid_phone_format_error_txt,
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

        _state
            .distinctUntilChanged { old, new ->
                (old.firstName.isNotBlank() == new.firstName.isNotBlank())
                        && (old.lastName.isNotBlank() == new.lastName.isNotBlank())
                        && (old.login == new.login)
                        && old.passwordValid == new.passwordValid && old.repeatPassword == new.repeatPassword
                        && old.emailValid == new.emailValid && old.phoneValid == new.phoneValid
            }
            .onEach { state ->
                _state.update {
                    it.copy(
                        canSignIn = state.login.isNotBlank() && state.passwordValid.isValid,
                        canSignUp = state.firstName.isNotBlank() && state.lastName.isNotBlank()
                                && state.login.length > 10
                                && state.passwordValid.isValid && state.passwordsMatch
                                && state.emailValid.isValid && (state.phone.isBlank() || state.phoneValid.isValid)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private val dataInputMutex = Mutex()
    fun onEvent(event: AuthUiEvent) {
        viewModelScope.launch {
            when (event) {
                is AuthUiEvent.EnterEmail -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(email = event.newEmail)
                        }
                    }
                }

                is AuthUiEvent.EnterFirstName -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(firstName = event.newFirstName)
                        }
                    }
                }

                is AuthUiEvent.EnterLastName -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(lastName = event.newLastName)
                        }
                    }
                }

                is AuthUiEvent.EnterLogin -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(login = event.newLogin)
                        }
                    }
                }

                is AuthUiEvent.EnterPassword -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(password = event.newPassword)
                        }
                    }
                }

                is AuthUiEvent.EnterPhone -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(phone = event.newPhone)
                        }
                    }
                }

                AuthUiEvent.ConfirmSignIn -> {
                    //checking user can sign in
                    if (!state.value.canSignIn) {
                        _state.update {
                            it.copy(
                                result = Error(NetworkError.CONFLICT)
                            )
                        }
                        return@launch
                    }

                    //checking if there are no parallel requests
                    dataInputMutex.withLock {
                        if (state.value.result is Downloading)
                            return@launch
                        _state.update {
                            it.copy(result = Downloading())
                        }
                    }

                    //collecting required data
                    val login = state.value.login
                    val password = state.value.password
                    //requesting for signing in
                    val result = authRepository.signIn(AuthRequest(login, password)).also {
                        if (it is Succeed)
                            it.data?.let { tokenPair ->
                                authDataStore.updateUserToken(tokenPair)
                                val profile = profileRepository.getProfile()
                                if (profile is Succeed)
                                    profile.data?.let { profile ->
                                        authDataStore.updatePersonalData(
                                            profile.firstName,
                                            profile.lastName
                                        )
                                        authDataStore.updateEmail(profile.email)
                                        authDataStore.updateImageUrl(profile.imageUrl)
                                    }
                            }
                    }

                    //updating result
                    _state.update {
                        it.copy(
                            result = if (result is Error) Error(
                                result.info,
                                result.additionalInfo
                            ) else Succeed()
                        )
                    }
                }

                AuthUiEvent.ConfirmSignUp -> {
                    //checking if user can sign up
                    if (!state.value.canSignUp) {
                        _state.update {
                            it.copy(result = Error(NetworkError.CONFLICT))
                        }
                        return@launch
                    }

                    //checking that there are no parallel requests
                    dataInputMutex.withLock {
                        if (state.value.result is Downloading)
                            return@launch
                        _state.update {
                            it.copy(result = Downloading())
                        }
                    }

                    //generating request
                    val request = RegisterRequest(
                        state.value.firstName,
                        state.value.lastName,
                        state.value.login,
                        state.value.password,
                        state.value.email,
                        state.value.phone.ifBlank { null }
                    )

                    //sending sign up request
                    val signUpResult = authRepository.signUp(request)
                    if (signUpResult is Error) {
                        _state.update {
                            it.copy(result = Error(signUpResult.info))
                        }
                    }

                    //authorizing user
                    val result =
                        authRepository.signIn(AuthRequest(request.login, request.password)).also {
                            if (it is Succeed)
                                it.data?.let { tokenPair ->
                                    authDataStore.updateUserToken(tokenPair)
                                    val profile =
                                        profileRepository.getProfile()
                                    if (profile is Succeed)
                                        profile.data?.let { profile ->
                                            authDataStore.updatePersonalData(
                                                profile.firstName,
                                                profile.lastName
                                            )
                                            authDataStore.updateEmail(profile.email)
                                            authDataStore.updateImageUrl(profile.imageUrl)
                                        }
                                }
                        }

                    //updating result
                    _state.update {
                        it.copy(
                            result = if (result is Error) Error(
                                result.info,
                                result.additionalInfo
                            ) else Succeed()
                        )
                    }
                }

                AuthUiEvent.Authorize -> {
                    dataInputMutex.withLock {
                        if (state.value.result is Downloading)
                            return@launch
                        _state.update {
                            it.copy(
                                result = Downloading()
                            )
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(result = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val res = authRepository.authorize()
                    _state.update {
                        it.copy(result = res)
                    }
                }

                AuthUiEvent.ClearResult -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(result = null)
                        }
                    }
                }

                is AuthUiEvent.EnterRepeatPassword -> {
                    dataInputMutex.withLock {
                        _state.update {
                            it.copy(
                                repeatPassword = event.repeatPassword
                            )
                        }
                    }
                }
            }
        }
    }
}
