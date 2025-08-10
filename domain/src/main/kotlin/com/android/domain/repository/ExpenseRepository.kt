package com.android.domain.repository

import com.android.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    suspend fun addExpense(expense: Expense)

    fun getExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    fun getTotalForDateRange(startDate: Long, endDate: Long): Flow<Double>
}
