package com.example.financeapp.presentation.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.core.domain.model.AddEditUiState
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.usecase.GetTransactionByIdUseCase
import com.example.financeapp.core.domain.usecase.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    private val upsertTransaction: UpsertTransactionUseCase,
    private val getTransactionById: GetTransactionByIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditUiState(date = LocalDate.of(2026, 3, 5)))
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    private var editingId: Long? = null

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val tx = getTransactionById(id)
            if (tx != null) {
                editingId = tx.id
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditMode = true,
                        title = tx.title,
                        amount = tx.amount.toString(),
                        type = tx.type,
                        category = tx.category,
                        note = tx.note,
                        date = tx.date,
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = null) }
    fun onAmountChange(value: String) =
        _uiState.update { it.copy(amount = value, amountError = null) }

    fun onTypeChange(type: TransactionType) = _uiState.update {
        it.copy(
            type = type,
            category = if (type == TransactionType.INCOME) Category.SALARY else Category.FOOD,
        )
    }

    fun onCategoryChange(cat: Category) = _uiState.update { it.copy(category = cat) }
    fun onNoteChange(note: String) = _uiState.update { it.copy(note = note) }
    fun onDateChange(date: LocalDate) = _uiState.update { it.copy(date = date) }

    fun save() {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            hasError = true
        }
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Enter a valid positive amount") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            try {
                upsertTransaction(
                    Transaction(
                        id = editingId ?: 0L,
                        title = state.title.trim(),
                        amount = amount!!,
                        type = state.type,
                        category = state.category,
                        note = state.note.trim(),
                        date = state.date!!,
                    )
                )
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveError = e.message) }
            }
        }
    }

    fun onSavedConsumed() {
        _uiState.update {
            it.copy(
                isSaved = false,
                title = "",
                note = "",
                type = TransactionType.EXPENSE,
                isEditMode = false,
                amount = "",
                isLoading = false,
                category = Category.FOOD,
                date = LocalDate.now(),
                titleError = null,
                amountError = null,
                saveError = ""
            )
        }
    }
}
