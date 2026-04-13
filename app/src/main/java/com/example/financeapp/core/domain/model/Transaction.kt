package com.example.financeapp.core.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val title: String,
    val description: String = "",
    val date: LocalDate = LocalDate.of(2026, 3, 5),
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)