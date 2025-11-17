package com.zeafen.petwalker.presentation.walkers.walkerDetails

import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import kotlinx.datetime.DatePeriod

sealed interface WalkerDetailsPageUiEvent {
    data class LoadWalker(val walkerId: String) : WalkerDetailsPageUiEvent
    data class LoadWalkerReviews(val page: Int = 1) : WalkerDetailsPageUiEvent
    data class DeleteWalkerReview(val id: String): WalkerDetailsPageUiEvent
    data class LoadWalkerComplaints(val page: Int = 1) : WalkerDetailsPageUiEvent
    data class DeleteWalkerComplaint(val id: String): WalkerDetailsPageUiEvent
    data object LoadWalkerReviewsStats : WalkerDetailsPageUiEvent
    data object LoadWalkerComplaintsStats : WalkerDetailsPageUiEvent

    data class RecruitWalker(val assignmentId: String) : WalkerDetailsPageUiEvent
    data class LoadAvailableAssignments(val page: Int = 1) : WalkerDetailsPageUiEvent

    data class SetWalkerReviewsFilters(val positive: Boolean?, val period: DatePeriods?) :
        WalkerDetailsPageUiEvent

    data class SetWalkerComplaintsFilters(
        val topic: ComplaintTopic?,
        val status: ComplaintStatus?,
        val period: DatePeriods?
    ) : WalkerDetailsPageUiEvent

    data class SetSelectedTab(val tabIndex: Int) : WalkerDetailsPageUiEvent
    data class SetSearchAssignmentTitle(val title: String) : WalkerDetailsPageUiEvent
    data class SetAssignmentOrdering(val ordering: AssignmentsOrdering) : WalkerDetailsPageUiEvent
    data class LoadWalkerAssignment(val page: Int = 1) : WalkerDetailsPageUiEvent
    data class LoadAssignmentsAssignmentsStats(val period: DatePeriods) : WalkerDetailsPageUiEvent

}