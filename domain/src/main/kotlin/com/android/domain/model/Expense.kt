package com.android.domain.model

data class Expense(
    val id: Long = 0L, // Default to 0, actual ID will be set by Room
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String,
    val date: Long,
    val receiptImageUri: String? = null
)
