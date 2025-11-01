package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun UserInfoHeader(
    modifier: Modifier = Modifier,
    walkerFullName: String,
    walkerImageUrl: String?,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        PetWalkerAsyncImage(
            imageUrl = walkerImageUrl,
            asyncImageModifier = Modifier
                .weight(2f)
                .clip(CircleShape),
        )

        Text(modifier = Modifier
            .weight(3f),
            text = walkerFullName,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis
        )

    }
}