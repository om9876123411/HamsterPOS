package com.example.hamsterpos.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.regex.Pattern

class PaymentNotificationListener : NotificationListenerService() {

    // object ที่จะเก็บข้อมูลการชำระเงินล่าสุดที่ตรวจพบ
    // ในแอปจริง ควรใช้ BroadcastReceiver หรือ Flow เพื่อสื่อสารกับ UI
    companion object {
        var lastReceivedAmount: Double? = null
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName ?: return
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getString("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        // ตรวจสอบว่าเป็น Notification จากแอปธนาคารที่เรารองรับหรือไม่
        // ตัวอย่าง: K PLUS
        if (packageName == "com.kasikorn.retail.mbanking.wap") {
            // ใช้ Regular Expression เพื่อดึงจำนวนเงินออกจากข้อความ
            // ตัวอย่างข้อความ: "รับเงิน 80.00 บ. จาก..."
            val pattern = Pattern.compile("รับเงิน ([0-9,]+\\.?[0-9]*) บ.")
            val matcher = pattern.matcher(title + text)

            if (matcher.find()) {
                val amountString = matcher.group(1)?.replace(",", "")
                val amount = amountString?.toDoubleOrNull()
                if (amount != null) {
                    // เก็บค่าที่ดึงได้ไว้
                    lastReceivedAmount = amount
                    // TODO: ส่ง Broadcast หรืออัปเดต Flow เพื่อแจ้ง UI
                }
            }
        }
    }
}
