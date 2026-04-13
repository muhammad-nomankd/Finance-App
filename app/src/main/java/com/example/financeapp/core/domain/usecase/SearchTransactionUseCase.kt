package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.TransactionRepository
import javax.inject.Inject

class SearchTransactionsUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    operator fun invoke(query: String) = repo.searchTransactions(query)
}
