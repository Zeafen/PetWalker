package com.zeafen.petwalker.presentation.pets.petDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PetsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetDetailsViewModel(
    private val petsRepository: PetsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val downloadManager: PetWalkerDownloadManager
) : ViewModel() {

    private val _state: MutableStateFlow<PetDetailsUiState> = MutableStateFlow(PetDetailsUiState())
    val state: StateFlow<PetDetailsUiState> = _state.asStateFlow()

    private var medicalInfoLoadingJob: Job? = null
    fun onEvent(event: PetDetailsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PetDetailsUiEvent.LoadMedicalDoc -> {
                    _state.update {
                        it.copy(
                            fileLoadingError =
                                downloadManager.queryDownload(event.ref, event.name)
                        )
                    }
                }

                PetDetailsUiEvent.LoadMedicalInfo -> {
                    if (medicalInfoLoadingJob?.isActive == true)
                        medicalInfoLoadingJob?.cancel()

                    medicalInfoLoadingJob = launch {
                        _state.update {
                            it.copy(petMedicalInfo = APIResult.Downloading())
                        }
                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(petMedicalInfo = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        if (state.value.selectedPetId == null) {
                            _state.update {
                                it.copy(petMedicalInfo = APIResult.Error(NetworkError.NOT_FOUND))
                            }
                            return@launch
                        }

                        val medicalInfo = petsRepository.getPetMedicalInfo(
                            state.value.selectedPetId!!,
                            state.value.selectedMedicalInfoType
                        )
                        _state.update {
                            it.copy(petMedicalInfo = medicalInfo)
                        }
                    }
                }

                is PetDetailsUiEvent.LoadPet -> {
                    _state.update {
                        it.copy(pet = APIResult.Downloading())
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(pet = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val pet = petsRepository.getPet(event.petId)
                    val own = petsRepository.getIfOwnPet(event.petId)
                    _state.update {
                        it.copy(
                            selectedPetId = event.petId,
                            pet = pet,
                            own = (if (own is APIResult.Succeed) own.data else null) ?: false
                        )
                    }

                    onEvent(PetDetailsUiEvent.LoadMedicalInfo)
                }

                is PetDetailsUiEvent.SetSearchPetInfoType -> {
                    _state.update {
                        it.copy(selectedMedicalInfoType = event.type)
                    }
                    onEvent(PetDetailsUiEvent.LoadMedicalInfo)
                }
            }
        }
    }
}