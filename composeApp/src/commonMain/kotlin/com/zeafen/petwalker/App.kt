package com.zeafen.petwalker

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zeafen.petwalker.data.helpers.containsAny
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureViewModel
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsViewModel
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsViewModel
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsLoadGroup
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsPageUiEvent
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsPageViewModel
import com.zeafen.petwalker.presentation.auth.AuthUiEvent
import com.zeafen.petwalker.presentation.auth.AuthViewModel
import com.zeafen.petwalker.presentation.auth.ForgotPasswordUiEvent
import com.zeafen.petwalker.presentation.auth.ForgotPasswordViewModel
import com.zeafen.petwalker.presentation.channel.ChannelDetailsUiEvent
import com.zeafen.petwalker.presentation.channel.ChannelDetailsViewModel
import com.zeafen.petwalker.presentation.home.HomePageUiEvent
import com.zeafen.petwalker.presentation.home.HomePageViewModel
import com.zeafen.petwalker.presentation.map.MapPresentationType
import com.zeafen.petwalker.presentation.map.MapScreenUiEvent
import com.zeafen.petwalker.presentation.map.MapScreenViewModel
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureUiEvent
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureViewModel
import com.zeafen.petwalker.presentation.pets.petDetails.PetDetailsUiEvent
import com.zeafen.petwalker.presentation.pets.petDetails.PetDetailsViewModel
import com.zeafen.petwalker.presentation.pets.petsList.PetsPageUiEvent
import com.zeafen.petwalker.presentation.pets.petsList.PetsPageViewModel
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureUiEvent
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureViewModel
import com.zeafen.petwalker.presentation.posts.postDetailsPage.PostDetailsUiEvent
import com.zeafen.petwalker.presentation.posts.postDetailsPage.PostDetailsViewModel
import com.zeafen.petwalker.presentation.posts.postsList.PostsPageUiEvent
import com.zeafen.petwalker.presentation.posts.postsList.PostsPageViewModel
import com.zeafen.petwalker.presentation.profile.ProfilePageUiEvent
import com.zeafen.petwalker.presentation.profile.ProfilePageViewModel
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureViewModel
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureViewModel
import com.zeafen.petwalker.presentation.standard.navigation.NavStop
import com.zeafen.petwalker.presentation.standard.navigation.NavigationRoutes
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsViewModel
import com.zeafen.petwalker.presentation.walkers.walkersList.WalkersPageUiEvent
import com.zeafen.petwalker.presentation.walkers.walkersList.WalkersPageViewModel
import com.zeafen.petwalker.ui.assignments.AssignmentsPage
import com.zeafen.petwalker.ui.assignments.assignmentConfigure.AssignmentConfigurePage
import com.zeafen.petwalker.ui.assignments.assignmentDetails.AssignmentDetailsPage
import com.zeafen.petwalker.ui.assignments.recruitmentsPage.RecruitmentsPage
import com.zeafen.petwalker.ui.auth.ForgotPasswordPage
import com.zeafen.petwalker.ui.auth.SignInPage
import com.zeafen.petwalker.ui.auth.SignUpPage
import com.zeafen.petwalker.ui.channel.ChannelDetailsPage
import com.zeafen.petwalker.ui.complaints.ComplaintConfigurePage
import com.zeafen.petwalker.ui.home.HomePage
import com.zeafen.petwalker.ui.map.PetWalkerMap
import com.zeafen.petwalker.ui.pets.PetsPage
import com.zeafen.petwalker.ui.pets.petConfigure.PetConfigurePage
import com.zeafen.petwalker.ui.pets.petDetails.PetDetailsPage
import com.zeafen.petwalker.ui.posts.PostDetailsPage
import com.zeafen.petwalker.ui.posts.PostsPage
import com.zeafen.petwalker.ui.posts.postConfigure.PostConfigurePage
import com.zeafen.petwalker.ui.profile.ProfilePage
import com.zeafen.petwalker.ui.reviews.ReviewConfigurePage
import com.zeafen.petwalker.ui.standard.elements.LoadingResultPage
import com.zeafen.petwalker.ui.standard.elements.PetWalkerBottomBar
import com.zeafen.petwalker.ui.theme.PetWalker_theme
import com.zeafen.petwalker.ui.walkers.WalkersPage
import com.zeafen.petwalker.ui.walkers.walkerDetails.WalkerDetailsPage
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import ovh.plrapps.mapcompose.api.ExperimentalClusteringApi
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.ic_account_box
import petwalker.composeapp.generated.resources.ic_assignment
import petwalker.composeapp.generated.resources.ic_home
import petwalker.composeapp.generated.resources.ic_online
import petwalker.composeapp.generated.resources.ic_pet
import petwalker.composeapp.generated.resources.ic_posts
import petwalker.composeapp.generated.resources.ic_walk

@OptIn(ExperimentalMaterial3Api::class, ExperimentalClusteringApi::class)
@Composable
fun App() {
    val navController = rememberNavController()
    PetWalker_theme {
        Surface {
            Scaffold(
                bottomBar = {
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    if (backStackEntry != null &&
                        !(backStackEntry!!.destination.route?.containsAny(
                            true,
                            NavigationRoutes.AuthNavigation.toString(),
                            NavigationRoutes.SignIn.toString(),
                            NavigationRoutes.SignUp.toString(),
                            NavigationRoutes.Authorise.toString(),
                        ) ?: true)
                    )
                        PetWalkerBottomBar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            centerButton = {
                                FilledIconButton(
                                    modifier = Modifier
                                        .size(64.dp),
                                    onClick = {
                                        navController.navigate(NavigationRoutes.HomePage) {
                                            popUpTo(NavigationRoutes.ProfileNavigation) {
                                                inclusive = false
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_home),
                                        contentDescription = null
                                    )
                                }
                            },
                            navStops = listOf(
                                NavStop(
                                    NavigationRoutes.AssignmentsPage(false),
                                    Res.drawable.ic_assignment
                                ),
                                NavStop(
                                    NavigationRoutes.WalkersPage,
                                    Res.drawable.ic_walk
                                ),
                                NavStop(
                                    NavigationRoutes.PostsPage,
                                    Res.drawable.ic_posts
                                ),
                                NavStop(
                                    NavigationRoutes.PetsPage,
                                    Res.drawable.ic_pet
                                ),
                                NavStop(
                                    NavigationRoutes.RecruitmentsPage(),
                                    Res.drawable.ic_online
                                ),
                                NavStop(
                                    NavigationRoutes.ProfilePage,
                                    Res.drawable.ic_account_box
                                ),
                            ),
                            onNavStopClick = {
                                navController.navigate(it) {
                                    popUpTo(NavigationRoutes.ProfileNavigation) {
                                        inclusive = false
                                    }
                                }
                            }
                        )
                }
            ) { innerPadding ->
                NavHost(
                    modifier = Modifier
                        .padding(innerPadding),
                    navController = navController,
                    startDestination = NavigationRoutes.AuthNavigation
                ) {
                    navigation<NavigationRoutes.AuthNavigation>(
                        startDestination = NavigationRoutes.Authorise
                    ) {
                        composable<NavigationRoutes.Authorise> {
                            val parentEntry =
                                remember { navController.getBackStackEntry<NavigationRoutes.AuthNavigation>() }
                            val viewModel = koinViewModel<AuthViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(AuthUiEvent.Authorize)
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            if (state.result != null)
                                LoadingResultPage(
                                    state = state.result!!,
                                    onSuccessResult = {
                                        navController.navigate(NavigationRoutes.HomePage) {
                                            popUpTo(NavigationRoutes.AuthNavigation) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onReloadAfterError = {
                                        if (it == NetworkError.UNAUTHORIZED)
                                            navController.navigate(NavigationRoutes.SignUp) {
                                                popUpTo(NavigationRoutes.AuthNavigation) {
                                                    inclusive = true
                                                }
                                            }
                                        else
                                            navController.navigate(NavigationRoutes.SignIn) {
                                                popUpTo(NavigationRoutes.AuthNavigation) {
                                                    inclusive = true
                                                }
                                            }
                                    },

                                    )

                        }
                        composable<NavigationRoutes.SignUp> {
                            val parentEntry =
                                remember { navController.getBackStackEntry<NavigationRoutes.AuthNavigation>() }
                            val viewModel = koinViewModel<AuthViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            if (state.result == null)
                                SignUpPage(
                                    authState = state,
                                    onEvent = viewModel::onEvent,
                                    onGoToSignInClick = {
                                        navController.navigate(NavigationRoutes.SignIn) {
                                            popUpTo(NavigationRoutes.AuthNavigation) {
                                                inclusive = false
                                            }
                                        }
                                    }
                                )
                            else LoadingResultPage(
                                state = state.result!!,
                                onSuccessResult = {
                                    navController.navigate(NavigationRoutes.HomePage) {
                                        popUpTo(NavigationRoutes.AuthNavigation) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onReloadAfterError = { viewModel.onEvent(AuthUiEvent.ClearResult) }
                            )
                        }
                        composable<NavigationRoutes.SignIn> {
                            val parentEntry =
                                remember { navController.getBackStackEntry<NavigationRoutes.AuthNavigation>() }
                            val viewModel = koinViewModel<AuthViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            if (state.result == null)
                                SignInPage(
                                    authState = state,
                                    onEvent = viewModel::onEvent,
                                    onGoToSignUpClick = {
                                        navController.navigate(NavigationRoutes.SignUp) {
                                            popUpTo(NavigationRoutes.AuthNavigation) {
                                                inclusive = false
                                            }
                                        }
                                    },
                                    onGoToForgotPasswordClick = {
                                        navController.navigate(NavigationRoutes.SignIn) {
                                            popUpTo(NavigationRoutes.AuthNavigation)
                                        }
                                    }
                                )
                            else LoadingResultPage(
                                state = state.result!!,
                                onSuccessResult = {
                                    navController.navigate(NavigationRoutes.HomePage) {
                                        popUpTo(NavigationRoutes.AuthNavigation) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onReloadAfterError = { viewModel.onEvent(AuthUiEvent.ClearResult) }
                            )
                        }
                        composable<NavigationRoutes.ForgotPasswordPage> {
                            val parentEntry =
                                remember { navController.getBackStackEntry<NavigationRoutes.ForgotPasswordPage>() }
                            val viewModel = koinViewModel<ForgotPasswordViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            if (state.result == null)
                                ForgotPasswordPage(
                                    forgotPasswState = state,
                                    onEvent = viewModel::onEvent,
                                    onGoBackClick = {
                                        navController.navigateUp()
                                    }
                                )
                            else LoadingResultPage(
                                state = state.result!!,
                                onSuccessResult = {
                                    navController.navigate(NavigationRoutes.HomePage) {
                                        popUpTo(NavigationRoutes.AuthNavigation) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onReloadAfterError = { viewModel.onEvent(ForgotPasswordUiEvent.ClearResult) }
                            )

                        }
                    }

                    navigation<NavigationRoutes.ProfileNavigation>(
                        startDestination = NavigationRoutes.HomePage
                    ) {
                        composable<NavigationRoutes.HomePage> {
                            val viewModel = koinViewModel<HomePageViewModel>()
                            val state by viewModel.state.collectAsState()

                            val isLoading = remember {
                                derivedStateOf {
                                    state.isLoadingWalkers
                                            || state.bestWalker is APIResult.Downloading
                                            || state.bestWalkerStatistics is APIResult.Downloading
                                }
                            }

                            PullToRefreshBox(
                                isRefreshing = isLoading.value,
                                onRefresh = {
                                    viewModel.onEvent(HomePageUiEvent.LoadData)
                                }
                            ) {
                                HomePage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onGoToBestWalker = {
                                        navController.navigate(
                                            NavigationRoutes.WalkerInfoPage(
                                                it
                                            )
                                        )
                                    },
                                    onGoToWalkers = { navController.navigate(NavigationRoutes.WalkersPage) },
                                    onGoToOwnPets = { navController.navigate(NavigationRoutes.PetsPage) },
                                    onGoToRecruitmentsAsOwner = {
                                        navController.navigate(
                                            NavigationRoutes.RecruitmentsPage(RecruitmentsLoadGroup.AsOwner)
                                        )
                                    },
                                    onGoToRecruitmentsAsWalker = {
                                        navController.navigate(
                                            NavigationRoutes.RecruitmentsPage(RecruitmentsLoadGroup.AsWalker)
                                        )
                                    },
                                    onGoToAssignments = {
                                        navController.navigate(
                                            NavigationRoutes.AssignmentsPage(
                                                false
                                            )
                                        )
                                    },
                                    onGoToOwnAssignments = {
                                        navController.navigate(
                                            NavigationRoutes.AssignmentsPage(
                                                true
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        composable<NavigationRoutes.ProfilePage> {
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.ProfileNavigation>()
                            }
                            val viewModel = koinViewModel<ProfilePageViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val exitAccount by viewModel.exitAccount.collectAsStateWithLifecycle()

                            LaunchedEffect(exitAccount) {
                                if (exitAccount)
                                    navController.navigate(NavigationRoutes.AuthNavigation) {
                                        popUpTo<NavigationRoutes.AuthNavigation> {
                                            inclusive = true
                                        }
                                    }
                            }

                            PullToRefreshBox(
                                isRefreshing = state.profileLoadingResult is APIResult.Downloading,
                                onRefresh = { viewModel.onEvent(ProfilePageUiEvent.LoadProfile) }
                            ) {
                                ProfilePage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onGoToEditPasswordScreen = {
                                        navController.navigate(
                                            NavigationRoutes.ForgotPasswordPage
                                        )
                                    },
                                    onGoToSetDefaultLocationScreen = {
                                        navController.navigate(
                                            NavigationRoutes.MapPage(
                                                Json.encodeToString(
                                                    MapPresentationType.PickLocation
                                                )
                                            )
                                        )
                                    }
                                )
                            }

                        }
                        composable<NavigationRoutes.RecruitmentsPage> {
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.ProfileNavigation>()
                            }
                            val viewModel = koinViewModel<RecruitmentsPageViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val selectedGroup =
                                it.toRoute<NavigationRoutes.RecruitmentsPage>().initialTab

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(RecruitmentsPageUiEvent.SetFilters(selectedGroup))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            RecruitmentsPage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onSeeWalkerClick = {
                                    navController.navigate(
                                        NavigationRoutes.WalkerInfoPage(
                                            it
                                        )
                                    )
                                },
                                onSeeAssignmentClick = {
                                    navController.navigate(
                                        NavigationRoutes.AssignmentDetailsPage(
                                            it
                                        )
                                    )
                                }
                            )
                        }
                        composable<NavigationRoutes.MapPage> {
                            val args = it.toRoute<NavigationRoutes.MapPage>().loadInfoJs

                            val parentEntry = remember {
                                navController.getBackStackEntry(NavigationRoutes.ProfileNavigation)
                            }

                            val viewModel = koinViewModel<MapScreenViewModel>(
                                viewModelStoreOwner = parentEntry
                            )

                            LaunchedEffect(args) {
                                val data = try {
                                    Json.decodeFromString<MapPresentationType>(args)
                                } catch (_: Exception) {
                                    navController.navigateUp()
                                    null
                                }
                                if (data != null)
                                    viewModel.onEvent(MapScreenUiEvent.SetPresentationType(data))
                            }

                            val state by viewModel.state.collectAsState()
                            PetWalkerMap(
                                state = state,
                                onEvent = viewModel::onEvent,
                                streamProvider = viewModel.tileStream,
                                onOverlayClick = {
                                    when (state.presentationType) {
                                        is MapPresentationType.Assignment -> navController.navigateUp()
                                        is MapPresentationType.Assignments -> navController.navigate(
                                            NavigationRoutes.AssignmentDetailsPage(it)
                                        )

                                        is MapPresentationType.Walker -> navController.navigateUp()
                                        is MapPresentationType.Walkers -> navController.navigate(
                                            NavigationRoutes.WalkerInfoPage(it)
                                        )

                                        else -> {}
                                    }
                                },
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                    }

                    navigation<NavigationRoutes.WalkersNavigation>(
                        startDestination = NavigationRoutes.WalkersPage
                    ) {
                        composable<NavigationRoutes.WalkersPage> {
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.WalkersNavigation>()
                            }
                            val viewModel = koinViewModel<WalkersPageViewModel>(
                                viewModelStoreOwner = parentEntry
                            )
                            val state by viewModel.state.collectAsStateWithLifecycle()


                            PullToRefreshBox(
                                isRefreshing = state.walkers is APIResult.Downloading,
                                onRefresh = { viewModel.onEvent(WalkersPageUiEvent.LoadWalkers(state.lastSelectedPage)) }
                            ) {
                                WalkersPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onWalkerCardClick = {
                                        navController.navigate(
                                            NavigationRoutes.WalkerInfoPage(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        composable<NavigationRoutes.WalkerInfoPage> { entry ->
                            val viewModel = koinViewModel<WalkerDetailsViewModel>(
                                viewModelStoreOwner = entry
                            )
                            val id = entry.toRoute<NavigationRoutes.WalkerInfoPage>().userId
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(WalkerDetailsPageUiEvent.LoadWalker(id))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            WalkerDetailsPage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBackClick = { navController.navigateUp() },
                                onAddComplaintClick = {
                                    navController.navigate(
                                        NavigationRoutes.ComplaintConfigurePage(
                                            null,
                                            id
                                        )
                                    )
                                },
                                onGoToAssignmentClick = {
                                    navController.navigate(
                                        NavigationRoutes.AssignmentDetailsPage(
                                            it
                                        )
                                    )
                                }
                            )
                        }
                        composable<NavigationRoutes.ComplaintConfigurePage> {
                            val route = it.toRoute<NavigationRoutes.ComplaintConfigurePage>()
                            val viewModel = koinViewModel<ComplaintConfigureViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(
                                    ComplaintConfigureUiEvent.InitializeComplaint(
                                        route.complaintId,
                                        route.userId
                                    )
                                )
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            ComplaintConfigurePage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onCancelClick = { navController.navigateUp() }
                            )
                        }
                    }

                    navigation<NavigationRoutes.AssignmentsNavigation>(
                        startDestination = NavigationRoutes.AssignmentsPage(false)
                    ) {
                        composable<NavigationRoutes.AssignmentsPage> {
                            val loadOwn = it.toRoute<NavigationRoutes.AssignmentsPage>().loadOwn
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.AssignmentsNavigation>()
                            }
                            val viewModel = koinViewModel<AssignmentsViewModel>(
                                viewModelStoreOwner = parentEntry
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(AssignmentsUiEvent.SetLoadType(loadOwn))
                            }
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            PullToRefreshBox(
                                isRefreshing = state.assignments is APIResult.Downloading,
                                onRefresh = {
                                    viewModel.onEvent(
                                        AssignmentsUiEvent.LoadAssignments(
                                            state.lastSelectedPage
                                        )
                                    )
                                }
                            ) {
                                AssignmentsPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onAssignmentClick = {
                                        navController.navigate(
                                            NavigationRoutes.AssignmentDetailsPage(
                                                it
                                            )
                                        )
                                    },
                                    onAddAssignmentClick = {
                                        navController.navigate(
                                            NavigationRoutes.AssignmentConfigurePage(null)
                                        )
                                    },
                                    onEditAssignmentClick = {
                                        navController.navigate(
                                            NavigationRoutes.AssignmentConfigurePage(it)
                                        )
                                    }
                                )
                            }
                        }
                        composable<NavigationRoutes.AssignmentDetailsPage> {
                            val assignmentId =
                                it.toRoute<NavigationRoutes.AssignmentDetailsPage>().assignmentId
                            val viewModel = koinViewModel<AssignmentDetailsViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(
                                    AssignmentDetailsUiEvent.LoadAssignment(
                                        assignmentId
                                    )
                                )
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            AssignmentDetailsPage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBackClick = { navController.navigateUp() },
                                onGoToChannelClick = {
                                    navController.navigate(
                                        NavigationRoutes.AssignmentChannelPage(
                                            it
                                        )
                                    )
                                },
                                onLeaveReviewClick = {
                                    navController.navigate(
                                        NavigationRoutes.ReviewConfigurePage(
                                            null,
                                            it
                                        )
                                    )
                                }
                            )
                        }
                        composable<NavigationRoutes.AssignmentConfigurePage> {
                            val assignmentId =
                                it.toRoute<NavigationRoutes.AssignmentConfigurePage>().configureAssignmentId
                            val viewModel = koinViewModel<AssignmentConfigureViewModel>(
                                viewModelStoreOwner = it
                            )
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(
                                    AssignmentConfigureUiEvent.SetEditedAssignmentId(
                                        assignmentId
                                    )
                                )
                            }

                            val exitPageState by viewModel.exitPage.collectAsStateWithLifecycle()
                            LaunchedEffect(exitPageState) {
                                if (exitPageState && navController.currentBackStackEntry == it)
                                    navController.navigateUp()
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            AssignmentConfigurePage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBackClick = { navController.navigateUp() },
                            )
                        }
                        composable<NavigationRoutes.AssignmentChannelPage> {
                            val assignmentId =
                                it.toRoute<NavigationRoutes.AssignmentChannelPage>().channelAssignmentId
                            val viewModel = koinViewModel<ChannelDetailsViewModel>(
                                viewModelStoreOwner = it
                            )
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(ChannelDetailsUiEvent.LoadChannel(assignmentId))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            ChannelDetailsPage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                        composable<NavigationRoutes.ReviewConfigurePage> {
                            val route = it.toRoute<NavigationRoutes.ReviewConfigurePage>()
                            val viewModel = koinViewModel<ReviewConfigureViewModel>(
                                viewModelStoreOwner = it
                            )
                            LaunchedEffect(Unit) {
                                viewModel.onEvent(
                                    ReviewConfigureUiEvent.InitializeReview(
                                        route.reviewId,
                                        route.assignmentId
                                    )
                                )
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            ReviewConfigurePage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onCancelClick = { navController.navigateUp() }
                            )
                        }
                    }

                    navigation<NavigationRoutes.PetsNavigation>(
                        startDestination = NavigationRoutes.PetsPage
                    ) {
                        composable<NavigationRoutes.PetsPage> {
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.PetsNavigation>()
                            }
                            val viewModel = koinViewModel<PetsPageViewModel>(
                                viewModelStoreOwner = parentEntry
                            )

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            PullToRefreshBox(
                                isRefreshing = state.pets is APIResult.Downloading,
                                onRefresh = {
                                    viewModel.onEvent(PetsPageUiEvent.LoadOwnPets(state.lastSelectedPage))
                                }
                            ) {
                                PetsPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onPetClick = {
                                        navController.navigate(
                                            NavigationRoutes.PetInfoPage(
                                                it
                                            )
                                        )
                                    },
                                    onAddPetClick = {
                                        navController.navigate(
                                            NavigationRoutes.PetConfigurePage(
                                                null
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        composable<NavigationRoutes.PetInfoPage> {
                            val petId = it.toRoute<NavigationRoutes.PetInfoPage>().petId
                            val viewModel = koinViewModel<PetDetailsViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(PetDetailsUiEvent.LoadPet(petId))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            PullToRefreshBox(
                                isRefreshing = state.pet is APIResult.Downloading,
                                onRefresh = { viewModel.onEvent(PetDetailsUiEvent.LoadPet(petId)) }
                            ) {
                                PetDetailsPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onBackClick = { navController.navigateUp() },
                                    onEditPetClick = {
                                        navController.navigate(
                                            NavigationRoutes.PetConfigurePage(
                                                it
                                            )
                                        )
                                    }
                                )
                            }

                        }
                        composable<NavigationRoutes.PetConfigurePage> {
                            val petId =
                                it.toRoute<NavigationRoutes.PetConfigurePage>().configurePetId
                            val viewModel = koinViewModel<PetConfigureViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId(petId))
                            }

                            val exitPageState by viewModel.exitPage.collectAsStateWithLifecycle()
                            LaunchedEffect(exitPageState) {
                                if (exitPageState && navController.currentBackStackEntry == it)
                                    navController.navigateUp()
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            PetConfigurePage(
                                state = state,
                                onEvent = viewModel::onEvent,
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                    }

                    navigation<NavigationRoutes.PostsNavigation>(
                        startDestination = NavigationRoutes.PostsPage
                    ) {
                        composable<NavigationRoutes.PostsPage> {
                            val parentEntry = remember {
                                navController.getBackStackEntry<NavigationRoutes.PostsNavigation>()
                            }
                            val viewModel = koinViewModel<PostsPageViewModel>(
                                viewModelStoreOwner = parentEntry
                            )

                            val state by viewModel.state.collectAsStateWithLifecycle()

                            PullToRefreshBox(
                                isRefreshing = state.posts is APIResult.Downloading,
                                onRefresh = { viewModel.onEvent(PostsPageUiEvent.LoadPosts(state.lastSelectedPage)) }
                            ) {
                                PostsPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onAddPostClick = {
                                        navController.navigate(
                                            NavigationRoutes.PostConfigurePage(
                                                null
                                            )
                                        )
                                    },
                                    onGoToPostClick = {
                                        navController.navigate(NavigationRoutes.PostInfoPage(it))
                                    }
                                )
                            }
                        }
                        composable<NavigationRoutes.PostInfoPage> {
                            val postId = it.toRoute<NavigationRoutes.PostInfoPage>().postId
                            val viewModel = koinViewModel<PostDetailsViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(PostDetailsUiEvent.LoadPost(postId))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            PullToRefreshBox(
                                isRefreshing = state.post is APIResult.Downloading,
                                onRefresh = { viewModel.onEvent(PostDetailsUiEvent.LoadPost(postId)) }
                            ) {
                                PostDetailsPage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onBackClick = { navController.navigateUp() },
                                    onGoToCommentaryRoot = { TODO("Add commentary root screen") }
                                )
                            }
                        }
                        composable<NavigationRoutes.PostConfigurePage> {
                            val postId = it.toRoute<NavigationRoutes.PostInfoPage>().postId
                            val viewModel = koinViewModel<PostConfigureViewModel>(
                                viewModelStoreOwner = it
                            )

                            LaunchedEffect(Unit) {
                                viewModel.onEvent(PostConfigureUiEvent.SetSelectedPostId(postId))
                            }

                            val state by viewModel.state.collectAsStateWithLifecycle()
                            if (state.postLoadingResult == null)
                                PostConfigurePage(
                                    state = state,
                                    onEvent = viewModel::onEvent,
                                    onBackClick = { navController.navigateUp() },
                                )
                            else LoadingResultPage(
                                state = state.postLoadingResult!!,
                                onReloadAfterError = {
                                    viewModel.onEvent(
                                        PostConfigureUiEvent.SetSelectedPostId(
                                            postId
                                        )
                                    )
                                },
                                onSuccessResult = { viewModel.onEvent(PostConfigureUiEvent.ClearResult) }
                            )
                        }
                    }
                }
            }
        }
    }
}