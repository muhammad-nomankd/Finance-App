package com.example.financeapp.presentation.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

// ─────────────────────────────────────────────
// Animated Balance Counter
// ─────────────────────────────────────────────

@Composable
fun AnimatedBalanceText(
    amount: Double,
    currency: String = "$",
    modifier: Modifier = Modifier,
) {
    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "balance_animation"
    )
    val formatted = formatCurrency(animatedAmount.toDouble(), currency)
    Text(
        text = formatted,
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
    )
}
