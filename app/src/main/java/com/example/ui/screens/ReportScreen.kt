package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.model.GeneratedReport
import com.example.data.model.LocationEntity
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import com.example.engine.ReportConfig
import com.example.ui.components.CategoryBadge
import com.example.ui.components.PastelCard
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
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(
    activeProject: Project?,
    photos: List<ProgressPhoto>,
    locations: List<LocationEntity>,
    reports: List<GeneratedReport>,
    selectedPhotoIds: Set<Long>,
    isGenerating: Boolean,
    lastPdfFile: File?,
    onTogglePhotoSelection: (Long) -> Unit,
    onSelectAllPhotos: (List<ProgressPhoto>) -> Unit,
    onClearPhotoSelection: () -> Unit,
    onGenerateReport: (ReportConfig, List<String>, (File) -> Unit) -> Unit,
    onDeleteReport: (GeneratedReport) -> Unit,
    onPreviewReport: (ReportConfig, List<String>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Report configuration state
    var selectedDiscipline by remember { mutableStateOf("Semua Pekerjaan") }
    var selectedLocations by remember { mutableStateOf<Set<String>>(setOf("Semua Lokasi")) }
    var periodText by remember { mutableStateOf("Mingguan") }
    var reportDate by remember {
        mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()))
    }
    var authorName by remember { mutableStateOf("Ir. Ahmad Fauzi") }
    var authorRole by remember { mutableStateOf("Site Engineer") }
    var generalNotes by remember { mutableStateOf("") }

    val disciplines = listOf("Semua Pekerjaan", "STRUKTUR", "ARSITEK", "MEP")
    val periodOptions = listOf("Hari ini", "Mingguan", "Bulanan", "Custom")

    // Filter available photos based on discipline
    val availablePhotos = remember(photos, selectedDiscipline, selectedLocations) {
        photos.filter { p ->
            val matchDiscipline = selectedDiscipline == "Semua Pekerjaan" || p.workCategory.equals(selectedDiscipline, true)
            val matchLoc = selectedLocations.contains("Semua Lokasi") || selectedLocations.contains(p.locationName)
            matchDiscipline && matchLoc
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cetak Laporan PDF",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Format A4 Landscape Elegan • Berdasarkan Lokasi",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(PastelSageContainer)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "A4 LANDSCAPE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = PastelSageOnContainer
                    )
                }
            }
        }

        // Project Header Banner
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "HEADER LAPORAN PROYEK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextTertiary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = activeProject?.name ?: "Pilih Proyek",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Owner: ${activeProject?.owner ?: "-"}  •  Perusahaan: ${activeProject?.company ?: "-"}",
                        fontSize = 12.sp,
                        color = PastelDustyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Lokasi: ${activeProject?.location ?: "-"}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Pilihan Jenis Pekerjaan
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Pilihan Jenis Pekerjaan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        disciplines.forEach { disc ->
                            FilterChip(
                                selected = selectedDiscipline == disc,
                                onClick = { selectedDiscipline = disc },
                                label = { Text(disc, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (disc) {
                                        "STRUKTUR" -> PastelSageContainer
                                        "ARSITEK" -> PastelDustyBlueContainer
                                        "MEP" -> PastelPeachContainer
                                        else -> PastelBeigeSurface
                                    }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Pilihan Lokasi (User requested: "dalam menu cetaka ada pilihan lokasi")
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pilihan Lokasi / Zona Laporan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (selectedLocations.contains("Semua Lokasi")) "Semua Zona" else "${selectedLocations.size} Dipilih",
                            fontSize = 11.5.sp,
                            color = PastelSagePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val allAvailableLocations = listOf("Semua Lokasi") + locations.map { it.name }.distinct()
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allAvailableLocations.forEach { locName ->
                            val isSelected = selectedLocations.contains(locName)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (locName == "Semua Lokasi") {
                                        selectedLocations = setOf("Semua Lokasi")
                                    } else {
                                        val newSet = selectedLocations.toMutableSet()
                                        newSet.remove("Semua Lokasi")
                                        if (isSelected) {
                                            newSet.remove(locName)
                                            if (newSet.isEmpty()) newSet.add("Semua Lokasi")
                                        } else {
                                            newSet.add(locName)
                                        }
                                        selectedLocations = newSet
                                    }
                                },
                                label = { Text(locName, fontSize = 11.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PastelSageContainer,
                                    selectedLabelColor = PastelSageOnContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Photo Selection from Gallery (User requested: "Photo di seleksi dari gallery")
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Seleksi Foto dari Gallery",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${selectedPhotoIds.size} dari ${availablePhotos.size} foto terpilih",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }

                        Row {
                            TextButton(onClick = { onSelectAllPhotos(availablePhotos) }) {
                                Text("Pilih Semua", fontSize = 11.5.sp, color = PastelSagePrimary)
                            }
                            TextButton(onClick = onClearPhotoSelection) {
                                Text("Reset", fontSize = 11.5.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (availablePhotos.isEmpty()) {
                        Text(
                            text = "Tidak ada foto dokumentasi pada filter ini.",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        // Display photo selector grid
                        val rows = availablePhotos.chunked(3)
                        rows.forEach { rowPhotos ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPhotos.forEach { photo ->
                                    val isSelected = selectedPhotoIds.isEmpty() || selectedPhotoIds.contains(photo.id)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                width = if (isSelected) 2.5.dp else 1.dp,
                                                color = if (isSelected) PastelSagePrimary else PastelCardBorder,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable { onTogglePhotoSelection(photo.id) }
                                    ) {
                                        AsyncImage(
                                            model = File(photo.filePath),
                                            contentDescription = photo.caption,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Selection check indicator
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) PastelSagePrimary else Color.Black.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            }
                                        }

                                        // Bottom Location badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(3.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = photo.locationName,
                                                fontSize = 8.5.sp,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                                if (rowPhotos.size < 3) {
                                    for (i in rowPhotos.size until 3) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Form: Periode, Tanggal, Penyusun & Jabatan
        item {
            PastelCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Data Penyusunan Laporan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = periodText,
                            onValueChange = { periodText = it },
                            label = { Text("Periode Laporan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = reportDate,
                            onValueChange = { reportDate = it },
                            label = { Text("Tanggal Laporan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = authorName,
                            onValueChange = { authorName = it },
                            label = { Text("Nama Penyusun") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = authorRole,
                            onValueChange = { authorRole = it },
                            label = { Text("Jabatan") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = generalNotes,
                        onValueChange = { generalNotes = it },
                        label = { Text("Keterangan Umum Laporan") },
                        placeholder = { Text("Catatan kondisi lapangan atau kendala cuaca...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            }
        }

        // PREVIEW & EXPORT ACTIONS
        item {
            val reportConfig = ReportConfig(
                title = "Laporan Progress Pekerjaan",
                author = authorName.ifEmpty { "Site Engineer" },
                role = authorRole.ifEmpty { "Pengawas Lapangan" },
                selectedDiscipline = selectedDiscipline,
                generalNotes = generalNotes,
                reportDate = reportDate,
                periodText = periodText
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Button 1: PREVIEW LAPORAN (User requested: "Sebelum PDF dibuat, tampilkan: PREVIEW LAPORAN")
                OutlinedButton(
                    onClick = {
                        onPreviewReport(reportConfig, selectedLocations.toList())
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("preview_report_button")
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = null, tint = PastelSagePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PREVIEW LAPORAN (TAMPILAN A4)",
                        fontWeight = FontWeight.Bold,
                        color = PastelSagePrimary
                    )
                }

                // Button 2: GENERATE PDF
                Button(
                    onClick = {
                        onGenerateReport(reportConfig, selectedLocations.toList()) { generatedFile ->
                            // Automatically provide share option
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PastelSagePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("generate_pdf_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sedang Menyusun Laporan PDF...")
                    } else {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GENERATE PDF A4 LANDSCAPE",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // If recently generated, show quick share & print options
                if (lastPdfFile != null && lastPdfFile.exists()) {
                    PastelCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        borderColor = PastelSageContainer
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PastelSagePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PDF Siap Dibagikan / Dicetak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastPdfFile.name,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // SHARE PDF
                                Button(
                                    onClick = {
                                        sharePdfFile(context, lastPdfFile)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PastelDustyBlue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("share_pdf_button")
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("SHARE PDF", fontSize = 11.5.sp)
                                }

                                // CETAK PDF
                                OutlinedButton(
                                    onClick = {
                                        printPdfFile(context, lastPdfFile)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("print_pdf_button")
                                ) {
                                    Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp), tint = PastelSagePrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CETAK", fontSize = 11.5.sp, color = PastelSagePrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Riwayat Laporan Sebelumnya
        if (reports.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Riwayat Laporan Tersimpan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(reports) { rep ->
                PastelCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PastelSageContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = PastelSagePrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = rep.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${rep.date} • ${rep.author}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row {
                            val f = File(rep.filePath)
                            if (f.exists()) {
                                IconButton(
                                    onClick = { sharePdfFile(context, f) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = "Share", tint = PastelDustyBlue, modifier = Modifier.size(18.dp))
                                }
                            }
                            IconButton(
                                onClick = { onDeleteReport(rep) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = StatusRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun sharePdfFile(context: Context, file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Laporan Progress Pekerjaan - Laapor")
            putExtra(Intent.EXTRA_TEXT, "Berikut terlampir laporan progress pekerjaan proyek format PDF.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan PDF via"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun printPdfFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val printIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(printIntent, "Buka / Cetak PDF"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
