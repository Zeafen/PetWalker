package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.ReviewCardModel
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiState
import com.zeafen.petwalker.ui.reviews.ReviewCard
import com.zeafen.petwalker.ui.reviews.ReviewsFilteringDialog
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_filter
import petwalker.composeapp.generated.resources.ic_star
import petwalker.composeapp.generated.resources.reviews_count_txt

@Composable
fun WalkerReviewsTab(
    modifier: Modifier = Modifier,
    state: WalkerDetailsPageUiState,
    onGoToAssignmentClick: (String) -> Unit,
    onEvent: (WalkerDetailsPageUiEvent) -> Unit
) {
    var openFiltersDialog by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        when (state.walkerReviewsStats) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.3f),
                strokeWidth = 4.dp
            )

            is APIResult.Error ->
                ErrorInfoHint(
                    modifier = Modifier
                        .fillMaxWidth(),
                    errorInfo = "${
                        stringResource(state.walkerReviewsStats.info.infoResource())
                    }: ${state.walkerReviewsStats.additionalInfo}",
                    onReloadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews()) }
                )

            is APIResult.Succeed -> {
                ReviewsStatisticsSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    reviewsStats = state.walkerReviewsStats.data!!
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        onClick = { openFiltersDialog = true }) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_filter),
                            contentDescription = "Filters"
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp),
            thickness = 4.dp
        )
        ReviewsList(
            reviews = state.walkerReviews,
            onLoadPage = { onEvent(WalkerDetailsPageUiEvent.LoadWalkerReviews(it)) },
            onGoToAssignmentClick = onGoToAssignmentClick
        )
    }

    if (openFiltersDialog)
        ReviewsFilteringDialog(
            onDismissRequest = { openFiltersDialog = false },
            onClearClick = {},
            onDoneClick = { positive, period ->
                onEvent(
                    WalkerDetailsPageUiEvent.SetWalkerReviewsFilters(
                        positive,
                        period
                    )
                )
            }
        )
}


@Composable
fun ReviewsStatisticsSheet(
    modifier: Modifier = Modifier,
    reviewsStats: ReviewsStats
) {
    Column(modifier = modifier) {
        ReviewsStatsSheetHeader(
            rating = reviewsStats.rating,
            reviewsCount = reviewsStats.totalReviewsCount
        )
        Spacer(Modifier.height(12.dp))
        reviewsStats.ratingPercentage.keys.sortedDescending().forEach { rating ->
            StatProgress(
                option = rating.toString(),
                percentage = reviewsStats.ratingPercentage[rating]!!
            )
        }
    }
}

@Composable
fun ReviewsStatsSheetHeader(
    modifier: Modifier = Modifier,
    rating: Float,
    reviewsCount: Long
) {
    Column(modifier = modifier) {
        HintWithIcon(
            hint = rating.format(2),
            leadingIcon = painterResource(Res.drawable.ic_star),
            textStyle = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(Res.string.reviews_count_txt, reviewsCount),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun StatProgress(
    modifier: Modifier = Modifier,
    option: String,
    percentage: Float
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Light
        )
        LinearProgressIndicator(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .clip(CircleShape),
            gapSize = 0.dp,
            strokeCap = StrokeCap.Square,
            progress = { percentage / 100f }
        )
        Text(
            modifier = Modifier
                .width(64.dp),
            text = percentage.format(2),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ReviewsList(
    modifier: Modifier = Modifier,
    reviews: APIResult<PagedResult<ReviewCardModel>, Error>,
    onLoadPage: (page: Int) -> Unit,
    onGoToAssignmentClick: (String) -> Unit
) {
    when (reviews) {
        is APIResult.Downloading -> CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            strokeWidth = 4.dp
        )

        is APIResult.Error ->
            ErrorInfoHint(
                modifier = Modifier
                    .fillMaxWidth(),
                errorInfo = "${
                    stringResource(reviews.info.infoResource())
                }: ${reviews.additionalInfo}",
                onReloadPage = { onLoadPage(0) }
            )


        is APIResult.Succeed -> {
            LazyVerticalStaggeredGrid(
                modifier = modifier,
                columns = StaggeredGridCells.Adaptive(minSize = 250.dp)
            ) {
                items(reviews.data!!.result, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        onGoToAssignmentClick = onGoToAssignmentClick
                    )
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    PageSelectionRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        totalPages = reviews.data.totalPages,
                        currentPage = reviews.data.currentPage,
                        onPageClick = onLoadPage
                    )
                }
            }
        }
    }
}