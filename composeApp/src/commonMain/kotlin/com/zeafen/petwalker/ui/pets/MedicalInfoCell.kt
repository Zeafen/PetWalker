package com.zeafen.petwalker.ui.pets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.api.other.Attachment
import com.zeafen.petwalker.domain.models.api.other.AttachmentType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.ui.channel.AttachmentCell
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.*


@Composable
fun MedicalInfoCell(
    modifier: Modifier = Modifier,
    medicalInfo: PetMedicalInfo,
    onLoadInfoDoc: (name: String, ref: String) -> Unit
) {
    ExpandableContent(
        modifier = modifier,
        defaultContent = {
            Text(
                text = stringResource(medicalInfo.type.displayName),
                style = MaterialTheme.typography.bodyLarge
            )
        },
        expandableContent = {
            Column {
                medicalInfo.description?.let { desc ->
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 12.dp),
                        text = desc,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify
                    )
                }
                medicalInfo.reference?.let {
                    AttachmentCell(
                        attachment = Attachment(
                            medicalInfo.id,
                            AttachmentType.Document,
                            medicalInfo.name!!,
                            medicalInfo.reference
                        ),
                        onPlayAttachment = {},
                        onLoadAttachment = { ref, name ->
                            onLoadInfoDoc(name, ref)
                        }
                    )
                }
            }
        },
    )
}


@Composable
fun EditableMedicalInfoCell(
    modifier: Modifier = Modifier,
    medicalInfo: PetMedicalInfo,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    ExpandableContent(
        modifier = modifier,
        defaultContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = stringResource(medicalInfo.type.displayName),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }
                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_clear),
                        contentDescription = "Remove"
                    )
                }
            }
        },
        expandableContent = {
            Column {
                medicalInfo.description?.let { desc ->
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 12.dp),
                        text = desc,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify
                    )
                }
                medicalInfo.reference?.let {
                    AttachmentCell(
                        attachment = Attachment(
                            medicalInfo.id,
                            AttachmentType.Document,
                            medicalInfo.name!!,
                            medicalInfo.reference
                        ),
                    )
                }
            }
        },
    )
}