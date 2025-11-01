package com.zeafen.petwalker.presentation.posts.postDetailsPage

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ui.CommentaryModel

sealed interface PostDetailsUiEvent {
    data class LoadPost(val id: String) : PostDetailsUiEvent
    data class LoadCommentaries(val page: Int = 1) : PostDetailsUiEvent
    data class LoadAttachment(val ref: String, val name: String) : PostDetailsUiEvent
    data class SetCommentaryString(val string: String) : PostDetailsUiEvent
    data class SetRespondingCommentary(val commentary: CommentaryModel?) : PostDetailsUiEvent
    data class AddAttachment(val fileInfo: PetWalkerFileInfo) : PostDetailsUiEvent
    data class RemoveAttachment(val ref: String) : PostDetailsUiEvent
    data object SendCommentary : PostDetailsUiEvent
}