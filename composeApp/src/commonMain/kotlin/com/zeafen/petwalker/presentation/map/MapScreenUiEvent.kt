package com.zeafen.petwalker.presentation.map

import com.zeafen.petwalker.domain.models.api.users.APILocation

sealed interface MapScreenUiEvent {
    data object ReloadData : MapScreenUiEvent
    data class SetPresentationType(val type: MapPresentationType) : MapScreenUiEvent

    data class PickLocation(val location: APILocation): MapScreenUiEvent
}