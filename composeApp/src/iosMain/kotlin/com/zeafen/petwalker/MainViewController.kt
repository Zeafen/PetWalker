package com.zeafen.petwalker

import androidx.compose.ui.window.ComposeUIViewController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.zeafen.petwalker.di.initKoin

fun MainViewController() {
    initKoin()
    ComposeUIViewController { App() }
}