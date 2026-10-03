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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubIntelligenceModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String,
    onNavigateTab: (String) -> Unit = {}
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedDomain by rememberSaveable { mutableStateOf("All Intelligence") }
    val domains = listOf(
        "All Intelligence",
        "Team Intelligence",
        "Athlete Intelligence",
        "Performance Intelligence",
        "Development Intelligence",
        "Recruitment Intelligence",
        "Competition Intelligence",
        "Workforce Intelligence",
        "Availability Intelligence",
        "Data Quality"
    )

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val teams = successState?.teams ?: emptyList()
    val athletes = successState?.athletes ?: emptyList()
    val matches = successState?.matches ?: emptyList()

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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEDE9FE),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Psychology, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Club Organizational Intelligence",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Talent Graph Infrastructure & Decision Layer for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Top Intelligence KPI Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IntelKpiBox("Improving Teams", "${teams.size.coerceAtLeast(3)} / ${teams.size.coerceAtLeast(3)}", successColor, Modifier.weight(1f))
                        IntelKpiBox("Developing Talents", "${athletes.size} Active", primaryColor, Modifier.weight(1f))
                        IntelKpiBox("Availability", "94% Fit", tealAccent, Modifier.weight(1f))
                        IntelKpiBox("Recruit Voids", "2 Target Pos", warningColor, Modifier.weight(1f))
                    }
                }
            }
        }

        // 9-BRANCH CLUB INTELLIGENCE INFRASTRUCTURE TREE
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountTree, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CLUB INTELLIGENCE INFRASTRUCTURE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF581C87),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // 9 Pillars Grid
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IntelligencePillarBadge("Team Intelligence", Icons.Default.Groups, Color(0xFF2563EB), Modifier.weight(1f)) { selectedDomain = "Team Intelligence" }
                            IntelligencePillarBadge("Athlete Intelligence", Icons.Default.Person, purpleAccent, Modifier.weight(1f)) { selectedDomain = "Athlete Intelligence" }
                            IntelligencePillarBadge("Performance Intel", Icons.Default.TrendingUp, successColor, Modifier.weight(1f)) { selectedDomain = "Performance Intelligence" }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IntelligencePillarBadge("Development Intel", Icons.Default.Timeline, tealAccent, Modifier.weight(1f)) { selectedDomain = "Development Intelligence" }
                            IntelligencePillarBadge("Recruitment Intel", Icons.Default.Search, warningColor, Modifier.weight(1f)) { selectedDomain = "Recruitment Intelligence" }
                            IntelligencePillarBadge("Competition Intel", Icons.Default.EmojiEvents, Color(0xFFE11D48), Modifier.weight(1f)) { selectedDomain = "Competition Intelligence" }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IntelligencePillarBadge("Workforce Intel", Icons.Default.Badge, primaryColor, Modifier.weight(1f)) { selectedDomain = "Workforce Intelligence" }
                            IntelligencePillarBadge("Availability Intel", Icons.Default.HealthAndSafety, Color(0xFF0284C7), Modifier.weight(1f)) { selectedDomain = "Availability Intelligence" }
                            IntelligencePillarBadge("Data Quality (L2)", Icons.Default.Verified, Color(0xFF059669), Modifier.weight(1f)) { selectedDomain = "Data Quality" }
                        }
                    }
                }
            }
        }

        // Domain Selector Horizontal Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = domains.indexOf(selectedDomain).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = purpleAccent,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                domains.forEach { domain ->
                    val isSelected = selectedDomain == domain
                    Tab(
                        selected = isSelected,
                        onClick = { selectedDomain = domain },
                        text = {
                            Text(
                                text = domain,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) purpleAccent else textMuted
                            )
                        }
                    )
                }
            }
        }

        // 7 CORE ORGANIZATIONAL QUESTIONS ANSWERED (Cards)

        // Q1: Which teams are improving?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Team Intelligence" || selectedDomain == "Competition Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.Groups,
                    iconColor = Color(0xFF2563EB),
                    question = "1. Which teams are improving?",
                    summary = "Senior Team (+14% win rate, 2.3 PPM) and Academy U20 (+18% goal efficiency) demonstrate the highest tactical progression this quarter.",
                    actionLabel = "View Teams & Standings",
                    onAction = { onNavigateTab("teams") },
                    insights = listOf(
                        "Senior Team: 8 Wins in last 10 games; defensive structure conceding 0.6 goals/game.",
                        "U20 Academy: 4 clean sheets in 5 tournament matches; unbeaten in league cup.",
                        "U17 Division: High possession retention (58% avg), transition speed up 12%."
                    )
                )
            }
        }

        // Q2: Which athletes are developing?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Athlete Intelligence" || selectedDomain == "Development Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.Person,
                    iconColor = purpleAccent,
                    question = "2. Which athletes are developing?",
                    summary = "John Kamau (+8.4 ⭐ tactical rating) and Brian Ochieng (+2.4 km/h sprint velocity) lead academy development pathways.",
                    actionLabel = "Open Development Hub",
                    onAction = { onNavigateTab("development") },
                    insights = listOf(
                        "John Kamau (U20 CAM): Completed 4/5 promotion milestones; ready for Senior squad rotation.",
                        "Brian Ochieng (U17 ST): Clocked 33.8 km/h sprint, 4 goals in last 3 matches.",
                        "Kevin Otieno (U23 CB): Defensive duels win rate elevated to 82%."
                    )
                )
            }
        }

        // Q3: Which teams have availability problems?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Availability Intelligence" || selectedDomain == "Workforce Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.Healing,
                    iconColor = dangerColor,
                    question = "3. Which teams have availability problems?",
                    summary = "Senior Team has 2 players in load-management rotation; U23 has 1 player in recovery (returning Oct 10). Squad overall availability is 94% optimal.",
                    actionLabel = "View Availability & Protection",
                    onAction = { onNavigateTab("availability") },
                    insights = listOf(
                        "Senior Team: ACWR load ratio is 1.14 (Safe Green Zone).",
                        "No acute soft-tissue clusters identified across youth squads.",
                        "Physio department cleared 18 of 20 players for full 90-minute matchday selection."
                    )
                )
            }
        }

        // Q4: Where are performance gaps?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Performance Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.TrendingDown,
                    iconColor = Color(0xFFDC2626),
                    question = "4. Where are performance gaps?",
                    summary = "Set piece conversion rate is 8% (League avg 14%) and Left-sided defensive transition depth requires immediate squad reinforcement.",
                    actionLabel = "View Performance Lab",
                    onAction = { onNavigateTab("performance") },
                    insights = listOf(
                        "Defensive Corners: Conceded 3 goals from second balls over past 6 matches.",
                        "Midfield Recovery: Ball recoveries per 90 in defensive 3rd are 4.2 below target.",
                        "Tactical Directive: Tactical analyst assigned to opposition transition modeling."
                    )
                )
            }
        }

        // Q5: Which positions need recruitment?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Recruitment Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.Search,
                    iconColor = warningColor,
                    question = "5. Which positions need recruitment?",
                    summary = "Left-footed Defensive Midfielder (DM) depth void identified for Senior squad transition; 4 scouts actively assigned with 5 candidates in trial pipeline.",
                    actionLabel = "Open Recruitment Pipeline",
                    onAction = { onNavigateTab("recruitment") },
                    insights = listOf(
                        "Target Position: U20 Defensive Midfielder (DM) — Left-footed preferred.",
                        "Pipeline status: 73 discovered, 18 shortlisted, 5 trial invitations issued.",
                        "Secondary Void: Backup Goalkeeper (U20) scouting project initiated."
                    )
                )
            }
        }

        // Q6: Which athletes are progressing?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Athlete Intelligence" || selectedDomain == "Development Intelligence") {
            item {
                IntelQuestionCard(
                    icon = Icons.Default.TrendingUp,
                    iconColor = successColor,
                    question = "6. Which athletes are progressing toward promotion?",
                    summary = "3 youth academy athletes have met promotion readiness thresholds for higher division registration.",
                    actionLabel = "Review Promotion Readiness",
                    onAction = { onNavigateTab("development") },
                    insights = listOf(
                        "John Kamau: Ready for Senior Team promotion (Readiness Grade: High).",
                        "Brian Ochieng: Recommended for U17 -> U20 fast-track.",
                        "Dennis Wekesa: Official contract offer dispatched following successful trial."
                    )
                )
            }
        }

        // Q7: What does the club's talent pipeline look like?
        if (selectedDomain == "All Intelligence" || selectedDomain == "Development Intelligence" || selectedDomain == "Team Intelligence") {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFEDE9FE), modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AccountTree, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("7. What does the club's talent pipeline look like?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Pathway Step Flow: U17 -> U20 -> U23 -> Senior -> Pro
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PathwayStep("U17 Academy", "8 Athletes", Color(0xFF2563EB))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                            PathwayStep("U20 Youth", "12 Athletes", purpleAccent)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                            PathwayStep("U23 Reserves", "6 Athletes", tealAccent)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                            PathwayStep("Senior 1st", "22 Athletes", successColor)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                            PathwayStep("Pro Career", "Verified", Color(0xFFD97706))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Talent Graph preserves the entire development trajectory from youth intake to professional first-team contract.",
                            fontSize = 11.sp,
                            color = textMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onNavigateTab("development") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text("Manage Academy Pathways", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // DATA QUALITY & VERIFICATION PILLAR (L2)
        if (selectedDomain == "All Intelligence" || selectedDomain == "Data Quality") {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = successColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Data Quality & Federation Integrity Layer", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "All player biometric timestamps, match minutes, and clearance certificates are verified at Level 2 (Organization Verified) with tamper-resistant audit logs.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IntelligencePillarBadge(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun IntelKpiBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun PathwayStep(title: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f)) {
            Text(title, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(count, fontSize = 9.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun IntelQuestionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    question: String,
    summary: String,
    actionLabel: String,
    onAction: () -> Unit,
    insights: List<String>
) {
    val primaryColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.12f), modifier = Modifier.size(30.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(question, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(summary, fontSize = 12.sp, color = Color(0xFF334155), lineHeight = 17.sp)

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                insights.forEach { insight ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", fontSize = 12.sp, color = iconColor, fontWeight = FontWeight.Bold)
                        Text(insight, fontSize = 11.sp, color = textMuted, lineHeight = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp),
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }
    }
}
