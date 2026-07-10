package com.example.expense_tracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medicamentos ORDER BY hora ASC, minuto ASC")
    fun obtenerTodos(): Flow<List<MedicationEntity>>

    // Usado por BootReceiver para reprogramar alarmas tras un reinicio
    @Query("SELECT * FROM medicamentos WHERE activo = 1")
    suspend fun obtenerActivos(): List<MedicationEntity>

    @Insert
    suspend fun insertar(medicamento: MedicationEntity): Long

    @Update
    suspend fun actualizar(medicamento: MedicationEntity)

    @Delete
    suspend fun eliminar(medicamento: MedicationEntity)
}