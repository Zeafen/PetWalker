package com.zeafen.petwalker.presentation.assignments.recruitmentsPage

import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import kotlinx.datetime.LocalDateTime

sealed interface RecruitmentsPageUiEvent {
    data class LoadOwnRecruitments(val page: Int = 1, val outComing: Boolean) :
        RecruitmentsPageUiEvent

    data class DeleteRecruitment(val id: String) : RecruitmentsPageUiEvent
    data class SetLoadType(val loadIncoming: Boolean) : RecruitmentsPageUiEvent
    data class AcceptsRecruitment(val recruitmentId: String) : RecruitmentsPageUiEvent
    data class DeclineRecruitment(val recruitmentId: String) : RecruitmentsPageUiEvent
    data class SetFilters(
        val loadGroup: RecruitmentsLoadGroup = RecruitmentsLoadGroup.All,
        val state: RecruitmentState? = null,
        val dateFrom: LocalDateTime? = null,
        val dateUntil: LocalDateTime? = null
    ) : RecruitmentsPageUiEvent
}