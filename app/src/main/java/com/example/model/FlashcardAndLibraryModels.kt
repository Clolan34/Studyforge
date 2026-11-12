package com.example.model

enum class FlashcardCategory {
    FORMULA,
    DEFINITION,
    CONCEPT,
    CODE_SYNTAX
}

enum class ReviewRating {
    AGAIN, // Reset interval
    GOOD,  // Standard progression
    EASY   // Long jump
}

data class Flashcard(
    val id: String,
    val courseCode: String,
    val topic: String,
    val category: FlashcardCategory,
    val front: String,
    val back: String,
    val hint: String? = null,
    val formula: String? = null,
    val repetitions: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Double = 2.5,
    val nextReviewDate: Long = System.currentTimeMillis()
)

data class StudyTask(
    val id: String,
    val title: String,
    val courseCode: String,
    val topic: String,
    val type: String, // "LESSON", "PRACTICE", "LAB", "REVISION"
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val priority: String = "HIGH" // HIGH, MEDIUM, LOW
)

enum class DocumentStatus {
    READY,
    PROCESSING,
    NEEDS_REVIEW,
    FAILED
}

data class StudyDocument(
    val id: String,
    val title: String,
    val courseCode: String,
    val fileType: String,
    val uploadedDate: String,
    val pageCount: Int,
    val status: DocumentStatus,
    val extractedTopicsCount: Int,
    val summary: String,
    val keyFormulasExtracted: Int
)
