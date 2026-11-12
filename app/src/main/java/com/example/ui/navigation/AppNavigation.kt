package com.example.ui.navigation

import androidx.activity.compose.BackHandler
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
import com.example.ai.AiTutorService
import com.example.data.StudyForgeRepository
import com.example.model.ExamConfiguration
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class NavCategory(val title: String) {
    LEARNING("LEARNING"),
    EXAM_PREPARATION("EXAM PREPARATION"),
    STUDY_TOOLS("STUDY TOOLS")
}

enum class NavigationRoute(
    val routeId: String,
    val title: String,
    val category: NavCategory,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Dashboard", NavCategory.LEARNING, Icons.Default.Dashboard),
    COURSES("courses", "My Courses", NavCategory.LEARNING, Icons.Default.School),
    AI_TUTOR("ai_tutor", "AI Socratic Tutor", NavCategory.LEARNING, Icons.Default.Psychology),
    LIBRARY("library", "Study Library & Files", NavCategory.LEARNING, Icons.Default.FolderOpen),

    PRACTICE("practice", "Practice & Mock Exams", NavCategory.EXAM_PREPARATION, Icons.Default.Quiz),
    FLASHCARDS("flashcards", "Flashcards Recall", NavCategory.EXAM_PREPARATION, Icons.Default.Style),
    FORMULA_LIBRARY("formula_library", "Formula Library", NavCategory.EXAM_PREPARATION, Icons.Default.Functions),

    SCIENTIFIC_CALCULATOR("calculator", "Scientific Calculator", NavCategory.STUDY_TOOLS, Icons.Default.Calculate),
    PHYSICS_LAB("physics_lab", "Physics Lab", NavCategory.STUDY_TOOLS, Icons.Default.Science),
    PROGRAMMING_LAB("programming_lab", "C++ Programming Lab", NavCategory.STUDY_TOOLS, Icons.Default.Code),
    ANALYTICS("analytics", "Mastery Analytics", NavCategory.STUDY_TOOLS, Icons.Default.Analytics)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    repository: StudyForgeRepository,
    aiTutorService: AiTutorService,
    isDarkMode: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var currentRoute by remember { mutableStateOf(NavigationRoute.DASHBOARD) }
    var activeLessonId by remember { mutableStateOf<String?>(null) }
    var activeExamConfig by remember { mutableStateOf<ExamConfiguration?>(null) }
    var selectedCourseId by remember { mutableStateOf("course_physics") }
    var tutorInitialPrompt by remember { mutableStateOf("") }
    var calculatorInitialSnippet by remember { mutableStateOf("") }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val streak by repository.streakDays.collectAsState()
    val readiness by repository.examinationReadiness.collectAsState()

    // Handle system back navigation
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    // Sub-screen 1: Active Exam Simulator
    if (activeExamConfig != null) {
        ExamSimulatorScreen(
            configuration = activeExamConfig!!,
            repository = repository,
            onFinishExam = { activeExamConfig = null }
        )
        return
    }

    // Sub-screen 2: Active Lesson Player
    if (activeLessonId != null) {
        BackHandler { activeLessonId = null }
        LessonPlayerScreen(
            lessonId = activeLessonId!!,
            repository = repository,
            onBack = { activeLessonId = null },
            onNavigateToTutorWithPrompt = { prompt ->
                tutorInitialPrompt = prompt
                activeLessonId = null
                currentRoute = NavigationRoute.AI_TUTOR
            }
        )
        return
    }

    // BackHandler to return to Dashboard if on another route
    if (currentRoute != NavigationRoute.DASHBOARD) {
        BackHandler {
            currentRoute = NavigationRoute.DASHBOARD
        }
    }

    val navDrawerContent: @Composable () -> Unit = {
        ModalDrawerSheet(
            modifier = Modifier
                .width(310.dp)
                .testTag("main_navigation_drawer"),
            drawerContainerColor = MaterialTheme.colorScheme.surface
        ) {
            // Brand Logo & Student Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryIndigo, BrightBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(
                            text = "StudyForge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Learn Deeply. Excel Confidently.",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryIndigo,
                            fontSize = 10.sp
                        )
                    }
                }

                // Progress Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                            Text(
                                text = "$streak-Day Streak",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                        }
                        Text(
                            text = "Readiness: $readiness%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Groups
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavCategory.values().forEach { category ->
                    item {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }

                    val groupRoutes = NavigationRoute.values().filter { it.category == category }
                    items(groupRoutes) { route ->
                        val isSelected = currentRoute == route
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = route.title,
                                    tint = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = route.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                currentRoute = route
                                coroutineScope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("drawer_item_${route.routeId}"),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = PrimaryIndigo.copy(alpha = 0.15f),
                                selectedTextColor = PrimaryIndigo
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // System Footer
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )
                    Text(
                        text = "SYSTEM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(if (isDarkMode) "Switch to Light Theme" else "Switch to Dark Theme", fontSize = 13.sp)
                        },
                        selected = false,
                        onClick = onToggleTheme,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text("Settings & Shortcuts", fontSize = 13.sp)
                        },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showSettingsDialog = true
                        },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = navDrawerContent
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "StudyForge",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "›",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = currentRoute.category.title.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = currentRoute.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            modifier = Modifier.testTag("drawer_toggle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Global Search Button
                        IconButton(
                            onClick = { showSearchDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Theme Toggle Icon
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.size(36.dp).testTag("theme_toggle_btn")
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = WarningAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Direct one-tap AI Tutor button
                        IconButton(
                            onClick = {
                                tutorInitialPrompt = ""
                                currentRoute = NavigationRoute.AI_TUTOR
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo.copy(alpha = 0.2f))
                                .testTag("top_bar_ai_tutor_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Tutor",
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentRoute) {
                    NavigationRoute.DASHBOARD -> {
                        DashboardScreen(
                            repository = repository,
                            onNavigateToCourse = { courseId ->
                                selectedCourseId = courseId
                                currentRoute = NavigationRoute.COURSES
                            },
                            onNavigateToExam = { currentRoute = NavigationRoute.PRACTICE },
                            onNavigateToPhysicsLab = { currentRoute = NavigationRoute.PHYSICS_LAB },
                            onNavigateToProgrammingLab = { currentRoute = NavigationRoute.PROGRAMMING_LAB },
                            onNavigateToTutor = { currentRoute = NavigationRoute.AI_TUTOR },
                            onNavigateToFlashcards = { currentRoute = NavigationRoute.FLASHCARDS },
                            onNavigateToCalculator = { currentRoute = NavigationRoute.SCIENTIFIC_CALCULATOR },
                            onNavigateToFormulaLibrary = { currentRoute = NavigationRoute.FORMULA_LIBRARY }
                        )
                    }

                    NavigationRoute.LIBRARY -> {
                        LibraryPlannerScreen(repository = repository)
                    }

                    NavigationRoute.COURSES -> {
                        CoursesScreen(
                            repository = repository,
                            initialCourseId = selectedCourseId,
                            onSelectLesson = { lessonId ->
                                activeLessonId = lessonId
                            }
                        )
                    }

                    NavigationRoute.PRACTICE -> {
                        ExamPracticeScreen(
                            repository = repository,
                            aiTutorService = aiTutorService,
                            onStartExamSession = { config ->
                                activeExamConfig = config
                            },
                            onViewPastResults = {
                                // Handled in practice
                            }
                        )
                    }

                    NavigationRoute.ANALYTICS -> {
                        AnalyticsScreen(
                            repository = repository,
                            onTakePracticeExam = {
                                currentRoute = NavigationRoute.PRACTICE
                            }
                        )
                    }

                    NavigationRoute.SCIENTIFIC_CALCULATOR -> {
                        ScientificCalculatorScreen(
                            initialExpression = calculatorInitialSnippet,
                            onNavigateToFormulaLibrary = {
                                currentRoute = NavigationRoute.FORMULA_LIBRARY
                            }
                        )
                    }

                    NavigationRoute.FORMULA_LIBRARY -> {
                        FormulaLibraryScreen(
                            onSendToCalculator = { snippet ->
                                calculatorInitialSnippet = snippet
                                currentRoute = NavigationRoute.SCIENTIFIC_CALCULATOR
                            }
                        )
                    }

                    NavigationRoute.PHYSICS_LAB -> {
                        PhysicsLabScreen()
                    }

                    NavigationRoute.PROGRAMMING_LAB -> {
                        ProgrammingLabScreen()
                    }

                    NavigationRoute.AI_TUTOR -> {
                        AiTutorScreen(
                            aiTutorService = aiTutorService,
                            initialPrompt = tutorInitialPrompt
                        )
                    }

                    NavigationRoute.FLASHCARDS -> {
                        FlashcardsScreen(repository = repository)
                    }
                }
            }
        }
    }

    // Global Search Modal Dialog
    if (showSearchDialog) {
        var query by remember { mutableStateOf("") }
        val allQuestions by repository.questions.collectAsState()
        val allCourses by repository.courses.collectAsState()

        val filteredQuestions = remember(query) {
            if (query.length < 2) emptyList()
            else allQuestions.filter { it.prompt.contains(query, ignoreCase = true) || it.topic.contains(query, ignoreCase = true) }.take(6)
        }

        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            title = {
                Text("Search StudyForge Curriculum", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search lessons, kinematics, vectors, C++...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (query.isBlank()) {
                        Text(
                            text = "Quick shortcuts:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AssistChip(onClick = { query = "Kinematics" }, label = { Text("Kinematics") })
                            AssistChip(onClick = { query = "Vectors" }, label = { Text("Vectors") })
                            AssistChip(onClick = { query = "Pointers" }, label = { Text("Pointers") })
                        }
                    } else if (filteredQuestions.isEmpty()) {
                        Text(
                            text = "No direct question matches found for '$query'. Try searching a broader topic.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredQuestions) { q ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showSearchDialog = false
                                            currentRoute = NavigationRoute.PRACTICE
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(text = q.topic, style = MaterialTheme.typography.labelSmall, color = PrimaryIndigo, fontWeight = FontWeight.Bold)
                                        Text(text = q.prompt, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Settings & Shortcuts Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = PrimaryIndigo)
                    Text("StudyForge Settings & Shortcuts")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Platform Configuration",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Theme", style = MaterialTheme.typography.bodyMedium)
                        FilterChip(
                            selected = isDarkMode,
                            onClick = onToggleTheme,
                            label = { Text(if (isDarkMode) "Dark Theme" else "Light Theme") }
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Text(
                        text = "Curriculum Courses Installed:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• CSPC 132 — Physics for Computing (12 Modules, Past Exams)", style = MaterialTheme.typography.bodySmall)
                    Text("• CSSD 101 — Programming & Problem Solving in C++ (12 Modules)", style = MaterialTheme.typography.bodySmall)
                    Text("• Scientific Calculator: Recursive Descent Engine (No eval)", style = MaterialTheme.typography.bodySmall)

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Text(
                        text = "Version 2.4.0 • Linear & Notion Inspired Redesign",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}
