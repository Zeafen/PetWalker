package com.zeafen.petwalker.presentation.profile

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentsStats
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintsStats
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.Profile
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import kotlinx.datetime.LocalDateTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class ProfilePageUiState(
    val selectedTab: ProfileTabs = ProfileTabs.Main,
    val previousTab: ProfileTabs? = null,
    val profile: Profile? = null,
    val profileLoadingResult: APIResult<Unit, Error>? = null,
    val canEditInfo: Boolean = false,

    val imageUrl: PetWalkerFileInfo? = null,
    val services: List<UserService> = emptyList(),
    val desiredPayment: DesiredPayment? = null,
    val login: String = "",
    val loginValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),

    val firstName: String = "",
    val firstNameValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),

    val lastName: String = "",
    val lastNameValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),

    val aboutMe: String = "",
    val aboutMeValid: ValidationInfo = ValidationInfo(
        true,
        null,
        emptyList()
    ),

    val showAsWalker: Boolean = false,

    val emailEditingResult: APIResult<Unit, Error>? = null,
    val canConfirmEmail: Boolean = false,
    val email: String = "",
    val emailValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),

    val code: String = "",
    val codeValid: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val codeNextSendTime: LocalDateTime? = null,

    val reviewsStats: APIResult<ReviewsStats, Error>? = null,
    val complaintsStats: APIResult<ComplaintsStats, Error>? = null,
    val assignmentsStats: APIResult<AssignmentsStats, Error>? = null,
    val assignmentsStatsDatePeriod: DatePeriods = DatePeriods.All
)
