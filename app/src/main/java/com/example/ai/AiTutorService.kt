package com.example.ai

import android.util.Log
import com.example.model.*
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "AiTutorService"

enum class TutorMode(val title: String, val systemPersona: String) {
    SOCRATIC_HINT(
        "Socratic Tutor",
        "You are the StudyForge Socratic Tutor for university physics and computer science. Never give the direct answer immediately. Instead, analyze the student's thought process, identify misconceptions, and provide a targeted guiding question or hint that leads them to reason independently."
    ),
    BEGINNER_EXPLANATION(
        "Beginner Explanation",
        "You are an intuitive, friendly educational scientist. Explain complex physics and programming concepts using clear, intuitive real-world analogies, step-by-step intuition, and plain English before introducing formal equations."
    ),
    UNIVERSITY_LECTURE(
        "University Lecture",
        "You are a rigorous Harvard-style university professor in Physics and Computer Science. Present structured, mathematically rigorous definitions, theoretical derivations, boundary condition analyses, and exact algorithmic pseudocode."
    ),
    STEP_BY_STEP(
        "Step-by-Step Solution",
        "You are a master problem solver. Break down mathematical physics and programming problems into numbered, sequential steps with explicit given variables, formula substitutions, intermediate units, and clear justifications."
    ),
    EXAM_COACHING(
        "Exam Coaching",
        "You are an exam strategy specialist. Guide the student on exam time management, how marks are awarded in past papers, common traps to avoid in MCQs and structured questions, and precision in formula usage."
    ),
    CHALLENGE_MODE(
        "Challenge Mode",
        "You are a challenging academic quizmaster. Present deep, multi-step Level 4 reasoning questions, test edge cases, ask students to justify why naive solutions fail, and push them to think like true researchers."
    )
}

sealed interface AiResponseState {
    data object Idle : AiResponseState
    data object Loading : AiResponseState
    data class Success(val response: String) : AiResponseState
    data class Error(val message: String, val fallbackContent: String? = null) : AiResponseState
}

class AiTutorService {

    // Lazy initialization mandated by Firebase AI Logic skill
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
                topP = 0.95f
            }
        )
    }

    suspend fun askTutor(
        studentQuery: String,
        mode: TutorMode,
        courseContext: String = "CSPC 132 & CSSD 101",
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Pair<Boolean, String> {
        val prompt = buildString {
            appendLine("SYSTEM ROLE: ${mode.systemPersona}")
            appendLine("COURSE CONTEXT: $courseContext")
            appendLine("INSTRUCTION: Adhere to academic rigor. Ground answers in physics kinematics, vectors, problem analysis charts (PAC), IPO charts, and C++ programming. If the student made an error, guide them Socratically.")
            appendLine()
            if (conversationHistory.isNotEmpty()) {
                appendLine("PREVIOUS CONTEXT:")
                for ((role, text) in conversationHistory.takeLast(4)) {
                    appendLine("$role: $text")
                }
                appendLine()
            }
            appendLine("STUDENT: $studentQuery")
            appendLine("AI TUTOR:")
        }

        return try {
            val response = withTimeout(45_000L) {
                model.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Pair(true, text.trim())
            } else {
                Pair(false, generateFallbackTutorResponse(studentQuery, mode))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Tutor generation timed out", e)
            Pair(false, generateFallbackTutorResponse(studentQuery, mode))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI tutor error: ${e.message}", e)
            Pair(false, generateFallbackTutorResponse(studentQuery, mode))
        }
    }

    suspend fun generateGroundedQuestion(
        courseCode: String,
        topic: String,
        difficulty: DifficultyLevel,
        questionType: QuestionType
    ): Question? {
        val prompt = buildString {
            appendLine("You are an expert university examiner. Create a single rigorous, verified examination question for university students.")
            appendLine("Course: $courseCode")
            appendLine("Topic: $topic")
            appendLine("Difficulty: ${difficulty.label}")
            appendLine("Question Type: ${questionType.name}")
            appendLine()
            appendLine("Format your output strictly with these sections:")
            appendLine("PROMPT: [The full question statement]")
            appendLine("OPTION_A: [Option text if MCQ]")
            appendLine("OPTION_B: [Option text if MCQ]")
            appendLine("OPTION_C: [Option text if MCQ]")
            appendLine("OPTION_D: [Option text if MCQ]")
            appendLine("CORRECT_ANSWER: [Exact text of correct answer or option letter]")
            appendLine("EXPLANATION: [Step-by-step mathematical or algorithmic derivation]")
        }

        return try {
            val response = withTimeout(45_000L) {
                model.generateContent(prompt)
            }
            val rawText = response.text
            if (!rawText.isNullOrBlank()) {
                parseGeneratedQuestion(rawText, courseCode, topic, difficulty, questionType)
            } else {
                createLocalFallbackQuestion(courseCode, topic, difficulty, questionType)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Question generation timed out", e)
            createLocalFallbackQuestion(courseCode, topic, difficulty, questionType)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate question with AI", e)
            createLocalFallbackQuestion(courseCode, topic, difficulty, questionType)
        }
    }

    private fun parseGeneratedQuestion(
        rawText: String,
        courseCode: String,
        topic: String,
        difficulty: DifficultyLevel,
        questionType: QuestionType
    ): Question {
        val lines = rawText.lines()
        var prompt = ""
        val options = mutableListOf<String>()
        var correctAnswer = ""
        var explanation = ""

        var currentSection = ""
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("PROMPT:") -> {
                    currentSection = "PROMPT"
                    prompt = trimmed.removePrefix("PROMPT:").trim()
                }
                trimmed.startsWith("OPTION_") -> {
                    currentSection = "OPTION"
                    val opt = trimmed.substringAfter(":").trim()
                    if (opt.isNotBlank()) options.add(opt)
                }
                trimmed.startsWith("CORRECT_ANSWER:") -> {
                    currentSection = "ANSWER"
                    correctAnswer = trimmed.removePrefix("CORRECT_ANSWER:").trim()
                }
                trimmed.startsWith("EXPLANATION:") -> {
                    currentSection = "EXPLANATION"
                    explanation = trimmed.removePrefix("EXPLANATION:").trim()
                }
                else -> {
                    if (currentSection == "PROMPT") prompt += "\n" + trimmed
                    if (currentSection == "EXPLANATION") explanation += "\n" + trimmed
                }
            }
        }

        if (prompt.isBlank()) prompt = "Analytical problem on $topic in $courseCode."
        if (correctAnswer.isBlank()) correctAnswer = options.firstOrNull() ?: "Verified analytical solution"

        val correctIdx = options.indexOfFirst { it.equals(correctAnswer, ignoreCase = true) || it.startsWith(correctAnswer) }

        return Question(
            id = "gen_ai_${System.currentTimeMillis()}",
            courseCode = courseCode,
            topic = topic,
            type = if (options.size >= 2) QuestionType.MULTIPLE_CHOICE else questionType,
            prompt = prompt.trim(),
            options = if (options.size >= 2) options else listOf("Option A", "Option B", "Option C", "Option D"),
            correctAnswer = correctAnswer,
            correctOptionIndex = if (correctIdx >= 0) correctIdx else 0,
            explanation = explanation.ifBlank { "Generated by StudyForge AI grounded in academic course standards." },
            difficulty = difficulty,
            bloomsLevel = when (difficulty) {
                DifficultyLevel.LEVEL_1 -> "Remembering"
                DifficultyLevel.LEVEL_2 -> "Understanding"
                DifficultyLevel.LEVEL_3 -> "Applying"
                DifficultyLevel.LEVEL_4 -> "Analyzing"
            },
            origin = QuestionOrigin.AI_GENERATED,
            marks = difficulty.levelNumber,
            sourceReference = "StudyForge AI Tutor Engine — Grounded Synthesis"
        )
    }

    private fun createLocalFallbackQuestion(
        courseCode: String,
        topic: String,
        difficulty: DifficultyLevel,
        questionType: QuestionType
    ): Question {
        return if (courseCode.contains("132", ignoreCase = true)) {
            Question(
                id = "gen_local_${System.currentTimeMillis()}",
                courseCode = courseCode,
                topic = topic,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "A high-performance computing chip package of mass 0.04 kg is subjected to a net horizontal magnetic force of 0.8 N during robotic placement. Calculate the acceleration of the package.",
                options = listOf(
                    "20 m/s²",
                    "0.032 m/s²",
                    "32 m/s²",
                    "2.0 m/s²"
                ),
                correctAnswer = "20 m/s²",
                correctOptionIndex = 0,
                numericalAnswer = 20.0,
                explanation = "Using Newton's Second Law: F = m * a => a = F / m = 0.8 N / 0.04 kg = 20 m/s².",
                difficulty = difficulty,
                bloomsLevel = "Applying",
                origin = QuestionOrigin.AI_GENERATED,
                marks = 2,
                sourceReference = "CSPC 132 Physics Bank (Offline Socratic Generator)"
            )
        } else {
            Question(
                id = "gen_local_${System.currentTimeMillis()}",
                courseCode = courseCode,
                topic = topic,
                type = QuestionType.MULTIPLE_CHOICE,
                prompt = "In C++, what will occur if you declare an array `int arr[5];` and attempt to write `arr[5] = 10;`?",
                options = listOf(
                    "Undefined behavior / Buffer overflow (indices run from 0 to 4)",
                    "A compile-time syntax error on the assignment",
                    "The array automatically resizes to size 6",
                    "arr[5] points safely to the last element"
                ),
                correctAnswer = "Undefined behavior / Buffer overflow (indices run from 0 to 4)",
                correctOptionIndex = 0,
                explanation = "In C++, array indices are zero-based (0 to N-1). For an array of size 5, valid indices are 0, 1, 2, 3, and 4. Accessing index 5 is an off-by-one out-of-bounds access causing undefined behavior.",
                difficulty = difficulty,
                bloomsLevel = "Understanding",
                origin = QuestionOrigin.AI_GENERATED,
                marks = 2,
                sourceReference = "CSSD 101 Tony Gaddis Chapter 7 Arrays (Offline Generator)"
            )
        }
    }

    private fun generateFallbackTutorResponse(query: String, mode: TutorMode): String {
        val qLower = query.lowercase()
        return when {
            qLower.contains("vector") || qLower.contains("component") -> {
                """
### Socratic Physics Insight: Vector Components

Let's break this down methodically:

1. **Orthogonal Decomposition:** Any vector V with magnitude |V| at angle θ can be split into:
   - Horizontal component: Vx = |V| cos(θ)
   - Vertical component: Vy = |V| sin(θ)

2. **Guiding Question for You:**
   - Did your angle θ start from the positive X-axis (horizontal) or the Y-axis (vertical)?
   - If the angle is measured from the vertical, notice how the cosine and sine swap roles!

3. **Recombination Check:**
   Does √(Vx² + Vy²) equal your original magnitude |V|?

*What are the specific values of magnitude and angle you are working with?*
                """.trimIndent()
            }
            qLower.contains("acceleration") || qLower.contains("kinematic") || qLower.contains("motion") -> {
                """
### Socratic Kinematics Coach

When approaching uniform acceleration problems:

1. **List the 5 Core Variables (u, v, a, t, s):**
   - u = Initial velocity
   - v = Final velocity
   - a = Constant acceleration
   - t = Elapsed time
   - s = Displacement

2. **Select the Direct Equation:**
   - If you do NOT have time t, use: v² = u² + 2as
   - If you do NOT have final velocity v, use: s = ut + (1/2)at²
   - If you do NOT have displacement s, use: v = u + at

*Which 3 variables are explicitly given in your problem, and which one are you solving for?*
                """.trimIndent()
            }
            qLower.contains("loop") || qLower.contains("while") || qLower.contains("for") -> {
                """
### Algorithmic Logic Tutor: C++ Loops

Let's diagnose your loop logic:

1. **Pre-test (`while`, `for`) vs Post-test (`do-while`):**
   - Pre-test loops evaluate their conditional guard **before** any statement inside runs. If the condition begins as `false`, the body executes 0 times.
   - Post-test (`do-while`) executes the body first, then tests the condition.

2. **The 3 Common Loop Bugs:**
   - **Off-by-one:** Using `<` instead of `<=` or vice versa.
   - **Missing update:** Forgetting `i++` or `count += 1`, resulting in an infinite loop.
   - **Incorrect termination check:** Evaluating a variable before it has been initialized.

*Would you like to trace the values of your variables across the first 3 iterations together?*
                """.trimIndent()
            }
            qLower.contains("pac") || qLower.contains("ipo") || qLower.contains("problem") -> {
                """
### Software Problem Solving: PAC & IPO Charts

To solve university programming problems systematically:

1. **Problem Analysis Chart (PAC):**
   - **Given Data:** What inputs do you have?
   - **Required Results:** What exact outputs must be produced?
   - **Processing Requirements:** What formulas and conditions transform inputs into outputs?
   - **Alternative Solutions:** Are there multiple ways to approach the algorithm?

2. **Input-Process-Output (IPO) Chart:**
   - Maps directly to your code: Inputs $\to$ `cin`, Process $\to$ math/conditionals/loops, Output $\to$ `cout`.

*What is the problem statement you'd like to analyze?*
                """.trimIndent()
            }
            else -> {
                """
### StudyForge Academic Tutor

Welcome! I am ready to guide you through **CSPC 132 (Physics for Computing)** and **CSSD 101 (Programming and Problem Solving)**.

**How I can assist you:**
- **Step-by-step problem breakdown:** We will list givens, select formulas, and solve.
- **Conceptual understanding:** Discover *why* formulas work and how they relate to computing.
- **Code tracing:** Walk through C++ loops, conditionals, arrays, and functions.
- **Exam preparation:** Practice GCTU past papers and Harvard analytical challenges.

*Tell me which topic or specific question you are working on!*
                """.trimIndent()
            }
        }
    }
}
