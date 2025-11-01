package com.zeafen.petwalker

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.zeafen.petwalker.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PetWalker",
        ) {
            App()
        }
    }
}