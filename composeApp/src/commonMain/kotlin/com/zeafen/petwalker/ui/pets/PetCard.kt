package com.zeafen.petwalker.ui.pets

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.age_years_txt
import petwalker.composeapp.generated.resources.born_at_txt
import petwalker.composeapp.generated.resources.ic_app
import petwalker.composeapp.generated.resources.ic_calendar

@Composable
fun PetCard(
    modifier: Modifier = Modifier,
    pet: Pet
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        PetWalkerAsyncImage(
            asyncImageModifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.7f),
            imageUrl = pet.imageUrl,
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 12.dp),
            text = pet.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        FlowRow {
            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = pet.species,
                leadingIcon = painterResource(Res.drawable.ic_app),
            )
            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = stringResource(
                    Res.string.born_at_txt,
                    pet.date_birth.format(
                        LocalDateTime.Format {
                            LocalDateTime.Format {
                                day()
                                char('/')
                                monthNumber()
                                char('/')
                                year()
                                char(' ')
                                hour()
                                char(':')
                                minute()
                            }
                        }
                    )
                ),
                leadingIcon = painterResource(Res.drawable.ic_calendar),
            )
        }
    }
}

@Composable
fun PetCard(
    modifier: Modifier = Modifier,
    pet: Pet,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        onClick = onClick
    ) {
        PetWalkerAsyncImage(
            asyncImageModifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.7f),
            imageUrl = pet.imageUrl,
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 12.dp),
            text = pet.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        FlowRow {
            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = pet.species,
                leadingIcon = painterResource(Res.drawable.ic_app),
            )
            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = stringResource(
                    Res.string.born_at_txt,
                    pet.date_birth.format(
                        LocalDateTime.Format {
                            LocalDateTime.Format {
                                day()
                                char('/')
                                monthNumber()
                                char('/')
                                year()
                                char(' ')
                                hour()
                                char(':')
                                minute()
                            }
                        }
                    )
                ),
                leadingIcon = painterResource(Res.drawable.ic_calendar),
            )
        }
    }
}