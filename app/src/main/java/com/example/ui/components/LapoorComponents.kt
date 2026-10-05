package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContractorInfo
import com.example.ui.theme.PastelBeigeSurface
import com.example.ui.theme.PastelCardBorder
import com.example.ui.theme.PastelCreamBackground
import com.example.ui.theme.PastelDustyBlue
import com.example.ui.theme.PastelDustyBlueContainer
import com.example.ui.theme.PastelDustyBlueOnContainer
import com.example.ui.theme.PastelMutedPeach
import com.example.ui.theme.PastelPeachContainer
import com.example.ui.theme.PastelPeachOnContainer
import com.example.ui.theme.PastelSageContainer
import com.example.ui.theme.PastelSageOnContainer
import com.example.ui.theme.PastelSagePrimary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusBlueBg
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusPurple
import com.example.ui.theme.StatusPurpleBg
import com.example.ui.theme.StatusRed
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun PastelCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = PastelCardBorder,
    cornerRadius: Int = 16,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(cornerRadius.dp),
                spotColor = Color(0x18000000)
            )
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(cornerRadius.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bg, textColor) = when (status) {
        "Selesai" -> StatusGreenBg to StatusGreen
        "Sedang Dikerjakan" -> StatusBlueBg to StatusBlue
        "Progress" -> StatusPurpleBg to StatusPurple
        "Perbaikan" -> StatusRedBg to StatusRed
        else -> StatusAmberBg to StatusAmber
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun CategoryBadge(category: String, modifier: Modifier = Modifier) {
    val (bg, textColor) = when (category.uppercase()) {
        "STRUKTUR" -> PastelSageContainer to PastelSageOnContainer
        "ARSITEK" -> PastelDustyBlueContainer to PastelDustyBlueOnContainer
        "MEP" -> PastelPeachContainer to PastelPeachOnContainer
        else -> PastelBeigeSurface to TextSecondary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.uppercase(),
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun PastelProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    color: Color = PastelSagePrimary
) {
    val clamped = (progress.coerceIn(0, 100) / 100f)
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(PastelBeigeSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clamped)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun EmptyStateWidget(
    title: String = "Belum Ada Dokumentasi",
    subtitle: String = "Mulai dokumentasi pekerjaan dengan mengambil foto melalui kamera.",
    icon: ImageVector = Icons.Outlined.PhotoCamera,
    buttonText: String? = "AMBIL FOTO",
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(PastelSageContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PastelSagePrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelSagePrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("empty_state_action_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = buttonText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Dialog to add or edit contractor with work type selection and PIC info.
 * Requirement:
 * - Tambahkan Kontraktor dengan pilihan pekerjaan
 * - Tambahkan PIC Kontraktor
 * - Data Kontraktor Bisa di edit
 */
@Composable
fun ContractorDialog(
    initialContractor: ContractorInfo? = null,
    onDismiss: () -> Unit,
    onSave: (ContractorInfo) -> Unit
) {
    var name by remember { mutableStateOf(initialContractor?.name ?: "") }
    var selectedWorkType by remember { mutableStateOf(initialContractor?.workType ?: "STRUKTUR") }
    var picName by remember { mutableStateOf(initialContractor?.picName ?: "") }
    var picPhone by remember { mutableStateOf(initialContractor?.picPhone ?: "") }
    var notes by remember { mutableStateOf(initialContractor?.notes ?: "") }

    val workTypes = listOf("STRUKTUR", "ARSITEK", "MEP", "UMUM")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialContractor == null) "Tambah Kontraktor Proyek" else "Edit Data Kontraktor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kontraktor / Perusahaan") },
                    placeholder = { Text("Contoh: PT Bangun Cipta Struktur") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contractor_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelSagePrimary,
                        unfocusedBorderColor = PastelCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Pilihan Pekerjaan / Lingkup:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    workTypes.forEach { type ->
                        FilterChip(
                            selected = selectedWorkType.equals(type, ignoreCase = true),
                            onClick = { selectedWorkType = type },
                            label = { Text(type, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (type) {
                                    "STRUKTUR" -> PastelSageContainer
                                    "ARSITEK" -> PastelDustyBlueContainer
                                    "MEP" -> PastelPeachContainer
                                    else -> PastelBeigeSurface
                                },
                                selectedLabelColor = when (type) {
                                    "STRUKTUR" -> PastelSageOnContainer
                                    "ARSITEK" -> PastelDustyBlueOnContainer
                                    "MEP" -> PastelPeachOnContainer
                                    else -> TextPrimary
                                }
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = picName,
                    onValueChange = { picName = it },
                    label = { Text("Nama PIC Kontraktor") },
                    placeholder = { Text("Contoh: Ir. Budi Santoso") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contractor_pic_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelSagePrimary,
                        unfocusedBorderColor = PastelCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = picPhone,
                    onValueChange = { picPhone = it },
                    label = { Text("Nomor Kontak PIC") },
                    placeholder = { Text("Contoh: 0812-3456-7890") },
                    leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("contractor_pic_phone_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelSagePrimary,
                        unfocusedBorderColor = PastelCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan Tambahan") },
                    placeholder = { Text("Contoh: Sub-spesialis pembesian & bekisting") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelSagePrimary,
                        unfocusedBorderColor = PastelCardBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val result = (initialContractor ?: ContractorInfo(
                            name = name.trim(),
                            workType = selectedWorkType,
                            picName = picName.trim(),
                            picPhone = picPhone.trim(),
                            notes = notes.trim()
                        )).copy(
                            name = name.trim(),
                            workType = selectedWorkType,
                            picName = picName.trim(),
                            picPhone = picPhone.trim(),
                            notes = notes.trim()
                        )
                        onSave(result)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelSagePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_contractor_button")
            ) {
                Text("Simpan Kontraktor")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}

/**
 * Quick dialog to add new location dynamically
 */
@Composable
fun AddLocationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var locationName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Lokasi / Zona Baru",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Masukkan nama area atau zona kerja proyek (contoh: Villa 05, Sky Lounge, Basement B2):",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Nama Lokasi") },
                    placeholder = { Text("Contoh: Villa 05") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("location_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PastelSagePrimary,
                        unfocusedBorderColor = PastelCardBorder
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (locationName.isNotBlank()) {
                        onConfirm(locationName.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PastelSagePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_location_button")
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}
