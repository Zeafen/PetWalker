package com.zeafen.petwalker.domain.models.api.users

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val login : String,
    val firstName : String,
    val lastName : String,
    val aboutMe : String?,
    val imageUrl : String?,
    val email : String?,
    val phone : String?,
    val accountStatus : AccountStatus,
    val isOnline : Boolean?,
    val showAsWalker: Boolean,
    val services : List<UserService>,
    val securityLevel: ProfileSecurityLevel,
    val passwordLastChanged: LocalDateTime?,
)
