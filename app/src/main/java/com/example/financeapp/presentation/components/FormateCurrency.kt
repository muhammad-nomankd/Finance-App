package com.example.financeapp.presentation.components

import kotlin.math.absoluteValue

// ─────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────

fun formatCurrency(amount: Double, currency: String = "$"): String {
    return "$currency${"%,.2f".format(amount.absoluteValue)}"
}