package com.tidit.writelog.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.tidit.writelog.TiditApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TiditNotificationListener : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastRecordedTime: Long = 0L
    private val debounceIntervalMs = 15_000L

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName ?: return

        // Tuyệt đối bỏ qua chính ứng dụng WriteLogTidit
        if (packageName.equals(this.packageName, ignoreCase = true) ||
            packageName.equals("com.tidit.writelog", ignoreCase = true)
        ) {
            return
        }

        val targetPackage = TiditAccessibilityService.getTargetPackage(this)
        val isTargetApp = packageName.equals(targetPackage, ignoreCase = true) ||
                packageName.contains("didid", ignoreCase = true) ||
                packageName.contains("lgcns", ignoreCase = true)

        if (!isTargetApp) return

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRecordedTime < debounceIntervalMs) {
            return
        }

        val extras = sbn.notification.extras
        val title = extras?.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        val subText = extras?.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString() ?: ""
        val ticker = sbn.notification.tickerText?.toString() ?: ""

        val detail = listOf(title, text, subText, ticker).filter { it.isNotBlank() }.joinToString(" - ")

        lastRecordedTime = currentTime

        serviceScope.launch {
            TiditApp.instance.repository.recordLog(
                source = "Tidit (Thông báo)",
                note = detail.ifBlank { "Quẹt thẻ thành công (NFC ngầm)" }
            )
        }
    }
}
