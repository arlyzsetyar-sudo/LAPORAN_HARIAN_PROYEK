package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProgressPhoto
import com.example.engine.PhotoLayoutEngine
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Laapor", appName)
    }

    @Test
    fun `photo layout engine chunks max 4 photos per page and calculates 4 spaces`() {
        val samplePhotos = (1..10).map { i ->
            ProgressPhoto(
                id = i.toLong(),
                projectId = 1L,
                locationName = "Villa 01",
                workCategory = "STRUKTUR",
                subWork = "Kolom",
                filePath = "/fake/path/photo_$i.jpg",
                timestamp = System.currentTimeMillis(),
                dateStr = "04 Oktober 2026",
                timeStr = "10:00",
                caption = "Foto $i",
                fieldNotes = "Catatan $i",
                progress = 60,
                status = "Sedang Dikerjakan"
            )
        }

        val pages = PhotoLayoutEngine.createPagesFromPhotos(samplePhotos)
        assertEquals(3, pages.size)
        assertEquals(4, pages[0].photos.size)
        assertEquals(4, pages[1].photos.size)
        assertEquals(2, pages[2].photos.size)

        // Verifying 4 spaces per page
        val slots = PhotoLayoutEngine.calculateSlots(pages[0], 235f, 95f, 560f, 435f)
        assertEquals(4, slots.size)
    }
}
