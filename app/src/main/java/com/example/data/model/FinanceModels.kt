package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["timestamp"])]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String, // "Makanan", "Sewa", "Transportasi", "Gaji", "Investasi", "Belanja", "Koneksi Bank", "Lainnya"
    val timestamp: Long = System.currentTimeMillis(),
    val bankSource: String? = null // e.g. "BCA Sandbox" if synced
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val category: String, // "ALL" or specific categories
    val limitAmount: Double,
    val alertThresholdPercent: Int = 80 // alert trigger percentage (e.g. 80%)
)

@Entity(tableName = "bank_connections")
data class BankConnection(
    @PrimaryKey val bankId: String, // e.g., "bca", "mandiri", "bni", "bri"
    val bankName: String, // e.g., "BCA", "Bank Mandiri"
    val accountNumber: String,
    val balance: Double,
    val isConnected: Boolean = false,
    val lastSyncTime: Long = 0
)

@Entity(tableName = "saving_goals")
data class SavingGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String, // e.g. "Desember 2026"
    val category: String // "Liburan", "Gawai", "Dana Darurat", "Kendaraan", "Lainnya"
)

@Entity(tableName = "recurring_bills")
data class RecurringBill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String, // "Makanan", "Hiburan", "Sewa", "Transportasi", "Lainnya"
    val billingCycle: String, // "Bulanan", "Mingguan", "Tahunan"
    val dueDate: String, // "Tanggal 10" atau "Setiap tanggal 5"
    val lastClaimedTimestamp: Long = 0
)

fun Transaction.getCleanTitle(): String {
    return this.title
        .replace(" (BCA)", "")
        .replace(" (Mandiri)", "")
        .replace(" (BNI)", "")
        .replace(" (BRI)", "")
        .replace(" (OVO)", "")
        .replace(" (GoPay)", "")
        .replace(" (ShopeePay)", "")
        .replace(" (Bank Notifikasi)", "")
}

data class SavingChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetAmount: Double,
    val amountPerCheckIn: Double,
    val currentProgress: Int, // Jumlah check-in yang selesai
    val targetProgress: Int,  // Target jumlah check-in (misal 10)
    val scheduleText: String, // misal "Setiap hari Jumat"
    val status: String = "ACTIVE" // "ACTIVE", "COMPLETED"
)

data class VirtualBadge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String, // misal "🛡️", "🎯", "🏆", "👑", "🌟"
    val isUnlocked: Boolean = false,
    val unlockProgressText: String = ""
)

