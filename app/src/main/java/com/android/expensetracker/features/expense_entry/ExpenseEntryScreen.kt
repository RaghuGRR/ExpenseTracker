package com.android.expensetracker.features.expense_entry

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.expensetracker.ui.common.AppDropdownField
import com.android.expensetracker.ui.common.AppTextField
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseEntryScreen(
    viewModel: ExpenseEntryViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val title by viewModel.title.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val category by viewModel.category.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val receiptImageUriString by viewModel.receiptImageUri.collectAsState()
    val totalSpentToday by viewModel.totalSpentToday.collectAsState()

    val categories = remember { listOf("Staff", "Travel", "Food", "Utility", "Other") }
    // expandedCategoryDropdown is now managed within AppDropdownField

    val uiEvent by viewModel.uiEvents.collectAsState()

    LaunchedEffect(uiEvent) {
        uiEvent?.let {
            when (it) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    viewModel.consumeUiEvent() // Consume the event after showing Toast
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onReceiptImageSelected(uri?.toString())
    }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add New Expense") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Total Spent Today: ${currencyFormat.format(totalSpentToday)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            AppTextField(
                value = title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = "Title*",
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank()
            )

            AppTextField(
                value = amount,
                onValueChange = { viewModel.onAmountChange(it) },
                label = "Amount (₹)*",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = amount.toDoubleOrNull() == null || (amount.toDoubleOrNull() ?: 0.0) <= 0
            )

            AppDropdownField(
                selectedValue = category,
                onValueSelected = { viewModel.onCategoryChange(it) },
                label = "Category*",
                options = categories,
                modifier = Modifier.fillMaxWidth(),
                isError = category.isBlank()
            )

            AppTextField(
                value = notes,
                onValueChange = { viewModel.onNotesChange(it) },
                label = "Notes (Optional)",
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                singleLine = false // Ensure singleLine is false for maxLines > 1
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (receiptImageUriString == null) "Select Receipt Image" else "Change Receipt Image")
            }

            receiptImageUriString?.let {
                Text(
                    "Selected: ${Uri.parse(it).lastPathSegment ?: "Image"}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.addExpense() },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Submit Expense")
            }
        }
    }
}
