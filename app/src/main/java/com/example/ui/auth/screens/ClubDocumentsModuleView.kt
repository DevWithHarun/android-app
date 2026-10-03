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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDocumentsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedTabCategory by rememberSaveable { mutableStateOf("All Documents") }
    val categories = listOf("All Documents", "Contracts", "Identification & Passports", "Federation Licenses", "Consent & Medical Clearances", "Transfer Agreements")

    var showUploadDialog by remember { mutableStateOf(false) }

    val successState = clubDashboardState as? ClubDashboardUiState.Success
    val rawDocs = successState?.documents ?: emptyList()

    var docList by remember(rawDocs) {
        mutableStateOf(
            if (rawDocs.isNotEmpty()) rawDocs
            else listOf(
                ClubDocumentModel("doc_1", "", "Senior Professional Contract — John Kamau", "Contracts", "John Kamau", "https://talentgraph.org/docs/c1.pdf", "2027-12-31", "L2 — Organization Verified", "Club General Counsel", "2026-09-15"),
                ClubDocumentModel("doc_2", "", "Academy Registration Certificate & Parental Consent", "Consent & Medical Clearances", "Brian Ochieng (U20)", "https://talentgraph.org/docs/c2.pdf", "Permanent", "L2 — Organization Verified", "Academy Director", "2026-09-20"),
                ClubDocumentModel("doc_3", "", "Federation Club License Premier Division 2026/27", "Federation Licenses", "Mombasa United FC", "https://talentgraph.org/docs/c3.pdf", "2027-06-30", "L2 — Federation Verified", "National Football Federation", "2026-08-01"),
                ClubDocumentModel("doc_4", "", "FIFA / Federation Transfer Clearance Certificate", "Transfer Agreements", "Kevin Otieno", "https://talentgraph.org/docs/c4.pdf", "Permanent", "L2 — Organization Verified", "Registrar", "2026-09-01"),
                ClubDocumentModel("doc_5", "", "National ID & Medical Pre-Competition Screening", "Identification & Passports", "Squad (22 Athletes)", "https://talentgraph.org/docs/c5.pdf", "2027-01-15", "L2 — Medical Board Verified", "Senior Physio", "2026-09-25")
            )
        )
    }

    val filteredDocs = remember(selectedTabCategory, docList) {
        if (selectedTabCategory == "All Documents") docList
        else docList.filter { it.category.equals(selectedTabCategory, ignoreCase = true) }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Document Vault & Compliance",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Encrypted Repository for Legal Contracts, Athlete Passports & Licenses for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Button(
                            onClick = { showUploadDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upload Doc", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedTabCategory).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB),
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedTabCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF2563EB) else textMuted
                            )
                        }
                    )
                }
            }
        }

        // Document Cards
        items(filteredDocs) { doc ->
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
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(shape = CircleShape, color = Color(0xFFEFF6FF), modifier = Modifier.size(38.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        when (doc.category) {
                                            "Contracts" -> Icons.Default.Gavel
                                            "Federation Licenses" -> Icons.Default.Badge
                                            "Identification & Passports" -> Icons.Default.PermIdentity
                                            else -> Icons.Default.Description
                                        },
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(doc.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("Scope: ${doc.targetEntity} • ${doc.category}", fontSize = 11.sp, color = textMuted)
                            }
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                            Text(
                                text = doc.verificationLevel,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF8FAFC))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Uploaded by: ${doc.uploadedBy} (${doc.uploadedAt})", fontSize = 10.sp, color = textMuted)
                        Text("Expiry: ${doc.expiryDate}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (doc.expiryDate.contains("2026")) dangerColor else primaryColor)
                    }
                }
            }
        }
    }

    // DIALOG: Upload Document
    if (showUploadDialog) {
        var docTitle by remember { mutableStateOf("") }
        var targetEntity by remember { mutableStateOf("Senior Team") }
        var docCategory by remember { mutableStateOf("Contracts") }
        var expiryDate by remember { mutableStateOf("2027-12-31") }

        Dialog(onDismissRequest = { showUploadDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Upload Vault Document", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showUploadDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = docTitle, onValueChange = { docTitle = it }, label = { Text("Document Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = targetEntity, onValueChange = { targetEntity = it }, label = { Text("Target Athlete / Team") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Text("Document Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    val catOpts = listOf("Contracts", "Identification & Passports", "Federation Licenses", "Consent & Medical Clearances", "Transfer Agreements")
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        catOpts.forEach { c ->
                            FilterChip(selected = docCategory == c, onClick = { docCategory = c }, label = { Text(c, fontSize = 10.sp) })
                        }
                    }

                    OutlinedTextField(value = expiryDate, onValueChange = { expiryDate = it }, label = { Text("Expiry Date (or Permanent)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            if (docTitle.isNotBlank()) {
                                val newDoc = ClubDocumentModel(
                                    id = "doc_${System.currentTimeMillis()}",
                                    title = docTitle,
                                    category = docCategory,
                                    targetEntity = targetEntity,
                                    fileUrl = "https://talentgraph.org/docs/${System.currentTimeMillis()}.pdf",
                                    expiryDate = expiryDate,
                                    verificationLevel = "L2 — Organization Verified",
                                    uploadedBy = "Club Admin",
                                    uploadedAt = "2026-09-30"
                                )
                                docList = listOf(newDoc) + docList
                                showUploadDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Save & Verify Document", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
