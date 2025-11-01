package com.zeafen.petwalker.presentation.posts.postsList

import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.PostOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.PostModel

data class PostsPageUiState(
    val posts: APIResult<PagedResult<PostModel>, Error> = APIResult.Downloading(),
    val lastSelectedPage: Int = 1,
    val searchTopic: String = "",
    val selectedType: PostType? = null,
    val selectedPeriod: DatePeriods? = null,
    val ordering: PostOrdering? = null,
    val ascending: Boolean = true,
    val fileLoadingError: Error? = null
)
