package com.zeafen.petwalker.domain.models

import com.zeafen.petwalker.domain.models.api.auth.TokenResponse
import com.zeafen.petwalker.domain.models.api.users.APILocation
import com.zeafen.petwalker.domain.models.api.users.LocationInfo

data class UserInfo(
    val firstName: String = "",
    val lastName: String = "",
    val email : String? = "",
    val defaultLocationInfo: APILocation? = null,
    val token : TokenResponse? = null,
    val imageUrl : String? = null
)
