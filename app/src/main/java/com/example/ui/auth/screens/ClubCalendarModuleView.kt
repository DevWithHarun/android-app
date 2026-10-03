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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubCalendarModuleView(
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

    var currentViewMode by rememberSaveable { mutableStateOf("Month View") }
    val viewModes = listOf("Day View", "Week View", "Month View", "By Team", "By Competition")

    var selectedEventType by rememberSaveable { mutableStateOf("All Events") }
    val eventTypes = listOf("All Events", "Matches", "Training", "Trials", "Scouting", "Deadlines", "Reviews")

    var showAddEventDialog by remember { mutableStateOf(false) }

    val backendEvents = (clubDashboardState as? ClubDashboardUiState.Success)?.calendarEvents ?: emptyList()
    val eventsList = backendEvents

    val filteredEvents = remember(selectedEventType, eventsList) {
        if (selectedEventType == "All Events") eventsList
        else eventsList.filter { it.eventType.contains(selectedEventType, ignoreCase = true) }
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
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Club Unified Calendar",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Organization Schedule & Event Matrix for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showAddEventDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Event", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // View Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        viewModes.forEach { mode ->
                            FilterChip(
                                selected = currentViewMode == mode,
                                onClick = { currentViewMode = mode },
                                label = { Text(mode, fontSize = 11.sp, fontWeight = if (currentViewMode == mode) FontWeight.Bold else FontWeight.Normal) }
                            )
                        }
                    }
                }
            }
        }

        // Event Type Filter Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = eventTypes.indexOf(selectedEventType).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB),
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                eventTypes.forEach { type ->
                    val isSelected = selectedEventType == type
                    Tab(
                        selected = isSelected,
                        onClick = { selectedEventType = type },
                        text = {
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF2563EB) else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Events List
        items(filteredEvents) { ev ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(ev.date.takeLast(2), fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
                            Text("OCT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (ev.eventType) {
                                    "Matches" -> Color(0xFFFEE2E2)
                                    "Training" -> Color(0xFFCCFBF1)
                                    "Trials" -> Color(0xFFFAF5FF)
                                    "Scouting" -> Color(0xFFFEF3C7)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = ev.eventType.uppercase(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (ev.eventType) {
                                        "Matches" -> dangerColor
                                        "Training" -> Color(0xFF0F766E)
                                        "Trials" -> purpleAccent
                                        "Scouting" -> warningColor
                                        else -> primaryColor
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(ev.time, fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ev.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("${ev.targetTeam} • ${ev.venue}", fontSize = 11.sp, color = textMuted)
                    }
                }
            }
        }
    }

    // DIALOG: Add Event
    if (showAddEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventType by remember { mutableStateOf("Matches") }
        var dateInput by remember { mutableStateOf("2026-10-08") }
        var timeInput by remember { mutableStateOf("15:00") }
        var teamInput by remember { mutableStateOf("Senior Team 1st XI") }
        var venueInput by remember { mutableStateOf("Main Stadium") }

        Dialog(onDismissRequest = { showAddEventDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Schedule Organization Event", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showAddEventDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = eventTitle, onValueChange = { eventTitle = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Text("Event Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    val types = listOf("Matches", "Training", "Trials", "Scouting", "Deadlines", "Reviews")
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        types.forEach { t ->
                            FilterChip(selected = eventType == t, onClick = { eventType = t }, label = { Text(t, fontSize = 10.sp) })
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = dateInput, onValueChange = { dateInput = it }, label = { Text("Date") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = timeInput, onValueChange = { timeInput = it }, label = { Text("Time") }, modifier = Modifier.weight(1f), singleLine = true)
                    }

                    OutlinedTextField(value = teamInput, onValueChange = { teamInput = it }, label = { Text("Target Team / Scope") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = venueInput, onValueChange = { venueInput = it }, label = { Text("Venue / Location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            if (eventTitle.isNotBlank()) {
                                clubDashboardViewModel.createCalendarEvent(
                                    title = eventTitle.trim(),
                                    eventType = eventType,
                                    date = dateInput.trim(),
                                    time = timeInput.trim(),
                                    targetTeam = teamInput.trim(),
                                    venue = venueInput.trim()
                                )
                                showAddEventDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Add to Organization Calendar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
