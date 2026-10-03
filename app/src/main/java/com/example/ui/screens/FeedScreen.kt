package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.TalentUiState
import com.example.ui.util.MediaUtils
import com.example.ui.util.PhotoEffect
import com.example.ui.util.PostCardStyle
import com.example.data.FeedRepository
import com.example.data.SocialPost
import com.example.data.CommentItem
import kotlinx.coroutines.launch

data class StoryItem(
    val id: Long,
    val author: String,
    val media: String
)

data class FeedAlert(
    val id: Long,
    val title: String,
    val description: String,
    val timeAgo: String,
    val category: String,
    val isUnread: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: TalentUiState = TalentUiState(),
    onNavigateHome: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onOpenAuth: () -> Unit = {}
) {
    var activeTab by remember { mutableStateOf("feed") } // "feed", "analytics", or "alerts"
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    var isCreateModalOpen by remember { mutableStateOf(false) }
    var storyModalOpen by remember { mutableStateOf(false) }
    var currentStoryMedia by remember { mutableStateOf("") }
    var showMessagesDialog by remember { mutableStateOf(false) }
    var activeVideoUrlForPlayback by remember { mutableStateOf<String?>(null) }
    var activeVideoTitleForPlayback by remember { mutableStateOf("") }

    var newPostContent by remember { mutableStateOf("") }
    var newPostPlatform by remember { mutableStateOf("twitter") }
    var newPostCategory by remember { mutableStateOf("football") }

    val stories = remember {
        listOf(
            StoryItem(1, "James Njoroge", "https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&q=80&w=800"),
            StoryItem(2, "Sarah Mutuku", "https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&q=80&w=800"),
            StoryItem(3, "Coach Marcus", "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&q=80&w=800")
        )
    }

    val feedRepository = remember { FeedRepository() }
    val currentUserId = if (uiState.isAuthenticated) uiState.userEmail.ifBlank { uiState.userName } else "guest_user"
    var posts by remember { mutableStateOf<List<SocialPost>?>(null) }
    var alerts by remember { mutableStateOf<List<com.example.data.FeedAlert>>(emptyList()) }
    var userProfileData by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var clearAllAlerts by remember { mutableStateOf(false) }
    val displayedAlerts = if (clearAllAlerts) emptyList() else alerts
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUserId) {
        coroutineScope.launch {
            feedRepository.observeFeedPosts(currentUserId).collect { loadedPosts ->
                posts = loadedPosts
            }
        }
        coroutineScope.launch {
            feedRepository.observeRealAlerts(currentUserId).collect { loadedAlerts ->
                alerts = loadedAlerts
            }
        }
        coroutineScope.launch {
            feedRepository.observeUserProfile(currentUserId).collect { profile ->
                userProfileData = profile
            }
        }
    }

    val currentPosts = posts ?: emptyList()
    val analytics = remember(currentPosts, alerts, userProfileData) {
        feedRepository.computeAnalytics(currentPosts, alerts.size, userProfileData)
    }
    val filteredPosts = currentPosts.filter { post ->
        val matchesSearch = searchQuery.isBlank() ||
                post.content.contains(searchQuery, ignoreCase = true) ||
                post.author.contains(searchQuery, ignoreCase = true) ||
                post.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "all" || post.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateSurface = Color(0xFF0F172A)
    val slateBorder = Color(0xFF334155)

    Scaffold(
        containerColor = slateBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Home icon (directs to landing page)
                        IconButton(
                            onClick = onNavigateHome,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // 2. Talent Graph Logo (replaces icon before name)
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF10B981))),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = "Talent Graph Logo",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // 3. Search Bar (replaces name Verve & Vigor)
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text("Search feed...", color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = slateSurface,
                                unfocusedContainerColor = slateSurface,
                                focusedBorderColor = amberColor.copy(alpha = 0.6f),
                                unfocusedBorderColor = slateBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = amberColor
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // 4. Messaging Icon (replaces Social Feed yellow option)
                        IconButton(
                            onClick = { showMessagesDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Messages",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // 5. User Profile Icon with Online/Offline indicator (replaces Analytics Grid)
                        IconButton(
                            onClick = {
                                if (uiState.isAuthenticated) {
                                    onNavigateToDashboard()
                                } else {
                                    onOpenAuth()
                                }
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val userMedia = MediaUtils.getMediaModel(uiState.userPhotoUrl)
                                if (userMedia != null && uiState.userPhotoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = userMedia,
                                        contentDescription = "User Profile Photo",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .border(
                                                1.5.dp,
                                                if (uiState.isAuthenticated) Color(0xFF10B981) else Color.Gray,
                                                CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(slateSurface, CircleShape)
                                            .border(
                                                1.5.dp,
                                                if (uiState.isAuthenticated) amberColor else slateBorder,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (uiState.isAuthenticated && uiState.userName.isNotBlank()) {
                                            Text(
                                                text = uiState.userName.take(2).uppercase(),
                                                color = amberColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = "Profile",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Online / Offline Indicator Dot
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(if (uiState.isAuthenticated) Color(0xFF22C55E) else Color(0xFF64748B))
                                        .border(1.5.dp, slateBg, CircleShape)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = slateBg.copy(alpha = 0.95f))
            )
        },
        bottomBar = {
            // 5 tabs exactly as requested: Feed | Profile | + | Analytics | Alerts
            // Note: Floating Action Button above alerts tab has been removed
            NavigationBar(
                containerColor = slateBg,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Feed itself (not app home)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DynamicFeed, contentDescription = "Feed") },
                    label = { Text("Feed", fontSize = 10.sp) },
                    selected = activeTab == "feed",
                    onClick = { activeTab = "feed" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = amberColor,
                        selectedTextColor = amberColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = slateSurface
                    )
                )

                // Tab 2: Profile (directs to user dashboard by role; if not logged in prompts login)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = false,
                    onClick = {
                        if (uiState.isAuthenticated) {
                            onNavigateToDashboard()
                        } else {
                            onOpenAuth()
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                // Tab 3: Plus tab (creates post; if not logged in prompts login)
                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(amberColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Create Post",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = { Text("", fontSize = 10.sp) },
                    selected = false,
                    onClick = {
                        if (uiState.isAuthenticated) {
                            isCreateModalOpen = true
                        } else {
                            onOpenAuth()
                        }
                    }
                )

                // Tab 4: Analytics
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
                    label = { Text("Analytics", fontSize = 10.sp) },
                    selected = activeTab == "analytics",
                    onClick = { activeTab = "analytics" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = amberColor,
                        selectedTextColor = amberColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = slateSurface
                    )
                )

                // Tab 5: Alerts
                NavigationBarItem(
                    icon = {
                        BadgedBox(
                            badge = {
                                if (alerts.isNotEmpty()) {
                                    Badge(containerColor = amberColor, contentColor = Color.Black) {
                                        Text("${alerts.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts")
                        }
                    },
                    label = { Text("Alerts", fontSize = 10.sp) },
                    selected = activeTab == "alerts",
                    onClick = { activeTab = "alerts" },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = amberColor,
                        selectedTextColor = amberColor,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = slateSurface
                    )
                )
            }
        }
    ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(slateBg)
        ) {
            when (activeTab) {
                "feed" -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Stories Reel
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LazyRow(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    item {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable {
                                                if (uiState.isAuthenticated) {
                                                    isCreateModalOpen = true
                                                } else {
                                                    onOpenAuth()
                                                }
                                            }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .background(slateBg, CircleShape)
                                                    .border(2.dp, slateBorder, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, tint = amberColor)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Add Story", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    items(stories) { story ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable {
                                                currentStoryMedia = story.media
                                                storyModalOpen = true
                                            }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .background(
                                                        Brush.linearGradient(listOf(amberColor, Color(0xFFF43F5E))),
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(60.dp)
                                                        .background(slateBg, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    AsyncImage(
                                                        model = story.media,
                                                        contentDescription = "${story.author} Story",
                                                        modifier = Modifier
                                                            .size(56.dp)
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = story.author.substringBefore(" "),
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Create Post Box
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        // User Profile Photo
                                        val userMedia = MediaUtils.getMediaModel(uiState.userPhotoUrl)
                                        if (userMedia != null && uiState.userPhotoUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = userMedia,
                                                contentDescription = "My Photo",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .border(1.5.dp, amberColor, CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(amberColor.copy(alpha = 0.2f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (uiState.isAuthenticated && uiState.userName.isNotBlank()) uiState.userName.take(2).uppercase() else "TG",
                                                    color = amberColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(slateBg, RoundedCornerShape(12.dp))
                                                .border(1.dp, slateBorder, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    if (uiState.isAuthenticated) {
                                                        isCreateModalOpen = true
                                                    } else {
                                                        onOpenAuth()
                                                    }
                                                }
                                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                        ) {
                                            Text(
                                                text = if (uiState.isAuthenticated) "Share training highlight or match update..." else "Log in to share your sports highlights...",
                                                color = Color.Gray,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Quick Upload & Effects action buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            // Photo Button
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = slateBg,
                                                border = BorderStroke(1.dp, slateBorder),
                                                modifier = Modifier.clickable {
                                                    if (uiState.isAuthenticated) {
                                                        isCreateModalOpen = true
                                                    } else {
                                                        onOpenAuth()
                                                    }
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = amberColor, modifier = Modifier.size(14.dp))
                                                    Text("Photo", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // Video Reel Button
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = slateBg,
                                                border = BorderStroke(1.dp, slateBorder),
                                                modifier = Modifier.clickable {
                                                    if (uiState.isAuthenticated) {
                                                        isCreateModalOpen = true
                                                    } else {
                                                        onOpenAuth()
                                                    }
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(14.dp))
                                                    Text("Video", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // Effect Studio Button
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = slateBg,
                                                border = BorderStroke(1.dp, slateBorder),
                                                modifier = Modifier.clickable {
                                                    if (uiState.isAuthenticated) {
                                                        isCreateModalOpen = true
                                                    } else {
                                                        onOpenAuth()
                                                    }
                                                }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                                    Text("Effects", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (uiState.isAuthenticated) {
                                                    isCreateModalOpen = true
                                                } else {
                                                    onOpenAuth()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                                            shape = RoundedCornerShape(50.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                                        ) {
                                            Text("+ Create", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Category Filter Chips
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val categories = listOf("all", "football", "basketball", "athletics", "scout")
                                items(categories) { cat ->
                                    FilterChip(
                                        selected = selectedCategory == cat,
                                        onClick = { selectedCategory = cat },
                                        label = { Text(cat.replaceFirstChar { it.uppercase() }) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = amberColor,
                                            selectedLabelColor = Color.Black,
                                            containerColor = slateSurface,
                                            labelColor = Color.Gray
                                        )
                                    )
                                }
                            }
                        }

                        // Posts Stream
                        if (posts == null) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = amberColor)
                                }
                            }
                        } else if (posts!!.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                    Text("No posts yet — be the first to share an update!", color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            }
                        } else if (filteredPosts.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                    Text("No matching posts found.", color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            items(filteredPosts, key = { it.id }) { post ->
                            val postBorder = when (post.postStyle) {
                                "gold" -> BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                                "neon" -> BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6))))
                                "fire" -> BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFF43F5E), Color(0xFFEA580C))))
                                "scout" -> BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFFF59E0B))))
                                else -> BorderStroke(1.dp, slateBorder)
                            }

                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = postBorder,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Custom Post Style Badge if selected
                                    val styleBadgeText = when (post.postStyle) {
                                        "gold" -> "🏆 CHAMPION REEL"
                                        "neon" -> "⚡ PRO ATHLETE"
                                        "fire" -> "🔥 MATCHDAY HIGHLIGHT"
                                        "scout" -> "⭐ FKF VERIFIED"
                                        else -> null
                                    }
                                    if (styleBadgeText != null) {
                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = amberColor.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, amberColor.copy(alpha = 0.4f)),
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Text(
                                                text = styleBadgeText,
                                                color = amberColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Real user profile photo if uploaded, otherwise existing initial avatar
                                            val postPhotoModel = MediaUtils.getMediaModel(post.authorPhotoUrl)
                                            if (postPhotoModel != null && !post.authorPhotoUrl.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = postPhotoModel,
                                                    contentDescription = "${post.author} Profile Photo",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .border(1.5.dp, amberColor.copy(alpha = 0.5f), CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .background(
                                                            Brush.linearGradient(listOf(amberColor, Color(0xFFF43F5E))),
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(36.dp)
                                                            .background(slateBg, CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            post.author.take(1).uppercase(),
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = post.author,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )
                                                Text(text = post.timeAgo, fontSize = 11.sp, color = Color.Gray)
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = slateBg,
                                            border = BorderStroke(1.dp, slateBorder)
                                        ) {
                                            Text(
                                                text = post.platform,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = amberColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = post.content,
                                        fontSize = 14.sp,
                                        color = Color.LightGray,
                                        lineHeight = 20.sp
                                    )

                                    if (post.media != null) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        val isVideo = post.mediaType == "video" || MediaUtils.isMediaVideo(post.media)

                                        if (isVideo) {
                                            // Rich Sports Video Highlight Reel
                                            Card(
                                                shape = RoundedCornerShape(14.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.Black),
                                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(230.dp)
                                                    .clickable {
                                                        activeVideoUrlForPlayback = post.media
                                                        activeVideoTitleForPlayback = post.content.take(35).ifBlank { "Highlight Reel" }
                                                    }
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    // Background Gradient
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(
                                                                Brush.verticalGradient(
                                                                    listOf(Color(0xFF0F172A), Color(0xFF020617))
                                                                )
                                                            )
                                                    )

                                                    // Center Play Button & Action
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(58.dp)
                                                                .background(amberColor, CircleShape),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                Icons.Default.PlayArrow,
                                                                contentDescription = "Play Video",
                                                                tint = Color.Black,
                                                                modifier = Modifier.size(34.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Surface(
                                                            shape = RoundedCornerShape(20.dp),
                                                            color = Color.Black.copy(alpha = 0.75f),
                                                            border = BorderStroke(1.dp, amberColor.copy(alpha = 0.4f))
                                                        ) {
                                                            Text(
                                                                text = "Tap to Play Video Reel",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 11.sp,
                                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                                            )
                                                        }
                                                    }

                                                    // Top Video Tag
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = Color.Black.copy(alpha = 0.8f),
                                                        modifier = Modifier
                                                            .align(Alignment.TopStart)
                                                            .padding(10.dp)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            Icon(Icons.Default.Videocam, contentDescription = null, tint = amberColor, modifier = Modifier.size(13.dp))
                                                            Text(
                                                                text = if (post.mediaName.isNotBlank()) post.mediaName else "VIDEO REEL • HD",
                                                                color = Color.White,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            // Photo with applied PhotoEffect filter & vignette
                                            Card(
                                                shape = RoundedCornerShape(14.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.Black),
                                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(230.dp)
                                            ) {
                                                Box(modifier = Modifier.fillMaxSize()) {
                                                    AsyncImage(
                                                        model = MediaUtils.getMediaModel(post.media),
                                                        contentDescription = "Post photo",
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop,
                                                        colorFilter = MediaUtils.getEffectColorFilter(post.mediaEffect)
                                                    )

                                                    // Spotlight vignette overlay if active
                                                    if (post.mediaEffect == "spotlight") {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    Brush.radialGradient(
                                                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                                                    )
                                                                )
                                                        )
                                                    }

                                                    // Effect badge on photo if non-default
                                                    if (post.mediaEffect != "original" && post.mediaEffect.isNotBlank()) {
                                                        val effectObj = PhotoEffect.values().find { it.id == post.mediaEffect }
                                                        if (effectObj != null) {
                                                            Surface(
                                                                shape = RoundedCornerShape(20.dp),
                                                                color = Color.Black.copy(alpha = 0.75f),
                                                                border = BorderStroke(1.dp, amberColor.copy(alpha = 0.5f)),
                                                                modifier = Modifier
                                                                    .align(Alignment.TopStart)
                                                                    .padding(10.dp)
                                                            ) {
                                                                Text(
                                                                    text = "${effectObj.iconEmoji} ${effectObj.displayName}",
                                                                    color = Color.White,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(slateBg, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text(text = "${post.metrics.impressions} Impressions", fontSize = 11.sp, color = Color.Gray)
                                        Text(text = "${post.metrics.engagement}% Engagement", fontSize = 11.sp, color = amberColor, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = slateBorder)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val currentUserId = if (uiState.isAuthenticated) uiState.userEmail.ifBlank { uiState.userName } else "Guest"
                                        val isLiked = post.likes.contains(currentUserId)

                                        TextButton(onClick = {
                                            if (!uiState.isAuthenticated) {
                                                onOpenAuth()
                                                return@TextButton
                                            }
                                            if (isLiked) post.likes.remove(currentUserId) else post.likes.add(currentUserId)
                                        }) {
                                            Icon(
                                                if (isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = null,
                                                tint = if (isLiked) amberColor else Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${post.likes.size} Likes", color = if (isLiked) amberColor else Color.Gray, fontSize = 12.sp)
                                        }

                                        TextButton(onClick = { post.showComments = !post.showComments }) {
                                            Icon(Icons.Outlined.Comment, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${post.comments.size} Comments", color = Color.Gray, fontSize = 12.sp)
                                        }

                                        TextButton(onClick = { /* Share */ }) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Share", color = Color.Gray, fontSize = 12.sp)
                                        }
                                    }

                                    if (post.showComments) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            HorizontalDivider(color = slateBorder)
                                            for (comment in post.comments) {
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = slateBg,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(comment.content, fontSize = 12.sp, color = Color.LightGray)
                                                    }
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                OutlinedTextField(
                                                    value = post.newCommentText,
                                                    onValueChange = { post.newCommentText = it },
                                                    placeholder = { Text("Write a comment...", fontSize = 12.sp, color = Color.Gray) },
                                                    modifier = Modifier.weight(1f).height(48.dp),
                                                    shape = RoundedCornerShape(50.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedTextColor = Color.White,
                                                        unfocusedTextColor = Color.White,
                                                        focusedBorderColor = amberColor,
                                                        unfocusedBorderColor = slateBorder
                                                    )
                                                )
                                                Button(
                                                    onClick = {
                                                        if (!uiState.isAuthenticated) {
                                                            onOpenAuth()
                                                            return@Button
                                                        }
                                                        if (post.newCommentText.isNotBlank()) {
                                                            val authorName = if (uiState.userName.isNotBlank() && uiState.userName != "Athlete") uiState.userName else "Verified Athlete"
                                                            post.comments.add(CommentItem(System.currentTimeMillis(), authorName, post.newCommentText.trim()))
                                                            post.newCommentText = ""
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                                                    shape = RoundedCornerShape(50.dp)
                                                ) {
                                                    Text("Send", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        }

                        // Trending Section
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = amberColor, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Trending Athletes", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    val trending = listOf("James Njoroge" to "88% Dev", "Sarah Mutuku" to "96% Dev", "Alex Kiprop" to "91% Dev")
                                    trending.forEach { (name, score) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                            Text(score, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = amberColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "analytics" -> {
                    // Analytics Grid View
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.BarChart, contentDescription = null, tint = amberColor)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Real-Time Analytics Grid", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Cross-platform impression metrics and audience engagement.", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        // 5 Stat Cards
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Total Impressions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.totalImpressions}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Real-time live", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Engagement Rate", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.engagementRate}%", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Calculated live", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Scout Radar Pings", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.scoutRadarPings}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Network active", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Verified Video Plays", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.verifiedVideoPlays}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Stream verified", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Profile & Discovery Views", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text("${analytics.profileViews}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                    Text("Audience impressions from feed & discovery networks", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Profile Comments", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.profileComments}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Real engagement", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = slateSurface),
                                    border = BorderStroke(1.dp, slateBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text("Profile Reactions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text("${analytics.profileReactions}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(vertical = 4.dp))
                                        Text("Likes & reactions", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                "alerts" -> {
                    // Alerts Feed View
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Alerts & Notifications",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { clearAllAlerts = true }) {
                                    Text("Clear All", color = amberColor, fontSize = 12.sp)
                                }
                            }
                        }

                        if (displayedAlerts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No new alerts at this time.", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }

                        items(displayedAlerts) { alert ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = slateSurface),
                                border = BorderStroke(1.dp, slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(amberColor.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = amberColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = alert.title,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = alert.timeAgo,
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = alert.description,
                                            color = Color.LightGray,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(50.dp),
                                            color = slateBg
                                        ) {
                                            Text(
                                                text = alert.category,
                                                color = amberColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
    }

    // Create Post Modal with Photo/Video Upload & Effects Studio
    CreatePostModal(
        isOpen = isCreateModalOpen,
        onDismiss = { isCreateModalOpen = false },
        uiState = uiState,
        onOpenAuth = onOpenAuth,
        onPublishPost = { content, platform, category, mediaUri, mediaType, mediaName, photoEffect, postStyle ->
            coroutineScope.launch {
                val authorId = if (uiState.isAuthenticated) uiState.userEmail.ifBlank { uiState.userName } else "user_guest"
                val authorEmail = if (uiState.isAuthenticated) uiState.userEmail.ifBlank { "guest@talentgraph.com" } else "guest@talentgraph.com"
                val authorName = if (uiState.userName.isNotBlank() && uiState.userName != "Athlete") uiState.userName else "Verified Athlete"
                val authorPhotoUrl = uiState.userPhotoUrl.takeIf { it.isNotBlank() }

                feedRepository.createPost(
                    authorId = authorId,
                    authorEmail = authorEmail,
                    authorName = authorName,
                    authorPhotoUrl = authorPhotoUrl,
                    content = content,
                    mediaUrl = mediaUri?.toString(),
                    mediaType = mediaType
                )
                isCreateModalOpen = false
            }
        }
    )

    // Highlight Reel Video Player Dialog
    if (activeVideoUrlForPlayback != null) {
        FeedVideoPlayerDialog(
            videoUrl = activeVideoUrlForPlayback!!,
            title = activeVideoTitleForPlayback,
            onDismiss = { activeVideoUrlForPlayback = null }
        )
    }

    // Story Dialog
    if (storyModalOpen) {
        Dialog(onDismissRequest = { storyModalOpen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f)
                    .background(Color.Black, RoundedCornerShape(24.dp))
                    .border(1.dp, slateBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = currentStoryMedia,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { storyModalOpen = false },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }

    // Messages Dialog
    if (showMessagesDialog) {
        Dialog(onDismissRequest = { showMessagesDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = slateSurface,
                border = BorderStroke(1.dp, slateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = amberColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Direct Messages", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        IconButton(onClick = { showMessagesDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val messageThreads = listOf(
                        Triple("Coach Marcus", "Your elevation on the jump shot looks sharp. Bring game footage tomorrow!", "10m ago"),
                        Triple("FKF Scout Desk", "Profile verification review in progress.", "1h ago"),
                        Triple("Coastal FC Director", "We would like to discuss training squad trial options.", "Yesterday")
                    )

                    messageThreads.forEach { (sender, snippet, time) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = slateBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(sender, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(time, color = Color.Gray, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(snippet, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!uiState.isAuthenticated) {
                        Button(
                            onClick = {
                                showMessagesDialog = false
                                onOpenAuth()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log in to Reply & Send Messages", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    } else {
                        Button(
                            onClick = { showMessagesDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = slateBg, contentColor = Color.White),
                            border = BorderStroke(1.dp, slateBorder),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
