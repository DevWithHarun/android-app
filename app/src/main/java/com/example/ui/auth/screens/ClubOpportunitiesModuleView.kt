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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubOpportunitiesModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedCategory by rememberSaveable { mutableStateOf("All Opportunities") }
    val categories = listOf("All Opportunities", "Trials", "Tournaments", "Scholarships", "Partnerships & Sponsorships")

    var showCreateDialog by remember { mutableStateOf(false) }

    var oppsList by remember {
        mutableStateOf(
            listOf(
                ClubOpportunityModel("opp_1", "", "U20 Defensive Midfielder Official Trial", "Trials", "Defensive Midfielder (DM)", "U20", "2026-10-15", "Open trial for left-footed central midfielder with competitive regional experience.", "Open", 18),
                ClubOpportunityModel("opp_2", "", "East Africa Youth Invitational Cup 2026", "Tournaments", "All Positions", "U17", "2026-11-01", "Regional youth tournament invitation in Nairobi.", "Open", 1),
                ClubOpportunityModel("opp_3", "", "Talent Graph Academy Education Scholarship", "Scholarships", "Forward / Midfielder", "U17 / U20", "2026-10-31", "Full boarding and tuition secondary school scholarship.", "Open", 9),
                ClubOpportunityModel("opp_4", "", "Sporting Apparel & Kit Sponsorship 2027", "Partnerships & Sponsorships", "Club Level", "All", "2026-12-01", "Official corporate sponsor partnership for matchday kits.", "Under Review", 3)
            )
        )
    }

    val filteredList = remember(selectedCategory, oppsList) {
        if (selectedCategory == "All Opportunities") oppsList
        else oppsList.filter { it.type.contains(selectedCategory, ignoreCase = true) }
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
                                    color = Color(0xFFFAF5FF),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Club Opportunities & Trials",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Open Trials, Tournaments, Scholarships & Partnerships for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Opp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Category Filter Chips
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
                categories.forEach { c ->
                    val isSelected = selectedCategory == c
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = c },
                        text = {
                            Text(
                                text = c,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) purpleAccent else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Opportunities List
        items(filteredList) { opp ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE9FE)) {
                                    Text(opp.type.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = purpleAccent)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Age Group: ${opp.ageGroup}", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(opp.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                            Text(
                                text = "${opp.applicantsCount} APPLICANTS",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF047857)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(opp.description, fontSize = 11.sp, color = Color(0xFF334155), lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF8FAFC))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Target: ${opp.targetPosition}", fontSize = 10.sp, color = textMuted)
                        Text("Deadline: ${opp.deadline}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = warningColor)
                    }
                }
            }
        }
    }

    // DIALOG: Create Opportunity
    if (showCreateDialog) {
        var oppTitle by remember { mutableStateOf("") }
        var oppType by remember { mutableStateOf("Trials") }
        var targetPos by remember { mutableStateOf("All Positions") }
        var ageGroup by remember { mutableStateOf("U20") }
        var deadline by remember { mutableStateOf("2026-11-15") }
        var desc by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showCreateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Publish Club Opportunity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showCreateDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = oppTitle, onValueChange = { oppTitle = it }, label = { Text("Opportunity Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Text("Opportunity Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    val types = listOf("Trials", "Tournaments", "Scholarships", "Partnerships & Sponsorships")
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        types.forEach { t ->
                            FilterChip(selected = oppType == t, onClick = { oppType = t }, label = { Text(t, fontSize = 10.sp) })
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = targetPos, onValueChange = { targetPos = it }, label = { Text("Target Position") }, modifier = Modifier.weight(1f), singleLine = true)
                        OutlinedTextField(value = ageGroup, onValueChange = { ageGroup = it }, label = { Text("Age Category") }, modifier = Modifier.weight(1f), singleLine = true)
                    }

                    OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Application Deadline") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description & Requirements") }, modifier = Modifier.fillMaxWidth())

                    Button(
                        onClick = {
                            if (oppTitle.isNotBlank()) {
                                val newOpp = ClubOpportunityModel(
                                    id = "opp_${System.currentTimeMillis()}",
                                    title = oppTitle,
                                    type = oppType,
                                    targetPosition = targetPos,
                                    ageGroup = ageGroup,
                                    deadline = deadline,
                                    description = desc,
                                    status = "Open",
                                    applicantsCount = 0
                                )
                                oppsList = listOf(newOpp) + oppsList
                                showCreateDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Publish Opportunity", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
