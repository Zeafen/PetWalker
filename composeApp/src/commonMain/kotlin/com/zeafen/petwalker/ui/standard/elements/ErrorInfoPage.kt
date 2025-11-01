package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.error_info_page_header
import petwalker.composeapp.generated.resources.ic_error
import petwalker.composeapp.generated.resources.ic_refresh
import petwalker.composeapp.generated.resources.reload_btn_text

@Composable
fun ErrorInfoPage(
    modifier: Modifier = Modifier,
    errorInfo: String,
    onReloadPage: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(0.8f),
            painter = painterResource(Res.drawable.ic_error),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        Text(
            text = stringResource(Res.string.error_info_page_header),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = TextUnit(
                0.1f,
                TextUnitType.Em
            ),
            fontWeight = FontWeight.W500
        )

        Text(
            modifier = Modifier
                .padding(vertical = 12.dp),
            text = errorInfo,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
            letterSpacing = TextUnit(
                2f,
                TextUnitType.Sp
            ),
            fontWeight = FontWeight.W400
        )
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            text = stringResource(Res.string.reload_btn_text),
            trailingIcon = painterResource(Res.drawable.ic_refresh),
            onClick = onReloadPage
        )
    }
}

@Composable
fun ErrorInfoHint(
    modifier: Modifier = Modifier,
    errorInfo: String,
    onReloadPage: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        HintWithIcon(
            hint = errorInfo,
            leadingIcon = painterResource(Res.drawable.ic_error),
            textColor = MaterialTheme.colorScheme.error,
            textStyle = MaterialTheme.typography.titleLarge
        )
        PetWalkerChip(
            label = stringResource(Res.string.reload_btn_text),
            icon = painterResource(Res.drawable.ic_refresh),
            onClick = onReloadPage,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}