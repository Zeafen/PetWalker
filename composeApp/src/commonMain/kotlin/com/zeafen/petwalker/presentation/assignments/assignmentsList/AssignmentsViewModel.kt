package com.zeafen.petwalker.presentation.assignments.assignmentsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AssignmentsViewModel(
    private val assignmentsRepository: AssignmentsRepository,
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _state: MutableStateFlow<AssignmentsUiState> =
        MutableStateFlow(AssignmentsUiState())
    val state: StateFlow<AssignmentsUiState> =
        _state.asStateFlow()

    init {
        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private val titleInputMutex = Mutex()
    private var assignmentLoadingJob: Job? = null
    fun onEvent(event: AssignmentsUiEvent) {
        viewModelScope.launch {
            when (event) {
                AssignmentsUiEvent.ClearFilters -> {
                    _state.update {
                        it.copy(
                            maxDistance = null,
                            postedFrom = null,
                            postedUntil = null,
                            services = null,
                        )
                    }
                    onEvent(AssignmentsUiEvent.LoadAssignments())
                }

                is AssignmentsUiEvent.LoadAssignments -> {
                    if (assignmentLoadingJob?.isActive == true)
                        assignmentLoadingJob?.cancel()

                    assignmentLoadingJob = launch {
                        _state.update {
                            it.copy(assignments = APIResult.Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            return@launch
                        }

                        val location =
                            locationService.location.first()

                        val assignments = if (!state.value.loadOwn) {
                            assignmentsRepository.getAssignments(
                                event.page,
                                15,
                                state.value.searchTitle,
                                location,
                                state.value.maxDistance,
                                state.value.postedFrom?.toString(),
                                state.value.postedUntil?.toString(),
                                state.value.services,
                                state.value.ordering,
                                state.value.ordering?.let { state.value.ascending }
                            )
                        } else {
                            if (state.value.loadAsOwner)
                                assignmentsRepository.getOwnAssignmentsAsOwner(
                                    event.page,
                                    15,
                                    state.value.searchTitle,
                                    location,
                                    state.value.maxDistance,
                                    state.value.postedFrom?.toString(),
                                    state.value.postedUntil?.toString(),
                                    state.value.services,
                                    state.value.ordering,
                                    state.value.ordering?.let { state.value.ascending }
                                )
                            else assignmentsRepository.getOwnAssignmentsAsWalker(
                                event.page,
                                15,
                                state.value.searchTitle,
                                location,
                                state.value.maxDistance,
                                state.value.postedFrom?.toString(),
                                state.value.postedUntil?.toString(),
                                state.value.services,
                                state.value.ordering,
                                state.value.ordering?.let { state.value.ascending }
                            )
                        }

                        if (assignments is APIResult.Error) {
                            _state.update {
                                it.copy(
                                    assignments = APIResult.Error(assignments.info)
                                )
                            }
                            return@launch
                        }
                        val models = (assignments as APIResult.Succeed).data!!.result.map {
                            async {
                                getModelForAssignment(it, location)
                            }
                        }

                        _state.update {
                            it.copy(
                                assignments = APIResult.Succeed(
                                    PagedResult(
                                        result = models.awaitAll().mapNotNull { it },
                                        pageSize = assignments.data!!.pageSize,
                                        totalPages = assignments.data.totalPages,
                                        currentPage = assignments.data.currentPage
                                    )
                                )
                            )
                        }
                    }
                }

                is AssignmentsUiEvent.SetFilters -> {
                    _state.update {
                        it.copy(
                            maxDistance = event.maxDistance,
                            postedFrom = event.from,
                            postedUntil = event.until,
                            services = event.services
                        )
                    }
                    onEvent(AssignmentsUiEvent.LoadAssignments())
                }

                is AssignmentsUiEvent.SetOrdering -> {
                    _state.update {
                        it.copy(
                            ascending = if (event.ordering == it.ordering) !it.ascending else true,
                            ordering = event.ordering
                        )
                    }
                    onEvent(AssignmentsUiEvent.LoadAssignments())
                }

                is AssignmentsUiEvent.SetSearchTitle -> {
                    titleInputMutex.withLock {
                        _state.update {
                            it.copy(
                                searchTitle = event.title
                            )
                        }
                    }
                    onEvent(AssignmentsUiEvent.LoadAssignments())
                }

                is AssignmentsUiEvent.SetLoadType -> {
                    val prevLoadType = state.value.loadOwn
                    _state.update {
                        it.copy(loadOwn = event.loadOwn)
                    }

                    if (prevLoadType != event.loadOwn || state.value.assignments is APIResult.Downloading) {
                        onEvent(AssignmentsUiEvent.LoadAssignments())
                    }
                }

                is AssignmentsUiEvent.SetOwnLoadGroup -> {
                    if (event.loadOwnAsOwner != state.value.loadAsOwner) {
                        _state.update {
                            it.copy(loadAsOwner = event.loadOwnAsOwner)
                        }
                        onEvent(AssignmentsUiEvent.LoadAssignments())
                    }
                }
            }
        }
    }

    private suspend fun getModelForAssignment(
        assignment: Assignment,
        location: APILocation?
    ): AssignmentModel? {
        val ownerInfo = usersRepository.getWalker(assignment.ownerId)
        return if (ownerInfo is APIResult.Succeed)
            ownerInfo.data?.let { user ->
                return AssignmentModel(
                    assignment.id,
                    "${user.firstName} ${user.lastName}",
                    user.imageUrl,
                    assignment.title,
                    assignment.type,
                    assignment.datePublished,
                    assignment.dateTime,
                    assignment.location,
                    location?.calculateDistance(assignment.location)?.toFloat(),
                    assignment.payment
                )
            }
        else null
    }
}