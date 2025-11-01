package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.SerialName
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.high_security_description
import petwalker.composeapp.generated.resources.high_security_display_name
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.low_security_description
import petwalker.composeapp.generated.resources.low_security_display_name
import petwalker.composeapp.generated.resources.middle_security_description
import petwalker.composeapp.generated.resources.middle_security_display_name
import petwalker.composeapp.generated.resources.sent

enum class ProfileSecurityLevel(
    val displayNameRes: StringResource,
    val descriptionRes: StringResource,
    val iconRes: DrawableResource
) {
    Low(
        Res.string.low_security_display_name,
        Res.string.low_security_description,
        Res.drawable.ic_online
    ),
    Middle(
        Res.string.middle_security_display_name,
        Res.string.middle_security_description,
        Res.drawable.ic_online
    ),
    High(
        Res.string.high_security_display_name,
        Res.string.high_security_description,
        Res.drawable.sent
    )
}