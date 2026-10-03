package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.window.Dialog
import com.example.data.ScoutActivityEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScoutActivityModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState
) {
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedFilter by remember { mutableStateOf("All Activity") }
    var showAddDialog by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<ScoutActivityEntity?>(null) }
    var activityToDelete by remember { mutableStateOf<ScoutActivityEntity?>(null) }

    var allowScoutAccess by remember { mutableStateOf(true) }
    var alertOnView by remember { mutableStateOf(true) }

    val totalImpressions = uiState.scoutActivities.size
    val uniqueScouts = remember(uiState.scoutActivities) {
        uiState.scoutActivities.map { it.viewerName }.distinct().size
    }

    val filteredActivities = remember(selectedFilter, uiState.scoutActivities) {
        when (selectedFilter) {
            "Dossier Downloads" -> uiState.scoutActivities.filter { it.action.contains("Download", ignoreCase = true) || it.action.contains("PDF", ignoreCase = true) }
            "Video Reviews" -> uiState.scoutActivities.filter { it.sectionAccessed.contains("Video", ignoreCase = true) || it.action.contains("Video", ignoreCase = true) }
            "Contact Inquiries" -> uiState.scoutActivities.filter { it.action.contains("Contact", ignoreCase = true) || it.action.contains("Inquiry", ignoreCase = true) }
            else -> uiState.scoutActivities
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Scout Radar Overview
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFECFDF5)
                            ) {
                                Text(
                                    text = "GLOBAL SCOUT RADAR",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Scout Activity & Inquiries",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Real-time audit log of certified scouts, agents, and club directors viewing your passport.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFA855F7).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Counters
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScoutStatItem(value = "$totalImpressions", label = "Total Views")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        ScoutStatItem(value = "$uniqueScouts", label = "Unique Scouts")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        ScoutStatItem(value = if (allowScoutAccess) "Active" else "Hidden", label = "Radar Status")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Scout Interaction / Inquiry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Privacy & Broadcast Controls Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("SCOUT PRIVACY & BROADCAST CONTROLS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Allow Verified Scouts to Access Passport", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primaryColor)
                            Text("Permits licensed federation and club scouts to view metrics.", fontSize = 11.sp, color = textMuted)
                        }
                        Switch(checked = allowScoutAccess, onCheckedChange = { allowScoutAccess = it })
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Real-Time View Notifications", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primaryColor)
                            Text("Receive alerts when a scout inspects match highlights.", fontSize = 11.sp, color = textMuted)
                        }
                        Switch(checked = alertOnView, onCheckedChange = { alertOnView = it })
                    }
                }
            }
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("All Activity", "Dossier Downloads", "Video Reviews", "Contact Inquiries")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else primaryColor
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SCOUT ACCESS LOGS (${filteredActivities.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Log", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (filteredActivities.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No scout activity recorded", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
                        Text("When verified scouts or agency representatives inspect your passport or match videos, they will appear here.", color = textMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Register Scout Access", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredActivities, key = { it.id }) { activity ->
                ScoutActivityCard(
                    activity = activity,
                    onEdit = { activityToEdit = activity },
                    onDelete = { activityToDelete = activity },
                    onShare = {
                        shareScoutInteraction(context, activity)
                    }
                )
            }
        }
    }

    // Modal: Add Scout Activity
    if (showAddDialog) {
        AddScoutActivityDialog(
            onDismiss = { showAddDialog = false },
            onSave = { viewer, action, timestamp, section ->
                viewModel.addScoutActivity(viewer, action, timestamp, section)
                showAddDialog = false
            }
        )
    }

    // Modal: Edit Scout Activity
    if (activityToEdit != null) {
        EditScoutActivityDialog(
            activity = activityToEdit!!,
            onDismiss = { activityToEdit = null },
            onSave = { updated ->
                viewModel.updateScoutActivity(updated)
                activityToEdit = null
            }
        )
    }

    // Modal: Delete Confirmation
    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("Delete Scout Activity Log?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this scout log?") },
            confirmButton = {
                Button(
                    onClick = {
                        activityToDelete?.let { viewModel.deleteScoutActivity(it) }
                        activityToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ScoutStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ScoutActivityCard(
    activity: ScoutActivityEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3E8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(activity.viewerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                        Text("${activity.action} • Section: ${activity.sectionAccessed}", fontSize = 12.sp, color = textMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFECFDF5)
                ) {
                    Text(
                        text = "VERIFIED SCOUT",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Timestamp:", fontSize = 11.sp, color = textMuted)
                    Text(activity.timestamp, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Federation Verified Scout Log", fontSize = 11.sp, color = textMuted)

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddScoutActivityDialog(
    onDismiss: () -> Unit,
    onSave: (viewerName: String, action: String, timestamp: String, sectionAccessed: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var viewerName by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("Inspected Full Match Repertoire") }
    var timestamp by remember { mutableStateOf("Just now") }
    var sectionAccessed by remember { mutableStateOf("Performance & Video Reels") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Log Scout Interaction", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = viewerName,
                    onValueChange = { viewerName = it },
                    label = { Text("Scout / Club Official Name") },
                    placeholder = { Text("e.g. Jean-Luc Dubois (OGC Nice Scout)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("Action Performed") },
                    placeholder = { Text("e.g. Downloaded Certified PDF Dossier") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sectionAccessed,
                    onValueChange = { sectionAccessed = it },
                    label = { Text("Section Accessed") },
                    placeholder = { Text("Performance & Video Reels") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = timestamp,
                    onValueChange = { timestamp = it },
                    label = { Text("Timestamp / Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (viewerName.isNotBlank()) {
                                onSave(viewerName, action, timestamp, sectionAccessed)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Log", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditScoutActivityDialog(
    activity: ScoutActivityEntity,
    onDismiss: () -> Unit,
    onSave: (ScoutActivityEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var viewerName by remember { mutableStateOf(activity.viewerName) }
    var action by remember { mutableStateOf(activity.action) }
    var sectionAccessed by remember { mutableStateOf(activity.sectionAccessed) }
    var timestamp by remember { mutableStateOf(activity.timestamp) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Scout Activity Log", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = viewerName, onValueChange = { viewerName = it }, label = { Text("Scout Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = action, onValueChange = { action = it }, label = { Text("Action") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sectionAccessed, onValueChange = { sectionAccessed = it }, label = { Text("Section") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = timestamp, onValueChange = { timestamp = it }, label = { Text("Timestamp") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (viewerName.isNotBlank()) {
                                onSave(activity.copy(viewerName = viewerName, action = action, sectionAccessed = sectionAccessed, timestamp = timestamp))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun shareScoutInteraction(context: Context, activity: ScoutActivityEntity) {
    val text = """
        TALENT GRAPH • SCOUT AUDIT RECORD
        Scout: ${activity.viewerName}
        Action: ${activity.action}
        Section: ${activity.sectionAccessed}
        Date: ${activity.timestamp}
        
        Logged via Talent Graph Global Scout Radar.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Scout View: ${activity.viewerName}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Scout Activity"))
}
