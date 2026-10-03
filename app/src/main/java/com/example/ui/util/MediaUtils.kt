package com.example.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import java.io.File
import java.io.FileOutputStream

enum class PhotoEffect(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val description: String
) {
    ORIGINAL("original", "Normal", "✨", "Natural authentic colors"),
    CINEMATIC("cinematic", "Cinematic", "🎬", "High contrast sport broadcast"),
    MONOCHROME("bw", "B&W", "🖤", "Dramatic athletic monochrome"),
    GOLDEN_HOUR("golden", "Golden Glow", "🏆", "Warm victory champion tint"),
    CYBER_NEON("neon", "Cyber Neon", "⚡", "Electric stadium energy boost"),
    VINTAGE_SEPIA("vintage", "Vintage", "📜", "Classic heritage sport film"),
    EMERALD_PITCH("emerald", "Turf Pitch", "⚽", "Vivid stadium grass clarity"),
    SPOTLIGHT("spotlight", "Spotlight", "🎯", "Center focus with dark vignette")
}

enum class PostCardStyle(
    val id: String,
    val displayName: String,
    val badgeLabel: String
) {
    DEFAULT("default", "Classic Slate", ""),
    GOLD_CHAMPION("gold", "Gold Glory", "🏆 CHAMPION REEL"),
    NEON_PULSE("neon", "Neon Pulse", "⚡ PRO ATHLETE"),
    CRIMSON_FIRE("fire", "Speed Flame", "🔥 MATCHDAY HIGHLIGHT"),
    SCOUT_VERIFIED("scout", "Scout Verified", "⭐ FKF VERIFIED")
}

object MediaUtils {
    /**
     * Converts any image source (base64 data URI, HTTP URL, content URI, or local file path)
     * into a Coil-compatible model object.
     */
    fun getMediaModel(urlOrPath: String?): Any? {
        if (urlOrPath.isNullOrBlank()) return null
        val trimmed = urlOrPath.trim()
        return when {
            trimmed.startsWith("data:") && trimmed.contains("base64,") -> {
                try {
                    val base64Part = trimmed.substringAfter("base64,")
                    android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
                } catch (e: Exception) {
                    trimmed
                }
            }
            trimmed.startsWith("content:") || trimmed.startsWith("file:") -> Uri.parse(trimmed)
            trimmed.startsWith("/") -> File(trimmed)
            else -> trimmed
        }
    }

    /**
     * Determines whether a given media string represents a video file or stream.
     */
    fun isMediaVideo(urlOrPath: String?): Boolean {
        if (urlOrPath.isNullOrBlank()) return false
        val lower = urlOrPath.lowercase()
        return lower.endsWith(".mp4") ||
                lower.endsWith(".mkv") ||
                lower.endsWith(".mov") ||
                lower.endsWith(".webm") ||
                lower.endsWith(".3gp") ||
                lower.contains("video") ||
                lower.contains("googlevideo") ||
                lower.contains("highlight_reel") ||
                lower.contains("season_highlight")
    }

    /**
     * Returns a Compose ColorFilter for the specified PhotoEffect ID.
     */
    fun getEffectColorFilter(effectId: String?): ColorFilter? {
        return when (effectId) {
            PhotoEffect.MONOCHROME.id -> {
                val matrix = ColorMatrix().apply { setToSaturation(0f) }
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.CINEMATIC.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        1.3f, 0f, 0f, 0f, -25f,
                        0f, 1.3f, 0f, 0f, -25f,
                        0f, 0f, 1.3f, 0f, -25f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.GOLDEN_HOUR.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        1.25f, 0.1f, 0f, 0f, 20f,
                        0.05f, 1.15f, 0f, 0f, 10f,
                        0f, 0f, 0.8f, 0f, -20f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.CYBER_NEON.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        0.85f, 0f, 0.25f, 0f, -10f,
                        0f, 1.25f, 0.15f, 0f, 10f,
                        0.1f, 0.2f, 1.45f, 0f, 30f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.VINTAGE_SEPIA.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.EMERALD_PITCH.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        0.85f, 0f, 0f, 0f, -10f,
                        0f, 1.4f, 0f, 0f, 20f,
                        0f, 0f, 0.9f, 0f, -10f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            PhotoEffect.SPOTLIGHT.id -> {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        1.15f, 0f, 0f, 0f, -15f,
                        0f, 1.15f, 0f, 0f, -15f,
                        0f, 0f, 1.15f, 0f, -15f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                ColorFilter.colorMatrix(matrix)
            }
            else -> null
        }
    }

    /**
     * Copy media selected via Uri to the app's persistent internal storage
     */
    fun copyUriToStorage(context: Context, uri: Uri, prefix: String, fallbackExtension: String): String {
        return try {
            val originalName = getFileNameFromUri(context, uri)
            val ext = originalName.substringAfterLast(".", fallbackExtension).ifBlank { fallbackExtension }
            val fileName = "${prefix}_${System.currentTimeMillis()}.$ext"
            val destinationFile = File(context.filesDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destinationFile).toString()
        } catch (e: Exception) {
            uri.toString()
        }
    }

    /**
     * Extract display name from ContentResolver Uri
     */
    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var name = ""
        if (uri.scheme == "content") {
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            name = it.getString(index)
                        }
                    }
                }
            } catch (e: Exception) {}
        }
        if (name.isBlank()) {
            name = uri.lastPathSegment ?: "media_file"
        }
        return name
    }

    /**
     * Create a clean local athlete photo in storage for testing upload
     */
    fun createSampleLocalPhoto(context: Context): Pair<String, String> {
        return try {
            val fileName = "athlete_photo_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            val bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Background Deep Teal
            paint.color = android.graphics.Color.parseColor("#0D9488")
            canvas.drawCircle(200f, 200f, 200f, paint)

            // Athletic silhouette
            paint.color = android.graphics.Color.parseColor("#1E293B")
            canvas.drawCircle(200f, 140f, 75f, paint)
            canvas.drawRoundRect(60f, 240f, 340f, 400f, 40f, 40f, paint)

            // Jersey Number
            paint.color = android.graphics.Color.WHITE
            paint.textSize = 54f
            paint.textAlign = Paint.Align.CENTER
            paint.isFakeBoldText = true
            canvas.drawText("#10", 200f, 340f, paint)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            Pair(Uri.fromFile(file).toString(), fileName)
        } catch (e: Exception) {
            Pair("", "")
        }
    }

    /**
     * Create or provide a local highlight video in storage for testing upload
     */
    fun createSampleLocalVideo(context: Context): Pair<String, String> {
        return try {
            val fileName = "season_highlight_${System.currentTimeMillis()}.mp4"
            val file = File(context.filesDir, fileName)
            val sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            if (!file.exists()) {
                file.writeText("video_descriptor: $sampleVideoUrl")
            }
            Pair(sampleVideoUrl, fileName)
        } catch (e: Exception) {
            Pair("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", "season_highlight.mp4")
        }
    }
}
