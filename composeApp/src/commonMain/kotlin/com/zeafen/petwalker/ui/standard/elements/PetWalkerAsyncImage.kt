package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.network.NetworkFetcher
import coil3.network.ktor3.asNetworkClient
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_image_not_found

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PetWalkerAsyncImage(
    asyncImageModifier: Modifier = Modifier,
    defaultImageModifier: Modifier = asyncImageModifier,
    asyncContentScale: ContentScale = ContentScale.Crop,
    defaultContentScale: ContentScale = asyncContentScale,
    imageUrl: String?,
    defaultImage: Painter? = null,
) {
    if (imageUrl.isNullOrBlank()) {
        Image(
            modifier = defaultImageModifier,
            painter = defaultImage ?: painterResource(Res.drawable.ic_image_not_found),
            contentDescription = "Image not found",
            contentScale = defaultContentScale
        )
    } else {
        val client = koinInject<HttpClient>()
        AsyncImage(
            modifier = asyncImageModifier,
            contentScale = asyncContentScale,
            imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
                .components {
                    add(
                        factory = NetworkFetcher.Factory(
                            networkClient = { client.asNetworkClient() }
                        )
                    )
                }
                .crossfade(true)
                .build(),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null
        )
    }
}