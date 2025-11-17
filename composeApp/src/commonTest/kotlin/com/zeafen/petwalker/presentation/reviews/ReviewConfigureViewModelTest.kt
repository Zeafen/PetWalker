package com.zeafen.petwalker.presentation.reviews

import assertk.assertAll
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.reviews.Review
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureUiEvent
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureViewModel
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

class ReviewConfigureViewModelTest {
    private val assignmentsRepMock = mock<AssignmentsRepository>()
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
    }

    //testing editing review text
    @Test
    fun reviewConfigureViewModel_SetReviewText_Blank_ValidationFailed() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("  "))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals("  ", actual.reviewText)
            assertFalse(actual.textValid.isValid)
        }
    }

    @Test
    fun reviewConfigureViewModel_SetReviewText_Lorem_Ipsum_ValidationFailed() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem Ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals("Lorem Ipsum", actual.reviewText)
            assertFalse(actual.textValid.isValid)
        }
    }

    @Test
    fun reviewConfigureViewModel_SetReviewText_More500symbols_ValidationFailed() = runTest {
        //defining
        val expectedText =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar. Integer feugiat ornare dui vitae volutpat."
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText(expectedText))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals(expectedText, actual.reviewText)
            assertFalse(actual.textValid.isValid)
        }
    }

    @Test
    fun reviewConfigureViewModel_SetReviewText_ValidText_ValidationPassed() = runTest {
        //defining
        val expectedText = "Lorem ipsum dolor sit amet."
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText(expectedText))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals(expectedText, actual.reviewText)
            assertTrue(actual.textValid.isValid)
        }
    }

    //testing editing rating
    @Test
    fun reviewConfigureViewModel_SetReviewRating_MinusValue_ValidationFailed() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(Int.MIN_VALUE))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals(Int.MIN_VALUE, actual.reviewRating)
            assertFalse(actual.ratingValid.isValid)
        }
    }

    @Test
    fun reviewConfigureViewModel_SetReviewRating_Greater5Value_ValidationFailed() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(Int.MAX_VALUE))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals(Int.MAX_VALUE, actual.reviewRating)
            assertFalse(actual.ratingValid.isValid)
        }
    }

    @Test
    fun reviewConfigureViewModel_SetReviewRating_AppropriateValue_ValidationPassed() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(3))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertEquals(3, actual.reviewRating)
            assertTrue(actual.ratingValid.isValid)
        }
    }

    //testing can publish validation
    @Test
    fun reviewConfigureViewModel_CanPublish_OneValid() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(3))
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @Test
    fun reviewConfigureViewModel_CanPublish_AllInvalid() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(Int.MIN_VALUE))
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @Test
    fun reviewConfigureViewModel_CanPublish_AllValid() = runTest {
        //defining
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(3))
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertTrue(actual.canPublish)
    }

    //testing initializing review
    @Test
    fun reviewConfigureViewModel_InitializeReview_LoadedBoth() = runTest {
        //defining
        val expectedReview = Review(
            "test-review-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            3,
            LocalDateTime(2000, 1, 1, 1, 1),
            null
        )
        everySuspend { reviewsRepoMock.getReviewById("test-review-id") } returns APIResult.Succeed(
            expectedReview
        )

        val expectedAssignment = Assignment(
            "test-assignment-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1),
            AssignmentState.Searching,
            APILocation(0.0, 0.0),
            null
        )
        everySuspend { assignmentsRepMock.getAssignmentById("test-assignment-id") } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining review
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(
            ReviewConfigureUiEvent.InitializeReview(
                "test-review-id",
                "test-assignment-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.assignmentLoadingResult)
            assertContentEquals(
                arrayOf<Any?>(expectedReview.rating, expectedReview.text),
                arrayOf(actual.reviewRating, actual.reviewText)
            )

            assertIs<APIResult.Succeed<Unit>>(actual.reviewLoadingResult)
            assertContentEquals(
                arrayOf<Any?>(expectedAssignment.type, expectedAssignment.title),
                arrayOf(actual.reviewedAssignmentType, actual.reviewedAssignmentTitle)
            )
        }
    }

    @Test
    fun reviewConfigureViewModel_InitializeReview_ReviewLoadingFailed() = runTest {
        //defining
        everySuspend { reviewsRepoMock.getReviewById("test-review-id") } returns APIResult.Error(
            NetworkError.SERVER_ERROR
        )

        val expectedAssignment = Assignment(
            "test-assignment-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1),
            AssignmentState.Searching,
            APILocation(0.0, 0.0),
            null
        )
        everySuspend { assignmentsRepMock.getAssignmentById("test-assignment-id") } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining review
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(
            ReviewConfigureUiEvent.InitializeReview(
                "test-review-id",
                "test-assignment-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.assignmentLoadingResult)

            assertIs<APIResult.Error<Error>>(actual.reviewLoadingResult)
        }
    }

    @Test
    fun reviewConfigureViewModel_InitializeReview_BothFailed() = runTest {
        //defining
        val expectedReviewError = NetworkError.SERVER_ERROR
        everySuspend { reviewsRepoMock.getReviewById("test-review-id") } returns APIResult.Error(
            expectedReviewError
        )

        val expectedAssignmentError = NetworkError.UNAUTHORIZED
        everySuspend { assignmentsRepMock.getAssignmentById("test-assignment-id") } returns APIResult.Error(
            expectedAssignmentError
        )

        //defining review
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(
            ReviewConfigureUiEvent.InitializeReview(
                "test-review-id",
                "test-assignment-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.assignmentLoadingResult)
            assertIs<APIResult.Error<Error>>(actual.reviewLoadingResult)

            assertContentEquals(
                arrayOf(expectedReviewError, expectedAssignmentError),
                arrayOf(actual.reviewLoadingResult.info, actual.assignmentLoadingResult.info)
            )
        }
    }

    //testing publishing review
    @Test
    fun reviewConfigureViewModel_PublishReview_CannotPublish_returnsError() = runTest {
        //defining review
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.PublishReview)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertIs<APIResult.Error<Error>>(actual.reviewLoadingResult)
    }

    @Test
    fun reviewConfigureViewModel_PublishReview_notLoaded_returnsError() = runTest {
        //defining review
        val expectedError = NetworkError.SERVER_ERROR
        everySuspend {
            reviewsRepoMock.postReview(any(), any())
        } returns APIResult.Error(expectedError)

        //defining view model
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(3))
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.PublishReview)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.reviewLoadingResult)
            assertEquals(expectedError, actual.reviewLoadingResult.info)
        }
    }

    @Test
    fun reviewConfigureViewModel_PublishReview_notLoaded_returnsSucceed() = runTest {
        //defining review
        val expectedReview = Review(
            "test-review-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            3,
            LocalDateTime(2000, 1, 1, 1, 1),
            null
        )
        everySuspend {
            reviewsRepoMock.postReview(any(), any())
        } returns APIResult.Succeed(expectedReview)

        //defining view model
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewRating(3))
        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.SetReviewText("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.PublishReview)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.reviewLoadingResult)
        }
    }

    @Test
    fun reviewConfigureViewModel_PublishReview_Loaded_returnsError() = runTest {
        //defining
        val expectedReview = Review(
            "test-review-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            3,
            LocalDateTime(2000, 1, 1, 1, 1),
            null
        )
        everySuspend { reviewsRepoMock.getReviewById("test-review-id") } returns APIResult.Succeed(
            expectedReview
        )

        val expectedAssignment = Assignment(
            "test-assignment-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1),
            AssignmentState.Searching,
            APILocation(0.0, 0.0),
            null
        )
        everySuspend { assignmentsRepMock.getAssignmentById("test-assignment-id") } returns APIResult.Succeed(
            expectedAssignment
        )

        val expectedError = NetworkError.SERVER_ERROR
        everySuspend {
            reviewsRepoMock.updateReview(any(), any())
        } returns APIResult.Error(expectedError)

        //defining view model
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(
            ReviewConfigureUiEvent.InitializeReview(
                "test-review-id",
                "test-assignment-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.PublishReview)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.reviewLoadingResult)
            assertEquals(expectedError, actual.reviewLoadingResult.info)
        }
    }

    @Test
    fun reviewConfigureViewModel_PublishReview_Loaded_returnsSucceed() = runTest {
        //defining
        val expectedReview = Review(
            "test-review-id",
            "test-sender-id",
            "test-user-id",
            "test-assignment-id",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
            3,
            LocalDateTime(2000, 1, 1, 1, 1),
            null
        )
        everySuspend { reviewsRepoMock.getReviewById("test-review-id") } returns APIResult.Succeed(
            expectedReview
        )

        val expectedAssignment = Assignment(
            "test-assignment-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            LocalDateTime(2000, 1, 1, 1, 1),
            LocalDateTime(2000, 1, 1, 1, 1),
            AssignmentState.Searching,
            APILocation(0.0, 0.0),
            null
        )
        everySuspend { assignmentsRepMock.getAssignmentById("test-assignment-id") } returns APIResult.Succeed(
            expectedAssignment
        )

        everySuspend {
            reviewsRepoMock.updateReview(any(), any())
        } returns APIResult.Succeed()

        //defining view model
        val reviewConfigureViewModel = ReviewConfigureViewModel(
            assignmentsRepMock,
            reviewsRepoMock,
            authDataStoreMock
        )

        //testing
        reviewConfigureViewModel.onEvent(
            ReviewConfigureUiEvent.InitializeReview(
                "test-review-id",
                "test-assignment-id"
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        reviewConfigureViewModel.onEvent(ReviewConfigureUiEvent.PublishReview)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = reviewConfigureViewModel.state.first()
        assertIs<APIResult.Succeed<Unit>>(actual.reviewLoadingResult)
    }
}