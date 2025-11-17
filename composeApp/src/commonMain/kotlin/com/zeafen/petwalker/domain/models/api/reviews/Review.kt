package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val id : String,
    val senderId : String,
    val userId : String,
    val assignmentId : String,
    val text : String,
    val isOwn: Boolean,
    val rating : Int,
    val datePosted : LocalDateTime,
    val dateUpdated : LocalDateTime?
)