package com.example.financeapp.core.domain.model

enum class GoalType(val label: String, val description: String) {
    SAVINGS_GOAL("Savings Goal", "Save a target amount by a deadline"),
    NO_SPEND_CHALLENGE("No-Spend Challenge", "Track days with zero unnecessary spending"),
    BUDGET_LIMIT("Budget Limit", "Stay under a monthly spending cap"),
}