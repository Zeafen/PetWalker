package com.zeafen.petwalker.presentation.posts.postDetailsPage

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.CommentaryModel
import com.zeafen.petwalker.domain.models.ui.PostModel
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class PostDetailsUiState(
    val selectedPostId: String? = null,
    val post: APIResult<PostModel, Error> = APIResult.Downloading(),
    val selectedParentCommentary: CommentaryModel? = null,

    val commentaries: List<CommentaryModel> = emptyList(),
    val currentCommentariesPageComb: Pair<Int, Int> = 1 to 1,
    val maxCommentariesPages: Int = 2,
    val commentariesLoadingError: Error? = null,
    val isCommentariesLoading: Boolean = false,
    val isLoadingDownwards: Boolean = false,
    val maxCommentariesPageReached: Boolean = false,

    val sendingCommentaryResult: ValidationInfo? = null,

    val selectedAttachmentUris: List<Attachment> = emptyList(),
    val attachmentFiles: Map<String, PetWalkerFileInfo> = mapOf(),
    val commentString: String = "",
    val canSend: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val fileLoadingResult: Error? = null
)