package com.example.financeapp.core.domain.model

data class FinancialSummary(
    val balance: Double,
    val totalBalance: Double,
    val totalIncome: Double,
    val totalExpense: Double,
    val savingsRate: Double,
)