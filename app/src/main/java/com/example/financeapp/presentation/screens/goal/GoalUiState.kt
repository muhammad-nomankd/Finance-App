package com.example.financeapp.presentation.screens.goal

import com.example.financeapp.core.domain.model.Goal

data class GoalsUiState(
    val isLoading: Boolean = true,
    val activeGoals: List<Goal> = emptyList(),
    val completedGoals: List<Goal> = emptyList(),
    val showAddDialog: Boolean = false,
    val showAddFundsDialog: Goal? = null,
    val snackbarMessage: String? = null,
)