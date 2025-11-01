package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.LocationInfo
import com.zeafen.petwalker.domain.models.api.users.Profile
import com.zeafen.petwalker.domain.models.api.users.ProfileRequest
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

interface ProfileRepository {
    /***
     * Get profile data from the source
     * @return current user`s profile data if request succeeded, otherwise - ErrorInfo
     */
    suspend fun getProfile(
    ): APIResult<Profile, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Send request to obtain the confirmation code through email
     */
    suspend fun getEmailCode(
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Send request to change assigned email and obtain the confirmation code
     * @param email New email address
     */
    suspend fun setEmailGetCode(
        email: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Send request to confirm assigned email using sent confirmation code
     * @param code Obtained through email confirmation code
     */
    suspend fun confirmEmail(
        code: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post user`s current location to the source
     * @return Saved user location if request succeeded, otherwise - ErrorInfo
     * @param location User`s current location (coordinates)
     */
    suspend fun postLocation(
        location: APILocation
    ): APIResult<LocationInfo, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post user`s available services
     * @param services List of the user services` set
     */
    suspend fun postServices(
        services: List<UserService>? = null
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Post an image to the profile
     * @return Url to the saved image
     * @param imageFile New profile`s image file
     */
    suspend fun postImage(
        imageFile: PetWalkerFileInfo
    ): APIResult<String, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Update profile data of the current user
     * @param request New profile`s data
     */
    suspend fun updateProfile(
        request: ProfileRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes user from the source
     */
    suspend fun deleteProfile(
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}