# 🔱 TorrentVault — Implementation Plan

## App Name: **TorrentVault**

*(Alternatives considered: MagnetDeck, TorrentForge, TrackerPulse)*

---

## Feasibility Assessment

### ✅ Fully Feasible
| Aspect | Verdict |
|---|---|
| Kotlin + Compose + MVVM + Hilt | Standard Android stack, no risk |
| OkHttp proxy routing (HTTP/SOCKS5) | Native `java.net.Proxy` support, trivial |
| Room + DataStore | Mature, stable libs |
| Magnet link → intent handoff | One-liner `ACTION_VIEW` intent |
| Jsoup HTML parsing | Battle-tested on Android |
| Glassmorphism / dark theme in Compose | Custom `Modifier.blur()` + alpha layers, doable |

### ⚠️ Risks & Mitigations
| Risk | Severity | Mitigation |
|---|---|---|
| Cloudflare blocks scraping | **High** | Rely on Prowlarr/Jackett API as primary; Jsoup fallback only for unprotected sites |
| Tracker site structure changes break scrapers | Medium | Abstract each tracker behind interface; version scraper configs independently |
| Google Play policy (torrent-related) | **High** | Distribute via GitHub Releases / F-Droid / APK direct |
| MCP server dependency | Medium | Prowlarr/Jackett optional — app works standalone with direct scraping |

### ❌ Cut from Scope (YAGNI)
- Headless browser on-device — too heavy, battery killer
- Built-in torrent download engine — delegate to Flud/qBittorrent via intents

---

## AI Token Consumption Estimate

| Phase | Est. Turns | Avg Tokens/Turn (in+out) | Subtotal |
|---|---|---|---|
| Project scaffolding + Gradle setup | 8-10 | ~4K | ~35K |
| Network layer + proxy engine | 10-12 | ~5K | ~55K |
| Room DB + repositories | 8-10 | ~4K | ~35K |
| Domain layer (use cases) | 5-6 | ~3K | ~16K |
| UI screens (5 screens × ~8 turns) | 35-40 | ~6K | ~220K |
| Glassmorphism / animations / polish | 10-15 | ~5K | ~60K |
| Debugging / iteration | 15-20 | ~5K | ~85K |
| **Total** | **~100-115 turns** | | **~500K–550K tokens** |

**Cost estimate** (at typical rates):
- Claude Sonnet: ~$1.50–$2.50
- Claude Opus: ~$7–$12
- GPT-4o: ~$2.50–$5.00

---

## Implementation Plan — 6 Phases

### Phase 1: Project Skeleton (Day 1)
- [ ] Android project init with Gradle version catalogs (`libs.versions.toml`)
- [ ] Configure: Kotlin 2.0+, Compose BOM, Hilt, Room, Retrofit, OkHttp, Jsoup, DataStore
- [ ] Package structure:
  ```
  com.torrentvault
  ├── data/          # Room, Retrofit, DataStore, repositories
  ├── domain/        # Models, UseCases
  ├── di/            # Hilt modules
  └── ui/            # Compose screens, theme, navigation
  ```
- [ ] Base theme: dark palette (`#0F172A` bg, `#00F0FF` primary, `#8B5CF6` secondary), Inter font

### Phase 2: Network + Proxy Engine (Day 2-3)
- [ ] `ProxyPreferences` DataStore — host, port, type (HTTP/SOCKS5), credentials, enabled flag
- [ ] `ProxyOkHttpClient` Hilt provider — reads DataStore, builds `OkHttpClient` with `java.net.Proxy`
- [ ] Retrofit service interfaces for Prowlarr/Jackett API
- [ ] `JsoupScraperService` — direct HTML parsing fallback with same proxy-aware OkHttp client
- [ ] `TrackerRepository` — unified interface, fans out to configured sources

### Phase 3: Data Layer (Day 3-4)
- [ ] Room entities: `TorrentEntity` (title, hash, size, seeders, leechers, category, status, source, timestamp)
- [ ] DAOs: CRUD + queries by category/status, search history
- [ ] `TorrentStatus` enum: `WISHLISTED`, `DOWNLOADING`, `COMPLETED`, `ARCHIVED`
- [ ] Repository pattern wrapping Room + remote sources

### Phase 4: Domain Layer (Day 4)
- [ ] `SearchTorrentsUseCase` — parallel search across trackers, merge + dedupe by info_hash
- [ ] `OrganizeTorrentUseCase` — move between categories
- [ ] `OpenMagnetUseCase` — build `ACTION_VIEW` intent for magnet URI
- [ ] Models: `TorrentItem`, `SearchResult`, `TrackerConfig`, `ProxyConfig`

### Phase 5: UI Screens (Day 5-8)
5 screens, priority order:

1. **Home/Dashboard** — search bar (animated expand), category chips, recent/trending grid
2. **Search Results** — torrent cards (title bold, size/seeders/leechers icons, magnet FAB)
3. **Torrent Detail** — full metadata, tracker info, action buttons
4. **Settings/Proxy** — toggle switch, animated input fields, connection test button
5. **Library** — tabs for Wishlist/Downloading/Completed, swipe actions

Each screen: ViewModel + StateFlow, glassmorphism bottom nav, card elevation animations, Compose Navigation with type-safe routes.

### Phase 6: Polish (Day 8-10)
- [ ] Micro-animations: bookmark bounce, shared element transitions, shimmer loading
- [ ] Error states, empty states, offline mode
- [ ] ProGuard/R8 rules
- [ ] Build variants: debug/release
- [ ] README + APK release config

---

## Architecture

```
┌─────────────────────────────────────────────┐
│                  UI Layer                    │
│  Compose Screens → ViewModels → StateFlow   │
├─────────────────────────────────────────────┤
│               Domain Layer                   │
│  UseCases (SearchTorrents, Organize, etc.)  │
├─────────────────────────────────────────────┤
│                Data Layer                    │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐ │
│  │ Room DB  │  │ Retrofit │  │  Jsoup    │ │
│  │ (local)  │  │(Prowlarr)│  │ (scraper) │ │
│  └──────────┘  └─────┬────┘  └─────┬─────┘ │
│                      │             │        │
│                ┌─────▼─────────────▼─────┐  │
│                │   OkHttp + Proxy Engine  │  │
│                │   (reads DataStore)      │  │
│                └─────────────────────────┘  │
└─────────────────────────────────────────────┘
```

---

## Key Decisions
- **Prowlarr/Jackett = Optional** — app ships functional without any server
- **No Play Store** — GitHub Releases / F-Droid distribution
- **No built-in download** — delegate to external torrent clients via intents
