package com.example.data.auth

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object RoleResolver {
    private const val TAG = "RoleResolver"
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    suspend fun resolveRole(uid: String): UserRole {
        if (uid.isBlank()) {
            Log.w(TAG, "resolveRole called with blank uid")
            return UserRole.NONE
        }

        return try {
            // 1. Check primary 'users' collection document
            val userDoc = firestore.collection("users").document(uid).get().await()
            if (userDoc.exists()) {
                val roleStr = userDoc.getString("role")
                val role = UserRole.fromValue(roleStr)
                if (role != UserRole.NONE) {
                    Log.d(TAG, "Resolved role: $role from 'users' collection (role field: '$roleStr') for uid: $uid")
                    return role
                }
            }

            // 2. Check role-specific direct collections
            val collections = listOf("coaches", "scouts", "platform_admins", "analysts", "athletes")
            for (col in collections) {
                val doc = firestore.collection(col).document(uid).get().await()
                if (doc.exists()) {
                    val roleStr = doc.getString("role") ?: col.dropLast(1)
                    val role = UserRole.fromValue(roleStr)
                    if (role != UserRole.NONE) {
                        Log.d(TAG, "Resolved role: $role from '$col' collection for uid: $uid")
                        return role
                    }
                }
            }

            // 3. Check 'club_members' collection (querying userId where role is admin/club)
            val clubMembersQuery = firestore.collection("club_members")
                .whereEqualTo("userId", uid)
                .limit(1)
                .get()
                .await()
            if (!clubMembersQuery.isEmpty) {
                val memberDoc = clubMembersQuery.documents.first()
                val roleStr = memberDoc.getString("role")
                val role = UserRole.fromValue(roleStr)
                if (role != UserRole.NONE) {
                    Log.d(TAG, "Resolved role: $role from 'club_members' query for uid: $uid")
                    return role
                }
            }

            Log.w(TAG, "No matching role found in any collection for uid: $uid, defaulting to NONE")
            UserRole.NONE
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving role for uid: $uid", e)
            UserRole.NONE
        }
    }
}
