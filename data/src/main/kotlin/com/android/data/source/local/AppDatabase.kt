package com.android.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.data.entity.ExpenseEntity
import com.android.data.source.local.dao.ExpenseDao

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_tracker_db"
    }
}
