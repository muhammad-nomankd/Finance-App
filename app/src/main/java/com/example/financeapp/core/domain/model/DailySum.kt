package com.example.financeapp.core.domain.model

import java.time.LocalDate
data class DailySum(
    val date: String,          // Room reads date(date) SQL result as plain "2024-01-15"
    val totalAmount: Double,
)