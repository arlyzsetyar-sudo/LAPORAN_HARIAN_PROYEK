package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageWatermarkHelper {

    /**
     * Applies an elegant, non-intrusive watermark to the captured photo.
     * Text format: "LAAPOR | [Project Name] | [Location] | [Date & Time]"
     */
    fun processAndWatermarkPhoto(
        context: Context,
        sourceFile: File,
        projectName: String,
        locationName: String,
        workCategory: String,
        timestamp: Long = System.currentTimeMillis()
    ): File {
        // Read bitmap with proper orientation
        val rawBitmap = loadBitmapWithExif(sourceFile) ?: return sourceFile

        val width = rawBitmap.width
        val height = rawBitmap.height

        val workingBitmap = rawBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(workingBitmap)

        val dateText = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
        val cleanProject = if (projectName.isNotBlank()) projectName else "Proyek"
        val cleanLoc = if (locationName.isNotBlank()) locationName else "Lokasi Lapangan"
        val watermarkLine1 = "LAAPOR • $cleanProject"
        val watermarkLine2 = "$cleanLoc | $workCategory | $dateText WIB"

        // Scale font relative to bitmap size
        val baseSize = (height * 0.024f).coerceIn(24f, 48f)
        val textPaintTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = baseSize
            isFakeBoldText = true
            setShadowLayer(4f, 2f, 2f, Color.argb(180, 0, 0, 0))
        }

        val textPaintSub = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(235, 245, 238) // soft sage white
            textSize = baseSize * 0.82f
            setShadowLayer(3f, 1f, 1f, Color.argb(160, 0, 0, 0))
        }

        val bounds1 = Rect()
        textPaintTitle.getTextBounds(watermarkLine1, 0, watermarkLine1.length, bounds1)
        val bounds2 = Rect()
        textPaintSub.getTextBounds(watermarkLine2, 0, watermarkLine2.length, bounds2)

        val textWidth = maxOf(bounds1.width(), bounds2.width())
        val paddingHorizontal = baseSize * 0.8f
        val paddingVertical = baseSize * 0.5f

        val badgeWidth = textWidth + (paddingHorizontal * 2)
        val badgeHeight = (baseSize * 2.2f) + (paddingVertical * 2)

        // Bottom-right placement with margin
        val margin = baseSize * 0.8f
        val left = width - badgeWidth - margin
        val top = height - badgeHeight - margin
        val right = width - margin
        val bottom = height - margin

        val badgeRect = RectF(left, top, right, bottom)

        // Semi-transparent elegant dark sage badge
        val badgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(195, 26, 42, 34) // translucent deep sage
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 117, 150, 132) // sage border
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
        }

        val cornerRadius = baseSize * 0.4f
        canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, badgeBgPaint)
        canvas.drawRoundRect(badgeRect, cornerRadius, cornerRadius, borderPaint)

        // Small brand icon indicator dot
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(139, 195, 74) // vibrant indicator
        }
        canvas.drawCircle(left + paddingHorizontal * 0.5f, top + paddingVertical + baseSize * 0.4f, baseSize * 0.16f, dotPaint)

        // Draw texts
        canvas.drawText(
            watermarkLine1,
            left + paddingHorizontal * 0.9f,
            top + paddingVertical + baseSize * 0.75f,
            textPaintTitle
        )
        canvas.drawText(
            watermarkLine2,
            left + paddingHorizontal * 0.9f,
            top + paddingVertical + baseSize * 1.8f,
            textPaintSub
        )

        // Save output to photos directory
        val dir = File(context.filesDir, "laapor_photos")
        if (!dir.exists()) dir.mkdirs()

        val destFile = File(dir, "photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(destFile).use { out ->
            workingBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }

        // Clean up raw temp file if different
        if (sourceFile.exists() && sourceFile.absolutePath != destFile.absolutePath) {
            try { sourceFile.delete() } catch (_: Exception) {}
        }

        return destFile
    }

    private fun loadBitmapWithExif(file: File): Bitmap? {
        val original = BitmapFactory.decodeFile(file.absolutePath) ?: return null
        return try {
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(original, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(original, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(original, 270f)
                else -> original
            }
        } catch (_: Exception) {
            original
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }
}
