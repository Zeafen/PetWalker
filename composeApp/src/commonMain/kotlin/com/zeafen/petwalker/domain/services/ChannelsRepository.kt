package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.messaging.Channel
import com.zeafen.petwalker.domain.models.api.messaging.Message
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

interface ChannelsRepository {
    /***
     * Gets assignment channel
     * @return Channel entity for the specified assignment if request succeeded, otherwise - Error info
     * @param assignmentId - Identifier of the assignment
     */
    suspend fun getAssignmentChannel(
        assignmentId: String
    ): APIResult<Channel, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets messages of the assignment channel
     * @return Messages list of the specified channel if request succeeded, otherwise - Error info
     * @param channelId - Identifier of the channel
     * @param page - page of the messages list
     * @param perPage - page size of the messages list
     */
    suspend fun getChannelMessages(
        channelId: String,
        page: Int? = null,
        perPage: Int? = null
    ): APIResult<PagedResult<Message>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Posts message to the channel
     * @return Posted message entity if request succeeded, otherwise - Error info
     * @param channelId - Identifier of the channel
     */
    suspend fun postMessage(
        channelId: String,
        messageBody: String? = null,
        files: List<PetWalkerFileInfo>? = null
    ): APIResult<Message, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates message of the channel
     * @param channelId - Identifier of the channel
     * @param messageId - Identifier of the message to update
     * @param messageBody - new Message body
     */
    suspend fun updateMessage(
        channelId: String,
        messageId: String,
        messageBody: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes message from the channel
     * @param channelId - Identifier of the channel
     * @param messageId - Identifier of the message
     */
    suspend fun deleteMessage(
        channelId: String,
        messageId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}