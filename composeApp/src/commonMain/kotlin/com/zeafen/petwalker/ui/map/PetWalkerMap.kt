package com.zeafen.petwalker.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.getDegreesCoordinatesFromMercator
import com.zeafen.petwalker.data.helpers.toTileLoc
import com.zeafen.petwalker.domain.models.TileLoc
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.map.MapScreenUIState
import com.zeafen.petwalker.presentation.map.MapScreenUiEvent
import com.zeafen.petwalker.presentation.map.PetWalkerTile
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.io.Buffer
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import ovh.plrapps.mapcompose.api.ExperimentalClusteringApi
import ovh.plrapps.mapcompose.api.addCallout
import ovh.plrapps.mapcompose.api.addClusterer
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.centerOnMarker
import ovh.plrapps.mapcompose.api.enableMarkerDrag
import ovh.plrapps.mapcompose.api.enableRotation
import ovh.plrapps.mapcompose.api.enableScrolling
import ovh.plrapps.mapcompose.api.enableZooming
import ovh.plrapps.mapcompose.api.fullSize
import ovh.plrapps.mapcompose.api.getMarkerInfo
import ovh.plrapps.mapcompose.api.hasMarker
import ovh.plrapps.mapcompose.api.markerDerivedState
import ovh.plrapps.mapcompose.api.onMarkerClick
import ovh.plrapps.mapcompose.api.removeAllMarkers
import ovh.plrapps.mapcompose.api.removeMarker
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.MapUI
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_error
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_location_pin
import petwalker.composeapp.generated.resources.ic_refresh
import petwalker.composeapp.generated.resources.ic_search
import petwalker.composeapp.generated.resources.ic_success
import petwalker.composeapp.generated.resources.img
import petwalker.composeapp.generated.resources.loading_label
import petwalker.composeapp.generated.resources.map_screen_header
import petwalker.composeapp.generated.resources.pick_location_btn_text
import petwalker.composeapp.generated.resources.success_label
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalClusteringApi::class)
@Composable
fun PetWalkerMap(
    modifier: Modifier = Modifier,
    state: MapScreenUIState,
    streamProvider: TileStreamProvider,
    onEvent: (MapScreenUiEvent) -> Unit,
    onBackClick: () -> Unit,
    onOverlayClick: (id: String) -> Unit
) {
    var popupContent = remember(state.tilesLoadingResult) {
        state.tilesLoadingResult
    }
    val markerWidth = with(LocalDensity.current) { 32.dp.toPx() }
    val markerHeight = with(LocalDensity.current) { 48.dp.toPx() }
    var followCurrentUserLoc by remember {
        mutableStateOf(false)
    }
    var followObservedTile by remember {
        mutableStateOf(false)
    }
    val mapState = remember {
        val n = 20
        MapState(n + 1, 2.0.pow(8 + n).roundToInt(), 2.0.pow(8 + n).roundToInt()) {
            minimumScaleMode(Forced(0.01))
        }
            .apply {
                addLayer(tileStreamProvider = streamProvider)
                addClusterer("default") { ids ->
                    {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                )
                                .alpha(0.4f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ids.size.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                enableZooming()
                enableRotation()
                enableScrolling()


                onMarkerClick { id, x, y ->
                    state.loadedTiles.firstOrNull { it.id == id }?.let { tile ->
                        addCallout(
                            id, x, y,
                            absoluteOffset = DpOffset(0.dp, (-52).dp)
                        ) {
                            PetWalkerCallout(
                                modifier = Modifier
                                    .heightIn(max = 200.dp)
                                    .fillMaxWidth(0.9f)
                                    .wrapContentWidth()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        onOverlayClick(id)
                                    }
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.onSurface,
                                        RoundedCornerShape(16.dp)
                                    ),
                                image = tile.image,
                                title = tile.title,
                                description = tile.description,
                                subDescription = tile.subDescription,
                            )
                        }
                    }
                }
            }
    }

    //updating markers
    LaunchedEffect(state.loadedTiles) {
        mapState.removeAllMarkers()

        //hide unused markers
        val markersToHide = mapState.markerDerivedState().value.filter {
            state.loadedTiles.any { tile -> tile.id != it.id }
        }.map { it.id }
        markersToHide.forEach { mapState.removeMarker(it) }

        //showing new markers
        state.loadedTiles.filter { !markersToHide.contains(it.id) }.forEach { tile ->
            val tileLocation = tile.location.toTileLoc(20)
            mapState.addMarker(
                tile.id,
                tileLocation.x / mapState.fullSize.width,
                tileLocation.y / mapState.fullSize.height
            ) {
                tile.image?.let {
                    Image(
                        modifier = Modifier
                            .size(32.dp, 48.dp)
                            .drawWithCache {
                                onDrawWithContent {
                                    val color = if (state.observedTileId == tile.id)
                                        Color(255, 165, 0)
                                    else Color.Black

                                    val path = Path()
                                        .apply {
                                            addOval(
                                                Rect(
                                                    center = Offset(
                                                        this@onDrawWithContent.size.width * 0.5f,
                                                        this@onDrawWithContent.size.height * 0.3f
                                                    ),
                                                    radius = markerWidth * 0.8f / 2f - markerWidth * 0.02f,
                                                ),
                                                Path.Direction.Clockwise
                                            )
                                        }
                                    clipPath(path) {
                                        this@onDrawWithContent.drawContent()
                                    }
                                    translate(
                                        center.x,
                                        this.size.height
                                    ) {
                                        drawLine(
                                            start = Offset(0f, 0f),
                                            end = Offset(
                                                -markerWidth * 0.5f,
                                                -markerHeight * 0.7f
                                            ),
                                            color = color,
                                            strokeWidth = 2.dp.toPx()
                                        )
                                        drawLine(
                                            start = Offset(0f, 0f),
                                            end = Offset(
                                                markerWidth * 0.5f,
                                                -markerHeight * 0.7f
                                            ),
                                            color = color,
                                            strokeWidth = 2.dp.toPx()
                                        )
                                    }
                                    drawArc(
                                        startAngle = 270f,
                                        sweepAngle = -90f,
                                        topLeft = Offset(
                                            this.size.width * 0.01f,
                                            this.size.height * 0.01f,
                                        ),
                                        size = Size(
                                            this.size.width * 0.99f,
                                            this.size.height * 0.6f
                                        ),
                                        useCenter = false,
                                        color = color,
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Bevel
                                        )
                                    )
                                    drawArc(
                                        startAngle = 270f,
                                        sweepAngle = 90f,
                                        topLeft = Offset(
                                            this.size.width * 0.01f,
                                            this.size.height * 0.01f,
                                        ),
                                        size = Size(
                                            this.size.width * 0.99f,
                                            this.size.height * 0.6f
                                        ),
                                        useCenter = false,
                                        color = color,
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Bevel
                                        )
                                    )
                                }
                            },
                        bitmap = it,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                } ?: Icon(
                    painter = painterResource(Res.drawable.ic_location_pin),
                    contentDescription = null
                )
            }
        }
    }

    //updating location picker marker
    LaunchedEffect(
        state.currentUserLocation,
        state.showPickedLocationPin
    ) {
        if (!state.showPickedLocationPin)
            mapState.removeMarker("pick_location")

        if (state.showPickedLocationPin) {
            val currentLocation = state.currentUserLocation?.toTileLoc(20) ?: TileLoc(
                20,
                mapState.fullSize.width / 2.0,
                mapState.fullSize.height / 2.0,
            )

            if (!mapState.hasMarker("pick_location")) {
                mapState.addMarker(
                    "pick_location",
                    currentLocation.x / mapState.fullSize.width,
                    currentLocation.y / mapState.fullSize.height,
                ) {
                    Image(
                        modifier = Modifier
                            .size(32.dp, 48.dp),
                        painter = painterResource(Res.drawable.ic_location_pin),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
                mapState.enableMarkerDrag("pick_location")
            }
        }
    }

    //following user`s location
    LaunchedEffect(
        state.currentUserLocation,
        followCurrentUserLoc
    ) {
        if (followCurrentUserLoc && state.currentUserLocation != null) {
            val tileLoc = state.currentUserLocation.toTileLoc(20)
            val relativeX = (tileLoc.x / mapState.fullSize.width)
            val relativeY = (tileLoc.y / mapState.fullSize.height)
            mapState
                .scrollTo(
                    relativeX,
                    relativeY,
                )
        }
    }

    //following observed tile
    LaunchedEffect(
        followObservedTile,
        state.observedTileId,
        state.observedTileId?.let {
            mapState.getMarkerInfo(state.observedTileId)
        }
    ) {
        if (!followCurrentUserLoc && followObservedTile && state.observedTileId != null) {
            mapState.centerOnMarker(state.observedTileId)
        }
    }

    Scaffold(
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.map_screen_header),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_go_back),
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(MapScreenUiEvent.ReloadData) }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_refresh),
                            contentDescription = "Go back"
                        )
                    }
                }
            )
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                MapUI(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = mapState
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.showPickedLocationPin)
                        FilledIconButton(
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (followCurrentUserLoc)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                            onClick = {
                                followCurrentUserLoc = !followCurrentUserLoc
                                if (followCurrentUserLoc)
                                    followObservedTile = false
                            }
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_account_box),
                                contentDescription = "Focus on current user"
                            )
                        }

                    state.observedTileId?.let {
                        FilledIconButton(
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (followObservedTile)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                            onClick = {
                                followObservedTile = !followObservedTile
                                if (followObservedTile)
                                    followCurrentUserLoc = false
                            }
                        ) {
                            Icon(
                                painterResource(Res.drawable.ic_search),
                                contentDescription = "Focus on observed tile"
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(0.6f)
                        .padding(bottom = 12.dp)
                ) {
                    AnimatedVisibility(
                        visible = state.showPickedLocationPin
                    ) {
                        PetWalkerButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(Res.string.pick_location_btn_text),
                            onClick = {
                                mapState.getMarkerInfo("pick_location")?.let { info ->
                                    val location = getDegreesCoordinatesFromMercator(
                                        info.x,
                                        info.y,
                                        mapState.fullSize.width.toDouble()
                                    )
                                    onEvent(MapScreenUiEvent.PickLocation(location))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    if (popupContent != null)
        Popup(
            alignment = Alignment.TopCenter,
            onDismissRequest = { popupContent = null },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (popupContent!!) {
                    is APIResult.Downloading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp),
                            strokeWidth = 4.dp
                        )
                    }

                    is APIResult.Error<*> -> Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(Res.drawable.ic_error),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                    is APIResult.Succeed<*> -> Image(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource(Res.drawable.ic_success),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when (popupContent!!) {
                        is APIResult.Downloading -> stringResource(Res.string.loading_label)
                        is APIResult.Error<*> -> stringResource((popupContent as APIResult.Error<*>).info.infoResource())
                        is APIResult.Succeed<*> -> stringResource(Res.string.success_label)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (popupContent is APIResult.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }
}

@Composable
fun PetWalkerCallout(
    modifier: Modifier = Modifier,
    image: ImageBitmap?,
    title: String,
    description: String,
    subDescription: String? = null,
) {
    Box(
        modifier = modifier
    ) {
        Row {
            image?.let {
                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight()
                        .weight(2f)
                        .clip(RoundedCornerShape(16.dp)),
                    bitmap = image,
                    contentScale = Crop,
                    contentDescription = "Tile image"
                )
            }
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                subDescription?.let {
                    Text(
                        text = subDescription,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Light,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


val client by lazy { HttpClient() }

@OptIn(ExperimentalClusteringApi::class)
@Preview
@Composable
private fun Preview() {
    PetWalker_theme {
        Surface {
            val largeImage = imageResource(Res.drawable.img)
            var state by remember {
                mutableStateOf(
                    MapScreenUIState(
                        showPickedLocationPin = true,
                        currentUserLocation =
                            APILocation(
                                55.750110,
                                37.617119
                            ),
                        loadedTiles = listOf(
                            PetWalkerTile(
                                "0",
                                "Me",
                                "My location",
                                null,
                                APILocation(
                                    55.515259,
                                    36.986847
                                ),
                            ),
                            PetWalkerTile(
                                "1",
                                "Vnukovo",
                                "Vnukovo airport",
                                null,
                                APILocation(
                                    55.611711,
                                    37.300232
                                ),
                            ),
                            PetWalkerTile(
                                "2",
                                "Aprelevka",
                                "Not so favorite station",
                                null,
                                APILocation(
                                    55.545571,
                                    37.074738
                                ),
                            ),
                            PetWalkerTile(
                                "3",
                                "Selyatino",
                                "Home, sweet home",
                                null,
                                APILocation(
                                    55.381607,
                                    36.721802
                                ),
                            ),
                            PetWalkerTile(
                                "4",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed posuere condimentum vehicula. Cras convallis augue vel dapibus malesuada. Suspendisse faucibus nec tellus sit amet malesuada. Quisque sagittis orci ut orci sollicitudin, id pretium diam dignissim. In libero purus, semper sed ligula vitae, dapibus malesuada urna. Donec in massa eget libero interdum faucibus vitae at ligula. Praesent id leo quis nunc vehicula aliquet. In auctor leo sed nisl eleifend, ut consectetur nulla cursus. Sed pellentesque, purus id malesuada volutpat, est enim facilisis diam, a vehicula ex lectus non velit.",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed posuere condimentum vehicula. Cras convallis augue vel dapibus malesuada. Suspendisse faucibus nec tellus sit amet malesuada. Quisque sagittis orci ut orci sollicitudin, id pretium diam dignissim. In libero purus, semper sed ligula vitae, dapibus malesuada urna. Donec in massa eget libero interdum faucibus vitae at ligula. Praesent id leo quis nunc vehicula aliquet. In auctor leo sed nisl eleifend, ut consectetur nulla cursus. Sed pellentesque, purus id malesuada volutpat, est enim facilisis diam, a vehicula ex lectus non velit.",
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed posuere condimentum vehicula. Cras convallis augue vel dapibus malesuada. Suspendisse faucibus nec tellus sit amet malesuada. Quisque sagittis orci ut orci sollicitudin, id pretium diam dignissim. In libero purus, semper sed ligula vitae, dapibus malesuada urna. Donec in massa eget libero interdum faucibus vitae at ligula. Praesent id leo quis nunc vehicula aliquet. In auctor leo sed nisl eleifend, ut consectetur nulla cursus. Sed pellentesque, purus id malesuada volutpat, est enim facilisis diam, a vehicula ex lectus non velit.",
                                APILocation(
                                    55.371607,
                                    36.721802
                                ),
                                largeImage
                            ),
                        ),
                        observedTileId = "pick_location"
                    )
                )
            }
            val streamProvider = remember {
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

            PetWalkerMap(
                state = state,
                onEvent = {
                },
                onBackClick = {},
                streamProvider = streamProvider,
                onOverlayClick = {}
            )
        }
    }
}