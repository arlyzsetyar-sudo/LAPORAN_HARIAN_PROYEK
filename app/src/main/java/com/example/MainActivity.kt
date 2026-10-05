package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProgressPhoto
import com.example.engine.ReportConfig
import com.example.ui.screens.CameraCaptureScreen
import com.example.ui.screens.GalleryByDateScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectScreen
import com.example.ui.screens.ReportPreviewScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.WorkCategoryScreen
import com.example.ui.theme.LapoorTheme
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LapoorViewModel

enum class NavigationTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    PROYEK("Proyek", Icons.Filled.Business, Icons.Outlined.Business),
    FOTO("Ambil Foto", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    GALLERY("Gallery", Icons.Filled.Collections, Icons.Outlined.Collections),
    LAPORAN("Laporan", Icons.Filled.PictureAsPdf, Icons.Outlined.PictureAsPdf),
    PROFIL("Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

sealed class SubScreen {
    data object None : SubScreen()
    data class WorkCategory(val category: String) : SubScreen()
    data class ReportPreview(val config: ReportConfig, val selectedLocations: List<String>) : SubScreen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: LapoorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LapoorTheme {
                val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
                val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
                val locations by viewModel.locations.collectAsStateWithLifecycle()
                val photos by viewModel.photos.collectAsStateWithLifecycle()
                val reports by viewModel.reports.collectAsStateWithLifecycle()
                val selectedPhotoIds by viewModel.selectedPhotoIdsForReport.collectAsStateWithLifecycle()
                val isGeneratingReport by viewModel.isGeneratingReport.collectAsStateWithLifecycle()
                val lastPdfFile by viewModel.lastGeneratedPdfFile.collectAsStateWithLifecycle()
                val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }
                var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
                var currentSubScreen by remember { mutableStateOf<SubScreen>(SubScreen.None) }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearError()
                    }
                }

                // Handle system back navigation
                if (currentSubScreen !is SubScreen.None) {
                    BackHandler {
                        currentSubScreen = SubScreen.None
                    }
                } else if (currentTab != NavigationTab.HOME) {
                    BackHandler {
                        currentTab = NavigationTab.HOME
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        // Hide bottom bar when inside full camera mode or preview subscreen
                        val shouldShowBottomBar = currentSubScreen is SubScreen.None && currentTab != NavigationTab.FOTO
                        if (shouldShowBottomBar) {
                            NavigationBar(
                                containerColor = Color.White,
                                contentColor = PastelSagePrimary
                            ) {
                                NavigationTab.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            currentTab = tab
                                            currentSubScreen = SubScreen.None
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                                contentDescription = tab.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = PastelSagePrimary,
                                            selectedTextColor = PastelSagePrimary,
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary,
                                            indicatorColor = PastelSageContainer
                                        ),
                                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (val sub = currentSubScreen) {
                            is SubScreen.WorkCategory -> {
                                WorkCategoryScreen(
                                    initialCategory = sub.category,
                                    photos = photos,
                                    onNavigateToCamera = {
                                        currentSubScreen = SubScreen.None
                                        currentTab = NavigationTab.FOTO
                                    },
                                    onPhotoClick = { photo ->
                                        // View photo
                                    },
                                    onBack = { currentSubScreen = SubScreen.None }
                                )
                            }
                            is SubScreen.ReportPreview -> {
                                val proj = activeProject
                                if (proj != null) {
                                    val filteredForPreview = if (selectedPhotoIds.isNotEmpty()) {
                                        photos.filter { selectedPhotoIds.contains(it.id) }
                                    } else {
                                        photos
                                    }.let { list ->
                                        if (sub.selectedLocations.isNotEmpty() && !sub.selectedLocations.contains("Semua Lokasi")) {
                                            list.filter { sub.selectedLocations.contains(it.locationName) }
                                        } else list
                                    }.let { list ->
                                        if (sub.config.selectedDiscipline != "Semua Pekerjaan") {
                                            list.filter { it.workCategory.equals(sub.config.selectedDiscipline, true) }
                                        } else list
                                    }

                                    ReportPreviewScreen(
                                        project = proj,
                                        photos = filteredForPreview,
                                        contractors = viewModel.parseContractors(proj.contractorsJson),
                                        config = sub.config,
                                        onBack = { currentSubScreen = SubScreen.None },
                                        onEdit = { currentSubScreen = SubScreen.None },
                                        onGeneratePdf = {
                                            viewModel.generatePdfReport(sub.config, sub.selectedLocations) { file ->
                                                Toast.makeText(this@MainActivity, "PDF berhasil digenerate: ${file.name}", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        lastPdfFile = lastPdfFile
                                    )
                                } else {
                                    currentSubScreen = SubScreen.None
                                }
                            }
                            is SubScreen.None -> {
                                when (currentTab) {
                                    NavigationTab.HOME -> {
                                        HomeScreen(
                                            activeProject = activeProject,
                                            photos = photos,
                                            locationsCount = locations.size,
                                            onNavigateToCamera = { currentTab = NavigationTab.FOTO },
                                            onNavigateToProjects = { currentTab = NavigationTab.PROYEK },
                                            onNavigateToGallery = { currentTab = NavigationTab.GALLERY },
                                            onNavigateToReport = { currentTab = NavigationTab.LAPORAN },
                                            onNavigateToWorkCategory = { cat ->
                                                currentSubScreen = SubScreen.WorkCategory(cat)
                                            },
                                            onPhotoClick = { photo ->
                                                currentTab = NavigationTab.GALLERY
                                            }
                                        )
                                    }
                                    NavigationTab.PROYEK -> {
                                        ProjectScreen(
                                            projects = allProjects,
                                            activeProject = activeProject,
                                            parseContractors = { viewModel.parseContractors(it) },
                                            onSetActive = { viewModel.setActiveProject(it) },
                                            onSaveProject = { proj, contractors ->
                                                viewModel.saveProject(proj, contractors)
                                            },
                                            onDeleteProject = { viewModel.deleteProject(it) }
                                        )
                                    }
                                    NavigationTab.FOTO -> {
                                        CameraCaptureScreen(
                                            activeProject = activeProject,
                                            locations = locations,
                                            onAddLocation = { viewModel.addLocation(it) },
                                            onSavePhoto = { file, loc, cat, subWork, cap, notes, prog, status ->
                                                viewModel.saveCapturedPhoto(file, loc, cat, subWork, cap, notes, prog, status)
                                                Toast.makeText(this@MainActivity, "Foto dokumentasi tersimpan!", Toast.LENGTH_SHORT).show()
                                                currentTab = NavigationTab.GALLERY
                                            },
                                            onBack = { currentTab = NavigationTab.HOME }
                                        )
                                    }
                                    NavigationTab.GALLERY -> {
                                        GalleryByDateScreen(
                                            photos = photos,
                                            onNavigateToCamera = { currentTab = NavigationTab.FOTO },
                                            onUpdatePhoto = { viewModel.updatePhoto(it) },
                                            onDeletePhoto = { viewModel.deletePhoto(it) }
                                        )
                                    }
                                    NavigationTab.LAPORAN -> {
                                        ReportScreen(
                                            activeProject = activeProject,
                                            photos = photos,
                                            locations = locations,
                                            reports = reports,
                                            selectedPhotoIds = selectedPhotoIds,
                                            isGenerating = isGeneratingReport,
                                            lastPdfFile = lastPdfFile,
                                            onTogglePhotoSelection = { viewModel.togglePhotoSelectionForReport(it) },
                                            onSelectAllPhotos = { viewModel.selectAllPhotosForReport(it) },
                                            onClearPhotoSelection = { viewModel.clearPhotoSelectionForReport() },
                                            onGenerateReport = { config, locs, onSuccess ->
                                                viewModel.generatePdfReport(config, locs) { file ->
                                                    Toast.makeText(this@MainActivity, "PDF Berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                                    onSuccess(file)
                                                }
                                            },
                                            onDeleteReport = { viewModel.deleteReport(it) },
                                            onPreviewReport = { config, locs ->
                                                currentSubScreen = SubScreen.ReportPreview(config, locs)
                                            },
                                            onBack = { currentTab = NavigationTab.HOME }
                                        )
                                    }
                                    NavigationTab.PROFIL -> {
                                        ProfileScreen(
                                            activeProject = activeProject,
                                            totalPhotos = photos.size,
                                            totalLocations = locations.size
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
