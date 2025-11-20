package com.zeafen.petwalker.presentation.profile

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Profile
import com.zeafen.petwalker.domain.models.api.users.ProfileSecurityLevel
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ProfileRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.incorrect_length_least_error
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.login_taken_error_txt
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ProfileViewModelTest {
    private val profileRepositoryMock = mock<ProfileRepository>()
    private val usersRepositoryMock = mock<UsersRepository>()
    private val authDataStoreMock = mock<AuthDataStoreRepository>()
    private val reviewsRepositoryMock = mock<ReviewsRepository>()


    @BeforeTest
    fun initMocks() {
        every { authDataStoreMock.authDataStoreFlow } returns flowOf(
            UserInfo(
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                APILocation(0.0, 0.0),
                TokenResponse(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum dolor sit amet"
                ),
                "Lorem ipsum dolor sit amet"
            )
        )

        everySuspend { profileRepositoryMock.getProfile() } returns APIResult.Succeed(
            Profile(
                login = "Lorem ipsum",
                firstName = "Lorem",
                lastName = "ipsum",
                aboutMe = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                imageUrl = null,
                email = "Lorem@ipsum.com",
                phone = null,
                accountStatus = AccountStatus.Pending,
                isOnline = false,
                showAsWalker = true,
                services = emptyList(),
                securityLevel = ProfileSecurityLevel.Low,
                passwordLastChanged = null
            )
        )
        everySuspend { usersRepositoryMock.loginExists(any()) } returns APIResult.Succeed(false)
    }

    //section main
    @Test
    fun profileViewModel_setSelectedTab_EditInfo() = runTest {
        //define
        val expectedTab = ProfileTabs.EditInfo

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetSelectedTab(expectedTab))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertEquals(expectedTab, actual.selectedTab)
    }

    @Test
    fun profileViewModel_gotToPrevTab_Main() = runTest {
        //define
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetSelectedTab(ProfileTabs.EditInfo))
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(ProfilePageUiEvent.GoToPrevTab)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertEquals(ProfileTabs.Main, actual.selectedTab)
    }

    //editing info
    @Test
    fun profileViewModel_setLogin_existingOne_validationFailed() = runTest {
        //defining
        val expectedLogin = "Lorem_ipsum_dolor_sit_amet"
        everySuspend { usersRepositoryMock.loginExists(expectedLogin) } returns APIResult.Succeed(
            true
        )

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin(expectedLogin))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedLogin, actual.login)
            assertFalse(actual.loginValid.isValid)
            assertEquals(Res.string.login_taken_error_txt, actual.loginValid.errorResId)
        }
    }

    @Test
    fun profileViewModel_setLogin_Less10Symbols_validationFailed() = runTest {
        //defining
        val expectedLogin = "Lorem"

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin(expectedLogin))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedLogin, actual.login)
            assertFalse(actual.loginValid.isValid)
            assertEquals(Res.string.incorrect_length_least_error, actual.loginValid.errorResId)
        }
    }

    @Test
    fun profileViewModel_setLogin_More50Symbols_validationFailed() = runTest {
        //defining
        val expectedLogin =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin(expectedLogin))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedLogin, actual.login)
            assertFalse(actual.loginValid.isValid)
            assertEquals(Res.string.incorrect_length_max_error, actual.loginValid.errorResId)
        }
    }

    @Test
    fun profileViewModel_setLogin_validInput_validationPassed() = runTest {
        //defining
        val expectedLogin =
            "Lorem_ipsum_dolor"

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin(expectedLogin))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedLogin, actual.login)
            assertTrue(actual.loginValid.isValid)
        }
    }

    @Test
    fun profileViewModel_setFirstLastName_Blank_validationFailed() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("  "))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName(" "))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals("  ", actual.firstName)
            assertFalse(actual.firstNameValid.isValid)

            assertEquals(" ", actual.lastName)
            assertFalse(actual.lastNameValid.isValid)
        }
    }

    @Test
    fun profileViewModel_setFirstLastName_Larger200Symbols_validationFailed() = runTest {
        //defining
        val expectedFirstName =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis."
        val expectedLastName =
            "Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar."
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName(expectedFirstName))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName(expectedLastName))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedFirstName, actual.firstName)
            assertFalse(actual.firstNameValid.isValid)

            assertEquals(expectedLastName, actual.lastName)
            assertFalse(actual.lastNameValid.isValid)
        }
    }

    @Test
    fun profileViewModel_setFirstLastName_ValidInput_validationPassed() = runTest {
        //defining
        val expectedFirstName =
            "Lorem ipsum dolor"
        val expectedLastName =
            "Quisque convallis"
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName(expectedFirstName))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName(expectedLastName))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedFirstName, actual.firstName)
            assertTrue(actual.firstNameValid.isValid)

            assertEquals(expectedLastName, actual.lastName)
            assertTrue(actual.lastNameValid.isValid)
        }
    }

    @Test
    fun profileViewModel_setAboutMe_More500Symbols_validationFailed() = runTest {
        //defining
        val expectedAboutMe =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar. Integer feugiat ornare dui vitae volutpat. Mauris odio massa, vestibulum in pharetra ac, fringilla sed est."
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe(expectedAboutMe))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedAboutMe, actual.aboutMe)
            assertFalse(actual.aboutMeValid.isValid)
        }
    }

    @Test
    fun profileViewModel_setAboutMe_ValidInput_validationPassed() = runTest {
        //defining
        val expectedAboutMe =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet."
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe(expectedAboutMe))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertEquals(expectedAboutMe, actual.aboutMe)
            assertTrue(actual.aboutMeValid.isValid)
        }
    }

    //testing services editing
    @Test
    fun profileViewModel_AddService_ListEdited() = runTest {
        //defining
        val expectedServiceType = ServiceType.House_Sitting
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(1)
            assertEquals(expectedServiceType, actual.services.first().service)
            assertEquals(expectedAdditionalInfo, actual.services.first().additionalInfo)
            assertEquals(expectedPayment, actual.services.first().payment)
        }
    }

    @Test
    fun profileViewModel_AddService_AddedSameHouseSitting_OnlyFirstAdded() = runTest {
        //defining
        val expectedServiceType = ServiceType.House_Sitting
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                "Lorem ipsum",
                312f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(1)
            assertEquals(expectedServiceType, actual.services.first().service)
            assertEquals(expectedAdditionalInfo, actual.services.first().additionalInfo)
            assertEquals(expectedPayment, actual.services.first().payment)
        }
    }

    @Test
    fun profileViewModel_AddService_AddedSameOther_BothAdded() = runTest {
        //defining
        val expectedServiceType = ServiceType.Other
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedAdditionalInfo2 = "Lorem ipsum"
        val expectedPayment = 123f
        val expectedPayment2 = 312f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo2,
                expectedPayment2
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(2)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo2, expectedPayment2),
                arrayOf(
                    actual.services.last().service,
                    actual.services.last().additionalInfo,
                    actual.services.last().payment
                )
            )
        }
    }

    @Test
    fun profileViewModel_AddService_AddedOtherHouseSitting_BothAdded() = runTest {
        //defining
        val expectedServiceType = ServiceType.Walking
        val expectedServiceType2 = ServiceType.House_Sitting
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedAdditionalInfo2 = "Lorem ipsum"
        val expectedPayment = 123f
        val expectedPayment2 = 312f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType2,
                expectedAdditionalInfo2,
                expectedPayment2
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(2)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType2, expectedAdditionalInfo2, expectedPayment2),
                arrayOf(
                    actual.services.last().service,
                    actual.services.last().additionalInfo,
                    actual.services.last().payment
                )
            )
        }
    }

    //testing editing service
    @Test
    fun profileViewModel_editService_EditNonExisted_NothingHappened() = runTest {
        //defining
        val expectedServiceType = ServiceType.Walking
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(
            ProfilePageUiEvent.EditService(
                "test-id",
                ServiceType.House_Sitting,
                "Lorem ipsum",
                312f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(1)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
        }
    }

    @Test
    fun profileViewModel_editService_EditExistingService_ServiceNotEdited() = runTest {
        //defining
        val expectedServiceType = ServiceType.House_Sitting
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.Walking,
                "Lorem ipsum",
                321f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        val state = profileViewModel.state.first()
        profileViewModel.onEvent(
            ProfilePageUiEvent.EditService(
                state.services.first().id,
                ServiceType.Walking,
                "Lorem ipsum",
                312f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(2)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
        }
    }

    @Test
    fun profileViewModel_editService_EditExistingServiceOther_ServiceEdited() = runTest {
        //defining
        val expectedServiceType = ServiceType.Other
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.Walking,
                "LoremIpsum",
                456f
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                expectedServiceType,
                "Sed at nibh ut ante suscipit porta in et nisl.",
                321f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        val state = profileViewModel.state.first()
        profileViewModel.onEvent(
            ProfilePageUiEvent.EditService(
                state.services.first().id,
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(2)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
        }
    }

    @Test
    fun profileViewModel_editService_EditExistingService_ServiceEdited() = runTest {
        //defining
        val expectedServiceType = ServiceType.House_Sitting
        val expectedAdditionalInfo = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        val expectedPayment = 123f

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.Walking,
                "LoremIpsum",
                456f
            )
        )
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.Other,
                "Sed at nibh ut ante suscipit porta in et nisl.",
                321f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        val state = profileViewModel.state.first()
        profileViewModel.onEvent(
            ProfilePageUiEvent.EditService(
                state.services.first().id,
                expectedServiceType,
                expectedAdditionalInfo,
                expectedPayment
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(2)
            assertContentEquals(
                arrayOf<Any?>(expectedServiceType, expectedAdditionalInfo, expectedPayment),
                arrayOf(
                    actual.services.first().service,
                    actual.services.first().additionalInfo,
                    actual.services.first().payment
                )
            )
        }
    }

    //testing removing service
    @Test
    fun profileViewModel_deleteService_nonExisting_serviceNotDeleted() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.House_Sitting,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                123f
            )
        )
        profileViewModel.onEvent(ProfilePageUiEvent.RemoveService(""))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).hasSize(1)
        }
    }

    @Test
    fun profileViewModel_deleteService_existing_serviceDeleted() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.House_Sitting,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                123f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        val state = profileViewModel.state.first()
        profileViewModel.onEvent(ProfilePageUiEvent.RemoveService(state.services.first().id))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertThat(actual.services).isEmpty()
        }
    }

    //testing validation parameters unit
    @Test
    fun profileViewModel_canEditInfo_ValidInput_validationPassed() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem_ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("Lorem ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertTrue(actual.canEditInfo)
    }

    @Test
    fun profileViewModel_validInfo_AllInvalid_validationPassed() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar. Integer feugiat ornare dui vitae volutpat. Mauris odio massa, vestibulum in pharetra ac, fringilla sed est."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("  "))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("  "))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertFalse(actual.canEditInfo)
    }

    @Test
    fun profileViewModel_setAboutMe_OneValid_validationPassed() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("Lorem ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertFalse(actual.canEditInfo)
    }

    //testing publishing info
    @Test
    fun profileViewModel_publishInfo_cannotPublish_returnsError() = runTest {
        //defining
        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("Lorem ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(ProfilePageUiEvent.PublishEditedInfo)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.profileLoadingResult)
            assertEquals(NetworkError.CONFLICT, actual.profileLoadingResult.info)
        }
    }

    @Test
    fun profileViewModel_publishInfo_canPublish_updateProfileCausedError_returnsError() = runTest {
        //defining
        val expectedError = NetworkError.SERVER_ERROR
        everySuspend { profileRepositoryMock.updateProfile(any()) } returns APIResult.Error(
            expectedError
        )
        everySuspend { profileRepositoryMock.postServices(any()) } returns APIResult.Succeed()

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem_ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("Lorem ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(ProfilePageUiEvent.PublishEditedInfo)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.profileLoadingResult)
            assertEquals(expectedError, actual.profileLoadingResult.info)
        }
    }

    @Test
    fun profileViewModel_publishInfo_canPublish_updateServicesCausedError_returnsError() = runTest {
        //defining
        val expectedError = NetworkError.SERVER_ERROR
        everySuspend { profileRepositoryMock.updateProfile(any()) } returns APIResult.Succeed()
        everySuspend { profileRepositoryMock.postServices(any()) } returns APIResult.Error(
            expectedError
        )

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin("Lorem_ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName("Lorem ipsum"))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName("dolor sit amet"))
        profileViewModel.onEvent(
            ProfilePageUiEvent.AddService(
                ServiceType.House_Sitting,
                "Lorem ipsum dolor sit amet",
                123f
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(ProfilePageUiEvent.PublishEditedInfo)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.profileLoadingResult)
            assertEquals(expectedError, actual.profileLoadingResult.info)
        }
    }

    @Test
    fun profileViewModel_publishInfo_canPublish_returnsSucceed() = runTest {
        //defining
        val expectedProfile = Profile(
            login = "Lorem_ipsum",
            firstName = "Lorem ipsum",
            lastName = "dolor sit amet",
            aboutMe = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            imageUrl = null,
            email = "Lorem@ipsum.com",
            phone = null,
            accountStatus = AccountStatus.Pending,
            isOnline = false,
            showAsWalker = true,
            services = emptyList(),
            securityLevel = ProfileSecurityLevel.Low,
            passwordLastChanged = null
        )
        everySuspend { profileRepositoryMock.updateProfile(any()) } returns APIResult.Succeed()
        everySuspend { profileRepositoryMock.postServices(any()) } returns APIResult.Succeed()

        val profileViewModel = ProfilePageViewModel(
            profileRepositoryMock,
            usersRepositoryMock,
            authDataStoreMock,
            reviewsRepositoryMock,
            HttpClient()
        )
        withContext(Dispatchers.Default) { delay(10) }

        //testing
        profileViewModel.onEvent(ProfilePageUiEvent.SetAboutMe(expectedProfile.aboutMe!!))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLogin(expectedProfile.login))
        profileViewModel.onEvent(ProfilePageUiEvent.SetFirstName(expectedProfile.firstName))
        profileViewModel.onEvent(ProfilePageUiEvent.SetLastName(expectedProfile.lastName))
        withContext(Dispatchers.Default) { delay(10) }

        profileViewModel.onEvent(ProfilePageUiEvent.PublishEditedInfo)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = profileViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.profileLoadingResult)
            assertEquals(expectedProfile, actual.profile)
        }
    }
}