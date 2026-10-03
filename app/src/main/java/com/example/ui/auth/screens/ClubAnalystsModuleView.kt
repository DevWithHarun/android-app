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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAnalystsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var showCreateProjectDialog by remember { mutableStateOf(false) }

    val defaultAnalystProjects = remember {
        listOf(
            ClubAnalystProjectModel(
                id = "an_1",
                analystName = "David Kimani (Senior Tactical Analyst)",
                projectTitle = "Opponent Tactical Breakdown & Set Pieces",
                category = "Matches & Opposition",
                targetTeam = "Senior Team 1st XI",
                competition = "National Premier League",
                status = "Completed",
                keyFindings = "Identified vulnerability on opponent left-side transitional recovery; recommended 4-3-3 high pressing overload on midfield turnovers.",
                deliveryDate = "2026-09-30"
            ),
            ClubAnalystProjectModel(
                id = "an_2",
                analystName = "Grace Achieng (Performance & Workload Analyst)",
                projectTitle = "ACWR Workload & High-Speed Running Audit",
                category = "Workload & Biometrics",
                targetTeam = "Senior & U20 Squads",
                competition = "All Fixtures",
                status = "Completed",
                keyFindings = "Sprint meters increased +8% over the last 4 matches. All squad members within safe chronic load thresholds.",
                deliveryDate = "2026-09-28"
            ),
            ClubAnalystProjectModel(
                id = "an_3",
                analystName = "David Kimani",
                projectTitle = "Academy Promotion Readiness Index",
                category = "Development",
                targetTeam = "U17 & U20 Academy",
                competition = "Youth Championship",
                status = "In Progress",
                keyFindings = "Comparative analysis of 12 academy prospects against first-team performance benchmark metrics.",
                deliveryDate = "2026-10-05"
            )
        )
    }

    val backendProjects = (clubDashboardState as? ClubDashboardUiState.Success)?.analystProjects ?: emptyList()
    val analystProjects = backendProjects.ifEmpty { defaultAnalystProjects }

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
                                        Icon(Icons.Default.Analytics, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Club Tactical & Analytical Force",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tactical dossiers, match preparation & workload telemetry for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showCreateProjectDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Commission Tactical Analysis Project", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Analytical Specialization Domains
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Analytical Operational Scope", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Matches", "Training", "Athletes", "Teams", "Competitions", "Development", "Workload", "Availability").forEach { domain ->
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F5F9)) {
                                Text(domain, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
                            }
                        }
                    }
                }
            }
        }

        // Active Projects
        item {
            Text("Analytical Reports & Project Dossiers (${analystProjects.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        }

        items(analystProjects) { proj ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(proj.projectTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                            Text("Analyst: ${proj.analystName}", fontSize = 11.sp, color = purpleAccent, fontWeight = FontWeight.SemiBold)
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (proj.status == "Completed") Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = proj.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (proj.status == "Completed") Color(0xFF15803D) else Color(0xFFB45309)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scope: ${proj.category} • Target: ${proj.targetTeam} (${proj.competition})", fontSize = 11.sp, color = textMuted)

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Key Tactical Findings:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(proj.keyFindings, fontSize = 11.sp, color = Color(0xFF334155))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Delivered / Updated: ${proj.deliveryDate}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }

    if (showCreateProjectDialog) {
        CreateAnalystProjectDialog(
            onDismiss = { showCreateProjectDialog = false },
            onSave = { analyst, title, category, notes ->
                clubDashboardViewModel.createAnalystProject(
                    analystName = analyst,
                    projectTitle = title,
                    category = category,
                    targetTeam = "Senior Team 1st XI",
                    competition = "National Premier League",
                    keyFindings = notes,
                    deliveryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )
                showCreateProjectDialog = false
            }
        )
    }
}

@Composable
fun CreateAnalystProjectDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var analystName by remember { mutableStateOf("David Kimani") }
    var title by remember { mutableStateOf("Next Opponent Set Piece Audit") }
    var category by remember { mutableStateOf("Matches") }
    var notes by remember { mutableStateOf("Analyze defensive set-piece positioning and corner delivery patterns.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Commission Analysis Project", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = analystName, onValueChange = { analystName = it }, label = { Text("Assigned Analyst Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Analysis Project Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Scope (Matches, Workload, Training)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Project Brief & Tactical Objectives") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (analystName.isNotBlank() && title.isNotBlank()) {
                            onSave(analystName, title, category, notes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Commission Project", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
