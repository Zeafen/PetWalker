package com.zeafen.petwalker.ui.channel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.messaging.Message
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.presentation.standard.shapes.MessageBubbleShape
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_arrow_play
import petwalker.composeapp.generated.resources.ic_audio_file
import petwalker.composeapp.generated.resources.ic_calendar
import petwalker.composeapp.generated.resources.ic_check
import petwalker.composeapp.generated.resources.ic_clear
import petwalker.composeapp.generated.resources.ic_delete
import petwalker.composeapp.generated.resources.ic_document
import petwalker.composeapp.generated.resources.ic_edit
import petwalker.composeapp.generated.resources.ic_save
import petwalker.composeapp.generated.resources.ic_video_file
import petwalker.composeapp.generated.resources.sent

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageCard(
    modifier: Modifier = Modifier,
    message: Message,
    onLoadAttachment: (ref: String, name: String) -> Unit,
    onPlayAttachment: ((ref: String) -> Unit)? = null,
    onEditMessageClick: (() -> Unit)? = null,
    onDeleteMessageClick: (() -> Unit)? = null
) {
    val imageAttachments = remember(message.attachments) {
        message.attachments.filter { it.type == AttachmentType.Image }
    }
    val attachments = remember(message.attachments) {
        message.attachments.filter { it.type != AttachmentType.Image }
    }

    Card(
        modifier = modifier,
        shape = MessageBubbleShape(isOwn = message.isOwn),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp, pressedElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isOwn) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        if (message.isOwn) {
            FlowRow(
                modifier = Modifier
                    .padding(
                        start = if (message.isOwn) 0.dp else 16.dp,
                        end = if (message.isOwn) 16.dp else 0.dp
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    )
            ) {
                onEditMessageClick?.let {
                    IconButton(
                        onClick = it
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_edit),
                            contentDescription = "Edit"
                        )
                    }
                }
                onDeleteMessageClick?.let {
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

        message.body?.let {
            Text(
                modifier = Modifier
                    .padding(
                        start = if (message.isOwn) 0.dp else 16.dp,
                        end = if (message.isOwn) 16.dp else 0.dp
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    ),
                text = message.body,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            imageAttachments.forEach {
                PetWalkerAsyncImage(
                    asyncImageModifier = Modifier
                        .heightIn(max = 200.dp)
                        .padding(8.dp),
                    imageUrl = it.reference,
                )
            }
        }
        attachments.forEach {
            AttachmentCell(
                modifier = Modifier
                    .padding(
                        start = if (message.isOwn) 0.dp else 16.dp,
                        end = if (message.isOwn) 16.dp else 0.dp
                    ),
                attachment = it,
                onPlayAttachment = onPlayAttachment,
                onLoadAttachment = onLoadAttachment
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (message.isOwn) 0.dp else 16.dp,
                    end = if (message.isOwn) 16.dp else 0.dp
                ),
            contentAlignment = if (message.isOwn) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            Row {
                HintWithIcon(
                    hint = (message.dateEdited ?: message.dateSent).format(
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
                    leadingIcon = if (message.dateEdited != null) painterResource(Res.drawable.ic_edit) else painterResource(
                        Res.drawable.ic_calendar
                    )
                )
                Spacer(Modifier.width(12.dp))
                if (message.isRead)
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(Res.drawable.sent),
                        contentDescription = "Read"
                    )
                else
                    Icon(
                        painter = painterResource(Res.drawable.ic_check),
                        contentDescription = "Not read"
                    )

            }
        }
    }
}

@Composable
fun AttachmentCell(
    modifier: Modifier = Modifier,
    attachment: Attachment,
    onPlayAttachment: ((ref: String) -> Unit)? = null,
    onLoadAttachment: ((ref: String, name: String) -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp),
            painter = painterResource(
                when (attachment.type) {
                    AttachmentType.Video -> Res.drawable.ic_video_file
                    AttachmentType.Audio -> Res.drawable.ic_audio_file
                    else -> Res.drawable.ic_document
                }
            ),
            contentDescription = "File"
        )
        Text(
            modifier = Modifier
                .weight(1f),
            text = attachment.name,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        onPlayAttachment?.let {
            if (attachment.type != AttachmentType.Document)
                IconButton(
                    onClick = { onPlayAttachment(attachment.reference) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(32.dp),
                        painter = painterResource(Res.drawable.ic_arrow_play),
                        contentDescription = "Play"
                    )
                }
        }
        onLoadAttachment?.let {
            IconButton(
                onClick = { onLoadAttachment(attachment.reference, attachment.name) }
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp),
                    painter = painterResource(Res.drawable.ic_save),
                    contentDescription = "Play"
                )
            }
        }
        onRemoveClick?.let {
            IconButton(
                onClick = onRemoveClick
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp),
                    painter = painterResource(Res.drawable.ic_clear),
                    contentDescription = "Remove"
                )
            }
        }
    }
}
