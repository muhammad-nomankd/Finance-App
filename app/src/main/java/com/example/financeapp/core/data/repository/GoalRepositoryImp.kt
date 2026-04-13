package com.example.financeapp.core.data.repository

import com.example.financeapp.core.data.local.dao.GoalDao
import com.example.financeapp.core.data.local.entity.toDomain
import com.example.financeapp.core.data.local.entity.toEntity
import com.example.financeapp.core.domain.model.Goal
import com.example.financeapp.core.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> =
        dao.getAllGoals().map { it.map { e -> e.toDomain() } }

    override suspend fun getGoalById(id: Long): Goal? =
        dao.getGoalById(id)?.toDomain()

    override suspend fun upsertGoal(goal: Goal) =
        dao.upsertGoal(goal.toEntity())

    override suspend fun deleteGoal(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun updateGoalProgress(id: Long, amount: Double) =
        dao.addToGoalProgress(id, amount)
}

