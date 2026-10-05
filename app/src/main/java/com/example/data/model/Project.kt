package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val owner: String,
    val company: String,
    val contractNumber: String = "",
    val location: String = "",
    val consultant: String = "",
    val projectManager: String = "",
    val siteManager: String = "",
    val period: String = "",
    val reportDate: String = "",
    val notes: String = "",
    val contractorsJson: String = "[]",
    val logoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)

data class ContractorInfo(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val workType: String, // e.g. STRUKTUR, ARSITEK, MEP, UMUM
    val picName: String,
    val picPhone: String = "",
    val notes: String = ""
)
