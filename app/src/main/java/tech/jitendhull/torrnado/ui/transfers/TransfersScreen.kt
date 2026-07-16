package tech.jitendhull.torrnado.ui.transfers

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import tech.jitendhull.torrnado.ui.theme.EmeraldHealthy
import tech.jitendhull.torrnado.ui.theme.RoseDead

data class TransferItem(
    val id: String,
    val name: String,
    val totalSize: String,
    val progress: Float, // 0.0 to 1.0
    val downSpeed: String,
    val upSpeed: String,
    val eta: String,
    val status: TransferStatus,
    val seeders: Int,
    val leechers: Int
)

enum class TransferStatus {
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    ERROR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransfersScreen(
    modifier: Modifier = Modifier
) {
    // Mock transfer database state for the visual/theme blueprint
    var transfers by remember {
        mutableStateOf(
            listOf(
                TransferItem(
                    id = "1",
                    name = "Ubuntu 24.04.1 LTS Desktop (amd64) ISO",
                    totalSize = "4.2 GB",
                    progress = 0.72f,
                    downSpeed = "12.4 MB/s",
                    upSpeed = "1.2 MB/s",
                    eta = "4m 12s",
                    status = TransferStatus.DOWNLOADING,
                    seeders = 432,
                    leechers = 18
                ),
                TransferItem(
                    id = "2",
                    name = "Sintel.2010.1080p.Open-Movie.mp4",
                    totalSize = "650 MB",
                    progress = 0.45f,
                    downSpeed = "0 KB/s",
                    upSpeed = "0 KB/s",
                    eta = "Queued",
                    status = TransferStatus.PAUSED,
                    seeders = 89,
                    leechers = 3
                ),
                TransferItem(
                    id = "3",
                    name = "Big.Buck.Bunny.1080p.H264.mp4",
                    totalSize = "276 MB",
                    progress = 1.0f,
                    downSpeed = "0 KB/s",
                    upSpeed = "124 KB/s",
                    eta = "Done",
                    status = TransferStatus.COMPLETED,
                    seeders = 1120,
                    leechers = 0
                ),
                TransferItem(
                    id = "4",
                    name = "Tears.of.Steel.4K.UltraHD.mkv",
                    totalSize = "6.1 GB",
                    progress = 0.05f,
                    downSpeed = "0 KB/s",
                    upSpeed = "0 KB/s",
                    eta = "Failed",
                    status = TransferStatus.ERROR,
                    seeders = 0,
                    leechers = 1
                )
            )
        )
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Active", "Completed", "Paused")

    val filteredTransfers = when (selectedTabIndex) {
        1 -> transfers.filter { it.status == TransferStatus.DOWNLOADING }
        2 -> transfers.filter { it.status == TransferStatus.COMPLETED }
        3 -> transfers.filter { it.status == TransferStatus.PAUSED }
        else -> transfers
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
                text = "TRANSFERS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = "Manage your active torrent downloads",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            )
        }

        // M3 Segmented Buttons for Tab Filtering
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }

        // List of Transfers
        if (filteredTransfers.isEmpty()) {
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
                        text = "No transfers in this category",
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
                items(filteredTransfers, key = { it.id }) { item ->
                    TransferCard(
                        item = item,
                        onPauseToggle = {
                            transfers = transfers.map {
                                if (it.id == item.id) {
                                    val nextStatus = if (it.status == TransferStatus.DOWNLOADING) TransferStatus.PAUSED else TransferStatus.DOWNLOADING
                                    it.copy(status = nextStatus, downSpeed = if (nextStatus == TransferStatus.PAUSED) "0 KB/s" else "6.8 MB/s")
                                } else it
                            }
                        },
                        onDelete = {
                            transfers = transfers.filter { it.id != item.id }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransferCard(
    item: TransferItem,
    onPauseToggle: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatusChip(status = item.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val color = when (item.status) {
                    TransferStatus.DOWNLOADING -> MaterialTheme.colorScheme.primary
                    TransferStatus.PAUSED -> MaterialTheme.colorScheme.outline
                    TransferStatus.COMPLETED -> EmeraldHealthy
                    TransferStatus.ERROR -> RoseDead
                }

                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(item.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Speeds & Stats (Down/Up/ETA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.status == TransferStatus.DOWNLOADING) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Down",
                                tint = EmeraldHealthy,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = item.downSpeed,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldHealthy
                            )
                            VerticalDivider(modifier = Modifier.height(12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        }

                        if (item.upSpeed != "0 KB/s") {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Up",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = item.upSpeed,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = "Size: ${item.totalSize}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "ETA: ${item.eta} • Seeders: ${item.seeders} • Leechers: ${item.leechers}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Play / Pause / Delete actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.status == TransferStatus.DOWNLOADING || item.status == TransferStatus.PAUSED) {
                        IconButton(
                            onClick = onPauseToggle,
                            modifier = Modifier
                                .size(36.dp)
                                .bounceClick { onPauseToggle() }
                        ) {
                            Icon(
                                imageVector = if (item.status == TransferStatus.DOWNLOADING) Icons.Default.Menu else Icons.Default.PlayArrow,
                                contentDescription = if (item.status == TransferStatus.DOWNLOADING) "Pause" else "Resume",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .bounceClick { onDelete() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RoseDead,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: TransferStatus) {
    val (label, containerColor, textColor) = when (status) {
        TransferStatus.DOWNLOADING -> Triple("Downloading", MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), MaterialTheme.colorScheme.primary)
        TransferStatus.PAUSED -> Triple("Paused", MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), MaterialTheme.colorScheme.outline)
        TransferStatus.COMPLETED -> Triple("Completed", EmeraldHealthy.copy(alpha = 0.15f), EmeraldHealthy)
        TransferStatus.ERROR -> Triple("Error", RoseDead.copy(alpha = 0.15f), RoseDead)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            maxLines = 1,
            softWrap = false
        )
    }
}
