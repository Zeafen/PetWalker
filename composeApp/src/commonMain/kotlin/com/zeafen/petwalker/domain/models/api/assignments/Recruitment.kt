package com.zeafen.petwalker.domain.models.api.assignments

import kotlinx.serialization.Serializable

@Serializable
data class Recruitment(
    val id : String,
    val assignmentId : String,
    val userId : String,
    val outcoming : Boolean,
    val state : RecruitmentState
)
