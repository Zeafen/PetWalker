package com.zeafen.petwalker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.home.HomePageUiEvent
import com.zeafen.petwalker.presentation.home.HomePageUiState
import com.zeafen.petwalker.ui.standard.effects.newsDot
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.LogoWithHeaderSlogan
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import com.zeafen.petwalker.ui.walkers.WalkerCard
import com.zeafen.petwalker.ui.walkers.walkerDetails.ReviewsStatisticsSheet
import com.zeafen.petwalker.ui.walkers.walkerDetails.WalkerStatsColumn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.app_name
import petwalker.composeapp.generated.resources.assignments_page_header
import petwalker.composeapp.generated.resources.best_walker_tab_display_name
import petwalker.composeapp.generated.resources.featured_walkers_tab_display_name
import petwalker.composeapp.generated.resources.ic_assignment
import petwalker.composeapp.generated.resources.ic_message
import petwalker.composeapp.generated.resources.ic_pet
import petwalker.composeapp.generated.resources.ic_send
import petwalker.composeapp.generated.resources.ic_walk
import petwalker.composeapp.generated.resources.img
import petwalker.composeapp.generated.resources.loading_error_txt
import petwalker.composeapp.generated.resources.login_page_slogan
import petwalker.composeapp.generated.resources.own_assignments_page_header
import petwalker.composeapp.generated.resources.walkers_page_header

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    state: HomePageUiState,
    onEvent: (HomePageUiEvent) -> Unit,
    onGoToBestWalker: (id: String) -> Unit,
    onGoToAssignments: () -> Unit,
    onGoToOwnAssignments: () -> Unit,
    onGoToWalkers: () -> Unit,
    onGoToRecruitmentsAsOwner: () -> Unit,
    onGoToRecruitmentsAsWalker: () -> Unit,
    onGoToOwnPets: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .consumeWindowInsets(WindowInsets.systemBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            LogoWithHeaderSlogan(
                modifier = Modifier
                    .fillMaxWidth(),
                header = stringResource(Res.string.app_name),
                headerAlignment = TextAlign.Center,
                slogan = stringResource(Res.string.login_page_slogan)
            )
            Spacer(Modifier.height(24.dp))

            FlowRow(
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center
            ) {

                NavigationPair(
                    modifier = Modifier
                        .heightIn(min = 64.dp)
                        .fillMaxRowHeight()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    largeBtnText = stringResource(Res.string.own_assignments_page_header),
                    largeBtnIcon = painterResource(Res.drawable.ic_assignment),
                    largeBtnContainerColor = MaterialTheme.colorScheme.tertiary,
                    iconBtnContent = painterResource(Res.drawable.ic_pet),
                    iconBtnHasNews = false,
                    largeBtnHasNews = false,
                    onLargeBtnClick = onGoToOwnAssignments,
                    onIconBtnClick = onGoToOwnPets
                )
                NavigationPair(
                    modifier = Modifier
                        .heightIn(min = 64.dp)
                        .fillMaxRowHeight()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    isReversed = true,
                    largeBtnText = stringResource(Res.string.walkers_page_header),
                    largeBtnIcon = painterResource(Res.drawable.ic_walk),
                    largeBtnContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    iconBtnContent = painterResource(Res.drawable.ic_message),
                    iconBtnHasNews = state.hasNewRecruitmentsAsWalker,
                    largeBtnHasNews = false,
                    onLargeBtnClick = onGoToWalkers,
                    onIconBtnClick = onGoToRecruitmentsAsWalker
                )
                NavigationPair(
                    modifier = Modifier
                        .heightIn(min = 64.dp)
                        .fillMaxRowHeight()
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    largeBtnText = stringResource(Res.string.assignments_page_header),
                    largeBtnIcon = painterResource(Res.drawable.ic_assignment),
                    iconBtnContent = painterResource(Res.drawable.ic_send),
                    iconBtnHasNews = state.hasNewRecruitmentsAsOwner,
                    largeBtnHasNews = false,
                    onLargeBtnClick = onGoToAssignments,
                    onIconBtnClick = onGoToRecruitmentsAsOwner
                )
            }
            Spacer(Modifier.height(24.dp))

            PetWalkerLinkTextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.7f),
                text = stringResource(Res.string.best_walker_tab_display_name),
                style = MaterialTheme.typography.titleLarge,
                onClick = {
                    if (state.bestWalker is APIResult.Succeed)
                        state.bestWalker.data?.let {
                            onGoToBestWalker(it.id)
                        }
                }
            )
            when {
                state.bestWalker is APIResult.Downloading
                        || state.bestWalkerStatistics is APIResult.Downloading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .size(128.dp),
                        strokeWidth = 4.dp
                    )
                }

                state.bestWalker is APIResult.Error
                        || state.bestWalkerStatistics is APIResult.Error -> ErrorInfoHint(
                    errorInfo = when {
                        state.bestWalker is APIResult.Error -> "${
                            stringResource(state.bestWalker.info.infoResource())
                        }: ${state.bestWalker.additionalInfo}"

                        state.bestWalkerStatistics is APIResult.Error -> "${
                            stringResource(state.bestWalkerStatistics.info.infoResource())
                        }: ${state.bestWalkerStatistics.additionalInfo}"

                        else -> stringResource(Res.string.loading_error_txt)
                    },
                    onReloadPage = { onEvent(HomePageUiEvent.ReloadBestWalker) }
                )

                state.bestWalker is APIResult.Succeed
                        && state.bestWalkerStatistics is APIResult.Succeed -> {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp),
                    ) {
                        WalkerStatsColumn(
                            modifier = Modifier
                                .padding(end = 8.dp),
                            walker = state.bestWalker.data!!
                        )
                        Spacer(Modifier.height(16.dp))
                        BestWalkerInfoCell(
                            modifier = Modifier.fillMaxWidth(),
                            walker = state.bestWalker.data,
                            reviewsStats = state.bestWalkerStatistics.data!!
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))


            Text(
                text = stringResource(Res.string.featured_walkers_tab_display_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow {
                if (state.isLoadingWalkers && !state.isLoadingForward)
                    item {
                        CircularProgressIndicator(
                            Modifier
                                .size(64.dp),
                            strokeWidth = 4.dp
                        )
                    }

                items(
                    state.featuredWalkers.size,
                    key = { state.featuredWalkers[it].id }
                ) { index ->
                    val walker = state.featuredWalkers[index]

                    if (index > state.featuredWalkers.size - 1 && !state.maxWalkersPagesReached && !state.isLoadingWalkers)
                        onEvent(HomePageUiEvent.LoadBestWalkers(state.currentWalkersPagesComb.second + 1))
                    else if (walker.id == state.featuredWalkers.first().id
                        && state.currentWalkersPagesComb.first > 1
                        && !state.isLoadingWalkers
                    )
                        onEvent(HomePageUiEvent.LoadBestWalkers(state.currentWalkersPagesComb.first - 1))

                    WalkerCard(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(horizontal = 8.dp),
                        walker = walker,
                        distance = null
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationPair(
    modifier: Modifier = Modifier,
    isReversed: Boolean = false,
    largeBtnText: String,
    largeBtnIcon: Painter,
    largeBtnContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    largeBtnHasNews: Boolean,
    iconBtnContent: Painter,
    iconBtnHasNews: Boolean,
    onLargeBtnClick: () -> Unit,
    onIconBtnClick: () -> Unit,
) {
    val dotColor = MaterialTheme.colorScheme.error
    val largeBtnModifier = remember(largeBtnHasNews) {
        if (largeBtnHasNews)
            Modifier
                .fillMaxHeight()
                .newsDot(dotColor)
                .clip(RoundedCornerShape(12.dp))
                .background(largeBtnContainerColor)
                .clickable { onLargeBtnClick() }
                .padding(vertical = 4.dp, horizontal = 8.dp)
        else Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(largeBtnContainerColor)
            .clickable { onLargeBtnClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isReversed) {
            HintWithIcon(
                modifier = largeBtnModifier,
                hint = largeBtnText,
                leadingIcon = largeBtnIcon,
                textColor = MaterialTheme.colorScheme.contentColorFor(largeBtnContainerColor)
            )
            Spacer(Modifier.width(4.dp))
        }

        FilledIconButton(
            modifier = if (iconBtnHasNews)
                Modifier
                    .size(64.dp)
                    .newsDot(dotColor)
            else Modifier
                .size(64.dp),
            onClick = onIconBtnClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = iconBtnContent,
                contentDescription = null
            )
        }

        if (isReversed) {
            Spacer(Modifier.width(4.dp))
            HintWithIcon(
                modifier = largeBtnModifier,
                hint = largeBtnText,
                leadingIcon = largeBtnIcon,
                textColor = MaterialTheme.colorScheme.contentColorFor(largeBtnContainerColor)
            )
        }
    }
}

@Composable
fun BestWalkerInfoCell(
    modifier: Modifier = Modifier,
    walker: Walker,
    reviewsStats: ReviewsStats
) {
    var deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(3.5f)
        ) {
            Text(
                text = "${walker.firstName} ${walker.lastName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(12.dp))
            ReviewsStatisticsSheet(
                modifier = Modifier.fillMaxWidth(),
                reviewsStats = reviewsStats
            )
        }
        PetWalkerAsyncImage(
            asyncImageModifier = Modifier
                .heightIn(
                    max = when (deviceConfig) {
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
                .weight(3f),
            imageUrl = walker.imageUrl,
            defaultImage = painterResource(Res.drawable.img)
        )
    }
}