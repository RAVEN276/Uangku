package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BankConnection
import com.example.data.model.Budget
import com.example.data.model.Transaction
import com.example.data.model.SavingGoal
import com.example.data.model.RecurringBill
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun clearAllTransactions()

    // --- Budgets ---
    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    suspend fun getBudgetByCategory(category: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("DELETE FROM budgets")
    suspend fun clearBudgets()

    // --- Bank Connections ---
    @Query("SELECT * FROM bank_connections")
    fun getAllBankConnections(): Flow<List<BankConnection>>

    @Query("SELECT * FROM bank_connections WHERE bankId = :bankId LIMIT 1")
    suspend fun getBankConnectionById(bankId: String): BankConnection?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankConnection(connection: BankConnection)

    @Update
    suspend fun updateBankConnection(connection: BankConnection)

    @Query("DELETE FROM bank_connections")
    suspend fun clearBankConnections()

    // --- Saving Goals ---
    @Query("SELECT * FROM saving_goals")
    fun getAllSavingGoals(): Flow<List<SavingGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoal(goal: SavingGoal)

    @Update
    suspend fun updateSavingGoal(goal: SavingGoal)

    @Delete
    suspend fun deleteSavingGoal(goal: SavingGoal)

    @Query("DELETE FROM saving_goals")
    suspend fun clearAllSavingGoals()

    // --- Recurring Bills ---
    @Query("SELECT * FROM recurring_bills")
    fun getAllRecurringBills(): Flow<List<RecurringBill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringBill(bill: RecurringBill)

    @Update
    suspend fun updateRecurringBill(bill: RecurringBill)

    @Delete
    suspend fun deleteRecurringBill(bill: RecurringBill)

    @Query("DELETE FROM recurring_bills")
    suspend fun clearAllRecurringBills()
}
