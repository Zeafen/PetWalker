package com.zeafen.petwalker.data

import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.IOError
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDownloadsDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUserDomainMask
import platform.Foundation.downloadTaskWithURL
import platform.darwin.OS_SIGNPOST_ID_EXCLUSIVE

actual class PetWalkerDownloadManager {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun queryDownload(ref: String, name: String): Error? {
        return try {
            val task = NSURLSession.sharedSession.downloadTaskWithURL(
                NSURL(string = ref)
            ) { url, response, error ->
                if (((response as? NSHTTPURLResponse)?.statusCode
                        ?: -1) !in 200..299 || url == null
                )
                    return@downloadTaskWithURL

                try {
                    val downloadsDir = NSFileManager.defaultManager.URLForDirectory(
                        NSDownloadsDirectory,
                        NSUserDomainMask,
                        null,
                        false,
                        null
                    ) ?: return@downloadTaskWithURL

                    val destination =
                        downloadsDir.URLByAppendingPathComponent(url.lastPathComponent ?: name)
                            ?: return@downloadTaskWithURL

                    NSFileManager.defaultManager.moveItemAtURL(
                        url, destination, null
                    )
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            task.resume()
            null
        } catch (ex: Exception) {
            ex.printStackTrace()
            IOError.UNKNOWN
        }
    }
}