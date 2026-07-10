package com.example.expense_tracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val dosis: String,
    val hora: Int,
    val minuto: Int,
    val activo: Boolean = true,
    val notas: String = "",
    val icono: String = "💊"
)