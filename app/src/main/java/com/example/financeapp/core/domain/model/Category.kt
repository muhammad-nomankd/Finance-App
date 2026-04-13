package com.example.financeapp.core.domain.model

enum class Category(val label: String, val emoji: String) {
    FOOD("Food & Drinks", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    HEALTH("Health", "💊"),
    ENTERTAINMENT("Entertainment", "🎮"),
    BILLS("Bills & Utilities", "⚡"),
    EDUCATION("Education", "📚"),
    TRAVEL("Travel", "✈️"),
    SAVINGS("Savings", "💰"),
    SALARY("Salary", "💼"),
    FREELANCE("Freelance", "🖥️"),
    INVESTMENT("Investment", "📈"),
    GIFT("Gift", "🎁"),
    OTHER("Other", "📦"),
    RENT("Rent & Housing", "🏠"),
    UTILITIES("Utilities", "💡"),

    PERSONAL("Personal Care", "💅"),
}

val expenseCategories = listOf(
   Category.FOOD, Category.TRANSPORT, Category.SHOPPING, Category.ENTERTAINMENT,
    Category.HEALTH, Category.UTILITIES, Category.RENT, Category.EDUCATION,
 Category.TRAVEL, Category.PERSONAL, Category.OTHER
)

val incomeCategories = listOf(
    Category.SALARY,Category.FREELANCE, Category.INVESTMENT, Category.GIFT, Category.OTHER
)
