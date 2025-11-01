package com.zeafen.petwalker.domain.models.api.assignments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.booked_state_display_name
import petwalker.composeapp.generated.resources.closed_label
import petwalker.composeapp.generated.resources.completed_state_display_name
import petwalker.composeapp.generated.resources.in_progress_state_display_name
import petwalker.composeapp.generated.resources.pending_status_display_name
import petwalker.composeapp.generated.resources.searching_state_display_name

@Serializable
enum class AssignmentState(val displayName: StringResource) {
    @SerialName("searching")
    Searching(Res.string.searching_state_display_name),

    @SerialName("pending")
    Pending(Res.string.pending_status_display_name),

    @SerialName("in_progress")
    In_Progress(Res.string.in_progress_state_display_name),

    @SerialName("booked")
    Booked(Res.string.booked_state_display_name),

    @SerialName("completed")
    Completed(Res.string.completed_state_display_name),

    @SerialName("closed")
    Closed(Res.string.closed_label)
}