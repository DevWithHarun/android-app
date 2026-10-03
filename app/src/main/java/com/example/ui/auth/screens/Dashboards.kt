package com.example.ui.auth.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.auth.UserRole
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericDashboardScreen(
    title: String,
    role: UserRole,
    userName: String = "User",
    onSignOut: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = { Text(title, color = primaryColor, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = secondaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Good day, $userName 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = secondaryColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Role: ${role.value.uppercase()}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = secondaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState = TalentUiState(),
    userName: String = "Athlete",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("home") }
    var devTab by rememberSaveable { mutableStateOf("goals") }
    var showActionModal by remember { mutableStateOf(false) }
    var showMenuDrawer by remember { mutableStateOf(false) }
    var showEditProfileModal by remember { mutableStateOf(false) }
    var showUploadVideoModal by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    val greeting = remember(userName) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
        "$timeGreeting, $userName 👋"
    }

    BackHandler(enabled = selectedTab != "home") {
        selectedTab = "home"
    }

    Scaffold(
        containerColor = surfaceColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when(selectedTab) {
                            "home" -> "Command Center"
                            "career" -> "My Career"
                            "teams" -> "My Teams & Squads"
                            "documents" -> "Documents & Vault"
                            "performance" -> "Performance Analytics"
                            "development" -> "Development Suite"
                            "intelligence" -> "Athlete Intelligence Hub"
                            "availability" -> "Availability & Clearance"
                            "workload" -> "Workload & ACWR Monitor"
                            "network" -> "Scouting & Network Hub"
                            "opportunities" -> "Opportunities & Trials"
                            "scout_activity" -> "Scout Radar & Activity"
                            "profile" -> "My Profile"
                            "messages" -> "Messages & Chat"
                            else -> "Athlete Workspace"
                        },
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
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
                    IconButton(onClick = { selectedTab = "messages" }) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Messages", tint = textMuted)
                    }
                    IconButton(onClick = { selectedTab = "profile" }) {
                        if (uiState.userPhotoUrl.isNotBlank()) {
                            AsyncImage(
                                model = getMediaModel(uiState.userPhotoUrl),
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, secondaryColor.copy(alpha = 0.4f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(secondaryColor.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, secondaryColor.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(userName.take(2).uppercase(), color = secondaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)
            )
        },
        bottomBar = {
            // Exact Bottom Navigation: Home | Career | + | Alerts | Profile
            Surface(
                color = Color.Transparent,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavIcon(icon = Icons.Default.Home, label = "Home", isSelected = selectedTab == "home") { selectedTab = "home" }
                    BottomNavIcon(icon = Icons.Default.Route, label = "Career", isSelected = selectedTab == "career") { selectedTab = "career" }
                    
                    // Floating Plus Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(secondaryColor, CircleShape)
                            .clickable { showActionModal = true }
                            .border(4.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Quick Actions", tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    BottomNavIcon(icon = Icons.Outlined.Notifications, label = "Alerts", isSelected = selectedTab == "alerts") { selectedTab = "alerts" }
                    BottomNavIcon(icon = Icons.Outlined.Person, label = "Profile", isSelected = selectedTab == "profile") { selectedTab = "profile" }
                }
            }
        }
    ) { paddingVals ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingVals)) {
            if (viewModel != null) {
                when (selectedTab) {
                    "career" -> CareerModuleView(viewModel, uiState)
                    "teams" -> TeamsModuleView(viewModel, uiState)
                    "documents" -> DocumentsModuleView(viewModel, uiState)
                    "performance" -> PerformanceModuleView(viewModel, uiState)
                    "training" -> DevelopmentModuleView(viewModel, uiState, initialTab = "training")
                    "development" -> DevelopmentModuleView(viewModel, uiState, initialTab = devTab)
                    "intelligence" -> IntelligenceModuleView(viewModel, uiState, onNavigateTab = { tab -> selectedTab = tab })
                    "availability" -> AvailabilityModuleView(viewModel, uiState)
                    "workload" -> WorkloadModuleView(viewModel, uiState)
                    "network" -> NetworkModuleView(viewModel, uiState, onNavigateTab = { tab -> selectedTab = tab })
                    "opportunities" -> OpportunitiesModuleView(viewModel, uiState)
                    "scout_activity" -> ScoutActivityModuleView(viewModel, uiState)
                    "messages" -> MessagesModuleView(viewModel, uiState)
                    "profile" -> ProfileModuleView(viewModel, uiState)
                    "settings" -> SettingsModuleView(viewModel, uiState, onSignOut)
                    else -> HomeDashboardContent(
                        greeting,
                        cardBg,
                        BorderStroke(1.dp, borderColor),
                        secondaryColor,
                        Color(0xFF10B981),
                        onSignOut,
                        { tab -> selectedTab = tab },
                        { showActionModal = true },
                        { showUploadVideoModal = true },
                        uiState
                    )
                }
            } else {
                HomeDashboardContent(
                    greeting,
                    cardBg,
                    BorderStroke(1.dp, borderColor),
                    secondaryColor,
                    Color(0xFF10B981),
                    onSignOut,
                    { tab -> selectedTab = tab },
                    { showActionModal = true },
                    { showUploadVideoModal = true },
                    uiState
                )
            }
        }
    }

    // Dedicated Upload Video Dialog from Local Storage
    if (showUploadVideoModal) {
        UploadVideoDialog(
            onDismiss = { showUploadVideoModal = false },
            onUpload = { title, kpi, url, fileName ->
                viewModel?.updateUserProfile(
                    mapOf(
                        "videoTitle" to title,
                        "videoKpi" to kpi,
                        "videoUrl" to url,
                        "videoFileName" to fileName
                    )
                )
                showUploadVideoModal = false
            }
        )
    }

    // Quick Actions Bottom Dialog (Modal)
    if (showActionModal) {
        Dialog(onDismissRequest = { showActionModal = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = primaryColor)
                        IconButton(onClick = { showActionModal = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = textMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModalActionTile("Add Achievement", Icons.Default.EmojiEvents, Color(0xFFFEF3C7), Color(0xFFB45309), Modifier.weight(1f)) { showActionModal = false }
                        ModalActionTile("Upload Video", Icons.Default.VideoCall, Color(0xFFF3E8FF), Color(0xFF7E22CE), Modifier.weight(1f)) {
                            showActionModal = false
                            showUploadVideoModal = true
                        }
                        ModalActionTile("Add Document", Icons.Default.Description, Color(0xFFF1F5F9), Color(0xFF334155), Modifier.weight(1f)) { showActionModal = false }
                        ModalActionTile("Report Availability", Icons.Default.EventAvailable, Color(0xFFD1FAE5), Color(0xFF047857), Modifier.weight(1f)) { showActionModal = false }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModalActionTile("Add Training", Icons.Default.FitnessCenter, Color(0xFFDBEAFE), Color(0xFF1D4ED8), Modifier.weight(1f)) { showActionModal = false }
                        ModalActionTile("Request Verification", Icons.Default.Verified, Color(0xFFE0E7FF), Color(0xFF4338CA), Modifier.weight(1f)) { showActionModal = false }
                        ModalActionTile("Apply Opportunity", Icons.Default.Work, Color(0xFFFFEDD5), Color(0xFFC2410C), Modifier.weight(1f)) { showActionModal = false }
                        ModalActionTile("Share Profile", Icons.Default.Share, Color(0xFFFFE4E6), Color(0xFFBE123C), Modifier.weight(1f)) { showActionModal = false }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { showActionModal = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor)) {
                            Text("Settings", fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { showActionModal = false; onSignOut() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626))) {
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Menu Drawer (All Modules)
    if (showMenuDrawer) {
        Dialog(onDismissRequest = { showMenuDrawer = false }) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = surfaceColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(32.dp).background(primaryColor, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Text("All Modules", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                        }
                        IconButton(onClick = { showMenuDrawer = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = textMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { DrawerGroupTitle("Identity & Career") }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "profile"; showMenuDrawer = false }) {
                                DrawerModuleItem("My Profile", Icons.Outlined.Badge, secondaryColor)
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "career"; showMenuDrawer = false }) {
                                DrawerModuleItem("My Career", Icons.Default.Route, secondaryColor)
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "teams"; showMenuDrawer = false }) {
                                DrawerModuleItem("My Teams", Icons.Default.Groups, secondaryColor)
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "documents"; showMenuDrawer = false }) {
                                DrawerModuleItem("Documents", Icons.Outlined.FolderOpen, secondaryColor)
                            }
                        }

                        item { DrawerGroupTitle("Performance") }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "performance"; showMenuDrawer = false }) {
                                DrawerModuleItem("Performance", Icons.Default.BarChart, Color(0xFF3B82F6))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "performance"; showMenuDrawer = false }) {
                                DrawerModuleItem("Matches", Icons.Default.Timer, Color(0xFF3B82F6))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "performance"; showMenuDrawer = false }) {
                                DrawerModuleItem("Physical Profile", Icons.Default.MonitorHeart, Color(0xFF3B82F6))
                            }
                        }

                        item { DrawerGroupTitle("Development") }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "development"; devTab = "goals"; showMenuDrawer = false }) {
                                DrawerModuleItem("Development Goals", Icons.Default.TrendingUp, Color(0xFF10B981))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "development"; devTab = "training"; showMenuDrawer = false }) {
                                DrawerModuleItem("Training History", Icons.Default.FitnessCenter, Color(0xFF10B981))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "development"; devTab = "achievements"; showMenuDrawer = false }) {
                                DrawerModuleItem("Achievements", Icons.Default.EmojiEvents, Color(0xFF10B981))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "development"; devTab = "evidence"; showMenuDrawer = false }) {
                                DrawerModuleItem("Videos & Evidence", Icons.Default.VideoLibrary, Color(0xFF10B981))
                            }
                        }

                        item { DrawerGroupTitle("Intelligence") }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "intelligence"; showMenuDrawer = false }) {
                                DrawerModuleItem("Intelligence Hub", Icons.Default.Psychology, Color(0xFFA855F7))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "availability"; showMenuDrawer = false }) {
                                DrawerModuleItem("Availability & Clearance", Icons.Default.EventAvailable, Color(0xFFA855F7))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "workload"; showMenuDrawer = false }) {
                                DrawerModuleItem("Workload & ACWR", Icons.Default.BatteryChargingFull, Color(0xFFA855F7))
                            }
                        }

                        item { DrawerGroupTitle("Network & Scouting") }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "network"; showMenuDrawer = false }) {
                                DrawerModuleItem("Scouting & Network Hub", Icons.Default.Radar, Color(0xFF2563EB))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "opportunities"; showMenuDrawer = false }) {
                                DrawerModuleItem("Opportunities & Trials", Icons.Default.Work, Color(0xFF2563EB))
                            }
                        }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "scout_activity"; showMenuDrawer = false }) {
                                DrawerModuleItem("Scout Activity & Radar", Icons.Default.Visibility, Color(0xFF2563EB))
                            }
                        }

                        item { DrawerGroupTitle("Protection & Infrastructure") }
                        item { DrawerModuleItem("Risk & Protection", Icons.Default.Security, Color(0xFFF97316)) }
                        item { DrawerModuleItem("Insurance & Claims", Icons.Default.HealthAndSafety, Color(0xFFF97316)) }
                        item { DrawerModuleItem("Calendar", Icons.Default.CalendarMonth, Color(0xFF64748B)) }

                        item { DrawerGroupTitle("Administration") }
                        item { DrawerModuleItem("Payments", Icons.Default.CreditCard, Color(0xFF059669)) }
                        item { DrawerModuleItem("Privacy & Permissions", Icons.Default.Lock, Color(0xFF64748B)) }
                        item {
                            Box(modifier = Modifier.clickable { selectedTab = "settings"; showMenuDrawer = false }) {
                                DrawerModuleItem("Account Settings", Icons.Default.Settings, Color(0xFF64748B))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeDashboardContent(
    greeting: String,
    cardBg: Color,
    borderColor: BorderStroke,
    secondaryColor: Color,
    emeraldColor: Color,
    onSignOut: () -> Unit,
    onNavigateTab: (String) -> Unit,
    onOpenActionModal: () -> Unit,
    onUploadVideo: () -> Unit = onOpenActionModal,
    uiState: TalentUiState? = null
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    val primaryClubName = uiState?.clubs?.firstOrNull()?.clubName ?: uiState?.careerStints?.firstOrNull()?.organization ?: "Free Agent"
    val currentPos = (uiState?.userData?.get("position") as? String ?: uiState?.careerStints?.firstOrNull()?.position ?: "Unassigned")
    val totalMins = uiState?.matchLogs?.sumOf { it.minutesPlayed }?.toString() ?: "0"
    val totalGoals = uiState?.matchLogs?.sumOf { it.goals }?.toString() ?: "0"
    val totalAssists = uiState?.matchLogs?.sumOf { it.assists }?.toString() ?: "0"
    val avgRating = if (!uiState?.matchLogs.isNullOrEmpty()) String.format("%.0f%%", (uiState.matchLogs.map { it.matchRating }.average() * 10)) else "0%"

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: Welcome & Completeness
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = greeting,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val fid = (uiState?.userData?.get("federationId") as? String)?.takeIf { it.isNotBlank() } ?: "TG-ATHLETE"
                            Text(
                                text = "Talent Graph ID: #$fid",
                                fontSize = 12.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                VerificationBadge("Identity Verified")
                                if (uiState?.clubs?.isNotEmpty() == true) {
                                    VerificationBadge("Club Verified")
                                }
                                if (uiState?.careerStints?.isNotEmpty() == true) {
                                    VerificationBadge("Stint Verified")
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (uiState?.careerStints?.isNotEmpty() == true) "85%" else "40%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = secondaryColor
                            )
                            Text(
                                text = "Completeness",
                                fontSize = 10.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Current Status & Organization Affiliation Card
        item {
            val activeMembership = uiState?.memberships?.firstOrNull { it.status == "ACTIVE" }
            val primaryOrg = uiState?.organizations?.firstOrNull { it.id == (activeMembership?.orgId ?: 1L) }
                ?: uiState?.organizations?.firstOrNull()

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab("teams") }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CorporateFare, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ORGANIZATION & AFFILIATION", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        }
                        Text("Manage Affiliations →", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatusItem("Current Club", primaryOrg?.name ?: primaryClubName)
                        StatusItem("Team", activeMembership?.teamName ?: "First Team")
                        StatusItem("Membership", if (activeMembership != null) "Active" else "Free Agent", isBadge = true, badgeColor = emeraldColor)
                        StatusItem("Join Code", primaryOrg?.joinCode ?: "MUFC-7K92X")
                    }
                }
            }
        }

        // Performance Snapshot
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier.fillMaxWidth().clickable { onNavigateTab("performance") }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PERFORMANCE SNAPSHOT", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        }
                        Text("View Analytics →", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBox(totalMins, "Minutes", modifier = Modifier.weight(1f))
                        StatBox(totalGoals, "Goals", modifier = Modifier.weight(1f))
                        StatBox(totalAssists, "Assists", modifier = Modifier.weight(1f))
                        StatBox(avgRating, "Rating", accentColor = secondaryColor, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Intelligence & Readiness Snapshot
        item {
            val latestAvail = uiState?.availabilityRecords?.firstOrNull()
            val isAvail = latestAvail == null || latestAvail.status.contains("Available", ignoreCase = true) || latestAvail.status.contains("Fit", ignoreCase = true)
            val weekAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
            val monthAgo = System.currentTimeMillis() - (28L * 24 * 60 * 60 * 1000)
            val sevenDayLoad = uiState?.matchLogs?.filter { it.timestamp >= weekAgo }?.sumOf { it.minutesPlayed } ?: 0
            val twentyEightDayLoad = uiState?.matchLogs?.filter { it.timestamp >= monthAgo }?.sumOf { it.minutesPlayed } ?: 0
            val acwr = if (twentyEightDayLoad > 0) String.format("%.2f", sevenDayLoad.toDouble() / (twentyEightDayLoad.toDouble() / 4.0)) else "1.00"

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier.fillMaxWidth().clickable { onNavigateTab("intelligence") }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ATHLETE INTELLIGENCE & READINESS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        }
                        Text("View Hub →", fontSize = 11.sp, color = Color(0xFFA855F7), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBox(if (isAvail) "Cleared" else "Knock", "Availability", accentColor = if (isAvail) emeraldColor else Color(0xFFDC2626), modifier = Modifier.weight(1f))
                        StatBox(acwr, "ACWR Ratio", accentColor = Color(0xFFA855F7), modifier = Modifier.weight(1f))
                        StatBox("${sevenDayLoad}m", "7-Day Load", modifier = Modifier.weight(1f))
                        StatBox("Optimal", "Injury Risk", accentColor = emeraldColor, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Scouting & Network Snapshot
        item {
            val scoutViews = uiState?.scoutActivities?.size ?: 0
            val uniqueScouts = uiState?.scoutActivities?.map { it.viewerName }?.distinct()?.size ?: 0
            val oppsCount = uiState?.opportunities?.size ?: 0

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier.fillMaxWidth().clickable { onNavigateTab("network") }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SCOUTING NETWORK & RADAR", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        }
                        Text("View Network →", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBox("$scoutViews", "Scout Views", accentColor = Color(0xFF2563EB), modifier = Modifier.weight(1f))
                        StatBox("$uniqueScouts", "Clubs Watching", modifier = Modifier.weight(1f))
                        StatBox("$oppsCount", "Trials Available", modifier = Modifier.weight(1f))
                        StatBox("Active", "Radar Status", accentColor = emeraldColor, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Actions & Navigation
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = borderColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("QUICK ACTIONS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    QuickActionButton("Athlete Intelligence Hub", Icons.Default.Psychology) { onNavigateTab("intelligence") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Scouting & Network Hub", Icons.Default.Radar) { onNavigateTab("network") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Performance Analytics", Icons.Default.BarChart) { onNavigateTab("performance") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Development Suite", Icons.Default.TrendingUp) { onNavigateTab("development") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Availability & Clearance", Icons.Default.EventAvailable) { onNavigateTab("availability") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Workload & ACWR Monitor", Icons.Default.BatteryChargingFull) { onNavigateTab("workload") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("My Teams & Squad", Icons.Default.Groups) { onNavigateTab("teams") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Documents Vault", Icons.Outlined.FolderOpen) { onNavigateTab("documents") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Scout Activity & Radar", Icons.Default.Visibility) { onNavigateTab("scout_activity") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("My Career & Stints", Icons.Default.Route) { onNavigateTab("career") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("View Profile", Icons.Outlined.Person) { onNavigateTab("profile") }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("Upload Video", Icons.Default.VideoCall) { onUploadVideo() }
                    Spacer(modifier = Modifier.height(8.dp))
                    QuickActionButton("View Opportunities", Icons.Default.Work, isPrimary = true, primaryColor = secondaryColor) { onNavigateTab("opportunities") }
                }
            }
        }
    }
}

@Composable
fun BottomNavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val activeColor = Color(0xFF0D9488)
    val inactiveColor = Color(0xFF64748B)
    val color = if (isSelected) activeColor else inactiveColor

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun VerificationBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFECFDF5),
        border = BorderStroke(1.dp, Color(0xFFD1FAE5))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
        }
    }
}

@Composable
fun StatusItem(label: String, value: String, isBadge: Boolean = false, badgeColor: Color = Color.Green) {
    Column {
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(3.dp))
        if (isBadge) {
            Surface(shape = RoundedCornerShape(4.dp), color = badgeColor.copy(alpha = 0.15f)) {
                Text(value, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = badgeColor)
            }
        } else {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        }
    }
}

@Composable
fun StatBox(value: String, label: String, accentColor: Color = Color(0xFF0F172A), modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = accentColor)
            Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isPrimary: Boolean = false, primaryColor: Color = Color(0xFF0D9488), onClick: () -> Unit) {
    val bg = if (isPrimary) Color(0xFFF0FDFA) else Color(0xFFF8FAFC)
    val textColor = if (isPrimary) primaryColor else Color(0xFF1E293B)
    val border = if (isPrimary) BorderStroke(1.dp, Color(0xFFCCFBF1)) else BorderStroke(1.dp, Color(0xFFF1F5F9))

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = border,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }
}

@Composable
fun ModalActionTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = tint),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 2)
        }
    }
}

@Composable
fun DrawerGroupTitle(title: String) {
    Text(title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8), letterSpacing = 1.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp))
}

@Composable
fun DrawerModuleItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun UploadVideoDialog(
    onDismiss: () -> Unit,
    onUpload: (title: String, kpi: String, url: String, fileName: String) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var kpiTag by remember { mutableStateOf("Perform") }
    var videoUrl by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf("") }

    val videoContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            selectedFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "highlight_reel", "mp4")
            videoUrl = savedPath
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".")
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            selectedFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "highlight_reel", "mp4")
            videoUrl = savedPath
            if (title.isBlank()) {
                title = fileName.substringBeforeLast(".")
            }
        }
    }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload Highlight Reel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = primaryColor
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                .clickable {
                                    videoContentLauncher.launch("video/*")
                                }
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFFF3E8FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Select Video from Local Storage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "MP4, MKV, MOV, WebM from Gallery / Files",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { videoContentLauncher.launch("video/*") },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Browse Files", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        videoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gallery Picker", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    val (sampleUrl, sampleName) = createSampleLocalVideo(context)
                                    videoUrl = sampleUrl
                                    selectedFileName = sampleName
                                    if (title.isBlank()) {
                                        title = "Sprint Mechanics & Box-to-Box Reel"
                                    }
                                }
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Quick Test: Use Sample Highlight Video", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (selectedFileName.isNotBlank()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, Color(0xFFD1FAE5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                    Column {
                                        Text("Selected Video:", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                        Text(selectedFileName, fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            placeholder = { Text("e.g. Sprint mechanics & 1v1 Highlights") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Linked KPI Focus", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Perform", "Effic.", "Consist.", "Risk").forEach { tag ->
                                val isSelected = kpiTag == tag
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) secondaryColor else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { kpiTag = tag }
                                ) {
                                    Text(
                                        text = tag,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text("Video URL / File Path") },
                            placeholder = { Text("Selected local file or https://...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = textMuted, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val uploadTitle = if (title.isNotBlank()) title else "Highlight Reel"
                            onUpload(uploadTitle, kpiTag, videoUrl, selectedFileName)
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Upload Video", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
