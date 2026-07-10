package com.example.expense_tracker.data.repository

import com.example.expense_tracker.data.local.MedicationDao
import com.example.expense_tracker.data.local.MedicationEntity
import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val dao: MedicationDao) {

    val todosLosMedicamentos: Flow<List<MedicationEntity>> = dao.obtenerTodos()

    suspend fun agregar(medicamento: MedicationEntity): Long {
        return dao.insertar(medicamento)
    }

    suspend fun actualizar(medicamento: MedicationEntity) {
        dao.actualizar(medicamento)
    }

    suspend fun eliminar(medicamento: MedicationEntity) {
        dao.eliminar(medicamento)
    }

    suspend fun obtenerActivos(): List<MedicationEntity> {
        return dao.obtenerActivos()
    }
}