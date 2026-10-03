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
import com.example.data.OpportunityEntity
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel

@Composable
fun OpportunitiesModuleView(
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

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Opportunities") }
    var showAddDialog by remember { mutableStateOf(false) }
    var oppToEdit by remember { mutableStateOf<OpportunityEntity?>(null) }
    var oppToDelete by remember { mutableStateOf<OpportunityEntity?>(null) }
    var oppToApply by remember { mutableStateOf<OpportunityEntity?>(null) }

    // Aggregate statistics
    val totalOpportunities = uiState.opportunities.size
    val appliedCount = remember(uiState.opportunities) {
        uiState.opportunities.count { it.status.contains("Applied", ignoreCase = true) || it.status.contains("Shortlisted", ignoreCase = true) }
    }
    val trialsCount = remember(uiState.opportunities) {
        uiState.opportunities.count { it.type.contains("Trial", ignoreCase = true) }
    }

    val filteredOpportunities = remember(searchQuery, selectedFilter, uiState.opportunities) {
        uiState.opportunities.filter { opp ->
            val matchesSearch = opp.title.contains(searchQuery, ignoreCase = true) ||
                    opp.location.contains(searchQuery, ignoreCase = true) ||
                    opp.type.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Open Trials" -> opp.type.contains("Trial", ignoreCase = true)
                "Club Signings" -> opp.type.contains("Contract", ignoreCase = true) || opp.type.contains("Club", ignoreCase = true)
                "My Applications" -> opp.status.contains("Applied", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
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
        // Hero Overview Card
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
                                    text = "GLOBAL SCOUTING NETWORK",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Opportunities & Trials",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = "Verified club trials, scouting showcases, and academy trial opportunities.",
                                fontSize = 13.sp,
                                color = textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF3B82F6).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
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
                        OppStatItem(value = "$totalOpportunities", label = "Available")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        OppStatItem(value = "$trialsCount", label = "Trials")
                        VerticalDivider(modifier = Modifier.height(30.dp), color = borderColor)
                        OppStatItem(value = "$appliedCount", label = "Applied")
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
                        Text("Add Scouting Opportunity / Trial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search opportunities, clubs, or venues...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = textMuted)
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = secondaryColor,
                    unfocusedBorderColor = borderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf("All Opportunities", "Open Trials", "Club Signings", "My Applications")
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
                    text = "SCOUTING FIXTURES & LISTINGS (${filteredOpportunities.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Listing", fontSize = 12.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (filteredOpportunities.isEmpty()) {
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
                        Icon(Icons.Default.Star, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No opportunities found", fontWeight = FontWeight.Bold, color = primaryColor, fontSize = 15.sp)
                        Text("Add scouting trials, club invitations, or academy showcases to manage your career options.", color = textMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ Add Opportunity", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredOpportunities, key = { it.id }) { opp ->
                OpportunityCard(
                    opportunity = opp,
                    onApply = { oppToApply = opp },
                    onEdit = { oppToEdit = opp },
                    onDelete = { oppToDelete = opp },
                    onShare = {
                        shareOpportunityDetails(context, opp)
                    }
                )
            }
        }
    }

    // Modal: Add Opportunity
    if (showAddDialog) {
        AddOpportunityDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, type, location, deadline, desc, status ->
                viewModel.addOpportunity(title, type, location, deadline, desc, status)
                showAddDialog = false
            }
        )
    }

    // Modal: Edit Opportunity
    if (oppToEdit != null) {
        EditOpportunityDialog(
            opportunity = oppToEdit!!,
            onDismiss = { oppToEdit = null },
            onSave = { updated ->
                viewModel.updateOpportunity(updated)
                oppToEdit = null
            }
        )
    }

    // Modal: Apply Confirmation
    if (oppToApply != null) {
        AlertDialog(
            onDismissRequest = { oppToApply = null },
            title = { Text("Submit Scouting Application?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Your verified Talent Graph profile, performance metrics, and match dossier will be submitted to '${oppToApply?.title}'.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        oppToApply?.let { opp ->
                            viewModel.updateOpportunity(opp.copy(status = "Applied"))
                        }
                        oppToApply = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White)
                ) {
                    Text("Confirm Application", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { oppToApply = null }) { Text("Cancel") }
            }
        )
    }

    // Modal: Delete Confirmation
    if (oppToDelete != null) {
        AlertDialog(
            onDismissRequest = { oppToDelete = null },
            title = { Text("Delete Opportunity Listing?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${oppToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        oppToDelete?.let { viewModel.deleteOpportunity(it) }
                        oppToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { oppToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun OppStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun OpportunityCard(
    opportunity: OpportunityEntity,
    onApply: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val isApplied = opportunity.status.contains("Applied", ignoreCase = true) || opportunity.status.contains("Shortlisted", ignoreCase = true)

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
                    Text(opportunity.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${opportunity.type} • ${opportunity.location}",
                        fontSize = 12.sp,
                        color = textMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isApplied) Color(0xFFECFDF5) else Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = opportunity.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isApplied) Color(0xFF047857) else Color(0xFF2563EB)
                    )
                }
            }

            if (opportunity.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = opportunity.description,
                    fontSize = 12.sp,
                    color = textMuted,
                    lineHeight = 16.sp
                )
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
                    Text("Application Deadline:", fontSize = 11.sp, color = textMuted)
                    Text(opportunity.deadline, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isApplied) {
                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(15.dp))
                        Text("Dossier Submitted", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                    }
                }

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
fun AddOpportunityDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, type: String, location: String, deadline: String, desc: String, status: String) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Trial") }
    var location by remember { mutableStateOf("Nairobi, Kenya") }
    var deadline by remember { mutableStateOf("30 Nov 2026") }
    var desc by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Open") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Opportunity / Trial", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Opportunity Title") },
                    placeholder = { Text("e.g. KPL Pro Showcase 2026") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Opportunity Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Trial", "Contract", "Academy", "Scholarship").forEach { item ->
                        val isSel = type == item
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) primaryColor else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f).clickable { type = item }
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.padding(vertical = 6.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Deadline") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description & Requirements") },
                    placeholder = { Text("Open to U23 midfielders and attackers...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title, type, location, deadline, desc, status)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White)
                    ) {
                        Text("Save Listing", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditOpportunityDialog(
    opportunity: OpportunityEntity,
    onDismiss: () -> Unit,
    onSave: (OpportunityEntity) -> Unit
) {
    val primaryColor = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    var title by remember { mutableStateOf(opportunity.title) }
    var type by remember { mutableStateOf(opportunity.type) }
    var location by remember { mutableStateOf(opportunity.location) }
    var deadline by remember { mutableStateOf(opportunity.deadline) }
    var desc by remember { mutableStateOf(opportunity.description) }
    var status by remember { mutableStateOf(opportunity.status) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Edit Opportunity", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryColor)

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") }, modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline") }, modifier = Modifier.weight(1f))
                }

                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = textMuted)
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(opportunity.copy(title = title, type = type, location = location, deadline = deadline, description = desc, status = status))
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

fun shareOpportunityDetails(context: Context, opp: OpportunityEntity) {
    val text = """
        TALENT GRAPH • SCOUTING OPPORTUNITY
        Title: ${opp.title}
        Type: ${opp.type}
        Location: ${opp.location}
        Deadline: ${opp.deadline}
        Status: ${opp.status}
        Description: ${opp.description}
        
        Connected via Talent Graph Scouting Intelligence.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Scouting Trial: ${opp.title}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Opportunity"))
}
