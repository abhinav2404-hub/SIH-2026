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
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
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
                title = "Fair Price & Weight Tools",
                subtitle = "Easy Calculators for Price per 100g, Weight & Fines",
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

            // Tool 2: Net Weight Tolerance & Error (MPE) (State isolated)
            item {
                NetQuantityToleranceCard()
            }

            // Tool 3: Principal Display Panel Font Height Verifier (State isolated)
            item {
                ScheduleFontHeightCard()
            }

            // Tool 4: Statutory Penalty & Compounding Estimator (State isolated)
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
                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = MetrologyNavy)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Unit Sale Price (USP) • Price Per Unit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Rule 6(11): Compare cost per 100g or 100ml. Tells you if the big family pack is really cheaper.",
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
                    placeholder = { Text("e.g. 185.00") },
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
                    placeholder = { Text("e.g. 1000") },
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mandatory Label Declaration (Rule 6(11)):",
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Everyday Tip: By law, packages must print this price-per-unit so shoppers know the real value before paying.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF14532D),
                            fontSize = 11.sp
                        )
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
                    Icon(Icons.Default.ZoomIn, contentDescription = null, tint = MetrologyNavy)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Label & Font Size Verifier (Schedule II)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Rule 9: Check if printed price, weight & expiry numbers are large enough to read easily.",
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
                label = { Text("Pack Net Quantity (Weight in g or Volume in ml)", fontWeight = AppFontWeights.Body) },
                placeholder = { Text("e.g. 450") },
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Up to 200g: Min 2.0 mm (Small packs)\n• 200g to 500g: Min 4.0 mm (Medium boxes)\n• Above 500g: Min 6.0 mm (Large family packs)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            fontSize = 11.sp,
                            color = Color(0xFF1E40AF)
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
                else -> "₹ 1,00,000 + Imprisonment up to 1 Year (Section 36(2) Repeated Offense)"
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
                        text = "Section 36 Penalty & Fine Estimator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "Legal Metrology Act, 2009: Fines for missing MRP, wrong weight, smudged dates, or overcharging.",
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
                            "1st Offense (First Time)",
                            fontWeight = if (offenseType == 0) AppFontWeights.Subheader else AppFontWeights.Body
                        )
                    }
                )
                FilterChip(
                    selected = offenseType == 1,
                    onClick = { offenseType = 1 },
                    label = {
                        Text(
                            "2nd Offense (Second Time)",
                            fontWeight = if (offenseType == 1) AppFontWeights.Subheader else AppFontWeights.Body
                        )
                    }
                )
                FilterChip(
                    selected = offenseType == 2,
                    onClick = { offenseType = 2 },
                    label = {
                        Text(
                            "Repeated (Repeat Offender)",
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
                        text = "Statutory Fine / Compounding Amount:",
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Legal Protection: Selling non-standard packages without mandatory details or overcharging above printed MRP is illegal under Section 36.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7F1D1D),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Isolated Component: Net Quantity Weight Accuracy & Tolerance (MPE)
 * PCR 2011 First Schedule (Maximum Permissible Error)
 * Tells consumers and inspectors whether a packet is illegally short-weight.
 */
@Composable
fun NetQuantityToleranceCard() {
    var declaredQtyInput by remember { mutableStateOf("500") }
    var actualMeasuredInput by remember { mutableStateOf("488") }

    val declared = declaredQtyInput.toDoubleOrNull() ?: 500.0
    val actual = actualMeasuredInput.toDoubleOrNull() ?: 488.0

    val mpeGrams by remember(declared) {
        derivedStateOf {
            when {
                declared <= 50.0 -> declared * 0.09
                declared <= 100.0 -> 4.5
                declared <= 200.0 -> declared * 0.045
                declared <= 300.0 -> 9.0
                declared <= 500.0 -> declared * 0.03
                declared <= 1000.0 -> 15.0
                declared <= 10000.0 -> declared * 0.015
                declared <= 15000.0 -> 150.0
                else -> declared * 0.01
            }
        }
    }

    val minLegalWeight by remember(declared, mpeGrams) {
        derivedStateOf { (declared - mpeGrams).coerceAtLeast(0.0) }
    }
    val shortfall by remember(declared, actual) {
        derivedStateOf { declared - actual }
    }
    val isShortWeight by remember(actual, minLegalWeight) {
        derivedStateOf { actual < minLegalWeight }
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
                        text = "Net Weight Accuracy & Short-Weight Check",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header
                        )
                    )
                    Text(
                        text = "First Schedule (MPE): Tells you if a packet has less weight than what's printed on the box.",
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
                    value = declaredQtyInput,
                    onValueChange = { declaredQtyInput = it },
                    label = { Text("Printed Weight (g/ml)", fontWeight = AppFontWeights.Body) },
                    placeholder = { Text("e.g. 500") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_mpe_declared"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = actualMeasuredInput,
                    onValueChange = { actualMeasuredInput = it },
                    label = { Text("Actual Weight on Scale", fontWeight = AppFontWeights.Body) },
                    placeholder = { Text("e.g. 488") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_mpe_measured"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isShortWeight) Color(0xFFFEF2F2) else CompliancePassContainer,
                border = BorderStroke(1.dp, if (isShortWeight) Color(0xFFFECACA) else Color(0xFF86EFAC))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isShortWeight) "❌ ILLEGAL SHORT-WEIGHT DETECTED" else "✅ LEGAL & ACCURATE WEIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isShortWeight) Color(0xFF991B1B) else Color(0xFF0F5A07),
                                    fontWeight = AppFontWeights.Header
                                )
                            )
                            Text(
                                text = "Minimum Legal Weight: ${String.format(java.util.Locale.US, "%.1f", minLegalWeight)} g",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = AppFontWeights.Header,
                                    color = if (isShortWeight) Color(0xFF991B1B) else Color(0xFF0F5A07)
                                )
                            )
                        }

                        Icon(
                            imageVector = if (isShortWeight) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isShortWeight) Color(0xFFDC2626) else CompliancePass,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isShortWeight) {
                            "⚠️ Short by ${String.format(java.util.Locale.US, "%.1f", shortfall)}g! The law allows a maximum moisture/packing variation of only ${String.format(java.util.Locale.US, "%.1f", mpeGrams)}g. This pack is short-weight under Section 30."
                        } else {
                            "Deficit is ${String.format(java.util.Locale.US, "%.1f", shortfall.coerceAtLeast(0.0))}g, which is safely within the legal moisture allowance of ±${String.format(java.util.Locale.US, "%.1f", mpeGrams)}g."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isShortWeight) Color(0xFF7F1D1D) else Color(0xFF14532D),
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💡 Everyday Tip: Companies cannot sell packets lighter than the minimum legal weight. If a packet is short-weight, the consumer is entitled to a replacement or refund.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
