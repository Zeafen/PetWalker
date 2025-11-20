package com.zeafen.petwalker.ui.walkers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.ui.WalkerCardModel
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.RatingRow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.complaints_count_txt
import petwalker.composeapp.generated.resources.desired_payment_default_display_name
import petwalker.composeapp.generated.resources.distance_from_user_txt
import petwalker.composeapp.generated.resources.ic_location_pin
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.ic_refresh
import petwalker.composeapp.generated.resources.ic_service
import petwalker.composeapp.generated.resources.ic_walk
import petwalker.composeapp.generated.resources.ic_wallet
import petwalker.composeapp.generated.resources.online_label
import petwalker.composeapp.generated.resources.repeating_orders_count_label
import petwalker.composeapp.generated.resources.reviews_count_txt
import petwalker.composeapp.generated.resources.services_label

@Composable
fun WalkerCard(
    modifier: Modifier = Modifier,
    walker: Walker,
    distance: Float?
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
    ) {
        Row {
            PetWalkerAsyncImage(
                asyncImageModifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f)
                    .heightIn(max = 240.dp),
                defaultImageModifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f),
                imageUrl = walker.imageUrl
            )
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 4.dp)
            ) {
                Text(
                    text = "${walker.firstName} ${walker.lastName}",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2
                )
                RatingInfoColumn(
                    walkerRating = walker.rating,
                    walkerComplaintsCount = walker.complaintsCount,
                    walkerReviewsCount = walker.reviewsCount
                )
            }
        }
        WalkerCardInfoBody(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp),
            address = walker.location?.address,
            distance = distance,
            repeatingOrdersCount = walker.repeatingOrdersCount,
            isOnline = walker.isOnline,
            desiredPayment = walker.desiredPayment,
            services = walker.services.map { stringResource(it.service.displayName) }
        )
    }
}


@Composable
fun WalkerCard(
    modifier: Modifier = Modifier,
    walker: WalkerCardModel,
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
        Row {
            PetWalkerAsyncImage(
                asyncImageModifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f)
                    .heightIn(max = 240.dp),
                defaultImageModifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.4f),
                imageUrl = walker.imageUrl
            )
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 4.dp)
            ) {
                Text(
                    text = "${walker.firstName} ${walker.lastName}",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2
                )
                RatingInfoColumn(
                    walkerRating = walker.rating,
                    walkerComplaintsCount = walker.complaintsCount,
                    walkerReviewsCount = walker.reviewsCount
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        WalkerCardInfoBody(
            address = walker.location?.address,
            distance = walker.distance,
            repeatingOrdersCount = walker.repeatingOrdersCount,
            isOnline = walker.isOnline,
            desiredPayment = walker.desiredPayment,
            services = walker.services.map { stringResource(it.service.displayName) }
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingInfoColumn(
    modifier: Modifier = Modifier,
    walkerRating: Float,
    walkerReviewsCount: Long,
    walkerComplaintsCount: Long
) {
    Column(modifier = modifier) {
        RatingRow(
            currentRating = walkerRating,
            starSize = 24.dp
        )
        FlowRow {
            Text(
                text = stringResource(Res.string.reviews_count_txt, walkerReviewsCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(Res.string.complaints_count_txt, walkerComplaintsCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WalkerCardInfoBody(
    modifier: Modifier = Modifier,
    address: String?,
    distance: Float?,
    repeatingOrdersCount: Long,
    services: List<String>?,
    isOnline: Boolean?,
    desiredPayment: DesiredPayment?
) {

    FlowRow(modifier = modifier) {
        HintWithIcon(
            hint = "Desired payment: " +
                    stringResource(
                        desiredPayment?.displayName
                            ?: Res.string.desired_payment_default_display_name
                    ),
            leadingIcon = painterResource(Res.drawable.ic_wallet),
            textStyle = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.size(12.dp))

        address?.let {
            HintWithIcon(
                leadingIcon = painterResource(Res.drawable.ic_location_pin),
                hint = it
            )
            Spacer(Modifier.size(8.dp))
        }
        distance?.let {
            HintWithIcon(
                leadingIcon = painterResource(Res.drawable.ic_walk),
                hint = stringResource(
                    Res.string.distance_from_user_txt,
                    it.format(2)
                )
            )
            Spacer(Modifier.size(8.dp))
        }

        if (!services.isNullOrEmpty()) {
            HintWithIcon(
                leadingIcon = painterResource(Res.drawable.ic_service),
                hint = stringResource(Res.string.services_label) + ": " + services.joinToString()
            )
            Spacer(Modifier.size(8.dp))
        }

        HintWithIcon(
            leadingIcon = painterResource(Res.drawable.ic_refresh),
            hint = stringResource(
                Res.string.repeating_orders_count_label,
                repeatingOrdersCount
            )
        )
        Spacer(Modifier.size(8.dp))
        HintWithIcon(
            leadingIcon = painterResource(Res.drawable.ic_online),
            hint = stringResource(Res.string.online_label),
            textColor = when (isOnline) {
                true -> MaterialTheme.colorScheme.tertiary
                false -> MaterialTheme.colorScheme.error
                null -> Color.Unspecified
            }
        )
    }
}
