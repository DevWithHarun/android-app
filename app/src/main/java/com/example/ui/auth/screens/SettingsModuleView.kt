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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun SettingsModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var pushNotifications by remember { mutableStateOf(true) }
    var scoutAlerts by remember { mutableStateOf(true) }
    var trialAlerts by remember { mutableStateOf(true) }
    var weeklyReport by remember { mutableStateOf(false) }
    var twoFactorAuth by remember { mutableStateOf(true) }
    var defaultPrivacy by remember { mutableStateOf("Club Performance Data") }

    var showVerificationDialog by remember { mutableStateOf(false) }
    var selectedCredentialType by remember { mutableStateOf("National ID / Passport") }
    var credentialNumber by remember { mutableStateOf("") }
    var verificationNotes by remember { mutableStateOf("") }

    val verificationRequests = uiState.documents.filter { it.category == "Credential Verification" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Header
        item {
            Column {
                Text("Settings & Verification", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Text("Manage your Talent Graph account, credential verification, and privacy.", fontSize = 12.sp, color = textMuted)
            }
        }

        // --- REQUEST VERIFICATION SECTION ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("REQUEST & CREDENTIAL VERIFICATION", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text("Level 3 Active", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "Submit official documents (National ID, Passport, Federation Player Card) for official Talent Graph verification and federation badge approval.",
                        fontSize = 12.sp,
                        color = textMuted,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (verificationRequests.isNotEmpty()) {
                        Text("Submitted Verification Requests:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        verificationRequests.forEach { req ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, borderColor),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(req.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primaryColor)
                                        Text("Submitted: ${req.issueDate} • Status: ${req.verificationState}", fontSize = 11.sp, color = textMuted)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFFFBEB)
                                    ) {
                                        Text(req.verificationState, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Button(
                        onClick = { showVerificationDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Request New Verification / Upload ID", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Account Information Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ACCOUNT DETAILS", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingsProfileDetailRow("Signed in as", uiState.userName)
                    SettingsProfileDetailRow("Email", uiState.userEmail.ifBlank { "athlete@talentgraph.org" })
                    SettingsProfileDetailRow("Role", uiState.userRole.name)
                    SettingsProfileDetailRow("Talent Graph ID", "#TG-ATHLETE-00123")
                }
            }
        }

        // Notifications Preferences
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("NOTIFICATION PREFERENCES", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsSwitchRow("Push Notifications", "Receive real-time alerts on your device", pushNotifications) {
                        pushNotifications = it
                        Toast.makeText(context, "Preference updated", Toast.LENGTH_SHORT).show()
                    }
                    SettingsSwitchRow("Scout & Viewer Alerts", "Get notified when a club or scout views your profile", scoutAlerts) {
                        scoutAlerts = it
                        Toast.makeText(context, "Preference updated", Toast.LENGTH_SHORT).show()
                    }
                    SettingsSwitchRow("Trial & Opportunity Alerts", "Notifications for open club trials and scout invitations", trialAlerts) {
                        trialAlerts = it
                        Toast.makeText(context, "Preference updated", Toast.LENGTH_SHORT).show()
                    }
                    SettingsSwitchRow("Weekly Performance Summary", "Email digest of workload, match ratings, and ACWR", weeklyReport) {
                        weeklyReport = it
                        Toast.makeText(context, "Preference updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Privacy & Data Sharing Tier
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("PRIVACY & DATA SHARING", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Default profile visibility for connected scouts and clubs:", fontSize = 12.sp, color = textMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    listOf("Public Profile Only", "Club Performance Data", "Full Vault & Documents").forEach { tier ->
                        Surface(
                            onClick = {
                                defaultPrivacy = tier
                                Toast.makeText(context, "Default privacy set to $tier", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (defaultPrivacy == tier) Color(0xFFF0FDFA) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (defaultPrivacy == tier) secondaryColor else borderColor),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(tier, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = primaryColor)
                                RadioButton(
                                    selected = defaultPrivacy == tier,
                                    onClick = {
                                        defaultPrivacy = tier
                                        Toast.makeText(context, "Default privacy set to $tier", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = secondaryColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Security & 2FA
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SECURITY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsSwitchRow("Two-Factor Authentication", "Protect your Talent Graph account with 2FA verification", twoFactorAuth) {
                        twoFactorAuth = it
                        Toast.makeText(context, "2FA setting updated", Toast.LENGTH_SHORT).show()
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { Toast.makeText(context, "Password reset email sent to your registered address.", Toast.LENGTH_LONG).show() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Change Password", fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            }
        }

        // Data Export & Sign Out
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { Toast.makeText(context, "Career passport JSON & PDF export generated successfully.", Toast.LENGTH_LONG).show() },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Career Data & Passport", fontWeight = FontWeight.Bold, color = primaryColor)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.clearAllData()
                            Toast.makeText(context, "All local data cleared successfully.", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Local Data & Reset", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFDC2626)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Talent Graph", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- REQUEST VERIFICATION MODAL DIALOG ---
    if (showVerificationDialog) {
        Dialog(onDismissRequest = { showVerificationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Request Credential Verification", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                        IconButton(onClick = { showVerificationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = textMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Credential Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf("National ID Card", "Valid Passport", "Federation Player Card", "Medical Clearance Certificate").forEach { cType ->
                        Surface(
                            onClick = { selectedCredentialType = cType },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedCredentialType == cType) Color(0xFFF0FDFA) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (selectedCredentialType == cType) secondaryColor else borderColor),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(cType, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = primaryColor)
                                RadioButton(
                                    selected = selectedCredentialType == cType,
                                    onClick = { selectedCredentialType = cType },
                                    colors = RadioButtonDefaults.colors(selectedColor = secondaryColor)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = credentialNumber,
                        onValueChange = { credentialNumber = it },
                        label = { Text("Document / ID Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = verificationNotes,
                        onValueChange = { verificationNotes = it },
                        label = { Text("Additional Notes / Issuing Authority") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showVerificationDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", color = textMuted)
                        }
                        Button(
                            onClick = {
                                if (credentialNumber.isBlank()) {
                                    Toast.makeText(context, "Please enter document or ID number", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.requestVerification(selectedCredentialType, credentialNumber, verificationNotes)
                                    Toast.makeText(context, "Verification request submitted successfully!", Toast.LENGTH_LONG).show()
                                    showVerificationDialog = false
                                    credentialNumber = ""
                                    verificationNotes = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
                        ) {
                            Text("Submit Request", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}

@Composable
private fun SettingsSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1E293B))
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0D9488))
        )
    }
}
