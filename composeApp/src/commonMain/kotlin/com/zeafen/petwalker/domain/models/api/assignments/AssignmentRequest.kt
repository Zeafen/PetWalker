package com.zeafen.petwalker.domain.models.api.assignments

import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentRequest(
    val title: String,
    val description: String?,
    val type: ServiceType,
    val dateTime: LocalDateTime,
    val petIds: List<String>,
    val location: APILocation
)