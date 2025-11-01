package com.zeafen.petwalker.domain.models.api.reviews

data class ComplaintsStats(
    val totalCount: Long,
    val activeCount: Long,
    val solvedCount: Long,
    val topicsPercentage: Map<ComplaintTopic, Float>
)
