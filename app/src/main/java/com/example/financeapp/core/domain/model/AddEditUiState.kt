package com.example.financeapp.core.domain.model

import java.time.LocalDate

data class AddEditUiState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: Category = Category.FOOD,
    val note: String = "",
    val date: LocalDate,
    val titleError: String? = null,
    val amountError: String? = null,
    val isSaved: Boolean = false,
    val saveError: String? = null,
)