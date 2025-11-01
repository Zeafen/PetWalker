package com.zeafen.petwalker.data

import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class JvmLocationService(
    authDataStore: AuthDataStoreRepository
) : LocationService {
    override val location: Flow<APILocation?> =
        authDataStore.authDataStoreFlow.map { it.defaultLocationInfo }

    override fun startObserving() {}

    override fun cancelObserving() {}
}