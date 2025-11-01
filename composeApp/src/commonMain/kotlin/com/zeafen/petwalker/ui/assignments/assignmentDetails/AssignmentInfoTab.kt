package com.zeafen.petwalker.ui.assignments.assignmentDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiState
import com.zeafen.petwalker.ui.assignments.AssignmentCardHeader
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.default_description_txt
import petwalker.composeapp.generated.resources.description_label
import petwalker.composeapp.generated.resources.distance_from_user_txt
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.ic_payment
import petwalker.composeapp.generated.resources.ic_phone
import petwalker.composeapp.generated.resources.ic_service
import petwalker.composeapp.generated.resources.ic_status
import petwalker.composeapp.generated.resources.ic_time
import petwalker.composeapp.generated.resources.ic_walk
import petwalker.composeapp.generated.resources.payment_rubbles_txt
import petwalker.composeapp.generated.resources.recruiting_label
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
fun AssignmentInfoTab(
    modifier: Modifier = Modifier,
    state: AssignmentDetailsUiState,
    onEvent: (AssignmentDetailsUiEvent) -> Unit,
) {
    Column(modifier = modifier) {
        when (state.assignment) {
            is APIResult.Downloading -> CircularProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.4f)
                    .padding(top = 24.dp),
                strokeWidth = 4.dp
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(state.assignment.info.infoResource())
                }: ${state.assignment.additionalInfo}",
                onReloadPage = {
                    state.selectedAssignmentId?.let {
                        onEvent(AssignmentDetailsUiEvent.LoadAssignment(it))
                    }
                }
            )

            is APIResult.Succeed -> {
                AssignmentInfoBody(
                    assignment = state.assignment.data!!,
                    distance = state.distanceToAssignment,
                    canRecruit = state.canRecruit,
                    onRecruitClick = { onEvent(AssignmentDetailsUiEvent.RecruitToAssignment) }
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 12.dp),
            thickness = 4.dp
        )

        when (state.assignmentOwner) {
            is APIResult.Downloading -> CircularProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(state.assignmentOwner.info.infoResource())
                }: ${state.assignmentOwner.additionalInfo}",
                onReloadPage = { onEvent(AssignmentDetailsUiEvent.LoadOwner) }
            )

            is APIResult.Succeed -> {
                Column {
                    AssignmentCardHeader(
                        walkerFullName = "${state.assignmentOwner.data!!.firstName} ${state.assignmentOwner.data.lastName}",
                        walkerImageUrl = state.assignmentOwner.data.imageUrl,
                        assignmentPostedDate = if (state.assignment is APIResult.Succeed) state.assignment.data?.datePublished
                        else null
                    )
                    state.assignmentOwner.data.email?.let {
                        Spacer(Modifier.height(12.dp))
                        HintWithIcon(
                            hint = it,
                            leadingIcon = painterResource(Res.drawable.ic_attach_email)
                        )
                    }
                    state.assignmentOwner.data.phone?.let {
                        Spacer(Modifier.height(12.dp))
                        HintWithIcon(
                            hint = it,
                            leadingIcon = painterResource(Res.drawable.ic_phone)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun AssignmentInfoBody(
    modifier: Modifier = Modifier,
    assignment: Assignment,
    distance: Float?,
    canRecruit: Boolean,
    onRecruitClick: () -> Unit,
) {
    val deviceConf =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
    Column(modifier = modifier) {
        if (deviceConf != DeviceConfiguration.MOBILE_PORTRAIT)
            Row {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(
                            when (deviceConf) {
                                DeviceConfiguration.MOBILE_PORTRAIT -> 1f
                                else -> 0.3f
                            }
                        ),
                    painter = painterResource(assignment.type.displayImage),
                    contentDescription = "Assignment image",
                    contentScale = ContentScale.Crop
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = assignment.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = when (deviceConf) {
                        in listOf(
                            DeviceConfiguration.TABLET_PORTRAIT,
                            DeviceConfiguration.DESKTOP
                        ) -> Int.MAX_VALUE

                        in listOf(
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.MOBILE_LANDSCAPE,
                        ) -> 5

                        else -> 1
                    },
                    overflow = TextOverflow.Ellipsis
                )
            }
        else Column {
            Image(
                modifier = Modifier
                    .fillMaxWidth(
                        when (deviceConf) {
                            DeviceConfiguration.MOBILE_PORTRAIT -> 1f
                            else -> 0.3f
                        }
                    ),
                painter = painterResource(assignment.type.displayImage),
                contentDescription = "Assignment image",
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = assignment.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = when (deviceConf) {
                    in listOf(
                        DeviceConfiguration.TABLET_PORTRAIT,
                        DeviceConfiguration.DESKTOP
                    ) -> 10

                    in listOf(
                        DeviceConfiguration.TABLET_LANDSCAPE,
                        DeviceConfiguration.MOBILE_LANDSCAPE,
                    ) -> 6

                    else -> 3
                },
                overflow = TextOverflow.Ellipsis
            )
        }

        ExpandableContent(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            defaultContent = {
                Text(
                    text = stringResource(Res.string.description_label),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2
                )
            },
            expandableContent = {
                Text(
                    text = assignment.description
                        ?: stringResource(Res.string.default_description_txt),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        )
        Spacer(Modifier.height(12.dp))

        FlowRow {
            HintWithIcon(
                hint = stringResource(assignment.state.displayName),
                leadingIcon = painterResource(Res.drawable.ic_status),
                textStyle = MaterialTheme.typography.titleLarge,
                textColor = when (assignment.state) {
                    AssignmentState.In_Progress -> MaterialTheme.colorScheme.tertiary
                    AssignmentState.Completed -> MaterialTheme.colorScheme.primary
                    else -> null
                }
            )
            assignment.payment?.let {
                Spacer(Modifier.size(8.dp))
                HintWithIcon(
                    hint = stringResource(
                        Res.string.payment_rubbles_txt,
                        it.format(2)
                    ),
                    leadingIcon = painterResource(Res.drawable.ic_payment),
                    textStyle = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = stringResource(assignment.type.displayName),
                leadingIcon = painterResource(Res.drawable.ic_service)
            )

            distance?.let {
                Spacer(Modifier.size(12.dp))
                HintWithIcon(
                    hint = stringResource(
                        Res.string.distance_from_user_txt,
                        it.format(2)
                    ),
                    leadingIcon = painterResource(Res.drawable.ic_walk)
                )
            }

            Spacer(Modifier.size(12.dp))
            HintWithIcon(
                hint = assignment.dateTime.format(
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
                ),
                leadingIcon = painterResource(Res.drawable.ic_time)
            )
        }
        Spacer(Modifier.height(12.dp))
        if (canRecruit)
            PetWalkerButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.7f),
                text = stringResource(Res.string.recruiting_label),
                trailingIcon = painterResource(Res.drawable.ic_walk),
                onClick = onRecruitClick
            )
    }
}