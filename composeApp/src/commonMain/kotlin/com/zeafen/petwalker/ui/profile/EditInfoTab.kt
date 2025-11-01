package com.zeafen.petwalker.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.users.DesiredPayment
import com.zeafen.petwalker.domain.models.api.users.UserService
import com.zeafen.petwalker.ui.standard.elements.ExpandableContent
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.OptionsSelectedInput
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerServiceCard
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.about_me_input_hint
import petwalker.composeapp.generated.resources.about_me_label
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.desired_payment_default_display_name
import petwalker.composeapp.generated.resources.desired_payment_label
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.first_name_input_hint
import petwalker.composeapp.generated.resources.first_name_input_label
import petwalker.composeapp.generated.resources.ic_add
import petwalker.composeapp.generated.resources.ic_attach
import petwalker.composeapp.generated.resources.ic_map
import petwalker.composeapp.generated.resources.ic_service
import petwalker.composeapp.generated.resources.last_name_input_hint
import petwalker.composeapp.generated.resources.last_name_input_label
import petwalker.composeapp.generated.resources.services_label
import petwalker.composeapp.generated.resources.set_default_location_btn_text
import petwalker.composeapp.generated.resources.show_as_walker_label
import kotlin.math.min

@Composable
fun EditInfoTab(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    firstName: String,
    lastName: String,
    aboutMe: String,
    canPublish: Boolean,
    services: List<UserService>,
    desiredPayment: DesiredPayment?,
    showAsWalker: Boolean,
    firstNameValid: ValidationInfo,
    lastNameValid: ValidationInfo,
    aboutMeValid: ValidationInfo,
    onImageClick: () -> Unit,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAboutMeChanged: (String) -> Unit,
    onDesiredPaymentSelected: (DesiredPayment?) -> Unit,
    onAddServiceClick: () -> Unit,
    onEditServiceClick: (id: String) -> Unit,
    onDeleteServiceClick: (id: String) -> Unit,
    onEditShowAsWalker: (Boolean) -> Unit,
    onGoToSetDefaultLocation: () -> Unit,
    onCancel: () -> Unit,
    onPublishResult: () -> Unit
) {
    val iconBackGround = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val deviceConfig =
            DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

        PetWalkerAsyncImage(
            imageUrl = imageUrl,
            asyncImageModifier = Modifier
                .fillMaxWidth(0.5f)
                .heightIn(
                    max = when (deviceConfig) {
                        in listOf(
                            DeviceConfiguration.MOBILE_LANDSCAPE,
                            DeviceConfiguration.TABLET_PORTRAIT
                        ) -> 320.dp

                        in listOf(
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.DESKTOP
                        ) -> 400.dp

                        else -> 260.dp
                    }
                )
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithCache {
                    onDrawWithContent {
                        this.drawContent()
                        val radius = min(size.height, size.width) / 8f
                        val iconSize = radius * 0.8f

                        drawCircle(
                            Color.Black,
                            radius,
                            Offset(size.width - radius, size.height - radius),
                            blendMode = BlendMode.Clear
                        )

                        drawCircle(
                            color = iconBackGround,
                            radius = iconSize,
                            center = Offset(size.width - radius, size.height - radius),
                        )
                    }
                }
                .padding(bottom = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    onImageClick()
                }
        )
        Row {
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                value = firstName,
                label = stringResource(Res.string.first_name_input_label),
                hint = stringResource(Res.string.first_name_input_hint),
                isError = !firstNameValid.isValid,
                supportingText = if (!firstNameValid.isValid)
                    firstNameValid.errorResId?.let {
                        stringResource(it, *firstNameValid.formatArgs.toTypedArray())
                    }
                else null,
                singleLine = true,
                onValueChanged = onFirstNameChanged,
            )
            PetWalkerTextInput(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                value = lastName,
                label = stringResource(Res.string.last_name_input_label),
                hint = stringResource(Res.string.last_name_input_hint),
                isError = !lastNameValid.isValid,
                supportingText = if (!lastNameValid.isValid)
                    lastNameValid.errorResId?.let {
                        stringResource(it, *lastNameValid.formatArgs.toTypedArray())
                    }
                else null,
                singleLine = true,
                onValueChanged = onLastNameChanged,
            )
        }
        Spacer(Modifier.height(12.dp))
        OptionsSelectedInput(
            selectedOptions = desiredPayment?.let { listOf(it) } ?: emptyList(),
            availableOptions = DesiredPayment.entries.toList(),
            onOptionSelected = onDesiredPaymentSelected,
            onOptionDeleted = { onDesiredPaymentSelected(null) },
            label = stringResource(Res.string.desired_payment_label),
            hint = stringResource(Res.string.desired_payment_default_display_name),
            optionContent = {
                Text(
                    text = stringResource(it.displayName),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        )
        Spacer(Modifier.height(24.dp))

        if (deviceConfig in listOf(
                DeviceConfiguration.MOBILE_PORTRAIT,
                DeviceConfiguration.TABLET_PORTRAIT
            )
        )
            Column {
                PetWalkerButton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 8.dp),
                    text = stringResource(Res.string.set_default_location_btn_text),
                    trailingIcon = painterResource(Res.drawable.ic_map),
                    onClick = onGoToSetDefaultLocation
                )
                Row {
                    Switch(
                        checked = showAsWalker,
                        onCheckedChange = {
                            onEditShowAsWalker(it)
                        },
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.show_as_walker_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        else
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PetWalkerButton(
                    modifier = Modifier
                        .weight(2f),
                    text = stringResource(Res.string.set_default_location_btn_text),
                    trailingIcon = painterResource(Res.drawable.ic_map),
                    onClick = onGoToSetDefaultLocation
                )
                Spacer(Modifier.width(12.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Switch(
                        checked = showAsWalker,
                        onCheckedChange = {
                            onEditShowAsWalker(it)
                        },
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.show_as_walker_label),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        ExpandableContent(
            defaultContent = {
                HintWithIcon(
                    hint = stringResource(Res.string.services_label),
                    leadingIcon = painterResource(Res.drawable.ic_service),
                )
            },
            expandableContent = {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilledIconButton(
                            modifier = Modifier
                                .size(200.dp),
                            onClick = onAddServiceClick,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(0.9f),
                                painter = painterResource(Res.drawable.ic_add),
                                contentDescription = "Add service"
                            )
                        }
                    }
                    items(services) {
                        PetWalkerServiceCard(
                            modifier = Modifier
                                .widthIn(max = 256.dp)
                                .padding(horizontal = 8.dp),
                            service = it,
                            onDeleteClick = { onDeleteServiceClick(it.id) },
                            onEditClick = { onEditServiceClick(it.id) }
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(16.dp))

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
                .heightIn(max = 300.dp),
            value = aboutMe,
            label = stringResource(Res.string.about_me_label),
            hint = stringResource(Res.string.about_me_input_hint),
            isError = !aboutMeValid.isValid,
            supportingText = if (!aboutMeValid.isValid)
                aboutMeValid.errorResId?.let {
                    stringResource(it, *aboutMeValid.formatArgs.toTypedArray())
                }
            else null,
            onValueChanged = onAboutMeChanged
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
        ) {
            PetWalkerButton(
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                containerColor = MaterialTheme.colorScheme.error,
                text = stringResource(Res.string.cancel_btn_txt),
                onClick = onCancel
            )
            Spacer(Modifier.height(8.dp))
            PetWalkerButton(
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                text = stringResource(Res.string.done_btn_txt),
                enabled = canPublish,
                onClick = onPublishResult
            )
        }
    }
}

