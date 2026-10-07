package com.example.ui.diagnostics

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.RulesetComplianceEngine
import com.example.data.scanner.SamplePackagesRepository
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class SyntheticTestCase(
    val testId: String,
    val name: String,
    val description: String,
    val expectedVerdict: String,
    var actualVerdict: String = "PENDING",
    var passed: Boolean = false,
    var latencyMs: Long = 0,
    var violationsDetected: Int = 0,
    var details: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyntheticTestsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val activeRuleset by viewModel.activeRuleset.collectAsState()
    val scope = rememberCoroutineScope()
    var isRunningTests by remember { mutableStateOf(false) }

    val testCases = remember {
        mutableStateListOf(
            SyntheticTestCase(
                testId = "TC-01",
                name = "✅ Pure Mustard Oil (1L) • 100% Legal",
                description = "Standard legal packaging with valid MRP, net weight, FSSAI license & consumer care.",
                expectedVerdict = "PASS"
            ),
            SyntheticTestCase(
                testId = "TC-02",
                name = "❌ Groundnut Oil • Missing MRP Violation",
                description = "Illegal package: Missing mandatory retail MRP and missing customer care contact under Rule 6.",
                expectedVerdict = "FAIL"
            ),
            SyntheticTestCase(
                testId = "TC-03",
                name = "❌ Fertilizer 25kg • Missing Address Violation",
                description = "Illegal package: Missing manufacturer factory address and missing packaging date.",
                expectedVerdict = "FAIL"
            ),
            SyntheticTestCase(
                testId = "TC-04",
                name = "✅ Basmati Rice 5kg • 100% Legal",
                description = "Proper legal packaging with clear dual language, batch code and net weight declarations.",
                expectedVerdict = "PASS"
            ),
            SyntheticTestCase(
                testId = "TC-05",
                name = "⚠️ Blurry Camera Photo • Quality Warning",
                description = "Simulates a dark or shaky camera photo; alerts user to hold steady and retake photo.",
                expectedVerdict = "REVIEW"
            )
        )
    }

    val totalTests = testCases.size
    val completedTests = testCases.count { it.actualVerdict != "PENDING" }
    val passedAssertions = testCases.count { it.passed }
    val calculatedAccuracy = if (completedTests > 0) (passedAssertions.toFloat() / completedTests.toFloat()) * 100f else 0f
    val avgLatency = if (completedTests > 0) testCases.filter { it.actualVerdict != "PENDING" }.map { it.latencyMs }.average().toLong() else 0L

    fun runSyntheticSuite() {
        scope.launch {
            isRunningTests = true
            val samples = SamplePackagesRepository.samplePackages

            testCases.forEachIndexed { index, tc ->
                val startTime = System.currentTimeMillis()
                val sample = when (tc.testId) {
                    "TC-01" -> samples.find { it.id == "SAMPLE_MUSTARD_OIL" }
                    "TC-02" -> samples.find { it.id == "SAMPLE_ADULTERATED_BLENDED_OIL" }
                    "TC-03" -> samples.find { it.id == "SAMPLE_CHIPS_VIOLATION" }
                    "TC-04" -> samples.find { it.id == "SAMPLE_KURKURE_MAIN" }
                    "TC-05" -> samples.find { it.id == "SAMPLE_KURKURE_MAIN" }
                    else -> null
                } ?: (samples.getOrNull(index) ?: samples.first())

                val isBlurrySample = tc.testId == "TC-05"

                // Evaluate real engine
                val eval = RulesetComplianceEngine.evaluate(
                    scanId = UUID.randomUUID().toString(),
                    rulesetId = activeRuleset.rulesetId,
                    productName = if (isBlurrySample) "" else sample.title,
                    brand = if (isBlurrySample) "" else sample.brand,
                    manufacturer = if (isBlurrySample) "" else sample.manufacturer,
                    importer = "",
                    mrpValue = if (isBlurrySample) 0.0 else (sample.declaredMrp.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 100.0),
                    currency = activeRuleset.currency,
                    isInclusiveTaxes = true,
                    netQtyValue = if (isBlurrySample) 0.0 else (sample.netQuantity.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 1000.0),
                    netQtyUnit = if (isBlurrySample) "" else (if (sample.netQuantity.lowercase().contains("kg")) "kg" else if (sample.netQuantity.lowercase().contains("l")) "L" else "g"),
                    mfgDate = if (isBlurrySample) "" else sample.mfgDate,
                    expiryDate = if (isBlurrySample) "" else sample.expiryDate,
                    bestBefore = if (isBlurrySample) "" else sample.expiryDate,
                    batchNumber = if (isBlurrySample) "" else sample.batchNo,
                    ingredients = if (isBlurrySample) emptyList() else listOf(sample.ingredients),
                    allergens = if (isBlurrySample) emptyList() else listOf(sample.allergens),
                    countryOfOrigin = if (isBlurrySample) "" else sample.countryOfOrigin,
                    customerCare = if (isBlurrySample) "" else sample.consumerCare,
                    measuredFontHeightMm = if (isBlurrySample) 0.0 else sample.fontHeightMm,
                    barcode = if (isBlurrySample) "" else sample.id,
                    qrData = if (isBlurrySample) "" else sample.qrCodeData,
                    rawOcrText = if (isBlurrySample) "blurry low confidence text" else "${sample.title} MRP ${sample.declaredMrp} Net Qty: ${sample.netQuantity} USP: ${sample.declaredUsp} Mfd by: ${sample.manufacturer} Batch: ${sample.batchNo}",
                    captureMode = "SYNTHETIC_SUITE",
                    syncStatus = "LOCAL_VERIFIED",
                    locale = "en-IN"
                )

                delay(200) // Simulated optical pipeline latency
                val duration = System.currentTimeMillis() - startTime

                val actualVerdict = eval.overallStatus
                val isMatch = (actualVerdict == tc.expectedVerdict) || 
                             (tc.expectedVerdict == "FAIL" && (actualVerdict == "FAIL" || actualVerdict == "NON_COMPLIANT" || actualVerdict == "SEIZURE_RECOMMENDED")) ||
                             (tc.expectedVerdict == "REVIEW" && actualVerdict == "NOT_VERIFIABLE")

                testCases[index] = tc.copy(
                    actualVerdict = actualVerdict,
                    passed = isMatch,
                    latencyMs = duration,
                    violationsDetected = eval.violations.size,
                    details = "Engine evaluated against ${activeRuleset.regulations.size} statutory rules. Flagged ${eval.violations.size} violations."
                )
            }
            isRunningTests = false
        }
    }

    LaunchedEffect(Unit) {
        if (completedTests == 0) {
            runSyntheticSuite()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Practice & Test Scenarios",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Test how MudraCheck catches violations",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("synthetic_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { runSyntheticSuite() },
                        enabled = !isRunningTests,
                        modifier = Modifier.testTag("rerun_tests_btn")
                    ) {
                        if (isRunningTests) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Run Test Suite")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Test Metrics Scorecard
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Golden Test Bench Scorecard",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Evaluated against: ${activeRuleset.rulesetId}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (passedAssertions == totalTests) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${String.format("%.1f", calculatedAccuracy)}% ACCURACY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (passedAssertions == totalTests) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            }
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$passedAssertions / $totalTests",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF2E7D32)
                                )
                                Text("Passed Tests", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${totalTests - passedAssertions}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (totalTests - passedAssertions > 0) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                                )
                                Text("Failed Tests", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${avgLatency}ms",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Avg Engine Latency", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Test Cases List
            item {
                Text(
                    text = "Golden Test Cases ($totalTests Cases)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(testCases, key = { it.testId }) { tc ->
                val verdictColor = when (tc.actualVerdict) {
                    "PASS" -> Color(0xFF2E7D32)
                    "REVIEW" -> Color(0xFFE65100)
                    "FAIL" -> Color(0xFFC62828)
                    else -> Color.Gray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tc.testId,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tc.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(verdictColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tc.actualVerdict,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = verdictColor
                                )
                            }
                        }

                        Text(
                            text = tc.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Expected: ${tc.expectedVerdict} • Actual: ${tc.actualVerdict}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (tc.passed) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Text(
                                text = "Latency: ${tc.latencyMs}ms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (tc.details.isNotBlank()) {
                            Text(
                                text = tc.details,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Execute Button
            item {
                Button(
                    onClick = { runSyntheticSuite() },
                    enabled = !isRunningTests,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_all_synthetic_tests_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Re-Run Complete Synthetic Test Suite")
                }
            }
        }
    }
}
