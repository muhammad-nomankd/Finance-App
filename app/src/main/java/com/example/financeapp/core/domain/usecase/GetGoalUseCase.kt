package com.example.financeapp.core.domain.usecase

import com.example.financeapp.core.domain.repository.GoalRepository
import javax.inject.Inject
class GetGoalByIdUseCase @Inject constructor(private val repo: GoalRepository) {
    suspend operator fun invoke(id: Long) = repo.getGoalById(id)
}