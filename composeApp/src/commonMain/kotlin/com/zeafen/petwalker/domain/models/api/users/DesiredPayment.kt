package com.zeafen.petwalker.domain.models.api.users

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.desired_payment_card_display_name
import petwalker.composeapp.generated.resources.desired_payment_cash_display_name

@Serializable
enum class DesiredPayment(val displayName: StringResource) {
    @SerialName("cash")
    Cash(Res.string.desired_payment_cash_display_name),
    @SerialName("card")
    Card(Res.string.desired_payment_card_display_name)
}