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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
fun AnalystDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState? = null,
    userName: String = "Analyst",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("home") }
    var showActionModal by remember { mutableStateOf(false) }
    var showWorkspaceDrawer by remember { mutableStateOf(false) }
    var activeActionNotice by remember { mutableStateOf<String?>(null) }

    val primaryColor = Color(0xFF0F172A)
    val accentBlue = Color(0xFF2563EB)
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
                            color = Color(0xFFDBEAFE),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Memory, contentDescription = null, tint = accentBlue, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Intelligence Team",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "ANALYST DESK • L3 VERIFIED",
                                color = accentBlue,
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
                        IconButton(onClick = { showWorkspaceDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = primaryColor)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToFeed) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = "Feed Dashboard", tint = accentBlue)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = textMuted, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("L3 Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                    }
                    IconButton(onClick = { showActionModal = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Actions", tint = accentBlue)
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
                    selected = selectedTab == "explore",
                    onClick = { selectedTab = "explore" },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showActionModal = true },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(accentBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Action", tint = Color.White)
                        }
                    },
                    label = { Text("Create", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = selectedTab == "alerts",
                    onClick = { selectedTab = "alerts" },
                    icon = {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showWorkspaceDrawer = true },
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
            // Toast notification if action triggered
            if (activeActionNotice != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = accentBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(activeActionNotice ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accentBlue)
                            }
                            IconButton(onClick = { activeActionNotice = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = textMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Top KPI Cards (4 Cards)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalystKpiCard(Modifier.weight(1f), "Athletes Analyzed", "186", "Dataset", Icons.Default.DirectionsRun, Color(0xFF2563EB), Color(0xFFEFF6FF))
                    AnalystKpiCard(Modifier.weight(1f), "Matches Processed", "247", null, Icons.Default.Timer, Color(0xFF4F46E5), Color(0xFFEEF2FF))
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnalystKpiCard(Modifier.weight(1f), "Overall Data Quality", "94%", "L3 Verified", Icons.Default.Shield, Color(0xFF059669), Color(0xFFECFDF5))
                    AnalystKpiCard(Modifier.weight(1f), "Active ML Models", "8", null, Icons.Default.SmartToy, Color(0xFF9333EA), Color(0xFFFAF5FF))
                }
            }

            // Secondary Metric Bar
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        AnalystSubMetric(Modifier.weight(1f), "12", "Teams\nAnalyzed", primaryColor)
                        AnalystSubMetric(Modifier.weight(1f), "1.8k", "Training\nSessions", primaryColor)
                        AnalystSubMetric(Modifier.weight(1f), "14", "Active\nProjects", Color(0xFFEA580C))
                        AnalystSubMetric(Modifier.weight(1f), "6", "Reports\nPending", accentBlue)
                    }
                }
            }

            // Active Analysis Projects
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
                                Icon(Icons.Default.Science, contentDescription = null, tint = accentBlue, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Active Analysis Projects", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                            }
                            Text("WORKSPACE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accentBlue, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        // Project 1
                        AnalysisProjectItem(
                            title = "U20 Midfielder Development",
                            status = "In Progress",
                            statusColor = accentBlue,
                            dataset = "U20 Athletes",
                            period = "2025-2026",
                            records = "1,420",
                            methodology = "Progression vs. Minutes",
                            confidence = "High"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Project 2
                        AnalysisProjectItem(
                            title = "Workload Spike Correlation",
                            status = "Review Pending",
                            statusColor = Color(0xFF059669),
                            dataset = "Senior Squad",
                            period = "Last 90 Days",
                            records = "844",
                            methodology = "Accumulated Load vs Availability",
                            confidence = "High"
                        )
                    }
                }
            }

            // Recent Performance Trends
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
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recent Performance Trends", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = primaryColor)
                            }
                            Text("ALL TRENDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5), letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFEEF2FF), border = BorderStroke(1.dp, Color(0xFFE0E7FF))) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(36.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("U23 Defensive Transitions", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    Text("Recovery speed improved +12% over last 5 matches.", fontSize = 11.sp, color = textMuted)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0E7FF)) {
                                        Text("Valid", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4338CA), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Text("450 mins", fontSize = 9.sp, color = textMuted)
                                }
                            }
                        }
                    }
                }
            }

            // Data Provenance Pipeline (Emerald Card)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFA7F3D0), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Data Provenance Pipeline", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF022C22)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Missing Timestamps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("2 events", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFACC15))
                                }
                                Text("Physical tests logged without proper time metadata. Excluded from longitudinal models.", fontSize = 10.sp, color = Color(0xFFA7F3D0))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF022C22)) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Unverified Sources", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("0 events", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                }
                                Text("All recent match statistics trace back to L3 official competition sources.", fontSize = 10.sp, color = Color(0xFFA7F3D0))
                            }
                        }
                    }
                }
            }

            // Risk Analytics & Signal Detection
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Risk Analytics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Workload Anomaly (U20)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("The system detected a workload pattern requiring review. Training intensity spike does not match recovery baseline.", fontSize = 11.sp, color = textMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFFEAB308),
                            trackColor = Color(0xFFF1F5F9)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Performance Stability", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("Senior squad variation is within expected statistical limits over the last 30 days.", fontSize = 11.sp, color = textMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.35f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFFF1F5F9)
                        )
                    }
                }
            }
        }
    }

    if (showActionModal) {
        AnalystActionModal(
            onDismiss = { showActionModal = false },
            onActionSelected = { action ->
                activeActionNotice = "Started task: $action"
                showActionModal = false
            }
        )
    }

    if (showWorkspaceDrawer) {
        AnalystWorkspaceDrawer(
            onDismiss = { showWorkspaceDrawer = false },
            onSelect = { task ->
                activeActionNotice = "Navigating to $task"
                showWorkspaceDrawer = false
            },
            onSignOut = onSignOut
        )
    }
}

@Composable
fun AnalystKpiCard(
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
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
fun AnalystSubMetric(modifier: Modifier = Modifier, value: String, label: String, valColor: Color) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = valColor)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 12.sp)
    }
}

@Composable
fun AnalysisProjectItem(
    title: String,
    status: String,
    statusColor: Color,
    dataset: String,
    period: String,
    records: String,
    methodology: String,
    confidence: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Surface(shape = RoundedCornerShape(6.dp), color = statusColor.copy(alpha = 0.1f)) {
                    Text(status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dataset: $dataset", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("Period: $period", fontSize = 10.sp, color = Color(0xFF64748B))
                Text("Records: $records", fontSize = 10.sp, color = Color(0xFF64748B))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Methodology: $methodology • Confidence: $confidence", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF059669))
        }
    }
}

@Composable
fun AnalystActionModal(
    onDismiss: () -> Unit,
    onActionSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Create & Analyze", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Spacer(modifier = Modifier.height(14.dp))

                val actions = listOf(
                    Pair("Analysis Project", Icons.Default.Science),
                    Pair("Athlete Analysis", Icons.Default.DirectionsRun),
                    Pair("Team Analysis", Icons.Default.Groups),
                    Pair("Match Analysis", Icons.Default.Timer),
                    Pair("Run Comparison", Icons.Default.Balance),
                    Pair("Build Dashboard", Icons.Default.Dashboard),
                    Pair("Generate Report", Icons.Default.Assessment),
                    Pair("Data Query", Icons.Default.Code)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    actions.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { (label, icon) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.weight(1f).clickable { onActionSelected(label) }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
fun AnalystWorkspaceDrawer(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    onSignOut: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Analyst Workspace", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    item { Text("CORE ANALYTICS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp) }
                    listOf("Performance Analysis", "Intelligence Models", "Development Curves", "Skills & Attributes Radar").forEach { item ->
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                                Text(item, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { Text("DATA & INFRASTRUCTURE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp) }
                    listOf("Data Provenance & Quality", "Risk Signal Review", "ML Models Catalog", "Methodology Library", "JSON Data Exports").forEach { item ->
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
