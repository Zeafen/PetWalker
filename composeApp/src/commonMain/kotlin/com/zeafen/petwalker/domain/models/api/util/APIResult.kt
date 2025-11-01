package com.zeafen.petwalker.domain.models.api.util

sealed interface APIResult<out R, out E> {
    data class Succeed<out R>(val data: R? = null) : APIResult<R, Nothing>
    class Downloading : APIResult<Nothing, Nothing>
    class Error<out E : com.zeafen.petwalker.domain.models.api.util.Error>(
        val info: E,
        val additionalInfo: String? = null
    ) :
        APIResult<Nothing, E>
}