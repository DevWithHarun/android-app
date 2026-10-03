package com.example.ui.util

import com.google.firebase.firestore.DocumentSnapshot

object AthleteNameResolver {

    fun resolveAthleteName(
        name: String? = null,
        fullName: String? = null,
        displayName: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null
    ): String {
        if (!name.isNullOrBlank()) return name.trim()
        if (!fullName.isNullOrBlank()) return fullName.trim()
        if (!displayName.isNullOrBlank()) return displayName.trim()

        val formattedFirst = firstName?.trim()?.split(" ")?.joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ?: ""

        val formattedLast = lastName?.trim()?.split(" ")?.joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } ?: ""

        val combined = "$formattedFirst $formattedLast".trim()
        if (combined.isNotBlank()) {
            return combined
        }

        if (!email.isNullOrBlank() && email.contains("@")) {
            val prefix = email.substringBefore("@").replace(".", " ").replace("_", " ").trim()
            if (prefix.isNotBlank()) {
                return prefix.split(" ").filter { it.isNotBlank() }.joinToString(" ") { word ->
                    word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
            }
        }

        return "Athlete"
    }

    fun resolveFromDoc(doc: DocumentSnapshot): String {
        val data = doc.data
        return resolveAthleteName(
            name = (data?.get("name") ?: try { doc.get("name") } catch (e: Exception) { null }) as? String,
            fullName = (data?.get("fullName") ?: try { doc.get("fullName") } catch (e: Exception) { null }) as? String,
            displayName = (data?.get("displayName") ?: try { doc.get("displayName") } catch (e: Exception) { null }) as? String,
            firstName = (data?.get("firstName") ?: try { doc.get("firstName") } catch (e: Exception) { null }) as? String,
            lastName = (data?.get("lastName") ?: try { doc.get("lastName") } catch (e: Exception) { null }) as? String,
            email = (data?.get("email") ?: try { doc.get("email") } catch (e: Exception) { null }) as? String
        )
    }

    fun resolveFromMap(data: Map<String, Any?>?): String {
        if (data == null) return "Athlete"
        return resolveAthleteName(
            name = data["name"] as? String,
            fullName = data["fullName"] as? String,
            displayName = data["displayName"] as? String,
            firstName = data["firstName"] as? String,
            lastName = data["lastName"] as? String,
            email = data["email"] as? String
        )
    }

    fun resolvePhotoUrl(doc: DocumentSnapshot?): String? {
        if (doc == null) return null
        val data = doc.data
        val candidates = listOf("photoUrl", "profilePhotoUrl", "photoURL", "avatarUrl", "photo", "profilePhoto", "imageUrl", "image", "picture")
        for (key in candidates) {
            val raw = data?.get(key) ?: try { doc.get(key) } catch (e: Exception) { null }
            val url = (raw as? String)?.trim()
            if (!url.isNullOrBlank()) return url
        }
        return null
    }

    fun resolvePhotoUrl(data: Map<String, Any?>?): String? {
        if (data == null) return null
        val candidates = listOf("photoUrl", "profilePhotoUrl", "photoURL", "avatarUrl", "photo", "profilePhoto", "imageUrl", "image", "picture")
        for (key in candidates) {
            val url = (data[key] as? String)?.trim()
            if (!url.isNullOrBlank()) return url
        }
        return null
    }
}
