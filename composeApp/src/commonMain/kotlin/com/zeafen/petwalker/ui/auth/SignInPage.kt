package com.zeafen.petwalker.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import petwalker.composeapp.generated.resources.forgot_password_btn_text
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_arrow_forward
import petwalker.composeapp.generated.resources.ic_password
import petwalker.composeapp.generated.resources.login_email_input_hint
import petwalker.composeapp.generated.resources.login_email_input_label
import petwalker.composeapp.generated.resources.login_page_header
import petwalker.composeapp.generated.resources.login_page_slogan
import petwalker.composeapp.generated.resources.password_input_hint
import petwalker.composeapp.generated.resources.password_input_label
import petwalker.composeapp.generated.resources.sign_in_btn_text
import petwalker.composeapp.generated.resources.sign_up_btn_text
import petwalker.composeapp.generated.resources.sign_up_link_txt

@Composable
fun SignInPage(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
    onGoToSignUpClick: () -> Unit,
    onGoToForgotPasswordClick: () -> Unit
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
                header = stringResource(Res.string.login_page_header),
                slogan = stringResource(Res.string.login_page_slogan)
            )
            SignInPageFields(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .fillMaxWidth(),
                authState = authState,
                onEvent = onEvent,
            )
            HorizontalDivider(
                Modifier
                    .padding(vertical = 32.dp)
            )
            SignInPageLinks(
                onGoToSignUpClick = onGoToSignUpClick,
                onGoToForgotPasswordClick = onGoToForgotPasswordClick
            )
        }
    }
}

@Composable
fun SignInPageFields(
    modifier: Modifier = Modifier,
    authState: AuthUiState,
    onEvent: (AuthUiEvent) -> Unit,
) {
    Column(modifier = modifier) {
        PetWalkerTextInput(
            onValueChanged = { onEvent(AuthUiEvent.EnterLogin(it)) },
            value = authState.login,
            leadingIcon = painterResource(Res.drawable.ic_account_box),
            label = stringResource(Res.string.login_email_input_label),
            hint = stringResource(Res.string.login_email_input_hint)
        )
        Spacer(Modifier.height(12.dp))
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
        Spacer(Modifier.height(24.dp))
        PetWalkerButton(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .fillMaxWidth(0.9f),
            text = stringResource(Res.string.sign_in_btn_text),
            trailingIcon = painterResource(Res.drawable.ic_arrow_forward),
            enabled = authState.canSignIn
        ) {
            onEvent(AuthUiEvent.ConfirmSignIn)
        }
    }
}

@Composable
fun SignInPageLinks(
    modifier: Modifier = Modifier,
    onGoToSignUpClick: () -> Unit,
    onGoToForgotPasswordClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.sign_up_link_txt),
                style = MaterialTheme.typography.bodyLarge
            )
            PetWalkerLinkTextButton(
                text = stringResource(Res.string.sign_up_btn_text),
                onClick = onGoToSignUpClick
            )
        }
        PetWalkerLinkTextButton(
            text = stringResource(Res.string.forgot_password_btn_text),
            onClick = onGoToForgotPasswordClick
        )
    }
}
