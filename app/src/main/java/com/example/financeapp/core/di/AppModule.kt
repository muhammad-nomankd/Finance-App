package com.example.financeapp.core.di

import android.content.Context
import androidx.room.Room
import com.example.financeapp.core.data.local.dao.GoalDao
import com.example.financeapp.core.data.local.dao.TransactionDao
import com.example.financeapp.core.data.local.db.FinanceDatabase
import com.example.financeapp.core.data.repository.GoalRepositoryImpl
import com.example.financeapp.core.data.repository.TransactionRepositoryImpl
import com.example.financeapp.core.domain.repository.GoalRepository
import com.example.financeapp.core.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinanceDatabase =
        Room.databaseBuilder(context, FinanceDatabase::class.java, "finance.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransactionDao(db: FinanceDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideGoalDao(db: FinanceDatabase): GoalDao = db.goalDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository
}