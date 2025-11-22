package com.zeafen.petwalker.presentation.reviews

import assertk.assertAll
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.reviews.Complaint
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintStatus
import com.zeafen.petwalker.domain.models.api.reviews.ComplaintTopic
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ComplaintConfigureViewModel {
    private val assignmentsRepoMock = mock<AssignmentsRepository>()
    private val usersRepoMock = mock<UsersRepository>()
    private val reviewsRepoMock = mock<ReviewsRepository>()
    private val authDataStoreMock = mock<AuthDataStoreRepository>()


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

        everySuspend {
            assignmentsRepoMock.getOwnAssignmentsAsOwner(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.SERVER_ERROR)
    }


    //testing editing details
    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_Blank_ValidationFailed() = runTest {
        //defining
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("  "))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertEquals("  ", actual.complaintDetails)
            assertFalse(actual.detailsValid.isValid)
        }
    }

    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_Lorem_Ipsum_ValidationFailed() = runTest {
        //defining
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem Ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertEquals("Lorem Ipsum", actual.complaintDetails)
            assertFalse(actual.detailsValid.isValid)
        }
    }

    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_More500symbols_ValidationFailed() =
        runTest {
            //defining
            val expectedText =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar. Integer feugiat ornare dui vitae volutpat."
            val complaintConfigureViewModel = ComplaintConfigureViewModel(
                usersRepoMock,
                reviewsRepoMock,
                assignmentsRepoMock,
                authDataStoreMock
            )

            //testing
            complaintConfigureViewModel.onEvent(
                ComplaintConfigureUiEvent.SetComplaintDetails(
                    expectedText
                )
            )
            withContext(Dispatchers.Default) { delay(10) }

            //assert
            val actual = complaintConfigureViewModel.state.first()
            assertAll {
                assertEquals(expectedText, actual.complaintDetails)
                assertFalse(actual.detailsValid.isValid)
            }
        }

    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_Less50Symbols_ValidationFailed() = runTest {
        //defining
        val expectedText = "Lorem ipsum dolor sit amet."
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.SetComplaintDetails(
                expectedText
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertEquals(expectedText, actual.complaintDetails)
            assertFalse(actual.detailsValid.isValid)
        }
    }

    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_More50SymbolsLess5words_ValidationFailed() =
        runTest {
            //defining
            val expectedText = "Loremipsumdolorsitamet.Sedatnibhutantesuscipitportainetnisl."
            val complaintConfigureViewModel = ComplaintConfigureViewModel(
                usersRepoMock,
                reviewsRepoMock,
                assignmentsRepoMock,
                authDataStoreMock
            )

            //testing
            complaintConfigureViewModel.onEvent(
                ComplaintConfigureUiEvent.SetComplaintDetails(
                    expectedText
                )
            )
            withContext(Dispatchers.Default) { delay(10) }

            //assert
            val actual = complaintConfigureViewModel.state.first()
            assertAll {
                assertEquals(expectedText, actual.complaintDetails)
                assertFalse(actual.detailsValid.isValid)
            }
        }

    @Test
    fun complaintConfigureViewModel_SetComplaintDetails_More50SymbolsMore5words_ValidationPassed() =
        runTest {
            //defining
            val expectedText =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."
            val complaintConfigureViewModel = ComplaintConfigureViewModel(
                usersRepoMock,
                reviewsRepoMock,
                assignmentsRepoMock,
                authDataStoreMock
            )

            //testing
            complaintConfigureViewModel.onEvent(
                ComplaintConfigureUiEvent.SetComplaintDetails(
                    expectedText
                )
            )
            withContext(Dispatchers.Default) { delay(10) }

            //assert
            val actual = complaintConfigureViewModel.state.first()
            assertAll {
                assertEquals(expectedText, actual.complaintDetails)
                assertTrue(actual.detailsValid.isValid)
            }
        }


    //testing editing topic
    @Test
    fun complaintConfigureViewModel_SetComplaintTopic() = runTest {
        //defining
        val expected = ComplaintTopic.Toxic_Behaviour
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintTopic(expected))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertEquals(expected, actual.complaintTopic)
    }

    //testing can publish validation
    @Test
    fun complaintConfigureViewModel_CanPublish_OneValid() = runTest {
        //defining
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintTopic(null))
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @Test
    fun complaintConfigureViewModel_CanPublish_AllInvalid() = runTest {
        //defining
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintTopic(null))
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @Test
    fun complaintConfigureViewModel_CanPublish_AllValid() = runTest {
        //defining
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.SetComplaintTopic(ComplaintTopic.Toxic_Behaviour)
        )
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertTrue(actual.canPublish)
    }

    //testing initializing review
    @Test
    fun complaintConfigureViewModel_InitializeComplaint_LoadedBoth() = runTest {
        //defining
        val expectedComplaint = Complaint(
            "test-review-id",
            "test-sender-id",
            "test-walker-id",
            "test-assignment-id",
            ComplaintTopic.Toxic_Behaviour,
            ComplaintStatus.Pending,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            true,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1)
        )
        everySuspend { reviewsRepoMock.getComplaintById("test-complaint-id") } returns APIResult.Succeed(
            expectedComplaint
        )

        val expectedWalker = Walker(
            "test-walker-id",
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet",
            null,
            "Lorem ipsum dolor sit amet",
            null,
            null,
            5.0f,
            123L,
            123L,
            123L,
            AccountStatus.Pending,
            null,
            null,
            listOf(),
            null
        )
        everySuspend { usersRepoMock.getWalker("test-walker-id") } returns APIResult.Succeed(
            expectedWalker
        )

        //defining review
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.InitializeComplaint(
                "test-complaint-id",
                "test-walker-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.complaintLoadingResult)
            assertContentEquals(
                arrayOf<Any?>(expectedComplaint.body, expectedComplaint.topic),
                arrayOf(actual.complaintDetails, actual.complaintTopic)
            )

            assertIs<APIResult.Succeed<Unit>>(actual.reviewedWalkerLoadingRes)
            assertContentEquals(
                arrayOf<Any?>(
                    "${expectedWalker.firstName} ${expectedWalker.lastName}",
                    expectedWalker.imageUrl
                ),
                arrayOf(actual.reviewedWalkerName, actual.reviewedWalkerImageUrl)
            )
        }
    }

    @Test
    fun complaintConfigureViewModel_InitializeComplaint_ComplaintLoadingFailed() = runTest {
        //defining
        val expectedErrorCode = NetworkError.SERVER_ERROR
        everySuspend { reviewsRepoMock.getComplaintById("test-complaint-id") } returns APIResult.Error(
            expectedErrorCode
        )

        //defining review
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.InitializeComplaint(
                "test-complaint-id",
                "test-walker-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.complaintLoadingResult)
            assertEquals(expectedErrorCode, actual.complaintLoadingResult.info)
        }
    }

    @Test
    fun complaintConfigureViewModel_InitializeReview_WalkerFailed() = runTest {
        //defining
        val expectedComplaint = Complaint(
            "test-review-id",
            "test-sender-id",
            "test-walker-id",
            "test-assignment-id",
            ComplaintTopic.Toxic_Behaviour,
            ComplaintStatus.Pending,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            true,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1)
        )
        everySuspend { reviewsRepoMock.getComplaintById("test-complaint-id") } returns APIResult.Succeed(
            expectedComplaint
        )

        val expectedErrorCode = NetworkError.SERVER_ERROR
        everySuspend { usersRepoMock.getWalker("test-walker-id") } returns APIResult.Error(
            expectedErrorCode
        )

        //defining review
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.InitializeComplaint(
                "test-complaint-id",
                "test-walker-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.complaintLoadingResult)
            assertContentEquals(
                arrayOf<Any?>(expectedComplaint.body, expectedComplaint.topic),
                arrayOf(actual.complaintDetails, actual.complaintTopic)
            )

            assertIs<APIResult.Error<Error>>(actual.reviewedWalkerLoadingRes)
            assertEquals(expectedErrorCode, actual.reviewedWalkerLoadingRes.info)
        }
    }

    //testing publishing review
    @Test
    fun complaintConfigureViewModel_PublishComplaint_CannotPublish_returnsError() = runTest {
        //defining review
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.PublishComplaint)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertIs<APIResult.Error<Error>>(actual.complaintLoadingResult)
    }

    @Test
    fun complaintConfigureViewModel_PublishComplaint_notLoaded_returnsError() = runTest {
        //defining review
        val expectedError = NetworkError.SERVER_ERROR
        everySuspend {
            reviewsRepoMock.postComplaint(any(), any())
        } returns APIResult.Error(expectedError)

        //defining view model
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.SetComplaintTopic(ComplaintTopic.Toxic_Behaviour)
        )
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        withContext(Dispatchers.Default) { delay(10) }

        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.PublishComplaint)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.complaintLoadingResult)
            assertEquals(expectedError, actual.complaintLoadingResult.info)
        }
    }

    @Test
    fun complaintConfigureViewModel_PublishComplaint_notLoaded_returnsSucceed() = runTest {
        //defining review
        val expectedComplaint = Complaint(
            "test-review-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            ComplaintTopic.Toxic_Behaviour,
            ComplaintStatus.Pending,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            true,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1)
        )
        everySuspend {
            reviewsRepoMock.postComplaint(any(), any())
        } returns APIResult.Succeed(expectedComplaint)

        //defining view model
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.SetComplaintTopic(ComplaintTopic.Toxic_Behaviour)
        )
        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.SetComplaintDetails("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl."))
        withContext(Dispatchers.Default) { delay(10) }

        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.PublishComplaint)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.complaintLoadingResult)
            assertEquals(expectedComplaint.id, actual.selectedComplaintId)
        }
    }

    @Test
    fun complaintConfigureViewModel_PublishComplaint_Loaded_returnsError() = runTest {
        //defining review
        val expectedErrorCode = NetworkError.SERVER_ERROR
        val expectedComplaint = Complaint(
            "test-complaint-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            ComplaintTopic.Toxic_Behaviour,
            ComplaintStatus.Pending,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            true,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1)
        )
        everySuspend {
            reviewsRepoMock.updateComplaint(any(), any())
        } returns APIResult.Error(expectedErrorCode)
        everySuspend {
            reviewsRepoMock.getComplaintById(any())
        } returns APIResult.Succeed(expectedComplaint)

        everySuspend { usersRepoMock.getWalker(any()) } returns APIResult.Error(NetworkError.SERVER_ERROR)

        //defining view model
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.InitializeComplaint(
                "test-complaint-id",
                "test-walker-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.PublishComplaint)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.complaintLoadingResult)
            assertEquals(expectedErrorCode, actual.complaintLoadingResult.info)
        }
    }

    @Test
    fun complaintConfigureViewModel_PublishReview_Loaded_returnsSucceed() = runTest {
        //defining review
        val expectedComplaint = Complaint(
            "test-complaint-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            ComplaintTopic.Toxic_Behaviour,
            ComplaintStatus.Pending,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl.",
            true,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1)
        )
        everySuspend {
            reviewsRepoMock.updateComplaint(any(), any())
        } returns APIResult.Succeed()
        everySuspend {
            reviewsRepoMock.getComplaintById(any())
        } returns APIResult.Succeed(expectedComplaint)

        everySuspend { usersRepoMock.getWalker(any()) } returns APIResult.Error(NetworkError.SERVER_ERROR)

        //defining view model
        val complaintConfigureViewModel = ComplaintConfigureViewModel(
            usersRepoMock,
            reviewsRepoMock,
            assignmentsRepoMock,
            authDataStoreMock
        )

        //testing
        complaintConfigureViewModel.onEvent(
            ComplaintConfigureUiEvent.InitializeComplaint(
                "test-complaint-id",
                "test-walker-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        complaintConfigureViewModel.onEvent(ComplaintConfigureUiEvent.PublishComplaint)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = complaintConfigureViewModel.state.first()
        assertIs<APIResult.Succeed<Unit>>(actual.complaintLoadingResult)
    }
}