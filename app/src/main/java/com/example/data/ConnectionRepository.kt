package com.example.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

data class ConnectionDoc(
    val id: String = "",
    val requesterId: String = "",
    val recipientId: String = "",
    val status: String = "pending", // "pending", "accepted", "declined"
    val requestedAt: Date? = null,
    val respondedAt: Date? = null,
    val declinedAt: Date? = null,
    val messageCountBeforeAccept: Int = 1
)

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val type: String = "user", // "user" or "system"
    val timestamp: Date? = null
)

data class UserConnectionSummary(
    val connectionId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserPhoto: String?,
    val otherUserRole: String,
    val status: String,
    val requestedAt: Date?
)

class ConnectionRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getDeterministicId(uidA: String, uidB: String): String {
        return if (uidA < uidB) "${uidA}_$uidB" else "${uidB}_$uidA"
    }

    suspend fun getConnectionStatus(uidA: String, uidB: String): ConnectionDoc? {
        val docId = getDeterministicId(uidA, uidB)
        val snapshot = firestore.collection("connections").document(docId).get().await()
        return snapshot.toObject(ConnectionDoc::class.java)?.copy(id = snapshot.id)
    }

    fun observeConnection(uidA: String, uidB: String): Flow<ConnectionDoc?> = callbackFlow {
        val docId = getDeterministicId(uidA, uidB)
        val listener = firestore.collection("connections").document(docId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val doc = snapshot.toObject(ConnectionDoc::class.java)?.copy(id = snapshot.id)
                    trySend(doc)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeMessages(uidA: String, uidB: String): Flow<List<ChatMessage>> = callbackFlow {
        val docId = getDeterministicId(uidA, uidB)
        val listener = firestore.collection("connections").document(docId)
            .collection("messages")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(
        currentUid: String,
        currentUserName: String,
        recipientUid: String,
        recipientName: String,
        text: String
    ): Result<Unit> {
        val docId = getDeterministicId(currentUid, recipientUid)
        val connRef = firestore.collection("connections").document(docId)
        
        val snapshot = connRef.get().await()
        val now = Date()

        if (!snapshot.exists()) {
            val newConn = ConnectionDoc(
                id = docId,
                requesterId = currentUid,
                recipientId = recipientUid,
                status = "pending",
                requestedAt = now,
                messageCountBeforeAccept = 1
            )
            connRef.set(newConn).await()

            val msg = ChatMessage(
                senderId = currentUid,
                senderName = currentUserName,
                text = text,
                type = "user",
                timestamp = now
            )
            connRef.collection("messages").add(msg).await()
            return Result.success(Unit)
        }

        val conn = snapshot.toObject(ConnectionDoc::class.java) ?: return Result.failure(Exception("Invalid connection data"))

        when (conn.status) {
            "accepted" -> {
                val msg = ChatMessage(
                    senderId = currentUid,
                    senderName = currentUserName,
                    text = text,
                    type = "user",
                    timestamp = now
                )
                connRef.collection("messages").add(msg).await()
                return Result.success(Unit)
            }
            "pending" -> {
                if (conn.requesterId == currentUid) {
                    if (conn.messageCountBeforeAccept >= 2) {
                        return Result.failure(Exception("Waiting for $recipientName to accept your request before you can send more messages."))
                    }
                    connRef.update("messageCountBeforeAccept", FieldValue.increment(1)).await()
                    val msg = ChatMessage(
                        senderId = currentUid,
                        senderName = currentUserName,
                        text = text,
                        type = "user",
                        timestamp = now
                    )
                    connRef.collection("messages").add(msg).await()
                    return Result.success(Unit)
                } else {
                    return Result.failure(Exception("Recipient cannot send messages while request is pending."))
                }
            }
            "declined" -> {
                val declinedAt = conn.declinedAt
                if (declinedAt != null) {
                    val diffDays = (now.time - declinedAt.time) / (1000 * 60 * 60 * 24)
                    if (diffDays < 7) {
                        val remaining = 7 - diffDays
                        return Result.failure(Exception("You can try reconnecting with $recipientName after $remaining days."))
                    }
                }
                connRef.update(
                    mapOf(
                        "status" to "pending",
                        "requesterId" to currentUid,
                        "recipientId" to recipientUid,
                        "requestedAt" to now,
                        "respondedAt" to null,
                        "declinedAt" to null,
                        "messageCountBeforeAccept" to 1
                    )
                ).await()

                val msg = ChatMessage(
                    senderId = currentUid,
                    senderName = currentUserName,
                    text = text,
                    type = "user",
                    timestamp = now
                )
                connRef.collection("messages").add(msg).await()
                return Result.success(Unit)
            }
            else -> {
                return Result.failure(Exception("Unknown connection status."))
            }
        }
    }

    suspend fun acceptRequest(connectionId: String): Result<Unit> {
        val connRef = firestore.collection("connections").document(connectionId)
        val now = Date()
        connRef.update(
            mapOf(
                "status" to "accepted",
                "respondedAt" to now
            )
        ).await()

        val systemMsg = ChatMessage(
            senderId = "",
            senderName = "",
            text = "You can now message each other freely.",
            type = "system",
            timestamp = now
        )
        connRef.collection("messages").add(systemMsg).await()
        return Result.success(Unit)
    }

    suspend fun declineRequest(connectionId: String): Result<Unit> {
        val connRef = firestore.collection("connections").document(connectionId)
        connRef.update(
            mapOf(
                "status" to "declined",
                "declinedAt" to Date()
            )
        ).await()
        return Result.success(Unit)
    }

    fun observeAcceptedConnections(currentUid: String, onUpdate: (List<UserConnectionSummary>) -> Unit) {
        firestore.collection("connections")
            .whereEqualTo("requesterId", currentUid)
            .whereEqualTo("status", "accepted")
            .addSnapshotListener { snap1, _ ->
                firestore.collection("connections")
                    .whereEqualTo("recipientId", currentUid)
                    .whereEqualTo("status", "accepted")
                    .get()
                    .addOnSuccessListener { recSnap ->
                        val summaries = mutableListOf<UserConnectionSummary>()
                        val processedIds = mutableSetOf<String>()
                        val docs = (snap1?.documents ?: emptyList()) + recSnap.documents
                        for (doc in docs) {
                            val conn = doc.toObject(ConnectionDoc::class.java)?.copy(id = doc.id) ?: continue
                            val otherId = if (conn.requesterId == currentUid) conn.recipientId else conn.requesterId
                            if (processedIds.contains(otherId)) continue
                            processedIds.add(otherId)
                            summaries.add(
                                UserConnectionSummary(
                                    connectionId = conn.id,
                                    otherUserId = otherId,
                                    otherUserName = "User $otherId",
                                    otherUserPhoto = null,
                                    otherUserRole = "Athlete",
                                    status = conn.status,
                                    requestedAt = conn.requestedAt
                                )
                            )
                        }
                        onUpdate(summaries)
                    }
            }
    }

    fun observePendingRequestsReceived(currentUid: String, onUpdate: (List<ConnectionDoc>) -> Unit) {
        firestore.collection("connections")
            .whereEqualTo("recipientId", currentUid)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(ConnectionDoc::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                onUpdate(list)
            }
    }
}
