package com.example.ui.auth.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun CoachTeamsContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("My Teams Workspace", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Manage multiple teams, squads, and organizational relationships.", fontSize = 12.sp, color = textMuted)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFCCFBF1)) {
                                Text("ACTIVE TEAM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = secondaryColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Mombasa United FC - U20", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Text("Football (Soccer) • Senior / U23 Academy", fontSize = 12.sp, color = textMuted)
                        }
                        Box(modifier = Modifier.size(44.dp).background(secondaryColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = secondaryColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrainingMetricMiniCard(Modifier.weight(1f), "ROSTER", "${uiState.athletes.size} Athletes")
                        TrainingMetricMiniCard(Modifier.weight(1f), "STAFF", "4 Coaches")
                        TrainingMetricMiniCard(Modifier.weight(1f), "STATUS", "Active")
                    }
                }
            }
        }
    }
}

@Composable
fun CoachAthletesContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Squad Athletes Directory", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("${uiState.athletes.size} registered athletes in active squad", fontSize = 12.sp, color = textMuted)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.athletes) { athlete ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).background(secondaryColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(athlete.name.take(2).uppercase(), fontWeight = FontWeight.Bold, color = secondaryColor, fontSize = 12.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(athlete.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                            if (athlete.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(13.dp))
                            }
                        }
                        Text("${athlete.position} • Rating ${athlete.rating} • ${athlete.clubName}", fontSize = 11.sp, color = textMuted)
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F5F9)) {
                        Text("Active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun CoachAttendanceContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Attendance Tracking & Intelligence", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Session attendance records, trends, and absent patterns.", fontSize = 12.sp, color = textMuted)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TODAY'S SESSION ATTENDANCE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Attendance Rate: 92.8% (26 / 28 Present)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { 0.92f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = secondaryColor)
                }
            }
        }
        items(uiState.athletes.take(6)) { athlete ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(athlete.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primaryColor)
                        Text(athlete.position, fontSize = 11.sp, color = textMuted)
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFECFDF5)) {
                        Text("Present", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CoachPerformanceContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Performance Observations", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Technical, tactical, and physical match assessments.", fontSize = 12.sp, color = textMuted)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Objective Stats vs Coach Observations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Total team match logs recorded: ${uiState.matchLogs.size}", fontSize = 12.sp, color = textMuted)
                }
            }
        }
    }
}

@Composable
fun CoachDevelopmentContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Athlete Development Goals", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Measurable targets, development plans, and reviews.", fontSize = 12.sp, color = textMuted)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Goal: Improve Finishing from inside the box", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Category: Technical • Target: 8.0 • Deadline: Nov 2026", fontSize = 11.sp, color = textMuted)
                }
            }
        }
    }
}

@Composable
fun CoachAnalyticsContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Team Analytics & Intelligence", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Performance trends, workload distribution, and squad readiness.", fontSize = 12.sp, color = textMuted)
            }
        }
    }
}

@Composable
fun CoachAlertsContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Coach Alerts & Signals", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Explainable intelligence signals requiring attention.", fontSize = 12.sp, color = textMuted)
            }
        }
    }
}

@Composable
fun CoachReportsContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Operational Reports", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Generate and export team performance, workload, and match reports.", fontSize = 12.sp, color = textMuted)
            }
        }
    }
}

@Composable
fun CoachMessagesContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Messages & Communications", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Direct athlete messages, team announcements, and staff communication.", fontSize = 12.sp, color = textMuted)
            }
        }
    }
}

@Composable
fun CoachCalendarContent(uiState: TalentUiState) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Unified Schedule & Calendar", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Training sessions, matches, recovery sessions, and meetings.", fontSize = 12.sp, color = textMuted)
            }
        }
    }
}

@Composable
fun CoachSettingsContent(uiState: TalentUiState, onSignOut: () -> Unit) {
    val primaryColor = Color(0xFF0F172A)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Column {
                Text("Coach Profile & Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Manage coach credentials, qualifications, security, and sign out.", fontSize = 12.sp, color = textMuted)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Signed in as Coach", fontSize = 12.sp, color = textMuted)
                    Text(uiState.userName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    Text(uiState.userEmail, fontSize = 13.sp, color = textMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
