package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFilteredTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(
        query: String = "",
        type: TransactionType? = null,
        category: Category? = null,
    ): Flow<List<Transaction>> = repository.getAllTransactions().map { list ->
        list.filter { tx ->
            val matchesQuery = query.isBlank() ||
                    tx.title.contains(query, ignoreCase = true) ||
                    tx.note.contains(query, ignoreCase = true)
            val matchesType = type == null || tx.type == type
            val matchesCategory = category == null || tx.category == category
            matchesQuery && matchesType && matchesCategory
        }
    }
}

