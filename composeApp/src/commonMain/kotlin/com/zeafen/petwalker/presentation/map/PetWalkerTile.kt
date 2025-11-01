package com.zeafen.petwalker.presentation.map


import androidx.compose.ui.graphics.ImageBitmap
import com.zeafen.petwalker.domain.models.TileLoc
import com.zeafen.petwalker.domain.models.api.users.APILocation
import org.jetbrains.compose.resources.DrawableResource

data class PetWalkerTile(
    val id: String,
    val title: String,
    val description: String,
    val subDescription: String?,
    val location: APILocation,
    val image: ImageBitmap? = null
)