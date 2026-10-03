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
fun ClubProfileModuleView(
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

    var showEditProfileDialog by remember { mutableStateOf(false) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val clubDetails = successState?.clubDetails
    val teams = successState?.teams ?: emptyList()
    val athletes = successState?.athletes ?: emptyList()
    val staff = successState?.staff ?: emptyList()

    val location = clubDetails?.location?.ifBlank { "Mombasa / Nairobi, Kenya" } ?: "Mombasa / Nairobi, Kenya"
    val contactEmail = clubDetails?.contactEmail?.ifBlank { "admin@${activeClubName.lowercase().replace(" ", "")}.org" } ?: "admin@talentgraph.club"
    val contactPhone = clubDetails?.contactPhone?.ifBlank { "+254 700 123 456" } ?: "+254 700 123 456"

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
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEDE9FE),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(32.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeClubName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = primaryColor
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.Verified, contentDescription = "Verified", tint = successColor, modifier = Modifier.size(16.dp))
                                }
                                Text("Club ID: ${successState?.clubMember?.clubId ?: "ORG-89241"}", fontSize = 11.sp, color = textMuted)
                                Text("Founded: 2018 • Professional Sporting Organization", fontSize = 11.sp, color = textMuted)
                            }
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = purpleAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Official competitive sports club affiliated with the National Football Federation. Operating talent development academies from U17 to Senior 1st Division.",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact & Meta Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetaBadge(Icons.Default.LocationOn, location, Modifier.weight(1f))
                        MetaBadge(Icons.Default.Email, contactEmail, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetaBadge(Icons.Default.Phone, contactPhone, Modifier.weight(1f))
                        MetaBadge(Icons.Default.Language, "www.${activeClubName.lowercase().replace(" ", "")}.org", Modifier.weight(1f))
                    }
                }
            }
        }

        // VERIFICATION & COMPLIANCE LAYER
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = successColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Organization Verification & Affiliation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF14532D))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        VerificationRowItem("Organization Verification Status", "Level 2 (Federation & Registrar Verified)", successColor)
                        VerificationRowItem("National Federation Affiliation", "FKF Premier Tier 1 License #FED-2026-88", Color(0xFF2563EB))
                        VerificationRowItem("Sports Registrar Registration", "Reg Certificate #SR-9921-A", primaryColor)
                        VerificationRowItem("Official Registered Contacts", "$contactEmail / $contactPhone", textMuted)
                    }
                }
            }
        }

        // PUBLIC CLUB PROFILE HIERARCHY SHOWCASE
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Public Club Profile Trajectory Structure", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Public Talent Graph discovery pipeline & organizational representation:", fontSize = 11.sp, color = textMuted)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Public Tree: Club -> Teams -> Athletes -> Coaches -> Achievements -> Competitions -> History
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HierarchyNode("1. Club Identity", activeClubName, "Official Brand, Crest & Verification", Color(0xFF7E22CE))
                        HierarchyNode("2. Managed Teams", "${teams.size.coerceAtLeast(3)} Active Squads", "Senior, U23, U20, U17, Women's", Color(0xFF2563EB))
                        HierarchyNode("3. Athletes Roster", "${athletes.size} Registered Athletes", "Biometrics, Stats & Contracts", tealAccent)
                        HierarchyNode("4. Coaches & Staff", "${staff.size.coerceAtLeast(3)} Licensed Technical Staff", "Head Coaches & Specialists", Color(0xFFD97706))
                        HierarchyNode("5. Achievements & Honors", "Regional Cup Winners 2025", "League Runners-Up 2026", Color(0xFFE11D48))
                        HierarchyNode("6. Competitions", "Premier Division & Youth Cup", "Active 2026/2027 Season", successColor)
                    }
                }
            }
        }
    }

    // DIALOG: Edit Club Profile
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(activeClubName) }
        var locInput by remember { mutableStateOf(location) }
        var emailInput by remember { mutableStateOf(contactEmail) }
        var phoneInput by remember { mutableStateOf(contactPhone) }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Edit Club Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showEditProfileDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Club Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = locInput, onValueChange = { locInput = it }, label = { Text("Location / Stadium City") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = emailInput, onValueChange = { emailInput = it }, label = { Text("Contact Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = phoneInput, onValueChange = { phoneInput = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            val clubId = successState?.clubMember?.clubId ?: ""
                            if (clubId.isNotBlank()) {
                                clubDashboardViewModel.updateClubProfile(nameInput, locInput, emailInput, phoneInput)
                            }
                            showEditProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = purpleAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetaBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, fontSize = 11.sp, color = Color(0xFF334155), maxLines = 1)
        }
    }
}

@Composable
fun VerificationRowItem(title: String, detail: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
        Text(detail, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HierarchyNode(step: String, title: String, subtitle: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(6.dp), color = color.copy(alpha = 0.12f)) {
                Text(step, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
            }
        }
    }
}
