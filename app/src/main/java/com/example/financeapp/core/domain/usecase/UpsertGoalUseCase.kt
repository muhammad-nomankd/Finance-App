package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.model.Goal
import com.example.financeapp.core.domain.repository.GoalRepository
import javax.inject.Inject

class UpsertGoalUseCase @Inject constructor(private val repo: GoalRepository) {
    suspend operator fun invoke(goal: Goal) = repo.upsertGoal(goal)
}
