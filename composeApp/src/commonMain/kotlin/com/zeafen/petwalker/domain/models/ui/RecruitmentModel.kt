package com.zeafen.petwalker.domain.models.ui

import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import kotlinx.datetime.LocalDateTime

data class RecruitmentModel(
    val id: String,
    val assignmentTitle: String?,
    val assignmentDateTime: LocalDateTime?,
    val senderName: String?,
    val senderImageUrl: String?,
    val assignmentId: String,
    val walkerId: String,
    val outcoming: Boolean,
    val state: RecruitmentState,
)