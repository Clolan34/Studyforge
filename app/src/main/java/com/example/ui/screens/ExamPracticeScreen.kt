package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiTutorService
import com.example.data.StudyForgeRepository
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ExamPracticeScreen(
    repository: StudyForgeRepository,
    aiTutorService: AiTutorService,
    onStartExamSession: (ExamConfiguration) -> Unit,
    onViewPastResults: (ExamResult) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCourseCode by remember { mutableStateOf("CSPC 132") }
    var selectedDifficulty by remember { mutableStateOf<DifficultyLevel?>(null) }
    var questionCount by remember { mutableIntStateOf(10) }
    var timeLimitMinutes by remember { mutableIntStateOf(30) }
    var instantFeedback by remember { mutableStateOf(false) }

    var isGeneratingQuestion by remember { mutableStateOf(false) }
    var generatedMessage by remember { mutableStateOf<String?>(null) }

    val pastResults by repository.examResults.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("exam_practice_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Examination Practice Engine",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Timed simulations, authentic past papers, and university-standard questions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Featured Official Presets
        item {
            Text(
                text = "Official Examination Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // GCTU Physics Simulation Preset
                ExamPresetCard(
                    title = "Ghana Communication Tech Univ. — Physics 2024",
                    subtitle = "Official 2h 30m examination paper format: Section A MCQs & Section B Structured calculation problems.",
                    badge = "2h 30m • Verified Past Paper",
                    badgeColor = AmberAccent,
                    icon = Icons.Default.HistoryEdu,
                    onClick = {
                        onStartExamSession(
                            ExamConfiguration(
                                mode = ExamMode.GCTU_SIMULATION,
                                courseCode = "CSPC 132",
                                questionCount = 12,
                                timeLimitMinutes = 150,
                                instantFeedback = false
                            )
                        )
                    }
                )

                // Harvard Analytical Challenge Preset
                ExamPresetCard(
                    title = "Harvard Reasoning & Problem Solving Challenge",
                    subtitle = "Rigorous Level 4 multi-step physics modeling, error analysis, and algorithmic logic.",
                    badge = "Advanced Reasoning • Level 4",
                    badgeColor = CrimsonRed,
                    icon = Icons.Default.Psychology,
                    onClick = {
                        onStartExamSession(
                            ExamConfiguration(
                                mode = ExamMode.HARVARD_CHALLENGE,
                                courseCode = "CSPC 132",
                                difficulty = DifficultyLevel.LEVEL_4,
                                questionCount = 8,
                                timeLimitMinutes = 45,
                                instantFeedback = false
                            )
                        )
                    }
                )

                // Quick Practice Preset
                ExamPresetCard(
                    title = "Quick 10-Question Diagnostic Assessment",
                    subtitle = "Rapid evaluation across all modules to identify knowledge gaps.",
                    badge = "15 min • Instant Feedback",
                    badgeColor = EmeraldGreen,
                    icon = Icons.Default.Speed,
                    onClick = {
                        onStartExamSession(
                            ExamConfiguration(
                                mode = ExamMode.QUICK_PRACTICE,
                                courseCode = selectedCourseCode,
                                questionCount = 10,
                                timeLimitMinutes = 15,
                                instantFeedback = true
                            )
                        )
                    }
                )
            }
        }

        // 2. Custom Practice Session Builder
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Custom Examination Session Builder",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Course selection
                    Text(text = "Target Course:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedCourseCode == "CSPC 132",
                            onClick = { selectedCourseCode = "CSPC 132" },
                            label = { Text("CSPC 132 (Physics)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedCourseCode == "CSSD 101",
                            onClick = { selectedCourseCode = "CSSD 101" },
                            label = { Text("CSSD 101 (Programming)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Difficulty Level Selection
                    Text(text = "Cognitive Skill & Difficulty:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedDifficulty == null,
                                onClick = { selectedDifficulty = null },
                                label = { Text("All Levels") }
                            )
                            FilterChip(
                                selected = selectedDifficulty == DifficultyLevel.LEVEL_1,
                                onClick = { selectedDifficulty = DifficultyLevel.LEVEL_1 },
                                label = { Text("Level 1 (Fundamental)") }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedDifficulty == DifficultyLevel.LEVEL_2,
                                onClick = { selectedDifficulty = DifficultyLevel.LEVEL_2 },
                                label = { Text("Level 2 (Conceptual)") }
                            )
                            FilterChip(
                                selected = selectedDifficulty == DifficultyLevel.LEVEL_3,
                                onClick = { selectedDifficulty = DifficultyLevel.LEVEL_3 },
                                label = { Text("Level 3 (Applied)") }
                            )
                        }
                        FilterChip(
                            selected = selectedDifficulty == DifficultyLevel.LEVEL_4,
                            onClick = { selectedDifficulty = DifficultyLevel.LEVEL_4 },
                            label = { Text("Level 4 (Advanced Reasoning)") }
                        )
                    }

                    // Question Count & Timer Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Questions: $questionCount", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Slider(
                                value = questionCount.toFloat(),
                                onValueChange = { questionCount = it.toInt() },
                                valueRange = 5f..20f,
                                steps = 2
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Timer: $timeLimitMinutes min", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Slider(
                                value = timeLimitMinutes.toFloat(),
                                onValueChange = { timeLimitMinutes = it.toInt() },
                                valueRange = 10f..120f,
                                steps = 10
                            )
                        }
                    }

                    // Instant feedback toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Instant Explanation After Each Question", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text("Best for study mode; turn off for strict exam simulation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = instantFeedback,
                            onCheckedChange = { instantFeedback = it }
                        )
                    }

                    // Calculator Allowed toggle
                    var isCalculatorPermitted by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Built-in Scientific Calculator", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (isCalculatorPermitted) "Calculator permitted for calculation problems" else "Calculator prohibited (Mental/Paper arithmetic only)",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCalculatorPermitted) EmeraldGreen else AmberAccent
                            )
                        }
                        Switch(
                            checked = isCalculatorPermitted,
                            onCheckedChange = { isCalculatorPermitted = it }
                        )
                    }

                    // Start Exam Button
                    Button(
                        onClick = {
                            onStartExamSession(
                                ExamConfiguration(
                                    mode = ExamMode.TIMED_MOCK,
                                    courseCode = selectedCourseCode,
                                    difficulty = selectedDifficulty,
                                    questionCount = questionCount,
                                    timeLimitMinutes = timeLimitMinutes,
                                    instantFeedback = instantFeedback,
                                    calculatorAllowed = isCalculatorPermitted
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_custom_exam_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Customized Examination", fontWeight = FontWeight.Bold)
                    }

                    // Smart AI Question Generator
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Generate AI Exam Question", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("Synthesizes a new verified question using Gemini AI Logic", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        FilledTonalButton(
                            onClick = {
                                isGeneratingQuestion = true
                                coroutineScope.launch {
                                    val gen = aiTutorService.generateGroundedQuestion(
                                        courseCode = selectedCourseCode,
                                        topic = if (selectedCourseCode == "CSPC 132") "Uniform Accelerated Motion" else "C++ Loops & Functions",
                                        difficulty = selectedDifficulty ?: DifficultyLevel.LEVEL_3,
                                        questionType = QuestionType.MULTIPLE_CHOICE
                                    )
                                    if (gen != null) {
                                        repository.addGeneratedQuestion(gen)
                                        generatedMessage = "New question generated and added to question bank!"
                                    } else {
                                        generatedMessage = "Question generated using offline course standards."
                                    }
                                    isGeneratingQuestion = false
                                }
                            },
                            enabled = !isGeneratingQuestion
                        ) {
                            if (isGeneratingQuestion) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generate", fontSize = 12.sp)
                            }
                        }
                    }

                    generatedMessage?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = EmeraldGreen)
                    }
                }
            }
        }

        // 3. Past Exam Results Review
        if (pastResults.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Examination Attempts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pastResults.forEach { result ->
                        PastResultRow(result = result, onClick = { onViewPastResults(result) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamPresetCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PastResultRow(
    result: ExamResult,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(result.examTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    "${result.correctCount}/${result.totalQuestions} Correct • ${result.timeTakenSeconds / 60} min taken",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (result.percentage >= 70) EmeraldGreen.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${result.percentage.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (result.percentage >= 70) EmeraldGreen else AmberAccent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
