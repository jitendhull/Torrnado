package tech.jitendhull.torrnado.domain.model

enum class TorrentCategory(val displayName: String) {
    MOVIES("Movies"),
    SHOWS("TV Shows"),
    ANIME("Anime"),
    MUSIC("Music"),
    GAMES("Games"),
    BOOKS("Books"),
    GENERAL("General")
}

enum class IndexerType(val displayName: String) {
    AUTO("Auto (Direct Scrapers)"),
    YTS("YTS (Movies only)"),
    EZTV("EZTV (TV Shows only)"),
    NYAA("Nyaa.si (Anime only)"),
    ONE337X("1337x"),
    PIRATE_BAY("The Pirate Bay"),
    PROWLARR("Prowlarr"),
    JACKETT("Jackett")
}

enum class AppTheme(val displayName: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AccentTheme(val displayName: String) {
    CYBERPUNK("Cyberpunk (Cyan/Violet)"),
    OCEAN("Ocean Breeze (Teal/Blue)"),
    FOREST("Emerald Mint (Green/Teal)"),
    SUNSET("Amber Sunset (Orange/Rose)")
}

data class TorrentItem(
    val title: String,
    val magnetUrl: String,
    val torrentUrl: String?,
    val size: String,
    val seeders: Int,
    val leechers: Int,
    val category: TorrentCategory,
    val source: String,
    val infoHash: String? = null
)