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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAvailabilityAndProtectionView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var statusFilter by rememberSaveable { mutableStateOf("All Statuses") }
    val filterOptions = listOf(
        "All Statuses",
        "Available",
        "Unavailable",
        "Returning",
        "Restricted",
        "Participation Limitation"
    )

    var showMedicalAccessToggle by remember { mutableStateOf(false) }
    var medicalAccessAuthorized by rememberSaveable { mutableStateOf(false) }
    var showUpdateStatusDialog by remember { mutableStateOf(false) }
    var showProtectionActionDialog by remember { mutableStateOf(false) }
    var selectedRecordForAction by remember { mutableStateOf<ClubAvailabilityStatusModel?>(null) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val athletes = successState?.athletes ?: emptyList()

    // Availability roster state
    var availabilityList by remember(athletes) {
        mutableStateOf(
            listOf(
                ClubAvailabilityStatusModel(
                    id = "avail_1",
                    athleteName = athletes.getOrNull(0)?.name ?: "John Kamau",
                    teamName = "Senior Team",
                    position = "Forward / Attacking Mid",
                    status = "Available",
                    limitationNotes = "Full 90-min competitive match participation authorized. Optimal readiness.",
                    expectedReturnDate = "Immediate",
                    riskLevel = "Low",
                    protectionProtocol = "Standard Load Monitoring (ACWR 1.05)",
                    updatedBy = "Lead Team Physio & High Performance Lead",
                    updatedAt = "2026-09-30"
                ),
                ClubAvailabilityStatusModel(
                    id = "avail_2",
                    athleteName = athletes.getOrNull(1)?.name ?: "Brian Ochieng",
                    teamName = "U20 Youth Team",
                    position = "Striker (ST)",
                    status = "Restricted",
                    limitationNotes = "Participation limitation: Max 60 mins match play. High-speed running capped at 600m.",
                    expectedReturnDate = "Full clearance expected Oct 8",
                    riskLevel = "Moderate",
                    protectionProtocol = "Controlled Minutes Progression & Post-Match Cryotherapy",
                    updatedBy = "Academy Medical Staff",
                    updatedAt = "2026-09-29"
                ),
                ClubAvailabilityStatusModel(
                    id = "avail_3",
                    athleteName = athletes.getOrNull(2)?.name ?: "Kevin Otieno",
                    teamName = "U23 Reserves",
                    position = "Center Back (CB)",
                    status = "Returning",
                    limitationNotes = "Transitioning back from ankle sprain. Light ball drills & non-contact conditioning.",
                    expectedReturnDate = "2026-10-12",
                    riskLevel = "Moderate",
                    protectionProtocol = "Functional Return-to-Play Phase 3",
                    updatedBy = "Rehabilitation Specialist",
                    updatedAt = "2026-09-28"
                ),
                ClubAvailabilityStatusModel(
                    id = "avail_4",
                    athleteName = "Victor Wanyama (Trial)",
                    teamName = "Senior Team",
                    position = "Defensive Midfielder (DM)",
                    status = "Unavailable",
                    limitationNotes = "Hamstring tightness grade 1; resting for 7 days. Physiotherapy session daily.",
                    expectedReturnDate = "2026-10-15",
                    riskLevel = "High",
                    protectionProtocol = "Load De-escalation & Soft Tissue Protocol",
                    updatedBy = "Senior Medical Officer",
                    updatedAt = "2026-09-30"
                ),
                ClubAvailabilityStatusModel(
                    id = "avail_5",
                    athleteName = "Dennis Kiprotich",
                    teamName = "U17 Academy",
                    position = "Goalkeeper (GK)",
                    status = "Participation Limitation",
                    limitationNotes = "Exempt from diving drills on artificial turf due to shoulder recovery.",
                    expectedReturnDate = "Full clearance Oct 5",
                    riskLevel = "Low",
                    protectionProtocol = "Position-Specific Drill Modification",
                    updatedBy = "Youth Physio",
                    updatedAt = "2026-09-27"
                )
            )
        )
    }

    val filteredList = remember(statusFilter, availabilityList) {
        if (statusFilter == "All Statuses") availabilityList
        else availabilityList.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }

    val availableCount = availabilityList.count { it.status.equals("Available", ignoreCase = true) }
    val restrictedCount = availabilityList.count { it.status.equals("Restricted", ignoreCase = true) || it.status.equals("Participation Limitation", ignoreCase = true) }
    val unavailableCount = availabilityList.count { it.status.equals("Unavailable", ignoreCase = true) || it.status.equals("Returning", ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFCCFBF1),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = tealAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Availability & Athlete Protection",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Permissioned Operational Intelligence & Welfare Layer for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showUpdateStatusDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Operational KPI Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiStatusCard("Available / Fit", "$availableCount", successColor, Modifier.weight(1f))
                        KpiStatusCard("Restricted / Limit", "$restrictedCount", warningColor, Modifier.weight(1f))
                        KpiStatusCard("Out / Rehab", "$unavailableCount", dangerColor, Modifier.weight(1f))
                        KpiStatusCard("Squad Ready", "${((availableCount.toDouble() / availabilityList.size.coerceAtLeast(1)) * 100).toInt()}%", tealAccent, Modifier.weight(1f))
                    }
                }
            }
        }

        // 5-STEP PROTECTION LAYER WORKFLOW BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = successColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Organizational Protection Protocol",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF14532D)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Talent Graph ensures player safety through verified operational signals while strictly safeguarding medical confidentiality.",
                        fontSize = 11.sp,
                        color = Color(0xFF15803D)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5 Step Horizontal Flow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProtectionStepBadge("1. Verified Data", Color(0xFF2563EB))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(12.dp))
                        ProtectionStepBadge("2. Intel", purpleAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(12.dp))
                        ProtectionStepBadge("3. Risk Signals", warningColor)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(12.dp))
                        ProtectionStepBadge("4. Club Review", tealAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF86EFAC), modifier = Modifier.size(12.dp))
                        ProtectionStepBadge("5. Action", successColor)
                    }
                }
            }
        }

        // MEDICAL CONFIDENTIALITY & PERMISSIONS TOGGLE
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = CircleShape,
                            color = if (medicalAccessAuthorized) Color(0xFFEDE9FE) else Color(0xFFF1F5F9),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (medicalAccessAuthorized) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (medicalAccessAuthorized) purpleAccent else textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (medicalAccessAuthorized) "Medical Data Authorization Active" else "Operational View (Confidential)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = if (medicalAccessAuthorized) "Displaying authorized physio clinical reports & rehab metrics." else "Clinical diagnosis hidden. Showing operational availability & restrictions only.",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                        }
                    }

                    Switch(
                        checked = medicalAccessAuthorized,
                        onCheckedChange = { medicalAccessAuthorized = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = purpleAccent, checkedTrackColor = Color(0xFFDDD6FE))
                    )
                }
            }
        }

        // Status Filter Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = filterOptions.indexOf(statusFilter).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = tealAccent,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = statusFilter == filter
                    Tab(
                        selected = isSelected,
                        onClick = { statusFilter = filter },
                        text = {
                            Text(
                                text = filter,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) tealAccent else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Athlete Availability Cards
        items(filteredList) { item ->
            AvailabilityCard(
                model = item,
                isMedicalUnlocked = medicalAccessAuthorized,
                onTakeAction = {
                    selectedRecordForAction = item
                    showProtectionActionDialog = true
                }
            )
        }
    }

    // DIALOG: Log / Update Availability Status
    if (showUpdateStatusDialog) {
        var athleteName by remember { mutableStateOf("") }
        var teamName by remember { mutableStateOf("Senior Team") }
        var selectedStatus by remember { mutableStateOf("Available") }
        var limitationText by remember { mutableStateOf("") }
        var returnDate by remember { mutableStateOf("Immediate") }
        var riskLevel by remember { mutableStateOf("Low") }
        var protocolText by remember { mutableStateOf("Standard Load Monitoring") }

        Dialog(onDismissRequest = { showUpdateStatusDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Log Availability & Limitation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showUpdateStatusDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(
                        value = athleteName,
                        onValueChange = { athleteName = it },
                        label = { Text("Athlete Name") },
                        placeholder = { Text("e.g. Samuel Mutua") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Team Squad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Availability Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    val statusOpts = listOf("Available", "Unavailable", "Returning", "Restricted", "Participation Limitation")
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        statusOpts.forEach { opt ->
                            FilterChip(
                                selected = selectedStatus == opt,
                                onClick = { selectedStatus = opt },
                                label = { Text(opt, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = limitationText,
                        onValueChange = { limitationText = it },
                        label = { Text("Participation Limitation / Notes") },
                        placeholder = { Text("e.g. Max 45 mins, non-contact, gym only") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = returnDate,
                            onValueChange = { returnDate = it },
                            label = { Text("Expected Return") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = riskLevel,
                            onValueChange = { riskLevel = it },
                            label = { Text("Risk Level (Low/Med/High)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (athleteName.isNotBlank()) {
                                val newEntry = ClubAvailabilityStatusModel(
                                    id = "avail_${System.currentTimeMillis()}",
                                    athleteName = athleteName,
                                    teamName = teamName,
                                    status = selectedStatus,
                                    limitationNotes = if (limitationText.isNotBlank()) limitationText else "Operational clearance updated.",
                                    expectedReturnDate = returnDate,
                                    riskLevel = riskLevel,
                                    protectionProtocol = protocolText,
                                    updatedBy = "Club Admin / Medical Board",
                                    updatedAt = "2026-09-30"
                                )
                                availabilityList = listOf(newEntry) + availabilityList
                                showUpdateStatusDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Save Status Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG: Protection Action
    if (showProtectionActionDialog && selectedRecordForAction != null) {
        val athlete = selectedRecordForAction!!
        var actionType by remember { mutableStateOf("Load Management Protocol") }
        val actions = listOf("Load Management Protocol", "Matchday Squad Rest", "Specialist Consultation", "Return-to-Play Protocol")

        Dialog(onDismissRequest = { showProtectionActionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Execute Protection Action", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showProtectionActionDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    Text("Target Athlete: ${athlete.athleteName} (${athlete.teamName})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = purpleAccent)

                    Text("Select Action Protocol:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        actions.forEach { act ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (actionType == act) Color(0xFFCCFBF1) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, if (actionType == act) tealAccent else borderColor),
                                modifier = Modifier.fillMaxWidth().clickable { actionType = act }
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = actionType == act, onClick = { actionType = act })
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(act, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            showProtectionActionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Deploy Protection Protocol", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun KpiStatusCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ProtectionStepBadge(title: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun AvailabilityCard(
    model: ClubAvailabilityStatusModel,
    isMedicalUnlocked: Boolean,
    onTakeAction: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val tealAccent = Color(0xFF0D9488)

    val (badgeBg, badgeColor) = when {
        model.status.contains("Available", ignoreCase = true) -> Color(0xFFECFDF5) to Color(0xFF047857)
        model.status.contains("Restricted", ignoreCase = true) || model.status.contains("Limitation", ignoreCase = true) -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        model.status.contains("Returning", ignoreCase = true) -> Color(0xFFEFF6FF) to Color(0xFF1D4ED8)
        else -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(model.athleteName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Text("${model.teamName} • ${model.position}", fontSize = 11.sp, color = textMuted)
                }

                Surface(shape = RoundedCornerShape(6.dp), color = badgeBg) {
                    Text(
                        text = model.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Operational Participation Limitation Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Operational Limitation / Directive:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(model.limitationNotes, fontSize = 12.sp, color = Color(0xFF334155), lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Expected Return: ${model.expectedReturnDate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (model.expectedReturnDate == "Immediate") Color(0xFF059669) else Color(0xFFD97706))
                        Text("Risk: ${model.riskLevel}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (model.riskLevel == "Low") Color(0xFF059669) else Color(0xFFDC2626))
                    }
                }
            }

            // If Medical Access is authorized, show deep clinical data
            if (isMedicalUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFAF5FF),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Authorized Clinical Welfare Details:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF581C87))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Protocol: ${model.protectionProtocol}", fontSize = 11.sp, color = Color(0xFF6B21A8))
                        Text("Signed by: ${model.updatedBy} (${model.updatedAt})", fontSize = 10.sp, color = textMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onTakeAction,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = tealAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Protection Action", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
