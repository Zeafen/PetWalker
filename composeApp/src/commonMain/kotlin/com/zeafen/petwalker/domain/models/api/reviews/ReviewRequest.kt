package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    val text : String,
    val rating : Int
)
