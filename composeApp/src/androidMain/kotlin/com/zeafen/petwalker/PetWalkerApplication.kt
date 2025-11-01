package com.zeafen.petwalker

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.zeafen.petwalker.di.initKoin
import org.koin.android.ext.koin.androidContext

class PetWalkerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@PetWalkerApplication)
        }
    }
}