package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    suspend operator fun invoke(id: Long) = repo.getTransactionById(id)
}