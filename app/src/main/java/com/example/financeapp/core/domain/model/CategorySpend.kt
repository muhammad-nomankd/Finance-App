package com.example.financeapp.core.domain.model


data class CategorySpend(
    val category: Category,
    val amount: Double,
    val percentage: Float,
)