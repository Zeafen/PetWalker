package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.Serializable

@Serializable
data class ComplaintRequest(
    val topic : ComplaintTopic,
    val body : String,
    val assignmentId: String? = null,
)
