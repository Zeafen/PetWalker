package com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.PetWalkerDownloadManager
import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentRequest
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ChannelsRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.PetsRepository
import com.zeafen.petwalker.domain.services.RecruitmentsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AssignmentDetailsViewModel(
    private val assignmentsRepository: AssignmentsRepository,
    private val recruitmentsRepository: RecruitmentsRepository,
    private val usersRepository: UsersRepository,
    private val channelsRepository: ChannelsRepository,
    private val petsRepository: PetsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val locationService: LocationService,
    private val downloadManager: PetWalkerDownloadManager
) : ViewModel() {

    private val _state: MutableStateFlow<AssignmentDetailsUiState> =
        MutableStateFlow(AssignmentDetailsUiState())
    val state: StateFlow<AssignmentDetailsUiState> = _state.asStateFlow()

    init {
        _state
            .distinctUntilChangedBy { it.selectedAssignmentId }
            .onEach { value ->
                val canRecruit = value.selectedAssignmentId?.let {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token != null) {
                        val result = assignmentsRepository.canRecruitToAssignment(
                            value.selectedAssignmentId
                        )
                        if (result is APIResult.Succeed) result.data
                        else false
                    } else false
                } ?: false
                _state.update {
                    it.copy(canRecruit = canRecruit)
                }
            }
            .launchIn(viewModelScope)

        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private val mutex = Mutex()
    private var petsLoadingJob: Job? = null
    private var channelLoadingJob: Job? = null
    private var walkerInfoLoadingJob: Job? = null
    private var assignmentInteractionJob: Job? = null

    fun onEvent(event: AssignmentDetailsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is AssignmentDetailsUiEvent.LoadAssignment -> {
                    _state.update {
                        it.copy(
                            assignment = APIResult.Downloading()
                        )
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(assignment = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }
                    val location = locationService.location.first()

                    val assignment = assignmentsRepository.getAssignmentById(
                        event.assignmentId
                    )
                    val owner = async {
                        if (assignment is APIResult.Error) APIResult.Error(assignment.info)
                        else usersRepository.getWalker(
                            (assignment as APIResult.Succeed).data!!.ownerId
                        )
                    }
                    val canRecruit = async {
                        if (assignment is APIResult.Error) null
                        else {
                            val res =
                                assignmentsRepository.canRecruitToAssignment((assignment as APIResult.Succeed).data!!.id)
                            if (res is APIResult.Succeed)
                                res.data
                            else null
                        }
                    }

                    val owns = async {
                        if (assignment is APIResult.Error) null
                        else {
                            val res =
                                assignmentsRepository.doesOwnAssignment((assignment as APIResult.Succeed).data!!.id)
                            if (res is APIResult.Succeed)
                                res.data
                            else null
                        }
                    }

                    val distance =
                        if (assignment is APIResult.Succeed)
                            location?.calculateDistance(assignment.data!!.location)
                        else null

                    _state.update {
                        it.copy(
                            selectedAssignmentId = event.assignmentId,
                            selectedTabIndex = 0,
                            assignment = assignment,
                            assignmentOwner = owner.await(),
                            distanceToAssignment = distance?.toFloat(),
                            canRecruit = canRecruit.await() ?: false,
                            owns = owns.await() ?: false
                        )
                    }
                }

                is AssignmentDetailsUiEvent.LoadAttachmentData -> {
                    _state.update {
                        it.copy(
                            filesLoadingError =
                                downloadManager.queryDownload(event.ref, event.name)
                        )
                    }
                }

                AssignmentDetailsUiEvent.LoadChannel -> {
                    if (channelLoadingJob?.isActive != true)
                        channelLoadingJob = launch {
                            _state.update {
                                it.copy(assignmentChannel = APIResult.Downloading())
                            }

                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(assignmentChannel = APIResult.Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }

                            if (state.value.selectedAssignmentId == null) {
                                _state.update {
                                    it.copy(
                                        assignmentChannel = APIResult.Error(NetworkError.NOT_FOUND)
                                    )
                                }
                                return@launch
                            }

                            val channel = channelsRepository.getAssignmentChannel(
                                state.value.selectedAssignmentId!!
                            )
                            _state.update {
                                it.copy(
                                    assignmentChannel = channel
                                )
                            }
                        }
                }

                AssignmentDetailsUiEvent.LoadOwner -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(assignmentOwner = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    if (state.value.assignment !is APIResult.Succeed) {
                        _state.update {
                            it.copy(
                                assignmentOwner = APIResult.Error(NetworkError.NOT_FOUND)
                            )
                        }
                        return@launch
                    }
                    val walker = usersRepository.getWalker(
                        (state.value.assignment as APIResult.Succeed).data!!.ownerId
                    )
                    _state.update {
                        it.copy(
                            assignmentOwner = walker
                        )
                    }
                }

                is AssignmentDetailsUiEvent.LoadPets -> {
                    if (petsLoadingJob?.isActive == true)
                        petsLoadingJob?.cancel()
                    petsLoadingJob = launch {
                        _state.update {
                            it.copy(
                                assignmentPets = APIResult.Downloading()
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(assignmentPets = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        if (state.value.selectedAssignmentId == null) {
                            _state.update {
                                it.copy(assignmentPets = APIResult.Error(NetworkError.NOT_FOUND))
                            }
                            return@launch
                        }

                        val pets = petsRepository.getAssignmentPets(
                            state.value.selectedAssignmentId!!,
                            event.page,
                            15,
                            state.value.searchPetsName,
                            state.value.searchPetsSpecies
                        )
                        _state.update {
                            it.copy(
                                assignmentPets = pets,
                                lastSelectedPetPage = if (pets is APIResult.Succeed) pets.data!!.currentPage
                                else it.lastSelectedPetPage
                            )
                        }
                    }
                }

                AssignmentDetailsUiEvent.LoadWalker -> {
                    if (walkerInfoLoadingJob?.isActive != true)
                        walkerInfoLoadingJob = launch {
                            val token = authDataStore.authDataStoreFlow.first().token
                            if (token == null || token.accessToken.isBlank()) {
                                _state.update {
                                    it.copy(assignmentWalker = APIResult.Error(NetworkError.UNAUTHORIZED))
                                }
                                return@launch
                            }
                            if (state.value.assignment !is APIResult.Succeed) {
                                _state.update {
                                    it.copy(
                                        assignmentWalker = APIResult.Error(NetworkError.NOT_FOUND)
                                    )
                                }
                                return@launch
                            }

                            val walker =
                                (state.value.assignment as APIResult.Succeed).data!!.walkerId?.let {
                                    usersRepository.getWalker(
                                        it
                                    )
                                } ?: APIResult.Error(NetworkError.NOT_FOUND)

                            val distance = if (walker is APIResult.Succeed)
                                walker.data!!.location?.let {
                                    locationService.location.first()
                                        ?.calculateDistance(APILocation(it.latitude, it.longitude))
                                }
                            else null
                            _state.update {
                                it.copy(
                                    assignmentWalker = walker,
                                    distanceToWalker = distance?.toFloat()
                                )
                            }
                        }
                }

                is AssignmentDetailsUiEvent.SetSearchPetsName -> {
                    mutex.withLock {
                        _state.update {
                            it.copy(searchPetsName = event.name)
                        }
                    }
                    onEvent(AssignmentDetailsUiEvent.LoadPets())
                }

                is AssignmentDetailsUiEvent.SetSearchPetsSpecies -> {
                    mutex.withLock {
                        _state.update {
                            it.copy(searchPetsSpecies = event.species)
                        }
                    }
                    onEvent(AssignmentDetailsUiEvent.LoadPets())
                }

                is AssignmentDetailsUiEvent.SetSelectedTab -> {
                    _state.update {
                        it.copy(selectedTabIndex = event.index)
                    }
                    when {
                        AssignmentDetailsTabs.entries[event.index] == AssignmentDetailsTabs.WalkerInfo
                                && state.value.assignmentWalker !is APIResult.Succeed -> {
                            onEvent(AssignmentDetailsUiEvent.LoadWalker)
                        }

                        AssignmentDetailsTabs.entries[event.index] == AssignmentDetailsTabs.Pets
                                && state.value.assignmentPets !is APIResult.Succeed
                                && petsLoadingJob?.isActive != true -> {
                            onEvent(AssignmentDetailsUiEvent.LoadPets())
                        }

                        AssignmentDetailsTabs.entries[event.index] == AssignmentDetailsTabs.Channel
                                && state.value.assignmentChannel !is APIResult.Succeed -> {
                            onEvent(AssignmentDetailsUiEvent.LoadChannel)
                        }
                    }
                }

                AssignmentDetailsUiEvent.RecruitToAssignment -> {
                    mutex.withLock {
                        if (state.value.recruitingResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(recruitingResult = APIResult.Downloading())
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(recruitingResult = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val selectedAssignmentId = state.value.selectedAssignmentId
                    if (selectedAssignmentId == null) {
                        _state.update {
                            it.copy(recruitingResult = APIResult.Error(NetworkError.NOT_FOUND))
                        }
                        return@launch
                    }

                    val result = recruitmentsRepository.postRecruitment(
                        RecruitmentRequest(selectedAssignmentId, null)
                    )
                    _state.update {
                        it.copy(recruitingResult = result)
                    }
                }

                is AssignmentDetailsUiEvent.SetStatus -> {
                    if (assignmentInteractionJob?.isActive == true)
                        return@launch
                    assignmentInteractionJob = launch {
                        _state.update {
                            it.copy(assignment = APIResult.Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(assignment = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val selectedId = state.value.selectedAssignmentId
                        if (selectedId == null) {
                            _state.update {
                                it.copy(assignment = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val result = when (event.status) {
                            AssignmentState.Searching -> assignmentsRepository
                                .searchAssignment(
                                    selectedId
                                )

                            AssignmentState.In_Progress -> assignmentsRepository
                                .startAssignment(
                                    selectedId
                                )

                            AssignmentState.Closed ->
                                assignmentsRepository.closeAssignment(
                                    selectedId
                                )

                            AssignmentState.Completed ->
                                assignmentsRepository.completeAssignment(
                                    selectedId
                                )

                            else -> APIResult.Error(NetworkError.UNKNOWN)
                        }
                        if (result is APIResult.Error) {
                            _state.update {
                                it.copy(assignment = APIResult.Error(result.info))
                            }
                            return@launch
                        }

                        onEvent(AssignmentDetailsUiEvent.LoadAssignment(selectedId))
                    }
                }
            }
        }
    }
}