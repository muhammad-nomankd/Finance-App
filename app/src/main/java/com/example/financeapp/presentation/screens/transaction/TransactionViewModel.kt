package com.example.financeapp.presentation.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.core.domain.usecase.GetFilteredTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val filterType: TransactionType? = null,
    val filterCategory: Category? = null,
    val currency: String = "$",
    val snackbarMessage: String? = null,
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getFiltered: GetFilteredTransactionsUseCase,
    private val deleteTransaction: DeleteTransactionUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _filterType = MutableStateFlow<TransactionType?>(null)
    private val _filterCategory = MutableStateFlow<Category?>(null)
    private val _snackbar = MutableStateFlow<String?>(null)

    // DB results driven only by debounced query + filters
    private val _filteredTransactions = combine(
        _query.debounce(300),
        _filterType,
        _filterCategory,
    ) { query, type, category ->
        Triple(query, type, category)
    }.flatMapLatest { (query, type, category) ->
        getFiltered(query, type, category)
    }

    // Final uiState combines DB results with raw query (no flatMapLatest touching _query)
    val uiState: StateFlow<TransactionsUiState> = combine(
        _filteredTransactions,
        _query,
        _filterType,
        _filterCategory,
        _snackbar,
    ) { transactions, query, type, category, snackbar ->
        TransactionsUiState(
            isLoading = false,
            transactions = transactions,
            searchQuery = query,         // always reflects what user typed instantly
            filterType = type,
            filterCategory = category,
            snackbarMessage = snackbar,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionsUiState(),
    )

    fun onSearchQuery(query: String) { _query.value = query }
    fun onFilterType(type: TransactionType?) { _filterType.value = type }
    fun onFilterCategory(category: Category?) { _filterCategory.value = category }

    fun onDeleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransaction(id)
            _snackbar.value = "Transaction deleted"
        }
    }

    fun clearSnackbar() { _snackbar.value = null }
}