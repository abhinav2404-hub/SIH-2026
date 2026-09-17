package com.example.ui.transparency

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.CompliancePassContainer
import com.example.ui.theme.ComplianceWarning
import com.example.ui.theme.ComplianceWarningContainer
import com.example.ui.theme.MetrologyNavy

/**
 * FoodTransparencyScreen
 *
 * Implements the "Consumer Right to Know: Food Label Transparency Proposal":
 * 1. The 7 Core Demands (MRP integrity, QUID %, oil type transparency, safe oil/trans fats, disease warnings, quality grading, batch consistency).
 * 2. Quantitative Ingredient Declaration (QUID) rules under FSSAI 2020 with interactive simulator.
 * 3. Complete History and safety profile of Palm Oil (origins, colonial trade, industrial expansion, RSPO, saturated/trans fats).
 * 4. Oil Safety & Adulteration Guide.
 * 5. Exportable Citizen Proposal & Petition Draft.
 */
@Composable
fun FoodTransparencyScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Food Label Transparency",
                subtitle = "Consumer Right to Know & QUID Proposal",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                currentUser = currentUser
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.FOOD_TRANSPARENCY,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Scrollable Tab Row for the 5 comprehensive pillars
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = AgriForestGreen,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = AgriForestGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("7 Core Demands", fontWeight = AppFontWeights.Header, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_core_demands")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("QUID Rules & Calc", fontWeight = AppFontWeights.Header, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Percent, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_quid_calc")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Palm Oil History", fontWeight = AppFontWeights.Header, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_palm_oil_history")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Oil Safety Guide", fontWeight = AppFontWeights.Header, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Opacity, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_oil_safety")
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Citizen Petition", fontWeight = AppFontWeights.Header, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.testTag("tab_petition_draft")
                )
            }

            when (selectedTab) {
                0 -> CoreDemandsTab()
                1 -> QuidSimulatorTab()
                2 -> PalmOilHistoryTab()
                3 -> OilSafetyGuideTab()
                4 -> PetitionDraftTab()
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 0: 7 CORE DEMANDS
// -------------------------------------------------------------------------------------------------

data class CoreDemandItem(
    val number: Int,
    val title: String,
    val whyItMatters: String,
    val currentRegulatoryStatus: String,
    val proposalAsk: String,
    val icon: ImageVector,
    val statusColor: Color
)

@Composable
private fun CoreDemandsTab() {
    val context = LocalContext.current
    val demands = remember {
        listOf(
            CoreDemandItem(
                number = 1,
                title = "Exact MRP Printed Permanently",
                whyItMatters = "Prevents overcharging, price sticker tampering, and price manipulation at points of sale.",
                currentRegulatoryStatus = "Mandatory under Legal Metrology (Packaged Commodities) Rules 2011, Rule 6(1)(da). Enforcement at retail is the critical gap, not the statute.",
                proposalAsk = "Ban stickering over original MRPs; mandate QR-verifiable digital price stamping on all packaging.",
                icon = Icons.Default.Gavel,
                statusColor = CompliancePass
            ),
            CoreDemandItem(
                number = 2,
                title = "Ingredients with Exact Percentage (QUID)",
                whyItMatters = "Allows consumers to judge real content vs. deceptive marketing claims (e.g. 'Real Almonds', 'Real Fruit Juice').",
                currentRegulatoryStatus = "Partially covered by FSSAI QUID (Quantitative Ingredient Declaration) Regulations 2020, but only when named in title or emphasized with pictures.",
                proposalAsk = "Extend QUID universally to all major bulk ingredients and cooking bases (>5%), even when not explicitly highlighted in advertising.",
                icon = Icons.Default.Percent,
                statusColor = ComplianceWarning
            ),
            CoreDemandItem(
                number = 3,
                title = "Specific Oil Type Declaration",
                whyItMatters = "Different oils (palm, sunflower, mustard, vanaspati) have vastly different health and environmental profiles.",
                currentRegulatoryStatus = "FSSAI requires naming the oil in the ingredient list, but manufacturers exploit the 'Edible Vegetable Oil' blend loophole on front-of-pack.",
                proposalAsk = "Mandate specific botanical oil names (e.g. 'Contains 100% Palmolein Oil') in bold on the Principal Display Panel (PDP).",
                icon = Icons.Default.Opacity,
                statusColor = ComplianceWarning
            ),
            CoreDemandItem(
                number = 4,
                title = "Oil Safety & Processing Disclosure",
                whyItMatters = "Repeated commercial heating, industrial hydrogenation, and toxic aldehyde formation are known cardiovascular risks.",
                currentRegulatoryStatus = "FSSAI limits trans fats to maximum 2% in oils and fats (effective 2022), but testing results and heating cycles are invisible to shoppers.",
                proposalAsk = "Mandate on-pack declaration of trans fat content per serving (<0.2g), cold-pressed vs refined status, and ban reused frying oils in commercial batches.",
                icon = Icons.Default.HealthAndSafety,
                statusColor = ComplianceWarning
            ),
            CoreDemandItem(
                number = 5,
                title = "Front-of-Pack Disease Warnings",
                whyItMatters = "High trans fat, excess sodium, and high added sugar are clinically proven drivers of hypertension, diabetes, and heart disease.",
                currentRegulatoryStatus = "Front-of-pack warning labels (FOPNL / black octagons) are mandated in Chile, Mexico, and EU. India has drafted regulations but not yet enforced them.",
                proposalAsk = "Enforce mandatory high-sodium, high-sugar, and high-saturated-fat warning stamps on the front panel of all packaged ultra-processed foods.",
                icon = Icons.Default.Warning,
                statusColor = ComplianceFail
            ),
            CoreDemandItem(
                number = 6,
                title = "Quality Grading & License Verification",
                whyItMatters = "Helps consumers distinguish certified, authentic products from diluted, adulterated, or uninspected goods.",
                currentRegulatoryStatus = "FSSAI 14-digit license numbers and AGMARK/ISI standards exist by law, but are printed in illegibly small fonts without consumer verification tools.",
                proposalAsk = "Mandate high-contrast display of FSSAI and AGMARK licenses with scannable QR codes linking directly to the national compliance registry.",
                icon = Icons.Default.Shield,
                statusColor = CompliancePass
            ),
            CoreDemandItem(
                number = 7,
                title = "Strict Batch-to-Batch Recipe Consistency",
                whyItMatters = "Prevents brands from quietly substituting expensive ingredients with cheaper fillers between production runs without notice.",
                currentRegulatoryStatus = "Not currently covered under any standalone Indian statute. This is the groundbreaking new consumer demand of this charter.",
                proposalAsk = "Enact a statutory Batch Consistency Guarantee: any recipe modification exceeding +/- 2% must be registered with FSSAI and disclosed on packaging.",
                icon = Icons.Default.AutoAwesome,
                statusColor = MetrologyNavy
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Charter Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetrologyNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(AgriForestGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Consumer Right to Know Charter",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = AppFontWeights.Header,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Exact Label, Honest Product • 7 Core Demands",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFD8F3DC),
                                    fontWeight = AppFontWeights.Subheader
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Vague labels ('edible vegetable oil', 'natural flavours', undisclosed %) allow manufacturers to substitute cheap oils and fillers without disclosure. This charter bridges existing statutory laws with critical transparency reforms.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = AppFontWeights.Body,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // 7 Demand Cards
        items(demands) { demand ->
            DemandCard(demand)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DemandCard(demand: CoreDemandItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("demand_card_${demand.number}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = demand.statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "#${demand.number}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = AppFontWeights.Header,
                                    color = demand.statusColor
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = demand.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = if (isExpanded) "Less" else "Details",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgriForestGreen,
                            fontWeight = AppFontWeights.Subheader,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = demand.whyItMatters,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = AppFontWeights.Body,
                    lineHeight = 16.sp
                )
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = BorderLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Current Law & Status:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = AppFontWeights.Header,
                            color = AgriForestGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = demand.currentRegulatoryStatus,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = AppFontWeights.Body,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Proposed Citizen Reform Ask:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MetrologyNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = demand.proposalAsk,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = AppFontWeights.Body,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: QUID RULES & INTERACTIVE SIMULATOR
// -------------------------------------------------------------------------------------------------

@Composable
private fun QuidSimulatorTab() {
    var productName by remember { mutableStateOf("Mango Nectar Juice") }
    var ingredientName by remember { mutableStateOf("Mango Pulp") }
    var isNamedInTitle by remember { mutableStateOf(true) }
    var isPicturedOnPack by remember { mutableStateOf(true) }
    var isCompoundIngredient by remember { mutableStateOf(false) }
    var isMajorBaseOil by remember { mutableStateOf(false) }

    val quidVerdict by remember {
        derivedStateOf {
            when {
                isNamedInTitle || isPicturedOnPack -> {
                    Pair(
                        "MANDATORY UNDER FSSAI 2020 QUID",
                        "Because '${ingredientName.ifBlank { "Ingredient" }}' is named in the product title or visually emphasized with pictures, FSSAI Labelling & Display Regulations 2020 strictly mandate declaring its exact percentage (e.g. '$ingredientName (12%)')."
                    )
                }
                isCompoundIngredient -> {
                    Pair(
                        "MANDATORY COMPOUND BREAKDOWN",
                        "Compound ingredients (ingredients made of multiple items like chocolate coating) that exceed 5% of the total product must disclose their internal sub-ingredients in descending order."
                    )
                }
                isMajorBaseOil -> {
                    Pair(
                        "PROPOSED TRANSPARENCY REQUIREMENT",
                        "Current law does not require exact % for non-emphasized oils, but our Consumer Transparency Proposal demands that all base oils and major bulk ingredients (>5%) declare their exact percentage."
                    )
                }
                else -> {
                    Pair(
                        "VOLUNTARY / STANDARD DESCENDING ORDER",
                        "Under current FSSAI rules, ingredients must be listed in descending order by ingoing weight at time of manufacture. Exact % is currently voluntary unless emphasized on the pack."
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Legal Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Percent, contentDescription = null, tint = AgriForestGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "What is QUID (Quantitative Ingredient Declaration)?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = AppFontWeights.Header)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Under FSSAI Labelling & Display Regulations, 2020 (aligned with EU Reg 1169/2011), ingredients must be listed in descending order by weight. However, stating the exact numerical percentage (%) is legally required under specific triggers.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = AppFontWeights.Body,
                            lineHeight = 17.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuidBulletPoint("1. Trigger A: Named in Product Title (e.g. 'Mango Drink' -> must state 'Mango Pulp (12%)')")
                        QuidBulletPoint("2. Trigger B: Emphasized in pictures or text (e.g. almond imagery or 'Made with Real Butter')")
                        QuidBulletPoint("3. Trigger C: Essential to product character even if not explicitly named")
                        QuidBulletPoint("4. Added Water > 5% must be declared in the ingredients list")
                        QuidBulletPoint("5. Compound ingredients > 5% must be broken down into sub-ingredients")
                    }
                }
            }
        }

        // Interactive Simulator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, AgriEmerald)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive QUID Obligation Simulator",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = AgriForestGreen
                        )
                    )
                    Text(
                        text = "Test whether a food package is legally required to declare exact percentages.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = AppFontWeights.Body
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Product Name on Front of Pack", fontWeight = AppFontWeights.Body) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_quid_product_name"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ingredientName,
                        onValueChange = { ingredientName = it },
                        label = { Text("Specific Ingredient to Test", fontWeight = AppFontWeights.Body) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_quid_ingredient_name"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Package Claims & Physical Presentation:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = AppFontWeights.Header,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    CheckboxRow(
                        checked = isNamedInTitle,
                        onCheckedChange = { isNamedInTitle = it },
                        text = "Ingredient is part of product name (e.g. 'Almond Cookie')"
                    )
                    CheckboxRow(
                        checked = isPicturedOnPack,
                        onCheckedChange = { isPicturedOnPack = it },
                        text = "Ingredient is pictured or claimed (e.g. picture of real nuts)"
                    )
                    CheckboxRow(
                        checked = isCompoundIngredient,
                        onCheckedChange = { isCompoundIngredient = it },
                        text = "Is a compound ingredient making up > 5% of product"
                    )
                    CheckboxRow(
                        checked = isMajorBaseOil,
                        onCheckedChange = { isMajorBaseOil = it },
                        text = "Is a cooking oil or bulk base (>5%) without marketing emphasis"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Verdict Output Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isNamedInTitle || isPicturedOnPack) CompliancePassContainer else ComplianceWarningContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isNamedInTitle || isPicturedOnPack) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isNamedInTitle || isPicturedOnPack) CompliancePass else ComplianceWarning,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = quidVerdict.first,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = AppFontWeights.Header,
                                        color = if (isNamedInTitle || isPicturedOnPack) CompliancePass else Color(0xFF9A6B00)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = quidVerdict.second,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = AppFontWeights.Body,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
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

@Composable
private fun QuidBulletPoint(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .background(AgriForestGreen, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = AppFontWeights.Body,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun CheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = AgriForestGreen)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = AppFontWeights.Body
            )
        )
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: PALM OIL HISTORY & GLOBAL PROFILE
// -------------------------------------------------------------------------------------------------

data class HistoryMilestone(
    val era: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
private fun PalmOilHistoryTab() {
    val milestones = remember {
        listOf(
            HistoryMilestone(
                era = "3000 BCE - Ancient World",
                title = "Indigenous African Origins",
                description = "Palm oil originates from the African oil palm (Elaeis guineensis), native to West and Central Africa. Used for cooking for over 5,000 years, traces were discovered in Egyptian pharaonic tombs (~3000 BCE), transported via early transatlantic and Nile trade.",
                icon = Icons.Default.HistoryEdu,
                tag = "Ancient Heritage"
            ),
            HistoryMilestone(
                era = "15th - 19th Century",
                title = "Colonial Trade & Industrial Revolution",
                description = "European traders (Portuguese, British, Dutch) exported palm oil from West Africa. Following Britain's 1833 abolition of slavery, palm oil surged as the primary 'legitimate trade' commodity. It became indispensable in Europe's Industrial Revolution for industrial machinery lubrication, soaps, and candles.",
                icon = Icons.Default.Gavel,
                tag = "Colonial Trade"
            ),
            HistoryMilestone(
                era = "Early 20th Century (1917)",
                title = "Plantation Expansion to Southeast Asia",
                description = "Colonial powers transplanted oil palm trees to Southeast Asia. The British established commercial plantations in Malaysia in 1917, while the Dutch expanded cultivation in Sumatra, Indonesia, shifting global production epicenters.",
                icon = Icons.Default.Eco,
                tag = "Agronomic Migration"
            ),
            HistoryMilestone(
                era = "Post-WWII - Present Day",
                title = "Supermarket Dominance (85% Supply)",
                description = "Malaysia and Indonesia now supply ~85% of global palm oil. Because of its extraordinarily high yield per hectare (10x higher than soybean or sunflower), low cost, semi-solid texture at room temperature, and extended shelf-life, it is present in ~50% of all packaged supermarket goods.",
                icon = Icons.Default.Percent,
                tag = "Global Dominance"
            ),
            HistoryMilestone(
                era = "1990s - 2000s to Present",
                title = "Deforestation Controversy & RSPO",
                description = "Massive monoculture expansion triggered vast tropical deforestation, peatland drainage, carbon emissions, and loss of critically endangered orangutan habitats. This gave rise to the Roundtable on Sustainable Palm Oil (RSPO) in 2004 and 'No Palm Oil' labeling campaigns.",
                icon = Icons.Default.Warning,
                tag = "Environmental Crisis"
            ),
            HistoryMilestone(
                era = "Health & Nutritional Science",
                title = "Saturated Fats vs. Trans Fat Hazard",
                description = "Palm oil contains ~50% saturated fat (similar to dairy butter). While naturally trans-fat free in unrefined state, the major public health concern arises from industrial partial hydrogenation (producing Vanaspati) and commercial repeated deep-frying, which generate toxic trans-fatty acids.",
                icon = Icons.Default.HealthAndSafety,
                tag = "Nutritional Reality"
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AgriForestGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "The Trajectory of Palm Oil",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = AppFontWeights.Header)
                            )
                            Text(
                                text = "From Ancient Africa to ~50% of Modern Supermarket Items",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = AppFontWeights.Subheader
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Understanding the history of palm oil explains why it replaced indigenous cooking oils in India, why manufacturers mask it under 'Edible Vegetable Oil', and why consumers have a fundamental Right to Know.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = AppFontWeights.Body,
                            lineHeight = 17.sp
                        )
                    )
                }
            }
        }

        items(milestones) { milestone ->
            TimelineMilestoneCard(milestone)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TimelineMilestoneCard(milestone: HistoryMilestone) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AgriSproutMint
                ) {
                    Text(
                        text = milestone.era,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgriForestGreen,
                            fontWeight = AppFontWeights.Header,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = milestone.tag,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = AppFontWeights.Subheader,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = milestone.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = AppFontWeights.Header,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = AppFontWeights.Body,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: OIL SAFETY & ADULTERATION GUIDE
// -------------------------------------------------------------------------------------------------

data class OilProfile(
    val name: String,
    val category: String,
    val saturatedFat: String,
    val transFatStandard: String,
    val healthProfile: String,
    val labelingLoophole: String,
    val isRecommended: Boolean
)

@Composable
private fun OilSafetyGuideTab() {
    val oils = remember {
        listOf(
            OilProfile(
                name = "Palmolein / Palm Oil",
                category = "Industrial Cooking & Frying Base",
                saturatedFat = "~50% Saturated Fat",
                transFatStandard = "FSSAI Max 2.0% (Natural: 0%)",
                healthProfile = "High saturated fat solidifies at room temp. Highly stable for commercial deep frying, but excessive consumption elevates LDL cholesterol.",
                labelingLoophole = "Masked as 'Edible Vegetable Oil' or 'Palmolein' without bold front-of-pack warning.",
                isRecommended = false
            ),
            OilProfile(
                name = "Vanaspati (Hydrogenated Fat)",
                category = "Partially Hydrogenated Vegetable Fat",
                saturatedFat = "High Saturated + Trans Fat Risk",
                transFatStandard = "Strictly Capped at 2.0% (2022 FSSAI)",
                healthProfile = "Major cardiovascular risk factor. Historically loaded with trans fats (up to 20%). FSSAI mandates <=2% limit as of Jan 2022.",
                labelingLoophole = "Often listed as 'Vegetable Shortening' or 'Bakery Fat' in biscuits and sweets.",
                isRecommended = false
            ),
            OilProfile(
                name = "Blended Edible Vegetable Oil",
                category = "Commercial Retail Blend",
                saturatedFat = "Varies by Blend (typically 30-40%)",
                transFatStandard = "FSSAI Max 2.0%",
                healthProfile = "Blends must state percentage of each oil on the front of the pack under FSSAI rules (e.g. 80% Rice Bran, 20% Safflower).",
                labelingLoophole = "Manufacturers use cheap palmolein as bulk component (up to 80%) with a token 20% premium oil.",
                isRecommended = false
            ),
            OilProfile(
                name = "Kachi Ghani Mustard Oil",
                category = "Cold Pressed Traditional Seed Oil",
                saturatedFat = "~7-8% Low Saturated Fat",
                transFatStandard = "0% Trans Fat (Pure Cold Pressed)",
                healthProfile = "Rich in MUFA (Oleic/Erucic), balanced Omega 3 & 6. Natural antimicrobial and cardiovascular support.",
                labelingLoophole = "Must have AGMARK certification; adulteration with Argemone mexicana or palm oil strictly prohibited.",
                isRecommended = true
            ),
            OilProfile(
                name = "Refined Sunflower Oil",
                category = "Extracted Seed Oil",
                saturatedFat = "~10-12% Low Saturated Fat",
                transFatStandard = "FSSAI Max 2.0%",
                healthProfile = "High in Polyunsaturated Fatty Acids (PUFA) and Vitamin E, but susceptible to oxidation during repeated heating.",
                labelingLoophole = "Check for chemical solvent residues (Hexane) and repeated industrial frying degradation.",
                isRecommended = true
            ),
            OilProfile(
                name = "Virgin Coconut Oil",
                category = "Cold Pressed Tropical Kernel Oil",
                saturatedFat = "~86-90% (Medium Chain Triglycerides / MCT)",
                transFatStandard = "0% Trans Fat",
                healthProfile = "High in Lauric acid; rapidly metabolized as ketone energy. Highly heat stable without trans fat generation.",
                labelingLoophole = "Liquid above 24°C; distinguish Cold Pressed Virgin vs. chemical RBD (Refined Bleached Deodorized).",
                isRecommended = true
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cooking Oil Transparency & Safety Guide",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = AppFontWeights.Header)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "In 2022, FSSAI successfully lowered the statutory trans fat limit to 2% in all edible oils and fats. However, consumers still cannot easily tell what oil was used or whether it was repeatedly reheated in commercial kitchens.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = AppFontWeights.Body,
                            lineHeight = 17.sp
                        )
                    )
                }
            }
        }

        items(oils) { oil ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, if (oil.isRecommended) AgriEmerald.copy(alpha = 0.5f) else BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = oil.name,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = AppFontWeights.Header,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (oil.isRecommended) CompliancePassContainer else ComplianceWarningContainer
                        ) {
                            Text(
                                text = oil.saturatedFat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (oil.isRecommended) CompliancePass else Color(0xFF9A6B00),
                                    fontWeight = AppFontWeights.Subheader,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = oil.category,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = AppFontWeights.Subheader,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = oil.healthProfile,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = AppFontWeights.Body,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = ComplianceWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Labeling Loopholes: ${oil.labelingLoophole}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = AppFontWeights.Body,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
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

// -------------------------------------------------------------------------------------------------
// TAB 4: CITIZEN PETITION & PROPOSAL DRAFT
// -------------------------------------------------------------------------------------------------

@Composable
private fun PetitionDraftTab() {
    val context = LocalContext.current
    val proposalText = remember {
        """
        CITIZEN & INSPECTOR PETITION: CONSUMER RIGHT TO KNOW
        FOOD LABEL TRANSPARENCY & STATUTORY QUID PROPOSAL
        
        To:
        1. Chief Executive Officer, Food Safety and Standards Authority of India (FSSAI)
        2. Secretary, Department of Consumer Affairs, Ministry of Consumer Affairs, Food and Public Distribution, Govt. of India
        3. State Legal Metrology Enforcement Directorates
        
        Subject: Statutory Demands for Comprehensive Packaged Food Label Transparency
        
        Respected Authorities,
        
        We, the undersigned consumers, citizens, and legal metrology officers, submit this formal representation regarding critical transparency deficiencies on packaged food labels in India:
        
        1. EXACT MRP PROTECTION:
           Mandate permanent printing of MRP and ban price stickering under Legal Metrology Rules, 2011.
        
        2. UNIVERSAL QUID DISCLOSURE:
           Amend FSSAI Labelling and Display Regulations, 2020 to extend Quantitative Ingredient Declaration (QUID) percentages to all major bulk ingredients (>5%) and cooking bases, ending the loophole where un-emphasized fillers escape numerical disclosure.
        
        3. SPECIFIC OIL TRANSPARENCY:
           Prohibit generic terms like 'Edible Vegetable Oil'. Mandate specific disclosure of the botanical oil (e.g. 'Contains 100% Palmolein Oil') on the Principal Display Panel.
        
        4. TRANS FAT & OIL SAFETY CERTIFICATION:
           Mandate transparent display of trans-fat compliance (<2% FSSAI limit) and cold-pressed vs refined solvent extraction status on retail packs.
        
        5. FRONT-OF-PACK WARNING LABELS (FOPNL):
           Accelerate implementation of high sodium, added sugar, and saturated fat warning badges.
        
        6. VERIFIABLE QUALITY LICENSES:
           Enforce high-contrast display of 14-digit FSSAI licenses and AGMARK certifications with verifiable QR codes.
        
        7. STATUTORY BATCH-TO-BATCH RECIPE CONSISTENCY:
           Enact a legal guarantee against clandestine batch substitutions that dilute nutritional quality.
        
        Submitted in the interest of transparent consumer commerce and public health.
        """.trimIndent()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetrologyNavy)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Food Transparency Petition & Presentation Draft",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = AppFontWeights.Header,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Use this structured text for consumer petitions, collegiate presentations, or submission to FSSAI & consumer grievance forums.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFD8F3DC),
                            fontWeight = AppFontWeights.Subheader
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Food Transparency Petition", proposalText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Petition text copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_copy_petition"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AgriForestGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Petition", fontWeight = AppFontWeights.Header, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, proposalText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Food Transparency Proposal")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_share_petition"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Draft", fontWeight = AppFontWeights.Header, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Display Document Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Petition Text Preview",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = AppFontWeights.Header)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Text(
                            text = proposalText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
