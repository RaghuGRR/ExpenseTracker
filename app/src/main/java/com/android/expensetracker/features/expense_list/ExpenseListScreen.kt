package com.android.expensetracker.features.expense_list

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.domain.model.Expense
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val context = LocalContext.current

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }
    // Date formatter for individual expense items if needed, though group headers will use groupTitle
    val itemDateFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }


    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            viewModel.changeSelectedDate(calendar)
        },
        state.selectedCalendar.get(Calendar.YEAR),
        state.selectedCalendar.get(Calendar.MONTH),
        state.selectedCalendar.get(Calendar.DAY_OF_MONTH)
    )

    var showGroupingMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expenses: ${state.selectedDateFormatted}") },
                actions = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
                    }
                    Box {
                        IconButton(onClick = { showGroupingMenu = true }) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Group By")
                        }
                        DropdownMenu(
                            expanded = showGroupingMenu,
                            onDismissRequest = { showGroupingMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("No Grouping") },
                                onClick = {
                                    viewModel.setGroupingMode(GroupingMode.NONE)
                                    showGroupingMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Group by Category") },
                                onClick = {
                                    viewModel.setGroupingMode(GroupingMode.BY_CATEGORY)
                                    showGroupingMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total: ${currencyFormat.format(state.overallTotalAmount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Count: ${state.overallTotalCount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider()

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                        Button(onClick = {
                            viewModel.clearErrorMessage()
                            // Optionally, explicitly trigger a refresh if needed
                            viewModel.changeSelectedDate(state.selectedCalendar.clone() as Calendar)
                        }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (state.displayItems.isEmpty() && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No expenses found for this date.", modifier = Modifier.padding(16.dp))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp) // Only vertical for list, items handle horizontal
                ) {
                    /*state.displayItems.forEach { item ->
                        when (item) {
                            is ExpenseGroup -> {
                                stickyHeader {
                                    GroupHeader(
                                        title = item.groupTitle,
                                        count = item.totalCount,
                                        amount = item.totalAmount,
                                        currencyFormat = currencyFormat
                                    )
                                }
                                items(item.expenses, key = { "expense-${it.id}" }) { expense ->
                                    ExpenseListItem(
                                        expense = expense,
                                        currencyFormat = currencyFormat,
                                        dateFormat = itemDateFormat // For time within the day
                                    )
                                }
                            }
                            is Expense -> { // Ungrouped items
                                item(key = { "expense-${item.id}" }) {
                                    ExpenseListItem(
                                        expense = item,
                                        currencyFormat = currencyFormat,
                                        dateFormat = itemDateFormat
                                    )
                                }
                            }
                        }
                    }*/
                    items(
                        items = state.displayItems,
                        // The key must be unique across ALL items in the list.
                        key = { item ->
                            when (item) {
                                is ExpenseGroup -> "group-${item.groupTitle}"
                                is Expense -> "expense-${item.id}"
                                else -> item.hashCode().toString()
                            }
                        },
                        // contentType helps LazyColumn recycle views more efficiently
                        contentType = { item ->
                            when (item) {
                                is ExpenseGroup -> "group" // All groups are of this type
                                is Expense -> "expense"  // All expenses are of this type
                            }
                        }
                    ) { item ->
                        // Now, inside the item content lambda, you can use the 'when' statement
                        when (item) {
                            is ExpenseGroup -> {
                                // This is a special case. You can't call stickyHeader and items here.
                                // We must handle this by flattening the list in the ViewModel.
                                // See the "Better Solution" below. For a quick fix, this is an issue.
                                // For now, let's just display the group items as regular items.
                                Column { // Wrap them in a Column for demonstration
                                    GroupHeader(
                                        title = item.groupTitle,
                                        count = item.totalCount,
                                        amount = item.totalAmount,
                                        currencyFormat = currencyFormat
                                    )
                                    item.expenses.forEach { expense ->
                                        ExpenseListItem(
                                            expense = expense,
                                            currencyFormat = currencyFormat,
                                            dateFormat = itemDateFormat
                                        )
                                    }
                                }
                            }
                            is Expense -> { // Ungrouped items
                                ExpenseListItem(
                                    expense = item,
                                    currencyFormat = currencyFormat,
                                    dateFormat = itemDateFormat
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupHeader(title: String, count: Int, amount: Double, currencyFormat: NumberFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Column(horizontalAlignment = Alignment.End) {
            Text(currencyFormat.format(amount), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Count: $count", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


@Composable
fun ExpenseListItem(expense: Expense, currencyFormat: NumberFormat, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Padding for each card
            .clickable { /* Handle item click if needed */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top // Changed to Top for better alignment with multiline notes
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    // SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(expense.date),
                    // If grouping by category on a single day, time might be relevant
                    dateFormat.format(expense.date), // Shows time
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if(expense.category.isNotBlank() && expense.notes.isNotBlank()){
                     Text(expense.category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                if (expense.notes.isNotBlank()) {
                    Text(expense.notes, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                }
                 expense.receiptImageUri?.let {
                     Text("Receipt Attached", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                currencyFormat.format(expense.amount),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

