package com.example.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser = auth.currentUser

    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val friendlyMsg = when {
                e.message?.contains("credential", ignoreCase = true) == true ||
                e.message?.contains("password", ignoreCase = true) == true ||
                e.message?.contains("user", ignoreCase = true) == true ||
                e.message?.contains("expired", ignoreCase = true) == true ->
                    "Incorrect email or password. Please verify your login credentials."
                else -> e.localizedMessage ?: "Authentication failed. Please check your network and try again."
            }
            Result.failure(Exception(friendlyMsg))
        }
    }

    suspend fun signUp(email: String, pass: String, role: UserRole): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed: no UID")
            saveUserProfile(uid, email, role)
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(uid: String, email: String, role: UserRole): Result<Unit> {
        return try {
            val profileData = mapOf(
                "email" to email,
                "role" to role.value,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "onboarded" to true
            )
            // Save to primary role collection and general users collection
            val collectionName = when (role) {
                UserRole.ATHLETE -> "athletes"
                UserRole.COACH -> "coaches"
                UserRole.ANALYST -> "analysts"
                UserRole.SCOUT -> "scouts"
                UserRole.CLUB_ADMIN -> "club_admins"
                UserRole.PLATFORM_ADMIN -> "platform_admins"
                UserRole.NONE -> "users"
            }
            firestore.collection(collectionName).document(uid).set(profileData).await()
            firestore.collection("users").document(uid).set(profileData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveRole(uid: String): UserRole {
        return RoleResolver.resolveRole(uid)
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getUserProfile(uid: String): Map<String, Any?>? {
        return try {
            val collections = listOf("athletes", "coaches", "analysts", "scouts", "club_admins", "platform_admins", "users")
            for (col in collections) {
                val doc = firestore.collection(col).document(uid).get().await()
                if (doc.exists()) {
                    return doc.data
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any?>): Result<Unit> {
        return try {
            firestore.collection("athletes").document(uid).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            firestore.collection("users").document(uid).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
