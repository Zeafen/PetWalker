package com.zeafen.petwalker.presentation.reviews.complaintConfigure

import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic

sealed interface ComplaintConfigureUiEvent {
    data class InitializeComplaint(val complaintId: String?, val walkerId: String) :
        ComplaintConfigureUiEvent

    data object ReloadReviewedWalker : ComplaintConfigureUiEvent

    data class LoadOwnAssignments(val page: Int = 1) : ComplaintConfigureUiEvent
    data class SetComplaintDetails(val details: String) : ComplaintConfigureUiEvent
    data class SetComplaintTopic(val topic: ComplaintTopic?) : ComplaintConfigureUiEvent
    data class SetSelectedAssignment(val assignmentId: String?) : ComplaintConfigureUiEvent
    data object PublishComplaint : ComplaintConfigureUiEvent
    data object ClearResult : ComplaintConfigureUiEvent
}