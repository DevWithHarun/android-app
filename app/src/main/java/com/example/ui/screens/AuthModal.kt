package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.auth.UserRole
import com.example.ui.auth.AuthNavGraph
import com.example.ui.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onAuthenticate: (String, UserRole) -> Unit
) {
    if (!isOpen) return

    val authViewModel: AuthViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF020617),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AuthNavGraph(
                    viewModel = authViewModel,
                    onAuthComplete = { role ->
                        val email = FirebaseAuth.getInstance().currentUser?.email ?: "user@talentgraph.com"
                        onAuthenticate(email, role)
                        authViewModel.resetState()
                        onDismiss()
                    }
                )

                IconButton(
                    onClick = {
                        authViewModel.resetState()
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }
}
