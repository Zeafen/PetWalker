package com.zeafen.petwalker.domain.models.api.other

import kotlinx.serialization.SerialName
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.boarding
import petwalker.composeapp.generated.resources.boarding_service_default_description
import petwalker.composeapp.generated.resources.boarding_service_display_name
import petwalker.composeapp.generated.resources.drop_In_service_default_description
import petwalker.composeapp.generated.resources.drop_In_service_display_name
import petwalker.composeapp.generated.resources.drop_in
import petwalker.composeapp.generated.resources.house_Sitting_service_default_description
import petwalker.composeapp.generated.resources.house_Sitting_service_display_name
import petwalker.composeapp.generated.resources.house_sitting
import petwalker.composeapp.generated.resources.other
import petwalker.composeapp.generated.resources.other_service_default_description
import petwalker.composeapp.generated.resources.other_service_display_name
import petwalker.composeapp.generated.resources.walking
import petwalker.composeapp.generated.resources.walking_service_default_description
import petwalker.composeapp.generated.resources.walking_service_display_name


enum class  ServiceType(
    val displayName: StringResource,
    val defaultDescription: StringResource,
    val displayImage: DrawableResource
) {
    @SerialName("boarding")
    Boarding(
        Res.string.boarding_service_display_name,
        Res.string.boarding_service_default_description,
        Res.drawable.boarding
    ),

    @SerialName("walking")
    Walking(
        Res.string.walking_service_display_name,
        Res.string.walking_service_default_description,
        Res.drawable.walking
    ),

    @SerialName("drop_in")
    Drop_In(
        Res.string.drop_In_service_display_name,
        Res.string.drop_In_service_default_description,
        Res.drawable.drop_in
    ),

    @SerialName("house_sitting")
    House_Sitting(
        Res.string.house_Sitting_service_display_name,
        Res.string.house_Sitting_service_default_description,
        Res.drawable.house_sitting
    ),

    @SerialName("other")
    Other(
        Res.string.other_service_display_name,
        Res.string.other_service_default_description,
        Res.drawable.other
    )
}