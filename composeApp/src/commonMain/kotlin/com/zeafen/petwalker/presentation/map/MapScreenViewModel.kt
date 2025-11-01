package com.zeafen.petwalker.presentation.map


import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.APIResult.Downloading
import com.zeafen.petwalker.domain.models.api.util.APIResult.Error
import com.zeafen.petwalker.domain.models.api.util.APIResult.Succeed
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.UsersRepository
import com.zeafen.petwalker.presentation.map.MapScreenUiEvent.SetPresentationType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import ovh.plrapps.mapcompose.core.TileStreamProvider
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.check_walker_details_label
import petwalker.composeapp.generated.resources.distance_from_user_txt
import petwalker.composeapp.generated.resources.services_label


class MapScreenViewModel(
    private val walkersRepo: UsersRepository,
    private val assignmentsRepo: AssignmentsRepository,
    private val authDataStore: AuthDataStoreRepository,
    private val locationService: LocationService,
    private val client: HttpClient
) : ViewModel() {

    private val _state: MutableStateFlow<MapScreenUIState> = MutableStateFlow(MapScreenUIState())
    val state: StateFlow<MapScreenUIState> = _state.asStateFlow()

    val tileStream by lazy {
        TileStreamProvider { row, col, zoomLvl ->
            return@TileStreamProvider try {
                val url = "https://tile.openstreetmap.org/${zoomLvl}/${col}/${row}.png"
                val response = client.get(url)
                if (response.status.value !in 200..299)
                    null
                val buffer = Buffer()
                buffer.write(response.body<ByteArray>())
                buffer
            } catch (ex: Exception) {
                null
            }
        }
    }

    init {
        combine(authDataStore.authDataStoreFlow, locationService.location) { userInfo, location ->
            location ?: userInfo.defaultLocationInfo
        }
            .onEach { location ->
                _state.update {
                    it.copy(currentUserLocation = location)
                }
            }
            .launchIn(viewModelScope)

        locationService.startObserving()
    }

    override fun onCleared() {
        locationService.cancelObserving()
        super.onCleared()
    }

    private var tilesLoadingJob: Job? = null
    fun onEvent(event: MapScreenUiEvent) {
        viewModelScope.launch {
            when (event) {
                MapScreenUiEvent.ReloadData -> {
                    if (tilesLoadingJob?.isActive == true)
                        return@launch
                    onEvent(SetPresentationType(state.value.presentationType))
                }

                is MapScreenUiEvent.SetPresentationType -> {
                    if (tilesLoadingJob?.isActive == true)
                        tilesLoadingJob?.cancel()
                    tilesLoadingJob = launch {
                        _state.update {
                            it.copy(
                                tilesLoadingResult = Downloading(),
                                showPickedLocationPin = false
                            )
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                it.copy(tilesLoadingResult = Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }
                        val currentLocation = locationService.location.first()

                        when (event.type) {
                            is MapPresentationType.Assignment -> {
                                val assignment = assignmentsRepo.getAssignmentById(
                                    event.type.assignmentId
                                )

                                if (assignment is Error) {
                                    _state.update {
                                        it.copy(tilesLoadingResult = Error(assignment.info))
                                    }
                                    return@launch
                                }

                                //generating tile
                                val assignmentData = (assignment as Succeed).data!!
                                val tile = getTileForAssignment(assignmentData, currentLocation)

                                //updating loaded tiles
                                _state.update {
                                    it.copy(
                                        observedTileId = assignmentData.id,
                                        tilesLoadingResult = Succeed(),
                                        loadedTiles = listOf(tile)
                                    )
                                }
                            }

                            is MapPresentationType.Assignments -> {
                                val assignments = assignmentsRepo.getAssignments(
                                    event.type.page,
                                    event.type.pageSize,
                                    null,
                                    currentLocation,
                                    event.type.maxDistance,
                                    event.type.from?.toString(),
                                    event.type.until?.toString(),
                                    event.type.services,
                                    event.type.ordering,
                                    event.type.ascending
                                )

                                if (assignments is Error) {
                                    _state.update {
                                        it.copy(tilesLoadingResult = Error(assignments.info))
                                    }
                                    return@launch
                                }

                                val tiles = (assignments as Succeed).data!!.result.map {
                                    async {
                                        getTileForAssignment(it, currentLocation)
                                    }
                                }
                                _state.update {
                                    it.copy(
                                        loadedTiles = tiles.awaitAll(),
                                        observedTileId = null,
                                        tilesLoadingResult = Succeed()
                                    )
                                }
                            }

                            is MapPresentationType.Walker -> {
                                val walker =
                                    walkersRepo.getWalker(event.type.walkerId)

                                if (walker is Error) {
                                    _state.update {
                                        it.copy(
                                            tilesLoadingResult = Error(walker.info)
                                        )
                                    }
                                    return@launch
                                }

                                val tile = getTileForWalker(
                                    (walker as Succeed).data!!,
                                    currentLocation
                                )
                                if (tile == null) {
                                    _state.update {
                                        it.copy(tilesLoadingResult = Error(NetworkError.CONFLICT))
                                    }
                                    return@launch
                                }

                                _state.update {
                                    it.copy(
                                        observedTileId = tile.id,
                                        loadedTiles = listOf(tile),
                                        tilesLoadingResult = Succeed()
                                    )
                                }
                            }

                            is MapPresentationType.Walkers -> {
                                val walkers = walkersRepo.getWalkers(
                                    event.type.page,
                                    event.type.pageSize,
                                    currentLocation,
                                    event.type.maxDistance,
                                    null,
                                    event.type.services,
                                    event.type.maxComplaints,
                                    event.type.status,
                                    null,
                                    event.type.ordering,
                                    event.type.ascending
                                )

                                if (walkers is Error) {
                                    _state.update {
                                        it.copy(
                                            tilesLoadingResult = Error(walkers.info)
                                        )
                                    }
                                    return@launch
                                }

                                val tiles = (walkers as Succeed).data!!.result.map {
                                    async {
                                        getTileForWalker(
                                            it, currentLocation
                                        )
                                    }
                                }
                                _state.update {
                                    it.copy(
                                        loadedTiles = tiles.awaitAll().mapNotNull { it },
                                        tilesLoadingResult = Succeed()
                                    )
                                }
                            }

                            MapPresentationType.PickLocation -> {
                                _state.update {
                                    it.copy(
                                        tilesLoadingResult = APIResult.Succeed(),
                                        loadedTiles = emptyList(),
                                        showPickedLocationPin = true
                                    )
                                }
                            }
                        }
                    }
                }

                is MapScreenUiEvent.PickLocation -> {
                    authDataStore.updateDefaultLocation(event.location)
                }
            }
        }
    }


    private suspend fun getTileForAssignment(
        assignment: Assignment,
        currentUserLocation: APILocation?
    ): PetWalkerTile {
        val distanceFromUser = currentUserLocation
            ?.calculateDistance(assignment.location)
            ?.toFloat()

        //generating tile title
        val tileTitle = assignment.title

        //generating tile description
        val tileSubDescription =
            (
                    "${getString(assignment.type.displayName)}: " +
                            getString(assignment.type.defaultDescription) +
                            "\n${
                                distanceFromUser?.let {
                                    getString(
                                        Res.string.distance_from_user_txt,
                                        distanceFromUser.format(2)
                                    )
                                }
                            }"
                    )

        return PetWalkerTile(
            assignment.id,
            tileTitle,
            assignment.description ?: "",
            tileSubDescription,
            assignment.location,
            getDrawableResourceBytes(
                getSystemResourceEnvironment(),
                assignment.type.displayImage
            ).decodeToImageBitmap()
        )

    }

    private suspend fun getTileForWalker(
        walker: Walker,
        currentUserLocation: APILocation?,
    ): PetWalkerTile? {
        if (walker.location == null)
            return null

        val image = walker.imageUrl?.let {
            val response = client.get(it)
            response.async {
                if (response.status.value !in 200..299)
                    null
                response.body<ByteArray>().decodeToImageBitmap()
            }
        }

        //generating tile parameters
        val tileTitle = ("${walker.firstName} ${walker.lastName} " +
                "(${getString(walker.accountStatus.displayName)})")

        val servicesToDisplayList = walker.services
            .mapNotNull {
                if (it.service != ServiceType.Other)
                    getString(it.service.displayName)
                else null
            }
            .ifEmpty { listOf(getString(Res.string.check_walker_details_label)) }

        val distanceStr =
            currentUserLocation?.calculateDistance(
                APILocation(
                    walker.location.latitude,
                    walker.location.longitude
                )
            )?.toFloat()?.format(2)

        val tileSubDescription = (
                getString(Res.string.services_label) +
                        " ${
                            servicesToDisplayList.joinToString(
                                ", "
                            )
                        }" +
                        distanceStr?.let {
                            getString(Res.string.distance_from_user_txt, distanceStr)
                        }
                )


        //generating tile instance
        return PetWalkerTile(
            walker.id,
            tileTitle,
            walker.location.address,
            tileSubDescription,
            APILocation(
                walker.location.latitude,
                walker.location.longitude,
            ),
            image?.await()
        )
    }
}