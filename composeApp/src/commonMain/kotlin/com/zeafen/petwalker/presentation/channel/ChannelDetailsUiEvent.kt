package com.zeafen.petwalker.presentation.channel

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.messaging.Message

sealed interface ChannelDetailsUiEvent {
    data class LoadChannel(val assignmentId: String) : ChannelDetailsUiEvent
    data class LoadMessages(val page: Int = 1) : ChannelDetailsUiEvent
    data class SetMessageString(val string: String) : ChannelDetailsUiEvent
    data class AddAttachment(val fileInfo: PetWalkerFileInfo) : ChannelDetailsUiEvent
    data class RemoveAttachment(val ref: String) : ChannelDetailsUiEvent
    data object SendMessage: ChannelDetailsUiEvent

    data class DeleteMessage(val messageId: String): ChannelDetailsUiEvent

    data class SetEditedMessage(val message: Message?): ChannelDetailsUiEvent

    data class LoadAttachment(val ref: String, val name: String) : ChannelDetailsUiEvent
}