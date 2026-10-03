package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ClubRiskSignalItem(
    val id: String,
    val category: String, // Workload, Registration, Contract, Performance
    val title: String,
    val targetSubject: String,
    val severity: String, // High, Medium, Low
    val message: String,
    val status: String = "Active" // Active, Acknowledged, Resolved
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubRiskAndAlertsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String,
    onNavigateTab: (String) -> Unit = {}
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    val filters = listOf("All", "High", "Workload", "Licenses", "Contracts")

    var riskSignals by remember {
        mutableStateOf(
            listOf(
                ClubRiskSignalItem(
                    id = "risk_1",
                    category = "Workload",
                    title = "Workload Spike",
                    targetSubject = "Brian Ochieng (U20)",
                    severity = "High",
                    message = "ACWR ratio is 1.48 (>1.3 threshold). Reduce training load."
                ),
                ClubRiskSignalItem(
                    id = "risk_2",
                    category = "Licenses",
                    title = "Matchday Clearance Pending",
                    targetSubject = "Kevin Otieno (CB)",
                    severity = "High",
                    message = "Federation clearance document pending signature."
                ),
                ClubRiskSignalItem(
                    id = "risk_3",
                    category = "Contracts",
                    title = "Contract Expiring Soon",
                    targetSubject = "Coach Francis Kimanzi",
                    severity = "Medium",
                    message = "Contract term ends on 2026-10-31."
                ),
                ClubRiskSignalItem(
                    id = "risk_4",
                    category = "Performance",
                    title = "Defensive Transition Drop",
                    targetSubject = "Senior Team 1st XI",
                    severity = "Medium",
                    message = "Second ball recovery rate dropped 24% after 65 mins."
                )
            )
        )
    }

    val activeSignals = riskSignals.filter { it.status != "Resolved" }

    val filteredList = remember(selectedFilter, activeSignals) {
        when (selectedFilter) {
            "High" -> activeSignals.filter { it.severity == "High" }
            "Workload" -> activeSignals.filter { it.category == "Workload" }
            "Licenses" -> activeSignals.filter { it.category == "Licenses" }
            "Contracts" -> activeSignals.filter { it.category == "Contracts" }
            else -> activeSignals
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Club Alerts & Risks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "${activeSignals.size} active operational alerts requiring review",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeSignals.isNotEmpty()) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = if (activeSignals.isNotEmpty()) "${activeSignals.size} Active" else "All Clear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeSignals.isNotEmpty()) dangerColor else successColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Simple Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.forEach { f ->
                    val isSelected = selectedFilter == f
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) primaryColor else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedFilter = f }
                    ) {
                        Text(
                            text = f,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else textMuted,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Alert Items
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = successColor, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All alerts cleared.", fontSize = 13.sp, color = textMuted, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { signal ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, if (signal.severity == "High") Color(0xFFFECACA) else borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (signal.severity == "High") Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (signal.severity == "High") Icons.Default.Warning else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (signal.severity == "High") dangerColor else warningColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(signal.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("${signal.category} • ${signal.targetSubject}", fontSize = 11.sp, color = textMuted)
                            }
                        }

                        Button(
                            onClick = {
                                riskSignals = riskSignals.map { if (it.id == signal.id) it.copy(status = "Resolved") else it }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Resolve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
