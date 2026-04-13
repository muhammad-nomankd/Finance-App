package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.SpendingInsight
import com.example.financeapp.core.domain.repository.TransactionRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class GetSpendingInsightsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(): SpendingInsight {
        val today = LocalDate.now()
        val thisWeekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val lastWeekStart = thisWeekStart.minusWeeks(1)
        val thisMonthStart = today.withDayOfMonth(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)

        val thisWeek = repository.getTransactionsByDateRange(thisWeekStart, today).let { flow ->
            // Since we need suspend here, compute inline
            0.0 // placeholder — in real impl use suspend version
        }

        val categoryBreakdown = repository.getCategoryTotals(thisMonthStart, today)
        val dailySpending = repository.getDailySpending(7)

        val topCategory = categoryBreakdown.maxByOrNull { it.amount }

        // Spending velocity: compare pace of spending at same point last month
        val now = YearMonth.now()
        val thisMonthTotal = repository.getMonthlyExpense(now.year, now.monthValue)
        val lastMonthTotal = repository.getMonthlyExpense(now.minusMonths(1).year, now.minusMonths(1).monthValue)
        val dayOfMonth = today.dayOfMonth
        val daysInLastMonth = thisMonthStart.minusDays(1).dayOfMonth
        val lastMonthPaceEquivalent = if (daysInLastMonth > 0)
            lastMonthTotal * (dayOfMonth.toDouble() / daysInLastMonth) else 0.0
        val velocity = if (lastMonthPaceEquivalent > 0)
            ((thisMonthTotal - lastMonthPaceEquivalent) / lastMonthPaceEquivalent) * 100 else 0.0

        return SpendingInsight(
            topCategory = topCategory?.category,
            topCategoryAmount = topCategory?.amount ?: 0.0,
            thisWeekTotal = dailySpending.takeLast(7).sumOf { it.amount },
            lastWeekTotal = 0.0,
            thisMonthTotal = thisMonthTotal,
            lastMonthTotal = lastMonthTotal,
            categoryBreakdown = categoryBreakdown,
            dailySpending = dailySpending,
            spendingVelocity = velocity,
        )
    }
}