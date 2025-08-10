package com.android.expensetracker.features.expense_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

data class DailyTotal(
    val date: String, // Formatted date string
    val dayLabel: String, // Short label for chart e.g., "Mon"
    val amount: Double
)

data class CategoryTotal(
    val categoryName: String,
    val amount: Double
)

data class ReportScreenState(
    val reportTitle: String = "Last 7 Days Report",
    val dailyTotals: List<DailyTotal> = emptyList(),
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val overallTotal: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class ExpenseReportViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(ReportScreenState(isLoading = true))
    val screenState: StateFlow<ReportScreenState> = _screenState.asStateFlow()

    private val fullDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()) // e.g., Mon, 23 Oct 2023
    private val shortDayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // e.g., Mon
    private val categories = listOf("Groceries", "Transport", "Utilities", "Dining Out", "Entertainment", "Shopping", "Health")

    init {
        generateMockReport()
    }

    fun generateMockReport() {
        viewModelScope.launch {
            _screenState.value = _screenState.value.copy(isLoading = true)
            delay(300) // Simulate network delay

            val mockDailyTotals = mutableListOf<DailyTotal>()
            val calendar = Calendar.getInstance()
            var currentOverallTotal = 0.0

            for (i in 6 downTo 0) { // Last 7 days, ending with today
                val dayCalendar = calendar.clone() as Calendar
                dayCalendar.add(Calendar.DAY_OF_YEAR, -i)
                val dailyAmount = Random.nextDouble(50.0, 350.0)
                mockDailyTotals.add(
                    DailyTotal(
                        date = fullDateFormat.format(dayCalendar.time),
                        dayLabel = shortDayFormat.format(dayCalendar.time),
                        amount = dailyAmount
                    )
                )
                currentOverallTotal += dailyAmount
            }

            val mockCategoryTotals = categories.shuffled().take(Random.nextInt(3, categories.size.coerceAtLeast(4)))
                .map { category ->
                    CategoryTotal(
                        categoryName = category,
                        // Ensure category totals don't ridiculously exceed daily totals sum
                        amount = Random.nextDouble(currentOverallTotal * 0.1, currentOverallTotal * 0.4).coerceAtMost(currentOverallTotal * 0.5)
                    )
                }.sortedByDescending { it.amount }


            _screenState.value = ReportScreenState(
                dailyTotals = mockDailyTotals.reversed(), // Show most recent day first in lists, but chart will use original order.
                categoryTotals = mockCategoryTotals,
                overallTotal = currentOverallTotal,
                isLoading = false
            )
        }
    }

    fun simulateExportToPdf(): String {
        return "Report exported to PDF (simulated)"
    }

    fun simulateExportToCsv(): String {
        return "Report exported to CSV (simulated)"
    }

    fun getShareableReportText(): String {
        val state = _screenState.value
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val reportBuilder = StringBuilder()
        reportBuilder.appendLine(state.reportTitle)
        reportBuilder.appendLine("Overall Total: ${currencyFormat.format(state.overallTotal)}")
        reportBuilder.appendLine("\n--- Daily Totals ---")
        // Use original order for share text for chronological sense
        state.dailyTotals.reversed().forEach { // Reversed back to chronological for text
            reportBuilder.appendLine("${it.date}: ${currencyFormat.format(it.amount)}")
        }
        reportBuilder.appendLine("\n--- Category Totals (Last 7 Days) ---")
        state.categoryTotals.forEach {
            reportBuilder.appendLine("${it.categoryName}: ${currencyFormat.format(it.amount)}")
        }
        return reportBuilder.toString()
    }
}
