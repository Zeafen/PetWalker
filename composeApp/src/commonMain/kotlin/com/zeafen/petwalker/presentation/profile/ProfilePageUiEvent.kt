package com.zeafen.petwalker.presentation.profile

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment

sealed interface ProfilePageUiEvent {
    data object LoadProfile : ProfilePageUiEvent

    data object LoadReviewsStats : ProfilePageUiEvent

    data object LoadComplaintsStats : ProfilePageUiEvent

    data class LoadAssignmentsStats(val period: DatePeriods) : ProfilePageUiEvent

    data class SetSelectedTab(val tab: ProfileTabs) : ProfilePageUiEvent
    data object GoToPrevTab : ProfilePageUiEvent
    data class SetLogin(val login: String) : ProfilePageUiEvent
    data class SetFirstName(val firstName: String) : ProfilePageUiEvent
    data class SetLastName(val lastName: String) : ProfilePageUiEvent
    data class SetEmail(val email: String) : ProfilePageUiEvent
    data class SetCode(val code: String) : ProfilePageUiEvent
    data class SetAboutMe(val aboutMe: String) : ProfilePageUiEvent
    data class SetImageUri(val fileInfo: PetWalkerFileInfo) : ProfilePageUiEvent
    data class SetDesiredPayment(val desiredPayment: DesiredPayment?) : ProfilePageUiEvent

    data class SetShowAsWalker(val showAsWalker: Boolean) : ProfilePageUiEvent

    data class RemoveService(val id: String) : ProfilePageUiEvent
    data class AddService(
        val serviceType: ServiceType,
        val additionalInfo: String? = null,
        val payment: Float? = null
    ) : ProfilePageUiEvent

    data class EditService(
        val id: String,
        val serviceType: ServiceType,
        val additionalInfo: String? = null,
        val payment: Float? = null
    ) : ProfilePageUiEvent

    data object PublishEditedInfo : ProfilePageUiEvent
    data object SendConfirmationCode : ProfilePageUiEvent
    data object ConfirmEmail : ProfilePageUiEvent
    data object CancelEditing : ProfilePageUiEvent

    data object ExitAccount : ProfilePageUiEvent
    data object DeleteAccount : ProfilePageUiEvent
}