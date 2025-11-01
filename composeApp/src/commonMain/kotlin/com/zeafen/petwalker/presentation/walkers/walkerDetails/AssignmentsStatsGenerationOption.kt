package com.zeafen.petwalker.presentation.walkers.walkerDetails

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignment_stats_amount_display_name
import petwalker.composeapp.generated.resources.assignment_stats_avgincome_display_name
import petwalker.composeapp.generated.resources.assignment_stats_income_display_name
import petwalker.composeapp.generated.resources.reviews_tab_display_name

enum class AssignmentsStatsGenerationOption(val displayName: StringResource) {
    Amount(Res.string.assignment_stats_amount_display_name),
    TotalIncome(Res.string.assignment_stats_income_display_name),
    AverageIncome(Res.string.assignment_stats_avgincome_display_name)
}