package com.zeafen.petwalker.domain.models.api.util

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.no_internet_error
import petwalker.composeapp.generated.resources.not_found_error
import petwalker.composeapp.generated.resources.payload_too_large_data
import petwalker.composeapp.generated.resources.request_time_out_error
import petwalker.composeapp.generated.resources.serialization_error
import petwalker.composeapp.generated.resources.server_error
import petwalker.composeapp.generated.resources.unauthorized_error
import petwalker.composeapp.generated.resources.unknown_error

enum class NetworkError: Error {
    REQUEST_TIMEOUT,
    UNAUTHORIZED,
    CONFLICT,
    SERVER_ERROR,
    NO_INTERNET,
    NOT_FOUND,
    PAYLOAD_TOO_LARGE,
    SERIALIZATION,
    UNKNOWN, ;

    override fun infoResource(): StringResource {
        return when(this){
            NetworkError.REQUEST_TIMEOUT -> Res.string.request_time_out_error
            NetworkError.UNAUTHORIZED -> Res.string.unauthorized_error
            NetworkError.CONFLICT -> Res.string.conflict_error
            NetworkError.SERVER_ERROR -> Res.string.server_error
            NetworkError.NO_INTERNET -> Res.string.no_internet_error
            NetworkError.NOT_FOUND -> Res.string.not_found_error
            NetworkError.PAYLOAD_TOO_LARGE -> Res.string.payload_too_large_data
            NetworkError.SERIALIZATION -> Res.string.serialization_error
            NetworkError.UNKNOWN -> Res.string.unknown_error
        }
    }

}