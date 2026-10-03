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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.AuthUiState
import com.example.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupEmailScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onCodeSent: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateBorder = Color(0xFF334155)

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.CodeSent) {
            onCodeSent(email)
        }
    }

    Scaffold(
        containerColor = slateBg,
        topBar = {
            TopAppBar(
                title = { Text("Get Started", color = Color.White) },
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
                text = "Enter Your Email",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "We will send a 6-digit verification code to confirm your email.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
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
                onClick = { viewModel.sendVerificationCode(email) },
                enabled = uiState !is AuthUiState.Loading && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                shape = RoundedCornerShape(50.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
