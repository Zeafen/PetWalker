package com.zeafen.petwalker.domain.models.api.posts

import com.zeafen.petwalker.domain.models.api.other.Attachment
import kotlinx.serialization.Serializable

@Serializable
data class Commentary(
    val id: String,
    val userId: String,
    val postId: String,
    val parentCommentId: String?,
    val body: String,
    val dateSent: Long,
    val dateEdited: Long?,
    val childCommentariesCount: Long,
    val childCommentaries: List<Commentary>,
    val attachments: List<Attachment>
)
