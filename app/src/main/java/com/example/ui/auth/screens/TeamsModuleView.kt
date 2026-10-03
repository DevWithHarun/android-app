package com.example.ui.auth.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun TeamsModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState,
    clubNameOverride: String? = null
) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var currentSubTab by remember { mutableStateOf("memberships") } // memberships, connections, squad, requests
    var showJoinOrgModal by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }
    var showAddConnectionModal by remember { mutableStateOf(false) }
    var connectionToEditPermission by remember { mutableStateOf<ConnectionEntity?>(null) }
    var showAddClubModal by remember { mutableStateOf(false) }

    val activeMembership = uiState.memberships.firstOrNull { it.status == "ACTIVE" }
    val pendingMemberships = uiState.memberships.filter { it.status == "PENDING" }
    val endedMemberships = uiState.memberships.filter { it.status == "ENDED" }
    val primaryOrg = uiState.organizations.firstOrNull { it.id == (activeMembership?.orgId ?: 1L) }
        ?: uiState.organizations.firstOrNull()
    val displayClubName = clubNameOverride?.takeIf { it.isNotBlank() } ?: primaryOrg?.name ?: "Official Club"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Architecture Banner: Independent Identity & Membership Model
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF16A34A).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(24.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Independent Talent Graph Account",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "You own your identity and career data. Club affiliations are authorized memberships.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Sub-navigation Tabs
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SubTabButton(
                        label = "Memberships",
                        icon = Icons.Default.CorporateFare,
                        isSelected = currentSubTab == "memberships",
                        modifier = Modifier.weight(1f)
                    ) { currentSubTab = "memberships" }

                    SubTabButton(
                        label = "Connections",
                        icon = Icons.Default.Handshake,
                        isSelected = currentSubTab == "connections",
                        badgeCount = uiState.connections.size,
                        modifier = Modifier.weight(1f)
                    ) { currentSubTab = "connections" }

                    SubTabButton(
                        label = "Squad",
                        icon = Icons.Default.Groups,
                        isSelected = currentSubTab == "squad",
                        modifier = Modifier.weight(1f)
                    ) { currentSubTab = "squad" }

                    SubTabButton(
                        label = "Requests",
                        icon = Icons.Default.Inbox,
                        isSelected = currentSubTab == "requests",
                        badgeCount = pendingMemberships.size,
                        modifier = Modifier.weight(1f)
                    ) { currentSubTab = "requests" }
                }
            }
        }

        // --- TAB 1: MEMBERSHIPS & ORGANIZATIONS ---
        if (currentSubTab == "memberships") {
            // Current Active Organization Hero
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (activeMembership != null) Color(0xFFECFDF5) else Color(0xFFF1F5F9)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            if (activeMembership != null) Icons.Default.CheckCircle else Icons.Default.Info,
                                            contentDescription = null,
                                            tint = if (activeMembership != null) Color(0xFF059669) else textMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (activeMembership != null) "ACTIVE ORGANIZATION MEMBERSHIP" else "FREE AGENT / NO ACTIVE CLUB",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (activeMembership != null) Color(0xFF047857) else textMuted
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = displayClubName,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = primaryColor
                                )
                                Text(
                                    text = "${primaryOrg?.sport ?: "Football"} • ${primaryOrg?.location ?: "Mombasa, Kenya"}",
                                    fontSize = 12.sp,
                                    color = textMuted
                                )
                            }

                            IconButton(
                                onClick = { showQrModal = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape)
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = primaryColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Org ID and Join Code Pills
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, borderColor),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("ORGANIZATION ID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                                    Text(
                                        text = primaryOrg?.orgCode ?: "TG-CLUB-00421",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = primaryColor
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("CLUB JOIN CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                                    Text(
                                        text = primaryOrg?.joinCode ?: "MUFC-7K92X",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Join Club & Scan QR
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { showJoinOrgModal = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Club Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showQrModal = true },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, borderColor)
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("QR Onboard", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = primaryColor)
                            }
                        }
                    }
                }
            }

            // Membership & Career History (Preserves data across transfers)
            item {
                Text(
                    text = "Career & Organization History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryColor
                )
            }

            items(uiState.memberships) { membership ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = membership.orgName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = primaryColor
                                )
                                Text(
                                    text = "Team: ${membership.teamName} • Role: ${membership.role}",
                                    fontSize = 12.sp,
                                    color = textMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (membership.status) {
                                    "ACTIVE" -> Color(0xFFECFDF5)
                                    "PENDING" -> Color(0xFFFFFBEB)
                                    "ENDED" -> Color(0xFFF1F5F9)
                                    else -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    text = membership.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (membership.status) {
                                        "ACTIVE" -> Color(0xFF047857)
                                        "PENDING" -> Color(0xFFB45309)
                                        "ENDED" -> textMuted
                                        else -> Color(0xFFB91C1C)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (membership.status == "ENDED") {
                                    "Duration: ${membership.joinedAt} – ${membership.leftAt.ifBlank { "2026" }}"
                                } else if (membership.status == "ACTIVE") {
                                    "Joined: ${membership.joinedAt}"
                                } else {
                                    "Request pending approval"
                                },
                                fontSize = 11.sp,
                                color = textMuted
                            )

                            if (membership.status == "ACTIVE") {
                                TextButton(
                                    onClick = {
                                        viewModel.endMembership(membership)
                                        Toast.makeText(context, "Membership ended. Historical career data preserved.", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("Transfer / End Stint", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 2: CONNECTIONS & RELATIONSHIPS ---
        if (currentSubTab == "connections") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Authorized People & Staff", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                        Text("Relationship-based permissions to your talent records", fontSize = 11.sp, color = textMuted)
                    }
                    IconButton(
                        onClick = { showAddConnectionModal = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Connection", tint = primaryColor, modifier = Modifier.size(18.dp))
                    }
                }
            }

            items(uiState.connections) { connection ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    when (connection.connectedRole) {
                                        "Coach" -> Color(0xFFECFDF5)
                                        "Analyst" -> Color(0xFFEFF6FF)
                                        "Club Admin" -> Color(0xFFF3E8FF)
                                        else -> Color(0xFFFFF7ED)
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when (connection.connectedRole) {
                                    "Coach" -> Icons.Default.Sports
                                    "Analyst" -> Icons.Default.Analytics
                                    "Club Admin" -> Icons.Default.Shield
                                    else -> Icons.Default.SavedSearch
                                },
                                contentDescription = null,
                                tint = when (connection.connectedRole) {
                                    "Coach" -> Color(0xFF059669)
                                    "Analyst" -> Color(0xFF2563EB)
                                    "Club Admin" -> Color(0xFF7E22CE)
                                    else -> Color(0xFFEA580C)
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(connection.connectedName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Connected", tint = Color(0xFF059669), modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = "${connection.connectedRole} • ${connection.orgName}",
                                fontSize = 11.sp,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = "Permission: ${connection.permissionLevel.replace("_", " ")}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        IconButton(onClick = { connectionToEditPermission = connection }) {
                            Icon(Icons.Default.LockReset, contentDescription = "Permissions", tint = textMuted)
                        }
                    }
                }
            }
        }

        // --- TAB 3: SQUAD ROSTER ---
        if (currentSubTab == "squad") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${primaryOrg?.name ?: "Mombasa United"} Squad", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                        Text("${uiState.athletes.size} Athletes Registered in Squad", fontSize = 11.sp, color = textMuted)
                    }
                }
            }

            items(uiState.athletes) { athlete ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(secondaryColor.copy(alpha = 0.1f), CircleShape),
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
                            Text("${athlete.position} • Rating ${athlete.rating}", fontSize = 11.sp, color = textMuted)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text("Active Member", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                    }
                }
            }
        }

        // --- TAB 4: MEMBERSHIP REQUESTS QUEUE (CLUB ADMIN / APPROVALS) ---
        if (currentSubTab == "requests") {
            item {
                Text("Pending Membership Requests", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
            }

            if (pendingMemberships.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("All Caught Up!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                            Text("No pending membership requests waiting for review.", fontSize = 11.sp, color = textMuted)
                        }
                    }
                }
            } else {
                items(pendingMemberships) { request ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(request.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                                    Text("Role: ${request.userRole} • Requested: ${request.teamName}", fontSize = 12.sp, color = textMuted)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFFBEB)
                                ) {
                                    Text("PENDING", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                }
                            }

                            if (request.evidenceNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, borderColor),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Attachment, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = "Evidence: ${request.evidenceNotes}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF475569)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.rejectMembershipRequest(request)
                                        Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                                ) {
                                    Text("Reject", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.approveMembershipRequest(request)
                                        Toast.makeText(context, "Approved ${request.userName} as active member!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Approve", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MODAL: JOIN ORGANIZATION VIA CLUB CODE ---
    if (showJoinOrgModal) {
        JoinOrganizationDialog(
            organizations = uiState.organizations,
            onDismiss = { showJoinOrgModal = false },
            onSubmit = { code, teamName, evidence ->
                viewModel.requestJoinOrganization(
                    codeOrJoinCode = code,
                    teamName = teamName,
                    evidenceNotes = evidence,
                    onSuccess = { org ->
                        Toast.makeText(context, "Membership request sent to ${org.name}!", Toast.LENGTH_LONG).show()
                        showJoinOrgModal = false
                    },
                    onError = { err ->
                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }

    // --- MODAL: QR ONBOARDING & SHARING ---
    if (showQrModal) {
        QrOnboardingDialog(
            org = primaryOrg ?: OrganizationEntity(),
            onDismiss = { showQrModal = false }
        )
    }

    // --- MODAL: EDIT PERMISSION LEVEL ---
    connectionToEditPermission?.let { conn ->
        ManagePermissionDialog(
            connection = conn,
            onDismiss = { connectionToEditPermission = null },
            onSave = { newPerm ->
                viewModel.updateConnectionPermission(conn, newPerm)
                Toast.makeText(context, "Permissions updated for ${conn.connectedName}", Toast.LENGTH_SHORT).show()
                connectionToEditPermission = null
            },
            onDisconnect = {
                viewModel.removeConnection(conn)
                Toast.makeText(context, "Disconnected from ${conn.connectedName}", Toast.LENGTH_SHORT).show()
                connectionToEditPermission = null
            }
        )
    }

    // --- MODAL: ADD CONNECTION ---
    if (showAddConnectionModal) {
        AddConnectionDialog(
            onDismiss = { showAddConnectionModal = false },
            onAdd = { name, role, org, team, perm ->
                viewModel.addConnection(name, role, org, team, perm)
                Toast.makeText(context, "Connected with $name", Toast.LENGTH_SHORT).show()
                showAddConnectionModal = false
            }
        )
    }
}

@Composable
fun SubTabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    badgeCount: Int = 0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF64748B),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Color(0xFF0D9488) else Color(0xFFE2E8F0)
                ) {
                    Text(
                        text = "$badgeCount",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF334155)
                    )
                }
            }
        }
    }
}

// Dialog: Join Organization via Club Code
@Composable
fun JoinOrganizationDialog(
    organizations: List<OrganizationEntity>,
    onDismiss: () -> Unit,
    onSubmit: (code: String, teamName: String, evidence: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var codeInput by remember { mutableStateOf("MUFC-7K92X") }
    var selectedTeam by remember { mutableStateOf("First Team") }
    var evidenceInput by remember { mutableStateOf("Player registration & transfer clearance form") }

    val matchedOrg = remember(codeInput, organizations) {
        val clean = codeInput.trim().uppercase()
        organizations.firstOrNull {
            it.orgCode.equals(clean, ignoreCase = true) ||
            it.joinCode.equals(clean, ignoreCase = true) ||
            it.name.contains(clean, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Join Organization", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                Text(
                    "Enter the organization code or join code provided by your club admin.",
                    fontSize = 12.sp,
                    color = textMuted
                )

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    label = { Text("Club Code / Join Code") },
                    placeholder = { Text("e.g. MUFC-7K92X or TG-CLUB-00421") },
                    trailingIcon = {
                        IconButton(onClick = { codeInput = "MUFC-7K92X" }) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Sample Code", tint = Color(0xFF0D9488))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Live Preview Card
                if (matchedOrg != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(matchedOrg.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF166534))
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            }
                            Text("${matchedOrg.sport} • ${matchedOrg.location}", fontSize = 12.sp, color = Color(0xFF15803D))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Official verified organization ✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                }

                OutlinedTextField(
                    value = selectedTeam,
                    onValueChange = { selectedTeam = it },
                    label = { Text("Requested Team / Squad") },
                    placeholder = { Text("e.g. First Team, U20 Academy") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = evidenceInput,
                    onValueChange = { evidenceInput = it },
                    label = { Text("Evidence / Notes for Admin") },
                    placeholder = { Text("e.g. Player registration document") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (codeInput.isNotBlank()) {
                                onSubmit(codeInput, selectedTeam, evidenceInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Send Request", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: QR Code Onboarding
@Composable
fun QrOnboardingDialog(
    org: OrganizationEntity,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(org.name.ifBlank { "MOMBASA UNITED FC" }.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = primaryColor, letterSpacing = 1.sp)
                Text("Scan QR Code to Join First Team", fontSize = 12.sp, color = textMuted)

                // Stylized Simulated QR Code Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(2.dp, Color(0xFF1E293B)),
                    modifier = Modifier.size(180.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("JOIN CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textMuted)
                            Text(org.joinCode.ifBlank { "MUFC-7K92X" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = primaryColor)
                        }
                        IconButton(
                            onClick = {
                                clipboard.setText(AnnotatedString(org.joinCode.ifBlank { "MUFC-7K92X" }))
                                Toast.makeText(context, "Join code copied!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = primaryColor)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Dialog: Manage Permission Level
@Composable
fun ManagePermissionDialog(
    connection: ConnectionEntity,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDisconnect: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)
    var selectedLevel by remember { mutableStateOf(connection.permissionLevel) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Access & Permissions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                Text("Control what ${connection.connectedName} (${connection.connectedRole}) can access:", fontSize = 12.sp, color = textMuted)

                PermissionOption(
                    title = "Public Profile Only",
                    desc = "Bio, sport, position, and verified badges.",
                    isSelected = selectedLevel == "PUBLIC_PROFILE"
                ) { selectedLevel = "PUBLIC_PROFILE" }

                PermissionOption(
                    title = "Club Performance Data",
                    desc = "Training logs, match performance, workload, and development goals.",
                    isSelected = selectedLevel == "PERFORMANCE_DATA"
                ) { selectedLevel = "PERFORMANCE_DATA" }

                PermissionOption(
                    title = "Full Vault & Documents",
                    desc = "Passports, medical clearances, and contract history.",
                    isSelected = selectedLevel == "FULL_VAULT"
                ) { selectedLevel = "FULL_VAULT" }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDisconnect,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                    ) {
                        Text("Disconnect", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onSave(selectedLevel) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionOption(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF3B82F6) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2563EB))
            )
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
                Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
            }
        }
    }
}

// Dialog: Add Connection
@Composable
fun AddConnectionDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, role: String, org: String, team: String, perm: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Coach") }
    var org by remember { mutableStateOf("Mombasa United FC") }
    var team by remember { mutableStateOf("First Team") }
    var perm by remember { mutableStateOf("PERFORMANCE_DATA") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Team / Staff Connection", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Person Full Name") },
                    placeholder = { Text("e.g. David Mwangi") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role (Coach / Analyst / Scout / Admin)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = org,
                    onValueChange = { org = it },
                    label = { Text("Organization") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name, role, org, team, perm)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Connect", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
