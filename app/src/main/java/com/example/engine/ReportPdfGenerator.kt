package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import com.example.R
import com.example.data.model.ContractorInfo
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReportConfig(
    val title: String = "Laporan Progress Pekerjaan",
    val author: String = "Site Engineer",
    val role: String = "Pengawas Lapangan",
    val selectedDiscipline: String = "Semua Pekerjaan",
    val generalNotes: String = "",
    val reportDate: String = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()),
    val periodText: String = "Mingguan"
)

object ReportPdfGenerator {

    private const val PAGE_WIDTH = 842 // A4 Landscape width in points
    private const val PAGE_HEIGHT = 595 // A4 Landscape height in points

    fun generatePdf(
        context: Context,
        project: Project,
        photos: List<ProgressPhoto>,
        config: ReportConfig
    ): File {
        val pdfDocument = PdfDocument()

        // 1. Organize photo pages (4 photo spaces per page per location)
        val photoPages = PhotoLayoutEngine.createPagesFromPhotos(photos)

        // Total pages calculation: Cover (1) + Project Info (1) + Summary (1) + Photo Pages (photoPages.size)
        val totalPages = 3 + photoPages.size
        var currentPageNumber = 1

        // Parse contractors
        val contractors = parseContractors(project.contractorsJson)

        // 1. Draw Cover Page
        val coverPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        val coverPage = pdfDocument.startPage(coverPageInfo)
        drawCoverPage(context, coverPage.canvas, project, config, contractors)
        pdfDocument.finishPage(coverPage)
        currentPageNumber++

        // 2. Draw Project Information Page
        val infoPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        val infoPage = pdfDocument.startPage(infoPageInfo)
        drawProjectInfoPage(context, infoPage.canvas, project, config, contractors, currentPageNumber, totalPages)
        pdfDocument.finishPage(infoPage)
        currentPageNumber++

        // 3. Draw Summary Progress Page
        val summaryPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        val summaryPage = pdfDocument.startPage(summaryPageInfo)
        drawSummaryProgressPage(context, summaryPage.canvas, project, photos, config, currentPageNumber, totalPages)
        pdfDocument.finishPage(summaryPage)
        currentPageNumber++

        // 4. Draw Photo Documentation Pages (4 photo spaces per page with progress summary on left)
        for (page in photoPages) {
            val docPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
            val docPage = pdfDocument.startPage(docPageInfo)
            drawPhotoDocumentationPage(context, docPage.canvas, project, page, config, currentPageNumber, totalPages)
            pdfDocument.finishPage(docPage)
            currentPageNumber++
        }

        // Save PDF file
        val reportsDir = File(context.filesDir, "laapor_reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val cleanProject = project.name.replace("\\s+".toRegex(), "").filter { it.isLetterOrDigit() }.ifEmpty { "Proyek" }
        val cleanDiscipline = config.selectedDiscipline.replace("\\s+".toRegex(), "").filter { it.isLetterOrDigit() }.ifEmpty { "Semua" }
        val dateStamp = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val fileName = "LAAPOR_${cleanProject}_${cleanDiscipline}_$dateStamp.pdf"

        val outputFile = File(reportsDir, fileName)
        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun parseContractors(jsonStr: String): List<ContractorInfo> {
        val list = mutableListOf<ContractorInfo>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ContractorInfo(
                        id = obj.optString("id", i.toString()),
                        name = obj.optString("name", "-"),
                        workType = obj.optString("workType", "UMUM"),
                        picName = obj.optString("picName", "-"),
                        picPhone = obj.optString("picPhone", "-"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    // ==========================================
    // COVER PAGE
    // ==========================================
    private fun drawCoverPage(
        context: Context,
        canvas: Canvas,
        project: Project,
        config: ReportConfig,
        contractors: List<ContractorInfo>
    ) {
        // Background - Soft cream pastel
        canvas.drawColor(Color.rgb(249, 247, 242))

        // Decorative pastel sage ribbon on left
        val ribbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89) // Pastel Sage
        }
        canvas.drawRect(0f, 0f, 32f, PAGE_HEIGHT.toFloat(), ribbonPaint)

        // Accent pastel peach thin stripe
        val accentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(192, 115, 94) // Muted Peach
        }
        canvas.drawRect(32f, 0f, 38f, PAGE_HEIGHT.toFloat(), accentPaint)

        // Subtle geometric frame card
        val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(232, 227, 218)
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        val frameRect = RectF(70f, 45f, PAGE_WIDTH - 50f, PAGE_HEIGHT - 45f)
        canvas.drawRoundRect(frameRect, 16f, 16f, framePaint)
        canvas.drawRoundRect(frameRect, 16f, 16f, shadowPaint)

        // Draw Official Laapor Logo Badge at top left inside frame
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.laapor_logo_badge)
            if (logoBitmap != null) {
                val logoRect = RectF(100f, 75f, 145f, 120f)
                canvas.drawBitmap(logoBitmap, null, logoRect, null)
            }
        } catch (_: Exception) {}

        // Brand badge next to logo
        val badgeBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(226, 237, 230) // Pastel Sage Container
        }
        val badgeRect = RectF(155f, 80f, 255f, 115f)
        canvas.drawRoundRect(badgeRect, 8f, 8f, badgeBg)

        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 51, 41)
            textSize = 17f
            isFakeBoldText = true
        }
        canvas.drawText("LAAPOR", 172f, 104f, brandPaint)

        // Subtitle category
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(93, 105, 99)
            textSize = 13f
            letterSpacing = 0.08f
            isFakeBoldText = true
        }
        canvas.drawText("LAPORAN PROGRESS PEKERJAAN PROYEK", 100f, 160f, subtitlePaint)

        // Main Project Name - Big & Bold
        val projectTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 41, 37)
            textSize = 34f
            isFakeBoldText = true
        }
        canvas.drawText(project.name, 100f, 208f, projectTitlePaint)

        // Owner Name prominently below title
        val ownerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(74, 107, 130) // Dusty blue
            textSize = 18f
            isFakeBoldText = true
        }
        canvas.drawText("Owner: ${project.owner.ifEmpty { "Pemberi Tugas Proyek" }}", 100f, 240f, ownerPaint)

        // Horizontal divider line
        val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(232, 227, 218)
            strokeWidth = 1.5f
        }
        canvas.drawLine(100f, 260f, PAGE_WIDTH - 90f, 260f, divPaint)

        // Meta Details Cards Grid (2 Columns)
        val col1X = 100f
        val col2X = 460f

        drawMetaItem(canvas, col1X, 290f, "Lokasi Proyek", project.location.ifEmpty { "-" })
        drawMetaItem(canvas, col1X, 335f, "Nomor Kontrak", project.contractNumber.ifEmpty { "-" })
        drawMetaItem(canvas, col1X, 380f, "Konsultan Pengawas", project.consultant.ifEmpty { "-" })
        drawMetaItem(canvas, col1X, 425f, "Perusahaan / KSO", project.company.ifEmpty { "-" })

        drawMetaItem(canvas, col2X, 290f, "Periode Laporan", config.periodText.ifEmpty { project.period.ifEmpty { "Mingguan" } })
        drawMetaItem(canvas, col2X, 335f, "Tanggal Laporan", config.reportDate.ifEmpty { project.reportDate })
        drawMetaItem(canvas, col2X, 380f, "Disiplin Lingkup", config.selectedDiscipline)
        val leadContractor = contractors.firstOrNull()?.name ?: project.company.ifEmpty { "-" }
        drawMetaItem(canvas, col2X, 425f, "Kontraktor Utama", leadContractor)

        // Footer in Cover
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 151, 145)
            textSize = 10.5f
        }
        canvas.drawText("Disusun secara otomatis oleh Sistem Dokumentasi Kamera Real-Time Laapor", 100f, 515f, footerPaint)
        canvas.drawText("Dokumen Resmi Laporan Lapangan", PAGE_WIDTH - 250f, 515f, footerPaint)
    }

    private fun drawMetaItem(canvas: Canvas, x: Float, y: Float, label: String, value: String) {
        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 151, 145)
            textSize = 10f
        }
        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 41, 37)
            textSize = 12.5f
            isFakeBoldText = true
        }
        canvas.drawText(label.uppercase(), x, y, labelPaint)
        val safeVal = if (value.length > 45) value.take(42) + "..." else value
        canvas.drawText(safeVal, x, y + 16f, valuePaint)
    }

    // ==========================================
    // PAGE 2: PROJECT INFORMATION
    // ==========================================
    private fun drawProjectInfoPage(
        context: Context,
        canvas: Canvas,
        project: Project,
        config: ReportConfig,
        contractors: List<ContractorInfo>,
        pageNum: Int,
        total: Int
    ) {
        drawPageBase(context, canvas, project, "INFORMASI PROYEK & KONTRAKTOR", pageNum, total)

        // Card 1: Informasi Stakeholder & Manajemen (Left half)
        val leftCard = RectF(40f, 95f, 410f, 530f)
        drawPastelCard(canvas, leftCard, Color.rgb(255, 255, 255), Color.rgb(232, 227, 218))

        val cardTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89)
            textSize = 14f
            isFakeBoldText = true
        }
        canvas.drawText("DATA PROYEK & TIM MANAJEMEN", 60f, 125f, cardTitlePaint)

        var y = 160f
        drawMetaItem(canvas, 60f, y, "Nama Proyek", project.name); y += 45f
        drawMetaItem(canvas, 60f, y, "Pemberi Tugas (Owner)", project.owner.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Perusahaan Pengembang / KSO", project.company.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Nomor Kontrak", project.contractNumber.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Lokasi Proyek", project.location.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Konsultan Perencana / MK", project.consultant.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Project Manager", project.projectManager.ifEmpty { "-" }); y += 45f
        drawMetaItem(canvas, 60f, y, "Site Manager", project.siteManager.ifEmpty { "-" }); y += 45f

        // Card 2: Daftar Kontraktor & PIC (Right half)
        val rightCard = RectF(430f, 95f, PAGE_WIDTH - 40f, 530f)
        drawPastelCard(canvas, rightCard, Color.rgb(255, 255, 255), Color.rgb(232, 227, 218))

        canvas.drawText("DAFTAR KONTRAKTOR & PIC LAPANGAN", 450f, 125f, cardTitlePaint)

        if (contractors.isEmpty()) {
            val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(139, 151, 145)
                textSize = 12f
            }
            canvas.drawText("Belum ada data kontraktor terdaftar.", 450f, 165f, emptyPaint)
        } else {
            var cY = 150f
            contractors.take(5).forEachIndexed { idx, c ->
                val cBox = RectF(450f, cY, PAGE_WIDTH - 60f, cY + 68f)
                val boxBg = if (idx % 2 == 0) Color.rgb(243, 239, 231) else Color.rgb(249, 247, 242)
                drawPastelCard(canvas, cBox, boxBg, Color.rgb(232, 227, 218))

                val cNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(32, 41, 37)
                    textSize = 12.5f
                    isFakeBoldText = true
                }
                canvas.drawText("${idx + 1}. ${c.name}", 462f, cY + 22f, cNamePaint)

                // Work Type badge
                val badgeColor = when (c.workType.uppercase()) {
                    "STRUKTUR" -> Color.rgb(72, 105, 89)
                    "ARSITEK" -> Color.rgb(74, 107, 130)
                    "MEP" -> Color.rgb(192, 115, 94)
                    else -> Color.rgb(100, 110, 105)
                }
                val typePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = badgeColor
                    textSize = 10.5f
                    isFakeBoldText = true
                }
                canvas.drawText("Pekerjaan: ${c.workType}", 462f, cY + 40f, typePaint)

                val picPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(93, 105, 99)
                    textSize = 10.5f
                }
                canvas.drawText("PIC: ${c.picName}  •  Kontak: ${c.picPhone.ifEmpty { "-" }}", 462f, cY + 56f, picPaint)

                cY += 74f
            }
        }
    }

    // ==========================================
    // PAGE 3: PROGRESS SUMMARY
    // ==========================================
    private fun drawSummaryProgressPage(
        context: Context,
        canvas: Canvas,
        project: Project,
        photos: List<ProgressPhoto>,
        config: ReportConfig,
        pageNum: Int,
        total: Int
    ) {
        drawPageBase(context, canvas, project, "RINGKASAN PROGRESS & STATISTIK PROYEK", pageNum, total)

        // Metrics Row: 4 pastel cards
        val cardW = (PAGE_WIDTH - 80f - (3 * 16f)) / 4f
        val metricH = 80f
        val metricY = 95f

        val totalPhotos = photos.size
        val totalLocations = photos.map { it.locationName }.distinct().size
        val avgProgress = if (photos.isNotEmpty()) photos.map { it.progress }.average().toInt() else 0
        val completedCount = photos.count { it.status.equals("Selesai", ignoreCase = true) || it.progress >= 100 }

        drawMetricCard(canvas, 40f, metricY, cardW, metricH, "TOTAL DOKUMENTASI", "$totalPhotos Foto", Color.rgb(226, 237, 230), Color.rgb(72, 105, 89))
        drawMetricCard(canvas, 40f + cardW + 16f, metricY, cardW, metricH, "ZONA / LOKASI", "$totalLocations Lokasi", Color.rgb(227, 237, 245), Color.rgb(74, 107, 130))
        drawMetricCard(canvas, 40f + (cardW + 16f) * 2, metricY, cardW, metricH, "PROGRESS RATA-RATA", "$avgProgress%", Color.rgb(250, 235, 230), Color.rgb(192, 115, 94))
        drawMetricCard(canvas, 40f + (cardW + 16f) * 3, metricY, cardW, metricH, "PEKERJAAN SELESAI", "$completedCount Item", Color.rgb(243, 239, 231), Color.rgb(90, 80, 70))

        // Discipline Breakdown Card (Left)
        val leftRect = RectF(40f, 195f, 440f, 530f)
        drawPastelCard(canvas, leftRect, Color.WHITE, Color.rgb(232, 227, 218))

        val sectionTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89)
            textSize = 14f
            isFakeBoldText = true
        }
        canvas.drawText("PROGRESS BERDASARKAN DISIPLIN PEKERJAAN", 60f, 225f, sectionTitle)

        val categories = listOf("STRUKTUR", "ARSITEK", "MEP")
        var barY = 260f

        categories.forEach { cat ->
            val catPhotos = photos.filter { it.workCategory.equals(cat, ignoreCase = true) }
            val catAvg = if (catPhotos.isNotEmpty()) catPhotos.map { it.progress }.average().toInt() else 0
            val catColor = when (cat) {
                "STRUKTUR" -> Color.rgb(72, 105, 89)
                "ARSITEK" -> Color.rgb(74, 107, 130)
                else -> Color.rgb(192, 115, 94)
            }

            val catText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(32, 41, 37)
                textSize = 12.5f
                isFakeBoldText = true
            }
            canvas.drawText(cat, 60f, barY, catText)

            val pctText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = catColor
                textSize = 12.5f
                isFakeBoldText = true
            }
            canvas.drawText("$catAvg% (${catPhotos.size} foto)", 330f, barY, pctText)

            // Progress Bar Track
            val trackRect = RectF(60f, barY + 8f, 420f, barY + 22f)
            val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(240, 236, 230)
            }
            canvas.drawRoundRect(trackRect, 7f, 7f, trackPaint)

            // Filled Bar
            val fillW = (360f * (catAvg / 100f)).coerceAtLeast(6f)
            val fillRect = RectF(60f, barY + 8f, 60f + fillW, barY + 22f)
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = catColor
            }
            canvas.drawRoundRect(fillRect, 7f, 7f, fillPaint)

            barY += 55f
        }

        // Status breakdown right panel
        val rightRect = RectF(460f, 195f, PAGE_WIDTH - 40f, 530f)
        drawPastelCard(canvas, rightRect, Color.WHITE, Color.rgb(232, 227, 218))

        canvas.drawText("STATUS DISTRIBUSI LAPANGAN", 480f, 225f, sectionTitle)

        val statuses = listOf(
            "Selesai" to Color.rgb(56, 142, 60),
            "Sedang Dikerjakan" to Color.rgb(25, 118, 210),
            "Progress" to Color.rgb(123, 31, 162),
            "Perbaikan" to Color.rgb(198, 40, 40),
            "Belum Mulai" to Color.rgb(120, 130, 125)
        )

        var statY = 265f
        statuses.forEach { (statusName, colorCode) ->
            val count = photos.count { it.status.equals(statusName, ignoreCase = true) }
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorCode
            }
            canvas.drawCircle(490f, statY - 4f, 6f, dotPaint)

            val stPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(32, 41, 37)
                textSize = 12f
            }
            canvas.drawText(statusName, 508f, statY, stPaint)

            val countPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = colorCode
                textSize = 12f
                isFakeBoldText = true
            }
            canvas.drawText("$count Item", 720f, statY, countPaint)

            statY += 34f
        }

        // Catatan umum penyusun
        val noteBox = RectF(480f, 440f, PAGE_WIDTH - 60f, 510f)
        drawPastelCard(canvas, noteBox, Color.rgb(249, 247, 242), Color.rgb(232, 227, 218))
        val noteHead = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 151, 145)
            textSize = 9.5f
            isFakeBoldText = true
        }
        canvas.drawText("CATATAN UMUM LAPORAN:", 492f, 458f, noteHead)
        val noteBody = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(50, 60, 55)
            textSize = 10f
        }
        val noteText = config.generalNotes.ifEmpty { "Semua dokumentasi diambil langsung secara real-time via kamera HP tanpa manipulasi atau upload galeri." }
        canvas.drawText(if (noteText.length > 55) noteText.take(52) + "..." else noteText, 492f, 476f, noteBody)
        canvas.drawText("Penyusun: ${config.author} (${config.role})", 492f, 494f, noteBody)
    }

    private fun drawMetricCard(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        label: String,
        value: String,
        bgCol: Int,
        textCol: Int
    ) {
        val rect = RectF(x, y, x + w, y + h)
        drawPastelCard(canvas, rect, bgCol, Color.rgb(232, 227, 218))

        val labPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(93, 105, 99)
            textSize = 9f
            letterSpacing = 0.04f
            isFakeBoldText = true
        }
        canvas.drawText(label, x + 14f, y + 26f, labPaint)

        val valPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textCol
            textSize = 20f
            isFakeBoldText = true
        }
        canvas.drawText(value, x + 14f, y + 58f, valPaint)
    }

    // ==========================================
    // PAGE 4+: PHOTO DOCUMENTATION PAGE (4 SPACES PER LOCATION WITH LEFT SIDE PROGRESS SUMMARY)
    // ==========================================
    private fun drawPhotoDocumentationPage(
        context: Context,
        canvas: Canvas,
        project: Project,
        page: LocationPhotoPage,
        config: ReportConfig,
        pageNum: Int,
        total: Int
    ) {
        val subtitle = "DOKUMENTASI: ${page.locationName.uppercase()}" +
                if (page.totalPagesForLocation > 1) " (Bagian ${page.pageIndexForLocation} dari ${page.totalPagesForLocation})" else ""
        drawPageBase(context, canvas, project, subtitle, pageNum, total)

        // 1. RINGKASAN PROGRESS DI SAMPING KIRI LAYOUT (User requirement: "dengan ringkasan progress di samping kiri layout")
        val sidebarWidth = 195f
        val sideRect = RectF(40f, 95f, 40f + sidebarWidth, 530f)
        drawPastelCard(canvas, sideRect, Color.rgb(255, 255, 255), Color.rgb(232, 227, 218))

        // Side Header Ribbon
        val sideRibbon = RectF(40f, 95f, 40f + sidebarWidth, 140f)
        val sideRibbonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(226, 237, 230) // Pastel Sage
        }
        canvas.drawRoundRect(sideRibbon, 12f, 12f, sideRibbonPaint)

        val locTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 51, 41)
            textSize = 13.5f
            isFakeBoldText = true
        }
        val safeLoc = if (page.locationName.length > 20) page.locationName.take(18) + "..." else page.locationName
        canvas.drawText(safeLoc, 54f, 122f, locTitlePaint)

        // Progress Pill in Sidebar
        var sY = 165f
        val avgLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 151, 145)
            textSize = 9.5f
            isFakeBoldText = true
        }
        canvas.drawText("RINGKASAN PROGRESS ZONA:", 54f, sY, avgLabel); sY += 24f

        val progValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89)
            textSize = 28f
            isFakeBoldText = true
        }
        canvas.drawText("${page.averageProgress}%", 54f, sY, progValuePaint); sY += 15f

        // Mini progress bar in sidebar
        val miniTrack = RectF(54f, sY, 54f + sidebarWidth - 28f, sY + 8f)
        val trackP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(240, 236, 230) }
        canvas.drawRoundRect(miniTrack, 4f, 4f, trackP)

        val miniFillW = ((sidebarWidth - 28f) * (page.averageProgress / 100f)).coerceAtLeast(4f)
        val miniFill = RectF(54f, sY, 54f + miniFillW, sY + 8f)
        val fillP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(72, 105, 89) }
        canvas.drawRoundRect(miniFill, 4f, 4f, fillP)

        sY += 30f
        canvas.drawLine(54f, sY, 40f + sidebarWidth - 14f, sY, trackP)
        sY += 18f

        // Scope / Disiplin in sidebar
        canvas.drawText("DISIPLIN AKTIF:", 54f, sY, avgLabel); sY += 16f
        val catValPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 41, 37)
            textSize = 11f
            isFakeBoldText = true
        }
        val catsText = page.workCategories.joinToString(", ").ifEmpty { "Semua" }
        canvas.drawText(if (catsText.length > 22) catsText.take(20) + "..." else catsText, 54f, sY, catValPaint)
        sY += 25f

        canvas.drawText("4 SPACE GAMBAR:", 54f, sY, avgLabel); sY += 16f
        canvas.drawText("${page.photos.size} Foto dari 4 Slot", 54f, sY, catValPaint)
        sY += 28f

        // Sub-works active list
        canvas.drawText("ITEM PEKERJAAN:", 54f, sY, avgLabel); sY += 16f
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(74, 107, 130)
            textSize = 10f
        }
        val uniqueSubs = page.photos.map { it.subWork }.distinct().take(4)
        if (uniqueSubs.isEmpty()) {
            canvas.drawText("• Pekerjaan Fisik Lapangan", 54f, sY, subPaint)
            sY += 16f
        } else {
            uniqueSubs.forEach { sub ->
                val cleanSub = if (sub.length > 22) sub.take(20) + ".." else sub
                canvas.drawText("• $cleanSub", 54f, sY, subPaint)
                sY += 16f
            }
        }

        sY += 10f
        canvas.drawLine(54f, sY, 40f + sidebarWidth - 14f, sY, trackP)
        sY += 18f

        // Field note summary from photos
        val firstNote = page.photos.firstOrNull { it.fieldNotes.isNotBlank() }?.fieldNotes ?: ""
        if (firstNote.isNotBlank()) {
            canvas.drawText("CATATAN LAPANGAN:", 54f, sY, avgLabel); sY += 16f
            val notePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(93, 105, 99)
                textSize = 9.5f
            }
            val shortNote = if (firstNote.length > 40) firstNote.take(38) + "..." else firstNote
            canvas.drawText(shortNote, 54f, sY, notePaint)
        }

        // Layout template tag indicator
        val tmplTag = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(170, 180, 175)
            textSize = 8.5f
        }
        canvas.drawText("4-Space Layout: ${page.templateType.name}", 54f, 515f, tmplTag)

        // 2. 4 SPACE GAMBAR AREA (To the right of the sidebar)
        val photoAreaLeft = 40f + sidebarWidth + 14f
        val photoAreaTop = 95f
        val photoAreaWidth = PAGE_WIDTH - photoAreaLeft - 40f
        val photoAreaHeight = 530f - photoAreaTop

        val slots = PhotoLayoutEngine.calculateSlots(
            page = page,
            canvasLeft = photoAreaLeft,
            canvasTop = photoAreaTop,
            canvasWidth = photoAreaWidth,
            canvasHeight = photoAreaHeight
        )

        // Draw each of the 4 slots
        for (slot in slots) {
            drawPhotoSlot(canvas, slot)
        }
    }

    private fun drawPhotoSlot(canvas: Canvas, slot: PhotoPageSlot) {
        val imgRect = slot.bounds
        val capRect = slot.captionBounds
        val photo = slot.photo

        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 215, 205)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        if (photo != null) {
            // Active photo in this space
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(238, 235, 228)
            }
            canvas.drawRoundRect(imgRect, 8f, 8f, bgPaint)

            val bitmap = loadAndScaleBitmap(photo.filePath, imgRect.width().toInt(), imgRect.height().toInt())
            if (bitmap != null) {
                canvas.save()
                canvas.clipRect(imgRect)
                val srcRect = calculateCropSrcRect(bitmap.width, bitmap.height, imgRect.width(), imgRect.height())
                canvas.drawBitmap(bitmap, srcRect, imgRect, null)
                canvas.restore()
            } else {
                val phPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(139, 151, 145)
                    textSize = 11f
                }
                canvas.drawText("[Foto Dokumentasi]", imgRect.centerX() - 45f, imgRect.centerY(), phPaint)
            }
            canvas.drawRoundRect(imgRect, 8f, 8f, borderPaint)

            // Top-left photo number tag on image
            val tagBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(200, 30, 45, 38)
            }
            val tagRect = RectF(imgRect.left + 6f, imgRect.top + 6f, imgRect.left + 58f, imgRect.top + 22f)
            canvas.drawRoundRect(tagRect, 4f, 4f, tagBg)

            val tagText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 9.5f
                isFakeBoldText = true
            }
            canvas.drawText("Foto ${slot.slotNumber}", imgRect.left + 12f, imgRect.top + 18f, tagText)

            // Progress badge on top-right of image
            val progTagBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(210, 72, 105, 89)
            }
            val progTagRect = RectF(imgRect.right - 52f, imgRect.top + 6f, imgRect.right - 6f, imgRect.top + 22f)
            canvas.drawRoundRect(progTagRect, 4f, 4f, progTagBg)

            val progText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 9.5f
                isFakeBoldText = true
            }
            canvas.drawText("${photo.progress}%", imgRect.right - 44f, imgRect.top + 18f, progText)

            // Draw Caption box
            val capBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
            }
            canvas.drawRoundRect(capRect, 6f, 6f, capBg)
            canvas.drawRoundRect(capRect, 6f, 6f, borderPaint)

            val capTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(32, 41, 37)
                textSize = 10f
                isFakeBoldText = true
            }
            val capDescPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(93, 105, 99)
                textSize = 9f
            }

            val titleLine = "${photo.workCategory} • ${photo.subWork}  (${photo.status})"
            val cleanTitle = if (titleLine.length > 45) titleLine.take(42) + "..." else titleLine
            canvas.drawText(cleanTitle, capRect.left + 8f, capRect.top + 14f, capTitlePaint)

            val rawCaption = photo.caption.ifEmpty { "Pekerjaan pada area ${photo.locationName} dalam progress." }
            val noteSuffix = if (photo.fieldNotes.isNotBlank()) " [Catatan: ${photo.fieldNotes}]" else ""
            val fullDesc = rawCaption + noteSuffix
            val cleanDesc = if (fullDesc.length > 55) fullDesc.take(52) + "..." else fullDesc
            canvas.drawText(cleanDesc, capRect.left + 8f, capRect.top + 28f, capDescPaint)

            val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(139, 151, 145)
                textSize = 8.5f
            }
            canvas.drawText("${photo.dateStr} ${photo.timeStr}", capRect.right - 90f, capRect.top + 38f, metaPaint)
        } else {
            // Reserved 4-space slot placeholder
            val emptyBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(247, 245, 240)
            }
            canvas.drawRoundRect(imgRect, 8f, 8f, emptyBg)
            canvas.drawRoundRect(imgRect, 8f, 8f, borderPaint)

            val phText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(165, 175, 170)
                textSize = 10f
                isFakeBoldText = true
            }
            val subPhText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(180, 188, 184)
                textSize = 8.5f
            }
            canvas.drawText("Space Foto ${slot.slotNumber}", imgRect.left + 12f, imgRect.top + 24f, phText)
            canvas.drawText("Slot Dokumentasi Tersedia", imgRect.left + 12f, imgRect.top + 38f, subPhText)

            // Caption Box for empty slot
            val capBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(252, 250, 246) }
            canvas.drawRoundRect(capRect, 6f, 6f, capBg)
            canvas.drawRoundRect(capRect, 6f, 6f, borderPaint)
            val capEmpty = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(180, 188, 184)
                textSize = 8.5f
            }
            canvas.drawText("Dokumentasi tambahan dapat diambil langsung via kamera", capRect.left + 8f, capRect.top + 24f, capEmpty)
        }
    }

    private fun calculateCropSrcRect(srcW: Int, srcH: Int, dstW: Float, dstH: Float): Rect {
        val srcRatio = srcW.toFloat() / srcH.toFloat()
        val dstRatio = dstW / dstH

        return if (srcRatio > dstRatio) {
            val targetW = (srcH * dstRatio).toInt()
            val left = (srcW - targetW) / 2
            Rect(left, 0, left + targetW, srcH)
        } else {
            val targetH = (srcW / dstRatio).toInt()
            val top = (srcH - targetH) / 2
            Rect(0, top, srcW, top + targetH)
        }
    }

    private fun loadAndScaleBitmap(filePath: String, reqW: Int, reqH: Int): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, options)

            var sampleSize = 1
            if (options.outHeight > reqH || options.outWidth > reqW) {
                val halfH = options.outHeight / 2
                val halfW = options.outWidth / 2
                while ((halfH / sampleSize) >= reqH && (halfW / sampleSize) >= reqW) {
                    sampleSize *= 2
                }
            }

            val decodeOpts = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(file.absolutePath, decodeOpts)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // REUSABLE PAGE BASE (HEADER WITH LAAPOR LOGO & FOOTER)
    // ==========================================
    private fun drawPageBase(
        context: Context,
        canvas: Canvas,
        project: Project,
        pageSubtitle: String,
        pageNum: Int,
        total: Int
    ) {
        // Page background - soft cream
        canvas.drawColor(Color.rgb(249, 247, 242))

        // Top Header Banner
        val headerBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 255, 255)
        }
        val headerLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(232, 227, 218)
            strokeWidth = 1f
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 75f, headerBg)
        canvas.drawLine(0f, 75f, PAGE_WIDTH.toFloat(), 75f, headerLine)

        // Header Accent Ribbon
        val topRibbon = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89)
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 4f, topRibbon)

        // Draw Official Laapor Logo in Header
        var textStartX = 40f
        try {
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.laapor_logo_badge)
            if (logoBitmap != null) {
                val logoRect = RectF(40f, 13f, 91f, 64f)
                canvas.drawBitmap(logoBitmap, null, logoRect, null)
                textStartX = 100f
            }
        } catch (_: Exception) {}

        // Brand & Title
        val brandTag = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(72, 105, 89)
            textSize = 9.5f
            letterSpacing = 0.06f
            isFakeBoldText = true
        }
        canvas.drawText("LAAPOR  •  LAPORAN PROGRESS PEKERJAAN", textStartX, 24f, brandTag)

        // Project Name (Font lebih besar dan bold)
        val projNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(32, 41, 37)
            textSize = 17f
            isFakeBoldText = true
        }
        canvas.drawText(project.name, textStartX, 44f, projNamePaint)

        // Owner Name
        val ownerNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(74, 107, 130)
            textSize = 11.5f
            isFakeBoldText = true
        }
        canvas.drawText("Owner: ${project.owner.ifEmpty { "Pemberi Tugas" }}", textStartX, 62f, ownerNamePaint)

        // Right side of Header: Subtitle / Section title
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(93, 105, 99)
            textSize = 12f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(pageSubtitle, PAGE_WIDTH - 40f, 44f, subPaint)

        // Footer
        val footerLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(232, 227, 218)
            strokeWidth = 1f
        }
        canvas.drawLine(40f, PAGE_HEIGHT - 35f, PAGE_WIDTH - 40f, PAGE_HEIGHT - 35f, footerLine)

        val footerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 151, 145)
            textSize = 9.5f
        }
        val leftFoot = "${project.name}  |  Periode: ${project.period.ifEmpty { "Mingguan" }}"
        canvas.drawText(leftFoot, 40f, PAGE_HEIGHT - 18f, footerText)

        val pageFoot = "Halaman $pageNum dari $total"
        val pageFootPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(93, 105, 99)
            textSize = 9.5f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText(pageFoot, PAGE_WIDTH - 40f, PAGE_HEIGHT - 18f, pageFootPaint)
    }

    private fun drawPastelCard(canvas: Canvas, rect: RectF, fillCol: Int, strokeCol: Int) {
        val fillP = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillCol }
        val strokeP = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = strokeCol
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }
        canvas.drawRoundRect(rect, 10f, 10f, fillP)
        canvas.drawRoundRect(rect, 10f, 10f, strokeP)
    }
}
