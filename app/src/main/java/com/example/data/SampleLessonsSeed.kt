package com.example.data

import com.example.model.*

object SampleLessonsSeed {

    val lessonsMap: Map<String, Lesson> = mapOf(
        "phys_les_11" to Lesson(
            id = "phys_les_11",
            moduleId = "phys_mod_6",
            courseCode = "CSPC 132",
            title = "The 4 Kinematic Equations in Action",
            moduleTitle = "Uniform Accelerated Motion",
            objectives = listOf(
                "Identify when uniform acceleration assumptions apply",
                "Select the appropriate kinematic equation without unnecessary algebraic steps",
                "Solve multi-stage acceleration and deceleration problems"
            ),
            explanationText = """
# Uniform Accelerated Motion

Uniform accelerated motion describes a particle moving along a straight line with constant acceleration (a = constant). Under this physical constraint, velocity changes by equal amounts in equal intervals of time.

### The 4 Foundational Kinematic Equations:

1. **Velocity-Time Relation:**
   v = u + at
   *Relates initial velocity (u), final velocity (v), acceleration (a), and time (t). Note: displacement s is omitted.*

2. **Displacement-Time Relation:**
   s = ut + (1/2)at²
   *Calculates displacement directly when time and acceleration are known. Note: final velocity v is omitted.*

3. **Velocity-Displacement Relation (Torricelli's Equation):**
   v² = u² + 2as
   *Essential when time t is unknown or not required in the problem.*

4. **Average Velocity Displacement Relation:**
   s = ((u + v) / 2) * t
   *Calculates displacement when initial and final velocities are known, bypassing acceleration.*

### Sign Conventions:
- **Direction Matters:** Assign an explicit positive coordinate axis (e.g., upwards = positive, downwards = negative; or forward = positive).
- **Free-Fall Acceleration:** Near Earth's surface, g = 9.8 m/s² directed downward. If upward is positive, then a = -9.8 m/s².
- At maximum height of a projectile thrown vertically upwards, instantaneous velocity v = 0 m/s.
            """.trimIndent(),
            formulas = listOf(
                FormulaItem(
                    name = "First Kinematic Equation",
                    equation = "v = u + at",
                    explanation = "Linear change of velocity over time under constant acceleration.",
                    variables = mapOf("v" to "Final velocity (m/s)", "u" to "Initial velocity (m/s)", "a" to "Acceleration (m/s²)", "t" to "Elapsed time (s)")
                ),
                FormulaItem(
                    name = "Second Kinematic Equation",
                    equation = "s = ut + (1/2)at²",
                    explanation = "Integrates constant acceleration over time to compute net linear displacement.",
                    variables = mapOf("s" to "Displacement (m)", "u" to "Initial velocity (m/s)", "a" to "Acceleration (m/s²)", "t" to "Elapsed time (s)")
                ),
                FormulaItem(
                    name = "Third Kinematic Equation",
                    equation = "v² = u² + 2as",
                    explanation = "Work-energy derived relationship connecting velocity changes to spatial displacement.",
                    variables = mapOf("v" to "Final velocity (m/s)", "u" to "Initial velocity (m/s)", "a" to "Acceleration (m/s²)", "s" to "Displacement (m)")
                )
            ),
            workedExamples = listOf(
                WorkedExample(
                    problem = "A computing server cooling fan accelerates uniformly from rest to 3600 RPM (equivalent to 377 rad/s linear tip speed of 113.1 m/s) over a distance of 45.2 m along its outer perimeter. Find its acceleration and the time taken to reach full speed.",
                    given = "Initial velocity u = 0 m/s, final velocity v = 113.1 m/s, distance s = 45.2 m",
                    steps = listOf(
                        "Step 1: Identify the equation without time: v² = u² + 2as",
                        "Step 2: Rearrange for acceleration: a = (v² - u²) / (2s)",
                        "Step 3: Substitute values: a = (113.1² - 0) / (2 * 45.2) = 12791.6 / 90.4 = 141.5 m/s²",
                        "Step 4: Calculate time using v = u + at: t = (v - u) / a = 113.1 / 141.5 = 0.80 seconds"
                    ),
                    finalAnswer = "Acceleration = 141.5 m/s², Time taken = 0.80 s",
                    keyInsight = "When time is not given, always eliminate it first using v² = u² + 2as, then verify with the linear time formula."
                )
            ),
            keyTakeaways = listOf(
                "Kinematic equations are valid ONLY when acceleration is constant.",
                "Always write down the 5 variables (u, v, a, t, s) and check which 3 are known.",
                "Displacement is a vector quantity; watch out for negative signs when an object reverses direction."
            ),
            checkQuestion = CheckQuestion(
                prompt = "A high-speed optical packet transceiver decelerates uniformly from 40 m/s to 10 m/s over a distance of 150 m. What is its acceleration?",
                options = listOf(
                    "-5.0 m/s²",
                    "-3.0 m/s²",
                    "-7.5 m/s²",
                    "3.0 m/s²"
                ),
                correctIndex = 0,
                explanation = "Using v² = u² + 2as: 10² = 40² + 2*a*150 => 100 = 1600 + 300a => -1500 = 300a => a = -5.0 m/s² (negative indicates deceleration)."
            ),
            sourceReference = "Uniform Accelerated Motion.pdf — Section 2.3 & 2024 Exam Paper Q3"
        ),
        "phys_les_5" to Lesson(
            id = "phys_les_5",
            moduleId = "phys_mod_3",
            courseCode = "CSPC 132",
            title = "Orthogonal Decomposition & Direction Angles",
            moduleTitle = "Vector Components & Resolution",
            objectives = listOf(
                "Resolve an arbitrary vector into perpendicular Cartesian components",
                "Apply trigonometry correctly based on whether angle θ is with the horizontal or vertical axis",
                "Compute resultant vectors for multi-force systems in 2D space"
            ),
            explanationText = """
# Vector Resolution in 2D

In computer simulations, game physics engines, and graphics rendering, vectors are universally decomposed into orthogonal components along the Cartesian coordinate axes:
V = Vx i + Vy j

### Rectangular Decomposition:
For a vector V with magnitude |V| making an angle θ measured counterclockwise from the positive x-axis:
Vx = |V| cos(θ)
Vy = |V| sin(θ)

### Reconstructing Magnitude and Direction:
Given orthogonal components Vx and Vy:
- Magnitude:
  |V| = √(Vx² + Vy²)
- Direction Angle:
  θ = atan(|Vy| / |Vx|)
  *(Adjust for quadrant: Q1 = θ, Q2 = 180° - θ, Q3 = 180° + θ, Q4 = 360° - θ)*

### Computing Resultant of Concurrent Vectors:
To sum N arbitrary vectors F1, F2, ..., FN:
1. Compute the algebraic sum of all x-components: Rx = Σ Fix
2. Compute the algebraic sum of all y-components: Ry = Σ Fiy
3. Magnitude: R = √(Rx² + Ry²)
4. Direction: θ_R = atan2(Ry, Rx)
            """.trimIndent(),
            formulas = listOf(
                FormulaItem(
                    name = "X Component",
                    equation = "Vx = |V| * cos(θ)",
                    explanation = "Projection of vector V along the horizontal X-axis.",
                    variables = mapOf("Vx" to "Horizontal component", "|V|" to "Magnitude", "θ" to "Angle with positive X-axis")
                ),
                FormulaItem(
                    name = "Y Component",
                    equation = "Vy = |V| * sin(θ)",
                    explanation = "Projection of vector V along the vertical Y-axis.",
                    variables = mapOf("Vy" to "Vertical component", "|V|" to "Magnitude", "θ" to "Angle with positive X-axis")
                ),
                FormulaItem(
                    name = "Vector Magnitude",
                    equation = "|V| = sqrt(Vx² + Vy²)",
                    explanation = "Pythagorean reconstruction of Euclidean vector norm.",
                    variables = mapOf("|V|" to "Vector norm/magnitude", "Vx" to "X component", "Vy" to "Y component")
                )
            ),
            workedExamples = listOf(
                WorkedExample(
                    problem = "Two force vectors act at the origin: F1 = 50 N at 30° above positive x-axis, and F2 = 80 N at 135° from positive x-axis. Find the magnitude and direction of the resultant vector R.",
                    given = "F1 = 50 N, θ1 = 30°; F2 = 80 N, θ2 = 135°",
                    steps = listOf(
                        "F1x = 50 * cos(30°) = 50 * 0.8660 = +43.30 N",
                        "F1y = 50 * sin(30°) = 50 * 0.5000 = +25.00 N",
                        "F2x = 80 * cos(135°) = 80 * (-0.7071) = -56.57 N",
                        "F2y = 80 * sin(135°) = 80 * 0.7071 = +56.57 N",
                        "Rx = F1x + F2x = 43.30 - 56.57 = -13.27 N",
                        "Ry = F1y + F2y = 25.00 + 56.57 = +81.57 N",
                        "R = sqrt((-13.27)² + (81.57)²) = sqrt(176.09 + 6653.66) = sqrt(6829.75) = 82.64 N",
                        "Direction: Rx < 0, Ry > 0 (Quadrant 2). θ = 180° - atan(81.57 / 13.27) = 180° - 80.76° = 99.24°"
                    ),
                    finalAnswer = "Resultant R = 82.64 N at 99.24° from positive x-axis",
                    keyInsight = "Never add vector magnitudes directly (50 + 80 ≠ 130). Decompose into orthogonal axes first, sum each axis algebraically, then recombine."
                )
            ),
            keyTakeaways = listOf(
                "Orthogonal components are independent and can be treated as scalar values along each axis.",
                "Always check the trigonometric quadrant of the resultant using both component signs."
            ),
            checkQuestion = CheckQuestion(
                prompt = "A force of 100 N acts at an angle of 60° to the horizontal. What is its horizontal component?",
                options = listOf(
                    "50 N",
                    "86.6 N",
                    "100 N",
                    "70.7 N"
                ),
                correctIndex = 0,
                explanation = "Fx = F * cos(60°) = 100 * 0.5 = 50 N."
            ),
            sourceReference = "Vectors — Level 100.pdf & GCTU Past Paper 2024 Section B Q1"
        ),
        "prog_les_7" to Lesson(
            id = "prog_les_7",
            moduleId = "prog_mod_4",
            courseCode = "CSSD 101",
            title = "IPO Chart Architecture & Variable Mapping",
            moduleTitle = "Input-Process-Output (IPO) Charts",
            objectives = listOf(
                "Explain the role of IPO charts in bridging user requirements and computer code",
                "Construct standardized 3-column IPO tables with exact data types",
                "Translate an IPO chart directly into pseudocode and C++ functions"
            ),
            explanationText = """
# Input-Process-Output (IPO) Charts

In structured programming methodology, the **IPO Chart** is an indispensable software engineering tool. It acts as the blueprint separating data acquisition (Input), computational transformation (Process), and result delivery (Output).

### Anatomy of an IPO Chart:
1. **Input Column:** Lists all external data elements that must be provided by the user, files, or sensors to solve the problem.
2. **Process Column:** Enumerates step-by-step algorithms, mathematical formulas, and logical decisions required to convert inputs to outputs.
3. **Output Column:** Specifies the final results, reports, or data artifacts required by the user or calling system.

### The 4-Step Engineering Workflow:
1. **Understand Problem:** Extract nouns (variables) and verbs (actions/processes).
2. **Populate Output First:** What does the stakeholder actually need?
3. **Identify Input:** What raw parameters are needed to produce that output?
4. **Detail Process:** Write formulas, validations, loops, and conditions in clear imperative steps.
            """.trimIndent(),
            codeSnippet = """
// C++ Implementation of an IPO Chart for Salary Computation
#include <iostream>
#include <iomanip>
using namespace std;

int main() {
    // 1. INPUT variables
    double hoursWorked, hourlyRate;
    cout << "Enter hours worked: ";
    cin >> hoursWorked;
    cout << "Enter hourly pay rate: ";
    cin >> hourlyRate;

    // 2. PROCESS calculation with overtime logic
    double regularPay = 0.0, overtimePay = 0.0, grossPay = 0.0;
    if (hoursWorked <= 40.0) {
        regularPay = hoursWorked * hourlyRate;
    } else {
        regularPay = 40.0 * hourlyRate;
        double overtimeHours = hoursWorked - 40.0;
        overtimePay = overtimeHours * (hourlyRate * 1.5);
    }
    grossPay = regularPay + overtimePay;

    // 3. OUTPUT presentation
    cout << fixed << setprecision(2);
    cout << "--- Payroll Summary ---" << endl;
    cout << "Regular Pay:  $" << regularPay << endl;
    cout << "Overtime Pay: $" << overtimePay << endl;
    cout << "Gross Pay:    $" << grossPay << endl;

    return 0;
}
            """.trimIndent(),
            codeLanguage = "cpp",
            workedExamples = listOf(
                WorkedExample(
                    problem = "Design an IPO Chart to calculate the total resistance and current for 3 parallel resistors connected to a 12V DC power source.",
                    given = "Resistances R1, R2, R3 (ohms), Voltage V = 12.0 V",
                    steps = listOf(
                        "INPUT: R1, R2, R3 (positive floating point numbers)",
                        "PROCESS: (1) Verify R1, R2, R3 > 0. (2) Calculate equivalent resistance Req: 1/Req = 1/R1 + 1/R2 + 1/R3 => Req = 1 / ((1/R1) + (1/R2) + (1/R3)). (3) Calculate total current: I_total = V / Req.",
                        "OUTPUT: Equivalent Resistance Req, Total Current I_total"
                    ),
                    finalAnswer = "IPO Table fully mapped with input validation, parallel equivalent formula, and Ohm's law.",
                    keyInsight = "Always validate inputs in the Process section to guard against division by zero (e.g., R <= 0)."
                )
            ),
            keyTakeaways = listOf(
                "An IPO chart ensures you never write code without knowing exact inputs and required outputs.",
                "Process steps map 1-to-1 with sequential statements, conditionals, and loops."
            ),
            checkQuestion = CheckQuestion(
                prompt = "In an IPO Chart designed to compute student grade point averages (GPA), where does the grading scale threshold (e.g., A >= 80) belong?",
                options = listOf(
                    "Process Column",
                    "Input Column",
                    "Output Column",
                    "Header Metadata"
                ),
                correctIndex = 0,
                explanation = "Evaluation thresholds and decision logic belong in the Process column because they dictate how inputs are transformed into outputs."
            ),
            sourceReference = "CSSD 101 Programming and Problem Solving — Planning Your Solutions Chapter 2"
        ),
        "prog_les_19" to Lesson(
            id = "prog_les_19",
            moduleId = "prog_mod_10",
            courseCode = "CSSD 101",
            title = "While, Do-While, and For Loop Semantics",
            moduleTitle = "Loops and Repetition Structures",
            objectives = listOf(
                "Compare pre-test (while, for) vs post-test (do-while) loop semantics",
                "Identify common loop pitfalls: off-by-one errors and infinite loops",
                "Trace iteration variables using dry-run memory trace tables"
            ),
            explanationText = """
# Repetition Structures in C++

Loops allow a block of statements to be executed repeatedly until a specific Boolean termination condition evaluates to `false`.

### The Three Loop Constructs in C++:

1. **The `while` Loop (Pre-Test Loop):**
   - Tests the conditional expression **before** executing the loop body.
   - If the condition is false on first evaluation, the body is never executed (zero or more times).
   ```cpp
   while (condition) {
       // Loop body
       // Update statement
   }
   ```

2. **The `do-while` Loop (Post-Test Loop):**
   - Executes the loop body **first**, then evaluates the condition at the end.
   - Guaranteed to execute **at least once**.
   ```cpp
   do {
       // Loop body
   } while (condition); // Notice semicolon!
   ```

3. **The `for` Loop (Count-Controlled Pre-Test Loop):**
   - Combines initialization, test condition, and update step in a single header:
   ```cpp
   for (initialization; test; update) {
       // Body
   }
   ```

### Execution Order of `for (init; test; update)`:
1. `init` executes once.
2. `test` condition is evaluated. If `false`, terminate loop immediately.
3. If `true`, the loop body executes.
4. `update` statement runs.
5. Jump back to Step 2.
            """.trimIndent(),
            codeSnippet = """
// Demonstration of Pre-Test vs Post-Test Loop Behavior
#include <iostream>
using namespace std;

int main() {
    int counter = 10;

    cout << "Testing while loop with counter = 10:" << endl;
    while (counter < 5) {
        cout << "Inside while loop" << endl; // Never executed!
    }

    cout << "Testing do-while loop with counter = 10:" << endl;
    do {
        cout << "Inside do-while loop (runs at least once)" << endl;
    } while (counter < 5);

    return 0;
}
            """.trimIndent(),
            codeLanguage = "cpp",
            workedExamples = listOf(
                WorkedExample(
                    problem = "Trace the output of this C++ loop:\nint sum = 0;\nfor (int i = 1; i <= 5; i += 2) {\n    sum += i * i;\n}\ncout << sum;",
                    given = "i starts at 1, increments by 2, continues while i <= 5",
                    steps = listOf(
                        "Iteration 1: i = 1. Condition 1 <= 5 is true. sum = 0 + (1*1) = 1. Update: i becomes 3.",
                        "Iteration 2: i = 3. Condition 3 <= 5 is true. sum = 1 + (3*3) = 10. Update: i becomes 5.",
                        "Iteration 3: i = 5. Condition 5 <= 5 is true. sum = 10 + (5*5) = 35. Update: i becomes 7.",
                        "Iteration 4: i = 7. Condition 7 <= 5 is false. Loop terminates.",
                        "Output: 35"
                    ),
                    finalAnswer = "35",
                    keyInsight = "Always maintain a trace table tracking iteration number, variable values at start of loop, body execution, and variable values after update."
                )
            ),
            keyTakeaways = listOf(
                "Use a `for` loop when the number of iterations is known in advance.",
                "Use a `while` loop when looping depends on an arbitrary condition or sentinel value.",
                "Use a `do-while` loop when the code must execute at least once (e.g., user input validation menus)."
            ),
            checkQuestion = CheckQuestion(
                prompt = "How many times will this loop execute?\nint x = 5;\ndo {\n    x *= 2;\n} while (x < 40);",
                options = listOf(
                    "3 times",
                    "4 times",
                    "2 times",
                    "Infinite loop"
                ),
                correctIndex = 0,
                explanation = "Start x=5. (1) x becomes 10, 10 < 40 is true. (2) x becomes 20, 20 < 40 is true. (3) x becomes 40, 40 < 40 is false -> terminates! Total runs = 3."
            ),
            sourceReference = "Tony Gaddis — Starting Out with C++ 9th Edition Chapter 5"
        )
    )
}
