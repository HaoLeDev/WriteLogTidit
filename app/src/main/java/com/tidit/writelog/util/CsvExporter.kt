package com.tidit.writelog.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.tidit.writelog.data.model.AccessLog
import java.io.File
import java.io.FileWriter

object CsvExporter {

    fun exportAndShareLogs(context: Context, logs: List<AccessLog>): Boolean {
        if (logs.isEmpty()) return false

        try {
            val logsDir = File(context.cacheDir, "logs").apply {
                if (!exists()) mkdirs()
            }

            val fileName = "Tidit_Swipe_Log_${DateTimeUtils.formatForFileName()}.csv"
            val file = File(logsDir, fileName)

            FileWriter(file).use { writer ->
                // Write UTF-8 BOM so Excel opens Vietnamese characters cleanly
                writer.write("\uFEFF")
                writer.append("STT,Thời gian,Ngày,Giờ,Nguồn,Ghi chú\n")

                logs.forEachIndexed { index, log ->
                    val fullTime = DateTimeUtils.formatFullDateTime(log.timestamp)
                    val dateOnly = DateTimeUtils.formatDateOnly(log.timestamp)
                    val timeOnly = DateTimeUtils.formatTimeOnly(log.timestamp)
                    val source = escapeCsv(log.eventSource)
                    val note = escapeCsv(log.note)

                    writer.append("${index + 1},\"$fullTime\",\"$dateOnly\",\"$timeOnly\",\"$source\",\"$note\"\n")
                }
                writer.flush()
            }

            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Nhật ký quẹt Tidit - ${DateTimeUtils.formatDateOnly(System.currentTimeMillis())}")
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Xuất file nhật ký quẹt thẻ Tidit")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }
}
