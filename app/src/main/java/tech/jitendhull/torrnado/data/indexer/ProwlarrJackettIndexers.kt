package tech.jitendhull.torrnado.data.indexer

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import tech.jitendhull.torrnado.data.settings.SettingsManager
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProwlarrIndexer @Inject constructor(
    private val client: OkHttpClient,
    private val settingsManager: SettingsManager
) : TorrentIndexer {

    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        val baseUrl = runBlocking { settingsManager.prowlarrUrl.first() }
        val apiKey = runBlocking { settingsManager.prowlarrKey.first() }

        if (baseUrl.isBlank() || apiKey.isBlank()) return emptyList()

        val cleanUrl = baseUrl.trim().removeSuffix("/")
        val categoryIds = when (category) {
            TorrentCategory.MOVIES -> "2000"
            TorrentCategory.SHOWS -> "5000"
            TorrentCategory.ANIME -> "5070"
            TorrentCategory.MUSIC -> "3000"
            TorrentCategory.GAMES -> "4000"
            TorrentCategory.BOOKS -> "7000"
            TorrentCategory.GENERAL -> "2000,5000,3000,4000,7000"
        }

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val finalUrl = "$cleanUrl/api/v1/search?query=$encodedQuery&categories=$categoryIds"
        val request = Request.Builder()
            .url(finalUrl)
            .addHeader("X-Api-Key", apiKey)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()

                val jsonArray = Gson().fromJson(bodyString, JsonArray::class.java)
                val results = mutableListOf<TorrentItem>()

                for (element in jsonArray) {
                    val obj = element.asJsonObject
                    val title = obj.get("title")?.asString ?: "Unknown"
                    val infoHash = obj.get("infoHash")?.asString
                    val magnetUrl = obj.get("magnetUrl")?.asString
                    val downloadUrl = obj.get("downloadUrl")?.asString
                    val sizeBytes = obj.get("size")?.asLong ?: 0L
                    val seeders = obj.get("seeders")?.asInt ?: 0
                    val leechers = obj.get("peers")?.asInt ?: 0
                    val indexerName = obj.get("indexer")?.asString ?: "Prowlarr"

                    if (magnetUrl.isNullOrEmpty() && downloadUrl.isNullOrEmpty()) continue

                    val finalMagnet = magnetUrl ?: infoHash?.let {
                        "magnet:?xt=urn:btih:$it&dn=${URLEncoder.encode(title, "UTF-8")}"
                    } ?: ""

                    results.add(
                        TorrentItem(
                            title = title,
                            magnetUrl = finalMagnet,
                            torrentUrl = downloadUrl,
                            size = formatSize(sizeBytes),
                            seeders = seeders,
                            leechers = leechers,
                            category = category,
                            source = indexerName,
                            infoHash = infoHash
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

@Singleton
class JackettIndexer @Inject constructor(
    private val client: OkHttpClient,
    private val settingsManager: SettingsManager
) : TorrentIndexer {

    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        val baseUrl = runBlocking { settingsManager.jackettUrl.first() }
        val apiKey = runBlocking { settingsManager.jackettKey.first() }

        if (baseUrl.isBlank() || apiKey.isBlank()) return emptyList()

        val cleanUrl = baseUrl.trim().removeSuffix("/")
        val categoryId = when (category) {
            TorrentCategory.MOVIES -> "2000"
            TorrentCategory.SHOWS -> "5000"
            TorrentCategory.ANIME -> "5070"
            TorrentCategory.MUSIC -> "3000"
            TorrentCategory.GAMES -> "4000"
            TorrentCategory.BOOKS -> "7000"
            TorrentCategory.GENERAL -> "2000,5000,3000,4000,7000"
        }

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val finalUrl = "$cleanUrl/api/v2.0/indexers/all/results?apikey=$apiKey&Query=$encodedQuery&Category[]=$categoryId"
        val request = Request.Builder()
            .url(finalUrl)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()

                val jsonObject = Gson().fromJson(bodyString, JsonObject::class.java)
                val resultsArray = jsonObject.getAsJsonArray("Results") ?: return emptyList()
                val results = mutableListOf<TorrentItem>()

                for (element in resultsArray) {
                    val obj = element.asJsonObject
                    val title = obj.get("Title")?.asString ?: "Unknown"
                    val infoHash = obj.get("InfoHash")?.asString
                    val magnetUrl = obj.get("MagnetUri")?.asString
                    val downloadUrl = obj.get("Link")?.asString
                    val sizeBytes = obj.get("Size")?.asLong ?: 0L
                    val seeders = obj.get("Seeders")?.asInt ?: 0
                    val leechers = obj.get("Peers")?.asInt ?: 0
                    val trackerName = obj.get("Tracker")?.asString ?: "Jackett"

                    if (magnetUrl.isNullOrEmpty() && downloadUrl.isNullOrEmpty()) continue

                    val finalMagnet = magnetUrl ?: infoHash?.let {
                        "magnet:?xt=urn:btih:$it&dn=${URLEncoder.encode(title, "UTF-8")}"
                    } ?: ""

                    results.add(
                        TorrentItem(
                            title = title,
                            magnetUrl = finalMagnet,
                            torrentUrl = downloadUrl,
                            size = formatSize(sizeBytes),
                            seeders = seeders,
                            leechers = leechers,
                            category = category,
                            source = trackerName,
                            infoHash = infoHash
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}