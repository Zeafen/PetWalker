package com.zeafen.petwalker.domain.models.api.posts

import com.zeafen.petwalker.domain.models.api.other.Attachment
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val userId: String,
    val type: PostType,
    val topic: String,
    val body: String?,
    val dateSent: LocalDateTime,
    val dateEdited: LocalDateTime?,
    val commentsCount: Long,
    val attachments: List<Attachment>
)
