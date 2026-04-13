package com.example.financeapp.core.domain.model

data class SpendingByCategory(
    val category: Category,
    val amount: Double,
    val percentage: Float
)