package tech.jitendhull.torrnado.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.jitendhull.torrnado.domain.model.IndexerType
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.ui.theme.CyberCyan
import tech.jitendhull.torrnado.ui.theme.ElectricViolet

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 1. Indexers Settings Section
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

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Prowlarr Section
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
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Prowlarr")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Jackett Section
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
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Jackett")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Proxy Config Section
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
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = ElectricViolet
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
                                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
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
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Proxy Settings")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
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

    // Filter indexers that are valid for the specific category to prevent bad user selection
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
                    color = CyberCyan
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
                                color = if (indexer == currentIndexer) CyberCyan else MaterialTheme.colorScheme.onSurface
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
                                    tint = CyberCyan
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
    focusedBorderColor = CyberCyan,
    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedLabelColor = CyberCyan,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)