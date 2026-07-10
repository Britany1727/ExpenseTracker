package com.example.expense_tracker.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.expense_tracker.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Vuelve a programar todas las alarmas activas cuando el dispositivo se reinicia.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getInstance(context)
                val medicamentosActivos = database.medicationDao().obtenerActivos()
                medicamentosActivos.forEach { medicamento ->
                    ReminderScheduler.programarRecordatorio(
                        context,
                        medicamento.id,
                        medicamento.nombre,
                        medicamento.dosis,
                        medicamento.hora,
                        medicamento.minuto
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}