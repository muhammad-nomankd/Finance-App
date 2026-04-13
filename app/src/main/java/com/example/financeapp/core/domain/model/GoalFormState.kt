package com.example.financeapp.core.domain.model

import java.time.LocalDate

data class GoalFormState(
    val title: String = "",
    val targetAmount: String = "",
    val type: GoalType = GoalType.SAVINGS_GOAL,
    val deadline: LocalDate? = null,
    val titleError: String? = null,
    val amountError: String? = null,
)