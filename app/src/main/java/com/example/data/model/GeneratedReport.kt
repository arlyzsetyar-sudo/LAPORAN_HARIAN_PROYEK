package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class GeneratedReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val projectName: String,
    val title: String,
    val period: String,
    val date: String,
    val author: String,
    val role: String,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
