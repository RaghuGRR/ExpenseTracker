package com.android.expensetracker.features.expense_report

// Vico imports - ensure you have added the Vico dependency to your build.gradle.kts
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.expensetracker.ui.common.ReportRow // Specific import
import com.android.expensetracker.ui.common.ReportSection // Specific import
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    // --- VICO CHART SETUP ---
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(state.dailyTotals) {
        // Data for Vico should be in chronological order (oldest to newest for x-axis progression)
        // The current state.dailyTotals is newest to oldest for list display.
        // For the chart, we want the original order (oldest to newest).
        // The ViewModel's mock data generation stores it oldest to newest before reversing for display.
        // Let's assume the ViewModel can provide it in the original (chronological) order if needed
        // or we can reverse it here if state.dailyTotals is always newest-first.
        // For now, let's use state.dailyTotals and map it. Vico takes x from 0 upwards by default.
        val chartData = state.dailyTotals.reversed() // Reverse back to chronological for chart
        val entries = chartData.mapIndexed { index, dailyTotal ->
            entryOf(index.toFloat(), dailyTotal.amount.toFloat())
        }
        chartEntryModelProducer.setEntries(entries)
    }

    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, chartValues ->
        // Get the dayLabel from the chronologically ordered data
        state.dailyTotals.reversed().getOrNull(value.toInt())?.dayLabel ?: value.toInt().toString()
    }
    // --- END VICO CHART SETUP ---

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.reportTitle) },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, viewModel.getShareableReportText())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share Report")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Overall Total (Last 7 Days): ${currencyFormat.format(state.overallTotal)}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ReportSection(title = "Daily Spending Trend") {
                    if (state.dailyTotals.isNotEmpty()) {
                        // Ensure you have the appropriate Vico theme artifact e.g. vico-compose-m3
                        // ProvideChartStyle(currentChartStyle()) { // Using M3 style if available
                        Chart(
                            chart = columnChart(), // Use a column chart (bar chart)
                            chartModelProducer = chartEntryModelProducer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            startAxis = rememberStartAxis(
                                title = "Amount (${currencyFormat.currency?.symbol ?: "₹"})"
                            ),
                            bottomAxis = rememberBottomAxis(
                                valueFormatter = bottomAxisValueFormatter,
                                title = "Day"
                            ),
                            chartScrollState = rememberChartScrollState() // For scrollable charts
                        )
                        // }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No data available for chart.")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReportSection(title = "Daily Totals") {
                    // state.dailyTotals is already newest to oldest for display
                    state.dailyTotals.forEach { daily ->
                        ReportRow(label = daily.date, value = currencyFormat.format(daily.amount))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReportSection(title = "Category Totals (Last 7 Days)") {
                    state.categoryTotals.forEach { category ->
                        ReportRow(label = category.categoryName, value = currencyFormat.format(category.amount))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val message = viewModel.simulateExportToPdf()
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export PDF")
                    }
                    Button(
                        onClick = {
                            val message = viewModel.simulateExportToCsv()
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Export CSV")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

