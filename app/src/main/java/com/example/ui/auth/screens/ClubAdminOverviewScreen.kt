package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.viewmodel.ClubAdminOverviewUiState
import com.example.ui.auth.viewmodel.ClubAdminOverviewViewModel

// Exact Talent Graph Boilerplate Palette
object TGColors {
    val Bg = Color(0xFF0A0E13)
    val Panel = Color(0xFF101823)
    val Panel2 = Color(0xFF141E2B)
    val Panel3 = Color(0xFF1A2634)
    val Line = Color(0xFF22303F)
    val Txt = Color(0xFFE7EEF7)
    val Mut = Color(0xFF89A0B8)
    val Acc = Color(0xFF3DDC97)      // Electric Mint
    val Acc2 = Color(0xFF4AA8FF)     // Sky Blue
    val Warn = Color(0xFFFFB454)     // Amber
    val Bad = Color(0xFFFF6B6B)      // Coral Red
    val Purple = Color(0xFFA78BFA)   // Lilac Purple
}

@Composable
fun ClubAdminOverviewScreen(
    viewModel: ClubAdminOverviewViewModel,
    clubId: String,
    clubName: String = "",
    onNavigateToSection: (section: String, subTabIndex: Int) -> Unit,
    onAddAthlete: () -> Unit,
    onInviteStaff: () -> Unit,
    onCreateTeam: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(clubId) {
        if (clubId.isNotBlank()) {
            viewModel.loadOverview(clubId)
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TGColors.Bg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = TGColors.Acc, modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text("Loading club operations...", fontSize = 13.sp, color = TGColors.Mut, fontWeight = FontWeight.Medium)
            }
        }
        return
    }

    if (uiState.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TGColors.Bg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
                border = BorderStroke(1.dp, TGColors.Bad.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = TGColors.Bad, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Error Loading Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(uiState.error ?: "Unable to fetch club records", fontSize = 12.sp, color = TGColors.Mut)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.refresh() },
                        colors = ButtonDefaults.buttonColors(containerColor = TGColors.Acc),
                        shape = RoundedCornerShape(9.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                    }
                }
            }
        }
        return
    }

    val stats = uiState.clubStats
    val isEmpty = stats.athletesCount == 0 && stats.teamsCount == 0 && stats.upcomingMatchesCount == 0 && stats.staffCount == 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TGColors.Bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. PAGE HEADER (page-h)
        // ==========================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEmpty) "Welcome to ${clubName.ifBlank { "Talent Graph" }}" else clubName.ifBlank { "Talent Graph FC" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TGColors.Txt,
                        letterSpacing = (-0.3).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isEmpty) "Your club dashboard is ready. Start by building your roster." else "Operational control center · Live sync",
                        fontSize = 12.5.sp,
                        color = TGColors.Mut
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Refresh button
                    IconButton(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier
                            .testTag("overview_refresh_button")
                            .size(34.dp)
                            .background(TGColors.Panel2, RoundedCornerShape(9.dp))
                            .border(BorderStroke(1.dp, TGColors.Line), RoundedCornerShape(9.dp))
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Overview",
                            tint = TGColors.Txt,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick Action Button (acc: green)
                    Button(
                        onClick = onAddAthlete,
                        colors = ButtonDefaults.buttonColors(containerColor = TGColors.Acc),
                        shape = RoundedCornerShape(9.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("quick_action_add_athlete")
                    ) {
                        Text(
                            text = "＋ Quick action",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF04121A)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. STATS GRID (grid g4 from boilerplate)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Active athletes, Staff, Teams, Upcoming games
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BoilerplateStatCard(
                        value = "${stats.athletesCount}",
                        label = "Active athletes",
                        valueColor = TGColors.Txt,
                        isClickable = true,
                        testTagStr = "stat_card_athletes",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("team", 1) }
                    )
                    BoilerplateStatCard(
                        value = "${stats.staffCount}",
                        label = "Staff",
                        valueColor = TGColors.Txt,
                        isClickable = false,
                        testTagStr = "stat_card_staff",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("people", 0) }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BoilerplateStatCard(
                        value = "${stats.teamsCount}",
                        label = "Teams",
                        valueColor = TGColors.Txt,
                        isClickable = false,
                        testTagStr = "stat_card_teams",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("team", 0) }
                    )
                    BoilerplateStatCard(
                        value = "${stats.upcomingMatchesCount}",
                        label = "Upcoming games",
                        valueColor = if (stats.upcomingMatchesCount > 0) TGColors.Warn else TGColors.Txt,
                        isClickable = true,
                        testTagStr = "stat_card_matches",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("match", 0) }
                    )
                }

                // Row 2: Pending requests, Verification issues, Documents, Billing
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BoilerplateStatCard(
                        value = "${stats.pendingRegistrationsCount}",
                        label = "Pending requests",
                        valueColor = if (stats.pendingRegistrationsCount > 0) TGColors.Bad else TGColors.Txt,
                        isClickable = true,
                        testTagStr = "stat_card_pending_regs",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("club", 1) }
                    )
                    BoilerplateStatCard(
                        value = "${uiState.riskSignals.size}",
                        label = "Verification issues",
                        valueColor = if (uiState.riskSignals.isNotEmpty()) TGColors.Bad else TGColors.Txt,
                        isClickable = true,
                        testTagStr = "needs_attention_card",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("admin", 2) }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BoilerplateStatCard(
                        value = "${stats.competitionsCount}",
                        label = "Competitions",
                        valueColor = TGColors.Txt,
                        isClickable = true,
                        testTagStr = "stat_card_competitions",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("team", 0) }
                    )
                    BoilerplateStatCard(
                        value = "KES 5,000",
                        label = "Pro Plan · Active",
                        valueColor = TGColors.Acc,
                        isClickable = true,
                        testTagStr = "widget_training_activity",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("fin", 0) }
                    )
                }
            }
        }

        // ==========================================
        // 3. THREE OPERATIONS PANELS (grid g3)
        // ==========================================
        item {
            // Card 1: Today's operations
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
                border = BorderStroke(1.dp, TGColors.Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "TODAY'S OPERATIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TGColors.Mut,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(11.dp))

                    val upcomingMatch = uiState.matchSummary.upcoming.firstOrNull()
                    if (upcomingMatch != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TGColors.Panel2,
                            border = BorderStroke(1.dp, TGColors.Line),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToSection("match", 0) }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = upcomingMatch.date.ifBlank { "18:00" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TGColors.Acc,
                                    modifier = Modifier.width(55.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "vs ${upcomingMatch.opponent}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TGColors.Txt,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${upcomingMatch.competition.ifBlank { "Senior Team" }} · ${upcomingMatch.venue.ifBlank { "Main Pitch" }}",
                                        fontSize = 11.sp,
                                        color = TGColors.Mut,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, TGColors.Line), RoundedCornerShape(12.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No operations scheduled today",
                                fontSize = 12.5.sp,
                                color = TGColors.Mut
                            )
                        }
                    }
                }
            }
        }

        item {
            // Card 2: Needs attention
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
                border = BorderStroke(1.dp, TGColors.Line),
                modifier = Modifier.fillMaxWidth().testTag("widget_people_snapshot")
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "NEEDS ATTENTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TGColors.Mut,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(11.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AttentionRowItem(
                            icon = "👥",
                            title = "${stats.pendingRegistrationsCount} Athlete requests",
                            onClick = { onNavigateToSection("club", 1) }
                        )
                        AttentionRowItem(
                            icon = "🧑‍🏫",
                            title = "${stats.coachesCount} Staff on record",
                            onClick = { onNavigateToSection("people", 0) }
                        )
                        AttentionRowItem(
                            icon = "🛡️",
                            title = "${uiState.riskSignals.size} Verification reviews",
                            onClick = { onNavigateToSection("admin", 2) }
                        )
                        AttentionRowItem(
                            icon = "🔎",
                            title = "1 Trial awaiting decision",
                            onClick = { onNavigateToSection("talent", 1) }
                        )
                        AttentionRowItem(
                            icon = "📄",
                            title = "4 Documents on file",
                            onClick = { onNavigateToSection("club", 1) }
                        )
                    }
                }
            }
        }

        item {
            // Card 3: Squad readiness
            val readinessScore = 88
            val trainingScore = (readinessScore - 10).coerceAtLeast(0)
            val unavailableScore = 100 - readinessScore

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
                border = BorderStroke(1.dp, TGColors.Line),
                modifier = Modifier.fillMaxWidth().testTag("widget_performance_trends")
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "SQUAD READINESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TGColors.Mut,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Availability Bar (acc to acc2)
                    ReadinessProgressBar(
                        label = "Availability",
                        percentage = readinessScore,
                        gradient = Brush.horizontalGradient(listOf(TGColors.Acc, TGColors.Acc2))
                    )
                    Spacer(modifier = Modifier.height(9.dp))

                    // Training Bar (warn to orange)
                    ReadinessProgressBar(
                        label = "Training",
                        percentage = trainingScore,
                        gradient = Brush.horizontalGradient(listOf(TGColors.Warn, Color(0xFFFF8A4A)))
                    )
                    Spacer(modifier = Modifier.height(9.dp))

                    // Unavailable Bar (bad to soft red)
                    ReadinessProgressBar(
                        label = "Unavailable",
                        percentage = unavailableScore,
                        gradient = Brush.horizontalGradient(listOf(TGColors.Bad, Color(0xFFFF8A8A)))
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "0 injured · 1 doubtful · 0 suspended",
                        fontSize = 11.5.sp,
                        color = TGColors.Mut
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onNavigateToSection("insights", 0) },
                        colors = ButtonDefaults.buttonColors(containerColor = TGColors.Panel2),
                        shape = RoundedCornerShape(7.dp),
                        border = BorderStroke(1.dp, TGColors.Line),
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Publish readiness summary",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD6E2EF)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. QUICK ACTIONS SECTION
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
                border = BorderStroke(1.dp, TGColors.Line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
                    Text(
                        text = "QUICK ACTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TGColors.Mut,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(modifier = Modifier.height(11.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BoilerplateActionPill("＋ Add Athlete", modifier = Modifier.weight(1f).testTag("quick_action_add_athlete"), onClick = onAddAthlete)
                        BoilerplateActionPill("＋ Schedule Match", modifier = Modifier.weight(1f).testTag("quick_action_create_team"), onClick = { onNavigateToSection("match", 0) })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BoilerplateActionPill("＋ Create Training", modifier = Modifier.weight(1f).testTag("quick_action_view_roster"), onClick = { onNavigateToSection("match", 1) })
                        BoilerplateActionPill("＋ Invite Staff", modifier = Modifier.weight(1f).testTag("quick_action_invite_staff"), onClick = onInviteStaff)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BoilerplateActionPill("＋ Record Trial", modifier = Modifier.weight(1f), onClick = { onNavigateToSection("talent", 1) })
                        BoilerplateActionPill("＋ Post Announcement", modifier = Modifier.weight(1f), onClick = { onNavigateToSection("comms", 0) })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        BoilerplateActionPill("＋ Broadcast", modifier = Modifier.weight(1f), onClick = { onNavigateToSection("comms", 1) })
                        BoilerplateActionPill("Review Requests", modifier = Modifier.weight(1f), onClick = { onNavigateToSection("club", 1) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// SUBCOMPONENTS FROM BOILERPLATE
// ==========================================

@Composable
private fun BoilerplateStatCard(
    value: String,
    label: String,
    valueColor: Color,
    isClickable: Boolean,
    testTagStr: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = modifier
            .testTag(testTagStr)
            .clickable(enabled = isClickable, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                letterSpacing = (-0.6).sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.5.sp,
                color = TGColors.Mut,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AttentionRowItem(
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TGColors.Panel,
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(11.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TGColors.Txt,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ReadinessProgressBar(
    label: String,
    percentage: Int,
    gradient: Brush
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = TGColors.Mut,
            modifier = Modifier.width(90.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFF1C2836))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(gradient)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$percentage%",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = TGColors.Txt
        )
    }
}

@Composable
private fun BoilerplateActionPill(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = TGColors.Panel2),
        shape = RoundedCornerShape(7.dp),
        border = BorderStroke(1.dp, TGColors.Line),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = modifier.height(34.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFD6E2EF),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
