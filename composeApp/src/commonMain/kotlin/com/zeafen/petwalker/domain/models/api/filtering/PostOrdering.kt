package com.zeafen.petwalker.domain.models.api.filtering

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_ordering_display_name
import petwalker.composeapp.generated.resources.live_lines_ordering_display_name
import petwalker.composeapp.generated.resources.popularity_ordering_display_name
import petwalker.composeapp.generated.resources.thumbs_ordering_display_name

@Serializable
enum class PostOrdering(val displayName: StringResource) {
    Date(Res.string.date_ordering_display_name),
    LiveLines(Res.string.live_lines_ordering_display_name),
    Thumbs(Res.string.thumbs_ordering_display_name),
    Popularity(Res.string.popularity_ordering_display_name)
}