package com.zeafen.petwalker.domain.models.api.pets

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PetRequest(
    val name: String,
    val species: String,
    val breed: String,
    val description: String?,
    val date_birth: LocalDateTime,
    val weight: Float
)