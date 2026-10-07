package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.auth.OfficerUser
import com.example.ui.AppScreen
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriHarvestGold
import com.example.ui.theme.AgriLeafGreen
import com.example.ui.theme.AgriSproutMint
import com.example.ui.theme.AgriWheatAmber
import androidx.compose.material.icons.filled.Settings
import com.example.ui.theme.SaffronIndia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    currentUser: OfficerUser? = null,
    onLogoutClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null
) {
    Surface(
        color = AgriForestGreen,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Tricolor Top Accent Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(SaffronIndia)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(Color(0xFF138808))
                )
            }

            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AgriLeafGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Agriculture,
                                contentDescription = "Mudra Check Emblem",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = subtitle ?: "Department of Consumer Affairs • Legal Metrology Wing",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFD8F3DC),
                                    fontSize = 10.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    if (onSettingsClick != null) {
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.testTag("topbar_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                    if (currentUser != null && onLogoutClick != null) {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgriForestGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun AppBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.DASHBOARD,
            onClick = { onNavigate(AppScreen.DASHBOARD) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home Overview & Safety Summary") },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_dashboard"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSproutMint
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SCANNER || currentScreen == AppScreen.CAMERA,
            onClick = { onNavigate(AppScreen.SCANNER) },
            icon = {
                Icon(
                    Icons.Default.QrCodeScanner,
                    contentDescription = "Scan Package: Check Price, Weight, Expiry & Ingredients"
                )
            },
            label = { Text("Scan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_scanner"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSproutMint
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.HISTORY,
            onClick = { onNavigate(AppScreen.HISTORY) },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "Saved Scans & Past Package Reports") },
            label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_history"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSproutMint
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.TOOLS,
            onClick = { onNavigate(AppScreen.TOOLS) },
            icon = { Icon(Icons.Default.Calculate, contentDescription = "Tools: Fair Price per 100g & Letter Height Calculators") },
            label = { Text("Tools", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_tools"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSproutMint
            )
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.RULE_GUIDE || currentScreen == AppScreen.HELP_SEARCH,
            onClick = { onNavigate(AppScreen.HELP_SEARCH) },
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Guide & Rules: Plain Language Law & FAQ") },
            label = { Text("Guide", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.testTag("nav_guide"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriForestGreen,
                indicatorColor = AgriSproutMint
            )
        )
    }
}
