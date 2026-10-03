package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.data.AthleteEntity
import com.example.data.auth.UserRole
import com.example.ui.TalentViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.auth.screens.*
import com.example.ui.theme.MyApplicationTheme

object DashboardRoutes {
    const val ATHLETE = "athlete_dashboard"
    const val COACH = "coach_dashboard"
    const val ANALYST = "analyst_dashboard"
    const val SCOUT = "scout_dashboard"
    const val CLUB_ADMIN = "club_admin_dashboard"
    const val PLATFORM_ADMIN = "platform_admin_dashboard"
    const val PUBLIC_HOME = "public_home"
    const val FEED = "feed_dashboard"
}

class MainActivity : ComponentActivity() {
    private val viewModel: TalentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var currentRoute by remember { mutableStateOf<String>(Screen.Home.route) }

                val navigateToUserDashboard: () -> Unit = {
                    if (uiState.isAuthenticated) {
                        val destination = when (uiState.userRole) {
                            UserRole.ATHLETE -> DashboardRoutes.ATHLETE
                            UserRole.COACH -> DashboardRoutes.COACH
                            UserRole.ANALYST -> DashboardRoutes.ANALYST
                            UserRole.SCOUT -> DashboardRoutes.SCOUT
                            UserRole.CLUB_ADMIN -> DashboardRoutes.CLUB_ADMIN
                            UserRole.PLATFORM_ADMIN -> DashboardRoutes.PLATFORM_ADMIN
                            UserRole.NONE -> DashboardRoutes.ATHLETE
                        }
                        navController.navigate(destination) {
                            launchSingleTop = true
                        }
                    } else {
                        viewModel.setAuthModalOpen(true)
                    }
                }

                // Both unauthenticated and authenticated users access Feed Dashboard without login
                val startDest = DashboardRoutes.FEED

                NavHost(navController = navController, startDestination = startDest) {
                    // Central Feed Dashboard
                    composable(DashboardRoutes.FEED) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            FeedScreen(
                                uiState = uiState,
                                onNavigateHome = {
                                    navController.navigate(DashboardRoutes.PUBLIC_HOME) {
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToDashboard = navigateToUserDashboard,
                                onOpenAuth = { viewModel.setAuthModalOpen(true) }
                            )

                            AuthModal(
                                isOpen = uiState.isAuthModalOpen,
                                onDismiss = { viewModel.setAuthModalOpen(false) },
                                onAuthenticate = { email, role ->
                                    viewModel.authenticateUser(email, role)
                                }
                            )
                        }
                    }

                    // Role Dashboards
                    val returnToFeed: () -> Unit = {
                        navController.navigate(DashboardRoutes.FEED) {
                            popUpTo(DashboardRoutes.FEED) { inclusive = false }
                            launchSingleTop = true
                        }
                    }

                    composable(DashboardRoutes.ATHLETE) {
                        BackHandler { returnToFeed() }
                        AthleteDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(DashboardRoutes.COACH) {
                        BackHandler { returnToFeed() }
                        CoachDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(DashboardRoutes.ANALYST) {
                        BackHandler { returnToFeed() }
                        AnalystDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(DashboardRoutes.SCOUT) {
                        BackHandler { returnToFeed() }
                        ScoutDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(DashboardRoutes.CLUB_ADMIN) {
                        BackHandler { returnToFeed() }
                        ClubAdminDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(DashboardRoutes.PLATFORM_ADMIN) {
                        BackHandler { returnToFeed() }
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            userName = uiState.userName,
                            onNavigateToFeed = returnToFeed,
                            onSignOut = {
                                viewModel.logout()
                                navController.navigate(DashboardRoutes.FEED) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // Public Landing Page & Feature Exploration Flow
                    composable(DashboardRoutes.PUBLIC_HOME) {
                        val publicNavController = rememberNavController()
                        Box(modifier = Modifier.fillMaxSize()) {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    if (currentRoute != Screen.Feed.route) {
                                        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                                            val items = listOf(
                                                Triple(Screen.Home.route, "Home", Icons.Default.Home),
                                                Triple(Screen.Feed.route, "Feeds", Icons.Default.Article),
                                                Triple(Screen.Athletes.route, "Athletes", Icons.Default.DirectionsRun),
                                                Triple(Screen.Discovery.route, "Discovery", Icons.Default.Explore),
                                                Triple(Screen.Support.route, "Supports", Icons.Default.Headset)
                                            )
                                            items.forEach { (route, label, icon) ->
                                                NavigationBarItem(
                                                    icon = { Icon(icon, contentDescription = label) },
                                                    label = { Text(label) },
                                                    selected = currentRoute == route,
                                                    onClick = {
                                                        if (route == Screen.Feed.route) {
                                                            navController.navigate(DashboardRoutes.FEED) {
                                                                launchSingleTop = true
                                                            }
                                                        } else {
                                                            currentRoute = route
                                                            if (route == Screen.Athletes.route) {
                                                                viewModel.syncAthletesFromRemote()
                                                            }
                                                            publicNavController.navigate(route) {
                                                                popUpTo(publicNavController.graph.findStartDestination().id) {
                                                                    saveState = true
                                                                }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    NavHost(navController = publicNavController, startDestination = Screen.Home.route) {
                                        composable(Screen.Home.route) {
                                            HomeScreen(
                                                onNavigateToScouting = {
                                                    currentRoute = Screen.Athletes.route
                                                    publicNavController.navigate(Screen.Athletes.route)
                                                },
                                                onNavigateToAthleteProfile = {
                                                    if (uiState.athletes.isNotEmpty()) {
                                                        viewModel.setSelectedAthlete(uiState.athletes.first().firestoreId)
                                                        currentRoute = Screen.AthleteProfile.route
                                                        publicNavController.navigate(Screen.AthleteProfile.route)
                                                    } else {
                                                        viewModel.syncAthletesFromRemote()
                                                        currentRoute = Screen.Athletes.route
                                                        publicNavController.navigate(Screen.Athletes.route)
                                                    }
                                                },
                                                onNavigateToClubCoach = {
                                                    currentRoute = Screen.Discovery.route
                                                    publicNavController.navigate(Screen.Discovery.route)
                                                },
                                                onOpenAuth = {
                                                    viewModel.setAuthModalOpen(true)
                                                }
                                            )
                                        }
                                        composable(Screen.Feed.route) {
                                            FeedScreen(
                                                uiState = uiState,
                                                onNavigateHome = {
                                                    currentRoute = Screen.Home.route
                                                    publicNavController.navigate(Screen.Home.route) {
                                                        popUpTo(publicNavController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                onNavigateToDashboard = navigateToUserDashboard,
                                                onOpenAuth = { viewModel.setAuthModalOpen(true) }
                                            )
                                        }
                                        composable(Screen.Athletes.route) {
                                            ScoutingScreen(
                                                athletes = uiState.athletes,
                                                searchQuery = uiState.searchQuery,
                                                onSearchQueryChange = viewModel::setSearchQuery,
                                                selectedSport = uiState.selectedSportFilter,
                                                onSportSelect = viewModel::setSportFilter,
                                                onSelectAthlete = { athleteId ->
                                                    viewModel.setSelectedAthlete(athleteId)
                                                    currentRoute = Screen.AthleteProfile.route
                                                    publicNavController.navigate(Screen.AthleteProfile.route)
                                                },
                                                isLoading = uiState.isLoadingAthletes,
                                                onRefresh = { viewModel.syncAthletesFromRemote() }
                                            )
                                        }
                                        composable(Screen.Discovery.route) {
                                            DiscoveryScreen(
                                                athletes = uiState.athletes,
                                                clubs = uiState.clubs,
                                                onSelectAthlete = { athleteId ->
                                                    viewModel.setSelectedAthlete(athleteId)
                                                    currentRoute = Screen.AthleteProfile.route
                                                    publicNavController.navigate(Screen.AthleteProfile.route)
                                                },
                                                currentUid = uiState.userEmail.ifBlank { uiState.userName.ifBlank { "user_guest" } },
                                                currentUserName = if (uiState.userName.isNotBlank()) uiState.userName else "Verified Athlete"
                                            )
                                        }
                                        composable(Screen.Support.route) {
                                            SupportContactScreen()
                                        }
                                        composable(Screen.AthleteProfile.route) {
                                            val athlete = uiState.athletes.find { it.firestoreId == uiState.selectedAthleteId }
                                                ?: uiState.selectedAthleteId?.let {
                                                    AthleteEntity(firestoreId = it, name = "Athlete", sport = "Football", position = "Player", rating = 80, verifiedStats = "", clubName = "", joinedDate = "")
                                                }
                                                ?: uiState.athletes.firstOrNull()
                                            AthleteProfileScreen(
                                                athlete = athlete,
                                                matchLogs = uiState.matchLogs,
                                                onBack = { publicNavController.popBackStack() },
                                                onAddMatchLog = viewModel::addMatchLog
                                            )
                                        }
                                        composable(Screen.ClubCoach.route) {
                                            ClubCoachScreen(
                                                clubs = uiState.clubs,
                                                onAddClub = viewModel::addClub
                                            )
                                        }
                                    }
                                }
                            }

                            AuthModal(
                                isOpen = uiState.isAuthModalOpen,
                                onDismiss = { viewModel.setAuthModalOpen(false) },
                                onAuthenticate = { email, role ->
                                    viewModel.authenticateUser(email, role)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
