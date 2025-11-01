package com.zeafen.petwalker.ui.pets.petDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.format
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.presentation.pets.petDetails.PetDetailsUiEvent
import com.zeafen.petwalker.presentation.pets.petDetails.PetDetailsUiState
import com.zeafen.petwalker.ui.pets.MedicalInfoCell
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.age_label
import petwalker.composeapp.generated.resources.breed_label
import petwalker.composeapp.generated.resources.ic_edit
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.less_label
import petwalker.composeapp.generated.resources.medical_info_label
import petwalker.composeapp.generated.resources.more_label
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.pet_details_page_header
import petwalker.composeapp.generated.resources.species_label
import petwalker.composeapp.generated.resources.weigh_txt
import petwalker.composeapp.generated.resources.weight_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsPage(
    modifier: Modifier = Modifier,
    state: PetDetailsUiState,
    onEvent: (PetDetailsUiEvent) -> Unit,
    onEditPetClick: (petId: String) -> Unit,
    onBackClick: () -> Unit
) {
    var popupContent = remember(state.fileLoadingError) {
        state.fileLoadingError?.infoResource()
    }
    var expandDescription by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
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
                    AnimatedVisibility(visible = state.own) {
                        IconButton(
                            onClick = { state.selectedPetId?.let { onEditPetClick(it) } }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp),
                                painter = painterResource(Res.drawable.ic_edit),
                                contentDescription = "Go back"
                            )
                        }
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
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (state.pet) {
                is APIResult.Downloading -> CircularProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.4f)
                        .padding(top = 24.dp),
                    strokeWidth = 4.dp
                )

                is APIResult.Error -> ErrorInfoHint(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth()
                        .fillMaxWidth(0.7f),
                    errorInfo = "${
                        stringResource(state.pet.info.infoResource())
                    }: ${state.pet.additionalInfo}",
                    onReloadPage = { state.selectedPetId?.let { onEvent(PetDetailsUiEvent.LoadPet(it)) } }
                )

                is APIResult.Succeed -> {
                    PetWalkerAsyncImage(
                        imageUrl = state.pet.data!!.imageUrl,
                        asyncImageModifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .fillMaxWidth(
                                if (deviceConfig in listOf(
                                        DeviceConfiguration.DESKTOP,
                                        DeviceConfiguration.TABLET_LANDSCAPE
                                    )
                                ) 0.6f
                                else 0.9f
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStart = 32.dp, topEnd = 32.dp
                                )
                            )
                    )
                    PetDetailsPageStats(
                        pet = state.pet.data
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 12.dp),
                        thickness = 4.dp
                    )
                    MedicalInfoContent(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        medicalInfo = state.petMedicalInfo,
                        selectedInfoType = state.selectedMedicalInfoType,
                        onEvent = onEvent
                    )

                    state.pet.data.description?.let { desc ->
                        AnimatedContent(
                            targetState = expandDescription,
                        ) {
                            when {
                                expandDescription -> {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Justify,
                                        )

                                        PetWalkerLinkTextButton(
                                            text = stringResource(Res.string.less_label),
                                            onClick = { expandDescription = !expandDescription }
                                        )
                                    }
                                }

                                !expandDescription -> {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Justify,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        PetWalkerLinkTextButton(
                                            text = stringResource(Res.string.more_label),
                                            onClick = { expandDescription = !expandDescription }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

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
fun PetDetailsPageStats(
    modifier: Modifier = Modifier,
    pet: Pet
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .offset(x = 16.dp),
            maxLines = when (deviceConfig) {
                in listOf(
                    DeviceConfiguration.MOBILE_LANDSCAPE,
                    DeviceConfiguration.TABLET_PORTRAIT
                ) -> 9

                in listOf(
                    DeviceConfiguration.TABLET_LANDSCAPE,
                    DeviceConfiguration.DESKTOP
                ) -> 6

                else -> 3
            },
            text = pet.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PetStatCell(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(
                        max = when (deviceConfig) {
                            in listOf(
                                DeviceConfiguration.MOBILE_LANDSCAPE,
                                DeviceConfiguration.TABLET_PORTRAIT
                            ) -> 320.dp

                            in listOf(
                                DeviceConfiguration.TABLET_LANDSCAPE,
                                DeviceConfiguration.DESKTOP
                            ) -> 400.dp

                            else -> 240.dp
                        }
                    ),
                statisticValue = pet.species,
                statisticName = stringResource(Res.string.species_label),
            )
            PetStatCell(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .widthIn(
                        max = when (deviceConfig) {
                            in listOf(
                                DeviceConfiguration.MOBILE_LANDSCAPE,
                                DeviceConfiguration.TABLET_PORTRAIT
                            ) -> 320.dp

                            in listOf(
                                DeviceConfiguration.TABLET_LANDSCAPE,
                                DeviceConfiguration.DESKTOP
                            ) -> 400.dp

                            else -> 240.dp
                        }
                    ),
                statisticValue = pet.breed,
                statisticName = stringResource(Res.string.breed_label),
            )
            PetStatCell(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                statisticValue = stringResource(
                    Res.string.weigh_txt,
                    pet.weight.format(2)
                ),
                statisticName = stringResource(Res.string.weight_label),
            )
            PetStatCell(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                statisticValue = pet.date_birth.format(
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
                statisticName = stringResource(Res.string.age_label),
            )
        }
    }
}

@Composable
fun PetStatCell(
    modifier: Modifier = Modifier,
    statisticValue: String,
    statisticName: String,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                maxLines = when (deviceConfig) {
                    in listOf(
                        DeviceConfiguration.MOBILE_LANDSCAPE,
                        DeviceConfiguration.TABLET_PORTRAIT
                    ) -> 9

                    in listOf(
                        DeviceConfiguration.TABLET_LANDSCAPE,
                        DeviceConfiguration.DESKTOP
                    ) -> 6

                    else -> 3
                },
                textAlign = TextAlign.Center,
                text = statisticValue,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                textAlign = TextAlign.Center,
                text = statisticName,
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicalInfoContent(
    modifier: Modifier = Modifier,
    medicalInfo: APIResult<List<PetMedicalInfo>, Error>,
    selectedInfoType: PetInfoType?,
    onEvent: (PetDetailsUiEvent) -> Unit
) {
    ExpandableContent(
        modifier = modifier,
        defaultContent = {
            Text(
                text = stringResource(Res.string.medical_info_label),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        expandableContent = {
            Column {
                OptionsSelectedInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    hint = stringResource(Res.string.option_selection_hint),
                    selectedOptions = selectedInfoType?.let { listOf(it) } ?: emptyList(),
                    availableOptions = PetInfoType.entries.toList(),
                    onOptionDeleted = { onEvent(PetDetailsUiEvent.SetSearchPetInfoType(null)) },
                    onOptionSelected = { onEvent(PetDetailsUiEvent.SetSearchPetInfoType(it)) },
                    optionContent = {
                        Text(
                            text = stringResource(it.displayName),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
                when (medicalInfo) {
                    is APIResult.Downloading -> CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .fillMaxWidth(0.4f),
                        strokeWidth = 4.dp
                    )

                    is APIResult.Error -> ErrorInfoHint(
                        errorInfo = "${
                            stringResource(medicalInfo.info.infoResource())
                        }: ${medicalInfo.additionalInfo}",
                        onReloadPage = { onEvent(PetDetailsUiEvent.LoadMedicalInfo) }
                    )

                    is APIResult.Succeed -> {
                        FlowRow(
                            maxItemsInEachRow = 2,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            medicalInfo.data!!.forEach { info ->
                                MedicalInfoCell(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 16.dp),
                                    medicalInfo = info,
                                    onLoadInfoDoc = { name, ref ->
                                        onEvent(PetDetailsUiEvent.LoadMedicalDoc(name, ref))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        })
}