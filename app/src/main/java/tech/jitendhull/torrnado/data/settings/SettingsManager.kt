package tech.jitendhull.torrnado.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.jitendhull.torrnado.domain.model.IndexerType
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.AppTheme
import tech.jitendhull.torrnado.domain.model.AccentTheme
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "torrnado_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_PROWLARR_URL = stringPreferencesKey("prowlarr_url")
        private val KEY_PROWLARR_KEY = stringPreferencesKey("prowlarr_key")
        private val KEY_JACKETT_URL = stringPreferencesKey("jackett_url")
        private val KEY_JACKETT_KEY = stringPreferencesKey("jackett_key")

        private val KEY_PROXY_ENABLED = booleanPreferencesKey("proxy_enabled")
        private val KEY_PROXY_HOST = stringPreferencesKey("proxy_host")
        private val KEY_PROXY_PORT = intPreferencesKey("proxy_port")
        private val KEY_PROXY_TYPE = stringPreferencesKey("proxy_type")
        private val KEY_PROXY_USER = stringPreferencesKey("proxy_user")
        private val KEY_PROXY_PASS = stringPreferencesKey("proxy_pass")
        
        private val KEY_APP_THEME = stringPreferencesKey("app_theme")
        private val KEY_ACCENT_THEME = stringPreferencesKey("accent_theme")
    }

    // Dynamic key generation for category indexers
    private fun getCategoryKey(category: TorrentCategory): Preferences.Key<String> {
        return stringPreferencesKey("indexer_for_${category.name.lowercase()}")
    }

    fun getIndexerForCategory(category: TorrentCategory): Flow<IndexerType> {
        return dataStore.data.map { preferences ->
            val value = preferences[getCategoryKey(category)] ?: IndexerType.AUTO.name
            try {
                IndexerType.valueOf(value)
            } catch (e: Exception) {
                IndexerType.AUTO
            }
        }
    }

    suspend fun setIndexerForCategory(category: TorrentCategory, indexerType: IndexerType) {
        dataStore.edit { preferences ->
            preferences[getCategoryKey(category)] = indexerType.name
        }
    }

    val movieIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.MOVIES)
    val showIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.SHOWS)
    val animeIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.ANIME)
    val musicIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.MUSIC)
    val gameIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.GAMES)
    val bookIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.BOOKS)
    val generalIndexer: Flow<IndexerType> = getIndexerForCategory(TorrentCategory.GENERAL)

    val prowlarrUrl: Flow<String> = dataStore.data.map { it[KEY_PROWLARR_URL] ?: "" }
    val prowlarrKey: Flow<String> = dataStore.data.map { it[KEY_PROWLARR_KEY] ?: "" }
    val jackettUrl: Flow<String> = dataStore.data.map { it[KEY_JACKETT_URL] ?: "" }
    val jackettKey: Flow<String> = dataStore.data.map { it[KEY_JACKETT_KEY] ?: "" }

    suspend fun saveProwlarrSettings(url: String, key: String) {
        dataStore.edit {
            it[KEY_PROWLARR_URL] = url
            it[KEY_PROWLARR_KEY] = key
        }
    }

    suspend fun saveJackettSettings(url: String, key: String) {
        dataStore.edit {
            it[KEY_JACKETT_URL] = url
            it[KEY_JACKETT_KEY] = key
        }
    }

    val proxyEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_PROXY_ENABLED] ?: false }
    val proxyHost: Flow<String> = dataStore.data.map { it[KEY_PROXY_HOST] ?: "" }
    val proxyPort: Flow<Int> = dataStore.data.map { it[KEY_PROXY_PORT] ?: 8080 }
    val proxyType: Flow<String> = dataStore.data.map { it[KEY_PROXY_TYPE] ?: "HTTP" }
    val proxyUser: Flow<String> = dataStore.data.map { it[KEY_PROXY_USER] ?: "" }
    val proxyPass: Flow<String> = dataStore.data.map { it[KEY_PROXY_PASS] ?: "" }

    val appTheme: Flow<AppTheme> = dataStore.data.map { preferences ->
        val value = preferences[KEY_APP_THEME] ?: AppTheme.SYSTEM.name
        try { AppTheme.valueOf(value) } catch (e: Exception) { AppTheme.SYSTEM }
    }

    val accentTheme: Flow<AccentTheme> = dataStore.data.map { preferences ->
        val value = preferences[KEY_ACCENT_THEME] ?: AccentTheme.CYBERPUNK.name
        try { AccentTheme.valueOf(value) } catch (e: Exception) { AccentTheme.CYBERPUNK }
    }

    suspend fun saveProxySettings(
        enabled: Boolean,
        host: String,
        port: Int,
        type: String,
        user: String,
        pass: String
    ) {
        dataStore.edit {
            it[KEY_PROXY_ENABLED] = enabled
            it[KEY_PROXY_HOST] = host
            it[KEY_PROXY_PORT] = port
            it[KEY_PROXY_TYPE] = type
            it[KEY_PROXY_USER] = user
            it[KEY_PROXY_PASS] = pass
        }
    }

    suspend fun saveAppTheme(theme: AppTheme) {
        dataStore.edit { it[KEY_APP_THEME] = theme.name }
    }

    suspend fun saveAccentTheme(accent: AccentTheme) {
        dataStore.edit { it[KEY_ACCENT_THEME] = accent.name }
    }
}
