package com.zeafen.petwalker.domain.models.api.reviews

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.other_service_display_name
import petwalker.composeapp.generated.resources.pet_harm_topic_display_name
import petwalker.composeapp.generated.resources.toxic_behaviour_topic_display_name

@Serializable
enum class ComplaintTopic(val displayName: StringResource) {

    @SerialName("toxic_behaviour")
    Toxic_Behaviour(Res.string.toxic_behaviour_topic_display_name),


    @SerialName("pet_harm")
    Pet_Harm(Res.string.pet_harm_topic_display_name),

    @SerialName("other")
    Other(Res.string.other_service_display_name)
}