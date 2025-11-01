package com.zeafen.petwalker.presentation.reviews.reviewConfigure

import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.greater_than_error_txt

data class ReviewConfigureUiState(
    val selectedReviewId: String? = null,
    val reviewLoadingResult: APIResult<Unit, Error>? = null,

    val reviewedAssignmentId: String = "",
    val reviewedAssignmentType: ServiceType? = null,
    val reviewedAssignmentTitle: String = "",
    val assignmentLoadingResult: APIResult<Unit, Error>? = null,


    val currentUserImageUrl: String? = null,
    val currentUserName: String = "",

    val reviewRating: Int = 0,
    val ratingValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.greater_than_error_txt,
        listOf(0)
    ),
    val reviewText: String = "",
    val textValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),

    val canPublish: Boolean = false,
)
