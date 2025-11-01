package com.zeafen.petwalker.presentation.assignments.assignmentConfigure

import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.pets.Pet
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed interface AssignmentConfigureUiEvent {
    data class SetEditedAssignmentId(val id: String?) : AssignmentConfigureUiEvent

    data class SetAssignmentTitle(val title: String) : AssignmentConfigureUiEvent
    data class SetAssignmentDate(val date: LocalDateTime?) : AssignmentConfigureUiEvent
    data class SetAssignmentType(val type: ServiceType?) : AssignmentConfigureUiEvent
    data class SetAssignmentDescription(val description: String) : AssignmentConfigureUiEvent

    data class LoadAvailablePets(val page: Int = 1) : AssignmentConfigureUiEvent
    data class LoadAssignedPets(val page: Int = 1): AssignmentConfigureUiEvent
    data class AddAssignedPet(val pet: Pet) : AssignmentConfigureUiEvent
    data class RemoveAssignedPet(val petId: String) : AssignmentConfigureUiEvent

    data object ApplyChanges : AssignmentConfigureUiEvent
    data object DeleteAssignment : AssignmentConfigureUiEvent
}