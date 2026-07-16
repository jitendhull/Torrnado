package tech.jitendhull.torrnado.data.indexer

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface TorrentIndexer {
    fun search(query: String, category: TorrentCategory): List<TorrentItem>
}

fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    val group = digitGroups.coerceAtMost(units.size - 1)
    return String.format(Locale.US, "%.2f %s", bytes / Math.pow(1024.0, group.toDouble()), units[group])
}

@Singleton
class PirateBayIndexer @Inject constructor(
    private val client: OkHttpClient
) : TorrentIndexer {
    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://apibay.org/q.php?q=$encodedQuery"
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()
                if (bodyString.trim() == "[]" || bodyString.trim() == "null") return emptyList()

                val jsonArray = Gson().fromJson(bodyString, JsonArray::class.java)
                val results = mutableListOf<TorrentItem>()

                for (element in jsonArray) {
                    val obj = element.asJsonObject
                    val id = obj.get("id")?.asString ?: continue
                    if (id == "0") continue // No results found dummy object from Apibay

                    val name = obj.get("name")?.asString ?: "Unknown"
                    val infoHash = obj.get("info_hash")?.asString ?: continue
                    val seeders = obj.get("seeders")?.asString?.toIntOrNull() ?: 0
                    val leechers = obj.get("leechers")?.asString?.toIntOrNull() ?: 0
                    val sizeBytes = obj.get("size")?.asString?.toLongOrNull() ?: 0L

                    val magnet = "magnet:?xt=urn:btih:$infoHash&dn=${URLEncoder.encode(name, "UTF-8")}" +
                            "&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce" +
                            "&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80%2Fannounce" +
                            "&tr=udp%3A%2F%2Fopentracker.i2p.rocks%3A6969%2Fannounce" +
                            "&tr=udp%3A%2F%2Ftracker.internetwarriors.net%3A1337%2Fannounce"

                    results.add(
                        TorrentItem(
                            title = name,
                            magnetUrl = magnet,
                            torrentUrl = null,
                            size = formatSize(sizeBytes),
                            seeders = seeders,
                            leechers = leechers,
                            category = category,
                            source = "The Pirate Bay",
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
class YtsIndexer @Inject constructor(
    private val client: OkHttpClient
) : TorrentIndexer {
    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        // YTS only supports Movies
        if (category != TorrentCategory.MOVIES) return emptyList()

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://yts.mx/api/v2/list_movies.json?query_term=$encodedQuery"
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()

                val jsonObject = Gson().fromJson(bodyString, JsonObject::class.java)
                val status = jsonObject.get("status")?.asString ?: "failed"
                if (status != "ok") return emptyList()

                val data = jsonObject.getAsJsonObject("data") ?: return emptyList()
                val movies = data.getAsJsonArray("movies") ?: return emptyList()

                val results = mutableListOf<TorrentItem>()
                for (movieElement in movies) {
                    val movie = movieElement.asJsonObject
                    val title = movie.get("title_long")?.asString ?: movie.get("title")?.asString ?: "Unknown"
                    val torrents = movie.getAsJsonArray("torrents") ?: continue

                    for (torrentElement in torrents) {
                        val torrent = torrentElement.asJsonObject
                        val hash = torrent.get("hash")?.asString ?: continue
                        val quality = torrent.get("quality")?.asString ?: "720p"
                        val size = torrent.get("size")?.asString ?: "0 B"
                        val seeds = torrent.get("seeds")?.asInt ?: 0
                        val peers = torrent.get("peers")?.asInt ?: 0
                        val torrentUrl = torrent.get("url")?.asString

                        val name = "$title [$quality]"
                        val magnet = "magnet:?xt=urn:btih:$hash&dn=${URLEncoder.encode(name, "UTF-8")}" +
                                "&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969%2Fannounce" +
                                "&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80%2Fannounce"

                        results.add(
                            TorrentItem(
                                title = name,
                                magnetUrl = magnet,
                                torrentUrl = torrentUrl,
                                size = size,
                                seeders = seeds,
                                leechers = peers,
                                category = TorrentCategory.MOVIES,
                                source = "YTS",
                                infoHash = hash
                            )
                        )
                    }
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
class NyaaIndexer @Inject constructor(
    private val client: OkHttpClient
) : TorrentIndexer {
    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        // Nyaa only supports Anime
        if (category != TorrentCategory.ANIME) return emptyList()

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://nyaa.si/?page=rss&q=$encodedQuery"
        val request = Request.Builder().url(url).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()

                val doc = Jsoup.parse(bodyString, "", Parser.xmlParser())
                val items = doc.select("item")
                val results = mutableListOf<TorrentItem>()

                for (item in items) {
                    val title = item.select("title").text()
                    val torrentUrl = item.select("link").text()
                    val infoHash = item.select("nyaa|infoHash").text().ifEmpty {
                        item.select("infoHash").text()
                    }
                    val size = item.select("nyaa|size").text().ifEmpty {
                        item.select("size").text()
                    }.ifEmpty { "Unknown" }

                    val seeders = item.select("nyaa|seeders").text().ifEmpty {
                        item.select("seeders").text()
                    }.toIntOrNull() ?: 0

                    val leechers = item.select("nyaa|leechers").text().ifEmpty {
                        item.select("leechers").text()
                    }.toIntOrNull() ?: 0

                    val magnet = if (infoHash.isNotEmpty()) {
                        "magnet:?xt=urn:btih:$infoHash&dn=${URLEncoder.encode(title, "UTF-8")}"
                    } else {
                        // Fallback if link is a magnet
                        if (torrentUrl.startsWith("magnet:")) torrentUrl else ""
                    }

                    results.add(
                        TorrentItem(
                            title = title,
                            magnetUrl = magnet,
                            torrentUrl = if (torrentUrl.startsWith("http")) torrentUrl else null,
                            size = size,
                            seeders = seeders,
                            leechers = leechers,
                            category = TorrentCategory.ANIME,
                            source = "Nyaa.si",
                            infoHash = infoHash.ifEmpty { null }
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
class EztvIndexer @Inject constructor(
    private val client: OkHttpClient
) : TorrentIndexer {
    override fun search(query: String, category: TorrentCategory): List<TorrentItem> {
        // EZTV only supports TV Shows
        if (category != TorrentCategory.SHOWS) return emptyList()

        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        // EZTV uses eztv.re/api/get-torrents. It can search TV shows by title or IMDb.
        // The API returns the latest torrents. We query by setting a limit or searching.
        // Wait, their official search URL is https://eztv.re/api/get-torrents?limit=100
        // Actually, we can use search via query in EZTV if it is supported: `eztv.re/api/get-torrents?limit=100&query={query}`
        // Let's call it:
        val url = "https://eztv.re/api/get-torrents?limit=100&imdb_id=" // Usually needs imdb_id but query param can match.
        // Since EZTV API is IMDb-centric, let's scrape EZTV's search page instead or fallback to Pirate Bay if API fails,
        // or search EZTV feed.
        // EZTV search page: https://eztv.re/search/{query}
        val searchUrl = "https://eztv.re/search/$encodedQuery"
        val request = Request.Builder().url(searchUrl).build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val bodyString = response.body?.string() ?: return emptyList()

                val doc = Jsoup.parse(bodyString)
                val rows = doc.select("tr.forum_header_border")
                val results = mutableListOf<TorrentItem>()

                for (row in rows) {
                    val cols = row.select("td")
                    if (cols.size < 5) continue

                    val titleCol = cols[1]
                    val linkElement = titleCol.select("a.epinfo").first() ?: continue
                    val title = linkElement.text()

                    val downloadCol = cols[2]
                    val magnetElement = downloadCol.select("a.magnet").first()
                    val magnetUrl = magnetElement?.attr("href") ?: ""

                    val torrentElement = downloadCol.select("a.download_1, a.download_2").first()
                    val torrentUrl = torrentElement?.attr("href")

                    val sizeCol = cols[3]
                    val size = sizeCol.text()

                    val seedsCol = cols[5]
                    val seeders = seedsCol.text().toIntOrNull() ?: 0

                    // Eztv doesn't show leechers clearly in columns, but column 6 is peers
                    val leechers = 0

                    if (magnetUrl.isNotEmpty()) {
                        results.add(
                            TorrentItem(
                                title = title,
                                magnetUrl = magnetUrl,
                                torrentUrl = torrentUrl,
                                size = size,
                                seeders = seeders,
                                leechers = leechers,
                                category = TorrentCategory.SHOWS,
                                source = "EZTV"
                            )
                        )
                    }
                }
                results
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}