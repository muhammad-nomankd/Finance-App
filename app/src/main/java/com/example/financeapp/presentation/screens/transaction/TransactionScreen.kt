package com.example.financeapp.presentation.screens.transaction
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.core.domain.model.Transaction
import com.example.financeapp.core.domain.model.TransactionType
import com.example.financeapp.presentation.components.EmptyState
import com.example.financeapp.presentation.components.ShimmerBox
import com.example.financeapp.presentation.components.TransactionListItem
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddTransaction) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add transaction")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !uiState.isLoading,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.padding(bottom = 64.dp)
            ) {
                FloatingActionButton(
                    onClick = onAddTransaction,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            // ── Search Bar ──
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQuery,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // ── Filter Chips ──
            FilterChipsRow(
                selectedType = uiState.filterType,
                onTypeSelected = viewModel::onFilterType,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            // ── Transaction List ──
            if (uiState.isLoading) {
                LoadingList()
            } else if (uiState.transactions.isEmpty()) {
                EmptyState(
                    emoji = "🔍",
                    title = "No transactions found",
                    subtitle = if (uiState.searchQuery.isNotBlank())
                        "Try a different search term"
                    else
                        "Add your first transaction using the + button",
                    modifier = Modifier.padding(32.dp),
                )
            } else {
                TransactionList(
                    transactions = uiState.transactions,
                    currency = uiState.currency,
                    onEdit = onEditTransaction,
                    onDelete = { id ->
                        viewModel.onDeleteTransaction(id)
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search transactions...") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

@Composable
private fun FilterChipsRow(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedType == null,
            onClick = { onTypeSelected(null) },
            label = { Text("All") },
            leadingIcon = if (selectedType == null) {
                { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
            } else null,
        )
        FilterChip(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onTypeSelected(if (selectedType == TransactionType.INCOME) null else TransactionType.INCOME) },
            label = { Text("Income") },
            leadingIcon = { Text("💰", modifier = Modifier.size(16.dp)) },
        )
        FilterChip(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = { onTypeSelected(if (selectedType == TransactionType.EXPENSE) null else TransactionType.EXPENSE) },
            label = { Text("Expenses") },
            leadingIcon = { Text("💸", modifier = Modifier.size(16.dp)) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    currency: String,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    // Group by date
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    val grouped = remember(transactions) {
        transactions.groupBy {
            it.date
        }
    }
    LazyColumn(
        contentPadding = PaddingValues(bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        grouped.forEach { (date, txList) ->
            stickyHeader(key = date.toString()) {
                DateGroupHeader(date = date)
            }
            items(txList, key = { it.id }) { transaction ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        when (value) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onDelete(transaction.id)
                                true
                            }
                            SwipeToDismissBoxValue.StartToEnd -> {
                                onEdit(transaction.id)
                                false
                            }
                            else -> false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 3.dp)
                        .animateItem(),
                    backgroundContent = {
                        SwipeDismissBackground(dismissState)
                    },
                ) {
                    TransactionListItem(
                        transaction = transaction,
                        currency = currency,
                        onClick = { onEdit(transaction.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeDismissBackground(state: SwipeToDismissBoxState) {
    val isDelete = state.dismissDirection == SwipeToDismissBoxValue.EndToStart
    val isEdit = state.dismissDirection == SwipeToDismissBoxValue.StartToEnd
    val color = when {
        isDelete -> MaterialTheme.colorScheme.errorContainer
        isEdit -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = if (isDelete) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        if (isDelete) {
            Icon(Icons.Rounded.Delete, contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onErrorContainer)
        } else if (isEdit) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun DateGroupHeader(date: java.time.LocalDate) {
    val today = java.time.LocalDate.now()
    val label = when (date) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d"))
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun LoadingList() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(6) { ShimmerBox(modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)) }
    }
}
