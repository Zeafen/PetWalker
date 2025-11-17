package com.zeafen.petwalker.ui.complaints

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.ui.ComplaintModel
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_delete
import petwalker.composeapp.generated.resources.ic_edit
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.more_label
import petwalker.composeapp.generated.resources.see_assignment_btn_txt


@Composable
fun ComplaintCard(
    modifier: Modifier = Modifier,
    complaint: ComplaintModel,
    onSeeAssignmentClick: (String) -> Unit,
    onEditComplaintClick: (() -> Unit)? = null,
    onDeleteComplaintClick: (() -> Unit)? = null
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
        )
    ) {
        if (complaint.isOwn) {
        FlowRow(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 12.dp
                )
        ) {
            onEditComplaintClick?.let {
                IconButton(
                    onClick = it
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }
            }
            onDeleteComplaintClick?.let {
                IconButton(
                    onClick = it
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
        ComplaintCardHeader(
            modifier = Modifier
                .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            senderImageUrl = complaint.senderImageUrl,
            senderFullName = complaint.senderFullName,
            status = complaint.status,
            dateSent = complaint.datePosted,
            dateSolved = complaint.dateSolved
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 12.dp),
            thickness = 4.dp
        )
        ComplaintCardBody(
            modifier = Modifier
                .padding(horizontal = 12.dp),
            topic = complaint.topic,
            body = complaint.text,
            onSeeAssignmentClick = if (!complaint.assignmentId.isNullOrBlank()) {
                { onSeeAssignmentClick(complaint.assignmentId) }
            } else null
        )
    }
}

@Composable
fun ComplaintCardHeader(
    modifier: Modifier = Modifier,
    senderImageUrl: String?,
    senderFullName: String,
    status: ComplaintStatus,
    dateSolved: LocalDateTime?,
    dateSent: LocalDateTime
) {
    Column(
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PetWalkerAsyncImage(
                imageUrl = senderImageUrl,
                asyncImageModifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
            )
            Column(
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = senderFullName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateSent.format(
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
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        HintWithIcon(
            modifier = Modifier,
            hint = stringResource(status.displayName) +
                    if (status == ComplaintStatus.Solved && dateSolved != null)
                        "(${
                            dateSolved.format(LocalDateTime.Format {
                                day()
                                char('/')
                                monthNumber()
                                char('/')
                                year()
                                char(' ')
                                hour()
                                char(':')
                                minute()
                            })
                        })" else "",
            leadingIcon = painterResource(Res.drawable.ic_online),
            textColor = when (status) {
                ComplaintStatus.Active -> MaterialTheme.colorScheme.error
                ComplaintStatus.Solved -> MaterialTheme.colorScheme.tertiary
                else -> Color.Unspecified
            }
        )
    }

}

@Composable
fun ComplaintCardBody(
    modifier: Modifier = Modifier,
    topic: ComplaintTopic,
    body: String,
    onSeeAssignmentClick: (() -> Unit)? = null
) {
    var expandDescription by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(topic.displayName),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        AnimatedContent(
            targetState = expandDescription
        ) { expandReview ->
            when {
                expandReview -> {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                    )
                }

                !expandReview -> {
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            onSeeAssignmentClick?.let {
                PetWalkerLinkTextButton(
                    text = stringResource(Res.string.see_assignment_btn_txt),
                    onClick = onSeeAssignmentClick
                )
            }
            PetWalkerLinkTextButton(
                text = stringResource(Res.string.more_label),
                onClick = { expandDescription = !expandDescription }
            )
        }
    }
}