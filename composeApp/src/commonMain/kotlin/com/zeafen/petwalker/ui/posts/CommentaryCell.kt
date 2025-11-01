package com.zeafen.petwalker.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.ui.CommentaryModel
import com.zeafen.petwalker.presentation.standard.shapes.CommentaryBubbleShape
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.child_commentaries_count_txt
import petwalker.composeapp.generated.resources.date_edited_txt

@Composable
fun CommentaryCell(
    modifier: Modifier = Modifier,
    commentary: CommentaryModel,
    onPlayAttachment: ((ref: String) -> Unit)? = null,
    onLoadAttachment: (ref: String, name: String) -> Unit,
    onOpenCommentaryRoot: (id: String) -> Unit
) {
    val imageAttachments = remember(commentary.attachments) {
        commentary.attachments.filter { it.type == AttachmentType.Image }
    }
    val attachments = remember(commentary.attachments) {
        commentary.attachments.filter { it.type != AttachmentType.Image }
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = commentary.senderName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start
        )
        Row {
            PetWalkerAsyncImage(
                asyncImageModifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                imageUrl = commentary.senderImageUrl
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CommentaryBubbleShape(tipSize = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 16.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = commentary.dateSent.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(12.dp))
                imageAttachments.forEach {
                    PetWalkerAsyncImage(
                        asyncImageModifier = Modifier
                            .heightIn(max = 200.dp),
                        imageUrl = it.reference,
                    )
                }
                commentary.text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                    )
                }
                attachments.forEach {
                    AttachmentCell(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp
                            ),
                        attachment = it,
                        onPlayAttachment = onPlayAttachment,
                        onLoadAttachment = onLoadAttachment
                    )
                }
                commentary.dateEdited?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = stringResource(
                            Res.string.date_edited_txt,
                            commentary.dateSent.toString()
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
        PetWalkerLinkTextButton(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                .fillMaxWidth(0.8f),
            text = stringResource(
                Res.string.child_commentaries_count_txt,
                commentary.amountChildCommentaries
            ),
            onClick = { onOpenCommentaryRoot(commentary.id) }
        )
    }
}
