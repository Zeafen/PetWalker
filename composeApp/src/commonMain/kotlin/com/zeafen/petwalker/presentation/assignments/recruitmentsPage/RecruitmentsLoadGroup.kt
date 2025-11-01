package com.zeafen.petwalker.presentation.assignments.recruitmentsPage

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.all_option_display_name
import petwalker.composeapp.generated.resources.owner_option_display_name
import petwalker.composeapp.generated.resources.walker_details_pages_header

enum class RecruitmentsLoadGroup(val displayName: StringResource) {
    All(Res.string.all_option_display_name),
    AsWalker(Res.string.walker_details_pages_header),
    AsOwner(Res.string.owner_option_display_name)
}