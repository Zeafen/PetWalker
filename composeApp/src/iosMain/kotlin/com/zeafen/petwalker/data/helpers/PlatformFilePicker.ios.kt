package com.zeafen.petwalker.data.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFullScreen
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberDocumentPicker(onResult: (PetWalkerFileInfo?) -> Unit): DocumentPicker {


    val documentDelegate = remember {
        object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentAtURL: NSURL
            ) {
                didPickDocumentAtURL.startAccessingSecurityScopedResource()
                val data = NSData.dataWithContentsOfURL(didPickDocumentAtURL)
                val bytes = data?.bytes?.readBytes(data.length.toInt())
                val path = didPickDocumentAtURL.absoluteURL.toString()
                val displayName = didPickDocumentAtURL.lastPathComponent
                val mimetype = didPickDocumentAtURL.pathExtension?.let {
                    UTType.typeWithFilenameExtension(it)
                        ?.preferredMIMEType
                }
                didPickDocumentAtURL.stopAccessingSecurityScopedResource()
                onResult.invoke(
                    if (displayName == null || mimetype == null)
                        null
                    else
                        PetWalkerFileInfo(
                            path,
                            displayName,
                            mimetype,
                            bytes?.let {
                                { bytes }
                            }
                        )
                )
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }

    val currentViewController = UIApplication.sharedApplication.delegate?.window?.rootViewController
    return remember {
        DocumentPicker {
            val documentPicker = UIDocumentPickerViewController(
                documentTypes = it,
                inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen
            )
            documentPicker.setDelegate(documentDelegate)
            documentPicker.modalPresentationStyle = UIModalPresentationFullScreen
            documentPicker
            currentViewController?.presentViewController(documentPicker, true, null)
        }
    }
}

actual class DocumentPicker actual constructor(private val onLaunch: (List<String>) -> Unit) {
    actual fun launch(extensions: List<String>) {
        onLaunch(extensions)
    }
}