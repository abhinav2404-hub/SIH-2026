package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.InspectionRecord
import com.example.data.scanner.SamplePackagesRepository
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AppFontWeights
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.AgriSproutMint
import com.example.ui.theme.AgriWheatAmber
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
fun DashboardScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val inspections by viewModel.inspectionHistory.collectAsState()

    val totalInspections = inspections.size
    val compliantCount = inspections.count { it.overallStatus == "COMPLIANT" }
    val nonCompliantCount = inspections.count { it.overallStatus == "NON_COMPLIANT" || it.overallStatus == "MINOR_VIOLATIONS" }
    val seizureCount = inspections.count { it.overallStatus == "SEIZURE_RECOMMENDED" }
    val compliantPercentage = if (totalInspections > 0) (compliantCount * 100) / totalInspections else 100

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Legal Metrology Portal",
                subtitle = "Packaged Commodities Rules, 2011",
                currentUser = currentUser,
                onLogoutClick = { viewModel.logout() }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.DASHBOARD,
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
            // Officer Profile Card
            item {
                OfficerBadgeCard(
                    name = currentUser?.name ?: "Inspector R. K. Verma",
                    badge = currentUser?.officerId ?: "LMO-SR-2026-8941",
                    role = currentUser?.role?.title ?: "Senior Legal Metrology Officer",
                    jurisdiction = currentUser?.jurisdiction ?: "Central Verification Unit, State Metrology Wing"
                )
            }

            // High-Impact Primary Action: Start Scan Banner
            item {
                PrimaryScannerBanner(
                    onScanClick = { viewModel.navigateTo(AppScreen.SCANNER) }
                )
            }

            // Statutory Metrics Grid
            item {
                Text(
                    text = "Enforcement & Compliance Overview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatKpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Scanned",
                        value = "$totalInspections",
                        subtitle = "Audited batches",
                        color = MetrologyNavy,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    StatKpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Compliant Rate",
                        value = "$compliantPercentage%",
                        subtitle = "$compliantCount Passed",
                        color = CompliancePass,
                        containerColor = CompliancePassContainer
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatKpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Rule Violations",
                        value = "$nonCompliantCount",
                        subtitle = "Notices issued",
                        color = ComplianceWarning,
                        containerColor = ComplianceWarningContainer
                    )
                    StatKpiCard(
                        modifier = Modifier.weight(1f),
                        title = "Seizures",
                        value = "$seizureCount",
                        subtitle = "Sec 36 warranted",
                        color = ComplianceFail,
                        containerColor = ComplianceFailContainer
                    )
                }
            }

            // Quick SIH Test Benchmark Samples Carousel
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SIH 2026 Test Benchmark Suite",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "One-tap rule compliance tests across commodity classes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(SamplePackagesRepository.samplePackages) { sample ->
                        SampleBenchmarkCard(
                            sample = sample,
                            onClick = { viewModel.selectSamplePackage(sample) }
                        )
                    }
                }
            }

            // Google Search Grounded Reference Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.HELP_SEARCH) }
                        .testTag("banner_grounded_rules_help"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(1.dp, AgriEmerald.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(AgriForestGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AgriHarvestGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Statutory Reference Help",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AgriEmerald.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Search Grounded",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AgriForestGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Quick summaries for Legal Metrology, Seeds Act 1966 & Fertilizer Control Order 1985",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Help",
                            tint = AgriForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Statutory Tools & Utilities Quick Grid
            item {
                Text(
                    text = "Legal Metrology Utilities",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ToolActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Calculate,
                        title = "USP Calc",
                        subtitle = "Rule 6(1)(e)",
                        onClick = { viewModel.navigateTo(AppScreen.TOOLS) }
                    )
                    ToolActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.TextFormat,
                        title = "Font Verifier",
                        subtitle = "Schedule II",
                        onClick = { viewModel.navigateTo(AppScreen.TOOLS) }
                    )
                    ToolActionTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AutoAwesome,
                        title = "AI Reference",
                        subtitle = "Search Grounded",
                        onClick = { viewModel.navigateTo(AppScreen.HELP_SEARCH) }
                    )
                }
            }

            // Recent Audits Feed Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Inspection Audits",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "View All (${inspections.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MetrologyNavy,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .clickable { viewModel.navigateTo(AppScreen.HISTORY) }
                            .padding(4.dp)
                            .testTag("button_view_all_history")
                    )
                }
            }

            // Recent Inspections List (first 4 items)
            if (inspections.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No inspections conducted yet. Tap 'Scan Package' to begin auditing under Legal Metrology Rules.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(inspections.take(4)) { record ->
                    InspectionRecordCard(
                        record = record,
                        onClick = { viewModel.openExistingRecord(record) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun OfficerBadgeCard(
    name: String,
    badge: String,
    role: String,
    jurisdiction: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MetrologyNavy
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.5.dp, MetrologyGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = MetrologyGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MetrologyGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF138808).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(0xFF138808))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFF22C55E), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "ACTIVE DUTY",
                            color = Color(0xFF86EFAC),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DESIGNATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 9.sp
                        )
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PrimaryScannerBanner(onScanClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onScanClick() }
            .testTag("banner_start_scan"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D5519)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF234413),
                            Color(0xFF386A20)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE2EBD6).copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, Color(0xFFE2EBD6).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "AI-POWERED OCR & RULE ENGINE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFE2EBD6),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Scan Commodity Label",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Check Rule 6 declarations, MRP, USP, font height & country of origin instantly",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "Scan",
                        tint = MetrologyNavy,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatKpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = AppFontWeights.Subheader,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = AppFontWeights.Header,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun SampleBenchmarkCard(
    sample: com.example.data.model.SamplePackage,
    onClick: () -> Unit
) {
    val isCompliant = sample.expectedStatus == com.example.data.model.InspectionStatus.COMPLIANT
    val statusColor = when (sample.expectedStatus) {
        com.example.data.model.InspectionStatus.COMPLIANT -> CompliancePass
        com.example.data.model.InspectionStatus.MINOR_VIOLATIONS -> ComplianceWarning
        com.example.data.model.InspectionStatus.NON_COMPLIANT -> ComplianceFail
        com.example.data.model.InspectionStatus.SEIZURE_RECOMMENDED -> Color(0xFF990000)
    }

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("sample_card_${sample.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${sample.expectedComplianceScore}% SCORE",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            fontSize = 9.sp
                        )
                    )
                }

                Text(
                    text = sample.category.take(16),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sample.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )

            Text(
                text = "Qty: ${sample.netQuantity} • MRP: ${sample.declaredMrp.take(12)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isCompliant) "100% Compliant" else "Flagged Violations",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Test",
                    tint = MetrologyNavy,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ToolActionTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("tool_tile_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MetrologyNavy,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun InspectionRecordCard(
    record: InspectionRecord,
    onClick: () -> Unit
) {
    val isPass = record.overallStatus == "COMPLIANT"
    val statusColor = when (record.overallStatus) {
        "COMPLIANT" -> CompliancePass
        "MINOR_VIOLATIONS" -> ComplianceWarning
        "NON_COMPLIANT" -> ComplianceFail
        else -> Color(0xFF990000)
    }

    val statusLabel = when (record.overallStatus) {
        "COMPLIANT" -> "100% Compliant"
        "MINOR_VIOLATIONS" -> "Minor Defect"
        "NON_COMPLIANT" -> "${record.violationsCount} Violations"
        else -> "Seizure Recommended"
    }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(record.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("record_card_${record.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f))
                    .border(1.dp, statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${record.complianceScore}%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = AppFontWeights.Header,
                        color = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.productName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Brand: ${record.brandName} • Qty: ${record.netQuantity} • MRP: ${record.declaredMrp}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )

                Text(
                    text = "$formattedDate • ${record.inspectionLocation.take(28)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
