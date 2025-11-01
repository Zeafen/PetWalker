package com.zeafen.petwalker.data.helpers

import androidx.compose.runtime.Composable
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

@Composable
expect fun rememberDocumentPicker(onResult: (PetWalkerFileInfo?) -> Unit): DocumentPicker

expect class DocumentPicker(
    onLaunch: (extensions: List<String>) -> Unit
){
    fun launch(extensions: List<String>)
}

//"pdf", "jpeg", "png", "docx", "txt", "jpg", "svg", "mp4"
sealed interface ExtensionGroups{
    data object Image: ExtensionGroups{
        val exts = listOf("png", "jpg", "jpeg", "svg")
    }
    data object Audio: ExtensionGroups{
        val exts = listOf("mp3", "wav")
    }
    data object Video: ExtensionGroups{
        val exts = listOf("mp4", "wmv", "mkv")
    }
    data object Documents: ExtensionGroups{
        val exts = listOf("txt", "pdf", "docx", "xls", "doc", "rtf", "xlsx")
    }

}