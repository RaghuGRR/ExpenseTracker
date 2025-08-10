package com.android.domain.usecase

import com.android.domain.model.Expense
import com.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
){
    suspend fun addExpense(expense: Expense) {
        // Basic validation (can be expanded)
        if (expense.title.isBlank()) {
            throw IllegalArgumentException("Expense title cannot be blank.")
        }
        if (expense.amount <= 0) {
            throw IllegalArgumentException("Expense amount must be positive.")
        }
        repository.addExpense(expense)
    }

    fun getExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return repository.getExpensesForDateRange(startDate, endDate)
    }

    fun getTotalForDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return repository.getTotalForDateRange(startDate, endDate)
    }
}