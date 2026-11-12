package com.example.data

import com.example.model.*

object SampleQuestionsSeed {

    val questions: List<Question> = listOf(
        // === CSPC 132: Physics for Computing ===
        Question(
            id = "q_phys_1",
            courseCode = "CSPC 132",
            topic = "Uniform Accelerated Motion",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "An electronic particle inside an electrostatic accelerator starts from rest and achieves a velocity of 6.0 × 10⁶ m/s over a distance of 0.05 m. Assuming uniform acceleration, calculate the acceleration of the particle.",
            options = listOf(
                "3.6 × 10¹⁴ m/s²",
                "1.8 × 10¹⁴ m/s²",
                "7.2 × 10¹⁴ m/s²",
                "6.0 × 10¹² m/s²"
            ),
            correctAnswer = "3.6 × 10¹⁴ m/s²",
            correctOptionIndex = 0,
            numericalAnswer = 3.6e14,
            explanation = "Using Torricelli's equation v² = u² + 2as: (6.0 × 10⁶)² = 0² + 2 * a * 0.05 => 36 × 10¹² = 0.10 * a => a = (36 × 10¹²) / 0.10 = 360 × 10¹² = 3.6 × 10¹⁴ m/s².",
            steps = listOf(
                "Identify equation without time: v² = u² + 2as",
                "Rearrange: a = (v² - u²) / (2s)",
                "Substitute: a = (3.6 × 10¹³) / (0.1) = 3.6 × 10¹⁴ m/s²"
            ),
            difficulty = DifficultyLevel.LEVEL_3,
            bloomsLevel = "Applying",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 3,
            sourceReference = "GCTU CSPC 132 2024 Exam — Section A, Question 4"
        ),
        Question(
            id = "q_phys_2",
            courseCode = "CSPC 132",
            topic = "Scalars and Vectors",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Two displacement vectors in a computer graphics coordinate space are given by A = 4i - 3j + 2k and B = -2i + 5j + k. What is the scalar (dot) product A · B?",
            options = listOf(
                "-21",
                "-25",
                "+15",
                "-17"
            ),
            correctAnswer = "-21",
            correctOptionIndex = 0,
            numericalAnswer = -21.0,
            explanation = "The scalar dot product A · B = (Ax * Bx) + (Ay * By) + (Az * Bz) = (4 * -2) + (-3 * 5) + (2 * 1) = -8 - 15 + 2 = -21.",
            steps = listOf(
                "Multiply x components: 4 * (-2) = -8",
                "Multiply y components: -3 * 5 = -15",
                "Multiply z components: 2 * 1 = 2",
                "Sum components: -8 - 15 + 2 = -21"
            ),
            difficulty = DifficultyLevel.LEVEL_2,
            bloomsLevel = "Applying",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 2,
            sourceReference = "Vectors — Level 100 & GCTU Past Paper 2024 Section A"
        ),
        Question(
            id = "q_phys_3",
            courseCode = "CSPC 132",
            topic = "Velocity-Time Graphs",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "In a velocity-time graph representing a robotic arm assembly, what physical quantity does the area under the curve between two time instants represent?",
            options = listOf(
                "Displacement",
                "Instantaneous acceleration",
                "Kinetic energy",
                "Jerk (rate of change of acceleration)"
            ),
            correctAnswer = "Displacement",
            correctOptionIndex = 0,
            explanation = "Because velocity is the time derivative of position (v = ds/dt), the integral of velocity with respect to time ∫ v dt equals the net displacement s. Therefore, the geometric area under a v-t curve represents displacement.",
            difficulty = DifficultyLevel.LEVEL_1,
            bloomsLevel = "Remembering",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 2,
            sourceReference = "Uniform Accelerated Motion & 2024 Exam Paper Q7"
        ),
        Question(
            id = "q_phys_4",
            courseCode = "CSPC 132",
            topic = "Vector Components & Resolution",
            type = QuestionType.NUMERICAL_PROBLEM,
            prompt = "A drone experience two horizontal wind forces: F1 = 120 N directed due East (0°), and F2 = 90 N directed due North (90°). Calculate the magnitude of the resultant force acting on the drone in Newtons.",
            correctAnswer = "150 N",
            correctOptionIndex = -1,
            numericalAnswer = 150.0,
            numericalTolerance = 0.5,
            explanation = "Since the two forces are strictly orthogonal (East and North form a 90° angle), the resultant magnitude R is given by Pythagoras' theorem: R = √(Fx² + Fy²) = √(120² + 90²) = √(14400 + 8100) = √22500 = 150 N.",
            steps = listOf(
                "Identify orthogonal axes: Fx = 120 N, Fy = 90 N",
                "Apply Euclidean norm: R = √(120² + 90²)",
                "Evaluate square roots: √22500 = 150 N"
            ),
            difficulty = DifficultyLevel.LEVEL_2,
            bloomsLevel = "Applying",
            origin = QuestionOrigin.ADAPTED_PAST_QUESTION,
            marks = 3,
            sourceReference = "Vectors Level 100.pdf — Exercise 4"
        ),
        Question(
            id = "q_phys_5",
            courseCode = "CSPC 132",
            topic = "Uniform Accelerated Motion",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "A high-speed sensor module is dropped from rest into a vertical test chamber of height 78.4 m. Taking g = 9.8 m/s² and neglecting air drag, how long does it take to reach the floor?",
            options = listOf(
                "4.0 seconds",
                "2.8 seconds",
                "8.0 seconds",
                "5.6 seconds"
            ),
            correctAnswer = "4.0 seconds",
            correctOptionIndex = 0,
            numericalAnswer = 4.0,
            explanation = "Using s = ut + (1/2)gt². Since initial velocity u = 0, 78.4 = 0 + (1/2)(9.8)t² => 78.4 = 4.9 t² => t² = 78.4 / 4.9 = 16 => t = √16 = 4.0 s.",
            difficulty = DifficultyLevel.LEVEL_3,
            bloomsLevel = "Applying",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 3,
            sourceReference = "Uniform Accelerated Motion — GCTU Physics Section B"
        ),
        Question(
            id = "q_phys_6",
            courseCode = "CSPC 132",
            topic = "Work, Energy, and Power",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "An autonomous electric rover of mass 250 kg accelerates from 4 m/s to 12 m/s on a flat surface. By the Work-Energy Theorem, what is the net work done on the rover?",
            options = listOf(
                "16,000 J (16 kJ)",
                "8,000 J (8 kJ)",
                "32,000 J (32 kJ)",
                "20,000 J (20 kJ)"
            ),
            correctAnswer = "16,000 J (16 kJ)",
            correctOptionIndex = 0,
            numericalAnswer = 16000.0,
            explanation = "Work done W_net = ΔKE = (1/2)m(v² - u²) = (1/2) * 250 * (12² - 4²) = 125 * (144 - 16) = 125 * 128 = 16,000 J = 16 kJ.",
            difficulty = DifficultyLevel.LEVEL_3,
            bloomsLevel = "Applying",
            origin = QuestionOrigin.ADAPTED_PAST_QUESTION,
            marks = 3,
            sourceReference = "Physics Past Questions — Energy Module"
        ),
        Question(
            id = "q_phys_7",
            courseCode = "CSPC 132",
            topic = "Newton's Laws & Dynamics",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "Harvard Analytical Challenge: A box of mass m sits on a ramp inclined at angle θ with the horizontal. The coefficient of static friction is μs. What is the maximum angle θ_max before the box begins to slide down without an external push?",
            options = listOf(
                "θ_max = tan⁻¹(μs)",
                "θ_max = sin⁻¹(μs)",
                "θ_max = cos⁻¹(μs)",
                "θ_max = μs * 90°"
            ),
            correctAnswer = "θ_max = tan⁻¹(μs)",
            correctOptionIndex = 0,
            explanation = "At impending motion, downhill gravitational component equals maximum static friction: mg sin θ = fs,max. Since fs,max = μs N and normal force N = mg cos θ, we have mg sin θ = μs mg cos θ => sin θ / cos θ = μs => tan θ = μs => θ = tan⁻¹(μs). This is the classic angle of repose.",
            difficulty = DifficultyLevel.LEVEL_4,
            bloomsLevel = "Analyzing",
            origin = QuestionOrigin.AI_GENERATED,
            marks = 4,
            sourceReference = "Harvard Physics Mechanics Challenge Series & CSPC 132 Section B"
        ),

        // === CSSD 101: Programming & Problem Solving ===
        Question(
            id = "q_prog_1",
            courseCode = "CSSD 101",
            topic = "Problem Analysis Charts (PAC)",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "In the 4-quadrant Problem Analysis Chart (PAC) model, which quadrant contains the list of possible computational procedures, mathematical formulas, and algorithmic pathways to solve the problem?",
            options = listOf(
                "Processing Requirements",
                "Given Data",
                "Required Results",
                "Alternative Solutions"
            ),
            correctAnswer = "Processing Requirements",
            correctOptionIndex = 0,
            explanation = "The PAC is partitioned into 4 quadrants: (1) Given Data, (2) Required Results, (3) Processing Requirements, and (4) Alternative Solutions. The Processing Requirements quadrant outlines the exact equations, actions, and validation logic.",
            difficulty = DifficultyLevel.LEVEL_1,
            bloomsLevel = "Remembering",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 2,
            sourceReference = "CSSD 101 Planning Your Solutions — Chapter 1, Section 2"
        ),
        Question(
            id = "q_prog_2",
            courseCode = "CSSD 101",
            topic = "Input-Process-Output (IPO) Charts",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "When designing an IPO chart for a university GPA calculation system, where do student course credit hours and letter grade inputs belong?",
            options = listOf(
                "Input Column",
                "Process Column",
                "Output Column",
                "Module Metadata"
            ),
            correctAnswer = "Input Column",
            correctOptionIndex = 0,
            explanation = "Credit hours and grades are supplied from the academic transcript database or user entry, making them fundamental inputs required by the process to produce the cumulative GPA output.",
            difficulty = DifficultyLevel.LEVEL_1,
            bloomsLevel = "Understanding",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 2,
            sourceReference = "CSSD 101 Planning Your Solutions — Chapter 2"
        ),
        Question(
            id = "q_prog_3",
            courseCode = "CSSD 101",
            topic = "Loops and Repetition Structures",
            type = QuestionType.CODE_OUTPUT_PREDICTION,
            prompt = "What will be printed to the console when the following C++ code fragment is executed?",
            codeSnippet = """
#include <iostream>
using namespace std;

int main() {
    int total = 0;
    for (int k = 1; k <= 7; k += 2) {
        if (k == 5) continue;
        total += k;
    }
    cout << total << endl;
    return 0;
}
            """.trimIndent(),
            options = listOf(
                "11",
                "16",
                "10",
                "8"
            ),
            correctAnswer = "11",
            correctOptionIndex = 0,
            explanation = "Trace k values: k=1 (adds 1, total=1). k=3 (adds 3, total=4). k=5 (the `continue` statement skips the rest of the loop body!). k=7 (adds 7, total=11). Next k=9, which exceeds 7, loop exits. Final output is 11.",
            steps = listOf(
                "k=1: total = 0 + 1 = 1",
                "k=3: total = 1 + 3 = 4",
                "k=5: continue executes, skip total addition",
                "k=7: total = 4 + 7 = 11",
                "Loop ends; output = 11"
            ),
            difficulty = DifficultyLevel.LEVEL_3,
            bloomsLevel = "Analyzing",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 3,
            sourceReference = "Tony Gaddis — Starting Out with C++ 9th Edition Chapter 5 Test Bank"
        ),
        Question(
            id = "q_prog_4",
            courseCode = "CSSD 101",
            topic = "Conditional Statements",
            type = QuestionType.DEBUGGING_EXERCISE,
            prompt = "A junior programmer wrote the following C++ conditional to check if an examination score qualifies for Honors (score between 85 and 100 inclusive):\nif (85 <= score <= 100) {\n    cout << \"Honors\";\n}\nWhy does this code produce a subtle logical bug in C++?",
            options = listOf(
                "Chained comparison `85 <= score <= 100` evaluates as `(85 <= score) <= 100`, comparing a boolean (0 or 1) with 100, which is always true!",
                "C++ does not support the `<= ` operator inside an `if` condition.",
                "The score variable must be cast to a float before relational comparisons.",
                "The code will cause a fatal compiler syntax error and cannot build."
            ),
            correctAnswer = "Chained comparison `85 <= score <= 100` evaluates as `(85 <= score) <= 100`, comparing a boolean (0 or 1) with 100, which is always true!",
            correctOptionIndex = 0,
            explanation = "In C++, relational operators have left-to-right associativity. `85 <= score` evaluates to either `true` (1) or `false` (0). Then `1 <= 100` or `0 <= 100` is evaluated, which is ALWAYS true! The correct syntax is: `if (score >= 85 && score <= 100)`.",
            difficulty = DifficultyLevel.LEVEL_4,
            bloomsLevel = "Evaluating",
            origin = QuestionOrigin.ADAPTED_PAST_QUESTION,
            marks = 4,
            sourceReference = "Tony Gaddis Chapter 4 Common Programming Errors & Pitfalls"
        ),
        Question(
            id = "q_prog_5",
            courseCode = "CSSD 101",
            topic = "Flowcharts and Visual Logic",
            type = QuestionType.MULTIPLE_CHOICE,
            prompt = "In standard ANSI flowchart modeling, which geometric symbol is universally designated for decision and condition branching?",
            options = listOf(
                "Diamond",
                "Parallelogram",
                "Rectangle",
                "Oval (Rounded Rectangle)"
            ),
            correctAnswer = "Diamond",
            correctOptionIndex = 0,
            explanation = "In ANSI flowcharts: Diamond = Decision / Conditional branch; Parallelogram = Input / Output; Rectangle = Process / Computation; Oval / Rounded rectangle = Terminal (Start / End).",
            difficulty = DifficultyLevel.LEVEL_1,
            bloomsLevel = "Remembering",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 2,
            sourceReference = "CSSD 101 Planning Your Solutions — Chapter 3"
        ),
        Question(
            id = "q_prog_6",
            courseCode = "CSSD 101",
            topic = "Functions and Modular Design",
            type = QuestionType.CODE_OUTPUT_PREDICTION,
            prompt = "Trace the following C++ function call. What is the value of `val` after `modify(val)` completes?",
            codeSnippet = """
#include <iostream>
using namespace std;

void modify(int x) {
    x = x * 3 + 4;
}

int main() {
    int val = 5;
    modify(val);
    cout << val;
    return 0;
}
            """.trimIndent(),
            options = listOf(
                "5 (pass-by-value makes a local copy; original is unchanged)",
                "19",
                "15",
                "9"
            ),
            correctAnswer = "5 (pass-by-value makes a local copy; original is unchanged)",
            correctOptionIndex = 0,
            explanation = "The function parameter `x` is passed by value (no ampersand `&`). Therefore, `x` receives a copy of `val`. Modifying `x` inside `modify()` alters only the stack frame of that function. `val` in `main()` remains 5.",
            difficulty = DifficultyLevel.LEVEL_3,
            bloomsLevel = "Analyzing",
            origin = QuestionOrigin.ORIGINAL_PAST_QUESTION,
            marks = 3,
            sourceReference = "Tony Gaddis Chapter 6 Functions — Pass by Value vs Reference"
        )
    )
}
