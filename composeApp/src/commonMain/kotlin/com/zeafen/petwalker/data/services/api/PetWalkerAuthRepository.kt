package com.zeafen.petwalker.data.services.api

import com.zeafen.petwalker.di.BASE_URL
import com.zeafen.petwalker.domain.models.api.auth.AuthRequest
import com.zeafen.petwalker.domain.models.api.auth.RegisterRequest
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class PetWalkerAuthRepository(
    private val client: HttpClient
) : AuthRepository {
    override suspend fun signUp(request: RegisterRequest): APIResult<Unit, Error> {
        val result = try {
            client.post(BASE_URL + "auth/signup") {
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                APIResult.Succeed()
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun signIn(request: AuthRequest): APIResult<TokenResponse, Error> {
        val result = try {
            client.post(BASE_URL + "auth/signin") {
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                val body = result.body<TokenResponse>()
                APIResult.Succeed(body)
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun authorize(): APIResult<Unit, Error> {
        val result = try {
            client.get(BASE_URL + "auth/authorize")
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                APIResult.Succeed()
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun refreshAccessToken(refreshToken: String): APIResult<String, Error> {
        val result = try {
            client.get(BASE_URL + "auth/refresh") {
                header("refresh_token", refreshToken)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                val body = result.body<String>()
                APIResult.Succeed(body)
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun getEmailCode(email: String): APIResult<Unit, Error> {
        val result = try {
            client.get(BASE_URL + "auth/code") {
                parameter("email", email)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                APIResult.Succeed()
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun checkConfirmationCode(
        email: String,
        code: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.post(BASE_URL + "auth/code/check") {
                parameter("email", email)
                parameter("code", code)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                APIResult.Succeed()
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }

    override suspend fun updatePassword(
        email: String,
        code: String,
        password: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.post(BASE_URL + "auth/password") {
                parameter("email", email)
                parameter("code", code)
                parameter("password", password)
            }
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                APIResult.Succeed()
            }

            400 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            401 -> APIResult.Error(NetworkError.UNAUTHORIZED)
            404 -> APIResult.Error(NetworkError.NOT_FOUND, result.bodyAsText())
            409 -> APIResult.Error(NetworkError.CONFLICT, result.bodyAsText())
            408 -> APIResult.Error(NetworkError.REQUEST_TIMEOUT, result.bodyAsText())
            413 -> APIResult.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> APIResult.Error(NetworkError.SERVER_ERROR, result.bodyAsText())
            else -> APIResult.Error(NetworkError.UNKNOWN, result.bodyAsText())
        }
    }
}