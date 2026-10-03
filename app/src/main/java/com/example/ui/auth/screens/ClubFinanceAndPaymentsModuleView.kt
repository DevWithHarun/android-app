package com.example.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.ClubDashboardUiState
import com.example.ui.ClubDashboardViewModel

data class ClubInsurancePolicyModel(
    val policyId: String,
    val title: String,
    val underwriter: String,
    val coverageType: String, // Medical & Career Injury, Tournament Travel, Equipment & Facility
    val coverageAmount: String,
    val monthlyPremium: String,
    val status: String, // Active, Underwriting, Claim in Progress
    val claimsCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubFinanceAndPaymentsModuleView(
    clubDashboardViewModel: ClubDashboardViewModel,
    clubDashboardState: ClubDashboardUiState,
    activeClubName: String
) {
    val primaryColor = Color(0xFF0F172A)
    val purpleAccent = Color(0xFF7E22CE)
    val tealAccent = Color(0xFF0D9488)
    val successColor = Color(0xFF16A34A)
    val warningColor = Color(0xFFD97706)
    val dangerColor = Color(0xFFDC2626)
    val borderColor = Color(0xFFE2E8F0)
    val textMuted = Color(0xFF64748B)

    var selectedTab by rememberSaveable { mutableStateOf("Ledger & Payments") }
    val tabs = listOf("Ledger & Payments", "Protection & Insurance (Stage D)")

    var showNewTransactionDialog by remember { mutableStateOf(false) }
    var showMpesaStkDialog by remember { mutableStateOf(false) }

    val backendTransactions = (clubDashboardState as? ClubDashboardUiState.Success)?.financials ?: emptyList()
    val transactions = backendTransactions

    val insurancePolicies = remember {
        listOf(
            ClubInsurancePolicyModel("POL-9921", "Squad Career & Acute Injury Protection", "East Africa Sports Underwriters", "Comprehensive Medical & Rehabilitation", "KES 5,000,000 per athlete", "KES 45,000 / mo", "Active", 1),
            ClubInsurancePolicyModel("POL-4412", "Inter-County Matchday Travel Cover", "AIG Regional Underwriting", "Travel & Transit Liability", "KES 2,000,000 per trip", "KES 15,000 / mo", "Active", 0)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFECFDF5),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = successColor, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Finance, Payments & Protection",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryColor
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Organizational Treasury, Payroll & Stage D Protection for $activeClubName",
                                fontSize = 12.sp,
                                color = textMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { showMpesaStkDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("M-Pesa STK (KES 15k)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showNewTransactionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Payment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Treasury Summary Row
                    val totalIncome = transactions.filter { it.type.equals("Income", ignoreCase = true) }
                        .sumOf { tx ->
                            tx.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 0.0
                        }
                    val totalExpense = transactions.filter { it.type.equals("Expense", ignoreCase = true) }
                        .sumOf { tx ->
                            tx.amount.replace("[^0-9]".toRegex(), "").toDoubleOrNull() ?: 0.0
                        }

                    val incomeDisplay = if (totalIncome > 0) "KES %,.0f".format(totalIncome) else "KES 720,000"
                    val expenseDisplay = if (totalExpense > 0) "KES %,.0f".format(totalExpense) else "KES 885,000"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FinanceKpi("Total Income", incomeDisplay, successColor, Modifier.weight(1f))
                        FinanceKpi("Total Expense", expenseDisplay, dangerColor, Modifier.weight(1f))
                        FinanceKpi("Insured Assets", "KES 7.0M Cover", purpleAccent, Modifier.weight(1f))
                    }
                }
            }
        }

        // Sub Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0),
                containerColor = Color.White,
                contentColor = primaryColor,
                edgePadding = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            ) {
                tabs.forEach { t ->
                    val isSelected = selectedTab == t
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = t },
                        text = {
                            Text(
                                text = t,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) primaryColor else textMuted
                            )
                        }
                    )
                }
            }
        }

        if (selectedTab == "Ledger & Payments") {
            items(transactions) { tx ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tx.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("${tx.category} • Counterparty: ${tx.counterparty}", fontSize = 11.sp, color = textMuted)
                            }

                            Text(
                                text = (if (tx.type == "Income") "+ " else "- ") + tx.amount,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (tx.type == "Income") successColor else dangerColor
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFF8FAFC))
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Date: ${tx.date}", fontSize = 10.sp, color = textMuted)
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFECFDF5)) {
                                Text(tx.status.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                            }
                        }
                    }
                }
            }
        } else {
            // Stage D: Protection & Insurance Policies
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = purpleAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("STAGE D: ATHLETE PROTECTION & INSURANCE INFRASTRUCTURE", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF581C87))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Talent Graph preserves verified career telemetry to automate underwritten injury protections, claim validations, and welfare payouts.",
                            fontSize = 11.sp,
                            color = Color(0xFF6B21A8),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            items(insurancePolicies) { pol ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pol.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("Underwriter: ${pol.underwriter} • Policy #${pol.policyId}", fontSize = 11.sp, color = textMuted)
                            }
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFDCFCE7)) {
                                Text(pol.status.uppercase(), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coverage Limit: ${pol.coverageAmount}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                                Text("Premium: ${pol.monthlyPremium}", fontSize = 11.sp, color = textMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG: Log Payment
    if (showNewTransactionDialog) {
        var txTitle by remember { mutableStateOf("") }
        var txAmount by remember { mutableStateOf("KES ") }
        var txType by remember { mutableStateOf("Expense") }
        var txCategory by remember { mutableStateOf("Registration Fee") }
        var counterparty by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showNewTransactionDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Log Financial Record", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        IconButton(onClick = { showNewTransactionDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    OutlinedTextField(value = txTitle, onValueChange = { txTitle = it }, label = { Text("Transaction Description") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = txAmount, onValueChange = { txAmount = it }, label = { Text("Amount (KES)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = txType == "Expense", onClick = { txType = "Expense" }, label = { Text("Expense (Outflow)") }, modifier = Modifier.weight(1f))
                        FilterChip(selected = txType == "Income", onClick = { txType = "Income" }, label = { Text("Income (Inflow)") }, modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(value = counterparty, onValueChange = { counterparty = it }, label = { Text("Counterparty / Beneficiary") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    Button(
                        onClick = {
                            if (txTitle.isNotBlank()) {
                                clubDashboardViewModel.recordFinancialTransaction(
                                    title = txTitle.trim(),
                                    type = txType,
                                    category = txCategory,
                                    amount = txAmount.trim(),
                                    counterparty = counterparty.trim().ifBlank { "General" }
                                )
                                showNewTransactionDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("Save Record to Treasury", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIALOG: Safaricom M-Pesa STK Push Simulator (Matching blueprint)
    if (showMpesaStkDialog) {
        var phone by remember { mutableStateOf("+254 712 345 678") }
        var stkStep by remember { mutableStateOf("INPUT") } // INPUT, PIN, SUCCESS
        var stkPin by remember { mutableStateOf("") }
        var generatedReceipt by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showMpesaStkDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF059669), modifier = Modifier.size(28.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("M", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Safaricom M-Pesa STK Push", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                Text("Talent Graph Club Operating License", fontSize = 10.sp, color = textMuted)
                            }
                        }
                        IconButton(onClick = { showMpesaStkDialog = false }) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    }

                    when (stkStep) {
                        "INPUT" -> {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFDF5), border = BorderStroke(1.dp, Color(0xFFA7F3D0)), modifier = Modifier.fillMaxWidth()) {
                                Text("An instant SIM prompt will be sent to the administrator's phone to authorize billing.", fontSize = 11.sp, color = Color(0xFF047857), modifier = Modifier.padding(10.dp))
                            }

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("M-Pesa Registered Mobile Number *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF8FAFC), border = BorderStroke(1.dp, borderColor), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Subscription:", fontSize = 11.sp, color = textMuted)
                                        Text("Talent Graph Pro Club Tier 1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Paybill Business #:", fontSize = 11.sp, color = textMuted)
                                        Text("892341 (Talent Graph Org)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Amount to Pay:", fontSize = 11.sp, color = textMuted)
                                        Text("KES 15,000", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF059669))
                                    }
                                }
                            }

                            Button(
                                onClick = { stkStep = "PIN" },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Send M-Pesa STK Prompt", fontWeight = FontWeight.Bold)
                            }
                        }
                        "PIN" -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF0F172A)).padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("SIM TOOLKIT STK PROMPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), letterSpacing = 1.sp)
                                Text("Pay KES 15,000 to Talent Graph Org Paybill 892341 for MUFC-2026-HQ?", fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)

                                OutlinedTextField(
                                    value = stkPin,
                                    onValueChange = { if (it.length <= 4) stkPin = it },
                                    label = { Text("Enter 4-Digit M-Pesa PIN", color = Color(0xFF94A3B8)) },
                                    placeholder = { Text("••••", color = Color(0xFF64748B)) },
                                    modifier = Modifier.width(200.dp),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.White),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        if (stkPin.length == 4) {
                                            generatedReceipt = "SK${(10000000..99999999).random()}"
                                            clubDashboardViewModel.recordFinancialTransaction(
                                                title = "M-Pesa STK License Renewal (KES 15,000)",
                                                type = "Expense",
                                                category = "Federation & Platform License",
                                                amount = "KES 15,000",
                                                counterparty = "Talent Graph Org (Receipt #$generatedReceipt)"
                                            )
                                            stkStep = "SUCCESS"
                                        }
                                    },
                                    enabled = stkPin.length == 4,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Authorize Payment", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(48.dp))
                                Text("Payment Authorized Successfully!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = primaryColor)
                                Text("Receipt: $generatedReceipt", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF059669))
                                Text("Club operating license extended by 30 days. Ledger updated.", fontSize = 11.sp, color = textMuted)

                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { showMpesaStkDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Done", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FinanceKpi(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color, maxLines = 1)
            Text(label, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
