package com.example.ui.auth.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.TalentUiState
import kotlinx.coroutines.launch

@Composable
fun ConnectionsAndChatModuleView(
    viewModel: com.example.ui.TalentViewModel,
    uiState: TalentUiState
) {
    val connectionRepo = remember { ConnectionRepository() }
    val scope = rememberCoroutineScope()
    val currentUid = uiState.userEmail.ifBlank { uiState.userName.ifBlank { "user_guest" } }
    val currentUserName = if (uiState.userName.isNotBlank()) uiState.userName else "Verified Athlete"

    var selectedTab by remember { mutableStateOf("connections") } // "connections", "pending", "chat"
    var acceptedConnections by remember { mutableStateOf<List<UserConnectionSummary>>(emptyList()) }
    var pendingRequests by remember { mutableStateOf<List<ConnectionDoc>>(emptyList()) }

    var activeChatOtherUid by remember { mutableStateOf<String?>(null) }
    var activeChatOtherName by remember { mutableStateOf<String?>(null) }
    var activeChatOtherRole by remember { mutableStateOf<String?>(null) }

    // Real-time listeners
    DisposableEffect(currentUid) {
        connectionRepo.observeAcceptedConnections(currentUid) { summaries ->
            acceptedConnections = summaries
        }
        connectionRepo.observePendingRequestsReceived(currentUid) { reqs ->
            pendingRequests = reqs
        }
        onDispose {}
    }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val amberColor = Color(0xFFFBBF24)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Connections & Messaging", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Gated connection requests and secure chat channels", fontSize = 12.sp, color = textMuted)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs for Connections
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabButton(
                title = "Connections (${acceptedConnections.size})",
                selected = selectedTab == "connections",
                modifier = Modifier.weight(1f)
            ) {
                selectedTab = "connections"
                activeChatOtherUid = null
            }

            TabButton(
                title = "Pending (${pendingRequests.size})",
                selected = selectedTab == "pending",
                modifier = Modifier.weight(1f),
                badgeCount = pendingRequests.size
            ) {
                selectedTab = "pending"
                activeChatOtherUid = null
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeChatOtherUid != null) {
            // Active Gated Chat View
            GatedChatScreen(
                currentUid = currentUid,
                currentUserName = currentUserName,
                otherUid = activeChatOtherUid!!,
                otherName = activeChatOtherName ?: "User",
                otherRole = activeChatOtherRole ?: "Athlete",
                onBack = { activeChatOtherUid = null },
                connectionRepo = connectionRepo
            )
        } else {
            when (selectedTab) {
                "connections" -> {
                    if (acceptedConnections.isEmpty()) {
                        EmptyStateCard(
                            icon = Icons.Outlined.People,
                            title = "No accepted connections yet",
                            subtitle = "Connect with coaches, scouts, and athletes from the discovery feed.",
                            secondaryColor = secondaryColor,
                            cardBg = cardBg,
                            borderColor = borderColor,
                            primaryColor = primaryColor,
                            textMuted = textMuted
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            items(acceptedConnections) { conn ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = BorderStroke(1.dp, borderColor),
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        activeChatOtherUid = conn.otherUserId
                                        activeChatOtherName = conn.otherUserName
                                        activeChatOtherRole = conn.otherUserRole
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(secondaryColor.copy(alpha = 0.2f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    conn.otherUserName.take(1).uppercase(),
                                                    color = secondaryColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            }
                                            Column {
                                                Text(conn.otherUserName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                                Text("Role: ${conn.otherUserRole} • Connected", fontSize = 11.sp, color = textMuted)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                activeChatOtherUid = conn.otherUserId
                                                activeChatOtherName = conn.otherUserName
                                                activeChatOtherRole = conn.otherUserRole
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                            shape = RoundedCornerShape(50.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "pending" -> {
                    if (pendingRequests.isEmpty()) {
                        EmptyStateCard(
                            icon = Icons.Outlined.HourglassEmpty,
                            title = "No pending connection requests",
                            subtitle = "Incoming connection requests from other users will appear here.",
                            secondaryColor = amberColor,
                            cardBg = cardBg,
                            borderColor = borderColor,
                            primaryColor = primaryColor,
                            textMuted = textMuted
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) {
                            items(pendingRequests) { req ->
                                val requesterId = req.requesterId
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = BorderStroke(1.dp, amberColor.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(amberColor.copy(alpha = 0.3f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        requesterId.take(1).uppercase(),
                                                        color = Color(0xFFB45309),
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Column {
                                                    Text("Connection Request", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                                    Text("From User: $requesterId", fontSize = 11.sp, color = textMuted)
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color(0xFFFEF3C7)
                                            ) {
                                                Text(
                                                    "Pending",
                                                    color = Color(0xFFB45309),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        connectionRepo.acceptRequest(req.id)
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669), contentColor = Color.White),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f).height(38.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        connectionRepo.declineRequest(req.id)
                                                    }
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f).height(38.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        TextButton(
                                            onClick = {
                                                activeChatOtherUid = requesterId
                                                activeChatOtherName = "User $requesterId"
                                                activeChatOtherRole = "User"
                                            },
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text("View Conversation & Messages", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GatedChatScreen(
    currentUid: String,
    currentUserName: String,
    otherUid: String,
    otherName: String,
    otherRole: String,
    onBack: () -> Unit,
    connectionRepo: ConnectionRepository
) {
    val scope = rememberCoroutineScope()
    var connectionDoc by remember { mutableStateOf<ConnectionDoc?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var inputMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(currentUid, otherUid) {
        val connJob = scope.launch {
            connectionRepo.observeConnection(currentUid, otherUid).collect { doc ->
                connectionDoc = doc
            }
        }
        val msgJob = scope.launch {
            connectionRepo.observeMessages(currentUid, otherUid).collect { list ->
                messages = list
            }
        }
        onDispose {
            connJob.cancel()
            msgJob.cancel()
        }
    }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
    ) {
        // Chat Header with Back Button
        Row(
            modifier = Modifier.fillMaxWidth().background(cardBg, RoundedCornerShape(12.dp)).border(1.dp, borderColor, RoundedCornerShape(12.dp)).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                }
                Box(
                    modifier = Modifier.size(36.dp).background(secondaryColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(otherName.take(1).uppercase(), color = secondaryColor, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(otherName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                    val statusText = when (connectionDoc?.status) {
                        "accepted" -> "Connected (Free Messaging)"
                        "pending" -> "Pending Connection Request"
                        "declined" -> "Connection Declined"
                        else -> "New Connection"
                    }
                    Text(statusText, fontSize = 11.sp, color = if (connectionDoc?.status == "accepted") secondaryColor else Color(0xFFD97706))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Messages List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                if (msg.type == "system") {
                    // System Message (centered, muted, no bubble)
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Text(
                                text = msg.text,
                                fontSize = 11.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                } else {
                    val isMe = msg.senderId == currentUid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMe) secondaryColor else cardBg,
                            border = if (isMe) null else BorderStroke(1.dp, borderColor),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isMe) "You" else msg.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) Color.White.copy(alpha = 0.8f) else secondaryColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = if (isMe) Color.White else primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color(0xFFDC2626),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gated Input Area
        val status = connectionDoc?.status ?: "none"
        val isRequester = connectionDoc?.requesterId == currentUid
        val messageCount = connectionDoc?.messageCountBeforeAccept ?: 0

        when {
            status == "accepted" || status == "none" -> {
                // Normal input
                ChatInputBar(
                    inputMessage = inputMessage,
                    onValueChange = { inputMessage = it; errorMessage = null },
                    onSend = {
                        if (inputMessage.isNotBlank()) {
                            scope.launch {
                                val res = connectionRepo.sendMessage(currentUid, currentUserName, otherUid, otherName, inputMessage)
                                if (res.isSuccess) {
                                    inputMessage = ""
                                    errorMessage = null
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message
                                }
                            }
                        }
                    }
                )
            }
            status == "pending" && !isRequester -> {
                // Recipient in pending state: Cannot reply yet, show Accept & Decline buttons
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$otherName sent you a connection request. Accept to enable free messaging.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        connectionDoc?.id?.let { connectionRepo.acceptRequest(it) }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669), contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("Accept Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        connectionDoc?.id?.let { connectionRepo.declineRequest(it) }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            status == "pending" && isRequester -> {
                // Requester in pending state: max 2 messages
                if (messageCount >= 2) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Waiting for $otherName to accept your request before you can send more messages.",
                            fontSize = 12.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    ChatInputBar(
                        inputMessage = inputMessage,
                        onValueChange = { inputMessage = it; errorMessage = null },
                        onSend = {
                            if (inputMessage.isNotBlank()) {
                                scope.launch {
                                    val res = connectionRepo.sendMessage(currentUid, currentUserName, otherUid, otherName, inputMessage)
                                    if (res.isSuccess) {
                                        inputMessage = ""
                                        errorMessage = null
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message
                                    }
                                }
                            }
                        }
                    )
                }
            }
            status == "declined" -> {
                // Declined state / Cooldown check
                val declinedAt = connectionDoc?.declinedAt
                val now = java.util.Date()
                val diffDays = if (declinedAt != null) (now.time - declinedAt.time) / (1000 * 60 * 60 * 24) else 7L
                val canRetry = diffDays >= 7

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (canRetry) "Connection request was declined. You can now send a new request." else "Connection request was declined. You can try reconnecting with $otherName after ${7 - diffDays} days.",
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (canRetry) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        val res = connectionRepo.sendMessage(currentUid, currentUserName, otherUid, otherName, "Hello, re-requesting connection.")
                                        if (res.isSuccess) {
                                            errorMessage = null
                                        } else {
                                            errorMessage = res.exceptionOrNull()?.message
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Send New Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputMessage: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputMessage,
            onValueChange = onValueChange,
            placeholder = { Text("Type a secure message...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onSend,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488), contentColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun TabButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Color.White else Color.Transparent,
        shadowElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color(0xFF1E293B) else Color(0xFF64748B)
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF4444)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    secondaryColor: Color,
    cardBg: Color,
    borderColor: Color,
    primaryColor: Color,
    textMuted: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = textMuted, textAlign = TextAlign.Center)
        }
    }
}
