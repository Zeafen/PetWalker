package com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.messaging.Channel
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.Error

data class AssignmentDetailsUiState(
    val selectedAssignmentId: String? = null,
    val assignment: APIResult<Assignment, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val owns: Boolean = false,
    val filesLoadingError: Error? = null,

    val canRecruit: Boolean = false,
    val recruitingResult: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null,

    val distanceToAssignment: Float? = null,
    val assignmentOwner: APIResult<Walker, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),

    val assignmentPets: APIResult<PagedResult<Pet>, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val lastSelectedPetPage: Int = 1,
    val searchPetsName: String = "",
    val searchPetsSpecies: String = "",
    val petsAgeDescending: Boolean? = null,

    val assignmentWalker: APIResult<Walker, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val distanceToWalker: Float? = null,

    val assignmentChannel: APIResult<Channel, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),

    val selectedTabIndex: Int = 0
)