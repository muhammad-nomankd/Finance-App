package com.example.financeapp.core.domain.model

import java.time.LocalDate

data class DailySpend(
    val date: LocalDate,
    val amount: Double,
)
