package com.example.financeapp.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.financeapp.presentation.screens.goal.GoalsScreen
import com.example.financeapp.presentation.screens.home.HomeScreen
import com.example.financeapp.presentation.screens.insight.InsightsScreen
import com.example.financeapp.presentation.screens.transaction.AddEditTransactionScreen
import com.example.financeapp.presentation.screens.transaction.TransactionsScreen
import kotlinx.serialization.Serializable

// ─────────────────────────────────────────────────────────────────────────────
// Nav Keys
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data object HomeKey         : NavKey
@Serializable
data object TransactionsKey : NavKey
@Serializable
data object GoalsKey        : NavKey
@Serializable
data object InsightsKey     : NavKey

@Serializable
data class AddEditTransactionKey(val transactionId: Long? = null) : NavKey


// Tab model
enum class Tab(val rootKey: NavKey, val label: String, val emoji: String) {
    HOME(HomeKey, "Home", "🏠"),
    TRANSACTIONS(TransactionsKey, "Transactions", "📋"),
    GOALS(GoalsKey, "Goals", "🎯"),
    INSIGHTS(InsightsKey, "Insights", "📊"),
}



// Per-tab back stacks
data class TabBackStacks(
    val home: NavBackStack<NavKey>,
    val transactions: NavBackStack<NavKey>,
    val goals:        NavBackStack<NavKey>,
    val insights:     NavBackStack<NavKey>,
) {
    fun forTab(tab: Tab): NavBackStack<NavKey> = when (tab) {
        Tab.HOME         -> home
        Tab.TRANSACTIONS -> transactions
        Tab.GOALS        -> goals
        Tab.INSIGHTS     -> insights
    }

    fun canGoBack(tab: Tab) = forTab(tab).size > 1
}


@Composable
fun rememberTabBackStacks(): TabBackStacks = TabBackStacks(
    home         = rememberNavBackStack(HomeKey),
    transactions = rememberNavBackStack(TransactionsKey),
    goals        = rememberNavBackStack(GoalsKey),
    insights     = rememberNavBackStack(InsightsKey),
)

// ─────────────────────────────────────────────────────────────────────────────
// FinanceNavDisplay
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FinanceNavDisplay(
    activeTab: Tab,
    tabBackStacks: TabBackStacks,
) {
    val activeBackStack = tabBackStacks.forTab(activeTab)

    NavDisplay(
        backStack = activeBackStack,
        onBack = {
            if (activeBackStack.size > 1) activeBackStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator()

        ),
        transitionSpec = {
            (slideInHorizontally { it } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it } + fadeOut())
        },
        popTransitionSpec = {
            (slideInHorizontally { -it } + fadeIn()) togetherWith
                    (slideOutHorizontally { it } + fadeOut())
        },
        entryProvider = entryProvider {

            entry<HomeKey> {
                HomeScreen(
                    onAddTransaction = {
                        tabBackStacks.home.add(AddEditTransactionKey())
                    },
                    onSeeAllTransactions = {
                        tabBackStacks.home.add(TransactionsKey)
                    },
                )
            }

            entry<TransactionsKey> {
                TransactionsScreen(
                    onAddTransaction = {
                        activeBackStack.add(AddEditTransactionKey())
                    },
                    onEditTransaction = { transactionId ->
                        activeBackStack.add(AddEditTransactionKey(transactionId = transactionId))
                    },
                    onBack = {
                        if (activeBackStack.size > 1) activeBackStack.removeLastOrNull()
                    },
                )
            }

            entry<AddEditTransactionKey> { key ->
                AddEditTransactionScreen(
                    transactionId = key.transactionId,
                    onSaved = { activeBackStack.removeLastOrNull() },
                    onBack  = { activeBackStack.removeLastOrNull() },
                )
            }

            entry<GoalsKey> {
                GoalsScreen()
            }

            entry<InsightsKey> {
                InsightsScreen()
            }
        },
    )
}
