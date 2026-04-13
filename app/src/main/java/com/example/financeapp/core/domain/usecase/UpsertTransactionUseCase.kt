package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.repository.TransactionRepository
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        require(transaction.amount > 0) { "Amount must be positive" }
        require(transaction.title.isNotBlank()) { "Title cannot be blank" }
        repository.upsertTransaction(transaction)
    }
}