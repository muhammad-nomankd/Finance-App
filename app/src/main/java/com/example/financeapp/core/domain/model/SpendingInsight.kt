package com.example.financeapp.core.domain.model
data class SpendingInsight(
    val topCategory: Category?,
    val topCategoryAmount: Double,
    val thisWeekTotal: Double,
    val lastWeekTotal: Double,
    val thisMonthTotal: Double,
    val lastMonthTotal: Double,
    val categoryBreakdown: List<CategorySpend>,
    val dailySpending: List<DailySpend>,        // last 7 days
    val spendingVelocity: Double,               // % faster/slower vs last month same period
)