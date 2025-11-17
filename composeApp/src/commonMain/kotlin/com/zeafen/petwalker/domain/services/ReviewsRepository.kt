package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.filtering.ReviewOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.Complaint
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintRequest
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintsStats
import com.zeafen.petwalker.domain.models.api.reviews.Review
import com.zeafen.petwalker.domain.models.api.reviews.ReviewRequest
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats

interface ReviewsRepository {
    /***
     * Get a certain review for the specified identifier
     * @return Review entity if request succeeded, otherwise - Error info
     * @param id Review or reviewed assignment identifier identifier
     */
    suspend fun getReviewById(
        id: String,
    ): APIResult<Review, com.zeafen.petwalker.domain.models.api.util.Error>


    /***
     * Get reviews of specified user
     * @return Filtered list of the reviews of the user if request succeeded, otherwise - Error info
     * @param walkerId Reviewed user`s identifier
     * @param positive Filtering option for reviews` rating
     * @param period Filtering option for review`s posting date
     * @param ordering Ordering options for reviews
     * @param ascending placing order option
     */
    suspend fun getUserReviews(
        walkerId: String,
        page: Int? = null,
        perPage: Int? = null,
        positive: Boolean? = null,
        period: DatePeriods? = null,
        ordering: ReviewOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Review>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get reviews statistics for specified user
     * @return Statistics of the reviews of the user if request succeeded, otherwise - Error info
     * @param walkerId Reviewed user`s identifier. If null current authorized user statistics will be returned
     */
    suspend fun getUserReviewsStats(
        walkerId: String?
    ): APIResult<ReviewsStats, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get complaints statistics for specified user
     * @return Statistics of the reviews of the user if request succeeded, otherwise - Error info
     * @param walkerId Reviewed user`s identifier. If null current authorized user statistics will be returned
     */
    suspend fun getUserComplaintsStats(
        walkerId: String?
    ): APIResult<ComplaintsStats, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get a certain complaint for a specified identifier
     * @return Complaint entity if request succeeds, otherwise Error info
     * @param complaintId Complaint identifier
     */
    suspend fun getComplaintById(
        complaintId: String
    ): APIResult<Complaint, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Get complaints about specified user
     * @return Filtered list of the complaints about the user if request succeeded, otherwise - Error info
     * @param walkerId Reviewed user`s identifier
     * @param topic Filtering option for complaints` topic
     * @param status Filtering option for complaints` status
     * @param period Filtering option for complaints` posting date
     * @param dateDescending Ordering option for complaints list
     */
    suspend fun getUserComplaints(
        walkerId: String,
        page: Int? = null,
        perPage: Int? = null,
        topic: ComplaintTopic? = null,
        status: ComplaintStatus? = null,
        period: DatePeriods? = null,
        dateDescending: Boolean? = null
    ): APIResult<PagedResult<Complaint>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post review to user
     * @return Saved review entity if request succeeded, otherwise - Error info
     * @param assignmentId Identifier of the reviewed assignment
     * @param request Review data
     */
    suspend fun postReview(
        assignmentId: String,
        request: ReviewRequest
    ): APIResult<Review, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post complaint about user
     * @return Saved complaint entity if request succeeded, otherwise - Error info
     * @param walkerId Identifier of the Walker to complaint about
     * @param request Complaint data
     */
    suspend fun postComplaint(
        walkerId: String,
        request: ComplaintRequest
    ): APIResult<Complaint, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Update review data
     * @param reviewId Identifier of the review to update
     * @param request Review data
     */
    suspend fun updateReview(
        reviewId: String,
        request: ReviewRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Update complaint about user
     * @param complaintId Identifier of the complaint to update
     * @param request Complaint data
     */
    suspend fun updateComplaint(
        complaintId: String,
        request: ComplaintRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Delete review of the user
     * @param reviewId Identifier of the review to delete
     */
    suspend fun deleteReview(
        reviewId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Delete complaint about user
     * @param complaintId Identifier of the complaint to delete
     */
    suspend fun deleteComplaint(
        complaintId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}