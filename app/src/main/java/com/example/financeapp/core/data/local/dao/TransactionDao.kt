package com.example.financeapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.financeapp.core.data.local.entity.TransactionEntity
import com.example.financeapp.core.domain.model.CategorySummary
import com.example.financeapp.core.domain.model.DailySum
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY createdAt desc")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getTransactionsByDateRange(from: String, to: String): Flow<List<TransactionEntity>>
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE note LIKE '%' || :query || '%' 
        OR category LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalByType(type: String): Flow<Double?>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'INCOME'
        AND strftime('%Y', date) = :year 
        AND strftime('%m', date) = :month
    """)
    suspend fun getMonthlyIncome(year: String, month: String): Double

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM transactions 
        WHERE type = 'EXPENSE'
        AND strftime('%Y', date) = :year 
        AND strftime('%m', date) = :month
    """)
    suspend fun getMonthlyExpense(year: String, month: String): Double

    // ── String params instead of LocalDateTime ──────────────────────────────
    // Room cannot bind LocalDateTime directly. Pass ISO strings (e.g. "2024-01-01T00:00:00")
    // and let SQLite compare them lexicographically — works because ISO-8601 sorts correctly.

    @Query("""
        SELECT category, SUM(amount) as totalAmount 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :from AND date <= :to
        GROUP BY category 
        ORDER BY totalAmount DESC
    """)
    suspend fun getCategoryTotals(from: String, to: String): List<CategorySummary>

    @Query("""
        SELECT date(date) as date, SUM(amount) as totalAmount 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :from
        GROUP BY date(date) 
        ORDER BY date ASC
    """)
    suspend fun getDailySpending(from: String): List<DailySum>
}