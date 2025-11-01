package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.LocationInfo
import com.zeafen.petwalker.domain.models.api.users.UserService

data class WalkerCardModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val email: String?,
    val isOnline: Boolean?,
    val rating: Float,
    val reviewsCount: Long,
    val complaintsCount: Long,
    val repeatingOrdersCount: Long,
    val location: LocationInfo?,
    val distance: Float?,
    val desiredPayment: DesiredPayment?
)
