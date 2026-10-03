package com.example.ui.auth.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun AdminDashboardScreen(
    viewModel: TalentViewModel? = null,
    uiState: TalentUiState? = null,
    userName: String = "Platform Admin",
    onNavigateToFeed: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf("home") }
    var showActionModal by remember { mutableStateOf(false) }
    var showControlPlaneDrawer by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<String?>(null) }

    val bgDark = Color(0xFF0F172A)
    val cardDark = Color(0xFF1E293B)
    val borderDark = Color(0xFF334155)
    val redAccent = Color(0xFFEF4444)
    val emeraldAccent = Color(0xFF10B981)
    val textLight = Color(0xFFF8FAFC)
    val textDim = Color(0xFF94A3B8)

    BackHandler(enabled = selectedTab != "home") {
        selectedTab = "home"
    }

    Scaffold(
        containerColor = bgDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = redAccent.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, redAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = redAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Talent Graph Control",
                                color = textLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "PLATFORM ADMIN • GLOBAL ROOT",
                                color = redAccent,
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textLight)
                        }
                    } else {
                        IconButton(onClick = { showControlPlaneDrawer = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textLight)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToFeed) {
                        Icon(Icons.Default.DynamicFeed, contentDescription = "Feed Dashboard", tint = redAccent)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = emeraldAccent.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, emeraldAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(emeraldAccent, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("All Systems Go", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = emeraldAccent)
                        }
                    }
                    IconButton(onClick = { showActionModal = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Actions", tint = redAccent)
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = textDim)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgDark)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = bgDark, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "Overview") },
                    label = { Text("Overview", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = redAccent, selectedTextColor = redAccent, unselectedIconColor = textDim, unselectedTextColor = textDim, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = selectedTab == "users",
                    onClick = { selectedTab = "users" },
                    icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                    label = { Text("Users", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = redAccent, selectedTextColor = redAccent, unselectedIconColor = textDim, unselectedTextColor = textDim, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showActionModal = true },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(redAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Action", tint = Color.White)
                        }
                    },
                    label = { Text("Global", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = redAccent, selectedTextColor = redAccent, unselectedIconColor = textDim, unselectedTextColor = textDim, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = selectedTab == "alerts",
                    onClick = { selectedTab = "alerts" },
                    icon = {
                        BadgedBox(badge = { Badge(containerColor = redAccent) { Text("3", color = Color.White) } }) {
                            Icon(Icons.Default.Warning, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = redAccent, selectedTextColor = redAccent, unselectedIconColor = textDim, unselectedTextColor = textDim, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showControlPlaneDrawer = true },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
                    label = { Text("Menu", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = redAccent, selectedTextColor = redAccent, unselectedIconColor = textDim, unselectedTextColor = textDim, indicatorColor = Color.Transparent)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            if (noticeMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cardDark,
                        border = BorderStroke(1.dp, redAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(noticeMessage ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = redAccent)
                            IconButton(onClick = { noticeMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = textDim, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // High-Level Global KPIs (4 Cards)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminKpiCard(Modifier.weight(1f), "Active Users", "14,293", Icons.Default.Groups, Color(0xFF60A5FA), cardDark, borderDark)
                    AdminKpiCard(Modifier.weight(1f), "Organizations", "342", Icons.Default.Business, Color(0xFFA855F7), cardDark, borderDark)
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminKpiCard(Modifier.weight(1f), "Pending Verifications", "128", Icons.Default.Verified, Color(0xFFF97316), cardDark, borderDark)
                    AdminKpiCard(Modifier.weight(1f), "Risk Alerts (High)", "3", Icons.Default.Shield, redAccent, cardDark, borderDark)
                }
            }

            // Infrastructure Health Matrix
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDark),
                    border = BorderStroke(1.dp, borderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = emeraldAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Infrastructure Health", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textLight)
                            }
                            Text("ALL OPERATIONAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = emeraldAccent, letterSpacing = 0.5.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val systems = listOf(
                            Pair("API Services", "Operational"),
                            Pair("Firestore DB", "Operational"),
                            Pair("Authentication", "Operational"),
                            Pair("Cloud Storage", "Operational"),
                            Pair("Search Engine", "Degraded"),
                            Pair("Push Notifications", "Operational")
                        )

                        systems.chunked(2).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.forEach { (name, status) ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = bgDark,
                                        border = BorderStroke(1.dp, borderDark),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(8.dp).background(if (status == "Degraded") Color(0xFFFACC15) else emeraldAccent, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textLight)
                                                Text(status, fontSize = 9.sp, color = if (status == "Degraded") Color(0xFFFACC15) else emeraldAccent)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Governance & Risk Queue
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDark),
                    border = BorderStroke(1.dp, borderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Governance & Risk Queue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textLight)
                            }
                            Text("MANAGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA), letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        AdminQueueItem("Duplicate Athlete Detected", "Possible duplicate profile for ID: TG-10294. Two birth records found.", "Data Quality", Color(0xFFFACC15)) {
                            noticeMessage = "Opening duplicate review for athlete TG-10294..."
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AdminQueueItem("Risk Signal: Workload Override", "Coach requested override on High Workload for U20 squad.", "Risk Review", Color(0xFFF97316)) {
                            noticeMessage = "Opening Workload Override request..."
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AdminQueueItem("Organization Verification Pending", "Nairobi Elite Academy uploaded L3 documentation.", "Verification", Color(0xFF60A5FA)) {
                            noticeMessage = "Reviewing Nairobi Elite Academy L3 documentation..."
                        }
                    }
                }
            }

            // Live Audit Log Feed
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardDark),
                    border = BorderStroke(1.dp, borderDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live Platform Audit Log", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textLight)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        val logs = listOf(
                            Triple("Modified athlete record", "Admin-024 → TG-82941 (Club transfer verified)", "09:41"),
                            Triple("Deployed Model Version", "System (ML) → Perf-v2.4 (Confidence threshold 0.85)", "09:12"),
                            Triple("Permission Changed", "Admin-011 → Scout Group E (Granted L2 video access)", "08:55"),
                            Triple("Signal Generated", "Risk Engine → Team-XYZ (Workload threshold exceeded)", "08:30"),
                            Triple("Organization Verified", "Admin-003 → Org-992 (L3 Business Docs confirmed)", "07:45")
                        )

                        logs.forEach { (title, detail, time) ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(Color(0xFFA855F7), CircleShape).align(Alignment.CenterVertically))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textLight)
                                        Text(time, fontSize = 10.sp, color = textDim)
                                    }
                                    Text(detail, fontSize = 10.sp, color = textDim)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showActionModal) {
        PlatformActionModal(
            onDismiss = { showActionModal = false },
            onAction = { action ->
                noticeMessage = "Action Initiated: $action"
                showActionModal = false
            }
        )
    }

    if (showControlPlaneDrawer) {
        PlatformControlPlaneDrawer(
            onDismiss = { showControlPlaneDrawer = false },
            onSelect = { item ->
                noticeMessage = "Control plane: $item"
                showControlPlaneDrawer = false
            },
            onSignOut = onSignOut
        )
    }
}

@Composable
fun AdminKpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    bgColor: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(title.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF94A3B8), letterSpacing = 0.5.sp)
        }
    }
}

@Composable
fun AdminQueueItem(
    title: String,
    desc: String,
    tag: String,
    tagColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF0F172A),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(shape = RoundedCornerShape(4.dp), color = tagColor.copy(alpha = 0.2f)) {
                        Text(tag, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = tagColor, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun PlatformActionModal(onDismiss: () -> Unit, onAction: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Global Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8)) }
                }
                Spacer(modifier = Modifier.height(14.dp))

                val actions = listOf(
                    Pair("Verify Org", Icons.Default.VerifiedUser),
                    Pair("Review Risk", Icons.Default.Shield),
                    Pair("Data Audit", Icons.Default.Storage),
                    Pair("System Alert", Icons.Default.BroadcastOnHome),
                    Pair("Feature Flag", Icons.Default.Flag),
                    Pair("Manage Access", Icons.Default.Key)
                )

                actions.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { (label, icon) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.weight(1f).clickable { onAction(label) }
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(icon, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
fun PlatformControlPlaneDrawer(onDismiss: () -> Unit, onSelect: (String) -> Unit, onSignOut: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color(0xFF0F172A), modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.85f)) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Control Plane", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8)) }
                }
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    item { Text("ECOSYSTEM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp) }
                    listOf("Users Management", "Organizations", "Athletes & Rosters", "Competitions & Leagues", "Matches & Telemetry").forEach { item ->
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                                Text(item, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFCBD5E1), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { Text("SECURITY & INFRASTRUCTURE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp) }
                    listOf("Verification Center", "Data Governance", "Risk Administration", "Developer API & Keys", "Feature Flags").forEach { item ->
                        item {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF1E293B), modifier = Modifier.fillMaxWidth().clickable { onSelect(item) }) {
                                Text(item, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFCBD5E1), modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = onSignOut, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D), contentColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Text("Sign Out Root Admin", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
