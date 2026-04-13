package com.example.financeapp.core.data.local.db
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.core.data.local.dao.GoalDao
import com.example.financeapp.core.data.local.dao.TransactionDao
import com.example.financeapp.core.data.local.entity.GoalEntity
import com.example.financeapp.core.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class],
    version = 3,
    exportSchema = true
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
}
