package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.SerialName
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.banned_status_display_name
import petwalker.composeapp.generated.resources.pending_status_display_name
import petwalker.composeapp.generated.resources.verified_status_display_name

enum class AccountStatus(val displayName: StringResource) {
    @SerialName("pending")
    Pending(Res.string.pending_status_display_name),

    @SerialName("verified")
    Verified(Res.string.verified_status_display_name),

    @SerialName("banned")
    Banned(Res.string.banned_status_display_name)
}