package com.android.data.mapper

import com.android.data.entity.ExpenseEntity
import com.android.domain.model.Expense

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = this.id,
        title = this.title,
        amount = this.amount,
        category = this.category,
        notes = this.notes,
        date = this.date,
        receiptImageUri = this.receiptImageUri
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = this.id, // Using your provided logic
        title = this.title,
        amount = this.amount,
        category = this.category,
        notes = this.notes,
        date = this.date,
        receiptImageUri = this.receiptImageUri
    )
}

fun List<ExpenseEntity>.toDomain(): List<Expense> {
    return this.map { it.toDomain() }
}
