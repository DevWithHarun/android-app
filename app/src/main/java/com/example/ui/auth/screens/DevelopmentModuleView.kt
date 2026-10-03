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
import com.example.data.DevelopmentGoalEntity
import com.example.data.EvidenceEntity
import com.example.data.TrainingSessionEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DevelopmentModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState,
    initialTab: String = "goals"
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val emeraldColor = Color(0xFF10B981)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var currentTab by remember(initialTab) { mutableStateOf(initialTab) }

    // Dialog states
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<DevelopmentGoalEntity?>(null) }
    var goalToDelete by remember { mutableStateOf<DevelopmentGoalEntity?>(null) }

    var showAddTrainingDialog by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<TrainingSessionEntity?>(null) }
    var sessionToDelete by remember { mutableStateOf<TrainingSessionEntity?>(null) }

    var showAddAchievementDialog by remember { mutableStateOf(false) }
    var achievementToEdit by remember { mutableStateOf<AchievementEntity?>(null) }
    var achievementToDelete by remember { mutableStateOf<AchievementEntity?>(null) }

    var showAddEvidenceDialog by remember { mutableStateOf(false) }
    var evidenceToEdit by remember { mutableStateOf<EvidenceEntity?>(null) }
    var evidenceToDelete by remember { mutableStateOf<EvidenceEntity?>(null) }

    // Summary Metrics
    val activeGoalsCount = remember(uiState.developmentGoals) {
        uiState.developmentGoals.count { it.status.contains("Progress", ignoreCase = true) || it.status.contains("Active", ignoreCase = true) }
    }
    val totalTrainingMinutes = remember(uiState.trainingSessions) {
        uiState.trainingSessions.sumOf { it.durationMins }
    }
    val achievementsCount = uiState.achievements.size
    val evidenceCount = uiState.evidenceVault.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Overview Card
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
                                color = Color(0xFFECFDF5)
                            ) {
                                Text(
                                    text = "ATHLETIC DEVELOPMENT SUITE",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Player Development & Growth",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Milestone goals, training logs, tournament honors, and video evidence.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(emeraldColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = emeraldColor, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DevStatItem(value = "$activeGoalsCount", label = "Active Goals")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        DevStatItem(value = "${totalTrainingMinutes}m", label = "Training Mins")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        DevStatItem(value = "$achievementsCount", label = "Honors Won")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        DevStatItem(value = "$evidenceCount", label = "Video Reels")
                    }
                }
            }
        }

        // Navigation Tabs Row: Goals | Training | Achievements | Evidence
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        Triple("goals", "Goals", "${uiState.developmentGoals.size}"),
                        Triple("training", "Training", "${uiState.trainingSessions.size}"),
                        Triple("achievements", "Honors", "${uiState.achievements.size}"),
                        Triple("evidence", "Evidence", "${uiState.evidenceVault.size}")
                    )

                    tabs.forEach { (tabKey, title, count) ->
                        val isSelected = currentTab == tabKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) primaryColor else Color.Transparent)
                                .clickable { currentTab = tabKey }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else primaryColor
                                )
                                Text(
                                    text = count,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) secondaryColor else textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: DEVELOPMENT GOALS
        if (currentTab == "goals") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEVELOPMENT GOALS & MILESTONES (${uiState.developmentGoals.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddGoalDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.developmentGoals.isEmpty()) {
                item {
                    DevEmptyStateCard(
                        icon = Icons.Default.TrendingUp,
                        title = "No Development Goals Set",
                        subtitle = "Track technical, tactical, and physical milestones to accelerate development.",
                        buttonText = "+ Set First Goal",
                        onClick = { showAddGoalDialog = true }
                    )
                }
            } else {
                items(uiState.developmentGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onIncrement = {
                            val newLevel = (goal.currentLevel + 5).coerceAtMost(goal.targetLevel)
                            val newStatus = if (newLevel >= goal.targetLevel) "Achieved" else goal.status
                            viewModel.updateDevelopmentGoal(goal.copy(currentLevel = newLevel, status = newStatus))
                        },
                        onEdit = { goalToEdit = goal },
                        onDelete = { goalToDelete = goal }
                    )
                }
            }
        }

        // TAB 2: TRAINING SESSIONS & DRILLS
        if (currentTab == "training") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRAINING LOG & DRILL SESSIONS (${uiState.trainingSessions.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddTrainingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Training", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.trainingSessions.isEmpty()) {
                item {
                    DevEmptyStateCard(
                        icon = Icons.Default.FitnessCenter,
                        title = "No Training Sessions Logged",
                        subtitle = "Record tactical training, gym conditioning, and coach evaluation notes.",
                        buttonText = "+ Log Training Session",
                        onClick = { showAddTrainingDialog = true }
                    )
                }
            } else {
                items(uiState.trainingSessions, key = { it.id }) { session ->
                    TrainingSessionCard(
                        session = session,
                        onEdit = { sessionToEdit = session },
                        onDelete = { sessionToDelete = session }
                    )
                }
            }
        }

        // TAB 3: ACHIEVEMENTS & HONORS
        if (currentTab == "achievements") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HONORS, TROPHIES & ACCOLADES (${uiState.achievements.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddAchievementDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Honor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.achievements.isEmpty()) {
                item {
                    DevEmptyStateCard(
                        icon = Icons.Default.EmojiEvents,
                        title = "No Honors or Trophies Logged",
                        subtitle = "Record league championships, tournament MVP medals, and federation awards.",
                        buttonText = "+ Record First Achievement",
                        onClick = { showAddAchievementDialog = true }
                    )
                }
            } else {
                items(uiState.achievements, key = { it.id }) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        onEdit = { achievementToEdit = achievement },
                        onDelete = { achievementToDelete = achievement },
                        onShare = {
                            shareAchievementDetails(context, achievement)
                        }
                    )
                }
            }
        }

        // TAB 4: VIDEOS & VISUAL EVIDENCE
        if (currentTab == "evidence") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCOUTING REELS & VIDEO EVIDENCE (${uiState.evidenceVault.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textMuted,
                        letterSpacing = 1.sp
                    )
                    Button(
                        onClick = { showAddEvidenceDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Video Reel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uiState.evidenceVault.isEmpty()) {
                item {
                    DevEmptyStateCard(
                        icon = Icons.Default.VideoLibrary,
                        title = "No Video Reels or Evidence Uploaded",
                        subtitle = "Link match highlights, tactical reels, and verified drill footage for scout discovery.",
                        buttonText = "+ Add Video Reel",
                        onClick = { showAddEvidenceDialog = true }
                    )
                }
            } else {
                items(uiState.evidenceVault, key = { it.id }) { evidence ->
                    EvidenceCard(
                        evidence = evidence,
                        onEdit = { evidenceToEdit = evidence },
                        onDelete = { evidenceToDelete = evidence },
                        onShare = {
                            shareEvidenceDetails(context, evidence)
                        }
                    )
                }
            }
        }
    }

    // DIALOGS & MODALS

    // Add Goal
    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onSave = { statement, dim, date, cur, tar, stat ->
                viewModel.addDevelopmentGoal(statement, dim, date, cur, tar, stat)
                showAddGoalDialog = false
            }
        )
    }

    // Edit Goal
    if (goalToEdit != null) {
        EditGoalDialog(
            goal = goalToEdit!!,
            onDismiss = { goalToEdit = null },
            onSave = { updatedGoal ->
                viewModel.updateDevelopmentGoal(updatedGoal)
                goalToEdit = null
            }
        )
    }

    // Delete Goal
    if (goalToDelete != null) {
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Goal?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${goalToDelete?.goalStatement}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        goalToDelete?.let { viewModel.deleteDevelopmentGoal(it) }
                        goalToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Add Training Session
    if (showAddTrainingDialog) {
        AddTrainingDialog(
            onDismiss = { showAddTrainingDialog = false },
            onSave = { type, date, duration, intensity, notes, attendance ->
                viewModel.addTrainingSession(type, date, duration, intensity, notes, attendance)
                showAddTrainingDialog = false
            }
        )
    }

    // Edit Training Session
    if (sessionToEdit != null) {
        EditTrainingDialog(
            session = sessionToEdit!!,
            onDismiss = { sessionToEdit = null },
            onSave = { updatedSession ->
                viewModel.updateTrainingSession(updatedSession)
                sessionToEdit = null
            }
        )
    }

    // Delete Training Session
    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Delete Training Session?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this session log?") },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToDelete?.let { viewModel.deleteTrainingSession(it) }
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Add Achievement
    if (showAddAchievementDialog) {
        AddAchievementDialog(
            onDismiss = { showAddAchievementDialog = false },
            onSave = { title, comp, org, date, cat, ver ->
                viewModel.addAchievement(title, comp, org, date, cat, ver)
                showAddAchievementDialog = false
            }
        )
    }

    // Edit Achievement
    if (achievementToEdit != null) {
        EditAchievementDialog(
            achievement = achievementToEdit!!,
            onDismiss = { achievementToEdit = null },
            onSave = { updatedAch ->
                viewModel.updateAchievement(updatedAch)
                achievementToEdit = null
            }
        )
    }

    // Delete Achievement
    if (achievementToDelete != null) {
        AlertDialog(
            onDismissRequest = { achievementToDelete = null },
            title = { Text("Delete Achievement?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${achievementToDelete?.title}' from your honors?") },
            confirmButton = {
                Button(
                    onClick = {
                        achievementToDelete?.let { viewModel.deleteAchievement(it) }
                        achievementToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { achievementToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Add Evidence / Video Reel
    if (showAddEvidenceDialog) {
        AddEvidenceDialog(
            onDismiss = { showAddEvidenceDialog = false },
            onSave = { title, type, ver, date ->
                viewModel.addEvidence(title, type, ver, date)
                showAddEvidenceDialog = false
            }
        )
    }

    // Edit Evidence
    if (evidenceToEdit != null) {
        EditEvidenceDialog(
            evidence = evidenceToEdit!!,
            onDismiss = { evidenceToEdit = null },
            onSave = { updatedEv ->
                viewModel.updateEvidence(updatedEv)
                evidenceToEdit = null
            }
        )
    }

    // Delete Evidence
    if (evidenceToDelete != null) {
        AlertDialog(
            onDismissRequest = { evidenceToDelete = null },
            title = { Text("Delete Video Reel?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${evidenceToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        evidenceToDelete?.let { viewModel.deleteEvidence(it) }
                        evidenceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { evidenceToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun DevStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DevEmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(28.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
            Text(subtitle, color = textMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// 1. GOAL CARD
@Composable
fun GoalCard(
    goal: DevelopmentGoalEntity,
    onIncrement: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val progress = (goal.currentLevel.toFloat() / goal.targetLevel.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    val isAchieved = goal.status.contains("Achieved", ignoreCase = true) || progress >= 1f

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isAchieved) Color(0xFF10B981) else borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.goalStatement, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Dimension: ${goal.dimension} • Target: ${goal.targetDate}",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAchieved) Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = if (isAchieved) "ACHIEVED" else goal.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAchieved) Color(0xFF047857) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Milestone Progress", fontSize = 11.sp, color = textMuted, fontWeight = FontWeight.Medium)
                Text("${goal.currentLevel} / ${goal.targetLevel} (${(progress * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isAchieved) Color(0xFF10B981) else secondaryColor,
                trackColor = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isAchieved) {
                    OutlinedButton(
                        onClick = onIncrement,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("+5% Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(15.dp))
                        Text("Milestone Completed", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 2. TRAINING SESSION CARD
@Composable
fun TrainingSessionCard(
    session: TrainingSessionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(session.sessionType, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                        Text("${session.date} • ${session.durationMins} minutes", fontSize = 12.sp, color = textMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (session.intensity.lowercase()) {
                        "high", "peak" -> Color(0xFFFEE2E2)
                        "moderate" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFECFDF5)
                    }
                ) {
                    Text(
                        text = session.intensity.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (session.intensity.lowercase()) {
                            "high", "peak" -> Color(0xFFDC2626)
                            "moderate" -> Color(0xFFB45309)
                            else -> Color(0xFF047857)
                        }
                    )
                }
            }

            if (session.coachNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Coach Notes: ${session.coachNotes}",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Attendance: ${session.attendance.ifBlank { "Present" }}", fontSize = 11.sp, color = textMuted, fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 3. ACHIEVEMENT CARD
@Composable
fun AchievementCard(
    achievement: AchievementEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(achievement.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                        Text("${achievement.competition} • ${achievement.organization}", fontSize = 12.sp, color = textMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFECFDF5)
                ) {
                    Text(
                        text = achievement.verificationLevel.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Category: ${achievement.category} • Date: ${achievement.date}", fontSize = 11.sp, color = textMuted, fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// 4. EVIDENCE CARD
@Composable
fun EvidenceCard(
    evidence: EvidenceEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(evidence.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                        Text("${evidence.type} • Uploaded ${evidence.dateUploaded}", fontSize = 12.sp, color = textMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFECFDF5)
                ) {
                    Text(
                        text = evidence.verificationState.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Verified Match Clip • Ready for Scouts", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.SemiBold)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// DIALOGS

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onSave: (statement: String, dim: String, targetDate: String, cur: Int, tar: Int, stat: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var statement by remember { mutableStateOf("") }
    var dimension by remember { mutableStateOf("Technical") }
    var targetDate by remember { mutableStateOf("End of Season") }
    var currentLevel by remember { mutableStateOf("50") }
    var targetLevel by remember { mutableStateOf("90") }
    var status by remember { mutableStateOf("In Progress") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Development Goal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = statement,
                    onValueChange = { statement = it },
                    label = { Text("Milestone / Goal Statement") },
                    placeholder = { Text("e.g. Master weak foot volley accuracy") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Dimension", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Technical", "Tactical", "Physical", "Mental").forEach { dim ->
                        val isSel = dimension == dim
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f).clickable { dimension = dim }
                        ) {
                            Text(
                                text = dim,
                                modifier = Modifier.padding(vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentLevel,
                        onValueChange = { currentLevel = it },
                        label = { Text("Current (%)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetLevel,
                        onValueChange = { targetLevel = it },
                        label = { Text("Target (%)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (statement.isNotBlank()) {
                                onSave(statement, dimension, targetDate, currentLevel.toIntOrNull() ?: 50, targetLevel.toIntOrNull() ?: 90, status)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditGoalDialog(
    goal: DevelopmentGoalEntity,
    onDismiss: () -> Unit,
    onSave: (DevelopmentGoalEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var statement by remember { mutableStateOf(goal.goalStatement) }
    var dimension by remember { mutableStateOf(goal.dimension) }
    var targetDate by remember { mutableStateOf(goal.targetDate) }
    var currentLevel by remember { mutableStateOf(goal.currentLevel.toString()) }
    var targetLevel by remember { mutableStateOf(goal.targetLevel.toString()) }
    var status by remember { mutableStateOf(goal.status) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Goal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = statement,
                    onValueChange = { statement = it },
                    label = { Text("Goal Statement") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentLevel,
                        onValueChange = { currentLevel = it },
                        label = { Text("Current (%)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetLevel,
                        onValueChange = { targetLevel = it },
                        label = { Text("Target (%)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status (In Progress / Achieved)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (statement.isNotBlank()) {
                                onSave(
                                    goal.copy(
                                        goalStatement = statement,
                                        dimension = dimension,
                                        targetDate = targetDate,
                                        currentLevel = currentLevel.toIntOrNull() ?: goal.currentLevel,
                                        targetLevel = targetLevel.toIntOrNull() ?: goal.targetLevel,
                                        status = status
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTrainingDialog(
    onDismiss: () -> Unit,
    onSave: (type: String, date: String, duration: Int, intensity: String, notes: String, attendance: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var type by remember { mutableStateOf("Tactical & Positioning Drill") }
    var date by remember { mutableStateOf("Today") }
    var duration by remember { mutableStateOf("90") }
    var intensity by remember { mutableStateOf("High") }
    var notes by remember { mutableStateOf("") }
    var attendance by remember { mutableStateOf("Present") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Log Training Session", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Session / Drill Type") },
                    placeholder = { Text("e.g. High-Press Tactical Drills") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (mins)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = intensity,
                        onValueChange = { intensity = it },
                        label = { Text("Intensity") },
                        placeholder = { Text("High / Moderate") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Coach Evaluation Notes") },
                    placeholder = { Text("e.g. Excellent spatial awareness and counter pressing.") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (type.isNotBlank()) {
                                onSave(type, date, duration.toIntOrNull() ?: 90, intensity, notes, attendance)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditTrainingDialog(
    session: TrainingSessionEntity,
    onDismiss: () -> Unit,
    onSave: (TrainingSessionEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var type by remember { mutableStateOf(session.sessionType) }
    var duration by remember { mutableStateOf(session.durationMins.toString()) }
    var intensity by remember { mutableStateOf(session.intensity) }
    var notes by remember { mutableStateOf(session.coachNotes) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Training Session", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Session Type") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (mins)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = intensity, onValueChange = { intensity = it }, label = { Text("Intensity") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Coach Notes") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (type.isNotBlank()) {
                                onSave(session.copy(sessionType = type, durationMins = duration.toIntOrNull() ?: session.durationMins, intensity = intensity, coachNotes = notes))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddAchievementDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, comp: String, org: String, date: String, cat: String, ver: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var competition by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2024") }
    var category by remember { mutableStateOf("Individual Award") }
    var verification by remember { mutableStateOf("Federation Verified") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Record Honor / Award", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Honor / Award Title") }, placeholder = { Text("e.g. Tournament MVP / Golden Boot") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = competition, onValueChange = { competition = it }, label = { Text("Competition / League") }, placeholder = { Text("e.g. National U20 Championship") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Awarding Body / Organization") }, placeholder = { Text("e.g. Football Federation") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Year / Date") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, competition, organization, date, category, verification)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Honor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditAchievementDialog(
    achievement: AchievementEntity,
    onDismiss: () -> Unit,
    onSave: (AchievementEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf(achievement.title) }
    var competition by remember { mutableStateOf(achievement.competition) }
    var organization by remember { mutableStateOf(achievement.organization) }
    var date by remember { mutableStateOf(achievement.date) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Honor", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = competition, onValueChange = { competition = it }, label = { Text("Competition") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Organization") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(achievement.copy(title = title, competition = competition, organization = organization))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEvidenceDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, type: String, ver: String, date: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Match Highlights") }
    var date by remember { mutableStateOf("2024") }
    var verification by remember { mutableStateOf("Verified Clip") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Video Reel / Clip", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Video Title") }, placeholder = { Text("e.g. Solo Goal & Assist vs Mombasa Stars") }, modifier = Modifier.fillMaxWidth())

                Text("Clip Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Match Highlights", "Tactical Skills", "Gym / Fitness").forEach { cat ->
                        val isSel = type == cat
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f).clickable { type = cat }
                        ) {
                            Text(
                                text = cat.split(" ").first(),
                                modifier = Modifier.padding(vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date Recorded") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, type, verification, date)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Reel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditEvidenceDialog(
    evidence: EvidenceEntity,
    onDismiss: () -> Unit,
    onSave: (EvidenceEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf(evidence.title) }
    var type by remember { mutableStateOf(evidence.type) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = Color.White, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Video Reel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel", color = textMuted) }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(evidence.copy(title = title, type = type))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun shareAchievementDetails(context: Context, achievement: AchievementEntity) {
    val text = """
        TALENT GRAPH • ATHLETE OFFICIAL HONOR
        Honor: ${achievement.title}
        Competition: ${achievement.competition}
        Awarding Body: ${achievement.organization}
        Date: ${achievement.date}
        Status: ${achievement.verificationLevel}
        
        Certified via Talent Graph Sports Intelligence Platform.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Athlete Honor: ${achievement.title}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Achievement"))
}

fun shareEvidenceDetails(context: Context, evidence: EvidenceEntity) {
    val text = """
        TALENT GRAPH • VIDEO SCOUTING REEL
        Title: ${evidence.title}
        Category: ${evidence.type}
        Recorded: ${evidence.dateUploaded}
        Status: ${evidence.verificationState}
        
        Available for Scout Evaluation on Talent Graph.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Video Reel: ${evidence.title}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Video Reel"))
}
