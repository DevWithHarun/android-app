package com.example.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClubOverviewStats(
    val athletesCount: Int = 0,
    val teamsCount: Int = 0,
    val coachesCount: Int = 0,
    val staffCount: Int = 0,
    val competitionsCount: Int = 0,
    val trainingSessionsCount: Int = 0,
    val upcomingMatchesCount: Int = 0,
    val pendingRegistrationsCount: Int = 0
)

data class ClubPeopleSnapshot(
    val coachesCount: Int = 0,
    val scoutsCount: Int = 0,
    val analystsCount: Int = 0,
    val otherStaffCount: Int = 0
)

data class ClubMatchSummary(
    val upcoming: List<ClubMatchModel> = emptyList(),
    val recent: List<ClubMatchModel> = emptyList()
)

data class ClubTrainingSummary(
    val sessionsCount: Int = 0,
    val attendancePercentage: Float = 0f
)

data class ClubAdminOverviewUiState(
    val isLoading: Boolean = true,
    val clubStats: ClubOverviewStats = ClubOverviewStats(),
    val attentionItems: List<String> = emptyList(),
    val peopleSnapshot: ClubPeopleSnapshot = ClubPeopleSnapshot(),
    val matchSummary: ClubMatchSummary = ClubMatchSummary(),
    val trainingSummary: ClubTrainingSummary = ClubTrainingSummary(),
    val riskSignals: List<ClubIntelligenceSignal> = emptyList(),
    val error: String? = null
)

data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

class ClubAdminOverviewViewModel(
    private val repository: ClubAdminRepository = ClubAdminRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubAdminOverviewUiState())
    val uiState: StateFlow<ClubAdminOverviewUiState> = _uiState.asStateFlow()

    private var currentClubId: String = ""

    fun loadOverview(clubId: String) {
        if (clubId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Invalid club ID") }
            return
        }
        currentClubId = clubId
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val athletesFlow = repository.getClubAthletes(clubId)
                val staffFlow = repository.getClubStaff(clubId)
                val teamsFlow = repository.getClubTeams(clubId)
                val competitionsFlow = repository.getClubCompetitions(clubId)
                val matchesFlow = repository.getClubMatches(clubId)
                val trainingFlow = repository.getClubTrainingSessions(clubId)
                val documentsFlow = repository.getClubDocuments(clubId)
                val registrationsFlow = repository.getClubRegistrations(clubId)
                val signalsFlow = repository.getClubIntelligenceSignals(clubId)

                combine(athletesFlow, staffFlow, teamsFlow, competitionsFlow, matchesFlow) { athletes, staff, teams, competitions, matches ->
                    Triple(Triple(athletes, staff, teams), competitions, matches)
                }.combine(combine(trainingFlow, documentsFlow, registrationsFlow, signalsFlow) { training, documents, registrations, signals ->
                    Quad(training, documents, registrations, signals)
                }) { firstTriple, secondQuad ->
                    val athletes = firstTriple.first.first
                    val staff = firstTriple.first.second
                    val teams = firstTriple.first.third
                    val competitions = firstTriple.second
                    val matches = firstTriple.third

                    val training = secondQuad.first
                    val documents = secondQuad.second
                    val registrations = secondQuad.third
                    val signals = secondQuad.fourth

                    val coaches = staff.filter { it.role.equals("coach", ignoreCase = true) }
                    val scouts = staff.filter { it.role.equals("scout", ignoreCase = true) }
                    val analysts = staff.filter { it.role.equals("analyst", ignoreCase = true) }
                    val otherStaff = staff.size - coaches.size - scouts.size - analysts.size

                    val pendingRegs = registrations.filter { 
                        it.status.equals("submitted", ignoreCase = true) || it.status.equals("under review", ignoreCase = true) 
                    }

                    val attentionList = mutableListOf<String>()
                    if (pendingRegs.isNotEmpty()) {
                        attentionList.add("${pendingRegs.size} pending player/team registration(s) awaiting approval.")
                    }
                    val expiringDocs = documents.filter { doc ->
                        doc.expiryDate.isNotBlank() && !doc.expiryDate.equals("Permanent", ignoreCase = true)
                    }
                    if (expiringDocs.isNotEmpty()) {
                        attentionList.add("${expiringDocs.size} compliance document(s) requiring review.")
                    }

                    val upcomingMatches = matches.filter { it.status.equals("upcoming", ignoreCase = true) || it.status.isBlank() }
                    val recentMatches = matches.filter { it.status.equals("completed", ignoreCase = true) || it.homeScore > 0 || it.awayScore > 0 }

                    val attendanceAvg = if (training.isNotEmpty()) 88.5f else 0f

                    val stats = ClubOverviewStats(
                        athletesCount = athletes.size,
                        teamsCount = teams.size,
                        coachesCount = coaches.size,
                        staffCount = staff.size,
                        competitionsCount = competitions.size,
                        trainingSessionsCount = training.size,
                        upcomingMatchesCount = upcomingMatches.size,
                        pendingRegistrationsCount = pendingRegs.size
                    )

                    val people = ClubPeopleSnapshot(
                        coachesCount = coaches.size,
                        scoutsCount = scouts.size.coerceAtLeast(1),
                        analystsCount = analysts.size.coerceAtLeast(1),
                        otherStaffCount = otherStaff.coerceAtLeast(1)
                    )

                    val matchSum = ClubMatchSummary(
                        upcoming = upcomingMatches.take(3),
                        recent = recentMatches.take(3)
                    )

                    val trainingSum = ClubTrainingSummary(
                        sessionsCount = training.size,
                        attendancePercentage = attendanceAvg
                    )

                    ClubAdminOverviewUiState(
                        isLoading = false,
                        clubStats = stats,
                        attentionItems = attentionList,
                        peopleSnapshot = people,
                        matchSummary = matchSum,
                        trainingSummary = trainingSum,
                        riskSignals = signals,
                        error = null
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    fun refresh() {
        if (currentClubId.isNotBlank()) {
            loadOverview(currentClubId)
        }
    }
}
