package com.zeafen.petwalker.domain.models.api.assignments

import com.zeafen.petwalker.domain.models.api.filtering.DatePeriods
import com.zeafen.petwalker.domain.models.api.other.ServiceType
import kotlinx.serialization.Serializable

@Serializable
data class AssignmentsStats(
    val totalAmount: Long,
    val countsMap: Map<ServiceType, Float>,

    val totalIncome: Float,
    val incomesMap: Map<ServiceType, Float>,

    val avgIncome: Float,
    val avgIncomesMap: Map<ServiceType, Float>,
)