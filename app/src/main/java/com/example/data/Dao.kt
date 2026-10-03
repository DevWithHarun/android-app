package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteDao {
    @Query("SELECT * FROM athletes")
    fun getAllAthletes(): Flow<List<AthleteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: AthleteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthletes(athletes: List<AthleteEntity>)

    @Query("DELETE FROM athletes WHERE firestoreId = :firestoreId")
    suspend fun deleteAthleteById(firestoreId: String)

    @Query("DELETE FROM athletes")
    suspend fun clearAthletes()

    @Delete
    suspend fun deleteAthlete(athlete: AthleteEntity)
}

@Dao
interface MatchLogDao {
    @Query("SELECT * FROM match_logs ORDER BY id DESC")
    fun getAllMatchLogs(): Flow<List<MatchLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchLog(matchLog: MatchLogEntity)

    @Delete
    suspend fun deleteMatchLog(matchLog: MatchLogEntity)
}

@Dao
interface ClubDao {
    @Query("SELECT * FROM clubs")
    fun getAllClubs(): Flow<List<ClubEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClub(club: ClubEntity)

    @Delete
    suspend fun deleteClub(club: ClubEntity)
}

@Dao
interface CareerDao {
    @Query("SELECT * FROM career_stints ORDER BY id DESC")
    fun getAllStints(): Flow<List<CareerStintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStint(stint: CareerStintEntity)

    @Delete
    suspend fun deleteStint(stint: CareerStintEntity)
}

@Dao
interface TrainingDao {
    @Query("SELECT * FROM training_sessions ORDER BY id DESC")
    fun getAllSessions(): Flow<List<TrainingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity)

    @Delete
    suspend fun deleteSession(session: TrainingSessionEntity)
}

@Dao
interface DevelopmentDao {
    @Query("SELECT * FROM development_goals ORDER BY id DESC")
    fun getAllGoals(): Flow<List<DevelopmentGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: DevelopmentGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: DevelopmentGoalEntity)
}

@Dao
interface PhysicalDao {
    @Query("SELECT * FROM physical_measurements")
    fun getAllMeasurements(): Flow<List<PhysicalMeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: PhysicalMeasurementEntity)
}

@Dao
interface AvailabilityDao {
    @Query("SELECT * FROM availability_status ORDER BY id DESC")
    fun getAllAvailability(): Flow<List<AvailabilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(availability: AvailabilityEntity)

    @Delete
    suspend fun deleteAvailability(availability: AvailabilityEntity)
}

@Dao
interface WorkloadDao {
    @Query("SELECT * FROM workload_records ORDER BY id DESC")
    fun getAllWorkload(): Flow<List<WorkloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkload(workload: WorkloadEntity)

    @Delete
    suspend fun deleteWorkload(workload: WorkloadEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Delete
    suspend fun deleteAchievement(achievement: AchievementEntity)
}

@Dao
interface OpportunityDao {
    @Query("SELECT * FROM opportunities ORDER BY id DESC")
    fun getAllOpportunities(): Flow<List<OpportunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opportunity: OpportunityEntity)

    @Delete
    suspend fun deleteOpportunity(opportunity: OpportunityEntity)
}

@Dao
interface ScoutActivityDao {
    @Query("SELECT * FROM scout_activity ORDER BY id DESC")
    fun getAllActivity(): Flow<List<ScoutActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ScoutActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ScoutActivityEntity)
}

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence_vault ORDER BY id DESC")
    fun getAllEvidence(): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: EvidenceEntity)

    @Delete
    suspend fun deleteEvidence(evidence: EvidenceEntity)
}

@Dao
interface RiskProtectionDao {
    @Query("SELECT * FROM risk_protection")
    fun getAllSignals(): Flow<List<RiskProtectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: RiskProtectionEntity)
}

@Dao
interface InsuranceClaimDao {
    @Query("SELECT * FROM insurance_claims")
    fun getAllClaims(): Flow<List<InsuranceClaimEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: InsuranceClaimEntity)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY id DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
}

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar_events")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)
}

@Dao
interface PrivacyDao {
    @Query("SELECT * FROM privacy_settings")
    fun getAllSettings(): Flow<List<PrivacySettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: PrivacySettingEntity)
}

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations")
    fun getAllOrganizations(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations WHERE orgCode = :code OR joinCode = :code LIMIT 1")
    suspend fun findByCode(code: String): OrganizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(organization: OrganizationEntity)

    @Delete
    suspend fun deleteOrganization(organization: OrganizationEntity)
}

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams WHERE orgId = :orgId")
    fun getTeamsByOrg(orgId: Long): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Delete
    suspend fun deleteTeam(team: TeamEntity)
}

@Dao
interface OrganizationMembershipDao {
    @Query("SELECT * FROM organization_memberships ORDER BY id DESC")
    fun getAllMemberships(): Flow<List<OrganizationMembershipEntity>>

    @Query("SELECT * FROM organization_memberships WHERE userId = :userId")
    fun getMembershipsByUser(userId: String): Flow<List<OrganizationMembershipEntity>>

    @Query("SELECT * FROM organization_memberships WHERE orgId = :orgId AND status = 'PENDING'")
    fun getPendingRequestsForOrg(orgId: Long): Flow<List<OrganizationMembershipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: OrganizationMembershipEntity)

    @Delete
    suspend fun deleteMembership(membership: OrganizationMembershipEntity)
}

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM connections ORDER BY id DESC")
    fun getAllConnections(): Flow<List<ConnectionEntity>>

    @Query("SELECT * FROM connections WHERE userId = :userId")
    fun getConnectionsByUser(userId: String): Flow<List<ConnectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: ConnectionEntity)

    @Delete
    suspend fun deleteConnection(connection: ConnectionEntity)
}
