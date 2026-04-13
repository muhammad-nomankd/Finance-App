package com.example.financeapp.core.domain.model

import java.time.LocalDate

data class Goal(
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val deadline: LocalDate,
    val type: GoalType,
    val currentAmount: Double = 0.0,
    val streak: Int = 0,
    val emoji: String = "🎯",
    val isExpired: Boolean = false,
    val isCompleted: Boolean = false
)
val Goal.progressFraction: Float
    get() = if (targetAmount == 0.0) 0f else (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()

val Goal.isExpired: Boolean
    get() = deadline != null && LocalDate.now().isAfter(deadline) && !isCompleted
