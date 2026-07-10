package com.example.expense_tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expense_tracker.data.local.MedicationEntity
import com.example.expense_tracker.ui.theme.BlueCrossPrimary
import com.example.expense_tracker.ui.theme.ClaySurfaceVariant
import com.example.expense_tracker.ui.theme.ClayCard
import com.example.expense_tracker.ui.theme.ClayButton
import com.example.expense_tracker.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationScreen(
    viewModel: MedicationViewModel,
    onMedicamentoGuardado: (MedicationEntity) -> Unit,
    onRecordatorioToggle: (MedicationEntity) -> Unit,
    onRecordatorioDesactivado: (MedicationEntity) -> Unit,
    onHoraActualizada: (MedicationEntity) -> Unit,
    onMedicamentoEliminado: (MedicationEntity) -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val dosis by viewModel.dosis.collectAsState()
    val horaSeleccionada by viewModel.horaSeleccionada.collectAsState()
    val minutoSeleccionado by viewModel.minutoSeleccionado.collectAsState()
    val iconoSeleccionado by viewModel.iconoSeleccionado.collectAsState()
    val medicamentos by viewModel.medicamentos.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💊 ", fontSize = 22.sp)
                        Text(
                            "Mis Medicamentos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = BlueCrossPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            FormularioMedicamento(
                nombre = nombre,
                dosis = dosis,
                hora = horaSeleccionada,
                minuto = minutoSeleccionado,
                iconoSeleccionado = iconoSeleccionado,
                iconosDisponibles = viewModel.iconosDisponibles,
                onNombreChange = { viewModel.actualizarNombre(it) },
                onDosisChange = { viewModel.actualizarDosis(it) },
                onIconoChange = { viewModel.actualizarIcono(it) },
                onHoraChange = { h, m -> viewModel.actualizarHoraSeleccionada(h, m) },
                onGuardar = {
                    viewModel.guardarMedicamento { medicamentoGuardado ->
                        onMedicamentoGuardado(medicamentoGuardado)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recordatorios activos",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (medicamentos.isEmpty()) {
                Text(
                    text = "No hay medicamentos registrados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            } else {
                medicamentos.forEach { medicamento ->
                    Spacer(modifier = Modifier.height(10.dp))
                    MedicamentoItem(
                        medicamento = medicamento,
                        onToggleActivo = { activo ->
                            viewModel.cambiarEstadoRecordatorio(medicamento, activo) { actualizado ->
                                if (activo) onRecordatorioToggle(actualizado)
                                else onRecordatorioDesactivado(actualizado)
                            }
                        },
                        onHoraChange = { hora, minuto ->
                            viewModel.actualizarHoraMedicamento(medicamento, hora, minuto) { actualizado ->
                                onHoraActualizada(actualizado)
                            }
                        },
                        onEliminar = {
                            viewModel.eliminarMedicamento(medicamento) { eliminado ->
                                onMedicamentoEliminado(eliminado)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Selector de emoji/ícono en forma de "chips" horizontales.
 */
@Composable
fun SelectorIcono(
    iconos: List<String>,
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(iconos) { emoji ->
            val estaSeleccionado = emoji == seleccionado
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (estaSeleccionado) BlueCrossPrimary else ClaySurfaceVariant
                    )
                    .clickable { onSeleccionar(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioMedicamento(
    nombre: String,
    dosis: String,
    hora: Int,
    minuto: Int,
    iconoSeleccionado: String,
    iconosDisponibles: List<String>,
    onNombreChange: (String) -> Unit,
    onDosisChange: (String) -> Unit,
    onIconoChange: (String) -> Unit,
    onHoraChange: (Int, Int) -> Unit,
    onGuardar: () -> Unit
) {
    var mostrarTimePicker by remember { mutableStateOf(false) }
    val campoShape = RoundedCornerShape(20.dp)

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Nuevo medicamento",
                style = MaterialTheme.typography.titleMedium,
                color = BlueCrossPrimary
            )

            Text(
                text = "Elige un ícono",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            SelectorIcono(
                iconos = iconosDisponibles,
                seleccionado = iconoSeleccionado,
                onSeleccionar = onIconoChange
            )

            Text(
                text = "Nombre 💊",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                placeholder = { Text("Nombre del medicamento") },
                shape = campoShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueCrossPrimary,
                    unfocusedBorderColor = ClaySurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Dosis 🥄",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            OutlinedTextField(
                value = dosis,
                onValueChange = onDosisChange,
                placeholder = { Text("Dosis (ej. 1 tableta, 5ml)") },
                shape = campoShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlueCrossPrimary,
                    unfocusedBorderColor = ClaySurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarTimePicker = true },
                backgroundColor = ClaySurfaceVariant,
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hora de la toma ⏰",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = String.format("%02d:%02d", hora, minuto),
                        style = MaterialTheme.typography.titleMedium,
                        color = BlueCrossPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            ClayButton(
                text = "Guardar recordatorio",
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (mostrarTimePicker) {
        TimePickerDialog(
            horaInicial = hora,
            minutoInicial = minuto,
            onConfirm = { h, m ->
                onHoraChange(h, m)
                mostrarTimePicker = false
            },
            onDismiss = { mostrarTimePicker = false }
        )
    }
}

@Composable
fun MedicamentoItem(
    medicamento: MedicationEntity,
    onToggleActivo: (Boolean) -> Unit,
    onHoraChange: (Int, Int) -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarTimePicker by remember { mutableStateOf(false) }

    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ClaySurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = medicamento.icono, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicamento.nombre,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (medicamento.dosis.isNotBlank()) {
                        Text(
                            text = "${medicamento.dosis} • ${String.format("%02d:%02d", medicamento.hora, medicamento.minuto)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = medicamento.activo,
                    onCheckedChange = onToggleActivo,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = BlueCrossPrimary,
                        checkedTrackColor = ClaySurfaceVariant
                    )
                )

                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarTimePicker = true },
                backgroundColor = ClaySurfaceVariant,
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hora programada ⏰",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = String.format("%02d:%02d", medicamento.hora, medicamento.minuto),
                        style = MaterialTheme.typography.titleSmall,
                        color = BlueCrossPrimary
                    )
                }
            }
        }
    }

    if (mostrarTimePicker) {
        TimePickerDialog(
            horaInicial = medicamento.hora,
            minutoInicial = medicamento.minuto,
            onConfirm = { h, m ->
                onHoraChange(h, m)
                mostrarTimePicker = false
            },
            onDismiss = { mostrarTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    horaInicial: Int,
    minutoInicial: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = horaInicial,
        initialMinute = minutoInicial,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        text = { TimePicker(state = timePickerState) },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("Aceptar", color = BlueCrossPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}