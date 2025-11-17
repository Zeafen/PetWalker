package com.zeafen.petwalker.presentation.assignments

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsOnly
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.assignments.Assignment
import com.zeafen.petwalker.domain.models.api.assignments.AssignmentState
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.LocationService
import com.zeafen.petwalker.domain.services.PetsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureUiEvent
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureViewModel
import dev.mokkery.answering.calls
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.early_error_error_txt
import petwalker.composeapp.generated.resources.empty_fields_error_txt
import petwalker.composeapp.generated.resources.incorrect_length_max_error
import petwalker.composeapp.generated.resources.least_words_count_error_txt
import petwalker.composeapp.generated.resources.required_label
import kotlin.test.BeforeTest
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AssignmentConfigureViewModelTest {
    private val locationServiceMock = mock<LocationService>()
    private val assignmentsRepoMock = mock<AssignmentsRepository>()
    private val petsRepoMock = mock<PetsRepository>()
    private val usersRepoMock = mock<UsersRepository>()
    private val authDataStoreMock = mock<AuthDataStoreRepository>()

    private val testLocation = APILocation(0.0, 0.0)
    private val testDateTime = LocalDateTime(2000, 1, 1, 1, 1)

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

    @Test
    fun assignmentConfigureViewModel_addNewAssignedPet_emptyAssignedPetsList_successfullyAdded() =
        runTest {
//      defining
            val expectedPet = Pet(
                "test-pet-id",
                "test-owner-id",
                null,
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet, consectetur",
                "Lorem ipsum dolor sit amet, consectetur adipiscing",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                testDateTime,
                1f
            )
            val assignmentConfViewModel = AssignmentConfigureViewModel(
                assignmentsRepoMock,
                authDataStoreMock,
                petsRepoMock,
                locationServiceMock
            )

//      testing
            assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.AddAssignedPet(expectedPet))
            withContext(Dispatchers.Default) {
                delay(10)
            }

//      asserting
            val actual = assignmentConfViewModel.state.first()
            assertThat(actual.assignedPets).containsOnly(expectedPet)
        }

    //Description editing|validation tests
    @Test
    fun assignmentConfigureViewModel_addSameAssignedPet_emptyAssignedPetsList_notAdded() = runTest {
//      expected
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.AddAssignedPet(expectedPet))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.AddAssignedPet(expectedPet))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertThat(actual.assignedPets).containsOnly(expectedPet)
    }

    @Test
    fun assignmentConfigureViewModel_addDiffAssignedPet_emptyAssignedPetsList_notAdded() = runTest {
//      expected
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        val expectedPet2 = Pet(
            "test-pet-id-2",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.AddAssignedPet(expectedPet))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.AddAssignedPet(expectedPet2))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertThat(actual.assignedPets).containsOnly(expectedPet, expectedPet2)
    }

    @Test
    fun assignmentConfigureViewModel_setTypeOther_descriptionEmpty_validationFailed() = runTest {
//      expected
        val expectedValidation = Res.string.required_label

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(null, actual.descriptionNeeded && !actual.descriptionValidation.isValid)
        assertContentEquals(
            arrayOf<Any?>(expectedValidation, "", ServiceType.Other),
            arrayOf(
                actual.descriptionValidation.errorResId,
                actual.assignmentDescription,
                actual.assignmentType
            )
        )
    }

    @Test
    fun assignmentConfigureViewModel_setTypeOther_descriptionBlank_validationFailed() = runTest {
//      expected
        val expectedValidation = Res.string.required_label

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("                     "))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(null, actual.descriptionNeeded && !actual.descriptionValidation.isValid)
        assertContentEquals(
            arrayOf<Any?>(expectedValidation, "                     ", ServiceType.Other),
            arrayOf(
                actual.descriptionValidation.errorResId,
                actual.assignmentDescription,
                actual.assignmentType
            )
        )
    }

    @Test
    fun assignmentConfigureViewModel_setTypeWalking_descriptionEmpty_validationPassed() = runTest {

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Walking))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(null, !actual.descriptionNeeded && actual.descriptionValidation.isValid)
        assertContentEquals(
            arrayOf<Any?>(null, "", ServiceType.Walking),
            arrayOf(
                actual.descriptionValidation.errorResId,
                actual.assignmentDescription,
                actual.assignmentType
            )
        )
    }

    @Test
    fun assignmentConfigureViewModel_setTypeOther_descriptionLessThan5words_validationFailed() =
        runTest {
            val expectedErrorResId = Res.string.least_words_count_error_txt
//      defining
            val assignmentConfViewModel = AssignmentConfigureViewModel(
                assignmentsRepoMock,
                authDataStoreMock,
                petsRepoMock,
                locationServiceMock
            )

//      testing
            assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
            assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit"))
            withContext(Dispatchers.Default) {
                delay(10)
            }

//      asserting
            val actual = assignmentConfViewModel.state.first()
            assertTrue(null, actual.descriptionNeeded && !actual.descriptionValidation.isValid)
            assertContentEquals(
                arrayOf<Any?>(expectedErrorResId, "Lorem ipsum dolor sit", ServiceType.Other),
                arrayOf(
                    actual.descriptionValidation.errorResId,
                    actual.assignmentDescription,
                    actual.assignmentType
                )
            )
        }

    @Test
    fun assignmentConfigureViewModel_descriptionLarger500Symbols_validationFailed() = runTest {
        val expectedErrorResId = Res.string.incorrect_length_max_error
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(
            AssignmentConfigureUiEvent.SetAssignmentDescription(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor.\n" +
                        "Quisque nunc urna, vehicula sed fringilla ut, ornare id magna."
            )
        )
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(null, !actual.descriptionValidation.isValid)
        assertContentEquals(
            arrayOf<Any?>(
                expectedErrorResId,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor.\n" +
                        "Quisque nunc urna, vehicula sed fringilla ut, ornare id magna."
            ),
            arrayOf(
                actual.descriptionValidation.errorResId,
                actual.assignmentDescription
            )
        )
    }

    //Title editing|validation tests
    @Test
    fun assignmentConfigureViewModel_titleEmpty_validationFailed() = runTest {
//      expected
        val expectedErrorResId = Res.string.empty_fields_error_txt

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertFalse(actual.titleValidation.isValid)
        assertContentEquals(
            arrayOf(expectedErrorResId, ""),
            arrayOf<Any?>(actual.titleValidation.errorResId, actual.assignmentTitle)
        )
    }

    @Test
    fun assignmentConfigureViewModel_titleBlank_validationFailed() = runTest {
//      expected
        val expectedErrorResId = Res.string.empty_fields_error_txt

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("           "))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertFalse(actual.titleValidation.isValid)
        assertContentEquals(
            arrayOf(expectedErrorResId, "           "),
            arrayOf<Any?>(actual.titleValidation.errorResId, actual.assignmentTitle)
        )
    }

    @Test
    fun assignmentConfigureViewModel_titleLargerThan200_validationFailed() = runTest {
//      expected
        val expectedErrorResId = Res.string.incorrect_length_max_error

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertFalse(actual.titleValidation.isValid)
        assertContentEquals(
            arrayOf(
                expectedErrorResId,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor."
            ),
            arrayOf<Any?>(actual.titleValidation.errorResId, actual.assignmentTitle)
        )
    }

    @Test
    fun assignmentConfigureViewModel_titleAppropriate_validationPassed() = runTest {
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(actual.titleValidation.isValid)
        assertContentEquals(
            arrayOf(null, "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
            arrayOf<Any?>(actual.titleValidation.errorResId, actual.assignmentTitle)
        )
    }

    //Editing|validation assignment date
    @Test
    fun assignmentConfigureViewModel_dateNull_validationFailed() = runTest {
//      expected
        val expectedErrorResId = Res.string.empty_fields_error_txt

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(null))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertFalse(actual.dateValidation.isValid)
        assertContentEquals(
            arrayOf(expectedErrorResId, null),
            arrayOf<Any?>(actual.dateValidation.errorResId, actual.assignmentDate)
        )
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun assignmentConfigureViewModel_dateEarlierThanAllowed_validationFailed() = runTest {
//      expected
        val expectedErrorResId = Res.string.early_error_error_txt
        val expectedDate = Clock.System.now()
            .plus(59, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(expectedDate))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertFalse(actual.dateValidation.isValid)
        assertContentEquals(
            arrayOf(expectedErrorResId, expectedDate),
            arrayOf<Any?>(actual.dateValidation.errorResId, actual.assignmentDate)
        )
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun assignmentConfigureViewModel_dateAppropriate_validationPassed() = runTest {
//      expected
        val expectedDate = Clock.System.now()
            .plus(2, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())

//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(expectedDate))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertTrue(actual.dateValidation.isValid)
        assertContentEquals(
            arrayOf(null, expectedDate),
            arrayOf<Any?>(actual.dateValidation.errorResId, actual.assignmentDate)
        )
    }

    //Checking how validation are united
    @Test
    fun assignmentConfigureViewModel_allValidationsFailed_cannotPublish() = runTest {
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor. Quisque nunc urna, vehicula sed fringilla ut, ornare id magna."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.titleValidation.isValid)
            assertFalse(actual.dateValidation.isValid)
            assertFalse(actual.assignmentType != null)
            assertFalse(actual.descriptionValidation.isValid)
            assertFalse(actual.canPublish)
        }
    }

    @Test
    fun assignmentConfigureViewModel_onlyTitlePassed_cannotPublish() = runTest {
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor. Quisque nunc urna, vehicula sed fringilla ut, ornare id magna."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertTrue(actual.titleValidation.isValid)
            assertFalse(actual.dateValidation.isValid)
            assertFalse(actual.assignmentType != null)
            assertFalse(actual.descriptionValidation.isValid)
            assertFalse(actual.canPublish)
        }
    }

    @Test
    fun assignmentConfigureViewModel_onlyTypeDescriptionPassed_cannotPublish() = runTest {
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.titleValidation.isValid)
            assertFalse(actual.dateValidation.isValid)
            assertTrue(actual.assignmentType != null)
            assertTrue(actual.descriptionValidation.isValid)
            assertFalse(actual.canPublish)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun assignmentConfigureViewModel_onlyDatePassed_cannotPublish() = runTest {
//      defining
        val expectedDate = Clock.System.now()
            .plus(2, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(expectedDate))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus. Suspendisse felis urna, fringilla id diam vel, faucibus maximus dolor. Quisque nunc urna, vehicula sed fringilla ut, ornare id magna."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle(""))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.titleValidation.isValid)
            assertTrue(actual.dateValidation.isValid)
            assertFalse(actual.assignmentType != null)
            assertFalse(actual.descriptionValidation.isValid)
            assertFalse(actual.canPublish)
        }
    }

    @Test
    fun assignmentConfigureViewModel_atLeastOneFailed_cannotPublish() = runTest {
//      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(null))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertTrue(actual.titleValidation.isValid)
            assertFalse(actual.dateValidation.isValid)
            assertTrue(actual.assignmentType != null)
            assertTrue(actual.descriptionValidation.isValid)
            assertFalse(actual.canPublish)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun assignmentConfigureViewModel_allPassed_canPublish() = runTest {
//      defining
        val expectedDate = Clock.System.now()
            .plus(2, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(expectedDate))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertTrue(actual.titleValidation.isValid)
            assertTrue(actual.dateValidation.isValid)
            assertTrue(actual.assignmentType != null)
            assertTrue(actual.descriptionValidation.isValid)
            assertTrue(actual.canPublish)
        }
    }

    //Checking can apply changes when cannot publish
    @Test
    fun assignmentConfigureViewModel_applyChanges_cannotPublish_returnError() = runTest {
        //      defining
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.ApplyChanges)
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.publishingResult)
            assertEquals(actual.publishingResult.info, NetworkError.UNKNOWN)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun assignmentConfigureViewModel_applyChanges_nullId_returnError() = runTest {
        //      defining
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testDateTime,
            testDateTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        everySuspend {
            assignmentsRepoMock.postAssignment(
                any(),
            )
        } returns APIResult.Succeed(
            expectedAssignment
        )
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                listOf(expectedPet), 1, 2, 1
            )
        )

        val expectedDate = Clock.System.now()
            .plus(2, DateTimeUnit.HOUR)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDate(expectedDate))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentType(ServiceType.Other))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vel maximus mauris, eu molestie orci. Fusce eget enim elit. Suspendisse ultrices dui ut semper vulputate. Aenean felis velit, tempor ut neque eget, gravida facilisis eros. Mauris gravida at nibh id gravida. Duis eu est at mi pulvinar fermentum ac ut quam. Cras nibh justo, commodo et suscipit eu, iaculis at lorem. Nullam gravida aliquam luctus."))
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetAssignmentTitle("Lorem ipsum dolor sit amet, consectetur adipiscing elit."))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.ApplyChanges)
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.publishingResult)
            assertEquals(actual.selectedAssignmentId, expectedAssignment.id)

            assertFalse(actual.assignedPetsLoading)
            assertThat(actual.assignedPets).containsOnly(expectedPet)
        }
    }

    //checking loading pets by pages
    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_negativePage_loadFirstPage() = runTest {
        //expected
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testDateTime,
            testDateTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        val expectedPets1 = (1..5).map {
            expectedPet.copy(id = it.toString())
        }

        //defining mock for loading own pets
        everySuspend {
            petsRepoMock.getOwnPets(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        //defining mock for loading assigned pets
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                1,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets1, 1, 2, 5
            )
        )

        //defining mock for loading assignment data
        everySuspend { assignmentsRepoMock.getAssignmentById(any()) } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining viewModel
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetEditedAssignmentId("test-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(-1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.assignedPetsLoading)
            assertEquals(1 to 1, actual.assignedPetsPages)
            assertThat(actual.assignedPets).containsOnly(*expectedPets1.toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_largerPage_loadLargerPage() = runTest {
        //expected
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testDateTime,
            testDateTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        val expectedPets1 = (1..15).map {
            expectedPet.copy(id = it.toString())
        }
        val expectedPets2 = (16..30).map {
            expectedPet.copy(id = it.toString())
        }

        //defining mock for loading own pets
        everySuspend {
            petsRepoMock.getOwnPets(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        //defining mock for loading assigned pets page 1
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                1,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets1, 1, 2, 15
            )
        )

        //defining mock for loading assigned pets page 10
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                10,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets2, 10, 11, 15
            )
        )

        //defining mock for loading assignment data
        everySuspend { assignmentsRepoMock.getAssignmentById(any()) } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining view model
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetEditedAssignmentId("test-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(10))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.assignedPetsLoading)
            assertEquals(9 to 10, actual.assignedPetsPages)
            assertThat(actual.assignedPets).containsOnly(*expectedPets2.toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_2page_loadSecondPage() = runTest {
        //expected
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testDateTime,
            testDateTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        val expectedPets1 = (1..15).map {
            expectedPet.copy(id = it.toString())
        }
        val expectedPets2 = (16..30).map {
            expectedPet.copy(id = it.toString())
        }

        //defining mock for loading own pets
        everySuspend {
            petsRepoMock.getOwnPets(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        //defining mock for loading assigned pets page 1
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                1,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets1, 1, 2, 15
            )
        )

        //defining mock for loading assigned pets page 2
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                2,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets2, 2, 2, 15
            )
        )

        //defining mock for loading assignment data
        everySuspend { assignmentsRepoMock.getAssignmentById(any()) } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining view model
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetEditedAssignmentId("test-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(2))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.assignedPetsLoading)
            assertEquals(1 to 2, actual.assignedPetsPages)
            assertThat(actual.assignedPets).containsOnly(*(expectedPets1.plus(expectedPets2)).toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_upTo3pageDownTo_loadSecondPage() = runTest {
        //expected
        val expectedAssignment = Assignment(
            "test-id",
            "test-walker-id",
            "test-walker-id",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            "orem ipsum dolor sit amet, consectetur adipiscing elit.",
            ServiceType.House_Sitting,
            testDateTime,
            testDateTime,
            AssignmentState.Searching,
            testLocation,
            null
        )
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            testDateTime,
            1f
        )
        val expectedPets1 = (1..15).map {
            expectedPet.copy(id = it.toString())
        }
        val expectedPets2 = (16..30).map {
            expectedPet.copy(id = it.toString())
        }
        val expectedPets3 = (31..45).map {
            expectedPet.copy(id = it.toString())
        }

        //defining mock for loading own pets
        everySuspend {
            petsRepoMock.getOwnPets(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        //defining mock for loading assigned pets page 1
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                1,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets1, 1, 2, 15
            )
        )

        //defining mock for loading assigned pets page 2
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                2,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets2, 2, 2, 15
            )
        )

        //defining mock for loading assigned pets page 3
        everySuspend {
            petsRepoMock.getAssignmentPets(
                any(),
                3,
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedPets3, 2, 2, 15
            )
        )

        //defining mock for loading assignment data
        everySuspend { assignmentsRepoMock.getAssignmentById(any()) } returns APIResult.Succeed(
            expectedAssignment
        )

        //defining view model
        val assignmentConfViewModel = AssignmentConfigureViewModel(
            assignmentsRepoMock,
            authDataStoreMock,
            petsRepoMock,
            locationServiceMock
        )

//      testing
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.SetEditedAssignmentId("test-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //loading 2 page
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(2))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //loading 3 page
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(3))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //loading 1 page
        assignmentConfViewModel.onEvent(AssignmentConfigureUiEvent.LoadAssignedPets(1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = assignmentConfViewModel.state.first()
        assertAll {
            assertFalse(actual.assignedPetsLoading)
            assertEquals(1 to 2, actual.assignedPetsPages)
            assertThat(actual.assignedPets).containsOnly(*(expectedPets1.plus(expectedPets2)).toTypedArray())
        }
    }
}