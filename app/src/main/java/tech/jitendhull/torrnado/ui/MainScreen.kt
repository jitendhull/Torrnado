package tech.jitendhull.torrnado.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.jitendhull.torrnado.ui.search.SearchScreen
import tech.jitendhull.torrnado.ui.search.SearchViewModel
import tech.jitendhull.torrnado.ui.settings.SettingsScreen
import tech.jitendhull.torrnado.ui.settings.SettingsViewModel
import tech.jitendhull.torrnado.ui.transfers.TransfersScreen
import tech.jitendhull.torrnado.ui.feeds.FeedsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by rememberSaveable { mutableStateOf("search") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                // Search Tab
                NavigationBarItem(
                    selected = activeTab == "search",
                    onClick = { activeTab = "search" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Search") },
                    label = { Text("Search") }
                )

                // Downloads Tab (with count badge)
                NavigationBarItem(
                    selected = activeTab == "downloads",
                    onClick = { activeTab = "downloads" },
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text("1") // 1 active download
                                }
                            }
                        ) {
                            Icon(Icons.Default.List, contentDescription = "Downloads")
                        }
                    },
                    label = { Text("Transfers") }
                )

                // Feeds Tab (with dot badge)
                NavigationBarItem(
                    selected = activeTab == "feeds",
                    onClick = { activeTab = "feeds" },
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) // Dot badge
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Explore Feeds")
                        }
                    },
                    label = { Text("Explore") }
                )

                // Settings Tab
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = { activeTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    "search" -> SearchScreen(viewModel = searchViewModel)
                    "downloads" -> TransfersScreen()
                    "feeds" -> FeedsScreen()
                    "settings" -> SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}