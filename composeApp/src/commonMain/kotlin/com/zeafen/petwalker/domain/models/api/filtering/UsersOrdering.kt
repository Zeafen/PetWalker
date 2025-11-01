package com.zeafen.petwalker.domain.models.api.filtering

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.complaints_count_label
import petwalker.composeapp.generated.resources.location_ordering_display_name
import petwalker.composeapp.generated.resources.rating_ordering_display_name
import petwalker.composeapp.generated.resources.services_count_ordering_display_name

@Serializable
enum class UsersOrdering(
    val displayName: StringResource
) {
    Rating(Res.string.rating_ordering_display_name),
    ComplaintsCount(Res.string.complaints_count_label),
    ServicesCount(Res.string.services_count_ordering_display_name),
    Location(Res.string.location_ordering_display_name)
}