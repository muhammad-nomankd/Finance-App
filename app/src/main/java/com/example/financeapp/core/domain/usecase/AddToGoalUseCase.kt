package com.example.financeapp.core.domain.usecase
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.repository.GoalRepository
import com.example.financeapp.core.domain.repository.TransactionRepository
import java.time.LocalDate
import javax.inject.Inject

class AddToGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend operator fun invoke(goalId: Long, amount: Double, title: String) {
        goalRepository.updateGoalProgress(goalId, amount)
        // Also record as a transaction for full history
        transactionRepository.upsertTransaction(
            Transaction(
                amount = amount,
                type = TransactionType.EXPENSE, // savings is an "expense" from liquid cash
                category = Category.OTHER,
                title = title,
                note = "Saved towards goal",
                date = LocalDate.now(),
            )
        )
    }
}
