package com.zeafen.petwalker.ui.channel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.commentary_input_string
import petwalker.composeapp.generated.resources.ic_attach
import petwalker.composeapp.generated.resources.ic_clear
import petwalker.composeapp.generated.resources.ic_send
import petwalker.composeapp.generated.resources.ic_text


@Composable
fun CommentaryDataInputField(
    modifier: Modifier = Modifier,
    inputString: String,
    isEditingMessage: Boolean,
    onCancelEditingClick: () -> Unit,
    onInputChanged: (String) -> Unit,
    attachments: List<Attachment>,
    onRemoveAttachment: (ref: String) -> Unit,
    onAddAttachmentClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
        ) {
            items(attachments) { attachment ->
                AttachmentCell(
                    attachment = attachment,
                    onRemoveClick = { onRemoveAttachment(attachment.reference) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 8.dp)
        ) {
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f),
                value = inputString,
                hint = stringResource(Res.string.commentary_input_string),
                onValueChanged = onInputChanged,
                leadingIcon = painterResource(Res.drawable.ic_text)
            )
            IconButton(
                onClick = onAddAttachmentClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_attach),
                    contentDescription = "Attach file"
                )
            }
            FilledIconButton(
                onClick = onSendClick
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "Attach file"
                )
            }
            if (isEditingMessage) {
                IconButton(
                    onClick = onCancelEditingClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clear),
                        contentDescription = "Cancel editing"
                    )
                }
            }
        }
    }
}