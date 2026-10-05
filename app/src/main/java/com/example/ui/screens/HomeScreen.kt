package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Foundation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EmptyStateWidget
import com.example.ui.components.PastelCard
import com.example.ui.components.PastelProgressBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCardBorder
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelDustyBlueOnContainer
import com.example.ui.theme.PastelMutedPeach
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelPeachOnContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.io.File

@Composable
fun HomeScreen(
    activeProject: Project?,
    photos: List<ProgressPhoto>,
    locationsCount: Int,
    onNavigateToCamera: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToWorkCategory: (String) -> Unit,
    onPhotoClick: (ProgressPhoto) -> Unit
) {
    val totalPhotos = photos.size
    val avgProgress = if (photos.isNotEmpty()) photos.map { it.progress }.average().toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Brand
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.laapor_logo_badge),
                        contentDescription = "Logo Laapor",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Laapor",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = PastelSagePrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PastelSageContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PROYEK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelSageOnContainer
                                )
                            }
                        }
                        Text(
                            text = "Laporan Progress Pekerjaan Real-Time",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                OutlinedButton(
                    onClick = onNavigateToProjects,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("switch_project_button")
                ) {
                    Icon(
                        Icons.Filled.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = PastelSagePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ganti Proyek", fontSize = 12.sp, color = PastelSagePrimary)
                }
            }
        }

        // Active Project Hero Card
        item {
            if (activeProject != null) {
                PastelCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToProjects() },
                    backgroundColor = Color.White,
                    borderColor = PastelSageContainer,
                    cornerRadius = 20
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PastelSageContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Business,
                                        contentDescription = null,
                                        tint = PastelSagePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PROYEK AKTIF",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PastelSagePrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Owner: ${activeProject.owner.ifEmpty { "Pemberi Tugas" }}",
                                        fontSize = 12.sp,
                                        color = PastelDustyBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PastelSageContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$avgProgress%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelSageOnContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = activeProject.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (activeProject.location.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = activeProject.location,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Progress Keseluruhan Proyek",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PastelProgressBar(progress = avgProgress)
                    }
                }
            }
        }

        // 4 Quick Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "DOKUMENTASI",
                    value = "$totalPhotos Foto",
                    icon = Icons.Filled.CameraAlt,
                    bg = PastelSageContainer,
                    tint = PastelSagePrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "LOKASI ZONA",
                    value = "$locationsCount Zona",
                    icon = Icons.Filled.LocationOn,
                    bg = PastelDustyBlueContainer,
                    tint = PastelDustyBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Primary Action: BUAT DOKUMENTASI (Kamera Real-time)
        item {
            Button(
                onClick = onNavigateToCamera,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelSagePrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("home_take_photo_button")
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "AMBIL FOTO DOKUMENTASI (REAL-TIME)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Quick Action Tiles: Cetak Laporan & Gallery Photo Berdasarkan Tanggal
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PastelCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToReport() },
                    backgroundColor = Color.White,
                    borderColor = PastelPeachContainer
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelPeachContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                tint = PastelMutedPeach,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Cetak Laporan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Ekspor PDF A4 Landscape",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                PastelCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToGallery() },
                    backgroundColor = Color.White,
                    borderColor = PastelDustyBlueContainer
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelDustyBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Collections,
                                contentDescription = null,
                                tint = PastelDustyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Gallery Tanggal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Timeline foto per hari",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Disiplin Pekerjaan Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Disiplin Pekerjaan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Lihat Semua",
                        fontSize = 12.sp,
                        color = PastelSagePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WorkTypeCard(
                        title = "STRUKTUR",
                        subtext = "Pondasi, Kolom, Balok",
                        count = photos.count { it.workCategory.equals("STRUKTUR", true) },
                        icon = Icons.Outlined.Foundation,
                        bg = PastelSageContainer,
                        fg = PastelSageOnContainer,
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToWorkCategory("STRUKTUR") }

                    WorkTypeCard(
                        title = "ARSITEK",
                        subtext = "Dinding, Lantai, Plafon",
                        count = photos.count { it.workCategory.equals("ARSITEK", true) },
                        icon = Icons.Outlined.Architecture,
                        bg = PastelDustyBlueContainer,
                        fg = PastelDustyBlueOnContainer,
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToWorkCategory("ARSITEK") }

                    WorkTypeCard(
                        title = "MEP",
                        subtext = "Listrik, Plumbing, HVAC",
                        count = photos.count { it.workCategory.equals("MEP", true) },
                        icon = Icons.Outlined.Bolt,
                        bg = PastelPeachContainer,
                        fg = PastelPeachOnContainer,
                        modifier = Modifier.weight(1f)
                    ) { onNavigateToWorkCategory("MEP") }
                }
            }
        }

        // Recent Photos Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Dokumentasi Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${photos.size} Foto",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (photos.isEmpty()) {
                    EmptyStateWidget(
                        title = "Belum Ada Dokumentasi",
                        subtitle = "Dokumentasikan progress pekerjaan hari ini dengan mengambil foto kamera.",
                        buttonText = "AMBIL FOTO SEKARANG",
                        onButtonClick = onNavigateToCamera
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(photos.take(6)) { photo ->
                            RecentPhotoCard(photo = photo, onClick = { onPhotoClick(photo) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    bg: Color,
    tint: Color,
    modifier: Modifier = Modifier
) {
    PastelCard(
        modifier = modifier,
        backgroundColor = Color.White
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextTertiary)
                Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            }
        }
    }
}

@Composable
private fun WorkTypeCard(
    title: String,
    subtext: String,
    count: Int,
    icon: ImageVector,
    bg: Color,
    fg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = fg)
            Text(text = subtext, fontSize = 10.sp, color = fg.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$count Foto",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
        }
    }
}

@Composable
private fun RecentPhotoCard(
    photo: ProgressPhoto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(PastelBeigeSurface)
            ) {
                AsyncImage(
                    model = File(photo.filePath),
                    contentDescription = photo.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top badges
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
                            .background(Color.Black.copy(alpha = 0.6f))
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
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = photo.locationName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
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
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = photo.status)
            }
        }
    }
}
