# Stich Agent Prompt: UI/UX Design for Torrent Tracker

**Role:** Lead UI/UX Designer & Frontend Expert

**Task:** Design a highly intuitive, premium, and dynamic UI for a modern Android Torrent Tracking application. The design should feel sleek, slightly edgy (a refined "hacker/power-user" vibe), and heavily utilize modern design trends.

## Design System & Aesthetics

*   **Theme:** Deep Dark Mode by default. Backgrounds should be rich, deep hues (e.g., `#0F172A` - Slate 900 or `#121212` with subtle gradients) rather than flat pure black.
*   **Color Palette:**
    *   **Primary:** A vibrant, neon accent color (e.g., Cyberpunk Cyan `#00F0FF` or Electric Violet `#8B5CF6`) for critical actions, Floating Action Buttons (FABs), and active states.
    *   **Secondary:** Subtle slate grays for text and inactive icons to maintain high contrast but reduce eye strain.
    *   **Status Indicators:** Emerald Green for high seeders/healthy torrents, Rose Red for dead torrents (low seeders), Amber for warnings.
*   **Typography:** Modern sans-serif fonts like `Inter` or `Outfit`. Use heavy, tight weights for headers and clean, readable weights for metadata.
*   **Styling:**
    *   **Glassmorphism:** Use subtle frosted glass effects (blur with semi-transparent overlays) for bottom navigation bars, sticky headers, and modal bottom sheets.
    *   **Cards:** Clean, rounded-corner cards for torrent list items with subtle hover/press elevation animations and soft glowing drop-shadows.

## Key Screens to Design

1.  **Home / Dashboard:**
    *   A prominent, beautifully styled search bar at the top that expands fluidly when tapped.
    *   Horizontal scrollable chips for filtering (e.g., Movies, TV, Games, Software).
    *   A "Trending" or "Recent Searches" section using masonry or clean grid layouts.

2.  **Search Results / Torrent Card:**
    *   **Crucial Data Hierarchy:** The Torrent Title should be prominent. Secondary info (File Size, Seeders/Leechers ratios, Upload Date, Uploader) should be compactly organized using crisp iconography instead of bulky text labels.
    *   A clear, stylized "Magnet" quick-action button directly on the card for instant handoff.

3.  **Proxy & Network Settings Screen:**
    *   This needs to look accessible, not intimidating to normal users, but powerful enough for advanced users.
    *   Use sleek toggle switches for "Enable Geo-Bypass Proxy".
    *   Clean, animated input fields for Proxy IP, Port, Username, and Password with smooth validation state animations (red shake on error, green check on success).

4.  **Micro-animations & Interactions:**
    *   Adding a torrent to the watchlist should trigger a satisfying, tactile animation (e.g., a subtle bounce or particle burst on the bookmark icon).
    *   Page transitions should use smooth shared-element transitions where applicable.

**Deliverable:** Generate the UI concepts, layouts, and interaction flows (either as Jetpack Compose code or high-fidelity UI prototypes) that strictly adhere to these premium aesthetic guidelines. Do not settle for basic Material Design defaults; push for a custom, "WOW" factor that feels like a top-tier premium app.
