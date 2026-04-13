package com.example.financeapp.presentation.screens.insight

import com.example.financeapp.core.domain.model.SpendingInsight
import com.example.financeapp.core.domain.usecase.GetSpendingInsightsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InsightsUiState {
    data object Loading : InsightsUiState
    data class Success(val insight: SpendingInsight) : InsightsUiState
    data class Error(val message: String) : InsightsUiState
}

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getInsights: GetSpendingInsightsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Loading)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = InsightsUiState.Loading
            try {
                val insight = getInsights()
                _uiState.value = InsightsUiState.Success(insight)
            } catch (e: Exception) {
                _uiState.value = InsightsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
