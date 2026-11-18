package com.tidit.writelog.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tidit.writelog.data.model.AccessLog
import com.tidit.writelog.ui.theme.PrimaryBlue
import com.tidit.writelog.ui.theme.SecondaryTeal
import com.tidit.writelog.ui.theme.StatusInGreen
import com.tidit.writelog.ui.viewmodel.TiditViewModel
import com.tidit.writelog.util.DateTimeUtils

@Composable
fun DashboardScreen(viewModel: TiditViewModel) {
    val context = LocalContext.current
    val isServiceRunning by viewModel.isAccessibilityEnabled.collectAsState()
    val targetPackage by viewModel.targetPackage.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val todayCount by viewModel.todayLogCount.collectAsState()
    val latestLog by viewModel.latestLog.collectAsState()

    var selectedLogIds by remember { mutableStateOf(setOf<Long>()) }
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    var showPackageDialog by remember { mutableStateOf(false) }

    // Keep selected IDs in sync when logs are deleted
    LaunchedEffect(allLogs) {
        val validIds = allLogs.map { it.id }.toSet()
        selectedLogIds = selectedLogIds.intersect(validIds)
    }

    LaunchedEffect(Unit) {
        viewModel.refreshAccessibilityStatus(context)
    }

    // Group logs by date (dd/MM/yyyy)
    val groupedLogs = remember(allLogs) {
        allLogs.groupBy { DateTimeUtils.formatDateOnly(it.timestamp) }
    }

    val isAllSelected = allLogs.isNotEmpty() && selectedLogIds.size == allLogs.size
    val hasSelection = selectedLogIds.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Accessibility Service Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isServiceRunning) Color(0xFFE8F5E9) else Color(0xFFFFF3CD)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isServiceRunning) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isServiceRunning) StatusInGreen else Color(0xFF856404),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isServiceRunning) "Giám sát Tidit: ĐANG HOẠT ĐỘNG" else "Chưa bật quyền giám sát ngầm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isServiceRunning) StatusInGreen else Color(0xFF856404)
                        )
                        Text(
                            text = if (isServiceRunning)
                                "Tự động ghi lại thời gian mỗi khi bạn mở/quẹt Tidit"
                            else
                                "Cần cấp quyền Trợ năng (Accessibility) để theo dõi app Tidit",
                            fontSize = 12.sp,
                            color = if (isServiceRunning) StatusInGreen.copy(alpha = 0.8f) else Color(0xFF856404)
                        )
                    }
                }

                if (!isServiceRunning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.openAccessibilitySettings(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Bật quyền giám sát trong Cài đặt")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Package info & edit button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ứng dụng theo dõi: $targetPackage",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            IconButton(
                onClick = { showPackageDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Đổi package",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Số lần quẹt hôm nay", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(todayCount.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Lần quẹt gần nhất", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = latestLog?.let { DateTimeUtils.formatTimeOnly(it.timestamp) } ?: "--:--",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryTeal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Lịch sử (${allLogs.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (hasSelection) {
                    Text(
                        text = "Đã chọn: ${selectedLogIds.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (allLogs.isNotEmpty()) {
                // Select All / Deselect All Button
                IconButton(
                    onClick = {
                        selectedLogIds = if (isAllSelected) {
                            emptySet()
                        } else {
                            allLogs.map { it.id }.toSet()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isAllSelected) Icons.Default.Deselect else Icons.Default.SelectAll,
                        contentDescription = if (isAllSelected) "Bỏ chọn tất cả" else "Chọn tất cả",
                        tint = if (hasSelection) PrimaryBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Export CSV
            IconButton(
                onClick = { viewModel.exportLogsToCsv() }
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Xuất Excel / CSV",
                    tint = SecondaryTeal
                )
            }

            // Delete Button (Requires selection)
            IconButton(
                onClick = {
                    if (hasSelection) {
                        showDeleteSelectedDialog = true
                    } else {
                        Toast.makeText(context, "Vui lòng tích chọn mục cần xóa!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa mục đã chọn",
                    tint = if (hasSelection) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Log list grouped by Date
        if (allLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chưa có lượt quẹt nào được ghi",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Mở app Tidit để tự động ghi lại thời gian quẹt thẻ",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedLogs.forEach { (dateStr, logsInDate) ->
                    val allInDateSelected = logsInDate.all { selectedLogIds.contains(it.id) }

                    // Date Header
                    item(key = "header_$dateStr") {
                        DateGroupHeader(
                            dateTitle = DateTimeUtils.getDayTitle(dateStr, logsInDate.first().timestamp),
                            count = logsInDate.size,
                            isAllSelected = allInDateSelected,
                            onToggleSelectAllInDate = {
                                val dateIds = logsInDate.map { it.id }.toSet()
                                selectedLogIds = if (allInDateSelected) {
                                    selectedLogIds - dateIds
                                } else {
                                    selectedLogIds + dateIds
                                }
                            }
                        )
                    }

                    // Items for this Date
                    items(logsInDate, key = { it.id }) { log ->
                        val isSelected = selectedLogIds.contains(log.id)
                        LogItemCard(
                            log = log,
                            isSelected = isSelected,
                            onToggleSelect = {
                                selectedLogIds = if (isSelected) {
                                    selectedLogIds - log.id
                                } else {
                                    selectedLogIds + log.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog for Deleting Selected Items
    if (showDeleteSelectedDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSelectedDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa ${selectedLogIds.size} lượt quẹt đã tích chọn không?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLogsByIds(selectedLogIds)
                        selectedLogIds = emptySet()
                        showDeleteSelectedDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa (${selectedLogIds.size})")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSelectedDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Target Package Configuration Dialog
    if (showPackageDialog) {
        var inputPkg by remember { mutableStateOf(targetPackage) }
        AlertDialog(
            onDismissRequest = { showPackageDialog = false },
            title = { Text("Cấu hình Package Tidit") },
            text = {
                Column {
                    Text("Nhập Package name của app Tidit trên điện thoại của bạn:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputPkg,
                        onValueChange = { inputPkg = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mặc định: com.lgcns.didid (App Tidit chính thức)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTargetPackage(inputPkg)
                        showPackageDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPackageDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun DateGroupHeader(
    dateTitle: String,
    count: Int,
    isAllSelected: Boolean,
    onToggleSelectAllInDate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dateTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = PrimaryBlue.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "$count lượt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBlue,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onToggleSelectAllInDate,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (isAllSelected) "Bỏ chọn ngày" else "Chọn cả ngày",
                    fontSize = 11.sp,
                    color = PrimaryBlue
                )
            }
        }
    }
}

@Composable
fun LogItemCard(
    log: AccessLog,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(1.5.dp, PrimaryBlue) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for selection
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            )

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = DateTimeUtils.formatTimeOnly(log.timestamp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.eventSource,
                        fontSize = 12.sp,
                        color = SecondaryTeal,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (log.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.note,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}
