package com.zeafen.petwalker.data.services.api

import com.zeafen.petwalker.di.BASE_URL
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.PostOrdering
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.posts.Commentary
import com.zeafen.petwalker.domain.models.api.posts.Post
import com.zeafen.petwalker.domain.models.api.posts.PostType
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.PostsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully

class PetWalkerPostsRepository(
    private val client: HttpClient
) : PostsRepository {
    override suspend fun getPosts(
        page: Int?,
        perPage: Int?,
        topic: String?,
        type: PostType?,
        period: DatePeriods?,
        ordering: PostOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Post>, Error> {
        val result = try {
            client.get(BASE_URL + "posts") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("topic", topic)
                parameter("type", type)
                parameter("period", period)
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
                val body = result.body<PagedResult<Post>>()
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

    override suspend fun getUserPosts(
        walkerId: String,
        page: Int?,
        perPage: Int?,
        topic: String?,
        type: PostType?,
        period: DatePeriods?,
        ordering: PostOrdering?,
        ascending: Boolean?
    ): APIResult<PagedResult<Post>, Error> {
        val result = try {
            client.get(BASE_URL + "posts") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("topic", topic)
                parameter("type", type)
                parameter("period", period)
                parameter("ordering", ordering)
                parameter("ascending", ascending)
                parameter("userId", walkerId)
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
                val body = result.body<PagedResult<Post>>()
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

    override suspend fun getPost(
        postId: String
    ): APIResult<Post, Error> {
        val result = try {
            client.get(BASE_URL + "posts/$postId") {
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
                val body = result.body<Post>()
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

    override suspend fun getPostCommentaries(
        postId: String,
        page: Int?,
        perPage: Int?
    ): APIResult<PagedResult<Commentary>, Error> {
        val result = try {
            client.get(BASE_URL + "posts/$postId/commentaries") {
                parameter("page", page)
                parameter("perPage", perPage)
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
                val body = result.body<PagedResult<Commentary>>()
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

    override suspend fun getChildCommentaries(
        postId: String,
        commentaryId: String,
        page: Int?,
        perPage: Int?
    ): APIResult<PagedResult<Commentary>, Error> {
        val result = try {
            client.get(BASE_URL + "posts/$postId/commentaries/$commentaryId") {
                parameter("page", page)
                parameter("perPage", perPage)
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
                val body = result.body<PagedResult<Commentary>>()
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

    override suspend fun postPost(
        type: PostType,
        topic: String,
        body: String?,
        attachmentFiles: List<PetWalkerFileInfo>?
    ): APIResult<Post, Error> {
        val result = try {
            client.post(BASE_URL + "posts") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("topic", topic)
                            append("type", type.name)
                            attachmentFiles?.forEachIndexed { index, info ->
                                info.readBytes?.let { readBytes ->
                                    appendInput(
                                        "attachment$index",
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentType, info.mediaType)
                                        }
                                    ) {
                                        buildPacket { writeFully(readBytes()) }
                                    }
                                }
                            }
                        }
                    ))
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
                val body = result.body<Post>()
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

    override suspend fun postAttachments(
        postId: String,
        attachmentFiles: List<PetWalkerFileInfo>
    ): APIResult<List<Attachment>, Error> {
        val result = try {
            client.post(BASE_URL + "posts/$postId/attachment") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            attachmentFiles.forEachIndexed { index, info ->
                                info.readBytes?.let { readBytes ->
                                    appendInput(
                                        "attachment$index",
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentType, info.mediaType)
                                        }
                                    ) {
                                        buildPacket { writeFully(readBytes()) }
                                    }
                                }
                            }
                        }
                    ))
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
                val body = result.body<List<Attachment>>()
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

    override suspend fun postCommentary(
        postId: String,
        body: String?,
        attachmentFiles: List<PetWalkerFileInfo>?
    ): APIResult<Commentary, Error> {
        val result = try {
            client.post(BASE_URL + "posts/$postId/commentaries") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            body?.let {
                                append("body", body)
                            }
                            attachmentFiles?.forEachIndexed { index, info ->
                                info.readBytes?.let { readBytes ->
                                    appendInput(
                                        "attachment$index",
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentType, info.mediaType)
                                        }
                                    ) {
                                        buildPacket { writeFully(readBytes()) }
                                    }
                                }
                            }
                        }
                    ))
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
                val body = result.body<Commentary>()
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

    override suspend fun postCommentaryResponse(
        postId: String,
        parentCommentId: String,
        body: String?,
        attachmentFiles: List<PetWalkerFileInfo>?
    ): APIResult<Commentary, Error> {
        val result = try {
            client.post(BASE_URL + "posts/$postId/commentaries/$parentCommentId") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            body?.let {
                                append("body", body)
                            }
                            attachmentFiles?.forEachIndexed { index, info ->
                                info.readBytes?.let { readBytes ->
                                    appendInput(
                                        "attachment$index",
                                        headers = Headers.build {
                                            append(HttpHeaders.ContentType, info.mediaType)
                                        }
                                    ) {
                                        buildPacket { writeFully(readBytes()) }
                                    }
                                }
                            }
                        }
                    ))
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
                val body = result.body<Commentary>()
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

    override suspend fun updatePost(
        postId: String,
        topic: String?,
        body: String?,
        type: PostType?
    ): APIResult<Unit, Error> {
        val result = try {
            client.put(BASE_URL + "posts/$postId") {
                parameter("topic", topic)
                parameter("body", body)
                parameter("type", type)
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

    override suspend fun updateCommentary(
        postId: String,
        commentaryId: String,
        body: String?
    ): APIResult<Unit, Error> {
        val result = try {
            client.put(BASE_URL + "posts/$postId/commentaries/$commentaryId") {
                parameter("body", body)
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

    override suspend fun deletePost(
        postId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "posts/$postId") {
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

    override suspend fun deleteCommentary(
        postId: String,
        commentaryId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "posts/$postId/commentaries/$commentaryId") {
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

    override suspend fun deleteAttachment(
        postId: String,
        attachmentId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "posts/$postId/$attachmentId") {
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