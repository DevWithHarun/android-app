package com.example.ui.screens

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
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AthleteEntity
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.ui.util.MediaUtils

@Composable
fun HomeScreen(
    athletes: List<AthleteEntity> = emptyList(),
    onSelectAthlete: (String) -> Unit = {},
    onNavigateToScouting: () -> Unit,
    onNavigateToAthleteProfile: () -> Unit,
    onNavigateToClubCoach: () -> Unit,
    onOpenAuth: () -> Unit,
    isLoadingAthletes: Boolean = false,
    onRefreshAthletes: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Green)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "The Verified Truth in Sports",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Your Professional Identity in Sports",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Stop relying on highlight reels. Build a trusted, verifiable track record. Connect with clubs, scouts, and coaches using real data.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onOpenAuth,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Get Started", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onNavigateToScouting,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Explore Talent", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FeatureCheckItem("Verified Stats")
                        FeatureCheckItem("Scout Network")
                        FeatureCheckItem("Club Logistics")
                    }
                }
            }
        }

        // Live Real Athletes Roster Showcase
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 20.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Verified Athletes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Live Backend",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Direct from Talent Graph cloud roster & profiles",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRefreshAthletes) {
                            Icon(
                                Icons.Outlined.Refresh,
                                contentDescription = "Refresh athletes",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(onClick = onNavigateToScouting) {
                            Text("See All", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (isLoadingAthletes) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (athletes.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Synchronizing Real Athletes...", fontWeight = FontWeight.SemiBold)
                                Text("Tap refresh to fetch verified profiles from Firestore", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(onClick = onRefreshAthletes) {
                                Text("Fetch")
                            }
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        items(athletes.take(8), key = { it.firestoreId }) { athlete ->
                            HomeAthleteCard(
                                athlete = athlete,
                                onClick = { onSelectAthlete(athlete.firestoreId) }
                            )
                        }
                    }
                }
            }
        }

        // Challenge Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Text(
                    text = "The Challenge in Talent Discovery",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The current landscape is noisy, unverifiable, and focused on short-term wins.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                ChallengeCard(
                    icon = Icons.Default.Shield,
                    title = "Unverifiable Data",
                    description = "Self-reported stats and highlight reels without context make it difficult to trust what you see."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ChallengeCard(
                    icon = Icons.Default.VolumeUp,
                    title = "Signal vs. Noise",
                    description = "Social media hype and flood of information bury athletes who consistently put in the work."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ChallengeCard(
                    icon = Icons.Default.Timer,
                    title = "Short-Term Focus",
                    description = "The system rewards flashes of brilliance over long-term consistency and disciplined growth."
                )
            }
        }

        // Solution Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                Text(
                    text = "The Solution: The Talent Graph",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A professional identity layer for sports based on truth, structure, and long-term perspective.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                SolutionItem(
                    icon = Icons.Default.Verified,
                    title = "Verified Metrics",
                    desc = "Integrate with trusted sources to provide verified performance data as a single source of truth."
                )
                Spacer(modifier = Modifier.height(16.dp))
                SolutionItem(
                    icon = Icons.Default.AccountBox,
                    title = "Structured Profiles",
                    desc = "Move beyond the highlight reel with comprehensive profiles showcasing an athlete's full journey."
                )
                Spacer(modifier = Modifier.height(16.dp))
                SolutionItem(
                    icon = Icons.Default.TrendingUp,
                    title = "Long-Term Tracking",
                    desc = "Track progress over years, not just seasons, identifying trends and rewarding consistent improvement."
                )
            }
        }

        // Audience Portals Cards
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Built For Every Stakeholder",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AudienceCard(
                        title = "For Athletes",
                        desc = "Take control of your narrative and build credibility.",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToAthleteProfile,
                        modifier = Modifier.weight(1f)
                    )
                    AudienceCard(
                        title = "For Scouts",
                        desc = "Cut through noise with verified data.",
                        icon = Icons.Default.Search,
                        onClick = onNavigateToScouting,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AudienceCard(
                        title = "For Clubs",
                        desc = "Roster oversight and squad logistics.",
                        icon = Icons.Default.Groups,
                        onClick = onNavigateToClubCoach,
                        modifier = Modifier.weight(1f)
                    )
                    AudienceCard(
                        title = "For Coaches",
                        desc = "Match logging and talent authentication.",
                        icon = Icons.Default.Sports,
                        onClick = onNavigateToClubCoach,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCheckItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChallengeCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SolutionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AudienceCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HomeAthleteCard(athlete: AthleteEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = Modifier.width(220.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mediaModel = MediaUtils.getMediaModel(athlete.photoUrl)
                if (mediaModel != null) {
                    AsyncImage(
                        model = mediaModel,
                        contentDescription = "${athlete.name} photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = athlete.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "${athlete.rating}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = athlete.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (athlete.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = Color(0xFF0D9488),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = "${athlete.sport} • ${athlete.position}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = athlete.clubName,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
