package com.zeafen.petwalker.di

import com.zeafen.petwalker.data.JvmLocationService
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.data.datastore.AuthDataStoreRepositoryImpl
import com.zeafen.petwalker.data.datastore.createDataStore
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformAppModule: Module = module {
    single<AuthDataStoreRepository> {
        AuthDataStoreRepositoryImpl(createDataStore())
    }
    singleOf(::JvmLocationService).bind<LocationService>()

    single {
        KtorClientProvider()
    }
    single {
        get<KtorClientProvider>().createHttpClient(OkHttp.create())
    }
    singleOf(::PetWalkerDownloadManager)
}