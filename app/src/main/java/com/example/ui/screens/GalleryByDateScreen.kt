package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.ProgressPhoto
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EmptyStateWidget
import com.example.ui.components.PastelCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCardBorder
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.File

@Composable
fun GalleryByDateScreen(
    photos: List<ProgressPhoto>,
    onNavigateToCamera: () -> Unit,
    onUpdatePhoto: (ProgressPhoto) -> Unit,
    onDeletePhoto: (ProgressPhoto) -> Unit
) {
    var selectedDisciplineFilter by remember { mutableStateOf("Semua") }
    var selectedDetailPhoto by remember { mutableStateOf<ProgressPhoto?>(null) }

    val filteredPhotos = remember(photos, selectedDisciplineFilter) {
        if (selectedDisciplineFilter == "Semua") {
            photos
        } else {
            photos.filter { it.workCategory.equals(selectedDisciplineFilter, ignoreCase = true) }
        }
    }

    // Grouping strictly by capture date
    val groupedByDate = remember(filteredPhotos) {
        filteredPhotos.groupBy { it.dateStr }
    }

    if (selectedDetailPhoto != null) {
        PhotoDetailDialog(
            photo = selectedDetailPhoto!!,
            onDismiss = { selectedDetailPhoto = null },
            onSave = { updated ->
                onUpdatePhoto(updated)
                selectedDetailPhoto = null
            },
            onDelete = { toDelete ->
                onDeletePhoto(toDelete)
                selectedDetailPhoto = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Gallery Photo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Dokumentasi Berdasarkan Tanggal Pengambilan",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelSageContainer)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${filteredPhotos.size} Foto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = PastelSageOnContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Discipline Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Semua", "STRUKTUR", "ARSITEK", "MEP").forEach { filter ->
                FilterChip(
                    selected = selectedDisciplineFilter == filter,
                    onClick = { selectedDisciplineFilter = filter },
                    label = { Text(filter, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (filter) {
                            "STRUKTUR" -> PastelSageContainer
                            "ARSITEK" -> PastelDustyBlueContainer
                            "MEP" -> PastelPeachContainer
                            else -> PastelSageContainer
                        }
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (groupedByDate.isEmpty()) {
            EmptyStateWidget(
                title = "Belum Ada Dokumentasi",
                subtitle = "Semua foto proyek yang diambil melalui kamera akan tersusun otomatis sesuai tanggal di sini.",
                buttonText = "AMBIL FOTO SEKARANG",
                onButtonClick = onNavigateToCamera,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                groupedByDate.forEach { (dateStr, datePhotos) ->
                    item(key = dateStr) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Date Section Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(PastelSageContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.CalendarToday,
                                            contentDescription = null,
                                            tint = PastelSagePrimary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Text(
                                    text = "${datePhotos.size} Dokumentasi",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }

                            // 2-column Grid for this date
                            val rows = datePhotos.chunked(2)
                            rows.forEach { rowItems ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { photo ->
                                        GalleryPhotoItem(
                                            photo = photo,
                                            modifier = Modifier.weight(1f),
                                            onClick = { selectedDetailPhoto = photo }
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryPhotoItem(
    photo: ProgressPhoto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(PastelBeigeSurface)
            ) {
                AsyncImage(
                    model = File(photo.filePath),
                    contentDescription = photo.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category and Progress badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryBadge(category = photo.workCategory)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${photo.progress}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Watermark indicator bar at bottom of image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xC01E2E25))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LAAPOR • ${photo.timeStr}",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = photo.locationName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = photo.subWork,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (photo.fieldNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Catatan: ${photo.fieldNotes}",
                        fontSize = 10.sp,
                        color = PastelSagePrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = photo.status)
            }
        }
    }
}

/**
 * Detailed modal allowing inspection of watermark, photo attributes, editing notes/progress, or deleting.
 */
@Composable
private fun PhotoDetailDialog(
    photo: ProgressPhoto,
    onDismiss: () -> Unit,
    onSave: (ProgressPhoto) -> Unit,
    onDelete: (ProgressPhoto) -> Unit
) {
    var caption by remember { mutableStateOf(photo.caption) }
    var fieldNotes by remember { mutableStateOf(photo.fieldNotes) }
    var progress by remember { mutableFloatStateOf(photo.progress.toFloat()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Foto Dokumentasi?") },
            text = { Text("Foto ini akan dihapus permanen dari perangkat dan laporan proyek.") },
            confirmButton = {
                Button(
                    onClick = { onDelete(photo) },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text("Hapus Permanen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = PastelCreamBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Dialog Top Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryBadge(category = photo.workCategory)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = photo.locationName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full image view with watermark intact
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        AsyncImage(
                            model = File(photo.filePath),
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Time stamp badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pengambilan: ${photo.dateStr}, ${photo.timeStr} WIB",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Watermark Terpasang",
                            fontSize = 11.sp,
                            color = PastelSagePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Edit caption
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text("Keterangan Foto") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelSagePrimary,
                            unfocusedBorderColor = PastelCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Edit field notes (Catatan)
                    OutlinedTextField(
                        value = fieldNotes,
                        onValueChange = { fieldNotes = it },
                        label = { Text("Menu Catatan Lapangan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PastelSagePrimary,
                            unfocusedBorderColor = PastelCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress Fisik:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("${progress.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PastelSagePrimary)
                    }
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(thumbColor = PastelSagePrimary, activeTrackColor = PastelSagePrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Buttons: Delete & Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = { showDeleteConfirm = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = StatusRed)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hapus Foto")
                        }

                        Button(
                            onClick = {
                                onSave(photo.copy(
                                    caption = caption,
                                    fieldNotes = fieldNotes,
                                    progress = progress.toInt()
                                ))
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PastelSagePrimary)
                        ) {
                            Text("Simpan Perubahan")
                        }
                    }
                }
            }
        }
    }
}
