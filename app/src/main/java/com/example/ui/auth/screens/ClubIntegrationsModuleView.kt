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
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ClubIntegrationSystem(
    val id: String,
    val name: String,
    val category: String, // Federation, Competition, Payment, Video/Telemetry, Wearables/GPS, Insurance
    val status: String, // Connected, Configured, Available
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubIntegrationsModuleView(
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

    val systems = remember {
        listOf(
            ClubIntegrationSystem("sys_1", "National Football Federation (FKF Portal)", "Federation Systems", "Connected", "Real-time player digital licensing and registration validation.", Icons.Default.VerifiedUser),
            ClubIntegrationSystem("sys_2", "Premier League Official Competition CMS", "Competition Systems", "Connected", "Matchday scores, lineups, official referee cards, and standings sync.", Icons.Default.SportsScore),
            ClubIntegrationSystem("sys_3", "M-Pesa / Bank Gateway Payments", "Payment Systems", "Connected", "Automated dues collection, registration payments, and staff payroll disbursement.", Icons.Default.AccountBalance),
            ClubIntegrationSystem("sys_4", "GPS Wearables & Catapult Telemetry", "Wearables & GPS", "Connected", "High-speed running distance, player load, and sprint biometrics ingestion.", Icons.Default.Watch),
            ClubIntegrationSystem("sys_5", "Hudl / Wyscout Video & Analytics Platform", "Video & Analytics", "Configured", "Opposition scouting, tactical video cutups, and player event tags.", Icons.Default.VideoCameraBack),
            ClubIntegrationSystem("sys_6", "Sports Underwriters Stage D Insurance Portal", "Insurance Systems", "Configured", "Automated claim submission from verified match injury telemetry.", Icons.Default.Shield)
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
                            color = Color(0xFFF3E8FF),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Sensors, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "External Integrations & Telemetry",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Talent Graph Infrastructure Connectors & API Gateways for $activeClubName",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
            }
        }

        items(systems) { sys ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(sys.icon, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sys.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFECFDF5)) {
                                Text(sys.status.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = successColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(sys.description, fontSize = 11.sp, color = textMuted, lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}
