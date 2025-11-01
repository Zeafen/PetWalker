package com.zeafen.petwalker.presentation.pets.petsList

import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error

data class PetsPageUiState(
    val pets: APIResult<PagedResult<Pet>, Error> = APIResult.Downloading(),
    val lastSelectedPage: Int = 1,
    val searchPetsName: String = "",
    val searchPetsSpecies: String = "",
)
