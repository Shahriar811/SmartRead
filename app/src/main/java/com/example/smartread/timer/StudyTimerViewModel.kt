package com.example.smartread.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartread.history.HistoryRepository
import com.example.smartread.history.StudySessionRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerState(
    val studyMinutes: Int = 25,
    val rewardMinutes: Int = 5,
    val subject: String = "General",
    val useSingleSubject: Boolean = false,
    val phase: TimerPhase = TimerPhase.Idle,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val cycleCount: Int = 0,
    val totalStudySecondsCompleted: Long = 0L
) {
    val displayMinutes: Int get() = remainingSeconds / 60
    val displaySeconds: Int get() = remainingSeconds % 60
    val canEditSettings: Boolean get() = phase == TimerPhase.Idle && !isRunning
}

class StudyTimerViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()
    val historySessions: StateFlow<List<StudySessionRecord>> = historyRepository.sessions

    private var tickJob: Job? = null
    private var studySecondsThisBlock: Long = 0L
    
    // Callbacks for notifications and sounds
    var onPhaseComplete: ((TimerPhase) -> Unit)? = null

    fun setStudyMinutes(minutes: Int) {
        if (minutes in 1..120) {
            _state.update {
                if (it.canEditSettings) it.copy(studyMinutes = minutes) else it
            }
        }
    }

    fun setRewardMinutes(minutes: Int) {
        if (minutes in 1..60) {
            _state.update {
                if (it.canEditSettings) it.copy(rewardMinutes = minutes) else it
            }
        }
    }

    fun setSubject(subject: String) {
        val trimmed = subject.trim().ifEmpty { "General" }
        _state.update {
            if (it.canEditSettings) it.copy(subject = trimmed) else it
        }
    }

    fun setUseSingleSubject(useSingle: Boolean) {
        _state.update {
            if (it.canEditSettings) it.copy(
                useSingleSubject = useSingle,
                subject = if (useSingle) "General" else it.subject
            ) else it
        }
    }

    fun start() {
        val current = _state.value
        if (current.phase == TimerPhase.Idle) {
            studySecondsThisBlock = 0L
            _state.update {
                it.copy(
                    phase = TimerPhase.Study,
                    remainingSeconds = it.studyMinutes * 60,
                    isRunning = true,
                    cycleCount = it.cycleCount + 1
                )
            }
            startTicker()
        } else {
            _state.update { it.copy(isRunning = true) }
            startTicker()
        }
    }

    fun pause() {
        _state.update { it.copy(isRunning = false) }
        tickJob?.cancel()
        tickJob = null
    }

    fun reset() {
        tickJob?.cancel()
        tickJob = null
        _state.update {
            TimerState(
                studyMinutes = it.studyMinutes,
                rewardMinutes = it.rewardMinutes,
                subject = it.subject,
                useSingleSubject = it.useSingleSubject
            )
        }
    }

    private fun startTicker() {
        tickJob?.cancel()
        tickJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000L)
                _state.update { s ->
                    if (!s.isRunning) return@update s
                    val next = s.remainingSeconds - 1
                    if (next <= 0) {
                        switchPhase(s)
                    } else {
                        s.copy(
                            remainingSeconds = next,
                            totalStudySecondsCompleted = if (s.phase == TimerPhase.Study) {
                                s.totalStudySecondsCompleted + 1
                            } else s.totalStudySecondsCompleted
                        )
                    }
                }
            }
        }
    }

    private fun switchPhase(current: TimerState): TimerState {
        val completedPhase = current.phase
        var newState = when (current.phase) {
            TimerPhase.Study -> {
                studySecondsThisBlock = current.studyMinutes * 60L
                current.copy(
                    phase = TimerPhase.Reward,
                    remainingSeconds = current.rewardMinutes * 60,
                    totalStudySecondsCompleted = current.totalStudySecondsCompleted + 1
                )
            }
            TimerPhase.Reward -> current.copy(
                phase = TimerPhase.Study,
                remainingSeconds = current.studyMinutes * 60,
                cycleCount = current.cycleCount + 1
            )
            TimerPhase.Idle -> current
        }
        
        // When study phase completes, save to history
        if (completedPhase == TimerPhase.Study) {
            val record = StudySessionRecord(
                id = System.currentTimeMillis(),
                subject = current.subject,
                studyMinutes = current.studyMinutes,
                rewardMinutes = current.rewardMinutes,
                totalStudySeconds = studySecondsThisBlock,
                completedAt = System.currentTimeMillis()
            )
            viewModelScope.launch { historyRepository.addSession(record) }
        }
        
        if (completedPhase != TimerPhase.Idle) {
            onPhaseComplete?.invoke(completedPhase)
        }
        
        return newState
    }
    
    override fun onCleared() {
        super.onCleared()
        tickJob?.cancel()
    }

    fun getSubjectSuggestions(): List<String> = historyRepository.getSubjectSuggestions()

    companion object {
        fun provideFactory(historyRepository: HistoryRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    StudyTimerViewModel(historyRepository) as T
            }
    }
}
