package com.zeafen.petwalker.domain.models.api.messaging

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id : String,
    val memberUserName: String,
    val memberImageUrl: String?,
    val isClosed : Boolean,
    val unreadMessagesCount : Long,
    val lastMessages : List<Message>?
)