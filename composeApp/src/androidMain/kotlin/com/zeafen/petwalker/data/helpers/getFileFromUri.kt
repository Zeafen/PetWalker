package com.zeafen.petwalker.data.helpers

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import okhttp3.MediaType.Companion.toMediaType

fun Context.getFileInfoFromUri(uri: Uri): PetWalkerFileInfo? {
    var file: PetWalkerFileInfo? = null
    val cursor = applicationContext.contentResolver.query(
        uri,
        arrayOf(
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
        ),
        null,
        null,
        null
    )?.use {
        it.moveToFirst()
        val displayNameIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val mimeTypeIndex = it.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
        val displayName = it.getString(displayNameIndex)
        val mimeType = it.getString(mimeTypeIndex)

        file = PetWalkerFileInfo(uri.path, displayName, mimeType.toMediaType().type, null)
    }

    if (file != null)
        this.contentResolver.openInputStream(uri)?.use {
            val bytes = it.buffered().readBytes()
            file = file.copy(
                readBytes = {
                    bytes
                }
            )
        }

    return file
}