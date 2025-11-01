package com.zeafen.petwalker.domain.models.api.filtering

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.all_option_display_name
import petwalker.composeapp.generated.resources.last_month_option_display_name
import petwalker.composeapp.generated.resources.last_week_option_display_name
import petwalker.composeapp.generated.resources.last_year_option_display_name
import petwalker.composeapp.generated.resources.today_option_display_name

@Serializable
enum class DatePeriods(val displayName: StringResource) {
    All(Res.string.all_option_display_name),
    LastYear(Res.string.last_year_option_display_name),
    LastMonth(Res.string.last_month_option_display_name),
    LastWeek(Res.string.last_week_option_display_name),
    Today(Res.string.today_option_display_name)
}