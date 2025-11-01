package com.zeafen.petwalker.domain.services

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

interface PostsRepository {
    /***
     * Get and filters posts from the source
     * @return filtered list of the posts if request succeeded, otherwise - Error info
     * @param page Current page of the list of the posts
     * @param perPage Page size of the list of the posts
     * @param topic Posts` filtering option for the topic
     * @param type Posts` filtering option for the type
     * @param period Posts` filtering option for the creation date
     * @param ordering Posts` ordering option
     * @param ascending Posts` placing order
     */
    suspend fun getPosts(
        page: Int? = null,
        perPage: Int? = null,
        topic: String? = null,
        type: PostType? = null,
        period: DatePeriods? = null,
        ordering: PostOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Post>, Error>

    /***
     * Get and filters posts of the specified user from the source
     * @return filtered list of the specified user`s posts if request succeeded, otherwise - Error info
     * @param walkerId Identifier of the walker
     * @param page Current page of the list of the posts
     * @param perPage Page size of the list of the posts
     * @param topic Posts` filtering option for the topic
     * @param type Posts` filtering option for the type
     * @param period Posts` filtering option for the creation date
     * @param ordering Posts` ordering option
     * @param ascending Posts` placing order
     */
    suspend fun getUserPosts(
        walkerId: String,
        page: Int? = null,
        perPage: Int? = null,
        topic: String? = null,
        type: PostType? = null,
        period: DatePeriods? = null,
        ordering: PostOrdering? = null,
        ascending: Boolean? = null
    ): APIResult<PagedResult<Post>, Error>

    /***
     * Get post entity with specified identifier
     * @return post entity if request succeeded, otherwise - Error info
     * @param postId Identifier of the post
     */
    suspend fun getPost(
        postId: String
    ): APIResult<Post, Error>

    /***
     * Get commentaries of the post from the source
     * @return list of the commentaries of the posts if request succeeded, otherwise - Error info
     * @param page Current page of the commentaries` list
     * @param perPage Page size of the commentaries` list
     */
    suspend fun getPostCommentaries(
        postId: String,
        page: Int? = null,
        perPage: Int? = null
    ): APIResult<PagedResult<Commentary>, Error>

    /***
     * Get specified post`s commentaries from the source
     * @return list of the specified post`s commentaries if request succeeded, otherwise - Error info
     * @param page Current page of the commentaries` list
     * @param perPage Page size of the commentaries` list
     */
    suspend fun getChildCommentaries(
        postId: String,
        commentaryId: String,
        page: Int? = null,
        perPage: Int? = null
    ): APIResult<PagedResult<Commentary>, Error>

    /***
     * Post post`s data into the source
     * @return posted Post entity if request succeeded, otherwise - Error info
     * @param type Post`s type
     * @param topic Post`s topic
     * @param body Post`s text content
     * @param attachmentFiles Post`s files content
     */
    suspend fun postPost(
        type: PostType,
        topic: String,
        body: String? = null,
        attachmentFiles: List<PetWalkerFileInfo>? = null
    ): APIResult<Post, Error>

    suspend fun postAttachments(
        postId: String,
        attachmentFiles: List<PetWalkerFileInfo>
    ): APIResult<List<Attachment>, Error>

    /***
     * Post commentary`s data to the specified post into the source
     * @return posted Commentary entity if request succeeded, otherwise - Error info
     * @param postId Identifier of the post
     * @param body Commentary`s text content
     * @param attachmentFiles Commentary`s files content
     */
    suspend fun postCommentary(
        postId: String,
        body: String? = null,
        attachmentFiles: List<PetWalkerFileInfo>? = null
    ): APIResult<Commentary, Error>

    /***
     * Post commentary`s data as a response to another commentary into the source
     * @return posted Commentary entity if request succeeded, otherwise - Error info
     * @param postId Identifier of the post
     * @param parentCommentId Identifier of the commentary to respond
     * @param body Commentary`s text content
     * @param attachmentFiles Commentary`s files content
     */
    suspend fun postCommentaryResponse(
        postId: String,
        parentCommentId: String,
        body: String? = null,
        attachmentFiles: List<PetWalkerFileInfo>? = null
    ): APIResult<Commentary, Error>

    /***
     * Update post data in the source
     * @param postId Identifier of the post to update
     * @param body Commentary`s text content
     * @param topic new post`s topic
     * @param body new post`s body
     */
    suspend fun updatePost(
        postId: String,
        topic: String? = null,
        body: String? = null,
        type: PostType? = null,
    ): APIResult<Unit, Error>

    /***
     * Update commentary data in the source
     * @param postId Identifier of the post
     * @param commentaryId Identifier of the commentary to update
     * @param body Commentary`s text content
     */
    suspend fun updateCommentary(
        postId: String,
        commentaryId: String,
        body: String? = null
    ): APIResult<Unit, Error>

    /***
     * Delete post data from the source
     * @param postId Identifier of the post to delete
     */
    suspend fun deletePost(
        postId: String
    ): APIResult<Unit, Error>

    /***
     * Delete commentary of the post from the source
     * @param postId Identifier of the post
     * @param commentaryId Identifier of the commentary to delete
     */
    suspend fun deleteCommentary(
        postId: String,
        commentaryId: String
    ): APIResult<Unit, Error>

    suspend fun deleteAttachment(
        postId: String,
        attachmentId: String
    ): APIResult<Unit, Error>
}