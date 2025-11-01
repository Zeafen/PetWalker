package com.zeafen.petwalker.presentation.posts.postConfigure

import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

sealed interface PostConfigureUiEvent {
    data class SetSelectedPostId(val id: String?) : PostConfigureUiEvent
    data object PublishData : PostConfigureUiEvent
    data class SetSelectedTab(val index: Int) : PostConfigureUiEvent

    data class SetPostTitle(val title: String) : PostConfigureUiEvent
    data class SetPostType(val type: PostType?) : PostConfigureUiEvent
    data class SetPostText(val text: String) : PostConfigureUiEvent

    data class AddAttachment(val fileInfo: PetWalkerFileInfo) : PostConfigureUiEvent
    data class RemoveAttachment(val id: String) : PostConfigureUiEvent
    data object ClearResult: PostConfigureUiEvent
}