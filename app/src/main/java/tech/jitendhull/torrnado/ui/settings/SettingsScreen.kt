package tech.jitendhull.torrnado.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.jitendhull.torrnado.domain.model.IndexerType
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.AppTheme
import tech.jitendhull.torrnado.domain.model.AccentTheme

enum class SettingsTab(val displayName: String) {
    APPEARANCE("Appearance"),
    INDEXERS("Indexers"),
    PROVIDERS("Providers"),
    NETWORK("Network")
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var activeTab by remember { mutableStateOf(SettingsTab.APPEARANCE) }

    // Local inputs initialized from state
    var prowlarrUrl by remember { mutableStateOf("") }
    var prowlarrKey by remember { mutableStateOf("") }
    var jackettUrl by remember { mutableStateOf("") }
    var jackettKey by remember { mutableStateOf("") }

    var proxyEnabled by remember { mutableStateOf(false) }
    var proxyType by remember { mutableStateOf("HTTP") }
    var proxyHost by remember { mutableStateOf("") }
    var proxyPort by remember { mutableStateOf("0") }
    var proxyUser by remember { mutableStateOf("") }
    var proxyPass by remember { mutableStateOf("") }

    // Sync state to local fields once loaded
    LaunchedEffect(state) {
        prowlarrUrl = state.prowlarrUrl
        prowlarrKey = state.prowlarrKey
        jackettUrl = state.jackettUrl
        jackettKey = state.jackettKey
        proxyEnabled = state.proxyEnabled
        proxyType = state.proxyType
        proxyHost = state.proxyHost
        proxyPort = state.proxyPort.toString()
        proxyUser = state.proxyUser
        proxyPass = state.proxyPass
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "SETTINGS",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    letterSpacing = 2.sp
                )
            )
        }

        // Tab Selector Row
        TabRow(
            selectedTabIndex = activeTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = primaryColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                    color = primaryColor
                )
            },
            divider = {
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingsTab.values().forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = {
                        Text(
                            text = tab.displayName,
                            fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selectedContentColor = primaryColor,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Content Area with fade/slide animations for fluid transition
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn() + slideInHorizontally { if (targetState.ordinal > initialState.ordinal) it else -it } togetherWith
                            fadeOut() + slideOutHorizontally { if (targetState.ordinal > initialState.ordinal) -it else it }
                },
                label = "settings_tabs_transition"
            ) { targetTab ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    when (targetTab) {
                        SettingsTab.APPEARANCE -> {
                            AppearanceSettings(
                                state = state,
                                onThemeChange = { viewModel.updateAppTheme(it) },
                                onAccentChange = { viewModel.updateAccentTheme(it) }
                            )
                        }
                        SettingsTab.INDEXERS -> {
                            SettingsCard(title = "Category Indexers") {
                                CategoryIndexerRow("Movies", TorrentCategory.MOVIES, state.movieIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.MOVIES, it)
                                }
                                CategoryIndexerRow("TV Shows", TorrentCategory.SHOWS, state.showIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.SHOWS, it)
                                }
                                CategoryIndexerRow("Anime", TorrentCategory.ANIME, state.animeIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.ANIME, it)
                                }
                                CategoryIndexerRow("Music", TorrentCategory.MUSIC, state.musicIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.MUSIC, it)
                                }
                                CategoryIndexerRow("Games", TorrentCategory.GAMES, state.gameIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.GAMES, it)
                                }
                                CategoryIndexerRow("Books", TorrentCategory.BOOKS, state.bookIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.BOOKS, it)
                                }
                                CategoryIndexerRow("General", TorrentCategory.GENERAL, state.generalIndexer) {
                                    viewModel.updateIndexer(TorrentCategory.GENERAL, it)
                                }
                            }
                        }
                        SettingsTab.PROVIDERS -> {
                            // Prowlarr Section
                            SettingsCard(title = "Prowlarr Configuration") {
                                OutlinedTextField(
                                    value = prowlarrUrl,
                                    onValueChange = { prowlarrUrl = it },
                                    label = { Text("Base URL (e.g. http://192.168.1.100:9696)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = settingsTextFieldColors()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = prowlarrKey,
                                    onValueChange = { prowlarrKey = it },
                                    label = { Text("API Key") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = settingsTextFieldColors()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.updateProwlarr(prowlarrUrl, prowlarrKey) },
                                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Save Prowlarr", color = MaterialTheme.colorScheme.onSecondary)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Jackett Section
                            SettingsCard(title = "Jackett Configuration") {
                                OutlinedTextField(
                                    value = jackettUrl,
                                    onValueChange = { jackettUrl = it },
                                    label = { Text("Base URL (e.g. http://192.168.1.100:9117)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = settingsTextFieldColors()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = jackettKey,
                                    onValueChange = { jackettKey = it },
                                    label = { Text("API Key") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = settingsTextFieldColors()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.updateJackett(jackettUrl, jackettKey) },
                                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Save Jackett", color = MaterialTheme.colorScheme.onSecondary)
                                }
                            }
                        }
                        SettingsTab.NETWORK -> {
                            SettingsCard(title = "Geo-Bypass Proxy Settings") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Enable Proxy",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Switch(
                                        checked = proxyEnabled,
                                        onCheckedChange = { proxyEnabled = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = primaryColor,
                                            checkedTrackColor = secondaryColor
                                        )
                                    )
                                }

                                AnimatedVisibility(visible = proxyEnabled) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Proxy Type Selection
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            listOf("HTTP", "SOCKS5").forEach { type ->
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.clickable { proxyType = type }
                                                ) {
                                                    RadioButton(
                                                        selected = proxyType == type,
                                                        onClick = { proxyType = type },
                                                        colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                                                    )
                                                    Text(type, color = MaterialTheme.colorScheme.onBackground)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = proxyHost,
                                            onValueChange = { proxyHost = it },
                                            label = { Text("Proxy Host / IP") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = settingsTextFieldColors()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = proxyPort,
                                            onValueChange = { proxyPort = it },
                                            label = { Text("Proxy Port") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = settingsTextFieldColors()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = proxyUser,
                                            onValueChange = { proxyUser = it },
                                            label = { Text("Username (Optional)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = settingsTextFieldColors()
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        OutlinedTextField(
                                            value = proxyPass,
                                            onValueChange = { proxyPass = it },
                                            label = { Text("Password (Optional)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            colors = settingsTextFieldColors()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val port = proxyPort.toIntOrNull() ?: 0
                                        viewModel.updateProxy(proxyEnabled, proxyType, proxyHost, port, proxyUser, proxyPass)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = secondaryColor),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Save Proxy Settings", color = MaterialTheme.colorScheme.onSecondary)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun AppearanceSettings(
    state: SettingsUiState,
    onThemeChange: (AppTheme) -> Unit,
    onAccentChange: (AccentTheme) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    SettingsCard(title = "App Theme") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTheme.values().forEach { theme ->
                val isSelected = state.appTheme == theme
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onThemeChange(theme) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    SettingsCard(title = "Accent Color Scheme") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AccentTheme.values().forEach { accent ->
                val isSelected = state.accentTheme == accent
                val (color1, color2) = when (accent) {
                    AccentTheme.CYBERPUNK -> Pair(Color(0xFF00F0FF), Color(0xFF8B5CF6))
                    AccentTheme.OCEAN -> Pair(Color(0xFF38BDF8), Color(0xFF60A5FA))
                    AccentTheme.FOREST -> Pair(Color(0xFF34D399), Color(0xFF059669))
                    AccentTheme.SUNSET -> Pair(Color(0xFFF97316), Color(0xFFF43F5E))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onAccentChange(accent) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = accent.displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(color1)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(color2)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun CategoryIndexerRow(
    categoryLabel: String,
    category: TorrentCategory,
    currentIndexer: IndexerType,
    onIndexerSelected: (IndexerType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val availableIndexers = when (category) {
        TorrentCategory.MOVIES -> listOf(IndexerType.AUTO, IndexerType.YTS, IndexerType.PIRATE_BAY, IndexerType.PROWLARR, IndexerType.JACKETT)
        TorrentCategory.SHOWS -> listOf(IndexerType.AUTO, IndexerType.EZTV, IndexerType.PIRATE_BAY, IndexerType.PROWLARR, IndexerType.JACKETT)
        TorrentCategory.ANIME -> listOf(IndexerType.AUTO, IndexerType.NYAA, IndexerType.PIRATE_BAY, IndexerType.PROWLARR, IndexerType.JACKETT)
        else -> listOf(IndexerType.AUTO, IndexerType.PIRATE_BAY, IndexerType.ONE337X, IndexerType.PROWLARR, IndexerType.JACKETT)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoryLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentIndexer.name,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                availableIndexers.forEach { indexer ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = indexer.name,
                                color = if (indexer == currentIndexer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onIndexerSelected(indexer)
                            expanded = false
                        },
                        trailingIcon = {
                            if (indexer == currentIndexer) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun settingsTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)