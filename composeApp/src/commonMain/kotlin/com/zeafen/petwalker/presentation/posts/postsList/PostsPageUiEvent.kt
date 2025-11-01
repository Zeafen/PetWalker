package com.zeafen.petwalker.presentation.posts.postsList

import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.PostOrdering
import com.zeafen.petwalker.domain.models.api.posts.PostType

sealed interface PostsPageUiEvent {
    data class LoadPosts(val page: Int = 1) : PostsPageUiEvent
    data class SetSearchTopic(val topic: String) : PostsPageUiEvent
    data class SetOrdering(val ordering: PostOrdering?) : PostsPageUiEvent
    data class LoadAttachment(val ref: String, val name: String) : PostsPageUiEvent
    data class SetFilters(
        val type: PostType?,
        val period: DatePeriods?
    ) : PostsPageUiEvent
}