package com.example.expense_tracker.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderScheduler {

    const val EXTRA_MED_ID = "extra_medication_id"
    const val EXTRA_MED_NOMBRE = "extra_medication_nombre"
    const val EXTRA_MED_DOSIS = "extra_medication_dosis"
    const val EXTRA_MED_HORA = "extra_medication_hora"
    const val EXTRA_MED_MINUTO = "extra_medication_minuto"

    fun programarRecordatorio(
        context: Context,
        medicamentoId: Int,
        nombre: String,
        dosis: String,
        hora: Int,
        minuto: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_MED_ID, medicamentoId)
            putExtra(EXTRA_MED_NOMBRE, nombre)
            putExtra(EXTRA_MED_DOSIS, dosis)
            putExtra(EXTRA_MED_HORA, hora)
            putExtra(EXTRA_MED_MINUTO, minuto)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamentoId, // request code único por medicamento
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendario.timeInMillis <= System.currentTimeMillis()) {
            calendario.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendario.timeInMillis,
            pendingIntent
        )
    }

    fun cancelarRecordatorio(context: Context, medicamentoId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamentoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}