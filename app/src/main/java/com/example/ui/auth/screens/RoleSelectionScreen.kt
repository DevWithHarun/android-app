package com.example.ui.auth.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.UserRole
import com.example.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onRoleSelected: (UserRole) -> Unit
) {
    // Platform admin MUST NEVER appear as a choice
    val roles = listOf(
        UserRole.ATHLETE to "Athlete (Player / Prospect)",
        UserRole.COACH to "Coach / Trainer",
        UserRole.ANALYST to "Analyst / Performance Data",
        UserRole.SCOUT to "Scout / Talent Recruiter",
        UserRole.CLUB_ADMIN to "Club Administrator / Academy"
    )

    var selected by remember { mutableStateOf(UserRole.ATHLETE) }

    val amberColor = Color(0xFFFBBF24)
    val slateBg = Color(0xFF020617)
    val slateSurface = Color(0xFF0F172A)
    val slateBorder = Color(0xFF334155)

    Scaffold(
        containerColor = slateBg,
        topBar = {
            TopAppBar(
                title = { Text("Select Your Role", color = Color.White) },
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "How will you use Talent Graph?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select your primary role in African football ecosystem.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(roles) { (role, label) ->
                        val isSelected = selected == role
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = slateSurface),
                            border = BorderStroke(1.dp, if (isSelected) amberColor else slateBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected = role }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isSelected) amberColor else Color.White
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = amberColor)
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.setSelectedRole(selected)
                    onRoleSelected(selected)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = amberColor, contentColor = Color.Black),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
