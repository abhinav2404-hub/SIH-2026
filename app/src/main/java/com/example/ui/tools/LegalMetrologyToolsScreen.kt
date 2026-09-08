package com.example.ui.tools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AppFontWeights
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.CompliancePassContainer
import com.example.ui.theme.MetrologyNavy

@Composable
fun LegalMetrologyToolsScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Legal Metrology Utilities",
                subtitle = "Statutory Calculators & Standards",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                currentUser = currentUser
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.TOOLS,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tool 1: Unit Sale Price (USP) Calculator (State isolated)
            item {
                UspCalculatorCard()
            }

            // Tool 2: Principal Display Panel Font Height Verifier (State isolated)
            item {
                ScheduleFontHeightCard()
            }

            // Tool 3: Statutory Penalty & Compounding Estimator (State isolated)
            item {
                PenaltyEstimatorCard()
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Isolated Component: Unit Sale Price (USP) Calculator
 * State is moved down to this component so typing here does not trigger
 * re-renders/recompositions in other tools on the screen.
 * Calculations are memoized using derivedStateOf.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UspCalculatorCard() {
    var mrpInput by remember { mutableStateOf("185.00") }
    var qtyInput by remember { mutableStateOf("1000") }
    var selectedUnit by remember { mutableStateOf("ml") }

    val calculatedUsp by remember {
        derivedStateOf {
            val mrp = mrpInput.toDoubleOrNull() ?: 0.0
            val qty = qtyInput.toDoubleOrNull() ?: 1.0
            if (qty > 0) mrp / qty else 0.0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, tint = MetrologyNavy)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Unit Sale Price (USP) Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Rule 6(1)(e) - Mandatory per unit pricing",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = mrpInput,
                    onValueChange = { mrpInput = it },
                    label = { Text("Declared MRP (₹)", fontWeight = AppFontWeights.Body) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_usp_mrp"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = qtyInput,
                    onValueChange = { qtyInput = it },
                    label = { Text("Net Quantity", fontWeight = AppFontWeights.Body) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_usp_qty"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // FlowRow wraps chips gracefully on 375px / 390px mobile viewports (Zero horizontal overflow)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("g", "ml", "kg", "L", "piece").forEach { unit ->
                    FilterChip(
                        selected = selectedUnit == unit,
                        onClick = { selectedUnit = unit },
                        label = {
                            Text(
                                text = "per $unit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedUnit == unit) AppFontWeights.Subheader else AppFontWeights.Body
                                )
                            )
                        },
                        modifier = Modifier.testTag("chip_unit_$unit")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Output Result Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CompliancePassContainer,
                border = BorderStroke(1.dp, Color(0xFF86EFAC))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mandatory Label Declaration:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF0F5A07),
                                fontWeight = AppFontWeights.Header
                            )
                        )
                        Text(
                            text = "₹ ${String.format(java.util.Locale.US, "%.3f", calculatedUsp)} / $selectedUnit",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = AppFontWeights.Header,
                                color = Color(0xFF0F5A07)
                            )
                        )
                    }

                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CompliancePass,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

/**
 * Isolated Component: Principal Display Panel Font Height Verifier
 * State is isolated to eliminate unnecessary re-renders of sibling cards.
 * Calculations are memoized using derivedStateOf.
 */
@Composable
fun ScheduleFontHeightCard() {
    var fontPackWeight by remember { mutableStateOf("450") }

    val requiredFontHeightMm by remember {
        derivedStateOf {
            val weightVal = fontPackWeight.toDoubleOrNull() ?: 0.0
            when {
                weightVal <= 200.0 -> 2.0
                weightVal <= 500.0 -> 4.0
                else -> 6.0
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TextFormat, contentDescription = null, tint = MetrologyNavy)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Schedule II Font Height Verifier",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Rule 9 & Schedule II Legibility Thresholds",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = fontPackWeight,
                onValueChange = { fontPackWeight = it },
                label = { Text("Pack Net Quantity (in g or ml)", fontWeight = AppFontWeights.Body) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_font_pack_weight"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Statutory Minimum Numeral Height:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF1E40AF),
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "$requiredFontHeightMm mm Minimum Height",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MetrologyNavy
                        )
                    )
                    Text(
                        text = "Under Schedule II: <=200g (min 2.0mm), 200-500g (min 4.0mm), >500g (min 6.0mm)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            fontSize = 10.sp,
                            color = Color(0xFF3B82F6)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Isolated Component: Statutory Penalty & Compounding Estimator
 * State is isolated and calculations memoized.
 * Chips are kept in a responsive wrapping layout.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PenaltyEstimatorCard() {
    var offenseType by remember { mutableIntStateOf(0) }

    val penaltyAmount by remember {
        derivedStateOf {
            when (offenseType) {
                0 -> "₹ 25,000 (Section 36(1) First Offense)"
                1 -> "₹ 50,000 (Section 36(1) Second Offense)"
                else -> "₹ 1,00,000 + Imprisonment up to 1 Year (Section 36(2) Repeated)"
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MetrologyNavy)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Section 36 Penalty Estimator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Legal Metrology Act, 2009 Offense Provisions",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = offenseType == 0,
                    onClick = { offenseType = 0 },
                    label = {
                        Text(
                            "1st Offense",
                            fontWeight = if (offenseType == 0) AppFontWeights.Subheader else AppFontWeights.Body
                        )
                    }
                )
                FilterChip(
                    selected = offenseType == 1,
                    onClick = { offenseType = 1 },
                    label = {
                        Text(
                            "2nd Offense",
                            fontWeight = if (offenseType == 1) AppFontWeights.Subheader else AppFontWeights.Body
                        )
                    }
                )
                FilterChip(
                    selected = offenseType == 2,
                    onClick = { offenseType = 2 },
                    label = {
                        Text(
                            "Repeated",
                            fontWeight = if (offenseType == 2) AppFontWeights.Subheader else AppFontWeights.Body
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFECACA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Compounding / Penalty Amount:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF991B1B),
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = penaltyAmount,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = Color(0xFF991B1B)
                        )
                    )
                }
            }
        }
    }
}
