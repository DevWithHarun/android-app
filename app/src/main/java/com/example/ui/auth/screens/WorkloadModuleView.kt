package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.WorkloadEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.util.*

@Composable
fun WorkloadModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var showLogDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<WorkloadEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<WorkloadEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("All Logs") }

    // Dynamic real workload statistics derived from match logs & recorded workload
    val totalMatchMinutes = remember(uiState.matchLogs) { uiState.matchLogs.sumOf { it.minutesPlayed } }
    val weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
    val monthAgo = System.currentTimeMillis() - (28L * 24 * 60 * 60 * 1000)

    val realSevenDayMinutes = remember(uiState.matchLogs) {
        uiState.matchLogs.filter { it.timestamp >= weekAgo }.sumOf { it.minutesPlayed }
    }
    val realTwentyEightDayMinutes = remember(uiState.matchLogs) {
        uiState.matchLogs.filter { it.timestamp >= monthAgo }.sumOf { it.minutesPlayed }
    }

    val latestLog = uiState.workloadRecords.firstOrNull()
    val displaySevenDay = if (latestLog != null) latestLog.sevenDayLoad else realSevenDayMinutes
    val displayTwentyEightDay = if (latestLog != null) latestLog.twentyEightDayLoad else realTwentyEightDayMinutes

    val acwrRatio = remember(displaySevenDay, displayTwentyEightDay) {
        val chronicWeekly = (displayTwentyEightDay.toDouble() / 4.0).coerceAtLeast(1.0)
        displaySevenDay.toDouble() / chronicWeekly
    }

    val isOptimal = acwrRatio in 0.8..1.3
    val isOveruse = acwrRatio > 1.5

    val filteredLogs = remember(selectedFilter, uiState.workloadRecords) {
        when (selectedFilter) {
            "Optimal" -> uiState.workloadRecords.filter { it.signalStatus.contains("Optimal", ignoreCase = true) }
            "Elevated / Hazard" -> uiState.workloadRecords.filter { it.signalStatus.contains("Fatigue", ignoreCase = true) || it.signalStatus.contains("High", ignoreCase = true) }
            else -> uiState.workloadRecords
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Acute to Chronic Workload Ratio (ACWR) Intelligence
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, if (isOptimal) Color(0xFF10B981) else if (isOveruse) Color(0xFFEF4444) else borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when {
                                    isOptimal -> Color(0xFFECFDF5)
                                    isOveruse -> Color(0xFFFEE2E2)
                                    else -> Color(0xFFFEF3C7)
                                }
                            ) {
                                Text(
                                    text = when {
                                        isOptimal -> "ACWR IN OPTIMAL SWEET SPOT"
                                        isOveruse -> "HIGH INJURY HAZARD (OVERTRAINING)"
                                        else -> "SUBLIMINAL LOAD"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isOptimal -> Color(0xFF047857)
                                        isOveruse -> Color(0xFFDC2626)
                                        else -> Color(0xFFB45309)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Workload & Fatigue Intelligence",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Sports science tracking of acute exertion load vs rolling chronic capacity.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFA855F7).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WorkloadStatItem(value = String.format("%.2f", acwrRatio), label = "ACWR Ratio")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        WorkloadStatItem(value = "${displaySevenDay} AU", label = "7-Day Acute")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        WorkloadStatItem(value = "${displayTwentyEightDay} AU", label = "28-Day Chronic")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        WorkloadStatItem(value = "${totalMatchMinutes}m", label = "Match Play")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recommendation Banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Text(
                                text = latestLog?.recommendation ?: if (isOptimal) "Cleared for full training intensity and 90-minute match play." else "Implement active recovery and monitor hamstring/groin tightness.",
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Action Button
                    Button(
                        onClick = { showLogDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Session Workload (RPE)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECORDED EXERTION AUDIT LOGS (${filteredLogs.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showLogDialog = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Entry", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No customized workload logs saved", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
                        Text("Default metrics are being calculated directly from your match logs. Record custom RPE logs for deeper sports science insights.", color = textMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showLogDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Record Workload Entry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                WorkloadItemCard(
                    log = log,
                    onEdit = { itemToEdit = log },
                    onDelete = { itemToDelete = log },
                    onShare = {
                        shareWorkloadReport(context, log)
                    }
                )
            }
        }
    }

    // Modal: Add Workload
    if (showLogDialog) {
        LogWorkloadDialog(
            onDismiss = { showLogDialog = false },
            onSave = { seven, twentyEight, mins, signal, rec ->
                viewModel.addWorkload(seven, twentyEight, mins, signal, rec)
                showLogDialog = false
            }
        )
    }

    // Modal: Edit Workload
    if (itemToEdit != null) {
        EditWorkloadDialog(
            log = itemToEdit!!,
            onDismiss = { itemToEdit = null },
            onSave = { updated ->
                viewModel.updateWorkload(updated)
                itemToEdit = null
            }
        )
    }

    // Modal: Delete Confirmation
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Workload Entry?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this workload record?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteWorkload(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun WorkloadStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun WorkloadItemCard(
    log: WorkloadEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val ratio = log.sevenDayLoad.toDouble() / (log.twentyEightDayLoad.toDouble() / 4.0).coerceAtLeast(1.0)
    val isOptimal = ratio in 0.8..1.3

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Exertion: ${log.sevenDayLoad} AU Acute / ${log.twentyEightDayLoad} AU Chronic", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Match Play: ${log.matchMinutes} mins • ACWR: ${String.format("%.2f", ratio)}", fontSize = 12.sp, color = textMuted)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isOptimal) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = log.signalStatus.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOptimal) Color(0xFF047857) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recommendation: ${log.recommendation}",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 12.sp,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sports Science Workload Unit", fontSize = 11.sp, color = textMuted)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LogWorkloadDialog(
    onDismiss: () -> Unit,
    onSave: (seven: Int, twentyEight: Int, mins: Int, signal: String, rec: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var sevenDay by remember { mutableStateOf("180") }
    var twentyEightDay by remember { mutableStateOf("720") }
    var matchMins by remember { mutableStateOf("90") }
    var signalStatus by remember { mutableStateOf("Optimal") }
    var recommendation by remember { mutableStateOf("Continue normal load training") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Log Workload Record", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sevenDay,
                        onValueChange = { sevenDay = it },
                        label = { Text("7-Day Acute (AU)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = twentyEightDay,
                        onValueChange = { twentyEightDay = it },
                        label = { Text("28-Day Chronic (AU)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = matchMins,
                        onValueChange = { matchMins = it },
                        label = { Text("Match Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = signalStatus,
                        onValueChange = { signalStatus = it },
                        label = { Text("Signal Status") },
                        placeholder = { Text("Optimal / High") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = recommendation,
                    onValueChange = { recommendation = it },
                    label = { Text("Physio / Coach Recommendation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            onSave(
                                sevenDay.toIntOrNull() ?: 180,
                                twentyEightDay.toIntOrNull() ?: 720,
                                matchMins.toIntOrNull() ?: 90,
                                signalStatus,
                                recommendation
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditWorkloadDialog(
    log: WorkloadEntity,
    onDismiss: () -> Unit,
    onSave: (WorkloadEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var sevenDay by remember { mutableStateOf(log.sevenDayLoad.toString()) }
    var twentyEightDay by remember { mutableStateOf(log.twentyEightDayLoad.toString()) }
    var matchMins by remember { mutableStateOf(log.matchMinutes.toString()) }
    var signalStatus by remember { mutableStateOf(log.signalStatus) }
    var recommendation by remember { mutableStateOf(log.recommendation) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Workload Entry", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sevenDay,
                        onValueChange = { sevenDay = it },
                        label = { Text("7-Day Acute (AU)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = twentyEightDay,
                        onValueChange = { twentyEightDay = it },
                        label = { Text("28-Day Chronic (AU)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = matchMins,
                        onValueChange = { matchMins = it },
                        label = { Text("Match Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = signalStatus,
                        onValueChange = { signalStatus = it },
                        label = { Text("Signal Status") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = recommendation,
                    onValueChange = { recommendation = it },
                    label = { Text("Recommendation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            onSave(
                                log.copy(
                                    sevenDayLoad = sevenDay.toIntOrNull() ?: log.sevenDayLoad,
                                    twentyEightDayLoad = twentyEightDay.toIntOrNull() ?: log.twentyEightDayLoad,
                                    matchMinutes = matchMins.toIntOrNull() ?: log.matchMinutes,
                                    signalStatus = signalStatus,
                                    recommendation = recommendation
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun shareWorkloadReport(context: Context, log: WorkloadEntity) {
    val ratio = log.sevenDayLoad.toDouble() / (log.twentyEightDayLoad.toDouble() / 4.0).coerceAtLeast(1.0)
    val text = """
        TALENT GRAPH • SPORTS SCIENCE WORKLOAD DOSSIER
        Acute Load (7-Day): ${log.sevenDayLoad} AU
        Chronic Load (28-Day): ${log.twentyEightDayLoad} AU
        ACWR Ratio: ${String.format("%.2f", ratio)}
        Match Minutes: ${log.matchMinutes} mins
        Signal Status: ${log.signalStatus}
        Recommendation: ${log.recommendation}
        
        Certified by Talent Graph Athletic Intelligence Engine.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Workload Report: ACWR ${String.format("%.2f", ratio)}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Workload Report"))
}
