package com.zeafen.petwalker.presentation.assignments

import com.zeafen.petwalker.data.helpers.calculateDistance
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.filtering.AssignmentsOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.AssignmentModel
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.UsersRepository
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsViewModel
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class AssignmentsListViewModelTest {
    private val locationServiceMock = mock<LocationService>()
    private val assignmentsRepoMock = mock<AssignmentsRepository>()
    private val usersRepoMock = mock<UsersRepository>()
    private val authDataStoreMock = mock<AuthDataStoreRepository>()

    private val testLocation = APILocation(0.0, 0.0)
    private val testTime = LocalDateTime(2000, 1, 1, 1, 1)

    @BeforeTest
    fun initMocks() {
        every { locationServiceMock.location } returns flowOf(testLocation)
        every { locationServiceMock.startObserving() } calls {}
        every { locationServiceMock.cancelObserving() } calls {}
        every { authDataStoreMock.authDataStoreFlow } returns flowOf(
            UserInfo(
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                testLocation,
                TokenResponse(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum dolor sit amet"
                ),
                "Lorem ipsum dolor sit amet"
            )
        )
        everySuspend { usersRepoMock.getWalker(any()) } returns APIResult.Succeed(
            Walker(
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
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_loadAssignmentsTest_returnSucceed() = runTest {
        //defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignmentModel = AssignmentModel(
            expectedAssignment.id,
            "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
            null,
            expectedAssignment.title,
            expectedAssignment.type,
            expectedAssignment.datePublished,
            expectedAssignment.dateTime,
            testLocation,
            testLocation
                .calculateDistance(testLocation).toFloat(),
            null
        )

        everySuspend {
            assignmentsRepoMock.getAssignments(
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
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(AssignmentsUiEvent.LoadAssignments(1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        val actual = assignmentsViewModel.state.first().assignments
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual)
        assertNotNull(actual.data)
        assertContains(
            actual.data.result,
            expectedAssignmentModel
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_loadAssignmentsTest_returnFailure() = runTest {
        everySuspend {
            assignmentsRepoMock.getAssignments(
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
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(AssignmentsUiEvent.LoadAssignments(1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        val actual = assignmentsViewModel.state.first().assignments
        assertIs<APIResult.Error<Error>>(actual)
        assertEquals(actual.info, NetworkError.UNAUTHORIZED)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_setFilters_returnSucceed() = runTest {
        //defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignmentModel = AssignmentModel(
            expectedAssignment.id,
            "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
            null,
            expectedAssignment.title,
            expectedAssignment.type,
            expectedAssignment.datePublished,
            expectedAssignment.dateTime,
            testLocation,
            testLocation
                .calculateDistance(testLocation).toFloat(),
            null
        )

        everySuspend {
            assignmentsRepoMock.getAssignments(
                any(),
                any(),
                any(),
                testLocation,
                1f,
                testTime.toString(),
                LocalDateTime(2000, 2, 1, 1, 1).toString(),
                listOf(ServiceType.House_Sitting),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetFilters(
                1f,
                testTime,
                LocalDateTime(2000, 2, 1, 1, 1),
                listOf(ServiceType.House_Sitting)
            )
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        val actual = assignmentsViewModel.state.first()
        assertContentEquals(
            arrayOf<Any?>(
                1f,
                testTime,
                LocalDateTime(2000, 2, 1, 1, 1),
                listOf(ServiceType.House_Sitting)
            ),
            arrayOf(actual.maxDistance, actual.postedFrom, actual.postedUntil, actual.services)
        )
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual.assignments)
        assertNotNull(actual.assignments.data)
        assertContains(
            actual.assignments.data.result,
            expectedAssignmentModel
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_setOrdering_returnSucceed() = runTest {
        //defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignmentModel = AssignmentModel(
            expectedAssignment.id,
            "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
            null,
            expectedAssignment.title,
            expectedAssignment.type,
            expectedAssignment.datePublished,
            expectedAssignment.dateTime,
            testLocation,
            testLocation
                .calculateDistance(testLocation).toFloat(),
            null
        )

        everySuspend {
            assignmentsRepoMock.getAssignments(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                AssignmentsOrdering.AmountPets,
                true
            )
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetOrdering(AssignmentsOrdering.AmountPets)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        val actual = assignmentsViewModel.state.first().assignments
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual)
        assertNotNull(actual.data)
        assertContains(
            actual.data.result,
            expectedAssignmentModel
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_changeOrdering_ascendingChanged() = runTest {
        //defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        everySuspend {
            assignmentsRepoMock.getAssignments(
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
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing initial set
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetOrdering(AssignmentsOrdering.AmountPets)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

//        assert
        var actual = assignmentsViewModel.state.first()
        assertContentEquals(
            arrayOf<Any?>(AssignmentsOrdering.AmountPets, true),
            arrayOf(actual.ordering, actual.ascending)
        )

        //testing repeating set
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetOrdering(AssignmentsOrdering.AmountPets)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

//        assert
        actual = assignmentsViewModel.state.first()
        assertContentEquals(
            arrayOf<Any?>(AssignmentsOrdering.AmountPets, false),
            arrayOf(actual.ordering, actual.ascending)
        )

//      testing changing ordering
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetOrdering(AssignmentsOrdering.Location)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

//        assert
        actual = assignmentsViewModel.state.first()
        assertContentEquals(
            arrayOf<Any?>(AssignmentsOrdering.Location, true),
            arrayOf(actual.ordering, actual.ascending)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_setSearchTitle_resultSucceed() = runTest {
        //defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignmentModel = AssignmentModel(
            expectedAssignment.id,
            "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
            null,
            expectedAssignment.title,
            expectedAssignment.type,
            expectedAssignment.datePublished,
            expectedAssignment.dateTime,
            testLocation,
            testLocation
                .calculateDistance(testLocation).toFloat(),
            null
        )

        everySuspend {
            assignmentsRepoMock.getAssignments(
                any(),
                any(),
                "Lorem ipsum dolor sit amet",
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetSearchTitle("Lorem ipsum dolor sit amet")
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        val actual = assignmentsViewModel.state.first()
        assertEquals("Lorem ipsum dolor sit amet", actual.searchTitle)
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual.assignments)
        assertNotNull(actual.assignments.data)
        assertContains(
            actual.assignments.data.result,
            expectedAssignmentModel
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun assignmentsViewModel_setLoadType_resultSucceed() = runTest {
        //expected
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignment2 = Assignment(
            "test-id-2",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.-2",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testTime,
            testTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedAssignmentModel = AssignmentModel(
            expectedAssignment.id,
            "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
            null,
            expectedAssignment.title,
            expectedAssignment.type,
            expectedAssignment.datePublished,
            expectedAssignment.dateTime,
            testLocation,
            testLocation
                .calculateDistance(testLocation).toFloat(),
            null
        )

        //defining
        everySuspend {
            assignmentsRepoMock.getOwnAssignmentsAsWalker(
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
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment
                ), 1, 1, 1
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
        } returns APIResult.Succeed(
            PagedResult(
                listOf(
                    expectedAssignment2
                ), 1, 1, 1
            )
        )

        val assignmentsViewModel = AssignmentsViewModel(
            assignmentsRepoMock,
            usersRepoMock,
            authDataStoreMock,
            locationServiceMock
        )

//      testing
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetLoadType(true)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        var actual = assignmentsViewModel.state.first()
        assertEquals(true, actual.loadOwn)
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual.assignments)
        assertNotNull(actual.assignments.data)
        assertContains(
            actual.assignments.data.result,
            expectedAssignmentModel
        )


        //testing changing the load group
        assignmentsViewModel.onEvent(
            AssignmentsUiEvent.SetOwnLoadGroup(true)
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //asserting
        actual = assignmentsViewModel.state.first()
        assertEquals(true, actual.loadAsOwner)
        assertIs<APIResult.Succeed<PagedResult<AssignmentModel>>>(actual.assignments)
        assertNotNull(actual.assignments.data)
        assertContains(
            actual.assignments.data.result,
            AssignmentModel(
                expectedAssignment2.id,
                "Lorem ipsum dolor sit amet Lorem ipsum dolor sit amet",
                null,
                expectedAssignment2.title,
                expectedAssignment2.type,
                expectedAssignment2.datePublished,
                expectedAssignment2.dateTime,
                testLocation,
                testLocation
                    .calculateDistance(testLocation).toFloat(),
                null
            )
        )
    }
}