package com.zeafen.petwalker.ui.walkers.walkerDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.ui.assignments.AssignmentCard
import com.zeafen.petwalker.ui.standard.elements.ErrorInfoHint
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PageSelectionRow
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.assignments_page_header
import petwalker.composeapp.generated.resources.ic_number
import petwalker.composeapp.generated.resources.option_not_selected_error
import petwalker.composeapp.generated.resources.recruiting_label

@Composable
fun RecruitingDialog(
    onDismissRequest: () -> Unit,
    availableAssignments: APIResult<PagedResult<AssignmentModel>, Error>,
    onDoneRecruiting: (assignmentId: String) -> Unit,
    onLoadAssignments: (page: Int) -> Unit
) {
    var selectedAssignmentId by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }

    Dialog(
        onDismissRequest,
        properties = DialogProperties()
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
                title = stringResource(Res.string.recruiting_label),
                onClearFiltersClick = onDismissRequest,
                onDoneFiltersClick = {
                    if (selectedAssignmentId == null)
                        popupContent = Res.string.option_not_selected_error
                    else
                        onDoneRecruiting(selectedAssignmentId!!)
                }
            )
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (availableAssignments) {
                    is APIResult.Downloading -> CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .fillMaxWidth(0.4f),
                        strokeWidth = 4.dp
                    )

                    is APIResult.Error -> ErrorInfoHint(
                        errorInfo = "${
                            stringResource(availableAssignments.info.infoResource())
                        }: ${availableAssignments.additionalInfo}",
                        onReloadPage = { onLoadAssignments(0) }
                    )

                    is APIResult.Succeed -> {
                        HintWithIcon(
                            modifier = Modifier
                                .padding(end = 24.dp, bottom = 12.dp),
                            leadingIcon = painterResource(Res.drawable.ic_number),
                            hint = stringResource(Res.string.assignments_page_header),
                            textStyle = MaterialTheme.typography.titleLarge
                        )
                        LazyRow {
                            items(availableAssignments.data!!.result) {
                                AssignmentCard(
                                    modifier = Modifier
                                        .widthIn(max = 256.dp)
                                        .padding(horizontal = 12.dp),
                                    backgroundColor = if (selectedAssignmentId == it.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    assignment = it,
                                    onClick = { selectedAssignmentId = it }
                                )
                            }
                        }
                        PageSelectionRow(
                            totalPages = availableAssignments.data!!.totalPages,
                            currentPage = availableAssignments.data.currentPage,
                            onPageClick = { onLoadAssignments(it) }
                        )
                    }
                }

                if(popupContent != null)
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
        }
    }
}