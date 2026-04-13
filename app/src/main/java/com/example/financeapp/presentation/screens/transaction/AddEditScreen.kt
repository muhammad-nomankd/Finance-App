package com.example.financeapp.presentation.screens.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.core.domain.model.Category
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.core.domain.model.expenseCategories
import com.example.financeapp.core.domain.model.incomeCategories
import com.financeapp.ui.theme.expenseColor
import com.financeapp.ui.theme.incomeColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddEditTransactionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    BackHandler {
        viewModel.onSavedConsumed()
        onBack()
    }
    // Load existing transaction if editing
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId)
        } else {
            viewModel.onSavedConsumed()
        }
    }

    // Navigate back on save

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.onSavedConsumed()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Transaction" else "New Transaction",
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                        viewModel.onSavedConsumed()
                    }) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.save()
                            viewModel.onSavedConsumed()
                        },
                        enabled = !uiState.isLoading,
                    ) {
                        Text("Save", fontWeight = FontWeight.SemiBold)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // ── Type Toggle (Income / Expense) ──
            TypeToggle(
                selected = uiState.type,
                onSelect = viewModel::onTypeChange,
            )

            // ── Amount Input ──
            AmountInput(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                currency = "$",
                type = uiState.type,
                error = uiState.amountError,
            )

            // ── Title ──
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                placeholder = { Text("e.g. Grocery shopping") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.titleError != null,
                supportingText = uiState.titleError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp),
            )

            // ── Category Picker ──
            CategoryPicker(
                selectedCategory = uiState.category,
                type = uiState.type,
                onCategorySelect = viewModel::onCategoryChange,
            )

            // ── Note (optional) ──
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note (optional)") },
                placeholder = { Text("Add a note...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
            )
// ── Date Picker ──
            DatePickerField(
                selectedDate = uiState.date!!,
                onDateSelected = viewModel::onDateChange,
            )
            // ── Save Button ──
            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (uiState.isEditMode) "Update Transaction" else "Save Transaction",
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Error message
            uiState.saveError?.let {
                Text(
                    it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TypeToggle(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit,
) {
    val incomeColor = MaterialTheme.incomeColor
    val expenseColor = MaterialTheme.expenseColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        TransactionType.entries.forEach { type ->
            val isSelected = selected == type
            val color = if (type == TransactionType.INCOME) incomeColor else expenseColor
            val label = if (type == TransactionType.INCOME) "💰 Income" else "💸 Expense"

            Surface(
                onClick = { onSelect(type) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent,
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AmountInput(
    value: String,
    onValueChange: (String) -> Unit,
    currency: String,
    type: TransactionType,
    error: String?,
) {
    val color =
        if (type == TransactionType.INCOME) MaterialTheme.incomeColor else MaterialTheme.expenseColor

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "Amount", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = value,
            onValueChange = { newVal ->
                // Only allow valid decimal input
                if (newVal.isEmpty() || newVal.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                    onValueChange(newVal)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            prefix = {
                Text(
                    currency,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color,
                )
            },
            textStyle = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = color,
            ),
            placeholder = {
                Text(
                    "0.00", style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            singleLine = true,
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = color.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun CategoryPicker(
    selectedCategory: Category,
    type: TransactionType,
    onCategorySelect: (Category) -> Unit,
) {
    val categories = if (type == TransactionType.INCOME) incomeCategories else expenseCategories

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Category", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories) { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category) },
                    label = { Text("${category.emoji} ${category.label}") },
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }
    }


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(java.time.ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli(),
        // Allow selecting any past or present date
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        },
    )

    // Display field
    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d yyyy")),
        onValueChange = {},
        readOnly = true,
        label = { Text("Date") },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = "Pick date")
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showPicker = true },
        shape = RoundedCornerShape(12.dp),
    )

    // Dialog
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val picked = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(picked)
                    }
                    showPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}