package tech.jitendhull.torrnado.domain.repository

import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem

interface TorrentRepository {
    suspend fun searchTorrents(query: String, category: TorrentCategory): List<TorrentItem>
}