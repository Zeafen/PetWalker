package com.zeafen.petwalker.presentation.walkers.walkersList

import com.zeafen.petwalker.domain.models.api.filtering.UsersOrdering
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import com.zeafen.petwalker.domain.models.api.users.AccountStatus
import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.util.Error
import com.zeafen.petwalker.domain.models.ui.WalkerCardModel

data class WalkersPageUiState(
    val walkers: APIResult<PagedResult<WalkerCardModel>, Error> = APIResult.Downloading(),
    val lastSelectedPage: Int = 1,
    val searchName: String = "",
    val searchServices: List<ServiceType>? = null,
    val maxComplaintsCount: Int? = null,
    val status: AccountStatus? = null,
    val showOnline: Boolean? = null,
    val ordering: UsersOrdering? = null,
    val ascending: Boolean = true,
)
