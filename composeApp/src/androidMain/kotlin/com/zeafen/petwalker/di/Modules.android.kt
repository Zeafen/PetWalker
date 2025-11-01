package com.zeafen.petwalker.di

import com.zeafen.petwalker.data.AndroidLocationService
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.data.datastore.AuthDataStoreRepositoryImpl
import com.zeafen.petwalker.data.datastore.createDataStore
import com.zeafen.petwalker.data.helpers.AllCertsTrustManager
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import io.ktor.client.engine.config
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.security.SecureRandom
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession

actual val platformAppModule: Module = module {
    single<AuthDataStoreRepository> {
        AuthDataStoreRepositoryImpl(createDataStore(this.androidContext()))
    }
    single<PetWalkerDownloadManager>{
        PetWalkerDownloadManager(this.androidContext())
    }
    single<LocationService> {
        AndroidLocationService(this.androidContext())
    }
    single {
        KtorClientProvider()
    }
    single {
        get<KtorClientProvider>().createHttpClient(OkHttp.config {
            config {
                val trustAllCerts = AllCertsTrustManager()
                val sslContext = SSLContext.getInstance("SSL")

                sslContext.init(null, arrayOf(trustAllCerts), SecureRandom())
                sslSocketFactory(sslContext.socketFactory, trustAllCerts)

                hostnameVerifier { p0, p1 -> true }
            }
        }.create())
    }
}