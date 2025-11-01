package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.users.APILocation
import kotlinx.coroutines.flow.Flow

interface LocationService {
    val location: Flow<APILocation?>
    fun startObserving()
    fun cancelObserving()
}