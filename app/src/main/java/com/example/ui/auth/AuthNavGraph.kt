package com.example.ui.auth

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.auth.UserRole
import com.example.ui.auth.screens.*

@Composable
fun AuthNavGraph(
    viewModel: AuthViewModel,
    onAuthComplete: (UserRole) -> Unit = {}
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    var signupEmail by remember { mutableStateOf("") }
    var chosenRole by remember { mutableStateOf(UserRole.ATHLETE) }

    var isHandled by remember { mutableStateOf(false) }

    // Check initial auth state on start
    LaunchedEffect(Unit) {
        viewModel.checkInitialAuthState()
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success && !isHandled) {
            isHandled = true
            val role = (uiState as AuthUiState.Success).role
            onAuthComplete(role)
        } else if (uiState !is AuthUiState.Success) {
            isHandled = false
        }
    }

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToSignup = { navController.navigate("signup_email") }
            )
        }
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = { role ->
                    onAuthComplete(role)
                }
            )
        }
        composable("signup_email") {
            SignupEmailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCodeSent = { email ->
                    signupEmail = email
                    navController.navigate("signup_code?email=$email")
                }
            )
        }
        composable(
            route = "signup_code?email={email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("email") ?: signupEmail
            SignupCodeScreen(
                viewModel = viewModel,
                email = emailArg,
                onBack = { navController.popBackStack() },
                onCodeVerified = {
                    navController.navigate("role_selection")
                }
            )
        }
        composable("role_selection") {
            RoleSelectionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRoleSelected = { role ->
                    chosenRole = role
                    navController.navigate("signup_password")
                }
            )
        }
        composable("signup_password") {
            SignupPasswordScreen(
                viewModel = viewModel,
                email = signupEmail,
                role = chosenRole,
                onBack = { navController.popBackStack() },
                onSignupSuccess = { role ->
                    onAuthComplete(role)
                }
            )
        }
    }
}
