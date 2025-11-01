package com.zeafen.petwalker.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zeafen.petwalker.presentation.auth.AuthUiEvent
import com.zeafen.petwalker.presentation.auth.AuthUiState
import com.zeafen.petwalker.ui.standard.elements.LogoWithHeaderSlogan
import com.zeafen.petwalker.ui.standard.elements.PetWalkerButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerLinkTextButton
import com.zeafen.petwalker.ui.standard.elements.PetWalkerTextInput
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.email_input_hint
import petwalker.composeapp.generated.resources.email_input_label
import petwalker.composeapp.generated.resources.first_name_input_hint
import petwalker.composeapp.generated.resources.first_name_input_label
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_arrow_forward
import petwalker.composeapp.generated.resources.ic_attach_email
import petwalker.composeapp.generated.resources.ic_login
import petwalker.composeapp.generated.resources.ic_password
import petwalker.composeapp.generated.resources.ic_phone
import petwalker.composeapp.generated.resources.last_name_input_hint
import petwalker.composeapp.generated.resources.last_name_input_label
import petwalker.composeapp.generated.resources.login_input_hint
import petwalker.composeapp.generated.resources.login_input_label
import petwalker.composeapp.generated.resources.password_input_hint
import petwalker.composeapp.generated.resources.password_input_label
import petwalker.composeapp.generated.resources.passwords_match_error_txt
import petwalker.composeapp.generated.resources.phone_input_hint
import petwalker.composeapp.generated.resources.phone_input_label
import petwalker.composeapp.generated.resources.repeat_password_input_hint
import petwalker.composeapp.generated.resources.repeat_password_input_label
import petwalker.composeapp.generated.resources.sign_in_btn_text
import petwalker.composeapp.generated.resources.sign_in_link_txt
import petwalker.composeapp.generated.resources.sign_up_btn_text
import petwalker.composeapp.generated.resources.signup_page_header
import petwalker.composeapp.generated.resources.signup_page_slogan

@Composable
fun SignUpPage(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
    onGoToSignInClick: () -> Unit,
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
                .verticalScroll(rememberScrollState())
        ) {
            LogoWithHeaderSlogan(
                header = stringResource(Res.string.signup_page_header),
                slogan = stringResource(Res.string.signup_page_slogan)
            )
            SignUpPageFields(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                authState = authState,
                onEvent = onEvent,
            )
            HorizontalDivider(
                Modifier
                    .padding(vertical = 32.dp)
            )
            SignUpPageLinks(
                authState = authState,
                onEvent = onEvent,
                onGoToSignInClick = onGoToSignInClick
            )
        }
    }
}

@Composable
fun SignUpPageFields(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
) {
    Column(modifier = modifier) {
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterFirstName(it)) },
            value = authState.firstName,
            leadingIcon = painterResource(Res.drawable.ic_account_box),
            label = stringResource(Res.string.first_name_input_label),
            hint = stringResource(Res.string.first_name_input_hint)
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterLastName(it)) },
            value = authState.lastName,
            leadingIcon = painterResource(Res.drawable.ic_account_box),
            label = stringResource(Res.string.last_name_input_label),
            hint = stringResource(Res.string.last_name_input_hint)
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterLogin(it)) },
            value = authState.login,
            leadingIcon = painterResource(Res.drawable.ic_login),
            label = stringResource(Res.string.login_input_label),
            hint = stringResource(Res.string.login_input_hint)
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterEmail(it)) },
            value = authState.email,
            isError = !authState.emailValid.isValid,
            supportingText = authState.emailValid.errorResId?.let {
                stringResource(it, *authState.emailValid.formatArgs.toTypedArray())
            },
            leadingIcon = painterResource(Res.drawable.ic_attach_email),
            label = stringResource(Res.string.email_input_label),
            hint = stringResource(Res.string.email_input_hint)
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterPhone(it)) },
            value = authState.phone,
            isError = !authState.phoneValid.isValid,
            supportingText = authState.phoneValid.errorResId?.let {
                stringResource(it, *authState.phoneValid.formatArgs.toTypedArray())
            },
            leadingIcon = painterResource(Res.drawable.ic_phone),
            label = stringResource(Res.string.phone_input_label),
            hint = stringResource(Res.string.phone_input_hint)
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterPassword(it)) },
            value = authState.password,
            leadingIcon = painterResource(Res.drawable.ic_password),
            label = stringResource(Res.string.password_input_label),
            hint = stringResource(Res.string.password_input_hint),
            isSecretInput = true,
            isError = !authState.passwordValid.isValid,
            supportingText = authState.passwordValid.errorResId?.let {
                stringResource(it, *authState.passwordValid.formatArgs.toTypedArray())
            }
        )
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterRepeatPassword(it)) },
            value = authState.repeatPassword,
            leadingIcon = painterResource(Res.drawable.ic_password),
            label = stringResource(Res.string.repeat_password_input_label),
            hint = stringResource(Res.string.repeat_password_input_hint),
            isSecretInput = true,
            isError = !authState.passwordsMatch,
            supportingText = if (!authState.passwordsMatch) stringResource(Res.string.passwords_match_error_txt)
            else null
        )
    }
}

@Composable
fun SignUpPageLinks(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
    onGoToSignInClick: () -> Unit,
) {
    Column(modifier = modifier) {
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.sign_up_btn_text),
            trailingIcon = painterResource(Res.drawable.ic_arrow_forward),
            enabled = authState.canSignUp
        ) {
            onEvent(AuthUiEvent.ConfirmSignUp)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.sign_in_link_txt),
                style = MaterialTheme.typography.bodyLarge
            )
            PetWalkerLinkTextButton(
                text = stringResource(Res.string.sign_in_btn_text),
                onClick = onGoToSignInClick
            )
        }
    }
}
