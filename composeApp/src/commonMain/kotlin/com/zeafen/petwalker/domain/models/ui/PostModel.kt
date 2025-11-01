package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.domain.models.api.other.Attachment
import kotlinx.datetime.LocalDateTime

data class PostModel(
    val id: String,
    val senderName: String,
    val senderImageUrl: String?,
    val topic: String,
    val type: PostType,
    val body: String?,
    val attachments: List<Attachment>,
    val commentsCount: Long,
    val dateSent: LocalDateTime,
    val dateEdited: LocalDateTime?
)
