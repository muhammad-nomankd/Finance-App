package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.FinancialSummary
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


class GetFinancialSummaryUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    operator fun invoke(): Flow<FinancialSummary> {
        return combine(
            repo.getTotalByType(TransactionType.INCOME),
            repo.getTotalByType(TransactionType.EXPENSE)
        ) { income, expense ->
            val i = income ?: 0.0
            val e = expense ?: 0.0
            FinancialSummary(
                totalBalance = i - e,
                totalIncome = i,
                totalExpense = e,
                savingsRate = if (i > 0) ((i - e) / i).coerceIn(0.0, 1.0) else 0.0,
                balance = i - e
            )
        }
    }
}