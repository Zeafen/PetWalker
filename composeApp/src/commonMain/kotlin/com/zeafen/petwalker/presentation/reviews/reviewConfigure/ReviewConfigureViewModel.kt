package com.zeafen.petwalker.presentation.reviews.reviewConfigure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.countWords
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.reviews.ReviewRequest
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
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
import petwalker.composeapp.generated.resources.greater_than_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_least_error
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.least_words_count_error_txt
import petwalker.composeapp.generated.resources.less_than_error_txt

class ReviewConfigureViewModel(
    private val assignmentsRepository: AssignmentsRepository,
    private val reviewsRepository: ReviewsRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<ReviewConfigureUiState> = MutableStateFlow(
        ReviewConfigureUiState()
    )
    val state: StateFlow<ReviewConfigureUiState> =
        _state.asStateFlow()

    init {
        authDataStore.authDataStoreFlow
            .onEach { value ->
                _state.update {
                    it.copy(
                        currentUserName = "${value.lastName} ${value.firstName}",
                        currentUserImageUrl = value.imageUrl
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChangedBy { it.reviewText }
            .onEach { value ->
                _state.update {
                    it.copy(
                        textValid = when {
                            value.reviewText.isEmpty() ->
                                ValidationInfo(
                                    false,
                                    Res.string.empty_fields_error_txt,
                                    emptyList()
                                )

                            value.reviewText.countWords() < 5 ->
                                ValidationInfo(
                                    false,
                                    Res.string.least_words_count_error_txt,
                                    listOf(5)
                                )

                            value.reviewText.length < 50 ->
                                ValidationInfo(
                                    false,
                                    Res.string.incorrect_length_least_error,
                                    listOf(50)
                                )

                            value.reviewText.length > 500 ->
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
            .distinctUntilChangedBy { it.reviewRating }
            .onEach { value ->
                _state.update {
                    it.copy(
                        ratingValid = when {
                            value.reviewRating < 1 ->
                                ValidationInfo(
                                    false,
                                    Res.string.greater_than_error_txt,
                                    listOf(0)
                                )

                            value.reviewRating > 5 ->
                                ValidationInfo(
                                    false,
                                    Res.string.less_than_error_txt,
                                    listOf(5)
                                )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChanged { old, new ->
                old.textValid == new.textValid
                        && old.ratingValid == new.ratingValid
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canPublish = value.ratingValid.isValid && value.textValid.isValid
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private val inputMutex = Mutex()
    fun onEvent(event: ReviewConfigureUiEvent) {
        viewModelScope.launch {
            when (event) {
                ReviewConfigureUiEvent.PublishReview -> {
                    if (!state.value.canPublish) {
                        _state.update {
                            it.copy(reviewLoadingResult = Error(NetworkError.CONFLICT))
                        }
                        return@launch
                    }

                    inputMutex.withLock {
                        if (state.value.reviewLoadingResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(reviewLoadingResult = Downloading())
                        }
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(reviewLoadingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val review = ReviewRequest(
                        state.value.reviewText,
                        state.value.reviewRating
                    )
                    when {
                        state.value.selectedReviewId != null -> {
                            val result = reviewsRepository.updateReview(
                                state.value.selectedReviewId!!,
                                review
                            )
                                _state.update {
                                it.copy(reviewLoadingResult = result)
                            }
                        }

                        state.value.selectedReviewId == null -> {
                            val result = reviewsRepository.postReview(
                                state.value.reviewedAssignmentId,
                                review
                            )
                            if (result is APIResult.Error) {
                                _state.update {
                                    it.copy(reviewLoadingResult = Error(result.info))
                                }
                                return@launch
                            }

                            _state.update {
                                it.copy(
                                    reviewLoadingResult = Succeed(),
                                    selectedReviewId = (result as APIResult.Succeed).data!!.id
                                )
                            }
                        }
                    }
                }

                ReviewConfigureUiEvent.ReloadReviewedAssignment -> {
                    if (state.value.assignmentLoadingResult is APIResult.Downloading)
                        return@launch

                    _state.update {
                        it.copy(assignmentLoadingResult = Downloading())
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(assignmentLoadingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    if (state.value.reviewedAssignmentId.isEmpty()) {
                        _state.update {
                            it.copy(assignmentLoadingResult = Error(NetworkError.NOT_FOUND))
                        }
                        return@launch
                    }
                    val assignment =
                        assignmentsRepository.getAssignmentById(
                            state.value.reviewedAssignmentId
                        )

                    _state.update {
                        it.copy(
                            assignmentLoadingResult = if (assignment is APIResult.Error)
                                Error(assignment.info)
                            else Succeed(),
                            reviewedAssignmentTitle = (assignment as APIResult.Succeed).data?.title
                                ?: "",
                            reviewedAssignmentType = assignment.data?.type
                        )
                    }
                }

                is ReviewConfigureUiEvent.SetReviewRating -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(reviewRating = event.rating)
                        }
                    }
                }

                is ReviewConfigureUiEvent.SetReviewText -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(reviewText = event.text)
                        }
                    }
                }

                is ReviewConfigureUiEvent.InitializeReview -> {
                    _state.update {
                        it.copy(
                            reviewLoadingResult = Downloading(),
                            selectedReviewId = event.reviewId,
                            reviewedAssignmentId = event.assignmentId
                        )
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(reviewLoadingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    launch {
                        val review = event.reviewId?.let {
                            reviewsRepository.getReviewById(
                                event.reviewId
                            )
                        } ?: reviewsRepository.getReviewById(event.assignmentId)
                        _state.update {
                            if (review is APIResult.Error)
                                it.copy(
                                    reviewLoadingResult = Error(review.info)
                                )
                            else
                                it.copy(
                                    reviewLoadingResult = Succeed(),
                                    selectedReviewId = (review as APIResult.Succeed).data?.id,
                                    reviewText = review.data?.text ?: "",
                                    reviewRating = review.data?.rating ?: 0
                                )
                        }
                    }
                    launch {
                        _state.update {
                            it.copy(
                                assignmentLoadingResult = Downloading(),
                                reviewedAssignmentId = event.assignmentId
                            )
                        }
                        val assignment = assignmentsRepository.getAssignmentById(
                            event.assignmentId
                        )
                        _state.update {
                            if (assignment is APIResult.Error)
                                it.copy(
                                    assignmentLoadingResult = Error(assignment.info)
                                )
                            else it.copy(
                                reviewedAssignmentTitle = (assignment as APIResult.Succeed).data?.title
                                    ?: "",
                                reviewedAssignmentType = assignment.data?.type,
                                assignmentLoadingResult = Succeed()
                            )
                        }
                    }
                }

                ReviewConfigureUiEvent.ClearResult -> {
                    if (state.value.reviewLoadingResult !is APIResult.Downloading)
                        inputMutex.withLock {
                            _state.update {
                                it.copy(reviewLoadingResult = null)
                            }
                        }
                }
            }
        }
    }

}