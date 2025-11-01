package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class APILocation(
    val latitude: Double,
    val longitude: Double
)