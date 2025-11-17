package com.zeafen.petwalker.ui.walkers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerDialogHeader
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.description_input_hint
import petwalker.composeapp.generated.resources.description_label
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.float_input_hint
import petwalker.composeapp.generated.resources.greater_than_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.nan_error_txt
import petwalker.composeapp.generated.resources.option_selection_hint
import petwalker.composeapp.generated.resources.payment_label
import petwalker.composeapp.generated.resources.required_label
import petwalker.composeapp.generated.resources.services_label
import petwalker.composeapp.generated.resources.user_service_label

@Composable
fun UserServiceConfigureDialog(
    onDismissRequest: () -> Unit,
    onDoneEditing: (service: ServiceType, additionalInfo: String, payment: Float?) -> Unit,
    initialValue: UserService? = null
) {
    var selectedService = remember(initialValue) {
        initialValue?.service
    }
    var additionalInfo = remember(initialValue) {
        initialValue?.additionalInfo ?: ""
    }
    var payment = remember(initialValue) {
        initialValue?.payment?.toString() ?: ""
    }
    val isInfoError by remember {
        derivedStateOf {
            additionalInfo.isBlank() && selectedService == ServiceType.Other
        }
    }
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }
    val paymentValid = remember(payment) {
        when {
            payment.isNotBlank() && payment.toFloatOrNull() == null ->
                ValidationInfo(
                    false,
                    Res.string.nan_error_txt,
                    emptyList()
                )

            payment.isNotBlank() && payment.toFloat() < 0 ->
                ValidationInfo(
                    false,
                    Res.string.greater_than_error_txt,
                    listOf(0)
                )

            payment.isNotBlank() && payment.takeWhile { ch -> ch !in ".," }.length > 10 ->
                ValidationInfo(
                    false,
                    Res.string.incorrect_length_max_error,
                    listOf(10)
                )

            else -> ValidationInfo(true, null, emptyList())
        }
    }

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
                title = stringResource(Res.string.user_service_label),
                onClearFiltersClick = onDismissRequest,
                onDoneFiltersClick = {
                    if (selectedService == null || (selectedService == ServiceType.Other && additionalInfo.isBlank()))
                        popupContent = Res.string.conflict_error
                    else onDoneEditing(selectedService!!, additionalInfo, payment.toFloatOrNull())
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
                    selectedOptions = selectedService?.let { listOf(it) } ?: emptyList(),
                    availableOptions = ServiceType.entries.toList(),
                    label = stringResource(Res.string.services_label),
                    hint = stringResource(Res.string.option_selection_hint),
                    supportingText = stringResource(Res.string.required_label),
                    onOptionSelected = { selectedService = it },
                    onOptionDeleted = { selectedService = null },
                    optionContent = {
                        Text(
                            text = stringResource(it.displayName),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                )
                Spacer(Modifier.height(12.dp))
                PetWalkerTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 128.dp),
                    value = payment,
                    label = stringResource(Res.string.payment_label),
                    hint = stringResource(Res.string.float_input_hint),
                    isError = !paymentValid.isValid,
                    supportingText = if (!paymentValid.isValid)
                        paymentValid.errorResId?.let {
                            stringResource(it, *paymentValid.formatArgs.toTypedArray())
                        }
                    else null,
                    onValueChanged = { payment = it },
                )

                Spacer(Modifier.height(8.dp))
                PetWalkerTextInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 256.dp),
                    value = additionalInfo,
                    label = stringResource(Res.string.description_label),
                    hint = stringResource(Res.string.description_input_hint),
                    isError = isInfoError,
                    supportingText = if (isInfoError)
                        stringResource(Res.string.empty_fields_error_txt)
                    else null,
                    onValueChanged = { additionalInfo = it },
                )

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