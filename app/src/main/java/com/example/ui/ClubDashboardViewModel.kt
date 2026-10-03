package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface ClubDashboardUiState {
    object Loading : ClubDashboardUiState
    object NoClubFound : ClubDashboardUiState
    data class Success(
        val clubMember: ClubMember,
        val clubDetails: ClubModel?,
        val athletes: List<AthleteEntity> = emptyList(),
        val staff: List<ClubMember> = emptyList(),
        val teams: List<ClubTeamModel> = emptyList(),
        val competitions: List<ClubCompetitionModel> = emptyList(),
        val matches: List<ClubMatchModel> = emptyList(),
        val playerPerformances: List<ClubPlayerPerformanceModel> = emptyList(),
        val fitnessAssessments: List<ClubFitnessAssessmentModel> = emptyList(),
        val trainingSessions: List<ClubTrainingModel> = emptyList(),
        val documents: List<ClubDocumentModel> = emptyList(),
        val registrations: List<ClubRegistrationModel> = emptyList(),
        val opportunities: List<ClubOpportunityModel> = emptyList(),
        val signals: List<ClubIntelligenceSignal> = emptyList(),
        val auditLogs: List<ClubAuditEntry> = emptyList(),
        val financials: List<ClubFinancialRecord> = emptyList(),
        val developmentPlans: List<ClubDevelopmentPlanModel> = emptyList(),
        val recruitmentPipeline: List<ClubRecruitmentPipelineModel> = emptyList(),
        val scoutAssignments: List<ClubScoutAssignmentModel> = emptyList(),
        val analystProjects: List<ClubAnalystProjectModel> = emptyList(),
        val availabilityRecords: List<ClubAvailabilityStatusModel> = emptyList(),
        val calendarEvents: List<ClubCalendarEventModel> = emptyList()
    ) : ClubDashboardUiState
    data class Error(val message: String) : ClubDashboardUiState
}

private data class PrimaryClubData(
    val clubDetails: ClubModel?,
    val athletes: List<AthleteEntity>,
    val staff: List<ClubMember>,
    val teams: List<ClubTeamModel>,
    val competitions: List<ClubCompetitionModel>
)

private data class SecondaryClubData(
    val matches: List<ClubMatchModel>,
    val playerPerformances: List<ClubPlayerPerformanceModel>,
    val fitnessAssessments: List<ClubFitnessAssessmentModel>,
    val training: List<ClubTrainingModel>,
    val docs: List<ClubDocumentModel>
)

private data class TertiaryClubData(
    val regs: List<ClubRegistrationModel>,
    val opps: List<ClubOpportunityModel>,
    val signals: List<ClubIntelligenceSignal>,
    val audits: List<ClubAuditEntry>,
    val financials: List<ClubFinancialRecord>
)

private data class QuaternaryClubData(
    val devPlans: List<ClubDevelopmentPlanModel>,
    val recruitment: List<ClubRecruitmentPipelineModel>,
    val scouts: List<ClubScoutAssignmentModel>,
    val analysts: List<ClubAnalystProjectModel>,
    val availability: List<ClubAvailabilityStatusModel>,
    val calendarEvents: List<ClubCalendarEventModel>
)

class ClubDashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ClubAdminRepository()

    private val _uiState = MutableStateFlow<ClubDashboardUiState>(ClubDashboardUiState.Loading)
    val uiState: StateFlow<ClubDashboardUiState> = _uiState.asStateFlow()

    private val _actionFeedback = MutableSharedFlow<String>()
    val actionFeedback: SharedFlow<String> = _actionFeedback.asSharedFlow()

    init {
        observeAdminMembership()
    }

    private fun observeAdminMembership() {
        viewModelScope.launch {
            _uiState.value = ClubDashboardUiState.Loading
            repository.getCurrentAdminMembership().collectLatest { member ->
                if (member == null) {
                    _uiState.value = ClubDashboardUiState.NoClubFound
                } else {
                    val clubId = member.clubId
                    val primaryFlow = combine(
                        repository.getClubDetails(clubId),
                        repository.getClubAthletes(clubId),
                        repository.getClubStaff(clubId),
                        repository.getClubTeams(clubId),
                        repository.getClubCompetitions(clubId)
                    ) { clubDetails, athletes, staff, teams, competitions ->
                        PrimaryClubData(clubDetails, athletes, staff, teams, competitions)
                    }

                    val secondaryFlow = combine(
                        repository.getClubMatches(clubId),
                        repository.getClubPlayerPerformances(clubId),
                        repository.getClubFitnessAssessments(clubId),
                        repository.getClubTrainingSessions(clubId),
                        repository.getClubDocuments(clubId)
                    ) { matches, playerPerfs, fitnessAss, training, docs ->
                        SecondaryClubData(matches, playerPerfs, fitnessAss, training, docs)
                    }

                    val tertiaryFlow = combine(
                        repository.getClubRegistrations(clubId),
                        repository.getClubOpportunities(clubId),
                        repository.getClubIntelligenceSignals(clubId),
                        repository.getClubAuditLogs(clubId),
                        repository.getClubFinancials(clubId)
                    ) { regs, opps, signals, audits, financials ->
                        TertiaryClubData(regs, opps, signals, audits, financials)
                    }

                    val quaternaryFlow = combine(
                        repository.getClubDevelopmentPlans(clubId),
                        repository.getClubRecruitmentPipeline(clubId),
                        repository.getClubScoutAssignments(clubId),
                        repository.getClubAnalystProjects(clubId),
                        repository.getClubAvailabilityRecords(clubId)
                    ) { devPlans, recruitment, scouts, analysts, availability ->
                        QuaternaryClubData(devPlans, recruitment, scouts, analysts, availability, emptyList())
                    }.combine(repository.getClubCalendarEvents(clubId)) { quat, calEvents ->
                        quat.copy(calendarEvents = calEvents)
                    }

                    combine(primaryFlow, secondaryFlow, tertiaryFlow, quaternaryFlow) { primary, secondary, tertiary, quat ->
                        ClubDashboardUiState.Success(
                            clubMember = member,
                            clubDetails = primary.clubDetails,
                            athletes = primary.athletes,
                            staff = primary.staff,
                            teams = primary.teams,
                            competitions = primary.competitions,
                            matches = secondary.matches,
                            playerPerformances = secondary.playerPerformances,
                            fitnessAssessments = secondary.fitnessAssessments,
                            trainingSessions = secondary.training,
                            documents = secondary.docs,
                            registrations = tertiary.regs,
                            opportunities = tertiary.opps,
                            signals = tertiary.signals,
                            auditLogs = tertiary.audits,
                            financials = tertiary.financials,
                            developmentPlans = quat.devPlans,
                            recruitmentPipeline = quat.recruitment,
                            scoutAssignments = quat.scouts,
                            analystProjects = quat.analysts,
                            availabilityRecords = quat.availability,
                            calendarEvents = quat.calendarEvents
                        )
                    }.collect { successState ->
                        _uiState.value = successState
                    }
                }
            }
        }
    }

    // --- Actions ---

    fun addAthlete(name: String, position: String, sport: String = "Football (Soccer)", rating: Int = 75) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.addAthleteToSquad(current.clubMember.clubId, name, position, sport, rating)
                _actionFeedback.emit("Successfully registered $name to squad!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to add athlete: ${e.localizedMessage}")
            }
        }
    }

    fun invitePlayer(
        playerName: String,
        contactMethod: String,
        contactValue: String,
        tier: String = "First Team",
        position: String = "Forward (ST)",
        inviteCode: String,
        inviteLink: String
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.invitePlayer(
                    clubId = current.clubMember.clubId,
                    clubName = current.clubMember.clubName,
                    playerName = playerName,
                    contactMethod = contactMethod,
                    contactValue = contactValue,
                    tier = tier,
                    position = position,
                    inviteCode = inviteCode,
                    inviteLink = inviteLink
                )
                _actionFeedback.emit("Invitation dispatched to $playerName! Code: $inviteCode")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to send invitation: ${e.localizedMessage}")
            }
        }
    }

    fun createTeam(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createTeam(
                    clubId = current.clubMember.clubId,
                    name = name,
                    ageCategory = ageCategory,
                    coach = coach,
                    competition = competition,
                    gender = gender,
                    assistantCoach = assistantCoach,
                    analyst = analyst,
                    sport = sport,
                    formation = formation,
                    notes = notes
                )
                _actionFeedback.emit("Team $name ($ageCategory) created successfully in Firestore!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to create team: ${e.localizedMessage}")
            }
        }
    }

    fun deleteTeam(teamId: String, teamName: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.deleteTeam(current.clubMember.clubId, teamId, teamName)
                _actionFeedback.emit("Team $teamName removed.")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to remove team: ${e.localizedMessage}")
            }
        }
    }

    fun createCompetition(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createCompetition(
                    clubId = current.clubMember.clubId,
                    name = name,
                    season = season,
                    tier = tier,
                    division = division,
                    federation = federation,
                    teamName = teamName,
                    status = status,
                    tablePosition = tablePosition,
                    points = points
                )
                _actionFeedback.emit("Registered into $name ($season)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to register competition: ${e.localizedMessage}")
            }
        }
    }

    fun deleteCompetition(competitionId: String, compName: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.deleteCompetition(current.clubMember.clubId, competitionId, compName)
                _actionFeedback.emit("Withdrawn from $compName.")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to withdraw: ${e.localizedMessage}")
            }
        }
    }

    fun updateMatchScore(
        matchId: String,
        homeScore: Int,
        awayScore: Int,
        status: String,
        minute: Int = 90,
        notes: String = "",
        manOfTheMatch: String = ""
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateMatchScore(
                    clubId = current.clubMember.clubId,
                    matchId = matchId,
                    homeScore = homeScore,
                    awayScore = awayScore,
                    status = status,
                    minute = minute,
                    notes = notes,
                    manOfTheMatch = manOfTheMatch
                )
                _actionFeedback.emit("Match score updated to $homeScore - $awayScore ($status)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to update score: ${e.localizedMessage}")
            }
        }
    }

    fun addMatchEvent(
        matchId: String,
        minute: Int,
        type: String,
        team: String,
        player: String,
        assistPlayer: String = "",
        subInPlayer: String = "",
        notes: String = ""
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.addMatchEvent(
                    clubId = current.clubMember.clubId,
                    matchId = matchId,
                    minute = minute,
                    type = type,
                    team = team,
                    player = player,
                    assistPlayer = assistPlayer,
                    subInPlayer = subInPlayer,
                    notes = notes
                )
                _actionFeedback.emit("Logged $type for $player ($minute')")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to log event: ${e.localizedMessage}")
            }
        }
    }

    fun updateMatchLineup(
        matchId: String,
        startingXI: List<String>,
        substitutes: List<String>,
        formation: String,
        captain: String
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateMatchLineup(
                    clubId = current.clubMember.clubId,
                    matchId = matchId,
                    startingXI = startingXI,
                    substitutes = substitutes,
                    formation = formation,
                    captain = captain
                )
                _actionFeedback.emit("Match squad lineup updated ($formation)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to update lineup: ${e.localizedMessage}")
            }
        }
    }

    fun updateMatchStats(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateMatchStats(
                    clubId = current.clubMember.clubId,
                    matchId = matchId,
                    possessionHome = possessionHome,
                    shotsHome = shotsHome,
                    shotsAway = shotsAway,
                    shotsOnTargetHome = shotsOnTargetHome,
                    shotsOnTargetAway = shotsOnTargetAway,
                    cornersHome = cornersHome,
                    cornersAway = cornersAway,
                    foulsHome = foulsHome,
                    foulsAway = foulsAway
                )
                _actionFeedback.emit("Match statistics updated!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to update stats: ${e.localizedMessage}")
            }
        }
    }

    fun deleteMatch(matchId: String, matchTitle: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.deleteMatch(current.clubMember.clubId, matchId, matchTitle)
                _actionFeedback.emit("Match fixture '$matchTitle' removed.")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to remove fixture: ${e.localizedMessage}")
            }
        }
    }

    fun inviteStaff(displayName: String, role: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.inviteStaff(current.clubMember.clubId, current.clubMember.clubName, displayName, role)
                _actionFeedback.emit("Invited $displayName as $role!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to invite staff: ${e.localizedMessage}")
            }
        }
    }

    fun registerAthlete(athleteName: String, regNumber: String, competition: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.registerAthlete(current.clubMember.clubId, athleteName, regNumber, competition)
                _actionFeedback.emit("Registration completed for $athleteName!")
            } catch (e: Exception) {
                _actionFeedback.emit("Registration error: ${e.localizedMessage}")
            }
        }
    }

    fun createMatch(
        teamName: String,
        opponent: String,
        date: String,
        time: String = "15:00",
        venue: String = "Home",
        stadium: String = "Main Stadium",
        city: String = "Nairobi",
        comp: String = "Premier League",
        round: String = "Matchday 1",
        matchType: String = "League",
        referee: String = "Official Match Official",
        formation: String = "4-3-3",
        captain: String = "",
        notes: String = ""
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createMatch(
                    clubId = current.clubMember.clubId,
                    teamName = teamName,
                    opponent = opponent,
                    date = date,
                    time = time,
                    venue = venue,
                    stadium = stadium,
                    city = city,
                    competition = comp,
                    round = round,
                    matchType = matchType,
                    referee = referee,
                    formation = formation,
                    captain = captain,
                    notes = notes
                )
                _actionFeedback.emit("Match fixture scheduled: $teamName vs $opponent")
            } catch (e: Exception) {
                _actionFeedback.emit("Match error: ${e.localizedMessage}")
            }
        }
    }

    fun createTraining(teamName: String, type: String, date: String, duration: Int, load: Int, notes: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createTrainingSession(current.clubMember.clubId, teamName, type, date, duration, load, notes)
                _actionFeedback.emit("Training session scheduled for $teamName")
            } catch (e: Exception) {
                _actionFeedback.emit("Training error: ${e.localizedMessage}")
            }
        }
    }

    fun createOpportunity(title: String, type: String, targetPos: String, deadline: String, desc: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createOpportunity(current.clubMember.clubId, title, type, targetPos, deadline, desc)
                _actionFeedback.emit("Opportunity published: $title")
            } catch (e: Exception) {
                _actionFeedback.emit("Opportunity error: ${e.localizedMessage}")
            }
        }
    }

    fun uploadDocument(title: String, category: String, target: String, verification: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.uploadDocument(current.clubMember.clubId, title, category, target, verification)
                _actionFeedback.emit("Document '$title' securely uploaded to vault!")
            } catch (e: Exception) {
                _actionFeedback.emit("Upload error: ${e.localizedMessage}")
            }
        }
    }

    fun logPlayerPerformance(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.logPlayerPerformance(
                    clubId = current.clubMember.clubId,
                    athleteId = athleteId,
                    athleteName = athleteName,
                    position = position,
                    teamName = teamName,
                    matchesPlayed = matchesPlayed,
                    minutesPlayed = minutesPlayed,
                    goals = goals,
                    assists = assists,
                    averageRating = averageRating,
                    passAccuracy = passAccuracy,
                    tacklesWon = tacklesWon,
                    cleanSheets = cleanSheets,
                    readinessStatus = readinessStatus,
                    coachEvaluation = coachEvaluation,
                    evaluatedBy = evaluatedBy,
                    recentRatings = recentRatings
                )
                _actionFeedback.emit("Performance log saved for $athleteName!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to log performance: ${e.localizedMessage}")
            }
        }
    }

    fun logFitnessAssessment(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.logFitnessAssessment(
                    clubId = current.clubMember.clubId,
                    athleteId = athleteId,
                    athleteName = athleteName,
                    teamName = teamName,
                    testDate = testDate,
                    sprintSpeed = sprintSpeed,
                    vo2Max = vo2Max,
                    verticalJump = verticalJump,
                    yoyoLevel = yoyoLevel,
                    bodyFat = bodyFat,
                    acwrRatio = acwrRatio,
                    grade = grade,
                    notes = notes
                )
                _actionFeedback.emit("Physical fitness evaluation recorded for $athleteName ($grade)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to log fitness assessment: ${e.localizedMessage}")
            }
        }
    }

    fun deletePlayerPerformance(performanceId: String, athleteName: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.deletePlayerPerformance(current.clubMember.clubId, performanceId, athleteName)
                _actionFeedback.emit("Performance record removed for $athleteName.")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to remove performance record: ${e.localizedMessage}")
            }
        }
    }

    fun deleteFitnessAssessment(assessmentId: String, athleteName: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.deleteFitnessAssessment(current.clubMember.clubId, assessmentId, athleteName)
                _actionFeedback.emit("Fitness assessment removed for $athleteName.")
            } catch (e: Exception) {
                _actionFeedback.emit("Failed to remove fitness assessment: ${e.localizedMessage}")
            }
        }
    }

    fun updateClubProfile(clubName: String, location: String, email: String, phone: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateClubProfile(current.clubMember.clubId, clubName, location, email, phone)
                _actionFeedback.emit("Club profile updated successfully!")
            } catch (e: Exception) {
                _actionFeedback.emit("Update error: ${e.localizedMessage}")
            }
        }
    }

    fun recordFinancialTransaction(
        title: String,
        type: String,
        category: String,
        amount: String,
        date: String = "2026-09-30",
        status: String = "Paid",
        counterparty: String = "General"
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.recordFinancialTransaction(
                    clubId = current.clubMember.clubId,
                    title = title,
                    type = type,
                    category = category,
                    amount = amount,
                    date = date,
                    status = status,
                    counterparty = counterparty
                )
                _actionFeedback.emit("Recorded transaction: $title ($amount)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Payment recording error: ${e.localizedMessage}")
            }
        }
    }

    fun createCalendarEvent(
        title: String,
        eventType: String,
        date: String,
        time: String = "10:00",
        targetTeam: String = "Senior Team",
        venue: String = "Main Stadium",
        notes: String = ""
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createCalendarEvent(
                    clubId = current.clubMember.clubId,
                    title = title,
                    eventType = eventType,
                    date = date,
                    time = time,
                    targetTeam = targetTeam,
                    venue = venue,
                    notes = notes
                )
                _actionFeedback.emit("Created event: $title on $date!")
            } catch (e: Exception) {
                _actionFeedback.emit("Calendar event error: ${e.localizedMessage}")
            }
        }
    }

    fun createDevelopmentPlan(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createDevelopmentPlan(
                    clubId = current.clubMember.clubId,
                    athleteId = athleteId,
                    athleteName = athleteName,
                    currentTeam = currentTeam,
                    pathwayTarget = pathwayTarget,
                    progressPercent = progressPercent,
                    promotionReadiness = promotionReadiness,
                    primaryGoal = primaryGoal,
                    targetPos = targetPos,
                    mentor = mentor,
                    coachAssessment = coachAssessment
                )
                _actionFeedback.emit("Created IDP plan for $athleteName!")
            } catch (e: Exception) {
                _actionFeedback.emit("IDP error: ${e.localizedMessage}")
            }
        }
    }

    fun addRecruitmentCandidate(
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
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.addRecruitmentCandidate(
                    clubId = current.clubMember.clubId,
                    candidateName = candidateName,
                    position = position,
                    currentClub = currentClub,
                    age = age,
                    foot = foot,
                    stage = stage,
                    scoutName = scoutName,
                    scoutRating = scoutRating,
                    notes = notes,
                    trialDate = trialDate,
                    targetTeam = targetTeam
                )
                _actionFeedback.emit("Added $candidateName to recruitment pipeline ($stage)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Recruitment candidate error: ${e.localizedMessage}")
            }
        }
    }

    fun updateRecruitmentStage(pipelineId: String, candidateName: String, newStage: String, decisionStatus: String) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateRecruitmentStage(current.clubMember.clubId, pipelineId, candidateName, newStage, decisionStatus)
                _actionFeedback.emit("Updated $candidateName to $newStage!")
            } catch (e: Exception) {
                _actionFeedback.emit("Pipeline update error: ${e.localizedMessage}")
            }
        }
    }

    fun createScoutAssignment(
        scoutName: String,
        projectTitle: String,
        targetPosition: String,
        ageRange: String,
        territory: String,
        specialization: String,
        deadline: String
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createScoutAssignment(
                    clubId = current.clubMember.clubId,
                    scoutName = scoutName,
                    projectTitle = projectTitle,
                    targetPosition = targetPosition,
                    ageRange = ageRange,
                    territory = territory,
                    specialization = specialization,
                    deadline = deadline
                )
                _actionFeedback.emit("Assigned scouting mission: $projectTitle to $scoutName!")
            } catch (e: Exception) {
                _actionFeedback.emit("Scout assignment error: ${e.localizedMessage}")
            }
        }
    }

    fun createAnalystProject(
        analystName: String,
        projectTitle: String,
        category: String,
        targetTeam: String,
        competition: String,
        keyFindings: String,
        deliveryDate: String
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.createAnalystProject(
                    clubId = current.clubMember.clubId,
                    analystName = analystName,
                    projectTitle = projectTitle,
                    category = category,
                    targetTeam = targetTeam,
                    competition = competition,
                    keyFindings = keyFindings,
                    deliveryDate = deliveryDate
                )
                _actionFeedback.emit("Commissioned analysis: $projectTitle!")
            } catch (e: Exception) {
                _actionFeedback.emit("Analyst project error: ${e.localizedMessage}")
            }
        }
    }

    fun updateAthleteAvailability(
        athleteName: String,
        teamName: String,
        position: String,
        newStatus: String,
        limitationNotes: String,
        expectedReturnDate: String,
        riskLevel: String,
        protocol: String
    ) {
        val current = _uiState.value as? ClubDashboardUiState.Success ?: return
        viewModelScope.launch {
            try {
                repository.updateAthleteAvailability(
                    clubId = current.clubMember.clubId,
                    athleteName = athleteName,
                    teamName = teamName,
                    position = position,
                    newStatus = newStatus,
                    limitationNotes = limitationNotes,
                    expectedReturnDate = expectedReturnDate,
                    riskLevel = riskLevel,
                    protocol = protocol
                )
                _actionFeedback.emit("Updated availability for $athleteName to $newStatus ($protocol)!")
            } catch (e: Exception) {
                _actionFeedback.emit("Availability error: ${e.localizedMessage}")
            }
        }
    }
}
