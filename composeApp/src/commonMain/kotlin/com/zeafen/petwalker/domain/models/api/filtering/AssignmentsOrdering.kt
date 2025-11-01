package com.zeafen.petwalker.domain.models.api.filtering

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.location_ordering_display_name
import petwalker.composeapp.generated.resources.pet_count_ordering_display_name
import petwalker.composeapp.generated.resources.time_ordering_display_name
import petwalker.composeapp.generated.resources.user_rating_ordering_display_name

enum class AssignmentsOrdering(val displayName: StringResource) {
    Time(Res.string.time_ordering_display_name),
    Location(Res.string.location_ordering_display_name),
    AmountPets(Res.string.pet_count_ordering_display_name),
    OwnerRating(Res.string.user_rating_ordering_display_name)
}