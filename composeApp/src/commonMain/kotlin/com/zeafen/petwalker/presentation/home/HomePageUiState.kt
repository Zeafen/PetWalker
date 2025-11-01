package com.zeafen.petwalker.presentation.home

import  com.zeafen.petwalker.domain.models.api.reviews.ReviewsStats
import com.zeafen.petwalker.domain.models.api.users.Walker
import com.zeafen.petwalker.domain.models.api.util.APIResult

data class HomePageUiState(
    val currentUserName: String = "",
    val currentUserImageUrl: String? = null,


    val bestWalker: APIResult<Walker, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),
    val bestWalkerStatistics: APIResult<ReviewsStats, com.zeafen.petwalker.domain.models.api.util.Error> = APIResult.Downloading(),

    val featuredWalkers: List<Walker> = emptyList(),
    val currentWalkersPagesComb: Pair<Int, Int> = 1 to 1,
    val maxWalkerPages: Int = 2,
    val isLoadingWalkers: Boolean = false,
    val isLoadingForward: Boolean = false,
    val maxWalkersPagesReached: Boolean = false,
    val walkersLoadingError: com.zeafen.petwalker.domain.models.api.util.Error? = null,

    val hasNewRecruitmentsAsOwner: Boolean = false,
    val hasNewRecruitmentsAsWalker: Boolean = false,
)
