package com.zeafen.petwalker.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.ui.standard.elements.CodeInputField
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.code_input_label
import petwalker.composeapp.generated.resources.email_input_hint
import petwalker.composeapp.generated.resources.email_input_label
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.send_code_btn_txt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmailConfigureDialog(
    email: String,
    emailValid: ValidationInfo,
    code: String,
    codeValid: ValidationInfo,
    onEmailEdited: (String) -> Unit,
    onCodeEdited: (String) -> Unit,
    onGenerateCodeClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onDoneClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column {
            PetWalkerDialogHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                title = stringResource(Res.string.email_input_label),
                onDoneFiltersClick = onDoneClick,
                onClearFiltersClick = onDismissRequest
            )
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                PetWalkerTextInput(
                    value = email,
                    onValueChanged = onEmailEdited,
                    label = stringResource(Res.string.email_input_label),
                    hint = stringResource(Res.string.email_input_hint),
                    leadingIcon = painterResource(Res.drawable.ic_attach_email),
                    isError = !emailValid.isValid,
                    supportingText = if (!emailValid.isValid)
                        emailValid.errorResId?.let {
                            stringResource(it, *emailValid.formatArgs.toTypedArray())
                        }
                    else null
                )

                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    CodeInputField(
                        modifier = Modifier
                            .widthIn(max = 300.dp)
                            .padding(vertical = 12.dp),
                        code = code,
                        label = stringResource(Res.string.code_input_label),
                        onCodeChanged = onCodeEdited,
                        isError = !codeValid.isValid,
                        supportingText = if (!codeValid.isValid)
                            codeValid.errorResId?.let {
                                stringResource(it, *codeValid.formatArgs.toTypedArray())
                            } else null
                    )
                    PetWalkerButton(
                        modifier = Modifier
                            .fillMaxRowHeight()
                            .wrapContentHeight()
                            .padding(horizontal = 8.dp),
                        text = stringResource(Res.string.send_code_btn_txt),
                        onClick = onGenerateCodeClick
                    )
                }
            }
        }
    }
}