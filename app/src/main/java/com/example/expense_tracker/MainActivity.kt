package com.example.expense_tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expense_tracker.alarm.ReminderScheduler
import com.example.expense_tracker.data.local.AppDatabase
import com.example.expense_tracker.data.local.MedicationEntity
import com.example.expense_tracker.data.repository.MedicationRepository
import com.example.expense_tracker.ui.MedicationScreen
import com.example.expense_tracker.ui.MedicationViewModel
import com.example.expense_tracker.ui.MedicationViewModelFactory
import com.example.expense_tracker.ui.theme.MedRecordatorioTheme

class MainActivity : ComponentActivity() {

    private var medicamentoPendiente: MedicationEntity? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            medicamentoPendiente?.let { programarAlarma(it) }
        }
        medicamentoPendiente = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = MedicationRepository(database.medicationDao())
        val viewModelFactory = MedicationViewModelFactory(repository)

        setContent {
            MedRecordatorioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MedicationViewModel = viewModel(factory = viewModelFactory)

                    MedicationScreen(
                        viewModel = viewModel,
                        onMedicamentoGuardado = { medicamento -> solicitarProgramacion(medicamento) },
                        onRecordatorioToggle = { medicamento -> solicitarProgramacion(medicamento) },
                        onRecordatorioDesactivado = { medicamento ->
                            ReminderScheduler.cancelarRecordatorio(this, medicamento.id)
                        },
                        onHoraActualizada = { medicamento -> solicitarProgramacion(medicamento) },
                        onMedicamentoEliminado = { medicamento ->
                            ReminderScheduler.cancelarRecordatorio(this, medicamento.id)
                        }
                    )
                }
            }
        }
    }

    private fun solicitarProgramacion(medicamento: MedicationEntity) {
        if (!medicamento.activo) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> programarAlarma(medicamento)

                else -> {
                    medicamentoPendiente = medicamento
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            programarAlarma(medicamento)
        }
    }

    private fun programarAlarma(medicamento: MedicationEntity) {
        ReminderScheduler.programarRecordatorio(
            this,
            medicamento.id,
            medicamento.nombre,
            medicamento.dosis,
            medicamento.hora,
            medicamento.minuto
        )
    }
}