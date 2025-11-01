package com.zeafen.petwalker.data

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.IOError

actual class PetWalkerDownloadManager(
    private val context: Context
) {
    actual suspend fun queryDownload(ref: String, name: String): Error? {
        val downLoadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(ref.toUri())
            .apply {
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    name
                )
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }
        return if (downLoadManager.enqueue(request) > 0) null else IOError.UNKNOWN
    }
}