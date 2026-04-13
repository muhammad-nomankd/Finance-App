package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.model.WeeklyTrend
import com.example.financeapp.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class GetWeeklyTrendUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    operator fun invoke(): Flow<List<WeeklyTrend>> {
        val today = LocalDate.now()
        val weekStart = today.with(DayOfWeek.MONDAY)
        val weekEnd = today.with(DayOfWeek.SUNDAY)
        return repo.getTransactionsByDateRange(weekStart, weekEnd).map { transactions ->
            (0..6).map { offset ->
                val day = weekStart.plusDays(offset.toLong())
                val dayTxns = transactions.filter { it.date == day }
                WeeklyTrend(
                    dayLabel = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    income = dayTxns.filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount },
                    expense = dayTxns.filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount }
                )
            }
        }
    }
}