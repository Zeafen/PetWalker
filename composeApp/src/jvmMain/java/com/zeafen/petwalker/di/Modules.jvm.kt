package com.zeafen.petwalker.di

import com.zeafen.petwalker.data.JvmLocationService
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.data.datastore.AuthDataStoreRepositoryImpl
import com.zeafen.petwalker.data.datastore.createDataStore
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import io.ktor.client.engine.config
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

actual val platformAppModule: Module = module {
    single<AuthDataStoreRepository> {
        AuthDataStoreRepositoryImpl(createDataStore())
    }
    singleOf(::JvmLocationService).bind<LocationService>()

    single {
        KtorClientProvider()
    }
    single {
        get<KtorClientProvider>().createHttpClient(OkHttp.config {
            config {
                val trustAllCerts = object : X509TrustManager{
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate?>?,
                        authType: String?
                    ) {}

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate?>?,
                        authType: String?
                    ) {}

                    override fun getAcceptedIssuers(): Array<out X509Certificate?>? = arrayOf()
                }
                val sslContext = SSLContext.getInstance("SSL")

                sslContext.init(null, arrayOf(trustAllCerts), SecureRandom())
                sslSocketFactory(sslContext.socketFactory, trustAllCerts)

                hostnameVerifier { p0, p1 -> true }
            }
        }.create())
    }
    singleOf(::PetWalkerDownloadManager)
}