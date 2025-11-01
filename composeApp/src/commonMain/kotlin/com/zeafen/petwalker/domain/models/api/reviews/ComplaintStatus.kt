package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.active_status_display_name
import petwalker.composeapp.generated.resources.dismissed_status_display_name
import petwalker.composeapp.generated.resources.pending_status_display_name
import petwalker.composeapp.generated.resources.solved_status_display_name

@Serializable
enum class ComplaintStatus(val displayName: StringResource) {
    @SerialName("pending")
    Pending(Res.string.pending_status_display_name),

    @SerialName("active")
    Active(Res.string.active_status_display_name),

    @SerialName("solved")
    Solved(Res.string.solved_status_display_name),

    @SerialName("dismissed")
    Dismissed(Res.string.dismissed_status_display_name)
}