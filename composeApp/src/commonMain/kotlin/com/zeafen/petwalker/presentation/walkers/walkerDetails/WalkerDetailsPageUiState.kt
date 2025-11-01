package com.zeafen.petwalker.presentation.walkers.walkerDetails

import com.zeafen.petwalker.domain.models.api.assignments.AssignmentsStats
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.ReviewOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintsStats
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.domain.models.ui.ComplaintModel
import com.zeafen.petwalker.domain.models.ui.ReviewCardModel

data class WalkerDetailsPageUiState(
    val walker: APIResult<Walker, Error> = APIResult.Downloading(),
    val selectedWalkerId: String? = null,
    val distance: Float? = null,

    val availableAssignments: APIResult<PagedResult<AssignmentModel>, Error> = APIResult.Downloading(),
    val recruitingResult: APIResult<Unit, Error>? = null,

    val walkerReviews: APIResult<PagedResult<ReviewCardModel>, Error> = APIResult.Downloading(),
    val selectedReviewsPage: Int = 1,
    val positiveReviews: Boolean? = null,
    val reviewsPeriod: DatePeriods? = null,
    val reviewsOrdering: ReviewOrdering? = null,
    val reviewsAscending: Boolean = true,

    val walkerAssignments: APIResult<PagedResult<AssignmentModel>, Error> = APIResult.Downloading(),
    val walkerAssignmentStats: APIResult<AssignmentsStats, Error> = APIResult.Downloading(),
    val assignmentsStatsDatePeriod: DatePeriods = DatePeriods.All,
    val searchAssignmentTitle: String = "",
    val assignmentOrdering: AssignmentsOrdering? = null,
    val assignmentAscending: Boolean = true,
    val selectedAssignmentsPage: Int = 1,

    val walkerComplaints: APIResult<PagedResult<ComplaintModel>, Error> = APIResult.Downloading(),
    val selectedComplaintsPage: Int = 1,
    val complaintTopic: ComplaintTopic? = null,
    val complaintStatus: ComplaintStatus? = null,
    val complaintsPeriod: DatePeriods? = null,
    val dateAscending: Boolean? = null,

    val walkerReviewsStats: APIResult<ReviewsStats, Error> = APIResult.Downloading(),
    val walkerComplaintsStats: APIResult<ComplaintsStats, Error> = APIResult.Downloading(),
    val selectedTabIndex: Int = 0,
)