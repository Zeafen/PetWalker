package com.zeafen.petwalker.data.helpers

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

fun HttpClient.clearBearerTokenValues() {
    val bearerProvider = authProviders.firstOrNull {
        it is BearerAuthProvider
    } as BearerAuthProvider?
    bearerProvider?.clearToken()
}