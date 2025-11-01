package com.zeafen.petwalker.domain.models.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequest(
    val confirmCode : String,
    val password : String
)
