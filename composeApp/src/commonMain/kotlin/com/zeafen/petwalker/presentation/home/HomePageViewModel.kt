@file:OptIn(ExperimentalTime::class)

package com.zeafen.petwalker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.RecruitmentsRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomePageViewModel(
    private val usersRepository: UsersRepository,
    private val reviewsRepository: ReviewsRepository,
    private val recruitmentsRepository: RecruitmentsRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<HomePageUiState> = MutableStateFlow(HomePageUiState())
    val state: StateFlow<HomePageUiState> = _state.asStateFlow()

    private var walkersLoadingJob: Job? = null

    init {
        authDataStore.authDataStoreFlow
            .onEach { userInfo ->
                _state.update {
                    it.copy(
                        currentUserName = "${userInfo.firstName} ${userInfo.lastName}",
                        currentUserImageUrl = userInfo.imageUrl
                    )
                }
            }
            .launchIn(viewModelScope)

        state
            .distinctUntilChanged { old, new ->
                old.maxWalkerPages == new.maxWalkerPages
                        && old.currentWalkersPagesComb == new.currentWalkersPagesComb
            }
            .onEach { value ->
                _state.update {
                    it.copy(maxWalkersPagesReached = value.currentWalkersPagesComb.second == it.maxWalkerPages)
                }
            }
            .launchIn(viewModelScope)

        onEvent(HomePageUiEvent.LoadData)
        onEvent(HomePageUiEvent.LoadBestWalkers())
        onEvent(HomePageUiEvent.ReloadBestWalker)
    }

    fun onEvent(event: HomePageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is HomePageUiEvent.LoadBestWalkers -> {
                    if (walkersLoadingJob?.isActive == true)
                        walkersLoadingJob?.cancel()
                    walkersLoadingJob = launch {
                        val currentComb = when {
                            event.page < state.value.currentWalkersPagesComb.first - 1 ->
                                event.page.coerceAtLeast(1) to (event.page + 1).coerceAtLeast(1)

                            event.page == state.value.currentWalkersPagesComb.first - 1 ->
                                event.page to state.value.currentWalkersPagesComb.first

                            event.page == state.value.currentWalkersPagesComb.second + 1 ->
                                state.value.currentWalkersPagesComb.second to event.page

                            event.page > state.value.currentWalkersPagesComb.second + 1 ->
                                (event.page - 1).coerceAtLeast(1) to event.page.coerceAtLeast(1)

                            else -> 1 to 1
                        }
                        _state.update {
                            it.copy(
                                isLoadingWalkers = true,
                                isLoadingForward = it.currentWalkersPagesComb.second == currentComb.first,
                                walkersLoadingError = null
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null || token.accessToken.isBlank()) {
                            _state.update {
                                it.copy(
                                    walkersLoadingError = NetworkError.UNAUTHORIZED,
                                    isLoadingWalkers = false
                                )
                            }
                            return@launch
                        }

                        val walkers = usersRepository.getWalkers(
                            page = event.page,
                            perPage = 15,
                            ordering = UsersOrdering.Rating,
                            ascending = false
                        )
                        if (walkers is APIResult.Error) {
                            _state.update {
                                it.copy(
                                    walkersLoadingError = walkers.info,
                                    isLoadingWalkers = false
                                )
                            }
                            return@launch
                        }

                        val maxPages = (walkers as APIResult.Succeed).data!!.totalPages
                        val result = if (event.page == 1 && walkers.data!!.result.size > 1)
                            walkers.data.result.subList(1, max(walkers.data.result.size - 1, 1))
                        else walkers.data!!.result

                        val newWalkers = when {
                            currentComb.second == state.value.currentWalkersPagesComb.first ->
                                result + state.value.featuredWalkers.take(15)

                            currentComb.first == state.value.currentWalkersPagesComb.second ->
                                state.value.featuredWalkers.takeLast(15) + result

                            else -> result
                        }

                        _state.update {
                            it.copy(
                                currentWalkersPagesComb = currentComb,
                                featuredWalkers = newWalkers,
                                isLoadingWalkers = false,
                                maxWalkerPages = maxPages
                            )
                        }
                    }
                }

                HomePageUiEvent.LoadData -> {
                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank())
                        return@launch
                    val now = Clock.System.now()
                    val timeZone = TimeZone.currentSystemDefault()
                    val timeFrom =
                        now.minus(1, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)

                    val newRecruitmentsAsOwner =
                        async {
                            val data = recruitmentsRepository.getRecruitmentsAsOwner(
                                page = 1,
                                perPage = 10,
                                state = RecruitmentState.Pending,
                                outcoming = false,
                                timeFrom = timeFrom.toString()
                            )
                            when {
                                data is APIResult.Succeed -> data.data?.result?.any() ?: false
                                else -> false
                            }
                        }
                    val newRecruitmentsAsWalker =
                        async {
                            val data = recruitmentsRepository.getRecruitmentsAsWalker(
                                page = 1,
                                perPage = 10,
                                state = RecruitmentState.Pending,
                                outcoming = false,
                                timeFrom = timeFrom.toString()
                            )
                            when {
                                data is APIResult.Succeed -> data.data?.result?.any() ?: false
                                else -> false
                            }
                        }

                    _state.update {
                        it.copy(
                            hasNewRecruitmentsAsOwner = newRecruitmentsAsOwner.await(),
                            hasNewRecruitmentsAsWalker = newRecruitmentsAsWalker.await()
                        )
                    }
                }

                HomePageUiEvent.ReloadBestWalker -> {
                    _state.update {
                        it.copy(
                            bestWalker = APIResult.Downloading(),
                            bestWalkerStatistics = APIResult.Downloading()
                        )
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null || token.accessToken.isBlank()) {
                        _state.update {
                            it.copy(bestWalker = APIResult.Error(NetworkError.UNAUTHORIZED))
                        }
                        return@launch
                    }

                    val bestWalker = usersRepository.getWalkers(
                        page = 1,
                        perPage = 1,
                        ordering = UsersOrdering.Rating,
                        ascending = false
                    )

                    if (bestWalker is APIResult.Error) {
                        _state.update {
                            it.copy(bestWalker = APIResult.Error(bestWalker.info))
                        }
                        return@launch
                    }

                    val walker = (bestWalker as APIResult.Succeed).data!!.result.firstOrNull()


                    val statistics = walker?.let {
                        reviewsRepository.getUserReviewsStats(walker.id)
                    }

                    _state.update {
                        it.copy(
                            bestWalker = walker?.let {
                                APIResult.Succeed(it)
                            } ?: APIResult.Error(NetworkError.NOT_FOUND),
                            bestWalkerStatistics = statistics
                                ?: APIResult.Error(NetworkError.NOT_FOUND)
                        )
                    }
                }
            }
        }
    }
}