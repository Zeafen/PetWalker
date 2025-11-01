package com.zeafen.petwalker.ui.standard.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CodeInputField(
    modifier: Modifier = Modifier,
    code: String,
    codeMaxLength: Int = 5,
    onCodeChanged: (String) -> Unit,
    label: String? = null,
    supportingText: String? = null,
    isError: Boolean = false
) {
    val indices = remember(codeMaxLength) {
        (0..<codeMaxLength).toList()
    }
    val focusControllers = remember(indices) {
        indices.associateWith {
            FocusRequester()
        }
    }

    Column(
        modifier = modifier
    ) {
        label?.let {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            indices.forEach { index ->
                OutlinedTextField(
                    modifier = Modifier
                        .width(50.dp)
                        .padding(horizontal = 4.dp)
                        .focusRequester(focusControllers[index]!!)
                        .onFocusChanged { state ->
                            if (state.isFocused && index - code.length > 1)
                                focusControllers[code.lastIndex.coerceAtLeast(0)]!!.requestFocus()
                        }
                        .onKeyEvent { event ->
                            if (event.key == Key.Backspace)
                                when {
                                    code.length == index && index > 0 -> focusControllers[index - 1]!!.requestFocus()
                                    code.length - index == 1 -> onCodeChanged(
                                        code.substring(
                                            0,
                                            code.lastIndex
                                        )
                                    )

                                    code.length - index > 1 -> {
                                        focusControllers[code.lastIndex]!!.requestFocus()
                                        onCodeChanged(code.substring(0, code.lastIndex))
                                    }
                                }
                            false
                        },
                    value = code.getOrNull(index)?.toString() ?: "",
                    onValueChange = { value ->
                        if (value.matches(Regex("[A-Za-z0-9]+"))) {
                            onCodeChanged(
                                if (code.length <= index)
                                    code.plus(value.first())
                                else
                                    code.toCharArray().apply {
                                        set(
                                            index,
                                            value.toList().minus(code[index]).firstOrNull()
                                                ?: value.first()
                                        )
                                    }.concatToString()
                            )
                            if (index < indices.lastIndex)
                                focusControllers[index + 1]!!.requestFocus()
                        }
                    },
                    isError = isError
                )
            }
        }
        supportingText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Light,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
            )
        }
    }
}