package com.example.financeapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.Goal
import com.example.financeapp.core.domain.model.GoalType
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String,
    val date: String,        // ← was Long, now "2025-04-11"
    val note: String,
    val createdAt: Long = System.currentTimeMillis(),
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = TransactionType.valueOf(type),
    category = Category.valueOf(category),
    date = LocalDate.parse(date),   // "2025-04-11" → LocalDate
    note = note,
    createdAt = createdAt,
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type.name,
    category = category.name,
    date = date.toString(),         // LocalDate → "2025-04-11"
    note = note,
    createdAt = createdAt,
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: Long,         // LocalDate.toEpochDay()
    val emoji: String,
    val isCompleted: Boolean,
    val type: GoalType = GoalType.SAVINGS_GOAL,
    val isExpired: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),

)

fun GoalEntity.toDomain() = Goal(
    id = id,
    title = title,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    deadline = LocalDate.ofEpochDay(deadline),
    emoji = emoji,
    isCompleted = isCompleted,
    type = type,
    currentAmount = currentAmount,
    isExpired = isExpired
)

fun Goal.toEntity() = GoalEntity(
    id = id,
    title = title,
    targetAmount = targetAmount,
    savedAmount = savedAmount,
    deadline = deadline.toEpochDay(),
    emoji = emoji,
    isCompleted = isCompleted
)