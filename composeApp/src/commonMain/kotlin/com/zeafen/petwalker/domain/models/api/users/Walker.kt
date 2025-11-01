package com.zeafen.petwalker.domain.models.api.users

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Walker(
    val id : String,
    val firstName : String,
    val lastName : String,
    val aboutMe : String?,
    val imageUrl : String?,
    val email : String?,
    val phone : String?,
    val isOnline : Boolean?,
    val rating : Float,
    val reviewsCount : Long,
    val complaintsCount : Long,
    val repeatingOrdersCount: Long,
    val accountStatus : AccountStatus,
    val lastLogged : LocalDateTime?,
    val location : LocationInfo?,
    val services : List<UserService>,
    val desiredPayment: DesiredPayment?
)
