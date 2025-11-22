package com.zeafen.petwalker.presentation.assignments

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.containsOnly
import com.zeafen.petwalker.domain.models.UserInfo
import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.messaging.Channel
import com.zeafen.petwalker.domain.models.api.messaging.Message
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.ChannelsRepository
import com.zeafen.petwalker.presentation.channel.ChannelDetailsUiEvent
import com.zeafen.petwalker.presentation.channel.ChannelDetailsViewModel
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ChannelDetailsViewModelTest {
    private val channelsRepoMock = mock<ChannelsRepository>()
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

    //testing loading channel data
    @Test
    fun channelDetailsViewModel_LoadChannel_returnsError_cannotSendMessage() = runTest {
        //defining
        val expectedErrorCode = NetworkError.UNAUTHORIZED
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Error(
            expectedErrorCode
        )

        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

        //testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertIs<APIResult.Error<Error>>(actual.channel)
            assertEquals(expectedErrorCode, actual.channel.info)
            assertFalse(actual.canSend.isValid)
        }
    }

    @Test
    fun channelDetailsViewModel_LoadChannel_returnsSucceedClosed_cannotSendMessage() = runTest {
        //defining
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            true,
            123L,
            emptyList()
        )
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )

        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

        //testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Channel>>(actual.channel)
            assertEquals(expectedOpenedChannel, actual.channel.data)
            assertFalse(actual.canSend.isValid)
        }
    }

    @Test
    fun channelDetailsViewModel_LoadChannel_returnsSucceed_canSendMessage() = runTest {
        //defining
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            false,
            123L,
            emptyList()
        )
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                any(),
                any()
            )
        } returns APIResult.Error(
            NetworkError.UNAUTHORIZED
        )

        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

        //testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.SetMessageString("Lorem ipsum dolor sit amet"))
        withContext(Dispatchers.Default) { delay(10) }

        //assert
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertIs<APIResult.Succeed<Channel>>(actual.channel)
            assertEquals(expectedOpenedChannel, actual.channel.data)
            assertTrue(actual.canSend.isValid)
        }
    }

    //testing messages loading
    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_negativePage_loadFirstPage() = runTest {
        //expected
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            false,
            123L,
            emptyList()
        )
        val expectedMessage = Message(
            id = "test-message-id",
            senderId = "test-sender-id",
            body = "Lorem ipsum dolor sit amet",
            isRead = false,
            isOwn = true,
            dateSent = LocalDateTime(2000, 1, 1, 1, 1),
            dateEdited = null,
            attachments = emptyList()
        )
        val expectedMessages1 = (1..15).map {
            expectedMessage.copy(id = it.toString())
        }

        //defining mock for loading assigned pets
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                1,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages1, 1, 11, 15
            )
        )

        //defining mock for loading channel data
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )

        //defining viewModel
        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

//      testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(-1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertFalse(actual.areMessagesLoading)
            assertEquals(1 to 1, actual.currentMessagesPageComb)
            assertThat(actual.messages).containsOnly(*expectedMessages1.toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_largerPage_loadLargerPage() = runTest {
        //expected
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            false,
            123L,
            emptyList()
        )
        val expectedMessage = Message(
            id = "test-message-id",
            senderId = "test-sender-id",
            body = "Lorem ipsum dolor sit amet",
            isRead = false,
            isOwn = true,
            dateSent = LocalDateTime(2000, 1, 1, 1, 1),
            dateEdited = null,
            attachments = emptyList()
        )
        val expectedMessages1 = (1..15).map {
            expectedMessage.copy(id = it.toString())
        }
        val expectedMessages2 = (16..30).map {
            expectedMessage.copy(id = it.toString())
        }

        //defining mock for loading assigned pets page 1
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                1,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages1, 1, 11, 15
            )
        )

        //defining mock for loading assigned pets page 10
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                10,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages2, 10, 11, 15
            )
        )

        //defining mock for loading channel data
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )

        //defining viewModel
        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

//      testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(10))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertFalse(actual.areMessagesLoading)
            assertEquals(9 to 10, actual.currentMessagesPageComb)
            assertThat(actual.messages).containsOnly(*expectedMessages2.toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_2page_loadSecondPage() = runTest {
        //expected
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            false,
            123L,
            emptyList()
        )
        val expectedMessage = Message(
            id = "test-message-id",
            senderId = "test-sender-id",
            body = "Lorem ipsum dolor sit amet",
            isRead = false,
            isOwn = true,
            dateSent = LocalDateTime(2000, 1, 1, 1, 1),
            dateEdited = null,
            attachments = emptyList()
        )
        val expectedMessages1 = (1..15).map {
            expectedMessage.copy(id = it.toString())
        }
        val expectedMessages2 = (16..30).map {
            expectedMessage.copy(id = it.toString())
        }

        //defining mock for loading assigned pets page 1
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                1,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages1, 1, 11, 15
            )
        )

        //defining mock for loading assigned pets page 2
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                2,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages2, 2, 11, 15
            )
        )

        //defining mock for loading channel data
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )

        //defining viewModel
        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

//      testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) {
            delay(50)
        }

        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(2))
        withContext(Dispatchers.Default) {
            delay(50)
        }

//      asserting
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertFalse(actual.areMessagesLoading)
            assertEquals(1 to 2, actual.currentMessagesPageComb)
            assertThat(actual.messages).containsOnly(*(expectedMessages1.plus(expectedMessages2)).toTypedArray())
        }
    }

    @Test
    fun assignmentConfigureViewModel_loadAssignedPets_upTo3pageDownTo_loadSecondPage() = runTest {
        //expected
        val expectedOpenedChannel = Channel(
            "test-channel-id",
            "Lorem ipsum dolor sit amet",
            null,
            false,
            123L,
            emptyList()
        )
        val expectedMessage = Message(
            id = "test-message-id",
            senderId = "test-sender-id",
            body = "Lorem ipsum dolor sit amet",
            isRead = false,
            isOwn = true,
            dateSent = LocalDateTime(2000, 1, 1, 1, 1),
            dateEdited = null,
            attachments = emptyList()
        )
        val expectedMessages1 = (1..15).map {
            expectedMessage.copy(id = it.toString())
        }
        val expectedMessages2 = (16..30).map {
            expectedMessage.copy(id = it.toString())
        }
        val expectedMessages3 = (31..45).map {
            expectedMessage.copy(id = it.toString())
        }

        //defining mock for loading assigned pets page 1
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                1,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages1, 1, 11, 15
            )
        )

        //defining mock for loading assigned pets page 2
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                2,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages2, 2, 11, 15
            )
        )

        //defining mock for loading assigned pets page 3
        everySuspend {
            channelsRepoMock.getChannelMessages(
                any(),
                3,
                any(),
            )
        } returns APIResult.Succeed(
            PagedResult(
                expectedMessages3, 3, 11, 15
            )
        )

        //defining mock for loading channel data
        everySuspend { channelsRepoMock.getAssignmentChannel("test-assignment-id") } returns APIResult.Succeed(
            expectedOpenedChannel
        )

        //defining viewModel
        val channelDetailsViewModel = ChannelDetailsViewModel(
            channelsRepoMock,
            authDataStoreMock,
            null
        )

//      testing
        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadChannel("test-assignment-id"))
        withContext(Dispatchers.Default) {
            delay(10)
        }

        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(2))
        withContext(Dispatchers.Default) {
            delay(10)
        }


        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(3))
        withContext(Dispatchers.Default) {
            delay(10)
        }


        channelDetailsViewModel.onEvent(ChannelDetailsUiEvent.LoadMessages(1))
        withContext(Dispatchers.Default) {
            delay(10)
        }

//      asserting
        val actual = channelDetailsViewModel.state.first()
        assertAll {
            assertFalse(actual.areMessagesLoading)
            assertEquals(1 to 2, actual.currentMessagesPageComb)
            assertThat(actual.messages).containsOnly(*(expectedMessages1.plus(expectedMessages2)).toTypedArray())
        }
    }
}