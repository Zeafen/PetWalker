package com.zeafen.petwalker.ui.assignments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.UserInfoHeader
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.distance_from_user_txt
import petwalker.composeapp.generated.resources.ic_edit
import petwalker.composeapp.generated.resources.ic_service
import petwalker.composeapp.generated.resources.ic_time
import petwalker.composeapp.generated.resources.ic_walk
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun AssignmentCard(
    modifier: Modifier = Modifier,
    assignment: AssignmentModel,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (assignmentId: String) -> Unit,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(backgroundColor)
        ),
        onClick = { onClick(assignment.id) }
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                AssignmentCardHeader(
                    modifier = Modifier
                        .fillMaxWidth(),
                    walkerFullName = assignment.ownerName,
                    walkerImageUrl = assignment.ownerImageUrl,
                    assignmentPostedDate = assignment.datePublished
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    thickness = 4.dp
                )

                AssignmentCardBody(
                    assignment = assignment
                )

            }
            onEditClick?.let {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp),
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit Assignment"
                    )
                }
            }
        }
    }
}

@Composable
fun AssignmentCard(
    modifier: Modifier = Modifier,
    assignment: AssignmentModel,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(backgroundColor)
        ),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                AssignmentCardHeader(
                    modifier = Modifier
                        .fillMaxWidth(),
                    walkerFullName = assignment.ownerName,
                    walkerImageUrl = assignment.ownerImageUrl,
                    assignmentPostedDate = assignment.datePublished
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 12.dp),
                    thickness = 4.dp
                )

                AssignmentCardBody(
                    assignment = assignment
                )
            }
            onEditClick?.let {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp),
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit Assignment"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun AssignmentCardHeader(
    modifier: Modifier = Modifier,
    walkerFullName: String,
    walkerImageUrl: String?,
    assignmentPostedDate: LocalDateTime?
) {
    Column(modifier = modifier) {
        val deviceConf =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
        UserInfoHeader(
            modifier = Modifier
                .heightIn(
                    max =
                        when (deviceConf) {
                            in listOf(
                                DeviceConfiguration.MOBILE_PORTRAIT,
                                DeviceConfiguration.MOBILE_LANDSCAPE
                            ) -> 140.dp

                            DeviceConfiguration.TABLET_PORTRAIT -> 280.dp

                            else -> 320.dp
                        }
                ),
            walkerFullName = walkerFullName,
            walkerImageUrl = walkerImageUrl
        )
        assignmentPostedDate?.let {
            Text(
                text = assignmentPostedDate.format(
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
                )
            )
        }
    }
}

@Composable
fun AssignmentCardBody(
    modifier: Modifier = Modifier,
    assignment: AssignmentModel
) {
    Column(
        modifier = modifier,
    ) {
        val deviceConf =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            text = assignment.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Ellipsis,
            maxLines = when (deviceConf) {
                in listOf(
                    DeviceConfiguration.TABLET_PORTRAIT,
                    DeviceConfiguration.DESKTOP
                ) -> 12

                in listOf(
                    DeviceConfiguration.TABLET_LANDSCAPE,
                    DeviceConfiguration.MOBILE_LANDSCAPE,
                ) -> 6

                else -> 3
            },
            fontWeight = FontWeight.SemiBold
        )
        FlowRow {
            HintWithIcon(
                hint = stringResource(assignment.type.displayName),
                leadingIcon = painterResource(Res.drawable.ic_service)
            )
            Spacer(Modifier.size(8.dp))
            HintWithIcon(
                hint = assignment.dateTime.toString(),
                leadingIcon = painterResource(Res.drawable.ic_time)
            )
            assignment.distance?.let {
                Spacer(Modifier.size(8.dp))
                HintWithIcon(
                    hint = stringResource(
                        Res.string.distance_from_user_txt,
                        it.format(2)
                    ),
                    leadingIcon = painterResource(Res.drawable.ic_walk)
                )
            }
        }
    }
}
