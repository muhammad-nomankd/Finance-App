

https://github.com/user-attachments/assets/cfdae7ac-5a43-491e-a7eb-c0ebe477aa9a

# FinanceApp — Personal Finance Companion

## Overview
One paragraph explaining what the app does and the key product decisions you made.

## Architecture
Clean Architecture (Data / Domain / Presentation), MVVM, Navigation 3

## Tech Stack
Kotlin, Jetpack Compose, Room, Hilt, Navigation 3, Vico charts, DataStore

## Features
- Home dashboard with balance, income/expense summary, savings rate ring
- Transaction tracking with swipe-to-delete, search, date grouping
- Goals (Savings Goal, No-Spend Challenge with streak, Budget Limit)
- Insights with 7-day area chart and monthly bar chart (Vico)
- Dark mode

## Design Decisions & Assumptions
- No real backend — Room for local persistence
- Currency is set globally in Settings
- Streak resets if no check-in the previous day

## Setup Instructions
1. Clone the repo
2. Open in Android Studio Hedgehog or newer
3. Run on device or emulator (minSdk 26)
