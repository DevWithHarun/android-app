package com.example.ui.auth.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoutDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState? = null,
    userName: String = "Scout Harun",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("home") }
    var showActionModal by remember { mutableStateOf(false) }
    var showScoutingHubDrawer by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<String?>(null) }

    val primaryColor = Color(0xFF0F172A)
    val emeraldAccent = Color(0xFF059669)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

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
                            color = Color(0xFFD1FAE5),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Explore, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Talent Discovery",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "SCOUT DESK • ACCREDITED",
                                color = emeraldAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
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
                        IconButton(onClick = { showScoutingHubDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryColor)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToFeed) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = "Feed Dashboard", tint = emeraldAccent)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Accredited", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                    }
                    IconButton(onClick = { showActionModal = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Actions", tint = emeraldAccent)
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = textMuted)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "search",
                    onClick = { selectedTab = "search" },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showActionModal = true },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(emeraldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Action", tint = Color.White)
                        }
                    },
                    label = { Text("Scout", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "alerts",
                    onClick = { selectedTab = "alerts" },
                    icon = {
                        BadgedBox(badge = { Badge { Text("4") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showScoutingHubDrawer = true },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "More") },
                    label = { Text("More", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            // Welcome Header
            item {
                Column {
                    Text(
                        text = "Good morning, $userName 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "What talent prospects should we investigate today?",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
            }

            if (noticeMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFD1FAE5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(noticeMessage ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = emeraldAccent)
                            IconButton(onClick = { noticeMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = textMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Scouting Overview KPIs (4 Cards)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScoutKpiCard(Modifier.weight(1f), "Active Shortlists", "8", null, Icons.Default.FormatListBulleted, emeraldAccent, Color(0xFFECFDF5))
                    ScoutKpiCard(Modifier.weight(1f), "Watchlist Athletes", "42", "+4 active", Icons.Default.Visibility, Color(0xFF2563EB), Color(0xFFEFF6FF))
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScoutKpiCard(Modifier.weight(1f), "New Matches", "6", "Since login", Icons.Default.Timer, Color(0xFF9333EA), Color(0xFFFAF5FF))
                    ScoutKpiCard(Modifier.weight(1f), "Reports Pending", "3", null, Icons.Default.RateReview, Color(0xFFEA580C), Color(0xFFFFF7ED))
                }
            }

            // Quick Tools Bar
            item {
                Column {
                    Text("QUICK TOOLS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            ScoutQuickBtn("Discover Athletes", Icons.Default.Public, emeraldAccent) {
                                noticeMessage = "Opening Global Talent Radar Discovery..."
                            }
                        }
                        item {
                            ScoutQuickBtn("Create Shortlist", Icons.Default.PlaylistAdd, Color(0xFF2563EB)) {
                                noticeMessage = "Opening New Shortlist Creator..."
                            }
                        }
                        item {
                            ScoutQuickBtn("Compare Players", Icons.Default.CompareArrows, Color(0xFFA855F7)) {
                                noticeMessage = "Opening Player Head-to-Head Comparison..."
                            }
                        }
                        item {
                            ScoutQuickBtn("Create Report", Icons.Default.NoteAlt, Color(0xFFEA580C)) {
                                noticeMessage = "Opening Verified Scouting Dossier Form..."
                            }
                        }
                    }
                }
            }

            // Today's Priorities Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Today's Priorities", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                            }
                            Text("SAVED CRITERIA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        ScoutPriorityRow(
                            "12 new athletes match your saved searches",
                            "Matches criteria for: \"U20 Left-footed defenders\"",
                            Icons.Default.Search,
                            Color(0xFFECFDF5),
                            emeraldAccent
                        ) { noticeMessage = "Displaying 12 new matching U20 defender profiles..." }

                        Spacer(modifier = Modifier.height(8.dp))
                        ScoutPriorityRow(
                            "4 watchlist athletes played yesterday",
                            "Videos and verified performance metrics are now available.",
                            Icons.Default.StarBorder,
                            Color(0xFFEFF6FF),
                            Color(0xFF2563EB)
                        ) { noticeMessage = "Reviewing match telemetry for 4 watchlist athletes..." }

                        Spacer(modifier = Modifier.height(8.dp))
                        ScoutPriorityRow(
                            "3 athletes showed performance changes",
                            "John Mwangi + 2 others exceeded rolling avg by >15%.",
                            Icons.Default.TrendingUp,
                            Color(0xFFFAF5FF),
                            Color(0xFFA855F7)
                        ) { noticeMessage = "Opening performance surge intelligence..." }

                        Spacer(modifier = Modifier.height(8.dp))
                        ScoutPriorityRow(
                            "2 scouting reports require completion",
                            "Post-match observation reports pending for \"Mombasa Prospects\".",
                            Icons.Default.EditNote,
                            Color(0xFFFFF7ED),
                            Color(0xFFEA580C)
                        ) { noticeMessage = "Opening Scouting Report Drafts..." }
                    }
                }
            }

            // Active Shortlists Widget
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Active Shortlists", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text("VIEW ALL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399), letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            Triple("U20 Centre Backs", "Project: ABC Academy", "24"),
                            Triple("2027 Academy Intake", "Project: Central Ops", "38"),
                            Triple("Kenya Midfielders", "Project: Open Search", "17")
                        ).forEach { (title, project, count) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(project, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF064E3B)) {
                                        Text(count, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Open Opportunities Pipeline
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("My Open Opportunities", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                                Text("ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("ABC Club — U20 Midfielder Trial", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("Deadline: 25 Sept • Nairobi High Performance Complex", fontSize = 11.sp, color = textMuted)

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("APPLICANTS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                Text("48", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            }
                            Column {
                                Text("SHORTLISTED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                Text("12", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE))
                            }
                            Column {
                                Text("RECOMMENDED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                Text("3", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = emeraldAccent)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showActionModal) {
        ScoutActionModal(
            onDismiss = { showActionModal = false },
            onAction = { action ->
                noticeMessage = "Selected: $action"
                showActionModal = false
            }
        )
    }

    if (showScoutingHubDrawer) {
        ScoutHubDrawer(
            onDismiss = { showScoutingHubDrawer = false },
            onSelect = { item ->
                noticeMessage = "Navigating to $item"
                showScoutingHubDrawer = false
            },
            onSignOut = onSignOut
        )
    }
}

@Composable
fun ScoutKpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    badge: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                if (badge != null) {
                    Surface(shape = RoundedCornerShape(6.dp), color = bgColor) {
                        Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text(title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun ScoutQuickBtn(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun ScoutPriorityRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    onOpen: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth().clickable { onOpen() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(36.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun ScoutActionModal(onDismiss: () -> Unit, onAction: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Scout Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Spacer(modifier = Modifier.height(14.dp))

                val actions = listOf(
                    Pair("Add Shortlist", Icons.Default.PlaylistAdd),
                    Pair("Create Report", Icons.Default.NoteAlt),
                    Pair("Observation", Icons.Default.Visibility),
                    Pair("Opportunity", Icons.Default.Work),
                    Pair("Schedule Trial", Icons.Default.Event),
                    Pair("Contact Athlete", Icons.Default.Chat),
                    Pair("Recommend", Icons.Default.Send),
                    Pair("Global Search", Icons.Default.Search)
                )

                actions.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (label, icon) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFECFDF5),
                                modifier = Modifier.weight(1f).clickable { onAction(label) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
fun ScoutHubDrawer(onDismiss: () -> Unit, onSelect: (String) -> Unit, onSignOut: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.85f)) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Scouting Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    item { Text("DISCOVERY & SEARCH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp) }
                    listOf("Global Talent Discovery", "Search Filters", "My Shortlists", "Watchlist", "Compare Athletes").forEach { item ->
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                                Text(item, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { Text("INTELLIGENCE & RECRUITMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp) }
                    listOf("Athlete Profiles", "Performance Desk", "Verified Video Hub", "Scouting Reports", "Trial Pipeline").forEach { item ->
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                                Text(item, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onSignOut, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)), modifier = Modifier.fillMaxWidth()) {
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
