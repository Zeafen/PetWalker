package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.other.Attachment

data class CommentaryModel(
    val id: String,
    val postId: String,
    val senderName: String,
    val senderImageUrl: String?,
    val text: String?,
    val attachments: List<Attachment>,
    val dateSent: Long,
    val dateEdited: Long?,
    val amountChildCommentaries: Long,
    val childCommentaries: List<CommentaryModel>
)

