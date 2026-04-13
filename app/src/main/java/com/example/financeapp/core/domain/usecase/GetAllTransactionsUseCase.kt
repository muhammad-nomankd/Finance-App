package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.TransactionRepository
import javax.inject.Inject


class GetAllTransactionsUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    operator fun invoke() = repo.getAllTransactions()
}
