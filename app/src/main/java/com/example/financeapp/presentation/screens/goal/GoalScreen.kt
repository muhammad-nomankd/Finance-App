package com.example.financeapp.presentation.screens.goal

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.core.domain.model.Goal
import com.example.financeapp.core.domain.model.GoalFormState
import com.example.financeapp.core.domain.model.GoalType
import com.example.financeapp.core.domain.model.progressFraction
import com.example.financeapp.presentation.components.CircularProgressRing
import com.example.financeapp.presentation.components.EmptyState
import com.example.financeapp.presentation.components.SectionHeader
import java.time.format.DateTimeFormatter

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val addFundsAmount by viewModel.addFundsAmount.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals & Challenges", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = viewModel::showAddGoalDialog) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add goal")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddGoalDialog,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New goal")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp),
            ) {
                // ── Active Goals ──
                if (uiState.activeGoals.isEmpty()) {
                    item {
                        EmptyState(
                            emoji = "🎯",
                            title = "No active goals",
                            subtitle = "Create a savings goal, no-spend challenge, or budget tracker",
                            actionLabel = "Create Goal",
                            onAction = { viewModel.showAddGoalDialog() },
                            modifier = Modifier.padding(32.dp),
                        )
                    }
                } else {
                    item {
                        SectionHeader(
                            title = "Active Goals",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                    items(uiState.activeGoals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onAddFunds = { viewModel.showAddFundsDialog(goal) },
                            onDelete = { viewModel.deleteGoal(goal.id) },
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .animateItem(),
                        )
                    }
                }

                // ── Completed Goals ──
                if (uiState.completedGoals.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Completed",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        )
                    }
                    items(uiState.completedGoals, key = { "done_${it.id}" }) { goal ->
                        CompletedGoalCard(
                            goal = goal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }

    // ── Add Goal Dialog ──
    if (uiState.showAddDialog) {
        AddGoalDialog(
            formState = formState,
            onTitleChange = viewModel::onFormTitleChange,
            onAmountChange = viewModel::onFormAmountChange,
            onTypeChange = viewModel::onFormTypeChange,
            onSave = viewModel::saveGoal,
            onDismiss = viewModel::dismissAddGoalDialog,
        )
    }

    // ── Add Funds Dialog ──
    uiState.showAddFundsDialog?.let { goal ->
        AddFundsDialog(
            goal = goal,
            amount = addFundsAmount,
            onAmountChange = viewModel::onAddFundsAmountChange,
            onConfirm = viewModel::addFundsToGoal,
            onDismiss = viewModel::dismissAddFundsDialog,
        )
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onAddFunds: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var triggered by rememberSaveable { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (triggered) goal.progressFraction else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic)
    )
    LaunchedEffect(goal) {triggered = true }
    val color = when (goal.type) {
        GoalType.SAVINGS_GOAL -> MaterialTheme.colorScheme.primary
        GoalType.NO_SPEND_CHALLENGE -> MaterialTheme.colorScheme.tertiary
        GoalType.BUDGET_LIMIT -> MaterialTheme.colorScheme.secondary
    }
    val typeEmoji = when (goal.type) {
        GoalType.SAVINGS_GOAL -> "🏦"
        GoalType.NO_SPEND_CHALLENGE -> "🔥"
        GoalType.BUDGET_LIMIT -> "📊"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(typeEmoji, fontSize = 24.sp)
                    Column {
                        Text(
                            goal.title, style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            goal.type.label, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                // Streak badge (for no-spend challenge)
                if (goal.type == GoalType.NO_SPEND_CHALLENGE && goal.streak > 0) {
                    StreakBadge(streak = goal.streak)
                } else {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Rounded.DeleteOutline, contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressRing(
                    progress = animatedProgress,
                    size = 72.dp,
                    strokeWidth = 7.dp,
                    progressColor = color,
                ) {
                    Text(
                        "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            formatCurrency(goal.currentAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color,
                        )
                        Text(
                            "of ${formatCurrency(goal.targetAmount)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { goal.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = color,
                        trackColor = color.copy(alpha = 0.15f),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val remaining = goal.targetAmount - goal.currentAmount
                    Text(
                        "${formatCurrency(remaining)} remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // Deadline
                    goal.deadline?.let { deadline ->
                        Text(
                            "Due ${deadline.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Add funds button
            OutlinedButton(
                onClick = onAddFunds,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Funds")
            }
        }
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text("🔥", fontSize = 14.sp)
        Text(
            "$streak day streak",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CompletedGoalCard(goal: Goal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(if (goal.isCompleted) "✅" else "⏰", fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    goal.title, style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    if (goal.isCompleted) "Completed! ${formatCurrency(goal.targetAmount)} saved"
                    else "Expired — ${formatCurrency(goal.currentAmount)} of ${formatCurrency(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AddGoalDialog(
    formState: GoalFormState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (GoalType) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(8.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "New Goal", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Goal type selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Type", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    GoalType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (formState.type == type)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .clickable { onTypeChange(type) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = formState.type == type,
                                onClick = { onTypeChange(type) },
                            )
                            Column {
                                Text(
                                    type.label, style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    type.description, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = formState.title,
                    onValueChange = onTitleChange,
                    label = { Text("Goal name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = formState.titleError != null,
                    supportingText = formState.titleError?.let { { Text(it) } },
                    shape = RoundedCornerShape(12.dp),
                )

                OutlinedTextField(
                    value = formState.targetAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Target amount") },
                    prefix = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = formState.amountError != null,
                    supportingText = formState.amountError?.let { { Text(it) } },
                    shape = RoundedCornerShape(12.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFundsDialog(
    goal: Goal,
    amount: String,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text("💰", fontSize = 32.sp) },
        title = { Text("Add to \"${goal.title}\"") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) onAmountChange(it) },
                label = { Text("Amount") },
                prefix = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
            )
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = amount.toDoubleOrNull() != null) {
                Text("Add Funds")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

private fun formatCurrency(amount: Double): String = "\$${"%,.2f".format(amount)}"
