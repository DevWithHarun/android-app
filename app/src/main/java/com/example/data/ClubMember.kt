package com.example.data

data class ClubMember(
    val userId: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val role: String = "admin",
    val status: String = "active",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val photoUrl: String = "",
    val joinedAt: Any? = null,
    val invitedAt: Any? = null,
    val invitedBy: String = "",
    val createdAt: Any? = null
) {
    val fullName: String
        get() = if (displayName.isNotBlank()) displayName
                else if (firstName.isNotBlank() || lastName.isNotBlank()) "$firstName $lastName".trim()
                else "Club Member"
}
