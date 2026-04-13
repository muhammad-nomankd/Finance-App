package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteTransaction(id)
}