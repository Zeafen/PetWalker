package com.zeafen.petwalker.presentation.walkers.walkersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.WalkerCardModel
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WalkersPageViewModel(
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val locationService: LocationService
) : ViewModel() {

    private val _state: MutableStateFlow<WalkersPageUiState> =
        MutableStateFlow(WalkersPageUiState())

    val state = _state.asStateFlow()

    init {
        onEvent(WalkersPageUiEvent.LoadWalkers())
        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private var walkersLoadingJob: Job? = null
    private val titleInputMutex = Mutex()
    fun onEvent(event: WalkersPageUiEvent) {
        viewModelScope.launch {
            when (event) {
                WalkersPageUiEvent.ClearFilters -> {
                    _state.update {
                        it.copy(
                            searchServices = null,
                            maxComplaintsCount = null,
                            status = null,
                            showOnline = null
                        )
                    }
                    onEvent(WalkersPageUiEvent.LoadWalkers())
                }

                is WalkersPageUiEvent.EnterSearchTitle -> {
                    titleInputMutex.withLock {
                        _state.update {
                            it.copy(searchName = event.searchName)
                        }
                    }
                    onEvent(WalkersPageUiEvent.LoadWalkers())
                }

                is WalkersPageUiEvent.LoadWalkers -> {
                    if (walkersLoadingJob?.isActive == true)
                        walkersLoadingJob?.cancel()

                    walkersLoadingJob = launch {
                        _state.update {
                            it.copy(
                                walkers = APIResult.Downloading()
                            )
                        }

                        val tokenPair = authDataStore.authDataStoreFlow.first().token
                        if (tokenPair == null) {
                            _state.update {
                                it.copy(walkers = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val location = state.value.ordering?.let {
                            if (it == UsersOrdering.Location)
                                locationService.location.first()
                            else null
                        }

                        val walkers = usersRepository.getWalkers(
                            event.page,
                            15,
                            location,
                            50f,
                            state.value.searchName,
                            state.value.searchServices,
                            state.value.maxComplaintsCount,
                            state.value.status,
                            state.value.showOnline,
                            state.value.ordering,
                            state.value.ordering?.let { state.value.ascending }
                        )
                        if (walkers is APIResult.Error) {
                            _state.update {
                                it.copy(
                                    walkers = APIResult.Error(
                                        walkers.info,
                                        walkers.additionalInfo
                                    ),
                                )
                            }
                            return@launch
                        }

                        val models = (walkers as APIResult.Succeed).data!!.result.map {
                            async {
                                getModelForWalker(it)
                            }
                        }
                        _state.update {
                            it.copy(
                                walkers = APIResult.Succeed(
                                    PagedResult(
                                        models.awaitAll(),
                                        currentPage = walkers.data!!.currentPage,
                                        totalPages = walkers.data.totalPages,
                                        pageSize = walkers.data.pageSize,
                                    )
                                ),
                                lastSelectedPage = walkers.data.currentPage
                            )
                        }

                    }
                }

                is WalkersPageUiEvent.SetFilters -> {
                    _state.update {
                        it.copy(
                            searchServices = event.services,
                            maxComplaintsCount = event.maxComplaints,
                            status = event.status,
                            showOnline = event.online
                        )
                    }
                    onEvent(WalkersPageUiEvent.LoadWalkers())
                }

                is WalkersPageUiEvent.SetUsersOrdering -> {
                    _state.update {
                        it.copy(
                            ascending = if (it.ordering == event.ordering) !it.ascending else true,
                            ordering = event.ordering
                        )
                    }
                    onEvent(WalkersPageUiEvent.LoadWalkers())
                }
            }
        }

    }

    private suspend fun getModelForWalker(walker: Walker): WalkerCardModel {
        return WalkerCardModel(
            walker.id,
            walker.firstName,
            walker.lastName,
            walker.imageUrl,
            walker.email,
            walker.isOnline,
            walker.rating,
            walker.reviewsCount,
            walker.complaintsCount,
            walker.repeatingOrdersCount,
            walker.location,
            walker.location?.let { loc ->
                locationService.location.first()?.calculateDistance(
                    APILocation(loc.latitude, loc.longitude)
                )
            }?.toFloat(),
            walker.desiredPayment,
            walker.services
        )
    }
}