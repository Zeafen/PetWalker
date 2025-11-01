package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentRequest
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error

interface AssignmentsRepository {
    /***
     * Gets all assignments from the source
     * @return Filtered assignments list if the request succeeded, otherwise - Error info
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getAssignments(
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets assignments of the current user from the source, where user is owner of the assignments
     * @return Filtered own assignments list where user is owner of each assignment if the request succeeded, otherwise - Error info
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getOwnAssignmentsAsOwner(
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets open assignments of the current user from the source, where user is owner of the assignments
     * @return Filtered own assignments list where user is owner of each assignment if the request succeeded, otherwise - Error info
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getOwnOpenAssignmentsAsOwner(
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets assignments of the current user from the source, where user is walker for the assignments
     * @return Filtered own assignments list where user is walker for each assignment if the request succeeded, otherwise - Error info
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getOwnAssignmentsAsWalker(
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, Error>

    /***
     * Gets assignments of the specified user from the source, where user is owner of the assignments
     * @return Filtered own assignments list where user is owner of each assignment if the request succeeded, otherwise - com.zeafen.petwalker.domain.models.api.util.Error info
     * @param walkerId - Identifier of the assignments walker
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getUserAssignmentsAsOwner(
        walkerId: String,
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets assignments of the specified user from the source, where user is walker for the assignments
     * @return Filtered own assignments list where user is walker of each assignment if the request succeeded, otherwise - Error info
     * @param walkerId - Identifier of the assignments walker
     * @param page - current page of the assignments list
     * @param perPage - page size of the assignments list
     * @param title - search string for assignments title
     * @param location - location of the user for showing nearest assignments
     * @param maxDistance - max distance for assignments to show
     * @param services - list of the services in which assignment should be
     * @param timeFrom - beginning of the acceptable time period of the assignments creation time
     * @param timeTo - end of the acceptable time period of the assignments creation time
     * @param ordering - Ordering options of the assignments list
     * @param ascending - Placing Order of the assignments list
     */
    suspend fun getUserAssignmentsAsWalker(
        walkerId: String,
        page: Int? = null,
        perPage: Int? = null,
        title: String? = null,
        location: APILocation? = null,
        maxDistance: Float? = null,
        timeFrom: String? = null,
        timeTo: String? = null,
        services: List<ServiceType>? = null,
        ordering: AssignmentsOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Assignment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets assignment entity for the specified Identifier
     * @returnAssignment entity if the request succeeded, otherwise - Error info
     * @param assignmentId - Identifier of searched assignment
     */
    suspend fun getAssignmentById(
        assignmentId: String
    ): APIResult<Assignment, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets if user can send recruitment to assignment
     * @returnAssignment entity if the request succeeded, otherwise - Error info
     * @param assignmentId - Identifier of assignment
     */
    suspend fun canRecruitToAssignment(
        assignmentId: String
    ): APIResult<Boolean, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Posts assignment to the source
     * @param request - Assignment data to post
     */
    suspend fun postAssignment(
        request: AssignmentRequest
    ): APIResult<Assignment, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates existing assignment in the source
     * @param assignmentId - Identifier of the assignment to update
     * @param request - Assignment data to update
     */
    suspend fun updateAssignment(
        assignmentId: String,
        request: AssignmentRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates assignment in the source by changing its state to 'In Process'
     * @param assignmentId - Identifier of the assignment to start
     */
    suspend fun startAssignment(
        assignmentId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates assignment in the source by changing its state to 'Completed'
     * @param assignmentId - Identifier of the assignment to complete
     */
    suspend fun completeAssignment(
        assignmentId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates assignment in the source by changing its state to 'Closed'
     * @param assignmentId - Identifier of the assignment to close
     */
    suspend fun closeAssignment(
        assignmentId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates assignment in the source by changing its state to 'Closed'
     * @param assignmentId - Identifier of the assignment to close
     */
    suspend fun  searchAssignment(
        assignmentId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes specified assignment from the source
     * @param assignmentId - Identifier of the assignment to delete
     */
    suspend fun deleteAssignment(
        assignmentId: String,
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}