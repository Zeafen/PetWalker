package com.zeafen.petwalker.presentation.assignments.assignmentsList

import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import kotlinx.datetime.LocalDateTime

sealed interface AssignmentsUiEvent {
    data class LoadAssignments(val page: Int = 1) : AssignmentsUiEvent
    data class SetOwnLoadGroup(val loadOwnAsOwner: Boolean) : AssignmentsUiEvent
    data class SetLoadType(val loadOwn: Boolean) : AssignmentsUiEvent
    data class SetSearchTitle(val title: String) : AssignmentsUiEvent
    data class SetOrdering(val ordering: AssignmentsOrdering) : AssignmentsUiEvent
    data object ClearFilters : AssignmentsUiEvent
    data class SetFilters(
        val maxDistance: Float?,
        val from: LocalDateTime?,
        val until: LocalDateTime?,
        val services: List<ServiceType>?
    ) : AssignmentsUiEvent
}