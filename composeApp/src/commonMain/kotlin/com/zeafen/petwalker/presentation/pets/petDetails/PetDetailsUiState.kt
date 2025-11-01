package com.zeafen.petwalker.presentation.pets.petDetails

import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error

data class PetDetailsUiState(
    val pet: APIResult<Pet, Error> = APIResult.Downloading(),
    val own: Boolean = false,
    val selectedPetId: String? = null,
    val petMedicalInfo: APIResult<List<PetMedicalInfo>, Error> = APIResult.Downloading(),
    val selectedMedicalInfoType: PetInfoType? = null,
    val fileLoadingError: Error? = null
)
