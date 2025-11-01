package com.zeafen.petwalker.data.services.api

import com.zeafen.petwalker.di.BASE_URL
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentsStats
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.UsersRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class PetWalkerUsersRepository(
    private val client: HttpClient
) : UsersRepository {
    override suspend fun getWalkers(
        page: Int?,
        perPage: Int?,
        locationInfo: APILocation?,
        maxDistance: Float?,
        name: String?,
        services: List<ServiceType>?,
        maxComplaintsCount: Int?,
        status: AccountStatus?,
        online: Boolean?,
        ordering: UsersOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Walker>, Error> {
        val result = try {
            client.get(BASE_URL + "users") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("location", locationInfo)
                parameter("maxDistance", maxDistance)
                parameter("name", name)
                parameter("services", services)
                parameter("maxComplaintsCount", maxComplaintsCount)
                parameter("status", status)
                parameter("online", online)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
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
                val body = result.body<PagedResult<Walker>>()
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

    override suspend fun getWalker(
        walkerId: String
    ): APIResult<Walker, Error> {
        val result = try {
            client.get(BASE_URL + "users/$walkerId") {
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
                val body = result.body<Walker>()
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

    override suspend fun getUserAssignmentStats(
        userId: String?,
        period: DatePeriods?
    ): APIResult<AssignmentsStats, Error> {
        val result = try {
            client.get(BASE_URL + "assignments/assignmentsStats") {
                contentType(io.ktor.http.ContentType.Application.Json)
                period?.let {
                    parameter("period", period.name)
                }
                userId?.let {
                    parameter("userId", it)
                }
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
                val body = result.body<AssignmentsStats>()
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

    override suspend fun loginExists(
        login: String
    ): APIResult<Boolean, Error> {
        val result = try {
            client.get(BASE_URL + "users/exist") {
                parameter("login", login)
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
                val body = result.body<Boolean>()
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

}