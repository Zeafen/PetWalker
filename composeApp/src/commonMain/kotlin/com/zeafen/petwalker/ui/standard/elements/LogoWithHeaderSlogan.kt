package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_app

@Composable
fun LogoWithHeaderSlogan(
    modifier: Modifier = Modifier,
    header: String,
    headerAlignment: TextAlign = TextAlign.Start,
    slogan: String,
    sloganAlignment: TextAlign = TextAlign.Start,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .width(100.dp),
            painter = painterResource(Res.drawable.ic_app),
            contentDescription = "Pet walker",
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .padding(bottom = 12.dp, top = 16.dp),
            text = header,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = headerAlignment
        )
        Text(
            text = slogan,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = sloganAlignment
        )

    }
}