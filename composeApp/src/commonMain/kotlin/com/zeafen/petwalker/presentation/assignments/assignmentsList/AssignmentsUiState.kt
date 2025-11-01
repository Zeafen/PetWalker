package com.zeafen.petwalker.presentation.assignments.assignmentsList

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import kotlinx.datetime.LocalDateTime

data class AssignmentsUiState(
    val assignments: APIResult<PagedResult<AssignmentModel>, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val loadOwn: Boolean = false,
    val loadAsOwner: Boolean = false,
    val lastSelectedPage: Int = 1,
    val searchTitle: String = "",
    val maxDistance: Float? = null,
    val postedFrom: LocalDateTime? = null,
    val postedUntil: LocalDateTime? = null,
    val services: List<ServiceType>? = null,
    val ordering: AssignmentsOrdering? = null,
    val ascending: Boolean = true
)
