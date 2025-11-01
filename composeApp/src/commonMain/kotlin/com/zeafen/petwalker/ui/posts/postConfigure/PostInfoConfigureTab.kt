package com.zeafen.petwalker.ui.posts.postConfigure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.text_label
import petwalker.composeapp.generated.resources.title_input_hint
import petwalker.composeapp.generated.resources.title_label

@Composable
fun PostInfoConfigureTab(
    modifier: Modifier = Modifier,
    postTitle: String,
    titleValidation: ValidationInfo,
    onTitleChanged: (String) -> Unit,
    postText: String,
    textValidation: ValidationInfo,
    onTextChanged: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        PetWalkerTextInput(
            value = postTitle,
            singleLine = true,
            isError = !titleValidation.isValid,
            supportingText = if (!titleValidation.isValid)
                titleValidation.errorResId?.let {
                    stringResource(it, *titleValidation.formatArgs.toTypedArray())
                }
            else null,
            label = stringResource(Res.string.title_label),
            hint = stringResource(Res.string.title_input_hint),
            leadingIcon = painterResource(Res.drawable.ic_text),
            onValueChanged = onTitleChanged
        )

        Spacer(Modifier.height(12.dp))
        PetWalkerTextInput(
            modifier = Modifier
            .heightIn(max = 300.dp),
            value = postText,
            isError = !textValidation.isValid,
            supportingText = if (!textValidation.isValid)
                textValidation.errorResId?.let {
                    stringResource(it, *textValidation.formatArgs.toTypedArray())
                }
            else null,
            label = stringResource(Res.string.text_label),
            hint = stringResource(Res.string.description_input_hint),
            onValueChanged = onTextChanged
        )
    }
}