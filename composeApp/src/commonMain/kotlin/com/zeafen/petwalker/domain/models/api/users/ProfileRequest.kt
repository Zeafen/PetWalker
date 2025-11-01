package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val login : String,
    val firstName : String,
    val lastName : String,
    val aboutMe : String?,
    val showAsWalker: Boolean?,
    val desiredPayment: DesiredPayment?
)
