package com.example.financeapp.presentation.theme.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ─── Palette ────────────────────────────────────────────────────────────────
val Mint = Color(0xFF00C9A7)
val MintLight = Color(0xFF80E4D2)
val MintDark = Color(0xFF008F75)
val DeepNavy = Color(0xFF0A0E1A)
val SurfaceNavy = Color(0xFF111827)
val CardNavy = Color(0xFF1A2235)
val TextPrimary = Color(0xFFF0F4FF)
val TextSecondary = Color(0xFF8899BB)
val ExpenseRed = Color(0xFFFF6B6B)
val IncomeGreen = Color(0xFF00C9A7)
val Gold = Color(0xFFFFD166)
val Purple = Color(0xFF9B72CF)

// Light palette
val LightBackground = Color(0xFFF5F7FF)
val LightSurface = Color(0xFFFFFFFF)
val LightCard = Color(0xFFF0F4FF)
val LightPrimary = Color(0xFF008F75)
val LightOnPrimary = Color(0xFFFFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = Mint,
    onPrimary = DeepNavy,
    primaryContainer = MintDark,
    onPrimaryContainer = MintLight,
    secondary = Gold,
    onSecondary = DeepNavy,
    tertiary = Purple,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    error = ExpenseRed,
    outline = Color(0xFF2A3850)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = MintLight,
    onPrimaryContainer = MintDark,
    secondary = Color(0xFFD4A017),
    onSecondary = LightOnPrimary,
    tertiary = Purple,
    background = LightBackground,
    onBackground = Color(0xFF0A0E1A),
    surface = LightSurface,
    onSurface = Color(0xFF0A0E1A),
    surfaceVariant = LightCard,
    onSurfaceVariant = Color(0xFF3D5280),
    error = ExpenseRed,
    outline = Color(0xFFD0D8F0)
)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)