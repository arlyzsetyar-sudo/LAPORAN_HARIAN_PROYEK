package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ContractorInfo
import com.example.data.model.GeneratedReport
import com.example.data.model.LocationEntity
import com.example.data.model.ProgressPhoto
import com.example.data.model.Project
import com.example.data.repository.ProjectRepository
import com.example.engine.ImageWatermarkHelper
import com.example.engine.ReportConfig
import com.example.engine.ReportPdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LapoorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    init {
        val db = AppDatabase.getInstance(application)
        repository = ProjectRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val allProjects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProject: StateFlow<Project?> = repository.activeProject
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val locations: StateFlow<List<LocationEntity>> = activeProject.flatMapLatest { proj ->
        if (proj != null) repository.getLocations(proj.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val photos: StateFlow<List<ProgressPhoto>> = activeProject.flatMapLatest { proj ->
        if (proj != null) repository.getPhotos(proj.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val reports: StateFlow<List<GeneratedReport>> = activeProject.flatMapLatest { proj ->
        if (proj != null) repository.getReports(proj.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Group photos by capture date for the Gallery by Date menu
    val photosGroupedByDate: StateFlow<Map<String, List<ProgressPhoto>>> = photos.combine(flowOf(Unit)) { list, _ ->
        list.groupBy { it.dateStr }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Photos selected for report generation
    private val _selectedPhotoIdsForReport = MutableStateFlow<Set<Long>>(emptySet())
    val selectedPhotoIdsForReport: StateFlow<Set<Long>> = _selectedPhotoIdsForReport.asStateFlow()

    // Report generation state
    private val _isGeneratingReport = MutableStateFlow(false)
    val isGeneratingReport: StateFlow<Boolean> = _isGeneratingReport.asStateFlow()

    private val _lastGeneratedPdfFile = MutableStateFlow<File?>(null)
    val lastGeneratedPdfFile: StateFlow<File?> = _lastGeneratedPdfFile.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun setActiveProject(projectId: Long) {
        viewModelScope.launch {
            repository.setActiveProject(projectId)
            _selectedPhotoIdsForReport.value = emptySet()
        }
    }

    fun saveProject(
        project: Project,
        contractors: List<ContractorInfo>
    ) {
        viewModelScope.launch {
            val contractorsJson = serializeContractors(contractors)
            val updated = project.copy(contractorsJson = contractorsJson)
            repository.saveProject(updated)
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun addLocation(name: String) {
        val projId = activeProject.value?.id ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addLocation(projId, name)
        }
    }

    fun deleteLocation(location: LocationEntity) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    fun saveCapturedPhoto(
        tempFile: File,
        locationName: String,
        workCategory: String,
        subWork: String,
        caption: String,
        fieldNotes: String,
        progress: Int,
        status: String
    ) {
        val proj = activeProject.value ?: return
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                // Process and apply watermark
                val watermarkedFile = ImageWatermarkHelper.processAndWatermarkPhoto(
                    context = getApplication(),
                    sourceFile = tempFile,
                    projectName = proj.name,
                    locationName = locationName,
                    workCategory = workCategory,
                    timestamp = now
                )

                val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date(now))
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now))
                val photoCount = photos.value.count { it.locationName == locationName } + 1

                val photo = ProgressPhoto(
                    projectId = proj.id,
                    locationName = locationName,
                    workCategory = workCategory,
                    subWork = subWork,
                    filePath = watermarkedFile.absolutePath,
                    timestamp = now,
                    dateStr = dateStr,
                    timeStr = timeStr,
                    caption = caption,
                    fieldNotes = fieldNotes,
                    progress = progress,
                    status = status,
                    photoNumber = photoCount
                )
                repository.savePhoto(photo)
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menyimpan foto: ${e.message}"
            }
        }
    }

    fun updatePhoto(photo: ProgressPhoto) {
        viewModelScope.launch {
            repository.updatePhoto(photo)
        }
    }

    fun deletePhoto(photo: ProgressPhoto) {
        viewModelScope.launch {
            try {
                val f = File(photo.filePath)
                if (f.exists()) f.delete()
            } catch (_: Exception) {}
            repository.deletePhoto(photo)
        }
    }

    // Report photo selection
    fun togglePhotoSelectionForReport(photoId: Long) {
        val current = _selectedPhotoIdsForReport.value.toMutableSet()
        if (current.contains(photoId)) {
            current.remove(photoId)
        } else {
            current.add(photoId)
        }
        _selectedPhotoIdsForReport.value = current
    }

    fun selectAllPhotosForReport(all: List<ProgressPhoto>) {
        _selectedPhotoIdsForReport.value = all.map { it.id }.toSet()
    }

    fun clearPhotoSelectionForReport() {
        _selectedPhotoIdsForReport.value = emptySet()
    }

    // Generate PDF
    fun generatePdfReport(
        config: ReportConfig,
        selectedLocations: List<String> = emptyList(),
        onSuccess: (File) -> Unit
    ) {
        val proj = activeProject.value
        if (proj == null) {
            _errorMessage.value = "Laporan belum dapat dibuat. Silakan pilih atau buat proyek terlebih dahulu."
            return
        }

        viewModelScope.launch {
            _isGeneratingReport.value = true
            try {
                // Filter photos based on selection, location filter, and discipline filter
                val allProjectPhotos = photos.value

                var filtered = if (_selectedPhotoIdsForReport.value.isNotEmpty()) {
                    allProjectPhotos.filter { _selectedPhotoIdsForReport.value.contains(it.id) }
                } else {
                    allProjectPhotos
                }

                if (selectedLocations.isNotEmpty() && !selectedLocations.contains("Semua Lokasi")) {
                    filtered = filtered.filter { selectedLocations.contains(it.locationName) }
                }

                if (config.selectedDiscipline != "Semua Pekerjaan") {
                    filtered = filtered.filter { it.workCategory.equals(config.selectedDiscipline, ignoreCase = true) }
                }

                if (filtered.isEmpty()) {
                    _errorMessage.value = "Laporan belum dapat dibuat. Silakan periksa kembali data dan pilih dokumentasi foto."
                    _isGeneratingReport.value = false
                    return@launch
                }

                val generatedFile = ReportPdfGenerator.generatePdf(
                    context = getApplication(),
                    project = proj,
                    photos = filtered,
                    config = config
                )

                _lastGeneratedPdfFile.value = generatedFile

                // Save report entity to database
                val reportEntity = GeneratedReport(
                    projectId = proj.id,
                    projectName = proj.name,
                    title = config.title,
                    period = config.periodText,
                    date = config.reportDate,
                    author = config.author,
                    role = config.role,
                    filePath = generatedFile.absolutePath
                )
                repository.saveReport(reportEntity)

                onSuccess(generatedFile)
            } catch (e: Exception) {
                _errorMessage.value = "Laporan belum dapat dibuat: ${e.message}"
            } finally {
                _isGeneratingReport.value = false
            }
        }
    }

    fun deleteReport(report: GeneratedReport) {
        viewModelScope.launch {
            try {
                val f = File(report.filePath)
                if (f.exists()) f.delete()
            } catch (_: Exception) {}
            repository.deleteReport(report)
        }
    }

    private fun serializeContractors(list: List<ContractorInfo>): String {
        val array = JSONArray()
        for (c in list) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("workType", c.workType)
            obj.put("picName", c.picName)
            obj.put("picPhone", c.picPhone)
            obj.put("notes", c.notes)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseContractors(jsonStr: String): List<ContractorInfo> {
        val list = mutableListOf<ContractorInfo>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ContractorInfo(
                        id = obj.optString("id", i.toString()),
                        name = obj.optString("name", ""),
                        workType = obj.optString("workType", "UMUM"),
                        picName = obj.optString("picName", ""),
                        picPhone = obj.optString("picPhone", ""),
                        notes = obj.optString("notes", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
