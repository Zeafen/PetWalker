package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlinx.coroutines.flow.Flow

interface AuthDataStoreRepository {
    val authDataStoreFlow: Flow<UserInfo>
    suspend fun updateUserToken(newToken: TokenResponse?)
    suspend fun updateEmail(email: String?)
    suspend fun updatePersonalData(firstName: String, lastName: String)
    suspend fun updateImageUrl(newUrl: String?)

    suspend fun updateDefaultLocation(location: APILocation?)
    suspend fun clearData()
}