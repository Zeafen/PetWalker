package com.zeafen.petwalker.ui.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureUiState
import com.zeafen.petwalker.presentation.standard.shapes.CommentaryBubbleShape
import com.zeafen.petwalker.ui.assignments.AssignmentCard
import com.zeafen.petwalker.ui.standard.effects.shimmerEffect
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PagedOptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import com.zeafen.petwalker.ui.standard.elements.UserInfoHeader
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignment_details_pages_header
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.text_label
import petwalker.composeapp.generated.resources.topic_label
import petwalker.composeapp.generated.resources.undefined_txt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintConfigurePage(
    modifier: Modifier = Modifier,
    state: ComplaintConfigureUiState,
    onEvent: (ComplaintConfigureUiEvent) -> Unit,
    onCancelClick: () -> Unit
) {
    var deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    when (state.reviewedWalkerLoadingRes) {
                        is APIResult.Downloading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .shimmerEffect()
                                        .padding(8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .shimmerEffect()
                                        .padding(8.dp)
                                )
                            }
                        }

                        is APIResult.Error -> {
                            ErrorInfoHint(
                                errorInfo = "${
                                    stringResource(state.reviewedWalkerLoadingRes.info.infoResource())
                                }: ${state.reviewedWalkerLoadingRes.additionalInfo}",
                                onReloadPage = { onEvent(ComplaintConfigureUiEvent.ReloadReviewedWalker) }
                            )
                        }

                        else -> {
                            UserInfoHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(
                                        max =
                                            when (deviceConfig) {
                                                in listOf(
                                                    DeviceConfiguration.MOBILE_PORTRAIT,
                                                    DeviceConfiguration.MOBILE_LANDSCAPE
                                                ) -> 140.dp

                                                DeviceConfiguration.TABLET_PORTRAIT -> 280.dp

                                                else -> 320.dp
                                            }
                                    ),
                                walkerFullName = state.reviewedWalkerName.ifEmpty {
                                    stringResource(
                                        Res.string.undefined_txt
                                    )
                                },
                                walkerImageUrl = state.reviewedWalkerImageUrl
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancelClick
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_go_back),
                            contentDescription = "Cancel"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .consumeWindowInsets(WindowInsets.systemBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CommentaryBubbleShape(tipSize = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 18.dp, end = 6.dp),
            ) {
                UserInfoHeader(
                    modifier = Modifier
                        .heightIn(
                            max =
                                when (deviceConfig) {
                                    in listOf(
                                        DeviceConfiguration.MOBILE_PORTRAIT,
                                        DeviceConfiguration.MOBILE_LANDSCAPE
                                    ) -> 140.dp

                                    DeviceConfiguration.TABLET_PORTRAIT -> 280.dp

                                    else -> 320.dp
                                }
                        ),
                    walkerFullName = state.currentUserName.ifEmpty { stringResource(Res.string.undefined_txt) },
                    walkerImageUrl = state.currentUserImageUrl
                )
                Spacer(Modifier.height(8.dp))
                PagedOptionsSelectedInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .padding(vertical = 12.dp),
                    selectedOptions = state.selectedAssignment?.let { listOf(it) } ?: emptyList(),
                    availableOptions = state.ownLoadedAssignments,
                    hint = stringResource(Res.string.option_selection_hint),
                    label = stringResource(Res.string.assignment_details_pages_header),
                    onOptionSelected = { onEvent(ComplaintConfigureUiEvent.SetSelectedAssignment(it.id)) },
                    onOptionDeleted = { onEvent(ComplaintConfigureUiEvent.SetSelectedAssignment(null)) },
                    onAvailableOptionsPageSelected = {
                        onEvent(
                            ComplaintConfigureUiEvent.LoadOwnAssignments(
                                it
                            )
                        )
                    },
                    optionContent = {
                        Text(
                            modifier = Modifier
                                .widthIn(max = 128.dp),
                            text = it.title,
                            style = MaterialTheme.typography.bodyLarge,

                            maxLines = when (deviceConfig) {
                                in listOf(
                                    DeviceConfiguration.TABLET_PORTRAIT,
                                    DeviceConfiguration.DESKTOP,
                                    DeviceConfiguration.TABLET_LANDSCAPE,
                                    DeviceConfiguration.MOBILE_LANDSCAPE,
                                ) -> 6

                                else -> 3
                            },
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    expandedOptionContent = {
                        AssignmentCard(
                            modifier = Modifier
                                .height(228.dp)
                                .widthIn(max = 228.dp),
                            assignment = it,
                            backgroundColor = if (state.selectedAssignment?.id == it.id)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
            OptionsSelectedInput(
                selectedOptions = state.complaintTopic?.let { listOf(it) } ?: emptyList(),
                availableOptions = ComplaintTopic.entries.toList(),
                hint = stringResource(Res.string.option_selection_hint),
                label = stringResource(Res.string.topic_label),
                onOptionSelected = { onEvent(ComplaintConfigureUiEvent.SetComplaintTopic(it)) },
                onOptionDeleted = { onEvent(ComplaintConfigureUiEvent.SetSelectedAssignment(null)) },
                optionContent = {
                    Text(
                        text = stringResource(it.displayName),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
            PetWalkerTextInput(
                modifier = Modifier
                    .heightIn(max = 300.dp),
                value = state.complaintDetails,
                onValueChanged = { onEvent(ComplaintConfigureUiEvent.SetComplaintDetails(it)) },
                label = stringResource(Res.string.text_label),
                hint = stringResource(Res.string.description_input_hint),
                isError = !state.detailsValid.isValid,
                supportingText = if (!state.detailsValid.isValid)
                    state.detailsValid.errorResId?.let {
                        stringResource(it, *state.detailsValid.formatArgs.toTypedArray())
                    } else null
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .fillMaxWidth(0.4f)
            ) {
                PetWalkerButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(Res.string.cancel_btn_txt),
                    containerColor = MaterialTheme.colorScheme.error,
                    onClick = onCancelClick
                )
                PetWalkerButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(Res.string.done_btn_txt),
                    enabled = state.canPublish,
                    onClick = { onEvent(ComplaintConfigureUiEvent.PublishComplaint) }
                )
            }
        }
    }
}