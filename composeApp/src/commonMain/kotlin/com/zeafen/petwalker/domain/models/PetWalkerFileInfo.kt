package com.zeafen.petwalker.domain.models

data class PetWalkerFileInfo(
    val path: String?,
    val displayName: String,
    val mediaType: String,
    val readBytes: (() -> ByteArray)?
)