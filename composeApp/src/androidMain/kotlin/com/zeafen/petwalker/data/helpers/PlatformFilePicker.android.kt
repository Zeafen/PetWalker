package com.zeafen.petwalker.data.helpers

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

@Composable
actual fun rememberDocumentPicker(onResult: (PetWalkerFileInfo?) -> Unit): DocumentPicker {
    val ctx = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            object : ActivityResultContracts.GetContent() {
                override fun createIntent(context: Context, input: String): Intent {
                    if (input.contains(";")) {
                        val args = input.split(";")
                        return Intent(Intent.ACTION_GET_CONTENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(args))
                        }
                    } else
                        return super.createIntent(context, input)
                }
            }) { uri ->
            onResult(uri?.let { ctx.getFileInfoFromUri(it) })
        }
    return remember {
        DocumentPicker {
            launcher.launch(it.joinToString { "*/$it;" })
        }
    }
}

actual class DocumentPicker actual constructor(
    private val onLaunch: (extensions: List<String>) -> Unit
) {
    actual fun launch(extensions: List<String>) {
        onLaunch(extensions)
    }
}