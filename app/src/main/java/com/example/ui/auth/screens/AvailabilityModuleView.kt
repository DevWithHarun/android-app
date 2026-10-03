package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.data.AvailabilityEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AvailabilityModuleView(
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

    var selectedFilter by remember { mutableStateOf("All Records") }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<AvailabilityEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<AvailabilityEntity?>(null) }

    // Current latest availability status
    val latestStatus = uiState.availabilityRecords.firstOrNull()
    val isAvailable = latestStatus == null || latestStatus.status.contains("Available", ignoreCase = true) || latestStatus.status.contains("Fit", ignoreCase = true)

    val filteredList = remember(selectedFilter, uiState.availabilityRecords) {
        when (selectedFilter) {
            "Available / Cleared" -> uiState.availabilityRecords.filter { it.status.contains("Available", ignoreCase = true) || it.status.contains("Fit", ignoreCase = true) }
            "Injured / Rehab" -> uiState.availabilityRecords.filter { it.status.contains("Injured", ignoreCase = true) || it.status.contains("Rehab", ignoreCase = true) }
            "Suspended / Ineligible" -> uiState.availabilityRecords.filter { it.status.contains("Suspended", ignoreCase = true) || it.status.contains("Leave", ignoreCase = true) }
            else -> uiState.availabilityRecords
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
        // Hero Card: Current Real-Time Availability
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, if (isAvailable) Color(0xFF10B981) else Color(0xFFEF4444)),
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
                                color = if (isAvailable) Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                            ) {
                                Text(
                                    text = if (isAvailable) "SELECTION CLEARED • FULLY FIT" else "RESTRICTED AVAILABILITY",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAvailable) Color(0xFF047857) else Color(0xFFDC2626)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = latestStatus?.status ?: "Available for Selection",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (latestStatus != null) "Return to Play: ${latestStatus.expectedReturnDate.ifBlank { "Immediate" }} • ${latestStatus.reason.ifBlank { "100% Medical clearance passed" }}" else "No active injuries or restrictions reported.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (isAvailable) Color(0xFFECFDF5) else Color(0xFFFEE2E2), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAvailable) Icons.Default.EventAvailable else Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = if (isAvailable) Color(0xFF047857) else Color(0xFFDC2626),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showUpdateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f).height(44.dp)
                        ) {
                            Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Update Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (!isAvailable) {
                            Button(
                                onClick = {
                                    viewModel.addAvailability(
                                        status = "Available for Selection",
                                        returnDate = "Immediate",
                                        reason = "Medical staff cleared for matchday selection"
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857), contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1.2f).height(44.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark Available", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                shareAvailabilityStatus(context, latestStatus?.status ?: "Available for Selection", latestStatus?.expectedReturnDate ?: "Immediate", latestStatus?.reason ?: "Cleared for play")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).height(44.dp)
                        ) {
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
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
                val filters = listOf("All Records", "Available / Cleared", "Injured / Rehab", "Suspended / Ineligible")
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
                    text = "STATUS AUDIT & MEDICAL LOGS (${filteredList.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showUpdateDialog = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Entry", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of availability logs
        if (filteredList.isEmpty()) {
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
                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No availability logs found", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
                        Text("Log status updates whenever you are fit, in rehab, or sidelined.", color = textMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showUpdateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Record Status Update", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { item ->
                AvailabilityItemCard(
                    item = item,
                    onEdit = { itemToEdit = item },
                    onDelete = { itemToDelete = item },
                    onShare = {
                        shareAvailabilityStatus(context, item.status, item.expectedReturnDate, item.reason)
                    }
                )
            }
        }
    }

    // Modal: Update Status
    if (showUpdateDialog) {
        UpdateAvailabilityDialog(
            onDismiss = { showUpdateDialog = false },
            onSave = { status, returnDate, reason ->
                viewModel.addAvailability(status, returnDate, reason)
                showUpdateDialog = false
            }
        )
    }

    // Modal: Edit Status Log
    if (itemToEdit != null) {
        EditAvailabilityDialog(
            item = itemToEdit!!,
            onDismiss = { itemToEdit = null },
            onSave = { updated ->
                viewModel.updateAvailability(updated)
                itemToEdit = null
            }
        )
    }

    // Modal: Delete Confirmation
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Status Log?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this availability record?") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteAvailability(it) }
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AvailabilityItemCard(
    item: AvailabilityEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val isAvailable = item.status.contains("Available", ignoreCase = true) || item.status.contains("Fit", ignoreCase = true)
    val dateFormatted = remember(item.timestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.timestamp))
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.status, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Logged on $dateFormatted • Return: ${item.expectedReturnDate.ifBlank { "Immediate" }}",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAvailable) Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = if (isAvailable) "CLEARED" else "SIDELINED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAvailable) Color(0xFF047857) else Color(0xFFDC2626)
                    )
                }
            }

            if (item.reason.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Diagnosis / Notes: ${item.reason}",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Verified Athlete Passport Status", fontSize = 11.sp, color = textMuted)

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
fun UpdateAvailabilityDialog(
    onDismiss: () -> Unit,
    onSave: (status: String, returnDate: String, reason: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var status by remember { mutableStateOf("Available for Selection") }
    var returnDate by remember { mutableStateOf("Immediate") }
    var reason by remember { mutableStateOf("Cleared by medical staff for full match play") }

    val presetStatuses = listOf(
        "Available for Selection",
        "Injured (Rehab)",
        "Suspended (Cards)",
        "Personal Leave",
        "Contract Negotiations"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Update Availability Status", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                Text("Select Official Status", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    presetStatuses.forEach { item ->
                        val isSel = status == item
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth().clickable {
                                status = item
                                returnDate = if (item.contains("Available")) "Immediate" else "2 Weeks"
                            }
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else primaryColor
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    label = { Text("Expected Return to Play") },
                    placeholder = { Text("e.g. Immediate or 15 Nov 2026") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Medical / Selection Notes") },
                    placeholder = { Text("e.g. Full squad training resumed") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (status.isNotBlank()) {
                                onSave(status, returnDate, reason)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Status", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditAvailabilityDialog(
    item: AvailabilityEntity,
    onDismiss: () -> Unit,
    onSave: (AvailabilityEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var status by remember { mutableStateOf(item.status) }
    var returnDate by remember { mutableStateOf(item.expectedReturnDate) }
    var reason by remember { mutableStateOf(item.reason) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Status Record", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    label = { Text("Expected Return Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason / Diagnosis Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (status.isNotBlank()) {
                                onSave(item.copy(status = status, expectedReturnDate = returnDate, reason = reason))
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

fun shareAvailabilityStatus(context: Context, status: String, returnDate: String, reason: String) {
    val text = """
        TALENT GRAPH • OFFICIAL ATHLETE AVAILABILITY DOSSIER
        Status: $status
        Expected Return to Play: $returnDate
        Medical / Selection Notes: $reason
        
        Certified via Talent Graph Sports Intelligence Platform.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Athlete Availability: $status")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Availability Status"))
}
