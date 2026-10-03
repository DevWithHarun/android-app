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

data class ClubPersonModel(
    val id: String,
    val name: String,
    val roleCategory: String, // Athlete, Coach, Assistant Coach, Scout, Analyst, Medical/Performance, Administrator, Team Manager, Official, Support Staff
    val specificTitle: String,
    val assignedTeam: String,
    val status: String = "Active", // Active, Suspended, Pending Invite, Inactive
    val permissions: String = "Standard Staff",
    val email: String,
    val phone: String,
    val joinedYear: String = "2026",
    val activityCount: Int = 14
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubPeopleModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All People") }
    var showInvitePersonDialog by remember { mutableStateOf(false) }
    var selectedPersonForDetails by remember { mutableStateOf<ClubPersonModel?>(null) }

    val categories = listOf(
        "All People",
        "Coaches",
        "Athletes",
        "Scouts",
        "Analysts",
        "Medical / Physio",
        "Administrators",
        "Team Managers",
        "Support Staff"
    )

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val athletes = successState?.athletes ?: emptyList()
    val staff = successState?.staff ?: emptyList()

    // Aggregate all people inside the organization
    var peopleList by remember(athletes, staff) {
        mutableStateOf(
            listOf(
                ClubPersonModel("p1", "Coach Francis Kimanzi", "Coaches", "Head Coach", "Senior Team 1st XI", "Active", "Full Technical Authority", "f.kimanzi@talentgraph.club", "+254 711 234 567", "2024", 82),
                ClubPersonModel("p2", "Salim Ali", "Coaches", "Assistant Coach", "Senior Team 1st XI", "Active", "Tactical & Training Edit", "s.ali@talentgraph.club", "+254 722 345 678", "2025", 54),
                ClubPersonModel("p3", "David Kimani", "Analysts", "Senior Tactical Analyst", "Senior & U23", "Active", "Performance & Video Intel", "d.kimani@talentgraph.club", "+254 733 456 789", "2025", 68),
                ClubPersonModel("p4", "Patrick Mutua", "Scouts", "Chief Talent Scout", "Western & Coast Region", "Active", "Recruitment Submission", "p.mutua@talentgraph.club", "+254 744 567 890", "2026", 45),
                ClubPersonModel("p5", "Dr. George Otieno", "Medical / Physio", "Head Team Physician", "All Squads", "Active", "Medical Welfare & Clearance", "g.otieno@talentgraph.club", "+254 755 678 901", "2024", 94),
                ClubPersonModel("p6", "Grace Achieng", "Medical / Physio", "Performance & Load Specialist", "Academy & Senior", "Active", "Workload & ACWR Access", "g.achieng@talentgraph.club", "+254 766 789 012", "2026", 39),
                ClubPersonModel("p7", "John Kamau", "Athletes", "Attacking Midfielder", "Senior Team / U20", "Active", "Athlete Profile", "j.kamau@talentgraph.club", "+254 777 890 123", "2025", 31),
                ClubPersonModel("p8", "Brian Ochieng", "Athletes", "Striker", "U20 Youth Team", "Active", "Athlete Profile", "b.ochieng@talentgraph.club", "+254 788 901 234", "2026", 19),
                ClubPersonModel("p9", "Alice Wambui", "Team Managers", "Operations & Logistics Manager", "Senior Team", "Active", "Travel & Matchday Admin", "a.wambui@talentgraph.club", "+254 799 012 345", "2024", 112)
            )
        )
    }

    val filteredPeople = remember(searchQuery, selectedCategory, peopleList) {
        peopleList.filter { person ->
            val matchQuery = searchQuery.isBlank() || person.name.contains(searchQuery, ignoreCase = true) || person.specificTitle.contains(searchQuery, ignoreCase = true) || person.assignedTeam.contains(searchQuery, ignoreCase = true)
            val matchCategory = when (selectedCategory) {
                "All People" -> true
                "Coaches" -> person.roleCategory == "Coaches"
                "Athletes" -> person.roleCategory == "Athletes"
                "Scouts" -> person.roleCategory == "Scouts"
                "Analysts" -> person.roleCategory == "Analysts"
                "Medical / Physio" -> person.roleCategory == "Medical / Physio"
                "Administrators" -> person.roleCategory == "Administrators"
                "Team Managers" -> person.roleCategory == "Team Managers"
                "Support Staff" -> person.roleCategory == "Support Staff"
                else -> true
            }
            matchQuery && matchCategory
        }
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
                                    color = Color(0xFFEDE9FE),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "People & Human Resources",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Organizational HR Layer & Access Directory for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showInvitePersonDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Invite Person", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name, role title, or team assignment...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }

        // Category Filter Horizontal Scroll
        item {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = purpleAccent,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) purpleAccent else textMuted
                            )
                        }
                    )
                }
            }
        }

        // People Directory Items
        items(filteredPeople) { person ->
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = when (person.roleCategory) {
                                    "Coaches" -> Color(0xFFEFF6FF)
                                    "Scouts" -> Color(0xFFFFFBEB)
                                    "Analysts" -> Color(0xFFECFDF5)
                                    "Medical / Physio" -> Color(0xFFFAF5FF)
                                    else -> Color(0xFFF1F5F9)
                                },
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = person.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = when (person.roleCategory) {
                                            "Coaches" -> Color(0xFF2563EB)
                                            "Scouts" -> Color(0xFFD97706)
                                            "Analysts" -> Color(0xFF059669)
                                            "Medical / Physio" -> purpleAccent
                                            else -> primaryColor
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(person.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("${person.specificTitle} • ${person.assignedTeam}", fontSize = 11.sp, color = textMuted)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (person.status == "Active") Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = person.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (person.status == "Active") Color(0xFF047857) else Color(0xFFDC2626)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF8FAFC))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Permissions: ${person.permissions}", fontSize = 10.sp, color = textMuted)
                        Text("Logged actions: ${person.activityCount}", fontSize = 10.sp, color = purpleAccent, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedPersonForDetails = person },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Manage Access", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                peopleList = peopleList.map {
                                    if (it.id == person.id) it.copy(status = if (it.status == "Active") "Suspended" else "Active") else it
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (person.status == "Active") dangerColor else successColor
                            ),
                            modifier = Modifier.weight(1f).height(34.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text(if (person.status == "Active") "Suspend" else "Activate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Invite Person
    if (showInvitePersonDialog) {
        var inviteName by remember { mutableStateOf("") }
        var inviteEmail by remember { mutableStateOf("") }
        var inviteRole by remember { mutableStateOf("Coaches") }
        var inviteTitle by remember { mutableStateOf("Assistant Coach") }
        var inviteTeam by remember { mutableStateOf("Senior Team 1st XI") }

        Dialog(onDismissRequest = { showInvitePersonDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Invite Person / Staff", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showInvitePersonDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = inviteName, onValueChange = { inviteName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = inviteEmail, onValueChange = { inviteEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Text("Role Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    val roles = listOf("Coaches", "Scouts", "Analysts", "Medical / Physio", "Team Managers", "Support Staff")
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        roles.forEach { r ->
                            FilterChip(selected = inviteRole == r, onClick = { inviteRole = r }, label = { Text(r, fontSize = 10.sp) })
                        }
                    }

                    OutlinedTextField(value = inviteTitle, onValueChange = { inviteTitle = it }, label = { Text("Specific Position Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = inviteTeam, onValueChange = { inviteTeam = it }, label = { Text("Assigned Team / Unit") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            if (inviteName.isNotBlank()) {
                                val newPerson = ClubPersonModel(
                                    id = "p_${System.currentTimeMillis()}",
                                    name = inviteName,
                                    roleCategory = inviteRole,
                                    specificTitle = inviteTitle,
                                    assignedTeam = inviteTeam,
                                    email = inviteEmail,
                                    phone = "+254 700 000 000"
                                )
                                peopleList = listOf(newPerson) + peopleList
                                showInvitePersonDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Send Official Invitation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG: Manage Access Details
    if (selectedPersonForDetails != null) {
        val person = selectedPersonForDetails!!
        Dialog(onDismissRequest = { selectedPersonForDetails = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("HR Relationship & Permissions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { selectedPersonForDetails = null }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    Text("Member: ${person.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = purpleAccent)
                    Text("Role: ${person.specificTitle} (${person.roleCategory})", fontSize = 12.sp, color = primaryColor)
                    Text("Team Scope: ${person.assignedTeam}", fontSize = 12.sp, color = textMuted)
                    Text("Permissions: ${person.permissions}", fontSize = 12.sp, color = textMuted)
                    Text("Contact: ${person.email} • ${person.phone}", fontSize = 11.sp, color = textMuted)
                    Text("Joined Organization: ${person.joinedYear} • Total Actions Logged: ${person.activityCount}", fontSize = 11.sp, color = textMuted)

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { selectedPersonForDetails = null },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
