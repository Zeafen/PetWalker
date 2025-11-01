package com.zeafen.petwalker.domain.models.api.other

import kotlinx.serialization.SerialName

enum class AttachmentType {
    @SerialName("video")
    Video,

    @SerialName("audio")
    Audio,

    @SerialName("image")
    Image,

    @SerialName("document")
    Document
}