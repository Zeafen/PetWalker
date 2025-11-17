package com.zeafen.petwalker.presentation.auth

import assertk.assertAll
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Profile
import com.zeafen.petwalker.domain.models.api.users.ProfileSecurityLevel
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.AuthRepository
import com.zeafen.petwalker.domain.services.ProfileRepository
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AuthViewModelTest {
    private val authRepoMock = mock<AuthRepository>()
    private val profileRepoMock = mock<ProfileRepository>()
    private val authDataStoreMock = mock<AuthDataStoreRepository>()


    //testing input events
    @Test
    fun authViewModel_EnterEmail_invalidEmail_validationFailed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Lorem ipsum dolor sit amet", actual.email)
            assertFalse(actual.emailValid.isValid)
        }
    }

    @Test
    fun authViewModel_EnterEmail_validEmail_validationPassed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Loremipsum@gmail.com"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Loremipsum@gmail.com", actual.email)
            assertTrue(actual.emailValid.isValid)
        }
    }

    @Test
    fun authViewModel_EnterPhone_invalidValue_validationFailed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterPhone("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Lorem ipsum dolor sit amet", actual.phone)
            assertFalse(actual.phoneValid.isValid)
        }
    }

    @Test
    fun authViewModel_EnterPhone_validValue_validationPassed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterPhone("99999999999"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("99999999999", actual.phone)
            assertFalse(actual.phoneValid.isValid)
        }
    }

    @Test
    fun authViewModel_EnterFirstNameLastNameLogin() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterFirstName("Lorem ipsum dolor sit"))
        authViewModel.onEvent(AuthUiEvent.EnterLastName("Lorem ipsum dolor sit amet"))
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Lorem"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Lorem ipsum dolor sit", actual.firstName)
            assertEquals("Lorem ipsum dolor sit amet", actual.lastName)
            assertEquals("Lorem", actual.login)
        }
    }

    @Test
    fun authViewModel_EnterPasswordRepeatPassword_invalidPassw_validationFailed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem ipsum dolor sit amet"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Lorem"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Lorem ipsum dolor sit amet", actual.password)
            assertEquals("Lorem", actual.repeatPassword)
            assertFalse(actual.passwordValid.isValid)
            assertFalse(actual.passwordsMatch)
        }
    }

    @Test
    fun authViewModel_EnterPasswordRepeatPassword_validPassw_validationPasssed() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertEquals("Lorem_ipsum_123", actual.password)
            assertEquals("Lorem_ipsum_123", actual.repeatPassword)
            assertTrue(actual.passwordValid.isValid)
            assertTrue(actual.passwordsMatch)
        }
    }


    //testing sign in validations unit
    @Test
    fun authViewModel_CanSignIn_LoginPasswInvalid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterLogin(""))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertFalse(actual.canSignIn)
    }

    @Test
    fun authViewModel_CanSignIn_LoginValidPasswInvalid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Passw"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertFalse(actual.canSignIn)
    }

    @Test
    fun authViewModel_CanSignIn_LoginPasswValid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Passw"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertTrue(actual.canSignIn)
    }

    ////testing sign up validations unit
    @Test
    fun authViewModel_CanSignUp_allInvalid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Loremipsum"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Loremipsum@"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertFalse(actual.canSignUp)
    }

    @Test
    fun authViewModel_CanSignUp_OneValid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Loremipsum@gmail.com"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Loremipsum"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Loremipsum@"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertFalse(actual.canSignUp)
    }

    @Test
    fun authViewModel_CanSignUp_AllValid() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterFirstName("Lorem ipsum dolor sit amet"))
        authViewModel.onEvent(AuthUiEvent.EnterLastName("Lorem ipsum dolor"))
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Lorem ipsum"))
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Loremipsum@gmail.com"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertTrue(actual.canSignUp)
    }

    //testing sign in function
    @Test
    fun authViewModel_ConfirmSignIn_cannotSignIn_returnsError() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.ConfirmSignIn)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(NetworkError.CONFLICT, actual.result.info)
        }
    }

    @Test
    fun authViewModel_ConfirmSignIn_canSignIn_returnsError() = runTest {
        //defining
        val expectedErrorCode = NetworkError.CONFLICT
        everySuspend { authRepoMock.signIn(any()) } returns APIResult.Error(expectedErrorCode)

        //defining view model
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Passw"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) { delay(10) }

        authViewModel.onEvent(AuthUiEvent.ConfirmSignIn)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(expectedErrorCode, actual.result.info)
        }
    }

    @Test
    fun authViewModel_ConfirmSignIn_canSignIn_returnsSucceed() = runTest {
        //defining
        val expectedErrorCode = NetworkError.CONFLICT
        everySuspend { authRepoMock.signIn(any()) } returns APIResult.Succeed(
            TokenResponse(
                "access",
                "refresh"
            )
        )
        everySuspend { profileRepoMock.getProfile() } returns APIResult.Succeed(
            Profile(
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                null,
                null,
                null,
                AccountStatus.Verified,
                null,
                false,
                emptyList(),
                ProfileSecurityLevel.Middle,
                null
            )
        )

        //defining authDataStore mocks
        everySuspend { authDataStoreMock.updateUserToken(any()) } calls {}
        everySuspend { authDataStoreMock.updatePersonalData(any(), any()) } calls {}
        everySuspend { authDataStoreMock.updateEmail(any()) } calls {}
        everySuspend { authDataStoreMock.updateImageUrl(any()) } calls {}

        //defining view model
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Passw"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) { delay(10) }

        authViewModel.onEvent(AuthUiEvent.ConfirmSignIn)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = authViewModel.state.first()
        assertIs<APIResult.Succeed<Unit>>(actual.result)
    }

    //testing sign up function
    @Test
    fun authViewModel_ConfirmSignUp_cannotSignUp_returnsError() = runTest {
        //defining
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.ConfirmSignUp)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(NetworkError.CONFLICT, actual.result.info)
        }
    }

    @Test
    fun authViewModel_ConfirmSignUp_canSignUp_returnsError() = runTest {
        //defining
        val expectedErrorCode = NetworkError.CONFLICT
        everySuspend { authRepoMock.signUp(any()) } returns APIResult.Error(expectedErrorCode)

        //defining view model
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterFirstName("Lorem ipsum dolor sit amet"))
        authViewModel.onEvent(AuthUiEvent.EnterLastName("Lorem ipsum dolor"))
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Lorem ipsum"))
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Loremipsum@gmail.com"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) { delay(10) }

        authViewModel.onEvent(AuthUiEvent.ConfirmSignUp)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = authViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(expectedErrorCode, actual.result.info)
        }
    }

    @Test
    fun authViewModel_ConfirmSignUp_canSignUp_returnsSucceed() = runTest {
        //defining
        everySuspend { authRepoMock.signUp(any()) } returns APIResult.Succeed()
        everySuspend { authRepoMock.signIn(any()) } returns APIResult.Succeed(
            TokenResponse(
                "access",
                "refresh"
            )
        )
        everySuspend { profileRepoMock.getProfile() } returns APIResult.Succeed(
            Profile(
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                null,
                null,
                null,
                AccountStatus.Verified,
                null,
                false,
                emptyList(),
                ProfileSecurityLevel.Middle,
                null
            )
        )

        //defining authDataStore mocks
        everySuspend { authDataStoreMock.updateUserToken(any()) } calls {}
        everySuspend { authDataStoreMock.updatePersonalData(any(), any()) } calls {}
        everySuspend { authDataStoreMock.updateEmail(any()) } calls {}
        everySuspend { authDataStoreMock.updateImageUrl(any()) } calls {}

        //defining view model
        val authViewModel = AuthViewModel(
            authRepoMock,
            profileRepoMock,
            authDataStoreMock
        )

        //testing
        authViewModel.onEvent(AuthUiEvent.EnterFirstName("Lorem ipsum dolor sit amet"))
        authViewModel.onEvent(AuthUiEvent.EnterLastName("Lorem ipsum dolor"))
        authViewModel.onEvent(AuthUiEvent.EnterLogin("Lorem_ipsum"))
        authViewModel.onEvent(AuthUiEvent.EnterEmail("Loremipsum@gmail.com"))
        authViewModel.onEvent(AuthUiEvent.EnterPassword("Lorem_ipsum_123"))
        authViewModel.onEvent(AuthUiEvent.EnterRepeatPassword("Lorem_ipsum_123"))
        withContext(Dispatchers.Default) { delay(50) }

        authViewModel.onEvent(AuthUiEvent.ConfirmSignUp)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = authViewModel.state.first()
        assertIs<APIResult.Succeed<Unit>>(actual.result)
    }
}