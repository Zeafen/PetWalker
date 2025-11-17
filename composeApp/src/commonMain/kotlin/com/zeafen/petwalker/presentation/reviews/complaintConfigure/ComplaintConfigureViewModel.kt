package com.zeafen.petwalker.presentation.reviews.complaintConfigure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.countWords
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintRequest
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
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
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_least_error
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.least_words_count_error_txt

class ComplaintConfigureViewModel(
    private val usersRepository: UsersRepository,
    private val reviewsRepository: ReviewsRepository,
    private val assignmentsRepository: AssignmentsRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ComplaintConfigureUiState> = MutableStateFlow(
        ComplaintConfigureUiState()
    )
    val state: StateFlow<ComplaintConfigureUiState> =
        _state.asStateFlow()

    init {
        state
            .distinctUntilChangedBy { it.complaintDetails }
            .onEach { value ->
                _state.update {
                    it.copy(
                        detailsValid = when {
                            value.complaintDetails.isBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            value.complaintDetails.length <= 50 && value.complaintDetails.isNotBlank() ->
                                ValidationInfo(
                                    false,
                                    Res.string.incorrect_length_least_error,
                                    listOf(51)
                                )

                            value.complaintDetails.countWords() < 5 && value.complaintDetails.length > 50 ->
                                ValidationInfo(
                                    false,
                                    Res.string.least_words_count_error_txt,
                                    listOf(5)
                                )

                            value.complaintDetails.length > 500 ->
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
            .distinctUntilChanged { old, new ->
                old.detailsValid == new.detailsValid
                        && old.complaintTopic == new.complaintTopic
            }
            .onEach { value ->
                _state.update {
                    it.copy(canPublish = value.detailsValid.isValid && value.complaintTopic != null)
                }
            }
            .launchIn(viewModelScope)

        onEvent(ComplaintConfigureUiEvent.LoadOwnAssignments(1))
    }


    private val inputMutex = Mutex()
    private var ownAssignmentsLoadingJob: Job? = null
    fun onEvent(event: ComplaintConfigureUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ComplaintConfigureUiEvent.InitializeComplaint -> {
                    _state.update {
                        it.copy(
                            complaintLoadingResult = Downloading(),
                            selectedComplaintId = event.complaintId,
                            reviewedWalkerId = event.walkerId
                        )
                    }

                    when {
                        event.complaintId != null -> {
                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(complaintLoadingResult = Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            val complaint = reviewsRepository.getComplaintById(
                                event.complaintId
                            )

                            if (complaint is APIResult.Succeed) {
                                launch {
                                    loadWalker(complaint.data!!.userId)
                                }
                                _state.update {
                                    it.copy(
                                        complaintLoadingResult = Succeed(),
                                        complaintDetails = complaint.data?.body ?: "",
                                        complaintTopic = complaint.data?.topic,
                                    )
                                }
                            } else
                                _state.update {
                                    it.copy(
                                        complaintLoadingResult = Error((complaint as APIResult.Error).info)
                                    )
                                }
                        }

                        else -> {
                            if (event.walkerId.isBlank()) {
                                _state.update {
                                    it.copy(complaintLoadingResult = Error(NetworkError.NOT_FOUND))
                                }
                                return@launch
                            }
                            _state.update {
                                it.copy(
                                    complaintDetails = "",
                                    complaintTopic = null,
                                    complaintLoadingResult = Succeed()
                                )
                            }

                            loadWalker(event.walkerId)
                        }
                    }
                }

                is ComplaintConfigureUiEvent.LoadOwnAssignments -> {
                    if (ownAssignmentsLoadingJob?.isActive == true)
                        ownAssignmentsLoadingJob?.cancel()

                    ownAssignmentsLoadingJob = launch {
                        _state.update {
                            it.copy(ownLoadedAssignments = Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    ownLoadedAssignments = Error(NetworkError.UNAUTHORIZED)
                                )
                            }
                            return@launch
                        }

                        val assignments = assignmentsRepository.getOwnAssignmentsAsOwner(
                            event.page, 15
                        )
                        if (assignments is APIResult.Error) {
                            _state.update {
                                it.copy(ownLoadedAssignments = Error(assignments.info))
                            }
                            return@launch
                        }

                        val models = (assignments as APIResult.Succeed).data!!.result.map {
                            async {
                                getModelForAssignment(it)
                            }
                        }

                        _state.update {
                            it.copy(
                                ownLoadedAssignments = Succeed(
                                    PagedResult(
                                        result = models.awaitAll().toList(),
                                        totalPages = assignments.data!!.totalPages,
                                        currentPage = assignments.data.currentPage,
                                        pageSize = assignments.data.pageSize,
                                    )
                                ),
                                ownAssignmentsLastLoadedPage = assignments.data.currentPage
                            )
                        }
                    }
                }

                ComplaintConfigureUiEvent.PublishComplaint -> {
                    inputMutex.withLock {
                        if (!state.value.canPublish || state.value.complaintLoadingResult is APIResult.Downloading) {
                            _state.update {
                                it.copy(
                                    complaintLoadingResult = Error(NetworkError.CONFLICT)
                                )
                            }
                            return@launch
                        }
                        _state.update {
                            it.copy(complaintLoadingResult = Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(
                                complaintLoadingResult = Error(NetworkError.UNAUTHORIZED)
                            )
                        }
                        return@launch
                    }

                    val request = ComplaintRequest(
                        state.value.complaintTopic!!,
                        state.value.complaintDetails,
                        state.value.selectedAssignment?.id
                    )
                    when {
                        state.value.selectedComplaintId == null -> {
                            val result = reviewsRepository.postComplaint(
                                state.value.reviewedWalkerId,
                                request
                            )

                            if (result is APIResult.Error) {
                                _state.update {
                                    it.copy(
                                        complaintLoadingResult = Error(result.info)
                                    )
                                }
                                return@launch
                            }
                            _state.update {
                                it.copy(
                                    complaintLoadingResult = Succeed(),
                                    selectedComplaintId = (result as APIResult.Succeed).data!!.id
                                )
                            }
                        }

                        else -> {
                            val result = reviewsRepository.updateComplaint(
                                state.value.selectedComplaintId!!,
                                request
                            )

                            _state.update {
                                it.copy(
                                    complaintLoadingResult = result
                                )
                            }
                        }
                    }
                }

                ComplaintConfigureUiEvent.ReloadReviewedWalker -> {
                    loadWalker(state.value.reviewedWalkerId)
                }

                is ComplaintConfigureUiEvent.SetComplaintDetails -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                complaintDetails = event.details
                            )
                        }
                    }
                }

                is ComplaintConfigureUiEvent.SetComplaintTopic -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                complaintTopic = event.topic
                            )
                        }
                    }
                }

                is ComplaintConfigureUiEvent.SetSelectedAssignment -> {
                    inputMutex.withLock {
                        val ownLoadedAssignments = state.value.ownLoadedAssignments
                        _state.update {
                            it.copy(
                                selectedAssignment =
                                    if (ownLoadedAssignments is APIResult.Succeed)
                                        ownLoadedAssignments.data?.result?.firstOrNull { it.id == event.assignmentId }
                                    else null
                            )
                        }
                    }
                }

                ComplaintConfigureUiEvent.ClearResult -> {
                    if (state.value.complaintLoadingResult !is APIResult.Downloading)
                        inputMutex.withLock {
                            _state.update {
                                it.copy(complaintLoadingResult = null)
                            }
                        }
                }
            }
        }
    }

    private suspend fun loadWalker(walkerId: String) {
        if (state.value.reviewedWalkerLoadingRes is APIResult.Downloading)
            return

        val token = authDataStore.authDataStoreFlow.first().token
        if (token == null || token.accessToken.isBlank()) {
            _state.update {
                it.copy(
                    reviewedWalkerLoadingRes = APIResult.Error(NetworkError.UNAUTHORIZED),
                    reviewedWalkerId = walkerId
                )
            }
            return
        }

        val walker = usersRepository.getWalker(walkerId)
        if (walker is APIResult.Error)
            _state.update {
                it.copy(
                    reviewedWalkerLoadingRes = APIResult.Error(walker.info)
                )
            }
        else _state.update {
            it.copy(
                reviewedWalkerLoadingRes = APIResult.Succeed(),
                reviewedWalkerId = walkerId,
                reviewedWalkerName = (walker as APIResult.Succeed)
                    .data?.let { "${it.firstName} ${it.lastName}" }
                    ?: "",
                reviewedWalkerImageUrl = walker.data?.imageUrl
            )
        }
    }


    private suspend fun getModelForAssignment(
        assignment: Assignment
    ): AssignmentModel {
        val ownerInfo = usersRepository.getWalker(assignment.ownerId)

        return AssignmentModel(
            assignment.id,
            if (ownerInfo is APIResult.Succeed)
                ownerInfo.data?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown"
            else "Unknown",
            if (ownerInfo is APIResult.Succeed)
                ownerInfo.data?.imageUrl
            else null,
            assignment.title,
            assignment.type,
            assignment.datePublished,
            assignment.dateTime,
            assignment.location,
            null,
            assignment.payment
        )

    }
}