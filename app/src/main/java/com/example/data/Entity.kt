package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "athletes")
data class AthleteEntity(
    @PrimaryKey val firestoreId: String,
    val name: String,
    val sport: String,
    val position: String,
    val rating: Int,
    val verifiedStats: String,
    val clubName: String,
    val joinedDate: String,
    val isVerified: Boolean = true,
    val photoUrl: String? = null
)

@Entity(tableName = "match_logs")
data class MatchLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val athleteId: Long = 1,
    val matchTitle: String,
    val minutesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val matchRating: Int,
    val verifiedByCoach: String,
    val competition: String = "League Match",
    val opponent: String = "",
    val matchDate: String = "2024",
    val result: String = "Won",
    val homeScore: Int = 1,
    val awayScore: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clubName: String,
    val role: String,
    val staffCount: Int,
    val activeSquadSize: Int,
    val location: String
)

@Entity(tableName = "career_stints")
data class CareerStintEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organization: String,
    val team: String,
    val position: String,
    val competition: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val verificationLevel: String
)

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionType: String,
    val date: String,
    val durationMins: Int,
    val intensity: String,
    val coachNotes: String,
    val attendance: String
)

@Entity(tableName = "development_goals")
data class DevelopmentGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalStatement: String,
    val dimension: String,
    val targetDate: String,
    val currentLevel: Int,
    val targetLevel: Int,
    val status: String
)

@Entity(tableName = "physical_measurements")
data class PhysicalMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testType: String,
    val value: String,
    val unit: String,
    val testDate: String,
    val verificationStatus: String
)

@Entity(tableName = "availability_status")
data class AvailabilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val status: String,
    val expectedReturnDate: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workload_records")
data class WorkloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sevenDayLoad: Int,
    val twentyEightDayLoad: Int,
    val matchMinutes: Int,
    val signalStatus: String,
    val recommendation: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val competition: String,
    val organization: String,
    val date: String,
    val category: String,
    val verificationLevel: String
)

@Entity(tableName = "opportunities")
data class OpportunityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String,
    val location: String,
    val deadline: String,
    val description: String,
    val status: String
)

@Entity(tableName = "scout_activity")
data class ScoutActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val viewerName: String,
    val action: String,
    val timestamp: String,
    val sectionAccessed: String
)

@Entity(tableName = "evidence_vault")
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String,
    val verificationState: String,
    val dateUploaded: String
)

@Entity(tableName = "risk_protection")
data class RiskProtectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val signalStatus: String,
    val reason: String,
    val confidence: String
)

@Entity(tableName = "insurance_claims")
data class InsuranceClaimEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val policyId: String,
    val incidentDescription: String,
    val status: String,
    val amount: String
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val verificationState: String,
    val issueDate: String = "2024",
    val expiryDate: String = "Permanent",
    val fileType: String = "PDF",
    val fileSize: String = "1.2 MB",
    val issuingAuthority: String = "Official Authority"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val messageText: String,
    val timestamp: String,
    val isIncoming: Boolean
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val timestamp: String,
    val isRead: Boolean
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventTitle: String,
    val eventType: String,
    val eventDate: String,
    val location: String
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionTitle: String,
    val amount: String,
    val date: String,
    val status: String
)

@Entity(tableName = "privacy_settings")
data class PrivacySettingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fieldName: String,
    val visibilityLevel: String
)

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orgCode: String = "TG-CLUB-00421",
    val joinCode: String = "MUFC-7K92X",
    val name: String = "Mombasa United FC",
    val type: String = "Club",
    val sport: String = "Football (Soccer)",
    val location: String = "Mombasa, Kenya",
    val isVerified: Boolean = true,
    val adminUserId: String = "",
    val logoUrl: String = "",
    val description: String = "Official verified member organization of the Talent Graph sports ecosystem."
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orgId: Long = 1,
    val orgName: String = "Mombasa United FC",
    val name: String = "First Team",
    val sport: String = "Football (Soccer)",
    val ageGroup: String = "Senior / U23",
    val headCoachName: String = "David Mwangi"
)

@Entity(tableName = "organization_memberships")
data class OrganizationMembershipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "athlete_current",
    val userName: String = "John Kamau",
    val userPhotoUrl: String = "",
    val userRole: String = "ATHLETE",
    val orgId: Long = 1,
    val orgName: String = "Mombasa United FC",
    val orgCode: String = "TG-CLUB-00421",
    val teamId: Long = 1,
    val teamName: String = "First Team",
    val role: String = "ATHLETE",
    val status: String = "ACTIVE", // ACTIVE, PENDING, REJECTED, ENDED
    val joinedAt: String = "2026-09-22",
    val leftAt: String = "",
    val evidenceUrl: String = "",
    val evidenceNotes: String = "Official club player registration",
    val requestedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "connections")
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "athlete_current",
    val connectedUserId: String = "",
    val connectedName: String = "David Mwangi",
    val connectedRole: String = "Coach",
    val orgName: String = "Mombasa United FC",
    val teamName: String = "First Team",
    val status: String = "CONNECTED", // CONNECTED, PENDING, REQUESTED
    val permissionLevel: String = "PERFORMANCE_DATA", // PUBLIC_PROFILE, PERFORMANCE_DATA, FULL_VAULT
    val connectedSince: String = "2026"
)
