package com.example.financeapp.core.domain.model

data class WeeklyTrend(
    val dayLabel: String,
    val income: Double,
    val expense: Double
)