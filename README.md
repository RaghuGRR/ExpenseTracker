# ExpenseTracker Android App

## App Overview

ExpenseTracker is an Android application designed to help users efficiently log, manage, and track
their daily expenses. It offers features for quick expense entry, categorization, date-wise expense
viewing, and basic reporting.

## AI Usage Summary

This project's development was significantly assisted by Gemini, a large language model from Google,
integrated within Android Studio. Gemini helped with generating code for new features (like theme
switching and dependency imports and report Screen Chart models), refactoring existing code to debugging UI and theming issues, and generating this README file. It provided
suggestions for modern Android development practices, including ViewModel interactions, UI updates,
and theme management.

## Key Prompt Logs

Here are some representative prompts used during the development process with the AI assistant:

* `Create a Kotlin data class for a Room database entity named `Expense`. It must include:

- `id`: Primary key, auto-generated Long.
- `title`: String.
- `amount`: Double.
- `category`: String.
- `date`: Long (to store timestamp).
- `notes`: Optional String.
- `receiptImagePath`: Optional String.`

* Generate the `build.gradle.kts` (app-level) dependencies for an Android Jetpack Compose project. I
  need libraries for:

- Jetpack Compose (UI, Material3, Tooling)
- Navigation Compose
- ViewModel with StateFlow (Lifecycle)
- Room for local database (runtime, KTX, compiler)
- A charting library, like patrykandpatrick for Compose

* `Generate the App themeing to setup the Green family Color Scheme ` (Prompt to debug theme color
  application)
* `Generate the Theme Switching`
* `Generate Mock report for last 7 days:`
  Daily totals
  Category-wise totals
  Bar or line chart (mocked)
  Export (optional):
  Simulate PDF/CSV export
  Trigger Share intent (optional)
* `Generate Readme.md` (This request)

## Checklist of Features Implemented

**I. Core Expense Management:**

-   [x] **Expense Entry:**
    - Dedicated screen to input expense details (title, amount, category, date, notes).
    - Option to attach a receipt image using an image picker.
-   [x] **Expense Display & Listing:**
    - Main screen listing expenses, filterable by a selected date.
    - Dynamic grouping of expenses by category.
    - Display of overall total amount and item count for the selected period.
-   [x] **Date Navigation:** Calendar-based date selection to view expenses for specific days.

**II. Reporting & Analysis:**

-   [x] **Expense Report Screen:**
    - Displays overall total expenses for a defined period (e.g., last 7 days).
    - Visualizes daily spending trends using a bar chart (Vico library).
    - Lists daily total expenses.
    - Lists total expenses per category over the period.
    - Provides a "Share Report" feature for sharing report text.
    - Includes (simulated) "Export to PDF" and "Export to CSV" functionality.

**III. User Interface & Experience (UI/UX):**

-   [x] **Intuitive Navigation:**
    - Bottom navigation bar for primary app sections (e.g., Expense List, Reports).
    - Floating Action Button (FAB) for quick access to add new expenses.
-   [x] **Modern UI with Jetpack Compose:** Entire UI built using Jetpack Compose for a declarative
    and modern user experience.

**IV. Theming & Customization:**

-   [x] **Dynamic Theme System:**
    - User-selectable themes: Light, Dark, and System Default.
    - Theme preference saved and persisted across app sessions using SharedPreferences.
    - Interactive `ThemeSwitcherIcon` in the `TopAppBar` for on-the-fly theme changes.
-   [x] **Custom Green Color Scheme:**
    - App-wide consistent green theme (light and dark variants) defined in `Color.kt` and
      `Theme.kt`.
    - Theme colors correctly applied to all UI components, including `TopAppBar`, `BottomAppBar`,
      and other Material components.

**V. Key Technical Details & Optimizations (AI Assisted):**

-   [x] **State Management:**
    - Effective use of `ViewModel`s to separate UI logic and manage screen state.
    - Utilization of Kotlin `StateFlow` and `SharedFlow` for reactive UI updates and one-time
      events (e.g., signaling save success).
-   [x] **Asynchronous Operations:** Kotlin Coroutines employed for background tasks, such as saving
    expense data to the repository.
-   [x] **Dependency Injection:** (Assumed Hilt is set up and used for ViewModels and Repositories).

**VI. Project Documentation:**

-   [x] **Comprehensive README:** This `README.md` file, detailing project overview, AI
    collaboration, key development prompts, and this feature checklist, generated and refined with
    AI assistance.

