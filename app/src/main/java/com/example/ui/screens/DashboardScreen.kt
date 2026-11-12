package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyForgeRepository
import com.example.model.Course
import com.example.ui.theme.*

data class PriorityTask(
    val id: String,
    val title: String,
    val tag: String,
    val courseCode: String,
    val initialCompleted: Boolean = false
)

@Composable
fun DashboardScreen(
    repository: StudyForgeRepository,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToExam: () -> Unit,
    onNavigateToPhysicsLab: () -> Unit,
    onNavigateToProgrammingLab: () -> Unit,
    onNavigateToTutor: () -> Unit,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToCalculator: (() -> Unit)? = null,
    onNavigateToFormulaLibrary: (() -> Unit)? = null
) {
    val courses by repository.courses.collectAsState()
    val streak by repository.streakDays.collectAsState()
    val questionsCount by repository.questionsAnswered.collectAsState()
    val averageScore by repository.averageScore.collectAsState()
    val readiness by repository.examinationReadiness.collectAsState()
    val examDate by repository.examDate.collectAsState()

    var showExamDateDialog by remember { mutableStateOf(false) }
    var tempExamDate by remember { mutableStateOf(examDate) }

    // Today's Priorities interactive state
    val priorities = remember {
        mutableStateListOf(
            PriorityTask("p1", "Uniform Accelerated Motion — Revise kinematics formulas", "Physics", "CSPC 132", false),
            PriorityTask("p2", "Vectors & Trigonometry — Solve 5 practice exam questions", "Exam Drill", "CSPC 132", false),
            PriorityTask("p3", "C++ Pointers & References — Review memory allocation", "Coding", "CSSD 101", true),
            PriorityTask("p4", "Physics 2024 Past Paper — Timed Section A attempt", "Mock Exam", "CSPC 132", false)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // 1. Welcome Hero Banner
        item {
            DashboardWelcomeHero(
                streakDays = streak,
                onContinueLearning = {
                    val firstCourse = courses.firstOrNull()?.id ?: "course_physics"
                    onNavigateToCourse(firstCourse)
                },
                onStartMockExam = onNavigateToExam,
                onAskAiClick = onNavigateToTutor
            )
        }

        // 2. Metrics Cards Grid (6 Essential Performance Indicators)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ACADEMIC MASTERY METRICS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                // Row 1 of 3 metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Exam Readiness",
                        value = "$readiness%",
                        subtitle = "Overall preparedness",
                        icon = Icons.Default.Verified,
                        accentColor = SuccessGreen,
                        progress = readiness / 100f,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Courses Active",
                        value = "${courses.size}",
                        subtitle = "CSPC 132 & CSSD 101",
                        icon = Icons.Default.School,
                        accentColor = BrightBlue,
                        progress = 1.0f,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Study Streak",
                        value = "$streak Days",
                        subtitle = "Active consistency",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = WarningAmber,
                        progress = minOf(1.0f, streak / 7f),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2 of 3 metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Topics Mastered",
                        value = "18 / 24",
                        subtitle = "75% completed",
                        icon = Icons.Default.CheckCircle,
                        accentColor = PrimaryIndigo,
                        progress = 18f / 24f,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Average Quiz Score",
                        value = "${averageScore.toInt()}%",
                        subtitle = "From $questionsCount drills",
                        icon = Icons.Default.Analytics,
                        accentColor = AccentCyan,
                        progress = (averageScore / 100.0).toFloat(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Weekly Study Hours",
                        value = "14.5 hrs",
                        subtitle = "Goal: 15 hrs / wk",
                        icon = Icons.Default.Schedule,
                        accentColor = Color(0xFFA855F7),
                        progress = 14.5f / 15f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Upcoming Exam Countdown Banner
        item {
            UpcomingExamBanner(
                countdownText = examDate,
                onEditClick = { showExamDateDialog = true },
                onStartPractice = onNavigateToExam
            )
        }

        // 4. Today's Priorities Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.TaskAlt, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Today's Study Priorities",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        val completedCount = priorities.count { it.initialCompleted }
                        Text(
                            text = "$completedCount of ${priorities.size} completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    priorities.forEachIndexed { index, task ->
                        var isChecked by remember(task.id) { mutableStateOf(task.initialCompleted) }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isChecked) MaterialTheme.colorScheme.surface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isChecked = !isChecked }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it }
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isChecked) FontWeight.Normal else FontWeight.SemiBold,
                                        color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = task.courseCode,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PrimaryIndigo,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "• ${task.tag}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Course Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Courses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Undergraduate Physics & Computer Science Curriculum",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                courses.forEach { course ->
                    CourseCard(
                        course = course,
                        onContinueClick = { onNavigateToCourse(course.id) }
                    )
                }
            }
        }

        // 6. Interactive Study Labs & Tools Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "INTERACTIVE STUDY TOOLS & LABS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Physics Lab",
                        subtitle = "Vectors & Simulation",
                        icon = Icons.Default.Science,
                        gradient = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPhysicsLab
                    )
                    QuickActionCard(
                        title = "C++ Lab",
                        subtitle = "Trace & Algorithmic",
                        icon = Icons.Default.Code,
                        gradient = listOf(Color(0xFF059669), Color(0xFF047857)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProgrammingLab
                    )
                    QuickActionCard(
                        title = "AI Tutor",
                        subtitle = "Socratic Coach",
                        icon = Icons.Default.Psychology,
                        gradient = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTutor
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = "Calculator",
                        subtitle = "Precision Engine",
                        icon = Icons.Default.Calculate,
                        gradient = listOf(Color(0xFF4F46E5), Color(0xFF4338CA)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCalculator?.invoke() }
                    )
                    QuickActionCard(
                        title = "Formulas",
                        subtitle = "SI Standards Reference",
                        icon = Icons.Default.Functions,
                        gradient = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToFormulaLibrary?.invoke() }
                    )
                    QuickActionCard(
                        title = "Flashcards",
                        subtitle = "Spaced Repetition",
                        icon = Icons.Default.Style,
                        gradient = listOf(Color(0xFFD97706), Color(0xFFB45309)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFlashcards
                    )
                }
            }
        }
    }

    // Exam Date Edit Dialog
    if (showExamDateDialog) {
        AlertDialog(
            onDismissRequest = { showExamDateDialog = false },
            title = { Text("Update Target Examination Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Set your university examination milestone date for personalized study recommendations.")
                    OutlinedTextField(
                        value = tempExamDate,
                        onValueChange = { tempExamDate = it },
                        label = { Text("Exam Date / Timeline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateExamDate(tempExamDate)
                        showExamDateDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExamDateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DashboardWelcomeHero(
    streakDays: Int,
    onContinueLearning: () -> Unit,
    onStartMockExam: () -> Unit,
    onAskAiClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF10182B),
                        Color(0xFF17213A),
                        Color(0xFF1E2A4A)
                    )
                )
            )
            .border(1.dp, PrimaryIndigo.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryIndigo.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "WELCOME BACK, SCHOLAR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WarningAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$streakDays Days Streak",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Your next breakthrough starts here.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 30.sp
                )
                Text(
                    text = "Pick up where you left off, strengthen weak topics, and get exam-ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onContinueLearning,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Continue Learning", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onStartMockExam,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Mock Exam", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.15f)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun UpcomingExamBanner(
    countdownText: String,
    onEditClick: () -> Unit,
    onStartPractice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Exam Countdown",
                        tint = ErrorRed
                    )
                }
                Column {
                    Text(
                        text = "Target Examination Milestone",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = countdownText,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.EditCalendar, contentDescription = "Edit Date", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                Button(
                    onClick = onStartPractice,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Practice Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onContinueClick: () -> Unit
) {
    val progressPercent = if (course.totalLessons > 0) (course.completedLessons * 100 / course.totalLessons) else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onContinueClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryIndigo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (course.code.contains("132")) Icons.Default.Science else Icons.Default.Code,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = course.code,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryIndigo
                        )
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$progressPercent% Mastered",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryIndigo,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Next Topic: ${if (course.code.contains("132")) "Torricelli's Kinematic Law" else "Pointer Arithmetic & Tracing"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${course.completedLessons} of ${course.totalLessons} Lessons Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Continue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
