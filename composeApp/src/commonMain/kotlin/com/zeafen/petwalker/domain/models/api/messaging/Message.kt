package com.zeafen.petwalker.domain.models.api.messaging

import com.zeafen.petwalker.domain.models.api.other.Attachment
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val senderId: String,
    val body: String?,
    val isRead: Boolean,
    val isOwn: Boolean,
    val dateSent: LocalDateTime,
    val dateEdited: LocalDateTime?,
    val attachments: List<Attachment>
)
