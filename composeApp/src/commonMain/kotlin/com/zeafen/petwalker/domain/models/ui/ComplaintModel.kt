package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import kotlinx.datetime.LocalDateTime

data class ComplaintModel(
    val id: String,
    val senderImageUrl: String?,
    val senderFullName: String,
    val assignmentId: String?,
    val topic: ComplaintTopic,
    val status: ComplaintStatus,
    val text: String,
    val datePosted: LocalDateTime,
    val dateSolved: LocalDateTime?,
    val isOwn: Boolean,
)