package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.GoalRepository
import javax.inject.Inject

class GetAllGoalsUseCase @Inject constructor(private val repo: GoalRepository) {
    operator fun invoke() = repo.getAllGoals()
}

