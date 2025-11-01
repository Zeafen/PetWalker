package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.assignments.Recruitment
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentRequest
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import com.zeafen.petwalker.domain.models.api.other.PagedResult

interface RecruitmentsRepository {
    /***
     * Get own recruitments as assignment owner
     * @return filtered list of recruitments if request succeeded, otherwise - Error info
     * @param page Current page of the recruitments` list
     * @param perPage Current page size of the recruitments` list
     * @param state Filtering option for the recruitment state
     * @param outcoming Filtering option for the recruitment direction
     * @param timeFrom Filtering option for the beginning of the period of time in which recruitment was sent
     * @param timeTo Filtering option for the end of the period of time in which recruitment was sent
     */
    suspend fun getRecruitmentsAsOwner(
        page: Int? = null,
        perPage: Int? = null,
        state: RecruitmentState? = null,
        outcoming: Boolean? = null,
        timeFrom: String? = null,
        timeTo: String? = null
    ): APIResult<PagedResult<Recruitment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get own recruitments as walker
     * @return filtered list of recruitments if request succeeded, otherwise - Error info
     * @param page Current page of the recruitments` list
     * @param perPage Current page size of the recruitments` list
     * @param state Filtering option for the recruitment state
     * @param outcoming Filtering option for the recruitment direction
     * @param timeFrom Filtering option for the beginning of the period of time in which recruitment was sent
     * @param timeTo Filtering option for the end of the period of time in which recruitment was sent
     */
    suspend fun getRecruitmentsAsWalker(
        page: Int? = null,
        perPage: Int? = null,
        state: RecruitmentState? = null,
        outcoming: Boolean? = null,
        timeFrom: String? = null,
        timeTo: String? = null
    ): APIResult<PagedResult<Recruitment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get own recruitments
     * @return filtered list of recruitments if request succeeded, otherwise - Error info
     * @param page Current page of the recruitments` list
     * @param perPage Current page size of the recruitments` list
     * @param state Filtering option for the recruitment state
     * @param outcoming Filtering option for the recruitment direction
     * @param timeFrom Filtering option for the beginning of the period of time in which recruitment was sent
     * @param timeTo Filtering option for the end of the period of time in which recruitment was sent
     */
    suspend fun getRecruitments(
        page: Int? = null,
        perPage: Int? = null,
        state: RecruitmentState? = null,
        outcoming: Boolean? = null,
        timeFrom: String? = null,
        timeTo: String? = null
    ): APIResult<PagedResult<Recruitment>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post recruitment to assignment
     * @param request Recruitment data
     */
    suspend fun postRecruitment(
        request: RecruitmentRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Update recruitment and set its state to 'Accepted'
     * @param recruitmentId Identifier of the recruitment
     */
    suspend fun approveRecruitment(
        recruitmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Update recruitment and set its state to 'Denied'
     * @param recruitmentId Identifier of the recruitment
     */
    suspend fun declineRecruitment(
        recruitmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Delete recruitment from the source
     * @param recruitmentId Identifier of the recruitment to delete
     */
    suspend fun deleteRecruitment(
        recruitmentId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}