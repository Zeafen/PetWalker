package com.zeafen.petwalker.presentation.posts.postConfigure

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.attachments_tab_display_name
import petwalker.composeapp.generated.resources.images_tab_display_name
import petwalker.composeapp.generated.resources.info_tab_display_name

enum class PostConfigureTabs(
    val displayName: StringResource
) {
    InfoInput(Res.string.info_tab_display_name),
    Images(Res.string.images_tab_display_name),
    Attachments(Res.string.attachments_tab_display_name)
}