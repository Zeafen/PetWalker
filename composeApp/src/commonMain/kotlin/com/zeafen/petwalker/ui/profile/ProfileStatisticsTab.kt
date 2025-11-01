package com.zeafen.petwalker.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentsStats
import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintsStats
import com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import com.zeafen.petwalker.ui.walkers.walkerDetails.AssignmentsStatisticsSheet
import com.zeafen.petwalker.ui.walkers.walkerDetails.ComplaintsStatisticsSheet
import com.zeafen.petwalker.ui.walkers.walkerDetails.ReviewsStatisticsSheet
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignment_stats_label
import petwalker.composeapp.generated.resources.complaints_tab_display_name
import petwalker.composeapp.generated.resources.reviews_tab_display_name

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticsTab(
    modifier: Modifier = Modifier,
    assignmentsStats: APIResult<AssignmentsStats, Error>,
    onLoadAssignmentsStats: (DatePeriods) -> Unit,
    selectedAssignmentsStatsPeriod: DatePeriods,
    complaintsStats: APIResult<ComplaintsStats, Error>,
    reviewsStats: APIResult<ReviewsStats, Error>,
    onLoadReviewsStats: () -> Unit,
    onLoadComplaintsStats: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        ExpandableContent(
            defaultContent = {
                Text(
                    stringResource(Res.string.assignment_stats_label),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            expandableContent = {
                PullToRefreshBox(
                    isRefreshing = assignmentsStats is APIResult.Downloading,
                    onRefresh = { onLoadAssignmentsStats(selectedAssignmentsStatsPeriod) }
                ) {
                    when (assignmentsStats) {
                        is APIResult.Downloading -> CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .size(64.dp),
                            strokeWidth = 4.dp
                        )

                        is APIResult.Error -> ErrorInfoHint(
                            modifier = Modifier
                                .fillMaxWidth(),
                            errorInfo = "${
                                stringResource(assignmentsStats.info.infoResource())
                            }: ${assignmentsStats.additionalInfo}",
                            onReloadPage = { onLoadAssignmentsStats(selectedAssignmentsStatsPeriod) }
                        )

                        is APIResult.Succeed -> AssignmentsStatisticsSheet(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            assignmentsStats = assignmentsStats.data!!,
                            onChangeSelectedPeriod = {
                                it?.let {
                                    onLoadAssignmentsStats(it)
                                }
                            },
                            selectedDatePeriod = selectedAssignmentsStatsPeriod
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        ExpandableContent(
            defaultContent = {
                Text(
                    stringResource(Res.string.reviews_tab_display_name),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            expandableContent = {
                PullToRefreshBox(
                    isRefreshing = reviewsStats is APIResult.Downloading,
                    onRefresh = onLoadReviewsStats
                ) {
                    when (reviewsStats) {
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
                                    stringResource(reviewsStats.info.infoResource())
                                }: ${reviewsStats.additionalInfo}",
                                onReloadPage = onLoadReviewsStats
                            )

                        is APIResult.Succeed -> {
                            ReviewsStatisticsSheet(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                reviewsStats = reviewsStats.data!!
                            )
                        }
                    }
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        ExpandableContent(
            defaultContent = {
                Text(
                    stringResource(Res.string.complaints_tab_display_name),
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            expandableContent = {
                PullToRefreshBox(
                    isRefreshing = complaintsStats is APIResult.Downloading,
                    onRefresh = onLoadComplaintsStats
                ) {
                    when (complaintsStats) {
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
                                    stringResource(complaintsStats.info.infoResource())
                                }: ${complaintsStats.additionalInfo}",
                                onReloadPage = onLoadComplaintsStats
                            )

                        is APIResult.Succeed -> {
                            ComplaintsStatisticsSheet(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                complaintsStats = complaintsStats.data!!
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PetWalker_theme {
        Surface {
            ProfileStatisticsTab(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                assignmentsStats = APIResult.Succeed(
                    AssignmentsStats(
                        123L,
                        mapOf(
                            ServiceType.House_Sitting to 50f,
                            ServiceType.Boarding to 25f,
                            ServiceType.Walking to 12.5f,
                            ServiceType.Other to 6.25f,
                            ServiceType.Drop_In to 6.25f,
                        ),
                        123f,
                        mapOf(
                            ServiceType.House_Sitting to 50f,
                            ServiceType.Boarding to 25f,
                            ServiceType.Walking to 12.5f,
                            ServiceType.Other to 12.5f,
                            ServiceType.Drop_In to 23f,
                        ),
                        62.5f,
                        mapOf(
                            ServiceType.House_Sitting to 30f,
                            ServiceType.Boarding to 15f,
                            ServiceType.Walking to 7.5f,
                            ServiceType.Other to 5f,
                            ServiceType.Drop_In to 5f,
                        ),
                    )
                ),
                onLoadAssignmentsStats = {},
                selectedAssignmentsStatsPeriod = DatePeriods.All,
                reviewsStats = APIResult.Succeed(
                    ReviewsStats(
                        5f,
                        123L, mapOf(
                            5 to 50f,
                            4 to 25f,
                            3 to 12.5f,
                            2 to 6.25f,
                            1 to 6.25f
                        )
                    )
                ),
                complaintsStats = APIResult.Succeed(
                    ComplaintsStats(
                        123L,
                        12L,
                        23L,
                        mapOf(
                            ComplaintTopic.Toxic_Behaviour to 50f,
                            ComplaintTopic.Pet_Harm to 25f,
                            ComplaintTopic.Other to 25f,
                        )
                    )
                ),
                onLoadReviewsStats = {},
                onLoadComplaintsStats = {}
            )
        }
    }
}

