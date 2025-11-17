package com.zeafen.petwalker.presentation.reviews.reviewConfigure

sealed interface ReviewConfigureUiEvent {
    data class InitializeReview(val reviewId: String?, val assignmentId: String) :
        ReviewConfigureUiEvent
    data object ReloadReviewedAssignment : ReviewConfigureUiEvent

    data class SetReviewRating(val rating: Int) : ReviewConfigureUiEvent
    data class SetReviewText(val text: String) : ReviewConfigureUiEvent

    data object PublishReview : ReviewConfigureUiEvent
    data object ClearResult : ReviewConfigureUiEvent
}