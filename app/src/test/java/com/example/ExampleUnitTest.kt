package com.example

import com.example.service.MyNotificationListenerService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `rejects non-official apps and self package`() {
        assertFalse(MyNotificationListenerService.isOfficialFinancialPackage("com.whatsapp"))
        assertFalse(MyNotificationListenerService.isOfficialFinancialPackage("com.aistudio.uangku.kxmpzq"))
        assertTrue(MyNotificationListenerService.isOfficialFinancialPackage("com.bca"))
        assertTrue(MyNotificationListenerService.isOfficialFinancialPackage("id.co.bankmandiri.livin"))
        assertTrue(MyNotificationListenerService.isOfficialFinancialPackage("id.dana"))

        // WhatsApp message containing Rp amount must never be recorded
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.whatsapp",
                title = "Budi",
                text = "Transfer masuk Rp 500.000 sudah diterima ya"
            )
        )

        // Uangku's own alert notification must never re-trigger itself
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.aistudio.uangku.kxmpzq",
                title = "Peringatan Anggaran",
                text = "Pengeluaran Anda Rp 500.000 melebihi batas"
            )
        )
    }

    @Test
    fun `rejects bare years, otp, balance info, promos, and failed notifications`() {
        // Bare digits like year 2026 without Rp/IDR currency marker must be rejected
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.bca",
                title = "myBCA Terpasang",
                text = "Aplikasi terpasang 2026 versi terbaru berhasil diperbarui"
            )
        )

        // OTP notification with digits must be rejected
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "id.co.bankmandiri.livin",
                title = "Kode OTP Transaksi",
                text = "Gunakan kode verifikasi 849201 untuk transfer Rp 100.000. Rahasia!"
            )
        )

        // Balance inquiry / info saldo must be rejected
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.bri.brimo",
                title = "Info Saldo Rekening",
                text = "Sisa saldo efektif Anda saat ini Rp 4.500.000"
            )
        )

        // Promo / voucher notifications must be rejected
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "id.dana",
                title = "Promo Spesial Hari Ini",
                text = "Dapatkan voucher diskon bayar QRIS Rp 25.000"
            )
        )

        // Failed / declined transaction must be rejected
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.gojek.app",
                title = "Pembayaran Gagal",
                text = "Pembayaran QRIS sebesar Rp 75.000 gagal karena saldo kurang"
            )
        )
    }

    @Test
    fun `rejects negative currency values to prevent negative expenses`() {
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "com.bca",
                title = "Mutasi Rekening",
                text = "Debit pembayaran -Rp50.000 berhasil"
            )
        )
        assertNull(
            MyNotificationListenerService.parseNotification(
                packageName = "ovo.id",
                title = "Transaksi OVO",
                text = "Pembayaran merchant sebesar Rp -120.000 berhasil"
            )
        )
    }

    @Test
    fun `classifies polite thank-you payment notification as EXPENSE with positive amount`() {
        val tx = MyNotificationListenerService.parseNotification(
            packageName = "com.bca",
            title = "Transaksi QRIS Berhasil",
            text = "Terima kasih telah melakukan pembayaran QRIS sebesar Rp 50.000,- di Kopi Kenangan"
        )

        assertNotNull(tx)
        assertEquals("EXPENSE", tx!!.type)
        assertEquals(50_000.0, tx.amount, 0.001)
        assertEquals("BCA", tx.bankSource)
        assertEquals("Belanja", tx.category)
    }

    @Test
    fun `parses valid INCOME and EXPENSE notifications from official banks and ewallets`() {
        val salaryIncome = MyNotificationListenerService.parseNotification(
            packageName = "id.co.bankmandiri.livin",
            title = "Dana Masuk Rekening",
            text = "Transfer masuk sebesar Rp 7.500.000 dari PT MAJU JAYA telah diterima"
        )
        assertNotNull(salaryIncome)
        assertEquals("INCOME", salaryIncome!!.type)
        assertEquals(7_500_000.0, salaryIncome.amount, 0.001)
        assertEquals("Mandiri", salaryIncome.bankSource)

        val gopayTopUp = MyNotificationListenerService.parseNotification(
            packageName = "com.gojek.gopay",
            title = "Top Up Berhasil",
            text = "Isi saldo GoPay sebesar IDR 250.000,00 berhasil masuk"
        )
        assertNotNull(gopayTopUp)
        assertEquals("INCOME", gopayTopUp!!.type)
        assertEquals(250_000.0, gopayTopUp.amount, 0.001)
        assertEquals("GoPay", gopayTopUp.bankSource)

        val seabankExpense = MyNotificationListenerService.parseNotification(
            packageName = "id.co.seabank.seabankid",
            title = "Pembayaran Berhasil",
            text = "Pembelian di Tokopedia senilai Rp 185.500 berhasil dibayar"
        )
        assertNotNull(seabankExpense)
        assertEquals("EXPENSE", seabankExpense!!.type)
        assertEquals(185_500.0, seabankExpense.amount, 0.001)
        assertEquals("SeaBank", seabankExpense.bankSource)
    }
}
