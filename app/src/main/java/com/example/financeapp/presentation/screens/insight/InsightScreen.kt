package com.example.financeapp.presentation.screens.insight

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeapp.core.domain.model.CategorySpend
import com.example.financeapp.core.domain.model.DailySpend
import com.example.financeapp.core.domain.model.SpendingInsight
import com.example.financeapp.presentation.components.EmptyState
import com.example.financeapp.presentation.components.SectionHeader
import com.example.financeapp.presentation.components.ShimmerBox
import com.financeapp.ui.theme.FinanceColors
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = viewModel::load) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                    }
                },
            )
        },
    ) { padding ->
        when (val state = uiState) {
            InsightsUiState.Loading -> InsightsLoadingState(padding)
            is InsightsUiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) { EmptyState("⚠️", "Could not load insights", state.message) }
            is InsightsUiState.Success -> InsightsContent(state.insight, padding)
        }
    }
}

@Composable
private fun InsightsContent(insight: SpendingInsight, padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        SpendingVelocityCard(velocity = insight.spendingVelocity)
        MonthComparisonCard(thisMonth = insight.thisMonthTotal, lastMonth = insight.lastMonthTotal)
        SectionHeader(title = "This Month vs Last Month")
        AnimatedBarChart(thisMonth = insight.thisMonthTotal, lastMonth = insight.lastMonthTotal)
        insight.topCategory?.let { cat ->
            TopCategoryCard(category = cat.label, emoji = cat.emoji, amount = insight.topCategoryAmount)
        }
        if (insight.categoryBreakdown.isNotEmpty()) {
            SectionHeader(title = "Spending by Category")
            AnimatedDonutChart(categories = insight.categoryBreakdown)
            Spacer(modifier = Modifier.height(8.dp))
            CategoryBreakdownList(categories = insight.categoryBreakdown)
        } else {
            EmptyState("📊", "No spending data yet", "Add some expenses to see your spending breakdown")
        }
        SectionHeader(title = "Spending — Last 7 Days")
        if (insight.dailySpending.isNotEmpty()) {
            AnimatedLineChart(dailySpending = insight.dailySpending)
        } else {
            EmptyState("📉", "No data yet", "Add expenses to see your daily trend")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Animated Line Chart ──────────────────────────────────────────────────────

@Composable
private fun AnimatedLineChart(dailySpending: List<DailySpend>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val dayFormatter = DateTimeFormatter.ofPattern("EEE")

    var triggered by rememberSaveable { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "line_chart_progress",
    )
    LaunchedEffect(dailySpending) { triggered = true }

    val maxAmount = dailySpending.maxOfOrNull { it.amount }?.takeIf { it > 0 } ?: 1.0

    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        val pts = dailySpending.mapIndexed { i, day ->
                            val x = if (dailySpending.size > 1)
                                i * w / (dailySpending.size - 1).toFloat() else w / 2f
                            val y = h - (day.amount / maxAmount).toFloat() * h * 0.85f
                            Offset(x, y)
                        }

                        // Animated slice of points
                        val animatedCount = (pts.size * progress).toInt().coerceAtLeast(
                            if (progress > 0f) 1 else 0
                        )
                        if (animatedCount < 1) return@drawBehind
                        val visiblePts = pts.take(animatedCount)

                        // Fill gradient under line
                        if (visiblePts.size > 1) {
                            val fillPath = Path().apply {
                                moveTo(visiblePts.first().x, h)
                                visiblePts.forEach { lineTo(it.x, it.y) }
                                lineTo(visiblePts.last().x, h)
                                close()
                            }
                            drawPath(
                                fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.3f),
                                        Color.Transparent,
                                    ),
                                    startY = 0f,
                                    endY = h,
                                ),
                            )

                            // Line
                            val linePath = Path().apply {
                                moveTo(visiblePts.first().x, visiblePts.first().y)
                                visiblePts.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(
                                linePath,
                                color = primaryColor,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                            )
                        }

                        // Dots
                        visiblePts.forEach { pt ->
                            drawCircle(color = surfaceColor, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = primaryColor, radius = 3.5.dp.toPx(), center = pt)
                        }
                    },
            )

            Spacer(modifier = Modifier.height(8.dp))

            // X-axis labels
            Row(modifier = Modifier.fillMaxWidth()) {
                dailySpending.forEach { day ->
                    Text(
                        day.date.format(dayFormatter),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

// ── Animated Bar Chart ───────────────────────────────────────────────────────

@Composable
private fun AnimatedBarChart(thisMonth: Double, lastMonth: Double) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    var triggered by rememberSaveable { mutableStateOf(false) }
    val thisProgress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutCubic),
        label = "this_month_bar",
    )
    val lastProgress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 900, delayMillis = 150, easing = EaseOutCubic),
        label = "last_month_bar",
    )
    LaunchedEffect(thisMonth, lastMonth) { triggered = true }

    val maxVal = maxOf(thisMonth, lastMonth).takeIf { it > 0 } ?: 1.0

    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                LegendDot(color = secondaryColor, label = "Last Month")
                LegendDot(color = primaryColor, label = "This Month")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .drawBehind {
                        val barWidth = size.width * 0.22f
                        val gap = size.width * 0.08f
                        val totalGroup = barWidth * 2 + gap
                        val groupX = (size.width - totalGroup) / 2f

                        // Last month bar
                        val lastH = (lastMonth / maxVal).toFloat() * size.height * lastProgress
                        drawRoundRect(
                            color = secondaryColor,
                            topLeft = Offset(groupX, size.height - lastH),
                            size = Size(barWidth, lastH),
                            cornerRadius = CornerRadius(8.dp.toPx()),
                        )

                        // This month bar
                        val thisH = (thisMonth / maxVal).toFloat() * size.height * thisProgress
                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset(groupX + barWidth + gap, size.height - thisH),
                            size = Size(barWidth, thisH),
                            cornerRadius = CornerRadius(8.dp.toPx()),
                        )
                    },
            ) {}

            // Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "\$${"%,.0f".format(lastMonth)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = secondaryColor,
                    )
                    Text(
                        "Last Month",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "\$${"%,.0f".format(thisMonth)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                    )
                    Text(
                        "This Month",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// ── Animated Donut Chart ─────────────────────────────────────────────────────

@Composable
private fun AnimatedDonutChart(categories: List<CategorySpend>) {
    val colors = FinanceColors.ChartColors

    var triggered by rememberSaveable { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "donut_progress",
    )
    LaunchedEffect(categories) { triggered = true }

    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Donut
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        val stroke = 28.dp.toPx()
                        val inset = stroke / 2f
                        val arcSize = Size(size.width - stroke, size.height - stroke)
                        var startAngle = -90f
                        categories.forEachIndexed { idx, spend ->
                            val sweep = (spend.percentage / 100f) * 360f * progress
                            drawArc(
                                color = colors[idx % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arcSize,
                                style = Stroke(width = stroke, cap = StrokeCap.Butt),
                            )
                            startAngle += sweep
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                val top = categories.firstOrNull()
                if (top != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(top.category.emoji, fontSize = 22.sp)
                        Text(
                            "${top.percentage.toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Mini legend
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.take(5).forEachIndexed { idx, spend ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(colors[idx % colors.size]),
                        )
                        Text(
                            "${spend.category.emoji} ${spend.category.label}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            "${spend.percentage.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ── Supporting Composables ───────────────────────────────────────────────────

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SpendingVelocityCard(velocity: Double) {
    val isFaster = velocity > 0
    val color = if (isFaster) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val label = if (isFaster)
        "Spending ${abs(velocity).toInt()}% faster than last month"
    else
        "Spending ${abs(velocity).toInt()}% slower than last month"
    val emoji = if (isFaster) "⚡" else "✅"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 32.sp)
            Column {
                Text(
                    "Spending Velocity",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(
                    if (isFaster) "Keep an eye on your budget!" else "Great pace — keep it up!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MonthComparisonCard(thisMonth: Double, lastMonth: Double) {
    val diff = thisMonth - lastMonth
    val pct = if (lastMonth > 0) (diff / lastMonth * 100) else 0.0
    val isMore = diff > 0

    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Monthly Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ComparisonColumn("This Month", thisMonth, MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
                ComparisonColumn("Last Month", lastMonth, MaterialTheme.colorScheme.secondary)
            }
            if (lastMonth > 0) {
                val badgeColor = if (isMore) MaterialTheme.colorScheme.errorContainer
                else MaterialTheme.colorScheme.primaryContainer
                val textColor = if (isMore) MaterialTheme.colorScheme.onErrorContainer
                else MaterialTheme.colorScheme.onPrimaryContainer
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        "${if (isMore) "+" else ""}${pct.toInt()}% vs last month",
                        style = MaterialTheme.typography.labelMedium,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonColumn(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            "\$${"%,.0f".format(amount)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
private fun TopCategoryCard(category: String, emoji: String, amount: Double) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 36.sp)
            Column {
                Text(
                    "Top Spending Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                )
                Text(
                    category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Text(
                    "\$${"%,.2f".format(amount)} this month",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownList(categories: List<CategorySpend>) {
    val colors = FinanceColors.ChartColors
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            categories.forEachIndexed { idx, spend ->
                val barColor = colors[idx % colors.size]

                var triggered by rememberSaveable { mutableStateOf(false) }
                val animatedProgress by animateFloatAsState(
                    targetValue = if (triggered) spend.percentage / 100f else 0f,
                    animationSpec = tween(
                        durationMillis = 800,
                        delayMillis = idx * 80,
                        easing = EaseOutCubic,
                    ),
                    label = "bar_$idx",
                )
                LaunchedEffect(spend) { triggered = true }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(spend.category.emoji, fontSize = 16.sp)
                            Text(spend.category.label, style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "${spend.percentage.toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                "\$${"%,.2f".format(spend.amount)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = barColor,
                        trackColor = barColor.copy(alpha = 0.15f),
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightsLoadingState(padding: PaddingValues) {
    Column(
        modifier = Modifier.padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(140.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(160.dp))
    }
}