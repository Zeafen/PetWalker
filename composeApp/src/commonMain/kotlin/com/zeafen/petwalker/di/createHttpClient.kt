package com.zeafen.petwalker.di

import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val BASE_URL = "https://10.185.25.103:7129/"

class KtorClientProvider(
) : KoinComponent {
    val authDataStore: AuthDataStoreRepository by inject()

    fun createHttpClient(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {

            install(Auth) {
                bearer {
                    loadTokens {
                        authDataStore.authDataStoreFlow.first().token?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }
                    refreshTokens {
                        val refreshToken =
                            authDataStore.authDataStoreFlow.first().token?.refreshToken

                        refreshToken?.let {
                            val accessTokenRequest = client.get(BASE_URL + "auth/refresh") {
                                header("refresh_token", refreshToken)
                                markAsRefreshTokenRequest()
                            }
                            if (accessTokenRequest.status.value !in 200..299)
                                return@let null

                            val accessToken = accessTokenRequest.body<String>().replace("\"", "")
                            authDataStore.updateUserToken(TokenResponse(accessToken, refreshToken))

                            BearerTokens(accessToken, refreshToken)
                        }
                    }
                }
            }

            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}