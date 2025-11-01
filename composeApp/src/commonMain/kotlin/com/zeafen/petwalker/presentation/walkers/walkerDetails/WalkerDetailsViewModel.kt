package com.zeafen.petwalker.presentation.walkers.walkerDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentRequest
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.Complaint
import com.zeafen.petwalker.domain.models.api.reviews.Review
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.domain.models.ui.ComplaintModel
import com.zeafen.petwalker.domain.models.ui.ReviewCardModel
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.RecruitmentsRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
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

class WalkerDetailsViewModel(
    private val usersRepository: UsersRepository,
    private val assignmentsRepository: AssignmentsRepository,
    private val recruitmentsRepository: RecruitmentsRepository,
    private val reviewsRepository: ReviewsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _state: MutableStateFlow<WalkerDetailsPageUiState> = MutableStateFlow(
        WalkerDetailsPageUiState()
    )
    val state: StateFlow<WalkerDetailsPageUiState> =
        _state.asStateFlow()

    private var walkerLoadingJob: Job? = null
    private var walkerAssignmentsLoadingJob: Job? = null
    private var availableAssignmentsLoadingJob: Job? = null
    private var walkerReviewsLoadingJob: Job? = null
    private var walkerComplaintsLoadingJob: Job? = null

    init {
        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private val mutex = Mutex()
    fun onEvent(event: WalkerDetailsPageUiEvent) {
        viewModelScope.launch {
            when (event) {

                is WalkerDetailsPageUiEvent.LoadWalker -> {
                    if (walkerLoadingJob?.isActive == true)
                        walkerLoadingJob?.cancel()
                    walkerAssignmentsLoadingJob = launch {
                        _state.update {
                            it.copy(
                                selectedWalkerId = event.walkerId,
                                walker = Downloading()
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(
                                    walker = Downloading()
                                )
                            }
                            return@launch
                        }

                        val walker = usersRepository.getWalker(
                            event.walkerId
                        )
                        val location = locationService.location.first()
                        val distance =
                            if (walker is APIResult.Succeed && walker.data != null && location != null)
                                walker.data.location?.let {
                                    location.calculateDistance(
                                        APILocation(
                                            it.latitude,
                                            it.longitude
                                        )
                                    )
                                }
                            else null


                        _state.update {
                            it.copy(
                                walker = walker,
                                distance = distance?.toFloat()
                            )
                        }
                    }
                }

                is WalkerDetailsPageUiEvent.LoadWalkerAssignment -> {
                    if (walkerAssignmentsLoadingJob?.isActive == true)
                        walkerAssignmentsLoadingJob?.cancel()
                    walkerReviewsLoadingJob = launch {
                        _state.update {
                            it.copy(
                                walkerAssignments = Downloading()
                            )
                        }

                        if (state.value.selectedWalkerId == null) {
                            _state.update {
                                it.copy(
                                    walkerAssignments = Error(NetworkError.NOT_FOUND)
                                )
                            }
                            return@launch
                        }
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(
                                    walkerAssignments = Error(
                                        NetworkError.UNAUTHORIZED
                                    )
                                )
                            }
                            return@launch
                        }

                        val location = state.value.assignmentOrdering?.let {
                            if (it == AssignmentsOrdering.Location)
                                locationService.location.first()
                            else null
                        }
                        val assignments = assignmentsRepository.getUserAssignmentsAsWalker(
                            state.value.selectedWalkerId!!,
                            event.page,
                            15,
                            state.value.searchAssignmentTitle,
                            location
                        )

                        if (assignments is APIResult.Error) {
                            _state.update {
                                it.copy(walkerAssignments = Error(assignments.info))
                            }
                            return@launch
                        }
                        val models = (assignments as APIResult.Succeed).data!!.result.map {
                            async { getModelForAssignment(it) }
                        }

                        _state.update {
                            it.copy(
                                walkerAssignments = Succeed(
                                    PagedResult(
                                        models.awaitAll().mapNotNull { it },
                                        assignments.data!!.currentPage,
                                        assignments.data.totalPages,
                                        assignments.data.pageSize
                                    )
                                ),
                                selectedAssignmentsPage = assignments.data.currentPage
                            )
                        }
                    }
                }

                is WalkerDetailsPageUiEvent.LoadWalkerReviews -> {
                    if (walkerReviewsLoadingJob?.isActive == true)
                        walkerReviewsLoadingJob?.cancel()
                    walkerReviewsLoadingJob = launch {
                        _state.update {
                            it.copy(
                                walkerReviews = Downloading()
                            )
                        }

                        if (state.value.selectedWalkerId == null) {
                            _state.update {
                                it.copy(
                                    walkerReviews = Error(NetworkError.NOT_FOUND)
                                )
                            }
                            return@launch
                        }
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(
                                    walkerReviews = Error(
                                        NetworkError.UNAUTHORIZED
                                    )
                                )
                            }
                            return@launch
                        }

                        val reviews = reviewsRepository.getUserReviews(
                            state.value.selectedWalkerId!!,
                            state.value.selectedReviewsPage,
                            15,
                            state.value.positiveReviews,
                            state.value.reviewsPeriod,
                            state.value.reviewsOrdering,
                            state.value.reviewsOrdering?.let { state.value.reviewsAscending },
                        )

                        if (reviews is APIResult.Error) {
                            _state.update {
                                it.copy(walkerReviews = Error(reviews.info))
                            }
                            return@launch
                        }
                        val models = (reviews as APIResult.Succeed).data!!.result.map {
                            async { getModelForReview(it) }
                        }

                        _state.update {
                            it.copy(
                                walkerReviews = Succeed(
                                    PagedResult(
                                        models.awaitAll().mapNotNull { it },
                                        reviews.data!!.currentPage,
                                        reviews.data.totalPages,
                                        reviews.data.pageSize
                                    )
                                )
                            )
                        }
                    }

                }

                WalkerDetailsPageUiEvent.LoadWalkerReviewsStats -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                walkerReviewsStats = Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    if (state.value.selectedWalkerId == null) {
                        _state.update {
                            it.copy(
                                walkerReviewsStats = Error(NetworkError.NOT_FOUND)
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            walkerReviewsStats = reviewsRepository.getUserReviewsStats(
                                state.value.selectedWalkerId!!
                            )
                        )
                    }
                }

                is WalkerDetailsPageUiEvent.SetAssignmentOrdering -> {
                    _state.update {
                        it.copy(
                            assignmentAscending = if (event.ordering == it.assignmentOrdering) !it.assignmentAscending else true,
                            assignmentOrdering = event.ordering
                        )
                    }
                    onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment())
                }

                is WalkerDetailsPageUiEvent.SetSearchAssignmentTitle -> {
                    mutex.withLock {
                        _state.update {
                            it.copy(
                                searchAssignmentTitle = event.title,
                            )
                        }
                    }
                    onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment())
                }

                is WalkerDetailsPageUiEvent.SetSelectedTab -> {
                    _state.update {
                        it.copy(
                            selectedTabIndex = event.tabIndex
                        )
                    }
                    when {
                        WalkerDetailsPageTabs.entries[event.tabIndex] == WalkerDetailsPageTabs.Reviews
                                && state.value.walkerReviews !is APIResult.Succeed
                                && walkerReviewsLoadingJob?.isActive != true -> {
                            onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews())
                        }

                        WalkerDetailsPageTabs.entries[event.tabIndex] == WalkerDetailsPageTabs.Complaints
                                && state.value.walkerComplaints !is APIResult.Succeed
                                && walkerComplaintsLoadingJob?.isActive != true -> {
                            onEvent(WalkerDetailsPageUiEvent.LoadWalkerComplaints())
                        }

                        WalkerDetailsPageTabs.entries[event.tabIndex] == WalkerDetailsPageTabs.AssignmentsHistory
                                && state.value.walkerAssignments !is APIResult.Succeed
                                && walkerAssignmentsLoadingJob?.isActive != true -> {
                            onEvent(WalkerDetailsPageUiEvent.LoadWalkerAssignment())
                        }
                    }
                }

                is WalkerDetailsPageUiEvent.SetWalkerReviewsFilters -> {
                    _state.update {
                        it.copy(
                            reviewsPeriod = event.period,
                            positiveReviews = event.positive,
                        )
                    }
                    onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews())
                }

                is WalkerDetailsPageUiEvent.LoadWalkerComplaints -> {
                    if (walkerComplaintsLoadingJob?.isActive == true)
                        walkerComplaintsLoadingJob?.cancel()
                    walkerComplaintsLoadingJob = launch {
                        _state.update {
                            it.copy(
                                walkerComplaints = Downloading()
                            )
                        }

                        if (state.value.selectedWalkerId == null) {
                            _state.update {
                                it.copy(
                                    walkerComplaints = Error(NetworkError.NOT_FOUND)
                                )
                            }
                            return@launch
                        }
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(
                                    walkerComplaints = Error(
                                        NetworkError.UNAUTHORIZED
                                    )
                                )
                            }
                            return@launch
                        }

                        val complaints = reviewsRepository.getUserComplaints(
                            state.value.selectedWalkerId!!,
                            state.value.selectedComplaintsPage,
                            15,
                            state.value.complaintTopic,
                            state.value.complaintStatus,
                            state.value.complaintsPeriod,
                        )

                        if (complaints is APIResult.Error) {
                            _state.update {
                                it.copy(walkerComplaints = Error(complaints.info))
                            }
                            return@launch
                        }
                        val models = (complaints as APIResult.Succeed).data!!.result.map {
                            async { getModelForComplaint(it) }
                        }

                        _state.update {
                            it.copy(
                                walkerComplaints = Succeed(
                                    PagedResult(
                                        models.awaitAll().mapNotNull { it },
                                        complaints.data!!.currentPage,
                                        complaints.data.totalPages,
                                        complaints.data.pageSize
                                    )
                                )
                            )
                        }
                    }
                }

                WalkerDetailsPageUiEvent.LoadWalkerComplaintsStats -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                walkerComplaintsStats = Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    if (state.value.selectedWalkerId == null) {
                        _state.update {
                            it.copy(
                                walkerComplaintsStats = Error(NetworkError.NOT_FOUND)
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            walkerComplaintsStats = reviewsRepository.getUserComplaintsStats(
                                state.value.selectedWalkerId!!
                            )
                        )
                    }
                }

                is WalkerDetailsPageUiEvent.SetWalkerComplaintsFilters -> {
                    _state.update {
                        it.copy(
                            complaintTopic = event.topic,
                            complaintStatus = event.status,
                            complaintsPeriod = event.period
                        )
                    }
                    onEvent(WalkerDetailsPageUiEvent.LoadWalkerComplaints())
                }

                is WalkerDetailsPageUiEvent.RecruitWalker -> {
                    mutex.withLock {
                        if (state.value.recruitingResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(recruitingResult = Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(recruitingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val selectedWalkerId = state.value.selectedWalkerId
                    if (selectedWalkerId == null) {
                        _state.update {
                            it.copy(recruitingResult = Error(NetworkError.NOT_FOUND))
                        }
                        return@launch
                    }

                    val result = recruitmentsRepository.postRecruitment(
                        RecruitmentRequest(event.assignmentId, selectedWalkerId)
                    )

                    _state.update {
                        it.copy(
                            recruitingResult = result
                        )
                    }
                }

                is WalkerDetailsPageUiEvent.LoadAvailableAssignments -> {
                    if (availableAssignmentsLoadingJob?.isActive == true)
                        availableAssignmentsLoadingJob?.cancel()

                    availableAssignmentsLoadingJob = launch {
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(availableAssignments = Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val assignments = assignmentsRepository.getOwnOpenAssignmentsAsOwner(
                            page = event.page,
                            perPage = 5
                        )
                        if (assignments is APIResult.Error) {
                            _state.update {
                                it.copy(availableAssignments = Error(assignments.info))
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
                                availableAssignments = Succeed(
                                    PagedResult(
                                        result = models.awaitAll().mapNotNull { it },
                                        totalPages = assignments.data!!.totalPages,
                                        currentPage = assignments.data.currentPage,
                                        pageSize = assignments.data.pageSize
                                    )
                                )
                            )
                        }
                    }
                }

                is WalkerDetailsPageUiEvent.LoadAssignmentsAssignmentsStats -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                walkerComplaintsStats = Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                        return@launch
                    }

                    if (state.value.selectedWalkerId == null) {
                        _state.update {
                            it.copy(
                                walkerAssignmentStats = Error(NetworkError.NOT_FOUND)
                            )
                        }
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            walkerAssignmentStats = usersRepository.getUserAssignmentStats(
                                state.value.selectedWalkerId!!,
                                event.period
                            ),
                            assignmentsStatsDatePeriod = event.period
                        )
                    }
                }
            }
        }
    }

    private suspend fun getModelForAssignment(
        assignment: Assignment
    ): AssignmentModel? {
        val location = locationService.location.first()
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

    private suspend fun getModelForReview(
        review: Review
    ): ReviewCardModel? {
        val ownerInfo = usersRepository.getWalker(review.senderId)
        return if (ownerInfo is APIResult.Succeed) ownerInfo.data?.let { user ->
            ReviewCardModel(
                review.id,
                user.imageUrl,
                "${user.firstName} ${user.lastName}",
                review.assignmentId,
                review.text,
                review.rating,
                review.datePosted,
                review.dateUpdated
            )
        }
        else null
    }

    private suspend fun getModelForComplaint(
        complaint: Complaint
    ): ComplaintModel? {
        val ownerInfo = usersRepository.getWalker(complaint.senderId)
        return if (ownerInfo is APIResult.Succeed) ownerInfo.data?.let { user ->
            ComplaintModel(
                complaint.id,
                user.imageUrl,
                "${user.firstName} ${user.lastName}",
                complaint.assignmentId,
                complaint.topic,
                complaint.status,
                complaint.body,
                complaint.datePosted,
                complaint.dateSolved
            )
        }
        else null
    }
}