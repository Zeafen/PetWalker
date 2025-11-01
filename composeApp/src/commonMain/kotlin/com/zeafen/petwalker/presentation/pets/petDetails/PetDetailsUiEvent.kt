package com.zeafen.petwalker.presentation.pets.petDetails

import com.zeafen.petwalker.domain.models.api.pets.PetInfoType

sealed interface PetDetailsUiEvent {
    data class LoadPet(val petId: String) : PetDetailsUiEvent
    data object LoadMedicalInfo : PetDetailsUiEvent
    data class LoadMedicalDoc(val name: String, val ref: String): PetDetailsUiEvent

    data class SetSearchPetInfoType(val type: PetInfoType?) : PetDetailsUiEvent
}