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
}
