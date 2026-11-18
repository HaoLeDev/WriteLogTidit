package com.tidit.writelog.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    private val fullDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    private val timeOnlyFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fileTimestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }

    fun formatTimeOnly(timestamp: Long): String {
        return timeOnlyFormat.format(Date(timestamp))
    }

    fun formatDateOnly(timestamp: Long): String {
        return dateOnlyFormat.format(Date(timestamp))
    }

    fun formatForFileName(timestamp: Long = System.currentTimeMillis()): String {
        return fileTimestampFormat.format(Date(timestamp))
    }

    fun isToday(timestamp: Long): Boolean {
        val todayStr = formatDateOnly(System.currentTimeMillis())
        val checkStr = formatDateOnly(timestamp)
        return todayStr == checkStr
    }

    fun getDayTitle(dateString: String, sampleTimestamp: Long): String {
        val now = Calendar.getInstance()
        val itemCal = Calendar.getInstance().apply { timeInMillis = sampleTimestamp }

        val isSameYear = now.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR)
        val isSameDay = isSameYear && now.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR)

        return when {
            isSameDay -> "Hôm nay • $dateString"
            isYesterday -> "Hôm qua • $dateString"
            else -> {
                val dayOfWeek = when (itemCal.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> "Thứ Hai"
                    Calendar.TUESDAY -> "Thứ Ba"
                    Calendar.WEDNESDAY -> "Thứ Tư"
                    Calendar.THURSDAY -> "Thứ Năm"
                    Calendar.FRIDAY -> "Thứ Sáu"
                    Calendar.SATURDAY -> "Thứ Bảy"
                    Calendar.SUNDAY -> "Chủ Nhật"
                    else -> ""
                }
                if (dayOfWeek.isNotEmpty()) "$dayOfWeek • $dateString" else dateString
            }
        }
    }
}
