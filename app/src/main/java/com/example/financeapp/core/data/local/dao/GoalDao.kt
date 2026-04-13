package com.example.financeapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.financeapp.core.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Upsert
    suspend fun upsertGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Long)
    @Query("UPDATE goals SET currentAmount = currentAmount + :amount WHERE id = :id")
    suspend fun addToGoalProgress(id: Long, amount: Double)

    @Query("UPDATE goals SET currentAmount = :amount, isCompleted = (currentAmount >= targetAmount) WHERE id = :id")
    suspend fun updateProgress(id: Long, amount: Double)
}
