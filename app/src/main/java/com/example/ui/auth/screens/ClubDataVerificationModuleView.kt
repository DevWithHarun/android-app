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

data class VerificationLevelItem(
    val levelCode: String,
    val levelName: String,
    val description: String,
    val confidenceRating: String,
    val badgeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDataVerificationModuleView(
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

    val levels = remember {
        listOf(
            VerificationLevelItem("L0", "Self Reported", "Data inputted directly by the athlete without external documentation.", "Confidence: Low (Unverified)", Color(0xFF94A3B8)),
            VerificationLevelItem("L1", "Submitted Evidence", "Athlete uploaded certificate, photo, or fixture sheet pending verification.", "Confidence: Moderate (Evidence Attached)", Color(0xFF2563EB)),
            VerificationLevelItem("L2", "Organization Verified", "Authenticated by licensed Head Coach, Team Manager, or Club Admin with audit trail.", "Confidence: High (Club Authenticated)", purpleAccent),
            VerificationLevelItem("L3", "Official / Authorized Source", "Directly synchronized from National Federation, FIFA TMS, or official competition league system.", "Confidence: Very High (Federation Synced)", tealAccent),
            VerificationLevelItem("L4", "Multiple Independent Sources", "Corroborated across GPS telemetry, referee official match report, and federation roster.", "Confidence: Highest (Multi-Source Consensus)", successColor)
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
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = successColor, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Data Integrity & Verification Layer",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = primaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Talent Graph Multi-Tier Verification & Trust Architecture for $activeClubName",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }
            }
        }

        // 5-LEVEL VERIFICATION MATRIX
        item {
            Text("Verification Level Standards (L0 ➔ L4)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        }

        items(levels) { lvl ->
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
                        shape = RoundedCornerShape(12.dp),
                        color = lvl.badgeColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(lvl.levelCode, fontSize = 16.sp, fontWeight = FontWeight.Black, color = lvl.badgeColor)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(lvl.levelName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(lvl.description, fontSize = 11.sp, color = textMuted, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(lvl.confidenceRating, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = lvl.badgeColor)
                    }
                }
            }
        }
    }
}
