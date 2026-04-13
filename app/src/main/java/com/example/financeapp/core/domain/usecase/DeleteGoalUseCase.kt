package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.GoalRepository
import javax.inject.Inject

class DeleteGoalUseCase @Inject constructor(
    private val repository: GoalRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteGoal(id)
}