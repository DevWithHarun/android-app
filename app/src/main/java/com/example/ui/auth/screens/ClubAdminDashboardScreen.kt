package com.example.ui.auth.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAdminDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState? = null,
    userName: String = "Admin",
    clubName: String = "Official Club",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val realViewModel = remember { viewModel ?: TalentViewModel(context.applicationContext as android.app.Application) }
    val realUiState = uiState ?: TalentUiState()

    val clubDashboardViewModel: ClubDashboardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val clubDashboardState by clubDashboardViewModel.uiState.collectAsStateWithLifecycle()

    var currentSection by rememberSaveable { mutableStateOf("overview") }
    var currentSubTabIndex by rememberSaveable { mutableIntStateOf(0) }

    fun navigateToSection(section: String, subTabIndex: Int = 0) {
        currentSection = section
        currentSubTabIndex = subTabIndex
    }

    fun navigateToLegacyKey(key: String) {
        when (key.lowercase()) {
            "overview", "home", "dashboard" -> navigateToSection("overview", 0)
            "team", "teams" -> navigateToSection("team", 0)
            "athletes", "squad" -> navigateToSection("team", 1)
            "club", "profile" -> navigateToSection("club", 0)
            "documents", "vault" -> navigateToSection("club", 1)
            "registrations", "clearances" -> navigateToSection("club", 1)
            "people", "staff" -> navigateToSection("people", 0)
            "match", "matches", "fixtures" -> navigateToSection("match", 0)
            "training", "workload" -> navigateToSection("match", 1)
            "calendar" -> navigateToSection("match", 2)
            "talent", "recruitment" -> navigateToSection("talent", 0)
            "scouts", "trials" -> navigateToSection("talent", 1)
            "availability", "protection" -> navigateToSection("talent", 2)
            "insights", "performance" -> navigateToSection("insights", 0)
            "intelligence" -> navigateToSection("insights", 0)
            "reports" -> navigateToSection("reports", 0)
            "comms", "announcements", "feed" -> navigateToSection("comms", 0)
            "messages", "broadcast" -> navigateToSection("comms", 1)
            "fin", "finance", "billing", "mpesa" -> navigateToSection("fin", 0)
            "admin", "permissions", "users" -> navigateToSection("admin", 0)
            "audit" -> navigateToSection("admin", 1)
            "verification", "data" -> navigateToSection("admin", 2)
            "settings" -> navigateToSection("admin", 3)
            "alerts", "risk" -> navigateToSection("alerts", 0)
            "stats", "statistics", "hub", "stat" -> navigateToSection("stats", 0)
            else -> navigateToSection("overview", 0)
        }
    }

    var showMenuDrawer by remember { mutableStateOf(false) }
    var showCreateActionSheet by remember { mutableStateOf(false) }
    var showUpdateProfileDialog by remember { mutableStateOf(false) }
    var showAddAthleteDialog by remember { mutableStateOf(false) }
    var showInviteStaffDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<String?>(null) }
    var adminPhotoUrl by rememberSaveable { mutableStateOf(realUiState.userPhotoUrl) }

    val feedbackMessage by clubDashboardViewModel.actionFeedback.collectAsStateWithLifecycle(initialValue = "")
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage.isNotBlank()) {
            noticeMessage = feedbackMessage
        }
    }

    LaunchedEffect(realUiState.userPhotoUrl) {
        if (realUiState.userPhotoUrl.isNotBlank()) {
            adminPhotoUrl = realUiState.userPhotoUrl
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriString = uri.toString()
            adminPhotoUrl = uriString
            realViewModel.updateUserProfile(mapOf("photoUrl" to uriString))
            showUpdateProfileDialog = false
            noticeMessage = "Admin profile photo updated from local storage!"
        }
    }

    // Exact Talent Graph Boilerplate Palette
    val bg = Color(0xFF0A0E13)
    val panel = Color(0xFF101823)
    val panel2 = Color(0xFF141E2B)
    val panel3 = Color(0xFF1A2634)
    val line = Color(0xFF22303F)
    val txt = Color(0xFFE7EEF7)
    val mut = Color(0xFF89A0B8)
    val acc = Color(0xFF3DDC97)      // Electric Mint
    val acc2 = Color(0xFF4AA8FF)     // Sky Blue
    val warn = Color(0xFFFFB454)     // Amber
    val bad = Color(0xFFFF6B6B)      // Coral Red
    val purple = Color(0xFFA78BFA)   // Lilac Purple

    // Theme Aliases for consistent styling across dialogs & subviews
    val brandPurple = acc
    val softPurple = panel2
    val inkColor = txt
    val primaryColor = txt
    val purpleAccent = acc
    val tealAccent = acc2
    val surfaceColor = bg
    val cardBg = panel
    val borderColor = line
    val textMuted = mut
    val redColor = bad

    BackHandler(enabled = currentSection != "overview") {
        currentSection = "overview"
        currentSubTabIndex = 0
    }

    val activeClubName = when (val state = clubDashboardState) {
        is ClubDashboardUiState.Success -> state.clubDetails?.clubName?.takeIf { it.isNotBlank() } ?: state.clubMember.clubName.ifBlank { clubName }
        else -> clubName
    }
    val adminFullName = when (val state = clubDashboardState) {
        is ClubDashboardUiState.Success -> state.clubMember.fullName
        else -> userName
    }

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val clubLogoUrl = when (val state = clubDashboardState) {
                            is ClubDashboardUiState.Success -> state.clubDetails?.logoUrl?.takeIf { it.isNotBlank() }
                            else -> null
                        }

                        // Logo Button with mint-to-blue gradient
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(Brush.linearGradient(listOf(acc, acc2))),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!clubLogoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = clubLogoUrl,
                                    contentDescription = "Club Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = activeClubName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "TG" }.uppercase(),
                                    color = Color(0xFF04121A),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeClubName.ifBlank { "Talent Graph FC" },
                                    color = txt,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Verified Club",
                                    tint = acc,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "Football · Club Admin",
                                color = mut,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showMenuDrawer = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation Menu", tint = txt)
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = txt)
                    }
                    val alertSignalsCount = (clubDashboardState as? ClubDashboardUiState.Success)?.signals?.size ?: 1
                    IconButton(onClick = { navigateToSection("alerts", 0) }) {
                        BadgedBox(badge = {
                            Badge(containerColor = bad, contentColor = Color.White) {
                                Text(alertSignalsCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }) {
                            Icon(Icons.Default.Chat, contentDescription = "Inbox", tint = txt)
                        }
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { showUpdateProfileDialog = true }
                    ) {
                        val displayPhoto = adminPhotoUrl.takeIf { it.isNotBlank() } ?: realUiState.userPhotoUrl
                        if (displayPhoto.isNotBlank()) {
                            AsyncImage(
                                model = displayPhoto,
                                contentDescription = "Admin Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(purple, acc2))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(adminFullName.take(2).uppercase().ifBlank { "AD" }, color = Color(0xFF0A0E13), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = panel)
            )
        },
        bottomBar = {
            // Boilerplate bottom-bar: Home, Squad, Plus button, Inbox, More
            Surface(
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Home (Overview)
                    BoilerplateBottomItem(
                        label = "Home",
                        icon = Icons.Default.GridView,
                        isSelected = currentSection == "overview",
                        activeColor = acc,
                        inactiveColor = mut,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToSection("overview", 0) }
                    )

                    // 2. Squad (Maintained teams tab as requested!)
                    BoilerplateBottomItem(
                        label = "Squad",
                        icon = Icons.Default.Groups,
                        isSelected = currentSection == "team",
                        activeColor = acc,
                        inactiveColor = mut,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToSection("team", 0) }
                    )

                    // 3. Center Elevated Plus Button
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = acc,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(42.dp)
                                .clickable { showAddAthleteDialog = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Quick Add",
                                    tint = Color(0xFF04121A),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // 4. Inbox (Messages)
                    BoilerplateBottomItem(
                        label = "Inbox",
                        icon = Icons.Default.Chat,
                        isSelected = currentSection == "comms",
                        activeColor = acc,
                        inactiveColor = mut,
                        modifier = Modifier.weight(1f),
                        onClick = { navigateToSection("comms", 0) }
                    )

                    // 5. More (Drawer)
                    BoilerplateBottomItem(
                        label = "More",
                        icon = Icons.Default.MoreHoriz,
                        isSelected = showMenuDrawer,
                        activeColor = acc,
                        inactiveColor = mut,
                        modifier = Modifier.weight(1f),
                        onClick = { showMenuDrawer = true }
                    )
                }
            }
        }
    ) { paddingValues ->
        val sectionSubTabs = when (currentSection) {
            "club" -> listOf("Club Profile", "Documents")
            "people" -> listOf("People Directory")
            "team" -> listOf("Teams & Competitions", "Athletes & Squad")
            "match" -> listOf("Matches & Fixtures", "Training & Attendance", "Master Calendar")
            "talent" -> listOf("Recruitment Pipeline", "Scouts & Trials", "Availability Status")
            "insights" -> listOf("Performance Lab")
            "comms" -> listOf("Announcements & Feed", "Messages & Broadcast")
            "admin" -> listOf("Permissions & RBAC", "Audit Log", "Data Verification", "System Settings")
            "fin" -> listOf("Treasury & M-Pesa")
            "reports" -> listOf("Federation Reports")
            "alerts" -> listOf("Risk & Alerts", "Notifications")
            "stats" -> listOf("Players", "Teams", "Goals", "Discipline")
            else -> emptyList()
        }

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Section Sub-Tabs Row (Only displayed when there are multiple functional sub-tabs)
            if (currentSection != "overview" && sectionSubTabs.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(panel)
                        .border(BorderStroke(1.dp, line))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    sectionSubTabs.forEachIndexed { index, tabTitle ->
                        val isSelected = index == currentSubTabIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) panel2 else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) acc else line),
                            modifier = Modifier.clickable {
                                currentSubTabIndex = index
                            }
                        ) {
                            Text(
                                text = tabTitle,
                                color = if (isSelected) acc else mut,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.5.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Module Screen Content Switcher
            Box(modifier = Modifier.weight(1f)) {
                when (currentSection) {
                    "club" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubProfileModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> ClubDocumentsModuleView(clubDashboardState = clubDashboardState)
                        }
                    }
                    "people" -> {
                        ClubPeopleModuleView(
                            clubDashboardViewModel = clubDashboardViewModel,
                            clubDashboardState = clubDashboardState,
                            activeClubName = activeClubName
                        )
                    }
                    "team" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubTeamsAndCompetitionsView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> ClubAthletesModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName,
                                onAddAthlete = { showAddAthleteDialog = true }
                            )
                        }
                    }
                    "match" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubMatchesAndFixturesView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            1 -> ClubTrainingModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> ClubCalendarModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                        }
                    }
                    "talent" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubRecruitmentModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            1 -> ClubScoutsModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> ClubAvailabilityAndProtectionView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                        }
                    }
                    "fin" -> {
                        ClubFinanceAndPaymentsModuleView(
                            clubDashboardViewModel = clubDashboardViewModel,
                            clubDashboardState = clubDashboardState,
                            activeClubName = activeClubName
                        )
                    }
                    "reports" -> {
                        ClubReportsModuleView(
                            clubDashboardViewModel = clubDashboardViewModel,
                            clubDashboardState = clubDashboardState,
                            activeClubName = activeClubName
                        )
                    }
                    "insights" -> {
                        ClubPerformanceModuleView(
                            clubDashboardViewModel = clubDashboardViewModel,
                            clubDashboardState = clubDashboardState,
                            activeClubName = activeClubName
                        )
                    }
                    "comms" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubAnnouncementsModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> ClubMessagesModuleView(activeClubName = activeClubName)
                        }
                    }
                    "admin" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubPermissionsModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            1 -> ClubAuditModuleView(clubDashboardState = clubDashboardState)
                            2 -> ClubDataVerificationModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName
                            )
                            else -> SettingsModuleView(
                                viewModel = realViewModel,
                                uiState = realUiState,
                                onSignOut = onSignOut
                            )
                        }
                    }
                    "alerts" -> {
                        when (currentSubTabIndex) {
                            0 -> ClubRiskAndAlertsModuleView(
                                clubDashboardViewModel = clubDashboardViewModel,
                                clubDashboardState = clubDashboardState,
                                activeClubName = activeClubName,
                                onNavigateTab = { navigateToLegacyKey(it) }
                            )
                            else -> ClubNotificationsSubView(
                                clubDashboardState = clubDashboardState,
                                realUiState = realUiState
                            )
                        }
                    }
                    "stats" -> {
                        ClubStatisticsHubView(
                            clubDashboardViewModel = clubDashboardViewModel,
                            clubDashboardState = clubDashboardState,
                            activeClubName = activeClubName,
                            selectedTabIndex = currentSubTabIndex,
                            onTabSelected = { currentSubTabIndex = it }
                        )
                    }
                    else -> {
                        // "overview" Command Center
                        when (val state = clubDashboardState) {
                            is ClubDashboardUiState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = brandPurple)
                                }
                            }
                            is ClubDashboardUiState.NoClubFound -> {
                                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, borderColor)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(48.dp))
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("No Active Club Membership", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("No active club membership document was found for your account in Firestore (`club_members` where status == 'active').", fontSize = 13.sp, color = textMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Button(onClick = onSignOut, colors = ButtonDefaults.buttonColors(containerColor = brandPurple)) {
                                                Text("Sign Out / Switch Account", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                            is ClubDashboardUiState.Error -> {
                                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Error: ${state.message}", color = redColor)
                                }
                            }
                            is ClubDashboardUiState.Success -> {
                                val overviewViewModel: com.example.ui.auth.viewmodel.ClubAdminOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                                ClubAdminOverviewScreen(
                                    viewModel = overviewViewModel,
                                    clubId = state.clubMember.clubId,
                                    clubName = activeClubName,
                                    onNavigateToSection = { sec, sub -> navigateToSection(sec, sub) },
                                    onAddAthlete = { showAddAthleteDialog = true },
                                    onInviteStaff = { showInviteStaffDialog = true },
                                    onCreateTeam = { navigateToSection("team", 0) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Quick Action Sheet
    if (showCreateActionSheet) {
        ClubCreateActionSheet(
            onDismiss = { showCreateActionSheet = false },
            onActionSelected = { actionKey ->
                showCreateActionSheet = false
                when (actionKey) {
                    "athlete" -> showAddAthleteDialog = true
                    "team" -> navigateToSection("team", 0)
                    "staff" -> showInviteStaffDialog = true
                    "match" -> navigateToSection("match", 0)
                    "training" -> navigateToSection("match", 1)
                    "document" -> navigateToSection("club", 1)
                    "announcement" -> navigateToSection("comms", 0)
                    "opportunity" -> navigateToSection("comms", 2)
                    "register" -> navigateToSection("club", 2)
                    "report" -> navigateToSection("insights", 3)
                    else -> navigateToLegacyKey(actionKey)
                }
            }
        )
    }

    // Hamburger Menu Drawer
    if (showMenuDrawer) {
        ClubMenuDrawer(
            activeClubName = activeClubName,
            currentSection = currentSection,
            currentSubTabIndex = currentSubTabIndex,
            onDismiss = { showMenuDrawer = false },
            onNavigate = { section, subIndex ->
                navigateToSection(section, subIndex)
            },
            onSignOut = onSignOut
        )
    }

    // Search Dialog
    if (showSearchDialog) {
        ClubSearchDialog(onDismiss = { showSearchDialog = false })
    }

    // Update Profile Photo Dialog
    if (showUpdateProfileDialog) {
        Dialog(onDismissRequest = { showUpdateProfileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Admin Profile Photo",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        IconButton(onClick = { showUpdateProfileDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayPhoto = adminPhotoUrl.takeIf { it.isNotBlank() } ?: realUiState.userPhotoUrl
                        if (displayPhoto.isNotBlank()) {
                            AsyncImage(
                                model = displayPhoto,
                                contentDescription = "Profile Photo Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                adminFullName.take(2).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = purpleAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Upload a photo directly from your device storage to update your club administrator profile.",
                        fontSize = 12.sp,
                        color = textMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload from Local Storage", fontWeight = FontWeight.Bold)
                    }

                    if (adminPhotoUrl.isNotBlank() || realUiState.userPhotoUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                adminPhotoUrl = ""
                                realViewModel.updateUserProfile(mapOf("photoUrl" to ""))
                                showUpdateProfileDialog = false
                                noticeMessage = "Profile photo removed."
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Remove Photo", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    // Add / Invite Athlete Dialog
    if (showAddAthleteDialog) {
        val allAthletes = (clubDashboardState as? ClubDashboardUiState.Success)?.athletes ?: emptyList()
        PlayerInviteModal(
            activeClubName = activeClubName,
            clubDashboardViewModel = clubDashboardViewModel,
            existingAthletes = allAthletes,
            onDismiss = { showAddAthleteDialog = false }
        )
    }

    // Invite Staff Dialog
    if (showInviteStaffDialog) {
        var staffName by remember { mutableStateOf("") }
        var staffRole by remember { mutableStateOf("Coach") }

        Dialog(onDismissRequest = { showInviteStaffDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Invite Club Staff", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showInviteStaffDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = staffName,
                        onValueChange = { staffName = it },
                        label = { Text("Staff Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = staffRole,
                        onValueChange = { staffRole = it },
                        label = { Text("Role (Coach, Analyst, Scout, Physio)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (staffName.isNotBlank()) {
                                clubDashboardViewModel.inviteStaff(
                                    displayName = staffName.trim(),
                                    role = staffRole.trim()
                                )
                                showInviteStaffDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Staff Invite", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- SUBVIEWS & HELPERS ---

@Composable
fun BoilerplateBottomItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

@Composable
fun ClubStatMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TGColors.Txt)
        Text(label, fontSize = 9.sp, color = TGColors.Mut, maxLines = 1)
    }
}

data class ClubDrawerGroup(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val subItems: List<String>
)

@Composable
fun ClubMenuDrawer(
    activeClubName: String,
    currentSection: String,
    currentSubTabIndex: Int,
    onDismiss: () -> Unit,
    onNavigate: (section: String, subTabIndex: Int) -> Unit,
    onSignOut: () -> Unit
) {
    var showSignOutConfirm by remember { mutableStateOf(false) }
    var expandedGroup by remember { mutableStateOf<String?>(currentSection) }

    val navGroups = remember {
        listOf(
            ClubDrawerGroup("club", "Club Profile", Icons.Default.Shield, listOf("Club Profile", "Documents")),
            ClubDrawerGroup("people", "Personnel & Staff", Icons.Default.Groups, listOf("People Directory")),
            ClubDrawerGroup("team", "Squad & Tiers", Icons.Default.Groups, listOf("Teams & Competitions", "Athletes & Squad")),
            ClubDrawerGroup("match", "Matches & Training", Icons.Default.SportsScore, listOf("Matches & Fixtures", "Training & Attendance", "Master Calendar")),
            ClubDrawerGroup("stats", "Statistics Hub", Icons.Default.BarChart, listOf("Players", "Teams", "Goals", "Discipline")),
            ClubDrawerGroup("talent", "Talent & Scouting", Icons.Default.PersonSearch, listOf("Recruitment Pipeline", "Scouts & Trials", "Availability Status")),
            ClubDrawerGroup("insights", "Performance Lab", Icons.Default.Analytics, listOf("Performance Lab")),
            ClubDrawerGroup("fin", "Billing & Treasury", Icons.Default.AccountBalanceWallet, listOf("Treasury & M-Pesa")),
            ClubDrawerGroup("comms", "Communications", Icons.Default.Chat, listOf("Announcements & Feed", "Messages & Broadcast")),
            ClubDrawerGroup("admin", "Governance & Admin", Icons.Default.AdminPanelSettings, listOf("Permissions & RBAC", "Audit Log", "Data Verification", "System Settings")),
            ClubDrawerGroup("reports", "Reports & Exports", Icons.Default.Assessment, listOf("Federation Reports"))
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TGColors.Panel,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            border = BorderStroke(1.dp, TGColors.Line)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Brush.linearGradient(listOf(TGColors.Acc, TGColors.Acc2))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            activeClubName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "TG" }.uppercase(),
                            color = Color(0xFF04121A),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            activeClubName.ifBlank { "Talent Graph FC" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = TGColors.Txt,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            "Club Operations & Control Plane",
                            fontSize = 10.5.sp,
                            color = TGColors.Mut,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TGColors.Mut, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = TGColors.Line)

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(navGroups) { group ->
                        val isExpanded = expandedGroup == group.id
                        val isCurrentGroup = currentSection == group.id

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(9.dp))
                        ) {
                            Surface(
                                shape = RoundedCornerShape(9.dp),
                                color = if (isCurrentGroup) TGColors.Panel2 else Color.Transparent,
                                border = BorderStroke(1.dp, if (isCurrentGroup) TGColors.Acc.copy(alpha = 0.5f) else Color.Transparent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (group.subItems.size == 1) {
                                            onNavigate(group.id, 0)
                                            onDismiss()
                                        } else {
                                            expandedGroup = if (isExpanded) null else group.id
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        group.icon,
                                        contentDescription = null,
                                        tint = if (isCurrentGroup) TGColors.Acc else TGColors.Mut,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        group.label,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (isCurrentGroup) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrentGroup) TGColors.Txt else Color(0xFFC3D2E2),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (group.subItems.size > 1) {
                                        Icon(
                                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = TGColors.Mut,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            if (group.subItems.size > 1 && isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 28.dp, top = 2.dp, bottom = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    group.subItems.forEachIndexed { subIndex, subTitle ->
                                        val isSubSelected = currentSection == group.id && currentSubTabIndex == subIndex
                                        Surface(
                                            shape = RoundedCornerShape(7.dp),
                                            color = if (isSubSelected) TGColors.Panel3 else Color.Transparent,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onNavigate(group.id, subIndex)
                                                    onDismiss()
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSubSelected) TGColors.Acc else TGColors.Line)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = subTitle,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSubSelected) TGColors.Acc else TGColors.Mut,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = TGColors.Line)

                Column(modifier = Modifier.padding(12.dp)) {
                    if (!showSignOutConfirm) {
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = TGColors.Bad.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, TGColors.Bad.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSignOutConfirm = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = TGColors.Bad, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TGColors.Bad)
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(9.dp),
                            color = TGColors.Panel2,
                            border = BorderStroke(1.dp, TGColors.Bad.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Confirm Sign Out?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TGColors.Bad)
                                Text("You will return to the sign-in screen.", fontSize = 11.sp, color = TGColors.Mut)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { showSignOutConfirm = false },
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, TGColors.Line),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Cancel", fontSize = 11.5.sp, color = TGColors.Mut)
                                    }
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onSignOut()
                                        },
                                        modifier = Modifier.weight(1f).height(34.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TGColors.Bad),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Sign Out", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubMatchesModuleView(
    clubDashboardState: ClubDashboardUiState,
    onNewMatch: () -> Unit
) {
    val matches = (clubDashboardState as? ClubDashboardUiState.Success)?.matches ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Match Fixtures & Results", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Official competition schedules & score logs", fontSize = 12.sp, color = Color(0xFF64748B))
                }
                Button(onClick = onNewMatch, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.SportsScore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (matches.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No scheduled match fixtures found.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(matches) { match ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(match.competition, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                            Text(match.date, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(match.teamName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF1F5F9)) {
                                Text("${match.homeScore} - ${match.awayScore}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                            Text(match.opponent, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Venue: ${match.venue} • Status: ${match.status}", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
fun ClubDocumentsModuleView(clubDashboardState: ClubDashboardUiState) {
    val docs = (clubDashboardState as? ClubDashboardUiState.Success)?.documents ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Document Vault & Compliance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Encrypted repository for legal contracts, player ID cards, and federation affiliations.", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        if (docs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Document vault is currently empty.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(docs) { doc ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                Text("Target: ${doc.targetEntity} • ${doc.category}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                            Text(doc.verificationLevel, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubRegistrationsModuleView(clubDashboardState: ClubDashboardUiState) {
    val regs = (clubDashboardState as? ClubDashboardUiState.Success)?.registrations ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Federation Registrations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Official athlete licenses and league competition clearances.", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        if (regs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No federation registration records on file.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(regs) { reg ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(reg.athleteName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                                Text(reg.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("License No: ${reg.registrationNumber} • ${reg.competition}", fontSize = 11.sp, color = Color(0xFF64748B), fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun ClubAuditModuleView(clubDashboardState: ClubDashboardUiState) {
    val audits = (clubDashboardState as? ClubDashboardUiState.Success)?.auditLogs ?: emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Organizational Audit Logs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Traceable security records and administrative change history.", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        if (audits.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Audit log is currently empty.", fontSize = 13.sp, color = Color(0xFF64748B))
                    }
                }
            }
        } else {
            items(audits) { entry ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(entry.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            Text(entry.timestamp, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(entry.changeDetails, fontSize = 12.sp, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("By ${entry.actorName} (${entry.actorRole})", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
fun ClubMessagesModuleView(activeClubName: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Club Comms & Messages", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Internal broadcasts, coach communication, and team channels for $activeClubName.", fontSize = 12.sp, color = Color(0xFF64748B))
        }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFEFF6FF), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Official Club Announcement", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                            Text("Welcome to the TalentGraph Club Admin portal.", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubSearchDialog(onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Search Organization", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search athletes, coaches, documents...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun ClubCreateActionSheet(
    onDismiss: () -> Unit,
    onActionSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.95f),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CREATE & DISPATCH", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF7E22CE), letterSpacing = 1.sp)
                        Text("Quick Operational Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                val actions = listOf(
                    Triple("athlete", "Add Athlete to Squad", Icons.Default.PersonAdd),
                    Triple("team", "Create New Team", Icons.Default.Groups),
                    Triple("staff", "Invite Coach or Staff", Icons.Default.Badge),
                    Triple("match", "Create Match Fixture", Icons.Default.SportsScore),
                    Triple("training", "Create Training Session", Icons.Default.FitnessCenter),
                    Triple("document", "Upload Document / Contract", Icons.Default.UploadFile),
                    Triple("announcement", "Broadcast Announcement", Icons.Default.Campaign)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(actions) { (key, label, icon) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onActionSelected(key) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEDE9FE),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClubNotificationsSubView(
    clubDashboardState: ClubDashboardUiState,
    realUiState: TalentUiState
) {
    val inkColor = Color(0xFF1D1530)
    val textMuted = Color(0xFF6B6480)
    val redColor = Color(0xFFE5484D)
    val borderColor = Color(0xFFE6E1F2)

    val signals = (clubDashboardState as? ClubDashboardUiState.Success)?.signals ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Alerts & Notification Feed", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = inkColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Real-time operational notices, schedule changes, and federation alerts.", fontSize = 12.sp, color = textMuted)
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, redColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Clearances Expiring", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = inkColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2 players need renewal before Friday.", fontSize = 13.sp, color = textMuted)
                }
            }
        }

        items(signals) { sig ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, if (sig.severity.equals("high", ignoreCase = true)) redColor else borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sig.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = inkColor)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (sig.severity.equals("high", ignoreCase = true)) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                sig.severity.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (sig.severity.equals("high", ignoreCase = true)) redColor else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(sig.description, fontSize = 13.sp, color = textMuted)
                }
            }
        }
    }
}

@Composable
fun OverviewStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
