package com.zeafen.petwalker.ui.pets.petConfigure

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.add_btn_txt
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.description_label
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.medical_info_label
import petwalker.composeapp.generated.resources.option_selection_hint

@Composable
fun MedicalInfoConfigureDialog(
    medicalInfo: PetMedicalInfo? = null,
    onDoneClick: (
        type: PetInfoType,
        name: String?,
        description: String,
        doc: PetWalkerFileInfo?
    ) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedType by rememberSaveable(medicalInfo) {
        mutableStateOf(medicalInfo?.type)
    }
    var description by rememberSaveable {
        mutableStateOf(medicalInfo?.description ?: "")
    }
    var document by rememberSaveable {
        mutableStateOf<Pair<Attachment, PetWalkerFileInfo?>?>(medicalInfo?.reference?.let {
            Attachment("", AttachmentType.Document, medicalInfo.name ?: "Undefined", it) to null
        })
    }
    val documentPicker = rememberDocumentPicker { fileInfo ->
        document = fileInfo?.let {
            Attachment(
                "",
                AttachmentType.Document,
                fileInfo.displayName,
                fileInfo.path.toString()
            ) to fileInfo
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column {
            PetWalkerDialogHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                onClearFiltersClick = {
                    onDismissRequest()
                },
                title = stringResource(Res.string.medical_info_label),
                onDoneFiltersClick = {
                    if (selectedType != null && (description.isNotBlank() || document?.first != null)) {
                        onDoneClick(
                            selectedType!!,
                            document?.first?.name,
                            description,
                            document?.second
                        )
                        onDismissRequest()
                    }
                }
            )
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .clip(
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OptionsSelectedInput(
                    selectedOptions = selectedType?.let { listOf(it) } ?: emptyList(),
                    availableOptions = PetInfoType.entries.toList(),
                    hint = stringResource(Res.string.option_selection_hint),
                    onOptionSelected = { selectedType = it },
                    onOptionDeleted = { selectedType = null },
                    optionContent = {
                        Text(
                            text = stringResource(it.displayName),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )

                Spacer(Modifier.height(12.dp))
                PetWalkerTextInput(
                    modifier = Modifier.weight(1f),
                    value = description,
                    label = stringResource(Res.string.description_label),
                    hint = stringResource(Res.string.description_input_hint),
                    leadingIcon = painterResource(Res.drawable.ic_text),
                    onValueChanged = { description = it }
                )

                Spacer(Modifier.height(12.dp))
                AnimatedContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth()
                        .fillMaxWidth(0.9f),
                    targetState = document
                ) { doc ->
                    if (doc == null)
                        PetWalkerButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = stringResource(Res.string.add_btn_txt),
                            onClick = {
                                documentPicker.launch(
                                    ExtensionGroups.Documents.exts
                                            + ExtensionGroups.Image.exts
                                            + ExtensionGroups.Audio.exts
                                            + ExtensionGroups.Video.exts
                                )
                            }
                        )
                    else AttachmentCell(
                        attachment = doc.first,
                        onRemoveClick = {
                            document = null
                        }
                    )
                }
            }
        }
    }
}