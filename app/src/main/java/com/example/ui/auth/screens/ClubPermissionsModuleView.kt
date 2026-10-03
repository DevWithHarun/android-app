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
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ContextualPermissionRole(
    val roleTitle: String,
    val hierarchyLevel: String,
    val teamScope: String,
    val accessibleDomains: List<String>,
    val restrictedDomains: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubPermissionsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    val permissionMatrix = remember {
        listOf(
            ContextualPermissionRole(
                roleTitle = "Club Admin / Executive",
                hierarchyLevel = "Level 1 — Full Organizational Control Plane",
                teamScope = "All Club Squads & Technical Units",
                accessibleDomains = listOf("All Athletes & Teams", "Treasury & Contracts", "Recruitment & Scouts", "Federation Clearances", "Audit Trail"),
                restrictedDomains = listOf("Unrestricted Access")
            ),
            ContextualPermissionRole(
                roleTitle = "Head Coach (U20 Academy)",
                hierarchyLevel = "Level 2 — Squad Technical Authority",
                teamScope = "U20 Youth Team Only",
                accessibleDomains = listOf("U20 Athletes Roster", "U20 Training & Matchday Tactics", "U20 Development IDPs", "U20 Basic Availability"),
                restrictedDomains = listOf("Private Financial Records", "Other Club Teams (Senior/U17)", "Sensitive Legal Contracts", "Confidential Medical Reports")
            ),
            ContextualPermissionRole(
                roleTitle = "Tactical Analyst",
                hierarchyLevel = "Level 3 — Data & Video Intelligence Scope",
                teamScope = "Senior & U23 Assigned Squads",
                accessibleDomains = listOf("Match Telemetry & GPS Logs", "Opposition Video Reports", "Tactical Event Logs", "Performance Ratings"),
                restrictedDomains = listOf("Club Financial Ledger", "Staff Payroll", "Official Clearances Submit")
            ),
            ContextualPermissionRole(
                roleTitle = "Chief Scout",
                hierarchyLevel = "Level 3 — External Discovery Pipeline",
                teamScope = "Western & Coastal Territories",
                accessibleDomains = listOf("Candidate Discovery Pipeline", "Trial Submissions", "Scouting Reports", "Open Positions"),
                restrictedDomains = listOf("Internal Squad Salaries", "Medical History", "Executive Governance")
            )
        )
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEDE9FE),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contextual Permissions & Access Control",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fine-grained Data Sensitivity & Scoped Authority for $activeClubName",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
            }
        }

        // CONTEXTUAL PERMISSION FORMULA BANNER
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TALENT GRAPH CONTEXTUAL PERMISSION FORMULA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = purpleAccent,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Role + Organization + Team + Relationship + Permission + Data Sensitivity",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Prevents broad, leaky permissions. A coach assigned to U20 automatically accesses only U20 records while restricted from private financial or medical files.",
                        fontSize = 11.sp,
                        color = textMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Roles Matrix List
        items(permissionMatrix) { role ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(role.roleTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Text(role.hierarchyLevel, fontSize = 11.sp, color = purpleAccent, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                        Text("Scope: ${role.teamScope}", modifier = Modifier.padding(8.dp), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Allowed
                    Text("Authorized Data Scope:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = successColor)
                    role.accessibleDomains.forEach { domain ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = successColor, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(domain, fontSize = 11.sp, color = Color(0xFF334155))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Restricted
                    Text("Restricted & Gated Data:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = dangerColor)
                    role.restrictedDomains.forEach { domain ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = dangerColor, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(domain, fontSize = 11.sp, color = textMuted)
                        }
                    }
                }
            }
        }
    }
}
