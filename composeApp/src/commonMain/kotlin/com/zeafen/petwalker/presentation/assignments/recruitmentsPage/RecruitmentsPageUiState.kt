package com.zeafen.petwalker.presentation.assignments.recruitmentsPage

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.ui.RecruitmentModel
import kotlinx.datetime.LocalDateTime

data class RecruitmentsPageUiState(
    val incomingRecruitments: APIResult<PagedResult<RecruitmentModel>, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val outcomingRecruitments: APIResult<PagedResult<RecruitmentModel>, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val lastSelectedIncomingPage: Int = 1,
    val lastSelectedOutcomingPage: Int = 1,

    val recruitmentsDirectionIndex: Int = 0,
    val openResultDialog: Boolean = false,
    val recruitmentRequestResult: APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>? = null,
    val selectedLoadGroup: RecruitmentsLoadGroup = RecruitmentsLoadGroup.All,
    val selectedState: RecruitmentState? = null,
    val dateFrom: LocalDateTime? = null,
    val dateUntil: LocalDateTime? = null,
)
