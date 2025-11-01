package com.zeafen.petwalker.domain.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val login : String,
    val password : String
)
