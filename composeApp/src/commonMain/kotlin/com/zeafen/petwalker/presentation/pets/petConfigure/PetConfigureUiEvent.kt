package com.zeafen.petwalker.presentation.pets.petConfigure

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import kotlinx.datetime.LocalDateTime

sealed interface PetConfigureUiEvent {
    data class SetSelectedPetId(val id: String?) : PetConfigureUiEvent

    data class SetPetName(val name: String) : PetConfigureUiEvent
    data class SetPetSpecies(val species: String) : PetConfigureUiEvent
    data class SetPetBreed(val breed: String) : PetConfigureUiEvent
    data class SetPetImage(val fileInfo: PetWalkerFileInfo) : PetConfigureUiEvent
    data class SetPetWeight(val weight: String) : PetConfigureUiEvent
    data class SetPetDateBirth(val dateBirth: LocalDateTime?) : PetConfigureUiEvent
    data class SetPetDescription(val description: String) : PetConfigureUiEvent

    data class SelectMedicalInfo(val id: String?) : PetConfigureUiEvent
    data class AddPetMedialInfo(
        val type: PetInfoType,
        val name: String?,
        val description: String?,
        val document: PetWalkerFileInfo?
    ) : PetConfigureUiEvent

    data class EditMedicalInfo(
        val id: String,
        val type: PetInfoType,
        val name: String?,
        val description: String?,
        val document: PetWalkerFileInfo?
    ) : PetConfigureUiEvent

    data class RemovePetMedialInfo(val id: String) : PetConfigureUiEvent

    data object PublishData : PetConfigureUiEvent
    data object DeletePet : PetConfigureUiEvent
    data object ClearResult : PetConfigureUiEvent
}