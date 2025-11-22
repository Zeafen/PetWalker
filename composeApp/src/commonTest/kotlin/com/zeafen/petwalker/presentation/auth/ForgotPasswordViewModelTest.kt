package com.zeafen.petwalker.presentation.auth

import assertk.assertAll
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthRepository
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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ForgotPasswordViewModelTest {
    private val authRepoMock = mock<AuthRepository>()

    @Test
    fun forgotPasswViewModel_GenerateCode_invalidEmail_returnsError() = runTest {
        //defining
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterEmail("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.SendCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(ForgotPasswordStage.SendCode, actual.stage)
            assertEquals(NetworkError.NOT_FOUND, actual.result.info)
        }
    }

    @Test
    fun forgotPasswViewModel_GenerateCode_validEmail_returnsSucceed() = runTest {
        //defining
        everySuspend { authRepoMock.getEmailCode(any()) } returns APIResult.Succeed()

        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterEmail("Loremipsum@gmail.com"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.SendCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertContentEquals(
                arrayOf<Any?>(ForgotPasswordStage.ConfirmCode, null),
                arrayOf(actual.stage, actual.result)
            )
        }
    }

    @Test
    fun forgotPasswViewModel_GenerateCode_validEmail_returnsError() = runTest {
        //defining
        val expectedErrorCode = NetworkError.SERVER_ERROR
        everySuspend { authRepoMock.getEmailCode(any()) } returns APIResult.Error(expectedErrorCode)

        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterEmail("Loremipsum@gmail.com"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.SendCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(expectedErrorCode, actual.result.info)
        }
    }

    @Test
    fun forgotPasswViewModel_EnterCode() = runTest {
        //defining
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }
        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertEquals("Lorem ipsum dolor sit amet", actual.code)
        }
    }

    @Test
    fun forgotPasswViewModel_ConfirmCodeCode_codeBlank_returnsError() = runTest {
        //defining

        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.ConfirmCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(NetworkError.UNKNOWN, actual.result.info)
        }
    }

    @Test
    fun forgotPasswViewModel_ConfirmCodeCode_codeValid_returnsError() = runTest {
        //defining
        val expectedErrorCode = NetworkError.UNKNOWN
        everySuspend { authRepoMock.checkConfirmationCode(any(), any()) } returns APIResult.Error(
            expectedErrorCode
        )

        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.ConfirmCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(expectedErrorCode, actual.result.info)
        }
    }

    @Test
    fun forgotPasswViewModel_ConfirmCodeCode_codeValid_returnsSucceed() = runTest {
        //defining
        everySuspend {
            authRepoMock.checkConfirmationCode(
                any(),
                any()
            )
        } returns APIResult.Succeed()

        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.ConfirmCode)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertEquals(ForgotPasswordStage.ChangePassword, actual.stage)
            assertEquals(null, actual.result)
        }
    }

    @Test
    fun forgotPasswViewModel_EnterPassword() = runTest {
        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterPassword("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertEquals("Lorem ipsum dolor sit amet", actual.password)
    }

    @Test
    fun forgotPasswViewModel_EnterRepeatPassword() = runTest {
        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterRepeatPassword("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertEquals("Lorem ipsum dolor sit amet", actual.repeatPassword)
    }

    @Test
    fun forgotPasswViewModel_ChangePassword_InvalidParameters_returnsError() = runTest {
        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode(""))
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterEmail(""))
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterPassword(""))
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterRepeatPassword("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.ChangePassword)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.result)
            assertEquals(NetworkError.UNKNOWN, actual.result.info)
        }
    }

    @Test
    fun forgotPasswViewModel_ChangePassword_ValidParams_returnsSucceed() = runTest {
        //define
        everySuspend { authRepoMock.updatePassword("Loremipsum@gmail.com", "Lorem ipsum dolor sit amet", "Lorem_Ipsum_123") } returns APIResult.Succeed()

        //defining view model
        val forgotPasswViewModel = ForgotPasswordViewModel(authRepoMock)

        //testing
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterCode("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) {
            delay(10)
        }
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterEmail("Loremipsum@gmail.com"))
        withContext(Dispatchers.Default) {
            delay(10)
        }
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterPassword("Lorem_Ipsum_123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }
        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.EnterRepeatPassword("Lorem_Ipsum_123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }
        withContext(Dispatchers.Default) {
            delay(10)
        }

        forgotPasswViewModel.onEvent(ForgotPasswordUiEvent.ChangePassword)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = forgotPasswViewModel.state.first()
        assertIs<APIResult.Succeed<Unit>>(actual.result)
    }
}