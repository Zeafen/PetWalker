package com.zeafen.petwalker.data.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil3.annotation.InternalCoilApi
import coil3.util.MimeTypeMap
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@OptIn(InternalCoilApi::class)
@Composable
actual fun rememberDocumentPicker(onResult: (PetWalkerFileInfo?) -> Unit): DocumentPicker {

    val fileDialog = JFileChooser()

    return remember {
        DocumentPicker {
            fileDialog.isMultiSelectionEnabled = false
            fileDialog.fileFilter = FileNameExtensionFilter("Files", *it.toTypedArray())
            val res = fileDialog.showOpenDialog(null)
            val file = if (res == JFileChooser.APPROVE_OPTION)
                fileDialog.selectedFile
            else null
            onResult(
                file?.let { selectedFile ->
                    PetWalkerFileInfo(
                        selectedFile.absolutePath,
                        selectedFile.name,
                        MimeTypeMap.getMimeTypeFromExtension(selectedFile.extension) ?: "",
                        readBytes = {
                            selectedFile.readBytes()
                        }
                    )
                }
            )
        }
    }
}

actual class DocumentPicker actual constructor(private val onLaunch: (List<String>) -> Unit) {
    actual fun launch(extensions: List<String>) {
        onLaunch(extensions)
    }
}