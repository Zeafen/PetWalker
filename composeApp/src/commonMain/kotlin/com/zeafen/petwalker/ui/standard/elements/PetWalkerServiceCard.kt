package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.users.UserService
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_clear
import petwalker.composeapp.generated.resources.ic_edit
import petwalker.composeapp.generated.resources.payment_rubbles_txt

@Composable
fun PetWalkerServiceCard(
    modifier: Modifier = Modifier,
    service: UserService,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(12.dp),
                painter = painterResource(service.service.displayImage),
                contentDescription = "Pet walker",
                contentScale = ContentScale.FillWidth
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                onEditClick?.let {
                    FilledIconButton(
                        onClick = onEditClick
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_edit),
                            contentDescription = "Edit service"
                        )
                    }
                }
                onDeleteClick?.let {
                    FilledIconButton(
                        onClick = onDeleteClick
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_clear),
                            contentDescription = "Delete service"
                        )
                    }
                }
            }
        }
        ExpandableContent(
            borderColor = Color.Transparent,
            defaultContent = {
                Text(
                    modifier = Modifier
                        .padding(bottom = 12.dp, top = 24.dp),
                    text = stringResource(service.service.displayName),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            expandableContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    service.payment?.let {
                        Text(
                            text = stringResource(
                                Res.string.payment_rubbles_txt,
                                it.format(2)
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    Text(
                        modifier = Modifier
                            .heightIn(max = 128.dp)
                            .verticalScroll(rememberScrollState()),
                        text = service.additionalInfo
                            ?: stringResource(service.service.defaultDescription),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        )
    }

}
