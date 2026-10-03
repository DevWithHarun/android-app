package com.example.ui.auth.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class TeamStatsRecord(
    val team: String,
    val matches: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: String,
    val cleanSheets: Int,
    val points: Int,
    val winPercent: String,
    val avgGoals: String,
    val avgConceded: String,
    val possessionPercent: String,
    val passPercent: String,
    val homeMatches: Int = 10,
    val homeWins: Int = 7,
    val awayMatches: Int = 10,
    val awayWins: Int = 6,
    val homeGoals: Int = 22,
    val awayGoals: Int = 16,
    val shots: Int = 210,
    val shotsOnTarget: Int = 104,
    val corners: Int = 98,
    val chancesCreated: Int = 42
)

data class GoalEventRecord(
    val date: String,
    val match: String,
    val player: String,
    val goalTime: String,
    val assist: String,
    val goalType: String,
    val scoreAfterGoal: String
)

data class DisciplineEventRecord(
    val date: String,
    val match: String,
    val player: String,
    val event: String,
    val minute: String,
    val reason: String,
    val cardSymbol: String
)

@Composable
fun ClubStatisticsHubView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String,
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val bg = TGColors.Bg
    val panel = TGColors.Panel
    val panel2 = TGColors.Panel2
    val panel3 = TGColors.Panel3
    val line = TGColors.Line
    val txt = TGColors.Txt
    val mut = TGColors.Mut
    val acc = TGColors.Acc
    val acc2 = TGColors.Acc2
    val warn = TGColors.Warn
    val bad = TGColors.Bad

    var selectedTab by remember(selectedTabIndex) { mutableIntStateOf(selectedTabIndex.coerceIn(0, 3)) }
    val tabs = listOf("Players", "Teams", "Goals", "Discipline")

    // Filter states
    var selectedSeason by rememberSaveable { mutableStateOf("2026") }
    val seasons = listOf("2026", "2025", "All")

    var selectedCompetition by rememberSaveable { mutableStateOf("All") }
    val competitions = listOf("All", "Premier League", "National Cup", "Super Cup")

    var selectedTeam by rememberSaveable { mutableStateOf("All") }
    val teamOptions = listOf("All", "First Team", "U21", "U18")

    var selectedPosition by rememberSaveable { mutableStateOf("All") }
    val positions = listOf("All", "FW", "MF", "DF", "GK")

    var selectedVenue by rememberSaveable { mutableStateOf("All") }
    val venues = listOf("All", "Home", "Away")

    var searchPlayerQuery by rememberSaveable { mutableStateOf("") }

    // Drill down modal state
    var selectedPlayerForDrillDown by remember { mutableStateOf<PlayerStatsRecord?>(null) }

    // Core Player Records (Includes exact user specifications for Harun and Nzai)
    val rawPlayers = remember {
        listOf(
            PlayerStatsRecord(
                name = "Harun",
                position = "FW",
                team = "First Team",
                apps = 18,
                starts = 15,
                minutes = 1321,
                goals = 11,
                assists = 7,
                shots = 43,
                shotsOnTarget = 27,
                keyPasses = 31,
                passes = 482,
                passAccuracy = "83.2%",
                dribbles = 64,
                successfulDribbles = 41,
                tackles = 18,
                interceptions = 9,
                clearances = 6,
                fouls = 12,
                yellowCards = 2,
                redCards = 0,
                penaltyGoals = 2,
                nonPenaltyGoals = 9,
                ownGoals = 0,
                matchesScoredIn = 9,
                firstGoalMinute = "12'",
                lastGoalMinute = "87'",
                secondYellows = 0,
                suspensions = 0,
                matchesMissed = 0,
                offsides = 3,
                penaltiesConceded = 0
            ),
            PlayerStatsRecord(
                name = "Nzai",
                position = "MF",
                team = "First Team",
                apps = 16,
                starts = 12,
                minutes = 1087,
                goals = 8,
                assists = 5,
                shots = 36,
                shotsOnTarget = 21,
                keyPasses = 24,
                passes = 421,
                passAccuracy = "79.8%",
                dribbles = 52,
                successfulDribbles = 32,
                tackles = 24,
                interceptions = 14,
                clearances = 11,
                fouls = 16,
                yellowCards = 3,
                redCards = 1,
                penaltyGoals = 1,
                nonPenaltyGoals = 7,
                ownGoals = 0,
                matchesScoredIn = 7,
                firstGoalMinute = "8'",
                lastGoalMinute = "79'",
                secondYellows = 1,
                suspensions = 1,
                matchesMissed = 2,
                offsides = 2,
                penaltiesConceded = 1
            ),
            PlayerStatsRecord(
                name = "John Kamau",
                position = "FW",
                team = "First Team",
                apps = 14,
                starts = 11,
                minutes = 980,
                goals = 6,
                assists = 4,
                shots = 28,
                shotsOnTarget = 18,
                keyPasses = 19,
                passes = 310,
                passAccuracy = "81.0%",
                dribbles = 38,
                successfulDribbles = 24,
                tackles = 14,
                interceptions = 8,
                clearances = 4,
                fouls = 10,
                yellowCards = 1,
                redCards = 0
            ),
            PlayerStatsRecord(
                name = "Brian Ochieng",
                position = "MF",
                team = "U21",
                apps = 15,
                starts = 14,
                minutes = 1140,
                goals = 4,
                assists = 8,
                shots = 22,
                shotsOnTarget = 12,
                keyPasses = 38,
                passes = 640,
                passAccuracy = "86.5%",
                dribbles = 44,
                successfulDribbles = 30,
                tackles = 32,
                interceptions = 22,
                clearances = 12,
                fouls = 18,
                yellowCards = 2,
                redCards = 0
            ),
            PlayerStatsRecord(
                name = "Kevin Otieno",
                position = "DF",
                team = "First Team",
                apps = 18,
                starts = 18,
                minutes = 1620,
                goals = 2,
                assists = 1,
                shots = 8,
                shotsOnTarget = 4,
                keyPasses = 6,
                passes = 810,
                passAccuracy = "88.2%",
                dribbles = 12,
                successfulDribbles = 9,
                tackles = 54,
                interceptions = 41,
                clearances = 68,
                fouls = 22,
                yellowCards = 4,
                redCards = 0
            )
        )
    }

    // Filtered Players
    val filteredPlayers = remember(searchPlayerQuery, selectedPosition, selectedTeam, rawPlayers) {
        rawPlayers.filter { p ->
            val matchName = searchPlayerQuery.isBlank() || p.name.contains(searchPlayerQuery, ignoreCase = true)
            val matchPos = selectedPosition == "All" || p.position.equals(selectedPosition, ignoreCase = true)
            val matchTeam = selectedTeam == "All" || p.team.equals(selectedTeam, ignoreCase = true)
            matchName && matchPos && matchTeam
        }
    }

    // Core Team Records (Exact user specifications)
    val teamRecords = remember {
        listOf(
            TeamStatsRecord(
                team = "First Team",
                matches = 20,
                wins = 13,
                draws = 4,
                losses = 3,
                goalsFor = 38,
                goalsAgainst = 17,
                goalDifference = "+21",
                cleanSheets = 8,
                points = 43,
                winPercent = "65%",
                avgGoals = "1.90",
                avgConceded = "0.85",
                possessionPercent = "56.4%",
                passPercent = "82.1%",
                homeMatches = 10,
                homeWins = 7,
                awayMatches = 10,
                awayWins = 6,
                homeGoals = 22,
                awayGoals = 16,
                shots = 264,
                shotsOnTarget = 118,
                corners = 112,
                chancesCreated = 52
            ),
            TeamStatsRecord(
                team = "U21",
                matches = 18,
                wins = 9,
                draws = 5,
                losses = 4,
                goalsFor = 29,
                goalsAgainst = 20,
                goalDifference = "+9",
                cleanSheets = 6,
                points = 32,
                winPercent = "50%",
                avgGoals = "1.61",
                avgConceded = "1.11",
                possessionPercent = "53.2%",
                passPercent = "78.6%",
                homeMatches = 9,
                homeWins = 5,
                awayMatches = 9,
                awayWins = 4,
                homeGoals = 16,
                awayGoals = 13,
                shots = 196,
                shotsOnTarget = 88,
                corners = 84,
                chancesCreated = 36
            ),
            TeamStatsRecord(
                team = "U18",
                matches = 16,
                wins = 7,
                draws = 3,
                losses = 6,
                goalsFor = 24,
                goalsAgainst = 22,
                goalDifference = "+2",
                cleanSheets = 4,
                points = 24,
                winPercent = "43.8%",
                avgGoals = "1.50",
                avgConceded = "1.38",
                possessionPercent = "50.8%",
                passPercent = "75.4%",
                homeMatches = 8,
                homeWins = 4,
                awayMatches = 8,
                awayWins = 3,
                homeGoals = 13,
                awayGoals = 11,
                shots = 162,
                shotsOnTarget = 68,
                corners = 66,
                chancesCreated = 28
            )
        )
    }

    // Goal Events (Exact user specifications)
    val goalEvents = remember {
        listOf(
            GoalEventRecord("12 Mar", "First Team vs ABC", "Harun", "23'", "Nzai", "Open Play", "1–0"),
            GoalEventRecord("12 Mar", "First Team vs ABC", "Harun", "71'", "—", "Penalty", "2–1"),
            GoalEventRecord("19 Mar", "First Team vs XYZ", "Nzai", "44'", "Harun", "Open Play", "1–0"),
            GoalEventRecord("26 Mar", "First Team vs KCB", "Harun", "14'", "John Kamau", "Counter Attack", "1–0"),
            GoalEventRecord("02 Apr", "First Team vs DEF", "Brian Ochieng", "82'", "Nzai", "Header", "2–0")
        )
    }

    // Discipline Events (Exact user specifications)
    val disciplineEvents = remember {
        listOf(
            DisciplineEventRecord("12 Mar", "vs ABC", "Harun", "Yellow Card", "54'", "Foul", "🟨"),
            DisciplineEventRecord("19 Mar", "vs XYZ", "Nzai", "Yellow Card", "31'", "Foul", "🟨"),
            DisciplineEventRecord("02 Apr", "vs DEF", "Nzai", "Red Card", "78'", "Serious foul", "🟥"),
            DisciplineEventRecord("09 Apr", "vs Bandari", "Kevin Otieno", "Yellow Card", "67'", "Persistent infringement", "🟨"),
            DisciplineEventRecord("16 Apr", "vs Gor Mahia", "Harun", "Yellow Card", "89'", "Dissent", "🟨")
        )
    }

    // Function to handle CSV export
    fun exportCurrentTabCsv() {
        val (fileName, csvContent) = when (selectedTab) {
            0 -> {
                val header = "Player,Position,Apps,Starts,Minutes,Goals,Assists,Shots,Shots on Target,Key Passes,Passes,Pass %,Dribbles,Successful Dribbles,Tackles,Interceptions,Clearances,Fouls,YC,RC\n"
                val rows = filteredPlayers.joinToString("\n") { p ->
                    "${p.name},${p.position},${p.apps},${p.starts},${p.minutes},${p.goals},${p.assists},${p.shots},${p.shotsOnTarget},${p.keyPasses},${p.passes},${p.passAccuracy},${p.dribbles},${p.successfulDribbles},${p.tackles},${p.interceptions},${p.clearances},${p.fouls},${p.yellowCards},${p.redCards}"
                }
                "Player_Stats_$selectedSeason.csv" to (header + rows)
            }
            1 -> {
                val header = "Team,Matches,Wins,Draws,Losses,Goals For,Goals Against,Goal Difference,Clean Sheets,Points,Win %,Avg Goals,Avg Conceded,Possession %,Pass %\n"
                val rows = teamRecords.joinToString("\n") { t ->
                    "${t.team},${t.matches},${t.wins},${t.draws},${t.losses},${t.goalsFor},${t.goalsAgainst},${t.goalDifference},${t.cleanSheets},${t.points},${t.winPercent},${t.avgGoals},${t.avgConceded},${t.possessionPercent},${t.passPercent}"
                }
                "Team_Stats_$selectedSeason.csv" to (header + rows)
            }
            2 -> {
                val header = "Player,Team,Goals,Assists,Penalty Goals,Non-Penalty Goals,Own Goals,Minutes,Goals/90,Shots,Shot Conversion,Matches Scored In,First Goal,Last Goal\n"
                val rows = filteredPlayers.joinToString("\n") { p ->
                    val g90 = if (p.minutes > 0) String.format("%.2f", p.goals.toDouble() / (p.minutes / 90.0)) else "0.00"
                    val conv = if (p.shots > 0) String.format("%.1f%%", p.goals.toDouble() * 100 / p.shots) else "0.0%"
                    "${p.name},${p.team},${p.goals},${p.assists},${p.penaltyGoals},${p.nonPenaltyGoals},${p.ownGoals},${p.minutes},$g90,${p.shots},$conv,${p.matchesScoredIn},${p.firstGoalMinute},${p.lastGoalMinute}"
                }
                "Goal_Stats_$selectedSeason.csv" to (header + rows)
            }
            else -> {
                val header = "Player,Team,Matches,Fouls Committed,Fouls Suffered,Yellow Cards,2nd Yellow,Red Cards,Suspensions,Matches Missed,Offsides,Penalties Conceded\n"
                val rows = filteredPlayers.joinToString("\n") { p ->
                    "${p.name},${p.team},${p.apps},${p.fouls},19,${p.yellowCards},${p.secondYellows},${p.redCards},${p.suspensions},${p.matchesMissed},${p.offsides},${p.penaltiesConceded}"
                }
                "Discipline_Stats_$selectedSeason.csv" to (header + rows)
            }
        }

        // Copy to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(fileName, csvContent))

        // Open share chooser
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csvContent)
            type = "text/csv"
        }
        try {
            context.startActivity(Intent.createChooser(sendIntent, "Export $fileName"))
        } catch (e: Exception) {
            Toast.makeText(context, "$fileName copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner & CSV Export Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CLUB STATS",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = txt,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "$activeClubName • Read-Only Matchday & Performance Analytics",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = mut
                    )
                }

                Button(
                    onClick = { exportCurrentTabCsv() },
                    colors = ButtonDefaults.buttonColors(containerColor = acc),
                    shape = RoundedCornerShape(9.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("CSV EXPORT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                }
            }
        }

        // 2. 4 Primary Read-Only Tabs Switcher
        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabs.forEachIndexed { index, tabName ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) panel2 else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSelected) acc else Color.Transparent),
                            modifier = Modifier.weight(1f).clickable {
                                selectedTab = index
                                onTabSelected(index)
                            }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = tabName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) acc else mut
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Dropdown Filters Bar (Season, Competition, Team, Position, Venue, Search)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterDropdownPill("Season", selectedSeason) {
                        val next = seasons[(seasons.indexOf(selectedSeason) + 1) % seasons.size]
                        selectedSeason = next
                    }
                    FilterDropdownPill("Competition", selectedCompetition) {
                        val next = competitions[(competitions.indexOf(selectedCompetition) + 1) % competitions.size]
                        selectedCompetition = next
                    }
                    if (selectedTab != 1) { // Teams tab doesn't filter by team
                        FilterDropdownPill("Team", selectedTeam) {
                            val next = teamOptions[(teamOptions.indexOf(selectedTeam) + 1) % teamOptions.size]
                            selectedTeam = next
                        }
                    }
                    if (selectedTab == 0) { // Position filter for Players tab
                        FilterDropdownPill("Position", selectedPosition) {
                            val next = positions[(positions.indexOf(selectedPosition) + 1) % positions.size]
                            selectedPosition = next
                        }
                    }
                    FilterDropdownPill("Venue", selectedVenue) {
                        val next = venues[(venues.indexOf(selectedVenue) + 1) % venues.size]
                        selectedVenue = next
                    }
                }

                if (selectedTab != 1) {
                    OutlinedTextField(
                        value = searchPlayerQuery,
                        onValueChange = { searchPlayerQuery = it },
                        placeholder = { Text("Search Player (e.g. Harun, Nzai)...", fontSize = 11.5.sp, color = mut) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = mut, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(9.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc, unfocusedBorderColor = line,
                            focusedTextColor = txt, unfocusedTextColor = txt,
                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                        )
                    )
                }
            }
        }

        // ==================== TAB 1: PLAYERS STATS ====================
        if (selectedTab == 0) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("PLAYERS PERFORMANCE TABLE", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                                Text("Answers: “How has each player performed?” • Tap player to view match breakdown", fontSize = 10.5.sp, color = mut)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = panel2) {
                                Text("${filteredPlayers.size} Players", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, color = acc, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Horizontally scrollable 20-column table
                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Player", 120.dp, isHeader = true, isLeft = true)
                                TableCell("Pos", 48.dp, isHeader = true)
                                TableCell("Apps", 50.dp, isHeader = true)
                                TableCell("Starts", 52.dp, isHeader = true)
                                TableCell("Minutes", 68.dp, isHeader = true)
                                TableCell("Goals", 52.dp, isHeader = true, color = acc)
                                TableCell("Assists", 56.dp, isHeader = true, color = acc2)
                                TableCell("Shots", 52.dp, isHeader = true)
                                TableCell("SoT", 48.dp, isHeader = true)
                                TableCell("KP", 46.dp, isHeader = true)
                                TableCell("Passes", 62.dp, isHeader = true)
                                TableCell("Pass %", 62.dp, isHeader = true)
                                TableCell("Drb", 48.dp, isHeader = true)
                                TableCell("Succ Drb", 66.dp, isHeader = true)
                                TableCell("Tackles", 60.dp, isHeader = true)
                                TableCell("Int", 46.dp, isHeader = true)
                                TableCell("Clear", 52.dp, isHeader = true)
                                TableCell("Fouls", 50.dp, isHeader = true)
                                TableCell("YC", 42.dp, isHeader = true, color = warn)
                                TableCell("RC", 42.dp, isHeader = true, color = bad)
                            }

                            HorizontalDivider(color = line)

                            // Table Rows
                            filteredPlayers.forEachIndexed { idx, p ->
                                Row(
                                    modifier = Modifier
                                        .clickable { selectedPlayerForDrillDown = p }
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(modifier = Modifier.width(120.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = acc, maxLines = 1)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = mut, modifier = Modifier.size(13.dp))
                                    }
                                    TableCell(p.position, 48.dp)
                                    TableCell("${p.apps}", 50.dp)
                                    TableCell("${p.starts}", 52.dp)
                                    TableCell(String.format("%,d", p.minutes), 68.dp)
                                    TableCell("${p.goals}", 52.dp, color = acc, isBold = true)
                                    TableCell("${p.assists}", 56.dp, color = acc2, isBold = true)
                                    TableCell("${p.shots}", 52.dp)
                                    TableCell("${p.shotsOnTarget}", 48.dp)
                                    TableCell("${p.keyPasses}", 46.dp)
                                    TableCell(String.format("%,d", p.passes), 62.dp)
                                    TableCell(p.passAccuracy, 62.dp)
                                    TableCell("${p.dribbles}", 48.dp)
                                    TableCell("${p.successfulDribbles}", 66.dp)
                                    TableCell("${p.tackles}", 60.dp)
                                    TableCell("${p.interceptions}", 46.dp)
                                    TableCell("${p.clearances}", 52.dp)
                                    TableCell("${p.fouls}", 50.dp)
                                    TableCell("${p.yellowCards}", 42.dp, color = warn, isBold = true)
                                    TableCell("${p.redCards}", 42.dp, color = bad, isBold = true)
                                }
                                if (idx < filteredPlayers.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== TAB 2: TEAMS STATS ====================
        if (selectedTab == 1) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("TEAM PERFORMANCE TABLE", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                                Text("Answers: “How has each team performed?”", fontSize = 10.5.sp, color = mut)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Team", 110.dp, isHeader = true, isLeft = true)
                                TableCell("Matches", 62.dp, isHeader = true)
                                TableCell("Wins", 50.dp, isHeader = true, color = acc)
                                TableCell("Draws", 50.dp, isHeader = true)
                                TableCell("Losses", 52.dp, isHeader = true, color = bad)
                                TableCell("Goals For", 70.dp, isHeader = true)
                                TableCell("Goals Ag", 70.dp, isHeader = true)
                                TableCell("GD", 50.dp, isHeader = true, color = acc)
                                TableCell("Clean Sh", 64.dp, isHeader = true)
                                TableCell("Points", 56.dp, isHeader = true, color = acc, isBold = true)
                                TableCell("Win %", 58.dp, isHeader = true)
                                TableCell("Avg Goals", 72.dp, isHeader = true)
                                TableCell("Avg Conc", 72.dp, isHeader = true)
                                TableCell("Poss %", 60.dp, isHeader = true)
                                TableCell("Pass %", 60.dp, isHeader = true)
                            }

                            HorizontalDivider(color = line)

                            teamRecords.forEachIndexed { idx, t ->
                                Row(
                                    modifier = Modifier
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(t.team, 110.dp, isLeft = true, isBold = true, color = txt)
                                    TableCell("${t.matches}", 62.dp)
                                    TableCell("${t.wins}", 50.dp, color = acc, isBold = true)
                                    TableCell("${t.draws}", 50.dp)
                                    TableCell("${t.losses}", 52.dp, color = bad)
                                    TableCell("${t.goalsFor}", 70.dp)
                                    TableCell("${t.goalsAgainst}", 70.dp)
                                    TableCell(t.goalDifference, 50.dp, color = acc, isBold = true)
                                    TableCell("${t.cleanSheets}", 64.dp)
                                    TableCell("${t.points}", 56.dp, color = acc, isBold = true)
                                    TableCell(t.winPercent, 58.dp)
                                    TableCell(t.avgGoals, 72.dp)
                                    TableCell(t.avgConceded, 72.dp)
                                    TableCell(t.possessionPercent, 60.dp)
                                    TableCell(t.passPercent, 60.dp)
                                }
                                if (idx < teamRecords.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }

            // 4 Breakdown Category Cards as requested: Results, Goals, Performance, Home/Away
            item {
                val primaryTeam = teamRecords.firstOrNull() ?: teamRecords[0]
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("TEAM CATEGORY BREAKDOWN (${primaryTeam.team})", fontSize = 12.sp, fontWeight = FontWeight.Black, color = mut)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TeamBreakdownBox(
                            title = "RESULTS",
                            items = listOf(
                                "Matches" to "${primaryTeam.matches}",
                                "Wins" to "${primaryTeam.wins}",
                                "Draws" to "${primaryTeam.draws}",
                                "Losses" to "${primaryTeam.losses}",
                                "Points" to "${primaryTeam.points}",
                                "Win %" to primaryTeam.winPercent,
                                "Draw %" to "${primaryTeam.draws * 100 / primaryTeam.matches}%",
                                "Loss %" to "${primaryTeam.losses * 100 / primaryTeam.matches}%"
                            ),
                            accentColor = acc,
                            modifier = Modifier.weight(1f)
                        )

                        TeamBreakdownBox(
                            title = "GOALS",
                            items = listOf(
                                "Goals Scored" to "${primaryTeam.goalsFor}",
                                "Goals Conceded" to "${primaryTeam.goalsAgainst}",
                                "Goal Diff" to primaryTeam.goalDifference,
                                "Goals / Match" to primaryTeam.avgGoals,
                                "Conceded / Match" to primaryTeam.avgConceded,
                                "Clean Sheets" to "${primaryTeam.cleanSheets}"
                            ),
                            accentColor = acc2,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TeamBreakdownBox(
                            title = "PERFORMANCE",
                            items = listOf(
                                "Possession" to primaryTeam.possessionPercent,
                                "Pass Completion" to primaryTeam.passPercent,
                                "Shots" to "${primaryTeam.shots}",
                                "Shots on Target" to "${primaryTeam.shotsOnTarget}",
                                "Corners" to "${primaryTeam.corners}",
                                "Chances Created" to "${primaryTeam.chancesCreated}"
                            ),
                            accentColor = warn,
                            modifier = Modifier.weight(1f)
                        )

                        TeamBreakdownBox(
                            title = "HOME / AWAY",
                            items = listOf(
                                "Home Matches" to "${primaryTeam.homeMatches}",
                                "Home Wins" to "${primaryTeam.homeWins}",
                                "Away Matches" to "${primaryTeam.awayMatches}",
                                "Away Wins" to "${primaryTeam.awayWins}",
                                "Home Goals" to "${primaryTeam.homeGoals}",
                                "Away Goals" to "${primaryTeam.awayGoals}"
                            ),
                            accentColor = Color(0xFFA78BFA),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ==================== TAB 3: GOALS STATS ====================
        if (selectedTab == 2) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("GOALS BY PLAYER", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                                Text("Answers: “Who scored, how, when and in which matches?”", fontSize = 10.5.sp, color = mut)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Player", 100.dp, isHeader = true, isLeft = true)
                                TableCell("Team", 90.dp, isHeader = true)
                                TableCell("Goals", 52.dp, isHeader = true, color = acc)
                                TableCell("Assists", 56.dp, isHeader = true, color = acc2)
                                TableCell("Pen Goals", 72.dp, isHeader = true)
                                TableCell("Non-Pen", 68.dp, isHeader = true)
                                TableCell("Own G", 56.dp, isHeader = true)
                                TableCell("Minutes", 68.dp, isHeader = true)
                                TableCell("Goals/90", 68.dp, isHeader = true, color = acc)
                                TableCell("Shots", 52.dp, isHeader = true)
                                TableCell("Shot Conv", 74.dp, isHeader = true)
                                TableCell("Scored In", 72.dp, isHeader = true)
                                TableCell("1st Goal", 64.dp, isHeader = true)
                                TableCell("Last Goal", 64.dp, isHeader = true)
                            }

                            HorizontalDivider(color = line)

                            filteredPlayers.forEachIndexed { idx, p ->
                                val g90 = if (p.minutes > 0) String.format("%.2f", p.goals.toDouble() / (p.minutes / 90.0)) else "0.00"
                                val conv = if (p.shots > 0) String.format("%.1f%%", p.goals.toDouble() * 100 / p.shots) else "0.0%"

                                Row(
                                    modifier = Modifier
                                        .clickable { selectedPlayerForDrillDown = p }
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(p.name, 100.dp, isLeft = true, isBold = true, color = acc)
                                    TableCell(p.team, 90.dp)
                                    TableCell("${p.goals}", 52.dp, color = acc, isBold = true)
                                    TableCell("${p.assists}", 56.dp, color = acc2)
                                    TableCell("${p.penaltyGoals}", 72.dp)
                                    TableCell("${p.nonPenaltyGoals}", 68.dp)
                                    TableCell("${p.ownGoals}", 56.dp)
                                    TableCell(String.format("%,d", p.minutes), 68.dp)
                                    TableCell(g90, 68.dp, color = acc, isBold = true)
                                    TableCell("${p.shots}", 52.dp)
                                    TableCell(conv, 74.dp)
                                    TableCell("${p.matchesScoredIn}", 72.dp)
                                    TableCell(p.firstGoalMinute, 64.dp)
                                    TableCell(p.lastGoalMinute, 64.dp)
                                }
                                if (idx < filteredPlayers.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }

            // Goal Event History Table
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("GOAL EVENT HISTORY LOG", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                                Text("Propagated directly from official live match records", fontSize = 10.5.sp, color = mut)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = panel2) {
                                Text("${goalEvents.size} Logged Goals", modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.sp, color = acc, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Date", 68.dp, isHeader = true, isLeft = true)
                                TableCell("Match", 140.dp, isHeader = true, isLeft = true)
                                TableCell("Player", 90.dp, isHeader = true, isLeft = true)
                                TableCell("Time", 60.dp, isHeader = true, color = acc)
                                TableCell("Assist", 90.dp, isHeader = true)
                                TableCell("Goal Type", 100.dp, isHeader = true)
                                TableCell("Score After", 84.dp, isHeader = true, isBold = true)
                            }

                            HorizontalDivider(color = line)

                            goalEvents.forEachIndexed { idx, ev ->
                                Row(
                                    modifier = Modifier
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 9.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(ev.date, 68.dp, isLeft = true, color = mut)
                                    TableCell(ev.match, 140.dp, isLeft = true, isBold = true, color = txt)
                                    TableCell(ev.player, 90.dp, isLeft = true, color = acc, isBold = true)
                                    TableCell(ev.goalTime, 60.dp, color = acc, isBold = true)
                                    TableCell(ev.assist, 90.dp, color = if (ev.assist == "—") mut else acc2)
                                    TableCell(ev.goalType, 100.dp, color = txt)
                                    TableCell(ev.scoreAfterGoal, 84.dp, isBold = true, color = txt)
                                }
                                if (idx < goalEvents.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== TAB 4: DISCIPLINE STATS ====================
        if (selectedTab == 3) {
            // Discipline summary strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DISCIPLINE SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Black, color = mut)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryPill("Total Yellow Cards", "34", warn)
                        SummaryPill("Total Red Cards", "2", bad)
                        SummaryPill("Total Fouls", "112", txt)
                        SummaryPill("Total Offsides", "28", acc2)
                        SummaryPill("Total Suspensions", "2", bad)
                        SummaryPill("Matches Missed", "3", warn)
                        SummaryPill("Pens Conceded", "1", mut)
                    }
                }
            }

            // Discipline Table by Player
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("PLAYER DISCIPLINARY TABLE", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                                Text("Answers: “What disciplinary events have occurred?”", fontSize = 10.5.sp, color = mut)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Player", 100.dp, isHeader = true, isLeft = true)
                                TableCell("Team", 90.dp, isHeader = true)
                                TableCell("Matches", 62.dp, isHeader = true)
                                TableCell("Fouls Comm", 84.dp, isHeader = true)
                                TableCell("Fouls Suff", 80.dp, isHeader = true)
                                TableCell("Yellow Cards", 90.dp, isHeader = true, color = warn)
                                TableCell("2nd Yellow", 76.dp, isHeader = true)
                                TableCell("Red Cards", 74.dp, isHeader = true, color = bad)
                                TableCell("Suspensions", 86.dp, isHeader = true)
                                TableCell("Missed", 62.dp, isHeader = true)
                                TableCell("Offsides", 64.dp, isHeader = true)
                                TableCell("Pens Conc", 78.dp, isHeader = true)
                            }

                            HorizontalDivider(color = line)

                            filteredPlayers.forEachIndexed { idx, p ->
                                Row(
                                    modifier = Modifier
                                        .clickable { selectedPlayerForDrillDown = p }
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 10.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(p.name, 100.dp, isLeft = true, isBold = true, color = acc)
                                    TableCell(p.team, 90.dp)
                                    TableCell("${p.apps}", 62.dp)
                                    TableCell("${p.fouls}", 84.dp)
                                    TableCell("19", 80.dp)
                                    TableCell("${p.yellowCards}", 90.dp, color = warn, isBold = true)
                                    TableCell("${p.secondYellows}", 76.dp)
                                    TableCell("${p.redCards}", 74.dp, color = bad, isBold = true)
                                    TableCell("${p.suspensions}", 86.dp)
                                    TableCell("${p.matchesMissed}", 62.dp)
                                    TableCell("${p.offsides}", 64.dp)
                                    TableCell("${p.penaltiesConceded}", 78.dp)
                                }
                                if (idx < filteredPlayers.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }

            // Discipline Event History Table
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = panel,
                    border = BorderStroke(1.dp, line),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("DISCIPLINE EVENT HISTORY", fontSize = 13.sp, fontWeight = FontWeight.Black, color = txt)
                        Text("Real-time bookings recorded by official referees", fontSize = 10.5.sp, color = mut)

                        Spacer(modifier = Modifier.height(10.dp))

                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(scrollState)) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(panel2)
                                    .padding(vertical = 9.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell("Date", 70.dp, isHeader = true, isLeft = true)
                                TableCell("Match", 110.dp, isHeader = true, isLeft = true)
                                TableCell("Player", 90.dp, isHeader = true, isLeft = true)
                                TableCell("Event", 100.dp, isHeader = true)
                                TableCell("Minute", 58.dp, isHeader = true)
                                TableCell("Reason", 140.dp, isHeader = true, isLeft = true)
                                TableCell("Card", 48.dp, isHeader = true)
                            }

                            HorizontalDivider(color = line)

                            disciplineEvents.forEachIndexed { idx, ev ->
                                Row(
                                    modifier = Modifier
                                        .background(if (idx % 2 == 1) panel2.copy(alpha = 0.4f) else Color.Transparent)
                                        .padding(vertical = 9.dp, horizontal = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(ev.date, 70.dp, isLeft = true, color = mut)
                                    TableCell(ev.match, 110.dp, isLeft = true, isBold = true, color = txt)
                                    TableCell(ev.player, 90.dp, isLeft = true, color = acc, isBold = true)
                                    TableCell(ev.event, 100.dp, isBold = true, color = if (ev.cardSymbol == "🟥") bad else warn)
                                    TableCell(ev.minute, 58.dp, color = txt)
                                    TableCell(ev.reason, 140.dp, isLeft = true, color = mut)
                                    TableCell(ev.cardSymbol, 48.dp, fontSize = 14.sp)
                                }
                                if (idx < disciplineEvents.lastIndex) {
                                    HorizontalDivider(color = line.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Drill down modal on player row click
    selectedPlayerForDrillDown?.let { player ->
        PlayerStatsDrillDownModal(
            player = player,
            onDismiss = { selectedPlayerForDrillDown = null }
        )
    }
}

// --- SUBVIEWS & HELPERS ---

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isLeft: Boolean = false,
    isBold: Boolean = false,
    color: Color = Color.Unspecified,
    fontSize: androidx.compose.ui.unit.TextUnit = 11.sp
) {
    val finalColor = if (color != Color.Unspecified) color else if (isHeader) TGColors.Mut else TGColors.Txt
    val finalWeight = if (isHeader) FontWeight.Black else if (isBold) FontWeight.Bold else FontWeight.Medium

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = finalWeight,
        color = finalColor,
        textAlign = if (isLeft) TextAlign.Start else TextAlign.Center,
        modifier = Modifier.width(width).padding(horizontal = 4.dp),
        maxLines = 1
    )
}

@Composable
private fun FilterDropdownPill(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = TGColors.Panel2,
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$label: ", fontSize = 10.5.sp, color = TGColors.Mut)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TGColors.Acc)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TGColors.Mut, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun TeamBreakdownBox(
    title: String,
    items: List<Pair<String, String>>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TGColors.Panel,
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Black, color = accentColor)
            HorizontalDivider(color = TGColors.Line)
            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, fontSize = 10.5.sp, color = TGColors.Mut)
                    Text(value, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
                }
            }
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = TGColors.Panel,
        border = BorderStroke(1.dp, TGColors.Line)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TGColors.Mut)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}
