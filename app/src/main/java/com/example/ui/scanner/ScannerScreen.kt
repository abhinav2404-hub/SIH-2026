package com.example.ui.scanner

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InspectionStatus
import com.example.data.model.SamplePackage
import com.example.data.scanner.SamplePackagesRepository
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.ComplianceFail
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceWarning

@Composable
fun ScannerScreen(viewModel: MainViewModel) {
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analysisStep by viewModel.analysisStep.collectAsState()
    val isCameraOpen by viewModel.isCameraOpen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Camera, 1: Benchmark Samples, 2: Custom Text

    // If CameraX fullscreen lens is triggered
    if (isCameraOpen) {
        CameraCaptureInterface(
            onPhotoCaptured = { bitmap ->
                viewModel.onCameraPhotoCaptured(bitmap)
            },
            onClose = {
                viewModel.closeCamera()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = viewModel.getString("scan_title"),
                subtitle = viewModel.getString("scan_subtitle"),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab Selection Bar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = AgriLeafGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AgriLeafGreen,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Live CameraX", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_live_camerax")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Agri Samples", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_sample_packages")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Manual Text", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_custom_ocr")
                    )
                }

                when (selectedTab) {
                    0 -> LiveCameraTabContent(
                        onLaunchCamera = { viewModel.openCamera() },
                        onQuickTest = {
                            val sample = SamplePackagesRepository.samplePackages.first()
                            viewModel.selectSamplePackage(sample)
                        }
                    )
                    1 -> BenchmarkSamplesTabContent(
                        onSelectSample = { sample ->
                            viewModel.selectSamplePackage(sample)
                        }
                    )
                    2 -> CustomTextTabContent(
                        onAnalyze = { name, brand, cat, text ->
                            viewModel.analyzeCustomLabelText(name, brand, cat, text)
                        }
                    )
                }
            }

            // Analysis Loading Overlay
            if (isAnalyzing) {
                AnalysisLoadingOverlay(analysisStep)
            }
        }
    }
}

@Composable
private fun LiveCameraTabContent(
    onLaunchCamera: () -> Unit,
    onQuickTest: () -> Unit
) {
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // CameraX Viewfinder Simulation Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0F1E14))
                .border(2.dp, AgriLeafGreen, RoundedCornerShape(20.dp))
                .clickable { onLaunchCamera() }
                .testTag("viewfinder_box"),
            contentAlignment = Alignment.Center
        ) {
            // Laser Scanning Line Animation
            Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * laserPosition
                drawLine(
                    color = Color(0xFF52B788),
                    start = Offset(20f, y),
                    end = Offset(size.width - 20f, y),
                    strokeWidth = 3.dp.toPx()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AgriLeafGreen.copy(alpha = 0.25f))
                        .border(1.dp, AgriLeafGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color(0xFF52B788),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tap to Launch CameraX Live Lens",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Position fertilizer sacks, seed packs, or edible oil bottles inside the Rule 8 Principal Display Panel target.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD8F3DC),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Button(
            onClick = onLaunchCamera,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("button_open_camerax"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriLeafGreen,
                contentColor = Color.White
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Camera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open CameraX Lens", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onQuickTest,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("button_quick_sample_scan"),
            shape = RoundedCornerShape(23.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AgriLeafGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Quick Scan Mustard Oil Benchmark", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statutory Legal Metrology Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = AgriLeafGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Automated Checks Under PCR Rules 2011",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "Rule 6(1)(a)-(g): Manufacturer address, Net Wt, MRP incl. taxes, USP, and Consumer Helpline",
                    "Rule 8: Principal Display Panel area (40% for cylindrical, 100% for rectangular)",
                    "Rule 9 / Schedule II: Minimum font height (1.0mm to 6.0mm depending on pack size)",
                    "Fertilizer & Seed Act: FCO composition declaration & germination validity"
                ).forEach { ruleText ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(AgriLeafGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ruleText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenchmarkSamplesTabContent(
    onSelectSample: (SamplePackage) -> Unit
) {
    val samples = remember { SamplePackagesRepository.samplePackages }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select an agricultural or packaged commodity benchmark for instant automated compliance testing:",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        samples.forEach { sample ->
            val statusColor = when (sample.expectedStatus) {
                InspectionStatus.COMPLIANT -> CompliancePass
                InspectionStatus.MINOR_VIOLATIONS,
                InspectionStatus.NON_COMPLIANT -> ComplianceWarning
                InspectionStatus.SEIZURE_RECOMMENDED -> ComplianceFail
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSample(sample) }
                    .testTag("sample_item_${sample.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (sample.category.contains("Seed") || sample.category.contains("Fertilizer")) {
                                Icons.Default.Agriculture
                            } else {
                                Icons.Default.Eco
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sample.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${sample.brand} • ${sample.category}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Net: ${sample.netQuantity} | MRP: ${sample.declaredMrp}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgriLeafGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Test",
                        tint = AgriLeafGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTextTabContent(
    onAnalyze: (String, String, String, String) -> Unit
) {
    var productName by remember { mutableStateOf("KrishiVeda Bio Zinc Fertilizer") }
    var brandName by remember { mutableStateOf("KrishiVeda Agro") }
    var category by remember { mutableStateOf("Agri Seeds & Fertilizers") }
    var rawText by remember {
        mutableStateOf(
            "KRISHIVEDA BIO ZINC FERTILIZER\nNet Weight: 25 kg\nMRP: Rs. 850.00 (incl. of all taxes)\nUSP: Rs. 34.00 / kg\nMfg Date: 02/2026\nManufactured by: KrishiVeda Agro Industries, Karnal, Haryana - 132001\nConsumer Care: care@krishiveda.in / 1800-180-1551\nCountry of Origin: India\nZinc Content: 21% min."
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth().testTag("input_custom_product_name"),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = brandName,
            onValueChange = { brandName = it },
            label = { Text("Brand / Manufacturer") },
            modifier = Modifier.fillMaxWidth().testTag("input_custom_brand"),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Commodity Category") },
            modifier = Modifier.fillMaxWidth().testTag("input_custom_category"),
            shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            label = { Text("Raw Package Label Text (OCR Output)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("input_custom_raw_text"),
            shape = RoundedCornerShape(10.dp)
        )

        Button(
            onClick = {
                onAnalyze(productName, brandName, category, rawText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("button_analyze_custom_text"),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AgriLeafGreen,
                contentColor = Color.White
            )
        ) {
            Text("Analyze Compliance Under Rules 6, 8, 9", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AnalysisLoadingOverlay(stepMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = AgriLeafGreen,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Legal Metrology Verification",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stepMessage,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
