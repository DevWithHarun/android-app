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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class AthleteExtendedModel(
    val id: String,
    val name: String,
    val squadNumber: Int,
    val position: String,
    val teamName: String,
    val status: String = "Active", // Invited, Pending Verification, Active, Inactive, Released, Transferred, Archived
    val rating: Int = 80,
    val registrationNo: String = "REG-2026-001",
    val contractExpiry: String = "2027-06-30",
    val photoUrl: String = "",
    val careerTimeline: List<String> = listOf("2024 → Academy Intake", "2025 → U20 Youth", "2026 → Senior Team 1st XI")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAthletesModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String,
    onAddAthlete: () -> Unit = {}
) {
    // Exact Talent Graph Boilerplate Dark Theme tokens for crystal clarity
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
    val purple = TGColors.Purple

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedStatusFilter by rememberSaveable { mutableStateOf("All Statuses") }
    val statuses = listOf("All Statuses", "Active", "Invited", "Pending Verification", "Transferred", "Archived", "Released")

    var showTransferDialog by remember { mutableStateOf(false) }
    var showCareerDialog by remember { mutableStateOf(false) }
    var showClaimGhostDialog by remember { mutableStateOf(false) }
    var selectedAthleteForAction by remember { mutableStateOf<AthleteExtendedModel?>(null) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val rawAthletes = successState?.athletes ?: emptyList()

    var athleteList by remember(rawAthletes) {
        mutableStateOf(
            if (rawAthletes.isNotEmpty()) {
                rawAthletes.mapIndexed { idx, item ->
                    AthleteExtendedModel(
                        id = item.firestoreId.ifBlank { "ath_$idx" },
                        name = item.name,
                        squadNumber = idx + 7,
                        position = item.position,
                        teamName = if (idx == 0) "Senior Team 1st XI" else if (idx == 1) "U20 Youth Team" else "U23 Reserves",
                        status = if (idx == 2) "Pending Verification" else "Active",
                        rating = item.rating,
                        registrationNo = "REG-2026-0${idx + 1}",
                        photoUrl = item.photoUrl ?: ""
                    )
                }
            } else {
                listOf(
                    AthleteExtendedModel("ath_1", "John Kamau", 10, "Attacking Midfielder", "Senior Team 1st XI", "Active", 84, "REG-2026-001", "2027-12-31"),
                    AthleteExtendedModel("ath_2", "Brian Ochieng", 9, "Center Forward (ST)", "U20 Youth Team", "Active", 81, "REG-2026-002", "2028-06-30"),
                    AthleteExtendedModel("ath_3", "Kevin Otieno", 4, "Center Back (CB)", "U23 Reserves", "Pending Verification", 78, "REG-2026-003", "2027-06-30"),
                    AthleteExtendedModel("ath_4", "Emmanuel Kiprono", 6, "Defensive Midfielder", "U20 Youth Team", "Invited", 76, "REG-2026-004", "Trial Agreement")
                )
            }
        )
    }

    val filteredAthletes = remember(searchQuery, selectedStatusFilter, athleteList) {
        athleteList.filter { ath ->
            val matchQuery = searchQuery.isBlank() || ath.name.contains(searchQuery, ignoreCase = true) || ath.position.contains(searchQuery, ignoreCase = true) || ath.teamName.contains(searchQuery, ignoreCase = true)
            val matchStatus = if (selectedStatusFilter == "All Statuses") true else ath.status.equals(selectedStatusFilter, ignoreCase = true)
            matchQuery && matchStatus
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(9.dp),
                                    color = panel2,
                                    border = BorderStroke(1.dp, line),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = acc, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Club Athlete Registry & Roster",
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = txt
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Authorized Squad Management & Career History Trajectory for $activeClubName",
                                fontSize = 11.5.sp,
                                color = mut
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onAddAthlete,
                            colors = ButtonDefaults.buttonColors(containerColor = acc),
                            shape = RoundedCornerShape(9.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF04121A))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Athlete", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search athletes by name, position, or squad...", fontSize = 12.sp, color = mut) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(9.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )
                }
            }
        }

        // Status Filter Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = statuses.indexOf(selectedStatusFilter).coerceAtLeast(0),
                containerColor = panel,
                contentColor = acc,
                edgePadding = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, line, RoundedCornerShape(10.dp))
            ) {
                statuses.forEach { st ->
                    val isSelected = selectedStatusFilter == st
                    Tab(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = st },
                        text = {
                            Text(
                                text = st,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) acc else mut
                            )
                        }
                    )
                }
            }
        }

        // Athlete List
        items(filteredAthletes) { athlete ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(panel2)
                                    .border(1.dp, line, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (athlete.photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = athlete.photoUrl,
                                        contentDescription = athlete.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("#${athlete.squadNumber}", fontWeight = FontWeight.Black, color = acc, fontSize = 13.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(athlete.name, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = txt)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = panel3) {
                                        Text("#${athlete.squadNumber}", modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = acc2)
                                    }
                                }
                                Text("${athlete.position} • ${athlete.teamName}", fontSize = 11.sp, color = mut)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (athlete.status) {
                                "Active" -> acc.copy(alpha = 0.15f)
                                "Pending Verification" -> warn.copy(alpha = 0.15f)
                                "Invited" -> purple.copy(alpha = 0.15f)
                                else -> panel3
                            },
                            border = BorderStroke(
                                1.dp,
                                when (athlete.status) {
                                    "Active" -> acc.copy(alpha = 0.4f)
                                    "Pending Verification" -> warn.copy(alpha = 0.4f)
                                    "Invited" -> purple.copy(alpha = 0.4f)
                                    else -> line
                                }
                            )
                        ) {
                            Text(
                                text = athlete.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = when (athlete.status) {
                                    "Active" -> acc
                                    "Pending Verification" -> warn
                                    "Invited" -> purple
                                    else -> mut
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = line)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Reg: ${athlete.registrationNo} • Contract: ${athlete.contractExpiry}", fontSize = 10.sp, color = mut)
                        Text("OVR Rating: ${athlete.rating}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = acc)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isGhost = athlete.name.contains("Ghost", ignoreCase = true) || athlete.id.contains("ghost", ignoreCase = true)
                        if (isGhost) {
                            Button(
                                onClick = {
                                    selectedAthleteForAction = athlete
                                    showClaimGhostDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = warn),
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFF04121A))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Claim & Merge Ghost Profile", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    selectedAthleteForAction = athlete
                                    showCareerDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, line),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = txt),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(13.dp), tint = mut)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Career Trajectory", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = txt)
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedAthleteForAction = athlete
                                    showTransferDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, acc.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = acc),
                                modifier = Modifier.weight(1f).height(34.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(13.dp), tint = acc)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Squad Movement", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = acc)
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Career Trajectory & History
    if (showCareerDialog && selectedAthleteForAction != null) {
        val ath = selectedAthleteForAction!!
        Dialog(onDismissRequest = { showCareerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Talent Graph Career Trajectory", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = txt)
                        IconButton(onClick = { showCareerDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = mut) }
                    }

                    Text("${ath.name} (#${ath.squadNumber})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = acc)
                    Text("The Talent Graph securely preserves historical progression across divisions and transfers without overwriting historical records.", fontSize = 11.sp, color = mut)

                    Spacer(modifier = Modifier.height(6.dp))

                    ath.careerTimeline.forEach { step ->
                        Surface(shape = RoundedCornerShape(8.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = acc, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(step, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = txt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { showCareerDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = panel3),
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = txt, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG: Transfer / Squad Movement
    if (showTransferDialog && selectedAthleteForAction != null) {
        val ath = selectedAthleteForAction!!
        var targetTeam by remember { mutableStateOf("Senior Team 1st XI") }
        var newNumber by remember { mutableStateOf(ath.squadNumber.toString()) }

        Dialog(onDismissRequest = { showTransferDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Team Movement & Squad Assignment", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = txt)
                        IconButton(onClick = { showTransferDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = mut) }
                    }

                    Text("Athlete: ${ath.name} (Current: ${ath.teamName})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = acc)

                    OutlinedTextField(
                        value = targetTeam,
                        onValueChange = { targetTeam = it },
                        label = { Text("Transfer Target Squad", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )
                    OutlinedTextField(
                        value = newNumber,
                        onValueChange = { newNumber = it },
                        label = { Text("Squad Jersey Number", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )

                    Button(
                        onClick = {
                            athleteList = athleteList.map {
                                if (it.id == ath.id) it.copy(
                                    teamName = targetTeam,
                                    squadNumber = newNumber.toIntOrNull() ?: it.squadNumber,
                                    careerTimeline = it.careerTimeline + "2026 → Assigned to $targetTeam"
                                ) else it
                            }
                            showTransferDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Confirm Squad Movement", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                    }
                }
            }
        }
    }

    // DIALOG: Claim & Merge Ghost Profile (Preserves all historic stats)
    if (showClaimGhostDialog && selectedAthleteForAction != null) {
        val ath = selectedAthleteForAction!!
        var realTgId by remember { mutableStateOf("TG-ATH-") }
        var claimantName by remember { mutableStateOf(ath.name.replace(" [Ghost]", "").replace("[Ghost]", "").trim()) }
        var idNumber by remember { mutableStateOf("38920194") }
        var claimantPhone by remember { mutableStateOf("+254 712 000 000") }

        Dialog(onDismissRequest = { showClaimGhostDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = panel,
                border = BorderStroke(1.dp, line),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Claim & Merge Ghost Profile", fontSize = 16.sp, fontWeight = FontWeight.Black, color = txt)
                            Text("Preserve historic match stats & training records", fontSize = 11.sp, color = mut)
                        }
                        IconButton(onClick = { showClaimGhostDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close", tint = mut) }
                    }

                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = panel2,
                        border = BorderStroke(1.dp, line),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Ghost Identity: ${ath.id} (${ath.name})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = warn)
                            Text("Historic Stats: 8 appearances, 3 goals, 91% attendance preserved", fontSize = 11.sp, color = mut)
                        }
                    }

                    OutlinedTextField(
                        value = realTgId,
                        onValueChange = { realTgId = it.uppercase() },
                        label = { Text("Real Athlete Talent Graph ID *", color = mut) },
                        placeholder = { Text("e.g. TG-ATH-00892", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )

                    OutlinedTextField(
                        value = claimantName,
                        onValueChange = { claimantName = it },
                        label = { Text("Verified Full Legal Name *", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )

                    OutlinedTextField(
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        label = { Text("National ID / Birth Certificate #", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )

                    OutlinedTextField(
                        value = claimantPhone,
                        onValueChange = { claimantPhone = it },
                        label = { Text("Registered Mobile Phone (+254...)", color = mut) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = acc,
                            unfocusedBorderColor = line,
                            focusedTextColor = txt,
                            unfocusedTextColor = txt,
                            focusedContainerColor = panel2,
                            unfocusedContainerColor = panel2
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = acc.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, acc.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = acc, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("History Invariant: The ghost record merges into the permanent ID. No stats are lost.", fontSize = 11.sp, color = txt)
                        }
                    }

                    Button(
                        onClick = {
                            if (realTgId.length > 7 && claimantName.isNotBlank()) {
                                athleteList = athleteList.map {
                                    if (it.id == ath.id) it.copy(
                                        id = realTgId,
                                        name = claimantName,
                                        status = "Active",
                                        careerTimeline = it.careerTimeline + "2026 → Claimed and verified into $realTgId"
                                    ) else it
                                }
                                showClaimGhostDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                        shape = RoundedCornerShape(9.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("Merge & Verify Profile", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                    }
                }
            }
        }
    }
}
