package com.example.data.repository

import com.example.data.db.FinanceDao
import com.example.data.model.BankConnection
import com.example.data.model.Budget
import com.example.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {

    val allTransactions: Flow<List<Transaction>> = financeDao.getAllTransactions()
    val allBudgets: Flow<List<Budget>> = financeDao.getAllBudgets()
    val allBankConnections: Flow<List<BankConnection>> = financeDao.getAllBankConnections()

    suspend fun insertTransaction(transaction: Transaction) {
        financeDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        financeDao.deleteTransaction(transaction)
    }

    suspend fun clearAllTransactions() {
        financeDao.clearAllTransactions()
    }

    suspend fun getBudgetByCategory(category: String): Budget? {
        return financeDao.getBudgetByCategory(category)
    }

    suspend fun insertBudget(budget: Budget) {
        financeDao.insertBudget(budget)
    }

    suspend fun deleteBudget(budget: Budget) {
        financeDao.deleteBudget(budget)
    }

    suspend fun clearBudgets() {
        financeDao.clearBudgets()
    }

    suspend fun clearBankConnections() {
        financeDao.clearBankConnections()
    }

    suspend fun getBankConnectionById(bankId: String): BankConnection? {
        return financeDao.getBankConnectionById(bankId)
    }

    suspend fun insertBankConnection(connection: BankConnection) {
        financeDao.insertBankConnection(connection)
    }

    suspend fun updateBankConnection(connection: BankConnection) {
        financeDao.updateBankConnection(connection)
    }

    // Prepopulate database with realistic dummy/seed data for a polished first use
    suspend fun prepopulateSeedDataIfNeeded() {
        // We will do a seed check inside the ViewModel or inside DB initialization
    }
}
