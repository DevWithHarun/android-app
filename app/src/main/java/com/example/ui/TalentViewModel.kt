package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.auth.AuthRepository
import com.example.data.auth.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class TalentUiState(
    val athletes: List<AthleteEntity> = emptyList(),
    val matchLogs: List<MatchLogEntity> = emptyList(),
    val clubs: List<ClubEntity> = emptyList(),
    val careerStints: List<CareerStintEntity> = emptyList(),
    val trainingSessions: List<TrainingSessionEntity> = emptyList(),
    val developmentGoals: List<DevelopmentGoalEntity> = emptyList(),
    val physicalMeasurements: List<PhysicalMeasurementEntity> = emptyList(),
    val availabilityRecords: List<AvailabilityEntity> = emptyList(),
    val workloadRecords: List<WorkloadEntity> = emptyList(),
    val achievements: List<AchievementEntity> = emptyList(),
    val opportunities: List<OpportunityEntity> = emptyList(),
    val scoutActivities: List<ScoutActivityEntity> = emptyList(),
    val evidenceVault: List<EvidenceEntity> = emptyList(),
    val riskSignals: List<RiskProtectionEntity> = emptyList(),
    val insuranceClaims: List<InsuranceClaimEntity> = emptyList(),
    val documents: List<DocumentEntity> = emptyList(),
    val messages: List<MessageEntity> = emptyList(),
    val notifications: List<NotificationEntity> = emptyList(),
    val calendarEvents: List<CalendarEventEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val privacySettings: List<PrivacySettingEntity> = emptyList(),
    val organizations: List<OrganizationEntity> = emptyList(),
    val teams: List<TeamEntity> = emptyList(),
    val memberships: List<OrganizationMembershipEntity> = emptyList(),
    val connections: List<ConnectionEntity> = emptyList(),
    val selectedAthleteId: String? = null,
    val searchQuery: String = "",
    val selectedSportFilter: String = "All",
    val isLoadingAthletes: Boolean = false,
    val isAuthModalOpen: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userEmail: String = "",
    val userName: String = "Athlete",
    val userPhotoUrl: String = "",
    val userRole: UserRole = UserRole.ATHLETE,
    val userData: Map<String, Any?>? = null
)

class TalentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TalentDatabase.getDatabase(application)
    private val repository = TalentRepository(
        database.athleteDao(),
        database.matchLogDao(),
        database.clubDao(),
        database.careerDao(),
        database.trainingDao(),
        database.developmentDao(),
        database.physicalDao(),
        database.availabilityDao(),
        database.workloadDao(),
        database.achievementDao(),
        database.opportunityDao(),
        database.scoutActivityDao(),
        database.evidenceDao(),
        database.riskProtectionDao(),
        database.insuranceClaimDao(),
        database.documentDao(),
        database.messageDao(),
        database.notificationDao(),
        database.calendarDao(),
        database.paymentDao(),
        database.privacyDao(),
        database.organizationDao(),
        database.teamDao(),
        database.organizationMembershipDao(),
        database.connectionDao()
    )
    private val authRepository = AuthRepository()
    private val firestore = FirebaseFirestore.getInstance()

    private fun loadInitialSession(): TalentUiState {
        val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("email", "") ?: ""
        val isAuth = prefs.getBoolean("is_authenticated", savedEmail.isNotBlank())
        val savedRoleStr = prefs.getString("role", "athlete")
        val savedRole = UserRole.fromValue(savedRoleStr)
        val savedName = prefs.getString("name", "")?.takeIf { it.isNotBlank() } ?: "Athlete"
        val savedPhoto = prefs.getString("photoUrl", "") ?: ""
        val localProfile = prefs.all.mapValues { it.value }

        return TalentUiState(
            isAuthenticated = isAuth && savedEmail.isNotBlank(),
            userEmail = savedEmail,
            userName = savedName,
            userPhotoUrl = savedPhoto,
            userRole = if (savedRole != UserRole.NONE) savedRole else UserRole.ATHLETE,
            userData = localProfile.ifEmpty { null }
        )
    }

    private val _uiState = MutableStateFlow(loadInitialSession())
    private var userProfileListener: com.google.firebase.firestore.ListenerRegistration? = null

    private val baseDataFlow = combine(
        repository.allAthletes,
        repository.allMatchLogs,
        repository.allClubs,
        repository.allStints,
        repository.allTraining,
        repository.allGoals,
        repository.allMeasurements,
        repository.allAvailability,
        repository.allWorkload,
        repository.allAchievements,
        repository.allOpportunities,
        repository.allScoutActivity,
        repository.allEvidence,
        repository.allRiskSignals
    ) { flows -> flows }

    private val platformDataFlow = combine(
        repository.allClaims,
        repository.allDocuments,
        repository.allMessages,
        repository.allNotifications,
        repository.allEvents,
        repository.allPayments,
        repository.allPrivacySettings,
        repository.allOrganizations,
        repository.allTeams,
        repository.allMemberships,
        repository.allConnections
    ) { flows -> flows }

    val uiState: StateFlow<TalentUiState> = combine(
        baseDataFlow,
        platformDataFlow,
        _uiState
    ) { baseFlows, platformFlows, state ->
        val athletes = baseFlows[0] as List<AthleteEntity>
        val matchLogs = baseFlows[1] as List<MatchLogEntity>
        val clubs = baseFlows[2] as List<ClubEntity>
        val stints = baseFlows[3] as List<CareerStintEntity>
        val training = baseFlows[4] as List<TrainingSessionEntity>
        val goals = baseFlows[5] as List<DevelopmentGoalEntity>
        val measurements = baseFlows[6] as List<PhysicalMeasurementEntity>
        val availability = baseFlows[7] as List<AvailabilityEntity>
        val workload = baseFlows[8] as List<WorkloadEntity>
        val achievements = baseFlows[9] as List<AchievementEntity>
        val opportunities = baseFlows[10] as List<OpportunityEntity>
        val scoutActivity = baseFlows[11] as List<ScoutActivityEntity>
        val evidence = baseFlows[12] as List<EvidenceEntity>
        val riskSignals = baseFlows[13] as List<RiskProtectionEntity>

        val claims = platformFlows[0] as List<InsuranceClaimEntity>
        val documents = platformFlows[1] as List<DocumentEntity>
        val messages = platformFlows[2] as List<MessageEntity>
        val notifications = platformFlows[3] as List<NotificationEntity>
        val events = platformFlows[4] as List<CalendarEventEntity>
        val payments = platformFlows[5] as List<PaymentEntity>
        val privacy = platformFlows[6] as List<PrivacySettingEntity>
        val organizations = platformFlows[7] as List<OrganizationEntity>
        val teams = platformFlows[8] as List<TeamEntity>
        val memberships = platformFlows[9] as List<OrganizationMembershipEntity>
        val connections = platformFlows[10] as List<ConnectionEntity>

        state.copy(
            athletes = athletes,
            matchLogs = matchLogs,
            clubs = clubs,
            careerStints = stints,
            trainingSessions = training,
            developmentGoals = goals,
            physicalMeasurements = measurements,
            availabilityRecords = availability,
            workloadRecords = workload,
            achievements = achievements,
            opportunities = opportunities,
            scoutActivities = scoutActivity,
            evidenceVault = evidence,
            riskSignals = riskSignals,
            insuranceClaims = claims,
            documents = documents,
            messages = messages,
            notifications = notifications,
            calendarEvents = events,
            payments = payments,
            privacySettings = privacy,
            organizations = organizations,
            teams = teams,
            memberships = memberships,
            connections = connections
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = loadInitialSession()
    )

    init {
        checkAuthStatus()
        purgeMockData()
        seedDefaultOrganizationsAndConnections()
        syncAthletesFromRemote()
        syncClubsAndSquadFromRemote()
        syncDocumentsFromRemote()
        syncMatchesFromRemote()
        syncDevelopmentFromRemote()
        syncIntelligenceFromRemote()
    }

    private val mockKeywords = listOf(
        "faith_kipyegon", "ferdinand_omanyala", "victor_wanyama", "collins_sichenje",
        "erick_otieno", "joseph_okumu", "michael_olunga", "richard_odada",
        "timothy_ouma", "tyler_ongwae",
        "kipyegon", "kipegon", "omanyala", "wanyama", "wanayama",
        "sichenje", "olunga", "otieno", "okumu", "odada", "ouma", "ongwae"
    )

    private fun isMockAthlete(id: String, name: String = ""): Boolean {
        val idLower = id.lowercase().trim()
        val nameLower = name.lowercase().trim()
        return mockKeywords.any { kw ->
            idLower == kw || idLower.contains(kw) || nameLower.contains(kw)
        }
    }

    private fun purgeMockData() {
        viewModelScope.launch {
            try {
                // Delete mock athletes from Room repository and Firestore
                repository.allAthletes.first().forEach { athlete ->
                    if (isMockAthlete(athlete.firestoreId, athlete.name)) {
                        repository.deleteAthleteById(athlete.firestoreId)
                        try {
                            firestore.collection("athletes").document(athlete.firestoreId).delete()
                        } catch (e: Exception) {}
                    }
                }

                mockKeywords.forEach { mockId ->
                    repository.deleteAthleteById(mockId)
                    try {
                        firestore.collection("athletes").document(mockId).delete()
                    } catch (e: Exception) {}
                }

                val mockClubNames = listOf("Coastal FC", "Mombasa Elite Academy", "Nyali Youth Sports Centre")
                repository.allStints.first().forEach { stint ->
                    if (stint.organization in mockClubNames) {
                        repository.deleteStint(stint)
                    }
                }
                repository.allAchievements.first().forEach { ach ->
                    if (ach.organization in mockClubNames || ach.organization == "Kenya Football Federation") {
                        repository.deleteAchievement(ach)
                    }
                }
                repository.allMatchLogs.first().forEach { match ->
                    if (match.verifiedByCoach in listOf("Coach David", "Coach Omondi") ||
                        mockClubNames.any { match.matchTitle.contains(it) }) {
                        repository.deleteMatchLog(match)
                    }
                }
                repository.allDocuments.first().forEach { doc ->
                    if (mockClubNames.any { doc.title.contains(it) } || doc.title.contains("FKF Federation Clearance")) {
                        repository.deleteDocument(doc)
                    }
                }
            } catch (e: Exception) {}
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
            val isAuth = prefs.getBoolean("is_authenticated", false)
            val savedEmail = prefs.getString("email", "") ?: ""
            val savedRoleStr = prefs.getString("role", "athlete")
            val savedRole = UserRole.fromValue(savedRoleStr)
            val savedName = prefs.getString("name", "Athlete")?.takeIf { it.isNotBlank() } ?: "Athlete"
            val savedPhoto = prefs.getString("photoUrl", "") ?: ""
            val localProfile = prefs.all.mapValues { it.value }

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null || (isAuth && savedEmail.isNotBlank())) {
                val activeEmail = user?.email ?: savedEmail
                val fallbackRole = if (savedRole != UserRole.NONE) savedRole else UserRole.ATHLETE

                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        userEmail = activeEmail,
                        userName = savedName,
                        userPhotoUrl = savedPhoto,
                        userRole = fallbackRole,
                        userData = localProfile,
                        isAuthModalOpen = false
                    )
                }

                if (user != null) {
                    try {
                        val remoteRole = try { authRepository.resolveRole(user.uid) } catch (e: Exception) { UserRole.NONE }
                        val remoteProfile = try { authRepository.getUserProfile(user.uid) } catch (e: Exception) { null } ?: emptyMap()
                        val mergedProfile = localProfile + remoteProfile
                        val finalRole = if (remoteRole != UserRole.NONE) remoteRole else fallbackRole
                        val nameFromProfile = mergedProfile["name"] as? String ?: mergedProfile["fullName"] as? String
                        val actualName = nameFromProfile ?: user.displayName ?: savedName
                        val photoUrl = com.example.ui.util.AthleteNameResolver.resolvePhotoUrl(mergedProfile)
                            ?: user.photoUrl?.toString()
                            ?: savedPhoto

                        _uiState.update { current ->
                            current.copy(
                                userName = actualName,
                                userPhotoUrl = photoUrl,
                                userRole = finalRole,
                                userData = mergedProfile
                            )
                        }

                        prefs.edit()
                            .putBoolean("is_authenticated", true)
                            .putString("name", actualName)
                            .putString("role", finalRole.value)
                            .putString("photoUrl", photoUrl)
                            .commit()

                        attachUserListeners(user.uid, finalRole)
                    } catch (e: Exception) {}
                }
            } else {
                userProfileListener?.remove()
                val localName = localProfile["name"] as? String ?: localProfile["fullName"] as? String ?: "Athlete"
                val localPhoto = com.example.ui.util.AthleteNameResolver.resolvePhotoUrl(localProfile) ?: ""
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        userEmail = "",
                        userName = localName,
                        userPhotoUrl = localPhoto,
                        userRole = UserRole.ATHLETE,
                        userData = localProfile.ifEmpty { null }
                    )
                }
            }
        }
    }

    private fun attachUserListeners(uid: String, fallbackRole: UserRole) {
        userProfileListener?.remove()
        userProfileListener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data ?: emptyMap()
                    val remoteRole = UserRole.fromValue(data["role"] as? String)
                    val activeRole = if (remoteRole != UserRole.NONE) remoteRole else fallbackRole
                    val name = data["name"] as? String ?: data["fullName"] as? String
                    val photo = com.example.ui.util.AthleteNameResolver.resolvePhotoUrl(data)

                    _uiState.update { current ->
                        current.copy(
                            userName = name ?: current.userName,
                            userPhotoUrl = photo ?: current.userPhotoUrl,
                            userRole = activeRole,
                            userData = (current.userData ?: emptyMap()) + data
                        )
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        userProfileListener?.remove()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSportFilter(sport: String) {
        _uiState.update { it.copy(selectedSportFilter = sport) }
    }

    fun setSelectedAthlete(id: String?) {
        _uiState.update { it.copy(selectedAthleteId = id) }
    }

    fun setAuthModalOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isAuthModalOpen = isOpen) }
    }

    fun authenticateUser(email: String, role: UserRole = UserRole.ATHLETE) {
        val resolvedRole = if (role != UserRole.NONE) role else UserRole.ATHLETE
        val emailPrefix = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        val currentName = _uiState.value.userName.takeIf { it.isNotBlank() && it != "Athlete" && it != "User" } ?: emailPrefix

        // Update state synchronously and immediately - firmly authenticated
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                userEmail = email,
                userName = currentName,
                userRole = resolvedRole,
                isAuthModalOpen = false
            )
        }

        // Persist session to SharedPreferences immediately with commit()
        try {
            val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
            prefs.edit()
                .putBoolean("is_authenticated", true)
                .putString("email", email)
                .putString("name", currentName)
                .putString("role", resolvedRole.value)
                .putString("photoUrl", _uiState.value.userPhotoUrl)
                .commit()
        } catch (e: Exception) {}

        // In background, sync with Firebase Auth and Firestore profile without wiping authentication
        viewModelScope.launch {
            try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val remoteProfile = try { authRepository.getUserProfile(user.uid) } catch (e: Exception) { null }
                    val nameFromProfile = remoteProfile?.get("name") as? String ?: remoteProfile?.get("fullName") as? String
                    val actualName = nameFromProfile ?: user.displayName ?: currentName
                    val photoUrl = user.photoUrl?.toString() ?: (remoteProfile?.get("photoUrl") as? String) ?: _uiState.value.userPhotoUrl
                    val remoteRole = try { authRepository.resolveRole(user.uid) } catch (e: Exception) { UserRole.NONE }
                    val finalRole = if (remoteRole != UserRole.NONE) remoteRole else resolvedRole

                    _uiState.update { current ->
                        current.copy(
                            userName = actualName,
                            userPhotoUrl = photoUrl,
                            userRole = finalRole,
                            userData = (current.userData ?: emptyMap()) + (remoteProfile ?: emptyMap())
                        )
                    }

                    try {
                        val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
                        prefs.edit()
                            .putBoolean("is_authenticated", true)
                            .putString("name", actualName)
                            .putString("role", finalRole.value)
                            .putString("photoUrl", photoUrl)
                            .commit()
                    } catch (e: Exception) {}

                    attachUserListeners(user.uid, finalRole)
                }
            } catch (e: Exception) {}
        }
    }

    fun logout() {
        authRepository.signOut()
        userProfileListener?.remove()
        try {
            val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
            prefs.edit().clear().commit()
        } catch (e: Exception) {}
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                userEmail = "",
                userName = "Athlete",
                userPhotoUrl = "",
                userRole = UserRole.ATHLETE,
                userData = null
            )
        }
    }

    fun updateUserProfile(updates: Map<String, Any?>) {
        viewModelScope.launch {
            val currentMap = _uiState.value.userData?.toMutableMap() ?: mutableMapOf()
            currentMap.putAll(updates)

            val updatedPhotoUrl = (updates["photoUrl"] as? String)?.takeIf { it.isNotBlank() }
                ?: _uiState.value.userPhotoUrl
            val updatedName = (updates["fullName"] as? String ?: updates["name"] as? String)?.takeIf { it.isNotBlank() }
                ?: _uiState.value.userName

            _uiState.update {
                it.copy(
                    userName = updatedName,
                    userPhotoUrl = updatedPhotoUrl,
                    userData = currentMap
                )
            }

            try {
                val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                for ((key, value) in currentMap) {
                    if (value is String) editor.putString(key, value)
                    else if (value is Boolean) editor.putBoolean(key, value)
                    else if (value is Int) editor.putInt(key, value)
                    else if (value is Long) editor.putLong(key, value)
                }
                editor.apply()
            } catch (e: Exception) {}

            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                try {
                    authRepository.updateUserProfile(user.uid, updates)
                } catch (e: Exception) {}
            }

            // Profile update saved successfully
        }
    }

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_athlete"
    }

    fun addMatchLog(athleteId: Long, title: String, mins: Int, goals: Int, assists: Int, rating: Int, coach: String) {
        viewModelScope.launch {
            val log = MatchLogEntity(athleteId = athleteId, matchTitle = title, minutesPlayed = mins, goals = goals, assists = assists, matchRating = rating, verifiedByCoach = coach)
            repository.insertMatchLog(log)
            try {
                firestore.collection("athletes").document(getUserId()).collection("matches").add(log).await()
            } catch (e: Exception) {}
        }
    }

    fun updateMatchLog(log: MatchLogEntity) {
        viewModelScope.launch {
            repository.insertMatchLog(log)
            try {
                firestore.collection("athletes").document(getUserId()).collection("matches").document(log.id.toString()).set(log).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteMatchLog(log: MatchLogEntity) {
        viewModelScope.launch {
            repository.deleteMatchLog(log)
            try {
                firestore.collection("athletes").document(getUserId()).collection("matches").document(log.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun syncMatchesFromRemote() {
        viewModelScope.launch {
            val uid = getUserId()
            try {
                val matchSnap = firestore.collection("athletes").document(uid).collection("matches").get().await()
                matchSnap.documents.forEach { doc ->
                    val athleteId = doc.getLong("athleteId") ?: 0L
                    val title = doc.getString("matchTitle") ?: return@forEach
                    val mins = (doc.getLong("minutesPlayed") ?: 90L).toInt()
                    val goals = (doc.getLong("goals") ?: 0L).toInt()
                    val assists = (doc.getLong("assists") ?: 0L).toInt()
                    val rating = (doc.getLong("matchRating") ?: 7L).toInt()
                    val coach = doc.getString("verifiedByCoach") ?: "Head Coach"
                    val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    repository.insertMatchLog(MatchLogEntity(
                        athleteId = athleteId,
                        matchTitle = title,
                        minutesPlayed = mins,
                        goals = goals,
                        assists = assists,
                        matchRating = rating,
                        verifiedByCoach = coach,
                        timestamp = timestamp
                    ))
                }
            } catch (e: Exception) {}
        }
    }

    fun addClub(clubName: String, role: String, staffCount: Int, squadSize: Int, location: String) {
        viewModelScope.launch {
            val club = ClubEntity(clubName = clubName, role = role, staffCount = staffCount, activeSquadSize = squadSize, location = location)
            repository.insertClub(club)
            try {
                firestore.collection("athletes").document(getUserId()).collection("clubs").add(club).await()
            } catch (e: Exception) {}
        }
    }

    fun updateClub(club: ClubEntity) {
        viewModelScope.launch {
            repository.insertClub(club)
            try {
                firestore.collection("athletes").document(getUserId()).collection("clubs").document(club.id.toString()).set(club).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteClub(club: ClubEntity) {
        viewModelScope.launch {
            repository.deleteClub(club)
            try {
                firestore.collection("athletes").document(getUserId()).collection("clubs").document(club.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addSquadMember(name: String, position: String, rating: Int, clubName: String, isVerified: Boolean = true) {
        viewModelScope.launch {
            val member = AthleteEntity(
                firestoreId = java.util.UUID.randomUUID().toString(),
                name = name,
                sport = "Football",
                position = position,
                rating = rating,
                verifiedStats = "Registered Squad Player",
                clubName = clubName,
                joinedDate = "2024",
                isVerified = isVerified
            )
            repository.insertAthlete(member)
            try {
                firestore.collection("athletes").document(getUserId()).collection("squad").add(member).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteSquadMember(athlete: AthleteEntity) {
        viewModelScope.launch {
            repository.deleteAthlete(athlete)
            try {
                firestore.collection("athletes").document(getUserId()).collection("squad").document(athlete.firestoreId).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun syncAthletesFromRemote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAthletes = true) }
            try {
                val athleteSnap = firestore.collection("athletes").get().await()
                val fetchedIds = mutableSetOf<String>()
                val remoteAthletes = mutableListOf<AthleteEntity>()

                athleteSnap.documents.forEach { doc ->
                    val firestoreId = doc.id
                    val name = com.example.ui.util.AthleteNameResolver.resolveFromDoc(doc)
                    val isMock = isMockAthlete(firestoreId, name)
                    if (isMock) {
                        repository.deleteAthleteById(firestoreId)
                        try {
                            firestore.collection("athletes").document(firestoreId).delete()
                        } catch (e: Exception) {}
                        return@forEach
                    }

                    fetchedIds.add(firestoreId)

                    val photoUrl = com.example.ui.util.AthleteNameResolver.resolvePhotoUrl(doc)
                    val sport = doc.getString("sport") ?: "Football (Soccer)"
                    val pos = doc.getString("position")?.trim()?.ifBlank { null }
                        ?: doc.getString("role")?.trim()?.ifBlank { null }
                        ?: "Position: Not provided"
                    val rating = (doc.getLong("rating") ?: 80L).toInt()
                    val stats = doc.getString("verifiedStats") ?: doc.getString("bio") ?: "Verified Talent Graph Athlete"
                    val club = doc.getString("clubName") ?: doc.getString("club") ?: "Independent"
                    val joined = doc.getString("joinedDate") ?: "2024"
                    val isVerified = doc.getBoolean("isVerified") ?: true

                    remoteAthletes.add(
                        AthleteEntity(
                            firestoreId = firestoreId,
                            name = name,
                            sport = sport,
                            position = pos,
                            rating = rating,
                            verifiedStats = stats,
                            clubName = club,
                            joinedDate = joined,
                            isVerified = isVerified,
                            photoUrl = photoUrl
                        )
                    )
                }

                // Remove stale rows not present in Firestore or matching mock keywords
                val existingAthletes = repository.allAthletes.first()
                existingAthletes.forEach { existing ->
                    if (!fetchedIds.contains(existing.firestoreId) || isMockAthlete(existing.firestoreId, existing.name)) {
                        repository.deleteAthleteById(existing.firestoreId)
                    }
                }

                if (remoteAthletes.isNotEmpty()) {
                    repository.insertAthletes(remoteAthletes)
                }
            } catch (e: Exception) {
                android.util.Log.e("TalentViewModel", "Error syncing athletes from remote", e)
            } finally {
                _uiState.update { it.copy(isLoadingAthletes = false) }
            }
        }
    }

    fun syncClubsAndSquadFromRemote() {
        viewModelScope.launch {
            val uid = getUserId()
            try {
                val clubSnap = firestore.collection("athletes").document(uid).collection("clubs").get().await()
                clubSnap.documents.forEach { doc ->
                    val name = doc.getString("clubName") ?: return@forEach
                    val role = doc.getString("role") ?: "Current Squad"
                    val staff = (doc.getLong("staffCount") ?: 0L).toInt()
                    val squad = (doc.getLong("activeSquadSize") ?: 0L).toInt()
                    val loc = doc.getString("location") ?: ""
                    repository.insertClub(ClubEntity(clubName = name, role = role, staffCount = staff, activeSquadSize = squad, location = loc))
                }

                val squadSnap = firestore.collection("athletes").document(uid).collection("squad").get().await()
                squadSnap.documents.forEach { doc ->
                    val name = doc.getString("name") ?: return@forEach
                    if (isMockAthlete(doc.id, name)) {
                        repository.deleteAthleteById(doc.id)
                        return@forEach
                    }
                    val pos = doc.getString("position")?.trim()?.ifBlank { null } ?: "Position: Not provided"
                    val rating = (doc.getLong("rating") ?: 75L).toInt()
                    val club = doc.getString("clubName") ?: ""
                    val joined = doc.getString("joinedDate") ?: "2024"
                    val verified = doc.getBoolean("isVerified") ?: true
                    val photoUrl = com.example.ui.util.AthleteNameResolver.resolvePhotoUrl(doc)
                    repository.insertAthlete(AthleteEntity(firestoreId = doc.id, name = name, sport = "Football", position = pos, rating = rating, verifiedStats = "Registered Squad Player", clubName = club, joinedDate = joined, isVerified = verified, photoUrl = photoUrl))
                }
            } catch (e: Exception) {}
        }
    }

    fun addCareerStint(org: String, team: String, pos: String, comp: String, start: String, end: String, status: String, verification: String) {
        viewModelScope.launch {
            val stint = CareerStintEntity(organization = org, team = team, position = pos, competition = comp, startDate = start, endDate = end, status = status, verificationLevel = verification)
            repository.insertStint(stint)
            try {
                firestore.collection("athletes").document(getUserId()).collection("career").add(stint).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteCareerStint(stint: CareerStintEntity) {
        viewModelScope.launch {
            repository.deleteStint(stint)
        }
    }

    fun updateCareerStint(stint: CareerStintEntity) {
        viewModelScope.launch {
            repository.insertStint(stint)
            try {
                firestore.collection("athletes").document(getUserId()).collection("career").document(stint.id.toString()).set(stint).await()
            } catch (e: Exception) {}
        }
    }

    fun addTrainingSession(type: String, date: String, duration: Int, intensity: String, notes: String, attendance: String) {
        viewModelScope.launch {
            val session = TrainingSessionEntity(sessionType = type, date = date, durationMins = duration, intensity = intensity, coachNotes = notes, attendance = attendance)
            repository.insertSession(session)
            try {
                firestore.collection("athletes").document(getUserId()).collection("training").add(session).await()
            } catch (e: Exception) {}
        }
    }

    fun updateTrainingSession(session: TrainingSessionEntity) {
        viewModelScope.launch {
            repository.insertSession(session)
            try {
                firestore.collection("athletes").document(getUserId()).collection("training").document(session.id.toString()).set(session).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteTrainingSession(session: TrainingSessionEntity) {
        viewModelScope.launch {
            repository.deleteSession(session)
            try {
                firestore.collection("athletes").document(getUserId()).collection("training").document(session.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addDevelopmentGoal(statement: String, dimension: String, targetDate: String, current: Int, target: Int, status: String) {
        viewModelScope.launch {
            val goal = DevelopmentGoalEntity(goalStatement = statement, dimension = dimension, targetDate = targetDate, currentLevel = current, targetLevel = target, status = status)
            repository.insertGoal(goal)
            try {
                firestore.collection("athletes").document(getUserId()).collection("goals").add(goal).await()
            } catch (e: Exception) {}
        }
    }

    fun updateDevelopmentGoal(goal: DevelopmentGoalEntity) {
        viewModelScope.launch {
            repository.insertGoal(goal)
            try {
                firestore.collection("athletes").document(getUserId()).collection("goals").document(goal.id.toString()).set(goal).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteDevelopmentGoal(goal: DevelopmentGoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            try {
                firestore.collection("athletes").document(getUserId()).collection("goals").document(goal.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addPhysicalMeasurement(testType: String, value: String, unit: String, date: String, verification: String) {
        viewModelScope.launch {
            val measurement = PhysicalMeasurementEntity(testType = testType, value = value, unit = unit, testDate = date, verificationStatus = verification)
            repository.insertMeasurement(measurement)
            try {
                firestore.collection("athletes").document(getUserId()).collection("physical").add(measurement).await()
            } catch (e: Exception) {}
        }
    }

    fun addAvailability(status: String, returnDate: String, reason: String) {
        viewModelScope.launch {
            val availability = AvailabilityEntity(status = status, expectedReturnDate = returnDate, reason = reason)
            repository.insertAvailability(availability)
            try {
                firestore.collection("athletes").document(getUserId()).collection("availability").add(availability).await()
            } catch (e: Exception) {}
        }
    }

    fun updateAvailability(availability: AvailabilityEntity) {
        viewModelScope.launch {
            repository.insertAvailability(availability)
            try {
                firestore.collection("athletes").document(getUserId()).collection("availability").document(availability.id.toString()).set(availability).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteAvailability(availability: AvailabilityEntity) {
        viewModelScope.launch {
            repository.deleteAvailability(availability)
            try {
                firestore.collection("athletes").document(getUserId()).collection("availability").document(availability.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addWorkload(sevenDayLoad: Int, twentyEightDayLoad: Int, matchMinutes: Int, signalStatus: String, recommendation: String) {
        viewModelScope.launch {
            val workload = WorkloadEntity(sevenDayLoad = sevenDayLoad, twentyEightDayLoad = twentyEightDayLoad, matchMinutes = matchMinutes, signalStatus = signalStatus, recommendation = recommendation)
            repository.insertWorkload(workload)
            try {
                firestore.collection("athletes").document(getUserId()).collection("workload").add(workload).await()
            } catch (e: Exception) {}
        }
    }

    fun updateWorkload(workload: WorkloadEntity) {
        viewModelScope.launch {
            repository.insertWorkload(workload)
            try {
                firestore.collection("athletes").document(getUserId()).collection("workload").document(workload.id.toString()).set(workload).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteWorkload(workload: WorkloadEntity) {
        viewModelScope.launch {
            repository.deleteWorkload(workload)
            try {
                firestore.collection("athletes").document(getUserId()).collection("workload").document(workload.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addAchievement(title: String, comp: String, org: String, date: String, category: String, verification: String) {
        viewModelScope.launch {
            val achievement = AchievementEntity(title = title, competition = comp, organization = org, date = date, category = category, verificationLevel = verification)
            repository.insertAchievement(achievement)
            try {
                firestore.collection("athletes").document(getUserId()).collection("achievements").add(achievement).await()
            } catch (e: Exception) {}
        }
    }

    fun updateAchievement(achievement: AchievementEntity) {
        viewModelScope.launch {
            repository.insertAchievement(achievement)
            try {
                firestore.collection("athletes").document(getUserId()).collection("achievements").document(achievement.id.toString()).set(achievement).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteAchievement(achievement: AchievementEntity) {
        viewModelScope.launch {
            repository.deleteAchievement(achievement)
            try {
                firestore.collection("athletes").document(getUserId()).collection("achievements").document(achievement.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addEvidence(title: String, type: String, verification: String, date: String) {
        viewModelScope.launch {
            val evidence = EvidenceEntity(title = title, type = type, verificationState = verification, dateUploaded = date)
            repository.insertEvidence(evidence)
            try {
                firestore.collection("athletes").document(getUserId()).collection("evidence").add(evidence).await()
            } catch (e: Exception) {}
        }
    }

    fun updateEvidence(evidence: EvidenceEntity) {
        viewModelScope.launch {
            repository.insertEvidence(evidence)
            try {
                firestore.collection("athletes").document(getUserId()).collection("evidence").document(evidence.id.toString()).set(evidence).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteEvidence(evidence: EvidenceEntity) {
        viewModelScope.launch {
            repository.deleteEvidence(evidence)
            try {
                firestore.collection("athletes").document(getUserId()).collection("evidence").document(evidence.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun syncDevelopmentFromRemote() {
        viewModelScope.launch {
            val uid = getUserId()
            try {
                // Goals
                val goalSnap = firestore.collection("athletes").document(uid).collection("goals").get().await()
                goalSnap.documents.forEach { doc ->
                    val stmt = doc.getString("goalStatement") ?: return@forEach
                    val dim = doc.getString("dimension") ?: "Technical"
                    val targetDate = doc.getString("targetDate") ?: "Next Month"
                    val current = (doc.getLong("currentLevel") ?: 50L).toInt()
                    val target = (doc.getLong("targetLevel") ?: 90L).toInt()
                    val status = doc.getString("status") ?: "In Progress"
                    repository.insertGoal(DevelopmentGoalEntity(goalStatement = stmt, dimension = dim, targetDate = targetDate, currentLevel = current, targetLevel = target, status = status))
                }

                // Training
                val trainSnap = firestore.collection("athletes").document(uid).collection("training").get().await()
                trainSnap.documents.forEach { doc ->
                    val type = doc.getString("sessionType") ?: return@forEach
                    val date = doc.getString("date") ?: "Recent"
                    val dur = (doc.getLong("durationMins") ?: 90L).toInt()
                    val intensity = doc.getString("intensity") ?: "High"
                    val notes = doc.getString("coachNotes") ?: ""
                    val att = doc.getString("attendance") ?: "Present"
                    repository.insertSession(TrainingSessionEntity(sessionType = type, date = date, durationMins = dur, intensity = intensity, coachNotes = notes, attendance = att))
                }

                // Achievements
                val achSnap = firestore.collection("athletes").document(uid).collection("achievements").get().await()
                achSnap.documents.forEach { doc ->
                    val title = doc.getString("title") ?: return@forEach
                    val comp = doc.getString("competition") ?: ""
                    val org = doc.getString("organization") ?: ""
                    val date = doc.getString("date") ?: ""
                    val cat = doc.getString("category") ?: "Individual Award"
                    val ver = doc.getString("verificationLevel") ?: "Verified"
                    repository.insertAchievement(AchievementEntity(title = title, competition = comp, organization = org, date = date, category = cat, verificationLevel = ver))
                }

                // Evidence
                val evSnap = firestore.collection("athletes").document(uid).collection("evidence").get().await()
                evSnap.documents.forEach { doc ->
                    val title = doc.getString("title") ?: return@forEach
                    val type = doc.getString("type") ?: "Video Highlight"
                    val ver = doc.getString("verificationState") ?: "Verified"
                    val date = doc.getString("dateUploaded") ?: "2024"
                    repository.insertEvidence(EvidenceEntity(title = title, type = type, verificationState = ver, dateUploaded = date))
                }
            } catch (e: Exception) {}
        }
    }

    fun addOpportunity(title: String, type: String, location: String, deadline: String, desc: String, status: String = "Open") {
        viewModelScope.launch {
            val opp = OpportunityEntity(title = title, type = type, location = location, deadline = deadline, description = desc, status = status)
            repository.insertOpportunity(opp)
            try {
                firestore.collection("athletes").document(getUserId()).collection("opportunities").add(opp).await()
            } catch (e: Exception) {}
        }
    }

    fun applyOpportunity(title: String, type: String, location: String, deadline: String, desc: String) {
        addOpportunity(title, type, location, deadline, desc, "Applied")
    }

    fun updateOpportunity(opp: OpportunityEntity) {
        viewModelScope.launch {
            repository.insertOpportunity(opp)
            try {
                firestore.collection("athletes").document(getUserId()).collection("opportunities").document(opp.id.toString()).set(opp).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteOpportunity(opp: OpportunityEntity) {
        viewModelScope.launch {
            repository.deleteOpportunity(opp)
            try {
                firestore.collection("athletes").document(getUserId()).collection("opportunities").document(opp.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun addScoutActivity(viewerName: String, action: String, timestamp: String, sectionAccessed: String) {
        viewModelScope.launch {
            val activity = ScoutActivityEntity(viewerName = viewerName, action = action, timestamp = timestamp, sectionAccessed = sectionAccessed)
            repository.insertActivity(activity)
            try {
                firestore.collection("athletes").document(getUserId()).collection("scout_activity").add(activity).await()
            } catch (e: Exception) {}
        }
    }

    fun updateScoutActivity(activity: ScoutActivityEntity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
            try {
                firestore.collection("athletes").document(getUserId()).collection("scout_activity").document(activity.id.toString()).set(activity).await()
            } catch (e: Exception) {}
        }
    }

    fun deleteScoutActivity(activity: ScoutActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
            try {
                firestore.collection("athletes").document(getUserId()).collection("scout_activity").document(activity.id.toString()).delete().await()
            } catch (e: Exception) {}
        }
    }

    fun syncIntelligenceFromRemote() {
        viewModelScope.launch {
            val uid = getUserId()
            try {
                // Availability
                val availSnap = firestore.collection("athletes").document(uid).collection("availability").get().await()
                availSnap.documents.forEach { doc ->
                    val status = doc.getString("status") ?: return@forEach
                    val returnDate = doc.getString("expectedReturnDate") ?: ""
                    val reason = doc.getString("reason") ?: ""
                    val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                    repository.insertAvailability(AvailabilityEntity(status = status, expectedReturnDate = returnDate, reason = reason, timestamp = ts))
                }

                // Workload
                val workSnap = firestore.collection("athletes").document(uid).collection("workload").get().await()
                workSnap.documents.forEach { doc ->
                    val seven = (doc.getLong("sevenDayLoad") ?: 0L).toInt()
                    val twentyEight = (doc.getLong("twentyEightDayLoad") ?: 0L).toInt()
                    val matchMins = (doc.getLong("matchMinutes") ?: 0L).toInt()
                    val signal = doc.getString("signalStatus") ?: "Optimal"
                    val rec = doc.getString("recommendation") ?: "Maintain routine"
                    repository.insertWorkload(WorkloadEntity(sevenDayLoad = seven, twentyEightDayLoad = twentyEight, matchMinutes = matchMins, signalStatus = signal, recommendation = rec))
                }

                // Opportunities
                val oppSnap = firestore.collection("athletes").document(uid).collection("opportunities").get().await()
                oppSnap.documents.forEach { doc ->
                    val title = doc.getString("title") ?: return@forEach
                    val type = doc.getString("type") ?: "Trial"
                    val loc = doc.getString("location") ?: ""
                    val deadline = doc.getString("deadline") ?: ""
                    val desc = doc.getString("description") ?: ""
                    val stat = doc.getString("status") ?: "Open"
                    repository.insertOpportunity(OpportunityEntity(title = title, type = type, location = loc, deadline = deadline, description = desc, status = stat))
                }

                // Scout Activity
                val scoutSnap = firestore.collection("athletes").document(uid).collection("scout_activity").get().await()
                scoutSnap.documents.forEach { doc ->
                    val name = doc.getString("viewerName") ?: return@forEach
                    val act = doc.getString("action") ?: "Viewed Profile"
                    val ts = doc.getString("timestamp") ?: "Recently"
                    val sec = doc.getString("sectionAccessed") ?: "Full Passport"
                    repository.insertActivity(ScoutActivityEntity(viewerName = name, action = act, timestamp = ts, sectionAccessed = sec))
                }
            } catch (e: Exception) {}
        }
    }

    fun addDocument(
        title: String,
        category: String,
        state: String,
        issueDate: String = "2024",
        expiryDate: String = "Permanent",
        fileType: String = "PDF",
        fileSize: String = "1.2 MB",
        issuingAuthority: String = "Official Authority"
    ) = viewModelScope.launch {
        val doc = DocumentEntity(
            title = title,
            category = category,
            verificationState = state,
            issueDate = issueDate,
            expiryDate = expiryDate,
            fileType = fileType,
            fileSize = fileSize,
            issuingAuthority = issuingAuthority
        )
        repository.insertDocument(doc)
        try {
            firestore.collection("athletes").document(getUserId()).collection("documents").add(doc).await()
        } catch (e: Exception) {}
    }

    fun updateDocument(doc: DocumentEntity) = viewModelScope.launch {
        repository.insertDocument(doc)
        try {
            firestore.collection("athletes").document(getUserId()).collection("documents").document(doc.id.toString()).set(doc).await()
        } catch (e: Exception) {}
    }

    fun deleteDocument(doc: DocumentEntity) = viewModelScope.launch {
        repository.deleteDocument(doc)
        try {
            firestore.collection("athletes").document(getUserId()).collection("documents").document(doc.id.toString()).delete().await()
        } catch (e: Exception) {}
    }

    fun syncDocumentsFromRemote() {
        viewModelScope.launch {
            val uid = getUserId()
            try {
                val docSnap = firestore.collection("athletes").document(uid).collection("documents").get().await()
                docSnap.documents.forEach { doc ->
                    val title = doc.getString("title") ?: return@forEach
                    val category = doc.getString("category") ?: "General"
                    val state = doc.getString("verificationState") ?: "Verified"
                    val issue = doc.getString("issueDate") ?: "2024"
                    val expiry = doc.getString("expiryDate") ?: "Permanent"
                    val type = doc.getString("fileType") ?: "PDF"
                    val size = doc.getString("fileSize") ?: "1.2 MB"
                    val auth = doc.getString("issuingAuthority") ?: "Official Authority"
                    repository.insertDocument(DocumentEntity(
                        title = title,
                        category = category,
                        verificationState = state,
                        issueDate = issue,
                        expiryDate = expiry,
                        fileType = type,
                        fileSize = size,
                        issuingAuthority = auth
                    ))
                }
            } catch (e: Exception) {}
        }
    }

    fun sendMessage(sender: String, text: String, isIncoming: Boolean) = viewModelScope.launch {
        val msg = MessageEntity(senderName = sender, messageText = text, timestamp = "Just now", isIncoming = isIncoming)
        repository.insertMessage(msg)
        try {
            firestore.collection("athletes").document(getUserId()).collection("messages").add(msg).await()
        } catch (e: Exception) {}
    }

    fun requestVerification(credentialType: String, docNumber: String, notes: String) = viewModelScope.launch {
        val doc = DocumentEntity(
            title = "Verification Request: $credentialType (#$docNumber)",
            category = "Credential Verification",
            verificationState = "Pending Review",
            issueDate = "2026-09-29",
            expiryDate = "Permanent",
            fileType = "Digital",
            fileSize = "0.8 MB",
            issuingAuthority = "Talent Graph National Verification Bureau"
        )
        repository.insertDocument(doc)
        try {
            firestore.collection("athletes").document(getUserId()).collection("documents").add(doc).await()
        } catch (e: Exception) {}
    }

    private fun seedDefaultOrganizationsAndConnections() {
        viewModelScope.launch {
            try {
                val existingOrgs = repository.allOrganizations.first()
                if (existingOrgs.isEmpty()) {
                    val mombasa = OrganizationEntity(
                        orgCode = "TG-CLUB-00421",
                        joinCode = "MUFC-7K92X",
                        name = "Mombasa United FC",
                        type = "Club",
                        sport = "Football (Soccer)",
                        location = "Mombasa, Kenya",
                        isVerified = true,
                        description = "Official verified member organization of the Talent Graph sports ecosystem."
                    )
                    val coastAcademy = OrganizationEntity(
                        orgCode = "TG-CLUB-00108",
                        joinCode = "COAST-33B",
                        name = "Coast Strikers Academy",
                        type = "Academy",
                        sport = "Football (Soccer)",
                        location = "Kilifi, Kenya",
                        isVerified = true,
                        description = "Premier youth development academy and talent development center."
                    )
                    repository.insertOrganization(mombasa)
                    repository.insertOrganization(coastAcademy)

                    repository.insertTeam(TeamEntity(orgId = 1, orgName = "Mombasa United FC", name = "First Team", sport = "Football (Soccer)", ageGroup = "Senior / U23", headCoachName = "David Mwangi"))
                    repository.insertTeam(TeamEntity(orgId = 1, orgName = "Mombasa United FC", name = "U20 Development Squad", sport = "Football (Soccer)", ageGroup = "Under 20", headCoachName = "Peter Ochieng"))
                    repository.insertTeam(TeamEntity(orgId = 2, orgName = "Coast Strikers Academy", name = "Coast U18 Elite", sport = "Football (Soccer)", ageGroup = "Under 18", headCoachName = "Hassan Juma"))

                    // Active current membership
                    repository.insertMembership(
                        OrganizationMembershipEntity(
                            userId = getUserId(),
                            userName = _uiState.value.userName,
                            userRole = "ATHLETE",
                            orgId = 1,
                            orgName = "Mombasa United FC",
                            orgCode = "TG-CLUB-00421",
                            teamId = 1,
                            teamName = "First Team",
                            role = "ATHLETE",
                            status = "ACTIVE",
                            joinedAt = "2026-09-22",
                            evidenceNotes = "Official federation player registration document"
                        )
                    )

                    // Historical ended membership (shows athlete owns identity across club transfers)
                    repository.insertMembership(
                        OrganizationMembershipEntity(
                            userId = getUserId(),
                            userName = _uiState.value.userName,
                            userRole = "ATHLETE",
                            orgId = 2,
                            orgName = "Coast Strikers Academy",
                            orgCode = "TG-CLUB-00108",
                            teamId = 3,
                            teamName = "Coast U18 Elite",
                            role = "ATHLETE",
                            status = "ENDED",
                            joinedAt = "2025-01-10",
                            leftAt = "2026-08-30",
                            evidenceNotes = "Youth academy graduation certificate"
                        )
                    )

                    // Pending membership requests in Club Admin queue
                    repository.insertMembership(
                        OrganizationMembershipEntity(
                            userId = "user_kamau_01",
                            userName = "John Kamau",
                            userRole = "ATHLETE",
                            orgId = 1,
                            orgName = "Mombasa United FC",
                            orgCode = "TG-CLUB-00421",
                            teamId = 1,
                            teamName = "First Team",
                            role = "ATHLETE",
                            status = "PENDING",
                            joinedAt = "",
                            evidenceNotes = "Player registration & medical clearance certificate (PDF)"
                        )
                    )
                    repository.insertMembership(
                        OrganizationMembershipEntity(
                            userId = "user_kevin_02",
                            userName = "Kevin Omondi",
                            userRole = "COACH",
                            orgId = 1,
                            orgName = "Mombasa United FC",
                            orgCode = "TG-CLUB-00421",
                            teamId = 2,
                            teamName = "U20 Development Squad",
                            role = "COACH",
                            status = "PENDING",
                            joinedAt = "",
                            evidenceNotes = "CAF B Coaching License & First Aid certification"
                        )
                    )

                    // Connections
                    repository.insertConnection(
                        ConnectionEntity(
                            userId = getUserId(),
                            connectedName = "David Mwangi",
                            connectedRole = "Coach",
                            orgName = "Mombasa United FC",
                            teamName = "First Team",
                            status = "CONNECTED",
                            permissionLevel = "PERFORMANCE_DATA",
                            connectedSince = "2026"
                        )
                    )
                    repository.insertConnection(
                        ConnectionEntity(
                            userId = getUserId(),
                            connectedName = "Brian Otieno",
                            connectedRole = "Analyst",
                            orgName = "Mombasa United FC",
                            teamName = "First Team",
                            status = "CONNECTED",
                            permissionLevel = "PERFORMANCE_DATA",
                            connectedSince = "2026"
                        )
                    )
                    repository.insertConnection(
                        ConnectionEntity(
                            userId = getUserId(),
                            connectedName = "John Kamau",
                            connectedRole = "Club Admin",
                            orgName = "Mombasa United FC",
                            teamName = "First Team",
                            status = "CONNECTED",
                            permissionLevel = "FULL_VAULT",
                            connectedSince = "2026"
                        )
                    )
                    repository.insertConnection(
                        ConnectionEntity(
                            userId = getUserId(),
                            connectedName = "Sarah Jenkins",
                            connectedRole = "Authorized Scout",
                            orgName = "Global Talent Scouting Network",
                            teamName = "Pro Scouting Desk",
                            status = "CONNECTED",
                            permissionLevel = "PUBLIC_PROFILE",
                            connectedSince = "2026"
                        )
                    )
                }
            } catch (e: Exception) {}
        }
    }

    fun requestJoinOrganization(
        codeOrJoinCode: String,
        teamName: String,
        evidenceNotes: String,
        onSuccess: (OrganizationEntity) -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            val orgs = repository.allOrganizations.first()
            val cleanCode = codeOrJoinCode.trim().uppercase()
            val org = orgs.firstOrNull {
                it.orgCode.equals(cleanCode, ignoreCase = true) ||
                it.joinCode.equals(cleanCode, ignoreCase = true) ||
                it.name.contains(cleanCode, ignoreCase = true)
            }
            if (org == null) {
                onError("No organization found with code: $codeOrJoinCode")
                return@launch
            }

            val membership = OrganizationMembershipEntity(
                userId = getUserId(),
                userName = _uiState.value.userName,
                userPhotoUrl = _uiState.value.userPhotoUrl,
                userRole = _uiState.value.userRole.name,
                orgId = org.id,
                orgName = org.name,
                orgCode = org.orgCode,
                teamId = 1,
                teamName = teamName.ifBlank { "First Team" },
                role = _uiState.value.userRole.name,
                status = "PENDING",
                joinedAt = "",
                evidenceNotes = evidenceNotes.ifBlank { "Player registration / transfer clearance" }
            )
            repository.insertMembership(membership)
            repository.insertNotification(
                NotificationEntity(
                    title = "Membership Request Submitted",
                    body = "Your request to join ${org.name} (${teamName.ifBlank { "First Team" }}) was submitted for review.",
                    timestamp = "Just now",
                    isRead = false
                )
            )
            onSuccess(org)
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Failed to submit membership request")
        }
    }

    fun approveMembershipRequest(membership: OrganizationMembershipEntity) = viewModelScope.launch {
        val updated = membership.copy(
            status = "ACTIVE",
            joinedAt = "2026-09-29"
        )
        repository.insertMembership(updated)
        repository.insertNotification(
            NotificationEntity(
                title = "Membership Approved",
                body = "Welcome to ${membership.orgName}! Your membership for ${membership.teamName} is now ACTIVE.",
                timestamp = "Just now",
                isRead = false
            )
        )
        // Add automatic connection with club admin / coach
        repository.insertConnection(
            ConnectionEntity(
                userId = membership.userId,
                connectedName = _uiState.value.userName,
                connectedRole = "Club Admin",
                orgName = membership.orgName,
                teamName = membership.teamName,
                status = "CONNECTED",
                permissionLevel = "PERFORMANCE_DATA",
                connectedSince = "2026"
            )
        )
    }

    fun rejectMembershipRequest(membership: OrganizationMembershipEntity) = viewModelScope.launch {
        val updated = membership.copy(
            status = "REJECTED"
        )
        repository.insertMembership(updated)
    }

    fun endMembership(membership: OrganizationMembershipEntity) = viewModelScope.launch {
        val updated = membership.copy(
            status = "ENDED",
            leftAt = "2026-09-29"
        )
        repository.insertMembership(updated)
        repository.insertNotification(
            NotificationEntity(
                title = "Membership Status Updated",
                body = "Affiliation with ${membership.orgName} marked as ENDED. Historical verified records remain in your profile.",
                timestamp = "Just now",
                isRead = false
            )
        )
    }

    fun createOrganization(
        name: String,
        sport: String,
        location: String,
        orgCode: String = "TG-CLUB-${(1000..9999).random()}",
        joinCode: String = "${name.take(4).uppercase()}-${(100..999).random()}X",
        onSuccess: (OrganizationEntity) -> Unit = {}
    ) = viewModelScope.launch {
        val org = OrganizationEntity(
            name = name,
            sport = sport,
            location = location,
            orgCode = orgCode,
            joinCode = joinCode,
            isVerified = true,
            adminUserId = getUserId()
        )
        repository.insertOrganization(org)
        onSuccess(org)
    }

    fun createTeam(
        orgId: Long,
        orgName: String,
        teamName: String,
        sport: String,
        ageGroup: String,
        headCoachName: String
    ) = viewModelScope.launch {
        val team = TeamEntity(
            orgId = orgId,
            orgName = orgName,
            name = teamName,
            sport = sport,
            ageGroup = ageGroup,
            headCoachName = headCoachName
        )
        repository.insertTeam(team)
    }

    fun updateConnectionPermission(connection: ConnectionEntity, newPermission: String) = viewModelScope.launch {
        val updated = connection.copy(permissionLevel = newPermission)
        repository.insertConnection(updated)
    }

    fun updateConnectionStatus(connection: ConnectionEntity, newStatus: String) = viewModelScope.launch {
        val updated = connection.copy(status = newStatus)
        repository.insertConnection(updated)
    }

    fun addConnection(
        name: String,
        role: String,
        org: String,
        team: String,
        permission: String
    ) = viewModelScope.launch {
        val conn = ConnectionEntity(
            userId = getUserId(),
            connectedName = name,
            connectedRole = role,
            orgName = org,
            teamName = team,
            status = "CONNECTED",
            permissionLevel = permission,
            connectedSince = "2026"
        )
        repository.insertConnection(conn)
    }

    fun removeConnection(connection: ConnectionEntity) = viewModelScope.launch {
        repository.deleteConnection(connection)
    }

    fun clearAllData() = viewModelScope.launch {
        try {
            database.clearAllTables()
            val prefs = getApplication<Application>().getSharedPreferences("talent_graph_profile", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            checkAuthStatus()
        } catch (e: Exception) {}
    }
}
