package com.zeafen.petwalker.data

import com.zeafen.petwalker.domain.models.api.util.Error

expect class PetWalkerDownloadManager {
    suspend fun queryDownload(ref: String, name: String): Error?
}