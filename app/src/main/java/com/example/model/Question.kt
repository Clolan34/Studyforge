package com.example.model

enum class QuestionType {
    MULTIPLE_CHOICE,
    NUMERICAL_PROBLEM,
    TRUE_OR_FALSE,
    CODE_OUTPUT_PREDICTION,
    DEBUGGING_EXERCISE,
    SHORT_ANSWER,
    ESSAY_EXPLANATION
}

enum class DifficultyLevel(val label: String, val levelNumber: Int) {
    LEVEL_1("Level 1 — Fundamental Knowledge", 1),
    LEVEL_2("Level 2 — Conceptual Understanding", 2),
    LEVEL_3("Level 3 — Applied Problem Solving", 3),
    LEVEL_4("Level 4 — Advanced Reasoning", 4)
}

enum class QuestionOrigin(val label: String) {
    ORIGINAL_PAST_QUESTION("Original Past Question"),
    ADAPTED_PAST_QUESTION("Adapted Past Question"),
    AI_GENERATED("AI-Generated Practice Question")
}

data class Question(
    val id: String,
    val courseCode: String,
    val topic: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val correctOptionIndex: Int = -1,
    val numericalAnswer: Double? = null,
    val numericalTolerance: Double = 0.05,
    val codeSnippet: String? = null,
    val explanation: String,
    val steps: List<String> = emptyList(),
    val difficulty: DifficultyLevel,
    val bloomsLevel: String, // Remembering, Understanding, Applying, Analyzing, Evaluating
    val origin: QuestionOrigin,
    val marks: Int = 2,
    val sourceReference: String? = null
)

data class UserQuestionResponse(
    val questionId: String,
    val selectedOptionIndex: Int? = null,
    val enteredTextAnswer: String = "",
    val isFlagged: Boolean = false,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean? = null,
    val provisionalAiFeedback: String? = null
)
