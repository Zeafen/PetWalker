package com.zeafen.petwalker.data.services.api

import com.zeafen.petwalker.di.BASE_URL
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentRequest
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class PetWalkerAssignmentsRepository(
    private val client: HttpClient
) : AssignmentsRepository {
    override suspend fun getAssignments(
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getOwnAssignmentsAsOwner(
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments/my/owner") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getOwnOpenAssignmentsAsOwner(
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments/my/owner/open") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getOwnAssignmentsAsWalker(
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments/my/walker") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getUserAssignmentsAsOwner(
        walkerId: String,
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
                parameter("ownerId", walkerId)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getUserAssignmentsAsWalker(
        walkerId: String,
        page: Int?,
        perPage: Int?,
        title: String?,
        location: APILocation?,
        maxDistance: Float?,
        timeFrom: String?,
        timeTo: String?,
        services: List<ServiceType>?,
        ordering: AssignmentsOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("title", title)
                parameter("location", location)
                parameter("maxDistance", maxDistance)
                parameter("timeFrom", timeFrom)
                parameter("timeTo", timeTo)
                parameter("services", services)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
                parameter("walkerId", walkerId)
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
                val body = result.body<PagedResult<Assignment>>()
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

    override suspend fun getAssignmentById(
        assignmentId: String
    ): APIResult<Assignment, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments/$assignmentId") {
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
                val body = result.body<Assignment>()
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

    override suspend fun canRecruitToAssignment(
        assignmentId: String
    ): APIResult<Boolean, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.get(BASE_URL + "assignments/$assignmentId/canrecruit") {
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

    override suspend fun postAssignment(
        request: AssignmentRequest
    ): APIResult<Assignment, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.post(BASE_URL + "assignments") {
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
                val body = result.body<Assignment>()
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

    override suspend fun updateAssignment(
        assignmentId: String,
        request: AssignmentRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.put(BASE_URL + "assignments/$assignmentId") {
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

    override suspend fun startAssignment(
        assignmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.put(BASE_URL + "assignments/$assignmentId/start") {
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

    override suspend fun completeAssignment(
        assignmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.put(BASE_URL + "assignments/$assignmentId/complete") {
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

    override suspend fun closeAssignment(
        assignmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.put(BASE_URL + "assignments/$assignmentId/close") {
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

    override suspend fun searchAssignment(assignmentId: String): APIResult<Unit, Error> {
        val result = try {
            client.put(BASE_URL + "assignments/$assignmentId/search") {
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

    override suspend fun deleteAssignment(
        assignmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error> {
        val result = try {
            client.delete(BASE_URL + "assignments/$assignmentId") {
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
}