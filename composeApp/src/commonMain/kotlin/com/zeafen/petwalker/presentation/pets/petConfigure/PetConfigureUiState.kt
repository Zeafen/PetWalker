package com.zeafen.petwalker.presentation.pets.petConfigure

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import kotlinx.datetime.LocalDateTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.nan_error_txt

data class PetConfigureUiState(
    val selectedPetId: String? = null,
    val petLoadingResult: APIResult<Unit, Error>? = null,

    val petName: String = "",
    val nameValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val petSpecies: String = "",
    val speciesValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val petBreed: String = "",
    val breedValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val petWeight: String = "",
    val weightValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.nan_error_txt,
        emptyList()
    ),
    val petDateBirth: LocalDateTime? = null,
    val date_birthValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.nan_error_txt,
        emptyList()
    ),
    val petImageUri: PetWalkerFileInfo? = null,
    val petDesc: String = "",
    val canPublish: Boolean = false,

    val petMedicalInfos: List<PetMedicalInfo> = emptyList(),
    val medicalInfoDocs: Map<String, PetWalkerFileInfo> = mapOf(),
    val medicalInfoEditingResult: APIResult<Unit, Error>? = null,
    val selectedMedicalInfo: PetMedicalInfo? = null,
)
