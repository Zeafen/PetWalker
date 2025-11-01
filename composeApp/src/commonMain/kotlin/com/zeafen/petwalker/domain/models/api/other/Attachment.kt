package com.zeafen.petwalker.domain.models.api.other

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id : String,
    val type : AttachmentType,
    val name: String,
    val reference : String
)
