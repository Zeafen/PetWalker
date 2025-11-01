package com.zeafen.petwalker.ui.assignments.recruitmentsPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.api.assignments.RecruitmentState
import com.zeafen.petwalker.domain.models.ui.RecruitmentModel
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import com.zeafen.petwalker.ui.standard.elements.UserInfoHeader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.accept_btn_txt
import petwalker.composeapp.generated.resources.decline_btn_txt
import petwalker.composeapp.generated.resources.ic_delete
import petwalker.composeapp.generated.resources.see_assignment_btn_txt
import petwalker.composeapp.generated.resources.see_walker_btn_txt
import petwalker.composeapp.generated.resources.undefined_txt

@Composable
fun RecruitmentCard(
    modifier: Modifier = Modifier,
    recruitment: RecruitmentModel,
    onSeeWalkerClick: (id: String) -> Unit,
    onSeeAssignmentClick: (String) -> Unit,
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        val deviceConf =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

        Box {
            Column {
                UserInfoHeader(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        )
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    walkerFullName = recruitment.senderName
                        ?: stringResource(Res.string.undefined_txt),
                    walkerImageUrl = recruitment.senderImageUrl
                )


                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    thickness = 4.dp
                )
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp),
                    text = recruitment.assignmentTitle?.let {
                        "${recruitment.assignmentTitle} - ${
                            recruitment.assignmentDateTime.toString()
                        }"
                    } ?: stringResource(Res.string.undefined_txt),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = when (deviceConf) {
                        in listOf(
                            DeviceConfiguration.TABLET_PORTRAIT,
                            DeviceConfiguration.DESKTOP,
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.MOBILE_LANDSCAPE,
                        ) -> 6

                        else -> 3
                    },
                )
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp),
                    text = stringResource(recruitment.state.displayName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = when (recruitment.state) {
                        RecruitmentState.Pending -> Color.Unspecified
                        RecruitmentState.Accepted -> MaterialTheme.colorScheme.tertiary
                        RecruitmentState.Denied -> MaterialTheme.colorScheme.error
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PetWalkerLinkTextButton(
                        text = stringResource(Res.string.see_walker_btn_txt),
                        onClick = { onSeeWalkerClick(recruitment.walkerId) }
                    )
                    PetWalkerLinkTextButton(
                        text = stringResource(Res.string.see_assignment_btn_txt),
                        onClick = { onSeeAssignmentClick(recruitment.assignmentId) }
                    )
                }
                if (!recruitment.outcoming) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        thickness = 4.dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PetWalkerButton(
                            text = stringResource(Res.string.accept_btn_txt),
                            onClick = onAcceptClick
                        )
                        PetWalkerButton(
                            text = stringResource(Res.string.decline_btn_txt),
                            onClick = onDeclineClick,
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            onDeleteClick?.let {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    onClick = onDeleteClick
                ) {
                    Icon(
                        painterResource(Res.drawable.ic_delete),
                        contentDescription = "Delete recruitment"
                    )
                }
            }
        }
    }
}