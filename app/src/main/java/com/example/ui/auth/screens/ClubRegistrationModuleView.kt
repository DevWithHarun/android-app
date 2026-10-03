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
fun ClubRegistrationModuleView(
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

    var selectedFilter by rememberSaveable { mutableStateOf("All Registrations") }
    val filters = listOf("All Registrations", "Verified Clearances", "Pending Federation Review", "Expiring Soon", "Team Registrations")

    var showNewClearanceDialog by remember { mutableStateOf(false) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val rawRegistrations = successState?.registrations ?: emptyList()

    var regList by remember(rawRegistrations) {
        mutableStateOf(
            if (rawRegistrations.isNotEmpty()) rawRegistrations
            else listOf(
                ClubRegistrationModel("reg_1", "", "John Kamau", "FKF-PL-2026-0914", "National Premier League", "Verified", "2026-12-31", "National Football Federation"),
                ClubRegistrationModel("reg_2", "", "Brian Ochieng", "FKF-YC-2026-4412", "National Youth Cup U20", "Verified", "2026-12-31", "Youth League Committee"),
                ClubRegistrationModel("reg_3", "", "Kevin Otieno", "FKF-CL-2026-8819", "Reserve League Division 1", "Pending", "2026-10-15", "Federation Clearance Office"),
                ClubRegistrationModel("reg_4", "", "Senior Team Roster (22 Athletes)", "TEAM-FKF-2026-PL", "Premier League 2026/27", "Verified", "2027-06-30", "Federation Competition Dept"),
                ClubRegistrationModel("reg_5", "", "Dennis Kiprotich", "FKF-AC-2026-1102", "Academy Championship U17", "Expiring", "2026-10-08", "Regional Association")
            )
        )
    }

    val filteredList = remember(selectedFilter, regList) {
        when (selectedFilter) {
            "Verified Clearances" -> regList.filter { it.status.equals("Verified", ignoreCase = true) }
            "Pending Federation Review" -> regList.filter { it.status.equals("Pending", ignoreCase = true) }
            "Expiring Soon" -> regList.filter { it.status.equals("Expiring", ignoreCase = true) }
            "Team Registrations" -> regList.filter { it.athleteName.contains("Team", ignoreCase = true) }
            else -> regList
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
                                    color = Color(0xFFF3E8FF),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AppRegistration, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Federation Clearances & Registrations",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Official Competition Eligibility & License Vault for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showNewClearanceDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Clearance", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5-STEP VERIFICATION WORKFLOW BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FEDERATION CLEARANCE VERIFICATION PIPELINE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF581C87),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepBadge("1. Submits", Color(0xFF2563EB))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(12.dp))
                        StepBadge("2. Club Reviews", purpleAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(12.dp))
                        StepBadge("3. Club Verifies", tealAccent)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(12.dp))
                        StepBadge("4. Federation", Color(0xFFD97706))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = textMuted, modifier = Modifier.size(12.dp))
                        StepBadge("5. Verified Record", successColor)
                    }
                }
            }
        }

        // Filter Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = purpleAccent,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                filters.forEach { f ->
                    val isSelected = selectedFilter == f
                    Tab(
                        selected = isSelected,
                        onClick = { selectedFilter = f },
                        text = {
                            Text(
                                text = f,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) purpleAccent else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Registration Records List
        items(filteredList) { reg ->
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
                            Surface(shape = CircleShape, color = Color(0xFFEFF6FF), modifier = Modifier.size(38.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(reg.athleteName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("License: ${reg.registrationNumber}", fontSize = 11.sp, color = textMuted)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (reg.status) {
                                "Verified" -> Color(0xFFECFDF5)
                                "Pending" -> Color(0xFFFEF3C7)
                                "Expiring" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFF1F5F9)
                            }
                        ) {
                            Text(
                                text = reg.status.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = when (reg.status) {
                                    "Verified" -> Color(0xFF047857)
                                    "Pending" -> Color(0xFFB45309)
                                    "Expiring" -> Color(0xFFDC2626)
                                    else -> textMuted
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF8FAFC))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Competition: ${reg.competition}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                        Text("Expiry: ${reg.expiryDate}", fontSize = 10.sp, color = if (reg.status == "Expiring") dangerColor else textMuted)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Verified by: ${reg.verifiedBy}", fontSize = 10.sp, color = textMuted)
                }
            }
        }
    }

    // DIALOG: New Clearance / License Submission
    if (showNewClearanceDialog) {
        var entityName by remember { mutableStateOf("") }
        var regNum by remember { mutableStateOf("FKF-${System.currentTimeMillis().toString().takeLast(6)}") }
        var compName by remember { mutableStateOf("National Premier League") }
        var expiryInput by remember { mutableStateOf("2026-12-31") }

        Dialog(onDismissRequest = { showNewClearanceDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Submit Official Clearance", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showNewClearanceDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = entityName, onValueChange = { entityName = it }, label = { Text("Athlete / Squad Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = regNum, onValueChange = { regNum = it }, label = { Text("Federation License #") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = compName, onValueChange = { compName = it }, label = { Text("Competition League") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = expiryInput, onValueChange = { expiryInput = it }, label = { Text("Registration Expiry Date") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            if (entityName.isNotBlank()) {
                                val newEntry = ClubRegistrationModel(
                                    id = "reg_${System.currentTimeMillis()}",
                                    athleteName = entityName,
                                    registrationNumber = regNum,
                                    competition = compName,
                                    status = "Verified",
                                    expiryDate = expiryInput,
                                    verifiedBy = "National Football Federation"
                                )
                                regList = listOf(newEntry) + regList
                                showNewClearanceDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Submit & Verify License", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StepBadge(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
        Text(label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
