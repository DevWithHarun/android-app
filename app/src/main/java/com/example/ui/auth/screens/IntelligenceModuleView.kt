package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun IntelligenceModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState,
    onNavigateTab: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)
    val purpleColor = Color(0xFFA855F7)

    var selectedSubTab by remember { mutableStateOf("Overview & Radar") }
    var showCheckInDialog by remember { mutableStateOf(false) }

    // Subjective check-in state
    var sleepRating by remember { mutableFloatStateOf(8f) }
    var sorenessRating by remember { mutableFloatStateOf(3f) }
    var stressRating by remember { mutableFloatStateOf(2f) }
    var energyRating by remember { mutableFloatStateOf(9f) }
    var lastCheckInSubmitted by remember { mutableStateOf(false) }

    // Dynamic readiness score calculation
    val latestStatus = uiState.availabilityRecords.firstOrNull()
    val isAvailable = latestStatus == null || latestStatus.status.contains("Available", ignoreCase = true) || latestStatus.status.contains("Fit", ignoreCase = true)

    val weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
    val monthAgo = System.currentTimeMillis() - (28L * 24 * 60 * 60 * 1000)
    val realSevenDayMinutes = remember(uiState.matchLogs) {
        uiState.matchLogs.filter { it.timestamp >= weekAgo }.sumOf { it.minutesPlayed }
    }
    val realTwentyEightDayMinutes = remember(uiState.matchLogs) {
        uiState.matchLogs.filter { it.timestamp >= monthAgo }.sumOf { it.minutesPlayed }
    }
    val latestWorkload = uiState.workloadRecords.firstOrNull()
    val displaySevenDay = latestWorkload?.sevenDayLoad ?: realSevenDayMinutes
    val displayTwentyEightDay = latestWorkload?.twentyEightDayLoad ?: realTwentyEightDayMinutes

    val acwrRatio = remember(displaySevenDay, displayTwentyEightDay) {
        val chronicWeekly = (displayTwentyEightDay.toDouble() / 4.0).coerceAtLeast(1.0)
        displaySevenDay.toDouble() / chronicWeekly
    }

    val readinessScore = remember(isAvailable, acwrRatio, sleepRating, sorenessRating, energyRating) {
        if (!isAvailable) {
            45
        } else {
            val base = 85
            val acwrPenalty = if (acwrRatio > 1.5) -20 else if (acwrRatio < 0.8) -5 else 5
            val subjectiveBonus = ((sleepRating + energyRating - sorenessRating) * 1.5).toInt()
            (base + acwrPenalty + subjectiveBonus).coerceIn(40, 98)
        }
    }

    val readinessStatus = when {
        readinessScore >= 85 -> "PEAK MATCH CONDITION"
        readinessScore >= 70 -> "OPTIMAL READINESS"
        readinessScore >= 55 -> "MODERATE FATIGUE ACCUMULATION"
        else -> "ELEVATED INJURY RISK / REST"
    }

    val readinessColor = when {
        readinessScore >= 85 -> Color(0xFF059669)
        readinessScore >= 70 -> Color(0xFF0D9488)
        readinessScore >= 55 -> Color(0xFFF59E0B)
        else -> Color(0xFFDC2626)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Executive Readiness & Intelligence Overview
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
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
                                color = readinessColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = readinessStatus,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = readinessColor
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Athlete Intelligence Hub",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Comprehensive physical readiness, availability audits, and workload telemetry.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Readiness Score Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(readinessColor.copy(alpha = 0.1f), CircleShape)
                                .border(2.dp, readinessColor.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$readinessScore%",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = readinessColor
                                )
                                Text(
                                    text = "INDEX",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Readiness telemetry strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IntelligenceStatItem(
                            label = "Selection Status",
                            value = if (isAvailable) "Cleared" else "Restricted",
                            textColor = if (isAvailable) Color(0xFF059669) else Color(0xFFDC2626)
                        )
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        IntelligenceStatItem(
                            label = "ACWR Ratio",
                            value = String.format("%.2f", acwrRatio),
                            textColor = if (acwrRatio in 0.8..1.3) Color(0xFF059669) else Color(0xFFF59E0B)
                        )
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        IntelligenceStatItem(
                            label = "Weekly Load",
                            value = "${displaySevenDay} AU",
                            textColor = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showCheckInDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).height(44.dp)
                        ) {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (lastCheckInSubmitted) "Update Check-In" else "Daily Readiness Check-In",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val shareText = "Athlete Intelligence Report:\nReadiness Index: $readinessScore%\nStatus: ${latestStatus?.status ?: "Available"}\nACWR Ratio: ${String.format("%.2f", acwrRatio)}\n7-Day Exertion: ${displaySevenDay} AU"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Intelligence Summary"))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.8f).height(44.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Sub-Navigation Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val subTabs = listOf("Overview & Radar", "Availability Hub", "Workload & ACWR")
                items(subTabs) { tab ->
                    val isSelected = selectedSubTab == tab
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedSubTab = tab }
                    ) {
                        Text(
                            text = tab,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else primaryColor
                        )
                    }
                }
            }
        }

        // Independent Module Cards & Navigation
        when (selectedSubTab) {
            "Overview & Radar" -> {
                // Feature 1: Availability & Medical Clearance Independent Portal Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(if (isAvailable) Color(0xFFECFDF5) else Color(0xFFFEE2E2), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.EventAvailable,
                                            contentDescription = null,
                                            tint = if (isAvailable) Color(0xFF047857) else Color(0xFFDC2626),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Availability & Medical Clearance",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "${uiState.availabilityRecords.size} recorded audit entries",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isAvailable) Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                                ) {
                                    Text(
                                        text = if (isAvailable) "CLEARED" else "RESTRICTED",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAvailable) Color(0xFF047857) else Color(0xFFDC2626)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Current Status: ${latestStatus?.status ?: "Available for Selection"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                            Text(
                                text = "Expected Return: ${latestStatus?.expectedReturnDate ?: "Immediate"} • ${latestStatus?.reason ?: "Passed all physiological benchmarks"}",
                                fontSize = 12.sp,
                                color = textMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("availability") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Open Dedicated Availability Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Feature 2: Workload & ACWR Telemetry Independent Portal Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(purpleColor.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.BatteryChargingFull,
                                            contentDescription = null,
                                            tint = purpleColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Workload & ACWR Monitor",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "Acute:Chronic Exertion Ratio: ${String.format("%.2f", acwrRatio)}",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (acwrRatio in 0.8..1.3) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = if (acwrRatio in 0.8..1.3) "SWEET SPOT" else "CAUTION",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (acwrRatio in 0.8..1.3) Color(0xFF047857) else Color(0xFFB45309)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Training Capacity Recommendation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = latestWorkload?.recommendation ?: if (acwrRatio in 0.8..1.3) "Safe to maintain high-intensity conditioning and full competitive match fixtures." else "Schedule active recovery session to mitigate tendon fatigue.",
                                fontSize = 12.sp,
                                color = textMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("workload") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Open Dedicated Workload & ACWR Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Feature 3: Injury Prevention & Musculoskeletal Risk Matrix
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PREVENTIVE BIOMECHANICAL RISK AUDIT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textMuted,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            RiskMetricRow(label = "Hamstring & Posterior Chain", status = "Low Risk (96% Symmetry)", isOk = true)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                            RiskMetricRow(label = "Adductor / Groin Strain Index", status = "Optimal (Load Balance)", isOk = true)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                            RiskMetricRow(label = "Patellar Tendon Exertion", status = if (acwrRatio > 1.4) "Moderate Stress" else "Low Risk", isOk = acwrRatio <= 1.4)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))
                            RiskMetricRow(label = "Rest & Neuromuscular Recovery", status = if (sleepRating >= 7f) "Well Rested (${sleepRating.toInt()}/10)" else "Sub-Optimal Sleep", isOk = sleepRating >= 7f)
                        }
                    }
                }
            }

            "Availability Hub" -> {
                // Direct access to Availability component
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Availability Management Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "Track medical clearances, injury rehabilitation, return-to-play stages, and status sharing.",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("availability") },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Fullscreen Availability Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "Workload & ACWR" -> {
                // Direct access to Workload component
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Workload & Fatigue Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "Acute:Chronic Workload Ratio (ACWR), session RPE logging, match minutes derived load, and fatigue warnings.",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("workload") },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Fullscreen Workload & ACWR Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Daily Readiness Check-In
    if (showCheckInDialog) {
        Dialog(onDismissRequest = { showCheckInDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Readiness Check-In",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        IconButton(onClick = { showCheckInDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                        }
                    }
                    Text(
                        text = "Subjective neuromuscular ratings calibrate sports science readiness.",
                        fontSize = 12.sp,
                        color = textMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Sleep Quality
                    ReadinessSlider(
                        label = "Sleep Quality & Restfulness",
                        value = sleepRating,
                        onValueChange = { sleepRating = it },
                        valueLabel = "${sleepRating.toInt()}/10"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slider 2: Muscle Soreness (Lower is better)
                    ReadinessSlider(
                        label = "Muscle Soreness / Tightness",
                        value = sorenessRating,
                        onValueChange = { sorenessRating = it },
                        valueLabel = "${sorenessRating.toInt()}/10 (Lower = Less Pain)"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slider 3: Mental Stress
                    ReadinessSlider(
                        label = "Mental Stress / Cognitive Focus",
                        value = stressRating,
                        onValueChange = { stressRating = it },
                        valueLabel = "${stressRating.toInt()}/10"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Slider 4: Energy Level
                    ReadinessSlider(
                        label = "Overall Energy & Motivation",
                        value = energyRating,
                        onValueChange = { energyRating = it },
                        valueLabel = "${energyRating.toInt()}/10"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            lastCheckInSubmitted = true
                            showCheckInDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save & Calculate Readiness Index", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun IntelligenceStatItem(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RiskMetricRow(label: String, status: String, isOk: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isOk) Color(0xFF10B981) else Color(0xFFF59E0B), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOk) Color(0xFF047857) else Color(0xFFB45309)
            )
        }
    }
}

@Composable
fun ReadinessSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
            Text(text = valueLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF0D9488),
                activeTrackColor = Color(0xFF0D9488),
                inactiveTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}
