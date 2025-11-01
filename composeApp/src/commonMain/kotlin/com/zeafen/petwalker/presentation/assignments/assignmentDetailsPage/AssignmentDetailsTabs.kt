package com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignment_pets_tab_display_name
import petwalker.composeapp.generated.resources.channel_tab_display_name
import petwalker.composeapp.generated.resources.info_tab_display_name
import petwalker.composeapp.generated.resources.walker_info_tab_display_name

enum class AssignmentDetailsTabs(val displayName: StringResource) {
    Info(Res.string.info_tab_display_name),
    Pets(Res.string.assignment_pets_tab_display_name),
    WalkerInfo(Res.string.walker_info_tab_display_name),
    Channel(Res.string.channel_tab_display_name)
}