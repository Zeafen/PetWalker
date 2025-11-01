package com.zeafen.petwalker.domain.models.api.util

import org.jetbrains.compose.resources.StringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.cannot_access_file_error_txt
import petwalker.composeapp.generated.resources.file_already_exists_error_txt
import petwalker.composeapp.generated.resources.file_not_found_error_txt
import petwalker.composeapp.generated.resources.not_found_error
import petwalker.composeapp.generated.resources.unknown_error

enum class IOError : Error {
    FILE_ALREADY_EXISTS,
    FILE_NOT_FOUND,
    CANNOT_READ_DATA,
    ACCESS_DENIED,
    UNKNOWN
    ;

    override fun infoResource(): StringResource {
        return when (this) {
            IOError.FILE_ALREADY_EXISTS -> Res.string.file_already_exists_error_txt
            IOError.FILE_NOT_FOUND -> Res.string.file_not_found_error_txt
            IOError.CANNOT_READ_DATA -> Res.string.not_found_error
            IOError.ACCESS_DENIED -> Res.string.cannot_access_file_error_txt
            IOError.UNKNOWN -> Res.string.unknown_error
        }
    }
}