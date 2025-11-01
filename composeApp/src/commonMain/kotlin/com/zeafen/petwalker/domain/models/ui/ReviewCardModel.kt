package com.zeafen.petwalker.domain.models.ui

import kotlinx.datetime.LocalDateTime

data class ReviewCardModel(
    val id : String,
    val senderImageUrl: String?,
    val senderFullName: String,
    val assignmentId : String,
    val text : String,
    val rating : Int,
    val datePosted : LocalDateTime,
    val dateUpdated : LocalDateTime?
)