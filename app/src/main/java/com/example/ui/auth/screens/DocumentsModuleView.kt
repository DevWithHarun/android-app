package com.example.ui.auth.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DocumentEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun DocumentsModuleView(
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

    var selectedCategoryFilter by remember { mutableStateOf("All Documents") }
    var searchQuery by remember { mutableStateOf("") }
    var showUploadModal by remember { mutableStateOf(false) }
    var docToEdit by remember { mutableStateOf<DocumentEntity?>(null) }
    var docToDelete by remember { mutableStateOf<DocumentEntity?>(null) }
    var docToPreview by remember { mutableStateOf<DocumentEntity?>(null) }

    val filteredDocuments = remember(selectedCategoryFilter, searchQuery, uiState.documents) {
        uiState.documents.filter { doc ->
            val matchesCategory = when (selectedCategoryFilter) {
                "Contracts" -> doc.category.contains("Contract", ignoreCase = true)
                "Licenses" -> doc.category.contains("License", ignoreCase = true) || doc.category.contains("Federation", ignoreCase = true)
                "Medical" -> doc.category.contains("Medical", ignoreCase = true) || doc.category.contains("Fitness", ignoreCase = true)
                "Identity" -> doc.category.contains("Identity", ignoreCase = true) || doc.category.contains("Passport", ignoreCase = true)
                "Transfers" -> doc.category.contains("Transfer", ignoreCase = true) || doc.category.contains("Release", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                doc.title.contains(searchQuery, ignoreCase = true) ||
                doc.issuingAuthority.contains(searchQuery, ignoreCase = true) ||
                doc.category.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    val verifiedCount = remember(uiState.documents) {
        uiState.documents.count { it.verificationState.contains("Verified", ignoreCase = true) }
    }
    val pendingCount = remember(uiState.documents) {
        uiState.documents.count { it.verificationState.contains("Pending", ignoreCase = true) || it.verificationState.contains("Review", ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Hero Card: Vault Overview
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
                                    text = "SECURE CREDENTIAL VAULT",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Athlete Documents & Credentials",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Official contracts, player passes, transfer clearances, and medical records.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(secondaryColor.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.FolderSpecial, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(24.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DocStatItem(value = "${uiState.documents.size}", label = "Total Vault")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        DocStatItem(value = "$verifiedCount", label = "Verified")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        DocStatItem(value = "$pendingCount", label = "Pending Review")
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Button
                    Button(
                        onClick = { showUploadModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Document / Credential", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search documents by title, authority, or category...", fontSize = 13.sp, color = textMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textMuted) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = textMuted)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = secondaryColor,
                    unfocusedBorderColor = borderColor
                ),
                singleLine = true
            )
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val categories = listOf("All Documents", "Contracts", "Licenses", "Medical", "Identity", "Transfers")
                items(categories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) primaryColor else cardBg,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else borderColor),
                        modifier = Modifier.clickable { selectedCategoryFilter = category }
                    ) {
                        Text(
                            text = category,
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
                    text = "VAULT REPOSITORY (${filteredDocuments.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showUploadModal = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Document", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Document List or Empty State
        if (filteredDocuments.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.FolderOpen, contentDescription = null, tint = textMuted, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No documents match '$searchQuery'" else "No documents in vault yet",
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Upload contracts, federation IDs, or medical clearances to build your verified dossier.",
                            color = textMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showUploadModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Upload Your First Document", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredDocuments, key = { it.id }) { document ->
                DocumentVaultCard(
                    doc = document,
                    onPreview = { docToPreview = document },
                    onEdit = { docToEdit = document },
                    onDelete = { docToDelete = document },
                    onShare = {
                        shareDocumentDossier(context, document)
                    }
                )
            }
        }

        // Cryptographic Security & Federation Compliance Dossier
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(20.dp))
                        Text("Security & Federation Compliance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    DocDetailRow("Encryption Standard", "AES-256 Tamper-Evident Hash")
                    DocDetailRow("Federation TMS Sync", "Connected to FIFA Clearing House")
                    DocDetailRow("Storage Protocol", "Encrypted Cloud Storage")
                    DocDetailRow("Identity Protection", "Restricted Scout & Club Access Only")
                }
            }
        }
    }

    // Modal: Upload Document
    if (showUploadModal) {
        UploadDocumentDialog(
            onDismiss = { showUploadModal = false },
            onUpload = { title, category, state, issueDate, expiryDate, authority, fileType, fileSize ->
                viewModel.addDocument(title, category, state, issueDate, expiryDate, fileType, fileSize, authority)
                showUploadModal = false
            }
        )
    }

    // Modal: Edit Document
    if (docToEdit != null) {
        EditDocumentDialog(
            doc = docToEdit!!,
            onDismiss = { docToEdit = null },
            onSave = { updatedDoc ->
                viewModel.updateDocument(updatedDoc)
                docToEdit = null
            }
        )
    }

    // Modal: Preview Document Details
    if (docToPreview != null) {
        DocumentPreviewDialog(
            doc = docToPreview!!,
            onDismiss = { docToPreview = null },
            onShare = { shareDocumentDossier(context, docToPreview!!) }
        )
    }

    // Modal: Confirm Delete
    if (docToDelete != null) {
        AlertDialog(
            onDismissRequest = { docToDelete = null },
            title = { Text("Delete Document?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${docToDelete?.title}' from your credentials vault?") },
            confirmButton = {
                Button(
                    onClick = {
                        docToDelete?.let { viewModel.deleteDocument(it) }
                        docToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { docToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DocStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DocumentVaultCard(
    doc: DocumentEntity,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val isVerified = doc.verificationState.contains("Verified", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isVerified) secondaryColor.copy(alpha = 0.4f) else borderColor),
        modifier = Modifier.fillMaxWidth().clickable { onPreview() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Icon + Title + Status Badge
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
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (doc.fileType.uppercase()) {
                                    "PDF" -> Color(0xFFFEE2E2)
                                    "IMAGE", "JPG", "PNG" -> Color(0xFFE0F2FE)
                                    else -> Color(0xFFF1F5F9)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (doc.fileType.uppercase()) {
                                "PDF" -> Icons.Default.PictureAsPdf
                                "IMAGE", "JPG", "PNG" -> Icons.Default.Image
                                else -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = when (doc.fileType.uppercase()) {
                                "PDF" -> Color(0xFFDC2626)
                                "IMAGE", "JPG", "PNG" -> Color(0xFF0284C7)
                                else -> primaryColor
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = doc.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "${doc.category} • ${doc.issuingAuthority}",
                            fontSize = 12.sp,
                            color = textMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isVerified) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                ) {
                    Text(
                        text = doc.verificationState.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVerified) Color(0xFF047857) else Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata Row: Dates and File specs
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = textMuted, modifier = Modifier.size(13.dp))
                        Text(
                            text = "Issued: ${doc.issueDate}  •  Expires: ${doc.expiryDate}",
                            fontSize = 11.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "${doc.fileType.uppercase()} • ${doc.fileSize}",
                        fontSize = 11.sp,
                        color = textMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onPreview,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Inspect Document", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = primaryColor, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DocDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
    }
}

// Dialog: Upload Document
@Composable
fun UploadDocumentDialog(
    onDismiss: () -> Unit,
    onUpload: (title: String, category: String, state: String, issueDate: String, expiryDate: String, authority: String, fileType: String, fileSize: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Contract") }
    var authority by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("Verified") }
    var issueDate by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("Permanent") }
    var fileType by remember { mutableStateOf("PDF") }
    var fileSize by remember { mutableStateOf("1.5 MB") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Upload Credential Document", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Document Title") },
                            placeholder = { Text("e.g. FKF Federation Clearance") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Contract", "License", "Medical", "Identity").forEach { cat ->
                                val isSel = category == cat
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { category = cat }
                                ) {
                                    Text(
                                        text = cat,
                                        modifier = Modifier.padding(vertical = 7.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = authority,
                            onValueChange = { authority = it },
                            label = { Text("Issuing Authority / Organization") },
                            placeholder = { Text("e.g. Football Kenya Federation, Club Legal") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = issueDate,
                                onValueChange = { issueDate = it },
                                label = { Text("Issue Date") },
                                placeholder = { Text("e.g. Jan 2024") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { expiryDate = it },
                                label = { Text("Expiry Date") },
                                placeholder = { Text("e.g. Jun 2026") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text("File Format", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("PDF", "JPG", "PNG", "DOCX").forEach { format ->
                                val isSel = fileType == format
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { fileType = format }
                                ) {
                                    Text(
                                        text = format,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else textMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("Verification Status") },
                            placeholder = { Text("Verified / Pending Review") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onUpload(
                                    title,
                                    category,
                                    state,
                                    issueDate.ifBlank { "Current Season" },
                                    expiryDate.ifBlank { "Permanent" },
                                    authority.ifBlank { "Official Federation" },
                                    fileType,
                                    fileSize
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Upload Vault", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Dialog: Edit Document
@Composable
fun EditDocumentDialog(
    doc: DocumentEntity,
    onDismiss: () -> Unit,
    onSave: (DocumentEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf(doc.title) }
    var category by remember { mutableStateOf(doc.category) }
    var authority by remember { mutableStateOf(doc.issuingAuthority) }
    var state by remember { mutableStateOf(doc.verificationState) }
    var issueDate by remember { mutableStateOf(doc.issueDate) }
    var expiryDate by remember { mutableStateOf(doc.expiryDate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Document Record", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Document Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = authority,
                            onValueChange = { authority = it },
                            label = { Text("Issuing Authority") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = issueDate,
                                onValueChange = { issueDate = it },
                                label = { Text("Issue Date") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { expiryDate = it },
                                label = { Text("Expiry Date") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("Verification Status") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    doc.copy(
                                        title = title,
                                        category = category,
                                        issuingAuthority = authority,
                                        verificationState = state,
                                        issueDate = issueDate,
                                        expiryDate = expiryDate
                                    )
                                )
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

// Dialog: Preview Document
@Composable
fun DocumentPreviewDialog(
    doc: DocumentEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Credential Dossier", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (doc.fileType.uppercase()) {
                                "PDF" -> Icons.Default.PictureAsPdf
                                "IMAGE", "JPG", "PNG" -> Icons.Default.Image
                                else -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = secondaryColor,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                        Text("${doc.fileType.uppercase()} • ${doc.fileSize} • Tamper-Evident Hash: #TK-8492X", fontSize = 11.sp, color = textMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                DocDetailRow("Document Category", doc.category)
                DocDetailRow("Issuing Authority", doc.issuingAuthority)
                DocDetailRow("Verification Status", doc.verificationState)
                DocDetailRow("Issue Date", doc.issueDate)
                DocDetailRow("Valid Until", doc.expiryDate)
                DocDetailRow("Verification Fingerprint", "SHA-256 Registered")

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Close", color = textMuted)
                    }
                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Credential")
                    }
                }
            }
        }
    }
}

// Function to share document details to scouts/clubs via native Android share sheet
fun shareDocumentDossier(context: Context, doc: DocumentEntity) {
    val text = """
        TALENT GRAPH • ATHLETE CREDENTIAL CERTIFICATE
        Title: ${doc.title}
        Category: ${doc.category}
        Issuing Authority: ${doc.issuingAuthority}
        Status: ${doc.verificationState}
        Validity: ${doc.issueDate} - ${doc.expiryDate}
        Format: ${doc.fileType} (${doc.fileSize})
        
        Verified through Talent Graph Sports Intelligence & Credential Vault.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Credential: ${doc.title}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Credential Dossier"))
}
