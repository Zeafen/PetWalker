package com.zeafen.petwalker.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.auth.ForgotPasswordUiEvent
import com.zeafen.petwalker.presentation.auth.ForgotPasswordStage
import com.zeafen.petwalker.presentation.auth.ForgotPasswordUiState
import com.zeafen.petwalker.ui.standard.elements.LogoWithHeaderSlogan
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.back_to_login_link_txt
import petwalker.composeapp.generated.resources.change_password_btn_txt
import petwalker.composeapp.generated.resources.confirm_btn_txt
import petwalker.composeapp.generated.resources.confirmation_code_input_hint
import petwalker.composeapp.generated.resources.confirmation_code_input_label
import petwalker.composeapp.generated.resources.email_input_hint
import petwalker.composeapp.generated.resources.email_input_label
import petwalker.composeapp.generated.resources.forgot_password_btn_text
import petwalker.composeapp.generated.resources.ic_arrow_forward
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.ic_password
import petwalker.composeapp.generated.resources.invalid_confirmation_code_error_txt
import petwalker.composeapp.generated.resources.password_input_hint
import petwalker.composeapp.generated.resources.password_input_label
import petwalker.composeapp.generated.resources.passwords_match_error_txt
import petwalker.composeapp.generated.resources.repeat_password_input_hint
import petwalker.composeapp.generated.resources.repeat_password_input_label
import petwalker.composeapp.generated.resources.send_code_btn_txt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordPage(
    modifier: Modifier = Modifier,
    forgotPasswState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit,
    onGoBackClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clip(
                    RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .consumeWindowInsets(WindowInsets.systemBars)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Spacer(Modifier.height(32.dp))
            AnimatedContent(
                targetState = forgotPasswState.stage,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal)
                        slideInHorizontally() { it } + fadeIn(spring(stiffness = Spring.StiffnessLow)) togetherWith
                                slideOutHorizontally() { -it } + fadeOut(spring(stiffness = Spring.StiffnessLow)) using SizeTransform(
                            false
                        )
                    else slideInHorizontally() { -it } + fadeIn(spring(stiffness = Spring.StiffnessLow)) togetherWith
                            slideOutHorizontally() { it } + fadeOut(spring(stiffness = Spring.StiffnessLow)) using SizeTransform(
                        false
                    )
                }
            ) { targetStage ->
                Column {
                    LogoWithHeaderSlogan(
                        header = stringResource(Res.string.forgot_password_btn_text),
                        slogan = stringResource(targetStage.slogan)
                    )
                    Spacer(Modifier.height(100.dp))
                    when (targetStage) {
                        ForgotPasswordStage.SendCode -> {
                            ForgotPasswordFields_Stage1(
                                forgotPasswState = forgotPasswState,
                                onEvent = onEvent
                            )
                        }

                        ForgotPasswordStage.ConfirmCode -> {
                            ForgotPasswordFields_Stage2(
                                forgotPasswState = forgotPasswState,
                                onEvent = onEvent
                            )
                        }

                        ForgotPasswordStage.ChangePassword -> {
                            ForgotPasswordFields_Stage3(
                                forgotPasswState = forgotPasswState,
                                onEvent = onEvent
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    PetWalkerLinkTextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(),
                        text = stringResource(Res.string.back_to_login_link_txt),
                        onClick = onGoBackClick
                    )
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordFields_Stage1(
    modifier: Modifier = Modifier,
    forgotPasswState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit
) {
    Column(modifier = modifier) {
        PetWalkerTextInput(
            onValueChanged = { onEvent(ForgotPasswordUiEvent.EnterEmail(it)) },
            value = forgotPasswState.email,
            isError = !forgotPasswState.emailValid.isValid,
            supportingText = forgotPasswState.emailValid.errorResId?.let {
                stringResource(
                    it,
                    *forgotPasswState.emailValid.formatArgs.toTypedArray()
                )
            },
            leadingIcon = painterResource(Res.drawable.ic_attach_email),
            hint = stringResource(Res.string.email_input_hint),
            label = stringResource(Res.string.email_input_label)
        )
        Spacer(Modifier.height(12.dp))
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.send_code_btn_txt),
            trailingIcon = painterResource(Res.drawable.ic_arrow_forward),
            enabled = forgotPasswState.emailValid.isValid
        ) {
            onEvent(ForgotPasswordUiEvent.SendCode)
        }
    }
}

@Composable
fun ForgotPasswordFields_Stage2(
    modifier: Modifier = Modifier,
    forgotPasswState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit
) {
    Column(modifier = modifier) {
        PetWalkerTextInput(
            onValueChanged = { onEvent(ForgotPasswordUiEvent.EnterCode(it)) },
            value = forgotPasswState.code,
            supportingText =
                if (forgotPasswState.code.isNotBlank())
                    stringResource(Res.string.invalid_confirmation_code_error_txt)
                else null,
            leadingIcon = painterResource(Res.drawable.ic_password),
            label = stringResource(Res.string.confirmation_code_input_label),
            hint = stringResource(Res.string.confirmation_code_input_hint)
        )
        Spacer(Modifier.height(12.dp))
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.confirm_btn_txt),
            trailingIcon = painterResource(Res.drawable.ic_arrow_forward),
            enabled = forgotPasswState.code.isNotBlank()
        ) {
            onEvent(ForgotPasswordUiEvent.ConfirmCode)
        }
    }
}

@Composable
fun ForgotPasswordFields_Stage3(
    modifier: Modifier = Modifier,
    forgotPasswState: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit
) {
    Column(modifier = modifier) {
        PetWalkerTextInput(
            onValueChanged = { onEvent(ForgotPasswordUiEvent.EnterPassword(it)) },
            value = forgotPasswState.password,
            isError = !forgotPasswState.passwordValid.isValid,
            supportingText = forgotPasswState.passwordValid.errorResId?.let {
                stringResource(
                    it,
                    *forgotPasswState.passwordValid.formatArgs.toTypedArray()
                )
            },
            leadingIcon = painterResource(Res.drawable.ic_password),
            label = stringResource(Res.string.password_input_label),
            hint = stringResource(Res.string.password_input_hint)
        )
        Spacer(Modifier.height(12.dp))
        PetWalkerTextInput(
            onValueChanged = { onEvent(ForgotPasswordUiEvent.EnterRepeatPassword(it)) },
            value = forgotPasswState.repeatPassword,
            isError = !forgotPasswState.passwordsMatch,
            supportingText = if (forgotPasswState.passwordsMatch)
                stringResource(Res.string.passwords_match_error_txt)
            else null,
            leadingIcon = painterResource(Res.drawable.ic_password),
            hint = stringResource(Res.string.repeat_password_input_hint),
            label = stringResource(Res.string.repeat_password_input_label)
        )
        Spacer(Modifier.height(24.dp))
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.change_password_btn_txt),
            trailingIcon = painterResource(Res.drawable.ic_arrow_forward),
            enabled = forgotPasswState.canChangePassword
        ) {
            onEvent(ForgotPasswordUiEvent.ChangePassword)
        }
    }
}
