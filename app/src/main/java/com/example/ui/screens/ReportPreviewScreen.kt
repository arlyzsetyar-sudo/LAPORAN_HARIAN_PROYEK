package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ContractorInfo
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import com.example.engine.LocationPhotoPage
import com.example.engine.PhotoLayoutEngine
import com.example.engine.ReportConfig
import com.example.ui.components.CategoryBadge
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
fun ReportPreviewScreen(
    project: Project,
    photos: List<ProgressPhoto>,
    contractors: List<ContractorInfo>,
    config: ReportConfig,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onGeneratePdf: () -> Unit,
    lastPdfFile: File?
) {
    val context = LocalContext.current
    val photoPages = remember(photos) {
        PhotoLayoutEngine.createPagesFromPhotos(photos)
    }

    val totalPages = 3 + photoPages.size
    var currentPageIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("preview_back_button")) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                }
                Text(
                    text = "Preview Laporan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Row {
                TextButton(onClick = onEdit, modifier = Modifier.testTag("preview_edit_button")) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = PastelSagePrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("EDIT", color = PastelSagePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                if (lastPdfFile != null && lastPdfFile.exists()) {
                    IconButton(onClick = { sharePdf(context, lastPdfFile) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", tint = PastelDustyBlue)
                    }
                    IconButton(onClick = { printPdf(context, lastPdfFile) }) {
                        Icon(Icons.Filled.Print, contentDescription = "Cetak", tint = PastelSagePrimary)
                    }
                }
            }
        }

        // Page Navigation Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                enabled = currentPageIndex > 0,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Hal. Sebelumnya", fontSize = 11.sp)
            }

            Text(
                text = "Halaman ${currentPageIndex + 1} dari $totalPages",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PastelSagePrimary
            )

            OutlinedButton(
                onClick = { if (currentPageIndex < totalPages - 1) currentPageIndex++ },
                enabled = currentPageIndex < totalPages - 1,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Hal. Berikutnya", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
            }
        }

        // Sheet Canvas Preview (Simulates A4 Landscape Sheet in Pastel Theme)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, PastelCardBorder, RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            when (currentPageIndex) {
                0 -> PreviewCoverSheet(project, config, contractors)
                1 -> PreviewProjectInfoSheet(project, config, contractors)
                2 -> PreviewSummarySheet(project, photos, config)
                else -> {
                    val pageIdx = currentPageIndex - 3
                    if (pageIdx in photoPages.indices) {
                        PreviewLocationDocSheet(project, photoPages[pageIdx], currentPageIndex + 1, totalPages)
                    }
                }
            }
        }

        // Bottom Action Bar: Generate PDF & Direct Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("KEMBALI")
            }

            Button(
                onClick = onGeneratePdf,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PastelSagePrimary),
                modifier = Modifier
                    .weight(2f)
                    .testTag("preview_generate_pdf_button")
            ) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("GENERATE PDF", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// PREVIEW PAGE 1: COVER
// -------------------------------------------------------------
@Composable
private fun PreviewCoverSheet(
    project: Project,
    config: ReportConfig,
    contractors: List<ContractorInfo>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7F2))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.laapor_logo_badge),
                contentDescription = "Logo Laapor",
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PastelSageContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("LAAPOR", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = PastelSageOnContainer)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "LAPORAN PROGRESS PEKERJAAN PROYEK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )
        // Main Project Name - Big & Bold
        Text(
            text = project.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Owner: ${project.owner.ifEmpty { "-" }}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PastelDustyBlue
        )

        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PastelCardBorder))
        Spacer(modifier = Modifier.height(14.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                MetaLine("LOKASI PROYEK", project.location.ifEmpty { "-" })
                MetaLine("NOMOR KONTRAK", project.contractNumber.ifEmpty { "-" })
                MetaLine("KONSULTAN", project.consultant.ifEmpty { "-" })
            }
            Column(modifier = Modifier.weight(1f)) {
                MetaLine("PERIODE", config.periodText)
                MetaLine("TANGGAL", config.reportDate)
                MetaLine("DISIPLIN", config.selectedDiscipline)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Dokumen resmi hasil dokumentasi kamera real-time di lapangan",
            fontSize = 10.sp,
            color = TextTertiary
        )
    }
}

// -------------------------------------------------------------
// PREVIEW PAGE 2: PROJECT INFO
// -------------------------------------------------------------
@Composable
private fun PreviewProjectInfoSheet(
    project: Project,
    config: ReportConfig,
    contractors: List<ContractorInfo>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SheetHeader(project, "INFORMASI PROYEK & KONTRAKTOR")
        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Left Card: Project Details
            PastelCard(modifier = Modifier.weight(1f), backgroundColor = PastelBeigeSurface) {
                Column {
                    Text("DATA STAKEHOLDER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PastelSagePrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    MetaLine("Owner", project.owner)
                    MetaLine("Perusahaan", project.company)
                    MetaLine("PM", project.projectManager)
                    MetaLine("Site Manager", project.siteManager)
                }
            }

            // Right Card: Contractor List with PIC
            PastelCard(modifier = Modifier.weight(1f), backgroundColor = PastelBeigeSurface) {
                Column {
                    Text("KONTRAKTOR & PIC LAPANGAN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PastelSagePrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (contractors.isEmpty()) {
                        Text("Belum ada kontraktor terdaftar.", fontSize = 11.sp, color = TextTertiary)
                    } else {
                        contractors.forEach { c ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CategoryBadge(category = c.workType)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${c.name} (PIC: ${c.picName})", fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PREVIEW PAGE 3: SUMMARY
// -------------------------------------------------------------
@Composable
private fun PreviewSummarySheet(
    project: Project,
    photos: List<ProgressPhoto>,
    config: ReportConfig
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SheetHeader(project, "RINGKASAN PROGRESS & STATISTIK")
        Spacer(modifier = Modifier.height(10.dp))

        // 4 Mini Stat Boxes
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MiniStat("DOKUMENTASI", "${photos.size} Foto", PastelSageContainer, PastelSagePrimary, Modifier.weight(1f))
            MiniStat("ZONA", "${photos.map { it.locationName }.distinct().size} Lokasi", PastelDustyBlueContainer, PastelDustyBlue, Modifier.weight(1f))
            val avg = if (photos.isNotEmpty()) photos.map { it.progress }.average().toInt() else 0
            MiniStat("PROGRESS", "$avg%", PastelPeachContainer, PastelMutedPeach, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Discipline bars
        Text("PROGRESS PER DISIPLIN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        listOf("STRUKTUR", "ARSITEK", "MEP").forEach { cat ->
            val catPhotos = photos.filter { it.workCategory.equals(cat, true) }
            val catAvg = if (catPhotos.isNotEmpty()) catPhotos.map { it.progress }.average().toInt() else 0
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("$catAvg% (${catPhotos.size} foto)", fontSize = 11.sp, color = PastelSagePrimary, fontWeight = FontWeight.Bold)
            }
            PastelProgressBar(progress = catAvg)
        }
    }
}

// -------------------------------------------------------------
// PREVIEW PAGE 4+: LOCATION DOCUMENTATION (Side progress summary at left + 4 Photo Spaces)
// -------------------------------------------------------------
@Composable
private fun PreviewLocationDocSheet(
    project: Project,
    page: LocationPhotoPage,
    pageNumber: Int,
    total: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        SheetHeader(project, "LOKASI: ${page.locationName.uppercase()}")
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // 1. RINGKASAN PROGRESS DI SAMPING KIRI LAYOUT (User requirement: "dengan ringkasan progress di samping kiri layout")
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PastelBeigeSurface)
                    .border(1.dp, PastelCardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Text(page.locationName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary)
                    Text("Ringkasan Progress:", fontSize = 8.5.sp, color = TextSecondary)
                    Text("${page.averageProgress}%", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PastelSagePrimary)
                    PastelProgressBar(progress = page.averageProgress)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Disiplin:", fontSize = 8.5.sp, color = TextSecondary)
                    Text(page.workCategories.joinToString(), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("4 Space Gambar:", fontSize = 8.5.sp, color = TextSecondary)
                    Text("${page.photos.size} Foto Terpasang", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = PastelSagePrimary)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sub-Pekerjaan:", fontSize = 8.5.sp, color = TextSecondary)
                    val subs = page.photos.map { it.subWork }.distinct().take(3)
                    if (subs.isEmpty()) {
                        Text("• Pekerjaan Fisik", fontSize = 8.5.sp, color = PastelDustyBlue)
                    } else {
                        subs.forEach {
                            Text("• $it", fontSize = 8.5.sp, color = PastelDustyBlue)
                        }
                    }

                    if (page.photos.any { it.fieldNotes.isNotBlank() }) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Catatan:", fontSize = 8.5.sp, color = TextSecondary)
                        val note = page.photos.first { it.fieldNotes.isNotBlank() }.fieldNotes
                        Text(if (note.length > 25) note.take(23) + ".." else note, fontSize = 8.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Text("4-Space: ${page.templateType.name}", fontSize = 7.sp, color = TextTertiary)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 2. 4 SPACE GAMBAR (Guaranteed 4 photo slots: Slots 1 to 4)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Row 1: Slots 1 and 2
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PhotoSpaceBox(photo = page.photos.getOrNull(0), slotNumber = 1, modifier = Modifier.weight(1f))
                    PhotoSpaceBox(photo = page.photos.getOrNull(1), slotNumber = 2, modifier = Modifier.weight(1f))
                }

                // Row 2: Slots 3 and 4
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PhotoSpaceBox(photo = page.photos.getOrNull(2), slotNumber = 3, modifier = Modifier.weight(1f))
                    PhotoSpaceBox(photo = page.photos.getOrNull(3), slotNumber = 4, modifier = Modifier.weight(1f))
                }
            }
        }

        // Mini footer
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(project.name, fontSize = 8.sp, color = TextTertiary)
            Text("Hal $pageNumber dari $total", fontSize = 8.sp, color = TextTertiary)
        }
    }
}

@Composable
private fun PhotoSpaceBox(
    photo: ProgressPhoto?,
    slotNumber: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(6.dp))
            .background(PastelBeigeSurface)
            .border(1.dp, PastelCardBorder, RoundedCornerShape(6.dp))
    ) {
        if (photo != null) {
            AsyncImage(
                model = File(photo.filePath),
                contentDescription = photo.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Slot Tag
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("Foto $slotNumber", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Bottom caption line
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.92f))
                    .padding(4.dp)
            ) {
                Column {
                    Text(
                        text = "${photo.subWork} (${photo.progress}%)",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = photo.caption,
                        fontSize = 7.5.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            // Empty designated 4-space slot
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xFFA0ABA5),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text("Space Foto 0$slotNumber", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA0ABA5))
                Text("Slot Dokumentasi", fontSize = 7.5.sp, color = Color(0xFFBAC3BF))
            }
        }
    }
}

@Composable
private fun SheetHeader(project: Project, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.laapor_logo_badge),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("LAAPOR • LAPORAN PROGRESS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PastelSagePrimary)
                    // Project Name - Larger and Bold
                    Text(project.name, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Text("Owner: ${project.owner}", fontSize = 9.5.sp, color = PastelDustyBlue, fontWeight = FontWeight.Bold)
                }
            }
            Text(subtitle, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(PastelCardBorder))
    }
}

@Composable
private fun MetaLine(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(label.uppercase(), fontSize = 9.sp, color = TextTertiary)
        Text(value, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun MiniStat(title: String, value: String, bg: Color, fg: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(6.dp)
    ) {
        Column {
            Text(title, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = fg)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = fg)
        }
    }
}

private fun sharePdf(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Laporan PDF"))
    } catch (_: Exception) {}
}

private fun printPdf(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Cetak PDF"))
    } catch (_: Exception) {}
}
