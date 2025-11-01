package com.zeafen.petwalker.presentation.walkers.walkersList

import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.AccountStatus

sealed interface WalkersPageUiEvent {
    data class EnterSearchTitle(val searchName: String) : WalkersPageUiEvent
    data class SetUsersOrdering(val ordering: UsersOrdering) : WalkersPageUiEvent
    data class SetFilters(
        val services: List<ServiceType>? = null,
        val maxComplaints: Int? = null,
        val status: AccountStatus? = null,
        val online: Boolean? = null
    ) : WalkersPageUiEvent

    data object ClearFilters : WalkersPageUiEvent
    data class LoadWalkers(val page: Int = 1) : WalkersPageUiEvent
}