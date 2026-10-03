package com.example.ui.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.UserRole
import com.example.ui.auth.AuthUiState
import com.example.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupPasswordScreen(
    viewModel: AuthViewModel,
    email: String,
    role: UserRole,
    onBack: () -> Unit,
    onSignupSuccess: (UserRole) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateBorder = Color(0xFF334155)

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onSignupSuccess((uiState as AuthUiState.Success).role)
        }
    }

    Scaffold(
        containerColor = slateBg,
        topBar = {
            TopAppBar(
                title = { Text("Create Password", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = slateBg)
            )
        }
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Secure Your Account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Role: ${role.value.replace('_', ' ').replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodyMedium,
                color = amberColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState is AuthUiState.Error) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = amberColor,
                    unfocusedBorderColor = slateBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = amberColor,
                    unfocusedBorderColor = slateBorder
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (password != confirmPassword) {
                        return@Button
                    }
                    viewModel.signUp(email, password, role)
                },
                enabled = uiState !is AuthUiState.Loading && password.length >= 6 && password == confirmPassword,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                shape = RoundedCornerShape(50.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("Complete Registration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
