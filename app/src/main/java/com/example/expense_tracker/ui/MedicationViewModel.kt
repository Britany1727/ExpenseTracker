package com.example.expense_tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expense_tracker.data.local.MedicationEntity
import com.example.expense_tracker.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(
    private val repository: MedicationRepository
) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _dosis = MutableStateFlow("")
    val dosis: StateFlow<String> = _dosis.asStateFlow()

    private val _horaSeleccionada = MutableStateFlow(8)
    val horaSeleccionada: StateFlow<Int> = _horaSeleccionada.asStateFlow()

    private val _minutoSeleccionado = MutableStateFlow(0)
    val minutoSeleccionado: StateFlow<Int> = _minutoSeleccionado.asStateFlow()

    private val _iconoSeleccionado = MutableStateFlow("💊")
    val iconoSeleccionado: StateFlow<String> = _iconoSeleccionado.asStateFlow()

    val iconosDisponibles = listOf("💊", "💉", "🧴", "🥄", "🍊", "🌿", "💧", "🩹")

    val medicamentos = repository.todosLosMedicamentos

    fun actualizarNombre(valor: String) { _nombre.value = valor }
    fun actualizarDosis(valor: String) { _dosis.value = valor }
    fun actualizarIcono(valor: String) { _iconoSeleccionado.value = valor }
    fun actualizarHoraSeleccionada(hora: Int, minuto: Int) {
        _horaSeleccionada.value = hora
        _minutoSeleccionado.value = minuto
    }

    fun guardarMedicamento(onGuardado: (MedicationEntity) -> Unit) {
        val nombreActual = _nombre.value.trim()
        if (nombreActual.isBlank()) return

        viewModelScope.launch {
            val nuevoMedicamento = MedicationEntity(
                nombre = nombreActual,
                dosis = _dosis.value.trim(),
                hora = _horaSeleccionada.value,
                minuto = _minutoSeleccionado.value,
                activo = true,
                icono = _iconoSeleccionado.value
            )
            val id = repository.agregar(nuevoMedicamento)

            _nombre.value = ""
            _dosis.value = ""
            _iconoSeleccionado.value = "💊"

            onGuardado(nuevoMedicamento.copy(id = id.toInt()))
        }
    }

    fun cambiarEstadoRecordatorio(
        medicamento: MedicationEntity,
        activo: Boolean,
        onCambiado: (MedicationEntity) -> Unit
    ) {
        viewModelScope.launch {
            val actualizado = medicamento.copy(activo = activo)
            repository.actualizar(actualizado)
            onCambiado(actualizado)
        }
    }

    fun actualizarHoraMedicamento(
        medicamento: MedicationEntity,
        hora: Int,
        minuto: Int,
        onActualizado: (MedicationEntity) -> Unit
    ) {
        viewModelScope.launch {
            val actualizado = medicamento.copy(hora = hora, minuto = minuto)
            repository.actualizar(actualizado)
            onActualizado(actualizado)
        }
    }

    fun eliminarMedicamento(medicamento: MedicationEntity, onEliminado: (MedicationEntity) -> Unit) {
        viewModelScope.launch {
            repository.eliminar(medicamento)
            onEliminado(medicamento)
        }
    }
}

class MedicationViewModelFactory(
    private val repository: MedicationRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
            return MedicationViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}