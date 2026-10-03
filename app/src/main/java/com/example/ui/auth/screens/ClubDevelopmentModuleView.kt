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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDevelopmentModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var showCreatePlanDialog by remember { mutableStateOf(false) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val athletes = successState?.athletes ?: emptyList()
    val teams = successState?.teams ?: emptyList()

    val defaultPlans = remember(athletes) {
        listOf(
            ClubDevelopmentPlanModel(
                id = "dev_1",
                athleteName = athletes.getOrNull(0)?.name ?: "John Kamau",
                currentTeam = "U20 Youth Team",
                pathwayTarget = "Senior Team 1st XI",
                progressPercent = 82,
                promotionReadiness = "High (Promotion Ready)",
                primarySkillGoal = "Defensive Transition & Pressing Angles",
                targetPosition = "Attacking Midfielder (CAM)",
                coachMentor = "Head Coach & Academy Director",
                milestonesCount = 5,
                completedMilestones = 4,
                coachAssessment = "Top performer in recent friendlies. Physically robust, tactical IQ ready for Senior 1st team rotation.",
                lastReviewDate = "2026-09-30"
            ),
            ClubDevelopmentPlanModel(
                id = "dev_2",
                athleteName = athletes.getOrNull(1)?.name ?: "Brian Ochieng",
                currentTeam = "U17 Academy",
                pathwayTarget = "U20 Youth Team",
                progressPercent = 68,
                promotionReadiness = "High (Promotion Ready)",
                primarySkillGoal = "Weak Foot Finishing & Aerobic Stamina",
                targetPosition = "Center Forward (ST)",
                coachMentor = "Youth Lead Coach",
                milestonesCount = 4,
                completedMilestones = 3,
                coachAssessment = "Sprint speed clocked at 33.8 km/h. Goal conversion rate is exceptional. Recommended for U20 fast-track.",
                lastReviewDate = "2026-09-28"
            ),
            ClubDevelopmentPlanModel(
                id = "dev_3",
                athleteName = athletes.getOrNull(2)?.name ?: "Kevin Otieno",
                currentTeam = "U23 Reserves",
                pathwayTarget = "Senior Team",
                progressPercent = 75,
                promotionReadiness = "Moderate",
                primarySkillGoal = "1v1 Defensive Duels & Aerial Clearance",
                targetPosition = "Center Back (CB)",
                coachMentor = "Senior Assistant Coach",
                milestonesCount = 5,
                completedMilestones = 3,
                coachAssessment = "Commanding leadership on the pitch. Working on lateral agility and recovery pace.",
                lastReviewDate = "2026-09-25"
            )
        )
    }

    val backendPlans = successState?.developmentPlans ?: emptyList()
    val plans = backendPlans.ifEmpty { defaultPlans }

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
                                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Athlete Development & Academy Pathways",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Individual Development Plans (IDPs), Milestones & Academy Pathway Pipeline for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showCreatePlanDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Individual Development Plan (IDP)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Pathway Flow Showcase Card: U17 -> U20 -> U23 -> Senior -> Pro
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Official Club Academy Pathway", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DevelopmentStep("U17 Academy", "Grassroots", Color(0xFF2563EB))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                        DevelopmentStep("U20 Youth", "Transition", purpleAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                        DevelopmentStep("U23 Reserves", "Pre-Senior", tealAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                        DevelopmentStep("Senior Team", "1st XI", successColor)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(14.dp))
                        DevelopmentStep("Pro Career", "Global", Color(0xFFEA580C))
                    }
                }
            }
        }

        // Active Development Plans Header
        item {
            Text(
                text = "Active Individual Development Plans (${defaultPlans.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )
        }

        // Development Plan Cards
        items(defaultPlans) { plan ->
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
                            Text(plan.athleteName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                            Text("${plan.currentTeam} ➔ Target: ${plan.pathwayTarget}", fontSize = 11.sp, color = purpleAccent, fontWeight = FontWeight.SemiBold)
                        }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = plan.promotionReadiness.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF15803D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Milestones: ${plan.completedMilestones} of ${plan.milestonesCount} Completed", fontSize = 11.sp, color = textMuted)
                        Text("${plan.progressPercent}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { plan.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = purpleAccent,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Focus: ${plan.primarySkillGoal} (${plan.targetPosition})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coach assessment: \"${plan.coachAssessment}\"", fontSize = 11.sp, color = textMuted, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mentor: ${plan.coachMentor} • Reviewed: ${plan.lastReviewDate}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }

    if (showCreatePlanDialog) {
        CreateDevelopmentPlanDialog(
            athletes = athletes,
            teams = teams,
            onDismiss = { showCreatePlanDialog = false },
            onSave = { athleteName, team, target, skill, mentor, notes ->
                clubDashboardViewModel.createDevelopmentPlan(
                    athleteId = "",
                    athleteName = athleteName,
                    currentTeam = team,
                    pathwayTarget = target,
                    primaryGoal = skill,
                    targetPos = "Midfielder",
                    mentor = mentor,
                    coachAssessment = notes
                )
                showCreatePlanDialog = false
            }
        )
    }
}

@Composable
fun DevelopmentStep(step: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
            Text(step, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Text(label, fontSize = 8.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun CreateDevelopmentPlanDialog(
    athletes: List<AthleteEntity>,
    teams: List<ClubTeamModel>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String) -> Unit
) {
    var athleteName by remember { mutableStateOf(athletes.firstOrNull()?.name ?: "Athlete") }
    var currentTeam by remember { mutableStateOf(teams.firstOrNull()?.name ?: "U17 Academy") }
    var pathwayTarget by remember { mutableStateOf("Senior Team") }
    var skillGoal by remember { mutableStateOf("Ball mastery, positioning & tactical transition") }
    var mentor by remember { mutableStateOf("Head Academy Coach") }
    var notes by remember { mutableStateOf("Candidate shows strong aptitude and fast learning trajectory.") }

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
                    Text("Create Individual Development Plan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = athleteName,
                    onValueChange = { athleteName = it },
                    label = { Text("Athlete Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentTeam,
                        onValueChange = { currentTeam = it },
                        label = { Text("Current Squad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pathwayTarget,
                        onValueChange = { pathwayTarget = it },
                        label = { Text("Pathway Target") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = skillGoal,
                    onValueChange = { skillGoal = it },
                    label = { Text("Core Skill & Tactical Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = mentor,
                    onValueChange = { mentor = it },
                    label = { Text("Assigned Coach / Mentor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Coach Assessment & Pathway Milestones") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (athleteName.isNotBlank()) {
                            onSave(athleteName, currentTeam, pathwayTarget, skillGoal, mentor, notes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Save & Publish IDP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
