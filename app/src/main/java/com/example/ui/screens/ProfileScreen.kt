package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.ui.components.PastelCard
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
    activeProject: Project?,
    totalPhotos: Int,
    totalLocations: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Identity Hero Card
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.laapor_logo_badge),
                        contentDescription = "Logo Laapor",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Laapor",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PastelSageContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("v1.0", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PastelSageOnContainer)
                            }
                        }
                        Text(
                            text = "Laporan Progress Pekerjaan Proyek",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Offline-First & Security Badge
        item {
            PastelCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PastelBeigeSurface
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CloudOff,
                        contentDescription = null,
                        tint = PastelSagePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "100% Offline-First",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Aplikasi dapat digunakan di lokasi proyek tanpa koneksi internet. Semua data proyek dan foto tersimpan lokal.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Feature Checklist
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Standar Dokumentasi Konstruksi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    FeatureItem(
                        title = "Kamera Real-Time Only",
                        desc = "Hanya foto langsung via kamera handphone. Bebas manipulasi, dilarang upload dari galeri.",
                        icon = Icons.Filled.CameraAlt
                    )

                    FeatureItem(
                        title = "Watermark Otomatis Elegan",
                        desc = "Mencantumkan Laapor, nama proyek, lokasi, tanggal, dan jam tanpa menutupi objek fisik.",
                        icon = Icons.Filled.Security
                    )

                    FeatureItem(
                        title = "Data Kontraktor & PIC Lengkap",
                        desc = "Pengelolaan kontraktor struktur, arsitek, MEP beserta PIC lapangan yang dapat diedit.",
                        icon = Icons.Filled.Engineering
                    )

                    FeatureItem(
                        title = "Menu Catatan Lapangan",
                        desc = "Catatan khusus kendala cuaca, instruksi konsultan, atau material pada setiap foto.",
                        icon = Icons.Filled.Info
                    )

                    FeatureItem(
                        title = "Gallery Berdasarkan Tanggal",
                        desc = "Timeline foto terstruktur rapi berdasarkan hari dan jam pengambilan.",
                        icon = Icons.Outlined.PhotoLibrary
                    )

                    FeatureItem(
                        title = "Aturan 1 Lokasi = Max 4 Foto",
                        desc = "Sistem layout acak profesional dengan ringkasan progress di samping foto.",
                        icon = Icons.Outlined.Speed
                    )

                    FeatureItem(
                        title = "Ekspor PDF A4 Landscape",
                        desc = "Cover elegan, informasi proyek, ringkasan progress, dan halaman foto per lokasi siap cetak.",
                        icon = Icons.Filled.PictureAsPdf
                    )
                }
            }
        }

        // Summary Info
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Statistik Penyimpanan Perangkat",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Proyek Aktif: ${activeProject?.name ?: "-"}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Total Dokumentasi Foto: $totalPhotos file tersimpan",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Total Zona Kerja: $totalLocations area terdaftar",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    title: String,
    desc: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(PastelSageContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = PastelSagePrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = TextPrimary)
            Text(desc, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
