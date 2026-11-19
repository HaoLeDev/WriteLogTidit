package com.tidit.writelog.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.SharedPreferences
import android.view.accessibility.AccessibilityEvent
import com.tidit.writelog.TiditApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TiditAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastRecordedTime: Long = 0L

    // Debounce time: minimum 15 seconds between 2 swipes
    private val debounceIntervalMs = 15_000L

    override fun onServiceConnected() {
        super.onServiceConnected()
        isRunning = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        // Tuyệt đối bỏ qua chính ứng dụng WriteLogTidit
        if (packageName.equals(this.packageName, ignoreCase = true) ||
            packageName.equals("com.tidit.writelog", ignoreCase = true)
        ) {
            return
        }

        val targetPackage = getTargetPackage(this)
        val isTargetApp = packageName.equals(targetPackage, ignoreCase = true) ||
                packageName.contains("didid", ignoreCase = true) ||
                packageName.contains("lgcns", ignoreCase = true)

        if (!isTargetApp) return

        // Bắt các sự kiện: Toast/Thông báo nổi, Mở app/dialog, Thay đổi nội dung, hoặc Click
        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            eventType != AccessibilityEvent.TYPE_VIEW_CLICKED
        ) {
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRecordedTime < debounceIntervalMs) {
            // Bỏ qua do debounce (tránh ghi đúp trong 15s)
            return
        }

        lastRecordedTime = currentTime

        // Trích xuất nội dung văn bản hiển thị
        val eventText = event.text?.joinToString(" ")?.trim() ?: ""
        val contentDesc = event.contentDescription?.toString()?.trim() ?: ""
        val textInfo = listOf(eventText, contentDesc).filter { it.isNotBlank() }.joinToString(" - ")

        val (source, note) = when (eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> 
                Pair("Tidit (Toast/Thông báo)", if (textInfo.isNotBlank()) "Nội dung: $textInfo" else "Thông báo xác thực quẹt thẻ")
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> 
                Pair("Tidit (Quẹt qua cửa)", if (textInfo.isNotBlank()) "Giao diện: $textInfo" else "Mở ứng dụng Tidit")
            else -> 
                Pair("Tidit (Tác vụ nền)", if (textInfo.isNotBlank()) "Chi tiết: $textInfo" else "Xác thực cửa ngầm")
        }

        serviceScope.launch {
            TiditApp.instance.repository.recordLog(
                source = source,
                note = note
            )
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    companion object {
        var isRunning: Boolean = false
            private set

        private const val PREF_NAME = "tidit_tracker_prefs"
        private const val KEY_TARGET_PACKAGE = "target_package"
        const val DEFAULT_PACKAGE = "com.lgcns.didid"

        fun getTargetPackage(context: Context): String {
            val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_TARGET_PACKAGE, DEFAULT_PACKAGE) ?: DEFAULT_PACKAGE
        }

        fun setTargetPackage(context: Context, packageName: String) {
            val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_TARGET_PACKAGE, packageName.trim()).apply()
        }
    }
}
