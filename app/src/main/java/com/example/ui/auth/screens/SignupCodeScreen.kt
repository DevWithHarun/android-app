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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.auth.AuthUiState
import com.example.ui.auth.AuthViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupCodeScreen(
    viewModel: AuthViewModel,
    email: String,
    onBack: () -> Unit,
    onCodeVerified: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(0) }
    var cooldown by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateBorder = Color(0xFF334155)

    LaunchedEffect(cooldown) {
        if (cooldown > 0) {
            delay(1000L)
            cooldown--
        } else {
            canResend = true
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.EmailVerified) {
            onCodeVerified()
        }
    }

    Scaffold(
        containerColor = slateBg,
        topBar = {
            TopAppBar(
                title = { Text("Verify Email", color = Color.White) },
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
                text = "Enter 6-Digit Code",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sent to $email. (Tip: Enter any 6 digits for testing)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
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
                value = code,
                onValueChange = { if (it.length <= 6) code = it },
                label = { Text("Verification Code") },
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        if (canResend && attempts < 5) {
                            attempts++
                            cooldown = 30
                            canResend = false
                            viewModel.sendVerificationCode(email)
                        }
                    },
                    enabled = canResend && attempts < 5
                ) {
                    Text(
                        text = if (cooldown > 0) "Resend code in ${cooldown}s" else "Resend Code",
                        color = if (canResend) amberColor else Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "Attempts: $attempts/5",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (attempts >= 5) {
                        return@Button
                    }
                    viewModel.verifyCode(code)
                },
                enabled = uiState !is AuthUiState.Loading && code.length == 6 && attempts < 5,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                shape = RoundedCornerShape(50.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text("Verify Code", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
