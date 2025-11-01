package com.zeafen.petwalker.domain.models.api.assignments

import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Assignment(
    val id: String,
    val ownerId: String,
    val walkerId: String?,
    val title: String,
    val description: String?,
    val type: ServiceType,
    val datePublished: LocalDateTime,
    val dateTime: LocalDateTime,
    val state: AssignmentState,
    val location: APILocation,
    val payment: Float?
)