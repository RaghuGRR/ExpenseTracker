package com.android.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.expensetracker.features.expense_entry.ExpenseEntryScreen
import com.android.expensetracker.features.expense_list.ExpenseListScreen
import com.android.expensetracker.features.expense_report.ExpenseReportScreen
import com.android.expensetracker.ui.common.ThemeSwitcherIcon
import com.android.expensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

// Define navigation routes
object AppRoutes {
    const val EXPENSE_LIST = "expense_list"
    const val EXPENSE_ENTRY = "expense_entry"
    const val EXPENSE_REPORT = "expense_report"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@AndroidEntryPoint // Important for Hilt if your ViewModels are Hilt-injected
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme(dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(AppRoutes.EXPENSE_LIST, "Expenses", Icons.Filled.List),
        BottomNavItem(AppRoutes.EXPENSE_REPORT, "Report", Icons.Filled.Assessment)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { // Add TopAppBar here
            TopAppBar(
                title = { Text("Expense Tracker") }, // Static title for now
                actions = {
                    ThemeSwitcherIcon() // Add the theme switcher icon
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    IconButton(
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.label,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.80f) // MODIFIED HERE
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppRoutes.EXPENSE_ENTRY) }) {
                Icon(Icons.Filled.Add, "Add Expense")
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.EXPENSE_LIST,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.EXPENSE_LIST) {
                ExpenseListScreen() // ViewModel will be hiltViewModel() by default
            }
            composable(AppRoutes.EXPENSE_ENTRY) {
                ExpenseEntryScreen() // ViewModel will be hiltViewModel() by default
            }
            composable(AppRoutes.EXPENSE_REPORT) {
                ExpenseReportScreen() // ViewModel will be hiltViewModel() by default
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ExpenseTrackerTheme {
        AppNavigation()
    }
}
