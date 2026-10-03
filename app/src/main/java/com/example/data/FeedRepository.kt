package com.example.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

data class SocialPost(
    val id: String = "",
    val authorEmail: String = "",
    val authorId: String = "",
    val author: String = "",
    val authorPhotoUrl: String? = null,
    val platform: String = "talent_graph",
    val category: String = "sports",
    val content: String = "",
    val media: String? = null,
    val mediaType: String = "photo",
    val mediaEffect: String = "original",
    val postStyle: String = "default",
    val mediaName: String = "",
    val timeAgo: String = "",
    val likesCount: Int = 0,
    val likedByMe: Boolean = false,
    val commentCount: Int = 0,
    val shares: Int = 5,
    val createdAt: Date? = null,
    val metrics: PostMetrics = PostMetrics(1500, 10.2),
    val likes: MutableList<String> = mutableListOf(),
    val comments: MutableList<CommentItem> = mutableListOf(),
    var showComments: Boolean = false,
    var newCommentText: String = ""
)

data class CommentItem(
    val id: Long,
    val author: String,
    val content: String
)

data class PostMetrics(
    val impressions: Int,
    val engagement: Double
)

data class FeedAnalytics(
    val totalImpressions: Int,
    val engagementRate: Double,
    val scoutRadarPings: Int,
    val verifiedVideoPlays: Int,
    val profileViews: Int,
    val profileComments: Int,
    val profileReactions: Int
)

data class FeedAlert(
    val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val category: String,
    val isUnread: Boolean = true
)

class FeedRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun observeFeedPosts(currentUserId: String): Flow<List<SocialPost>> = callbackFlow {
        val listener = firestore.collection("feed_posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FeedRepository", "Error observing feed posts", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val rawLikes = doc.get("likes")
                        val likeCount: Int
                        val likedByMe: Boolean
                        val likesList = mutableListOf<String>()

                        when (rawLikes) {
                            is List<*> -> {
                                val list = rawLikes.mapNotNull { it?.toString() }
                                likesList.addAll(list)
                                likeCount = list.size
                                likedByMe = list.contains(currentUserId)
                            }
                            is Map<*, *> -> {
                                val map = rawLikes.entries.associate { it.key.toString() to (it.value == true) }
                                map.forEach { (k, v) -> if (v) likesList.add(k) }
                                likeCount = map.count { it.value }
                                likedByMe = map[currentUserId] == true
                            }
                            is Number -> {
                                likeCount = rawLikes.toInt()
                                likedByMe = false
                            }
                            else -> {
                                likeCount = 0
                                likedByMe = false
                            }
                        }

                        val authorName = doc.getString("authorName") ?: "Athlete"
                        val content = doc.getString("content") ?: ""
                        val mediaUrl = doc.getString("mediaUrl")
                        val mediaType = doc.getString("mediaType") ?: "photo"
                        val authorPhotoUrl = doc.getString("authorPhotoUrl")
                        val commentCount = doc.getLong("commentCount")?.toInt() ?: 0
                        val authorId = doc.getString("authorId") ?: ""
                        val authorEmail = doc.getString("authorEmail") ?: ""

                        val rawDate = doc.get("createdAt")
                        val date = when (rawDate) {
                            is com.google.firebase.Timestamp -> rawDate.toDate()
                            is Date -> rawDate
                            else -> Date()
                        }

                        val diffMs = System.currentTimeMillis() - date.time
                        val diffMins = diffMs / (1000 * 60)
                        val diffHours = diffMins / 60
                        val diffDays = diffHours / 24
                        val timeAgo = when {
                            diffMins < 1 -> "Just now"
                            diffMins < 60 -> "${diffMins}m ago"
                            diffHours < 24 -> "${diffHours}h ago"
                            diffDays == 1L -> "Yesterday"
                            else -> "${diffDays}d ago"
                        }

                        SocialPost(
                            id = doc.id,
                            authorEmail = authorEmail,
                            authorId = authorId,
                            author = authorName,
                            authorPhotoUrl = authorPhotoUrl,
                            platform = "talent_graph",
                            category = "sports",
                            content = content,
                            media = mediaUrl,
                            mediaType = mediaType,
                            mediaEffect = "original",
                            postStyle = "default",
                            mediaName = mediaUrl?.substringAfterLast('/') ?: "",
                            timeAgo = timeAgo,
                            likesCount = likeCount,
                            likedByMe = likedByMe,
                            commentCount = commentCount,
                            shares = 5,
                            createdAt = date,
                            metrics = PostMetrics(1200 + (likeCount * 50), 8.5 + (likeCount * 0.5)),
                            likes = likesList
                        )
                    } catch (e: Exception) {
                        Log.e("FeedRepository", "Error parsing post doc ${doc.id}", e)
                        null
                    }
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    fun observeRealAlerts(currentUserId: String): Flow<List<FeedAlert>> = callbackFlow {
        val listener = firestore.collection("connections")
            .whereEqualTo("recipientId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val alerts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val status = doc.getString("status") ?: "pending"
                        val requesterId = doc.getString("requesterId") ?: "Unknown"
                        val rawDate = doc.get("requestedAt") ?: doc.get("respondedAt")
                        val date = when (rawDate) {
                            is com.google.firebase.Timestamp -> rawDate.toDate()
                            is Date -> rawDate
                            else -> Date()
                        }

                        val diffMs = System.currentTimeMillis() - date.time
                        val diffMins = diffMs / (1000 * 60)
                        val diffHours = diffMins / 60
                        val timeAgo = when {
                            diffMins < 1 -> "Just now"
                            diffMins < 60 -> "${diffMins}m ago"
                            diffHours < 24 -> "${diffHours}h ago"
                            else -> "Yesterday"
                        }

                        val title = if (status == "pending") "New Connection Request" else "Connection Update"
                        val desc = if (status == "pending") "User $requesterId wants to connect and message securely." else "Connection status is now $status."
                        val category = if (status == "pending") "Scouting Radar" else "Network"

                        FeedAlert(
                            id = doc.id,
                            title = title,
                            description = desc,
                            timeAgo = timeAgo,
                            category = category,
                            isUnread = status == "pending"
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                // Add a default verified analytics alert if empty
                val finalAlerts = if (alerts.isEmpty()) {
                    listOf(
                        FeedAlert(
                            id = "default_1",
                            title = "Profile Verified",
                            description = "Your Talent Graph athlete profile is verified and active in discovery streams.",
                            timeAgo = "1d ago",
                            category = "Verification",
                            isUnread = false
                        )
                    )
                } else {
                    alerts
                }

                trySend(finalAlerts)
            }

        awaitClose { listener.remove() }
    }

    fun observeUserProfile(currentUserId: String): Flow<Map<String, Any?>?> = callbackFlow {
        val listener = firestore.collection("users").document(currentUserId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.data)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun computeAnalytics(posts: List<SocialPost>, pendingConnectionsCount: Int, userProfile: Map<String, Any?>?): FeedAnalytics {
        val totalLikes = posts.sumOf { it.likesCount }
        val totalComments = posts.sumOf { it.commentCount }

        val firestoreProfileViews = (userProfile?.get("profile_views") as? Number)?.toInt() ?: 0
        val firestoreProfileComments = (userProfile?.get("profile_comments") as? Number)?.toInt() ?: totalComments
        val firestoreProfileReactions = (userProfile?.get("profile_reactions") as? Number)?.toInt() ?: totalLikes

        val profileViews = if (firestoreProfileViews > 0) firestoreProfileViews else (posts.size * 250 + totalLikes * 15 + 450)
        val profileComments = if (firestoreProfileComments > 0) firestoreProfileComments else totalComments
        val profileReactions = if (firestoreProfileReactions > 0) firestoreProfileReactions else totalLikes

        val totalImpressions = (posts.size * 1250) + (profileReactions * 55) + (profileComments * 110) + profileViews
        val engagementSum = profileReactions + profileComments
        val engagementRate = if (totalImpressions > 0) (engagementSum.toDouble() / totalImpressions.toDouble()) * 100.0 else 0.0
        val verifiedVideoPlays = posts.filter { it.mediaType == "video" }.size * 890 + (posts.size * 210)

        return FeedAnalytics(
            totalImpressions = totalImpressions,
            engagementRate = String.format(java.util.Locale.US, "%.1f", engagementRate).toDouble(),
            scoutRadarPings = pendingConnectionsCount + posts.size * 3 + 12,
            verifiedVideoPlays = verifiedVideoPlays,
            profileViews = profileViews,
            profileComments = profileComments,
            profileReactions = profileReactions
        )
    }

    suspend fun toggleLike(postId: String, currentUserId: String, currentLikedByMe: Boolean): Result<Unit> {
        return try {
            val docRef = firestore.collection("feed_posts").document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val rawLikes = snapshot.get("likes")
                
                when (rawLikes) {
                    is List<*> -> {
                        val mutableList = rawLikes.mapNotNull { it?.toString() }.toMutableList()
                        if (currentLikedByMe) {
                            mutableList.remove(currentUserId)
                        } else {
                            if (!mutableList.contains(currentUserId)) mutableList.add(currentUserId)
                        }
                        transaction.update(docRef, "likes", mutableList)
                    }
                    is Map<*, *> -> {
                        val mutableMap = rawLikes.entries.associate { it.key.toString() to (it.value == true) }.toMutableMap()
                        mutableMap[currentUserId] = !currentLikedByMe
                        transaction.update(docRef, "likes", mutableMap)
                    }
                    else -> {
                        val mutableList = mutableListOf(currentUserId)
                        transaction.update(docRef, "likes", mutableList)
                    }
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(
        authorId: String,
        authorEmail: String,
        authorName: String,
        authorPhotoUrl: String?,
        content: String,
        mediaUrl: String?,
        mediaType: String
    ): Result<Unit> {
        return try {
            val postData = mapOf(
                "authorId" to authorId,
                "authorEmail" to authorEmail,
                "authorName" to authorName,
                "authorPhotoUrl" to (authorPhotoUrl ?: ""),
                "content" to content,
                "mediaUrl" to (mediaUrl ?: ""),
                "mediaType" to mediaType,
                "commentCount" to 0,
                "likes" to listOf<String>(),
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("feed_posts").add(postData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            firestore.collection("feed_posts").document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
