package com.zeafen.petwalker.presentation.reviews.complaintConfigure

import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class ComplaintConfigureUiState(
    val selectedComplaintId: String? = null,
    val complaintLoadingResult: APIResult<Unit, Error>? = null,

    val reviewedWalkerId: String = "",
    val reviewedWalkerName: String = "",
    val reviewedWalkerImageUrl: String? = null,
    val reviewedWalkerLoadingRes: APIResult<Unit, Error>? = null,

    val currentUserName: String = "",
    val currentUserImageUrl: String? = null,

    val complaintDetails: String = "",
    val detailsValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val complaintTopic: ComplaintTopic? = null,
    val canPublish: Boolean = false,

    val ownLoadedAssignments: APIResult<PagedResult<AssignmentModel>, Error> = APIResult.Downloading(),
    val ownAssignmentsLastLoadedPage: Int = 1,
    val selectedAssignment: AssignmentModel? = null
)