package com.zeafen.petwalker.domain.models.api.other

import kotlinx.serialization.Serializable

@Serializable
data class PagedResult<T>(
    val result : List<T>,
    val currentPage : Int,
    val totalPages : Int,
    val pageSize : Int
)
