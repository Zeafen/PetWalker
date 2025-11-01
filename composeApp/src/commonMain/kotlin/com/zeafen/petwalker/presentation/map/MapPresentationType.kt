package com.zeafen.petwalker.presentation.map

import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
sealed interface MapPresentationType {

    @Serializable
    data class Walkers(
        val page: Int,
        val pageSize: Int,
        val maxDistance: Float? = null,
        val services: List<ServiceType>? = null,
        val maxComplaints: Int? = null,
        val status: AccountStatus? = null,
        val ordering: UsersOrdering? = null,
        val ascending: Boolean? = null,
    ) : MapPresentationType

    @Serializable
    data class Assignments(
        val page: Int,
        val pageSize: Int,
        val maxDistance: Float? = null,
        val from: LocalDateTime? = null,
        val until: LocalDateTime? = null,
        val services: List<ServiceType>? = null,
        val ordering: AssignmentsOrdering? = null,
        val ascending: Boolean? = null
    ) : MapPresentationType

    @Serializable
    data class Assignment(
        val assignmentId: String
    ) : MapPresentationType

    @Serializable
    data class Walker(
        val walkerId: String
    ) : MapPresentationType

    @Serializable
    data object PickLocation: MapPresentationType
}