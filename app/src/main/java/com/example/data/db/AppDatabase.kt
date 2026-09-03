package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BankConnection
import com.example.data.model.Budget
import com.example.data.model.Transaction
import com.example.data.model.SavingGoal
import com.example.data.model.RecurringBill

@Database(
    entities = [Transaction::class, Budget::class, BankConnection::class, SavingGoal::class, RecurringBill::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ensure saving_goals table exists
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `saving_goals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `targetAmount` REAL NOT NULL, 
                        `currentAmount` REAL NOT NULL, 
                        `targetDate` TEXT NOT NULL, 
                        `category` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                // Ensure recurring_bills table exists
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recurring_bills` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `amount` REAL NOT NULL, 
                        `category` TEXT NOT NULL, 
                        `billingCycle` TEXT NOT NULL, 
                        `dueDate` TEXT NOT NULL, 
                        `lastClaimedTimestamp` INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                // Ensure bankSource column and timestamp index are present
                try {
                    db.execSQL("ALTER TABLE `transactions` ADD COLUMN `bankSource` TEXT DEFAULT NULL")
                } catch (_: Exception) {
                    // Column already present in existing schema
                }
                try {
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_timestamp` ON `transactions` (`timestamp`)")
                } catch (_: Exception) {
                    // Index already present
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uangku_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build().also { INSTANCE = it }
            }
        }
    }
}
