package com.zeafen.petwalker.domain.models.api.assignments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.accepted_state_display_name
import petwalker.composeapp.generated.resources.denied_state_display_name
import petwalker.composeapp.generated.resources.pending_status_display_name

@Serializable
enum class RecruitmentState(val displayName: StringResource) {
    @SerialName("pending")
    Pending(Res.string.pending_status_display_name),

    @SerialName("accepted")
    Accepted(Res.string.accepted_state_display_name),

    @SerialName("denied")
    Denied(Res.string.denied_state_display_name)
}