package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.hide_ic
import petwalker.composeapp.generated.resources.show_ic

@Composable
fun PetWalkerTextInput(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    isError: Boolean = false,
    isSecretInput: Boolean = false,
    supportingText: String? = null,
    label: String? = null,
    hint: String? = null,
    leadingIcon: Painter? = null,
    singleLine: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Unspecified
) {
    var valueVisible by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        label?.let {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(
                modifier = Modifier
                    .height(4.dp)
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChanged,
            visualTransformation = if (isSecretInput && !valueVisible) PasswordVisualTransformation(
                '*'
            )
            else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                errorCursorColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                errorTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            placeholder = {
                hint?.let {
                    Text(
                        text = hint,
                        fontWeight = FontWeight.W300,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            },
            trailingIcon = {
                if (isSecretInput)
                    IconButton(
                        onClick = { valueVisible = !valueVisible }
                    ) {
                        when {
                            valueVisible -> {
                                Icon(
                                    painter = painterResource(Res.drawable.show_ic),
                                    contentDescription = "Hide"
                                )
                            }

                            !valueVisible -> {
                                Icon(
                                    painter = painterResource(Res.drawable.hide_ic),
                                    contentDescription = "Show"
                                )
                            }
                        }
                    }
            },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null
                    )
                }
            },
            supportingText = {
                if (!supportingText.isNullOrBlank()) {
                    Text(text = supportingText, style = MaterialTheme.typography.bodyMedium)
                }
            },
            isError = isError,
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge,

            )
    }
}
