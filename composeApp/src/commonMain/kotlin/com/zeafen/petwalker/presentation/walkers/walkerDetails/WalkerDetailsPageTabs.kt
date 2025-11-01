package com.zeafen.petwalker.presentation.walkers.walkerDetails

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignments_history_tab_display_name
import petwalker.composeapp.generated.resources.complaints_tab_display_name
import petwalker.composeapp.generated.resources.pending_status_display_name
import petwalker.composeapp.generated.resources.reviews_tab_display_name

enum class WalkerDetailsPageTabs(val displayName: StringResource) {
    Info(Res.string.pending_status_display_name),
    Reviews(Res.string.reviews_tab_display_name),
    Complaints(Res.string.complaints_tab_display_name),
    AssignmentsHistory(Res.string.assignments_history_tab_display_name)
}