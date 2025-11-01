package com.zeafen.petwalker.presentation.home

sealed interface HomePageUiEvent {
    data object LoadData: HomePageUiEvent
    data object ReloadBestWalker: HomePageUiEvent
    data class LoadBestWalkers(val page: Int = 1): HomePageUiEvent
}