package com.example.data.repository

import com.example.data.db.FinanceDao
import com.example.data.model.BankConnection
import com.example.data.model.Budget
import com.example.data.model.Transaction
import com.example.data.model.SavingGoal
import com.example.data.model.RecurringBill
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {

    val allTransactions: Flow<List<Transaction>> = financeDao.getAllTransactions()
    val allBudgets: Flow<List<Budget>> = financeDao.getAllBudgets()
    val allBankConnections: Flow<List<BankConnection>> = financeDao.getAllBankConnections()
    val allSavingGoals: Flow<List<SavingGoal>> = financeDao.getAllSavingGoals()
    val allRecurringBills: Flow<List<RecurringBill>> = financeDao.getAllRecurringBills()

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

    // --- Saving Goals ---
    suspend fun insertSavingGoal(goal: SavingGoal) {
        financeDao.insertSavingGoal(goal)
    }

    suspend fun updateSavingGoal(goal: SavingGoal) {
        financeDao.updateSavingGoal(goal)
    }

    suspend fun deleteSavingGoal(goal: SavingGoal) {
        financeDao.deleteSavingGoal(goal)
    }

    suspend fun clearAllSavingGoals() {
        financeDao.clearAllSavingGoals()
    }

    // --- Recurring Bills ---
    suspend fun insertRecurringBill(bill: RecurringBill) {
        financeDao.insertRecurringBill(bill)
    }

    suspend fun updateRecurringBill(bill: RecurringBill) {
        financeDao.updateRecurringBill(bill)
    }

    suspend fun deleteRecurringBill(bill: RecurringBill) {
        financeDao.deleteRecurringBill(bill)
    }

    suspend fun clearAllRecurringBills() {
        financeDao.clearAllRecurringBills()
    }

    // Prepopulate database with realistic dummy/seed data for a polished first use
    suspend fun prepopulateSeedDataIfNeeded() {
        // We will do a seed check inside the ViewModel or inside DB initialization
    }
}
