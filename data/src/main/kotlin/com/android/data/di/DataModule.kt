package com.android.data.di

import android.app.Application
import androidx.room.Room
import com.android.data.repository.ExpenseRepositoryImpl
import com.android.data.source.local.AppDatabase
import com.android.data.source.local.dao.ExpenseDao
import com.android.domain.repository.ExpenseRepository
import com.android.domain.usecase.ExpenseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(
        expenseDao: ExpenseDao
    ): ExpenseRepository {
        return ExpenseRepositoryImpl(
            expenseDao = expenseDao
        )
    }

    @Provides
    @Singleton
    fun provideExpenseUseCase(
        repository: ExpenseRepository
    ): ExpenseUseCase {
        return ExpenseUseCase(
           repository = repository
        )
    }
}
