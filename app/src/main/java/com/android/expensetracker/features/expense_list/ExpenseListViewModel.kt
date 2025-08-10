package com.android.expensetracker.features.expense_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.domain.model.Expense
import com.android.domain.usecase.ExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

enum class GroupingMode {
    NONE,
    BY_CATEGORY
    // Consider BY_TIME if you want to group by time periods within the selected day
}

data class ExpenseGroup(
    val groupTitle: String,
    val expenses: List<Expense>,
    val totalAmount: Double,
    val totalCount: Int
)

data class ExpenseListScreenState(
    val displayItems: List<Any> = emptyList(), // Can be Expense or ExpenseGroup
    val currentGrouping: GroupingMode = GroupingMode.NONE,
    val selectedCalendar: Calendar = Calendar.getInstance(),
    val overallTotalCount: Int = 0,
    val overallTotalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedDateFormatted: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseUseCases: ExpenseUseCase
) : ViewModel() {

    private val _selectedCalendar = MutableStateFlow(Calendar.getInstance())
    private val _groupingMode = MutableStateFlow(GroupingMode.NONE)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Flow that fetches raw expenses whenever selectedCalendar changes
    private val _rawExpensesFlow: Flow<List<Expense>> = _selectedCalendar.flatMapLatest { calendar ->
        _isLoading.value = true
        _errorMessage.value = null // Clear previous error
        val (startOfDay, endOfDay) = getStartAndEndOfDay(calendar)
        expenseUseCases.getExpensesForDateRange(startOfDay, endOfDay)
            .catch { e ->
                _errorMessage.value = "Error fetching expenses: ${e.localizedMessage}"
                emit(emptyList()) // Emit empty list on error to clear previous data
            }
            .onEach { _isLoading.value = false } // Set loading false after data is emitted or error
            .onStart { _isLoading.value = true } // Ensure loading is true when flow starts
    }

    val screenState: StateFlow<ExpenseListScreenState> = combine(
        _rawExpensesFlow,
        _groupingMode,
        _selectedCalendar,
        _isLoading,
        _errorMessage
    ) { rawExpenses, grouping, calendar, isLoading, errorMsg ->

        val overallTotalAmount = rawExpenses.sumOf { it.amount }
        val overallTotalCount = rawExpenses.size
        val formattedDate = _dateFormat.format(calendar.time)

        val displayItems: List<Any> = when (grouping) {
            GroupingMode.NONE -> rawExpenses
            GroupingMode.BY_CATEGORY -> {
                if (rawExpenses.isEmpty()) emptyList()
                else rawExpenses.groupBy { it.category }
                    .map { (category, expensesInCategory) ->
                        ExpenseGroup(
                            groupTitle = category,
                            expenses = expensesInCategory.sortedByDescending { it.date }, // Sort within group
                            totalAmount = expensesInCategory.sumOf { it.amount },
                            totalCount = expensesInCategory.size
                        )
                    }.sortedBy { it.groupTitle } // Sort groups by title
            }
        }

        ExpenseListScreenState(
            displayItems = displayItems,
            currentGrouping = grouping,
            selectedCalendar = calendar,
            overallTotalCount = overallTotalCount,
            overallTotalAmount = overallTotalAmount,
            isLoading = isLoading,
            errorMessage = errorMsg,
            selectedDateFormatted = formattedDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseListScreenState(isLoading = true) // Initial state
    )

    init {
        // Initial trigger, selectedCalendar already has today's date
        // The flatMapLatest in _rawExpensesFlow will pick it up.
    }

    fun changeSelectedDate(calendar: Calendar) {
        _selectedCalendar.value = calendar
    }

    fun setGroupingMode(mode: GroupingMode) {
        _groupingMode.value = mode
    }

    private fun getStartAndEndOfDay(calendar: Calendar): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar // Clone to avoid mutating the original
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endOfDay = cal.timeInMillis
        return Pair(startOfDay, endOfDay)
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
