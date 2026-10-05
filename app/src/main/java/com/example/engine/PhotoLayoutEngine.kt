package com.example.engine

import android.graphics.RectF
import com.example.data.model.ProgressPhoto

enum class LayoutTemplateType {
    LAYOUT_A, // 2x2 Grid (4 photo spaces)
    LAYOUT_B, // 1 large left + 3 stacked right (4 photo spaces)
    LAYOUT_C, // 1 large top + 3 in row bottom (4 photo spaces)
    LAYOUT_D, // 2 wide top + 2 bottom (4 photo spaces)
    LAYOUT_E, // 3 stacked left + 1 large right (4 photo spaces)
    LAYOUT_F  // Asymmetric balanced (4 photo spaces)
}

data class PhotoPageSlot(
    val photo: ProgressPhoto?,
    val bounds: RectF,
    val captionBounds: RectF,
    val slotNumber: Int
)

data class LocationPhotoPage(
    val locationName: String,
    val pageIndexForLocation: Int,
    val totalPagesForLocation: Int,
    val templateType: LayoutTemplateType,
    val photos: List<ProgressPhoto>,
    val averageProgress: Int,
    val workCategories: List<String>
)

object PhotoLayoutEngine {

    /**
     * Splits a list of photos into pages grouped by location.
     * Each page contains 4 photo spaces.
     */
    fun createPagesFromPhotos(photos: List<ProgressPhoto>): List<LocationPhotoPage> {
        val pages = mutableListOf<LocationPhotoPage>()
        val groupedByLoc = photos.groupBy { it.locationName }

        val templatesForFour = listOf(
            LayoutTemplateType.LAYOUT_A,
            LayoutTemplateType.LAYOUT_B,
            LayoutTemplateType.LAYOUT_C,
            LayoutTemplateType.LAYOUT_D,
            LayoutTemplateType.LAYOUT_E,
            LayoutTemplateType.LAYOUT_F
        )

        for ((locationName, locPhotos) in groupedByLoc) {
            val chunks = locPhotos.chunked(4)
            val totalPages = chunks.size.coerceAtLeast(1)

            chunks.forEachIndexed { pageIdx, chunkPhotos ->
                val avgProgress = if (chunkPhotos.isNotEmpty()) {
                    chunkPhotos.map { it.progress }.average().toInt()
                } else 0
                val categories = chunkPhotos.map { it.workCategory }.distinct()

                // Deterministic pseudo-random variation based on location name and page index
                val seed = (locationName.hashCode() + pageIdx * 37).let { if (it < 0) -it else it }
                val template = templatesForFour[seed % templatesForFour.size]

                pages.add(
                    LocationPhotoPage(
                        locationName = locationName,
                        pageIndexForLocation = pageIdx + 1,
                        totalPagesForLocation = totalPages,
                        templateType = template,
                        photos = chunkPhotos,
                        averageProgress = avgProgress,
                        workCategories = categories
                    )
                )
            }
        }
        return pages
    }

    /**
     * Computes the 4 photo space bounding boxes inside the photo area (width x height).
     * Guarantees 4 photo slots per page, populated with available photos.
     */
    fun calculateSlots(
        page: LocationPhotoPage,
        canvasLeft: Float,
        canvasTop: Float,
        canvasWidth: Float,
        canvasHeight: Float
    ): List<PhotoPageSlot> {
        val slots = mutableListOf<PhotoPageSlot>()
        val gap = 12f
        val captionHeight = 44f
        val photos = page.photos

        when (page.templateType) {
            LayoutTemplateType.LAYOUT_A -> {
                // 2x2 Grid (4 equal spaces)
                val colW = (canvasWidth - gap) / 2f
                val rowH = (canvasHeight - gap - (captionHeight * 2) - 16f) / 2f

                for (i in 0 until 4) {
                    val row = i / 2
                    val col = i % 2
                    val x = canvasLeft + col * (colW + gap)
                    val y = canvasTop + row * (rowH + captionHeight + gap + 4f)
                    val imgRect = RectF(x, y, x + colW, y + rowH)
                    val capRect = RectF(x, y + rowH + 2f, x + colW, y + rowH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }
            }

            LayoutTemplateType.LAYOUT_B -> {
                // 1 large left, 3 small stacked right (4 spaces)
                val leftW = (canvasWidth - gap) * 0.58f
                val rightW = canvasWidth - gap - leftW
                val leftH = canvasHeight - captionHeight - 8f

                // Slot 1: Large left
                val imgRect1 = RectF(canvasLeft, canvasTop, canvasLeft + leftW, canvasTop + leftH)
                val capRect1 = RectF(canvasLeft, canvasTop + leftH + 4f, canvasLeft + leftW, canvasTop + leftH + captionHeight + 4f)
                slots.add(PhotoPageSlot(photos.getOrNull(0), imgRect1, capRect1, 1))

                // Slots 2, 3, 4: Stacked on right
                val smallH = (canvasHeight - (gap * 2) - (captionHeight * 3) - 14f) / 3f
                val rightX = canvasLeft + leftW + gap

                for (i in 1..3) {
                    val y = canvasTop + (i - 1) * (smallH + captionHeight + gap)
                    val imgRect = RectF(rightX, y, rightX + rightW, y + smallH)
                    val capRect = RectF(rightX, y + smallH + 2f, rightX + rightW, y + smallH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }
            }

            LayoutTemplateType.LAYOUT_C -> {
                // 1 wide top, 3 in row bottom (4 spaces)
                val topH = (canvasHeight - gap - (captionHeight * 2) - 16f) * 0.54f
                val imgRect1 = RectF(canvasLeft, canvasTop, canvasLeft + canvasWidth, canvasTop + topH)
                val capRect1 = RectF(canvasLeft, canvasTop + topH + 2f, canvasLeft + canvasWidth, canvasTop + topH + captionHeight + 2f)
                slots.add(PhotoPageSlot(photos.getOrNull(0), imgRect1, capRect1, 1))

                val botW = (canvasWidth - (gap * 2)) / 3f
                val botTop = canvasTop + topH + captionHeight + gap + 4f
                val botH = canvasHeight - (botTop - canvasTop) - captionHeight - 4f

                for (i in 1..3) {
                    val x = canvasLeft + (i - 1) * (botW + gap)
                    val imgRect = RectF(x, botTop, x + botW, botTop + botH)
                    val capRect = RectF(x, botTop + botH + 2f, x + botW, botTop + botH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }
            }

            LayoutTemplateType.LAYOUT_D -> {
                // 2 wide top, 2 bottom (4 spaces)
                val colW = (canvasWidth - gap) / 2f
                val topH = (canvasHeight - gap - (captionHeight * 2) - 16f) * 0.52f
                val botH = (canvasHeight - gap - (captionHeight * 2) - 16f) * 0.48f

                for (i in 0 until 4) {
                    val row = i / 2
                    val col = i % 2
                    val x = canvasLeft + col * (colW + gap)
                    val currH = if (row == 0) topH else botH
                    val y = if (row == 0) canvasTop else canvasTop + topH + captionHeight + gap + 4f
                    val imgRect = RectF(x, y, x + colW, y + currH)
                    val capRect = RectF(x, y + currH + 2f, x + colW, y + currH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }
            }

            LayoutTemplateType.LAYOUT_E -> {
                // 3 stacked left, 1 large right (4 spaces)
                val rightW = (canvasWidth - gap) * 0.58f
                val leftW = canvasWidth - gap - rightW
                val smallH = (canvasHeight - (gap * 2) - (captionHeight * 3) - 14f) / 3f

                // Slots 1, 2, 3 stacked on left
                for (i in 0 until 3) {
                    val y = canvasTop + i * (smallH + captionHeight + gap)
                    val imgRect = RectF(canvasLeft, y, canvasLeft + leftW, y + smallH)
                    val capRect = RectF(canvasLeft, y + smallH + 2f, canvasLeft + leftW, y + smallH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }

                // Slot 4: Large right
                val rightX = canvasLeft + leftW + gap
                val rightH = canvasHeight - captionHeight - 8f
                val imgRect4 = RectF(rightX, canvasTop, rightX + rightW, canvasTop + rightH)
                val capRect4 = RectF(rightX, canvasTop + rightH + 4f, rightX + rightW, canvasTop + rightH + captionHeight + 4f)
                slots.add(PhotoPageSlot(photos.getOrNull(3), imgRect4, capRect4, 4))
            }

            LayoutTemplateType.LAYOUT_F -> {
                // Asymmetric balanced (4 spaces)
                val leftW = (canvasWidth - gap) * 0.48f
                val rightW = canvasWidth - gap - leftW
                val rowH = (canvasHeight - gap - (captionHeight * 2) - 16f) / 2f

                for (i in 0 until 4) {
                    val row = i / 2
                    val col = i % 2
                    val curW = if (col == 0) leftW else rightW
                    val x = if (col == 0) canvasLeft else canvasLeft + leftW + gap
                    val y = canvasTop + row * (rowH + captionHeight + gap + 4f)
                    val imgRect = RectF(x, y, x + curW, y + rowH)
                    val capRect = RectF(x, y + rowH + 2f, x + curW, y + rowH + captionHeight + 2f)
                    slots.add(PhotoPageSlot(photos.getOrNull(i), imgRect, capRect, i + 1))
                }
            }
        }

        return slots
    }
}
