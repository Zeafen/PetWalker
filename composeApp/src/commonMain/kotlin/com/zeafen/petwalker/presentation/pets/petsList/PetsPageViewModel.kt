package com.zeafen.petwalker.presentation.pets.petsList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PetsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PetsPageViewModel(
    private val petsRepository: PetsRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<PetsPageUiState> = MutableStateFlow(PetsPageUiState())
    val state: StateFlow<PetsPageUiState> = _state.asStateFlow()


    init {
        onEvent(PetsPageUiEvent.LoadOwnPets())
    }

    private var petsLoadingJob: Job? = null
    private val inputMutex = Mutex()
    fun onEvent(event: PetsPageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PetsPageUiEvent.LoadOwnPets -> {
                    if (petsLoadingJob?.isActive == true)
                        petsLoadingJob?.cancel()

                    petsLoadingJob = launch {
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(pets = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val pets = petsRepository.getOwnPets()
                        _state.update {
                            it.copy(
                                pets = pets,
                                lastSelectedPage = if (pets is APIResult.Succeed) pets.data!!.currentPage else it.lastSelectedPage
                            )
                        }
                    }
                }

                is PetsPageUiEvent.SetSearchName -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(searchPetsName = event.name)
                        }
                    }
                }

                is PetsPageUiEvent.SetSearchSpecies -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(searchPetsSpecies = event.species)
                        }
                    }
                }
            }
        }
    }
}