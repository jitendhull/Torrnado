# Hermes Agent Prompt: Torrent Tracker Android App

**Role:** Expert Android Developer & System Architect

**Task:** Build a modern, native Android application using Kotlin and Jetpack Compose for searching, tracking, and organizing torrents. The app must include built-in, user-accessible proxy support to bypass regional blocks on popular torrent trackers.

## System Design & Architecture

### Core Technologies
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles (Separation of Concerns: Data, Domain, UI layers).
*   **Concurrency:** Kotlin Coroutines and Flow
*   **Dependency Injection:** Hilt
*   **Networking:** OkHttp + Retrofit. *Crucial:* OkHttp will be used to intercept and route traffic through user-defined HTTP/SOCKS5 proxies.
*   **Local Storage:** Room Database (for saving torrent metadata, watchlists, and history) + DataStore (for user preferences, specifically the proxy network settings).
*   **HTML Parsing (Fallback):** Jsoup (for scraping trackers that lack standard APIs).

### Required Features
1.  **Proxy Configuration Engine:** A dedicated settings screen where users can input Custom HTTP/SOCKS5 proxy details. The OkHttp client must dynamically read these settings from DataStore and route requests through the proxy to bypass ISP blocks on torrent sites.
2.  **Unified Search Interface:** Search across multiple configured trackers simultaneously.
3.  **Torrent Organization:** Categories like "Wishlist", "Downloading" (tracked via external client integration), and "Completed". 
4.  **Magnet Link Handling:** Clicking a magnet link should correctly trigger an implicit intent (`ACTION_VIEW`) to open installed torrent clients (e.g., Flud, BitTorrent, uTorrent).

### Recommended MCP Servers to Utilize
*   **Prowlarr / Jackett MCP:** An MCP server that interfaces with a Prowlarr or Jackett instance. This abstracts away the immense complexity of scraping individual torrent sites and provides a unified, standardized API for querying hundreds of trackers at once.
*   **Web Scraper MCP (Alternative):** If a Prowlarr instance is unavailable, an MCP server capable of headless browsing (e.g., Puppeteer/Playwright) to bypass Cloudflare protection and Cloudflare Turnstile on specific torrent sites.

### Development Steps
1.  **Project Setup:** Initialize the Android project with Gradle version catalogs and the aforementioned tech stack.
2.  **Network Layer & Proxy Engine:** Implement the OkHttp client builder that reads proxy preferences from DataStore, configures the `java.net.Proxy`, and applies it to all outgoing Retrofit/Jsoup requests. Add connection timeout fallbacks.
3.  **Data Layer:** Define Room entities for `TorrentItem` and configure the DAOs for offline tracking and organization.
4.  **UI Implementation:** Build the Compose screens according to the design specifications, ensuring reactive state collection via StateFlow.
