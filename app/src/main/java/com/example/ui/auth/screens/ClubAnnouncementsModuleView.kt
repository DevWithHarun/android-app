package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ClubAnnouncementItem(
    val id: String,
    val title: String,
    val targetScope: String, // "Whole Club" or Team Name
    val content: String,
    val timestamp: String,
    val author: String,
    val pushSent: Boolean,
    val readCount: Int,
    val totalRecipients: Int,
    val attachments: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAnnouncementsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val brandPurple = Color(0xFF6D28D9)
    val inkColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)
    val successColor = Color(0xFF16A34A)

    var showNewAnnouncementDialog by remember { mutableStateOf(false) }

    val teams = (clubDashboardState as? ClubDashboardUiState.Success)?.teams ?: emptyList()
    val athletesCount = (clubDashboardState as? ClubDashboardUiState.Success)?.athletes?.size ?: 24

    var announcementsList by remember {
        mutableStateOf(
            listOf(
                ClubAnnouncementItem(
                    id = "ann_1",
                    title = "Pre-Season Training Schedule & Kit Distribution",
                    targetScope = "Whole Club",
                    content = "Official pre-season training begins this Friday at 07:00 AM at the Main Training Ground. All senior and academy players must collect their new season kit from the equipment room by Thursday evening.",
                    timestamp = "Today at 08:30 AM",
                    author = "Head Coach & Admin",
                    pushSent = true,
                    readCount = (athletesCount * 0.85).toInt().coerceAtLeast(18),
                    totalRecipients = athletesCount + 8,
                    attachments = listOf("Training_Schedule_2026.pdf", "Equipment_Checklist.pdf")
                ),
                ClubAnnouncementItem(
                    id = "ann_2",
                    title = "Medical & Clearance Document Expiry Reminder",
                    targetScope = "Whole Club",
                    content = "Please ensure all government ID cards and federation player passports are uploaded to the Document Vault. Players with expired clearances will not be registered for the upcoming league fixture.",
                    timestamp = "Yesterday at 04:15 PM",
                    author = "Club Registrar",
                    pushSent = true,
                    readCount = (athletesCount * 0.92).toInt().coerceAtLeast(20),
                    totalRecipients = athletesCount + 8,
                    attachments = listOf("Compliance_Guidelines.pdf")
                ),
                ClubAnnouncementItem(
                    id = "ann_3",
                    title = "U20 Academy Cup Lineup & Travel Briefing",
                    targetScope = "U20 Academy",
                    content = "Travel roster for the regional youth tournament has been confirmed. Departure is set for Saturday 06:00 AM sharp from the clubhouse. Parent consent forms must be signed.",
                    timestamp = "Sep 28, 2026",
                    author = "Youth Academy Director",
                    pushSent = true,
                    readCount = 16,
                    totalRecipients = 18,
                    attachments = listOf("Travel_Manifest.pdf")
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEDE9FE),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = brandPurple, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Club Announcements",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = inkColor
                                )
                                Text(
                                    text = "Broadcast official updates to whole club or specific teams",
                                    fontSize = 11.5.sp,
                                    color = textMuted
                                )
                            }
                        }

                        Button(
                            onClick = { showNewAnnouncementDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = brandPurple),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        items(announcementsList) { ann ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (ann.targetScope == "Whole Club") Color(0xFFEFF6FF) else Color(0xFFF3E8FF)
                        ) {
                            Text(
                                text = "TO: ${ann.targetScope.uppercase()}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (ann.targetScope == "Whole Club") Color(0xFF2563EB) else brandPurple
                            )
                        }

                        Text(
                            text = ann.timestamp,
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = ann.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = inkColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = ann.content,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Attachments if any
                    if (ann.attachments.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ann.attachments.forEach { file ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(file, fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Footer metrics: Push indicator & Read Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (ann.pushSent) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = successColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Push Notification Sent", fontSize = 11.sp, color = successColor, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        val readPct = if (ann.totalRecipients > 0) ((ann.readCount.toFloat() / ann.totalRecipients) * 100).toInt() else 0
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Read: ${ann.readCount}/${ann.totalRecipients} ($readPct%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = inkColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Post Announcement
    if (showNewAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var targetScope by remember { mutableStateOf("Whole Club") }
        var content by remember { mutableStateOf("") }
        var pushNotification by remember { mutableStateOf(true) }
        var hasAttachment by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showNewAnnouncementDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("New Club Announcement", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = inkColor)
                        IconButton(onClick = { showNewAnnouncementDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Announcement Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Target Audience
                    Column {
                        Text("Target Audience:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = targetScope == "Whole Club",
                                onClick = { targetScope = "Whole Club" },
                                label = { Text("Whole Club", fontSize = 11.sp) }
                            )
                            if (teams.isNotEmpty()) {
                                FilterChip(
                                    selected = targetScope == teams.first().name,
                                    onClick = { targetScope = teams.first().name },
                                    label = { Text(teams.first().name, fontSize = 11.sp) }
                                )
                            } else {
                                FilterChip(
                                    selected = targetScope == "U20 Academy",
                                    onClick = { targetScope = "U20 Academy" },
                                    label = { Text("U20 Academy", fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 5
                    )

                    // Push notification toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { pushNotification = !pushNotification }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = pushNotification, onCheckedChange = { pushNotification = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Dispatch push notification", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = inkColor)
                            Text("Instantly alerts players and coaches on mobile", fontSize = 10.sp, color = textMuted)
                        }
                    }

                    // Attachment toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { hasAttachment = !hasAttachment }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = hasAttachment, onCheckedChange = { hasAttachment = it })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Attach official PDF memo / schedule", fontSize = 12.sp, color = inkColor)
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                announcementsList = listOf(
                                    ClubAnnouncementItem(
                                        id = "ann_${System.currentTimeMillis()}",
                                        title = title.trim(),
                                        targetScope = targetScope,
                                        content = content.trim(),
                                        timestamp = "Just now",
                                        author = "Club Admin",
                                        pushSent = pushNotification,
                                        readCount = 0,
                                        totalRecipients = if (targetScope == "Whole Club") athletesCount + 8 else 18,
                                        attachments = if (hasAttachment) listOf("Official_Announcement_${System.currentTimeMillis().toString().takeLast(4)}.pdf") else emptyList()
                                    )
                                ) + announcementsList

                                showNewAnnouncementDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brandPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Broadcast Announcement", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
