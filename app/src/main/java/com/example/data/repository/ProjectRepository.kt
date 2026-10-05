package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.GeneratedReport
import com.example.data.model.LocationEntity
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class ProjectRepository(private val database: AppDatabase) {
    private val projectDao = database.projectDao()
    private val locationDao = database.locationDao()
    private val photoDao = database.photoDao()
    private val reportDao = database.reportDao()

    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val activeProject: Flow<Project?> = projectDao.getActiveProject()

    fun getLocations(projectId: Long): Flow<List<LocationEntity>> =
        locationDao.getLocationsByProject(projectId)

    fun getPhotos(projectId: Long): Flow<List<ProgressPhoto>> =
        photoDao.getPhotosByProject(projectId)

    fun getPhotosByCategory(projectId: Long, category: String): Flow<List<ProgressPhoto>> =
        photoDao.getPhotosByCategory(projectId, category)

    fun getPhotosByLocation(projectId: Long, locationName: String): Flow<List<ProgressPhoto>> =
        photoDao.getPhotosByLocation(projectId, locationName)

    fun getPhotosGroupedByDate(projectId: Long): Flow<List<ProgressPhoto>> =
        photoDao.getPhotosGroupedByDate(projectId)

    fun getPhotoCount(projectId: Long): Flow<Int> = photoDao.getPhotoCount(projectId)
    fun getActiveLocationCount(projectId: Long): Flow<Int> = photoDao.getActiveLocationCount(projectId)
    fun getAverageProgress(projectId: Long): Flow<Double?> = photoDao.getAverageProgress(projectId)
    fun getReports(projectId: Long): Flow<List<GeneratedReport>> = reportDao.getReportsByProject(projectId)

    suspend fun saveProject(project: Project): Long {
        return if (project.id == 0L) {
            val count = projectDao.getAllProjects().firstOrNull()?.size ?: 0
            val isFirst = count == 0
            val newProject = if (isFirst) project.copy(isActive = true) else project
            val newId = projectDao.insertProject(newProject)
            // Seed default locations for the new project
            seedDefaultLocations(newId)
            newId
        } else {
            projectDao.updateProject(project)
            project.id
        }
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }

    suspend fun setActiveProject(projectId: Long) {
        projectDao.deactivateAllProjects()
        projectDao.setActiveProject(projectId)
    }

    suspend fun addLocation(projectId: Long, name: String): Long {
        return locationDao.insertLocation(LocationEntity(projectId = projectId, name = name.trim()))
    }

    suspend fun deleteLocation(location: LocationEntity) {
        locationDao.deleteLocation(location)
    }

    suspend fun savePhoto(photo: ProgressPhoto): Long {
        return photoDao.insertPhoto(photo)
    }

    suspend fun updatePhoto(photo: ProgressPhoto) {
        photoDao.updatePhoto(photo)
    }

    suspend fun deletePhoto(photo: ProgressPhoto) {
        photoDao.deletePhoto(photo)
    }

    suspend fun saveReport(report: GeneratedReport): Long {
        return reportDao.insertReport(report)
    }

    suspend fun deleteReport(report: GeneratedReport) {
        reportDao.deleteReport(report)
    }

    private suspend fun seedDefaultLocations(projectId: Long) {
        val defaults = listOf(
            "Villa 01", "Villa 02", "Villa 03", "Villa 04",
            "Hill Club", "Beach Club", "Marine Sport Center",
            "Jetty", "BOH (Back of House)", "Landscape"
        )
        for (name in defaults) {
            locationDao.insertLocation(LocationEntity(projectId = projectId, name = name))
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        val existing = projectDao.getAllProjects().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val sampleContractors = """
                [
                  {"id":"c1","name":"PT Waskita Karya (Persero)","workType":"STRUKTUR","picName":"Ir. Budi Prasetyo","picPhone":"0812-3456-7890","notes":"Main contractor struktur gedung"},
                  {"id":"c2","name":"PT Total Bangun Persada","workType":"ARSITEK","picName":"Ar. Siti Rahma","picPhone":"0813-9876-5432","notes":"Finishing, interior & fasad"},
                  {"id":"c3","name":"PT Jaya Teknik MEP","workType":"MEP","picName":"Dedi Kurniawan, S.T.","picPhone":"0811-2233-4455","notes":"Electrical, HVAC & Plumbing"}
                ]
            """.trimIndent()

            val defaultProject = Project(
                id = 0,
                name = "Balak Luxury Resort & Spa",
                owner = "PT Balak Hospitality Nusantara",
                company = "KSO Balak Cipta Karya",
                contractNumber = "KTR-2026/BLR-09/081",
                location = "Kawasan Wisata Bahari Bukit Indah, Lombok Barat",
                consultant = "PT Arkonin Engineering & Architecture",
                projectManager = "Hendrawan Pratama, M.T.",
                siteManager = "Agus Subroto, S.T.",
                period = "Minggu ke-42 (Oktober 2026)",
                reportDate = "04 Oktober 2026",
                notes = "Pekerjaan struktur villa utama telah mencapai 75%. Dilanjutkan arsitektur interior dan instalasi MEP paralel.",
                contractorsJson = sampleContractors,
                isActive = true
            )
            val newId = projectDao.insertProject(defaultProject)
            seedDefaultLocations(newId)
        }
    }
}
