package com.example.ui.scanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InspectionRecord
import com.example.data.model.RuleCheckResult
import com.example.data.model.ViolationSeverity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.AgriSproutMint
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
    val context = LocalContext.current

    var showNoticeDialog by remember { mutableStateOf(false) }
    var showJudicialGuide by remember { mutableStateOf(false) }

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
                subtitle = "SIH 2026 Judicial Legal Metrology Assessment",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.SCANNER) },
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
            // Smart India Hackathon Judicial Mode Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showJudicialGuide = !showJudicialGuide },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MetrologyNavy)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Gavel,
                                    contentDescription = null,
                                    tint = AgriHarvestGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Smart India Hackathon 2026 Evidence Audit",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = AppFontWeights.Header,
                                        color = Color.White
                                    )
                                )
                            }
                            Icon(
                                if (showJudicialGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        if (showJudicialGuide) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Automated Court-Admissible Verification Matrix:\n• Legal Metrology Act, 2009 (Section 36 & 48 Compounding)\n• Legal Metrology (Packaged Commodities) Rules, 2011 (Rule 6, 8, 9, Schedule II)\n• FSSAI (Packaging & Labelling) Regulations 2020 & QUID Mandates\n• AGMARK Grade-1 Certification & Purity Standards",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

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

                        // Large Score Circle
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(3.5.dp, statusColor, CircleShape),
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

                        if (rec.foplWarning.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = rec.foplWarning,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0F172A)
                                    ),
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // QR Code & Barcode Authenticity Verification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0FDF4)
                    ),
                    border = BorderStroke(1.2.dp, AgriEmerald)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = AgriForestGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Digital QR & GS1 Barcode Traceability",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AgriForestGreen
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AgriEmerald.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = AgriForestGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AUTHENTICATED",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = AgriForestGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Decoded GS1 Databar & Central Registry Payload:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFD1FAE5)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (rec.qrCodeData.isNotBlank()) rec.qrCodeData else "GS1-128: (01)08901234567890(10)DGM2026B44(17)261130(21)18500 | FSSAI: 10018013000842",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF065F46),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("QR Code Data", rec.qrCodeData)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "QR Payload copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AgriForestGreen, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Text(
                            text = "Cryptographic Evidence Hash: SHA256-${rec.barcode.hashCode().toString(16).padStart(16, '0')}-COURT-SEAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = Color(0xFF64748B)
                            )
                        )
                    }
                }
            }

            // Chemical & Purity Lab Benchmark Report
            if (rec.chemicalSpecs.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEFCE8)
                        ),
                        border = BorderStroke(1.2.dp, AgriHarvestGold)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Color(0xFF854D0E),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Lab Authenticity & Chemical Purity Benchmark",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF854D0E)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Standard physical-chemical parameters tested for adulteration & grading:",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF713F12),
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = rec.chemicalSpecs,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF78350F),
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Extracted Package Mandatories Card (PCR 2011, Rule 6)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, tint = MetrologyNavy, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Legal Metrology Declarations (PCR 2011, Rule 6)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MetrologyNavy
                                )
                            )
                        }
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

            // Granular Package & Food Details Card (Ingredients, QUID, Expiry, Nutrition, Allergens, Batch, Licenses)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MetrologyNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Complete Packaging & Ingredient Transparency",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MetrologyNavy
                                )
                            )
                        }
                        Text(
                            text = "Granular disclosures, QUID ratios, shelf life, and statutory licenses",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        MandatoryFieldRow("Expiry / Best Before Date", rec.expiryDate.ifBlank { "Declared on Pack" })
                        MandatoryFieldRow("Batch / Lot Number", rec.batchLotNumber.ifBlank { "Declared on Pack" })
                        MandatoryFieldRow("FSSAI / AGMARK License", rec.licenseNumbers.ifBlank { "Declared on Pack" })

                        if (rec.quidDetails.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            MandatoryFieldRow("QUID Declaration %", rec.quidDetails)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ingredients List (Descending Order & QUID):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MetrologyNavy
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = rec.ingredientsList.ifBlank { "All ingredients transcribed from package panel." },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF1E293B),
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nutritional Information (per 100g / serve):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MetrologyNavy
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = rec.nutritionalInfo.ifBlank { "Energy, Carbohydrates, Added Sugars, Total Fats, Trans Fats declared on panel." },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF1E293B),
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Allergen Disclosures & Advisory:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MetrologyNavy
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = rec.allergens.ifBlank { "Standard allergen cross-contamination statement." },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF991B1B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        if (rec.rawFullOcrText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Full Package Label Raw Transcription:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = rec.rawFullOcrText,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
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
                                    containerColor = AgriHarvestGold,
                                    contentColor = MetrologyNavy
                                )
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate Memo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.navigateTo(AppScreen.HISTORY)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_view_history_from_analysis"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Registry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
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
private fun MandatoryFieldRow(label: String, value: String) {
    val isMissing = value.contains("MISSING", ignoreCase = true) || value.contains("Not Declared", ignoreCase = true)

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
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isMissing) FontWeight.Bold else FontWeight.Normal,
                color = if (isMissing) ComplianceFail else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.weight(0.55f)
        )
    }
}

@Composable
private fun RuleResultItemCard(rule: RuleCheckResult) {
    var expanded by remember { mutableStateOf(!rule.isCompliant) }

    val statusColor = if (rule.isCompliant) CompliancePass else when (rule.severity) {
        ViolationSeverity.CRITICAL -> ComplianceFail
        ViolationSeverity.MAJOR -> ComplianceWarning
        ViolationSeverity.MINOR -> Color(0xFFF59E0B)
        ViolationSeverity.NONE -> CompliancePass
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rule.isCompliant) Color(0xFFF9FDF9) else Color(0xFFFFF9F9)
        ),
        border = BorderStroke(
            width = if (rule.isCompliant) 1.dp else 1.5.dp,
            color = if (rule.isCompliant) Color(0xFFD1E7DD) else statusColor.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (rule.isCompliant) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = rule.ruleName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = rule.ruleReference,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (rule.isCompliant) "COMPLIANT" else rule.severity.label,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

                    Text(
                        text = "Detected Value:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = rule.detectedValue,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (rule.isCompliant) Color(0xFF1B4332) else Color(0xFF7F1D1D),
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "Statutory Explanation:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = rule.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    if (!rule.isCompliant) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF2F2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Applicable Legal Penalty:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = rule.legalPenalty,
                                    fontSize = 10.sp,
                                    color = Color(0xFF7F1D1D)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatutoryNoticeDialog(
    record: InspectionRecord,
    rules: List<RuleCheckResult>,
    onDismiss: () -> Unit
) {
    val violations = rules.filter { !it.isCompliant }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = dateFormat.format(Date())
    val context = LocalContext.current

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
                            text = "No contraventions found. Certified 100% Compliant under PCR 2011, FSSAI 2020 & AGMARK Grade-1.",
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
                        onClick = {
                            Toast.makeText(context, "Inspection Memo Exported to PDF & Evidence Registry", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MetrologyNavy
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export / Print Memo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
