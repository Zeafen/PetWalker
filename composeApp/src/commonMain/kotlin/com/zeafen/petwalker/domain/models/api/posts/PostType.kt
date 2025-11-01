package com.zeafen.petwalker.domain.models.api.posts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.advice_help_type_display_name
import petwalker.composeapp.generated.resources.discussion_type_display_name
import petwalker.composeapp.generated.resources.other_service_display_name
import petwalker.composeapp.generated.resources.story_type_display_name
import petwalker.composeapp.generated.resources.video_type_display_name

@Serializable
enum class PostType(val displayName: StringResource) {
    @SerialName("discussion")
    Discussion(Res.string.discussion_type_display_name),

    @SerialName("video")
    Video(Res.string.video_type_display_name),

    @SerialName("story")
    Story(Res.string.story_type_display_name),

    @SerialName("advice_help")
    Advice_Help(Res.string.advice_help_type_display_name),

    @SerialName("other")
    Other(Res.string.other_service_display_name)
}