package com.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.financeapp.ui.navigation.FinanceNavDisplay
import com.example.financeapp.ui.navigation.Tab
import com.example.financeapp.ui.navigation.rememberTabBackStacks
import com.financeapp.ui.theme.FinanceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinanceAppTheme {
                FinanceApp()
            }
        }
    }
}

@Composable
private fun FinanceApp() {

    // ── Navigation 3 state ────────────────────────────────────────────────────
    // Per-tab back stacks, each starting at its own root NavKey.
    // rememberNavBackStack uses SavedStateRegistry internally so state survives
    // both config changes and process death (keys are @Serializable).
    val tabBackStacks = rememberTabBackStacks()

    // The selected tab is kept in rememberSaveable so it survives config changes.
    // This is a plain enum — no NavController, no backstack entry, no route string.
    var activeTab by rememberSaveable { mutableStateOf(Tab.HOME) }

    // Derive whether to show the bottom bar:
    // hide it when a non-root entry is showing (e.g. AddEditTransaction overlay)
    val showBottomBar = tabBackStacks.forTab(activeTab).size <= 1

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                FinanceBottomBar(
                    activeTab = activeTab,
                    onTabSelect = { selectedTab ->
                        if (selectedTab == activeTab) {
                            // Tap the same tab again → pop back to root (clear sub-navigation)
                            val stack = tabBackStacks.forTab(selectedTab)
                            if (stack.size > 1) {
                                // Remove all entries above the root
                                repeat(stack.size - 1) { stack.removeLastOrNull() }
                            }
                        } else {
                            activeTab = selectedTab
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        // ── NavDisplay ────────────────────────────────────────────────────────
        // Single NavDisplay driven by the active tab's back stack.
        // Swapping activeTab swaps the back stack reference — NavDisplay
        // re-renders to show that tab's current top entry with correct state.
        FinanceNavDisplay(
            activeTab = activeTab,
            tabBackStacks = tabBackStacks,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom navigation bar
// ─────────────────────────────────────────────────────────────────────────────

private data class BottomNavItem(
    val tab: Tab,
    val icon: ImageVector,
    val iconSelected: ImageVector = icon,
)

private val bottomNavItems = listOf(
    BottomNavItem(Tab.HOME, Icons.Rounded.Home, Icons.Rounded.Home),
    BottomNavItem(Tab.TRANSACTIONS, Icons.Rounded.Receipt, Icons.Rounded.Receipt),
    BottomNavItem(Tab.GOALS, Icons.Rounded.Flag, Icons.Rounded.Flag),
    BottomNavItem(Tab.INSIGHTS, Icons.Rounded.Insights, Icons.Rounded.Insights),
)

@Composable
private fun FinanceBottomBar(
    activeTab: Tab,
    onTabSelect: (Tab) -> Unit,
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = activeTab == item.tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelect(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.iconSelected else item.icon,
                        contentDescription = item.tab.label,
                    )
                },
                label = { Text(item.tab.label) },
                alwaysShowLabel = false,
            )
        }
    }
}
