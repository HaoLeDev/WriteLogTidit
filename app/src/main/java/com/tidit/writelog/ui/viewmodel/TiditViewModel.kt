package com.tidit.writelog.ui.viewmodel

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tidit.writelog.TiditApp
import com.tidit.writelog.data.model.AccessLog
import com.tidit.writelog.service.TiditAccessibilityService
import com.tidit.writelog.util.CsvExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TiditViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as TiditApp).repository

    val isAccessibilityEnabled = MutableStateFlow(false)
    val isNotificationListenerEnabled = MutableStateFlow(false)
    val isBatteryOptimizationIgnored = MutableStateFlow(false)
    val targetPackage = MutableStateFlow(TiditAccessibilityService.getTargetPackage(application))

    val allLogs: StateFlow<List<AccessLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestLog: StateFlow<AccessLog?> = repository.latestLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayLogCount: StateFlow<Int> = repository.getTodayLogCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun refreshAllStatuses(context: Context) {
        // 1. Accessibility Service status
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        val enabledList = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK) ?: emptyList()
        val isAccEnabled = enabledList.any {
            it.resolveInfo.serviceInfo.packageName == context.packageName &&
                    it.resolveInfo.serviceInfo.name == TiditAccessibilityService::class.java.name
        }
        isAccessibilityEnabled.value = isAccEnabled || TiditAccessibilityService.isRunning

        // 2. Notification Listener status
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") ?: ""
        isNotificationListenerEnabled.value = flat.contains(context.packageName)

        // 3. Battery Optimization status
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        isBatteryOptimizationIgnored.value = pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở Cài đặt Trợ năng", Toast.LENGTH_SHORT).show()
        }
    }

    fun openNotificationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở Cài đặt Quyền thông báo", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestIgnoreBatteryOptimization(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Không thể mở Cài đặt Tiết kiệm pin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openTiditAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${targetPackage.value}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không tìm thấy ứng dụng ${targetPackage.value}", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateTargetPackage(newPackage: String) {
        if (newPackage.isNotBlank()) {
            TiditAccessibilityService.setTargetPackage(getApplication(), newPackage)
            targetPackage.value = newPackage.trim()
        }
    }

    /**
     * Allows manual trigger for testing
     */
    fun recordManualLog() {
        viewModelScope.launch {
            repository.recordLog(
                source = "Ghi thủ công",
                note = "Người dùng nhấn ghi log trực tiếp"
            )
            Toast.makeText(getApplication(), "Đã ghi nhận 1 lượt quẹt!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteLog(log: AccessLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun deleteLogsByIds(ids: Set<Long>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.deleteLogsByIds(ids)
            Toast.makeText(getApplication(), "Đã xóa ${ids.size} lượt quẹt!", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            Toast.makeText(getApplication(), "Đã xóa toàn bộ lịch sử", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportLogsToCsv() {
        viewModelScope.launch {
            val logs = repository.getAllLogsSnapshot()
            if (logs.isEmpty()) {
                Toast.makeText(getApplication(), "Chưa có log nào để xuất!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val success = CsvExporter.exportAndShareLogs(getApplication(), logs)
            if (!success) {
                Toast.makeText(getApplication(), "Không thể tạo file CSV!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
