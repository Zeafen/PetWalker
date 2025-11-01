package com.zeafen.petwalker.presentation.map

import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error

data class MapScreenUIState(
    val loadedTiles: List<PetWalkerTile> = emptyList(),
    val currentUserLocation: APILocation? = null,
    val showPickedLocationPin: Boolean = false,

    val observedTileId: String? = null,
    val tilesLoadingResult: APIResult<Unit, Error>? = null,
    val presentationType: MapPresentationType = MapPresentationType.Walkers(1, 20),
    val showNearest: Boolean = true,
)
