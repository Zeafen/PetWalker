package com.zeafen.petwalker.presentation.assignments.recruitmentsPage

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.incoming_tab_display_name
import petwalker.composeapp.generated.resources.outcoming_tab_display_name

enum class RecruitmentsDirection(val displayName: StringResource) {
    Incoming(Res.string.incoming_tab_display_name),
    OutComing(Res.string.outcoming_tab_display_name),
}