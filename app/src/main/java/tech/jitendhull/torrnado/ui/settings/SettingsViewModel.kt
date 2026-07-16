package tech.jitendhull.torrnado.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.jitendhull.torrnado.data.settings.SettingsManager
import tech.jitendhull.torrnado.domain.model.IndexerType
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.AppTheme
import tech.jitendhull.torrnado.domain.model.AccentTheme
import javax.inject.Inject

data class SettingsUiState(
    val prowlarrUrl: String = "",
    val prowlarrKey: String = "",
    val jackettUrl: String = "",
    val jackettKey: String = "",
    val proxyEnabled: Boolean = false,
    val proxyType: String = "HTTP",
    val proxyHost: String = "",
    val proxyPort: Int = 0,
    val proxyUser: String = "",
    val proxyPass: String = "",
    val movieIndexer: IndexerType = IndexerType.AUTO,
    val showIndexer: IndexerType = IndexerType.AUTO,
    val animeIndexer: IndexerType = IndexerType.AUTO,
    val musicIndexer: IndexerType = IndexerType.AUTO,
    val gameIndexer: IndexerType = IndexerType.AUTO,
    val bookIndexer: IndexerType = IndexerType.AUTO,
    val generalIndexer: IndexerType = IndexerType.AUTO,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val accentTheme: AccentTheme = AccentTheme.CYBERPUNK
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine<Any, SettingsUiState>(
        settingsManager.prowlarrUrl, settingsManager.prowlarrKey,
        settingsManager.jackettUrl, settingsManager.jackettKey,
        settingsManager.proxyEnabled, settingsManager.proxyType,
        settingsManager.proxyHost, settingsManager.proxyPort,
        settingsManager.proxyUser, settingsManager.proxyPass,
        settingsManager.movieIndexer, settingsManager.showIndexer,
        settingsManager.animeIndexer, settingsManager.musicIndexer,
        settingsManager.gameIndexer, settingsManager.bookIndexer,
        settingsManager.generalIndexer,
        settingsManager.appTheme, settingsManager.accentTheme
    ) { args ->
        SettingsUiState(
            prowlarrUrl = args[0] as String,
            prowlarrKey = args[1] as String,
            jackettUrl = args[2] as String,
            jackettKey = args[3] as String,
            proxyEnabled = args[4] as Boolean,
            proxyType = args[5] as String,
            proxyHost = args[6] as String,
            proxyPort = args[7] as Int,
            proxyUser = args[8] as String,
            proxyPass = args[9] as String,
            movieIndexer = args[10] as IndexerType,
            showIndexer = args[11] as IndexerType,
            animeIndexer = args[12] as IndexerType,
            musicIndexer = args[13] as IndexerType,
            gameIndexer = args[14] as IndexerType,
            bookIndexer = args[15] as IndexerType,
            generalIndexer = args[16] as IndexerType,
            appTheme = args[17] as AppTheme,
            accentTheme = args[18] as AccentTheme
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateProwlarr(url: String, key: String) {
        viewModelScope.launch {
            settingsManager.saveProwlarrSettings(url, key)
        }
    }

    fun updateJackett(url: String, key: String) {
        viewModelScope.launch {
            settingsManager.saveJackettSettings(url, key)
        }
    }

    fun updateProxy(enabled: Boolean, type: String, host: String, port: Int, user: String, pass: String) {
        viewModelScope.launch {
            settingsManager.saveProxySettings(enabled, host, port, type, user, pass)
        }
    }

    fun updateIndexer(category: TorrentCategory, indexer: IndexerType) {
        viewModelScope.launch {
            settingsManager.setIndexerForCategory(category, indexer)
        }
    }

    fun updateAppTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsManager.saveAppTheme(theme)
        }
    }

    fun updateAccentTheme(accent: AccentTheme) {
        viewModelScope.launch {
            settingsManager.saveAccentTheme(accent)
        }
    }
}
