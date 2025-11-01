package com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage

import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState

sealed interface AssignmentDetailsUiEvent {
    data class LoadAssignment(val assignmentId: String) : AssignmentDetailsUiEvent
    data object RecruitToAssignment: AssignmentDetailsUiEvent

    data object LoadChannel: AssignmentDetailsUiEvent
    data object LoadWalker : AssignmentDetailsUiEvent
    data object LoadOwner : AssignmentDetailsUiEvent
    data class LoadPets(val page: Int = 1) : AssignmentDetailsUiEvent

    data class SetSelectedTab(val index: Int) : AssignmentDetailsUiEvent
    data class SetSearchPetsName(val name: String) : AssignmentDetailsUiEvent
    data class SetSearchPetsSpecies(val species: String) : AssignmentDetailsUiEvent

    data class SetStatus(val status: AssignmentState): AssignmentDetailsUiEvent
    data class LoadAttachmentData(val ref: String, val name: String): AssignmentDetailsUiEvent
}