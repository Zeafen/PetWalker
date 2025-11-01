package com.zeafen.petwalker.presentation.posts.postConfigure

import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.empty_fields_error_txt

data class PostConfigureUiState(
    val selectedPostId: String? = null,
    val canPublish: Boolean = false,
    val postLoadingResult: APIResult<Unit, Error>? = null,

    val postTitle: String = "",
    val titleValidation: ValidationInfo = ValidationInfo(
        false,
        Res.string.empty_fields_error_txt,
        emptyList()
    ),
    val postType: PostType? = null,

    val postText: String = "",
    val textNeeded: Boolean = false,
    val textValidation: ValidationInfo = ValidationInfo(true, null, emptyList()),

    val attachments: List<Attachment> = emptyList(),
    val attachmentFiles: Map<String, PetWalkerFileInfo> = mapOf(),
    val selectedAttachment: Attachment? = null,
    val attachmentEditingResult: APIResult<Unit, Error>? = null,

    val selectedTabIndex: Int = 0
)
