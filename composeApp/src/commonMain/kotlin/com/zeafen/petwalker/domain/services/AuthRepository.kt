package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.auth.AuthRequest
import com.zeafen.petwalker.domain.models.api.auth.RegisterRequest
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.util.Error

interface AuthRepository {
    /**
     * Registers user with specified data in the source
     * @param request - registration request
     */
    suspend fun signUp(
        request: RegisterRequest
    ): APIResult<Unit, Error>

    /**
     * Authorises user in the source with login and password
     * @return - Token pair for the current user (refresh and access) if request succeeded, otherwise - Error info
     * @param request - authorization request
     */
    suspend fun signIn(
        request: AuthRequest
    ): APIResult<TokenResponse, Error>

    /**
     * Checks if specified token is valid for accessing the source
     */
    suspend fun authorize(
    ): APIResult<Unit, Error>

    /**
     * Gets new access token for specified refresh token of the user
     * @param refreshToken - refresh token of the user
     */
    suspend fun refreshAccessToken(
        refreshToken: String
    ): APIResult<String, Error>

    /**
     * Sends confirmation code to the specified email
     * @param email - email of the user
     */
    suspend fun getEmailCode(
        email: String
    ): APIResult<Unit, Error>

    /**
     * Checks if confirmation code code is valid
     * @return - Token pair for the current user (refresh and access) if request succeeded, otherwise - Error info
     * @param email Assigned email address
     * @param code Received confirmation code
     */
    suspend fun checkConfirmationCode(
        email: String, code: String
    ): APIResult<Unit, Error>

    /**
     * Updates account password
     * @return - Token pair for the current user (refresh and access) if request succeeded, otherwise - Error info
     * @param email Email address assigned to user account
     * @param code Received confirmation code
     * @param password New account password
     */
    suspend fun updatePassword(
        email: String,
        code: String,
        password: String
    ): APIResult<Unit, Error>
}