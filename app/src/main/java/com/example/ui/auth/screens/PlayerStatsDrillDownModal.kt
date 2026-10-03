package com.example.ui.auth.screens

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class PlayerStatsRecord(
    val name: String,
    val position: String,
    val team: String = "First Team",
    val apps: Int,
    val starts: Int,
    val minutes: Int,
    val goals: Int,
    val assists: Int,
    val shots: Int,
    val shotsOnTarget: Int,
    val keyPasses: Int,
    val passes: Int,
    val passAccuracy: String,
    val dribbles: Int,
    val successfulDribbles: Int,
    val tackles: Int,
    val interceptions: Int,
    val clearances: Int,
    val fouls: Int,
    val yellowCards: Int,
    val redCards: Int,
    val penaltyGoals: Int = 2,
    val nonPenaltyGoals: Int = 9,
    val ownGoals: Int = 0,
    val matchesScoredIn: Int = 9,
    val firstGoalMinute: String = "12'",
    val lastGoalMinute: String = "87'",
    val secondYellows: Int = 0,
    val suspensions: Int = 0,
    val matchesMissed: Int = 0,
    val offsides: Int = 3,
    val penaltiesConceded: Int = 0
)

data class PlayerMatchLog(
    val date: String,
    val match: String,
    val minutes: Int,
    val goals: Int,
    val assists: Int,
    val rating: String,
    val result: String
)

@Composable
fun PlayerStatsDrillDownModal(
    player: PlayerStatsRecord,
    onDismiss: () -> Unit
) {
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

    var selectedTab by rememberSaveable { mutableStateOf("Overview") }
    val drillTabs = listOf(
        "Overview", "Match-by-Match", "Attacking",
        "Passing", "Defensive", "Possession",
        "Discipline", "Trends"
    )

    val matchLogs = remember(player.name) {
        listOf(
            PlayerMatchLog("02 Oct", "vs Tusker FC", 90, 1, 1, "8.7", "W 2-1"),
            PlayerMatchLog("25 Sep", "vs Bandari FC", 85, 2, 0, "9.1", "W 3-0"),
            PlayerMatchLog("18 Sep", "vs Gor Mahia", 90, 0, 1, "7.8", "D 1-1"),
            PlayerMatchLog("11 Sep", "vs AFC Leopards", 78, 1, 0, "8.2", "W 1-0"),
            PlayerMatchLog("04 Sep", "vs Sofapaka", 90, 2, 1, "9.4", "W 4-1"),
            PlayerMatchLog("28 Aug", "vs Kariobangi Sharks", 65, 0, 0, "7.1", "L 0-1")
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = panel,
            border = BorderStroke(1.dp, line),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                // Header: Player Name, Position, Verification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = panel2,
                            border = BorderStroke(1.dp, acc),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    player.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = acc
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    player.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = txt
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = panel3) {
                                    Text(
                                        player.position,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = acc2
                                    )
                                }
                            }
                            Text(
                                "${player.team} • ${player.minutes} Mins • ${player.apps} Apps (${player.starts} Starts)",
                                fontSize = 11.5.sp,
                                color = mut
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = mut)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Drill-down 8-Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(panel2)
                        .horizontalScroll(rememberScrollState())
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    drillTabs.forEach { tabTitle ->
                        val isSel = selectedTab == tabTitle
                        Surface(
                            shape = RoundedCornerShape(7.dp),
                            color = if (isSel) panel3 else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSel) acc else Color.Transparent),
                            modifier = Modifier.clickable { selectedTab = tabTitle }
                        ) {
                            Text(
                                text = tabTitle,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) acc else mut,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content for selected drill-down subtab
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        "Overview" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("GOALS", "${player.goals}", acc, Modifier.weight(1f))
                                        DrillKPI("ASSISTS", "${player.assists}", acc2, Modifier.weight(1f))
                                        DrillKPI("MIN / GOAL", if (player.goals > 0) "${player.minutes / player.goals}'" else "—", warn, Modifier.weight(1f))
                                        DrillKPI("PASS %", player.passAccuracy, txt, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("SEASON PERFORMANCE SUMMARY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = acc)
                                            Text("• Output Rate: ${(player.goals.toDouble() / (player.minutes / 90.0)).coerceAtLeast(0.0).let { String.format("%.2f", it) }} Goals per 90 mins.", fontSize = 12.sp, color = txt)
                                            Text("• Chance Creation: ${player.keyPasses} key passes with ${player.assists} official assists.", fontSize = 12.sp, color = txt)
                                            Text("• Physical Duels: ${player.tackles} tackles, ${player.successfulDribbles} successful dribbles completed.", fontSize = 12.sp, color = txt)
                                            Text("• Fair Play: ${player.yellowCards} yellow cards, ${player.redCards} red cards recorded.", fontSize = 12.sp, color = txt)
                                        }
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text("LATEST MATCH LOG", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = mut)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            matchLogs.take(3).forEach { log ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("${log.date} • ${log.match}", fontSize = 11.5.sp, color = txt)
                                                    Text("${log.goals}G, ${log.assists}A • Rating ${log.rating}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = acc)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "Match-by-Match" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(matchLogs) { log ->
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(log.match, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = txt)
                                                Text("${log.date} • ${log.minutes} mins • Result ${log.result}", fontSize = 11.sp, color = mut)
                                            }
                                            Surface(shape = RoundedCornerShape(6.dp), color = panel3) {
                                                Text(
                                                    "Rating: ${log.rating}",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = acc
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "Attacking" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("TOTAL GOALS", "${player.goals}", acc, Modifier.weight(1f))
                                        DrillKPI("SHOTS", "${player.shots}", txt, Modifier.weight(1f))
                                        DrillKPI("ON TARGET", "${player.shotsOnTarget}", acc2, Modifier.weight(1f))
                                        DrillKPI("SHOT ACC %", if (player.shots > 0) "${(player.shotsOnTarget * 100 / player.shots)}%" else "—", warn, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            DrillStatRow("Penalty Goals", "${player.penaltyGoals}")
                                            DrillStatRow("Non-Penalty Goals", "${player.nonPenaltyGoals}")
                                            DrillStatRow("Primary Assists", "${player.assists}")
                                            DrillStatRow("Key Passes", "${player.keyPasses}")
                                            DrillStatRow("Earliest Goal Time", player.firstGoalMinute)
                                            DrillStatRow("Latest Goal Time", player.lastGoalMinute)
                                        }
                                    }
                                }
                            }
                        }

                        "Passing" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("PASSES", "${player.passes}", acc2, Modifier.weight(1f))
                                        DrillKPI("ACCURACY", player.passAccuracy, acc, Modifier.weight(1f))
                                        DrillKPI("KEY PASSES", "${player.keyPasses}", warn, Modifier.weight(1f))
                                        DrillKPI("ASSISTS", "${player.assists}", txt, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            DrillStatRow("Completed Passes", "${(player.passes * 0.83).toInt()}")
                                            DrillStatRow("Forward Passes", "${(player.passes * 0.44).toInt()}")
                                            DrillStatRow("Pass Completion in Opponent Half", "${player.passAccuracy}")
                                            DrillStatRow("Crosses Delivered", "34")
                                            DrillStatRow("Through Balls", "18")
                                        }
                                    }
                                }
                            }
                        }

                        "Defensive" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("TACKLES", "${player.tackles}", acc, Modifier.weight(1f))
                                        DrillKPI("INTERCEPTIONS", "${player.interceptions}", acc2, Modifier.weight(1f))
                                        DrillKPI("CLEARANCES", "${player.clearances}", warn, Modifier.weight(1f))
                                        DrillKPI("FOULS", "${player.fouls}", bad, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            DrillStatRow("Tackle Success Rate", "78.4%")
                                            DrillStatRow("Ground Duels Won", "46")
                                            DrillStatRow("Aerial Duels Won", "19")
                                            DrillStatRow("Recoveries in Defensive Third", "28")
                                            DrillStatRow("Blocks Recorded", "7")
                                        }
                                    }
                                }
                            }
                        }

                        "Possession" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("DRIBBLES", "${player.dribbles}", acc, Modifier.weight(1f))
                                        DrillKPI("SUCCESSFUL", "${player.successfulDribbles}", acc2, Modifier.weight(1f))
                                        DrillKPI("SUCCESS %", if (player.dribbles > 0) "${player.successfulDribbles * 100 / player.dribbles}%" else "—", warn, Modifier.weight(1f))
                                        DrillKPI("OFFSIDES", "${player.offsides}", bad, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            DrillStatRow("Total Touches", "${player.passes + player.dribbles + 400}")
                                            DrillStatRow("Touches in Opponent Box", "68")
                                            DrillStatRow("Dispossessed", "18")
                                            DrillStatRow("Turnovers Forced", "24")
                                            DrillStatRow("Fouls Drawn", "19")
                                        }
                                    }
                                }
                            }
                        }

                        "Discipline" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        DrillKPI("FOULS COMMITTED", "${player.fouls}", txt, Modifier.weight(1f))
                                        DrillKPI("YELLOW CARDS", "${player.yellowCards}", warn, Modifier.weight(1f))
                                        DrillKPI("RED CARDS", "${player.redCards}", bad, Modifier.weight(1f))
                                        DrillKPI("SUSPENSIONS", "${player.suspensions}", bad, Modifier.weight(1f))
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            DrillStatRow("Fouls Suffered", "19")
                                            DrillStatRow("Second Yellow Cards", "${player.secondYellows}")
                                            DrillStatRow("Matches Missed via Suspension", "${player.matchesMissed}")
                                            DrillStatRow("Penalties Conceded", "${player.penaltiesConceded}")
                                            DrillStatRow("Minutes per Booking", if (player.yellowCards > 0) "${player.minutes / player.yellowCards}'" else "No Cards")
                                        }
                                    }
                                }
                            }
                        }

                        else -> { // "Trends"
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("FORM TRAJECTORY & HEALTH INDEX", fontSize = 11.sp, fontWeight = FontWeight.Black, color = acc)
                                            Text("• Last 5 Match Ratings: 8.7 → 9.1 → 7.8 → 8.2 → 9.4", fontSize = 12.sp, color = txt)
                                            Text("• Scoring Streak: Goals scored in 4 of the last 5 competitive fixtures.", fontSize = 12.sp, color = txt)
                                            Text("• Physical Workload: 84.6% optimal minutes; 0 acute fatigue flags.", fontSize = 12.sp, color = txt)
                                            Text("• Tactical Role: Primary central forward with rotational pressing responsibilities.", fontSize = 12.sp, color = txt)
                                        }
                                    }
                                }
                                item {
                                    Surface(shape = RoundedCornerShape(10.dp), color = panel3, modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = acc, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Verified Club Admin match telemetry synchronized to player passport.", fontSize = 11.sp, color = mut)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = panel3),
                    shape = RoundedCornerShape(9.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Player Profile", color = txt, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DrillKPI(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = TGColors.Panel2,
        border = BorderStroke(1.dp, TGColors.Line),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TGColors.Mut, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1)
        }
    }
}

@Composable
private fun DrillStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TGColors.Mut)
        Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TGColors.Txt)
    }
}
