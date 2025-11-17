package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Complaint(
    val id : String,
    val senderId : String,
    val userId : String,
    val assignmentId : String?,
    val topic : ComplaintTopic,
    val status : ComplaintStatus,
    val body : String,
    val isOwn: Boolean,
    val datePosted : LocalDateTime,
    val dateSolved : LocalDateTime?
)
