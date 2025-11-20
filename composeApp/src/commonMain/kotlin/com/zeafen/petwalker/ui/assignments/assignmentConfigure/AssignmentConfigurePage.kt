package com.zeafen.petwalker.ui.assignments.assignmentConfigure

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.window.core.layout.WindowWidthSizeClass
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureUiState
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PagedOptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDatePicker
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTimePicker
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.assign_pets_hint
import petwalker.composeapp.generated.resources.assignment_details_pages_header
import petwalker.composeapp.generated.resources.confirm_delete_assignment_label
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.description_label
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.ic_delete
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_image_not_found
import petwalker.composeapp.generated.resources.ic_save
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.required_label
import petwalker.composeapp.generated.resources.title_input_hint
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun AssignmentConfigurePage(
    modifier: Modifier = Modifier,
    state: AssignmentConfigureUiState,
    onEvent: (AssignmentConfigureUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    var openConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }
    var toastMsgTxt by remember {
        mutableStateOf<StringResource?>(null)
    }



    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.assignment_details_pages_header),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(32.dp),
                            painter = painterResource(Res.drawable.ic_go_back),
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { openConfirmDeleteDialog = true }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = "Delete assignment"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        val windowWidthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

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
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.Top
            ) {
                AnimatedContent(
                    modifier = Modifier
                        .size(
                            size = when (windowWidthSizeClass) {
                                WindowWidthSizeClass.MEDIUM -> 240.dp
                                WindowWidthSizeClass.EXPANDED -> 360.dp
                                else -> 120.dp
                            }
                        )
                        .clip(RoundedCornerShape(12.dp)),
                    targetState = state.assignmentType
                ) { currentType ->
                    Image(
                        painter = painterResource(
                            currentType?.displayImage ?: Res.drawable.ic_image_not_found
                        ),
                        contentDescription = "Service type",
                        contentScale = ContentScale.Crop
                    )
                }
                AssignmentTitleTopicInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp),
                    selectedType = state.assignmentType,
                    onSelectedTypeChanged = {
                        onEvent(
                            AssignmentConfigureUiEvent.SetAssignmentType(
                                it
                            )
                        )
                    },
                    title = state.assignmentTitle,
                    titleValidation = state.titleValidation,
                    onTitleChanged = { onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle(it)) }
                )
            }
            Spacer(Modifier.height(16.dp))

            PetWalkerDatePicker(
                currentDate = state.assignmentDate,
                isError = !state.dateValidation.isValid,
                errorString = state.dateValidation.errorResId?.let {
                    stringResource(it)
                },
                onDateChanged = { onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(it)) }
            )
            Spacer(Modifier.height(16.dp))

            PetWalkerTimePicker(
                currentTime = state.assignmentDate?.time,
                isError = !state.dateValidation.isValid,
                errorString = state.dateValidation.errorResId?.let {
                    stringResource(it)
                },
                onTimeChanged = { h, m ->
                    onEvent(
                        AssignmentConfigureUiEvent.SetAssignmentTime(h, m)
                    )
                }
            )
            Spacer(Modifier.height(16.dp))

            PagedOptionsSelectedInput(
                selectedOptions = state.assignedPets,
                availableOptions = state.availablePets,
                hint = stringResource(Res.string.assign_pets_hint),
                onOptionSelected = { onEvent(AssignmentConfigureUiEvent.AddAssignedPet(it)) },
                onOptionDeleted = { onEvent(AssignmentConfigureUiEvent.RemoveAssignedPet(it.id)) },
                onAvailableOptionsPageSelected = {
                    onEvent(
                        AssignmentConfigureUiEvent.LoadAvailablePets(
                            it
                        )
                    )
                },
                optionContent = {
                    Text(
                        modifier = Modifier
                            .widthIn(max = 128.dp),
                        text = it.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                expandedOptionContent = { pet ->
                    Column(
                        modifier = Modifier
                            .height(228.dp)
                            .widthIn(max = 228.dp)
                    ) {
                        PetWalkerAsyncImage(
                            asyncImageModifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth()
                                .fillMaxWidth(0.7f),
                            imageUrl = pet.imageUrl,
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, top = 12.dp),
                            text = pet.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
            Spacer(Modifier.height(12.dp))

            PetWalkerTextInput(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .fillMaxWidth(),
                value = state.assignmentDescription,
                isError = !state.descriptionValidation.isValid,
                supportingText = if (!state.descriptionValidation.isValid)
                    state.descriptionValidation.errorResId?.let {
                        stringResource(it, *state.descriptionValidation.formatArgs.toTypedArray())
                    }
                else null,
                onValueChanged = { onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription(it)) },
                label = stringResource(Res.string.description_label),
                hint = stringResource(Res.string.description_input_hint)
            )

            Spacer(Modifier.height(24.dp))
            PetWalkerButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .fillMaxWidth(0.7f),
                text = stringResource(Res.string.done_btn_txt),
                trailingIcon = painterResource(Res.drawable.ic_save),
                onClick = {
                    if (state.canPublish)
                        onEvent(AssignmentConfigureUiEvent.ApplyChanges)
                    else
                        toastMsgTxt = Res.string.conflict_error
                }
            )
        }
    }

    if (openConfirmDeleteDialog)
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_delete_assignment_label),
            onConfirm = { onEvent(AssignmentConfigureUiEvent.DeleteAssignment) },
            onDismissRequest = { openConfirmDeleteDialog = false }
        )

    if (toastMsgTxt != null) {
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { toastMsgTxt = null }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = stringResource(toastMsgTxt!!),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AssignmentTitleTopicInput(
    modifier: Modifier = Modifier,
    selectedType: ServiceType?,
    onSelectedTypeChanged: (ServiceType?) -> Unit,
    title: String,
    titleValidation: ValidationInfo,
    onTitleChanged: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        PetWalkerTextInput(
            value = title,
            isError = !titleValidation.isValid,
            supportingText = if (!titleValidation.isValid)
                titleValidation.errorResId?.let {
                    stringResource(it, *titleValidation.formatArgs.toTypedArray())
                }
            else null,
            onValueChanged = onTitleChanged,
            hint = stringResource(Res.string.title_input_hint),
            leadingIcon = painterResource(Res.drawable.ic_text),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OptionsSelectedInput(
            availableOptions = ServiceType.entries.toList(),
            selectedOptions = selectedType?.let { listOf(it) } ?: emptyList(),
            onOptionDeleted = { onSelectedTypeChanged(null) },
            onOptionSelected = { onSelectedTypeChanged(it) },
            hint = stringResource(Res.string.option_selection_hint),
            supportingText = if (selectedType == null) stringResource(Res.string.required_label) else null,
            optionContent = {
                Text(
                    text = stringResource(it.displayName),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
    }
}