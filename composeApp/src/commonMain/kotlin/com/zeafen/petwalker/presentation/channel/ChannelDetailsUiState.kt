package com.zeafen.petwalker.presentation.channel

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.messaging.Channel
import com.zeafen.petwalker.domain.models.api.messaging.Message
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class ChannelDetailsUiState(
    val selectedChannelId: String? = null,
    val selectedMessageId: String? = null,
    val selectedAssignmentId: String? = null,
    val channel: APIResult<Channel, Error> = APIResult.Downloading(),
    val fileLoadingError: Error? = null,

    val messages: List<Message> = emptyList(),
    val currentMessagesPageComb: Pair<Int, Int> = 1 to 1,
    val maxMessagesPages: Int = 1,
    val messagesLoadingError: Error? = null,
    val areMessagesLoading: Boolean = false,
    val isLoadingDownwards: Boolean = false,
    val maxMessagesPageReached: Boolean = true,

    val sendingMessageResult: APIResult<*, Error>? = null,

    val selectedAttachmentUris: List<Attachment> = emptyList(),
    val selectedAttachmentFiles: Map<String, PetWalkerFileInfo> = mapOf(),
    val messageString: String = "",
    val canSend: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val canAddAttachment: Boolean = true
)
