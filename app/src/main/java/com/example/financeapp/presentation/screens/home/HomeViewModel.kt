package com.example.financeapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.core.domain.model.FinancialSummary
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.usecase.GetAllTransactionsUseCase
import com.example.financeapp.core.domain.usecase.GetFinancialSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val summary: FinancialSummary? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val currency: String = "$",
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFinancialSummary: GetFinancialSummaryUseCase,
    private val getAllTransactions: GetAllTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getFinancialSummary(),
                getAllTransactions(),
            ) { summary, transactions ->
                HomeUiState(
                    isLoading = false,
                    summary = summary,
                    recentTransactions = transactions.take(5),
                )
            }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { state -> _uiState.value = state }
        }
    }
}