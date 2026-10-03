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

data class PlayerAttendanceRecord(
    val athleteName: String,
    val position: String,
    val status: String // "Present", "Load Managed", "Injured", "Absent"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubTrainingModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val bg = TGColors.Bg
    val panel = TGColors.Panel
    val panel2 = TGColors.Panel2
    val panel3 = TGColors.Panel3
    val line = TGColors.Line
    val txt = TGColors.Txt
    val mut = TGColors.Mut
    val acc = TGColors.Acc
    val acc2 = TGColors.Acc2
    val warn = TGColors.Warn
    val bad = TGColors.Bad

    var showCreateSessionDialog by remember { mutableStateOf(false) }
    var selectedSessionForAttendance by remember { mutableStateOf<ClubTrainingModel?>(null) }
    var selectedTeamFilter by rememberSaveable { mutableStateOf("All Squads") }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val rawSessions = successState?.trainingSessions ?: emptyList()
    val registeredAthletes = successState?.athletes ?: emptyList()

    // Default template sessions if none logged yet
    val defaultSessions = remember {
        listOf(
            ClubTrainingModel("tr_1", "", "Senior Team 1st XI", "Tactical & Match Preparation", "2026-10-02", 90, "High", 94, 78, "Opponent press evasion, counter-press triggers & corner kick routines. Coach Francis Kimanzi."),
            ClubTrainingModel("tr_2", "", "U20 Youth Team", "High-Intensity Conditioning & Small-Sided Games", "2026-10-01", 85, "High", 91, 72, "4v4 transitions with 120-meter aerobic intervals. 4 athletes in load management."),
            ClubTrainingModel("tr_3", "", "U17 Academy", "Technical Ball Mastery & Positional Play", "2026-09-30", 75, "Medium", 88, 64, "First touch mastery in tight half-spaces and passing diamond drills."),
            ClubTrainingModel("tr_4", "", "Senior Team 1st XI", "Active Recovery & Mobility Protocol", "2026-09-29", 60, "Recovery", 100, 42, "Foam rolling, pool mobility, low-impact stretching and video tactical debrief.")
        )
    }

    var localSessionList by remember(rawSessions) {
        mutableStateOf(if (rawSessions.isNotEmpty()) rawSessions else defaultSessions)
    }

    val teamFilterOptions = listOf("All Squads", "Senior Team 1st XI", "U20 Youth Team", "U17 Academy")

    val displayedSessions = remember(selectedTeamFilter, localSessionList) {
        if (selectedTeamFilter == "All Squads") localSessionList
        else localSessionList.filter { it.teamName.equals(selectedTeamFilter, ignoreCase = true) }
    }

    // Dynamic Attendance Roster for Selected Session
    var attendanceRoster by remember(selectedSessionForAttendance) {
        mutableStateOf(
            if (registeredAthletes.isNotEmpty()) {
                registeredAthletes.mapIndexed { idx, ath ->
                    PlayerAttendanceRecord(
                        athleteName = ath.name,
                        position = ath.position,
                        status = when (idx % 5) {
                            3 -> "Load Managed"
                            4 -> "Injured"
                            else -> "Present"
                        }
                    )
                }
            } else {
                listOf(
                    PlayerAttendanceRecord("Harun", "FW", "Present"),
                    PlayerAttendanceRecord("Nzai", "MF", "Present"),
                    PlayerAttendanceRecord("John Kamau", "FW", "Present"),
                    PlayerAttendanceRecord("Brian Ochieng", "MF", "Load Managed"),
                    PlayerAttendanceRecord("Kevin Otieno", "DF", "Present"),
                    PlayerAttendanceRecord("Dennis Ouma", "GK", "Present"),
                    PlayerAttendanceRecord("Samuel Mwangi", "DF", "Injured"),
                    PlayerAttendanceRecord("Victor Wanyama", "MF", "Present")
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header Banner & Log Session Button
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = panel2,
                                border = BorderStroke(1.dp, acc),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = acc, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TRAINING & ATTENDANCE HUB",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = txt,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "$activeClubName • Squad Workload Volume, Attendance & Schedule",
                                    fontSize = 11.sp,
                                    color = mut
                                )
                            }
                        }

                        Button(
                            onClick = { showCreateSessionDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = acc),
                            shape = RoundedCornerShape(9.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Session", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Training Metrics Overview Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrainingMetricCard("Sessions Logged", "${displayedSessions.size}", txt, Modifier.weight(1f))
                        TrainingMetricCard("Avg Attendance", "92.4%", acc, Modifier.weight(1f))
                        TrainingMetricCard("Avg Squad Load", "74 / 100", warn, Modifier.weight(1f))
                        TrainingMetricCard("Weekly Volume", "14.5 hrs", acc2, Modifier.weight(1f))
                    }
                }
            }
        }

        // 2. Team Division Filter Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                teamFilterOptions.forEach { teamOption ->
                    val isSelected = selectedTeamFilter == teamOption
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) panel2 else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) acc else line),
                        modifier = Modifier.clickable { selectedTeamFilter = teamOption }
                    ) {
                        Text(
                            text = teamOption,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) acc else mut,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 3. Squad Workload & Injury Protection Protocol Notice
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = panel2,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = acc, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACWR WORKLOAD & ATTENDANCE AUDIT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = acc
                        )
                        Text(
                            text = "Acute-to-Chronic Workload Ratio is at 1.08 (Optimal Green Zone). 4 athletes currently on modified recovery drills.",
                            fontSize = 11.sp,
                            color = txt,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 4. Session Cards List
        items(displayedSessions) { session ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = session.sessionType,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = txt
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${session.teamName} • ${session.date} (${session.durationMins} mins)",
                                fontSize = 11.5.sp,
                                color = mut
                            )
                        }

                        val (badgeBg, badgeText) = when (session.intensity.lowercase()) {
                            "high" -> bad.copy(alpha = 0.15f) to bad
                            "recovery" -> acc2.copy(alpha = 0.15f) to acc2
                            else -> warn.copy(alpha = 0.15f) to warn
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = badgeBg,
                            border = BorderStroke(1.dp, badgeText.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${session.intensity.uppercase()} INTENSITY",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = badgeText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = session.coachNotes,
                        fontSize = 12.sp,
                        color = mut,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = line)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column {
                                Text("Attendance", fontSize = 9.5.sp, color = mut)
                                Text("${session.attendancePercent}%", fontSize = 13.sp, fontWeight = FontWeight.Black, color = acc)
                            }
                            Column {
                                Text("Load Index", fontSize = 9.5.sp, color = mut)
                                Text("${session.averageLoad} / 100", fontSize = 13.sp, fontWeight = FontWeight.Black, color = warn)
                            }
                        }

                        OutlinedButton(
                            onClick = { selectedSessionForAttendance = session },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, acc.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = acc),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Roll Call & Roster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: Interactive Roll Call & Attendance Roster
    selectedSessionForAttendance?.let { session ->
        Dialog(onDismissRequest = { selectedSessionForAttendance = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ATTENDANCE ROLL CALL",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = txt
                            )
                            Text(
                                text = "${session.teamName} • ${session.date}",
                                fontSize = 11.sp,
                                color = mut
                            )
                        }
                        IconButton(onClick = { selectedSessionForAttendance = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = mut)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live calculated stats for this roll call
                    val presentCount = attendanceRoster.count { it.status == "Present" }
                    val loadManagedCount = attendanceRoster.count { it.status == "Load Managed" }
                    val injuredCount = attendanceRoster.count { it.status == "Injured" }
                    val calculatedPercent = if (attendanceRoster.isNotEmpty()) (presentCount + loadManagedCount) * 100 / attendanceRoster.size else 0

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = panel2,
                        border = BorderStroke(1.dp, line),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Present", fontSize = 9.sp, color = mut)
                                Text("$presentCount", fontSize = 14.sp, fontWeight = FontWeight.Black, color = acc)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Load Managed", fontSize = 9.sp, color = mut)
                                Text("$loadManagedCount", fontSize = 14.sp, fontWeight = FontWeight.Black, color = warn)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Injured / Rehab", fontSize = 9.sp, color = mut)
                                Text("$injuredCount", fontSize = 14.sp, fontWeight = FontWeight.Black, color = bad)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Rate", fontSize = 9.sp, color = mut)
                                Text("$calculatedPercent%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = acc2)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap any player's badge to toggle attendance status:",
                        fontSize = 11.sp,
                        color = mut
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(attendanceRoster) { index, player ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = panel2,
                                border = BorderStroke(1.dp, line),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(player.athleteName, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = txt)
                                        Text(player.position, fontSize = 10.sp, color = mut)
                                    }

                                    val (chipBg, chipText) = when (player.status) {
                                        "Present" -> acc.copy(alpha = 0.15f) to acc
                                        "Load Managed" -> warn.copy(alpha = 0.15f) to warn
                                        "Injured" -> bad.copy(alpha = 0.15f) to bad
                                        else -> mut.copy(alpha = 0.15f) to mut
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = chipBg,
                                        border = BorderStroke(1.dp, chipText.copy(alpha = 0.5f)),
                                        modifier = Modifier.clickable {
                                            val nextStatus = when (player.status) {
                                                "Present" -> "Load Managed"
                                                "Load Managed" -> "Injured"
                                                "Injured" -> "Absent"
                                                else -> "Present"
                                            }
                                            attendanceRoster = attendanceRoster.toMutableList().also {
                                                it[index] = player.copy(status = nextStatus)
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(player.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = chipText)
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(Icons.Default.Sync, contentDescription = null, tint = chipText, modifier = Modifier.size(11.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            // Update session with new calculated attendance
                            localSessionList = localSessionList.map {
                                if (it.id == session.id) it.copy(attendancePercent = calculatedPercent) else it
                            }
                            selectedSessionForAttendance = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Confirm & Save Attendance", color = Color(0xFF04121A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG 2: Log Training Session
    if (showCreateSessionDialog) {
        var teamNameInput by remember { mutableStateOf("Senior Team 1st XI") }
        var sessionTypeInput by remember { mutableStateOf("Tactical & Match Preparation") }
        var durationInput by remember { mutableStateOf("90") }
        var intensityInput by remember { mutableStateOf("High") }
        var loadInput by remember { mutableStateOf("75") }
        var notesInput by remember { mutableStateOf("Defensive transitions, pressing triggers and set-piece positioning.") }

        Dialog(onDismissRequest = { showCreateSessionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Log Training Session", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = txt)
                        IconButton(onClick = { showCreateSessionDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = mut)
                        }
                    }

                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("Squad / Team", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc, unfocusedBorderColor = line,
                            focusedTextColor = txt, unfocusedTextColor = txt,
                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                        )
                    )

                    OutlinedTextField(
                        value = sessionTypeInput,
                        onValueChange = { sessionTypeInput = it },
                        label = { Text("Session Objective & Type", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc, unfocusedBorderColor = line,
                            focusedTextColor = txt, unfocusedTextColor = txt,
                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = durationInput,
                            onValueChange = { durationInput = it },
                            label = { Text("Duration (Mins)", color = mut) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = acc, unfocusedBorderColor = line,
                                focusedTextColor = txt, unfocusedTextColor = txt,
                                focusedContainerColor = panel2, unfocusedContainerColor = panel2
                            )
                        )

                        OutlinedTextField(
                            value = intensityInput,
                            onValueChange = { intensityInput = it },
                            label = { Text("Intensity (High/Med)", color = mut) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = acc, unfocusedBorderColor = line,
                                focusedTextColor = txt, unfocusedTextColor = txt,
                                focusedContainerColor = panel2, unfocusedContainerColor = panel2
                            )
                        )
                    }

                    OutlinedTextField(
                        value = loadInput,
                        onValueChange = { loadInput = it },
                        label = { Text("Estimated Load Index (0-100)", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc, unfocusedBorderColor = line,
                            focusedTextColor = txt, unfocusedTextColor = txt,
                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                        )
                    )

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Coach Technical Notes", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc, unfocusedBorderColor = line,
                            focusedTextColor = txt, unfocusedTextColor = txt,
                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val dur = durationInput.toIntOrNull() ?: 90
                            val loadVal = loadInput.toIntOrNull() ?: 75
                            val newSession = ClubTrainingModel(
                                id = "tr_${System.currentTimeMillis()}",
                                teamName = teamNameInput,
                                sessionType = sessionTypeInput,
                                durationMins = dur,
                                intensity = intensityInput,
                                date = "2026-10-03",
                                attendancePercent = 92,
                                averageLoad = loadVal,
                                coachNotes = notesInput
                            )
                            localSessionList = listOf(newSession) + localSessionList
                            clubDashboardViewModel.createTraining(
                                teamName = teamNameInput,
                                type = sessionTypeInput,
                                date = "2026-10-03",
                                duration = dur,
                                load = loadVal,
                                notes = notesInput
                            )
                            showCreateSessionDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Save Training Record", color = Color(0xFF04121A), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TGColors.Panel2,
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = TGColors.Mut, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
