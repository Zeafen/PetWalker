package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlinx.datetime.LocalDateTime

data class AssignmentModel(
    val id: String,
    val ownerName: String,
    val ownerImageUrl: String?,
    val title: String,
    val type: ServiceType,
    val datePublished: LocalDateTime,
    val dateTime: LocalDateTime,
    val location: APILocation,
    val distance: Float?,
    val payment: Float?,
)
