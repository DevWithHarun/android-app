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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubMatchesAndFixturesView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val successColor = Color(0xFF16A34A)
    val liveColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var statusFilter by rememberSaveable { mutableStateOf("All") }
    var teamFilter by rememberSaveable { mutableStateOf("All Teams") }
    var competitionFilter by rememberSaveable { mutableStateOf("All Competitions") }

    var showScheduleDialog by remember { mutableStateOf(false) }
    var selectedMatchForDetails by remember { mutableStateOf<ClubMatchModel?>(null) }
    var selectedMatchForScore by remember { mutableStateOf<ClubMatchModel?>(null) }
    var selectedMatchForEvent by remember { mutableStateOf<ClubMatchModel?>(null) }
    var selectedMatchForLineup by remember { mutableStateOf<ClubMatchModel?>(null) }
    var matchToDelete by remember { mutableStateOf<ClubMatchModel?>(null) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val allMatches = successState?.matches ?: emptyList()
    val clubTeams = successState?.teams ?: emptyList()
    val clubCompetitions = successState?.competitions ?: emptyList()
    val clubAthletes = successState?.athletes ?: emptyList()

    // Filter matches
    val filteredMatches = remember(allMatches, statusFilter, teamFilter, competitionFilter) {
        allMatches.filter { match ->
            val matchesStatus = when (statusFilter) {
                "Upcoming" -> match.status.equals("Upcoming", ignoreCase = true)
                "Live" -> match.status.equals("Live", ignoreCase = true) || match.status.equals("Halftime", ignoreCase = true)
                "Completed" -> match.status.equals("Completed", ignoreCase = true) || match.status.equals("Full Time", ignoreCase = true)
                "Postponed" -> match.status.equals("Postponed", ignoreCase = true) || match.status.equals("Cancelled", ignoreCase = true)
                else -> true
            }
            val matchesTeam = teamFilter == "All Teams" || match.teamName.equals(teamFilter, ignoreCase = true)
            val matchesComp = competitionFilter == "All Competitions" || match.competition.equals(competitionFilter, ignoreCase = true)
            matchesStatus && matchesTeam && matchesComp
        }
    }

    // Key Stats
    val liveCount = allMatches.count { it.status.equals("Live", ignoreCase = true) || it.status.equals("Halftime", ignoreCase = true) }
    val upcomingCount = allMatches.count { it.status.equals("Upcoming", ignoreCase = true) }
    val completedCount = allMatches.count { it.status.equals("Completed", ignoreCase = true) || it.status.equals("Full Time", ignoreCase = true) }
    val wonCount = allMatches.count {
        (it.status.equals("Completed", ignoreCase = true) || it.status.equals("Full Time", ignoreCase = true)) &&
                ((it.venue.equals("Home", ignoreCase = true) && it.homeScore > it.awayScore) ||
                        (it.venue.equals("Away", ignoreCase = true) && it.awayScore > it.homeScore))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
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
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.SportsScore, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Matches & Fixtures Hub",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Official matchday operations, live scoring, lineups & calendar for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // KPI Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MatchKpiCard(
                            label = "Total Fixtures",
                            value = "${allMatches.size}",
                            color = primaryColor,
                            modifier = Modifier.weight(1f)
                        )
                        MatchKpiCard(
                            label = "Live In-Play",
                            value = "$liveCount",
                            color = if (liveCount > 0) liveColor else textMuted,
                            modifier = Modifier.weight(1f)
                        )
                        MatchKpiCard(
                            label = "Upcoming",
                            value = "$upcomingCount",
                            color = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        )
                        MatchKpiCard(
                            label = "Victories",
                            value = "$wonCount / $completedCount",
                            color = successColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showScheduleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Schedule New Match Fixture", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Filter Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Status Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Upcoming", "Live", "Completed", "Postponed").forEach { status ->
                        val selected = statusFilter == status
                        FilterChip(
                            selected = selected,
                            onClick = { statusFilter = status },
                            label = {
                                Text(
                                    text = if (status == "Live" && liveCount > 0) "🔴 Live ($liveCount)" else status,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (status == "Live") Color(0xFFFEE2E2) else purpleAccent.copy(alpha = 0.15f),
                                selectedLabelColor = if (status == "Live") liveColor else purpleAccent
                            )
                        )
                    }
                }

                // Team & Competition Filter Dropdowns Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Team filter
                    val availableTeams = remember(clubTeams) { listOf("All Teams") + clubTeams.map { it.name }.distinct() }
                    var teamDropdownExpanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { teamDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = teamFilter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = teamDropdownExpanded,
                            onDismissRequest = { teamDropdownExpanded = false }
                        ) {
                            availableTeams.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t, fontSize = 13.sp) },
                                    onClick = {
                                        teamFilter = t
                                        teamDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Competition filter
                    val availableComps = remember(clubCompetitions, allMatches) {
                        listOf("All Competitions") + (clubCompetitions.map { it.name } + allMatches.map { it.competition }).filter { it.isNotBlank() }.distinct()
                    }
                    var compDropdownExpanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { compDropdownExpanded = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = competitionFilter,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        DropdownMenu(
                            expanded = compDropdownExpanded,
                            onDismissRequest = { compDropdownExpanded = false }
                        ) {
                            availableComps.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c, fontSize = 13.sp) },
                                    onClick = {
                                        competitionFilter = c
                                        compDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Match Fixture List Items
        if (filteredMatches.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.EventBusy,
                            contentDescription = null,
                            tint = textMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matches found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No fixtures match the selected filters or none have been scheduled yet.",
                            fontSize = 12.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showScheduleDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule Match Now", fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredMatches, key = { it.id.ifBlank { "${it.teamName}_${it.opponent}_${it.date}" } }) { match ->
                MatchCardItem(
                    match = match,
                    activeClubName = activeClubName,
                    onViewDetails = { selectedMatchForDetails = match },
                    onUpdateScore = { selectedMatchForScore = match },
                    onAddEvent = { selectedMatchForEvent = match },
                    onEditLineup = { selectedMatchForLineup = match },
                    onDelete = { matchToDelete = match }
                )
            }
        }
    }

    // --- DIALOGS ---

    // 1. Schedule Match Dialog
    if (showScheduleDialog) {
        ScheduleMatchDialog(
            clubTeams = clubTeams,
            clubCompetitions = clubCompetitions,
            onDismiss = { showScheduleDialog = false },
            onSchedule = { team, opp, date, time, venue, stadium, city, comp, round, mType, ref, form, capt, notes ->
                clubDashboardViewModel.createMatch(
                    teamName = team,
                    opponent = opp,
                    date = date,
                    time = time,
                    venue = venue,
                    stadium = stadium,
                    city = city,
                    comp = comp,
                    round = round,
                    matchType = mType,
                    referee = ref,
                    formation = form,
                    captain = capt,
                    notes = notes
                )
                showScheduleDialog = false
            }
        )
    }

    // 2. Full Match Center & Details Modal
    selectedMatchForDetails?.let { match ->
        MatchCenterDetailDialog(
            match = match,
            activeClubName = activeClubName,
            onDismiss = { selectedMatchForDetails = null },
            onQuickUpdateScore = {
                selectedMatchForDetails = null
                selectedMatchForScore = match
            },
            onAddEvent = {
                selectedMatchForDetails = null
                selectedMatchForEvent = match
            },
            onEditLineup = {
                selectedMatchForDetails = null
                selectedMatchForLineup = match
            }
        )
    }

    // 3. Update Score & Status Dialog
    selectedMatchForScore?.let { match ->
        UpdateScoreDialog(
            match = match,
            activeClubName = activeClubName,
            onDismiss = { selectedMatchForScore = null },
            onSave = { homeScore, awayScore, status, minute, motm, notes ->
                clubDashboardViewModel.updateMatchScore(
                    matchId = match.id,
                    homeScore = homeScore,
                    awayScore = awayScore,
                    status = status,
                    minute = minute,
                    notes = notes,
                    manOfTheMatch = motm
                )
                selectedMatchForScore = null
            }
        )
    }

    // 4. Log Match Event Dialog
    selectedMatchForEvent?.let { match ->
        AddMatchEventDialog(
            match = match,
            activeClubName = activeClubName,
            clubAthletes = clubAthletes,
            onDismiss = { selectedMatchForEvent = null },
            onSaveEvent = { min, type, team, player, assist, subIn, notes ->
                clubDashboardViewModel.addMatchEvent(
                    matchId = match.id,
                    minute = min,
                    type = type,
                    team = team,
                    player = player,
                    assistPlayer = assist,
                    subInPlayer = subIn,
                    notes = notes
                )
                selectedMatchForEvent = null
            }
        )
    }

    // 5. Matchday Squad Lineup & Tactics Dialog
    selectedMatchForLineup?.let { match ->
        MatchLineupDialog(
            match = match,
            clubAthletes = clubAthletes,
            onDismiss = { selectedMatchForLineup = null },
            onSaveLineup = { startingXI, subs, formation, captain ->
                clubDashboardViewModel.updateMatchLineup(
                    matchId = match.id,
                    startingXI = startingXI,
                    substitutes = subs,
                    formation = formation,
                    captain = captain
                )
                selectedMatchForLineup = null
            }
        )
    }

    // 6. Delete Confirmation Dialog
    matchToDelete?.let { match ->
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            icon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFDC2626)) },
            title = { Text("Delete Match Fixture?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to cancel and delete the match fixture '${match.teamName} vs ${match.opponent}' on ${match.date}?") },
            confirmButton = {
                Button(
                    onClick = {
                        clubDashboardViewModel.deleteMatch(match.id, "${match.teamName} vs ${match.opponent}")
                        matchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete Fixture")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { matchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// COMPOSABLES & SUB-VIEWS
// ==========================================

@Composable
fun MatchKpiCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F5F9),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MatchCardItem(
    match: ClubMatchModel,
    activeClubName: String,
    onViewDetails: () -> Unit,
    onUpdateScore: () -> Unit,
    onAddEvent: () -> Unit,
    onEditLineup: () -> Unit,
    onDelete: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val liveColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    val isLive = match.status.equals("Live", ignoreCase = true) || match.status.equals("Halftime", ignoreCase = true)
    val isCompleted = match.status.equals("Completed", ignoreCase = true) || match.status.equals("Full Time", ignoreCase = true)
    val isUpcoming = match.status.equals("Upcoming", ignoreCase = true)

    val homeDisplayName = if (match.venue.equals("Home", ignoreCase = true)) match.teamName else match.opponent
    val awayDisplayName = if (match.venue.equals("Home", ignoreCase = true)) match.opponent else match.teamName

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (isLive) 1.5.dp else 1.dp,
            color = if (isLive) Color(0xFFEF4444) else borderColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Competition Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = match.competition.ifBlank { "League Match" },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                    if (match.round.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${match.round}",
                            fontSize = 11.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Match Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isLive -> Color(0xFFFEE2E2)
                        isCompleted -> Color(0xFFDCFCE7)
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE ${if (match.minute > 0) "${match.minute}'" else ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                        } else if (isCompleted) {
                            Text(
                                text = "FULL TIME",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        } else {
                            Text(
                                text = "${match.date} • ${match.time}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Presentation (Home vs Away)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3E8FF),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = homeDisplayName.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = purpleAccent,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = homeDisplayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (match.venue.equals("Home", ignoreCase = true)) "Home (Our Team)" else "Home",
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }

                // Middle Score / VS Box
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isUpcoming) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Text(
                                text = "VS",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = textMuted
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLive) Color(0xFFFEF2F2) else Color(0xFF0F172A)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${match.homeScore}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLive) Color(0xFFDC2626) else Color.White
                                )
                                Text(
                                    text = " - ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLive) Color(0xFFDC2626) else Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "${match.awayScore}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLive) Color(0xFFDC2626) else Color.White
                                )
                            }
                        }
                    }
                }

                // Away Team
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = awayDisplayName.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = primaryColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = awayDisplayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = primaryColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (match.venue.equals("Away", ignoreCase = true)) "Away (Our Team)" else "Away",
                        fontSize = 10.sp,
                        color = textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Venue, Stadium & Formation Meta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${match.stadium}, ${match.city}",
                        fontSize = 11.sp,
                        color = textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (match.formation.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DashboardCustomize, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = match.formation,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = purpleAccent
                        )
                    }
                }
            }

            // Quick Match Events Snippet (e.g. goals)
            if (match.matchEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    match.matchEvents.take(3).forEach { ev ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = when (ev.type) {
                                    "Goal" -> "⚽ ${ev.player} ${ev.minute}'"
                                    "Yellow Card" -> "🟨 ${ev.player} ${ev.minute}'"
                                    "Red Card" -> "🟥 ${ev.player} ${ev.minute}'"
                                    else -> "${ev.type} ${ev.player}"
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryColor
                            )
                        }
                    }
                    if (match.matchEvents.size > 3) {
                        Text("+${match.matchEvents.size - 3} more", fontSize = 10.sp, color = textMuted, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(38.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Match Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onUpdateScore,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isLive) liveColor else primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).height(38.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isUpcoming) "Set Result / Live" else "Update Score", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ==========================================
// DETAILED MATCH CENTER MODAL
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCenterDetailDialog(
    match: ClubMatchModel,
    activeClubName: String,
    onDismiss: () -> Unit,
    onQuickUpdateScore: () -> Unit,
    onAddEvent: () -> Unit,
    onEditLineup: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val textMuted = Color(0xFF64748B)

    var activeSubTab by rememberSaveable { mutableStateOf(0) }
    val tabTitles = listOf("Overview", "Timeline & Events", "Squad & Lineup", "Statistics & Report")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${match.teamName} vs ${match.opponent}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Text(
                            text = "${match.competition} • ${match.date} at ${match.time}",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = primaryColor)
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Scoreboard Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = match.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Team 1
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = match.teamName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = match.venue,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            // Score
                            Text(
                                text = "${match.homeScore} : ${match.awayScore}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            // Team 2
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = match.opponent,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (match.venue == "Home") "Away" else "Home",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📍 ${match.stadium}, ${match.city} • Referee: ${match.referee}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                // Tab Selector
                TabRow(
                    selectedTabIndex = activeSubTab,
                    containerColor = Color(0xFFF8FAFC),
                    contentColor = purpleAccent
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = activeSubTab == index,
                            onClick = { activeSubTab = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                    when (activeSubTab) {
                        0 -> MatchOverviewTab(match, onQuickUpdateScore, onAddEvent, onEditLineup)
                        1 -> MatchTimelineTab(match, onAddEvent)
                        2 -> MatchSquadLineupTab(match, onEditLineup)
                        3 -> MatchStatsTab(match)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchOverviewTab(
    match: ClubMatchModel,
    onQuickUpdateScore: () -> Unit,
    onAddEvent: () -> Unit,
    onEditLineup: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Fixture Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Competition", match.competition)
                    DetailRow("Stage / Matchday", match.round)
                    DetailRow("Match Type", match.matchType)
                    DetailRow("Date & Kickoff", "${match.date} at ${match.time}")
                    DetailRow("Stadium / Arena", "${match.stadium} (${match.venue})")
                    DetailRow("Host City", match.city)
                    DetailRow("Lead Referee", match.referee)
                    DetailRow("Tactical Formation", match.formation)
                    DetailRow("Match Captain", match.captain.ifBlank { "Unassigned" })
                    if (match.manOfTheMatch.isNotBlank()) {
                        DetailRow("Man of the Match", "⭐ ${match.manOfTheMatch}")
                    }
                }
            }
        }

        if (match.notes.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Tactical & Matchday Notes", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(match.notes, fontSize = 12.sp, color = textMuted)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onQuickUpdateScore,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update Score", fontSize = 12.sp)
                }
                Button(
                    onClick = onAddEvent,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Event", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MatchTimelineTab(
    match: ClubMatchModel,
    onAddEvent: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chronological Timeline", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Button(
                    onClick = onAddEvent,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Event", fontSize = 11.sp)
                }
            }
        }

        if (match.matchEvents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No match events logged yet.", fontSize = 12.sp, color = textMuted)
                }
            }
        } else {
            items(match.matchEvents.sortedBy { it.minute }) { ev ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEDE9FE),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${ev.minute}'", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7E22CE))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = when (ev.type) {
                                            "Goal" -> "⚽ Goal"
                                            "Yellow Card" -> "🟨 Yellow Card"
                                            "Red Card" -> "🟥 Red Card"
                                            "Substitution" -> "🔄 Substitution"
                                            "Penalty" -> "🥅 Penalty Goal"
                                            else -> ev.type
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = primaryColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(${ev.team.uppercase()})", fontSize = 10.sp, color = textMuted)
                                }
                                Text(
                                    text = if (ev.assistPlayer.isNotBlank()) "${ev.player} (Assist: ${ev.assistPlayer})"
                                    else if (ev.subInPlayer.isNotBlank()) "Out: ${ev.player} • In: ${ev.subInPlayer}"
                                    else ev.player,
                                    fontSize = 12.sp,
                                    color = textMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchSquadLineupTab(
    match: ClubMatchModel,
    onEditLineup: () -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val textMuted = Color(0xFF64748B)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Formation: ${match.formation}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    if (match.captain.isNotBlank()) {
                        Text("Captain: © ${match.captain}", fontSize = 11.sp, color = purpleAccent, fontWeight = FontWeight.SemiBold)
                    }
                }
                Button(
                    onClick = onEditLineup,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Lineup", fontSize = 11.sp)
                }
            }
        }

        // Starting XI Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Starting XI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (match.startingLineup.isEmpty()) {
                        Text("No starting lineup specified.", fontSize = 12.sp, color = textMuted)
                    } else {
                        match.startingLineup.forEachIndexed { idx, player ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${idx + 1}. $player", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                                if (player.equals(match.captain, ignoreCase = true)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE9FE)) {
                                        Text("CAPTAIN", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, color = purpleAccent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Substitutes Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Substitutes Bench", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (match.substitutes.isEmpty()) {
                        Text("No substitutes specified.", fontSize = 12.sp, color = textMuted)
                    } else {
                        match.substitutes.forEachIndexed { idx, sub ->
                            Text("${idx + 1}. $sub", fontSize = 12.sp, color = textMuted, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchStatsTab(match: ClubMatchModel) {
    val primaryColor = Color(0xFF0F172A)

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Matchday Statistics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    StatComparisonBar("Possession", "${match.possessionHome}%", "${100 - match.possessionHome}%", match.possessionHome)
                    StatComparisonBar("Total Shots", "${match.shotsHome}", "${match.shotsAway}", if (match.shotsHome + match.shotsAway > 0) (match.shotsHome * 100) / (match.shotsHome + match.shotsAway) else 50)
                    StatComparisonBar("Shots on Target", "${match.shotsOnTargetHome}", "${match.shotsOnTargetAway}", if (match.shotsOnTargetHome + match.shotsOnTargetAway > 0) (match.shotsOnTargetHome * 100) / (match.shotsOnTargetHome + match.shotsOnTargetAway) else 50)
                    StatComparisonBar("Corners", "${match.cornersHome}", "${match.cornersAway}", if (match.cornersHome + match.cornersAway > 0) (match.cornersHome * 100) / (match.cornersHome + match.cornersAway) else 50)
                    StatComparisonBar("Fouls", "${match.foulsHome}", "${match.foulsAway}", if (match.foulsHome + match.foulsAway > 0) (match.foulsHome * 100) / (match.foulsHome + match.foulsAway) else 50)
                    StatComparisonBar("Yellow Cards", "${match.yellowCardsHome}", "${match.yellowCardsAway}", if (match.yellowCardsHome + match.yellowCardsAway > 0) (match.yellowCardsHome * 100) / (match.yellowCardsHome + match.yellowCardsAway) else 50)
                    StatComparisonBar("Red Cards", "${match.redCardsHome}", "${match.redCardsAway}", if (match.redCardsHome + match.redCardsAway > 0) (match.redCardsHome * 100) / (match.redCardsHome + match.redCardsAway) else 50)
                }
            }
        }
    }
}

@Composable
fun StatComparisonBar(label: String, val1: String, val2: String, percent1: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(val1, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
            Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
            Text(val2, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0F172A))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((percent1.coerceIn(5, 95)).toFloat())
                    .background(Color(0xFF7E22CE))
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(((100 - percent1).coerceIn(5, 95)).toFloat())
                    .background(Color(0xFF94A3B8))
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
    }
}

// ==========================================
// DIALOGS
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleMatchDialog(
    clubTeams: List<ClubTeamModel>,
    clubCompetitions: List<ClubCompetitionModel>,
    onDismiss: () -> Unit,
    onSchedule: (
        team: String,
        opponent: String,
        date: String,
        time: String,
        venue: String,
        stadium: String,
        city: String,
        competition: String,
        round: String,
        matchType: String,
        referee: String,
        formation: String,
        captain: String,
        notes: String
    ) -> Unit
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)

    var teamName by remember { mutableStateOf(clubTeams.firstOrNull()?.name ?: "Senior Team") }
    var opponentName by remember { mutableStateOf("") }
    var competition by remember { mutableStateOf(clubCompetitions.firstOrNull()?.name ?: "National Premier League") }
    var matchDate by remember { mutableStateOf("2026-10-15") }
    var matchTime by remember { mutableStateOf("15:00") }
    var venue by remember { mutableStateOf("Home") }
    var stadium by remember { mutableStateOf("City Stadium") }
    var city by remember { mutableStateOf("Nairobi") }
    var round by remember { mutableStateOf("Matchday 14") }
    var matchType by remember { mutableStateOf("League") }
    var referee by remember { mutableStateOf("Federation Referee") }
    var formation by remember { mutableStateOf("4-3-3") }
    var captain by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule New Match Fixture", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Club Team (e.g. Senior Team, U20)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = opponentName,
                            onValueChange = { opponentName = it },
                            label = { Text("Opponent Club / Team Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = competition,
                            onValueChange = { competition = it },
                            label = { Text("Competition / League Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = matchDate,
                                onValueChange = { matchDate = it },
                                label = { Text("Date (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = matchTime,
                                onValueChange = { matchTime = it },
                                label = { Text("Kickoff Time") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = venue,
                                onValueChange = { venue = it },
                                label = { Text("Venue (Home/Away)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = round,
                                onValueChange = { round = it },
                                label = { Text("Round (e.g. MD 10)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = stadium,
                                onValueChange = { stadium = it },
                                label = { Text("Stadium") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = formation,
                                onValueChange = { formation = it },
                                label = { Text("Tactical Formation") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = referee,
                                onValueChange = { referee = it },
                                label = { Text("Referee") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = captain,
                            onValueChange = { captain = it },
                            label = { Text("Match Captain") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Match Directives / Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (opponentName.isNotBlank()) {
                            onSchedule(
                                teamName.trim(),
                                opponentName.trim(),
                                matchDate.trim(),
                                matchTime.trim(),
                                venue.trim(),
                                stadium.trim(),
                                city.trim(),
                                competition.trim(),
                                round.trim(),
                                matchType.trim(),
                                referee.trim(),
                                formation.trim(),
                                captain.trim(),
                                notes.trim()
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Confirm & Save Fixture to Calendar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UpdateScoreDialog(
    match: ClubMatchModel,
    activeClubName: String,
    onDismiss: () -> Unit,
    onSave: (homeScore: Int, awayScore: Int, status: String, minute: Int, motm: String, notes: String) -> Unit
) {
    var homeScore by remember { mutableStateOf(match.homeScore) }
    var awayScore by remember { mutableStateOf(match.awayScore) }
    var status by remember { mutableStateOf(if (match.status == "Upcoming") "Live" else match.status) }
    var minute by remember { mutableStateOf(match.minute.takeIf { it > 0 } ?: 90) }
    var motm by remember { mutableStateOf(match.manOfTheMatch) }
    var notes by remember { mutableStateOf(match.notes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Record Match Score & Status", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Home Score
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.teamName, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (homeScore > 0) homeScore-- },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE2E8F0))
                            ) {
                                Text("-", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Text("$homeScore", fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp))
                            FilledIconButton(
                                onClick = { homeScore++ },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7E22CE))
                            ) {
                                Text("+", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }

                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Black)

                    // Away Score
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.opponent, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (awayScore > 0) awayScore-- },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE2E8F0))
                            ) {
                                Text("-", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Text("$awayScore", fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp))
                            FilledIconButton(
                                onClick = { awayScore++ },
                                modifier = Modifier.size(32.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7E22CE))
                            ) {
                                Text("+", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status selection
                Text("Match Status", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Live", "Halftime", "Completed", "Postponed").forEach { s ->
                        FilterChip(
                            selected = status.equals(s, ignoreCase = true),
                            onClick = { status = s },
                            label = { Text(s, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = motm,
                    onValueChange = { motm = it },
                    label = { Text("Man of the Match") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Match Result Summary Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onSave(homeScore, awayScore, status, minute, motm, notes) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save Match Result", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddMatchEventDialog(
    match: ClubMatchModel,
    activeClubName: String,
    clubAthletes: List<AthleteEntity>,
    onDismiss: () -> Unit,
    onSaveEvent: (minute: Int, type: String, team: String, player: String, assist: String, subIn: String, notes: String) -> Unit
) {
    var eventType by remember { mutableStateOf("Goal") }
    var eventTeam by remember { mutableStateOf("home") }
    var minuteText by remember { mutableStateOf("45") }
    var playerName by remember { mutableStateOf(clubAthletes.firstOrNull()?.name ?: "") }
    var assistPlayer by remember { mutableStateOf("") }
    var subInPlayer by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Log Matchday Event", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Event Type chips
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Goal", "Yellow Card", "Red Card", "Substitution", "Penalty").forEach { type ->
                        FilterChip(
                            selected = eventType == type,
                            onClick = { eventType = type },
                            label = { Text(type, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Team selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = eventTeam == "home",
                        onClick = { eventTeam = "home" },
                        label = { Text("Home (${match.teamName})", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = eventTeam == "away",
                        onClick = { eventTeam = "away" },
                        label = { Text("Away (${match.opponent})", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it },
                        label = { Text("Minute (1-120)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Primary Player *") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }

                if (eventType == "Goal") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = assistPlayer,
                        onValueChange = { assistPlayer = it },
                        label = { Text("Assist By (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else if (eventType == "Substitution") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subInPlayer,
                        onValueChange = { subInPlayer = it },
                        label = { Text("Sub In (Coming On) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (playerName.isNotBlank()) {
                            onSaveEvent(
                                minuteText.toIntOrNull() ?: 45,
                                eventType,
                                eventTeam,
                                playerName.trim(),
                                assistPlayer.trim(),
                                subInPlayer.trim(),
                                notes.trim()
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Record Event to Timeline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchLineupDialog(
    match: ClubMatchModel,
    clubAthletes: List<AthleteEntity>,
    onDismiss: () -> Unit,
    onSaveLineup: (startingXI: List<String>, subs: List<String>, formation: String, captain: String) -> Unit
) {
    var formation by remember { mutableStateOf(match.formation) }
    var captain by remember { mutableStateOf(match.captain) }
    var startingXIString by remember {
        mutableStateOf(
            if (match.startingLineup.isNotEmpty()) match.startingLineup.joinToString("\n")
            else clubAthletes.take(11).mapIndexed { i, a -> "${i + 1}. ${a.name} (${a.position})" }.joinToString("\n")
        )
    }
    var subsString by remember {
        mutableStateOf(
            if (match.substitutes.isNotEmpty()) match.substitutes.joinToString("\n")
            else clubAthletes.drop(11).take(7).mapIndexed { i, a -> "${i + 1}. ${a.name} (${a.position})" }.joinToString("\n")
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Configure Squad Lineup & Tactics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formation,
                        onValueChange = { formation = it },
                        label = { Text("Formation (e.g. 4-3-3)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = captain,
                        onValueChange = { captain = it },
                        label = { Text("Captain Name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = startingXIString,
                    onValueChange = { startingXIString = it },
                    label = { Text("Starting XI (One per line)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subsString,
                    onValueChange = { subsString = it },
                    label = { Text("Substitutes Bench (One per line)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val xiList = startingXIString.lines().map { it.trim() }.filter { it.isNotBlank() }
                        val subList = subsString.lines().map { it.trim() }.filter { it.isNotBlank() }
                        onSaveLineup(xiList, subList, formation.trim(), captain.trim())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save Tactical Lineup", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
