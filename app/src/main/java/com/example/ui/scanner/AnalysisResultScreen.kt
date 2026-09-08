package com.example.ui.scanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RuleCheckResult
import com.example.data.model.ViolationSeverity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AppFontWeights
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.ComplianceFailContainer
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.CompliancePassContainer
import com.example.ui.theme.ComplianceWarning
import com.example.ui.theme.ComplianceWarningContainer
import com.example.ui.theme.MetrologyGold
import com.example.ui.theme.MetrologyNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalysisResultScreen(viewModel: MainViewModel) {
    val record by viewModel.currentInspectionRecord.collectAsState()
    val ruleResults by viewModel.currentRuleResults.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showNoticeDialog by remember { mutableStateOf(false) }

    val statusColor = when (record?.overallStatus) {
        "COMPLIANT" -> CompliancePass
        "MINOR_VIOLATIONS" -> ComplianceWarning
        "NON_COMPLIANT" -> ComplianceFail
        else -> Color(0xFF990000)
    }

    val statusContainer = when (record?.overallStatus) {
        "COMPLIANT" -> CompliancePassContainer
        "MINOR_VIOLATIONS" -> ComplianceWarningContainer
        "NON_COMPLIANT" -> ComplianceFailContainer
        else -> Color(0xFFFFDADA)
    }

    val statusTitle = when (record?.overallStatus) {
        "COMPLIANT" -> "100% STATUTORY COMPLIANCE"
        "MINOR_VIOLATIONS" -> "MINOR NON-COMPLIANCE"
        "NON_COMPLIANT" -> "STATUTORY OFFENSE / VIOLATIONS FLAGGED"
        else -> "SEIZURE & PROSECUTION RECOMMENDED"
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Statutory Audit Report",
                subtitle = "Legal Metrology Rules, 2011 Assessment",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                currentUser = currentUser
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.SCANNER,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { paddingValues ->
        if (record == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No inspection report available.")
            }
            return@Scaffold
        }

        val rec = record!!

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Overall Status Banner & Score Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = statusContainer),
                    border = BorderStroke(1.5.dp, statusColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor
                            ) {
                                Text(
                                    text = statusTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Score: ${rec.complianceScore}/100",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = AppFontWeights.Header,
                                    color = statusColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Score Pill
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(3.dp, statusColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${rec.complianceScore}%",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = AppFontWeights.Header,
                                        color = statusColor
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = rec.productName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Brand: ${rec.brandName} • Category: ${rec.category}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Extracted Package Mandatories Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Extracted Label Declarations (Rule 6)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        MandatoryFieldRow("Declared Net Quantity", rec.netQuantity)
                        MandatoryFieldRow("Declared MRP", rec.declaredMrp)
                        MandatoryFieldRow("Unit Sale Price (USP)", rec.declaredUsp)
                        MandatoryFieldRow("Month & Year of Packing", rec.mfgPackingDate)
                        MandatoryFieldRow("Country of Origin", rec.countryOfOrigin)
                        MandatoryFieldRow("Manufacturer / Packer", rec.manufacturerAddress)
                        MandatoryFieldRow("Consumer Grievance Care", rec.consumerCareContact)
                    }
                }
            }

            // Rule-by-Rule Assessment Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rule-by-Rule Statutory Checklist",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${ruleResults.count { it.isCompliant }}/${ruleResults.size} Compliant",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MetrologyNavy
                        )
                    )
                }
            }

            // Rule Items List
            items(ruleResults) { rule ->
                RuleResultItemCard(rule = rule)
            }

            // Action Buttons Card: Notice Generation & Audit Registry
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MetrologyNavy
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Enforcement Actions & Statutory Notices",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Draft inspection memo under Legal Metrology Act, 2009",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.markNoticeGenerated()
                                    showNoticeDialog = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_generate_notice"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MetrologyGold,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Issue Notice", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.SCANNER) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_scan_another"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Scan Next", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showNoticeDialog && record != null) {
        StatutoryNoticeDialog(
            record = record!!,
            rules = ruleResults,
            onDismiss = { showNoticeDialog = false }
        )
    }
}

@Composable
fun MandatoryFieldRow(label: String, value: String) {
    val isMissing = value.contains("MISSING", ignoreCase = true) || value.contains("Not printed", ignoreCase = true)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isMissing) AppFontWeights.Header else AppFontWeights.Subheader,
                color = if (isMissing) ComplianceFail else MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = BorderLight.copy(alpha = 0.5f))
}

@Composable
fun RuleResultItemCard(rule: RuleCheckResult) {
    var expanded by remember { mutableStateOf(!rule.isCompliant) }

    val ruleColor = if (rule.isCompliant) CompliancePass else when (rule.severity) {
        ViolationSeverity.MINOR -> ComplianceWarning
        ViolationSeverity.MAJOR -> ComplianceFail
        ViolationSeverity.CRITICAL -> Color(0xFF990000)
        else -> CompliancePass
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("rule_card_${rule.ruleId}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, if (!rule.isCompliant) ruleColor.copy(alpha = 0.6f) else BorderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(ruleColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (rule.isCompliant) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = ruleColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rule.ruleName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = rule.ruleReference,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MetrologyNavy,
                            fontWeight = AppFontWeights.Subheader,
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ruleColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (rule.isCompliant) "PASS" else rule.severity.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ruleColor,
                            fontWeight = AppFontWeights.Header,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Detected Value:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = rule.detectedValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Subheader,
                            color = if (rule.isCompliant) MaterialTheme.colorScheme.onSurface else ComplianceFail
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Statutory Requirement:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = rule.requiredStandard,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Legal Assessment & Rule Explanation:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = rule.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    if (!rule.isCompliant) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ComplianceFailContainer
                        ) {
                            Text(
                                text = "⚖ Statutory Penalty: ${rule.legalPenalty}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ComplianceFail,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatutoryNoticeDialog(
    record: com.example.data.model.InspectionRecord,
    rules: List<RuleCheckResult>,
    onDismiss: () -> Unit
) {
    val violations = rules.filter { !it.isCompliant }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = dateFormat.format(Date())

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OFFICIAL STATUTORY NOTICE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MetrologyNavy
                        )
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Under Section 36 of Legal Metrology Act, 2009\nand Rule 32 of Legal Metrology (Packaged Commodities) Rules, 2011",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "MEMO NO: LM/ENF/2026/SEC36/${record.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )
                    Text(
                        text = "Date: $today • Inspector: ${record.inspectorName} (${record.inspectorBadge})",
                        fontSize = 10.sp,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = "Premises: ${record.inspectionLocation}",
                        fontSize = 10.sp,
                        color = Color(0xFF92400E)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "COMMODITY UNDER AUDIT:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${record.productName} (${record.brandName}) - MRP: ${record.declaredMrp}",
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "SPECIFIC CONTRAVENTIONS NOTED (${violations.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = ComplianceFail
                    )

                    if (violations.isEmpty()) {
                        Text(
                            text = "No contraventions found. Certified 100% Compliant.",
                            fontSize = 10.sp,
                            color = CompliancePass
                        )
                    } else {
                        violations.forEachIndexed { i, v ->
                            Text(
                                text = "${i + 1}. [${v.ruleReference}] ${v.ruleName}: ${v.explanation}",
                                fontSize = 10.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You are hereby directed to show cause within 15 days why legal proceedings under Section 36/49 should not be initiated, or avail compounding under Section 48.",
                        fontSize = 9.sp,
                        color = Color(0xFF57534E),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MetrologyNavy
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Export Memo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
