package com.example.ui.auth.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@Composable
fun ClubTeamsAndCompetitionsView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val context = LocalContext.current
    val primaryColor = TGColors.Txt
    val purpleAccent = TGColors.Acc
    val tealAccent = TGColors.Acc2
    val surfaceColor = TGColors.Bg
    val cardBg = TGColors.Panel
    val borderColor = TGColors.Line
    val textMuted = TGColors.Mut

    var currentSubTab by remember { mutableStateOf("teams") } // "teams", "competitions", "fixtures", "licensing"
    var showCreateTeamModal by remember { mutableStateOf(false) }
    var showCreateCompetitionModal by remember { mutableStateOf(false) }
    var showScheduleMatchModal by remember { mutableStateOf(false) }
    var showIssueLicenseModal by remember { mutableStateOf(false) }
    var matchToEditScore by remember { mutableStateOf<ClubMatchModel?>(null) }
    var teamToScheduleFor by remember { mutableStateOf<ClubTeamModel?>(null) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val teamsList = successState?.teams ?: emptyList()
    val competitionsList = successState?.competitions ?: emptyList()
    val matchesList = successState?.matches ?: emptyList()
    val registrationsList = successState?.registrations ?: emptyList()
    val athletesList = successState?.athletes ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- 1. Top Header Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = purpleAccent.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "CLUB COMPETITIVE ROSTER",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE9D5FF)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Teams & Competitions Command",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "$activeClubName • Squad Divisions, Tournaments & Federation Licenses",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Stats Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickStatsPill(
                            label = "TEAMS",
                            value = "${teamsList.size}",
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatsPill(
                            label = "COMPETITIONS",
                            value = "${competitionsList.size}",
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatsPill(
                            label = "MATCHES",
                            value = "${matchesList.size}",
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatsPill(
                            label = "LICENSES",
                            value = "${registrationsList.size}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons: + Create Team & + Register Competition
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreateTeamModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Team", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }

                        Button(
                            onClick = { showCreateCompetitionModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Competition", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // --- 2. Sub Navigation Tabs ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TabNavigationPill(
                        label = "Teams",
                        icon = Icons.Default.Groups,
                        count = teamsList.size,
                        isSelected = currentSubTab == "teams",
                        modifier = Modifier.weight(1f),
                        onClick = { currentSubTab = "teams" }
                    )
                    TabNavigationPill(
                        label = "Leagues",
                        icon = Icons.Default.EmojiEvents,
                        count = competitionsList.size,
                        isSelected = currentSubTab == "competitions",
                        modifier = Modifier.weight(1f),
                        onClick = { currentSubTab = "competitions" }
                    )
                    TabNavigationPill(
                        label = "Fixtures",
                        icon = Icons.Default.SportsScore,
                        count = matchesList.size,
                        isSelected = currentSubTab == "fixtures",
                        modifier = Modifier.weight(1f),
                        onClick = { currentSubTab = "fixtures" }
                    )
                    TabNavigationPill(
                        label = "Licenses",
                        icon = Icons.Default.Badge,
                        count = registrationsList.size,
                        isSelected = currentSubTab == "licensing",
                        modifier = Modifier.weight(1f),
                        onClick = { currentSubTab = "licensing" }
                    )
                }
            }
        }

        // --- 3. Content Sections Based on Selected Tab ---

        // ==================== TAB 1: TEAMS & ROSTERS ====================
        if (currentSubTab == "teams") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Club Squad Divisions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Squad categories, coaching staff, and tactical formations",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    IconButton(onClick = { showCreateTeamModal = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Team", tint = purpleAccent)
                    }
                }
            }

            if (teamsList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF3E8FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Squad Teams Configured", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create distinct squad divisions such as Senior First Team, U23 Development, or U19 Academy.",
                                fontSize = 12.sp,
                                color = textMuted,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showCreateTeamModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Create First Team Division", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(teamsList) { team ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Team Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEDE9FE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = team.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "${team.sport} • ${team.gender}",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF3E8FF)
                                ) {
                                    Text(
                                        text = team.ageCategory,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = purpleAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                InfoBlock(
                                    label = "HEAD COACH",
                                    value = team.headCoachName.ifBlank { "Unassigned" },
                                    modifier = Modifier.weight(1f)
                                )
                                InfoBlock(
                                    label = "COMPETITION",
                                    value = team.competition.ifBlank { "National League" },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                InfoBlock(
                                    label = "TACTICAL FORMATION",
                                    value = team.formation.ifBlank { "4-3-3" },
                                    modifier = Modifier.weight(1f)
                                )
                                InfoBlock(
                                    label = "TRAINING SCHEDULE",
                                    value = team.trainingSchedule.ifBlank { "Mon, Wed, Fri" },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Bottom Action Bar: Schedule Match & Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        teamToScheduleFor = team
                                        showScheduleMatchModal = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(15.dp), tint = primaryColor)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Schedule Fixture", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                }

                                IconButton(
                                    onClick = {
                                        clubDashboardViewModel.deleteTeam(team.id, team.name)
                                        Toast.makeText(context, "Removed team ${team.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Team", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== TAB 2: COMPETITIONS & LEAGUES ====================
        if (currentSubTab == "competitions") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active League Campaigns",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Tournaments, league standings, points, and federation sanctioning",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    IconButton(onClick = { showCreateCompetitionModal = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Competition", tint = purpleAccent)
                    }
                }
            }

            if (competitionsList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Competitions Registered", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Register your squad into active national leagues, cups, or regional tournaments.",
                                fontSize = 12.sp,
                                color = textMuted,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showCreateCompetitionModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Register First Competition", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(competitionsList) { comp ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFEF3C7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(comp.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                                        Text("${comp.tier} • Season ${comp.season}", fontSize = 12.sp, color = textMuted)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                        Text("SANCTIONED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(14.dp))

                            // Standings & Performance Strip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StandingBox(label = "POS", value = "#${comp.tablePosition}", modifier = Modifier.weight(1f), isHighlight = true)
                                StandingBox(label = "PTS", value = "${comp.points}", modifier = Modifier.weight(1f))
                                StandingBox(label = "P", value = "${comp.matchesPlayed}", modifier = Modifier.weight(1f))
                                StandingBox(label = "W", value = "${comp.wins}", modifier = Modifier.weight(1f))
                                StandingBox(label = "D", value = "${comp.draws}", modifier = Modifier.weight(1f))
                                StandingBox(label = "L", value = "${comp.losses}", modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Federation & Division info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Federation: ${comp.federation}",
                                    fontSize = 11.sp,
                                    color = textMuted
                                )
                                Text(
                                    text = "Team: ${comp.teamName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        showScheduleMatchModal = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.SportsSoccer, contentDescription = null, modifier = Modifier.size(15.dp), tint = primaryColor)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add League Fixture", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                }

                                IconButton(
                                    onClick = {
                                        clubDashboardViewModel.deleteCompetition(comp.id, comp.name)
                                        Toast.makeText(context, "Removed competition ${comp.name}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Competition", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== TAB 3: FIXTURES & RESULTS ====================
        if (currentSubTab == "fixtures") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Match Fixtures & Matchday Center",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Upcoming match schedules, venues, and live score logging",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    Button(
                        onClick = { showScheduleMatchModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fixture", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (matchesList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SportsScore, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Fixtures Scheduled", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                            Text("Schedule your upcoming league and cup match fixtures.", fontSize = 12.sp, color = textMuted)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showScheduleMatchModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Schedule Match", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(matchesList) { match ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = match.competition,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (match.status) {
                                        "Completed" -> Color(0xFFECFDF5)
                                        "Live" -> Color(0xFFFEF2F2)
                                        else -> Color(0xFFEFF6FF)
                                    }
                                ) {
                                    Text(
                                        text = match.status.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = when (match.status) {
                                            "Completed" -> Color(0xFF047857)
                                            "Live" -> Color(0xFFDC2626)
                                            else -> Color(0xFF1D4ED8)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Match Scoreboard Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(match.teamName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                    Text("Home", fontSize = 10.sp, color = textMuted)
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = if (match.status == "Upcoming") "VS" else "${match.homeScore} - ${match.awayScore}",
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }

                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text(match.opponent, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor, textAlign = TextAlign.End)
                                    Text("Away", fontSize = 10.sp, color = textMuted, textAlign = TextAlign.End)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(match.date, fontSize = 11.sp, color = textMuted)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(match.venue, fontSize = 11.sp, color = textMuted)
                                }

                                TextButton(
                                    onClick = { matchToEditScore = match },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = purpleAccent)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Update Result", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = purpleAccent)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== TAB 4: FEDERATION LICENSING ====================
        if (currentSubTab == "licensing") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Federation Player Licensing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Official federation passports, licenses, and matchday eligibility",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    Button(
                        onClick = { showIssueLicenseModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("License", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (registrationsList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = tealAccent, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Player Licenses Issued", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                            Text("Issue verified competition licenses for your registered squad athletes.", fontSize = 12.sp, color = textMuted)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showIssueLicenseModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Issue Player License", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                items(registrationsList) { reg ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
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
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFCCFBF1)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = tealAccent, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(reg.athleteName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                                        Text("Competition: ${reg.competition}", fontSize = 12.sp, color = textMuted)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(12.dp))
                                        Text(reg.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("LICENSE NUMBER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                    Text(
                                        text = reg.registrationNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = primaryColor
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("VALID THROUGH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                    Text(
                                        text = reg.expiryDate,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = primaryColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== DIALOGS & MODALS ====================

    // 1. Create Team Dialog
    if (showCreateTeamModal) {
        CreateTeamModal(
            onDismiss = { showCreateTeamModal = false },
            onSave = { name, ageCat, gender, coach, assistantCoach, analyst, comp, formation, training ->
                clubDashboardViewModel.createTeam(
                    name = name,
                    ageCategory = ageCat,
                    coach = coach,
                    competition = comp,
                    gender = gender,
                    assistantCoach = assistantCoach,
                    analyst = analyst,
                    formation = formation,
                    notes = training
                )
                showCreateTeamModal = false
            }
        )
    }

    // 2. Create Competition Dialog
    if (showCreateCompetitionModal) {
        CreateCompetitionModal(
            teams = teamsList,
            onDismiss = { showCreateCompetitionModal = false },
            onSave = { name, season, tier, division, federation, teamName ->
                clubDashboardViewModel.createCompetition(
                    name = name,
                    season = season,
                    tier = tier,
                    division = division,
                    federation = federation,
                    teamName = teamName
                )
                showCreateCompetitionModal = false
            }
        )
    }

    // 3. Schedule Match Dialog
    if (showScheduleMatchModal) {
        ScheduleMatchModal(
            teams = teamsList,
            preselectedTeam = teamToScheduleFor?.name ?: "Senior Team",
            onDismiss = {
                showScheduleMatchModal = false
                teamToScheduleFor = null
            },
            onSave = { teamName, opponent, date, venue, comp ->
                clubDashboardViewModel.createMatch(teamName, opponent, date, venue, comp)
                showScheduleMatchModal = false
                teamToScheduleFor = null
            }
        )
    }

    // 4. Update Match Score Dialog
    matchToEditScore?.let { match ->
        UpdateScoreModal(
            match = match,
            onDismiss = { matchToEditScore = null },
            onSave = { home, away, status ->
                clubDashboardViewModel.updateMatchScore(match.id, home, away, status)
                matchToEditScore = null
            }
        )
    }

    // 5. Issue License Dialog
    if (showIssueLicenseModal) {
        IssuePlayerLicenseModal(
            athletes = athletesList,
            competitions = competitionsList,
            onDismiss = { showIssueLicenseModal = false },
            onSave = { athleteName, licenseNo, comp ->
                clubDashboardViewModel.registerAthlete(athleteName, licenseNo, comp)
                showIssueLicenseModal = false
            }
        )
    }
}

// --- SUB COMPONENTS & DIALOGS ---

@Composable
private fun QuickStatsPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
            Text(label, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
private fun TabNavigationPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) TGColors.Panel2 else Color.Transparent,
        border = BorderStroke(1.dp, if (isSelected) TGColors.Acc else Color.Transparent),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) TGColors.Acc else TGColors.Mut,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isSelected) TGColors.Acc else TGColors.Mut
            )
        }
    }
}

@Composable
private fun InfoBlock(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TGColors.Mut)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TGColors.Txt)
    }
}

@Composable
private fun StandingBox(label: String, value: String, modifier: Modifier = Modifier, isHighlight: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isHighlight) TGColors.Panel3 else TGColors.Panel2,
        border = BorderStroke(1.dp, if (isHighlight) TGColors.Warn else TGColors.Line),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TGColors.Mut)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (isHighlight) TGColors.Warn else TGColors.Txt)
        }
    }
}

// 1. Create Team Modal
@Composable
private fun CreateTeamModal(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ageCategory by remember { mutableStateOf("Senior First Team") }
    var gender by remember { mutableStateOf("Men") }
    var coach by remember { mutableStateOf("") }
    var assistantCoach by remember { mutableStateOf("") }
    var analyst by remember { mutableStateOf("") }
    var competition by remember { mutableStateOf("National Premier League") }
    var formation by remember { mutableStateOf("4-3-3 Attacking") }
    var training by remember { mutableStateOf("Mon, Wed, Fri 08:30 AM") }

    val ageOptions = listOf("Senior First Team", "U23 Development", "U20 Academy", "U17 Youth", "U15 Junior")
    val genderOptions = listOf("Men", "Women", "Mixed")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
            border = BorderStroke(1.dp, TGColors.Line),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Create Squad Division", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                    Text("Configure new team category in Firestore", fontSize = 12.sp, color = TGColors.Mut)
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Team Name (e.g. Senior Men First Team)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Text("Age Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ageOptions) { opt ->
                            FilterChip(
                                selected = ageCategory == opt,
                                onClick = { ageCategory = opt },
                                label = { Text(opt, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Gender", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = coach,
                        onValueChange = { coach = it },
                        label = { Text("Head Coach Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = assistantCoach,
                        onValueChange = { assistantCoach = it },
                        label = { Text("Assistant Coach (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = analyst,
                        onValueChange = { analyst = it },
                        label = { Text("Performance Analyst (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = competition,
                        onValueChange = { competition = it },
                        label = { Text("Primary Competition") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = formation,
                        onValueChange = { formation = it },
                        label = { Text("Tactical Formation (e.g. 4-3-3, 4-2-3-1)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = training,
                        onValueChange = { training = it },
                        label = { Text("Training Schedule Times") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onSave(name, ageCategory, gender, coach, assistantCoach, analyst, competition, formation, training)
                                }
                            },
                            enabled = name.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Create Team", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 2. Create Competition Modal
@Composable
private fun CreateCompetitionModal(
    teams: List<ClubTeamModel>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var season by remember { mutableStateOf("2026/2027") }
    var tier by remember { mutableStateOf("Premier Tier 1") }
    var division by remember { mutableStateOf("National Premier Division") }
    var federation by remember { mutableStateOf("National Football Federation") }
    var teamName by remember { mutableStateOf(teams.firstOrNull()?.name ?: "Senior Team") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
            border = BorderStroke(1.dp, TGColors.Line),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Register Competition", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                    Text("Register club team into league or cup tournament", fontSize = 12.sp, color = TGColors.Mut)
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Competition Name (e.g. National Premier League)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = season,
                        onValueChange = { season = it },
                        label = { Text("Season (e.g. 2026/2027)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = tier,
                        onValueChange = { tier = it },
                        label = { Text("Tier / Category (e.g. Premier Tier 1)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = federation,
                        onValueChange = { federation = it },
                        label = { Text("Sanctioning Federation") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Participating Squad Division") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onSave(name, season, tier, division, federation, teamName)
                                }
                            },
                            enabled = name.isNotBlank(),
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Register", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 3. Schedule Match Modal
@Composable
private fun ScheduleMatchModal(
    teams: List<ClubTeamModel>,
    preselectedTeam: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String) -> Unit
) {
    var teamName by remember { mutableStateOf(preselectedTeam.ifBlank { teams.firstOrNull()?.name ?: "Senior Team" }) }
    var opponent by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-10-15") }
    var venue by remember { mutableStateOf("Home Stadium") }
    var competition by remember { mutableStateOf("National Premier League") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
            border = BorderStroke(1.dp, TGColors.Line),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Schedule Match Fixture", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Club Team") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = opponent,
                    onValueChange = { opponent = it },
                    label = { Text("Opponent Team Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = competition,
                    onValueChange = { competition = it },
                    label = { Text("Competition / League") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Match Date") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (opponent.isNotBlank()) {
                                onSave(teamName, opponent, date, venue, competition)
                            }
                        },
                        enabled = opponent.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Schedule", color = Color.White)
                    }
                }
            }
        }
    }
}

// 4. Update Score Modal
@Composable
private fun UpdateScoreModal(
    match: ClubMatchModel,
    onDismiss: () -> Unit,
    onSave: (Int, Int, String) -> Unit
) {
    var homeScoreText by remember { mutableStateOf(match.homeScore.toString()) }
    var awayScoreText by remember { mutableStateOf(match.awayScore.toString()) }
    var status by remember { mutableStateOf(match.status) }

    val statusOptions = listOf("Upcoming", "Live", "Completed")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
            border = BorderStroke(1.dp, TGColors.Line),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Update Match Result", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                Text("${match.teamName} vs ${match.opponent}", fontSize = 13.sp, color = TGColors.Mut)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = homeScoreText,
                        onValueChange = { homeScoreText = it },
                        label = { Text("${match.teamName} (Home)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Text("—", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = awayScoreText,
                        onValueChange = { awayScoreText = it },
                        label = { Text("${match.opponent} (Away)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Text("Match Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { opt ->
                        FilterChip(
                            selected = status == opt,
                            onClick = { status = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val home = homeScoreText.toIntOrNull() ?: 0
                            val away = awayScoreText.toIntOrNull() ?: 0
                            onSave(home, away, status)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Result", color = Color.White)
                    }
                }
            }
        }
    }
}

// 5. Issue Player License Modal
@Composable
private fun IssuePlayerLicenseModal(
    athletes: List<AthleteEntity>,
    competitions: List<ClubCompetitionModel>,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var athleteName by remember { mutableStateOf(athletes.firstOrNull()?.name ?: "") }
    var licenseNumber by remember { mutableStateOf("LIC-FKF-${(1000..9999).random()}") }
    var compName by remember { mutableStateOf(competitions.firstOrNull()?.name ?: "National Premier League") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = TGColors.Panel),
            border = BorderStroke(1.dp, TGColors.Line),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Issue Player License", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                Text("Generate verified competition license registered in Firestore", fontSize = 12.sp, color = TGColors.Mut)

                OutlinedTextField(
                    value = athleteName,
                    onValueChange = { athleteName = it },
                    label = { Text("Athlete Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = licenseNumber,
                    onValueChange = { licenseNumber = it },
                    label = { Text("Federation License ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = compName,
                    onValueChange = { compName = it },
                    label = { Text("Authorized Competition") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (athleteName.isNotBlank() && licenseNumber.isNotBlank()) {
                                onSave(athleteName, licenseNumber, compName)
                            }
                        },
                        enabled = athleteName.isNotBlank() && licenseNumber.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Issue License", color = Color.White)
                    }
                }
            }
        }
    }
}
