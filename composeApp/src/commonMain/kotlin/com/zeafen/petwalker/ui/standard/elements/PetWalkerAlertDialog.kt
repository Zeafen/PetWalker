package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.confirm_btn_txt

@Composable
fun PetWalkerAlertDialog(
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    title: String,
    text: String? = null,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            PetWalkerButton(
                text = stringResource(Res.string.confirm_btn_txt),
                onClick = onConfirm,
            )
        },
        dismissButton = {
            PetWalkerButton(
                text = stringResource(Res.string.cancel_btn_txt),
                containerColor = MaterialTheme.colorScheme.error,
                onClick = onDismissRequest,
            )
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = text?.let {
            {
                Text(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }
        },
        icon = icon?.let {
            {
                Icon(
                    modifier = Modifier
                        .size(100.dp),
                    painter = it,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null
                )
            }
        }
    )
}