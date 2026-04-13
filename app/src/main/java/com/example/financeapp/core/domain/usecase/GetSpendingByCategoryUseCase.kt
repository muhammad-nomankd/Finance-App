package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.SpendingByCategory
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class GetSpendingByCategoryUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    operator fun invoke(): Flow<List<SpendingByCategory>> {
        return repo.getTransactionsByType(TransactionType.EXPENSE).map { transactions ->
            val total = transactions.sumOf { it.amount }
            transactions.groupBy { it.category }
                .map { (cat, txns) ->
                    val amount = txns.sumOf { it.amount }
                    SpendingByCategory(
                        category = cat,
                        amount = amount,
                        percentage = if (total > 0) (amount / total * 100).toFloat() else 0f
                    )
                }
                .sortedByDescending { it.amount }
        }
    }
}