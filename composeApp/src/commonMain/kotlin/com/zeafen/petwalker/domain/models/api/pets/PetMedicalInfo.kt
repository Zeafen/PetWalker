package com.zeafen.petwalker.domain.models.api.pets

import kotlinx.serialization.Serializable

@Serializable
data class PetMedicalInfo(
    val id : String,
    val description : String?,
    val type : PetInfoType,
    val reference : String?,
    val name: String?
)
