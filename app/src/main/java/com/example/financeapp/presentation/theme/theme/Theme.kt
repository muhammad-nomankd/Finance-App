package com.financeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
// Color Palette
// ─────────────────────────────────────────────

object FinanceColors {
    val Primary = Color(0xFF1B5E20)          // Deep forest green
    val PrimaryVariant = Color(0xFF2E7D32)
    val PrimaryLight = Color(0xFF4CAF50)
    val Secondary = Color(0xFF00695C)        // Teal-green
    val Tertiary = Color(0xFFF57F17)         // Amber accent

    val IncomeGreen = Color(0xFF2E7D32)
    val ExpenseRed = Color(0xFFC62828)
    val SavingsBlue = Color(0xFF1565C0)
    val NeutralGray = Color(0xFF607D8B)

    // Surface colors
    val SurfaceLight = Color(0xFFF8FBF8)
    val SurfaceDark = Color(0xFF0D1B0E)
    val CardLight = Color(0xFFFFFFFF)
    val CardDark = Color(0xFF1A2B1B)

    // Chart colors
    val ChartColors = listOf(
        Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800),
        Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFF00BCD4),
        Color(0xFFFF5722), Color(0xFF795548),
    )
}

// ─────────────────────────────────────────────
// Light/Dark Color Schemes
// ─────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary = FinanceColors.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = FinanceColors.Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = FinanceColors.Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF9C4),
    onTertiaryContainer = Color(0xFFF57F17),
    background = FinanceColors.SurfaceLight,
    onBackground = Color(0xFF1A1C1A),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFDEE5DA),
    onSurfaceVariant = Color(0xFF424940),
    error = FinanceColors.ExpenseRed,
    outline = Color(0xFF72796F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003909),
    primaryContainer = Color(0xFF00531A),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF004F47),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF3E2700),
    background = FinanceColors.SurfaceDark,
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF131F14),
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C9BD),
    error = Color(0xFFEF9A9A),
    outline = Color(0xFF8C9388),
)

// ─────────────────────────────────────────────
// Typography
// ─────────────────────────────────────────────

val FinanceTypography = Typography(
    // Large displays — balance, big numbers
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-1).sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp,
    ),
    // Section headers
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    // Card titles
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    // Body text
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Captions, labels
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ─────────────────────────────────────────────
// Theme Composable
// ─────────────────────────────────────────────

@Composable
fun FinanceAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FinanceTypography,
        content = content,
    )
}

// ─────────────────────────────────────────────
// Convenience extensions
// ─────────────────────────────────────────────

val MaterialTheme.incomeColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFF81C784) else FinanceColors.IncomeGreen

val MaterialTheme.expenseColor: Color
    @Composable get() = if (isSystemInDarkTheme()) Color(0xFFEF9A9A) else FinanceColors.ExpenseRed