package com.zeafen.petwalker.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.zeafen.petwalker.data.helpers.DeviceConfiguration
import com.zeafen.petwalker.data.helpers.ExtensionGroups
import com.zeafen.petwalker.data.helpers.rememberDocumentPicker
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.ProfileSecurityLevel
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.presentation.profile.ProfilePageUiEvent
import com.zeafen.petwalker.presentation.profile.ProfilePageUiState
import com.zeafen.petwalker.presentation.profile.ProfileTabs
import com.zeafen.petwalker.presentation.standard.TwoLayerTopAppBar.rememberTwoLayerTopAppBarScrollBehavior
import com.zeafen.petwalker.ui.standard.effects.shimmerEffect
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAlertDialog
import com.zeafen.petwalker.ui.standard.elements.PetWalkerAsyncImage
import com.zeafen.petwalker.ui.standard.elements.TwoLayerTopAppBar
import com.zeafen.petwalker.ui.walkers.UserServiceConfigureDialog
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.are_sure_label
import petwalker.composeapp.generated.resources.confirm_delete_account_label
import petwalker.composeapp.generated.resources.confirm_logout_text_label
import petwalker.composeapp.generated.resources.conflict_error
import petwalker.composeapp.generated.resources.ic_exit
import petwalker.composeapp.generated.resources.ic_go_back
import petwalker.composeapp.generated.resources.ic_no_account
import petwalker.composeapp.generated.resources.profile_page_header

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    state: ProfilePageUiState,
    onEvent: (ProfilePageUiEvent) -> Unit,
    onGoToEditPasswordScreen: () -> Unit,
    onGoToSetDefaultLocationScreen: () -> Unit
) {
    var selectedServiceId by remember {
        mutableStateOf<String?>(null)
    }
    var openServiceConfigureDialog by remember {
        mutableStateOf(false)
    }
    var openEditEmailDialog by remember {
        mutableStateOf(false)
    }
    var openConfirmDeleteDialog by remember { mutableStateOf(false) }
    var openConfirmExitDialog by remember { mutableStateOf(false) }
    val scrollBehavior = rememberTwoLayerTopAppBarScrollBehavior()
    var popupContent by remember {
        mutableStateOf<StringResource?>(null)
    }
    val documentPicker = rememberDocumentPicker { fileInfo ->
        fileInfo?.let {
            onEvent(ProfilePageUiEvent.SetImageUri(it))
        }
    }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            TwoLayerTopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.profile_page_header),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    AnimatedVisibility(
                        visible = state.selectedTab != ProfileTabs.Main
                    ) {
                        IconButton(onClick = {
                            onEvent(ProfilePageUiEvent.GoToPrevTab)
                        }) {
                            Icon(painterResource(Res.drawable.ic_go_back), contentDescription = "")
                        }
                    }
                },
                additionalContent = {
                    AnimatedVisibility(
                        visible = state.selectedTab == ProfileTabs.Main,
                        enter = expandVertically(
                            tween(durationMillis = 500, delayMillis = 500),
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            spring(stiffness = Spring.StiffnessMediumLow)
                        ),
                        exit = shrinkVertically(
                            spring(stiffness = Spring.StiffnessMedium),
                            shrinkTowards = Alignment.Top
                        ) + fadeOut(
                            spring(stiffness = Spring.StiffnessMediumLow)
                        )
                    ) {
                        if (state.profile != null) {
                            ProfileTopBarAdditionalContent(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                profileImageUrl = state.imageUrl?.path,
                                profileFullName = "${state.profile.firstName} ${state.profile.lastName}",
                                profileAccountStatus = state.profile.accountStatus
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(128.dp)
                                        .shimmerEffect()
                                )
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .shimmerEffect()
                                )

                            }
                        }
                    }
                },
                scrollBehaviour = scrollBehavior
            )
        }
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
                .verticalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            AnimatedContent(
                targetState = state.selectedTab,
                transitionSpec = {
                    if (targetState == ProfileTabs.Main)
                        slideInHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { -it } + fadeIn(
                            spring(stiffness = Spring.StiffnessLow)
                        ) togetherWith
                                slideOutHorizontally(spring(stiffness = Spring.StiffnessMediumLow)) { it } + fadeOut(
                            spring(stiffness = Spring.StiffnessLow)
                        ) using SizeTransform(clip = false)
                    else slideInHorizontally(
                        tween(
                            durationMillis = 500,
                            delayMillis = 500
                        )
                    ) { it } + fadeIn(
                        tween(durationMillis = 500, delayMillis = 500)
                    ) togetherWith
                            slideOutHorizontally(
                                tween(
                                    durationMillis = 500,
                                    delayMillis = 500
                                )
                            ) { -it } + fadeOut(
                        tween(durationMillis = 500, delayMillis = 500)
                    ) using SizeTransform(clip = false)
                }
            ) { selectedTab ->
                when (selectedTab) {
                    ProfileTabs.Main -> {
                        ProfileMainTab(
                            onEditInfoClick = {
                                onEvent(
                                    ProfilePageUiEvent.SetSelectedTab(
                                        ProfileTabs.EditInfo
                                    )
                                )
                            },
                            onGoToSecurityClick = {
                                onEvent(
                                    ProfilePageUiEvent.SetSelectedTab(
                                        ProfileTabs.Security
                                    )
                                )
                            },
                            onDeleteAccountClick = { openConfirmDeleteDialog = true },
                            onExitAccountClick = { openConfirmExitDialog = true },
                            onGoToStatisticsClick = {
                                onEvent(
                                    ProfilePageUiEvent.SetSelectedTab(ProfileTabs.Statistics)
                                )
                            }
                        )
                    }

                    ProfileTabs.Security -> {
                        SecurityTab(
                            login = state.login,
                            loginValid = state.loginValid,
                            onLoginChanged = { onEvent(ProfilePageUiEvent.SetLogin(it)) },
                            onChangeEmailClick = { openEditEmailDialog = true },
                            onCancelEditingLogin = { onEvent(ProfilePageUiEvent.CancelEditing) },
                            onChangePasswordClick = onGoToEditPasswordScreen,
                            onDoneEditingLogin = {
                                if (state.canEditInfo)
                                    onEvent(ProfilePageUiEvent.PublishEditedInfo)
                                else popupContent = Res.string.conflict_error
                            },
                            securityLevel = state.profile?.securityLevel
                                ?: ProfileSecurityLevel.Low
                        )
                    }

                    ProfileTabs.EditInfo -> {
                        EditInfoTab(
                            imageUrl = state.imageUrl?.path,
                            firstName = state.firstName,
                            firstNameValid = state.firstNameValid,
                            lastName = state.lastName,
                            lastNameValid = state.lastNameValid,
                            aboutMe = state.aboutMe,
                            aboutMeValid = state.aboutMeValid,
                            services = state.services,
                            canPublish = state.canEditInfo,
                            desiredPayment = state.desiredPayment,
                            showAsWalker = state.showAsWalker,
                            onFirstNameChanged = { onEvent(ProfilePageUiEvent.SetFirstName(it)) },
                            onLastNameChanged = { onEvent(ProfilePageUiEvent.SetLastName(it)) },
                            onAboutMeChanged = { onEvent(ProfilePageUiEvent.SetAboutMe(it)) },
                            onDesiredPaymentSelected = {
                                onEvent(
                                    ProfilePageUiEvent.SetDesiredPayment(
                                        it
                                    )
                                )
                            },
                            onEditServiceClick = {
                                selectedServiceId = it
                                openServiceConfigureDialog = true
                            },
                            onAddServiceClick = {
                                selectedServiceId = null
                                openServiceConfigureDialog = true
                            },
                            onDeleteServiceClick = { onEvent(ProfilePageUiEvent.RemoveService(it)) },
                            onCancel = { onEvent(ProfilePageUiEvent.CancelEditing) },
                            onPublishResult = {
                                if (state.canEditInfo)
                                    onEvent(ProfilePageUiEvent.PublishEditedInfo)
                                else popupContent = Res.string.conflict_error
                            },
                            onImageClick = {
                                documentPicker.launch(ExtensionGroups.Image.exts)
                            },
                            onEditShowAsWalker = { onEvent(ProfilePageUiEvent.SetShowAsWalker(it)) },
                            onGoToSetDefaultLocation = onGoToSetDefaultLocationScreen
                        )
                    }

                    ProfileTabs.Statistics -> ProfileStatisticsTab(
                        reviewsStats = state.reviewsStats ?: APIResult.Downloading(),
                        complaintsStats = state.complaintsStats ?: APIResult.Downloading(),
                        assignmentsStats = state.assignmentsStats ?: APIResult.Downloading(),
                        selectedAssignmentsStatsPeriod = state.assignmentsStatsDatePeriod,
                        onLoadComplaintsStats = {
                            onEvent(ProfilePageUiEvent.LoadComplaintsStats)
                        },
                        onLoadReviewsStats = {
                            onEvent(ProfilePageUiEvent.LoadReviewsStats)
                        },
                        onLoadAssignmentsStats = {
                            onEvent(ProfilePageUiEvent.LoadAssignmentsStats(it))
                        },
                    )
                }
            }
        }
    }
    if (popupContent != null)
        Popup(
            alignment = Alignment.BottomCenter,
            onDismissRequest = { popupContent = null }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = stringResource(popupContent!!),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    if (openEditEmailDialog) {
        EmailConfigureDialog(
            email = state.email,
            emailValid = state.emailValid,
            code = state.code,
            codeValid = state.codeValid,
            onEmailEdited = { onEvent(ProfilePageUiEvent.SetEmail(it)) },
            onCodeEdited = { onEvent(ProfilePageUiEvent.SetCode(it)) },
            onGenerateCodeClick = { onEvent(ProfilePageUiEvent.SendConfirmationCode) },
            onDismissRequest = {
                openEditEmailDialog = false
                onEvent(ProfilePageUiEvent.CancelEditing)
            },
            onDoneClick = { onEvent(ProfilePageUiEvent.ConfirmEmail) }
        )
    }
    if (openServiceConfigureDialog) {
        UserServiceConfigureDialog(
            onDismissRequest = { openServiceConfigureDialog = false },
            onDoneEditing = { service, additionalInfo, payment ->
                selectedServiceId?.let {
                    onEvent(ProfilePageUiEvent.EditService(it, service, additionalInfo, payment))
                } ?: onEvent(ProfilePageUiEvent.AddService(service, additionalInfo, payment))
                openEditEmailDialog = false
            },
            initialValue = selectedServiceId?.let { selectedId -> state.services.firstOrNull { it.id == selectedId } }
        )
    }
    if (openConfirmExitDialog) {
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_logout_text_label),
            icon = painterResource(Res.drawable.ic_exit),
            onConfirm = { onEvent(ProfilePageUiEvent.ExitAccount) },
            onDismissRequest = { openConfirmExitDialog = false }
        )
    }
    if (openConfirmDeleteDialog) {
        PetWalkerAlertDialog(
            title = stringResource(Res.string.are_sure_label),
            text = stringResource(Res.string.confirm_delete_account_label),
            icon = painterResource(Res.drawable.ic_no_account),
            onConfirm = { onEvent(ProfilePageUiEvent.DeleteAccount) },
            onDismissRequest = { openConfirmDeleteDialog = false }
        )
    }
}

@Composable
fun ProfileTopBarAdditionalContent(
    modifier: Modifier = Modifier,
    profileFullName: String,
    profileImageUrl: String?,
    profileAccountStatus: AccountStatus,
) {
    val deviceConfig =
        DeviceConfiguration.fromWindowSizeClass(currentWindowAdaptiveInfo().windowSizeClass)

    val content: @Composable () -> Unit = {
        PetWalkerAsyncImage(
            imageUrl = profileImageUrl,
            asyncImageModifier = Modifier
                .height(
                    when (deviceConfig) {
                        in listOf(
                            DeviceConfiguration.MOBILE_PORTRAIT,
                            DeviceConfiguration.TABLET_PORTRAIT
                        ) -> 250.dp

                        in listOf(
                            DeviceConfiguration.TABLET_LANDSCAPE,
                            DeviceConfiguration.DESKTOP
                        ) -> 300.dp

                        else -> 140.dp
                    }
                )
                .clip(RoundedCornerShape(12.dp)),
        )
        Column {

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = profileFullName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(profileAccountStatus.displayName),
                style = MaterialTheme.typography.bodyLarge,
                color = when (profileAccountStatus) {
                    AccountStatus.Pending -> Color.Unspecified
                    AccountStatus.Verified -> Color.Green
                    AccountStatus.Banned -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
    if (deviceConfig in listOf(
            DeviceConfiguration.MOBILE_PORTRAIT,
            DeviceConfiguration.TABLET_PORTRAIT
        )
    )
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    else Row {
        content()
    }
}