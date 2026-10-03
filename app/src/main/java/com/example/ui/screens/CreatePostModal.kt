package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.TalentUiState
import com.example.ui.util.MediaUtils
import com.example.ui.util.PhotoEffect
import com.example.ui.util.PostCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    uiState: TalentUiState,
    onOpenAuth: () -> Unit,
    onPublishPost: (
        content: String,
        platform: String,
        category: String,
        mediaUri: String?,
        mediaType: String,
        mediaName: String,
        photoEffect: String,
        postStyle: String
    ) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var contentText by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("twitter") }
    var selectedCategory by remember { mutableStateOf("football") }

    // Media state
    var mediaTypeTab by remember { mutableStateOf("photo") } // "photo", "video", "none"
    var selectedMediaUri by remember { mutableStateOf<String?>(null) }
    var selectedMediaType by remember { mutableStateOf("none") } // "photo", "video", "none"
    var selectedMediaName by remember { mutableStateOf("") }

    // Effects state
    var selectedPhotoEffect by remember { mutableStateOf(PhotoEffect.ORIGINAL) }
    var selectedPostStyle by remember { mutableStateOf(PostCardStyle.DEFAULT) }

    // Photo pickers
    val photoContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = MediaUtils.getFileNameFromUri(context, uri)
            val path = MediaUtils.copyUriToStorage(context, uri, "post_photo", "jpg")
            selectedMediaUri = path
            selectedMediaType = "photo"
            selectedMediaName = name
            mediaTypeTab = "photo"
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = MediaUtils.getFileNameFromUri(context, uri)
            val path = MediaUtils.copyUriToStorage(context, uri, "post_photo", "jpg")
            selectedMediaUri = path
            selectedMediaType = "photo"
            selectedMediaName = name
            mediaTypeTab = "photo"
        }
    }

    // Video pickers
    val videoContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = MediaUtils.getFileNameFromUri(context, uri)
            val path = MediaUtils.copyUriToStorage(context, uri, "post_video", "mp4")
            selectedMediaUri = path
            selectedMediaType = "video"
            selectedMediaName = name
            mediaTypeTab = "video"
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = MediaUtils.getFileNameFromUri(context, uri)
            val path = MediaUtils.copyUriToStorage(context, uri, "post_video", "mp4")
            selectedMediaUri = path
            selectedMediaType = "video"
            selectedMediaName = name
            mediaTypeTab = "video"
        }
    }

    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateSurface = Color(0xFF0F172A)
    val slateBorder = Color(0xFF334155)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = slateSurface,
            border = BorderStroke(1.dp, slateBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(amberColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = amberColor, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Create Feed Post", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Share photo, video reel & athletic moments", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Author & Target Settings
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = slateBg,
                        border = BorderStroke(1.dp, slateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val userPhoto = MediaUtils.getMediaModel(uiState.userPhotoUrl)
                                    if (userPhoto != null && uiState.userPhotoUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = userPhoto,
                                            contentDescription = "User Photo",
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, amberColor, CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(amberColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (uiState.isAuthenticated && uiState.userName.isNotBlank()) uiState.userName.take(1).uppercase() else "A",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = if (uiState.isAuthenticated && uiState.userName.isNotBlank()) uiState.userName else "Verified Athlete",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = if (uiState.isAuthenticated) "Role: ${uiState.userRole.name}" else "Tap to authenticate",
                                            color = amberColor,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = slateSurface,
                                    border = BorderStroke(1.dp, slateBorder)
                                ) {
                                    Text(
                                        text = selectedPlatform.uppercase(),
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // Sports Category selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Sport:", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                val categories = listOf("football", "basketball", "athletics", "rugby", "fitness", "scout")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(categories) { cat ->
                                        val isSelected = selectedCategory == cat
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isSelected) amberColor else slateSurface,
                                            border = BorderStroke(1.dp, if (isSelected) amberColor else slateBorder),
                                            modifier = Modifier.clickable { selectedCategory = cat }
                                        ) {
                                            Text(
                                                text = cat.replaceFirstChar { it.uppercase() },
                                                color = if (isSelected) Color.Black else Color.LightGray,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Caption / Text Input
                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        placeholder = {
                            Text(
                                "What's happening in your sports journey? Add match reflections, stats, personal bests...",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = amberColor,
                            unfocusedBorderColor = slateBorder,
                            focusedContainerColor = slateBg,
                            unfocusedContainerColor = slateBg
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // 1. MEDIA UPLOAD SECTION (Local Machine: Photo or Video)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "UPLOAD MEDIA FROM DEVICE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = amberColor,
                                letterSpacing = 0.5.sp
                            )
                            if (selectedMediaUri != null) {
                                TextButton(
                                    onClick = {
                                        selectedMediaUri = null
                                        selectedMediaType = "none"
                                        selectedMediaName = ""
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remove Media", fontSize = 11.sp, color = Color(0xFFF43F5E))
                                }
                            }
                        }

                        // Media Type Segmented Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(slateBg, RoundedCornerShape(12.dp))
                                .border(1.dp, slateBorder, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Photo Tab
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (mediaTypeTab == "photo") amberColor else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { mediaTypeTab = "photo" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = if (mediaTypeTab == "photo") Color.Black else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Photo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (mediaTypeTab == "photo") Color.Black else Color.Gray
                                    )
                                }
                            }

                            // Video Tab
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (mediaTypeTab == "video") amberColor else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { mediaTypeTab = "video" }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.VideoCall,
                                        contentDescription = null,
                                        tint = if (mediaTypeTab == "video") Color.Black else Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Video Reel",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (mediaTypeTab == "video") Color.Black else Color.Gray
                                    )
                                }
                            }
                        }

                        // Upload Controls depending on Tab
                        if (mediaTypeTab == "photo") {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = slateBg,
                                border = BorderStroke(1.dp, if (selectedMediaType == "photo") amberColor.copy(alpha = 0.6f) else slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Select Photo from Local Machine",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        "JPEG, PNG, WebP from Local Files or Android Gallery",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { photoContentLauncher.launch("image/*") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, slateBorder),
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Browse Files", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Photo Picker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Quick local test button
                                    OutlinedButton(
                                        onClick = {
                                            val (sampleUri, sampleName) = MediaUtils.createSampleLocalPhoto(context)
                                            selectedMediaUri = sampleUri
                                            selectedMediaType = "photo"
                                            selectedMediaName = sampleName
                                            if (contentText.isBlank()) {
                                                contentText = "Strong morning speed training session! Form feels dialed in. 🏃‍♂️💨"
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFF0D9488)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2DD4BF)),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Quick Test: Use Sample Athlete Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            // Video Upload Tab
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = slateBg,
                                border = BorderStroke(1.dp, if (selectedMediaType == "video") amberColor.copy(alpha = 0.6f) else slateBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Select Video from Local Machine",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        "MP4, MKV, MOV clips from Local Machine or Video Gallery",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { videoContentLauncher.launch("video/*") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, slateBorder),
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Browse Files", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                videoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6), contentColor = Color.White),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(40.dp)
                                        ) {
                                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Video Picker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Quick local test button
                                    OutlinedButton(
                                        onClick = {
                                            val (sampleUri, sampleName) = MediaUtils.createSampleLocalVideo(context)
                                            selectedMediaUri = sampleUri
                                            selectedMediaType = "video"
                                            selectedMediaName = sampleName
                                            if (contentText.isBlank()) {
                                                contentText = "Game film breakdown: Transition sprint and tactical positioning from the weekend match. ⚽🎥"
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFF8B5CF6)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA78BFA)),
                                        modifier = Modifier.fillMaxWidth().height(36.dp)
                                    ) {
                                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Quick Test: Use Sample Highlight Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Selected Media Live Preview Card
                        if (selectedMediaUri != null) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.5.dp, amberColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                if (selectedMediaType == "video") Icons.Default.Videocam else Icons.Default.Image,
                                                contentDescription = null,
                                                tint = amberColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (selectedMediaType == "video") "Selected Video Attached" else "Selected Photo Attached",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF064E3B)
                                        ) {
                                            Text(
                                                text = selectedMediaName.ifBlank { if (selectedMediaType == "video") "highlight.mp4" else "photo.jpg" },
                                                color = Color(0xFF34D399),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    // Preview container
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (selectedMediaType == "photo") {
                                            AsyncImage(
                                                model = MediaUtils.getMediaModel(selectedMediaUri),
                                                contentDescription = "Selected Photo Preview",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                                colorFilter = MediaUtils.getEffectColorFilter(selectedPhotoEffect.id)
                                            )
                                            // Spotlight overlay
                                            if (selectedPhotoEffect == PhotoEffect.SPOTLIGHT) {
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

                                            // Effect pill on preview
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = Color.Black.copy(alpha = 0.7f),
                                                border = BorderStroke(1.dp, amberColor),
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(8.dp)
                                            ) {
                                                Text(
                                                    text = "${selectedPhotoEffect.iconEmoji} ${selectedPhotoEffect.displayName}",
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        } else {
                                            // Video preview placeholder
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .background(amberColor, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text("Local Video Highlight Ready", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Will be playable in feed post", color = Color.Gray, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. PHOTO & POST EFFECTS STUDIO SECTION
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "PHOTO & POST EFFECT STUDIO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = amberColor,
                            letterSpacing = 0.5.sp
                        )

                        // Photo Visual Filter Effects
                        Text(
                            "Photo Filter Effect (${selectedPhotoEffect.displayName}):",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.SemiBold
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(PhotoEffect.values()) { effect ->
                                val isSelected = selectedPhotoEffect == effect
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) amberColor.copy(alpha = 0.2f) else slateBg,
                                    border = BorderStroke(1.5.dp, if (isSelected) amberColor else slateBorder),
                                    modifier = Modifier.clickable { selectedPhotoEffect = effect }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(effect.iconEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            effect.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) amberColor else Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Post Card Styling / Frame Effect
                        Text(
                            "Post Frame Style (${selectedPostStyle.displayName}):",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            fontWeight = FontWeight.SemiBold
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(PostCardStyle.values()) { style ->
                                val isSelected = selectedPostStyle == style
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFF1E293B) else slateBg,
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        when (style) {
                                            PostCardStyle.GOLD_CHAMPION -> Color(0xFFF59E0B)
                                            PostCardStyle.NEON_PULSE -> Color(0xFF06B6D4)
                                            PostCardStyle.CRIMSON_FIRE -> Color(0xFFF43F5E)
                                            PostCardStyle.SCOUT_VERIFIED -> Color(0xFF10B981)
                                            PostCardStyle.DEFAULT -> if (isSelected) amberColor else slateBorder
                                        }
                                    ),
                                    modifier = Modifier.clickable { selectedPostStyle = style }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(
                                                    when (style) {
                                                        PostCardStyle.GOLD_CHAMPION -> Color(0xFFF59E0B)
                                                        PostCardStyle.NEON_PULSE -> Color(0xFF06B6D4)
                                                        PostCardStyle.CRIMSON_FIRE -> Color(0xFFF43F5E)
                                                        PostCardStyle.SCOUT_VERIFIED -> Color(0xFF10B981)
                                                        PostCardStyle.DEFAULT -> Color.Gray
                                                    },
                                                    CircleShape
                                                )
                                        )
                                        Text(
                                            text = style.displayName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button
                Button(
                    onClick = {
                        if (!uiState.isAuthenticated) {
                            onDismiss()
                            onOpenAuth()
                            return@Button
                        }
                        if (contentText.isNotBlank() || selectedMediaUri != null) {
                            onPublishPost(
                                contentText.ifBlank { if (selectedMediaType == "video") "Highlight reel clip" else "Athletic training snapshot" },
                                selectedPlatform,
                                selectedCategory,
                                selectedMediaUri,
                                selectedMediaType,
                                selectedMediaName,
                                selectedPhotoEffect.id,
                                selectedPostStyle.id
                            )
                            onDismiss()
                        }
                    },
                    enabled = contentText.isNotBlank() || selectedMediaUri != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = amberColor,
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF334155),
                        disabledContentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!uiState.isAuthenticated) "Log in to Publish Post" else "Publish Post to Feed",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
