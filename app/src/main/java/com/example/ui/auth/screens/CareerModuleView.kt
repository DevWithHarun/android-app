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
import com.example.data.AchievementEntity
import com.example.data.CareerStintEntity
import com.example.data.DocumentEntity
import com.example.data.MatchLogEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun CareerModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedFilter by remember { mutableStateOf("All Stints") }
    var showAddStintModal by remember { mutableStateOf(false) }
    var stintToEdit by remember { mutableStateOf<CareerStintEntity?>(null) }
    var stintToDelete by remember { mutableStateOf<CareerStintEntity?>(null) }
    var stintToAttachDoc by remember { mutableStateOf<CareerStintEntity?>(null) }
    var showAddAchievementModal by remember { mutableStateOf(false) }

    val userData = uiState.userData ?: emptyMap()
    val athleteName = (userData["fullName"] as? String ?: userData["name"] as? String ?: uiState.userName).ifBlank { "Athlete" }
    
    // Only real data from user profile or recorded career stints
    val activeStint = uiState.careerStints.firstOrNull { it.status.equals("Active", true) || it.endDate.equals("Present", true) }
    val currentClub = (userData["clubName"] as? String ?: userData["club"] as? String ?: activeStint?.organization ?: "").ifBlank { "Unattached / Free Agent" }
    val currentTeam = (userData["currentTeam"] as? String ?: userData["team"] as? String ?: activeStint?.team ?: "").ifBlank { "No Active Squad" }
    val federationId = (userData["federationId"] as? String ?: "").ifBlank { "Not Registered" }
    val primaryPosition = (userData["position"] as? String ?: activeStint?.position ?: uiState.careerStints.firstOrNull()?.position ?: "").ifBlank { "Unassigned" }
    val hasActiveContract = activeStint != null

    val filteredStints = remember(selectedFilter, uiState.careerStints) {
        when (selectedFilter) {
            "Active" -> uiState.careerStints.filter { it.status.equals("Active", ignoreCase = true) || it.endDate.equals("Present", ignoreCase = true) }
            "Historical" -> uiState.careerStints.filter { !it.status.equals("Active", ignoreCase = true) && !it.endDate.equals("Present", ignoreCase = true) }
            else -> uiState.careerStints
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Career Trajectory & Status
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (hasActiveContract) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = if (hasActiveContract) "ACTIVE CONTRACT" else "FREE AGENT",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasActiveContract) Color(0xFF047857) else textMuted
                                    )
                                }
                                Text("•  $federationId", fontSize = 11.sp, color = textMuted, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Career Pathway & Stints",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = if (currentClub != "Unattached / Free Agent") "$currentClub • $currentTeam" else "No active club affiliation",
                                fontSize = 13.sp,
                                color = if (hasActiveContract) secondaryColor else textMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(secondaryColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Route, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CareerStatItem(value = "${uiState.careerStints.size}", label = "Clubs & Stints")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        CareerStatItem(value = "${uiState.achievements.size}", label = "Honours & Cups")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        CareerStatItem(value = primaryPosition.take(12), label = "Primary Role")
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddStintModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Career Stint", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                shareCareerCv(context, athleteName, currentClub, currentTeam, uiState.careerStints, uiState.achievements, uiState.matchLogs)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share CV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Section: Visual Career Pathway Progression Roadmap
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Timeline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                            Text("Career Pathway Progression", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (uiState.careerStints.isNotEmpty()) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                if (uiState.careerStints.isNotEmpty()) "${uiState.careerStints.size} Verified Stages" else "Pathway Inactive",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.careerStints.isNotEmpty()) Color(0xFF047857) else textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CareerPathwayRoadmap(stints = uiState.careerStints)
                }
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("All Stints", "Active", "Historical", "Honours", "Clearance Status")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else primaryColor
                        )
                    }
                }
            }
        }

        // Section: Chronological Stints
        if (selectedFilter != "Honours" && selectedFilter != "Clearance Status") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CAREER TIMELINE (${filteredStints.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { showAddStintModal = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Stint", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (filteredStints.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Outlined.SportsSoccer, contentDescription = null, tint = textMuted, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No career stints recorded", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 14.sp)
                            Text("Log your genuine academy, youth, or senior club stints to start building your verified CV.", color = textMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { showAddStintModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ Add Your First Stint", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(filteredStints, key = { it.id }) { stint ->
                    // Filter match logs and documents matching this specific stint
                    val stintMatches = remember(uiState.matchLogs, stint.organization) {
                        uiState.matchLogs.filter { match ->
                            match.matchTitle.contains(stint.organization.trim(), ignoreCase = true)
                        }
                    }

                    val stintDocs = remember(uiState.documents, stint.organization) {
                        uiState.documents.filter { doc ->
                            doc.title.contains(stint.organization.trim(), ignoreCase = true)
                        }
                    }

                    CareerStintTimelineCard(
                        stint = stint,
                        stintMatches = stintMatches,
                        stintDocs = stintDocs,
                        onEdit = { stintToEdit = stint },
                        onDelete = { stintToDelete = stint },
                        onAttachProof = { stintToAttachDoc = stint }
                    )
                }
            }
        }

        // Section: Honours & Achievements
        if (selectedFilter == "All Stints" || selectedFilter == "Honours") {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HONOURS, TROPHIES & BENCHMARKS (${uiState.achievements.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { showAddAchievementModal = true }) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Trophy", fontSize = 12.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.achievements.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("No honours or trophies recorded yet.", color = textMuted, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(uiState.achievements) { achievement ->
                    AchievementHonourCard(achievement)
                }
            }
        }

        // Section: Clearance Status & Transfer Passport
        if (selectedFilter == "All Stints" || selectedFilter == "Clearance Status") {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FEDERATION CLEARANCE & PASSPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                            Text("Official Federation Registration Dossier", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        CareerDetailRow("Federation ID", federationId)
                        CareerDetailRow("International Transfer Clearance (ITC)", if (hasActiveContract) "Standard Clearance" else "Unregistered")
                        CareerDetailRow("Primary Club Registration", currentClub)
                        CareerDetailRow("Squad Category", currentTeam)
                        CareerDetailRow("Disciplinary Status", "Clean Record (0 Active Bans)")
                        CareerDetailRow("Registration Valid Thru", if (federationId != "Not Registered") "Active Season" else "Pending Registration")
                        CareerDetailRow("Representation Status", (userData["agency"] as? String ?: "").ifBlank { "Direct / Unrepresented" })
                    }
                }
            }
        }
    }

    // Modal: Add Career Stint
    if (showAddStintModal) {
        AddCareerStintDialog(
            defaultClub = if (currentClub != "Unattached / Free Agent") currentClub else "",
            defaultPosition = if (primaryPosition != "Unassigned") primaryPosition else "",
            onDismiss = { showAddStintModal = false },
            onAdd = { org, team, pos, comp, start, end, status, verification ->
                viewModel.addCareerStint(org, team, pos, comp, start, end, status, verification)
                showAddStintModal = false
            }
        )
    }

    // Modal: Edit Career Stint (Inline Editing)
    if (stintToEdit != null) {
        EditCareerStintDialog(
            stint = stintToEdit!!,
            onDismiss = { stintToEdit = null },
            onSave = { updatedStint ->
                viewModel.updateCareerStint(updatedStint)
                stintToEdit = null
            }
        )
    }

    // Modal: Attach Proof / Document to Stint
    if (stintToAttachDoc != null) {
        AttachProofDialog(
            stint = stintToAttachDoc!!,
            onDismiss = { stintToAttachDoc = null },
            onAttach = { title, category, verification ->
                viewModel.addDocument(title, category, verification)
                stintToAttachDoc = null
            }
        )
    }

    // Modal: Add Achievement
    if (showAddAchievementModal) {
        AddAchievementDialog(
            defaultClub = if (currentClub != "Unattached / Free Agent") currentClub else "",
            onDismiss = { showAddAchievementModal = false },
            onAdd = { title, comp, org, date, category, verification ->
                viewModel.addAchievement(title, comp, org, date, category, verification)
                showAddAchievementModal = false
            }
        )
    }

    // Confirm Delete Dialog
    if (stintToDelete != null) {
        AlertDialog(
            onDismissRequest = { stintToDelete = null },
            title = { Text("Delete Career Stint?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${stintToDelete?.organization} (${stintToDelete?.team}) from your career timeline?") },
            confirmButton = {
                Button(
                    onClick = {
                        stintToDelete?.let { viewModel.deleteCareerStint(it) }
                        stintToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { stintToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CareerPathwayRoadmap(stints: List<CareerStintEntity>) {
    val steps = if (stints.isNotEmpty()) {
        stints.reversed().mapIndexed { idx, stint ->
            val tier = if (idx == stints.size - 1) "Active" else "Step ${idx + 1}"
            tier to "${stint.organization} (${stint.team})"
        }
    } else {
        listOf(
            "Grassroots" to "Add Academy",
            "Development" to "Add Development",
            "Senior Tier" to "Add First Team",
            "Target Goal" to "Target Pro Club"
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val isCurrent = if (stints.isNotEmpty()) index == steps.size - 1 else false
            val isPassed = if (stints.isNotEmpty()) index < steps.size - 1 else false

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPassed -> Color(0xFF0D9488)
                                isCurrent -> Color(0xFF1E293B)
                                else -> Color(0xFFE2E8F0)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isPassed -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        isCurrent -> Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        else -> Text("${index + 1}", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = step.first,
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) Color(0xFF1E293B) else Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = step.second,
                    fontSize = 9.sp,
                    color = if (isCurrent) Color(0xFF0D9488) else Color(0xFF94A3B8),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CareerStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CareerStintTimelineCard(
    stint: CareerStintEntity,
    stintMatches: List<MatchLogEntity>,
    stintDocs: List<DocumentEntity>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAttachProof: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val isActive = stint.status.equals("Active", ignoreCase = true) || stint.endDate.equals("Present", ignoreCase = true)
    var isExpanded by remember { mutableStateOf(false) }

    // Real aggregate statistics from actual match logs
    val appearances = stintMatches.size
    val goals = stintMatches.sumOf { it.goals }
    val assists = stintMatches.sumOf { it.assists }
    val totalMinutes = stintMatches.sumOf { it.minutesPlayed }
    val avgRating = if (stintMatches.isNotEmpty()) String.format("%.1f", stintMatches.map { it.matchRating }.average()) else "-"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isActive) secondaryColor.copy(alpha = 0.5f) else borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) secondaryColor else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stint.organization.take(2).uppercase().ifBlank { "CL" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Column {
                        Text(
                            text = stint.organization,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "${stint.team} • ${stint.position}",
                            fontSize = 13.sp,
                            color = if (isActive) secondaryColor else textMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = if (isActive) "ACTIVE" else stint.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color(0xFF047857) else textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Competition & Dates Row
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Outlined.EmojiEvents, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                        Text(stint.competition.ifBlank { "Standard Competition" }, fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                    }
                    Text(
                        text = "${stint.startDate} – ${stint.endDate}",
                        fontSize = 11.sp,
                        color = textMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stint Statistics Summary Pill Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📊 $appearances Apps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("⏱️ ${totalMinutes}m", fontSize = 11.sp, color = textMuted)
                Text("⚽ $goals Goals", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF047857))
                Text("🎯 $assists Assists", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0284C7))
                Text("⭐ $avgRating", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Stint Intelligence Dossier (Match Breakdown & Attached Proof)
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text("STINT INTELLIGENCE & MATCH LOGS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (stintMatches.isNotEmpty()) {
                        stintMatches.forEach { match ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(match.matchTitle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                                Text("${match.minutesPlayed}m • ${match.goals}G ${match.assists}A • ⭐${match.matchRating}", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text("No match logs linked to this stint yet.", fontSize = 11.sp, color = textMuted)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = borderColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Attached Proof & Contracts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ATTACHED PROOF & CONTRACTS", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        TextButton(
                            onClick = onAttachProof,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach Proof", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (stintDocs.isNotEmpty()) {
                        stintDocs.forEach { doc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                Text(doc.title, fontSize = 11.sp, color = primaryColor, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFECFDF5)) {
                                    Text(doc.verificationState, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                }
                            }
                        }
                    } else {
                        Text("No documents attached yet. Tap 'Attach Proof' to add contracts or certificates.", fontSize = 11.sp, color = textMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Verification, Details Toggle, Edit & Delete actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                    Text(
                        text = stint.verificationLevel.ifBlank { "Self Reported" },
                        fontSize = 11.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(if (isExpanded) "Hide Dossier" else "View Dossier", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                        Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    }

                    // Edit Stint Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit stint", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }

                    // Delete Stint Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete stint", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementHonourCard(achievement: AchievementEntity) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFFEF3C7)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFFEF3C7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(achievement.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                Text("${achievement.competition} • ${achievement.organization}", fontSize = 12.sp, color = textMuted)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (achievement.date.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = achievement.date,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CareerDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
    }
}

// Dialog: Add Career Stint
@Composable
fun AddCareerStintDialog(
    defaultClub: String,
    defaultPosition: String,
    onDismiss: () -> Unit,
    onAdd: (org: String, team: String, pos: String, comp: String, start: String, end: String, status: String, verification: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    var org by remember { mutableStateOf(defaultClub) }
    var team by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf(defaultPosition) }
    var comp by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("Present") }
    var status by remember { mutableStateOf("Active") }
    var verification by remember { mutableStateOf("Self Reported") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
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
                    Text("Add Career Stint", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = borderColor)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = org,
                            onValueChange = { org = it },
                            label = { Text("Club / Organization") },
                            placeholder = { Text("Enter club name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = team,
                            onValueChange = { team = it },
                            label = { Text("Team / Squad Tier") },
                            placeholder = { Text("e.g. Senior First Team, U20, Academy") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = pos,
                            onValueChange = { pos = it },
                            label = { Text("Playing Position") },
                            placeholder = { Text("e.g. Centre Midfielder") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = comp,
                            onValueChange = { comp = it },
                            label = { Text("Competition / League") },
                            placeholder = { Text("Enter competition or league") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = start,
                                onValueChange = { start = it },
                                label = { Text("Start Date") },
                                placeholder = { Text("e.g. Jan 2023") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = end,
                                onValueChange = { end = it },
                                label = { Text("End Date") },
                                placeholder = { Text("e.g. Present") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Text("Contract Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Active", "Completed", "On Loan", "Trial").forEach { opt ->
                                val isSel = status == opt
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { status = opt }
                                ) {
                                    Text(
                                        text = opt,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = verification,
                            onValueChange = { verification = it },
                            label = { Text("Verification Level") },
                            placeholder = { Text("Federation Verified / Club Signed / Self Reported") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (org.isNotBlank() && team.isNotBlank()) {
                                onAdd(org, team, pos, comp, start, end, status, verification)
                            }
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Stint", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: Inline Editing of an Existing Stint
@Composable
fun EditCareerStintDialog(
    stint: CareerStintEntity,
    onDismiss: () -> Unit,
    onSave: (CareerStintEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    var org by remember { mutableStateOf(stint.organization) }
    var team by remember { mutableStateOf(stint.team) }
    var pos by remember { mutableStateOf(stint.position) }
    var comp by remember { mutableStateOf(stint.competition) }
    var start by remember { mutableStateOf(stint.startDate) }
    var end by remember { mutableStateOf(stint.endDate) }
    var status by remember { mutableStateOf(stint.status) }
    var verification by remember { mutableStateOf(stint.verificationLevel) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
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
                    Text("Edit Career Stint", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = borderColor)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = org,
                            onValueChange = { org = it },
                            label = { Text("Club / Organization") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = team,
                            onValueChange = { team = it },
                            label = { Text("Team / Squad Tier") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = pos,
                            onValueChange = { pos = it },
                            label = { Text("Playing Position") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = comp,
                            onValueChange = { comp = it },
                            label = { Text("Competition / League") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = start,
                                onValueChange = { start = it },
                                label = { Text("Start Date") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = end,
                                onValueChange = { end = it },
                                label = { Text("End Date") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Text("Contract Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Active", "Completed", "On Loan", "Trial").forEach { opt ->
                                val isSel = status.equals(opt, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { status = opt }
                                ) {
                                    Text(
                                        text = opt,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = verification,
                            onValueChange = { verification = it },
                            label = { Text("Verification Level") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (org.isNotBlank() && team.isNotBlank()) {
                                onSave(
                                    stint.copy(
                                        organization = org,
                                        team = team,
                                        position = pos,
                                        competition = comp,
                                        startDate = start,
                                        endDate = end,
                                        status = status,
                                        verificationLevel = verification
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Update Stint", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: Attach Proof / Official Contract to Stint
@Composable
fun AttachProofDialog(
    stint: CareerStintEntity,
    onDismiss: () -> Unit,
    onAttach: (title: String, category: String, verification: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var docTitle by remember { mutableStateOf("") }
    var docCategory by remember { mutableStateOf("Contract") }
    var docStatus by remember { mutableStateOf("Verified") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Attach Stint Proof Document", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = primaryColor)
                Text("Link a contract, release letter, or federated player pass to ${stint.organization}.", fontSize = 12.sp, color = textMuted)

                OutlinedTextField(
                    value = docTitle,
                    onValueChange = { docTitle = it },
                    label = { Text("Document Title") },
                    placeholder = { Text("e.g. Player Registration Pass") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Document Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Contract", "Release Letter", "ITC License").forEach { cat ->
                        val isSel = docCategory == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f).clickable { docCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(vertical = 7.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (docTitle.isNotBlank()) {
                                onAttach(docTitle, docCategory, docStatus)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Proof", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: Add Achievement
@Composable
fun AddAchievementDialog(
    defaultClub: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, comp: String, org: String, date: String, category: String, verification: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var comp by remember { mutableStateOf("") }
    var org by remember { mutableStateOf(defaultClub) }
    var date by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Individual") }
    var verification by remember { mutableStateOf("Verified") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Honour or Trophy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Trophy / Award Title") },
                    placeholder = { Text("e.g. League Best Midfielder") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = comp,
                    onValueChange = { comp = it },
                    label = { Text("Competition / Event") },
                    placeholder = { Text("e.g. Regional Championship") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = org,
                    onValueChange = { org = it },
                    label = { Text("Club / Federation") },
                    placeholder = { Text("e.g. Coastal FC") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Year") },
                        placeholder = { Text("e.g. 2024") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        placeholder = { Text("e.g. Individual") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(title, comp, org, date, category, verification)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Trophy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Function to share formatted career CV to scouts, agents, or coaches
fun shareCareerCv(
    context: Context,
    athleteName: String,
    club: String,
    team: String,
    stints: List<CareerStintEntity>,
    achievements: List<AchievementEntity>,
    matches: List<MatchLogEntity>
) {
    val builder = StringBuilder()
    builder.append("TALENT GRAPH • OFFICIAL ATHLETE CAREER PASSPORT\n")
    builder.append("Athlete: $athleteName\n")
    builder.append("Current Club: $club ($team)\n\n")

    builder.append("CAREER PATHWAY & STINTS:\n")
    if (stints.isEmpty()) {
        builder.append("• No stints recorded yet\n")
    } else {
        stints.forEach { s ->
            builder.append("• ${s.organization} (${s.team}) - ${s.position}\n")
            builder.append("  Competition: ${s.competition} | ${s.startDate} - ${s.endDate} [${s.status}]\n")
            builder.append("  Verification: ${s.verificationLevel}\n\n")
        }
    }

    if (achievements.isNotEmpty()) {
        builder.append("HONOURS & ACHIEVEMENTS:\n")
        achievements.forEach { a ->
            builder.append("🏆 ${a.title} (${a.date}) - ${a.competition}\n")
        }
        builder.append("\n")
    }

    if (matches.isNotEmpty()) {
        builder.append("RECENT MATCH LOGS:\n")
        matches.take(5).forEach { m ->
            builder.append("⚽ ${m.matchTitle} (${m.minutesPlayed}m, ${m.goals}G, ${m.assists}A, Rating: ${m.matchRating}/10)\n")
        }
        builder.append("\n")
    }

    builder.append("Verified via Talent Graph Sports Intelligence Platform")

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Career Passport: $athleteName")
        putExtra(Intent.EXTRA_TEXT, builder.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Share Career CV"))
}
