package com.example.smartread

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartread.history.HistoryRepository
import com.example.smartread.timer.StudyTimerViewModel
import com.example.smartread.timer.TimerPhase
import com.example.smartread.ui.TimerScreen
import com.example.smartread.ui.theme.SmartReadTheme
import com.example.smartread.utils.AlarmSoundHelper
import com.example.smartread.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    private var alarmSoundHelper: AlarmSoundHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)
        
        enableEdgeToEdge()
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }
            
            SmartReadTheme(darkTheme = isDarkTheme) {
                TimerScreenWithNotifications(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alarmSoundHelper?.release()
    }
}

@Composable
private fun TimerScreenWithNotifications(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyRepository = remember { HistoryRepository(context.applicationContext) }
    val viewModel: StudyTimerViewModel = viewModel(
        factory = StudyTimerViewModel.provideFactory(historyRepository)
    )
    val state by viewModel.state.collectAsState()
    
    // Initialize alarm sound helper
    val alarmSoundHelper = remember {
        AlarmSoundHelper(context)
    }
    
    // Request notification permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Set up callback for phase completion
    LaunchedEffect(viewModel) {
        viewModel.onPhaseComplete = { phase ->
            // Show notification
            NotificationHelper.showTimerCompleteNotification(
                context = context,
                phase = phase
            )
            // Play alarm sound for 10 seconds
            alarmSoundHelper.playAlarm(durationSeconds = 10)
        }
    }
    
    // Stop alarm when timer is reset (goes back to Idle)
    LaunchedEffect(state.phase) {
        if (state.phase == TimerPhase.Idle) {
            alarmSoundHelper.stopAlarm()
        }
    }

    Scaffold(modifier = modifier) { innerPadding ->
        TimerScreen(
            viewModel = viewModel,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}