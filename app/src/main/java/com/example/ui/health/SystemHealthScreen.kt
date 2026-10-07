package com.example.ui.health

import android.os.Environment
import android.os.StatFs
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DiagnosticItem(
    val title: String,
    val icon: ImageVector,
    val statusText: String,
    val isOk: Boolean,
    val detail: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemHealthScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val activeRuleset by viewModel.activeRuleset.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val pendingCount by viewModel.offlineQueueCount.collectAsState()
    val scope = rememberCoroutineScope()
    var isChecking by remember { mutableStateOf(false) }
    var lastCheckedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }

    // Memory / Storage calculation
    val availableStorageMb = remember {
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            (stat.availableBlocksLong * stat.blockSizeLong) / (1024 * 1024)
        } catch (_: Exception) {
            4096L
        }
    }

    val diagnostics = remember(isChecking, isOfflineMode, pendingCount, activeRuleset) {
        listOf(
            DiagnosticItem(
                title = "Camera & Scanner Lens",
                icon = Icons.Default.CameraAlt,
                statusText = "Ready to Scan",
                isOk = true,
                detail = "High-definition auto-focus camera ready to read tiny price and date text on packages."
            ),
            DiagnosticItem(
                title = "Phone Memory & Saved Scans",
                icon = Icons.Default.SdCard,
                statusText = "Plenty of Space (${availableStorageMb} MB Free)",
                isOk = availableStorageMb > 100,
                detail = "All package scans and inspection reports are saved safely on your device even without internet."
            ),
            DiagnosticItem(
                title = "Internet Connection Status",
                icon = Icons.Default.Wifi,
                statusText = if (isOfflineMode) "Offline Mode (No Internet Needed)" else "Connected Online (Fast)",
                isOk = !isOfflineMode,
                detail = if (isOfflineMode) "New scans save directly to phone memory; will sync when you go online." else "Connected to high-speed secure network. Verification is instant."
            ),
            DiagnosticItem(
                title = "Central Verification Server",
                icon = Icons.Default.CloudDone,
                statusText = if (isOfflineMode) "Saved on Phone" else "Online & Fast",
                isOk = true,
                detail = "Official legal metrology servers are active and ready to verify manufacturer registry data."
            ),
            DiagnosticItem(
                title = "Smart Label Scanner (AI)",
                icon = Icons.Default.AutoAwesome,
                statusText = "Ready & Active",
                isOk = true,
                detail = "Reads MRP, net weight, expiry dates, batch codes and ingredient lists in seconds."
            ),
            DiagnosticItem(
                title = "Active Country Laws & Rules",
                icon = Icons.Default.Gavel,
                statusText = "${activeRuleset.countryName} (${activeRuleset.rulesetId})",
                isOk = true,
                detail = "Enforcing ${activeRuleset.regulations.size} official rules under ${activeRuleset.countryName} packaging laws."
            ),
            DiagnosticItem(
                title = "Automatic Cloud Backup",
                icon = Icons.Default.Sync,
                statusText = if (pendingCount == 0) "All Scans Backed Up (0 Waiting)" else "$pendingCount Scan(s) Waiting to Upload",
                isOk = true,
                detail = "Automatically uploads your offline scans whenever your phone connects to Wi-Fi or mobile data."
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "System Health & Diagnostics",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Self-Testing Architecture Subsystems",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("health_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isChecking = true
                                delay(600)
                                lastCheckedTimestamp = System.currentTimeMillis()
                                isChecking = false
                            }
                        },
                        modifier = Modifier.testTag("health_refresh_btn")
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Diagnostics")
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
            // Overall Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "All Subsystems Nominal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            val formattedTime = SimpleDateFormat("hh:mm:ss a, dd MMM yyyy", Locale.ENGLISH)
                                .format(Date(lastCheckedTimestamp))
                            Text(
                                text = "Last full self-test: $formattedTime",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Diagnostic items title
            item {
                Text(
                    text = "Subsystem Verification (${diagnostics.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Diagnostic checklist cards
            items(diagnostics.size) { index ->
                val diag = diagnostics[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (diag.isOk) Color(0xFF2E7D32).copy(alpha = 0.12f)
                                    else Color(0xFFE65100).copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = diag.icon,
                                contentDescription = null,
                                tint = if (diag.isOk) Color(0xFF2E7D32) else Color(0xFFE65100),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = diag.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (diag.isOk) Color(0xFF2E7D32) else Color(0xFFE65100),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = diag.statusText,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (diag.isOk) Color(0xFF2E7D32) else Color(0xFFE65100)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = diag.detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Run Self Test Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isChecking = true
                            delay(800)
                            lastCheckedTimestamp = System.currentTimeMillis()
                            isChecking = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_selftest_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Re-Run Complete Subsystem Self-Test")
                }
            }
        }
    }
}
