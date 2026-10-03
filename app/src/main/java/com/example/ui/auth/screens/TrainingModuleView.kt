package com.example.ui.auth.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun TrainingModuleView(viewModel: TalentViewModel, uiState: TalentUiState) {
    var sessionType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    Column(modifier = Modifier.fillMaxSize().background(surfaceColor).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Training Sessions & History", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Log drills, conditioning, and training attendance.", fontSize = 12.sp, color = textMuted)
            }
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log Training", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.trainingSessions.isEmpty()) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = cardBg), border = BorderStroke(1.dp, borderColor), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No training sessions logged.", fontWeight = FontWeight.Bold, color = primaryColor)
                    Text("Tap 'Log Training' to record your sessions.", fontSize = 12.sp, color = textMuted)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.trainingSessions) { session ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cardBg), border = BorderStroke(1.dp, borderColor), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(session.sessionType, fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 16.sp)
                            Text("Duration: ${session.durationMins} mins • Intensity: ${session.intensity}", fontSize = 13.sp, color = textMuted)
                            Text("Notes: ${session.coachNotes}", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(24.dp), color = cardBg, border = BorderStroke(1.dp, borderColor), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Log Training Session", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = sessionType, onValueChange = { sessionType = it }, label = { Text("Session Type (e.g. Tactical / Strength)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (minutes)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        if (sessionType.isNotBlank()) {
                            viewModel.addTrainingSession(sessionType, "Today", duration.toIntOrNull() ?: 90, "High", "Completed full session", "Present")
                            showDialog = false
                            sessionType = ""
                            duration = ""
                        }
                    }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Text("Save Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
