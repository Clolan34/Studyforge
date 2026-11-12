package com.example.data

import com.example.model.*

object SampleDataSeed {

    val courses: List<Course> = listOf(
        Course(
            id = "course_physics",
            code = "CSPC 132",
            title = "Physics for Computing",
            description = "Fundamental physical principles applied to computing systems, mechanics, vectors, uniform accelerated motion, energy, and electrostatics.",
            level = "Level 100 / University 1st Year",
            totalModules = 12,
            completedLessons = 4,
            totalLessons = 24,
            colorHex = 0xFF3B82F6,
            icon = "physics"
        ),
        Course(
            id = "course_programming",
            code = "CSSD 101",
            title = "Programming & Problem Solving",
            description = "Algorithmic thinking, Problem Analysis Charts (PAC), Input-Process-Output (IPO) charts, flowcharts, pseudocode, and C++ control structures.",
            level = "Level 100 / University 1st Year",
            totalModules = 12,
            completedLessons = 6,
            totalLessons = 24,
            colorHex = 0xFF10B981,
            icon = "code"
        )
    )

    val physicsModules: List<CourseModule> = listOf(
        CourseModule(
            id = "phys_mod_1",
            courseId = "course_physics",
            number = 1,
            title = "Physical Quantities and Units",
            isSourceSupported = true,
            sourceName = "CSPC 132 Lecture Notes",
            objectives = listOf(
                "Distinguish fundamental vs derived quantities",
                "Apply dimensional analysis to verify physical formulas",
                "Work with SI base units and scientific notation"
            ),
            lessons = listOf(
                LessonSummary("phys_les_1", "phys_mod_1", "Base Units & Dimensional Equations", 15, true),
                LessonSummary("phys_les_2", "phys_mod_1", "Measurement Errors & Significant Figures", 12, true)
            )
        ),
        CourseModule(
            id = "phys_mod_2",
            courseId = "course_physics",
            number = 2,
            title = "Scalars and Vectors",
            isSourceSupported = true,
            sourceName = "Vectors Level 100.pdf",
            objectives = listOf(
                "Differentiate scalar and vector quantities",
                "Represent vectors graphically and geometrically",
                "Perform graphical vector addition and subtraction"
            ),
            lessons = listOf(
                LessonSummary("phys_les_3", "phys_mod_2", "Vector Properties & Geometric Addition", 18, true),
                LessonSummary("phys_les_4", "phys_mod_2", "Unit Vectors and Cartesian Representation", 15, true)
            )
        ),
        CourseModule(
            id = "phys_mod_3",
            courseId = "course_physics",
            number = 3,
            title = "Vector Components & Resolution",
            isSourceSupported = true,
            sourceName = "Vectors Level 100.pdf",
            objectives = listOf(
                "Resolve vectors into rectangular components (Vx = V cosθ, Vy = V sinθ)",
                "Calculate magnitude and direction angle from orthogonal components",
                "Compute resultant of multiple concurrent vectors"
            ),
            lessons = listOf(
                LessonSummary("phys_les_5", "phys_mod_3", "Orthogonal Decomposition & Direction Angles", 20, false),
                LessonSummary("phys_les_6", "phys_mod_3", "Resultant Vectors of Multi-Force Systems", 25, false)
            )
        ),
        CourseModule(
            id = "phys_mod_4",
            courseId = "course_physics",
            number = 4,
            title = "Dot Product and Cross Product",
            isSourceSupported = true,
            sourceName = "Physics for Computing 2024 Exam",
            objectives = listOf(
                "Evaluate scalar dot product A · B = |A||B| cos θ",
                "Compute vector cross product A × B via determinants",
                "Apply dot product to work calculation and computer graphics"
            ),
            lessons = listOf(
                LessonSummary("phys_les_7", "phys_mod_4", "Scalar Dot Product & Orthogonality Test", 20, false),
                LessonSummary("phys_les_8", "phys_mod_4", "Vector Cross Product & Area Calculations", 22, false)
            )
        ),
        CourseModule(
            id = "phys_mod_5",
            courseId = "course_physics",
            number = 5,
            title = "Motion and Kinematics",
            isSourceSupported = true,
            sourceName = "Uniform Accelerated Motion.pdf",
            objectives = listOf(
                "Differentiate distance vs displacement, speed vs velocity",
                "Calculate instantaneous vs average velocity and acceleration",
                "Construct and interpret motion graphs"
            ),
            lessons = listOf(
                LessonSummary("phys_les_9", "phys_mod_5", "1D Motion & Kinematic Variables", 18, false),
                LessonSummary("phys_les_10", "phys_mod_5", "Rate of Change & Acceleration Calculus", 20, false)
            )
        ),
        CourseModule(
            id = "phys_mod_6",
            courseId = "course_physics",
            number = 6,
            title = "Uniform Accelerated Motion",
            isSourceSupported = true,
            sourceName = "Uniform Accelerated Motion.pdf",
            objectives = listOf(
                "Derive and apply the 4 kinematic equations",
                "Solve vertical free-fall under gravity (g = 9.8 m/s²)",
                "Analyze multi-stage motion problems"
            ),
            lessons = listOf(
                LessonSummary("phys_les_11", "phys_mod_6", "The 4 Kinematic Equations in Action", 25, false),
                LessonSummary("phys_les_12", "phys_mod_6", "Free Fall & Vertical Projectiles", 20, false)
            )
        ),
        CourseModule(
            id = "phys_mod_7",
            courseId = "course_physics",
            number = 7,
            title = "Velocity-Time Graphs",
            isSourceSupported = true,
            sourceName = "Physics Past Questions",
            objectives = listOf(
                "Determine acceleration from slope (gradient = dv/dt)",
                "Calculate displacement from area under v-t graph",
                "Analyze trapezoidal and triangular motion profiles"
            ),
            lessons = listOf(
                LessonSummary("phys_les_13", "phys_mod_7", "Slope as Acceleration & Curve Integration", 20, false),
                LessonSummary("phys_les_14", "phys_mod_7", "Complex Multi-Stage Velocity Profiles", 22, false)
            )
        ),
        CourseModule(
            id = "phys_mod_8",
            courseId = "course_physics",
            number = 8,
            title = "Newton's Laws & Dynamics",
            isSourceSupported = true,
            sourceName = "CSPC 132 Physics Trail Questions",
            objectives = listOf(
                "Apply Newton's three laws to particles and systems",
                "Draw accurate Free Body Diagrams (FBD)",
                "Compute friction, normal force, and tension"
            ),
            lessons = listOf(
                LessonSummary("phys_les_15", "phys_mod_8", "Newton's Laws and Free Body Diagrams", 25, false),
                LessonSummary("phys_les_16", "phys_mod_8", "Frictional Forces & Inclined Planes", 25, false)
            )
        ),
        CourseModule(
            id = "phys_mod_9",
            courseId = "course_physics",
            number = 9,
            title = "Work, Energy, and Power",
            isSourceSupported = true,
            sourceName = "Physics Past Questions",
            objectives = listOf(
                "Calculate work done by constant and variable forces",
                "Apply Work-Energy Theorem (W_net = ΔKE)",
                "Formulate Conservation of Mechanical Energy"
            ),
            lessons = listOf(
                LessonSummary("phys_les_17", "phys_mod_9", "Work Done & Kinetic vs Potential Energy", 22, false),
                LessonSummary("phys_les_18", "phys_mod_9", "Conservation of Energy & Power Rating", 20, false)
            )
        ),
        CourseModule(
            id = "phys_mod_10",
            courseId = "course_physics",
            number = 10,
            title = "Simple Harmonic Motion",
            isSourceSupported = false,
            objectives = listOf(
                "Define conditions for SHM: a = -ω²x",
                "Analyze mass-spring and simple pendulum systems",
                "Calculate period, frequency, and energy in oscillation"
            ),
            lessons = listOf(
                LessonSummary("phys_les_19", "phys_mod_10", "Oscillatory Motion & Restoring Force", 22, false)
            )
        ),
        CourseModule(
            id = "phys_mod_11",
            courseId = "course_physics",
            number = 11,
            title = "Waves and Oscillations",
            isSourceSupported = false,
            objectives = listOf(
                "Categorize transverse vs longitudinal waves",
                "Apply wave equation v = f · λ",
                "Understand wave superposition and interference"
            ),
            lessons = listOf(
                LessonSummary("phys_les_20", "phys_mod_11", "Wave Properties & Sound Transmission", 20, false)
            )
        ),
        CourseModule(
            id = "phys_mod_12",
            courseId = "course_physics",
            number = 12,
            title = "Electricity and Electrostatics",
            isSourceSupported = true,
            sourceName = "Physics for Computing 2024 Exam",
            objectives = listOf(
                "Apply Coulomb's Law F = k|q1 q2|/r²",
                "Calculate electric field and electric potential",
                "Analyze capacitance and charge storage in memory cells"
            ),
            lessons = listOf(
                LessonSummary("phys_les_21", "phys_mod_12", "Coulomb's Law & Electric Fields", 25, false),
                LessonSummary("phys_les_22", "phys_mod_12", "Capacitors in Computing Architecture", 25, false)
            )
        )
    )

    val programmingModules: List<CourseModule> = listOf(
        CourseModule(
            id = "prog_mod_1",
            courseId = "course_programming",
            number = 1,
            title = "Introduction to Problem Solving",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Understand the 6 phases of software problem solving",
                "Differentiate algorithmic vs heuristic problem solving",
                "Formulate computational problem statements"
            ),
            lessons = listOf(
                LessonSummary("prog_les_1", "prog_mod_1", "The Software Engineering Problem Solving Cycle", 15, true),
                LessonSummary("prog_les_2", "prog_mod_1", "Heuristic vs Algorithmic Approaches", 12, true)
            )
        ),
        CourseModule(
            id = "prog_mod_2",
            courseId = "course_programming",
            number = 2,
            title = "Computational and Logical Problems",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Analyze algorithmic problems with mathematical logic",
                "Formulate truth tables and Boolean expressions",
                "Identify boundary conditions and edge cases"
            ),
            lessons = listOf(
                LessonSummary("prog_les_3", "prog_mod_2", "Logical Operators and Propositional Logic", 18, true),
                LessonSummary("prog_les_4", "prog_mod_2", "Problem Decomposition Strategies", 15, true)
            )
        ),
        CourseModule(
            id = "prog_mod_3",
            courseId = "course_programming",
            number = 3,
            title = "Problem Analysis Charts (PAC)",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Construct the 4 quadrants of a Problem Analysis Chart",
                "Isolate Given Data, Required Results, Processing Requirements, and Alternative Solutions",
                "Validate problem specifications before designing algorithms"
            ),
            lessons = listOf(
                LessonSummary("prog_les_5", "prog_mod_3", "Constructing 4-Quadrant PAC Diagrams", 20, true),
                LessonSummary("prog_les_6", "prog_mod_3", "PAC Case Studies: Payroll & Grade Computation", 22, true)
            )
        ),
        CourseModule(
            id = "prog_mod_4",
            courseId = "course_programming",
            number = 4,
            title = "Input-Process-Output (IPO) Charts",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Format standard Input, Process, Output tables",
                "Map user inputs to computational processes and output variables",
                "Transition from IPO charts directly into pseudocode"
            ),
            lessons = listOf(
                LessonSummary("prog_les_7", "prog_mod_4", "IPO Chart Architecture & Variable Mapping", 20, false),
                LessonSummary("prog_les_8", "prog_mod_4", "Transforming IPO Charts to Algorithm Steps", 20, false)
            )
        ),
        CourseModule(
            id = "prog_mod_5",
            courseId = "course_programming",
            number = 5,
            title = "Algorithms & Complexity",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Starting Out with C++",
            objectives = listOf(
                "Define algorithm properties: finiteness, definiteness, input, output, effectiveness",
                "Trace algorithm execution step-by-step with state tables",
                "Analyze basic time complexity and step counting"
            ),
            lessons = listOf(
                LessonSummary("prog_les_9", "prog_mod_5", "Core Characteristics of Robust Algorithms", 18, false),
                LessonSummary("prog_les_10", "prog_mod_5", "Step Tracing & Dry-Run State Tables", 22, false)
            )
        ),
        CourseModule(
            id = "prog_mod_6",
            courseId = "course_programming",
            number = 6,
            title = "Pseudocode Standards",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Write structured pseudocode using standard keywords (INPUT, OUTPUT, SET, IF, WHILE)",
                "Maintain proper indentation and control flow hierarchy",
                "Avoid language-dependent syntax in algorithm design"
            ),
            lessons = listOf(
                LessonSummary("prog_les_11", "prog_mod_6", "Standardized Keywords & Control Constructs", 20, false),
                LessonSummary("prog_les_12", "prog_mod_6", "Translating Word Problems to Structured Pseudocode", 25, false)
            )
        ),
        CourseModule(
            id = "prog_mod_7",
            courseId = "course_programming",
            number = 7,
            title = "Flowcharts and Visual Logic",
            isSourceSupported = true,
            sourceName = "CSSD 101 Planning Your Solutions",
            objectives = listOf(
                "Master ANSI standard flowchart symbols (Ovals, Rectangles, Diamonds, Parallelograms)",
                "Construct decision branching and loop back-edges",
                "Identify flowchart anti-patterns and infinite loop traps"
            ),
            lessons = listOf(
                LessonSummary("prog_les_13", "prog_mod_7", "Standard ANSI Flowchart Symbols & Conventions", 20, false),
                LessonSummary("prog_les_14", "prog_mod_7", "Branching & Looping Flowchart Diagrams", 25, false)
            )
        ),
        CourseModule(
            id = "prog_mod_8",
            courseId = "course_programming",
            number = 8,
            title = "Variables, Data Types, and C++ Basics",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Chapter 2 & 3",
            objectives = listOf(
                "Declare and initialize primitive types (int, double, char, bool, string)",
                "Understand integer division, modulus operator (%), and type casting",
                "Use cin and cout with proper stream manipulation"
            ),
            lessons = listOf(
                LessonSummary("prog_les_15", "prog_mod_8", "Data Types, Literals, and Memory Representation", 20, false),
                LessonSummary("prog_les_16", "prog_mod_8", "Arithmetic Expressions and Precedence Rules", 22, false)
            )
        ),
        CourseModule(
            id = "prog_mod_9",
            courseId = "course_programming",
            number = 9,
            title = "Conditional Statements",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Chapter 4",
            objectives = listOf(
                "Implement if, if-else, and nested if chains",
                "Construct multi-branch switch-case structures with break statements",
                "Evaluate complex Boolean expressions with short-circuit evaluation"
            ),
            lessons = listOf(
                LessonSummary("prog_les_17", "prog_mod_9", "Branching Logic & Relational Operators", 22, false),
                LessonSummary("prog_les_18", "prog_mod_9", "Switch-Case Constructs and Fall-Through Behavior", 20, false)
            )
        ),
        CourseModule(
            id = "prog_mod_10",
            courseId = "course_programming",
            number = 10,
            title = "Loops and Repetition Structures",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Chapter 5",
            objectives = listOf(
                "Compare pre-test (while, for) vs post-test (do-while) loops",
                "Manage counter-controlled and sentinel-controlled loops",
                "Debug off-by-one errors and infinite loops"
            ),
            lessons = listOf(
                LessonSummary("prog_les_19", "prog_mod_10", "While, Do-While, and For Loop Semantics", 25, false),
                LessonSummary("prog_les_20", "prog_mod_10", "Nested Loops & Pattern Generation", 25, false)
            )
        ),
        CourseModule(
            id = "prog_mod_11",
            courseId = "course_programming",
            number = 11,
            title = "Functions and Modular Design",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Chapter 6",
            objectives = listOf(
                "Write function prototypes, definitions, and return types",
                "Distinguish pass-by-value vs pass-by-reference (&)",
                "Trace local vs global variable scope and lifetime"
            ),
            lessons = listOf(
                LessonSummary("prog_les_21", "prog_mod_11", "Function Signatures & Pass-by-Value vs Reference", 25, false),
                LessonSummary("prog_les_22", "prog_mod_11", "Scope Resolution and Modular Problem Decomposition", 25, false)
            )
        ),
        CourseModule(
            id = "prog_mod_12",
            courseId = "course_programming",
            number = 12,
            title = "Arrays and Introduction to Objects",
            isSourceSupported = true,
            sourceName = "Tony Gaddis Chapter 7",
            objectives = listOf(
                "Declare, initialize, and traverse 1D and 2D arrays",
                "Pass arrays to functions with size parameters",
                "Understand introductory object-oriented concepts in C++"
            ),
            lessons = listOf(
                LessonSummary("prog_les_23", "prog_mod_12", "Array Memory Layout & Bounds Checking", 25, false),
                LessonSummary("prog_les_24", "prog_mod_12", "Passing Arrays to Functions & C++ Structs", 25, false)
            )
        )
    )
}
