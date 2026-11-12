package com.example.model

enum class ExamMode(val title: String, val subtitle: String) {
    QUICK_PRACTICE("Quick Practice", "10 rapid-fire questions across all topics"),
    TOPIC_PRACTICE("Topic Deep Dive", "Targeted practice for a specific curriculum unit"),
    PAST_QUESTIONS("Past Exam Archive", "Verified university past examination questions"),
    GCTU_SIMULATION("GCTU Physics 2024 Exam", "Official format: Section A MCQs & Section B Structured (2h 30m)"),
    HARVARD_CHALLENGE("Harvard Reasoning Challenge", "Level 4 multi-step analytical and algorithmic reasoning"),
    TIMED_MOCK("Custom Timed Mock", "Configurable full-scale examination simulation")
}

data class ExamConfiguration(
    val mode: ExamMode,
    val courseCode: String,
    val topic: String? = null,
    val difficulty: DifficultyLevel? = null,
    val questionCount: Int = 10,
    val timeLimitMinutes: Int = 30,
    val instantFeedback: Boolean = false,
    val calculatorAllowed: Boolean = true
)

data class ExamResult(
    val id: String,
    val examTitle: String,
    val courseCode: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unansweredCount: Int,
    val totalMarks: Int,
    val earnedMarks: Int,
    val percentage: Double,
    val timeTakenSeconds: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val topicAccuracy: Map<String, Double> = emptyMap(),
    val difficultyAccuracy: Map<DifficultyLevel, Double> = emptyMap(),
    val weakTopics: List<String> = emptyList(),
    val revisionPlan: List<String> = emptyList()
)
