package tech.jitendhull.torrnado

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Test
import tech.jitendhull.torrnado.domain.model.TorrentCategory
import tech.jitendhull.torrnado.domain.model.TorrentItem

class TorrentAppTest {

    @Test
    fun testTorznabParsing() {
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <rss version="2.0" xmlns:torznab="http://torznab.com/schemas/2015/feed">
                <channel>
                    <item>
                        <title>Big Buck Bunny (2008) [1080p] [Movies]</title>
                        <guid>http://example.com/torrent/1</guid>
                        <link>http://example.com/download/1.torrent</link>
                        <pubDate>Mon, 15 Jun 2026 12:00:00 +0000</pubDate>
                        <size>262144000</size>
                        <enclosure url="magnet:?xt=urn:btih:bunnyhash&dn=Big+Buck+Bunny" type="application/x-bittorrent" length="262144000"/>
                        <torznab:attr name="seeders" value="42"/>
                        <torznab:attr name="peers" value="10"/>
                        <torznab:attr name="category" value="2000"/>
                    </item>
                </channel>
            </rss>
        """.trimIndent()

        val doc = Jsoup.parse(xml, "", Parser.xmlParser())
        val itemElements = doc.select("item")
        assertEquals(1, itemElements.size)

        val itemEl = itemElements.first()!!
        val title = itemEl.select("title").text()
        val guid = itemEl.select("guid").text()
        
        var magnetUrl = ""
        val enclosure = itemEl.select("enclosure")
        if (enclosure.isNotEmpty()) {
            val url = enclosure.attr("url")
            if (url.startsWith("magnet:")) {
                magnetUrl = url
            }
        }
        if (magnetUrl.isEmpty()) {
            val link = itemEl.select("link").text()
            if (link.startsWith("magnet:")) {
                magnetUrl = link
            }
        }

        val size = itemEl.select("size").text().toLongOrNull() ?: 0L
        
        var seeders = 0
        var leechers = 0
        val attrs = itemEl.select("torznab|attr, attr")
        for (attr in attrs) {
            val name = attr.attr("name")
            val value = attr.attr("value")
            if (name == "seeders") {
                seeders = value.toIntOrNull() ?: 0
            } else if (name == "peers" || name == "leechers") {
                leechers = value.toIntOrNull() ?: 0
            }
        }

        val torrentItem = TorrentItem(
            title = title,
            magnetUrl = magnetUrl,
            torrentUrl = "http://example.com/download/1.torrent",
            size = "${size / (1024 * 1024)} MB",
            seeders = seeders,
            leechers = leechers,
            category = TorrentCategory.MOVIES,
            source = "MockIndexer",
            infoHash = guid
        )

        assertEquals("Big Buck Bunny (2008) [1080p] [Movies]", torrentItem.title)
        assertEquals("magnet:?xt=urn:btih:bunnyhash&dn=Big+Buck+Bunny", torrentItem.magnetUrl)
        assertEquals("250 MB", torrentItem.size)
        assertEquals(42, torrentItem.seeders)
        assertEquals(10, torrentItem.leechers)
        assertEquals("MockIndexer", torrentItem.source)
    }
}
