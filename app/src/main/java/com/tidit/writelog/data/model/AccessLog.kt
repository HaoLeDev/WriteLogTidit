package com.tidit.writelog.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tidit_access_logs")
data class AccessLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventSource: String = "Tidit (Quẹt qua cửa)",
    val note: String = ""
)
