package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Feed : Screen("feed")
    object Athletes : Screen("athletes")
    object Discovery : Screen("discovery")
    object Support : Screen("support")
    object AthleteProfile : Screen("athlete_profile")
    object ClubCoach : Screen("club_coach")
}
