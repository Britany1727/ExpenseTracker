package com.example.expense_tracker.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.expense_tracker.MainActivity
import com.example.expense_tracker.R

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoId = intent.getIntExtra(ReminderScheduler.EXTRA_MED_ID, -1)
        val nombre = intent.getStringExtra(ReminderScheduler.EXTRA_MED_NOMBRE) ?: "Medicamento"
        val dosis = intent.getStringExtra(ReminderScheduler.EXTRA_MED_DOSIS) ?: ""
        val hora = intent.getIntExtra(ReminderScheduler.EXTRA_MED_HORA, 0)
        val minuto = intent.getIntExtra(ReminderScheduler.EXTRA_MED_MINUTO, 0)

        mostrarNotificacion(context, medicamentoId, nombre, dosis)

        // Reprogramar la misma hora para el día siguiente
        if (medicamentoId != -1) {
            ReminderScheduler.programarRecordatorio(context, medicamentoId, nombre, dosis, hora, minuto)
        }
    }

    private fun mostrarNotificacion(context: Context, medicamentoId: Int, nombre: String, dosis: String) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val canal = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos para tomar tus medicamentos a la hora indicada"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500)
            setSound(soundUri, audioAttributes)
        }
        notificationManager.createNotificationChannel(canal)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            medicamentoId,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val texto = if (dosis.isNotBlank()) "Dosis: $dosis" else "Es hora de tu medicamento"

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Hora de tomar: $nombre")
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // ID único por medicamento para que no se sobrescriban notificaciones distintas
        notificationManager.notify(NOTIFICATION_ID_BASE + medicamentoId, notificacion)
    }

    companion object {
        const val CHANNEL_ID = "medication_reminder_channel"
        private const val NOTIFICATION_ID_BASE = 2000
    }
}