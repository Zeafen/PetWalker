package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.LocationInfo
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerServiceCard
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import com.zeafen.petwalker.ui.walkers.RatingInfoColumn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.about_me_label
import petwalker.composeapp.generated.resources.desired_payment_default_display_name
import petwalker.composeapp.generated.resources.distance_from_user_txt
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.ic_location_pin
import petwalker.composeapp.generated.resources.ic_message
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.ic_phone
import petwalker.composeapp.generated.resources.ic_refresh
import petwalker.composeapp.generated.resources.ic_service
import petwalker.composeapp.generated.resources.ic_verified
import petwalker.composeapp.generated.resources.ic_walk
import petwalker.composeapp.generated.resources.ic_wallet
import petwalker.composeapp.generated.resources.leave_review_btn_txt
import petwalker.composeapp.generated.resources.offline_label
import petwalker.composeapp.generated.resources.online_label
import petwalker.composeapp.generated.resources.recruiting_label
import petwalker.composeapp.generated.resources.repeating_orders_count_label
import petwalker.composeapp.generated.resources.services_label
import petwalker.composeapp.generated.resources.undefined_txt

@Composable
fun WalkerInfoTab(
    modifier: Modifier = Modifier,
    walker: Walker,
    distance: Float?,
    canReviewWalker: Boolean? = null,
    onLeaveReviewClick: (() -> Unit)? = null,
    onRecruitClick: (() -> Unit)? = null
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    val userInfoContent: @Composable () -> Unit = {
        PetWalkerAsyncImage(
            asyncImageModifier = Modifier
                .height(
                    when (deviceConfig) {
                        in listOf(
                            DeviceConfiguration.MOBILE_LANDSCAPE,
                            DeviceConfiguration.TABLET_PORTRAIT
                        ) -> 320.dp

                        in listOf(
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.DESKTOP
                        ) -> 400.dp

                        else -> 260.dp
                    }
                )
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp)),
            imageUrl = walker.imageUrl,
            asyncContentScale = ContentScale.Inside,
            defaultContentScale = ContentScale.Crop
        )
        WalkerPageHeader(
            walkerFirstName = walker.firstName,
            walkerLastName = walker.lastName,
            rating = walker.rating,
            reviewsCount = walker.reviewsCount,
            complaintCount = walker.complaintsCount,
            onLeaveReviewClick = if (canReviewWalker == true) onLeaveReviewClick else null
        )
    }
    Column(
        modifier = modifier
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        if (deviceConfig in listOf(
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.TABLET_PORTRAIT
            )
        ) Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userInfoContent()
        }
        else Row {
            userInfoContent()
        }


        Spacer(Modifier.height(24.dp))
        WalkerStatsColumn(
            walker = walker,
            distance = distance,
            maxItemsPerColumn = if (deviceConfig in listOf(
                    DeviceConfiguration.MOBILE_PORTRAIT,
                    DeviceConfiguration.TABLET_PORTRAIT
                )
            ) 2 else Int.MAX_VALUE
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 12.dp),
            thickness = 4.dp
        )
        WalkerContactInfoColumn(
            email = walker.email,
            phone = walker.phone,
            aboutMe = walker.aboutMe
        )
        onRecruitClick?.let {
            PetWalkerButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.9f),
                text = stringResource(Res.string.recruiting_label),
                trailingIcon = painterResource(Res.drawable.ic_location_pin),
                onClick = onRecruitClick
            )
        }
    }
}

@Composable
fun WalkerPageHeader(
    modifier: Modifier = Modifier,
    walkerFirstName: String,
    walkerLastName: String,
    rating: Float,
    complaintCount: Long,
    reviewsCount: Long,
    onLeaveReviewClick: (() -> Unit)?
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "$walkerFirstName $walkerLastName",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 4
        )
        Spacer(Modifier.height(12.dp))
        RatingInfoColumn(
            walkerRating = rating,
            walkerComplaintsCount = complaintCount,
            walkerReviewsCount = reviewsCount
        )
        Spacer(Modifier.height(12.dp))
        onLeaveReviewClick?.let {
            PetWalkerButton(
                text = stringResource(Res.string.leave_review_btn_txt),
                trailingIcon = painterResource(Res.drawable.ic_message),
                onClick = onLeaveReviewClick
            )
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WalkerStatsColumn(
    modifier: Modifier = Modifier,
    walker: Walker,
    distance: Float? = null,
    maxItemsPerColumn: Int = Int.MAX_VALUE,
) {
    FlowRow(
        modifier = modifier,
        maxItemsInEachRow = maxItemsPerColumn
    ) {
        HintWithIcon(
            modifier = Modifier
                .padding(8.dp),
            hint = stringResource(walker.accountStatus.displayName),
            leadingIcon = painterResource(Res.drawable.ic_verified),
            textColor = when (walker.accountStatus) {
                AccountStatus.Pending -> Color.Yellow
                AccountStatus.Verified -> Color.Green
                AccountStatus.Banned -> MaterialTheme.colorScheme.error
            }
        )
        HintWithIcon(
            modifier = Modifier
                .padding(8.dp),
            hint = "Desired payment: " +
                    stringResource(
                        walker.desiredPayment?.displayName
                            ?: Res.string.desired_payment_default_display_name
                    ),
            leadingIcon = painterResource(Res.drawable.ic_wallet),
            textStyle = MaterialTheme.typography.titleLarge,
        )


        walker.location?.address?.let {
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp),
                leadingIcon = painterResource(Res.drawable.ic_location_pin),
                hint = it
            )
        }

        distance?.let {
            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp),
                leadingIcon = painterResource(Res.drawable.ic_walk),
                hint = stringResource(
                    Res.string.distance_from_user_txt,
                    it.format(2)
                )
            )
        }

        HintWithIcon(
            modifier = Modifier
                .padding(8.dp),
            leadingIcon = painterResource(Res.drawable.ic_refresh),
            hint = stringResource(
                Res.string.repeating_orders_count_label,
                walker.repeatingOrdersCount
            )
        )

        HintWithIcon(
            modifier = Modifier
                .padding(8.dp),
            leadingIcon = painterResource(Res.drawable.ic_online),
            hint = stringResource(
                when (walker.isOnline) {
                    true -> Res.string.online_label
                    false -> Res.string.offline_label
                    null -> Res.string.undefined_txt
                }
            ),
            textColor = when (walker.isOnline) {
                true -> MaterialTheme.colorScheme.tertiary
                false -> MaterialTheme.colorScheme.error
                null -> Color.Unspecified
            }
        )
        ExpandableContent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            defaultContent = {
                HintWithIcon(
                    hint = stringResource(Res.string.services_label),
                    leadingIcon = painterResource(Res.drawable.ic_service),
                )
            },
            expandableContent = {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    items(walker.services) {
                        PetWalkerServiceCard(
                            modifier = Modifier
                                .widthIn(max = 256.dp)
                                .fillMaxRowHeight()
                                .padding(horizontal = 8.dp),
                            service = it
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun WalkerContactInfoColumn(
    modifier: Modifier = Modifier,
    email: String?,
    phone: String?,
    aboutMe: String?
) {
    Column(modifier = modifier) {
        email?.let {
            HintWithIcon(
                hint = it,
                leadingIcon = painterResource(Res.drawable.ic_attach_email)
            )
        }
        Spacer(Modifier.height(8.dp))
        phone?.let {
            HintWithIcon(
                hint = it,
                leadingIcon = painterResource(Res.drawable.ic_phone)
            )
        }
        Spacer(Modifier.height(16.dp))
        aboutMe?.let {
            ExpandableContent(
                defaultContent = {
                    HintWithIcon(
                        hint = stringResource(Res.string.about_me_label),
                        leadingIcon = painterResource(Res.drawable.ic_account_box)
                    )
                },
                expandableContent = {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
        }
    }
}