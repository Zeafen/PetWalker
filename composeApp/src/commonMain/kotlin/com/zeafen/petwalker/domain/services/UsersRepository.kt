package com.zeafen.petwalker.domain.services

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

interface UsersRepository {
    /***
     * Get walkers list
     * @return Filtered walkers list if request succeeded, otherwise - Error info
     * @param page Current page of the walkers` list
     * @param perPage Page size of the walkers` list
     * @param locationInfo Filtering option for users` location
     * @param maxDistance Filtering option for users` max distance from assigned location filter
     * @param name Filtering option for the users` full name
     * @param services Filtering option for the users` services list
     * @param maxComplaintsCount Filtering option for the users` max complaints count
     * @param status Filtering option for the users` account status
     * @param online Filtering option for the users` online status
     * @param ordering Ordering options for the users` list
     * @param ascending Placing order of the users` list
     */
    suspend fun getWalkers(
        page: Int? = null,
        perPage: Int? = null,
        locationInfo: APILocation? = null,
        maxDistance: Float? = null,
        name: String? = null,
        services: List<ServiceType>? = null,
        maxComplaintsCount: Int? = null,
        status: AccountStatus? = null,
        online: Boolean? = null,
        ordering: UsersOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Walker>, com.zeafen.petwalker.domain.models.api.util.Error>


    /***
     * Gets assignments stats for specified user
     * @param period - period of time, when certain assignment have taken the place
     * @param userId - identifier of the user to get stats for
     * @return statistics of assignment for certain user if userId is specified, otherwise - statistics for current authorized user
     */
    suspend fun getUserAssignmentStats(
        userId: String? = null,
        period: DatePeriods? = null
    ): APIResult<AssignmentsStats, Error>

    /***
     * Get walker entity with specified identifier
     * @return Walker entity if request succeeded, otherwise - Error info
     * @param walkerId Identifier of the walker
     */
    suspend fun getWalker(
        walkerId: String
    ): APIResult<Walker, com.zeafen.petwalker.domain.models.api.util.Error>


    /**
     * Checks if login has already been taken
     * @return If login is taken if request succeeds, otherwise - Error info
     * @param login Login to check
     */
    suspend fun loginExists(
        login: String
    ): APIResult<Boolean, com.zeafen.petwalker.domain.models.api.util.Error>
}