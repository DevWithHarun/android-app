package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ClubReportTemplate(
    val id: String,
    val title: String,
    val category: String, // Club, Performance, Development, Recruitment, Risk & Availability, Financial
    val description: String,
    val exportFormats: List<String> = listOf("PDF", "CSV", "Excel", "API")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubReportsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedCategory by rememberSaveable { mutableStateOf("All Reports") }
    val categories = listOf("All Reports", "Club & Activity", "Performance & Analytics", "Academy Development", "Recruitment & Trials", "Risk & Availability", "Financial & Protection")

    var selectedReportForExport by remember { mutableStateOf<ClubReportTemplate?>(null) }
    var exportFeedback by remember { mutableStateOf<String?>(null) }

    val reportTemplates = remember {
        listOf(
            ClubReportTemplate("rep_1", "Official Club Performance & Standing Audit", "Performance & Analytics", "Aggregated squad match ratings, win rates, and competition standings."),
            ClubReportTemplate("rep_2", "Talent Graph Academy Progression & Milestones", "Academy Development", "Quarterly individual development plan (IDP) milestones and promotion readiness index."),
            ClubReportTemplate("rep_3", "Scout Discovery & Recruitment Pipeline Report", "Recruitment & Trials", "Candidate discovery counts, scout evaluations, and trial conversion rates."),
            ClubReportTemplate("rep_4", "Squad Workload & Risk Signal Telemetry", "Risk & Availability", "ACWR chronic workload ratios, participation limitations, and return-to-play timelines."),
            ClubReportTemplate("rep_5", "Federation Clearances & Document Compliance Audit", "Club & Activity", "Level 2 verified credentials, registration validity, and contract expiration dates."),
            ClubReportTemplate("rep_6", "Treasury & Stage D Protection Ledger", "Financial & Protection", "Membership dues, federation fee records, and underwritten insurance policy limits.")
        )
    }

    val filteredReports = remember(selectedCategory, reportTemplates) {
        if (selectedCategory == "All Reports") reportTemplates
        else reportTemplates.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Summarize, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Organizational Reports Hub",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Automated Cross-Functional Intelligence & Data Exports for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    if (exportFeedback != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFDCFCE7), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(exportFeedback ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB),
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                categories.forEach { c ->
                    val isSelected = selectedCategory == c
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = c },
                        text = {
                            Text(
                                text = c,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF2563EB) else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Report Templates List
        items(filteredReports) { rep ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEFF6FF)) {
                                Text(rep.category.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(rep.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rep.description, fontSize = 11.sp, color = textMuted, lineHeight = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF8FAFC))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Export formats: PDF • CSV • Excel • API", fontSize = 10.sp, color = textMuted)

                        Button(
                            onClick = { selectedReportForExport = rep },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Export Format Selector
    if (selectedReportForExport != null) {
        val rep = selectedReportForExport!!
        Dialog(onDismissRequest = { selectedReportForExport = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Export Report", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { selectedReportForExport = null }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    Text(rep.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = purpleAccent)

                    val formats = listOf("PDF Document", "CSV Spreadsheets", "Excel (.xlsx) Workbook", "JSON / REST API Endpoint")
                    formats.forEach { fmt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth().clickable {
                                exportFeedback = "Successfully exported '${rep.title}' as $fmt!"
                                selectedReportForExport = null
                            }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(fmt, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
