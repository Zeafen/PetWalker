@file:OptIn(ExperimentalUuidApi::class)

package com.zeafen.petwalker.presentation.pets.petConfigure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.pets.PetRequest
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PetsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.date_less_than_error_txt
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.greater_than_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.nan_error_txt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class)
class PetConfigureViewModel(
    private val petsRepository: PetsRepository,
    private val authDataStore: AuthDataStoreRepository

) : ViewModel() {

    private val _state: MutableStateFlow<PetConfigureUiState> =
        MutableStateFlow(PetConfigureUiState())
    val state: StateFlow<PetConfigureUiState> = _state.asStateFlow()

    private val _exitPage: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exitPage: StateFlow<Boolean> = _exitPage.asStateFlow()

    init {
        _state
            .distinctUntilChangedBy { it.petName }
            .onEach { value ->
                _state.update {
                    it.copy(
                        nameValidation = when {
                            value.petName.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.petName.length > 200 -> ValidationInfo(
                                false,
                                Res.string.incorrect_length_max_error,
                                listOf(200)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
        _state
            .distinctUntilChangedBy { it.petSpecies }
            .onEach { value ->
                _state.update {
                    it.copy(
                        speciesValidation = when {
                            value.petSpecies.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.petSpecies.length > 200 -> ValidationInfo(
                                false,
                                Res.string.incorrect_length_max_error,
                                listOf(200)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
        _state
            .distinctUntilChangedBy { it.petBreed }
            .onEach { value ->
                _state.update {
                    it.copy(
                        breedValidation = when {
                            value.petBreed.isBlank() -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.petBreed.length > 200 -> ValidationInfo(
                                false,
                                Res.string.incorrect_length_max_error,
                                listOf(200)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
        _state
            .distinctUntilChangedBy { it.petWeight }
            .onEach { value ->
                _state.update {
                    it.copy(
                        weightValidation = when {
                            value.petWeight.toFloatOrNull() == null -> ValidationInfo(
                                false,
                                Res.string.nan_error_txt,
                                emptyList()
                            )

                            value.petWeight.toFloat() <= 0f -> ValidationInfo(
                                false,
                                Res.string.greater_than_error_txt,
                                listOf(0)
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
        _state
            .distinctUntilChangedBy { it.petDateBirth }
            .onEach { value ->
                _state.update {
                    it.copy(
                        date_birthValidation = when {

                            value.petDateBirth == null -> ValidationInfo(
                                false,
                                Res.string.empty_fields_error_txt,
                                emptyList()
                            )

                            value.petDateBirth > Clock.System.now()
                                .toLocalDateTime(TimeZone.currentSystemDefault()) -> ValidationInfo(
                                false,
                                Res.string.date_less_than_error_txt,
                                listOf(
                                    Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault())
                                        .format(LocalDateTime.Format {
                                            day()
                                            char('/')
                                            monthNumber()
                                            char('/')
                                            year()
                                            char(' ')
                                            hour()
                                            char(':')
                                            minute()
                                        })
                                )
                            )

                            else -> ValidationInfo(true, null, emptyList())
                        }
                    )
                }
            }
            .launchIn(viewModelScope)

        _state
            .distinctUntilChanged { old, new ->
                old.nameValidation == new.nameValidation
                        && old.speciesValidation == new.speciesValidation
                        && old.breedValidation == new.breedValidation
                        && old.weightValidation == new.weightValidation
                        && old.date_birthValidation == new.date_birthValidation
            }
            .onEach { value ->
                _state.update {
                    it.copy(
                        canPublish = value.nameValidation.isValid
                                && value.speciesValidation.isValid
                                && value.breedValidation.isValid
                                && value.date_birthValidation.isValid
                                && value.weightValidation.isValid
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private val inputMutex = Mutex()
    fun onEvent(event: PetConfigureUiEvent) {
        viewModelScope.launch {
            when (event) {
                is PetConfigureUiEvent.SetSelectedPetId -> {
                    _state.update {
                        it.copy(
                            petLoadingResult = Downloading(),
                            selectedPetId = event.id
                        )
                    }

                    if (event.id != null) {

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(petLoadingResult = Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val medicalInfo =
                            async {
                                val data = petsRepository.getPetMedicalInfo(
                                    event.id
                                )
                                if (data is Succeed)
                                    data.data
                                else null
                            }

                        val pet = petsRepository.getPet(event.id)
                        if (pet is Error) {
                            _state.update {
                                it.copy(petLoadingResult = Error(pet.info))
                            }
                            return@launch
                        }

                        inputMutex.withLock {
                            _state.update {
                                it.copy(
                                    petLoadingResult = Succeed(),
                                    petName = (pet as Succeed).data!!.name,
                                    petSpecies = pet.data!!.species,
                                    petBreed = pet.data.breed,
                                    petWeight = pet.data.weight.toString(),
                                    petDateBirth = pet.data.date_birth,
                                    petDesc = pet.data.description ?: "",
                                    petMedicalInfos = medicalInfo.await() ?: emptyList()
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                petLoadingResult = Succeed(),
                                petName = "",
                                petSpecies = "",
                                petBreed = "",
                                petWeight = "",
                                petDateBirth = null,
                                petDesc = "",
                                petMedicalInfos = emptyList()
                            )
                        }
                    }

                }

                PetConfigureUiEvent.PublishData -> {
                    if (state.value.petLoadingResult is Downloading)
                        return@launch
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petLoadingResult = Downloading())
                        }
                    }

                    if (!state.value.canPublish) {
                        _state.update {
                            it.copy(petLoadingResult = Error(NetworkError.CONFLICT))
                        }
                        return@launch
                    }
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(petLoadingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val pet = PetRequest(
                        state.value.petName,
                        state.value.petSpecies,
                        state.value.petBreed,
                        state.value.petDesc,
                        state.value.petDateBirth!!,
                        state.value.petWeight.toFloat(),
                    )
                    when {
                        state.value.selectedPetId != null -> {
                            val result = petsRepository.updatePet(
                                state.value.selectedPetId!!, pet
                            )
                            val imageResult = state.value.petImageUri?.let {
                                petsRepository.postPetImage(
                                    state.value.selectedPetId!!,
                                    it
                                )
                            }

                            _state.update {
                                it.copy(
                                    petLoadingResult = result,
                                    petImageUri = if (imageResult is APIResult.Succeed)
                                        imageResult.data?.let {
                                            PetWalkerFileInfo(
                                                it,
                                                "",
                                                "image",
                                                null
                                            )
                                        }
                                    else null
                                )
                            }
                        }

                        state.value.selectedPetId == null -> {
                            val result = petsRepository.postPet(pet)
                            if (result is Error) {
                                _state.update {
                                    it.copy(petLoadingResult = Error(result.info))
                                }
                                return@launch
                            }

                            val medicalInfoResult = state.value.petMedicalInfos.map {
                                async {
                                    val result = petsRepository.postPetMedicalInfo(
                                        (result as Succeed).data!!.id,
                                        it.type,
                                        it.description,
                                        state.value.medicalInfoDocs[it.id]
                                    )
                                    if (result is Succeed)
                                        result.data
                                    else null
                                }
                            }
                            _state.update {
                                it.copy(
                                    petLoadingResult = Succeed(),
                                    selectedPetId = (result as Succeed).data!!.id,
                                    petMedicalInfos = medicalInfoResult.awaitAll()
                                        .mapNotNull { it }
                                )
                            }
                        }
                    }
                }

                is PetConfigureUiEvent.SetPetSpecies -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petSpecies = event.species)
                        }
                    }
                }

                is PetConfigureUiEvent.SetPetDateBirth -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petDateBirth = event.dateBirth)
                        }
                    }
                }

                is PetConfigureUiEvent.SetPetDescription -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petDesc = event.description)
                        }
                    }

                }

                is PetConfigureUiEvent.SetPetImage -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petImageUri = event.fileInfo)
                        }
                    }
                }

                is PetConfigureUiEvent.SetPetName -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petName = event.name)
                        }
                    }
                }

                is PetConfigureUiEvent.SetPetWeight -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(petWeight = event.weight)
                        }
                    }
                }

                is PetConfigureUiEvent.AddPetMedialInfo -> {
                    var medicalInfo = PetMedicalInfo(
                        Uuid.random().toString(),
                        event.description,
                        event.type,
                        event.document?.toString(),
                        event.name
                    )

                    if (state.value.selectedPetId != null) {
                        _state.update {
                            it.copy(medicalInfoEditingResult = Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(medicalInfoEditingResult = Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val result = petsRepository.postPetMedicalInfo(
                            state.value.selectedPetId!!,
                            event.type,
                            event.description,
                            event.document
                        )
                        if (result is Error) {
                            _state.update {
                                it.copy(medicalInfoEditingResult = Error(result.info))
                            }
                            return@launch
                        }
                        medicalInfo = (result as Succeed).data!!

                    }
                    _state.update {
                        it.copy(
                            medicalInfoEditingResult = Succeed(),
                            petMedicalInfos = (it.petMedicalInfos + medicalInfo).distinctBy { it.reference }
                        )
                    }
                }

                is PetConfigureUiEvent.RemovePetMedialInfo -> {
                    state.value.petMedicalInfos.firstOrNull { it.id == event.id }?.let { removed ->
                        if (state.value.selectedPetId != null) {
                            _state.update {
                                it.copy(medicalInfoEditingResult = Downloading())
                            }
                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(medicalInfoEditingResult = Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            val result = petsRepository.deletePetMedicalInfo(
                                state.value.selectedPetId!!,
                                event.id
                            )
                            _state.update {
                                it.copy(medicalInfoEditingResult = result)
                            }
                            if (result is Error)
                                return@launch
                        }

                        _state.update {
                            it.copy(petMedicalInfos = it.petMedicalInfos - removed)
                        }
                    }
                }

                is PetConfigureUiEvent.EditMedicalInfo -> {
                    state.value.petMedicalInfos.firstOrNull { it.id == event.id }?.let { edited ->
                        var newMedicalInfo = edited.copy(
                            description = event.description,
                            type = event.type,
                            reference = event.document?.toString()
                        )

                        if (state.value.selectedPetId != null) {
                            _state.update {
                                it.copy(medicalInfoEditingResult = Downloading())
                            }
                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(medicalInfoEditingResult = Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            val result = petsRepository.updatePetMedicalInfo(
                                state.value.selectedPetId!!,
                                event.id,
                                event.type,
                                event.description,
                                event.document
                            )
                            if (result is Error) {
                                _state.update {
                                    it.copy(medicalInfoEditingResult = Error(result.info))
                                }
                                return@launch
                            } else {
                                newMedicalInfo = (result as Succeed).data!!
                                _state.update {
                                    it.copy(medicalInfoEditingResult = Succeed())
                                }
                            }
                        }

                        _state.update {
                            it.copy(petMedicalInfos = (it.petMedicalInfos - edited + newMedicalInfo))
                        }
                    }
                }

                is PetConfigureUiEvent.SelectMedicalInfo -> {
                    _state.update {
                        it.copy(selectedMedicalInfo = it.petMedicalInfos.firstOrNull { it.id == event.id })
                    }
                }

                PetConfigureUiEvent.DeletePet -> {
                    inputMutex.withLock {
                        if (state.value.petLoadingResult is Downloading)
                            return@launch
                        _state.update {
                            it.copy(petLoadingResult = Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(petLoadingResult = Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val selectedId = state.value.selectedPetId
                    if (selectedId == null) {
                        _state.update {
                            it.copy(petLoadingResult = Error(NetworkError.NOT_FOUND))
                        }
                        return@launch
                    }

                    val result = petsRepository.deletePet(selectedId)
                    if (result is Error) {
                        _state.update {
                            it.copy(petLoadingResult = Error(result.info))
                        }
                        return@launch
                    }
                    _state.update {
                        it.copy(petLoadingResult = Succeed())
                    }
                    _exitPage.update { true }
                }

                is PetConfigureUiEvent.SetPetBreed -> {
                    inputMutex.withLock {
                        _state.update {
                            it.copy(
                                petBreed = event.breed
                            )
                        }
                    }
                }

                PetConfigureUiEvent.ClearResult -> {
                    if (state.value.petLoadingResult !is APIResult.Downloading)
                        inputMutex.withLock {
                            _state.update {
                                it.copy(petLoadingResult = null)
                            }
                        }
                }
            }
        }
    }
}