package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val locationId: Long = 0,
    val locationName: String,
    val workCategory: String, // STRUKTUR, ARSITEK, MEP, etc.
    val subWork: String,
    val filePath: String,
    val timestamp: Long,
    val dateStr: String,
    val timeStr: String,
    val caption: String,
    val fieldNotes: String = "", // Menu Catatan
    val progress: Int = 0,
    val status: String = "Sedang Dikerjakan",
    val photoNumber: Int = 1
)

enum class WorkCategory(val title: String, val subWorks: List<String>) {
    STRUKTUR(
        "STRUKTUR",
        listOf(
            "Pondasi",
            "Kolom",
            "Balok",
            "Plat",
            "Tangga",
            "Struktur baja",
            "Struktur kayu",
            "Pekerjaan lainnya"
        )
    ),
    ARSITEK(
        "ARSITEK",
        listOf(
            "Dinding",
            "Lantai",
            "Plafon",
            "Pintu & jendela",
            "Finishing",
            "Interior",
            "Eksterior",
            "Pekerjaan lainnya"
        )
    ),
    MEP(
        "MEP",
        listOf(
            "Electrical",
            "Plumbing",
            "HVAC",
            "Fire Fighting",
            "ICT",
            "Sound System",
            "Ventilation",
            "Solar Panel",
            "Pekerjaan lainnya"
        )
    )
}
