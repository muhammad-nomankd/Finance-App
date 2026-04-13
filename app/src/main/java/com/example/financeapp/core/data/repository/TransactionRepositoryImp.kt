package com.example.financeapp.core.data.repository

import com.example.financeapp.core.data.local.dao.TransactionDao
import com.example.financeapp.core.data.local.entity.toDomain
import com.example.financeapp.core.data.local.entity.toEntity
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.CategorySpend
import com.example.financeapp.core.domain.model.DailySpend
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
	private val dao: TransactionDao
) : TransactionRepository {

	override fun getAllTransactions(): Flow<List<Transaction>> =
		dao.getAllTransactions().map { it.map { e -> e.toDomain() } }

	override fun getTransactionsByDateRange(from: LocalDate, to: LocalDate): Flow<List<Transaction>> =
		dao.getTransactionsByDateRange(
			from.toString(),  // ← was from.toEpochDay()
			to.toString(),    // ← was to.toEpochDay()
		).map { it.map { e -> e.toDomain() } }
	
	override suspend fun getCategoryTotals(from: LocalDate, to: LocalDate): List<CategorySpend> {
		val raw = dao.getCategoryTotals(
			from.toString(),   // "2026-04-01"  ← was from.atStartOfDay().toString()
			to.toString(),     // "2026-04-12"  ← was to.atTime(LocalTime.MAX).toString()
		)
		val total = raw.sumOf { it.totalAmount }
		return raw.map { summary ->
			CategorySpend(
				category = summary.category,
				amount = summary.totalAmount,
				percentage = if (total > 0) (summary.totalAmount / total * 100).toFloat() else 0f,
			)
		}
	}

	override suspend fun getDailySpending(days: Int): List<DailySpend> {
		val from = LocalDate.now().minusDays(days.toLong() - 1)
		return dao.getDailySpending(from.toString())   // "2026-04-06"  ← was from.atStartOfDay().toString()
			.map {
				DailySpend(
					date = LocalDate.parse(it.date),
					amount = it.totalAmount,
				)
			}
	}

	override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
		dao.getTransactionsByType(type.name).map { it.map { e -> e.toDomain() } }

	override fun searchTransactions(query: String): Flow<List<Transaction>> =
		dao.searchTransactions(query).map { it.map { e -> e.toDomain() } }

	override suspend fun getTransactionById(id: Long): Transaction? =
		dao.getTransactionById(id)?.toDomain()

	override suspend fun upsertTransaction(transaction: Transaction) =
		dao.upsertTransaction(transaction.toEntity())

	override suspend fun deleteTransaction(id: Long) =
		dao.deleteById(id)

	override fun getTotalByType(type: TransactionType): Flow<Double?> =
		dao.getTotalByType(type.name)

	override fun getTransactionsByCategory(category: Category): Flow<List<Transaction>> =
		dao.getTransactionsByCategory(category.name).map { it.map { e -> e.toDomain() } }


	override suspend fun getMonthlyIncome(year: Int, month: Int): Double =
		dao.getMonthlyIncome(
			year = year.toString(),
			month = month.toString().padStart(2, '0'),
		)

	override suspend fun getMonthlyExpense(year: Int, month: Int): Double =
		dao.getMonthlyExpense(
			year = year.toString(),
			month = month.toString().padStart(2, '0'),
		)
}
