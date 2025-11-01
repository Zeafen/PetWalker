package com.zeafen.petwalker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class AuthDataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : AuthDataStoreRepository {


    override val authDataStoreFlow: Flow<UserInfo> = dataStore.data.map {
        UserInfo(
            it[FIRST_NAME_KEY] ?: "",
            it[LAST_NAME_KEY] ?: "",
            it[EMAIL_NAME_KEY],
            it[DEFAULT_LOCATION_KEY]?.let {
                Json.decodeFromString<APILocation>(it)
            },
            TokenResponse(
                it[ACCESS_TOKEN_NAME_KEY] ?: "",
                it[REFRESH_TOKEN_NAME_KEY] ?: "",
            ),
            it[IMAGE_URL_NAME_KEY],
        )
    }

    override suspend fun updateUserToken(newToken: TokenResponse?) {
        dataStore.edit {
            if (newToken == null) {
                it.minusAssign(ACCESS_TOKEN_NAME_KEY)
                it.minusAssign(REFRESH_TOKEN_NAME_KEY)
            } else {
                it[ACCESS_TOKEN_NAME_KEY] = newToken.accessToken
                it[REFRESH_TOKEN_NAME_KEY] = newToken.refreshToken
            }
        }
    }

    override suspend fun updateEmail(email: String?) {
        dataStore.edit {
            if (email == null)
                it.minusAssign(EMAIL_NAME_KEY)
            else it[EMAIL_NAME_KEY] = email
        }
    }

    override suspend fun updatePersonalData(firstName: String, lastName: String) {
        dataStore.edit {
            it[FIRST_NAME_KEY] = firstName
            it[LAST_NAME_KEY] = lastName
        }
    }

    override suspend fun updateImageUrl(newUrl: String?) {
        dataStore.edit {
            if (newUrl == null)
                it.minusAssign(IMAGE_URL_NAME_KEY)
            else it[IMAGE_URL_NAME_KEY] = newUrl
        }
    }

    override suspend fun updateDefaultLocation(location: APILocation?) {
        dataStore.edit {
            if (location == null)
                it.minusAssign(DEFAULT_LOCATION_KEY)
            else it[DEFAULT_LOCATION_KEY] = Json.encodeToString(location)
        }
    }

    override suspend fun clearData() {
        dataStore.edit {
            it.minusAssign(FIRST_NAME_KEY)
            it.minusAssign(LAST_NAME_KEY)
            it.minusAssign(ACCESS_TOKEN_NAME_KEY)
            it.minusAssign(REFRESH_TOKEN_NAME_KEY)
            it.minusAssign(EMAIL_NAME_KEY)
            it.minusAssign(IMAGE_URL_NAME_KEY)
        }
    }

    companion object {
        val FIRST_NAME_KEY = stringPreferencesKey("first_name_key")
        val LAST_NAME_KEY = stringPreferencesKey("first_name_key")
        val ACCESS_TOKEN_NAME_KEY = stringPreferencesKey("access_token_key")
        val REFRESH_TOKEN_NAME_KEY = stringPreferencesKey("refresh_token_key")
        val EMAIL_NAME_KEY = stringPreferencesKey("email_key")
        val IMAGE_URL_NAME_KEY = stringPreferencesKey("image_url_key")

        val DEFAULT_LOCATION_KEY = stringPreferencesKey("default_location")
    }
}