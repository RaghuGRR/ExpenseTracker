package com.android.data.repository

import com.android.data.mapper.toDomain
import com.android.data.mapper.toEntity
import com.android.data.source.local.dao.ExpenseDao
import com.android.domain.model.Expense
import com.android.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense.toEntity())
    }

    override fun getExpensesForDateRange(startDate: Long, endDate: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesForDateRange(startDate, endDate)
            .map { entities -> entities.toDomain() }
    }

    override fun getTotalForDateRange(startDate: Long, endDate: Long): Flow<Double> {
        return expenseDao.getTotalForDateRange(startDate, endDate).map { it ?: 0.0 }
    }
}
