package com.zeafen.petwalker.domain.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken : String,
    val refreshToken : String
)
