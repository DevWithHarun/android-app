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
fun ClubScoutsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var showAssignDialog by remember { mutableStateOf(false) }

    val backendScouts = (clubDashboardState as? ClubDashboardUiState.Success)?.scoutAssignments ?: emptyList()
    val scoutAssignments = backendScouts

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
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Club Scout Force Management",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Firebase Firestore real-time territory assignments for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showAssignDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Assign New Scouting Project", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Active Projects Header
        item {
            Text("Active Scouting Projects & Territories (${scoutAssignments.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        }

        if (scoutAssignments.isEmpty()) {
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
                        Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Scout Assignments Found", fontWeight = FontWeight.Bold, color = primaryColor)
                        Text("Firestore returned no scout assignments. Tap 'Assign New Scouting Project' above to deploy scouts.", fontSize = 12.sp, color = textMuted, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(scoutAssignments) { proj ->
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
                                Text(proj.projectTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("Lead Scout: ${proj.scoutName} • Territory: ${proj.territory}", fontSize = 12.sp, color = textMuted)
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Text(
                                    text = proj.status.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Target Position: ${proj.targetPosition} (Age ${proj.ageRange})", fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.SemiBold)
                        Text("Specialization: ${proj.specialization}", fontSize = 11.5.sp, color = textMuted)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = borderColor)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${proj.athletesDiscovered}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = purpleAccent)
                                Text("Discovered", fontSize = 9.sp, color = textMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${proj.shortlisted}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = primaryColor)
                                Text("Shortlisted", fontSize = 9.sp, color = textMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${proj.trialsInvited}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = tealAccent)
                                Text("Trials Invited", fontSize = 9.sp, color = textMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Deadline: ${proj.deadline}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }

    if (showAssignDialog) {
        AssignScoutProjectDialog(
            onDismiss = { showAssignDialog = false },
            onSave = { scout, title, terr, pos ->
                clubDashboardViewModel.createScoutAssignment(
                    scoutName = scout,
                    projectTitle = title,
                    targetPosition = pos,
                    ageRange = "17–20",
                    territory = terr,
                    specialization = "Specialization in $pos",
                    deadline = "2026-11-30"
                )
                showAssignDialog = false
            }
        )
    }
}

@Composable
fun AssignScoutProjectDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var scoutName by remember { mutableStateOf("Chief Regional Scout") }
    var projectTitle by remember { mutableStateOf("U17 Left Wing Forward") }
    var territory by remember { mutableStateOf("Central & Nairobi Region") }
    var position by remember { mutableStateOf("Winger (LW / RW)") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Assign Scouting Project", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = scoutName, onValueChange = { scoutName = it }, label = { Text("Assigned Scout Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = projectTitle, onValueChange = { projectTitle = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = territory, onValueChange = { territory = it }, label = { Text("Assigned Territory") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Target Position Profile") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (scoutName.isNotBlank() && projectTitle.isNotBlank()) {
                            onSave(scoutName, projectTitle, territory, position)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Deploy Scouting Project to Firestore", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
