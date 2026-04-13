package com.example.financeapp.presentation.screens.goal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.core.domain.model.Goal
import com.example.financeapp.core.domain.model.GoalFormState
import com.example.financeapp.core.domain.model.GoalType
import com.example.financeapp.core.domain.usecase.AddToGoalUseCase
import com.example.financeapp.core.domain.usecase.DeleteGoalUseCase
import com.example.financeapp.core.domain.usecase.GetAllGoalsUseCase
import com.example.financeapp.core.domain.usecase.UpsertGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getAllGoals: GetAllGoalsUseCase,
    private val upsertGoal: UpsertGoalUseCase,
    private val deleteGoal: DeleteGoalUseCase,
    private val addToGoal: AddToGoalUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(GoalFormState())
    val formState: StateFlow<GoalFormState> = _formState.asStateFlow()

    private val _addFundsAmount = MutableStateFlow("")
    val addFundsAmount: StateFlow<String> = _addFundsAmount.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getAllGoals()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            snackbarMessage = e.message
                        )
                    }
                }
                .collect { goals ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            activeGoals = goals.filter { g -> !g.isCompleted && !g.isExpired },
                            completedGoals = goals.filter { g -> g.isCompleted || g.isExpired },
                        )
                    }
                }
        }
    }

    // ── Form actions ──
    fun onFormTitleChange(v: String) = _formState.update { it.copy(title = v, titleError = null) }
    fun onFormAmountChange(v: String) =
        _formState.update { it.copy(targetAmount = v, amountError = null) }

    fun onFormTypeChange(t: GoalType) = _formState.update { it.copy(type = t) }
    fun onFormDeadlineChange(d: LocalDate?) = _formState.update { it.copy(deadline = d) }
    fun onAddFundsAmountChange(v: String) {
        _addFundsAmount.value = v
    }

    fun showAddGoalDialog() {
        _formState.value = GoalFormState()
        _uiState.update { it.copy(showAddDialog = true) }
    }

    fun dismissAddGoalDialog() = _uiState.update { it.copy(showAddDialog = false) }

    fun showAddFundsDialog(goal: Goal) {
        _addFundsAmount.value = ""
        _uiState.update { it.copy(showAddFundsDialog = goal) }
    }

    fun dismissAddFundsDialog() = _uiState.update { it.copy(showAddFundsDialog = null) }

    fun saveGoal() {
        val form = _formState.value
        var hasError = false

        if (form.title.isBlank()) {
            _formState.update { it.copy(titleError = "Title is required") }
            hasError = true
        }
        val amount = form.targetAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _formState.update { it.copy(amountError = "Enter a valid amount") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            try {
                upsertGoal(
                    Goal(
                        title = form.title.trim(),
                        type = form.type,
                        targetAmount = amount!!,
                        deadline = form.deadline ?: LocalDate.now().plusMonths(3),
                    )
                )
                _uiState.update {
                    it.copy(
                        showAddDialog = false,
                        snackbarMessage = "Goal created! 🎯"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(snackbarMessage = e.message) }
            }
        }
    }

    fun addFundsToGoal() {
        val goal = _uiState.value.showAddFundsDialog ?: return
        val amount = _addFundsAmount.value.toDoubleOrNull() ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            addToGoal(goal.id, amount, "Saved: ${goal.title}")
            val newAmount = goal.currentAmount + amount
            val msg = if (newAmount >= goal.targetAmount) "🎉 Goal completed!" else "Added to goal!"
            _uiState.update { it.copy(showAddFundsDialog = null, snackbarMessage = msg) }
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            deleteGoal.invoke(id)
            _uiState.update { it.copy(snackbarMessage = "Goal deleted") }
        }
    }

    fun clearSnackbar() = _uiState.update { it.copy(snackbarMessage = null) }
}
