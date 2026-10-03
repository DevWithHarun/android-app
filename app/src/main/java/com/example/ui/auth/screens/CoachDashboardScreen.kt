package com.example.ui.auth.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import com.example.data.AthleteEntity
import com.example.data.TrainingSessionEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState = TalentUiState(),
    userName: String = "Coach Harun",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("home") }
    var showMenuDrawer by remember { mutableStateOf(false) }
    var showCreateTrainingModal by remember { mutableStateOf(false) }
    var showAttendanceModal by remember { mutableStateOf(false) }
    var showMatchSquadModal by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    val currentDateStr = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
    }

    BackHandler(enabled = selectedTab != "home") {
        selectedTab = "home"
    }

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFCCFBF1),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Sports, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = when (selectedTab) {
                                    "home" -> "Coach Command Center"
                                    "training" -> "Training Management"
                                    "match_day" -> "Match Day & Tactics"
                                    "workload" -> "Workload Intelligence"
                                    "availability" -> "Squad Availability"
                                    "teams" -> "My Teams & Squad"
                                    "athletes" -> "Athlete Directory"
                                    else -> "Coach Desk"
                                },
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "U20 Team • ${uiState.userName.ifBlank { userName }} • ${uiState.athletes.size} Athletes",
                                color = textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (selectedTab != "home") {
                        IconButton(onClick = { selectedTab = "home" }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryColor)
                        }
                    } else {
                        IconButton(onClick = { showMenuDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryColor)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToFeed) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = "Feed Dashboard", tint = secondaryColor)
                    }
                    if (selectedTab != "home") {
                        IconButton(onClick = { showMenuDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Drawer", tint = textMuted)
                        }
                    }
                    IconButton(onClick = { showCreateTrainingModal = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "New Training", tint = secondaryColor)
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = textMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "training",
                    onClick = { selectedTab = "training" },
                    icon = { Icon(Icons.Default.Timer, contentDescription = "Training") },
                    label = { Text("Training", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "match_day",
                    onClick = { selectedTab = "match_day" },
                    icon = { Icon(Icons.Default.SportsScore, contentDescription = "Match Day") },
                    label = { Text("Match", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "workload",
                    onClick = { selectedTab = "workload" },
                    icon = { Icon(Icons.Default.MonitorHeart, contentDescription = "Workload") },
                    label = { Text("Workload", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "availability",
                    onClick = { selectedTab = "availability" },
                    icon = { Icon(Icons.Default.EventAvailable, contentDescription = "Availability") },
                    label = { Text("Squad", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                "home" -> CoachHomeContent(
                    userName = uiState.userName.ifBlank { userName },
                    dateStr = currentDateStr,
                    uiState = uiState,
                    onNavigateTab = { selectedTab = it },
                    onCreateTraining = { showCreateTrainingModal = true },
                    onRecordAttendance = { showAttendanceModal = true },
                    onSelectSquad = { showMatchSquadModal = true }
                )
                "teams" -> CoachTeamsContent(uiState)
                "athletes" -> CoachAthletesContent(uiState)
                "training" -> CoachTrainingContent(
                    onCreateTraining = { showCreateTrainingModal = true },
                    onRecordAttendance = { showAttendanceModal = true }
                )
                "attendance" -> CoachAttendanceContent(uiState)
                "performance" -> CoachPerformanceContent(uiState)
                "development" -> CoachDevelopmentContent(uiState)
                "workload" -> CoachWorkloadContent()
                "availability" -> CoachAvailabilityContent()
                "match_day" -> CoachMatchDayContent(
                    onSelectSquad = { showMatchSquadModal = true }
                )
                "analytics" -> CoachAnalyticsContent(uiState)
                "alerts" -> CoachAlertsContent(uiState)
                "reports" -> CoachReportsContent(uiState)
                "messages" -> CoachMessagesContent(uiState)
                "calendar" -> CoachCalendarContent(uiState)
                "settings" -> CoachSettingsContent(uiState, onSignOut)
                else -> CoachHomeContent(
                    userName = uiState.userName.ifBlank { userName },
                    dateStr = currentDateStr,
                    uiState = uiState,
                    onNavigateTab = { selectedTab = it },
                    onCreateTraining = { showCreateTrainingModal = true },
                    onRecordAttendance = { showAttendanceModal = true },
                    onSelectSquad = { showMatchSquadModal = true }
                )
            }
        }
    }

    if (showMenuDrawer) {
        CoachMenuDrawer(
            selectedTab = selectedTab,
            onSelectTab = {
                selectedTab = it
                showMenuDrawer = false
            },
            onDismiss = { showMenuDrawer = false },
            onSignOut = onSignOut
        )
    }

    if (showCreateTrainingModal) {
        CreateTrainingModal(
            onDismiss = { showCreateTrainingModal = false },
            onSave = { title, duration, intensity, location ->
                viewModel?.addTrainingSession(
                    type = title,
                    date = "Today",
                    duration = duration,
                    intensity = "$intensity / 10",
                    notes = location,
                    attendance = "0 / ${uiState.athletes.size}"
                )
                showCreateTrainingModal = false
            }
        )
    }

    if (showAttendanceModal) {
        RecordAttendanceModal(
            athletes = uiState.athletes,
            onDismiss = { showAttendanceModal = false },
            onSave = {
                showAttendanceModal = false
            }
        )
    }

    if (showMatchSquadModal) {
        SelectSquadModal(
            athletes = uiState.athletes,
            onDismiss = { showMatchSquadModal = false }
        )
    }
}

@Composable
fun CoachHomeContent(
    userName: String,
    dateStr: String,
    uiState: TalentUiState,
    onNavigateTab: (String) -> Unit,
    onCreateTraining: () -> Unit,
    onRecordAttendance: () -> Unit,
    onSelectSquad: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    val totalAthletes = uiState.athletes.size.toString().ifBlank { "28" }
    val availableAthletes = uiState.athletes.filter { it.isVerified }.size.toString().ifBlank { "24" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Welcome Header & Quick Action Buttons
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Good day, $userName 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("U20 Team", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("• $dateStr", fontSize = 11.sp, color = textMuted)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSelectSquad,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = textMuted)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onCreateTraining,
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Training", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Today's Overview (4 KPI Cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Athletes
                CoachKpiCard(
                    modifier = Modifier.weight(1f).clickable { onNavigateTab("availability") },
                    title = "ATHLETES",
                    value = totalAthletes,
                    icon = Icons.Default.People,
                    iconTint = textMuted,
                    valColor = primaryColor
                )
                // Available
                CoachKpiCard(
                    modifier = Modifier.weight(1f).clickable { onNavigateTab("availability") },
                    title = "AVAILABLE",
                    value = availableAthletes,
                    icon = Icons.Default.CheckCircle,
                    iconTint = Color(0xFF059669),
                    valColor = Color(0xFF047857)
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Training Today
                CoachKpiCard(
                    modifier = Modifier.weight(1f).clickable { onNavigateTab("training") },
                    title = "TRAINING TODAY",
                    value = "4:00 PM",
                    icon = Icons.Default.Timer,
                    iconTint = secondaryColor,
                    valColor = primaryColor
                )
                // Next Match
                CoachKpiCard(
                    modifier = Modifier.weight(1f).clickable { onNavigateTab("match_day") },
                    title = "NEXT MATCH",
                    value = "Saturday",
                    icon = Icons.Default.Flag,
                    iconTint = Color(0xFF2563EB),
                    valColor = primaryColor
                )
            }
        }

        // Quick Actions Horizontal Bar
        item {
            Column {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        CoachQuickActionButton("Create Training", Icons.Default.Add, secondaryColor, onClick = onCreateTraining)
                    }
                    item {
                        CoachQuickActionButton("Record Attendance", Icons.Default.FactCheck, secondaryColor, onClick = onRecordAttendance)
                    }
                    item {
                        CoachQuickActionButton("Select Squad", Icons.Default.Groups, secondaryColor, onClick = onSelectSquad)
                    }
                    item {
                        CoachQuickActionButton("Workload Review", Icons.Default.MonitorHeart, secondaryColor, onClick = { onNavigateTab("workload") })
                    }
                }
            }
        }

        // Coach Alerts Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Coach Alerts", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                        }
                        Text("4 Active", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    CoachAlertItem(
                        title = "3 athletes have reduced availability",
                        sub = "Medical reviews pending for Ali, Kamau, and Hassan.",
                        icon = Icons.Default.Warning,
                        bgColor = Color(0xFFFFF7ED),
                        borderColor = Color(0xFFFFEDD5),
                        accentColor = Color(0xFFF97316)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CoachAlertItem(
                        title = "2 athletes have high recent workload",
                        sub = "Mwangi and Otieno exceeded 270 minutes this week.",
                        icon = Icons.Default.FavoriteBorder,
                        bgColor = Color(0xFFFEF2F2),
                        borderColor = Color(0xFFFEE2E2),
                        accentColor = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CoachAlertItem(
                        title = "21 athletes completed last training",
                        sub = "Average intensity logged: 7.2 / 10",
                        icon = Icons.Default.Check,
                        bgColor = Color(0xFFECFDF5),
                        borderColor = Color(0xFFD1FAE5),
                        accentColor = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CoachAlertItem(
                        title = "Match squad selection pending",
                        sub = "Draft Starting XI for Saturday vs Coastal Academy.",
                        icon = Icons.Default.Info,
                        bgColor = Color(0xFFEFF6FF),
                        borderColor = Color(0xFFDBEAFE),
                        accentColor = Color(0xFF3B82F6)
                    )
                }
            }
        }

        // Today's Schedule Timeline
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Today's Schedule", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    // Event 1 (Passed)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFFCBD5E1), CircleShape))
                            Box(modifier = Modifier.width(2.dp).height(44.dp).background(Color(0xFFE2E8F0)))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Video Analysis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textMuted, textDecoration = TextDecoration.LineThrough)
                                Text("10:00 AM", fontSize = 11.sp, color = textMuted)
                            }
                            Text("Tactical review of last match against Coastal", fontSize = 11.sp, color = textMuted)
                        }
                    }

                    // Event 2 (Active)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(12.dp).background(secondaryColor, CircleShape))
                            Box(modifier = Modifier.width(2.dp).height(50.dp).background(Color(0xFFE2E8F0)))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Team Training", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFCCFBF1)) {
                                    Text("4:00 PM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = secondaryColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("Technical + Tactical Focus (Build up under pressure)", fontSize = 11.sp, color = textMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                                listOf("JM", "DA", "BO", "KM", "+22").forEach { init ->
                                    Box(
                                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFE0E7FF)).border(1.5.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(init, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3730A3))
                                    }
                                }
                            }
                        }
                    }

                    // Event 3
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(10.dp).background(Color(0xFF94A3B8), CircleShape))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Recovery Session", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("6:30 PM", fontSize = 11.sp, color = textMuted)
                            }
                            Text("Physio block for high-load athletes", fontSize = 11.sp, color = textMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoachTrainingContent(
    onCreateTraining: () -> Unit,
    onRecordAttendance: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // Top Card: Today's Active Session
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
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFCCFBF1),
                            border = BorderStroke(1.dp, Color(0xFF99F6E4))
                        ) {
                            Text(
                                text = "TECHNICAL + TACTICAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = secondaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text("120 min", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMuted)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Build up play under pressure", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Text("4:00 PM • Main Pitch", fontSize = 12.sp, color = textMuted)

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("TARGET INTENSITY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                                Text("Medium (7/10)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("EXPECTED SQUAD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                Text("26 Players", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { /* Start session logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Session Clock", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRecordAttendance,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Attendance", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { /* Record load */ },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Record Load", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recent Completed Training History
        item {
            Text("RECENT TRAINING HISTORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Physical + Recovery", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Text("Yesterday, 10:00 AM", fontSize = 11.sp, color = textMuted)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFDF5), border = BorderStroke(1.dp, Color(0xFFD1FAE5))) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Completed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TrainingMetricMiniCard(Modifier.weight(1f), "ATTENDANCE", "26 / 28")
                        TrainingMetricMiniCard(Modifier.weight(1f), "INTENSITY", "7 / 10")
                        TrainingMetricMiniCard(Modifier.weight(1f), "DURATION", "120 min")
                        TrainingMetricMiniCard(Modifier.weight(1f), "LOAD", "840 AU")
                    }
                }
            }
        }
    }
}

@Composable
fun CoachMatchDayContent(onSelectSquad: () -> Unit) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        // Big Gradient Match Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E3A8A))))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.15f)) {
                                Text("UPCOMING FIXTURE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Text("Sat, 4:00 PM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("U20", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Talent Graph", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Home", color = Color(0xFF93C5FD), fontSize = 11.sp)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("VS", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Mombasa Sports Ground", fontSize = 10.sp, color = Color(0xFFBFDBFE))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF93C5FD))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Coastal Acad.", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("Away", color = Color(0xFF93C5FD), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Formation & Starting XI
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Starting XI (4-3-3)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                        TextButton(onClick = onSelectSquad) {
                            Text("Edit Squad", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val startingXI = listOf(
                        Pair("GK", "John Mwangi"),
                        Pair("LB", "Brian Otieno"),
                        Pair("CB", "David Ali"),
                        Pair("CB", "Kevin Musa"),
                        Pair("RB", "Ali Juma"),
                        Pair("CM", "Peter Kamau"),
                        Pair("CM", "Musa Omondi"),
                        Pair("AM", "Omar Juma"),
                        Pair("LW", "Hassan Said"),
                        Pair("RW", "Said Bakari"),
                        Pair("ST", "James Olunga")
                    )

                    startingXI.forEach { (pos, name) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (pos) {
                                        "GK" -> Color(0xFFFEF3C7)
                                        "LB", "CB", "RB" -> Color(0xFFDBEAFE)
                                        "CM", "AM" -> Color(0xFFD1FAE5)
                                        else -> Color(0xFFF3E8FF)
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(pos, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                            }
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap", tint = textMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Opponent Analysis & Tactics
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("OPPONENT ANALYSIS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Strengths", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BadgeChip("Aggressive Pressing", Color(0xFFFEF2F2), Color(0xFFDC2626))
                        BadgeChip("Fast Counter-attacks", Color(0xFFFEF2F2), Color(0xFFDC2626))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Weaknesses", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BadgeChip("Vulnerable on Set Pieces", Color(0xFFF0FDF4), Color(0xFF16A34A))
                        BadgeChip("Slow Defensive Transitions", Color(0xFFF0FDF4), Color(0xFF16A34A))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Key Danger Player: M. Kamau (#10 Playmaker, Left Footed)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                }
            }
        }
    }
}

@Composable
fun CoachWorkloadContent() {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            Text("TEAM WORKLOAD OVERVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkloadStatusCard(Modifier.weight(1f), "Low", "6", Color(0xFF60A5FA))
                WorkloadStatusCard(Modifier.weight(1f), "Normal", "15", Color(0xFF10B981))
                WorkloadStatusCard(Modifier.weight(1f), "High", "5", Color(0xFFF59E0B))
                WorkloadStatusCard(Modifier.weight(1f), "Very High", "2", Color(0xFFEF4444))
            }
        }

        // Workload Signal Spike Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Workload Signal: John Mwangi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Athlete played 3 matches in 8 days. Acute load has spiked beyond safe thresholds, increasing risk of non-contact injury.", fontSize = 12.sp, color = Color(0xFF7F1D1D))

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("PREV. PERIOD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                            Text("180 mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                        Column {
                            Text("CURRENT PERIOD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                            Text("270 mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                        }
                        Column {
                            Text("SPIKE CHANGE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                            Text("+50%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Adjust Schedule", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Notify Physio", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Athlete Workload List
        item {
            Text("INDIVIDUAL ATHLETE ACWR PROFILES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted, letterSpacing = 1.sp)
        }

        items(listOf(
            Triple("John Mwangi", "Forward • #9", Triple(80, 1.6f, Color(0xFFEF4444))),
            Triple("Brian Otieno", "Midfielder • #8", Triple(55, 1.1f, Color(0xFF10B981))),
            Triple("David Ali", "Defender • #4", Triple(25, 0.7f, Color(0xFF3B82F6)))
        )) { (name, role, data) ->
            val (loadPct, acwr, statusColor) = data
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Text(role, fontSize = 11.sp, color = textMuted)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.1f)) {
                            Text("ACWR: $acwr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("7-Day Acute Load ($loadPct%)", fontSize = 11.sp, color = textMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { loadPct / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = statusColor,
                        trackColor = Color(0xFFF1F5F9)
                    )
                }
            }
        }
    }
}

@Composable
fun CoachAvailabilityContent() {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var filter by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 30.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WorkloadStatusCard(Modifier.weight(1f), "Total", "28", primaryColor)
                WorkloadStatusCard(Modifier.weight(1f), "Available", "24", Color(0xFF10B981))
                WorkloadStatusCard(Modifier.weight(1f), "Questionable", "2", Color(0xFFF59E0B))
                WorkloadStatusCard(Modifier.weight(1f), "Unavailable", "2", Color(0xFFEF4444))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Injured", "Suspended", "Questionable", "Available").forEach { tag ->
                    FilterChip(
                        selected = filter == tag,
                        onClick = { filter = tag },
                        label = { Text(tag, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(listOf(
            CoachAthleteStatusData("Hassan Said", "Winger • #11", "Injured", "Hamstring Tear (Grade 2)", "Oct 12, 2026", Color(0xFFEF4444)),
            CoachAthleteStatusData("Eric Otieno", "Defender • #3", "Suspended", "Red Card (1 match remaining)", "Sep 20, 2026", Color(0xFFA855F7)),
            CoachAthleteStatusData("Ali Juma", "Right Back • #2", "Questionable", "Minor Ankle Roll (Late fitness test)", "Tomorrow", Color(0xFFF59E0B)),
            CoachAthleteStatusData("Kamau Peter", "Midfielder • #6", "Questionable", "Flu / Illness (Resting at home)", "Sep 19, 2026", Color(0xFFF59E0B)),
            CoachAthleteStatusData("Sam Kuria", "Goalkeeper • #1", "Available", "Cleared from Concussion Protocol", "Active", Color(0xFF10B981))
        ).filter { filter == "All" || it.status == filter }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Text(item.role, fontSize = 11.sp, color = textMuted)
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = item.accentColor.copy(alpha = 0.1f)) {
                            Text(item.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = item.accentColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Reason: ${item.diagnosis}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = primaryColor)
                    Text("Expected Return: ${item.returnDate}", fontSize = 11.sp, color = textMuted)
                }
            }
        }
    }
}

data class CoachAthleteStatusData(
    val name: String,
    val role: String,
    val status: String,
    val diagnosis: String,
    val returnDate: String,
    val accentColor: Color
)

@Composable
fun CoachKpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    valColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = valColor)
        }
    }
}

@Composable
fun CoachQuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun CoachAlertItem(
    title: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    borderColor: Color,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
            Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(sub, fontSize = 11.sp, color = Color(0xFF475569))
            }
        }
    }
}

@Composable
fun TrainingMetricMiniCard(modifier: Modifier = Modifier, title: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }
    }
}

@Composable
fun WorkloadStatusCard(modifier: Modifier = Modifier, title: String, count: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(count, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
        }
    }
}

@Composable
fun BadgeChip(text: String, bg: Color, textCol: Color) {
    Surface(shape = RoundedCornerShape(6.dp), color = bg) {
        Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textCol, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
    }
}

@Composable
fun CoachMenuDrawer(
    selectedTab: String,
    onSelectTab: (String) -> Unit,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Coach Workspace", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    item { Text("CORE MODULES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp) }
                    item { DrawerLinkItem("Home & Command Center", Icons.Default.Home, selectedTab == "home") { onSelectTab("home") } }
                    item { DrawerLinkItem("My Teams", Icons.Default.Groups, selectedTab == "teams") { onSelectTab("teams") } }
                    item { DrawerLinkItem("Athletes Directory", Icons.Default.DirectionsRun, selectedTab == "athletes") { onSelectTab("athletes") } }
                    item { DrawerLinkItem("Training Management", Icons.Default.Timer, selectedTab == "training") { onSelectTab("training") } }
                    item { DrawerLinkItem("Attendance Tracking", Icons.Default.FactCheck, selectedTab == "attendance") { onSelectTab("attendance") } }
                    item { DrawerLinkItem("Performance Observations", Icons.Default.BarChart, selectedTab == "performance") { onSelectTab("performance") } }
                    item { DrawerLinkItem("Development Goals", Icons.Default.TrendingUp, selectedTab == "development") { onSelectTab("development") } }
                    item { DrawerLinkItem("Workload & ACWR", Icons.Default.MonitorHeart, selectedTab == "workload") { onSelectTab("workload") } }
                    item { DrawerLinkItem("Squad Availability", Icons.Default.EventAvailable, selectedTab == "availability") { onSelectTab("availability") } }
                    item { DrawerLinkItem("Match Day & Starting XI", Icons.Default.SportsScore, selectedTab == "match_day") { onSelectTab("match_day") } }
                    item { DrawerLinkItem("Analytics", Icons.Default.PieChart, selectedTab == "analytics") { onSelectTab("analytics") } }
                    item { DrawerLinkItem("Coach Alerts", Icons.Default.Notifications, selectedTab == "alerts") { onSelectTab("alerts") } }
                    item { DrawerLinkItem("Reports", Icons.Default.Description, selectedTab == "reports") { onSelectTab("reports") } }
                    item { DrawerLinkItem("Messages", Icons.Default.Message, selectedTab == "messages") { onSelectTab("messages") } }
                    item { DrawerLinkItem("Calendar", Icons.Default.CalendarMonth, selectedTab == "calendar") { onSelectTab("calendar") } }
                    item { DrawerLinkItem("Profile / Settings", Icons.Default.Settings, selectedTab == "settings") { onSelectTab("settings") } }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSignOut,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DrawerLinkItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color(0xFF0F172A) else Color.Transparent,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF64748B), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) Color.White else Color(0xFF1E293B))
        }
    }
}

@Composable
fun CreateTrainingModal(onDismiss: () -> Unit, onSave: (String, Int, Int, String) -> Unit) {
    var objective by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("120") }
    var intensity by remember { mutableStateOf("7") }
    var location by remember { mutableStateOf("Main Pitch") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Create Training Session", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = objective,
                    onValueChange = { objective = it },
                    label = { Text("Primary Objective") },
                    placeholder = { Text("e.g. Build up play under pressure...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (min)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = intensity, onValueChange = { intensity = it }, label = { Text("Intensity (1-10)") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (objective.isNotBlank()) {
                                onSave(objective, duration.toIntOrNull() ?: 90, intensity.toIntOrNull() ?: 7, location)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Text("Save Session")
                    }
                }
            }
        }
    }
}

@Composable
fun RecordAttendanceModal(athletes: List<AthleteEntity>, onDismiss: () -> Unit, onSave: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Record Attendance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("Today's Session • ${athletes.size} Squad Members", fontSize = 11.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(athletes) { athlete ->
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF8FAFC), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(athlete.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                                    Text(athlete.position, fontSize = 10.sp, color = Color(0xFF64748B))
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                                    Text("Present", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Attendance")
                }
            }
        }
    }
}

@Composable
fun SelectSquadModal(athletes: List<AthleteEntity>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Squad Selection (Starting XI)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("Formation: 4-3-3 • Total Roster: ${athletes.size} Athletes", fontSize = 12.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(athletes.take(11)) { athlete ->
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(athlete.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(athlete.position, fontSize = 11.sp, color = Color(0xFF0D9488))
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF)) {
                                    Text("Starting XI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))) {
                    Text("Confirm Starting XI")
                }
            }
        }
    }
}
