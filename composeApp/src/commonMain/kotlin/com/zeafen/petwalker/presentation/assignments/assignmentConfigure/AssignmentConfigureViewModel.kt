@file:OptIn(ExperimentalTime::class)

package com.zeafen.petwalker.presentation.assignments.assignmentConfigure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.countWords
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentRequest
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.PetsRepository
import kotlinx.coroutines.Job
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
import petwalker.composeapp.generated.resources.early_error_error_txt
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.least_words_count_error_txt
import petwalker.composeapp.generated.resources.length_max_error
import petwalker.composeapp.generated.resources.required_label
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AssignmentConfigureViewModel(
    private val assignmentsRepository: AssignmentsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val petsRepository: PetsRepository,
    private val locationService: LocationService,
) : ViewModel() {

    private val _state: MutableStateFlow<AssignmentConfigureUiState> = MutableStateFlow(
        AssignmentConfigureUiState()
    )
    val state: StateFlow<AssignmentConfigureUiState> =
        _state.asStateFlow()

    private val _exitPage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exitPage: StateFlow<Boolean> = _exitPage.asStateFlow()

    init {
        _state
            .distinctUntilChangedBy { it.assignmentType }
            .onEach { value ->
                _state.update {
                    it.copy(descriptionNeeded = value.assignmentType == ServiceType.Other)
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChangedBy { it.assignmentTitle }
            .onEach { value ->
                _state.update {
                    it.copy(
                        titleValidation = when {
                            value.assignmentTitle.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.assignmentTitle.length > 200 -> ValidationInfo(
                                false,
                                Res.string.length_max_error,
                                listOf(200)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChangedBy { it.assignmentDate }
            .onEach { value ->
                _state.update {
                    it.copy(
                        dateValidation = when {
                            value.assignmentDate == null -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.assignmentDate < Clock.System.now()
                                .plus(1, DateTimeUnit.HOUR)
                                .toLocalDateTime(TimeZone.currentSystemDefault()) -> ValidationInfo(
                                false,
                                Res.string.early_error_error_txt,
                                emptyList()
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChanged { old, new ->
                old.assignmentDescription == new.assignmentDescription
                        && old.descriptionNeeded == new.descriptionNeeded
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        descriptionValidation = when {
                            value.descriptionNeeded && value.assignmentDescription.countWords() < 5 ->
                                ValidationInfo(
                                    false,
                                    Res.string.least_words_count_error_txt,
                                    listOf(5)
                                )

                            value.descriptionNeeded && value.assignmentDescription.isBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.required_label,
                                    emptyList()
                                )

                            value.assignmentDescription.length > 500 -> ValidationInfo(
                                false,
                                Res.string.length_max_error,
                                listOf(500)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChanged { old, new ->
                old.titleValidation == new.titleValidation
                        && old.assignmentType == new.assignmentType
                        && old.dateValidation == new.dateValidation
                        && old.descriptionValidation == new.descriptionValidation
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canPublish = value.titleValidation.isValid
                                && value.assignmentType != null
                                && value.dateValidation.isValid
                                && value.descriptionValidation.isValid
                    )
                }
            }
            .launchIn(viewModelScope)

        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private val inputMutex = Mutex()
    private var petsLoadingJob: Job? = null
    fun onEvent(event: AssignmentConfigureUiEvent) {
        viewModelScope.launch {
            when (event) {
                is AssignmentConfigureUiEvent.AddAssignedPet -> {
                    _state.update {
                        it.copy(
                            assignedPets = it.assignedPets.apply {
                                plus(event.pet)
                                distinct()
                            }
                        )
                    }
                }

                AssignmentConfigureUiEvent.ApplyChanges -> {
                    inputMutex.withLock {
                        if (state.value.publishingResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(publishingResult = APIResult.Downloading())
                        }
                    }

                    if (!state.value.canPublish) {
                        _state.update {
                            it.copy(publishingResult = APIResult.Error(NetworkError.UNKNOWN))
                        }
                        return@launch
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(publishingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    //publishing/updating assignment
                    val location = locationService.location.first()
                    if (location == null) {
                        _state.update {
                            it.copy(assignmentLoadingResult = APIResult.Error(NetworkError.UNKNOWN))
                        }
                        return@launch
                    }

                    val assignment = AssignmentRequest(
                        state.value.assignmentTitle,
                        state.value.assignmentDescription,
                        state.value.assignmentType!!,
                        state.value.assignmentDate!!,
                        state.value.assignedPets.map { it.id },
                        location
                    )
                    val result = state.value.selectedAssignmentId?.let {
                        assignmentsRepository.updateAssignment(
                            it,
                            assignment
                        )
                    } ?: assignmentsRepository.postAssignment(
                        assignment
                    )

                    _state.update {
                        it.copy(
                            publishingResult =
                                if (result is APIResult.Error)
                                    APIResult.Error(result.info)
                                else APIResult.Succeed()
                        )
                    }
                }

                is AssignmentConfigureUiEvent.LoadAvailablePets -> {
                    if (petsLoadingJob?.isActive == true)
                        petsLoadingJob?.cancel()

                    petsLoadingJob = launch {
                        _state.update {
                            it.copy(availablePets = APIResult.Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(availablePets = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val pets = petsRepository.getOwnPets(
                            event.page,
                            10
                        )
                        _state.update {
                            it.copy(availablePets = pets)
                        }
                    }
                }

                is AssignmentConfigureUiEvent.RemoveAssignedPet -> {
                    state.value.assignedPets.firstOrNull { it.id == event.petId }?.let { removed ->
                        _state.update {
                            it.copy(assignedPets = it.assignedPets.minus(removed))
                        }
                    }
                }

                is AssignmentConfigureUiEvent.SetAssignmentDescription -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(assignmentDescription = event.description)
                        }
                    }
                }

                is AssignmentConfigureUiEvent.SetAssignmentTitle -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(assignmentTitle = event.title)
                        }
                    }
                }

                is AssignmentConfigureUiEvent.SetAssignmentType -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(assignmentType = event.type)
                        }
                    }
                }

                is AssignmentConfigureUiEvent.SetEditedAssignmentId -> {
                    if (event.id == null)
                        _state.update {
                            it.copy(
                                selectedAssignmentId = null,
                                assignmentLoadingResult = null,
                                assignmentTitle = "",
                                assignedPets = emptyList(),
                                assignmentDescription = "",
                                assignmentType = null
                            )
                        }
                    else {
                        _state.update {
                            it.copy(
                                assignmentLoadingResult = APIResult.Downloading(),
                                selectedAssignmentId = event.id
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(assignmentLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }
                        val assignment =
                            assignmentsRepository.getAssignmentById(event.id)
                        if (assignment is APIResult.Error) {
                            _state.update {
                                it.copy(assignmentLoadingResult = APIResult.Error(assignment.info))
                            }
                            return@launch
                        } else if (assignment is APIResult.Succeed) {
                            inputMutex.withLock {
                                _state.update {
                                    it.copy(
                                        assignmentLoadingResult = APIResult.Succeed(),
                                        assignmentTitle = assignment.data!!.title,
                                        assignmentDescription = assignment.data.description ?: "",
                                        assignmentType = assignment.data.type,
                                    )
                                }
                            }
                        }
                    }
                    onEvent(AssignmentConfigureUiEvent.LoadAvailablePets())
                    onEvent(AssignmentConfigureUiEvent.LoadAssignedPets())
                }

                is AssignmentConfigureUiEvent.LoadAssignedPets -> {
                    val selectedId = state.value.selectedAssignmentId
                    if (selectedId.isNullOrBlank())
                        return@launch
                    inputMutex.withLock {
                        if (state.value.assignedPetsLoading)
                            return@launch
                        _state.update {
                            it.copy(
                                assignedPetsLoading = true,
                                isLoadingForward = it.assignedPetsPages.second < event.page
                            )
                        }
                    }

                    val currentComb = when {
                        event.page < state.value.assignedPetsPages.first - 1 ->
                            event.page.coerceAtLeast(1) to (event.page + 1).coerceAtLeast(1)

                        event.page == state.value.assignedPetsPages.first - 1 ->
                            event.page to state.value.assignedPetsPages.first

                        event.page == state.value.assignedPetsPages.second + 1 ->
                            state.value.assignedPetsPages.second to event.page

                        event.page > state.value.assignedPetsPages.second + 1 ->
                            (event.page - 1).coerceAtLeast(1) to event.page.coerceAtLeast(1)

                        else -> 1 to 1
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                assignedPetsLoading = false,
                                assignedPetsLoadingError = NetworkError.UNAUTHORIZED
                            )
                        }
                        return@launch
                    }

                    val result = petsRepository.getAssignmentPets(
                        selectedId,
                        event.page,
                        15
                    )

                    when (result) {
                        is APIResult.Error -> {
                            _state.update {
                                it.copy(
                                    assignedPetsLoadingError = result.info,
                                    assignedPetsLoading = false
                                )
                            }
                            return@launch
                        }

                        is APIResult.Succeed -> {
                            val maxPage = result.data?.totalPages ?: state.value.assignedPetsMaxPage
                            val newPets = when {
                                currentComb.second == state.value.assignedPetsPages.first ->
                                    result.data!!.result + state.value.assignedPets.take(15)

                                currentComb.first == state.value.assignedPetsPages.second ->
                                    state.value.assignedPets.takeLast(15) + result.data!!.result

                                else -> result.data!!.result
                            }

                            _state.update {
                                it.copy(
                                    assignedPets = newPets,
                                    assignedPetsLoading = false,
                                    assignedPetsLoadingError = null,
                                    assignedPetsMaxPage = maxPage
                                )
                            }
                        }

                        else -> _state.update {
                            it.copy(
                                assignedPetsLoadingError = NetworkError.UNKNOWN,
                                assignedPetsLoading = false
                            )
                        }
                    }
                }

                is AssignmentConfigureUiEvent.SetAssignmentDate -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                assignmentDate = event.date
                            )
                        }
                    }
                }

                AssignmentConfigureUiEvent.DeleteAssignment -> {
                    inputMutex.withLock {
                        if (state.value.publishingResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(publishingResult = APIResult.Downloading())
                        }
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(assignmentLoadingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val assignmentId = state.value.selectedAssignmentId
                    if (assignmentId == null) {
                        _state.update {
                            it.copy(assignmentLoadingResult = APIResult.Error(NetworkError.NOT_FOUND))
                        }
                        return@launch
                    }

                    val result = assignmentsRepository.deleteAssignment(
                        assignmentId
                    )
                    if (result is APIResult.Error) {
                        _state.update {
                            it.copy(assignmentLoadingResult = APIResult.Error(result.info))
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(assignmentLoadingResult = APIResult.Succeed())
                    }
                    _exitPage.update { true }
                }
            }
        }
    }
}