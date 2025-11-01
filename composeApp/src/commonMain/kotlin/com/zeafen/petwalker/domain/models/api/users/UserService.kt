package com.zeafen.petwalker.domain.models.api.users

import com.zeafen.petwalker.domain.models.api.other.ServiceType
import kotlinx.serialization.Serializable

@Serializable
data class UserService(
    val id: String,
    val service : ServiceType,
    val additionalInfo : String?,
    val payment: Float?
)
