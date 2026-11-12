package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calculator.ScientificCalculatorView
import com.example.data.StudyForgeRepository
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSimulatorScreen(
    configuration: ExamConfiguration,
    repository: StudyForgeRepository,
    onFinishExam: () -> Unit
) {
    val context = LocalContext.current
    val allQuestions by repository.questions.collectAsState()

    // Filter questions based on configuration
    val examQuestions = remember(configuration, allQuestions) {
        val filtered = allQuestions.filter {
            (configuration.courseCode.isEmpty() || it.courseCode.contains(configuration.courseCode, ignoreCase = true)) &&
            (configuration.difficulty == null || it.difficulty == configuration.difficulty)
        }
        val pool = if (filtered.size >= configuration.questionCount) filtered else allQuestions
        pool.shuffled().take(configuration.questionCount)
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val userResponses = remember {
        mutableStateMapOf<String, UserQuestionResponse>().apply {
            examQuestions.forEach { q ->
                put(q.id, UserQuestionResponse(questionId = q.id))
            }
        }
    }

    // Timer state
    var secondsRemaining by remember { mutableLongStateOf((configuration.timeLimitMinutes * 60).toLong()) }
    var isSubmitted by remember { mutableStateOf(false) }
    var showSubmitConfirmDialog by remember { mutableStateOf(false) }
    var showExamCalculator by remember { mutableStateOf(false) }
    var showQuestionMatrixDialog by remember { mutableStateOf(false) }
    var examResult by remember { mutableStateOf<ExamResult?>(null) }

    // Section split: First half is Section A (Conceptual / MCQ), second half is Section B (Applied / Problems)
    val sectionASize = remember(examQuestions.size) { (examQuestions.size + 1) / 2 }
    val currentSection = if (currentIndex < sectionASize) "Section A — Conceptual & Knowledge" else "Section B — Analytical & Problem Solving"

    // Countdown effect
    LaunchedEffect(isSubmitted) {
        if (!isSubmitted) {
            while (secondsRemaining > 0) {
                delay(1000L)
                secondsRemaining--
            }
            if (secondsRemaining <= 0) {
                isSubmitted = true
            }
        }
    }

    // Calculate result when submitted
    LaunchedEffect(isSubmitted) {
        if (isSubmitted && examResult == null) {
            val total = examQuestions.size
            var correct = 0
            var incorrect = 0
            var unanswered = 0
            var totalMarks = 0
            var earnedMarks = 0
            val topicCorrectMap = mutableMapOf<String, Int>()
            val topicTotalMap = mutableMapOf<String, Int>()

            examQuestions.forEach { q ->
                totalMarks += q.marks
                val resp = userResponses[q.id]
                topicTotalMap[q.topic] = (topicTotalMap[q.topic] ?: 0) + 1

                if (resp == null || !resp.isAnswered) {
                    unanswered++
                } else if (resp.selectedOptionIndex == q.correctOptionIndex) {
                    correct++
                    earnedMarks += q.marks
                    topicCorrectMap[q.topic] = (topicCorrectMap[q.topic] ?: 0) + 1
                } else {
                    incorrect++
                }
            }

            val percentage = if (totalMarks > 0) (earnedMarks.toDouble() / totalMarks) * 100.0 else 0.0
            val timeTaken = (configuration.timeLimitMinutes * 60) - secondsRemaining

            val topicAccuracy = topicTotalMap.mapValues { (topic, count) ->
                val c = topicCorrectMap[topic] ?: 0
                (c.toDouble() / count) * 100.0
            }

            val weakTopics = topicAccuracy.filter { it.value < 65.0 }.keys.toList()
            val revisionPlan = weakTopics.map { "Revise fundamental formulas and complete 10 problems on $it" }

            val result = ExamResult(
                id = "res_${System.currentTimeMillis()}",
                examTitle = configuration.mode.title,
                courseCode = configuration.courseCode,
                totalQuestions = total,
                correctCount = correct,
                incorrectCount = incorrect,
                unansweredCount = unanswered,
                totalMarks = totalMarks,
                earnedMarks = earnedMarks,
                percentage = percentage,
                timeTakenSeconds = timeTaken,
                topicAccuracy = topicAccuracy,
                weakTopics = weakTopics,
                revisionPlan = if (revisionPlan.isNotEmpty()) revisionPlan else listOf("Great performance! Maintain mastery with regular spaced repetition.")
            )

            examResult = result
            repository.recordExamResult(result)
        }
    }

    BackHandler(enabled = !isSubmitted) {
        showSubmitConfirmDialog = true
    }

    if (isSubmitted && examResult != null) {
        ExamResultsView(
            result = examResult!!,
            questions = examQuestions,
            userResponses = userResponses,
            sectionASize = sectionASize,
            onRetakeExam = {
                userResponses.clear()
                examQuestions.forEach { q -> userResponses[q.id] = UserQuestionResponse(questionId = q.id) }
                currentIndex = 0
                secondsRemaining = (configuration.timeLimitMinutes * 60).toLong()
                isSubmitted = false
                examResult = null
            },
            onDone = onFinishExam
        )
        return
    }

    val currentQuestion = examQuestions.getOrNull(currentIndex)
    val currentResponse = currentQuestion?.let { userResponses[it.id] }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = configuration.mode.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$currentSection • Question ${currentIndex + 1} of ${examQuestions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Question Palette Matrix Button
                    IconButton(
                        onClick = { showQuestionMatrixDialog = true },
                        modifier = Modifier.testTag("exam_matrix_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Question Matrix",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Calculator action button in exam toolbar
                    if (configuration.calculatorAllowed) {
                        IconButton(
                            onClick = { showExamCalculator = true },
                            modifier = Modifier
                                .testTag("exam_calculator_btn")
                                .padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Exam Calculator",
                                tint = BrightBlue
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Calculator is strictly prohibited for this examination session.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .testTag("exam_calculator_btn")
                                .padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Calculator Prohibited",
                                tint = Color.Gray.copy(alpha = 0.4f)
                            )
                        }
                    }

                    // Visible Countdown Timer
                    val minutes = secondsRemaining / 60
                    val seconds = secondsRemaining % 60
                    val timerColor = when {
                        secondsRemaining < 120 -> ErrorRed
                        secondsRemaining < 300 -> WarningAmber
                        else -> SuccessGreen
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = timerColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, timerColor),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = timerColor
                            )
                        }
                    }

                    // Submit button
                    Button(
                        onClick = { showSubmitConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("submit_exam_top_btn")
                    ) {
                        Text("Submit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    // Flag for review button
                    IconButton(
                        onClick = {
                            currentQuestion?.let { q ->
                                val old = userResponses[q.id] ?: UserQuestionResponse(q.id)
                                userResponses[q.id] = old.copy(isFlagged = !old.isFlagged)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentResponse?.isFlagged == true) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                            contentDescription = "Flag for Review",
                            tint = if (currentResponse?.isFlagged == true) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (currentIndex < examQuestions.size - 1) {
                        Button(
                            onClick = { currentIndex++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp))
                        }
                    } else {
                        Button(
                            onClick = { showSubmitConfirmDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Finish Exam")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Section Switcher & Question Palette
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentIndex < sectionASize) "Section A: Conceptual MCQs" else "Section B: Applied Problems",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                    Text(
                        text = "${currentIndex + 1} of ${examQuestions.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(examQuestions.size) { idx ->
                        val q = examQuestions[idx]
                        val resp = userResponses[q.id]
                        val isCurrent = idx == currentIndex
                        val isAnswered = resp?.isAnswered == true
                        val isFlagged = resp?.isFlagged == true

                        val pillBg = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isFlagged -> WarningAmber.copy(alpha = 0.2f)
                            isAnswered -> SuccessGreen.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        val pillBorder = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isFlagged -> WarningAmber
                            isAnswered -> SuccessGreen
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        }
                        val pillText = when {
                            isCurrent -> Color.White
                            isFlagged -> WarningAmber
                            isAnswered -> SuccessGreen
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(pillBg)
                                .border(1.5.dp, pillBorder, CircleShape)
                                .clickable { currentIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = pillText
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Current Question Body
            currentQuestion?.let { question ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question Metadata Badges
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = question.origin.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "${question.difficulty.label} • ${question.marks} Marks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Question Prompt
                    item {
                        Text(
                            text = question.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp
                        )
                    }

                    // Code Snippet if applicable
                    if (question.codeSnippet != null) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = question.codeSnippet,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }

                    // Options
                    if (question.type == QuestionType.MULTIPLE_CHOICE || question.options.isNotEmpty()) {
                        items(question.options.size) { optIdx ->
                            val optionText = question.options[optIdx]
                            val isSelected = currentResponse?.selectedOptionIndex == optIdx

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        userResponses[question.id] = (currentResponse ?: UserQuestionResponse(question.id)).copy(
                                            selectedOptionIndex = optIdx,
                                            isAnswered = true
                                        )
                                    }
                                    .testTag("exam_option_${optIdx}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryIndigo.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val letter = ('A' + optIdx).toString()
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    } else {
                        // Numerical / Text Input
                        item {
                            var textVal by remember(question.id) {
                                mutableStateOf(currentResponse?.enteredTextAnswer ?: "")
                            }
                            OutlinedTextField(
                                value = textVal,
                                onValueChange = {
                                    textVal = it
                                    userResponses[question.id] = (currentResponse ?: UserQuestionResponse(question.id)).copy(
                                        enteredTextAnswer = it,
                                        isAnswered = it.isNotBlank()
                                    )
                                },
                                label = { Text("Enter your calculated numerical answer") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Source Reference note
                    if (question.sourceReference != null) {
                        item {
                            Text(
                                text = "Curriculum Reference: ${question.sourceReference}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Question Matrix Dialog
    if (showQuestionMatrixDialog) {
        AlertDialog(
            onDismissRequest = { showQuestionMatrixDialog = false },
            title = {
                Text("Examination Question Matrix", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tap any question number to navigate directly:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        items(examQuestions.size) { idx ->
                            val q = examQuestions[idx]
                            val resp = userResponses[q.id]
                            val isCurrent = idx == currentIndex
                            val isAnswered = resp?.isAnswered == true
                            val isFlagged = resp?.isFlagged == true

                            val cellBg = when {
                                isCurrent -> PrimaryIndigo
                                isFlagged -> WarningAmber.copy(alpha = 0.2f)
                                isAnswered -> SuccessGreen.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val cellText = when {
                                isCurrent -> Color.White
                                isFlagged -> WarningAmber
                                isAnswered -> SuccessGreen
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(cellBg)
                                    .border(1.dp, if (isCurrent) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentIndex = idx
                                        showQuestionMatrixDialog = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "${idx + 1}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = cellText)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showQuestionMatrixDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Submission Confirmation Dialog
    if (showSubmitConfirmDialog) {
        val answeredCount = userResponses.values.count { it.isAnswered }
        val unansweredCount = examQuestions.size - answeredCount
        val flaggedCount = userResponses.values.count { it.isFlagged }

        AlertDialog(
            onDismissRequest = { showSubmitConfirmDialog = false },
            title = { Text("Submit Examination Session?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you ready to submit your exam responses?")
                    Text("• Answered: $answeredCount")
                    Text("• Unanswered: $unansweredCount", color = if (unansweredCount > 0) ErrorRed else MaterialTheme.colorScheme.onSurface)
                    if (flaggedCount > 0) {
                        Text("• Flagged for review: $flaggedCount", color = WarningAmber)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirmDialog = false
                        isSubmitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Yes, Submit Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirmDialog = false }) {
                    Text("Return to Exam")
                }
            }
        )
    }

    // Floating / Movable Exam Calculator Overlay
    if (showExamCalculator && configuration.calculatorAllowed) {
        Dialog(
            onDismissRequest = { showExamCalculator = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp)
                    .testTag("floating_exam_calculator_dialog"),
                contentAlignment = Alignment.Center
            ) {
                ScientificCalculatorView(
                    isExamMode = true,
                    onClose = { showExamCalculator = false }
                )
            }
        }
    }
}

@Composable
private fun ExamResultsView(
    result: ExamResult,
    questions: List<Question>,
    userResponses: Map<String, UserQuestionResponse>,
    sectionASize: Int,
    onRetakeExam: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("exam_results_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Examination Performance Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Percentage Badge
                    val scoreColor = if (result.percentage >= 70) SuccessGreen else WarningAmber
                    Text(
                        text = "${result.percentage.toInt()}%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )

                    Text(
                        text = "${result.earnedMarks} of ${result.totalMarks} Marks Earned",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStatItem(label = "Correct", value = "${result.correctCount}", color = SuccessGreen)
                        ResultStatItem(label = "Incorrect", value = "${result.incorrectCount}", color = ErrorRed)
                        ResultStatItem(label = "Unanswered", value = "${result.unansweredCount}", color = Color.Gray)
                        ResultStatItem(label = "Time Taken", value = "${result.timeTakenSeconds / 60}m", color = BrightBlue)
                    }

                    // Section breakdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryIndigo.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Section A (MCQ)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryIndigo)
                                Text("$sectionASize Questions", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentCyan.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Section B (Applied)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentCyanDark)
                                Text("${questions.size - sectionASize} Problems", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Action Row: Download PDF & Retake
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Performance report PDF exported successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download PDF", fontSize = 12.sp)
                }

                Button(
                    onClick = onRetakeExam,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Exam", fontSize = 12.sp)
                }
            }
        }

        // Topic Accuracy Breakdown
        if (result.topicAccuracy.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Accuracy by Curriculum Topic",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        result.topicAccuracy.forEach { (topic, acc) ->
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = topic, style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = "${acc.toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (acc >= 70) SuccessGreen else WarningAmber
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { (acc / 100.0).toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (acc >= 70) SuccessGreen else WarningAmber
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personalized Revision Recommendations
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryIndigo.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryIndigo)
                        Text(
                            text = "Personalized Revision Plan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                    }
                    result.revisionPlan.forEach { planItem ->
                        Text(
                            text = "• $planItem",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Comprehensive Question-by-Question Review Header
        item {
            Text(
                text = "Detailed Examination Review & Explanations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Review Items
        items(questions.size) { idx ->
            val q = questions[idx]
            val resp = userResponses[q.id]
            val isCorrect = resp?.selectedOptionIndex == q.correctOptionIndex

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isCorrect) SuccessGreen.copy(alpha = 0.5f) else ErrorRed.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${idx + 1} (${q.topic})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCorrect) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isCorrect) "✓ Correct" else "✗ Incorrect",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) SuccessGreen else ErrorRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = q.prompt, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                    if (q.options.isNotEmpty()) {
                        val yourAnswer = if (resp?.selectedOptionIndex != null && resp.selectedOptionIndex in q.options.indices) {
                            q.options[resp.selectedOptionIndex]
                        } else "Unanswered"
                        Text(text = "Your Answer: $yourAnswer", style = MaterialTheme.typography.bodySmall, color = if (isCorrect) SuccessGreen else ErrorRed)
                        Text(text = "Correct Answer: ${q.correctAnswer}", style = MaterialTheme.typography.bodySmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }

                    // Explanation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Harvard-Style Step-by-Step Explanation:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
            ) {
                Text("Return to Dashboard", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ResultStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
