package com.zeafen.petwalker.domain.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val firstName : String,
    val lastName : String,
    val login : String,
    val password : String,
    val email : String?,
    val phone : String?,
)
