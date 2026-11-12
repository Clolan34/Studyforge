package com.example.model

data class Course(
    val id: String,
    val code: String,
    val title: String,
    val description: String,
    val level: String,
    val totalModules: Int,
    val completedLessons: Int,
    val totalLessons: Int,
    val colorHex: Long,
    val icon: String,
    val modules: List<CourseModule> = emptyList()
)

data class CourseModule(
    val id: String,
    val courseId: String,
    val number: Int,
    val title: String,
    val isSourceSupported: Boolean,
    val sourceName: String? = null,
    val objectives: List<String>,
    val lessons: List<LessonSummary>
)

data class LessonSummary(
    val id: String,
    val moduleId: String,
    val title: String,
    val estimatedMinutes: Int,
    val isCompleted: Boolean = false
)
