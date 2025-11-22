package com.zeafen.petwalker.presentation.pets

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.PetsRepository
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureUiEvent
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureViewModel
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
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PetConfigureViewModelTest {
    val petsRepoMock = mock<PetsRepository>()
    val authDataStoreMock = mock<AuthDataStoreRepository>()

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

    //testing editing species, name and breed
    @Test
    fun petConfigureViewModel_SetPetSpecies_Blank_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("  "))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.speciesValidation.isValid)
            assertEquals("  ", actual.petSpecies)

            assertFalse(actual.nameValidation.isValid)
            assertEquals("  ", actual.petName)

            assertFalse(actual.breedValidation.isValid)
            assertEquals("  ", actual.petBreed)
        }
    }

    @Test
    fun petConfigureViewModel_SetPetSpecies_LargerThan200_ValidationFailed() = runTest {
        //defining
        val expectedValue =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed at nibh ut ante suscipit porta in et nisl. Nunc gravida sapien at urna dictum egestas. Donec ultrices ligula non arcu eleifend fringilla. Mauris varius rhoncus justo quis facilisis. Quisque convallis venenatis velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc dignissim erat turpis, nec ultrices turpis rhoncus sit amet. Vivamus at velit vestibulum purus fringilla pulvinar."
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies(expectedValue))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName(expectedValue))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed(expectedValue))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.speciesValidation.isValid)
            assertEquals(expectedValue, actual.petSpecies)

            assertFalse(actual.nameValidation.isValid)
            assertEquals(expectedValue, actual.petName)

            assertFalse(actual.breedValidation.isValid)
            assertEquals(expectedValue, actual.petBreed)
        }
    }

    @Test
    fun petConfigureViewModel_SetPetSpecies_SomeSpecies_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("Lorem ipsum"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("dolor"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertTrue(actual.speciesValidation.isValid)
        assertEquals("Lorem ipsum", actual.petSpecies)

        assertTrue(actual.nameValidation.isValid)
        assertEquals("dolor", actual.petName)

        assertTrue(actual.breedValidation.isValid)
        assertEquals("sit amet", actual.petBreed)
    }

    //testing editing date birth
    @Test
    fun petConfigureViewModel_SetDateBirth_Null_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(null))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.date_birthValidation.isValid)
            assertNull(actual.petDateBirth)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_SetDateBirth_LaterThanNow_ValidationFailed() = runTest {
        //defining
        val expectedDateTime = Clock.System.now()
            .plus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(expectedDateTime))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.date_birthValidation.isValid)
            assertEquals(expectedDateTime, actual.petDateBirth)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_SetDateBirth_EarlierThanNow_ValidationPassed() = runTest {
        //defining
        val expectedDateTime = Clock.System.now()
            .minus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(expectedDateTime))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertTrue(actual.date_birthValidation.isValid)
            assertEquals(expectedDateTime, actual.petDateBirth)
        }
    }

    //testing editing weight
    @Test
    fun petConfigureViewModel_SetWeight_nonFloat_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("Lorem ipsum"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.weightValidation.isValid)
            assertEquals("Lorem ipsum", actual.petWeight)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_SetWeight_LessThan0_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("-1"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertFalse(actual.weightValidation.isValid)
            assertEquals("-1", actual.petWeight)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_SetWeight_AnyPositive_ValidationPassed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertTrue(actual.weightValidation.isValid)
            assertEquals("123", actual.petWeight)
        }
    }

    //testing adding medical info
    @Test
    fun petConfigureViewModel_AddPetMedicalInfo_PetNotLoaded_returnsSucceed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).hasSize(1)
        }
    }

    @Test
    fun petConfigureViewModel_AddPetMedicalInfo_PetLoaded_returnsError_listEmpty() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        everySuspend { petsRepoMock.getPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend { petsRepoMock.getPetMedicalInfo(any(), any()) } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )
        everySuspend {
            petsRepoMock.postPetMedicalInfo(
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId("test-pet-id"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).isEmpty()
        }
    }

    @Test
    fun petConfigureViewModel_AddPetMedicalInfo_PetLoaded_returnsSucceed_listNotEmpty() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        val expectedPetMedicalInfo = PetMedicalInfo(
            "test-medinfo-id",
            "Lorem ipsum dolor sit amet",
            PetInfoType.Other,
            null,
            null
        )
        everySuspend { petsRepoMock.getPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend { petsRepoMock.getPetMedicalInfo(any(), any()) } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )
        everySuspend {
            petsRepoMock.postPetMedicalInfo(
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(expectedPetMedicalInfo)

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId("test-pet-id"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).containsOnly(expectedPetMedicalInfo)
        }
    }

    //testing removing medical info
    @Test
    fun petConfigureViewModel_RemovePetMedicalInfo_PetNotLoaded_returnsSucceed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }
        val value = petConfigureViewModel.state.first()

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.RemovePetMedialInfo(value.petMedicalInfos.first().id)
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).isEmpty()
        }
    }

    @Test
    fun petConfigureViewModel_RemovePetMedicalInfo_PetLoaded_returnsError_listFilled() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        val expectedPetMedicalInfo = PetMedicalInfo(
            "test-medinfo-id",
            "Lorem ipsum dolor sit amet",
            PetInfoType.Other,
            null,
            null
        )
        everySuspend { petsRepoMock.getPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend { petsRepoMock.getPetMedicalInfo(any(), any()) } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )
        everySuspend {
            petsRepoMock.postPetMedicalInfo(
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(expectedPetMedicalInfo)
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )
        everySuspend { petsRepoMock.deletePetMedicalInfo(any(), any()) } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId("test-pet-id"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.RemovePetMedialInfo(expectedPetMedicalInfo.id)
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).hasSize(1)
        }
    }

    @Test
    fun petConfigureViewModel_RemovePetMedicalInfo_PetLoaded_returnsSucceed_listEmpty() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        val expectedPetMedicalInfo = PetMedicalInfo(
            "test-medinfo-id",
            "Lorem ipsum dolor sit amet",
            PetInfoType.Other,
            null,
            null
        )
        everySuspend { petsRepoMock.getPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend { petsRepoMock.getPetMedicalInfo(any(), any()) } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )
        everySuspend {
            petsRepoMock.postPetMedicalInfo(
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(expectedPetMedicalInfo)
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )
        everySuspend { petsRepoMock.deletePetMedicalInfo(any(), any()) } returns APIResult.Succeed()

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId("test-pet-id"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet",
                PetWalkerFileInfo(
                    "Lorem ipsum dolor sit amet",
                    "Lorem ipsum",
                    "Lorem ipsum dolor",
                    null
                )
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.RemovePetMedialInfo(expectedPetMedicalInfo.id)
        )
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.medicalInfoEditingResult)
            assertThat(actual.petMedicalInfos).isEmpty()
        }
    }

    //testing uniting validation params into can publish param

    @Test
    fun petConfigureViewModel_CanPublish_AllParamsInvalid() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("  "))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @Test
    fun petConfigureViewModel_CanPublish_WeightValid() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("  "))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertFalse(actual.canPublish)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_CanPublish_AllParamsValid() = runTest {
        //defining
        val dateTime = Clock.System.now()
            .minus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("Lorem ipsum"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("Lorem ipsum dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(dateTime))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertTrue(actual.canPublish)
    }

    //testing publishing data
    @Test
    fun petConfigureViewModel_PublishData_CannotPublish_ValidationFailed() = runTest {
        //defining
        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.PublishData)
        withContext(Dispatchers.Default) {
            delay(10)
        }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertIs<APIResult.Error<Error>>(actual.petLoadingResult)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun petConfigureViewModel_PublishData_CanPublish_returningError() = runTest {
        //defining
        val expectedErrorCode = NetworkError.UNAUTHORIZED
        everySuspend { petsRepoMock.postPet(any()) } returns APIResult.Error(expectedErrorCode)

        val dateTime = Clock.System.now()
            .minus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("Lorem ipsum"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("Lorem ipsum dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(dateTime))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(PetConfigureUiEvent.PublishData)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.petLoadingResult)
            assertEquals(expectedErrorCode, actual.petLoadingResult.info)
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    @Test
    fun petConfigureViewModel_PublishData_CanPublish_returningSucceed() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        val expectedPetMedicalInfo = PetMedicalInfo(
            "test-medinfo-id",
            "Lorem ipsum dolor sit amet",
            PetInfoType.Other,
            null,
            null
        )
        everySuspend { petsRepoMock.postPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend {
            petsRepoMock.postPetMedicalInfo(
                any(),
                any(),
                any(),
                any()
            )
        } returns APIResult.Succeed(expectedPetMedicalInfo.copy(id = Uuid.random().toString()))

        val dateTime = Clock.System.now()
            .minus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("Lorem ipsum"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("Lorem ipsum dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(dateTime))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        petConfigureViewModel.onEvent(
            PetConfigureUiEvent.AddPetMedialInfo(
                PetInfoType.Other,
                "Lorem ipsum dolor sit amet",
                "Lorem ipsum dolor sit amet",
                null
            )
        )
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(PetConfigureUiEvent.PublishData)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.petLoadingResult)
            assertEquals(expectedPet.id, actual.selectedPetId)
            assertThat(actual.petMedicalInfos).hasSize(1)
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    @Test
    fun petConfigureViewModel_PublishData_PetSelected_CanPublish_returningSucceed() = runTest {
        //defining
        val expectedPet = Pet(
            "test-pet-id",
            "test-owner-id",
            null,
            "Lorem ipsum dolor sit amet",
            "Lorem ipsum dolor sit amet, consectetur",
            "Lorem ipsum dolor sit amet, consectetur adipiscing",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            LocalDateTime(2000, 1, 1, 1, 1),
            1f
        )
        everySuspend { petsRepoMock.getPet(any()) } returns APIResult.Succeed(expectedPet)
        everySuspend { petsRepoMock.updatePet(any(), any()) } returns APIResult.Succeed()
        everySuspend {
            petsRepoMock.getPetMedicalInfo(
                any(),
                any(),
            )
        } returns APIResult.Error(NetworkError.UNAUTHORIZED)

        val dateTime = Clock.System.now()
            .minus(10, DateTimeUnit.MINUTE)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        val petConfigureViewModel = PetConfigureViewModel(
            petsRepoMock,
            authDataStoreMock
        )

        //test
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetSelectedPetId("test-pet-id"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetSpecies("Lorem ipsum"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetName("Lorem ipsum dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetBreed("dolor sit amet"))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetDateBirth(dateTime))
        petConfigureViewModel.onEvent(PetConfigureUiEvent.SetPetWeight("123"))
        withContext(Dispatchers.Default) { delay(10) }

        petConfigureViewModel.onEvent(PetConfigureUiEvent.PublishData)
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = petConfigureViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Unit>>(actual.petLoadingResult)
        }
    }
}