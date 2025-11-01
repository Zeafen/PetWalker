package com.zeafen.petwalker.domain.models.api.pets

import kotlinx.serialization.SerialName
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.allergy_info_display_name
import petwalker.composeapp.generated.resources.meds_info_display_name
import petwalker.composeapp.generated.resources.other_service_display_name
import petwalker.composeapp.generated.resources.vaccination_info_display_name

enum class PetInfoType(val displayName: StringResource) {
    @SerialName("vaccination")
    Vaccination(Res.string.vaccination_info_display_name),

    @SerialName("allergy")
    Allergy(Res.string.allergy_info_display_name),

    @SerialName("meds")
    Meds(Res.string.meds_info_display_name),

    @SerialName("other")
    Other(Res.string.other_service_display_name)
}