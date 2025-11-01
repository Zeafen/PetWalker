package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val address: String
)