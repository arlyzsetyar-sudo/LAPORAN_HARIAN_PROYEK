package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ContractorInfo
import com.example.data.model.Project
import com.example.ui.components.CategoryBadge
import com.example.ui.components.ContractorDialog
import com.example.ui.components.PastelCard
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCardBorder
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProjectScreen(
    projects: List<Project>,
    activeProject: Project?,
    parseContractors: (String) -> List<ContractorInfo>,
    onSetActive: (Long) -> Unit,
    onSaveProject: (Project, List<ContractorInfo>) -> Unit,
    onDeleteProject: (Project) -> Unit
) {
    var showProjectDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<Project?>(null) }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Hapus Proyek?") },
            text = { Text("Menghapus proyek '${projectToDelete?.name}' akan menghapus data proyek ini.") },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { onDeleteProject(it) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showProjectDialog) {
        ProjectFormDialog(
            project = editingProject,
            initialContractors = editingProject?.let { parseContractors(it.contractorsJson) } ?: emptyList(),
            onDismiss = {
                showProjectDialog = false
                editingProject = null
            },
            onSave = { proj, contractors ->
                onSaveProject(proj, contractors)
                showProjectDialog = false
                editingProject = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelCreamBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Proyek",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Kelola Proyek, Kontraktor & PIC Lapangan",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = {
                        editingProject = null
                        showProjectDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PastelSagePrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("add_project_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Proyek Baru", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(projects) { project ->
                    val isActive = project.id == activeProject?.id
                    val contractors = parseContractors(project.contractorsJson)

                    PastelCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.White,
                        borderColor = if (isActive) PastelSagePrimary else PastelCardBorder,
                        cornerRadius = 16
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isActive) PastelSageContainer else PastelBeigeSurface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.Business,
                                            contentDescription = null,
                                            tint = if (isActive) PastelSagePrimary else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = project.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Owner: ${project.owner.ifEmpty { "-" }}",
                                            fontSize = 12.sp,
                                            color = PastelDustyBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PastelSageContainer)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "AKTIF",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PastelSageOnContainer
                                        )
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onSetActive(project.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Pilih", fontSize = 11.sp, color = PastelSagePrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Lokasi: ${project.location.ifEmpty { "-" }}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "Kontrak: ${project.contractNumber.ifEmpty { "-" }}  •  Periode: ${project.period.ifEmpty { "-" }}",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )

                            // Contractors Section in Card
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Kontraktor (${contractors.size}):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            if (contractors.isEmpty()) {
                                Text(
                                    text = "Belum ada kontraktor ditambahkan",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            } else {
                                contractors.take(3).forEach { c ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryBadge(category = c.workType)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${c.name} (PIC: ${c.picName})",
                                            fontSize = 11.5.sp,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            // Card Actions
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        editingProject = project
                                        showProjectDialog = true
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Proyek", tint = PastelSagePrimary, modifier = Modifier.size(18.dp))
                                }

                                IconButton(
                                    onClick = { projectToDelete = project },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Hapus Proyek", tint = StatusRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full project form dialog supporting project information and dynamic contractor list.
 * Requirement:
 * - Tambahkan Kontraktor dengan pilihan pekerjaan
 * - Tambahkan PIC Kontraktor
 * - Data Kontraktor Bisa di edit
 */
@Composable
private fun ProjectFormDialog(
    project: Project?,
    initialContractors: List<ContractorInfo>,
    onDismiss: () -> Unit,
    onSave: (Project, List<ContractorInfo>) -> Unit
) {
    var name by remember { mutableStateOf(project?.name ?: "") }
    var owner by remember { mutableStateOf(project?.owner ?: "") }
    var company by remember { mutableStateOf(project?.company ?: "") }
    var contractNumber by remember { mutableStateOf(project?.contractNumber ?: "") }
    var location by remember { mutableStateOf(project?.location ?: "") }
    var consultant by remember { mutableStateOf(project?.consultant ?: "") }
    var projectManager by remember { mutableStateOf(project?.projectManager ?: "") }
    var siteManager by remember { mutableStateOf(project?.siteManager ?: "") }
    var period by remember { mutableStateOf(project?.period ?: "") }
    var reportDate by remember { mutableStateOf(project?.reportDate ?: "") }
    var notes by remember { mutableStateOf(project?.notes ?: "") }

    var contractors by remember { mutableStateOf(initialContractors) }
    var showContractorSubDialog by remember { mutableStateOf(false) }
    var editingContractorIndex by remember { mutableStateOf<Int?>(null) }

    if (showContractorSubDialog) {
        val contractorToEdit = editingContractorIndex?.let { contractors.getOrNull(it) }
        ContractorDialog(
            initialContractor = contractorToEdit,
            onDismiss = {
                showContractorSubDialog = false
                editingContractorIndex = null
            },
            onSave = { savedContractor ->
                val list = contractors.toMutableList()
                val idx = editingContractorIndex
                if (idx != null && idx in list.indices) {
                    list[idx] = savedContractor
                } else {
                    list.add(savedContractor)
                }
                contractors = list
                showContractorSubDialog = false
                editingContractorIndex = null
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = PastelCreamBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (project == null) "Tambah Proyek Baru" else "Edit Data Proyek",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Tutup")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Proyek *") },
                            placeholder = { Text("Contoh: Pembangunan Villa Resort Bali") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("project_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = owner,
                            onValueChange = { owner = it },
                            label = { Text("Nama Owner (Pemberi Tugas) *") },
                            placeholder = { Text("Contoh: PT Hotel Sejahtera Abadi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("project_owner_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = company,
                            onValueChange = { company = it },
                            label = { Text("Nama Perusahaan / KSO") },
                            placeholder = { Text("Contoh: KSO Nusa Cipta Konstruksi") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = contractNumber,
                            onValueChange = { contractNumber = it },
                            label = { Text("Nomor Kontrak") },
                            placeholder = { Text("Contoh: KTR-2026/08/PROYEK-01") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Lokasi Proyek") },
                            placeholder = { Text("Contoh: Jl. Pantai Pandawa, Badung") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = consultant,
                            onValueChange = { consultant = it },
                            label = { Text("Nama Konsultan Pengawas / MK") },
                            placeholder = { Text("Contoh: PT Ciriajasa Engineering") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = projectManager,
                                onValueChange = { projectManager = it },
                                label = { Text("Project Manager") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = siteManager,
                                onValueChange = { siteManager = it },
                                label = { Text("Site Manager") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = period,
                                onValueChange = { period = it },
                                label = { Text("Periode Laporan") },
                                placeholder = { Text("Minggu ke-42") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = reportDate,
                                onValueChange = { reportDate = it },
                                label = { Text("Tanggal Laporan") },
                                placeholder = { Text("04 Oktober 2026") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // KONTRAKTOR MANAGEMENT SUB-SECTION
                        Spacer(modifier = Modifier.height(4.dp))
                        PastelCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Data Kontraktor & PIC",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Tambahkan Kontraktor & PIC (Struktur, Arsitek, MEP)",
                                            fontSize = 10.5.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            editingContractorIndex = null
                                            showContractorSubDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PastelSagePrimary),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("add_contractor_modal_button")
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tambah", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (contractors.isEmpty()) {
                                    Text(
                                        text = "Belum ada kontraktor ditambahkan. Tekan tombol Tambah di atas.",
                                        fontSize = 11.5.sp,
                                        color = TextTertiary
                                    )
                                } else {
                                    contractors.forEachIndexed { index, contractor ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PastelBeigeSurface)
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    CategoryBadge(category = contractor.workType)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = contractor.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = TextPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Text(
                                                    text = "PIC: ${contractor.picName} • ${contractor.picPhone.ifEmpty { "Tanpa telp" }}",
                                                    fontSize = 10.5.sp,
                                                    color = TextSecondary
                                                )
                                            }

                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        editingContractorIndex = index
                                                        showContractorSubDialog = true
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = PastelSagePrimary)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        val list = contractors.toMutableList()
                                                        list.removeAt(index)
                                                        contractors = list
                                                    },
                                                    modifier = Modifier.size(30.dp)
                                                ) {
                                                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", modifier = Modifier.size(16.dp), tint = StatusRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Keterangan Tambahan") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Batal", color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val updatedProject = (project ?: Project(name = name, owner = owner, company = company)).copy(
                                        name = name.trim(),
                                        owner = owner.trim(),
                                        company = company.trim(),
                                        contractNumber = contractNumber.trim(),
                                        location = location.trim(),
                                        consultant = consultant.trim(),
                                        projectManager = projectManager.trim(),
                                        siteManager = siteManager.trim(),
                                        period = period.trim(),
                                        reportDate = reportDate.trim(),
                                        notes = notes.trim()
                                    )
                                    onSave(updatedProject, contractors)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PastelSagePrimary),
                            modifier = Modifier.testTag("submit_project_button")
                        ) {
                            Text("Simpan Proyek")
                        }
                    }
                }
            }
        }
    }
}
