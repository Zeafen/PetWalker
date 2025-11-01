package com.zeafen.petwalker.data

import coil3.toUri
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.IOError
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.toByteArray
import okio.FileSystem
import java.io.File
import javax.swing.filechooser.FileSystemView

actual class PetWalkerDownloadManager(
    private val client: HttpClient
) {
    actual suspend fun queryDownload(ref: String, name: String): Error? {
        return try {
            //check if file already exists
            val desktopPath = FileSystemView.getFileSystemView().homeDirectory
            val file = File(desktopPath, name)
            if(file.exists())
                IOError.FILE_ALREADY_EXISTS

            val response = client.get(ref)
            if(response.status.value !in 200..299)
                IOError.CANNOT_READ_DATA

            val fileData = response.bodyAsChannel().toByteArray()


            file.writeBytes(fileData)
            null
        }
        catch (ex: Exception){
            ex.printStackTrace()
            IOError.UNKNOWN
        }
    }
}