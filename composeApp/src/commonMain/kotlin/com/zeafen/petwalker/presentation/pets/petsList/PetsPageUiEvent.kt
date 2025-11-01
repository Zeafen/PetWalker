package com.zeafen.petwalker.presentation.pets.petsList

sealed interface PetsPageUiEvent {
    data class LoadOwnPets(val page: Int = 1) : PetsPageUiEvent
    data class SetSearchName(val name: String) : PetsPageUiEvent
    data class SetSearchSpecies(val species: String) : PetsPageUiEvent
}