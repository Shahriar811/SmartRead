package com.example.smartread.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartread.history.StudySessionRecord
import com.example.smartread.timer.StudyTimerViewModel
import com.example.smartread.tips.StudyTipsData
import com.example.smartread.tips.TipCategory
import com.example.smartread.timer.TimerPhase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: StudyTimerViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val historySessions by viewModel.historySessions.collectAsState()
    var showHistorySheet by remember { mutableStateOf(false) }
    var showTipsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header with title and icon buttons (fixed sizes so they always fit)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Study & Reward Timer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(end = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TipsButton(onClick = { showTipsSheet = true })
                HistoryButton(onClick = { showHistorySheet = true })
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggle = onThemeToggle
                )
            }
        }

        // Settings card (when idle)
        if (state.canEditSettings) {
            SettingsCard(
                studyMinutes = state.studyMinutes,
                rewardMinutes = state.rewardMinutes,
                subject = state.subject,
                useSingleSubject = state.useSingleSubject,
                subjectSuggestions = viewModel.getSubjectSuggestions(),
                onStudyChange = viewModel::setStudyMinutes,
                onRewardChange = viewModel::setRewardMinutes,
                onSubjectChange = viewModel::setSubject,
                onUseSingleSubjectChange = viewModel::setUseSingleSubject
            )
        }

        // Countdown display (when idle, show upcoming study duration)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.phase != TimerPhase.Idle) {
                Text(
                    text = "📚 ${state.subject}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            CountdownSection(
                phase = state.phase,
                minutes = if (state.phase == TimerPhase.Idle) state.studyMinutes else state.displayMinutes,
                seconds = if (state.phase == TimerPhase.Idle) 0 else state.displaySeconds,
                cycleCount = state.cycleCount
            )
        }

        // Control buttons
        ControlButtons(
            phase = state.phase,
            isRunning = state.isRunning,
            onStart = viewModel::start,
            onPause = viewModel::pause,
            onReset = viewModel::reset
        )
    }

    if (showHistorySheet) {
        HistoryBottomSheet(
            sessions = historySessions,
            onDismiss = { showHistorySheet = false }
        )
    }
    if (showTipsSheet) {
        TipsBottomSheet(onDismiss = { showTipsSheet = false })
    }
}

@Composable
private fun TipsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Lightbulb,
            contentDescription = "Study & exam tips",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HistoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.History,
            contentDescription = "History",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryBottomSheet(
    sessions: List<StudySessionRecord>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "📋 Study History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (sessions.isEmpty()) {
                Text(
                    text = "No sessions yet. Complete a study block to see history here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sessions.forEach { record ->
                        HistoryItem(
                            subject = record.subject,
                            studyMinutes = record.studyMinutes,
                            rewardMinutes = record.rewardMinutes,
                            totalStudySeconds = record.totalStudySeconds,
                            date = Date(record.completedAt),
                            dateFormat = dateFormat
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipsBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "💡 Study & Exam Tips",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Use these tips to study better and perform well in exams.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))

            StudyTipsData.categories.forEach { category ->
                TipCategorySection(category = category)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TipCategorySection(
    category: TipCategory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${category.emoji} ${category.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            category.tips.forEachIndexed { index, tip ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(
    subject: String,
    studyMinutes: Int,
    rewardMinutes: Int,
    totalStudySeconds: Long,
    date: Date,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${totalStudySeconds / 60} min study · ${studyMinutes}m / ${rewardMinutes}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = dateFormat.format(date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ThemeToggleButton(
    isDarkTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .clickable(onClick = onToggle)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isDarkTheme) "🌙" else "☀️",
            fontSize = 20.sp
        )
    }
}

@Composable
private fun SettingsCard(
    studyMinutes: Int,
    rewardMinutes: Int,
    subject: String,
    useSingleSubject: Boolean,
    subjectSuggestions: List<String>,
    onStudyChange: (Int) -> Unit,
    onRewardChange: (Int) -> Unit,
    onSubjectChange: (String) -> Unit,
    onUseSingleSubjectChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "⏱️ Set Your Times",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Only one subject switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Only one subject (General)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = useSingleSubject,
                    onCheckedChange = onUseSingleSubjectChange
                )
            }

            // Subject selector (hidden or disabled when useSingleSubject)
            if (!useSingleSubject) {
                SubjectRow(
                    subject = subject,
                    suggestions = subjectSuggestions,
                    onSubjectChange = onSubjectChange
                )
            }

            TimeSetterRow(
                label = "📚 Study (min)",
                value = studyMinutes,
                range = 1..120,
                onValueChange = onStudyChange
            )
            TimeSetterRow(
                label = "🎉 Reward (min)",
                value = rewardMinutes,
                range = 1..60,
                onValueChange = onRewardChange
            )
        }
    }
}

@Composable
private fun SubjectRow(
    subject: String,
    suggestions: List<String>,
    onSubjectChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Show empty in the box when subject is "General" so typing replaces it instead of appending
    val displayValue = if (subject == "General") "" else subject
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "📖 Subject",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = displayValue,
            onValueChange = { newValue ->
                onSubjectChange(if (newValue.isBlank()) "General" else newValue.trim())
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("e.g. Math, Physics") },
            shape = RoundedCornerShape(12.dp)
        )
        if (suggestions.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.take(5).forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (subject == suggestion)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onSubjectChange(suggestion) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (subject == suggestion)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSetterRow(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { 
                    val newValue = (value - 1).coerceIn(range)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                enabled = value > range.first,
                modifier = Modifier
                    .weight(0.15f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "−",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedTextField(
                value = textValue,
                onValueChange = { newText ->
                    textValue = newText
                    val parsed = newText.toIntOrNull()
                    if (parsed != null && parsed in range) {
                        isError = false
                        onValueChange(parsed)
                    } else if (newText.isEmpty()) {
                        isError = false
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier
                    .weight(0.7f)
                    .height(56.dp),
                singleLine = true,
                isError = isError,
                shape = RoundedCornerShape(12.dp),
                supportingText = if (isError) {
                    { Text("Enter ${range.first}-${range.last}") }
                } else null,
                suffix = { 
                    Text(
                        "min", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                }
            )
            OutlinedButton(
                onClick = { 
                    val newValue = (value + 1).coerceIn(range)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                enabled = value < range.last,
                modifier = Modifier
                    .weight(0.15f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CountdownSection(
    phase: TimerPhase,
    minutes: Int,
    seconds: Int,
    cycleCount: Int,
    modifier: Modifier = Modifier
) {
    val (phaseLabel, containerColor) = when (phase) {
        TimerPhase.Idle -> "✨ Ready" to MaterialTheme.colorScheme.surfaceVariant
        TimerPhase.Study -> "📚 Study time" to MaterialTheme.colorScheme.primaryContainer
        TimerPhase.Reward -> "🎉 Reward time" to MaterialTheme.colorScheme.tertiaryContainer
    }
    val animatedColor by animateColorAsState(containerColor, label = "phaseColor")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (cycleCount > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Round $cycleCount",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = phaseLabel,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(animatedColor)
                .padding(horizontal = 48.dp, vertical = 32.dp)
        ) {
            Text(
                text = "%02d:%02d".format(minutes, seconds),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp
                ),
                color = when (phase) {
                    TimerPhase.Idle -> MaterialTheme.colorScheme.onSurfaceVariant
                    TimerPhase.Study -> MaterialTheme.colorScheme.onPrimaryContainer
                    TimerPhase.Reward -> MaterialTheme.colorScheme.onTertiaryContainer
                }
            )
        }
    }
}

@Composable
private fun ControlButtons(
    phase: TimerPhase,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        if (phase != TimerPhase.Idle) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    Icons.Default.Refresh, 
                    contentDescription = null, 
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Reset",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (isRunning) {
            Button(
                onClick = onPause,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    Icons.Default.Pause, 
                    contentDescription = null, 
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Pause",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow, 
                    contentDescription = null, 
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (phase == TimerPhase.Idle) "Start" else "Resume",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
