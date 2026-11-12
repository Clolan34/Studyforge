package com.example.model

data class Lesson(
    val id: String,
    val moduleId: String,
    val courseCode: String,
    val title: String,
    val moduleTitle: String,
    val objectives: List<String>,
    val explanationText: String,
    val formulas: List<FormulaItem> = emptyList(),
    val workedExamples: List<WorkedExample> = emptyList(),
    val codeSnippet: String? = null,
    val codeLanguage: String? = null,
    val keyTakeaways: List<String> = emptyList(),
    val checkQuestion: CheckQuestion,
    val sourceReference: String? = null
)

data class FormulaItem(
    val name: String,
    val equation: String,
    val explanation: String,
    val variables: Map<String, String>
)

data class WorkedExample(
    val problem: String,
    val given: String,
    val steps: List<String>,
    val finalAnswer: String,
    val keyInsight: String
)

data class CheckQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
