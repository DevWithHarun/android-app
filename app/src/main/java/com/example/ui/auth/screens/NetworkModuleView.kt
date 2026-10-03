package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

data class VerifiedScoutContact(
    val name: String,
    val role: String,
    val organization: String,
    val region: String,
    val isVerified: Boolean = true,
    val badgeColor: Color = Color(0xFF2563EB)
)

@Composable
fun NetworkModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState,
    onNavigateTab: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)
    val blueColor = Color(0xFF2563EB)

    var selectedSubTab by remember { mutableStateOf("Overview & Radar") }
    var allowScoutRadar by remember { mutableStateOf(true) }
    var selectedContactForInquiry by remember { mutableStateOf<VerifiedScoutContact?>(null) }
    var inquiryMessageText by remember { mutableStateOf("") }
    var inquirySentSuccess by remember { mutableStateOf(false) }

    // Pre-configured verified scout directory
    val verifiedScouts = remember {
        listOf(
            VerifiedScoutContact("David Bellingham", "Head of Youth Recruitment", "Arsenal FC Academy", "UK & Europe", badgeColor = Color(0xFFEF4444)),
            VerifiedScoutContact("Marco Richter", "International Scouting Coordinator", "Borussia Dortmund", "Global Emerging Markets", badgeColor = Color(0xFFF59E0B)),
            VerifiedScoutContact("Carlos Mendes", "Senior Talent Identification Lead", "Atlanta United FC (MLS)", "North America / Africa", badgeColor = Color(0xFF2563EB)),
            VerifiedScoutContact("Thierry Van Damme", "Chief Technical Scout", "KAA Gent", "Benelux & Central Europe", badgeColor = Color(0xFF0D9488)),
            VerifiedScoutContact("Sarah Ochieng", "FIFA Licensed Intermediary & Agent", "Apex Sports Management", "East Africa & Middle East", badgeColor = Color(0xFFA855F7)),
            VerifiedScoutContact("Robert Nsubuga", "Talent Acquisition Director", "CECAFA Regional League", "Pan-African Federation", badgeColor = Color(0xFF059669))
        )
    }

    val totalImpressions = uiState.scoutActivities.size
    val uniqueScouts = remember(uiState.scoutActivities) {
        uiState.scoutActivities.map { it.viewerName }.distinct().size
    }
    val totalOpportunities = uiState.opportunities.size
    val trialsCount = remember(uiState.opportunities) {
        uiState.opportunities.count { it.type.contains("Trial", ignoreCase = true) }
    }
    val appliedCount = remember(uiState.opportunities) {
        uiState.opportunities.count { it.status.contains("Applied", ignoreCase = true) || it.status.contains("Shortlisted", ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Global Scouting Network & Radar
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (allowScoutRadar) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = if (allowScoutRadar) "RADAR ACTIVE • BROADCASTING" else "STEALTH MODE • HIDDEN",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allowScoutRadar) Color(0xFF047857) else Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scouting & Network Hub",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Direct link to certified club scouts, trial fixtures, contracts, and talent identification.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF2563EB).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Counters Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NetworkStatItem(value = "$totalImpressions", label = "Passport Views")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        NetworkStatItem(value = "$uniqueScouts", label = "Unique Scouts")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        NetworkStatItem(value = "$totalOpportunities", label = "Open Trials")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        NetworkStatItem(value = "$appliedCount", label = "Applications")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab("opportunities") },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Browse Trial Fixtures", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val shareText = "Player Scouting Profile: ${uiState.userName}\nPosition: Footballer\nVerified Stats & Highlights available on Talent Graph Network."
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Athlete Profile"))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).height(44.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Sub-Navigation Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val subTabs = listOf("Overview & Radar", "Opportunities Hub", "Scout Activity Feed", "Verified Directory")
                items(subTabs) { tab ->
                    val isSelected = selectedSubTab == tab
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedSubTab = tab }
                    ) {
                        Text(
                            text = tab,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else primaryColor
                        )
                    }
                }
            }
        }

        // Independent Module Cards & Navigation
        when (selectedSubTab) {
            "Overview & Radar" -> {
                // Feature 1: Opportunities & Trials Independent Portal Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF2563EB).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Work,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Opportunities & Trials",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "$totalOpportunities active opportunities • $trialsCount trials available",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = "$appliedCount APPLIED",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Verified club trials, scouting showcases, and academy trial opportunities.",
                                fontSize = 12.sp,
                                color = textMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("opportunities") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Open Dedicated Opportunities Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Feature 2: Scout Activity & Radar Independent Portal Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFFA855F7).copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Color(0xFFA855F7),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Scout Activity & Radar",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "$totalImpressions total impressions logged",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF3E8FF)
                                ) {
                                    Text(
                                        text = "$uniqueScouts SCOUTS WATCHING",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E22CE)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Real-time audit log of certified scouts, agents, and club directors viewing your passport and videos.",
                                fontSize = 12.sp,
                                color = textMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("scout_activity") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Text("Open Dedicated Scout Activity Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Feature 3: Verified Scout & Recruiter Highlights Directory
                item {
                    Text(
                        text = "VERIFIED SCOUTS & DIRECTORS (${verifiedScouts.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                }

                items(verifiedScouts) { scout ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(scout.badgeColor.copy(alpha = 0.1f), CircleShape)
                                        .border(1.dp, scout.badgeColor.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = scout.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = scout.badgeColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = scout.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = scout.badgeColor, modifier = Modifier.size(14.dp))
                                    }
                                    Text(text = "${scout.role} • ${scout.organization}", fontSize = 11.sp, color = textMuted)
                                    Text(text = "Region: ${scout.region}", fontSize = 10.sp, color = secondaryColor, fontWeight = FontWeight.Medium)
                                }
                            }

                            Button(
                                onClick = {
                                    selectedContactForInquiry = scout
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = primaryColor),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Outlined.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Inquire", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "Opportunities Hub" -> {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Opportunities & Trials Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "Search and apply for open trials, club scouting combines, and elite academy fixtures.",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("opportunities") },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Fullscreen Opportunities Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "Scout Activity Feed" -> {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Scout Activity Feed Portal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = "Monitor profile views, PDF dossier downloads, and video highlight inspections from certified scouts.",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateTab("scout_activity") },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Fullscreen Scout Activity Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "Verified Directory" -> {
                item {
                    Text(
                        text = "ALL VERIFIED SCOUTS, DIRECTORS & AGENTS (${verifiedScouts.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                }

                items(verifiedScouts) { scout ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(scout.badgeColor.copy(alpha = 0.1f), CircleShape)
                                            .border(1.dp, scout.badgeColor.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = scout.name.take(2).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = scout.badgeColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = scout.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = scout.badgeColor, modifier = Modifier.size(14.dp))
                                        }
                                        Text(text = "${scout.role} • ${scout.organization}", fontSize = 12.sp, color = textMuted)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Text(
                                        text = "VERIFIED",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Coverage Region: ${scout.region}", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { selectedContactForInquiry = scout },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Direct Message", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val shareText = "Hello ${scout.name}, please review my verified player passport and match metrics: ${uiState.userName}."
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Dossier with ${scout.name}"))
                                    },
                                    border = BorderStroke(1.dp, borderColor),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(38.dp)
                                ) {
                                    Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Send Dossier", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal: Send Scout Inquiry / Message
    if (selectedContactForInquiry != null) {
        val scout = selectedContactForInquiry!!
        Dialog(onDismissRequest = { selectedContactForInquiry = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Direct Scout Inquiry",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        IconButton(onClick = { selectedContactForInquiry = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                        }
                    }

                    Text(
                        text = "To: ${scout.name} (${scout.organization})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryColor
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inquiryMessageText,
                        onValueChange = { inquiryMessageText = it },
                        label = { Text("Message / Trial Request") },
                        placeholder = { Text("Introduce yourself, specify your primary position, and request an evaluation or trial schedule...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (inquiryMessageText.isNotBlank()) {
                                viewModel.sendMessage(
                                    sender = uiState.userName,
                                    text = "To ${scout.name} (${scout.organization}): $inquiryMessageText",
                                    isIncoming = false
                                )
                                viewModel.addScoutActivity(
                                    viewerName = scout.name,
                                    action = "Direct Inquiry Sent: ${inquiryMessageText.take(30)}...",
                                    timestamp = "Just Now",
                                    sectionAccessed = "Direct Messaging"
                                )
                                inquiryMessageText = ""
                                selectedContactForInquiry = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Direct Message to Scout", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}
