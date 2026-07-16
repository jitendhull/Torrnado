package tech.jitendhull.torrnado.ui.feeds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.jitendhull.torrnado.ui.search.bounceClick

data class FeedItem(
    val id: String,
    val title: String,
    val category: String,
    val size: String,
    val age: String,
    val indexer: String,
    val seeders: Int,
    val leechers: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsScreen(
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Movies", "TV Shows", "Anime", "Games", "Music", "Books")
    var selectedCategory by remember { mutableStateOf("All") }

    val mockFeeds = remember {
        listOf(
            FeedItem(
                id = "1",
                title = "F1 2026.Grand.Prix.Monaco.1080p.HEVC.x265-Torr",
                category = "TV Shows",
                size = "2.8 GB",
                age = "12 min ago",
                indexer = "Prowlarr",
                seeders = 230,
                leechers = 14
            ),
            FeedItem(
                id = "2",
                title = "Cosmic.Voyage.2026.2160p.REPACK.BluRay.x264.DTS-HD",
                category = "Movies",
                size = "14.6 GB",
                age = "45 min ago",
                indexer = "YTS",
                seeders = 1520,
                leechers = 42
            ),
            FeedItem(
                id = "3",
                title = "CyberPunk.Edgerunners.S02E01.Japanese.1080p.NF.WEBRip",
                category = "Anime",
                size = "780 MB",
                age = "1h 15m ago",
                indexer = "Nyaa",
                seeders = 840,
                leechers = 8
            ),
            FeedItem(
                id = "4",
                title = "OpenOffice.Suite.v9.4.0.Pro.Edition.x64-Cracked",
                category = "Games",
                size = "1.2 GB",
                age = "2h 30m ago",
                indexer = "1337x",
                seeders = 98,
                leechers = 12
            ),
            FeedItem(
                id = "5",
                title = "Lofi.Girl.Chill.Beats.To.Relax.Study.2026.FLAC",
                category = "Music",
                size = "450 MB",
                age = "4h ago",
                indexer = "Jackett",
                seeders = 320,
                leechers = 2
            )
        )
    }

    val filteredFeeds = if (selectedCategory == "All") {
        mockFeeds
    } else {
        mockFeeds.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Beautiful Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "EXPLORE FEEDS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Real-time updates from your indexers",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            )
        }

        // Horizontal chips row for category filtering
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    leadingIcon = if (selectedCategory == category) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        // Feed items list
        if (filteredFeeds.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No feeds found for this category",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFeeds, key = { it.id }) { item ->
                    FeedCard(item = item)
                }
            }
        }
    }
}

@Composable
fun FeedCard(item: FeedItem) {
    var isBookmarked by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = item.age,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Seeders
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Seeds",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${item.seeders}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Size
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Size",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.size,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Source Indexer
                    Text(
                        text = "@${item.indexer}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isBookmarked = !isBookmarked },
                        modifier = Modifier
                            .size(36.dp)
                            .bounceClick { isBookmarked = !isBookmarked }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = {},
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .bounceClick {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add torrent",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}