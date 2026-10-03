package com.example.data

data class ClubTeamModel(
    val id: String = "",
    val clubId: String = "",
    val name: String = "",
    val ageCategory: String = "Senior",
    val gender: String = "Men",
    val headCoachName: String = "",
    val assistantCoachName: String = "",
    val analystName: String = "",
    val squadCount: Int = 0,
    val competition: String = "National League",
    val sport: String = "Football (Soccer)",
    val formation: String = "4-3-3",
    val trainingSchedule: String = "Mon, Wed, Fri 08:00",
    val notes: String = ""
)

data class ClubCompetitionModel(
    val id: String = "",
    val clubId: String = "",
    val name: String = "",
    val season: String = "2026/2027",
    val tier: String = "Premier Tier 1",
    val division: String = "National Premier Division",
    val federation: String = "National Football Federation",
    val status: String = "Active", // Active, Upcoming, Completed
    val teamName: String = "Senior Team",
    val matchesPlayed: Int = 12,
    val wins: Int = 8,
    val draws: Int = 3,
    val losses: Int = 1,
    val points: Int = 27,
    val tablePosition: Int = 2,
    val qualificationStatus: String = "Champions League Qualification Zone"
)

data class ClubRegistrationModel(
    val id: String = "",
    val clubId: String = "",
    val athleteName: String = "",
    val registrationNumber: String = "",
    val competition: String = "National League",
    val status: String = "Verified", // Verified, Pending, Expiring
    val expiryDate: String = "2026-12-31",
    val verifiedBy: String = "National Federation"
)

data class ClubDocumentModel(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val category: String = "Contract", // Contract, Consent, License, Identification, Registration
    val targetEntity: String = "General",
    val fileUrl: String = "",
    val expiryDate: String = "Permanent",
    val verificationLevel: String = "L2 — Organization Verified",
    val uploadedBy: String = "Admin",
    val uploadedAt: String = "2026-09-30"
)

data class ClubMatchEvent(
    val id: String = "",
    val minute: Int = 0,
    val type: String = "Goal", // Goal, Yellow Card, Red Card, Substitution, Penalty, Own Goal, Assist
    val team: String = "home", // home, away
    val player: String = "",
    val assistPlayer: String = "",
    val subInPlayer: String = "",
    val notes: String = ""
)

data class ClubMatchModel(
    val id: String = "",
    val clubId: String = "",
    val teamName: String = "Senior Team",
    val teamId: String = "",
    val opponent: String = "",
    val opponentLogoUrl: String = "",
    val date: String = "2026-10-05",
    val time: String = "15:00",
    val venue: String = "Home", // Home, Away, Neutral
    val stadium: String = "Main Stadium",
    val city: String = "Nairobi",
    val competition: String = "Premier League",
    val round: String = "Matchday 14",
    val matchType: String = "League", // League, Cup, Friendly, Playoff
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val status: String = "Upcoming", // Upcoming, Live, Halftime, Completed, Postponed, Cancelled
    val minute: Int = 0,
    val referee: String = "Match Official",
    val formation: String = "4-3-3",
    val captain: String = "",
    val homeTeamName: String = "",
    val awayTeamName: String = "",
    val matchEvents: List<ClubMatchEvent> = emptyList(),
    val possessionHome: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val foulsHome: Int = 0,
    val foulsAway: Int = 0,
    val yellowCardsHome: Int = 0,
    val yellowCardsAway: Int = 0,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0,
    val manOfTheMatch: String = "",
    val notes: String = "",
    val startingLineup: List<String> = emptyList(),
    val substitutes: List<String> = emptyList()
)

data class ClubTrainingModel(
    val id: String = "",
    val clubId: String = "",
    val teamName: String = "Senior Team",
    val sessionType: String = "Tactical & Match Prep",
    val date: String = "2026-10-01",
    val durationMins: Int = 90,
    val intensity: String = "High",
    val attendancePercent: Int = 92,
    val averageLoad: Int = 74,
    val coachNotes: String = "Focus on defensive transitions and set pieces."
)

data class ClubOpportunityModel(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val type: String = "Trial", // Trial, Academy Scouting, Scholarship, Tournament
    val targetPosition: String = "All Positions",
    val ageGroup: String = "U20",
    val deadline: String = "2026-11-01",
    val description: String = "",
    val status: String = "Open",
    val applicantsCount: Int = 0
)

data class ClubIntelligenceSignal(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val teamName: String = "Senior Team",
    val type: String = "Workload", // Workload, Availability, Performance, Registration
    val description: String = "",
    val severity: String = "Medium", // High, Medium, Low
    val status: String = "Active",
    val recommendedAction: String = "Review training schedule."
)

data class ClubAuditEntry(
    val id: String = "",
    val clubId: String = "",
    val actorName: String = "Admin",
    val actorRole: String = "Club Admin",
    val action: String = "",
    val targetRecord: String = "",
    val timestamp: String = "Just now",
    val changeDetails: String = ""
)

data class ClubPlayerPerformanceModel(
    val id: String = "",
    val clubId: String = "",
    val athleteId: String = "",
    val athleteName: String = "",
    val position: String = "Forward",
    val teamName: String = "Senior Team",
    val matchesPlayed: Int = 10,
    val minutesPlayed: Int = 850,
    val goals: Int = 6,
    val assists: Int = 4,
    val averageRating: Double = 7.8,
    val passAccuracy: Int = 84,
    val tacklesWon: Int = 18,
    val cleanSheets: Int = 0,
    val yellowCards: Int = 1,
    val redCards: Int = 0,
    val readinessStatus: String = "Match Ready", // Match Ready, Rotation, Load Managed, Injured, Suspended
    val recentRatings: List<Double> = emptyList(),
    val coachEvaluation: String = "Consistent form, excellent positional awareness and pressing.",
    val evaluatedBy: String = "Head Coach",
    val updatedAt: String = "2026-09-30"
)

data class ClubFitnessAssessmentModel(
    val id: String = "",
    val clubId: String = "",
    val athleteId: String = "",
    val athleteName: String = "",
    val teamName: String = "Senior Team",
    val testDate: String = "2026-09-28",
    val sprintSpeedKmH: Double = 33.5,
    val vo2Max: Double = 58.0,
    val verticalJumpCm: Double = 62.0,
    val yoyoTestLevel: String = "Level 20.4",
    val bodyFatPercent: Double = 9.8,
    val acwrRatio: Double = 1.05,
    val overallFitnessGrade: String = "A+",
    val trainerNotes: String = "High anaerobic capacity and quick recovery times."
)

data class ClubDevelopmentPlanModel(
    val id: String = "",
    val clubId: String = "",
    val athleteId: String = "",
    val athleteName: String = "",
    val currentTeam: String = "U17 Academy",
    val pathwayTarget: String = "Senior Team", // U17 -> U20 -> U23 -> Senior Team -> Pro
    val progressPercent: Int = 65,
    val promotionReadiness: String = "High", // High, Moderate, Developing
    val primarySkillGoal: String = "Tactical Awareness & First Touch",
    val targetPosition: String = "Attacking Midfielder",
    val coachMentor: String = "Head Academy Coach",
    val milestonesCount: Int = 5,
    val completedMilestones: Int = 3,
    val coachAssessment: String = "Excellent vision, ready for U20 transition next window.",
    val lastReviewDate: String = "2026-09-30"
)

data class ClubRecruitmentPipelineModel(
    val id: String = "",
    val clubId: String = "",
    val candidateName: String = "",
    val position: String = "Defensive Midfielder (DM)",
    val currentClub: String = "Regional Academy",
    val age: Int = 18,
    val preferredFoot: String = "Left",
    val stage: String = "Trial", // Discovered, Shortlist, Scout Eval, Club Review, Trial, Decision, Offer, Registered
    val scoutName: String = "Chief Regional Scout",
    val scoutRating: Double = 8.4,
    val evaluationNotes: String = "Dominant physical presence, high passing range under pressure.",
    val trialDate: String = "2026-10-10",
    val decisionStatus: String = "Pending Trial",
    val targetTeam: String = "Senior Team / U23"
)

data class ClubScoutAssignmentModel(
    val id: String = "",
    val clubId: String = "",
    val scoutName: String = "",
    val projectTitle: String = "U20 Defensive Midfielder Search",
    val targetPosition: String = "Defensive Midfielder (DM)",
    val ageRange: String = "17–20",
    val territory: String = "Western Region & National High Schools",
    val specialization: String = "Defensive Profiles & Academy Prospects",
    val athletesDiscovered: Int = 73,
    val shortlisted: Int = 18,
    val trialsInvited: Int = 5,
    val status: String = "Active",
    val deadline: String = "2026-11-15"
)

data class ClubAnalystProjectModel(
    val id: String = "",
    val clubId: String = "",
    val analystName: String = "",
    val projectTitle: String = "Opponent Tactical Breakdown & Set Pieces",
    val category: String = "Matches", // Matches, Training, Workload, Availability, Development, Competitions
    val targetTeam: String = "Senior Team",
    val competition: String = "Premier League",
    val status: String = "Completed", // In Progress, Completed, Review
    val keyFindings: String = "Identified vulnerability on wide transitions; recommended 4-3-3 high press.",
    val deliveryDate: String = "2026-09-30"
)

data class ClubAvailabilityStatusModel(
    val id: String = "",
    val clubId: String = "",
    val athleteId: String = "",
    val athleteName: String = "",
    val teamName: String = "Senior Team",
    val position: String = "Forward",
    val status: String = "Available", // Available, Unavailable, Returning, Restricted, Participation Limitation
    val limitationNotes: String = "Full training participation authorized.",
    val expectedReturnDate: String = "Immediate",
    val riskLevel: String = "Low", // Low, Moderate, High
    val protectionProtocol: String = "Standard Active Monitoring",
    val updatedBy: String = "Club Physio / Performance Lead",
    val updatedAt: String = "2026-09-30"
)

data class ClubFinancialRecord(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val type: String = "Income", // Income, Expense
    val category: String = "Membership", // Membership, Registration Fee, Equipment, Travel, Match Fee
    val amount: String = "KES 50,000",
    val date: String = "2026-09-30",
    val status: String = "Paid",
    val counterparty: String = "Federation / Sponsor"
)

data class ClubCalendarEventModel(
    val id: String = "",
    val clubId: String = "",
    val title: String = "",
    val eventType: String = "Matches", // Matches, Training, Trials, Scouting, Meetings, Registration Deadlines, Reviews, Expirations, Club Events
    val date: String = "2026-10-05",
    val time: String = "15:00",
    val targetTeam: String = "Senior Team",
    val venue: String = "Main Stadium",
    val notes: String = ""
)
