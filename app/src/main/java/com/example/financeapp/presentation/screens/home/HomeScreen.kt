package com.example.financeapp.presentation.screens.home
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.presentation.components.AnimatedBalanceText
import com.example.financeapp.presentation.components.CircularProgressRing
import com.example.financeapp.presentation.components.EmptyState
import com.example.financeapp.presentation.components.SectionHeader
import com.example.financeapp.presentation.components.ShimmerBox
import com.example.financeapp.presentation.components.SummaryChip
import com.example.financeapp.presentation.components.TransactionListItem

@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit,
    onSeeAllTransactions: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTransaction,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { paddingValues ->
        when {
            uiState.isLoading -> HomeLoadingState(paddingValues)
            uiState.error != null -> HomeErrorState(uiState.error!!, paddingValues)
            else -> HomeContent(
                uiState = uiState,
                onSeeAllTransactions = onSeeAllTransactions,
                onAddTransaction = onAddTransaction,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onSeeAllTransactions: () -> Unit,
    onAddTransaction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Hero Balance Card ──
        item {
            BalanceHeroCard(
                balance = uiState.summary?.balance ?: 0.0,
                income = uiState.summary?.totalIncome ?: 0.0,
                expense = uiState.summary?.totalExpense ?: 0.0,
                savingsRate = uiState.summary?.savingsRate?.toFloat() ?:0f,
                currency = uiState.currency,
            )
        }

        // ── Savings Progress ──
        item {
            SavingsRateSection(
                savingsRate = uiState.summary?.savingsRate?.toFloat() ?: 0f,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // ── Recent Transactions ──
        item {
            SectionHeader(
                title = "Recent Transactions",
                actionLabel = "See all",
                onAction = onSeeAllTransactions,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        if (uiState.recentTransactions.isEmpty()) {
            item {
                EmptyState(
                    emoji = "💸",
                    title = "No transactions yet",
                    subtitle = "Tap the + button to record your first transaction",
                    actionLabel = "Add Transaction",
                    onAction = onAddTransaction,
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            items(
                items = uiState.recentTransactions,
                key = { it.id },
            ) { transaction ->
                TransactionListItem(
                    transaction = transaction,
                    currency = uiState.currency,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 3.dp)
                        .animateItem(),
                )
            }
        }
    }
}

@Composable
private fun BalanceHeroCard(
    balance: Double,
    income: Double,
    expense: Double,
    savingsRate: Float,
    currency: String,
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(gradient)
            .padding(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Greeting row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        "Good morning 👋",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                    Text(
                        "Your balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                // Notification bell
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Rounded.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            // Balance
            AnimatedBalanceText(
                amount = balance,
                currency = currency,
            )

            // Income / Expense row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                SummaryChip(
                    label = "Income",
                    amount = income,
                    isIncome = true,
                    currency = currency,
                    modifier = Modifier.weight(1f),
                )
                SummaryChip(
                    label = "Expenses",
                    amount = expense,
                    isIncome = false,
                    currency = currency,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SavingsRateSection(savingsRate: Float, modifier: Modifier = Modifier) {
    var triggered by rememberSaveable { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (triggered) savingsRate else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "savings_rate_ring",
    )
    LaunchedEffect(savingsRate) { triggered = true }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressRing(
                progress = animatedProgress,
                size = 56.dp,
                strokeWidth = 6.dp,
                progressColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column {
                Text("Savings Rate", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(
                    when {
                        savingsRate >= 0.3f -> "Great! You're saving well this month 🎉"
                        savingsRate >= 0.1f -> "Decent — try to push above 30% 💪"
                        else -> "Low savings rate — review your expenses"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
@Composable
private fun HomeLoadingState(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier.padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(240.dp), shape = RoundedCornerShape(24.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
        repeat(4) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(64.dp))
        }
    }
}

@Composable
private fun HomeErrorState(error: String, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            emoji = "⚠️",
            title = "Something went wrong",
            subtitle = error,
        )
    }
}