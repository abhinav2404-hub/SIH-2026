package com.example.ui.guide

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.GroundedHelpResult
import com.example.data.api.GroundedWebSource
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.AppTopBar
import com.example.ui.theme.AgriEmerald
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.AgriSage
import com.example.ui.theme.AgriWheatAmber
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompliancePass
import com.example.ui.theme.ComplianceWarning
import com.example.ui.theme.MetrologyNavy

@Composable
fun LegalMetrologyHelpScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val searchQuery by viewModel.helpSearchQuery.collectAsState()
    val selectedCategory by viewModel.helpSelectedCategory.collectAsState()
    val helpResult by viewModel.helpResult.collectAsState()
    val isSearching by viewModel.isSearchingHelp.collectAsState()
    val context = LocalContext.current

    var searchInput by remember(searchQuery) { mutableStateOf(searchQuery) }

    val quickQuestions = remember {
        listOf(
            "Seeds Act 1966 & Seed labelling requirements (germination %, pure seed %, poison tag)" to "SEEDS_ACT",
            "Fertilizer Control Order (FCO) 1985 bag markings & NPK standards" to "FERTILIZER_FCO",
            "Legal Metrology PCR 2011 Rule 6 mandatory declarations (MRP, USP, Net Qty)" to "LEGAL_METROLOGY",
            "Rule 8 Principal Display Panel (PDP) dimensions & Schedule II minimum font height" to "PDP_FONT",
            "Agricultural & Institutional bulk package exemptions under Rule 26" to "LEGAL_METROLOGY",
            "Insecticides Rules 1971 toxicity color diamond square warning codes" to "PESTICIDES",
            "Legal Metrology Act 2009 Section 36 penalties and compounding fees" to "LEGAL_METROLOGY"
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Statutory Reference & Rules Help",
                subtitle = "Google Search Grounded Knowledge Center",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                currentUser = currentUser
            )
        },
        bottomBar = {
            AppBottomNavBar(
                currentScreen = AppScreen.HELP_SEARCH,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .testTag("help_search_list"),
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
                        selectedTabIndex = 1,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[1]),
                                color = AgriLeafGreen,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = false,
                            onClick = { viewModel.navigateTo(AppScreen.RULE_GUIDE) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Room DB Rules (6, 8, 9)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = true,
                            onClick = {},
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = AgriEmerald
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "AI Search Grounding Help",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriForestGreen
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Grounding Hero Card with Badges
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("help_grounding_hero_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AgriForestGreen
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Public,
                                        contentDescription = null,
                                        tint = AgriHarvestGold,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Legal Metrology & Agri Packaging",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "Google Search Grounded Knowledge Base",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFD8F3DC),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, AgriHarvestGold.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AgriHarvestGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Grounding On",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Get real-time, authoritative reference summaries for Legal Metrology (Packaged Commodities) Rules 2011, Seeds Act 1966, Fertilizer (Control) Order 1985, and Insecticides Rules grounded with official gazette and regulatory sources.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
            }

            // Search Bar Component
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("help_search_input"),
                            placeholder = {
                                Text(
                                    "Ask about rules, seed labels, FCO bags, or MRP/USP...",
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = AgriForestGreen
                                )
                            },
                            trailingIcon = {
                                if (searchInput.isNotBlank()) {
                                    IconButton(onClick = { searchInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.performGroundedHelpSearch(searchInput, selectedCategory)
                                }
                            ),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AgriLeafGreen,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grounding query with Google Search & PCR 2011",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )

                            Button(
                                onClick = {
                                    if (searchInput.isNotBlank()) {
                                        viewModel.performGroundedHelpSearch(searchInput, selectedCategory)
                                    }
                                },
                                enabled = searchInput.isNotBlank() && !isSearching,
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("button_submit_grounded_search"),
                                shape = RoundedCornerShape(19.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AgriForestGreen,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = AgriHarvestGold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Ask AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Topic Preset Chips (One-Tap Grounded Queries)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quick Statutory Questions",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "One-tap AI search",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgriLeafGreen,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickQuestions) { (question, cat) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .clickable {
                                        searchInput = question
                                        viewModel.performGroundedHelpSearch(question, cat)
                                    }
                                    .testTag("quick_question_${cat}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        when (cat) {
                                            "SEEDS_ACT" -> Icons.Default.Grass
                                            "FERTILIZER_FCO" -> Icons.Default.Science
                                            "PDP_FONT" -> Icons.Default.Bookmark
                                            "PESTICIDES" -> Icons.Default.Warning
                                            else -> Icons.Default.Gavel
                                        },
                                        contentDescription = null,
                                        tint = AgriForestGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = question.take(38) + "...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live Grounding Result Card
            item {
                if (isSearching) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, AgriEmerald.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = AgriLeafGreen,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Grounding with Google Search & Statutory Gazettes...",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Synthesizing Legal Metrology Rules, Seeds Act & FCO 1985 provisions",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                } else if (helpResult != null) {
                    val res = helpResult!!
                    GroundedHelpResultView(
                        result = res,
                        onOpenUrl = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Handled safely
                            }
                        }
                    )
                }
            }

            // Statutory Reference Cheatsheets (Expandable Quick Guides)
            item {
                Text(
                    text = "Statutory Regulatory Cheatsheets",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // Cheatsheet 1: Seeds Act, 1966 & Seeds Rules, 1968
            item {
                ExpandableReferenceCard(
                    title = "Seeds Act, 1966 & Labelling Standards",
                    tag = "Seeds Rules 1968 (Rules 7–13)",
                    icon = Icons.Default.Grass,
                    iconColor = AgriLeafGreen,
                    keyPoints = listOf(
                        "Tag Color Classification" to "Breeder Seed (White Tag), Foundation Seed (Opal Green Tag), Certified Seed (Blue Tag), Truthfully Labelled (Opal Green / White with producer mark).",
                        "Mandatory Parameters" to "Kind, Variety, Lot Number, Minimum Germination %, Pure Seed %, Inert Matter %, Other Crop Seeds %, Weed Seed % and Date of Test.",
                        "Treated Seed Poison Warning" to "Must bear skull and crossbones emblem + red label with text: 'POISON: TREATED SEED — NOT FOR HUMAN OR ANIMAL CONSUMPTION'.",
                        "Validity Period" to "Seed test validity is strictly 9 months from date of testing. Re-testing is mandatory for extension."
                    )
                )
            }

            // Cheatsheet 2: Fertilizer (Control) Order, 1985 (FCO)
            item {
                ExpandableReferenceCard(
                    title = "Fertilizer (Control) Order, 1985 (FCO)",
                    tag = "Essential Commodities Act 1955",
                    icon = Icons.Default.Science,
                    iconColor = AgriHarvestGold,
                    keyPoints = listOf(
                        "Standard Bag Weight" to "Standardized 45 kg / 50 kg HDPE woven sacks for Urea, DAP, NPK Complexes; 1 kg, 5 kg, 25 kg for bio-fertilizers.",
                        "Bharat Brand (PMBJUP)" to "Mandatory 2/3rd bag face reserved for 'Bharat' logo and subsidy branding under One Nation One Fertilizer directive.",
                        "Guaranteed Minimum Nutrient %" to "Total Nitrogen (N), Neutral Ammonium Citrate Soluble Phosphate (P2O5), Water Soluble Potash (K2O) and moisture limit.",
                        "Subsidized MRP Declaration" to "MRP must be clearly printed with 'inclusive of GST & applicable central subsidies'.",
                        "Penal Action" to "Misbranding / sub-standard nutrient levels attract criminal prosecution under Section 3/7 of Essential Commodities Act, 1955."
                    )
                )
            }

            // Cheatsheet 3: Legal Metrology PCR 2011 (Rule 6 Declarations)
            item {
                ExpandableReferenceCard(
                    title = "PCR 2011: Rule 6 Mandatory Declarations",
                    tag = "Rule 6(1)(a) to 6(1)(g)",
                    icon = Icons.Default.Gavel,
                    iconColor = AgriForestGreen,
                    keyPoints = listOf(
                        "Rule 6(1)(a) Mfg Address" to "Complete name and address of manufacturer, packer, or importer (including city, state, PIN code).",
                        "Rule 6(1)(b) Commodity Name" to "Generic or common name of the commodity (e.g. 'Cold Pressed Mustard Oil').",
                        "Rule 6(1)(c) Net Quantity" to "Standard metric units (g, kg, ml, l). No non-standard abbreviations like 'gms' or 'kilos'.",
                        "Rule 6(1)(d) Month & Year" to "Month and year of manufacture, packing, or import (e.g. '02/2026' or 'Feb 2026').",
                        "Rule 6(1)(da) MRP Statement" to "Must state 'MRP Rs. XX.XX (inclusive of all taxes)'. Smudged or higher charges are illegal.",
                        "Rule 6(1)(e) Unit Sale Price" to "Mandatory USP per g/kg/ml/litre/number for retail packages.",
                        "Rule 6(1)(g) Consumer Care" to "Name, address, telephone number, and email of grievance redressal officer."
                    )
                )
            }

            // Cheatsheet 4: Rule 8 Principal Display Panel & Schedule II Font Height
            item {
                ExpandableReferenceCard(
                    title = "Rule 8 PDP & Schedule II Minimum Font Height",
                    tag = "Schedule II Area Matrix",
                    icon = Icons.Default.Bookmark,
                    iconColor = MetrologyNavy,
                    keyPoints = listOf(
                        "Rectangular Package PDP" to "At least 40% of total surface area of package (height × width of largest side).",
                        "Cylindrical / Bottle PDP" to "At least 40% of height × circumference or 20% of total container surface area.",
                        "Net Qty ≤ 50g / 50ml" to "Minimum 1.0 mm font height (2.0 mm for blown/embossed letters).",
                        "50g < Net Qty ≤ 200g / ml" to "Minimum 2.0 mm font height (4.0 mm for blown/embossed letters).",
                        "200g < Net Qty ≤ 1 kg / litre" to "Minimum 4.0 mm font height (6.0 mm for blown/embossed letters).",
                        "Net Qty > 1 kg / litre" to "Minimum 6.0 mm font height (6.0 mm for blown/embossed letters).",
                        "Letter Aspect Ratio" to "Width of letter must be at least 1/3rd of its height (excluding '1' and 'I')."
                    )
                )
            }

            // Cheatsheet 5: Insecticides Rules 1971 Toxicity Codes
            item {
                ExpandableReferenceCard(
                    title = "Insecticides Rules 1971: Toxicity Diamond",
                    tag = "CIB&RC Labelling Standards",
                    icon = Icons.Default.Warning,
                    iconColor = Color(0xFFE63946),
                    keyPoints = listOf(
                        "Bright Red Square" to "EXTREMELY TOXIC (Oral LD50: 1–50 mg/kg). Requires skull & crossbones + word 'POISON' in red.",
                        "Bright Yellow Square" to "HIGHLY TOXIC (Oral LD50: 51–500 mg/kg). Requires word 'POISON' and antidote statement.",
                        "Bright Blue Square" to "MODERATELY TOXIC (Oral LD50: 501–5000 mg/kg). Requires word 'DANGER'.",
                        "Bright Green Square" to "SLIGHTLY TOXIC (Oral LD50: > 5000 mg/kg). Requires word 'CAUTION'.",
                        "Registration & Batch" to "Must declare CIB&RC Registration Number, Manufacturing License ID, Antidote instructions, and Expiry Date."
                    )
                )
            }

            // Footer Spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun GroundedHelpResultView(
    result: GroundedHelpResult,
    onOpenUrl: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grounded_help_result_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, AgriEmerald.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Grounding Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AgriHarvestGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Search Grounded Summary",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriForestGreen
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AgriEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Verified Reference",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgriForestGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Query: \"${result.query}\"",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Formatted Summary Text
            Text(
                text = result.summary,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    fontSize = 13.5.sp
                )
            )

            // Search Grounding Queries Section
            if (result.searchQueries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Google Search Queries Executed:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    result.searchQueries.forEach { q ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = q,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Grounding Web Sources & Links
            if (result.sources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Regulatory & Gazette Sources (${result.sources.size}):",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AgriForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.sources.forEach { source ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenUrl(source.uri) }
                                .testTag("grounding_source_${source.title.take(15)}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Language,
                                        contentDescription = null,
                                        tint = AgriLeafGreen,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = source.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                        Text(
                                            text = source.uri,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = AgriLeafGreen,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Icon(
                                    Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open Source Link",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Statutory Reference Badges
            if (result.statutoryReferences.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    result.statutoryReferences.forEach { stat ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AgriWheatAmber.copy(alpha = 0.2f),
                            border = BorderStroke(0.6.dp, AgriHarvestGold.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = AgriHarvestGold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stat,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
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
private fun ExpandableReferenceCard(
    title: String,
    tag: String,
    icon: ImageVector,
    iconColor: Color,
    keyPoints: List<Pair<String, String>>
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cheatsheet_${title.take(15)}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgriLeafGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand/Collapse",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    keyPoints.forEach { (heading, detail) ->
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AgriEmerald,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = heading,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                ),
                                modifier = Modifier.padding(start = 19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
