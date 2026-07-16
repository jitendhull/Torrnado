package tech.jitendhull.torrnado.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import tech.jitendhull.torrnado.data.indexer.EztvIndexer
import tech.jitendhull.torrnado.data.indexer.JackettIndexer
import tech.jitendhull.torrnado.data.indexer.NyaaIndexer
import tech.jitendhull.torrnado.data.indexer.PirateBayIndexer
import tech.jitendhull.torrnado.data.indexer.ProwlarrIndexer
import tech.jitendhull.torrnado.data.indexer.YtsIndexer
import tech.jitendhull.torrnado.data.settings.SettingsManager
import tech.jitendhull.torrnado.domain.model.IndexerType
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem
import tech.jitendhull.torrnado.domain.repository.TorrentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentRepositoryImpl @Inject constructor(
    private val settingsManager: SettingsManager,
    private val pirateBayIndexer: PirateBayIndexer,
    private val ytsIndexer: YtsIndexer,
    private val nyaaIndexer: NyaaIndexer,
    private val eztvIndexer: EztvIndexer,
    private val prowlarrIndexer: ProwlarrIndexer,
    private val jackettIndexer: JackettIndexer
) : TorrentRepository {

    override suspend fun searchTorrents(query: String, category: TorrentCategory): List<TorrentItem> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) return@withContext emptyList()

            val indexerType = settingsManager.getIndexerForCategory(category).first()

            val activeIndexer = when (indexerType) {
                IndexerType.AUTO -> when (category) {
                    TorrentCategory.MOVIES -> ytsIndexer
                    TorrentCategory.SHOWS -> eztvIndexer
                    TorrentCategory.ANIME -> nyaaIndexer
                    else -> pirateBayIndexer
                }
                IndexerType.YTS -> {
                    if (category == TorrentCategory.MOVIES) ytsIndexer else pirateBayIndexer
                }
                IndexerType.EZTV -> {
                    if (category == TorrentCategory.SHOWS) eztvIndexer else pirateBayIndexer
                }
                IndexerType.NYAA -> {
                    if (category == TorrentCategory.ANIME) nyaaIndexer else pirateBayIndexer
                }
                IndexerType.PIRATE_BAY, IndexerType.ONE337X -> pirateBayIndexer
                IndexerType.PROWLARR -> prowlarrIndexer
                IndexerType.JACKETT -> jackettIndexer
            }

            try {
                activeIndexer.search(query, category)
                    .sortedByDescending { it.seeders }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
}