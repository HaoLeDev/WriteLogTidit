package com.tidit.writelog.data.repository

import com.tidit.writelog.data.db.AccessLogDao
import com.tidit.writelog.data.model.AccessLog
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TiditRepository(
    private val accessLogDao: AccessLogDao
) {
    val allLogs: Flow<List<AccessLog>> = accessLogDao.getAllLogs()
    val latestLog: Flow<AccessLog?> = accessLogDao.getLatestLog()

    fun getTodayLogCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return accessLogDao.getTodayLogCount(calendar.timeInMillis)
    }

    suspend fun recordLog(source: String = "Tidit (Quẹt qua cửa)", note: String = ""): AccessLog {
        val log = AccessLog(
            timestamp = System.currentTimeMillis(),
            eventSource = source,
            note = note
        )
        val id = accessLogDao.insertLog(log)
        return log.copy(id = id)
    }

    suspend fun deleteLog(log: AccessLog) {
        accessLogDao.deleteLog(log)
    }

    suspend fun deleteLogsByIds(ids: Collection<Long>) {
        if (ids.isNotEmpty()) {
            accessLogDao.deleteLogsByIds(ids.toList())
        }
    }

    suspend fun clearAllLogs() {
        accessLogDao.clearAllLogs()
    }

    suspend fun getAllLogsSnapshot(): List<AccessLog> {
        return accessLogDao.getAllLogsSnapshot()
    }
}
