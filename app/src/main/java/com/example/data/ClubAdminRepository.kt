package com.example.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ClubAdminRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun getCurrentAdminMembership(): Flow<ClubMember?> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("club_members")
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("status", "active")
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val doc = snapshot?.documents?.firstOrNull()
                val member = doc?.toClubMemberSafe()
                trySend(member)
            }

        awaitClose { listener.remove() }
    }

    fun getClubDetails(clubId: String): Flow<ClubModel?> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("clubs").document(clubId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val club = snapshot?.toObject(ClubModel::class.java)
                trySend(club)
            }

        awaitClose { listener.remove() }
    }

    fun getClubAthletes(clubId: String): Flow<List<AthleteEntity>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val membersListener = firestore.collection("club_members")
            .whereEqualTo("clubId", clubId)
            .whereEqualTo("status", "active")
            .addSnapshotListener { _, _ ->
                firestore.collection("athletes")
                    .whereEqualTo("clubId", clubId)
                    .get()
                    .addOnSuccessListener { clubAthletesSnap ->
                        val squadFromAthletes = clubAthletesSnap.documents.mapNotNull { doc ->
                            doc.toAthleteSafe()
                        }

                        firestore.collection("clubs").document(clubId).collection("squad")
                            .get()
                            .addOnSuccessListener { squadSnap ->
                                val manualSquad = squadSnap.documents.mapNotNull { doc ->
                                    doc.toAthleteSafe()
                                }

                                val combined = (squadFromAthletes + manualSquad).distinctBy { it.firestoreId.ifBlank { it.name } }
                                trySend(combined)
                            }.addOnFailureListener {
                                trySend(squadFromAthletes)
                            }
                    }.addOnFailureListener {
                        trySend(emptyList())
                    }
            }

        awaitClose { membersListener.remove() }
    }

    fun getClubStaff(clubId: String): Flow<List<ClubMember>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val staffRoles = listOf("admin", "coach", "staff", "analyst", "scout", "director", "manager", "physio", "trainer")

        val listener = firestore.collection("club_members")
            .whereEqualTo("clubId", clubId)
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val allMembers = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubMemberSafe()
                } ?: emptyList()

                val staff = allMembers.filter { member ->
                    staffRoles.contains(member.role.lowercase()) || member.role.isBlank()
                }
                trySend(staff)
            }

        awaitClose { listener.remove() }
    }

    fun getClubTeams(clubId: String): Flow<List<ClubTeamModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("teams")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubTeamSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubCompetitions(clubId: String): Flow<List<ClubCompetitionModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("competitions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubCompetitionSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubMatches(clubId: String): Flow<List<ClubMatchModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("matches")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubMatchSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubTrainingSessions(clubId: String): Flow<List<ClubTrainingModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("training_sessions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubTrainingModel::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubDocuments(clubId: String): Flow<List<ClubDocumentModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("documents")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubDocumentModel::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubRegistrations(clubId: String): Flow<List<ClubRegistrationModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("registrations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubRegistrationModel::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubOpportunities(clubId: String): Flow<List<ClubOpportunityModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("opportunities")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubOpportunityModel::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubPlayerPerformances(clubId: String): Flow<List<ClubPlayerPerformanceModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("player_performances")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubPlayerPerformanceSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubFitnessAssessments(clubId: String): Flow<List<ClubFitnessAssessmentModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("fitness_assessments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubFitnessAssessmentSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubIntelligenceSignals(clubId: String): Flow<List<ClubIntelligenceSignal>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("signals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubIntelligenceSignal::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubAuditLogs(clubId: String): Flow<List<ClubAuditEntry>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("audit_logs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubAuditEntry::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubFinancials(clubId: String): Flow<List<ClubFinancialRecord>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("financials")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ClubFinancialRecord::class.java)?.copy(id = doc.id, clubId = clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubDevelopmentPlans(clubId: String): Flow<List<ClubDevelopmentPlanModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("development_plans")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubDevelopmentPlanSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubRecruitmentPipeline(clubId: String): Flow<List<ClubRecruitmentPipelineModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("recruitment_pipeline")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubRecruitmentPipelineSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubScoutAssignments(clubId: String): Flow<List<ClubScoutAssignmentModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("scout_assignments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubScoutAssignmentSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubAnalystProjects(clubId: String): Flow<List<ClubAnalystProjectModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("analyst_projects")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubAnalystProjectSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubAvailabilityRecords(clubId: String): Flow<List<ClubAvailabilityStatusModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("availability_records")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubAvailabilityStatusSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getClubCalendarEvents(clubId: String): Flow<List<ClubCalendarEventModel>> = callbackFlow {
        if (clubId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("clubs").document(clubId).collection("calendar_events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toClubCalendarEventSafe(clubId)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // --- MUTATION ACTIONS WITH FIRESTORE & AUDIT LOGGING ---

    suspend fun addAthleteToSquad(clubId: String, name: String, position: String, sport: String, rating: Int) {
        val athleteData = mapOf(
            "name" to name,
            "position" to position,
            "sport" to sport,
            "rating" to rating,
            "clubId" to clubId,
            "verifiedStats" to "Registered Squad Player",
            "joinedDate" to "2026",
            "isVerified" to true,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("athletes").add(athleteData).await()
        firestore.collection("clubs").document(clubId).collection("squad").add(athleteData).await()
        logAuditAction(clubId, "Added Athlete", name, "Registered $name ($position) to active squad.")
    }

    suspend fun invitePlayer(
        clubId: String,
        clubName: String,
        playerName: String,
        contactMethod: String,
        contactValue: String,
        tier: String,
        position: String,
        inviteCode: String,
        inviteLink: String
    ) {
        val inviteData = mapOf(
            "clubId" to clubId,
            "clubName" to clubName,
            "playerName" to playerName,
            "contactMethod" to contactMethod,
            "contactValue" to contactValue,
            "tier" to tier,
            "position" to position,
            "inviteCode" to inviteCode,
            "inviteLink" to inviteLink,
            "status" to "Invited",
            "expiresAt" to (System.currentTimeMillis() + (72 * 3600 * 1000L)),
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("invitations").add(inviteData).await()

        val athleteData = mapOf(
            "name" to playerName,
            "position" to position,
            "sport" to "Football (Soccer)",
            "rating" to 75,
            "clubId" to clubId,
            "status" to "Invited",
            "verifiedStats" to "Invitation Code: $inviteCode",
            "joinedDate" to "2026",
            "isVerified" to false,
            "inviteCode" to inviteCode,
            "inviteMethod" to contactMethod,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("athletes").add(athleteData).await()
        firestore.collection("clubs").document(clubId).collection("squad").add(athleteData).await()
        logAuditAction(clubId, "Invited Athlete", playerName, "Dispatched invitation to $playerName via $contactMethod with code $inviteCode.")
    }

    suspend fun createTeam(
        clubId: String,
        name: String,
        ageCategory: String,
        coach: String,
        competition: String,
        gender: String = "Men",
        assistantCoach: String = "",
        analyst: String = "",
        sport: String = "Football (Soccer)",
        formation: String = "4-3-3",
        notes: String = ""
    ) {
        val teamData = mapOf(
            "name" to name,
            "ageCategory" to ageCategory,
            "gender" to gender,
            "headCoachName" to coach,
            "assistantCoachName" to assistantCoach,
            "analystName" to analyst,
            "competition" to competition,
            "sport" to sport,
            "formation" to formation,
            "notes" to notes,
            "clubId" to clubId,
            "squadCount" to 0,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("teams").add(teamData).await()
        logAuditAction(clubId, "Created Team", name, "Created new squad category $name ($ageCategory - $gender).")
    }

    suspend fun deleteTeam(clubId: String, teamId: String, teamName: String) {
        firestore.collection("clubs").document(clubId).collection("teams").document(teamId).delete().await()
        logAuditAction(clubId, "Deleted Team", teamName, "Removed squad category $teamName.")
    }

    suspend fun createCompetition(
        clubId: String,
        name: String,
        season: String,
        tier: String,
        division: String,
        federation: String,
        teamName: String,
        status: String = "Active",
        tablePosition: Int = 1,
        points: Int = 0
    ) {
        val compData = mapOf(
            "name" to name,
            "season" to season,
            "tier" to tier,
            "division" to division,
            "federation" to federation,
            "teamName" to teamName,
            "status" to status,
            "tablePosition" to tablePosition,
            "points" to points,
            "matchesPlayed" to 0,
            "wins" to 0,
            "draws" to 0,
            "losses" to 0,
            "qualificationStatus" to "Registered / Eligible",
            "clubId" to clubId,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("competitions").add(compData).await()
        logAuditAction(clubId, "Registered Competition", name, "Registered $teamName into $name ($season).")
    }

    suspend fun deleteCompetition(clubId: String, competitionId: String, compName: String) {
        firestore.collection("clubs").document(clubId).collection("competitions").document(competitionId).delete().await()
        logAuditAction(clubId, "Removed Competition", compName, "Withdrew club registration from $compName.")
    }

    suspend fun updateMatchScore(
        clubId: String,
        matchId: String,
        homeScore: Int,
        awayScore: Int,
        status: String,
        minute: Int = 90,
        notes: String = "",
        manOfTheMatch: String = ""
    ) {
        val updates = mutableMapOf<String, Any>(
            "homeScore" to homeScore,
            "awayScore" to awayScore,
            "status" to status,
            "minute" to minute
        )
        if (notes.isNotBlank()) updates["notes"] = notes
        if (manOfTheMatch.isNotBlank()) updates["manOfTheMatch"] = manOfTheMatch

        firestore.collection("clubs").document(clubId).collection("matches").document(matchId).update(updates).await()
        logAuditAction(clubId, "Updated Match Score", matchId, "Score set to $homeScore - $awayScore ($status, min $minute).")
    }

    suspend fun createMatch(
        clubId: String,
        teamName: String,
        opponent: String,
        date: String,
        time: String = "15:00",
        venue: String = "Home",
        stadium: String = "Main Stadium",
        city: String = "Nairobi",
        competition: String = "Premier League",
        round: String = "Matchday 1",
        matchType: String = "League",
        referee: String = "Official Match Official",
        formation: String = "4-3-3",
        captain: String = "",
        notes: String = ""
    ) {
        val matchData = mapOf(
            "clubId" to clubId,
            "teamName" to teamName,
            "opponent" to opponent,
            "date" to date,
            "time" to time,
            "venue" to venue,
            "stadium" to stadium,
            "city" to city,
            "competition" to competition,
            "round" to round,
            "matchType" to matchType,
            "referee" to referee,
            "formation" to formation,
            "captain" to captain,
            "notes" to notes,
            "homeScore" to 0,
            "awayScore" to 0,
            "status" to "Upcoming",
            "minute" to 0,
            "possessionHome" to 50,
            "shotsHome" to 0,
            "shotsAway" to 0,
            "shotsOnTargetHome" to 0,
            "shotsOnTargetAway" to 0,
            "cornersHome" to 0,
            "cornersAway" to 0,
            "foulsHome" to 0,
            "foulsAway" to 0,
            "yellowCardsHome" to 0,
            "yellowCardsAway" to 0,
            "redCardsHome" to 0,
            "redCardsAway" to 0,
            "manOfTheMatch" to "",
            "matchEvents" to emptyList<Map<String, Any>>(),
            "startingLineup" to emptyList<String>(),
            "substitutes" to emptyList<String>(),
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("matches").add(matchData).await()
        logAuditAction(clubId, "Scheduled Fixture", "$teamName vs $opponent", "Fixture scheduled on $date at $stadium ($competition).")
    }

    suspend fun addMatchEvent(
        clubId: String,
        matchId: String,
        minute: Int,
        type: String,
        team: String,
        player: String,
        assistPlayer: String = "",
        subInPlayer: String = "",
        notes: String = ""
    ) {
        val eventMap = mapOf(
            "id" to java.util.UUID.randomUUID().toString(),
            "minute" to minute,
            "type" to type,
            "team" to team,
            "player" to player,
            "assistPlayer" to assistPlayer,
            "subInPlayer" to subInPlayer,
            "notes" to notes
        )
        val matchRef = firestore.collection("clubs").document(clubId).collection("matches").document(matchId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(matchRef)
            val currentEvents = snapshot.get("matchEvents") as? List<Map<String, Any>> ?: emptyList()
            val updatedEvents = currentEvents + eventMap
            transaction.update(matchRef, "matchEvents", updatedEvents)

            // Auto-increment score or cards if applicable
            if (type == "Goal" || type == "Penalty") {
                if (team == "home") {
                    val currentHome = snapshot.getLong("homeScore")?.toInt() ?: 0
                    transaction.update(matchRef, "homeScore", currentHome + 1)
                } else {
                    val currentAway = snapshot.getLong("awayScore")?.toInt() ?: 0
                    transaction.update(matchRef, "awayScore", currentAway + 1)
                }
            } else if (type == "Yellow Card") {
                if (team == "home") {
                    val currentCards = snapshot.getLong("yellowCardsHome")?.toInt() ?: 0
                    transaction.update(matchRef, "yellowCardsHome", currentCards + 1)
                } else {
                    val currentCards = snapshot.getLong("yellowCardsAway")?.toInt() ?: 0
                    transaction.update(matchRef, "yellowCardsAway", currentCards + 1)
                }
            } else if (type == "Red Card") {
                if (team == "home") {
                    val currentCards = snapshot.getLong("redCardsHome")?.toInt() ?: 0
                    transaction.update(matchRef, "redCardsHome", currentCards + 1)
                } else {
                    val currentCards = snapshot.getLong("redCardsAway")?.toInt() ?: 0
                    transaction.update(matchRef, "redCardsAway", currentCards + 1)
                }
            }
        }.await()
        logAuditAction(clubId, "Logged Match Event", "$type ($minute')", "$player for $team team.")
    }

    suspend fun updateMatchLineup(
        clubId: String,
        matchId: String,
        startingXI: List<String>,
        substitutes: List<String>,
        formation: String,
        captain: String
    ) {
        val updates = mapOf(
            "startingLineup" to startingXI,
            "substitutes" to substitutes,
            "formation" to formation,
            "captain" to captain
        )
        firestore.collection("clubs").document(clubId).collection("matches").document(matchId).update(updates).await()
        logAuditAction(clubId, "Updated Match Lineup", matchId, "Formation $formation, Captain: $captain, XI: ${startingXI.size} players.")
    }

    suspend fun updateMatchStats(
        clubId: String,
        matchId: String,
        possessionHome: Int,
        shotsHome: Int,
        shotsAway: Int,
        shotsOnTargetHome: Int,
        shotsOnTargetAway: Int,
        cornersHome: Int,
        cornersAway: Int,
        foulsHome: Int,
        foulsAway: Int
    ) {
        val updates = mapOf(
            "possessionHome" to possessionHome,
            "shotsHome" to shotsHome,
            "shotsAway" to shotsAway,
            "shotsOnTargetHome" to shotsOnTargetHome,
            "shotsOnTargetAway" to shotsOnTargetAway,
            "cornersHome" to cornersHome,
            "cornersAway" to cornersAway,
            "foulsHome" to foulsHome,
            "foulsAway" to foulsAway
        )
        firestore.collection("clubs").document(clubId).collection("matches").document(matchId).update(updates).await()
        logAuditAction(clubId, "Updated Match Statistics", matchId, "Possession $possessionHome%, Shots $shotsHome-$shotsAway.")
    }

    suspend fun inviteStaff(clubId: String, clubName: String, displayName: String, role: String) {
        val memberData = mapOf(
            "clubId" to clubId,
            "clubName" to clubName,
            "displayName" to displayName,
            "role" to role.lowercase(),
            "status" to "active",
            "joinedAt" to "2026-09-30",
            "invitedBy" to (auth.currentUser?.uid ?: "Admin")
        )
        firestore.collection("club_members").add(memberData).await()
        logAuditAction(clubId, "Invited Staff", displayName, "Assigned role $role to $displayName.")
    }

    suspend fun registerAthlete(clubId: String, athleteName: String, regNumber: String, competition: String) {
        val regData = mapOf(
            "clubId" to clubId,
            "athleteName" to athleteName,
            "registrationNumber" to regNumber,
            "competition" to competition,
            "status" to "Verified",
            "expiryDate" to "2026-12-31",
            "verifiedBy" to "National Federation"
        )
        firestore.collection("clubs").document(clubId).collection("registrations").add(regData).await()
        logAuditAction(clubId, "Registered Athlete", athleteName, "Competition license $regNumber issued.")
    }

    suspend fun deleteMatch(clubId: String, matchId: String, matchTitle: String) {
        firestore.collection("clubs").document(clubId).collection("matches").document(matchId).delete().await()
        logAuditAction(clubId, "Deleted Fixture", matchTitle, "Cancelled and deleted match fixture.")
    }

    suspend fun createTrainingSession(clubId: String, teamName: String, type: String, date: String, duration: Int, load: Int, notes: String) {
        val sessionData = mapOf(
            "clubId" to clubId,
            "teamName" to teamName,
            "sessionType" to type,
            "date" to date,
            "durationMins" to duration,
            "averageLoad" to load,
            "coachNotes" to notes,
            "attendancePercent" to 95,
            "intensity" to "High"
        )
        firestore.collection("clubs").document(clubId).collection("training_sessions").add(sessionData).await()
        logAuditAction(clubId, "Created Training", "$teamName - $type", "Training session created for $date.")
    }

    suspend fun createOpportunity(clubId: String, title: String, type: String, targetPos: String, deadline: String, desc: String) {
        val oppData = mapOf(
            "clubId" to clubId,
            "title" to title,
            "type" to type,
            "targetPosition" to targetPos,
            "deadline" to deadline,
            "description" to desc,
            "status" to "Open",
            "applicantsCount" to 0
        )
        firestore.collection("clubs").document(clubId).collection("opportunities").add(oppData).await()
        logAuditAction(clubId, "Created Opportunity", title, "Open $type recruitment published.")
    }

    suspend fun uploadDocument(clubId: String, title: String, category: String, target: String, verification: String) {
        val docData = mapOf(
            "clubId" to clubId,
            "title" to title,
            "category" to category,
            "targetEntity" to target,
            "verificationLevel" to verification,
            "expiryDate" to "Permanent",
            "uploadedBy" to (auth.currentUser?.displayName ?: "Admin"),
            "uploadedAt" to "2026-09-30"
        )
        firestore.collection("clubs").document(clubId).collection("documents").add(docData).await()
        logAuditAction(clubId, "Uploaded Document", title, "Added $category document with $verification.")
    }

    suspend fun logPlayerPerformance(
        clubId: String,
        athleteId: String,
        athleteName: String,
        position: String,
        teamName: String,
        matchesPlayed: Int,
        minutesPlayed: Int,
        goals: Int,
        assists: Int,
        averageRating: Double,
        passAccuracy: Int,
        tacklesWon: Int,
        cleanSheets: Int,
        readinessStatus: String,
        coachEvaluation: String,
        evaluatedBy: String = "Head Coach",
        recentRatings: List<Double> = emptyList()
    ) {
        val perfData = mapOf(
            "clubId" to clubId,
            "athleteId" to athleteId,
            "athleteName" to athleteName,
            "position" to position,
            "teamName" to teamName,
            "matchesPlayed" to matchesPlayed,
            "minutesPlayed" to minutesPlayed,
            "goals" to goals,
            "assists" to assists,
            "averageRating" to averageRating,
            "passAccuracy" to passAccuracy,
            "tacklesWon" to tacklesWon,
            "cleanSheets" to cleanSheets,
            "readinessStatus" to readinessStatus,
            "coachEvaluation" to coachEvaluation,
            "evaluatedBy" to evaluatedBy,
            "recentRatings" to recentRatings,
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        firestore.collection("clubs").document(clubId).collection("player_performances").add(perfData).await()
        logAuditAction(clubId, "Logged Player Performance", athleteName, "Rating: $averageRating, Goals: $goals, Status: $readinessStatus")
    }

    suspend fun logFitnessAssessment(
        clubId: String,
        athleteId: String,
        athleteName: String,
        teamName: String,
        testDate: String,
        sprintSpeed: Double,
        vo2Max: Double,
        verticalJump: Double,
        yoyoLevel: String,
        bodyFat: Double,
        acwrRatio: Double,
        grade: String,
        notes: String
    ) {
        val fitnessData = mapOf(
            "clubId" to clubId,
            "athleteId" to athleteId,
            "athleteName" to athleteName,
            "teamName" to teamName,
            "testDate" to testDate,
            "sprintSpeedKmH" to sprintSpeed,
            "vo2Max" to vo2Max,
            "verticalJumpCm" to verticalJump,
            "yoyoTestLevel" to yoyoLevel,
            "bodyFatPercent" to bodyFat,
            "acwrRatio" to acwrRatio,
            "overallFitnessGrade" to grade,
            "trainerNotes" to notes
        )
        firestore.collection("clubs").document(clubId).collection("fitness_assessments").add(fitnessData).await()
        logAuditAction(clubId, "Logged Fitness Assessment", athleteName, "Grade: $grade, Sprint: $sprintSpeed km/h, VO2: $vo2Max")
    }

    suspend fun deletePlayerPerformance(clubId: String, performanceId: String, athleteName: String) {
        firestore.collection("clubs").document(clubId).collection("player_performances").document(performanceId).delete().await()
        logAuditAction(clubId, "Deleted Performance Record", athleteName, "Removed performance evaluation.")
    }

    suspend fun deleteFitnessAssessment(clubId: String, assessmentId: String, athleteName: String) {
        firestore.collection("clubs").document(clubId).collection("fitness_assessments").document(assessmentId).delete().await()
        logAuditAction(clubId, "Deleted Fitness Assessment", athleteName, "Removed physical fitness test log.")
    }

    suspend fun recordFinancialTransaction(
        clubId: String,
        title: String,
        type: String,
        category: String,
        amount: String,
        date: String = "2026-09-30",
        status: String = "Paid",
        counterparty: String = "General"
    ) {
        val finData = mapOf(
            "clubId" to clubId,
            "title" to title,
            "type" to type,
            "category" to category,
            "amount" to amount,
            "date" to date,
            "status" to status,
            "counterparty" to counterparty,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("financials").add(finData).await()
        logAuditAction(clubId, "Recorded Financial Transaction", "$title ($amount)", "$type under $category to $counterparty.")
    }

    suspend fun createCalendarEvent(
        clubId: String,
        title: String,
        eventType: String,
        date: String,
        time: String = "10:00",
        targetTeam: String = "Senior Team",
        venue: String = "Main Grounds",
        notes: String = ""
    ) {
        val eventData = mapOf(
            "clubId" to clubId,
            "title" to title,
            "eventType" to eventType,
            "date" to date,
            "time" to time,
            "targetTeam" to targetTeam,
            "venue" to venue,
            "notes" to notes,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("calendar_events").add(eventData).await()
        logAuditAction(clubId, "Created Calendar Event", title, "$eventType scheduled on $date at $venue.")
    }

    suspend fun createDevelopmentPlan(
        clubId: String,
        athleteId: String,
        athleteName: String,
        currentTeam: String,
        pathwayTarget: String,
        progressPercent: Int = 50,
        promotionReadiness: String = "Moderate",
        primaryGoal: String,
        targetPos: String,
        mentor: String,
        coachAssessment: String
    ) {
        val planData = mapOf(
            "clubId" to clubId,
            "athleteId" to athleteId,
            "athleteName" to athleteName,
            "currentTeam" to currentTeam,
            "pathwayTarget" to pathwayTarget,
            "progressPercent" to progressPercent,
            "promotionReadiness" to promotionReadiness,
            "primarySkillGoal" to primaryGoal,
            "targetPosition" to targetPos,
            "coachMentor" to mentor,
            "milestonesCount" to 5,
            "completedMilestones" to 2,
            "coachAssessment" to coachAssessment,
            "lastReviewDate" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        firestore.collection("clubs").document(clubId).collection("development_plans").add(planData).await()
        logAuditAction(clubId, "Created IDP Plan", athleteName, "Target: $pathwayTarget ($targetPos), Mentor: $mentor.")
    }

    suspend fun addRecruitmentCandidate(
        clubId: String,
        candidateName: String,
        position: String,
        currentClub: String,
        age: Int,
        foot: String = "Right",
        stage: String = "Discovered",
        scoutName: String = "Chief Scout",
        scoutRating: Double = 8.0,
        notes: String = "",
        trialDate: String = "2026-10-10",
        targetTeam: String = "Senior Team"
    ) {
        val candidateData = mapOf(
            "clubId" to clubId,
            "candidateName" to candidateName,
            "position" to position,
            "currentClub" to currentClub,
            "age" to age,
            "preferredFoot" to foot,
            "stage" to stage,
            "scoutName" to scoutName,
            "scoutRating" to scoutRating,
            "evaluationNotes" to notes,
            "trialDate" to trialDate,
            "decisionStatus" to "In Review",
            "targetTeam" to targetTeam,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("recruitment_pipeline").add(candidateData).await()
        logAuditAction(clubId, "Added Recruitment Candidate", candidateName, "Position: $position, Scout Rating: $scoutRating, Stage: $stage.")
    }

    suspend fun updateRecruitmentStage(clubId: String, pipelineId: String, candidateName: String, newStage: String, decisionStatus: String) {
        val updates = mapOf(
            "stage" to newStage,
            "decisionStatus" to decisionStatus
        )
        firestore.collection("clubs").document(clubId).collection("recruitment_pipeline").document(pipelineId).update(updates).await()
        logAuditAction(clubId, "Updated Recruitment Stage", candidateName, "Moved candidate to $newStage ($decisionStatus).")
    }

    suspend fun createScoutAssignment(
        clubId: String,
        scoutName: String,
        projectTitle: String,
        targetPosition: String,
        ageRange: String,
        territory: String,
        specialization: String,
        deadline: String
    ) {
        val scoutData = mapOf(
            "clubId" to clubId,
            "scoutName" to scoutName,
            "projectTitle" to projectTitle,
            "targetPosition" to targetPosition,
            "ageRange" to ageRange,
            "territory" to territory,
            "specialization" to specialization,
            "athletesDiscovered" to 0,
            "shortlisted" to 0,
            "trialsInvited" to 0,
            "status" to "Active",
            "deadline" to deadline,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("scout_assignments").add(scoutData).await()
        logAuditAction(clubId, "Assigned Scout Project", projectTitle, "Assigned $scoutName to $territory ($targetPosition).")
    }

    suspend fun createAnalystProject(
        clubId: String,
        analystName: String,
        projectTitle: String,
        category: String,
        targetTeam: String,
        competition: String,
        keyFindings: String,
        deliveryDate: String
    ) {
        val analystData = mapOf(
            "clubId" to clubId,
            "analystName" to analystName,
            "projectTitle" to projectTitle,
            "category" to category,
            "targetTeam" to targetTeam,
            "competition" to competition,
            "status" to "In Progress",
            "keyFindings" to keyFindings,
            "deliveryDate" to deliveryDate,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("clubs").document(clubId).collection("analyst_projects").add(analystData).await()
        logAuditAction(clubId, "Commissioned Analysis", projectTitle, "Assigned $analystName for $targetTeam ($category).")
    }

    suspend fun updateAthleteAvailability(
        clubId: String,
        athleteName: String,
        teamName: String,
        position: String,
        newStatus: String,
        limitationNotes: String,
        expectedReturnDate: String,
        riskLevel: String,
        protocol: String
    ) {
        val recordData = mapOf(
            "clubId" to clubId,
            "athleteName" to athleteName,
            "teamName" to teamName,
            "position" to position,
            "status" to newStatus,
            "limitationNotes" to limitationNotes,
            "expectedReturnDate" to expectedReturnDate,
            "riskLevel" to riskLevel,
            "protectionProtocol" to protocol,
            "updatedBy" to (auth.currentUser?.displayName ?: "Physio Lead"),
            "updatedAt" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        firestore.collection("clubs").document(clubId).collection("availability_records").add(recordData).await()
        logAuditAction(clubId, "Updated Player Availability", athleteName, "Status: $newStatus (Risk: $riskLevel, Protocol: $protocol).")
    }

    suspend fun updateClubProfile(clubId: String, clubName: String, location: String, contactEmail: String, contactPhone: String) {
        val updates = mapOf(
            "clubName" to clubName,
            "location" to location,
            "contactEmail" to contactEmail,
            "contactPhone" to contactPhone
        )
        firestore.collection("clubs").document(clubId).update(updates).await()
        logAuditAction(clubId, "Updated Club Profile", clubName, "Updated organizational identity and contact info.")
    }

    suspend fun logAuditAction(clubId: String, action: String, target: String, details: String) {
        val auditData = mapOf(
            "clubId" to clubId,
            "actorName" to (auth.currentUser?.displayName ?: "Club Admin"),
            "actorRole" to "Club Administrator",
            "action" to action,
            "targetRecord" to target,
            "timestamp" to "2026-09-30 10:15",
            "changeDetails" to details
        )
        try {
            firestore.collection("clubs").document(clubId).collection("audit_logs").add(auditData).await()
        } catch (e: Exception) {}
    }
}

private fun DocumentSnapshot.toClubMemberSafe(): ClubMember {
    return try {
        ClubMember(
            userId = getString("userId") ?: id,
            clubId = getString("clubId") ?: "",
            clubName = getString("clubName") ?: "",
            role = getString("role") ?: "admin",
            status = getString("status") ?: "active",
            displayName = getString("displayName") ?: "",
            firstName = getString("firstName") ?: "",
            lastName = getString("lastName") ?: "",
            photoUrl = getString("photoUrl") ?: getString("avatarUrl") ?: getString("profileImageUrl") ?: "",
            invitedBy = getString("invitedBy") ?: "",
            joinedAt = get("joinedAt"),
            invitedAt = get("invitedAt"),
            createdAt = get("createdAt")
        )
    } catch (e: Exception) {
        ClubMember(userId = id)
    }
}

private fun DocumentSnapshot.toAthleteSafe(): AthleteEntity? {
    return try {
        val name = getString("name") ?: getString("fullName") ?: getString("displayName") ?: return null
        val position = getString("position") ?: getString("primaryPosition") ?: "Player"
        val sport = getString("sport") ?: "Football (Soccer)"
        val rating = getLong("rating")?.toInt() ?: 75
        val clubName = getString("clubName") ?: getString("clubId") ?: ""
        val verifiedStats = getString("verifiedStats") ?: "Active Squad"
        val joinedDate = getString("joinedDate") ?: "2026"
        val isVerified = getBoolean("isVerified") ?: true
        val photoUrl = getString("photoUrl") ?: getString("avatar") ?: getString("profilePhoto") ?: getString("profileImageUrl")
        AthleteEntity(
            firestoreId = id,
            name = name,
            sport = sport,
            position = position,
            rating = rating,
            verifiedStats = verifiedStats,
            clubName = clubName,
            joinedDate = joinedDate,
            isVerified = isVerified,
            photoUrl = photoUrl
        )
    } catch (e: Exception) {
        null
    }
}

private fun DocumentSnapshot.toClubTeamSafe(clubId: String): ClubTeamModel {
    return try {
        ClubTeamModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            name = getString("name") ?: "Team",
            ageCategory = getString("ageCategory") ?: "Senior",
            gender = getString("gender") ?: "Men",
            headCoachName = getString("headCoachName") ?: "",
            assistantCoachName = getString("assistantCoachName") ?: "",
            analystName = getString("analystName") ?: "",
            squadCount = getLong("squadCount")?.toInt() ?: 0,
            competition = getString("competition") ?: "National League",
            sport = getString("sport") ?: "Football (Soccer)",
            formation = getString("formation") ?: "4-3-3",
            trainingSchedule = getString("trainingSchedule") ?: "Mon, Wed, Fri 08:00",
            notes = getString("notes") ?: ""
        )
    } catch (e: Exception) {
        ClubTeamModel(id = id, clubId = clubId, name = getString("name") ?: "Team")
    }
}

private fun DocumentSnapshot.toClubCompetitionSafe(clubId: String): ClubCompetitionModel {
    return try {
        ClubCompetitionModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            name = getString("name") ?: "National Premier League",
            season = getString("season") ?: "2026/2027",
            tier = getString("tier") ?: "Premier Tier 1",
            division = getString("division") ?: "National Premier Division",
            federation = getString("federation") ?: "National Football Federation",
            status = getString("status") ?: "Active",
            teamName = getString("teamName") ?: "Senior Team",
            matchesPlayed = getLong("matchesPlayed")?.toInt() ?: 0,
            wins = getLong("wins")?.toInt() ?: 0,
            draws = getLong("draws")?.toInt() ?: 0,
            losses = getLong("losses")?.toInt() ?: 0,
            points = getLong("points")?.toInt() ?: 0,
            tablePosition = getLong("tablePosition")?.toInt() ?: 1,
            qualificationStatus = getString("qualificationStatus") ?: "Registered / Eligible"
        )
    } catch (e: Exception) {
        ClubCompetitionModel(id = id, clubId = clubId, name = getString("name") ?: "Competition")
    }
}

private fun DocumentSnapshot.toClubMatchSafe(clubId: String): ClubMatchModel {
    return try {
        val rawEvents = get("matchEvents") as? List<*> ?: emptyList<Any>()
        val parsedEvents = rawEvents.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            ClubMatchEvent(
                id = map["id"]?.toString() ?: java.util.UUID.randomUUID().toString(),
                minute = (map["minute"] as? Number)?.toInt() ?: 0,
                type = map["type"]?.toString() ?: "Goal",
                team = map["team"]?.toString() ?: "home",
                player = map["player"]?.toString() ?: "",
                assistPlayer = map["assistPlayer"]?.toString() ?: "",
                subInPlayer = map["subInPlayer"]?.toString() ?: "",
                notes = map["notes"]?.toString() ?: ""
            )
        }

        val startingXI = (get("startingLineup") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
        val subs = (get("substitutes") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

        ClubMatchModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            teamName = getString("teamName") ?: "Senior Team",
            teamId = getString("teamId") ?: "",
            opponent = getString("opponent") ?: "Opponent FC",
            opponentLogoUrl = getString("opponentLogoUrl") ?: "",
            date = getString("date") ?: "2026-10-05",
            time = getString("time") ?: "15:00",
            venue = getString("venue") ?: "Home",
            stadium = getString("stadium") ?: "Main Stadium",
            city = getString("city") ?: "Nairobi",
            competition = getString("competition") ?: "Premier League",
            round = getString("round") ?: "Matchday 1",
            matchType = getString("matchType") ?: "League",
            homeScore = getLong("homeScore")?.toInt() ?: 0,
            awayScore = getLong("awayScore")?.toInt() ?: 0,
            status = getString("status") ?: "Upcoming",
            minute = getLong("minute")?.toInt() ?: 0,
            referee = getString("referee") ?: "Official Match Official",
            formation = getString("formation") ?: "4-3-3",
            captain = getString("captain") ?: "",
            homeTeamName = getString("homeTeamName") ?: "",
            awayTeamName = getString("awayTeamName") ?: "",
            matchEvents = parsedEvents,
            possessionHome = getLong("possessionHome")?.toInt() ?: 50,
            shotsHome = getLong("shotsHome")?.toInt() ?: 0,
            shotsAway = getLong("shotsAway")?.toInt() ?: 0,
            shotsOnTargetHome = getLong("shotsOnTargetHome")?.toInt() ?: 0,
            shotsOnTargetAway = getLong("shotsOnTargetAway")?.toInt() ?: 0,
            cornersHome = getLong("cornersHome")?.toInt() ?: 0,
            cornersAway = getLong("cornersAway")?.toInt() ?: 0,
            foulsHome = getLong("foulsHome")?.toInt() ?: 0,
            foulsAway = getLong("foulsAway")?.toInt() ?: 0,
            yellowCardsHome = getLong("yellowCardsHome")?.toInt() ?: 0,
            yellowCardsAway = getLong("yellowCardsAway")?.toInt() ?: 0,
            redCardsHome = getLong("redCardsHome")?.toInt() ?: 0,
            redCardsAway = getLong("redCardsAway")?.toInt() ?: 0,
            manOfTheMatch = getString("manOfTheMatch") ?: "",
            notes = getString("notes") ?: "",
            startingLineup = startingXI,
            substitutes = subs
        )
    } catch (e: Exception) {
        ClubMatchModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubRegistrationSafe(clubId: String): ClubRegistrationModel {
    return try {
        ClubRegistrationModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            athleteName = getString("athleteName") ?: "Athlete",
            registrationNumber = getString("registrationNumber") ?: "REG-2026-001",
            competition = getString("competition") ?: "National League",
            status = getString("status") ?: "Verified",
            expiryDate = getString("expiryDate") ?: "2026-12-31",
            verifiedBy = getString("verifiedBy") ?: "National Federation"
        )
    } catch (e: Exception) {
        ClubRegistrationModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubPlayerPerformanceSafe(clubId: String): ClubPlayerPerformanceModel {
    return try {
        val ratingsList = (get("recentRatings") as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()
        ClubPlayerPerformanceModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            athleteId = getString("athleteId") ?: "",
            athleteName = getString("athleteName") ?: "Athlete",
            position = getString("position") ?: "Forward",
            teamName = getString("teamName") ?: "Senior Team",
            matchesPlayed = getLong("matchesPlayed")?.toInt() ?: 0,
            minutesPlayed = getLong("minutesPlayed")?.toInt() ?: 0,
            goals = getLong("goals")?.toInt() ?: 0,
            assists = getLong("assists")?.toInt() ?: 0,
            averageRating = getDouble("averageRating") ?: 7.5,
            passAccuracy = getLong("passAccuracy")?.toInt() ?: 80,
            tacklesWon = getLong("tacklesWon")?.toInt() ?: 0,
            cleanSheets = getLong("cleanSheets")?.toInt() ?: 0,
            yellowCards = getLong("yellowCards")?.toInt() ?: 0,
            redCards = getLong("redCards")?.toInt() ?: 0,
            readinessStatus = getString("readinessStatus") ?: "Match Ready",
            recentRatings = ratingsList,
            coachEvaluation = getString("coachEvaluation") ?: "High match fitness and tactical sharpness.",
            evaluatedBy = getString("evaluatedBy") ?: "Head Coach",
            updatedAt = getString("updatedAt") ?: "2026-09-30"
        )
    } catch (e: Exception) {
        ClubPlayerPerformanceModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubFitnessAssessmentSafe(clubId: String): ClubFitnessAssessmentModel {
    return try {
        ClubFitnessAssessmentModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            athleteId = getString("athleteId") ?: "",
            athleteName = getString("athleteName") ?: "Athlete",
            teamName = getString("teamName") ?: "Senior Team",
            testDate = getString("testDate") ?: "2026-09-30",
            sprintSpeedKmH = getDouble("sprintSpeedKmH") ?: 32.0,
            vo2Max = getDouble("vo2Max") ?: 55.0,
            verticalJumpCm = getDouble("verticalJumpCm") ?: 58.0,
            yoyoTestLevel = getString("yoyoTestLevel") ?: "Level 19.2",
            bodyFatPercent = getDouble("bodyFatPercent") ?: 10.5,
            acwrRatio = getDouble("acwrRatio") ?: 1.0,
            overallFitnessGrade = getString("overallFitnessGrade") ?: "A",
            trainerNotes = getString("trainerNotes") ?: "High conditioning level."
        )
    } catch (e: Exception) {
        ClubFitnessAssessmentModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubDevelopmentPlanSafe(clubId: String): ClubDevelopmentPlanModel {
    return try {
        ClubDevelopmentPlanModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            athleteId = getString("athleteId") ?: "",
            athleteName = getString("athleteName") ?: "Academy Player",
            currentTeam = getString("currentTeam") ?: "U17 Academy",
            pathwayTarget = getString("pathwayTarget") ?: "Senior Team",
            progressPercent = getLong("progressPercent")?.toInt() ?: 60,
            promotionReadiness = getString("promotionReadiness") ?: "High",
            primarySkillGoal = getString("primarySkillGoal") ?: "Tactical Awareness",
            targetPosition = getString("targetPosition") ?: "Attacking Midfielder",
            coachMentor = getString("coachMentor") ?: "Academy Coach",
            milestonesCount = getLong("milestonesCount")?.toInt() ?: 5,
            completedMilestones = getLong("completedMilestones")?.toInt() ?: 3,
            coachAssessment = getString("coachAssessment") ?: "Strong progression.",
            lastReviewDate = getString("lastReviewDate") ?: "2026-09-30"
        )
    } catch (e: Exception) {
        ClubDevelopmentPlanModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubRecruitmentPipelineSafe(clubId: String): ClubRecruitmentPipelineModel {
    return try {
        ClubRecruitmentPipelineModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            candidateName = getString("candidateName") ?: "Candidate",
            position = getString("position") ?: "Defensive Midfielder (DM)",
            currentClub = getString("currentClub") ?: "Regional Academy",
            age = getLong("age")?.toInt() ?: 18,
            preferredFoot = getString("preferredFoot") ?: "Left",
            stage = getString("stage") ?: "Trial",
            scoutName = getString("scoutName") ?: "Chief Scout",
            scoutRating = getDouble("scoutRating") ?: 8.0,
            evaluationNotes = getString("evaluationNotes") ?: "Strong prospect.",
            trialDate = getString("trialDate") ?: "2026-10-10",
            decisionStatus = getString("decisionStatus") ?: "Pending Trial",
            targetTeam = getString("targetTeam") ?: "Senior Team / U23"
        )
    } catch (e: Exception) {
        ClubRecruitmentPipelineModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubScoutAssignmentSafe(clubId: String): ClubScoutAssignmentModel {
    return try {
        ClubScoutAssignmentModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            scoutName = getString("scoutName") ?: "Regional Scout",
            projectTitle = getString("projectTitle") ?: "Scouting Project",
            targetPosition = getString("targetPosition") ?: "Midfielder",
            ageRange = getString("ageRange") ?: "17–20",
            territory = getString("territory") ?: "Western Region",
            specialization = getString("specialization") ?: "Youth Prospects",
            athletesDiscovered = getLong("athletesDiscovered")?.toInt() ?: 0,
            shortlisted = getLong("shortlisted")?.toInt() ?: 0,
            trialsInvited = getLong("trialsInvited")?.toInt() ?: 0,
            status = getString("status") ?: "Active",
            deadline = getString("deadline") ?: "2026-11-15"
        )
    } catch (e: Exception) {
        ClubScoutAssignmentModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubAnalystProjectSafe(clubId: String): ClubAnalystProjectModel {
    return try {
        ClubAnalystProjectModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            analystName = getString("analystName") ?: "Tactical Analyst",
            projectTitle = getString("projectTitle") ?: "Tactical Analysis",
            category = getString("category") ?: "Matches",
            targetTeam = getString("targetTeam") ?: "Senior Team",
            competition = getString("competition") ?: "Premier League",
            status = getString("status") ?: "Completed",
            keyFindings = getString("keyFindings") ?: "Tactical analysis ready.",
            deliveryDate = getString("deliveryDate") ?: "2026-09-30"
        )
    } catch (e: Exception) {
        ClubAnalystProjectModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubAvailabilityStatusSafe(clubId: String): ClubAvailabilityStatusModel {
    return try {
        ClubAvailabilityStatusModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            athleteId = getString("athleteId") ?: "",
            athleteName = getString("athleteName") ?: "Athlete",
            teamName = getString("teamName") ?: "Senior Team",
            position = getString("position") ?: "Forward",
            status = getString("status") ?: "Available",
            limitationNotes = getString("limitationNotes") ?: "Full training participation authorized.",
            expectedReturnDate = getString("expectedReturnDate") ?: "Immediate",
            riskLevel = getString("riskLevel") ?: "Low",
            protectionProtocol = getString("protectionProtocol") ?: "Active Monitoring",
            updatedBy = getString("updatedBy") ?: "Physio Lead",
            updatedAt = getString("updatedAt") ?: "2026-09-30"
        )
    } catch (e: Exception) {
        ClubAvailabilityStatusModel(id = id, clubId = clubId)
    }
}

private fun DocumentSnapshot.toClubCalendarEventSafe(clubId: String): ClubCalendarEventModel {
    return try {
        ClubCalendarEventModel(
            id = id,
            clubId = getString("clubId") ?: clubId,
            title = getString("title") ?: "Club Event",
            eventType = getString("eventType") ?: "Matches",
            date = getString("date") ?: "2026-10-05",
            time = getString("time") ?: "15:00",
            targetTeam = getString("targetTeam") ?: "Senior Team",
            venue = getString("venue") ?: "Main Stadium",
            notes = getString("notes") ?: ""
        )
    } catch (e: Exception) {
        ClubCalendarEventModel(id = id, clubId = clubId)
    }
}
