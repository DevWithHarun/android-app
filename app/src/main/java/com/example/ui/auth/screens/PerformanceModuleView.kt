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
import com.example.data.MatchLogEntity
import com.example.data.PhysicalMeasurementEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import com.example.util.PerformancePdfGenerator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

@Composable
fun PerformanceModuleView(
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

    var selectedFilter by remember { mutableStateOf("All Matches") }
    var showLogMatchModal by remember { mutableStateOf(false) }
    var showAddPhysicalModal by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<MatchLogEntity?>(null) }
    var matchToDelete by remember { mutableStateOf<MatchLogEntity?>(null) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var showPdfSuccessModal by remember { mutableStateOf(false) }

    // Aggregate Real Performance Statistics
    val totalMatches = uiState.matchLogs.size
    val totalMinutes = uiState.matchLogs.sumOf { it.minutesPlayed }
    val totalGoals = uiState.matchLogs.sumOf { it.goals }
    val totalAssists = uiState.matchLogs.sumOf { it.assists }
    val avgRating = if (uiState.matchLogs.isNotEmpty()) {
        String.format("%.1f", uiState.matchLogs.map { it.matchRating }.average())
    } else {
        "-"
    }
    val goalContributionPer90 = if (totalMinutes > 0) {
        String.format("%.2f", (totalGoals + totalAssists).toDouble() * 90.0 / totalMinutes.toDouble())
    } else {
        "0.00"
    }

    val filteredMatches = remember(selectedFilter, uiState.matchLogs) {
        when (selectedFilter) {
            "Top Rated (8.0+)" -> uiState.matchLogs.filter { it.matchRating >= 8 }
            "Goals & Assists" -> uiState.matchLogs.filter { it.goals > 0 || it.assists > 0 }
            else -> uiState.matchLogs
        }
    }

    // Workload calculation from real matches
    val recentSevenDayMinutes = remember(uiState.matchLogs) {
        val weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        uiState.matchLogs.filter { it.timestamp >= weekAgo }.sumOf { it.minutesPlayed }
    }
    val recentTwentyEightDayMinutes = remember(uiState.matchLogs) {
        val monthAgo = System.currentTimeMillis() - (28L * 24 * 60 * 60 * 1000)
        uiState.matchLogs.filter { it.timestamp >= monthAgo }.sumOf { it.minutesPlayed }
    }
    val acwrRatio = remember(recentSevenDayMinutes, recentTwentyEightDayMinutes) {
        val weeklyAvgChronic = (recentTwentyEightDayMinutes.toDouble() / 4.0).coerceAtLeast(1.0)
        recentSevenDayMinutes.toDouble() / weeklyAvgChronic
    }

    val onExportPdf: () -> Unit = {
        try {
            val pdf = PerformancePdfGenerator.generatePerformancePdf(
                context = context,
                athleteName = uiState.userName.ifBlank { "Athlete" },
                matches = uiState.matchLogs,
                measurements = uiState.physicalMeasurements,
                acwrRatio = acwrRatio,
                totalMinutes = totalMinutes,
                totalGoals = totalGoals,
                totalAssists = totalAssists,
                avgRating = avgRating
            )
            generatedPdfFile = pdf
            showPdfSuccessModal = true
            val dateStamp = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date())
            viewModel.addDocument(
                title = "Performance Dossier ($dateStamp)",
                category = "Performance",
                state = "Verified",
                issueDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                expiryDate = "Permanent",
                fileType = "PDF",
                fileSize = "${(pdf.length() / 1024).coerceAtLeast(1)} KB",
                issuingAuthority = "Talent Graph Official"
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
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
        // Hero Card: Overall Performance Overview
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
                                color = if (totalMatches > 0) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (totalMatches > 0) "OFFICIAL MATCH INTELLIGENCE" else "NO MATCHES LOGGED",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalMatches > 0) Color(0xFF047857) else textMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Performance Analytics",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Verified match fixtures, goal involvements, coach evaluations, and fatigue load.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Performance KPIs Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PerfStatItem(value = "$totalMatches", label = "Matches")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        PerfStatItem(value = "${totalMinutes}m", label = "Minutes")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        PerfStatItem(value = "$totalGoals G / $totalAssists A", label = "Involvements")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        PerfStatItem(value = avgRating, label = "Avg Rating")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action: Log Match Performance & Export PDF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showLogMatchModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Addchart, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Match", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showAddPhysicalModal = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).height(44.dp)
                        ) {
                            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Metric", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onExportPdf,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Recent Form Guide & Match Momentum
        if (uiState.matchLogs.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                                Text("Recent Form Guide (Last 5 Fixtures)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                            }
                            Text("G/A per 90: $goalContributionPer90", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.matchLogs.take(5).forEach { match ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when {
                                        match.matchRating >= 8 -> Color(0xFFECFDF5)
                                        match.matchRating >= 6 -> Color(0xFFF0FDF4)
                                        else -> Color(0xFFFFFBEB)
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        when {
                                            match.matchRating >= 8 -> Color(0xFF10B981)
                                            match.matchRating >= 6 -> Color(0xFF0D9488)
                                            else -> Color(0xFFF59E0B)
                                        }
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "⭐ ${match.matchRating}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                match.matchRating >= 8 -> Color(0xFF047857)
                                                match.matchRating >= 6 -> Color(0xFF0D9488)
                                                else -> Color(0xFFB45309)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${match.minutesPlayed}m",
                                            fontSize = 10.sp,
                                            color = textMuted,
                                            fontWeight = FontWeight.Medium
                                        )
                                        if (match.goals > 0 || match.assists > 0) {
                                            Text(
                                                text = "${match.goals}G ${match.assists}A",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF047857)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Athlete Metrics Trend Line Chart (Recharts Visualization: Speed, Endurance, Strength over time)
        if (selectedFilter == "All Matches" || selectedFilter == "Metric Trends" || selectedFilter == "Physical Profile") {
            item {
                RechartsPerformanceTrendCard(
                    measurements = uiState.physicalMeasurements,
                    onAddMetricClick = { showAddPhysicalModal = true }
                )
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("All Matches", "Metric Trends", "Top Rated (8.0+)", "Goals & Assists", "Workload & ACWR", "Physical Profile")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else primaryColor
                        )
                    }
                }
            }
        }

        // Section: Match Logs
        if (selectedFilter != "Workload & ACWR" && selectedFilter != "Physical Profile" && selectedFilter != "Metric Trends") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MATCH FIXTURE LOGS (${filteredMatches.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { showLogMatchModal = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Match", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (filteredMatches.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.SportsSoccer, contentDescription = null, tint = textMuted, modifier = Modifier.size(42.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No match records found", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 14.sp)
                            Text("Log league matches, tournament fixtures, or friendly games to track verified stats.", color = textMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showLogMatchModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ Log Your First Match", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredMatches, key = { it.id }) { match ->
                    MatchLogCard(
                        match = match,
                        onEdit = { matchToEdit = match },
                        onDelete = { matchToDelete = match },
                        onShare = {
                            shareMatchPerformance(context, match)
                        }
                    )
                }
            }
        }

        // Section: Workload & ACWR (Acute:Chronic Workload Ratio) Monitor
        if (selectedFilter == "All Matches" || selectedFilter == "Workload & ACWR") {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "WORKLOAD, FATIGUE & RECOVERY RADAR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                            Text("Acute to Chronic Workload Ratio (ACWR)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Calculated Ratio", fontSize = 11.sp, color = textMuted)
                                Text(
                                    text = String.format("%.2f", acwrRatio),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when {
                                        acwrRatio in 0.8..1.3 -> Color(0xFF047857)
                                        acwrRatio > 1.5 -> Color(0xFFDC2626)
                                        else -> Color(0xFFD97706)
                                    }
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    acwrRatio in 0.8..1.3 -> Color(0xFFECFDF5)
                                    acwrRatio > 1.5 -> Color(0xFFFEE2E2)
                                    else -> Color(0xFFFEF3C7)
                                }
                            ) {
                                Text(
                                    text = when {
                                        acwrRatio in 0.8..1.3 -> "OPTIMAL LOAD (LOW RISK)"
                                        acwrRatio > 1.5 -> "HIGH FATIGUE / OVERUSE RISK"
                                        else -> "SUBLIMINAL LOAD"
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        acwrRatio in 0.8..1.3 -> Color(0xFF047857)
                                        acwrRatio > 1.5 -> Color(0xFFDC2626)
                                        else -> Color(0xFFB45309)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        PerfDetailRow("7-Day Acute Match Load", "$recentSevenDayMinutes minutes")
                        PerfDetailRow("28-Day Chronic Rolling Total", "$recentTwentyEightDayMinutes minutes")
                        PerfDetailRow("Injury Prevention Status", if (acwrRatio <= 1.3) "Cleared for Full Match Play" else "Recommended Rotation / Recovery")
                    }
                }
            }
        }

        // Section: Physical Profile & Athletic Benchmarks
        if (selectedFilter == "All Matches" || selectedFilter == "Physical Profile") {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PHYSICAL ATTRIBUTES & ATHLETIC TESTING (${uiState.physicalMeasurements.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { showAddPhysicalModal = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Metric", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.physicalMeasurements.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No physical test results recorded yet.", color = textMuted, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showAddPhysicalModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Record Physical Test", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(uiState.physicalMeasurements, key = { it.id }) { measurement ->
                    PhysicalMeasurementCard(measurement)
                }
            }
        }

        // Export Official PDF Dossier Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text(
                                text = "Export Official PDF Dossier",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "Certified A4 dossier with verified match ratings, fatigue ACWR index, and athletic benchmarks.",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }
                    }

                    Button(
                        onClick = onExportPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal: Log Match Performance
    if (showLogMatchModal) {
        LogMatchDialog(
            onDismiss = { showLogMatchModal = false },
            onSave = { title, mins, goals, assists, rating, coach ->
                viewModel.addMatchLog(0L, title, mins, goals, assists, rating, coach)
                showLogMatchModal = false
            }
        )
    }

    // Modal: Edit Match Performance
    if (matchToEdit != null) {
        EditMatchDialog(
            match = matchToEdit!!,
            onDismiss = { matchToEdit = null },
            onSave = { updatedMatch: MatchLogEntity ->
                viewModel.updateMatchLog(updatedMatch)
                matchToEdit = null
            }
        )
    }

    // Modal: Add Physical Metric
    if (showAddPhysicalModal) {
        AddPhysicalMetricDialog(
            onDismiss = { showAddPhysicalModal = false },
            onSave = { testType, value, unit, date, verification ->
                viewModel.addPhysicalMeasurement(testType, value, unit, date, verification)
                showAddPhysicalModal = false
            }
        )
    }

    // Modal: Confirm Delete Match
    if (matchToDelete != null) {
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text("Delete Match Record?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${matchToDelete?.matchTitle}' from your performance history?") },
            confirmButton = {
                Button(
                    onClick = {
                        matchToDelete?.let { viewModel.deleteMatchLog(it) }
                        matchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: PDF Generated Success Dialog
    if (showPdfSuccessModal && generatedPdfFile != null) {
        PerformancePdfSuccessDialog(
            pdfFile = generatedPdfFile!!,
            onDismiss = { showPdfSuccessModal = false },
            onShare = { PerformancePdfGenerator.sharePerformancePdf(context, generatedPdfFile!!) },
            onView = { PerformancePdfGenerator.viewPerformancePdf(context, generatedPdfFile!!) }
        )
    }
}

@Composable
fun PerformancePdfSuccessDialog(
    pdfFile: File,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onView: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Performance Report Generated",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TalentGraph_Performance_Report.pdf",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color(0xFFDC2626)
                )
                Text(
                    text = "A4 certified dossier • ${(pdfFile.length() / 1024).coerceAtLeast(1)} KB\nAutomatically recorded to your Documents Vault.",
                    fontSize = 11.sp,
                    color = textMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onView,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Done", color = textMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PerfStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MatchLogCard(
    match: MatchLogEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val dateFormatted = remember(match.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(match.timestamp))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Rating Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.matchTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dateFormatted • Verified by ${match.verifiedByCoach.ifBlank { "Team Coach" }}",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        match.matchRating >= 8 -> Color(0xFFECFDF5)
                        match.matchRating >= 6 -> Color(0xFFF0FDF4)
                        else -> Color(0xFFFFFBEB)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            match.matchRating >= 8 -> Color(0xFF10B981)
                            match.matchRating >= 6 -> Color(0xFF0D9488)
                            else -> Color(0xFFF59E0B)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(13.dp))
                        Text(
                            text = "${match.matchRating} / 10",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏱️ ${match.minutesPlayed} mins", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                Text("⚽ ${match.goals} Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                Text("🎯 ${match.assists} Assists", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                    Text("Official Match Sheet", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Medium)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PhysicalMeasurementCard(measurement: PhysicalMeasurementEntity) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Speed, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(measurement.testType, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                Text("Tested: ${measurement.testDate} • ${measurement.verificationStatus}", fontSize = 12.sp, color = textMuted)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFECFDF5)
            ) {
                Text(
                    text = "${measurement.value} ${measurement.unit}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857)
                )
            }
        }
    }
}

@Composable
fun PerfDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
    }
}

// Dialog: Log Match
@Composable
fun LogMatchDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, mins: Int, goals: Int, assists: Int, rating: Int, coach: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("90") }
    var goals by remember { mutableStateOf("0") }
    var assists by remember { mutableStateOf("0") }
    var rating by remember { mutableStateOf("8") }
    var coach by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Log Match Performance", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Match Fixture / Opponent") },
                    placeholder = { Text("e.g. vs Coastal FC (League Round 14)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it },
                        label = { Text("Minutes Played") },
                        placeholder = { Text("90") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { rating = it },
                        label = { Text("Rating (1-10)") },
                        placeholder = { Text("8") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goals,
                        onValueChange = { goals = it },
                        label = { Text("Goals") },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = assists,
                        onValueChange = { assists = it },
                        label = { Text("Assists") },
                        placeholder = { Text("0") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = coach,
                    onValueChange = { coach = it },
                    label = { Text("Verified by Coach / Referee") },
                    placeholder = { Text("e.g. Coach Hassan") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    title,
                                    minutes.toIntOrNull() ?: 90,
                                    goals.toIntOrNull() ?: 0,
                                    assists.toIntOrNull() ?: 0,
                                    (rating.toIntOrNull() ?: 7).coerceIn(1, 10),
                                    coach.ifBlank { "Team Head Coach" }
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Match", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: Edit Match
@Composable
fun EditMatchDialog(
    match: MatchLogEntity,
    onDismiss: () -> Unit,
    onSave: (MatchLogEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf(match.matchTitle) }
    var minutes by remember { mutableStateOf(match.minutesPlayed.toString()) }
    var goals by remember { mutableStateOf(match.goals.toString()) }
    var assists by remember { mutableStateOf(match.assists.toString()) }
    var rating by remember { mutableStateOf(match.matchRating.toString()) }
    var coach by remember { mutableStateOf(match.verifiedByCoach) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Match Record", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Match Fixture") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { rating = it },
                        label = { Text("Rating (1-10)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = goals,
                        onValueChange = { goals = it },
                        label = { Text("Goals") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = assists,
                        onValueChange = { assists = it },
                        label = { Text("Assists") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = coach,
                    onValueChange = { coach = it },
                    label = { Text("Coach / Verifier") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    match.copy(
                                        matchTitle = title,
                                        minutesPlayed = minutes.toIntOrNull() ?: match.minutesPlayed,
                                        goals = goals.toIntOrNull() ?: match.goals,
                                        assists = assists.toIntOrNull() ?: match.assists,
                                        matchRating = (rating.toIntOrNull() ?: match.matchRating).coerceIn(1, 10),
                                        verifiedByCoach = coach
                                    )
                                )
                            }
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

// Dialog: Add Physical Metric
@Composable
fun AddPhysicalMetricDialog(
    onDismiss: () -> Unit,
    onSave: (testType: String, value: String, unit: String, date: String, verification: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var testType by remember { mutableStateOf("30m Sprint") }
    var value by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("s") }
    var date by remember { mutableStateOf("2024") }
    var verification by remember { mutableStateOf("Coach Verified") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Record Physical Test Metric", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                Text("Test Protocol", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("30m Sprint", "Yo-Yo IR1", "Vertical Jump", "Agility T-Test").forEach { type ->
                        val isSel = testType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f).clickable {
                                testType = type
                                unit = when (type) {
                                    "30m Sprint" -> "s"
                                    "Yo-Yo IR1" -> "m"
                                    "Vertical Jump" -> "cm"
                                    else -> "s"
                                }
                            }
                        ) {
                            Text(
                                text = type,
                                modifier = Modifier.padding(vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Score / Value") },
                        placeholder = { Text("e.g. 3.92") },
                        modifier = Modifier.weight(1.5f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Test Date") },
                        placeholder = { Text("e.g. Sep 2024") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = verification,
                        onValueChange = { verification = it },
                        label = { Text("Verifier") },
                        placeholder = { Text("Club Physio") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (value.isNotBlank()) {
                                onSave(testType, value, unit, date, verification)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Metric", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Share Match Performance Report via Android Native Intent
fun shareMatchPerformance(context: Context, match: MatchLogEntity) {
    val text = """
        TALENT GRAPH • OFFICIAL MATCH PERFORMANCE REPORT
        Fixture: ${match.matchTitle}
        Minutes: ${match.minutesPlayed} mins
        Goals: ${match.goals}
        Assists: ${match.assists}
        Match Rating: ${match.matchRating} / 10
        Verified by: ${match.verifiedByCoach}
        
        Verified through Talent Graph Sports Intelligence Platform.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Match Report: ${match.matchTitle}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Match Performance"))
}
