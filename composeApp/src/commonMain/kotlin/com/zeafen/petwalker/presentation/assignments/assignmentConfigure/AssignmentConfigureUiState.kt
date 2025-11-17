package com.zeafen.petwalker.presentation.assignments.assignmentConfigure

import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.util.APIResult
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class AssignmentConfigureUiState(
    val selectedAssignmentId: String? = null,
    val assignmentLoadingResult: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null,

    val assignedPets: List<Pet> = emptyList(),
    val assignedPetsLoading: Boolean = false,
    val assignedPetsLoadingError: com.zeafen.petwalker.domain.models.api.util.Error? = null,
    val assignedPetsPages: Pair<Int, Int> = 1 to 1,
    val assignedPetsMaxPage: Int = 2,
    val maxPageReached: Boolean = false,
    val isLoadingForward: Boolean = true,

    val availablePets: APIResult<PagedResult<Pet>, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val assignmentTitle: String = "",
    val titleValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val assignmentType: ServiceType? = null,
    val assignmentDescription: String = "",

    val assignmentDate: LocalDateTime? = null,
    val assignmentTime: LocalTime = LocalTime(0, 0),
    val dateValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val canPublish: Boolean = false,
    val publishingResult: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null,
    val descriptionNeeded: Boolean = false,
    val descriptionValidation: ValidationInfo = ValidationInfo(true, null, emptyList())
)
