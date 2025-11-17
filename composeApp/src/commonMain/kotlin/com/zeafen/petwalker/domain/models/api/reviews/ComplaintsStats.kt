package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.Serializable

@Serializable
data class ComplaintsStats(
    val totalCount: Long,
    val activeCount: Long,
    val solvedCount: Long,
    val topicsPercentage: Map<ComplaintTopic, Float>
)
