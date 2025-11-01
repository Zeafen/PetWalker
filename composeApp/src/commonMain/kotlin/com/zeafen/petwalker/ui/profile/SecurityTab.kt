package com.zeafen.petwalker.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.domain.models.ValidationInfo
import com.zeafen.petwalker.domain.models.api.users.ProfileSecurityLevel
import com.zeafen.petwalker.presentation.standard.shapes.ShieldShape
import com.zeafen.petwalker.ui.standard.elements.HintWithIcon
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.cancel_btn_txt
import petwalker.composeapp.generated.resources.change_email_btn_txt
import petwalker.composeapp.generated.resources.change_login_btn_txt
import petwalker.composeapp.generated.resources.change_password_btn_txt
import petwalker.composeapp.generated.resources.done_btn_txt
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.ic_password
import petwalker.composeapp.generated.resources.ic_text
import petwalker.composeapp.generated.resources.login_input_hint
import petwalker.composeapp.generated.resources.login_input_label

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecurityTab(
    modifier: Modifier = Modifier,
    securityLevel: ProfileSecurityLevel,
    login: String,
    loginValid: ValidationInfo,
    onLoginChanged: (String) -> Unit,
    onCancelEditingLogin: () -> Unit,
    onDoneEditingLogin: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
) {
    var editLogin by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HintWithIcon(
            hint = stringResource(securityLevel.displayNameRes),
            leadingIcon = painterResource(securityLevel.iconRes),
            textStyle = MaterialTheme.typography.titleLarge
        )
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(ShieldShape)
                .background(
                    when (securityLevel) {
                        ProfileSecurityLevel.Low -> Color.Red
                        ProfileSecurityLevel.Middle -> Color.Yellow
                        ProfileSecurityLevel.High -> Color.Green
                    }
                )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(securityLevel.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            AnimatedContent(
                targetState = editLogin,
                transitionSpec = {
                    if (targetState) {
                        slideInVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            initialOffsetY = { it }) + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ) togetherWith slideOutVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            targetOffsetY = { -it }) + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        )
                    } else {
                        slideInVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            initialOffsetY = { -it }) + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ) togetherWith slideOutVertically(
                            spring(stiffness = Spring.StiffnessMediumLow),
                            targetOffsetY = { it }) + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        )
                    }.using(SizeTransform(clip = false))
                }
            ) { openEditLogin ->
                if (openEditLogin) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        PetWalkerTextInput(
                            modifier = Modifier
                                .fillMaxRowHeight()
                                .wrapContentHeight()
                                .widthIn(max = 400.dp),
                            value = login,
                            label = stringResource(Res.string.login_input_label),
                            hint = stringResource(Res.string.login_input_hint),
                            leadingIcon = painterResource(Res.drawable.ic_text),
                            onValueChanged = onLoginChanged,
                            isError = !loginValid.isValid,
                            supportingText = if (!loginValid.isValid)
                                loginValid.errorResId?.let {
                                    stringResource(it, *loginValid.formatArgs.toTypedArray())
                                } else null,
                            singleLine = true
                        )
                        PetWalkerButton(
                            modifier = Modifier
                                .fillMaxRowHeight()
                                .wrapContentHeight()
                                .padding(8.dp),
                            text = stringResource(Res.string.cancel_btn_txt),
                            containerColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                editLogin = false
                                onCancelEditingLogin()
                            }
                        )
                        PetWalkerButton(
                            modifier = Modifier
                                .fillMaxRowHeight()
                                .wrapContentHeight()
                                .padding(8.dp),
                            text = stringResource(Res.string.done_btn_txt),
                            enabled = loginValid.isValid,
                            onClick = onDoneEditingLogin
                        )
                    }
                } else
                    HintWithIcon(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                editLogin = !editLogin
                            }
                            .padding(16.dp),
                        hint = stringResource(Res.string.change_login_btn_txt),
                        leadingIcon = painterResource(Res.drawable.ic_text),
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
            }
            Spacer(Modifier.height(12.dp))



            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onChangePasswordClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.change_password_btn_txt),
                leadingIcon = painterResource(Res.drawable.ic_password),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(12.dp))

            HintWithIcon(
                modifier = Modifier
                    .padding(8.dp)
                    .width(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        onChangeEmailClick()
                    }
                    .padding(16.dp),
                hint = stringResource(Res.string.change_email_btn_txt),
                leadingIcon = painterResource(Res.drawable.ic_attach_email),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}