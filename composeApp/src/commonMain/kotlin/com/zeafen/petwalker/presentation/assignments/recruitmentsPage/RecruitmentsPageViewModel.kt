package com.zeafen.petwalker.presentation.assignments.recruitmentsPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeafen.petwalker.domain.models.api.assignments.Recruitment
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.NetworkError
import com.zeafen.petwalker.domain.models.ui.RecruitmentModel
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthDataStoreRepository
import com.zeafen.petwalker.domain.services.RecruitmentsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RecruitmentsPageViewModel(
    private val recruitmentsRepository: RecruitmentsRepository,
    private val assignmentsRepository: AssignmentsRepository,
    private val usersRepository: UsersRepository,
    private val authDataStore: AuthDataStoreRepository
) : ViewModel() {

    private val _state: MutableStateFlow<RecruitmentsPageUiState> = MutableStateFlow(
        RecruitmentsPageUiState()
    )
    val state: StateFlow<RecruitmentsPageUiState> = _state.asStateFlow()

    private val mutex = Mutex()
    private var inRecruitmentsLoadingJob: Job? = null
    private var outRecruitmentsLoadingJob: Job? = null
    fun onEvent(event: RecruitmentsPageUiEvent) {
        viewModelScope.launch {
            when (event) {
                is RecruitmentsPageUiEvent.AcceptsRecruitment -> {
                    mutex.withLock {
                        if (state.value.recruitmentRequestResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Downloading(),
                                openResultDialog = true
                            )
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                    }

                    val result = recruitmentsRepository.approveRecruitment(
                        event.recruitmentId
                    )

                    _state.update {
                        it.copy(
                            recruitmentRequestResult = result
                        )
                    }
                    if (state.value.recruitmentsDirectionIndex == 0)
                        onEvent(
                            RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                state.value.lastSelectedIncomingPage,
                                false
                            )
                        )
                    else
                        onEvent(
                            RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                state.value.lastSelectedOutcomingPage,
                                true
                            )
                        )
                }

                is RecruitmentsPageUiEvent.DeclineRecruitment -> {
                    mutex.withLock {
                        if (state.value.recruitmentRequestResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Downloading(),
                                openResultDialog = true
                            )
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                    }

                    val result = recruitmentsRepository.declineRecruitment(
                        event.recruitmentId
                    )

                    _state.update {
                        it.copy(
                            recruitmentRequestResult = result
                        )
                    }
                }

                is RecruitmentsPageUiEvent.LoadOwnRecruitments -> {
                    if (inRecruitmentsLoadingJob?.isActive == true)
                        inRecruitmentsLoadingJob?.cancel()

                    inRecruitmentsLoadingJob = launch {
                        _state.update {
                            if (event.outComing)
                                it.copy(outcomingRecruitments = APIResult.Downloading())
                            else
                                it.copy(incomingRecruitments = APIResult.Downloading())
                        }

                        val token = authDataStore.authDataStoreFlow.first().token
                        if (token == null) {
                            _state.update {
                                if (event.outComing)
                                    it.copy(outcomingRecruitments = APIResult.Error(NetworkError.UNAUTHORIZED))
                                else
                                    it.copy(incomingRecruitments = APIResult.Error(NetworkError.UNAUTHORIZED))
                            }
                            return@launch
                        }

                        val recruitments = when (state.value.selectedLoadGroup) {
                            RecruitmentsLoadGroup.All ->
                                recruitmentsRepository.getRecruitments(
                                    event.page,
                                    15,
                                    state.value.selectedState,
                                    event.outComing,
                                    state.value.dateFrom?.toString(),
                                    state.value.dateUntil?.toString()
                                )

                            RecruitmentsLoadGroup.AsWalker ->
                                recruitmentsRepository.getRecruitmentsAsWalker(
                                    event.page,
                                    15,
                                    state.value.selectedState,
                                    event.outComing,
                                    state.value.dateFrom?.toString(),
                                    state.value.dateUntil?.toString()
                                )

                            RecruitmentsLoadGroup.AsOwner ->
                                recruitmentsRepository.getRecruitmentsAsOwner(
                                    event.page,
                                    15,
                                    state.value.selectedState,
                                    event.outComing,
                                    state.value.dateFrom?.toString(),
                                    state.value.dateUntil?.toString()
                                )
                        }

                        val models = if (recruitments is APIResult.Succeed) recruitments.data?.let {
                            it.result.map {
                                async { getModelForRecruitment(it) }
                            }
                        } else null

                        val result = if (recruitments is APIResult.Error)
                            APIResult.Error(recruitments.info)
                        else APIResult.Succeed(
                            PagedResult(
                                result = models!!.awaitAll(),
                                currentPage = (recruitments as APIResult.Succeed).data!!.currentPage,
                                totalPages = recruitments.data!!.totalPages,
                                pageSize = recruitments.data.pageSize
                            )
                        )

                        _state.update {
                            if (event.outComing)
                                it.copy(
                                    outcomingRecruitments = result,
                                    lastSelectedOutcomingPage = (
                                            if (result is APIResult.Succeed)
                                                result.data?.currentPage
                                            else null
                                            ) ?: it.lastSelectedOutcomingPage
                                )
                            else
                                it.copy(
                                    incomingRecruitments = result,
                                    lastSelectedIncomingPage = (
                                            if (result is APIResult.Succeed)
                                                result.data?.currentPage
                                            else null
                                            ) ?: it.lastSelectedIncomingPage
                                )
                        }
                    }
                }

                is RecruitmentsPageUiEvent.SetLoadType -> {
                    _state.update {
                        it.copy(recruitmentsDirectionIndex = if (event.loadIncoming) 0 else 1)
                    }
                    when {
                        event.loadIncoming
                                && state.value.incomingRecruitments is APIResult.Downloading
                                && inRecruitmentsLoadingJob?.isActive != true ->
                            onEvent(
                                RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                    state.value.lastSelectedIncomingPage,
                                    false
                                )
                            )

                        !event.loadIncoming
                                && state.value.outcomingRecruitments is APIResult.Downloading
                                && outRecruitmentsLoadingJob?.isActive != true ->
                            onEvent(
                                RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                    state.value.lastSelectedOutcomingPage,
                                    true
                                )
                            )
                    }
                }

                is RecruitmentsPageUiEvent.SetFilters -> {
                    _state.update {
                        it.copy(
                            selectedLoadGroup = event.loadGroup,
                            selectedState = event.state,
                            dateFrom = event.dateFrom,
                            dateUntil = event.dateUntil
                        )
                    }
                    onEvent(RecruitmentsPageUiEvent.LoadOwnRecruitments(outComing = state.value.recruitmentsDirectionIndex == 1))
                }

                is RecruitmentsPageUiEvent.DeleteRecruitment -> {
                    mutex.withLock {
                        if (state.value.recruitmentRequestResult is APIResult.Downloading)
                            return@launch
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Downloading(),
                                openResultDialog = true
                            )
                        }
                    }

                    val token = authDataStore.authDataStoreFlow.first().token
                    if (token == null) {
                        _state.update {
                            it.copy(
                                recruitmentRequestResult = APIResult.Error(
                                    NetworkError.UNAUTHORIZED
                                )
                            )
                        }
                    }

                    val result =
                        recruitmentsRepository.deleteRecruitment(event.id)
                    _state.update {
                        it.copy(recruitmentRequestResult = result)
                    }
                    if (result is APIResult.Succeed)
                        when (state.value.recruitmentsDirectionIndex) {
                            0 -> onEvent(
                                RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                    state.value.lastSelectedIncomingPage,
                                    false
                                )
                            )

                            1 -> onEvent(
                                RecruitmentsPageUiEvent.LoadOwnRecruitments(
                                    state.value.lastSelectedOutcomingPage,
                                    true
                                )
                            )
                        }
                }
            }
        }
    }

    private suspend fun getModelForRecruitment(
        recruitment: Recruitment
    ): RecruitmentModel {
        val assignment =
            assignmentsRepository.getAssignmentById(recruitment.assignmentId)
        val user =
            usersRepository.getWalker(recruitment.userId)

        val userData = if (user is APIResult.Succeed) user.data else null
        val assignmentData = if (assignment is APIResult.Succeed) assignment.data else null

        return RecruitmentModel(
            recruitment.id,
            assignmentData?.title,
            assignmentData?.dateTime,
            "${userData?.firstName} ${userData?.lastName}",
            userData?.imageUrl,
            recruitment.assignmentId,
            recruitment.userId,
            recruitment.outcoming,
            recruitment.state
        )
    }
}

