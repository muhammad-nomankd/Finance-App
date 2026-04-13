package com.example.financeapp.core.domain.repository

import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.CategorySpend
import com.example.financeapp.core.domain.model.DailySpend
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByDateRange(from: LocalDate, to: LocalDate): Flow<List<Transaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun upsertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)
    fun getTotalByType(type: TransactionType): Flow<Double?>
    fun getTransactionsByCategory(category: Category): Flow<List<Transaction>>
    suspend fun getDailySpending(days: Int): List<DailySpend>

    suspend fun getMonthlyIncome(year: Int, month: Int): Double
    suspend fun getMonthlyExpense(year: Int, month: Int): Double
    suspend fun getCategoryTotals(from: LocalDate, to: LocalDate): List<CategorySpend>
}