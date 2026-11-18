package com.tidit.writelog.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tidit.writelog.data.model.AccessLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessLogDao {
    @Query("SELECT * FROM tidit_access_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AccessLog>>

    @Query("SELECT * FROM tidit_access_logs ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLog(): Flow<AccessLog?>

    @Query("SELECT COUNT(*) FROM tidit_access_logs WHERE timestamp >= :startOfDay")
    fun getTodayLogCount(startOfDay: Long): Flow<Int>

    @Query("SELECT * FROM tidit_access_logs ORDER BY timestamp DESC")
    suspend fun getAllLogsSnapshot(): List<AccessLog>

    @Insert
    suspend fun insertLog(log: AccessLog): Long

    @Delete
    suspend fun deleteLog(log: AccessLog)

    @Query("DELETE FROM tidit_access_logs WHERE id IN (:ids)")
    suspend fun deleteLogsByIds(ids: List<Long>)

    @Query("DELETE FROM tidit_access_logs")
    suspend fun clearAllLogs()
}
