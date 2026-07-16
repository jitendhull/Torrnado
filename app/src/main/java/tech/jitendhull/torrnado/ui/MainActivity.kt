package tech.jitendhull.torrnado.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import tech.jitendhull.torrnado.ui.search.SearchViewModel
import tech.jitendhull.torrnado.ui.settings.SettingsViewModel
import tech.jitendhull.torrnado.ui.theme.TorrnadoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val searchViewModel: SearchViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsState by settingsViewModel.uiState.collectAsState()
            TorrnadoTheme(
                appTheme = settingsState.appTheme,
                accentTheme = settingsState.accentTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        searchViewModel = searchViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
