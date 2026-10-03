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
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubRecruitmentModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val warningColor = Color(0xFFD97706)
    val successColor = Color(0xFF16A34A)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var stageFilter by rememberSaveable { mutableStateOf("All Stages") }
    val stages = listOf("All Stages", "Discovered", "Shortlist", "Scout Eval", "Club Review", "Trial", "Decision", "Offer", "Registered")

    var showCreateRequirementDialog by remember { mutableStateOf(false) }

    val backendCandidates = (clubDashboardState as? ClubDashboardUiState.Success)?.recruitmentPipeline ?: emptyList()
    val candidates = backendCandidates

    val filteredCandidates = remember(stageFilter, candidates) {
        if (stageFilter == "All Stages") candidates
        else candidates.filter { it.stage.equals(stageFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
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
                                    color = Color(0xFFCCFBF1),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PersonSearch, contentDescription = null, tint = tealAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recruitment & Trial Pipeline",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Firebase Firestore real-time talent funnel for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showCreateRequirementDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = tealAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Candidate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pipeline Summary Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RecruitKpiCard("Total Pipeline", "${candidates.size}", primaryColor, Modifier.weight(1f))
                        RecruitKpiCard("Trials Scheduled", "${candidates.count { it.stage == "Trial" }}", tealAccent, Modifier.weight(1f))
                        RecruitKpiCard("Offers Extended", "${candidates.count { it.stage == "Offer" }}", successColor, Modifier.weight(1f))
                        RecruitKpiCard("High Rating (8.5+)", "${candidates.count { it.scoutRating >= 8.5 }}", purpleAccent, Modifier.weight(1f))
                    }
                }
            }
        }

        // Stage Filter Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stages.forEach { stage ->
                    val isSelected = stageFilter == stage
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) primaryColor else Color.White,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { stageFilter = stage }
                    ) {
                        Text(
                            text = stage,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else textMuted,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filteredCandidates.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PersonSearch, contentDescription = null, tint = tealAccent, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Recruitment Candidates Found", fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("Firestore returned no records for this stage. Tap 'Add Candidate' above to register talent.", fontSize = 12.sp, color = textMuted, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(filteredCandidates) { candidate ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(candidate.candidateName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("${candidate.position} • Age ${candidate.age} • ${candidate.currentClub}", fontSize = 12.sp, color = textMuted)
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (candidate.stage) {
                                    "Trial" -> Color(0xFFFEF3C7)
                                    "Offer" -> Color(0xFFDCFCE7)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ) {
                                Text(
                                    text = candidate.stage.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (candidate.stage) {
                                        "Trial" -> Color(0xFFD97706)
                                        "Offer" -> Color(0xFF16A34A)
                                        else -> primaryColor
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(candidate.evaluationNotes, fontSize = 12.sp, color = Color(0xFF334155), lineHeight = 16.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = borderColor)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Scout Rating: ${candidate.scoutRating} / 10", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = purpleAccent)
                                Text("Trial Date: ${candidate.trialDate}", fontSize = 11.sp, color = textMuted)
                            }

                            OutlinedButton(
                                onClick = {
                                    val nextStage = if (candidate.stage == "Discovered") "Shortlist" else "Trial"
                                    clubDashboardViewModel.updateRecruitmentStage(candidate.id, candidate.candidateName, nextStage, "In Progress")
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, tealAccent),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = tealAccent),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Advance Stage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateRequirementDialog) {
        CreateRecruitmentRequirementDialog(
            onDismiss = { showCreateRequirementDialog = false },
            onSave = { name, pos, club, age, foot, notes ->
                clubDashboardViewModel.addRecruitmentCandidate(
                    candidateName = name,
                    position = pos,
                    currentClub = club,
                    age = age.toIntOrNull() ?: 18,
                    foot = foot,
                    notes = notes
                )
                showCreateRequirementDialog = false
            }
        )
    }
}

@Composable
private fun RecruitKpiCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CreateRecruitmentRequirementDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("Brian Otieno") }
    var position by remember { mutableStateOf("Central Midfielder (CM)") }
    var currentClub by remember { mutableStateOf("Kisumu All Stars") }
    var age by remember { mutableStateOf("19") }
    var preferredFoot by remember { mutableStateOf("Right") }
    var notes by remember { mutableStateOf("High passing range and box-to-box stamina.") }

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
                    Text("Register Recruitment Candidate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Candidate Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Position / Role") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = currentClub, onValueChange = { currentClub = it }, label = { Text("Current Club / Academy") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = preferredFoot, onValueChange = { preferredFoot = it }, label = { Text("Preferred Foot") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Scout Evaluation & Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank() && position.isNotBlank()) {
                            onSave(name, position, currentClub, age, preferredFoot, notes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save to Firestore Pipeline", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
