package com.example.data

import kotlinx.coroutines.flow.Flow

class TalentRepository(
    private val athleteDao: AthleteDao,
    private val matchLogDao: MatchLogDao,
    private val clubDao: ClubDao,
    private val careerDao: CareerDao,
    private val trainingDao: TrainingDao,
    private val developmentDao: DevelopmentDao,
    private val physicalDao: PhysicalDao,
    private val availabilityDao: AvailabilityDao,
    private val workloadDao: WorkloadDao,
    private val achievementDao: AchievementDao,
    private val opportunityDao: OpportunityDao,
    private val scoutActivityDao: ScoutActivityDao,
    private val evidenceDao: EvidenceDao,
    private val riskProtectionDao: RiskProtectionDao,
    private val insuranceClaimDao: InsuranceClaimDao,
    private val documentDao: DocumentDao,
    private val messageDao: MessageDao,
    private val notificationDao: NotificationDao,
    private val calendarDao: CalendarDao,
    private val paymentDao: PaymentDao,
    private val privacyDao: PrivacyDao,
    private val organizationDao: OrganizationDao,
    private val teamDao: TeamDao,
    private val organizationMembershipDao: OrganizationMembershipDao,
    private val connectionDao: ConnectionDao
) {
    val allAthletes: Flow<List<AthleteEntity>> = athleteDao.getAllAthletes()
    val allMatchLogs: Flow<List<MatchLogEntity>> = matchLogDao.getAllMatchLogs()
    val allClubs: Flow<List<ClubEntity>> = clubDao.getAllClubs()
    val allStints: Flow<List<CareerStintEntity>> = careerDao.getAllStints()
    val allTraining: Flow<List<TrainingSessionEntity>> = trainingDao.getAllSessions()
    val allGoals: Flow<List<DevelopmentGoalEntity>> = developmentDao.getAllGoals()
    val allMeasurements: Flow<List<PhysicalMeasurementEntity>> = physicalDao.getAllMeasurements()
    val allAvailability: Flow<List<AvailabilityEntity>> = availabilityDao.getAllAvailability()
    val allWorkload: Flow<List<WorkloadEntity>> = workloadDao.getAllWorkload()
    val allAchievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    val allOpportunities: Flow<List<OpportunityEntity>> = opportunityDao.getAllOpportunities()
    val allScoutActivity: Flow<List<ScoutActivityEntity>> = scoutActivityDao.getAllActivity()
    val allEvidence: Flow<List<EvidenceEntity>> = evidenceDao.getAllEvidence()
    val allRiskSignals: Flow<List<RiskProtectionEntity>> = riskProtectionDao.getAllSignals()
    val allClaims: Flow<List<InsuranceClaimEntity>> = insuranceClaimDao.getAllClaims()
    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()
    val allMessages: Flow<List<MessageEntity>> = messageDao.getAllMessages()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val allEvents: Flow<List<CalendarEventEntity>> = calendarDao.getAllEvents()
    val allPayments: Flow<List<PaymentEntity>> = paymentDao.getAllPayments()
    val allPrivacySettings: Flow<List<PrivacySettingEntity>> = privacyDao.getAllSettings()
    val allOrganizations: Flow<List<OrganizationEntity>> = organizationDao.getAllOrganizations()
    val allTeams: Flow<List<TeamEntity>> = teamDao.getAllTeams()
    val allMemberships: Flow<List<OrganizationMembershipEntity>> = organizationMembershipDao.getAllMemberships()
    val allConnections: Flow<List<ConnectionEntity>> = connectionDao.getAllConnections()

    suspend fun findOrgByCode(code: String): OrganizationEntity? = organizationDao.findByCode(code)
    suspend fun insertOrganization(org: OrganizationEntity) = organizationDao.insertOrganization(org)
    suspend fun deleteOrganization(org: OrganizationEntity) = organizationDao.deleteOrganization(org)
    suspend fun insertTeam(team: TeamEntity) = teamDao.insertTeam(team)
    suspend fun deleteTeam(team: TeamEntity) = teamDao.deleteTeam(team)
    suspend fun insertMembership(membership: OrganizationMembershipEntity) = organizationMembershipDao.insertMembership(membership)
    suspend fun deleteMembership(membership: OrganizationMembershipEntity) = organizationMembershipDao.deleteMembership(membership)
    suspend fun insertConnection(conn: ConnectionEntity) = connectionDao.insertConnection(conn)
    suspend fun deleteConnection(conn: ConnectionEntity) = connectionDao.deleteConnection(conn)

    suspend fun insertAthlete(athlete: AthleteEntity) = athleteDao.insertAthlete(athlete)
    suspend fun insertAthletes(athletes: List<AthleteEntity>) = athleteDao.insertAthletes(athletes)
    suspend fun deleteAthleteById(firestoreId: String) = athleteDao.deleteAthleteById(firestoreId)
    suspend fun clearAthletes() = athleteDao.clearAthletes()
    suspend fun deleteAthlete(athlete: AthleteEntity) = athleteDao.deleteAthlete(athlete)
    suspend fun insertMatchLog(matchLog: MatchLogEntity) = matchLogDao.insertMatchLog(matchLog)
    suspend fun deleteMatchLog(matchLog: MatchLogEntity) = matchLogDao.deleteMatchLog(matchLog)
    suspend fun insertClub(club: ClubEntity) = clubDao.insertClub(club)
    suspend fun deleteClub(club: ClubEntity) = clubDao.deleteClub(club)
    suspend fun insertStint(stint: CareerStintEntity) = careerDao.insertStint(stint)
    suspend fun deleteStint(stint: CareerStintEntity) = careerDao.deleteStint(stint)
    suspend fun insertSession(session: TrainingSessionEntity) = trainingDao.insertSession(session)
    suspend fun deleteSession(session: TrainingSessionEntity) = trainingDao.deleteSession(session)
    suspend fun insertGoal(goal: DevelopmentGoalEntity) = developmentDao.insertGoal(goal)
    suspend fun deleteGoal(goal: DevelopmentGoalEntity) = developmentDao.deleteGoal(goal)
    suspend fun insertMeasurement(measurement: PhysicalMeasurementEntity) = physicalDao.insertMeasurement(measurement)
    suspend fun insertAvailability(availability: AvailabilityEntity) = availabilityDao.insertAvailability(availability)
    suspend fun deleteAvailability(availability: AvailabilityEntity) = availabilityDao.deleteAvailability(availability)
    suspend fun insertWorkload(workload: WorkloadEntity) = workloadDao.insertWorkload(workload)
    suspend fun deleteWorkload(workload: WorkloadEntity) = workloadDao.deleteWorkload(workload)
    suspend fun insertAchievement(achievement: AchievementEntity) = achievementDao.insertAchievement(achievement)
    suspend fun deleteAchievement(achievement: AchievementEntity) = achievementDao.deleteAchievement(achievement)
    suspend fun insertOpportunity(opportunity: OpportunityEntity) = opportunityDao.insertOpportunity(opportunity)
    suspend fun deleteOpportunity(opportunity: OpportunityEntity) = opportunityDao.deleteOpportunity(opportunity)
    suspend fun insertActivity(activity: ScoutActivityEntity) = scoutActivityDao.insertActivity(activity)
    suspend fun deleteActivity(activity: ScoutActivityEntity) = scoutActivityDao.deleteActivity(activity)
    suspend fun insertEvidence(evidence: EvidenceEntity) = evidenceDao.insertEvidence(evidence)
    suspend fun deleteEvidence(evidence: EvidenceEntity) = evidenceDao.deleteEvidence(evidence)
    suspend fun insertSignal(signal: RiskProtectionEntity) = riskProtectionDao.insertSignal(signal)
    suspend fun insertClaim(claim: InsuranceClaimEntity) = insuranceClaimDao.insertClaim(claim)
    suspend fun insertDocument(document: DocumentEntity) = documentDao.insertDocument(document)
    suspend fun deleteDocument(document: DocumentEntity) = documentDao.deleteDocument(document)
    suspend fun insertMessage(message: MessageEntity) = messageDao.insertMessage(message)
    suspend fun insertNotification(notification: NotificationEntity) = notificationDao.insertNotification(notification)
    suspend fun insertEvent(event: CalendarEventEntity) = calendarDao.insertEvent(event)
    suspend fun insertPayment(payment: PaymentEntity) = paymentDao.insertPayment(payment)
    suspend fun insertSetting(setting: PrivacySettingEntity) = privacyDao.insertSetting(setting)
}
