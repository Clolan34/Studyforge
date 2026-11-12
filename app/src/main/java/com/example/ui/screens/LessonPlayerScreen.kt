package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.FormulaItem
import com.example.model.Lesson
import com.example.model.WorkedExample
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonPlayerScreen(
    lessonId: String,
    repository: StudyForgeRepository,
    onBack: () -> Unit,
    onNavigateToTutorWithPrompt: (String) -> Unit
) {
    val context = LocalContext.current
    var currentLessonId by remember { mutableStateOf(lessonId) }
    val lesson = remember(currentLessonId) { repository.getLesson(currentLessonId) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var isSavedToNotes by remember { mutableStateOf(false) }

    var showLessonCalculator by remember { mutableStateOf(false) }
    var calcPresetExpression by remember { mutableStateOf("") }

    var showOutlineDialog by remember { mutableStateOf(false) }
    var showAssistantDialog by remember { mutableStateOf(false) }
    var personalNotesText by remember { mutableStateOf("") }

    if (lesson == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lesson not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${lesson.courseCode} • ${lesson.moduleTitle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("lesson_player_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Course Outline Drawer Button
                    IconButton(
                        onClick = { showOutlineDialog = true },
                        modifier = Modifier.testTag("lesson_outline_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = "Course Outline",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Download / Save Lesson
                    IconButton(
                        onClick = {
                            isSavedToNotes = !isSavedToNotes
                            Toast.makeText(
                                context,
                                if (isSavedToNotes) "Lesson saved for offline exam revision" else "Removed from saved notes",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.testTag("save_to_notes_btn")
                    ) {
                        Icon(
                            imageVector = if (isSavedToNotes) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save to Notes",
                            tint = if (isSavedToNotes) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Socratic AI Tutor Panel
                    IconButton(
                        onClick = { showAssistantDialog = true },
                        modifier = Modifier.testTag("lesson_ai_assistant_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Learning Assistant",
                            tint = PrimaryIndigo
                        )
                    }

                    // Scientific Calculator Button
                    IconButton(
                        onClick = {
                            calcPresetExpression = ""
                            showLessonCalculator = true
                        },
                        modifier = Modifier.testTag("lesson_calculator_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Scientific Calculator",
                            tint = BrightBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
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
                        onClick = { showOutlineDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Course Outline")
                    }

                    Button(
                        onClick = {
                            selectedTab = 4 // jump to practice question
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Text("Verify Understanding")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp))
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
            // Lesson Tabs: Theory, Formulas, Worked Examples, Code, Quiz
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Theory & Intuition") })
                if (lesson.formulas.isNotEmpty()) {
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Formulas (${lesson.formulas.size})") })
                }
                if (lesson.workedExamples.isNotEmpty()) {
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Worked Solutions") })
                }
                if (lesson.codeSnippet != null) {
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Code & Implementation") })
                }
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Practice Check") })
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Socratic Quick Actions Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { onNavigateToTutorWithPrompt("Explain '${lesson.title}' in simple terms with an analogy.") },
                            label = { Text("Explain Simpler", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                        AssistChip(
                            onClick = { onNavigateToTutorWithPrompt("Give me a challenging real-world university exam example for '${lesson.title}'.") },
                            label = { Text("Give Example", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                        AssistChip(
                            onClick = { onNavigateToTutorWithPrompt("Test my understanding of '${lesson.title}' with a Socratic question.") },
                            label = { Text("Ask AI Tutor", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> {
                        // Objectives Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Learning Objectives:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    lesson.objectives.forEach { obj ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text("✓", color = SuccessGreen, fontWeight = FontWeight.Bold)
                                            Text(text = obj, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }

                        // Detailed Academic Explanation
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Academic Concept & Intuition",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lesson.explanationText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 22.sp
                                    )

                                    if (lesson.sourceReference != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = BrightBlue, modifier = Modifier.size(16.dp))
                                                Text(
                                                    text = "Curriculum Reference: ${lesson.sourceReference}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = BrightBlue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Key Takeaways
                        if (lesson.keyTakeaways.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.35f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Key Takeaways for University Exams:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                        lesson.keyTakeaways.forEach { takeaway ->
                                            Text(
                                                text = "• $takeaway",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Formulas Tab
                        items(lesson.formulas.size) { index ->
                            FormulaCard(
                                formula = lesson.formulas[index],
                                onSendToCalculator = {
                                    calcPresetExpression = it
                                    showLessonCalculator = true
                                }
                            )
                        }
                    }

                    2 -> {
                        // Worked Examples Tab
                        items(lesson.workedExamples.size) { index ->
                            WorkedExampleCard(
                                example = lesson.workedExamples[index],
                                index = index + 1,
                                onSendToCalculator = {
                                    calcPresetExpression = it
                                    showLessonCalculator = true
                                }
                            )
                        }
                    }

                    3 -> {
                        // Code Snippet Tab
                        item {
                            lesson.codeSnippet?.let { code ->
                                CodeBlockCard(
                                    code = code,
                                    language = lesson.codeLanguage ?: "cpp"
                                )
                            }
                        }
                    }

                    4 -> {
                        // Verify Understanding Check Question Tab
                        item {
                            VerificationQuestionCard(
                                question = lesson.checkQuestion,
                                selectedIndex = selectedOptionIndex,
                                hasAnswered = hasAnswered,
                                onSelectOption = {
                                    selectedOptionIndex = it
                                    hasAnswered = true
                                },
                                onReset = {
                                    selectedOptionIndex = null
                                    hasAnswered = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // In-Lesson Scientific Calculator Dialog
    if (showLessonCalculator) {
        Dialog(
            onDismissRequest = { showLessonCalculator = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp)
                    .testTag("lesson_calculator_dialog"),
                contentAlignment = Alignment.Center
            ) {
                ScientificCalculatorView(
                    initialExpression = calcPresetExpression,
                    onClose = { showLessonCalculator = false }
                )
            }
        }
    }

    // Course Outline Modal Dialog
    if (showOutlineDialog) {
        val courseModules = remember(lesson.courseCode) {
            val courseId = if (lesson.courseCode.contains("132")) "course_physics" else "course_programming"
            repository.getModulesForCourse(courseId)
        }
        var searchOutline by remember { mutableStateOf("") }
        val filteredModules = remember(searchOutline, courseModules) {
            if (searchOutline.isBlank()) courseModules
            else courseModules.filter { it.title.contains(searchOutline, ignoreCase = true) || "Module ${it.number}".contains(searchOutline, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = { showOutlineDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = PrimaryIndigo)
                    Text("Course Syllabus Outline", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchOutline,
                        onValueChange = { searchOutline = it },
                        placeholder = { Text("Filter chapters and topics...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredModules) { mod ->
                            val isCurrent = mod.id == lesson.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) PrimaryIndigo.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCurrent) PrimaryIndigo else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentLessonId = mod.id
                                        showOutlineDialog = false
                                        selectedTab = 0
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Module ${mod.number}", style = MaterialTheme.typography.labelSmall, color = PrimaryIndigo, fontWeight = FontWeight.Bold)
                                        Text(text = mod.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                    }
                                    if (isCurrent) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = PrimaryIndigo
                                        ) {
                                            Text("Current", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showOutlineDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Contextual Learning Assistant Modal
    if (showAssistantDialog) {
        AlertDialog(
            onDismissRequest = { showAssistantDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryIndigo)
                    Text("Socratic Learning Assistant", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ask a contextual question or request an analogy for '${lesson.title}':",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            showAssistantDialog = false
                            onNavigateToTutorWithPrompt("Explain '${lesson.title}' using an everyday intuition analogy.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Explain with Real-World Analogy", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            showAssistantDialog = false
                            onNavigateToTutorWithPrompt("Give me a challenging university exam question testing '${lesson.title}'.")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Challenge Me with Exam Question", fontSize = 12.sp)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Text(
                        text = "Personal Study Notes:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = personalNotesText,
                        onValueChange = { personalNotesText = it },
                        placeholder = { Text("Write personal insights or memory hooks...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showAssistantDialog = false
                    if (personalNotesText.isNotBlank()) {
                        Toast.makeText(context, "Note recorded for this lesson", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Save & Close")
                }
            }
        )
    }
}

@Composable
private fun FormulaCard(
    formula: FormulaItem,
    onSendToCalculator: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = formula.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BrightBlue
            )

            // Equation display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formula.equation,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = WarningAmber
                )
            }

            Text(
                text = formula.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (formula.variables.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Text(
                    text = "Variable Definitions:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                formula.variables.forEach { (variable, meaning) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = variable,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = PrimaryIndigo
                        )
                        Text(
                            text = meaning,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = { onSendToCalculator?.invoke(formula.equation) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send to Calculator", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WorkedExampleCard(
    example: WorkedExample,
    index: Int,
    onSendToCalculator: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PrimaryIndigo.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Worked Analytical Solution #$index",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = example.problem,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Given Data
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Given Parameters: ${example.given}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }

            // Steps
            Text(
                text = "Analytical Solution Steps:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            example.steps.forEach { step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.ArrowRight, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(16.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Final Answer Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Final Calculated Result:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = example.finalAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Key Insight
            Text(
                text = "💡 Exam Key Insight: ${example.keyInsight}",
                style = MaterialTheme.typography.labelSmall,
                color = WarningAmber
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = { onSendToCalculator?.invoke("") },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Calculate Solution", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CodeBlockCard(code: String, language: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
                Text(
                    text = "Syntax-Checked",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFE2E8F0),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun VerificationQuestionCard(
    question: com.example.model.CheckQuestion,
    selectedIndex: Int?,
    hasAnswered: Boolean,
    onSelectOption: (Int) -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = WarningAmber.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Verify Your Understanding Before Advancing",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = WarningAmber,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = question.prompt,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                question.options.forEachIndexed { index, optionText ->
                    val isSelected = selectedIndex == index
                    val isCorrect = index == question.correctIndex

                    val borderColor = when {
                        !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        isCorrect -> SuccessGreen
                        isSelected -> ErrorRed
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    }

                    val containerColor = when {
                        !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        isCorrect -> SuccessGreen.copy(alpha = 0.15f)
                        isSelected -> ErrorRed.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !hasAnswered) { onSelectOption(index) },
                        shape = RoundedCornerShape(12.dp),
                        color = containerColor,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val letter = ('A' + index).toString()
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = letter,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (hasAnswered) {
                                if (isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = SuccessGreen)
                                } else if (isSelected) {
                                    Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = ErrorRed)
                                }
                            }
                        }
                    }
                }
            }

            // Explanation & Result
            if (hasAnswered) {
                val isCorrect = selectedIndex == question.correctIndex
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCorrect) SuccessGreen.copy(alpha = 0.1f) else WarningAmber.copy(alpha = 0.1f))
                        .border(1.dp, if (isCorrect) SuccessGreen else WarningAmber, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isCorrect) "✓ Conceptual Understanding Confirmed!" else "✗ Examination Misconception Identified",
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) SuccessGreen else WarningAmber
                        )
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onReset) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}
