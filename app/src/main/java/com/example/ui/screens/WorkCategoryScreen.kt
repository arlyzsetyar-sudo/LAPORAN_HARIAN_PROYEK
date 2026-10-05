package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Foundation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProgressPhoto
import com.example.data.model.WorkCategory
import com.example.ui.components.EmptyStateWidget
import com.example.ui.components.PastelCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelMutedPeach
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

@Composable
fun WorkCategoryScreen(
    initialCategory: String = "STRUKTUR",
    photos: List<ProgressPhoto>,
    onNavigateToCamera: () -> Unit,
    onPhotoClick: (ProgressPhoto) -> Unit,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedSubWorkFilter by remember { mutableStateOf("Semua") }

    val categories = listOf("STRUKTUR", "ARSITEK", "MEP")
    val currentSubWorks = remember(selectedCategory) {
        when (selectedCategory) {
            "STRUKTUR" -> listOf("Semua") + WorkCategory.STRUKTUR.subWorks
            "ARSITEK" -> listOf("Semua") + WorkCategory.ARSITEK.subWorks
            "MEP" -> listOf("Semua") + WorkCategory.MEP.subWorks
            else -> listOf("Semua")
        }
    }

    val filteredPhotos = remember(photos, selectedCategory, selectedSubWorkFilter) {
        photos.filter { it.workCategory.equals(selectedCategory, ignoreCase = true) }
            .let { list ->
                if (selectedSubWorkFilter == "Semua") list
                else list.filter { it.subWork.equals(selectedSubWorkFilter, ignoreCase = true) }
            }
    }

    val themeColor = when (selectedCategory) {
        "STRUKTUR" -> PastelSagePrimary
        "ARSITEK" -> PastelDustyBlue
        else -> PastelMutedPeach
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "Dokumentasi Pekerjaan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            containerColor = Color.White,
            contentColor = themeColor,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                val index = categories.indexOf(selectedCategory).coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = themeColor
                    )
                }
            }
        ) {
            categories.forEach { cat ->
                val selected = selectedCategory == cat
                Tab(
                    selected = selected,
                    onClick = {
                        selectedCategory = cat
                        selectedSubWorkFilter = "Semua"
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (cat) {
                                "STRUKTUR" -> Icons.Outlined.Foundation
                                "ARSITEK" -> Icons.Outlined.Architecture
                                else -> Icons.Outlined.Bolt
                            }
                            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cat, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }
        }

        // SubWork horizontal chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentSubWorks) { sub ->
                FilterChip(
                    selected = selectedSubWorkFilter == sub,
                    onClick = { selectedSubWorkFilter = sub },
                    label = { Text(sub, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (selectedCategory) {
                            "STRUKTUR" -> PastelSageContainer
                            "ARSITEK" -> PastelDustyBlueContainer
                            else -> PastelPeachContainer
                        }
                    )
                )
            }
        }

        if (filteredPhotos.isEmpty()) {
            EmptyStateWidget(
                title = "Belum Ada Dokumentasi $selectedCategory",
                subtitle = "Ambil foto progress untuk jenis pekerjaan $selectedCategory secara real-time.",
                buttonText = "AMBIL FOTO $selectedCategory",
                onButtonClick = onNavigateToCamera,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPhotos) { photo ->
                    PastelCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPhotoClick(photo) }
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PastelBeigeSurface)
                            ) {
                                AsyncImage(
                                    model = File(photo.filePath),
                                    contentDescription = photo.caption,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${photo.progress}%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = photo.locationName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    StatusBadge(status = photo.status)
                                }

                                Text(
                                    text = photo.subWork,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = themeColor
                                )

                                Text(
                                    text = photo.caption,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (photo.fieldNotes.isNotBlank()) {
                                    Text(
                                        text = "Catatan: ${photo.fieldNotes}",
                                        fontSize = 10.5.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${photo.dateStr} • ${photo.timeStr}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
