package com.example.financeapp.core.domain.repository

import com.example.financeapp.core.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun getGoalById(id: Long): Goal?
    suspend fun upsertGoal(goal: Goal)

    suspend fun deleteGoal(id: Long)

    suspend fun updateGoalProgress(id: Long, amount: Double)
}

