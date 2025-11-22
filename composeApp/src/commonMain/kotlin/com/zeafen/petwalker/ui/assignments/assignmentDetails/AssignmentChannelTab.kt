package com.zeafen.petwalker.ui.assignments.assignmentDetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiState
import com.zeafen.petwalker.ui.channel.MessageCard
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.channel_id_txt
import petwalker.composeapp.generated.resources.closed_label
import petwalker.composeapp.generated.resources.ic_closed
import petwalker.composeapp.generated.resources.ic_opened
import petwalker.composeapp.generated.resources.more_label
import petwalker.composeapp.generated.resources.opened_channel_label
import petwalker.composeapp.generated.resources.unread_messages_count_txt

@Composable
fun AssignmentChannelTab(
    modifier: Modifier = Modifier,
    state: AssignmentDetailsUiState,
    onEvent: (AssignmentDetailsUiEvent) -> Unit,
    onGoToChannelClick: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        when (state.assignmentChannel) {
            is APIResult.Downloading -> CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.3f)
            )

            is APIResult.Error -> ErrorInfoHint(
                errorInfo = "${
                    stringResource(state.assignmentChannel.info.infoResource())
                }: ${state.assignmentChannel.additionalInfo}",
                onReloadPage = { onEvent(AssignmentDetailsUiEvent.LoadChannel) }
            )

            is APIResult.Succeed -> {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(
                        Res.string.channel_id_txt,
                        state.assignmentChannel.data!!.id
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                HintWithIcon(
                    hint = stringResource(
                        if (state.assignmentChannel.data.isClosed) Res.string.closed_label
                        else Res.string.opened_channel_label
                    ),
                    leadingIcon = painterResource(
                        if (state.assignmentChannel.data.isClosed) Res.drawable.ic_closed
                        else Res.drawable.ic_opened
                    ),
                    textColor = if (state.assignmentChannel.data.isClosed) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(
                        Res.string.unread_messages_count_txt,
                        state.assignmentChannel.data.unreadMessagesCount
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )

                state.assignmentChannel.data.lastMessages?.let { messages ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(
                                BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                        reverseLayout = true
                    ) {
                        items(messages) { message ->
                            MessageCard(
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .fillMaxWidth()
                                    .wrapContentWidth(if (message.isOwn) Alignment.End else Alignment.Start)
                                    .fillMaxWidth(0.8f),
                                message = message,
                                onLoadAttachment = { ref, name ->
                                    onEvent(
                                        AssignmentDetailsUiEvent.LoadAttachmentData(
                                            ref,
                                            name
                                        )
                                    )
                                },
                            )
                        }
                        item {
                            PetWalkerLinkTextButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                text = stringResource(Res.string.more_label),
                                onClick = {
                                    state.selectedAssignmentId?.let {
                                        onGoToChannelClick(
                                            state.selectedAssignmentId
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}