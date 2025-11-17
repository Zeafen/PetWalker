package com.zeafen.petwalker.ui.pets.petConfigure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureUiEvent
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureUiState
import com.zeafen.petwalker.ui.pets.EditableMedicalInfoCell
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDatePicker
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.add_btn_txt
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.breed_label
import petwalker.composeapp.generated.resources.breed_search_field_hint
import petwalker.composeapp.generated.resources.confirm_delete_pet_label
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.description_label
import petwalker.composeapp.generated.resources.float_input_hint
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_app
import petwalker.composeapp.generated.resources.ic_delete
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_send
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.medical_info_label
import petwalker.composeapp.generated.resources.name_label
import petwalker.composeapp.generated.resources.name_search_field_hint
import petwalker.composeapp.generated.resources.pet_details_page_header
import petwalker.composeapp.generated.resources.species_label
import petwalker.composeapp.generated.resources.species_search_field_hint
import petwalker.composeapp.generated.resources.weight_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetConfigurePage(
    modifier: Modifier = Modifier,
    state: PetConfigureUiState,
    onEvent: (PetConfigureUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    var openMedicalInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var openConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }
    var popupContent by rememberSaveable {
        mutableStateOf<StringResource?>(null)
    }
    val documentPicker = rememberDocumentPicker { fileInfo ->
        fileInfo?.let {
            onEvent(PetConfigureUiEvent.SetPetImage(it))
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.pet_details_page_header),
                        style = MaterialTheme.typography.headlineLarge,
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
                            contentDescription = "Delete pet"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (state.canPublish)
                                onEvent(PetConfigureUiEvent.PublishData)
                            else {
                                popupContent = Res.string.conflict_error
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = "Go back"
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
            PetHeaderInput(
                modifier = Modifier
                    .heightIn(
                        max = when (deviceConfig) {
                            in listOf(
                                DeviceConfiguration.MOBILE_LANDSCAPE,
                                DeviceConfiguration.TABLET_PORTRAIT
                            ) -> 600.dp

                            in listOf(
                                DeviceConfiguration.TABLET_LANDSCAPE,
                                DeviceConfiguration.DESKTOP
                            ) -> 650.dp

                            else -> 500.dp
                        }
                    ),
                petName = state.petName,
                nameValidation = state.nameValidation,
                petSpecies = state.petSpecies,
                speciesValidation = state.speciesValidation,
                petBreed = state.petBreed,
                breedValidation = state.breedValidation,
                petImageUri = state.petImageUri?.toString(),
                onNameChanged = { onEvent(PetConfigureUiEvent.SetPetName(it)) },
                onSpeciesChanged = { onEvent(PetConfigureUiEvent.SetPetSpecies(it)) },
                onBreedChanged = { onEvent(PetConfigureUiEvent.SetPetBreed(it)) },
                onImageClick = {
                    documentPicker.launch(ExtensionGroups.Image.exts)
                }
            )
            Spacer(Modifier.height(12.dp))
            PetStatsInput(
                petWeight = state.petWeight,
                weightValidation = state.weightValidation,
                petBirthDate = state.petDateBirth,
                birthDateValidation = state.date_birthValidation,
                petDescription = state.petDesc,
                onWeightChanged = { onEvent(PetConfigureUiEvent.SetPetWeight(it)) },
                onBirthDateChanged = { onEvent(PetConfigureUiEvent.SetPetDateBirth(it)) },
                onDescriptionChanged = { onEvent(PetConfigureUiEvent.SetPetDescription(it)) }
            )
            Spacer(Modifier.height(12.dp))
            MedicalInfoSheet(
                petMedicalInfos = state.petMedicalInfos,
                onEditClick = {
                    onEvent(PetConfigureUiEvent.SelectMedicalInfo(it))
                    openMedicalInfoDialog = true
                },
                onDeleteClick = { onEvent(PetConfigureUiEvent.RemovePetMedialInfo(it)) },
                onAddInfoClick = {
                    onEvent(PetConfigureUiEvent.SelectMedicalInfo(null))
                    openMedicalInfoDialog = true
                }
            )

            if (openMedicalInfoDialog)
                MedicalInfoConfigureDialog(
                    medicalInfo = state.selectedMedicalInfo,
                    onDoneClick = { type, name, desc, doc ->
                        if (state.selectedMedicalInfo != null)
                            onEvent(
                                PetConfigureUiEvent.EditMedicalInfo(
                                    state.selectedMedicalInfo.id,
                                    type,
                                    name,
                                    desc,
                                    doc
                                )
                            )
                        else
                            onEvent(PetConfigureUiEvent.AddPetMedialInfo(type, name, desc, doc))
                    },
                    onDismissRequest = {
                        onEvent(PetConfigureUiEvent.SelectMedicalInfo(null))
                        openMedicalInfoDialog = false
                    },
                )
        }
    }

    if (openConfirmDeleteDialog)
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_delete_pet_label),
            onConfirm = { onEvent(PetConfigureUiEvent.DeletePet) },
            onDismissRequest = { openConfirmDeleteDialog = false }
        )

    if (popupContent != null)
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { popupContent = null }
        ) {
            Text(
                text = stringResource(popupContent!!),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
}

@Composable
fun PetHeaderInput(
    modifier: Modifier = Modifier,
    petName: String,
    nameValidation: ValidationInfo,
    petSpecies: String,
    speciesValidation: ValidationInfo,
    petBreed: String,
    breedValidation: ValidationInfo,
    petImageUri: String?,
    onImageClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSpeciesChanged: (String) -> Unit,
    onBreedChanged: (String) -> Unit
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        PetWalkerAsyncImage(
            asyncImageModifier = Modifier
                .weight(2f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onImageClick() },
            imageUrl = petImageUri
        )
        Spacer(Modifier.width(8.dp))

        if (deviceConfig == DeviceConfiguration.MOBILE_PORTRAIT || deviceConfig == DeviceConfiguration.TABLET_PORTRAIT)
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                PetWalkerTextInput(
                    value = petName,
                    singleLine = true,
                    isError = !nameValidation.isValid,
                    supportingText = if (!nameValidation.isValid)
                        nameValidation.errorResId?.let {
                            stringResource(it, *nameValidation.formatArgs.toTypedArray())
                        }
                    else null,
                    onValueChanged = onNameChanged,
                    leadingIcon = painterResource(Res.drawable.ic_text),
                    hint = stringResource(Res.string.name_search_field_hint),
                    label = stringResource(Res.string.name_label)
                )
                PetWalkerTextInput(
                    value = petSpecies,
                    singleLine = true,
                    isError = !speciesValidation.isValid,
                    supportingText = if (!speciesValidation.isValid)
                        speciesValidation.errorResId?.let {
                            stringResource(it, *speciesValidation.formatArgs.toTypedArray())
                        }
                    else null,
                    onValueChanged = onSpeciesChanged,
                    leadingIcon = painterResource(Res.drawable.ic_text),
                    hint = stringResource(Res.string.species_search_field_hint),
                    label = stringResource(Res.string.species_label)
                )
                PetWalkerTextInput(
                    value = petBreed,
                    singleLine = true,
                    isError = !breedValidation.isValid,
                    supportingText = if (!breedValidation.isValid)
                        breedValidation.errorResId?.let {
                            stringResource(it, *breedValidation.formatArgs.toTypedArray())
                        }
                    else null,
                    onValueChanged = onBreedChanged,
                    leadingIcon = painterResource(Res.drawable.ic_text),
                    hint = stringResource(Res.string.breed_search_field_hint),
                    label = stringResource(Res.string.breed_label)
                )
            }
        else FlowRow(
            modifier = Modifier.weight(3f),
            verticalArrangement = Arrangement.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(2f)
                    .padding(6.dp),
                value = petName,
                singleLine = true,
                isError = !nameValidation.isValid,
                supportingText = if (!nameValidation.isValid)
                    nameValidation.errorResId?.let {
                        stringResource(it, *nameValidation.formatArgs.toTypedArray())
                    }
                else null,
                onValueChanged = onNameChanged,
                leadingIcon = painterResource(Res.drawable.ic_text),
                hint = stringResource(Res.string.name_search_field_hint),
                label = stringResource(Res.string.name_label)
            )
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp),
                value = petSpecies,
                singleLine = true,
                isError = !speciesValidation.isValid,
                supportingText = if (!speciesValidation.isValid)
                    speciesValidation.errorResId?.let {
                        stringResource(it, *speciesValidation.formatArgs.toTypedArray())
                    }
                else null,
                onValueChanged = onSpeciesChanged,
                leadingIcon = painterResource(Res.drawable.ic_text),
                hint = stringResource(Res.string.species_search_field_hint),
                label = stringResource(Res.string.species_label)
            )
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f)
                    .padding(6.dp),
                value = petBreed,
                singleLine = true,
                isError = !breedValidation.isValid,
                supportingText = if (!breedValidation.isValid)
                    breedValidation.errorResId?.let {
                        stringResource(it, *breedValidation.formatArgs.toTypedArray())
                    }
                else null,
                onValueChanged = onBreedChanged,
                leadingIcon = painterResource(Res.drawable.ic_text),
                hint = stringResource(Res.string.breed_search_field_hint),
                label = stringResource(Res.string.species_label)
            )
        }
    }
}

@Composable
fun PetStatsInput(
    modifier: Modifier = Modifier,
    petWeight: String,
    weightValidation: ValidationInfo,
    petBirthDate: LocalDateTime?,
    birthDateValidation: ValidationInfo,
    petDescription: String,
    onWeightChanged: (String) -> Unit,
    onBirthDateChanged: (LocalDateTime?) -> Unit,
    onDescriptionChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        val deviceConfig =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            PetWalkerTextInput(
                modifier = Modifier.weight(1f),
                value = petWeight,
                onValueChanged = onWeightChanged,
                isError = !weightValidation.isValid,
                supportingText = if (!weightValidation.isValid)
                    weightValidation.errorResId?.let {
                        stringResource(it, *weightValidation.formatArgs.toTypedArray())
                    }
                else null,
                hint = stringResource(Res.string.float_input_hint),
                label = stringResource(Res.string.weight_label),
                keyboardType = KeyboardType.Decimal
            )
            Spacer(Modifier.width(16.dp))
            PetWalkerDatePicker(
                modifier = Modifier.weight(1f),
                currentDate = petBirthDate,
                onDateChanged = onBirthDateChanged,
                isError = !birthDateValidation.isValid,
                errorString = if (!birthDateValidation.isValid)
                    birthDateValidation.errorResId?.let {
                        stringResource(it, *birthDateValidation.formatArgs.toTypedArray())
                    }
                else null
            )
        }
        Spacer(Modifier.height(12.dp))

        PetWalkerTextInput(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(
                    if (deviceConfig in listOf(
                            DeviceConfiguration.DESKTOP,
                            DeviceConfiguration.TABLET_LANDSCAPE
                        )
                    ) 0.7f
                    else 0.9f
                )
                .heightIn(
                    max = when (deviceConfig) {
                        in listOf(
                            DeviceConfiguration.TABLET_PORTRAIT,
                            DeviceConfiguration.MOBILE_LANDSCAPE
                        ) -> 400.dp

                        in listOf(
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.DESKTOP
                        ) -> 700.dp

                        else -> 250.dp
                    }
                ),
            value = petDescription,
            onValueChanged = onDescriptionChanged,
            leadingIcon = painterResource(Res.drawable.ic_text),
            hint = stringResource(Res.string.description_input_hint),
            label = stringResource(Res.string.description_label)
        )
    }
}

@Composable
fun MedicalInfoSheet(
    modifier: Modifier = Modifier,
    petMedicalInfos: List<PetMedicalInfo>,
    onDeleteClick: (id: String) -> Unit,
    onEditClick: (id: String) -> Unit,
    onAddInfoClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        HintWithIcon(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            hint = stringResource(Res.string.medical_info_label),
            leadingIcon = painterResource(Res.drawable.ic_app),
            textStyle = MaterialTheme.typography.titleLarge
        )
        LazyColumn(
            modifier = Modifier
                .heightIn(max = 240.dp)
        ) {
            items(petMedicalInfos) {
                EditableMedicalInfoCell(
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    medicalInfo = it,
                    onEditClick = { onEditClick(it.id) },
                    onDeleteClick = { onDeleteClick(it.id) },
                )
            }
        }
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.add_btn_txt),
            trailingIcon = painterResource(Res.drawable.ic_add),
            onClick = onAddInfoClick
        )
    }
}