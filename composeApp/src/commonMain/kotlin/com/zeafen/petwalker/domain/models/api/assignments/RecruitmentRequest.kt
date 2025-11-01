package com.zeafen.petwalker.domain.models.api.assignments

import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentRequest(
    val assignmentId : String,
    val userId : String?
)
