package com.zeafen.petwalker.data.services.api

import com.zeafen.petwalker.di.BASE_URL
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.pets.PetRequest
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.PetsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException

class PetWalkerPetsRepository(
    private val client: HttpClient
) : PetsRepository {
    override suspend fun getAssignmentPets(
        assignmentId: String,
        page: Int?,
        perPage: Int?,
        name: String?,
        species: String?,
        ageDescending: Boolean?
    ): APIResult<PagedResult<Pet>, Error> {
        val result = try {
            client.get(BASE_URL + "pets") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("name", name)
                parameter("species", species)
                parameter("ageAscending", ageDescending?.not())
                parameter("assignmentId", assignmentId)
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
                val body = result.body<PagedResult<Pet>>()
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

    override suspend fun getOwnPets(
        page: Int?,
        perPage: Int?,
        name: String?,
        species: String?,
        ageDescending: Boolean?
    ): APIResult<PagedResult<Pet>, Error> {
        val result = try {
            client.get(BASE_URL + "pets") {
                parameter("page", page)
                parameter("perPage", perPage)
                parameter("name", name)
                parameter("species", species)
                parameter("ageAscending", ageDescending?.not())
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
                val body = result.body<PagedResult<Pet>>()
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

    override suspend fun getPet(
        petId: String
    ): APIResult<Pet, Error> {
        val result = try {
            client.get(BASE_URL + "pets/$petId") {
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
                val body = result.body<Pet>()
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

    override suspend fun getIfOwnPet(
        petId: String
    ): APIResult<Boolean, Error> {
        val result = try {
            client.get(BASE_URL + "pets/$petId/own") {
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

    override suspend fun getPetMedicalInfo(
        petId: String,
        type: PetInfoType?
    ): APIResult<List<PetMedicalInfo>, Error> {
        val result = try {
            client.get(BASE_URL + "pets/$petId/medicalInfo") {
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
                val body = result.body<List<PetMedicalInfo>>()
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

    override suspend fun postPet(
        request: PetRequest
    ): APIResult<Pet, Error> {
        val result = try {
            client.post(BASE_URL + "pets") {
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
                val body = result.body<Pet>()
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

    override suspend fun postPetImage(petId: String, imageFile: PetWalkerFileInfo): APIResult<String, Error> {
        val result = try {
            client.submitFormWithBinaryData(
                BASE_URL + "pets/$petId/image",
                formData {
                    imageFile.readBytes?.let { func ->
                        append("image", func(), Headers.build {
                            this.append(
                                HttpHeaders.ContentDisposition,
                                "filename=${
                                    imageFile.displayName.filter { ch ->
                                        Regex("[A-Za-z.0-9]").matches(
                                            ch.toString()
                                        )
                                    }
                                }"
                            )
                            this.append(
                                HttpHeaders.ContentType,
                                imageFile.mediaType
                            )
                        })
                    }
                })
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

    override suspend fun postPetMedicalInfo(
        petId: String,
        type: PetInfoType,
        additionalInfo: String?,
        document: PetWalkerFileInfo?
    ): APIResult<PetMedicalInfo, Error> {
        val result = try {
            client.submitFormWithBinaryData(
                BASE_URL + "pets/$petId/medicalInfo",
                formData {
                    this.append("type", type.ordinal)
                    additionalInfo?.let {
                        append("description", it)
                    }
                    document?.readBytes?.let { readBytes ->
                        this.append("document", readBytes(), Headers.build {
                            this.append(
                                HttpHeaders.ContentDisposition,
                                "filename=${
                                    document.displayName.filter { ch ->
                                        Regex("[A-Za-z.0-9]").matches(
                                            ch.toString()
                                        )
                                    }
                                }"
                            )
                            this.append(
                                HttpHeaders.ContentType,
                                document.mediaType
                            )
                        })
                    }
                }
            )
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                val body = result.body<PetMedicalInfo>()
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

    override suspend fun postAssignmentPet(
        assignmentId: String,
        petId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.post(BASE_URL + "assignments/$assignmentId/pets/$petId") {
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

    override suspend fun updatePet(
        petId: String,
        request: PetRequest
    ): APIResult<Unit, Error> {
        val result = try {
            client.put(BASE_URL + "pets/$petId") {
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

    override suspend fun updatePetMedicalInfo(
        petId: String,
        medicalInfoId: String,
        type: PetInfoType,
        additionalInfo: String?,
        document: PetWalkerFileInfo?
    ): APIResult<PetMedicalInfo, Error> {
        val result = try {
            client.submitFormWithBinaryData(
                BASE_URL + "pets/$petId/medicalInfo/$medicalInfoId",
                formData {
                    append("type", type.ordinal)
                    additionalInfo?.let {
                        append("description", it)
                    }
                    document?.readBytes?.let { readBytes ->
                        this.append("document", readBytes(), Headers.build {
                            this.append(
                                HttpHeaders.ContentDisposition,
                                "filename=${
                                    document.displayName.filter { ch ->
                                        Regex("[A-Za-z.0-9]").matches(
                                            ch.toString()
                                        )
                                    }
                                }"
                            )
                            this.append(
                                HttpHeaders.ContentType,
                                document.mediaType
                            )
                        })
                    }
                })
        } catch (e: UnresolvedAddressException) {
            return APIResult.Error(NetworkError.NO_INTERNET)
        } catch (e: SocketTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: ConnectTimeoutException) {
            return APIResult.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (result.status.value) {
            in 200..299 -> {
                val body = result.body<PetMedicalInfo>()
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

    override suspend fun deleteAssignmentPet(
        assignmentId: String,
        petId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "assignments/$assignmentId/pets/$petId") {
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

    override suspend fun deletePet(
        petId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "pets/$petId") {
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

    override suspend fun deletePetMedicalInfo(
        petId: String,
        medicalInfoId: String
    ): APIResult<Unit, Error> {
        val result = try {
            client.delete(BASE_URL + "pets/$petId/medicalinfo/$medicalInfoId") {
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