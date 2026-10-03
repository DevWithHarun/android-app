package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ComingSoonPlaceholderView(
    title: String,
    category: String,
    description: String,
    icon: ImageVector,
    plannedFeatures: List<String> = emptyList(),
    onBackToOverview: () -> Unit,
    onNavigateToSquad: () -> Unit
) {
    val brandPurple = Color(0xFF6D28D9)
    val softPurple = Color(0xFFEDE7FB)
    val amberBadge = Color(0xFFD97706)
    val amberBg = Color(0xFFFEF3C7)
    val inkColor = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Coming Soon Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = amberBg,
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(amberBadge)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PHASE 2 ROADMAP • COMING SOON",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = amberBadge,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(softPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = brandPurple,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = inkColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle / Category
                    Text(
                        text = "Module: $category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = brandPurple
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = description.ifBlank {
                            "This module is out of scope for the current MVP release and is actively preserved for Phase 2."
                        },
                        fontSize = 13.sp,
                        color = textMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBackToOverview,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Overview", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = inkColor)
                        }

                        Button(
                            onClick = onNavigateToSquad,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = brandPurple)
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Active MVP", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Planned Phase 2 Capabilities
        if (plannedFeatures.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = brandPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Planned Phase 2 Capabilities",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = inkColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        plannedFeatures.forEach { feature ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(brandPurple)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = feature,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Current Active MVP Scope Information
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Currently Active in this MVP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val mvpModules = listOf(
                        "Athlete Lifecycle (Invited, Pending, Active, Transferred, Archived)",
                        "Team Transfers & Squad Numbers with Immutable History",
                        "Document Vault, Categorization, Expiry Tracking & Verification",
                        "Match Fixtures, Squad Lineups, Score & Events Logging",
                        "Coaches Training Sessions & Attendance Marking",
                        "Athlete Availability Status & Expected Return Dates",
                        "Single Master Calendar with Day, Week & Month Views",
                        "Club Announcements (Whole Club / Specific Team) & Rule-Based Alerts"
                    )

                    mvpModules.forEach { mvpItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mvpItem, fontSize = 11.5.sp, color = Color(0xFF166534))
                        }
                    }
                }
            }
        }
    }
}
