package com.example.data.auth

enum class UserRole(val value: String) {
    ATHLETE("athlete"),
    COACH("coach"),
    ANALYST("analyst"),
    SCOUT("scout"),
    CLUB_ADMIN("club_admin"),
    PLATFORM_ADMIN("platform_admin"),
    NONE("none");

    companion object {
        fun fromValue(value: String?): UserRole {
            return when (value?.lowercase()?.trim()) {
                "athlete" -> ATHLETE
                "coach" -> COACH
                "analyst", "coach analyst" -> ANALYST
                "scout" -> SCOUT
                "club", "admin", "club_admin", "club admin" -> CLUB_ADMIN
                "platform_admin", "platform admin" -> PLATFORM_ADMIN
                else -> NONE
            }
        }
    }
}
