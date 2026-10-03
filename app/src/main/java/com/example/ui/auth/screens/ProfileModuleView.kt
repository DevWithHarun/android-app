package com.example.ui.auth.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.TalentUiState
import com.example.ui.TalentViewModel
import java.io.File
import java.io.FileOutputStream

// Utility: Convert any local path, file URI, content URI, or web link to Coil-compatible model
fun getMediaModel(urlOrPath: String): Any {
    return com.example.ui.util.MediaUtils.getMediaModel(urlOrPath) ?: urlOrPath
}

// Utility functions delegated to centralized MediaUtils
fun copyUriToStorage(context: Context, uri: Uri, prefix: String, fallbackExtension: String): String =
    com.example.ui.util.MediaUtils.copyUriToStorage(context, uri, prefix, fallbackExtension)

fun getFileNameFromUri(context: Context, uri: Uri): String =
    com.example.ui.util.MediaUtils.getFileNameFromUri(context, uri)

fun createSampleLocalPhoto(context: Context): Pair<String, String> =
    com.example.ui.util.MediaUtils.createSampleLocalPhoto(context)

fun createSampleLocalVideo(context: Context): Pair<String, String> =
    com.example.ui.util.MediaUtils.createSampleLocalVideo(context)


@Composable
fun ProfileModuleView(
    viewModel: TalentViewModel,
    uiState: TalentUiState
) {
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val surfaceColor = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var showEditModal by remember { mutableStateOf(false) }
    var initialEditTab by remember { mutableStateOf("photo") }
    var showVideoPlayer by remember { mutableStateOf(false) }

    // Read real user data from Firestore profile and local persistence
    val userData = uiState.userData ?: emptyMap()
    val fullName = com.example.ui.util.AthleteNameResolver.resolveFromMap(userData).ifBlank { uiState.userName }.ifBlank { uiState.userEmail.substringBefore("@").replaceFirstChar { it.uppercase() } }.ifBlank { "Athlete" }
    val userEmail = uiState.userEmail.ifBlank { "Not provided" }
    val country = (userData["country"] as? String ?: "").ifBlank { "Not provided" }
    val location = (userData["location"] as? String ?: userData["town"] as? String ?: "").ifBlank { "Not provided" }
    val currentTeam = (userData["currentTeam"] as? String ?: userData["team"] as? String ?: "").ifBlank { "Unassigned" }
    val clubName = (userData["clubName"] as? String ?: userData["club"] as? String ?: "").ifBlank { "Independent" }
    val sport = (userData["sport"] as? String ?: "").ifBlank { "Football" }
    val position = (userData["position"] as? String ?: "").ifBlank { "Not specified" }
    val secondaryPosition = (userData["secondaryPosition"] as? String ?: "").ifBlank { "None" }
    val preferredFoot = (userData["preferredFoot"] as? String ?: "").ifBlank { "Not specified" }
    val jerseyNumber = (userData["jerseyNumber"] as? String ?: "").ifBlank { "-" }
    val federationId = (userData["federationId"] as? String ?: "").ifBlank { "Pending Registration" }
    val competitionLevel = (userData["competitionLevel"] as? String ?: "").ifBlank { "Not specified" }
    val dob = (userData["dob"] as? String ?: userData["dateOfBirth"] as? String ?: "").ifBlank { "Not provided" }
    val nationality = (userData["nationality"] as? String ?: "").ifBlank { "Not provided" }
    val height = (userData["height"] as? String ?: "").ifBlank { "Not specified" }
    val weight = (userData["weight"] as? String ?: "").ifBlank { "Not specified" }
    val phone = (userData["phone"] as? String ?: "").ifBlank { "Not provided" }
    val emergencyContact = (userData["emergencyContact"] as? String ?: "").ifBlank { "Not provided" }
    val bio = (userData["bio"] as? String ?: "").ifBlank { "No bio added yet." }
    val videoTitle = (userData["videoTitle"] as? String ?: "").ifBlank { "Highlight Reel" }
    val videoKpi = (userData["videoKpi"] as? String ?: "").ifBlank { "" }
    val videoUrl = (userData["videoUrl"] as? String ?: "").ifBlank { "" }
    val videoFileName = (userData["videoFileName"] as? String ?: "").ifBlank { "" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header & Public Identity Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(secondaryColor.copy(alpha = 0.1f))
                            .border(2.5.dp, secondaryColor, CircleShape)
                            .clickable {
                                initialEditTab = "photo"
                                showEditModal = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.userPhotoUrl.isNotBlank()) {
                            AsyncImage(
                                model = getMediaModel(uiState.userPhotoUrl),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = fullName.take(2).uppercase(),
                                color = secondaryColor,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(primaryColor, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Change photo", tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$position • $currentTeam",
                        fontSize = 14.sp,
                        color = textMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$location, $country",
                        fontSize = 12.sp,
                        color = secondaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        VerificationBadge("Public Profile: Active")
                        VerificationBadge("Identity Verified")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                initialEditTab = "details"
                                showEditModal = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                initialEditTab = "photo"
                                showEditModal = true
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            border = BorderStroke(1.dp, borderColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Photo", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Bio Card
        item {
            ProfileSectionCard(title = "Athlete Bio", icon = Icons.Outlined.Person) {
                Text(
                    text = bio,
                    fontSize = 13.sp,
                    color = primaryColor,
                    lineHeight = 20.sp
                )
            }
        }

        // Video & Media Showcase Card
        item {
            ProfileSectionCard(title = "Video & Highlight Reel", icon = Icons.Default.VideoCall) {
                ProfileDetailRow("Highlight Title", videoTitle)
                ProfileDetailRow("Linked KPI", videoKpi)
                if (videoFileName.isNotBlank()) {
                    ProfileDetailRow("Local Video File", videoFileName)
                }

                if (videoUrl.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDFA),
                        border = BorderStroke(1.dp, Color(0xFFCCFBF1)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clickable { showVideoPlayer = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(secondaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (videoFileName.isNotBlank()) videoFileName else videoTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                                Text(
                                    text = if (videoUrl.startsWith("file:") || videoUrl.startsWith("/")) "Saved in local device storage • Tap to play" else "Ready to play • Tap to watch",
                                    fontSize = 11.sp,
                                    color = textMuted,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = { showVideoPlayer = true },
                                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Play", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = textMuted, modifier = Modifier.size(20.dp))
                            Text(
                                text = "No highlight video uploaded yet. Tap below to upload a video from your local device storage or enter a URL.",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            initialEditTab = "video"
                            showEditModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            initialEditTab = "photo"
                            showEditModal = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                        border = BorderStroke(1.dp, borderColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Details: Identity & Legal Card
        item {
            ProfileSectionCard(title = "Identity & Legal Details", icon = Icons.Default.Security) {
                ProfileDetailRow("Full Name", fullName)
                ProfileDetailRow("Country", country)
                ProfileDetailRow("Location / Town", location)
                ProfileDetailRow("Date of Birth", dob)
                ProfileDetailRow("Nationality", nationality)
            }
        }

        // Sporting Identity Card
        item {
            ProfileSectionCard(title = "Sporting Profile", icon = Icons.Default.SportsSoccer) {
                ProfileDetailRow("Sport", sport)
                ProfileDetailRow("Primary Position", position)
                ProfileDetailRow("Secondary Position", secondaryPosition)
                ProfileDetailRow("Current Team", currentTeam)
                ProfileDetailRow("Club Name", clubName)
                ProfileDetailRow("Preferred Foot", preferredFoot)
                ProfileDetailRow("Jersey Number", jerseyNumber)
                ProfileDetailRow("Competition Level", competitionLevel)
                ProfileDetailRow("Federation ID", federationId)
            }
        }

        // Physical Profile Card
        item {
            ProfileSectionCard(title = "Physical Profile & Metrics", icon = Icons.Default.FitnessCenter) {
                ProfileDetailRow("Height", height)
                ProfileDetailRow("Weight", weight)
                ProfileDetailRow("Dominant Side", preferredFoot)
            }
        }

        // Contact Information Card
        item {
            ProfileSectionCard(title = "Contact Information", icon = Icons.Default.ContactPhone) {
                ProfileDetailRow("Email", userEmail)
                ProfileDetailRow("Phone", phone)
                ProfileDetailRow("Emergency Contact", emergencyContact)
                ProfileDetailRow("Preferred Channel", "In-App Secure Messaging")
            }
        }

        // Verification Status Summary Card
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(18.dp))
                        Text("VERIFICATION BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textMuted, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val idStatus = if (userData["idVerified"] == true) "Verified" else "Self-Declared"
                    val clubStatus = if (clubName != "Independent") "$clubName Active" else "Independent"
                    val fedStatus = if (federationId != "Pending Registration") "$federationId Active" else "Pending Setup"
                    val testStatus = if (height != "Not specified" || weight != "Not specified") "Recorded" else "Pending"

                    VerificationStatusRow("Identity & Legal", idStatus)
                    VerificationStatusRow("Club Affiliation", clubStatus)
                    VerificationStatusRow("Sporting Credentials", fedStatus)
                    VerificationStatusRow("Physical Tests", testStatus)
                }
            }
        }
    }

    // Video Player Dialog for watching local and remote highlight videos
    if (showVideoPlayer && videoUrl.isNotBlank()) {
        VideoPlayerDialog(
            videoUrl = videoUrl,
            videoTitle = if (videoFileName.isNotBlank()) videoFileName else videoTitle,
            onDismiss = { showVideoPlayer = false }
        )
    }

    // Multi-Tab Edit Profile Modal
    if (showEditModal) {
        EditProfileModal(
            initialTab = initialEditTab,
            currentName = fullName,
            currentPhotoUrl = uiState.userPhotoUrl,
            currentCountry = country,
            currentLocation = location,
            currentTeam = currentTeam,
            currentClub = clubName,
            currentSport = sport,
            currentPosition = position,
            currentSecPosition = secondaryPosition,
            currentPreferredFoot = preferredFoot,
            currentJersey = jerseyNumber,
            currentDob = dob,
            currentPhone = phone,
            currentHeight = height,
            currentWeight = weight,
            currentBio = bio,
            currentVideoTitle = videoTitle,
            currentVideoKpi = videoKpi,
            currentVideoUrl = videoUrl,
            currentVideoFileName = videoFileName,
            onDismiss = { showEditModal = false },
            onSave = { updates ->
                viewModel.updateUserProfile(updates)
                showEditModal = false
            }
        )
    }
}

// In-app Video Player Dialog with Android VideoView & MediaController
@Composable
fun VideoPlayerDialog(
    videoUrl: String,
    videoTitle: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = videoTitle.ifBlank { "Highlight Reel" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        Text(
                            text = if (videoUrl.startsWith("file:") || videoUrl.startsWith("/")) "Playing from local storage" else "Online stream",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val mediaController = MediaController(ctx)
                                mediaController.setAnchorView(this)
                                setMediaController(mediaController)

                                val parsedUri = when {
                                    videoUrl.startsWith("content:") || videoUrl.startsWith("file:") -> Uri.parse(videoUrl)
                                    videoUrl.startsWith("/") -> Uri.fromFile(File(videoUrl))
                                    else -> Uri.parse(videoUrl)
                                }
                                setVideoURI(parsedUri)
                                setOnPreparedListener { mp ->
                                    mp.isLooping = true
                                    start()
                                }
                                setOnErrorListener { _, _, _ ->
                                    true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileModal(
    initialTab: String = "photo",
    currentName: String,
    currentPhotoUrl: String,
    currentCountry: String,
    currentLocation: String,
    currentTeam: String,
    currentClub: String,
    currentSport: String,
    currentPosition: String,
    currentSecPosition: String,
    currentPreferredFoot: String,
    currentJersey: String,
    currentDob: String,
    currentPhone: String,
    currentHeight: String,
    currentWeight: String,
    currentBio: String,
    currentVideoTitle: String,
    currentVideoKpi: String,
    currentVideoUrl: String,
    currentVideoFileName: String,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any?>) -> Unit
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(initialTab) }

    var editName by remember { mutableStateOf(currentName) }
    var editPhotoUrl by remember { mutableStateOf(currentPhotoUrl) }
    var editPhotoFileName by remember { mutableStateOf("") }

    var editCountry by remember { mutableStateOf(currentCountry) }
    var editLocation by remember { mutableStateOf(currentLocation) }
    var editTeam by remember { mutableStateOf(currentTeam) }
    var editClub by remember { mutableStateOf(currentClub) }
    var editSport by remember { mutableStateOf(currentSport) }
    var editPosition by remember { mutableStateOf(currentPosition) }
    var editSecPosition by remember { mutableStateOf(currentSecPosition) }
    var editPreferredFoot by remember { mutableStateOf(currentPreferredFoot) }
    var editJersey by remember { mutableStateOf(currentJersey) }
    var editDob by remember { mutableStateOf(currentDob) }
    var editPhone by remember { mutableStateOf(currentPhone) }
    var editHeight by remember { mutableStateOf(currentHeight) }
    var editWeight by remember { mutableStateOf(currentWeight) }
    var editBio by remember { mutableStateOf(currentBio) }

    var editVideoTitle by remember { mutableStateOf(currentVideoTitle) }
    var editVideoKpi by remember { mutableStateOf(currentVideoKpi) }
    var editVideoUrl by remember { mutableStateOf(currentVideoUrl) }
    var editVideoFileName by remember { mutableStateOf(currentVideoFileName) }

    // Universal Device Content Launcher (Files, Gallery, Downloads)
    val photoContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            editPhotoFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "profile_photo", "jpg")
            editPhotoUrl = savedPath
        }
    }

    // Modern Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            editPhotoFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "profile_photo", "jpg")
            editPhotoUrl = savedPath
        }
    }

    // Universal Device Video Content Launcher (Files, Storage, Videos, Downloads)
    val videoContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            editVideoFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "highlight_reel", "mp4")
            editVideoUrl = savedPath
            if (editVideoTitle.isBlank() || editVideoTitle == "Highlight Reel" || editVideoTitle == "Season Highlights") {
                editVideoTitle = fileName.substringBeforeLast(".")
            }
        }
    }

    // Modern Video Picker Launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = getFileNameFromUri(context, uri)
            editVideoFileName = fileName
            val savedPath = copyUriToStorage(context, uri, "highlight_reel", "mp4")
            editVideoUrl = savedPath
            if (editVideoTitle.isBlank() || editVideoTitle == "Highlight Reel" || editVideoTitle == "Season Highlights") {
                editVideoTitle = fileName.substringBeforeLast(".")
            }
        }
    }

    val primaryColor = Color(0xFF1E293B)
    val secondaryColor = Color(0xFF0D9488)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Your Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = primaryColor
                        )
                        Text(
                            text = "Upload photo, details, video, showcase, and bio.",
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textMuted)
                    }
                }

                HorizontalDivider(color = borderColor)

                // 5 Tab Header Bar: Photo | Details | Video | Showcase | Bio
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EditTabButton("Photo", Icons.Default.CameraAlt, activeTab == "photo", Modifier.weight(1f)) { activeTab = "photo" }
                    EditTabButton("Details", Icons.Default.LocationOn, activeTab == "details", Modifier.weight(1f)) { activeTab = "details" }
                    EditTabButton("Video", Icons.Default.Videocam, activeTab == "video", Modifier.weight(1f)) { activeTab = "video" }
                    EditTabButton("Showcase", Icons.Default.Star, activeTab == "showcase", Modifier.weight(1f)) { activeTab = "showcase" }
                    EditTabButton("Bio", Icons.Default.Person, activeTab == "bio", Modifier.weight(1f)) { activeTab = "bio" }
                }

                HorizontalDivider(color = borderColor)

                // Tab Content Pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    when (activeTab) {
                        "photo" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                item {
                                    // Interactive Local Upload Box
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                            .clickable {
                                                photoContentLauncher.launch("image/*")
                                            }
                                            .padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(86.dp)
                                                .clip(CircleShape)
                                                .background(secondaryColor.copy(alpha = 0.1f))
                                                .border(2.5.dp, secondaryColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (editPhotoUrl.isNotBlank()) {
                                                AsyncImage(
                                                    model = getMediaModel(editPhotoUrl),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Text(editName.take(2).uppercase(), fontWeight = FontWeight.Bold, color = secondaryColor, fontSize = 26.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Upload Photo from Local Storage",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "Select from device files, gallery, or downloads",
                                            fontSize = 11.sp,
                                            color = textMuted
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { photoContentLauncher.launch("image/*") },
                                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Browse Files", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                                                border = BorderStroke(1.dp, borderColor),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Gallery Picker", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        // Quick local test button to test photo upload without existing files on emulator
                                        TextButton(
                                            onClick = {
                                                val (savedPath, name) = createSampleLocalPhoto(context)
                                                if (savedPath.isNotBlank()) {
                                                    editPhotoUrl = savedPath
                                                    editPhotoFileName = name
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Quick Test: Use Sample Athlete Portrait", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (editPhotoFileName.isNotBlank() || (editPhotoUrl.isNotBlank() && !editPhotoUrl.startsWith("http"))) {
                                    item {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFECFDF5),
                                            border = BorderStroke(1.dp, Color(0xFFD1FAE5)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                                Column {
                                                    Text("Photo Ready:", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                                    Text(if (editPhotoFileName.isNotBlank()) editPhotoFileName else "Saved in local app storage", fontSize = 11.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                                        Text("  OR ADD PHOTO URL  ", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = editPhotoUrl,
                                        onValueChange = { editPhotoUrl = it },
                                        label = { Text("Profile Photo URL or Path") },
                                        placeholder = { Text("https://example.com/photo.jpg or local path") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        "details" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                item {
                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it },
                                        label = { Text("Full Legal / Display Name") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editCountry,
                                        onValueChange = { editCountry = it },
                                        label = { Text("Country") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editLocation,
                                        onValueChange = { editLocation = it },
                                        label = { Text("Location / Town") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editTeam,
                                        onValueChange = { editTeam = it },
                                        label = { Text("Current Team") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editClub,
                                        onValueChange = { editClub = it },
                                        label = { Text("Club Name") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editDob,
                                        onValueChange = { editDob = it },
                                        label = { Text("Date of Birth") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editPhone,
                                        onValueChange = { editPhone = it },
                                        label = { Text("Phone Number") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        "video" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                item {
                                    // Interactive Video Upload Box
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(2.dp, Color(0xFFCBD5E1), RoundedCornerShape(16.dp))
                                            .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                                            .clickable {
                                                videoContentLauncher.launch("video/*")
                                            }
                                            .padding(18.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(Color(0xFFF3E8FF), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(28.dp))
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Upload Video from Local Storage",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = primaryColor
                                        )
                                        Text(
                                            text = "MP4, MKV, MOV, WebM from Gallery, Files, Downloads",
                                            fontSize = 11.sp,
                                            color = textMuted
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { videoContentLauncher.launch("video/*") },
                                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Browse Videos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                                                border = BorderStroke(1.dp, borderColor),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Video Picker", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        // Quick test button to test video upload without needing local MP4 files
                                        TextButton(
                                            onClick = {
                                                val (savedPath, name) = createSampleLocalVideo(context)
                                                editVideoUrl = savedPath
                                                editVideoFileName = name
                                                if (editVideoTitle.isBlank() || editVideoTitle == "Highlight Reel") {
                                                    editVideoTitle = "Sprint Mechanics & Box-to-Box Reel"
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = secondaryColor, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Quick Test: Use Sample Highlight Video", fontSize = 11.sp, color = secondaryColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (editVideoFileName.isNotBlank() || (editVideoUrl.isNotBlank() && !editVideoUrl.startsWith("http"))) {
                                    item {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFECFDF5),
                                            border = BorderStroke(1.dp, Color(0xFFD1FAE5)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                                Column {
                                                    Text("Selected Video:", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                                    Text(if (editVideoFileName.isNotBlank()) editVideoFileName else "Saved in local storage", fontSize = 12.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = editVideoTitle,
                                        onValueChange = { editVideoTitle = it },
                                        label = { Text("Highlight Reel Title") },
                                        placeholder = { Text("e.g. Sprint Mechanics & Goal Distribution") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editVideoKpi,
                                        onValueChange = { editVideoKpi = it },
                                        label = { Text("Linked KPI Focus") },
                                        placeholder = { Text("e.g. Performance / Efficiency / Consistency / Risk") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                item {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                                        Text("  OR ADD VIDEO URL  ", fontSize = 10.sp, color = textMuted, fontWeight = FontWeight.Bold)
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                                    }
                                }

                                item {
                                    OutlinedTextField(
                                        value = editVideoUrl,
                                        onValueChange = { editVideoUrl = it },
                                        label = { Text("Video URL / Web Link or Path") },
                                        placeholder = { Text("https://... or local file path") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        "showcase" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                item {
                                    OutlinedTextField(
                                        value = editSport,
                                        onValueChange = { editSport = it },
                                        label = { Text("Sport") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editPosition,
                                        onValueChange = { editPosition = it },
                                        label = { Text("Primary Position") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editSecPosition,
                                        onValueChange = { editSecPosition = it },
                                        label = { Text("Secondary Position") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editPreferredFoot,
                                        onValueChange = { editPreferredFoot = it },
                                        label = { Text("Preferred Foot / Hand (e.g. Right, Left, Both)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editJersey,
                                        onValueChange = { editJersey = it },
                                        label = { Text("Jersey Number") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editHeight,
                                        onValueChange = { editHeight = it },
                                        label = { Text("Height (e.g. 182 cm)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                item {
                                    OutlinedTextField(
                                        value = editWeight,
                                        onValueChange = { editWeight = it },
                                        label = { Text("Weight (e.g. 76 kg)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        "bio" -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                item {
                                    Text("Athlete Statement & Bio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = primaryColor)
                                    Text("Describe your career philosophy, tactical strengths, and goals.", fontSize = 11.sp, color = textMuted)
                                }
                                item {
                                    OutlinedTextField(
                                        value = editBio,
                                        onValueChange = { editBio = it },
                                        label = { Text("Bio / Personal Statement") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp),
                                        maxLines = 6
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = borderColor)

                // Footer Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = textMuted, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val updates = mapOf(
                                "name" to editName,
                                "fullName" to editName,
                                "photoUrl" to editPhotoUrl,
                                "photoFileName" to editPhotoFileName,
                                "country" to editCountry,
                                "location" to editLocation,
                                "town" to editLocation,
                                "currentTeam" to editTeam,
                                "team" to editTeam,
                                "clubName" to editClub,
                                "club" to editClub,
                                "sport" to editSport,
                                "position" to editPosition,
                                "secondaryPosition" to editSecPosition,
                                "preferredFoot" to editPreferredFoot,
                                "jerseyNumber" to editJersey,
                                "dob" to editDob,
                                "dateOfBirth" to editDob,
                                "phone" to editPhone,
                                "height" to editHeight,
                                "weight" to editWeight,
                                "bio" to editBio,
                                "videoTitle" to editVideoTitle,
                                "videoKpi" to editVideoKpi,
                                "videoUrl" to editVideoUrl,
                                "videoFileName" to editVideoFileName
                            )
                            onSave(updates)
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Profile", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val activeBg = Color.White
    val inactiveBg = Color.Transparent
    val activeColor = Color(0xFF0D9488)
    val inactiveColor = Color(0xFF64748B)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeBg else inactiveBg,
        border = if (isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
        shadowElevation = if (isSelected) 1.dp else 0.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else inactiveColor
            )
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        Text(value, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VerificationStatusRow(domain: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(domain, fontSize = 13.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.SemiBold)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFECFDF5)
        ) {
            Text(
                text = status,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857)
            )
        }
    }
}
