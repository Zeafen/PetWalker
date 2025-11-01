package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.Serializable

@Serializable
data class ReviewsStats(
    val rating: Float,
    val totalReviewsCount: Long,
    val ratingPercentage: Map<Int, Float>
)
