package com.example.ui.guide

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MetrologyRuleEntity
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AppFontWeights
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.AgriWheatAmber

@Composable
fun LegalMetrologyGuideScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val dbRules by viewModel.allMetrologyRules.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "PCR 2011 & Legal Metrology Rules",
                subtitle = "Room Database Rule Schema (6, 8, 9, 18, 36)",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                currentUser = currentUser
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.RULE_GUIDE,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("rule_guide_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Switcher Tab (Rules DB vs AI Search Grounded Help)
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    TabRow(
                        selectedTabIndex = 0,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[0]),
                                color = AgriLeafGreen,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = true,
                            onClick = {},
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = AgriForestGreen
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Room DB Rules (6, 8, 9)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriForestGreen
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = false,
                            onClick = { viewModel.navigateTo(AppScreen.HELP_SEARCH) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = AgriHarvestGold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "AI Search Grounding Help",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Header Info Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AgriForestGreen
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = AgriWheatAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Legal Metrology (Packaged Commodities) Rules, 2011",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Statutory compliance criteria stored in local Room database engine for automated label inspection, OCR validation & violation indexing.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFD8F3DC),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            // Room Stored Metrology Rules (6, 8, 9, 18, 36)
            item {
                Text(
                    text = "Statutory Compliance Rules (${dbRules.size} Stored in Room DB)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            items(dbRules, key = { it.ruleId }) { rule ->
                DbRuleCard(rule)
            }

            // Schedule II: Font Height Matrix
            item {
                Text(
                    text = "Schedule II: Minimum Font Height Standards",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScheduleRow("Net Qty <= 50 g / ml", "Minimum 1.0 mm (Blown/moulded: 2.0 mm)")
                        ScheduleRow("50 g/ml < Qty <= 200 g/ml", "Minimum 2.0 mm (Blown/moulded: 4.0 mm)")
                        ScheduleRow("200 g/ml < Qty <= 1 kg / L", "Minimum 4.0 mm (Blown/moulded: 6.0 mm)")
                        ScheduleRow("Net Qty > 1 kg / L", "Minimum 6.0 mm (Blown/moulded: 6.0 mm)")
                    }
                }
            }

            // Statutory Enforcement Penalties
            item {
                Text(
                    text = "Enforcement Powers under LM Act, 2009",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "• Section 36(1): Penalty for selling non-standard packaged commodities up to ₹25,000 for 1st offense, ₹50,000 for 2nd offense, and up to ₹1,00,000 or 1-year imprisonment for repeat offenses.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = "• Section 36(2): Penalty for selling above MRP or altering sticker prices up to ₹50,000.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = "• Rule 32 (PCR 2011): Authority to issue compound notices and seize non-compliant consignment packages.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun DbRuleCard(rule: MetrologyRuleEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rule_card_${rule.ruleId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AgriLeafGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = rule.ruleNumber,
                        color = AgriLeafGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = rule.category,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = rule.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )

            if (rule.penaltyClause.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Penalty Clause: ${rule.penaltyClause}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AgriWheatAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun ScheduleRow(packSize: String, minHeight: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = packSize,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = AppFontWeights.Subheader,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Text(
            text = minHeight,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = AppFontWeights.Header,
                color = AgriLeafGreen
            ),
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
}
