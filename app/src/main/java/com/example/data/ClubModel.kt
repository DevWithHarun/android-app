package com.example.data

data class ClubModel(
    val clubName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val location: String = "",
    val isVerified: Boolean = false,
    val profileCompleted: Boolean = true,
    val logoUrl: String = "",
    val createdAt: Any? = null
)
