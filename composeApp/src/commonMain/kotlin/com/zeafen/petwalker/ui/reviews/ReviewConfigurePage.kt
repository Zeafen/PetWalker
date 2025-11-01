package com.zeafen.petwalker.ui.reviews

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
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
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureUiState
import com.zeafen.petwalker.presentation.standard.shapes.CommentaryBubbleShape
import com.zeafen.petwalker.ui.standard.effects.shimmerEffect
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.standard.elements.RatingRow
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import com.zeafen.petwalker.ui.standard.elements.UserInfoHeader
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_image_not_found
import petwalker.composeapp.generated.resources.text_label
import petwalker.composeapp.generated.resources.undefined_txt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewConfigurePage(
    modifier: Modifier = Modifier,
    state: ReviewConfigureUiState,
    onEvent: (ReviewConfigureUiEvent) -> Unit,
    onCancelClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    when (state.assignmentLoadingResult) {
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
                                    stringResource(state.assignmentLoadingResult.info.infoResource())
                                }: ${state.assignmentLoadingResult.additionalInfo}",
                                onReloadPage = { onEvent(ReviewConfigureUiEvent.ReloadReviewedAssignment) }
                            )
                        }

                        else -> {
                            AssignmentInfoHeader(
                                assignmentTitle = state.reviewedAssignmentTitle,
                                assignmentType = state.reviewedAssignmentType
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
        val deviceConfig =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

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
                    )
                    .clip(CommentaryBubbleShape(tipSize = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 16.dp, end = 12.dp),
                walkerFullName = state.currentUserName.ifEmpty { stringResource(Res.string.undefined_txt) },
                walkerImageUrl = state.currentUserImageUrl
            )
            Spacer(Modifier.height(24.dp))

            RatingRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                onStarClick = {
                    onEvent(ReviewConfigureUiEvent.SetReviewRating(it))
                },
                currentRating = state.reviewRating
            )
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f),
                value = state.reviewText,
                onValueChanged = { onEvent(ReviewConfigureUiEvent.SetReviewText(it)) },
                label = stringResource(Res.string.text_label),
                hint = stringResource(Res.string.description_input_hint),
                isError = !state.textValid.isValid,
                supportingText = if (!state.textValid.isValid)
                    state.textValid.errorResId?.let {
                        stringResource(it, *state.textValid.formatArgs.toTypedArray())
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
                    onClick = { onEvent(ReviewConfigureUiEvent.PublishReview) }
                )
            }
        }
    }
}


@Composable
fun AssignmentInfoHeader(
    modifier: Modifier = Modifier,
    assignmentTitle: String,
    assignmentType: ServiceType?
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp)),
            painter = painterResource(
                assignmentType?.displayImage ?: Res.drawable.ic_image_not_found
            ),
            contentDescription = "Assignment type"
        )
        Spacer(Modifier.width(12.dp))
        Text(
            modifier = Modifier
                .weight(4f),
            text = assignmentTitle,
            style = MaterialTheme.typography.titleLarge,
            maxLines = when (deviceConfig) {
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
            overflow = TextOverflow.Ellipsis
        )
    }
}