@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.zeafen.petwalker.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.clearBearerTokenValues
import com.zeafen.petwalker.data.helpers.isValidEmail
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.ProfileRequest
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ProfileRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_least_error
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.invalid_email_format_error_txt
import petwalker.composeapp.generated.resources.login_taken_error_txt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProfilePageViewModel(
    private val profileRepository: ProfileRepository,
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val reviewsRepository: ReviewsRepository,
    private val httpClient: HttpClient,
) : ViewModel() {

    private val _state: MutableStateFlow<ProfilePageUiState> =
        MutableStateFlow(ProfilePageUiState())
    val state: StateFlow<ProfilePageUiState> = _state.asStateFlow()

    private val _exitAccount: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exitAccount: StateFlow<Boolean> = _exitAccount.asStateFlow()

    private var loginCheck: Job? = null

    init {
        state
            .distinctUntilChangedBy { it.email }
            .onEach { value ->
                _state.update {
                    it.copy(
                        emailValid = when {
                            value.email.isBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            !value.email.isValidEmail() ->
                                ValidationInfo(
                                    false,
                                    Res.string.invalid_email_format_error_txt,
                                    emptyList()
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.firstName }
            .onEach { value ->
                _state.update {
                    it.copy(
                        firstNameValid = when {
                            value.firstName.isBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            value.firstName.length > 200 ->
                                ValidationInfo(
                                    false,
                                    Res.string.incorrect_length_max_error,
                                    listOf(200)
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.lastName }
            .onEach { value ->
                _state.update {
                    it.copy(
                        lastNameValid = when {
                            value.lastName.isBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            value.lastName.length > 200 ->
                                ValidationInfo(
                                    false,
                                    Res.string.incorrect_length_max_error,
                                    listOf(200)
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.aboutMe }
            .onEach { value ->
                _state.update {
                    it.copy(
                        aboutMeValid = when {
                            value.aboutMe.length > 500 ->
                                ValidationInfo(
                                    false,
                                    Res.string.incorrect_length_max_error,
                                    listOf(500)
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.code }
            .onEach { value ->
                _state.update {
                    it.copy(
                        codeValid = when {
                            value.code.length != 5 ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.login }
            .onEach { value ->
                if (loginCheck?.isActive == true)
                    loginCheck?.cancel()

                loginCheck = viewModelScope.launch {
                    val token = authDataStore.authDataStoreFlow.first().token
                    val loginTaken = token?.let {
                        val result = usersRepository.loginExists(value.login)
                        if (result is APIResult.Succeed)
                            result.data
                        else null
                    } ?: false

                    _state.update {
                        it.copy(
                            loginValid = when {
                                value.login.length < 10 ->
                                    ValidationInfo(
                                        false,
                                        Res.string.incorrect_length_least_error,
                                        listOf(10)
                                    )

                                value.login.length > 50 ->
                                    ValidationInfo(
                                        false,
                                        Res.string.incorrect_length_max_error,
                                        listOf(50)
                                    )

                                loginTaken ->
                                    ValidationInfo(
                                        false,
                                        Res.string.login_taken_error_txt,
                                        emptyList()
                                    )

                                else -> ValidationInfo(true, null, emptyList())
                            }
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChanged { old, new ->
                old.firstNameValid == new.firstNameValid &&
                        old.lastNameValid == new.lastNameValid &&
                        old.aboutMeValid == new.aboutMeValid &&
                        old.loginValid == new.loginValid
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canEditInfo = value.firstNameValid.isValid &&
                                value.lastNameValid.isValid && value.aboutMeValid.isValid
                                && value.loginValid.isValid
                    )
                }
            }
            .launchIn(viewModelScope)

        onEvent(ProfilePageUiEvent.LoadProfile)
    }

    private var profileEditingJob: Job? = null
    private var emailEditingJob: Job? = null
    private val inputMutex = Mutex()
    fun onEvent(event: ProfilePageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfilePageUiEvent.AddService -> {
                    inputMutex.withLock {
                        if (state.value.services.none { it.service == event.serviceType && it.service != ServiceType.Other })
                            _state.update {
                                it.copy(
                                    services = it.services + UserService(
                                        Uuid.random().toString(),
                                        event.serviceType,
                                        event.additionalInfo,
                                        event.payment
                                    )
                                )
                            }
                    }
                }

                ProfilePageUiEvent.CancelEditing -> {
                    _state.update {
                        it.copy(
                            firstName = it.profile?.firstName ?: "",
                            lastName = it.profile?.lastName ?: "",
                            aboutMe = it.profile?.aboutMe ?: "",
                            services = it.profile?.services ?: emptyList(),
                            imageUrl = PetWalkerFileInfo(it.profile?.imageUrl, "", "image", null),
                            email = it.profile?.email ?: "",
                            code = "",
                            login = it.profile?.login ?: "",
                            showAsWalker = it.profile?.showAsWalker ?: false
                        )
                    }
                }

                ProfilePageUiEvent.ConfirmEmail -> {
                    if (emailEditingJob?.isActive == true)
                        emailEditingJob?.cancel()
                    emailEditingJob = launch {
                        _state.update {
                            it.copy(
                                emailEditingResult = APIResult.Downloading()
                            )
                        }

                        //verifying request
                        if (!state.value.canConfirmEmail) {
                            _state.update {
                                it.copy(
                                    emailEditingResult = APIResult.Error(NetworkError.CONFLICT)
                                )
                            }
                            return@launch
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    emailEditingResult = APIResult.Error(NetworkError.UNAUTHORIZED)
                                )
                            }
                            return@launch
                        }

                        //confirming email
                        val result = profileRepository.confirmEmail(
                            state.value.code
                        )
                        _state.update {
                            it.copy(
                                emailEditingResult = result
                            )
                        }
                    }
                }

                ProfilePageUiEvent.DeleteAccount -> {
                    profileEditingJob?.cancel()
                    profileEditingJob = launch {
                        _state.update {
                            it.copy(
                                profileLoadingResult = APIResult.Downloading()
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(profileLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val result = profileRepository.deleteProfile()
                        if (result is APIResult.Error) {
                            _state.update {
                                it.copy(profileLoadingResult = result)
                            }
                            return@launch
                        } else {
                            authDataStore.clearData()
                            httpClient.clearBearerTokenValues()
                            _exitAccount.update { true }
                        }
                    }
                }

                is ProfilePageUiEvent.EditService -> {
                    val newList = state.value.services.toMutableList()
                    if (newList.any { it.service == event.serviceType && it.id != event.id && it.service != ServiceType.Other })
                        return@launch

                    newList.firstOrNull { it.id == event.id }?.let { userService ->
                        newList[newList.indexOf(userService)] = userService.copy(
                            service = event.serviceType,
                            additionalInfo = event.additionalInfo,
                            payment = event.payment
                        )
                        _state.update {
                            it.copy(
                                services = newList
                            )
                        }
                    }
                }

                ProfilePageUiEvent.ExitAccount -> {
                    authDataStore.clearData()
                    httpClient.clearBearerTokenValues()
                    _exitAccount.update { true }
                }

                ProfilePageUiEvent.GoToPrevTab -> {
                    _state.update {
                        it.copy(
                            selectedTab = it.previousTab ?: ProfileTabs.Main,
                            previousTab = null
                        )
                    }
                }

                ProfilePageUiEvent.PublishEditedInfo -> {
                    if (profileEditingJob?.isActive == true)
                        profileEditingJob?.cancel()
                    profileEditingJob = launch {
                        _state.update {
                            it.copy(
                                profileLoadingResult = APIResult.Downloading()
                            )
                        }

                        //verifying request
                        if (!state.value.canEditInfo) {
                            _state.update {
                                it.copy(
                                    profileLoadingResult = APIResult.Error(NetworkError.CONFLICT)
                                )
                            }
                            return@launch
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    profileLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED)
                                )
                            }
                            return@launch
                        }

                        val editedFirstName = state.value.firstName
                        val editedLastName = state.value.lastName
                        val editedAboutMe = state.value.aboutMe
                        val editedLogin = state.value.login
                        val editedServices = state.value.services
                        val editedDesiredPayment = state.value.desiredPayment
                        val editedShowAsWalker = state.value.showAsWalker

                        //editing profile
                        val imageRes =
                            async {
                                if (state.value.profile!!.imageUrl != state.value.imageUrl?.path)
                                    state.value.imageUrl?.let {
                                        profileRepository.postImage(
                                            it
                                        )
                                    }
                                else null
                            }

                        val services =
                            async {
                                profileRepository.postServices(
                                    editedServices
                                )
                            }

                        val profileResult =
                            async {
                                profileRepository.updateProfile(
                                    ProfileRequest(
                                        editedLogin,
                                        editedFirstName,
                                        editedLastName,
                                        editedAboutMe,
                                        editedShowAsWalker,
                                        editedDesiredPayment,
                                    )
                                )
                            }

                        awaitAll(
                            imageRes,
                            services,
                            profileResult
                        ).firstOrNull { it is APIResult.Error }?.let { result ->
                            _state.update {
                                it.copy(
                                    profileLoadingResult = APIResult.Error((result as APIResult.Error).info)
                                )
                            }
                            return@launch
                        }

                        _state.update {
                            it.copy(
                                profile = state.value.profile?.copy(
                                    firstName = editedFirstName,
                                    lastName = editedLastName,
                                    login = editedLogin,
                                    aboutMe = editedAboutMe,
                                    services = editedServices,
                                    imageUrl = (imageRes.await() as APIResult.Succeed?)?.data
                                ),
                                imageUrl = PetWalkerFileInfo(
                                    (imageRes.await() as APIResult.Succeed?)?.data,
                                    "",
                                    "",
                                    null
                                ),
                                profileLoadingResult = APIResult.Succeed()
                            )
                        }
                    }
                }

                is ProfilePageUiEvent.RemoveService -> {
                    state.value.services.firstOrNull { it.id == event.id }?.let { userService ->
                        _state.update {
                            it.copy(
                                services = it.services.minus(userService)
                            )
                        }
                    }
                }

                ProfilePageUiEvent.SendConfirmationCode -> {
                    if (emailEditingJob?.isActive == true)
                        return@launch
                    emailEditingJob = launch {
                        _state.update {
                            it.copy(emailEditingResult = APIResult.Downloading())
                        }

                        //verifying request
                        if (!state.value.emailValid.isValid) {
                            _state.update {
                                it.copy(emailEditingResult = APIResult.Error(NetworkError.CONFLICT))
                            }
                            return@launch
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    emailEditingResult = APIResult.Error(NetworkError.UNAUTHORIZED)
                                )
                            }
                            return@launch
                        }

                        //requesting code
                        val result = if (state.value.profile?.email == state.value.email)
                            profileRepository.getEmailCode()
                        else profileRepository.setEmailGetCode(
                            state.value.email
                        )

                        _state.update {
                            it.copy(
                                emailEditingResult = result,
                                codeNextSendTime = if (result is APIResult.Succeed) {
                                    val now = Clock.System.now()
                                    val timeZone = TimeZone.currentSystemDefault()
                                    now.plus(5, DateTimeUnit.MINUTE, timeZone)
                                        .toLocalDateTime(timeZone)
                                } else null
                            )
                        }
                    }
                }

                is ProfilePageUiEvent.SetAboutMe -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(aboutMe = event.aboutMe)
                        }
                    }
                }

                is ProfilePageUiEvent.SetCode -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(code = event.code)
                        }
                    }
                }

                is ProfilePageUiEvent.SetEmail -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(email = event.email)
                        }
                    }
                }

                is ProfilePageUiEvent.SetFirstName -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(firstName = event.firstName)
                        }
                    }
                }

                is ProfilePageUiEvent.SetImageUri -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                imageUrl = event.fileInfo
                            )
                        }
                    }
                }

                is ProfilePageUiEvent.SetLastName -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(lastName = event.lastName)
                        }
                    }
                }

                is ProfilePageUiEvent.SetLogin -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(login = event.login)
                        }
                    }
                }

                is ProfilePageUiEvent.SetSelectedTab -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                previousTab = it.selectedTab,
                                selectedTab = event.tab
                            )
                        }
                    }
                    if (event.tab == ProfileTabs.Statistics) {
                        onEvent(ProfilePageUiEvent.LoadReviewsStats)
                        onEvent(ProfilePageUiEvent.LoadComplaintsStats)
                        onEvent(ProfilePageUiEvent.LoadAssignmentsStats(DatePeriods.All))
                    }
                }

                ProfilePageUiEvent.LoadProfile -> {
                    profileEditingJob?.cancel()

                    profileEditingJob = launch {
                        _state.update {
                            it.copy(
                                profileLoadingResult = APIResult.Downloading()
                            )
                        }

                        //verifying request
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    profileLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED)
                                )
                            }
                            return@launch
                        }

                        //loading data
                        val profile = profileRepository.getProfile()
                        if (profile is APIResult.Error)
                            _state.update {
                                it.copy(
                                    profileLoadingResult = APIResult.Error(profile.info)
                                )
                            }
                        else _state.update {
                            it.copy(
                                profileLoadingResult = APIResult.Succeed(),
                                profile = (profile as APIResult.Succeed).data!!,
                                firstName = profile.data!!.firstName,
                                lastName = profile.data.lastName,
                                login = profile.data.login,
                                aboutMe = profile.data.aboutMe ?: "",
                                services = profile.data.services,
                                email = profile.data.email ?: "",
                                showAsWalker = profile.data.showAsWalker,
                                imageUrl = PetWalkerFileInfo(
                                    profile.data.imageUrl,
                                    "",
                                    "",
                                    null
                                )
                            )
                        }
                    }
                }

                is ProfilePageUiEvent.SetDesiredPayment -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(desiredPayment = event.desiredPayment)
                        }
                    }
                }

                is ProfilePageUiEvent.SetShowAsWalker -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                showAsWalker = event.showAsWalker
                            )
                        }
                    }
                }

                is ProfilePageUiEvent.LoadAssignmentsStats -> {
                    if (state.value.assignmentsStats is APIResult.Downloading)
                        return@launch
                    inputMutex.withLock {
                        _state.update {
                            it.copy(assignmentsStats = APIResult.Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(
                                assignmentsStats = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            assignmentsStats = usersRepository.getUserAssignmentStats(
                                null,
                                event.period
                            ),
                            assignmentsStatsDatePeriod = event.period
                        )
                    }
                }

                ProfilePageUiEvent.LoadComplaintsStats -> {
                    if (state.value.complaintsStats is APIResult.Downloading)
                        return@launch
                    inputMutex.withLock {
                        _state.update {
                            it.copy(complaintsStats = APIResult.Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(
                                complaintsStats = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            complaintsStats = reviewsRepository.getUserComplaintsStats(null)
                        )
                    }
                }

                ProfilePageUiEvent.LoadReviewsStats -> {
                    if (state.value.reviewsStats is APIResult.Downloading)
                        return@launch
                    inputMutex.withLock {
                        _state.update {
                            it.copy(reviewsStats = APIResult.Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(
                                reviewsStats = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            reviewsStats = reviewsRepository.getUserReviewsStats(null)
                        )
                    }
                }
            }
        }
    }
}