package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AthleteEntity
import com.example.data.MatchLogEntity
import com.example.ui.util.AthleteNameResolver
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun AthleteProfileScreen(
    athlete: AthleteEntity?,
    matchLogs: List<MatchLogEntity> = emptyList(),
    onBack: () -> Unit,
    onAddMatchLog: ((Long, String, Int, Int, Int, Int, String) -> Unit)? = null,
    onRefresh: () -> Unit = {}
) {
    val firestoreId = athlete?.firestoreId

    var isLoading by remember(firestoreId) { mutableStateOf(firestoreId != null) }
    var docSnapshot by remember(firestoreId) { mutableStateOf<DocumentSnapshot?>(null) }
    var fetchFailed by remember(firestoreId) { mutableStateOf(false) }

    LaunchedEffect(firestoreId) {
        if (firestoreId.isNullOrBlank()) {
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        fetchFailed = false
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("athletes")
                .document(firestoreId)
                .get()
                .await()
            if (doc.exists()) {
                docSnapshot = doc
            } else {
                docSnapshot = null
                fetchFailed = true
            }
        } catch (e: Exception) {
            fetchFailed = true
        } finally {
            isLoading = false
        }
    }

    if (athlete == null && docSnapshot == null && !isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    Icons.Default.PersonOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Athlete Not Found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "The requested athlete profile does not exist or has been removed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onBack) {
                    Text("Return to Scouting")
                }
            }
        }
        return
    }

    val doc = docSnapshot
    val rawData = doc?.data

    // Name resolution via the fixed fallback chain
    val resolvedName = if (doc != null) {
        AthleteNameResolver.resolveFromDoc(doc)
    } else {
        athlete?.name ?: "Athlete"
    }

    // Photo resolution
    val photoUrl = AthleteNameResolver.resolvePhotoUrl(doc)
        ?: AthleteNameResolver.resolvePhotoUrl(rawData)
        ?: athlete?.photoUrl?.trim()?.ifBlank { null }

    // Position & Team
    val rawPosition = (rawData?.get("position") ?: rawData?.get("role")).toSafeString()
        ?: athlete?.position?.trim()?.ifBlank { null }?.takeIf { it != "Midfielder" && it != "Position: Not provided" }

    val position = rawPosition ?: "Position: Not provided"

    val teamName = (rawData?.get("team")
        ?: rawData?.get("currentTeam")
        ?: rawData?.get("clubName")
        ?: rawData?.get("club")).toSafeString()
        ?: athlete?.clubName?.trim()?.ifBlank { null }
        ?: "Independent"

    val sport = (rawData?.get("sport")).toSafeString()
        ?: athlete?.sport?.trim()?.ifBlank { null }
        ?: "Football (Soccer)"

    // Personal & Physical metrics (honest: null if missing)
    val age = (rawData?.get("age")).toSafeInt()

    val dominantFoot = (rawData?.get("dominantFoot") ?: rawData?.get("foot")).toSafeString()

    val heightCm = (rawData?.get("heightCm") ?: rawData?.get("height")).toSafeLong()

    val weightKg = (rawData?.get("weightKg") ?: rawData?.get("weight")).toSafeLong()

    // Scores & Indices - NO fabricated defaults or deriving one from another
    val talentGraphScore = (rawData?.get("talentGraphScore")).toSafeInt()

    val compositeScoutingIndex = (rawData?.get("compositeScoutingIndex")).toSafeInt()

    val performanceIndex = (rawData?.get("performanceIndex")).toSafeInt()

    val readinessTier = (rawData?.get("readinessTier") ?: rawData?.get("tier")).toSafeString()

    val rawRisk = rawData?.get("riskIndex")
    val riskIndex: String? = when (rawRisk) {
        is String -> rawRisk.trim().ifBlank { null }
        is Double -> if (rawRisk in 0.0..1.0) "${(rawRisk * 100).toInt()}%" else "${rawRisk.toInt()}%"
        is Float -> if (rawRisk in 0f..1f) "${(rawRisk * 100).toInt()}%" else "${rawRisk.toInt()}%"
        is Number -> "${rawRisk.toInt()}%"
        else -> null
    }

    val isVerified = (rawData?.get("isVerified")).toSafeBoolean() ?: athlete?.isVerified ?: true

    // Detailed Attributes (Mental / Physical / Technical)
    val detailedAttributes = parseDetailedAttributes(doc, rawData)

    // Match History
    val matches = parseMatchHistory(doc, rawData, matchLogs)

    // Previous Teams
    val previousTeams = parsePreviousTeams(doc, rawData)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = resolvedName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "$sport • $position",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Profile Hero Header Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar: Coil AsyncImage or Initials placeholder
                            val mediaModel = com.example.ui.util.MediaUtils.getMediaModel(photoUrl)
                            if (mediaModel != null) {
                                AsyncImage(
                                    model = mediaModel,
                                    contentDescription = "$resolvedName Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = resolvedName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase(),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = resolvedName,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isVerified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified Profile",
                                            tint = Color(0xFF0D9488),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$position • $teamName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Talent Graph Verified Athlete",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Physical & Biographical Attributes Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricItem(label = "Age", value = age?.let { "$it yrs" } ?: "Not provided")
                            MetricItem(label = "Foot", value = dominantFoot ?: "Not provided")
                            MetricItem(label = "Height", value = heightCm?.let { "$it cm" } ?: "Not provided")
                            MetricItem(label = "Weight", value = weightKg?.let { "$it kg" } ?: "Not provided")
                        }
                    }
                }
            }

            // Key Scouting Metrics & Indices Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Scouting Index & Readiness",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IndexBadge(
                                label = "Talent Graph",
                                value = talentGraphScore?.toString() ?: "Pending",
                                modifier = Modifier.weight(1f),
                                highlightColor = MaterialTheme.colorScheme.primary
                            )
                            IndexBadge(
                                label = "Scouting Index",
                                value = compositeScoutingIndex?.toString() ?: "Pending",
                                modifier = Modifier.weight(1f),
                                highlightColor = Color(0xFF0284C7)
                            )
                            IndexBadge(
                                label = "Performance",
                                value = performanceIndex?.toString() ?: "Pending",
                                modifier = Modifier.weight(1f),
                                highlightColor = Color(0xFF10B981)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = readinessTier ?: "Readiness: Pending",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (riskIndex != null) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = if (riskIndex != null) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (riskIndex != null) "Risk: $riskIndex" else "Risk: Pending",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (riskIndex != null) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Detailed Attributes (Mental, Physical, Technical)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Detailed Attribute Ratings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (detailedAttributes.isEmpty()) {
                            Text(
                                text = "Attribute ratings pending evaluation for this athlete.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            if (detailedAttributes.technical.isNotEmpty()) {
                                AttributeSectionView(title = "Technical Attributes", attributes = detailedAttributes.technical)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            if (detailedAttributes.physical.isNotEmpty()) {
                                AttributeSectionView(title = "Physical Attributes", attributes = detailedAttributes.physical)
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            if (detailedAttributes.mental.isNotEmpty()) {
                                AttributeSectionView(title = "Mental & Tactical", attributes = detailedAttributes.mental)
                            }
                        }
                    }
                }
            }

            // Match History Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Verified Match History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${matches.size} recorded",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (matches.isEmpty()) {
                            Text(
                                text = "No match logs recorded yet for this athlete.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            matches.forEachIndexed { index, match ->
                                MatchHistoryRow(match)
                                if (index < matches.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Previous Teams / Career History Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Career History & Previous Teams",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (previousTeams.isEmpty()) {
                            Text(
                                text = "No previous club records listed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            previousTeams.forEachIndexed { index, team ->
                                CareerTeamRow(team)
                                if (index < previousTeams.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helpers & Components

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun IndexBadge(label: String, value: String, modifier: Modifier = Modifier, highlightColor: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = highlightColor.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(
                text = value,
                fontSize = if (value.length > 3) 13.sp else 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = highlightColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = highlightColor.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun AttributeSectionView(title: String, attributes: Map<String, Int>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        attributes.forEach { (name, rating) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                LinearProgressIndicator(
                    progress = { rating / 100f },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = when {
                        rating >= 85 -> Color(0xFF10B981)
                        rating >= 75 -> MaterialTheme.colorScheme.primary
                        else -> Color(0xFFF59E0B)
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$rating",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class MatchItem(
    val competition: String,
    val opponent: String,
    val goals: Int,
    val assists: Int,
    val rating: Int,
    val date: String
)

data class CareerTeamItem(
    val teamName: String,
    val league: String,
    val dates: String,
    val appearances: Int,
    val goals: Int,
    val assists: Int
)

@Composable
fun MatchHistoryRow(match: MatchItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${match.competition} vs ${match.opponent}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${match.date} • ${match.goals} Goals • ${match.assists} Assists",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "Rating: ${match.rating}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CareerTeamRow(team: CareerTeamItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = team.teamName,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${team.league} • ${team.dates}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${team.appearances} Apps • ${team.goals} G • ${team.assists} A",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

// Data Parsing Parsers
data class DetailedAttributes(
    val technical: Map<String, Int> = emptyMap(),
    val physical: Map<String, Int> = emptyMap(),
    val mental: Map<String, Int> = emptyMap()
) {
    fun isEmpty(): Boolean = technical.isEmpty() && physical.isEmpty() && mental.isEmpty()
}

private fun parseDetailedAttributes(doc: DocumentSnapshot?, rawData: Map<String, Any?>?): DetailedAttributes {
    val rawAttr = (doc?.get("detailedAttributes") ?: rawData?.get("detailedAttributes")) as? Map<*, *>
        ?: (doc?.get("attributes") ?: rawData?.get("attributes")) as? Map<*, *>

    fun extractSection(name: String): Map<String, Int> {
        val section = (rawAttr?.get(name) ?: rawAttr?.get(name.lowercase())) as? Map<*, *>
        if (section.isNullOrEmpty()) {
            return emptyMap()
        }
        val result = mutableMapOf<String, Int>()
        section.forEach { (k, v) ->
            val num = (v as? Number)?.toInt()
                ?: (v as? String)?.toIntOrNull()
            if (k is String && num != null) {
                result[k] = num
            }
        }
        return result
    }

    return DetailedAttributes(
        technical = extractSection("Technical"),
        physical = extractSection("Physical"),
        mental = extractSection("Mental")
    )
}

private fun parseMatchHistory(doc: DocumentSnapshot?, rawData: Map<String, Any?>?, fallbackLogs: List<MatchLogEntity>): List<MatchItem> {
    val rawList = (doc?.get("matchHistory") ?: rawData?.get("matchHistory")) as? List<*>
    if (!rawList.isNullOrEmpty()) {
        val result = mutableListOf<MatchItem>()
        for (item in rawList) {
            val map = item as? Map<*, *> ?: continue
            val comp = map["competition"] as? String ?: "Match"
            val opp = map["opponent"] as? String ?: "Opponent"
            val goals = (map["goals"] as? Number)?.toInt() ?: 0
            val assists = (map["assists"] as? Number)?.toInt() ?: 0
            val rating = (map["rating"] as? Number)?.toInt() ?: 0
            val date = map["date"] as? String ?: ""
            result.add(MatchItem(competition = comp, opponent = opp, goals = goals, assists = assists, rating = rating, date = date))
        }
        if (result.isNotEmpty()) return result
    }

    if (fallbackLogs.isNotEmpty()) {
        return fallbackLogs.take(5).map {
            MatchItem(
                competition = it.competition,
                opponent = it.opponent.ifBlank { "League Match" },
                goals = it.goals,
                assists = it.assists,
                rating = it.matchRating,
                date = it.matchDate
            )
        }
    }

    return emptyList()
}

private fun parsePreviousTeams(doc: DocumentSnapshot?, rawData: Map<String, Any?>?): List<CareerTeamItem> {
    val rawList = (doc?.get("previousTeams") ?: rawData?.get("previousTeams")) as? List<*>
    if (!rawList.isNullOrEmpty()) {
        val result = mutableListOf<CareerTeamItem>()
        for (item in rawList) {
            val map = item as? Map<*, *> ?: continue
            val name = (map["teamName"] ?: map["team"] ?: map["club"]) as? String ?: continue
            val league = map["league"] as? String ?: "National Division"
            val dates = (map["dates"] ?: "${map["from"] ?: ""} - ${map["to"] ?: ""}".trim().ifBlank { null }) as? String ?: ""
            val apps = (map["appearances"] ?: map["matches"]) as? Number ?: 0
            val goals = map["goals"] as? Number ?: 0
            val assists = map["assists"] as? Number ?: 0
            result.add(CareerTeamItem(teamName = name, league = league, dates = dates, appearances = apps.toInt(), goals = goals.toInt(), assists = assists.toInt()))
        }
        if (result.isNotEmpty()) return result
    }

    return emptyList()
}

private fun Any?.toSafeString(): String? {
    return when (this) {
        null -> null
        is String -> this.trim().ifBlank { null }
        is Number -> this.toString()
        is Boolean -> this.toString()
        else -> null
    }
}

private fun Any?.toSafeInt(): Int? {
    return when (this) {
        null -> null
        is Number -> this.toInt()
        is String -> this.trim().toDoubleOrNull()?.toInt()
        else -> null
    }
}

private fun Any?.toSafeLong(): Long? {
    return when (this) {
        null -> null
        is Number -> this.toLong()
        is String -> this.trim().toDoubleOrNull()?.toLong()
        else -> null
    }
}

private fun Any?.toSafeBoolean(): Boolean? {
    return when (this) {
        null -> null
        is Boolean -> this
        is String -> this.trim().toBooleanStrictOrNull()
        is Number -> this.toInt() != 0
        else -> null
    }
}
