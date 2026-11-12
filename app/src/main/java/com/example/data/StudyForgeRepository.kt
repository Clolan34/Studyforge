package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StudyForgeRepository {

    private val _courses = MutableStateFlow<List<Course>>(SampleDataSeed.courses)
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _questions = MutableStateFlow<List<Question>>(SampleQuestionsSeed.questions)
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _flashcards = MutableStateFlow<List<Flashcard>>(
        listOf(
            Flashcard(
                id = "fc_1",
                courseCode = "CSPC 132",
                topic = "Uniform Accelerated Motion",
                category = FlashcardCategory.FORMULA,
                front = "What is the 3rd Kinematic Equation (velocity-displacement) with no time variable?",
                back = "v² = u² + 2as\n\nWhere:\nv = final velocity (m/s)\nu = initial velocity (m/s)\na = acceleration (m/s²)\ns = displacement (m)",
                hint = "Derived from work-energy theorem",
                formula = "v² = u² + 2as"
            ),
            Flashcard(
                id = "fc_2",
                courseCode = "CSPC 132",
                topic = "Scalars and Vectors",
                category = FlashcardCategory.DEFINITION,
                front = "How do you calculate the scalar (dot) product of two vectors A and B?",
                back = "A · B = |A| |B| cos(θ) = (Ax * Bx) + (Ay * By) + (Az * Bz)\n\nNote: If A · B = 0, the two non-zero vectors are perpendicular (orthogonal).",
                hint = "Returns a scalar, not a vector",
                formula = "A · B = |A||B| cos θ"
            ),
            Flashcard(
                id = "fc_3",
                courseCode = "CSPC 132",
                topic = "Velocity-Time Graphs",
                category = FlashcardCategory.CONCEPT,
                front = "What do the SLOPE and AREA under a Velocity-Time graph represent?",
                back = "• Slope (gradient = dv/dt) = Instantaneous Acceleration\n• Area under curve (∫ v dt) = Net Displacement",
                hint = "Think derivatives vs integrals"
            ),
            Flashcard(
                id = "fc_4",
                courseCode = "CSSD 101",
                topic = "Problem Analysis Charts (PAC)",
                category = FlashcardCategory.DEFINITION,
                front = "What are the 4 quadrants of a Problem Analysis Chart (PAC)?",
                back = "1. Given Data (All provided input facts)\n2. Required Results (The desired outputs)\n3. Processing Requirements (Formulas, logic, validations)\n4. Alternative Solutions (Different possible design strategies)",
                hint = "Used before writing any code or flowcharts"
            ),
            Flashcard(
                id = "fc_5",
                courseCode = "CSSD 101",
                topic = "Loops and Repetition",
                category = FlashcardCategory.CODE_SYNTAX,
                front = "What is the fundamental execution difference between a `while` loop and a `do-while` loop in C++?",
                back = "• `while` loop: Pre-test loop. Condition is evaluated BEFORE body runs. Can execute 0 or more times.\n• `do-while` loop: Post-test loop. Condition is checked AFTER body runs. Guaranteed to execute AT LEAST ONCE.",
                hint = "Pre-test vs Post-test"
            ),
            Flashcard(
                id = "fc_6",
                courseCode = "CSSD 101",
                topic = "Functions",
                category = FlashcardCategory.CONCEPT,
                front = "In C++, what is the difference between Pass-by-Value and Pass-by-Reference?",
                back = "• Pass-by-Value (`int x`): A copy of the argument is passed. Modifications inside the function do not affect the original variable.\n• Pass-by-Reference (`int &x`): An alias (memory address) is passed. Modifications inside the function directly change the caller's variable.",
                hint = "Look for the ampersand & symbol"
            )
        )
    )
    val flashcards: StateFlow<List<Flashcard>> = _flashcards.asStateFlow()

    private val _studyTasks = MutableStateFlow<List<StudyTask>>(
        listOf(
            StudyTask("task_1", "Master Vector Component Resolution", "CSPC 132", "Vectors", "LESSON", 20, false, "HIGH"),
            StudyTask("task_2", "Solve 5 Uniform Acceleration Problems", "CSPC 132", "Kinematics", "PRACTICE", 25, false, "HIGH"),
            StudyTask("task_3", "Interactive Physics Lab: Vector Visualizer", "CSPC 132", "Vectors", "LAB", 15, true, "MEDIUM"),
            StudyTask("task_4", "Trace C++ Loop Output in Programming Lab", "CSSD 101", "Loops", "LAB", 20, false, "HIGH"),
            StudyTask("task_5", "Review 10 Spaced Repetition Flashcards", "CSPC 132", "Review", "REVISION", 10, false, "MEDIUM")
        )
    )
    val studyTasks: StateFlow<List<StudyTask>> = _studyTasks.asStateFlow()

    private val _documents = MutableStateFlow<List<StudyDocument>>(
        listOf(
            StudyDocument(
                id = "doc_1",
                title = "Uniform Accelerated Motion.pdf",
                courseCode = "CSPC 132",
                fileType = "PDF",
                uploadedDate = "2026-11-10",
                pageCount = 18,
                status = DocumentStatus.READY,
                extractedTopicsCount = 5,
                summary = "Full coverage of 1D kinematics, the 4 acceleration equations, free-fall gravity experiments, and velocity-time profile calculations.",
                keyFormulasExtracted = 6
            ),
            StudyDocument(
                id = "doc_2",
                title = "Physics for Computing — 2024 Exam Paper.pdf",
                courseCode = "CSPC 132",
                fileType = "PDF",
                uploadedDate = "2026-11-10",
                pageCount = 6,
                status = DocumentStatus.READY,
                extractedTopicsCount = 4,
                summary = "Official university examination paper: Section A 20 multiple choice questions, Section B 4 structured analytical questions with marking schemes.",
                keyFormulasExtracted = 8
            ),
            StudyDocument(
                id = "doc_3",
                title = "Vectors — Level 100.pdf",
                courseCode = "CSPC 132",
                fileType = "PDF",
                uploadedDate = "2026-11-09",
                pageCount = 24,
                status = DocumentStatus.READY,
                extractedTopicsCount = 6,
                summary = "Geometric and algebraic representations of vectors, dot products, cross products, coordinate systems, and force resolutions.",
                keyFormulasExtracted = 9
            ),
            StudyDocument(
                id = "doc_4",
                title = "Starting Out with C++: Control Structures (Tony Gaddis).pdf",
                courseCode = "CSSD 101",
                fileType = "PDF",
                uploadedDate = "2026-11-08",
                pageCount = 42,
                status = DocumentStatus.READY,
                extractedTopicsCount = 8,
                summary = "Chapters 2 through 7: data representation, conditional branching, while/for loops, functions, pass-by-reference, and 1D arrays.",
                keyFormulasExtracted = 0
            ),
            StudyDocument(
                id = "doc_5",
                title = "CSSD 101 Planning Your Solutions — PAC & IPO.docx",
                courseCode = "CSSD 101",
                fileType = "DOCX",
                uploadedDate = "2026-11-08",
                pageCount = 14,
                status = DocumentStatus.READY,
                extractedTopicsCount = 4,
                summary = "Problem Analysis Charts, Input-Process-Output tables, ANSI standard flowchart symbols, and pseudocode standards.",
                keyFormulasExtracted = 0
            )
        )
    )
    val documents: StateFlow<List<StudyDocument>> = _documents.asStateFlow()

    private val _examResults = MutableStateFlow<List<ExamResult>>(emptyList())
    val examResults: StateFlow<List<ExamResult>> = _examResults.asStateFlow()

    // Analytics state
    val streakDays = MutableStateFlow(4)
    val questionsAnswered = MutableStateFlow(48)
    val averageScore = MutableStateFlow(84.5)
    val topicsMastered = MutableStateFlow(9)
    val examinationReadiness = MutableStateFlow(78)
    val examDate = MutableStateFlow("Dec 18, 2026 (37 Days Left)")

    fun getModulesForCourse(courseId: String): List<CourseModule> {
        return if (courseId == "course_physics") {
            SampleDataSeed.physicsModules
        } else {
            SampleDataSeed.programmingModules
        }
    }

    fun getLesson(lessonId: String): Lesson? {
        return SampleLessonsSeed.lessonsMap[lessonId] ?: run {
            // Generate a comprehensive structured lesson if not in the pre-defined map
            val allModules = SampleDataSeed.physicsModules + SampleDataSeed.programmingModules
            for (m in allModules) {
                val found = m.lessons.find { it.id == lessonId }
                if (found != null) {
                    val courseCode = if (m.courseId == "course_physics") "CSPC 132" else "CSSD 101"
                    return Lesson(
                        id = found.id,
                        moduleId = m.id,
                        courseCode = courseCode,
                        title = found.title,
                        moduleTitle = m.title,
                        objectives = m.objectives,
                        explanationText = "# ${found.title}\n\nThis academic lesson is derived from the official curriculum for **$courseCode** ($${m.title}).\n\n### Theoretical Foundations:\nMastering ${found.title} requires rigorous attention to core definitions, mathematical formulations, and algorithmic decomposition.\n\n### Analytical Steps:\n1. Establish the known variables and parameters.\n2. Apply the foundational governing equation or logic chart.\n3. Verify units and boundary conditions.\n4. Synthesize your final conclusion.",
                        keyTakeaways = listOf(
                            "Consistent practice reinforces conceptual clarity.",
                            "Review associated formulas and past exam papers.",
                            "Test your understanding with the practice problems below."
                        ),
                        checkQuestion = CheckQuestion(
                            prompt = "Which principle is fundamental to ${found.title}?",
                            options = listOf(
                                "Rigorous boundary analysis & conservation laws",
                                "Arbitrary variable assignment without units",
                                "Ignoring initial conditions",
                                "Skipping problem decomposition"
                            ),
                            correctIndex = 0,
                            explanation = "Academic rigor requires careful boundary condition analysis and respecting conservation laws."
                        ),
                        sourceReference = m.sourceName ?: "StudyForge Core Academic Curriculum"
                    )
                }
            }
            null
        }
    }

    fun toggleTask(taskId: String) {
        _studyTasks.update { list ->
            list.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
        }
    }

    fun addCustomTask(title: String, courseCode: String, topic: String, duration: Int) {
        val newTask = StudyTask(
            id = "task_${System.currentTimeMillis()}",
            title = title,
            courseCode = courseCode,
            topic = topic,
            type = "REVISION",
            durationMinutes = duration,
            isCompleted = false,
            priority = "MEDIUM"
        )
        _studyTasks.update { listOf(newTask) + it }
    }

    fun recordExamResult(result: ExamResult) {
        _examResults.update { listOf(result) + it }
        questionsAnswered.update { it + result.totalQuestions }
        averageScore.update {
            val total = _examResults.value.sumOf { it.percentage }
            total / _examResults.value.size
        }
    }

    fun reviewFlashcard(cardId: String, rating: ReviewRating) {
        _flashcards.update { list ->
            list.map { card ->
                if (card.id == cardId) {
                    val newRepetitions = if (rating == ReviewRating.AGAIN) 0 else card.repetitions + 1
                    val newInterval = when (rating) {
                        ReviewRating.AGAIN -> 1
                        ReviewRating.GOOD -> (card.intervalDays * card.easeFactor).toInt().coerceAtLeast(1)
                        ReviewRating.EASY -> (card.intervalDays * card.easeFactor * 1.5).toInt().coerceAtLeast(3)
                    }
                    card.copy(
                        repetitions = newRepetitions,
                        intervalDays = newInterval,
                        nextReviewDate = System.currentTimeMillis() + (newInterval * 86400000L)
                    )
                } else card
            }
        }
    }

    fun addGeneratedQuestion(question: Question) {
        _questions.update { listOf(question) + it }
    }

    fun addCustomDocument(title: String, courseCode: String, type: String) {
        val newDoc = StudyDocument(
            id = "doc_${System.currentTimeMillis()}",
            title = title,
            courseCode = courseCode,
            fileType = type,
            uploadedDate = "2026-11-11",
            pageCount = 12,
            status = DocumentStatus.READY,
            extractedTopicsCount = 3,
            summary = "User-uploaded document processed by StudyForge extraction pipeline. Extracted formulas and structured practice exercises.",
            keyFormulasExtracted = 4
        )
        _documents.update { listOf(newDoc) + it }
    }

    fun updateExamDate(newDate: String) {
        examDate.value = newDate
    }
}
