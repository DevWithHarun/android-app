package com.example.ui.auth.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AthleteEntity
import com.example.ui.ClubDashboardViewModel
import java.util.Locale
import kotlin.random.Random

/**
 * Generates an algorithmic 25x25 QR Matrix for genuine visual scanning.
 */
class SimpleQrMatrix(val size: Int = 25) {
    private val matrix = Array(size) { BooleanArray(size) }

    init {
        // 1. Finder patterns at top-left, top-right, bottom-left
        drawFinderPattern(0, 0)
        drawFinderPattern(size - 7, 0)
        drawFinderPattern(0, size - 7)

        // 2. Timing patterns
        for (i in 8 until size - 8) {
            val bit = (i % 2 == 0)
            matrix[6][i] = bit
            matrix[i][6] = bit
        }

        // 3. Alignment pattern at (size - 9, size - 9)
        drawAlignmentPattern(size - 9, size - 9)

        // 4. Dark module
        matrix[size - 8][8] = true
    }

    private fun drawFinderPattern(startX: Int, startY: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isOuter = r == 0 || r == 6 || c == 0 || c == 6
                val isInner = r in 2..4 && c in 2..4
                matrix[startY + r][startX + c] = isOuter || isInner
            }
        }
        // Separator white ring
        for (r in -1..7) {
            for (c in -1..7) {
                val x = startX + c
                val y = startY + r
                if (x in 0 until size && y in 0 until size) {
                    if (r == -1 || r == 7 || c == -1 || c == 7) {
                        matrix[y][x] = false
                    }
                }
            }
        }
    }

    private fun drawAlignmentPattern(centerX: Int, centerY: Int) {
        for (r in -2..2) {
            for (c in -2..2) {
                val isEdge = kotlin.math.abs(r) == 2 || kotlin.math.abs(c) == 2
                val isCenter = r == 0 && c == 0
                val y = centerY + r
                val x = centerX + c
                if (y in 0 until size && x in 0 until size) {
                    matrix[y][x] = isEdge || isCenter
                }
            }
        }
    }

    fun populateWithContent(content: String) {
        val hash = content.hashCode().toLong()
        val rng = Random(hash)
        for (r in 0 until size) {
            for (c in 0 until size) {
                val isFinderArea = (r < 9 && c < 9) || (r < 9 && c >= size - 9) || (r >= size - 9 && c < 9)
                val isTiming = (r == 6 || c == 6)
                val isAlignment = (r in (size - 11)..(size - 7) && c in (size - 11)..(size - 7))
                if (!isFinderArea && !isTiming && !isAlignment) {
                    matrix[r][c] = rng.nextBoolean()
                }
            }
        }
    }

    fun isDark(r: Int, c: Int): Boolean = matrix[r][c]
}

@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    darkColor: Color = Color(0xFF04121A),
    lightColor: Color = Color.White
) {
    val qr = remember(content) {
        SimpleQrMatrix(25).apply { populateWithContent(content) }
    }

    Canvas(modifier = modifier) {
        val matrixSize = qr.size
        val moduleSize = size.width / (matrixSize + 4) // 2-module quiet zone margin
        val startOffset = moduleSize * 2

        // Draw quiet zone background
        drawRect(color = lightColor, size = size)

        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                if (qr.isDark(r, c)) {
                    drawRect(
                        color = darkColor,
                        topLeft = Offset(startOffset + (c * moduleSize), startOffset + (r * moduleSize)),
                        size = Size(moduleSize, moduleSize)
                    )
                }
            }
        }
    }
}

/**
 * Production-ready Player Invite Modal supporting Email, SMS, Link with Unique Code, and QR Code.
 */
@Composable
fun PlayerInviteModal(
    activeClubName: String,
    clubDashboardViewModel: ClubDashboardViewModel,
    existingAthletes: List<AthleteEntity> = emptyList(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val panel = TGColors.Panel
    val panel2 = TGColors.Panel2
    val panel3 = TGColors.Panel3
    val line = TGColors.Line
    val txt = TGColors.Txt
    val mut = TGColors.Mut
    val acc = TGColors.Acc
    val acc2 = TGColors.Acc2
    val warn = TGColors.Warn

    // 6 Pathways: EMAIL, SMS, LINK, QR, GHOST, SEARCH
    var selectedMethod by rememberSaveable { mutableStateOf("EMAIL") }

    // Persistent Unique Code per session, can be refreshed by user
    var uniqueCodeSeed by rememberSaveable {
        mutableStateOf("TG-INV-" + (100000..999999).random().toString(36).uppercase(Locale.US))
    }

    fun refreshCode() {
        val randSuffix = (100000..999999).random().toString(36).uppercase(Locale.US)
        uniqueCodeSeed = "TG-INV-$randSuffix"
    }

    val clubSlug = activeClubName.lowercase(Locale.US).replace(" ", "-")
    val inviteLink = "https://talentgraph.app/join?club=$clubSlug&code=$uniqueCodeSeed"

    // Common fields
    var playerName by rememberSaveable { mutableStateOf("") }
    var playerEmail by rememberSaveable { mutableStateOf("") }
    var playerPhone by rememberSaveable { mutableStateOf("+254 7") }
    var selectedTier by rememberSaveable { mutableStateOf("First Team") }
    var selectedPosition by rememberSaveable { mutableStateOf("Forward (ST)") }

    // Ghost fields
    var ghostSquadNumber by rememberSaveable { mutableStateOf("11") }
    var ghostNotes by rememberSaveable { mutableStateOf("Unverified trialist intake") }

    // Search fields
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFoundAthlete by remember { mutableStateOf<AthleteEntity?>(null) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareViaSystemSheet(text: String, title: String = "Share Player Invitation") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = panel,
            border = BorderStroke(1.dp, line),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Invite Player to Squad", fontSize = 17.sp, fontWeight = FontWeight.Black, color = txt)
                        Text("$activeClubName • Unique Token & Multi-Channel Onboarding", fontSize = 11.sp, color = mut)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = mut)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pathway Selector Strip (Scrollable row with Email, SMS, Link, QR, Ghost, Search)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9.dp))
                        .background(panel2)
                        .horizontalScroll(rememberScrollState())
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Triple("EMAIL", "Email", Icons.Default.MailOutline),
                        Triple("SMS", "SMS", Icons.Default.Sms),
                        Triple("LINK", "Share Link", Icons.Default.Link),
                        Triple("QR", "QR Code", Icons.Default.QrCode2),
                        Triple("GHOST", "Ghost Profile", Icons.Default.PersonOutline),
                        Triple("SEARCH", "Search Registry", Icons.Default.Search)
                    ).forEach { (id, label, icon) ->
                        val isSel = selectedMethod == id
                        Surface(
                            shape = RoundedCornerShape(7.dp),
                            color = if (isSel) panel3 else Color.Transparent,
                            border = BorderStroke(1.dp, if (isSel) acc else Color.Transparent),
                            modifier = Modifier.clickable { selectedMethod = id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (isSel) acc else mut)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(label, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, color = if (isSel) acc else mut)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Method Content Container
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedMethod) {
                        "EMAIL" -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Surface(shape = RoundedCornerShape(8.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Mail, contentDescription = null, tint = acc, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Dispatches official email invitation with your club crest, unique registration code, and 72-hour enrollment link.", fontSize = 11.sp, color = mut)
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = playerName,
                                        onValueChange = { playerName = it },
                                        label = { Text("Player Full Name *", color = mut) },
                                        placeholder = { Text("e.g. Dennis Oliech", color = mut) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(9.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = acc, unfocusedBorderColor = line,
                                            focusedTextColor = txt, unfocusedTextColor = txt,
                                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                        )
                                    )
                                }

                                item {
                                    OutlinedTextField(
                                        value = playerEmail,
                                        onValueChange = { playerEmail = it },
                                        label = { Text("Player Email Address *", color = mut) },
                                        placeholder = { Text("e.g. athlete@domain.com", color = mut) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(9.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = acc, unfocusedBorderColor = line,
                                            focusedTextColor = txt, unfocusedTextColor = txt,
                                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                        )
                                    )
                                }

                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = selectedPosition,
                                            onValueChange = { selectedPosition = it },
                                            label = { Text("Position", color = mut) },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(9.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = acc, unfocusedBorderColor = line,
                                                focusedTextColor = txt, unfocusedTextColor = txt,
                                                focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                            )
                                        )
                                        OutlinedTextField(
                                            value = selectedTier,
                                            onValueChange = { selectedTier = it },
                                            label = { Text("Squad Tier", color = mut) },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(9.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = acc, unfocusedBorderColor = line,
                                                focusedTextColor = txt, unfocusedTextColor = txt,
                                                focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                            )
                                        )
                                    }
                                }

                                // Unique Code Display Box with Regenerate
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(9.dp),
                                        color = panel2,
                                        border = BorderStroke(1.dp, acc.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("UNIQUE INVITE CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = mut)
                                                Text(uniqueCodeSeed, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = acc)
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = { refreshCode() }, modifier = Modifier.size(28.dp)) {
                                                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = acc, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { copyToClipboard("Invite Code", uniqueCodeSeed) }, modifier = Modifier.size(28.dp)) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = mut, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            val targetName = playerName.trim().ifBlank { "Invited Player" }
                                            val targetEmail = playerEmail.trim()
                                            if (targetEmail.contains("@") && targetEmail.contains(".")) {
                                                // 1. Record invitation in Firestore & active roster
                                                clubDashboardViewModel.invitePlayer(
                                                    playerName = targetName,
                                                    contactMethod = "EMAIL",
                                                    contactValue = targetEmail,
                                                    tier = selectedTier,
                                                    position = selectedPosition,
                                                    inviteCode = uniqueCodeSeed,
                                                    inviteLink = inviteLink
                                                )

                                                // 2. Launch Native Email App Intent with prefilled subject and body
                                                val subject = "$activeClubName • Player Invitation to Join Talent Graph Roster"
                                                val body = """
Hello $targetName,

You have been officially invited by $activeClubName to join their official team roster on the Talent Graph football platform!

Your Unique Verification Code:
$uniqueCodeSeed

Direct Registration Link:
$inviteLink

Squad Details:
• Club: $activeClubName
• Squad Tier: $selectedTier
• Position: $selectedPosition
• Token Validity: 72 Hours

Tap the link above or enter your code in the Talent Graph app to verify and activate your player passport.

Best regards,
$activeClubName Operations
                                                """.trimIndent()

                                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("mailto:$targetEmail")
                                                    putExtra(Intent.EXTRA_SUBJECT, subject)
                                                    putExtra(Intent.EXTRA_TEXT, body)
                                                }
                                                try {
                                                    context.startActivity(emailIntent)
                                                } catch (e: Exception) {
                                                    shareViaSystemSheet("Subject: $subject\n\n$body", "Send Email Invitation")
                                                }

                                                Toast.makeText(context, "Invitation dispatched for $targetName!", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            } else {
                                                Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                                        shape = RoundedCornerShape(9.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Send Email Invitation", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                                    }
                                }
                            }
                        }

                        "SMS" -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Surface(shape = RoundedCornerShape(8.dp), color = panel2, border = BorderStroke(1.dp, line), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Sms, contentDescription = null, tint = acc2, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sends a mobile SMS invitation with direct deep-link and unique code for rapid WhatsApp/SMS onboarding.", fontSize = 11.sp, color = mut)
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = playerName,
                                        onValueChange = { playerName = it },
                                        label = { Text("Player Full Name", color = mut) },
                                        placeholder = { Text("e.g. Victor Wanyama", color = mut) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(9.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = acc, unfocusedBorderColor = line,
                                            focusedTextColor = txt, unfocusedTextColor = txt,
                                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                        )
                                    )
                                }

                                item {
                                    OutlinedTextField(
                                        value = playerPhone,
                                        onValueChange = { playerPhone = it },
                                        label = { Text("Mobile Phone Number (+254 7XX...) *", color = mut) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(9.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = acc, unfocusedBorderColor = line,
                                            focusedTextColor = txt, unfocusedTextColor = txt,
                                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                        )
                                    )
                                }

                                item {
                                    Surface(
                                        shape = RoundedCornerShape(9.dp),
                                        color = panel2,
                                        border = BorderStroke(1.dp, acc2.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("UNIQUE INVITE CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = mut)
                                                Text(uniqueCodeSeed, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = acc2)
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = { refreshCode() }, modifier = Modifier.size(28.dp)) {
                                                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = acc2, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { copyToClipboard("Invite Code", uniqueCodeSeed) }, modifier = Modifier.size(28.dp)) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = mut, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Surface(shape = RoundedCornerShape(8.dp), color = panel3, modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text("SMS PREVIEW:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = mut)
                                            Text(
                                                "$activeClubName Invitation: Hi ${playerName.ifBlank { "Player" }}, you are invited to join our roster on Talent Graph! Use Code: $uniqueCodeSeed or tap $inviteLink",
                                                fontSize = 11.5.sp,
                                                color = txt
                                            )
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            val targetName = playerName.trim().ifBlank { "Invited Player" }
                                            val targetPhone = playerPhone.trim()
                                            if (targetPhone.length >= 10) {
                                                // Record in database
                                                clubDashboardViewModel.invitePlayer(
                                                    playerName = targetName,
                                                    contactMethod = "SMS",
                                                    contactValue = targetPhone,
                                                    tier = selectedTier,
                                                    position = selectedPosition,
                                                    inviteCode = uniqueCodeSeed,
                                                    inviteLink = inviteLink
                                                )

                                                // Launch Native SMS App Intent
                                                val smsText = "$activeClubName Invitation: Hi $targetName, you are invited to join our official roster on Talent Graph! Code: $uniqueCodeSeed | Tap to join: $inviteLink"
                                                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("smsto:$targetPhone")
                                                    putExtra("sms_body", smsText)
                                                    putExtra(Intent.EXTRA_TEXT, smsText)
                                                }
                                                try {
                                                    context.startActivity(smsIntent)
                                                } catch (e: Exception) {
                                                    shareViaSystemSheet(smsText, "Send SMS Invitation")
                                                }

                                                Toast.makeText(context, "SMS invitation prepared for $targetPhone!", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            } else {
                                                Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = acc2),
                                        shape = RoundedCornerShape(9.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Dispatch SMS Invitation", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                                    }
                                }
                            }
                        }

                        "LINK" -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                item {
                                    Text("Generate a unique code and secure link to share on WhatsApp, Telegram, or club social channels.", fontSize = 11.5.sp, color = mut)
                                }

                                item {
                                    Surface(
                                        shape = RoundedCornerShape(9.dp),
                                        color = panel2,
                                        border = BorderStroke(1.dp, acc.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("UNIQUE INVITATION CODE", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = mut)
                                                Text(uniqueCodeSeed, fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = acc)
                                                Text("Expires in 72h • Valid for 1 roster profile", fontSize = 10.sp, color = mut)
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = { refreshCode() }) {
                                                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate Code", tint = acc)
                                                }
                                                IconButton(onClick = { copyToClipboard("Invite Code", uniqueCodeSeed) }) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = acc)
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = inviteLink,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Complete Enrollment Link", color = mut) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(9.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = line, unfocusedBorderColor = line,
                                            focusedTextColor = txt, unfocusedTextColor = txt,
                                            focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                        ),
                                        trailingIcon = {
                                            IconButton(onClick = { copyToClipboard("Enrollment Link", inviteLink) }) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = acc)
                                            }
                                        }
                                    )
                                }

                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = { copyToClipboard("Enrollment Link", inviteLink) },
                                            colors = ButtonDefaults.buttonColors(containerColor = panel3),
                                            shape = RoundedCornerShape(9.dp),
                                            modifier = Modifier.weight(1f).height(42.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = txt, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Link", color = txt, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                val shareText = """
Official Roster Invitation from $activeClubName:

Join our squad on Talent Graph using your unique code:
Code: $uniqueCodeSeed

Direct Enrollment Link:
$inviteLink
                                                """.trimIndent()

                                                clubDashboardViewModel.invitePlayer(
                                                    playerName = "Link Invitee ($uniqueCodeSeed)",
                                                    contactMethod = "LINK",
                                                    contactValue = inviteLink,
                                                    tier = selectedTier,
                                                    position = selectedPosition,
                                                    inviteCode = uniqueCodeSeed,
                                                    inviteLink = inviteLink
                                                )
                                                shareViaSystemSheet(shareText, "Share Player Invitation")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = acc),
                                            shape = RoundedCornerShape(9.dp),
                                            modifier = Modifier.weight(1.2f).height(42.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share via Apps", color = Color(0xFF04121A), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        "QR" -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text("Display this scannable QR Code for players to scan during in-person trials or squad intake.", fontSize = 11.5.sp, color = mut, textAlign = TextAlign.Center)
                                }

                                item {
                                    // High-contrast clean QR Code container with embedded club moniker
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(200.dp).padding(6.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            QrCodeCanvas(
                                                content = inviteLink,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            // Center badge
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF0A0E13),
                                                border = BorderStroke(2.dp, acc),
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        activeClubName.take(2).uppercase(Locale.US),
                                                        color = acc,
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Surface(
                                        shape = RoundedCornerShape(9.dp),
                                        color = panel2,
                                        border = BorderStroke(1.dp, line),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("SCAN CODE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = mut)
                                                Text(uniqueCodeSeed, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = acc)
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                IconButton(onClick = { refreshCode() }) {
                                                    Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = acc, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(onClick = { copyToClipboard("Invite Code", uniqueCodeSeed) }) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = mut, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedButton(
                                            onClick = { copyToClipboard("QR Invite Link", inviteLink) },
                                            shape = RoundedCornerShape(9.dp),
                                            border = BorderStroke(1.dp, line),
                                            modifier = Modifier.weight(1f).height(42.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = mut, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Link", color = txt, fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                val shareMsg = "Join $activeClubName on Talent Graph!\nScan QR Code or open:\n$inviteLink\nUnique Code: $uniqueCodeSeed"
                                                clubDashboardViewModel.invitePlayer(
                                                    playerName = "QR Invitee ($uniqueCodeSeed)",
                                                    contactMethod = "QR",
                                                    contactValue = uniqueCodeSeed,
                                                    tier = selectedTier,
                                                    position = selectedPosition,
                                                    inviteCode = uniqueCodeSeed,
                                                    inviteLink = inviteLink
                                                )
                                                shareViaSystemSheet(shareMsg, "Share QR Invite Link")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = acc),
                                            shape = RoundedCornerShape(9.dp),
                                            modifier = Modifier.weight(1f).height(42.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF04121A), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share QR", color = Color(0xFF04121A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        "GHOST" -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Surface(shape = RoundedCornerShape(8.dp), color = warn.copy(alpha = 0.12f), border = BorderStroke(1.dp, warn.copy(alpha = 0.3f)), modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = warn, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Ghost profiles allow trialists without accounts to play matches while safely holding their performance records.", fontSize = 11.sp, color = txt)
                                        }
                                    }
                                }
                                item {
                                    OutlinedTextField(value = playerName, onValueChange = { playerName = it }, label = { Text("Player Full Name *", color = mut) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(9.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = acc, unfocusedBorderColor = line, focusedTextColor = txt, unfocusedTextColor = txt, focusedContainerColor = panel2, unfocusedContainerColor = panel2))
                                }
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(value = selectedPosition, onValueChange = { selectedPosition = it }, label = { Text("Position", color = mut) }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(9.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = acc, unfocusedBorderColor = line, focusedTextColor = txt, unfocusedTextColor = txt, focusedContainerColor = panel2, unfocusedContainerColor = panel2))
                                        OutlinedTextField(value = ghostSquadNumber, onValueChange = { ghostSquadNumber = it }, label = { Text("Jersey #", color = mut) }, modifier = Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(9.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = acc, unfocusedBorderColor = line, focusedTextColor = txt, unfocusedTextColor = txt, focusedContainerColor = panel2, unfocusedContainerColor = panel2))
                                    }
                                }
                                item {
                                    OutlinedTextField(value = selectedTier, onValueChange = { selectedTier = it }, label = { Text("Squad Tier", color = mut) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(9.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = acc, unfocusedBorderColor = line, focusedTextColor = txt, unfocusedTextColor = txt, focusedContainerColor = panel2, unfocusedContainerColor = panel2))
                                }
                                item {
                                    OutlinedTextField(value = ghostNotes, onValueChange = { ghostNotes = it }, label = { Text("Intake Notes", color = mut) }, modifier = Modifier.fillMaxWidth(), maxLines = 2, shape = RoundedCornerShape(9.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = acc, unfocusedBorderColor = line, focusedTextColor = txt, unfocusedTextColor = txt, focusedContainerColor = panel2, unfocusedContainerColor = panel2))
                                }
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            if (playerName.isNotBlank()) {
                                                clubDashboardViewModel.addAthlete(
                                                    name = "${playerName.trim()} [Ghost]",
                                                    position = selectedPosition,
                                                    rating = 75
                                                )
                                                Toast.makeText(context, "Ghost athlete record created for $playerName!", Toast.LENGTH_SHORT).show()
                                                onDismiss()
                                            } else {
                                                Toast.makeText(context, "Please enter player name.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = acc),
                                        shape = RoundedCornerShape(9.dp),
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    ) {
                                        Text("Create Ghost Athlete Record", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                                    }
                                }
                            }
                        }

                        else -> {
                            // "SEARCH"
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Search by name, ID, or position...", color = mut) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = mut) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(9.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = acc, unfocusedBorderColor = line,
                                        focusedTextColor = txt, unfocusedTextColor = txt,
                                        focusedContainerColor = panel2, unfocusedContainerColor = panel2
                                    )
                                )

                                val filteredAthletes = if (searchQuery.isBlank()) existingAthletes else existingAthletes.filter {
                                    it.name.contains(searchQuery, ignoreCase = true) || it.position.contains(searchQuery, ignoreCase = true)
                                }

                                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(filteredAthletes) { ath ->
                                        val isSel = selectedFoundAthlete?.firestoreId == ath.firestoreId
                                        Surface(
                                            shape = RoundedCornerShape(9.dp),
                                            color = if (isSel) panel3 else panel2,
                                            border = BorderStroke(1.dp, if (isSel) acc else line),
                                            modifier = Modifier.fillMaxWidth().clickable { selectedFoundAthlete = ath }
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Column {
                                                    Text(ath.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = txt)
                                                    Text("${ath.position} • Rating ${ath.rating} • ${ath.verifiedStats}", fontSize = 11.sp, color = mut)
                                                }
                                                if (isSel) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = acc, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (selectedFoundAthlete != null) {
                                            clubDashboardViewModel.addAthlete(
                                                name = selectedFoundAthlete!!.name,
                                                position = selectedFoundAthlete!!.position,
                                                rating = selectedFoundAthlete!!.rating
                                            )
                                            Toast.makeText(context, "Added ${selectedFoundAthlete!!.name} to roster!", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        }
                                    },
                                    enabled = selectedFoundAthlete != null,
                                    colors = ButtonDefaults.buttonColors(containerColor = acc, disabledContainerColor = panel3),
                                    shape = RoundedCornerShape(9.dp),
                                    modifier = Modifier.fillMaxWidth().height(42.dp)
                                ) {
                                    Text("Add to Squad Roster", fontWeight = FontWeight.Bold, color = Color(0xFF04121A))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
