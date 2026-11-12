package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgrammingLabScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("programming_lab_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Programming & Problem Solving Lab",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "C++ code tracing, IPO charts, flowcharts, and debugging challenges",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Code Tracing") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("IPO Chart Builder") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Debugging Arena") })
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> { item { CodeTracingLab() } }
                1 -> { item { IpoChartLab() } }
                2 -> { item { DebuggingArenaLab() } }
            }
        }
    }
}

@Composable
private fun CodeTracingLab() {
    var stepIndex by remember { mutableIntStateOf(0) }

    val traceSteps = listOf(
        TraceState(step = 0, k = 1, total = 0, condition = "1 <= 7 (true)", output = "", note = "Loop initialized: k=1, condition holds true."),
        TraceState(step = 1, k = 3, total = 1, condition = "3 <= 7 (true)", output = "", note = "k=1 executed: total = 0 + 1 = 1. k updated by +2 to 3."),
        TraceState(step = 2, k = 5, total = 4, condition = "5 <= 7 (true)", output = "", note = "k=3 executed: total = 1 + 3 = 4. k updated to 5. Notice: k==5 triggers `continue`!"),
        TraceState(step = 3, k = 7, total = 4, condition = "7 <= 7 (true)", output = "", note = "k=5 skipped total update. k updated to 7. Condition 7 <= 7 holds true."),
        TraceState(step = 4, k = 9, total = 11, condition = "9 <= 7 (false)", output = "11", note = "k=7 executed: total = 4 + 7 = 11. k updated to 9. Condition 9 <= 7 is false! Loop terminates."),
    )

    val currentTrace = traceSteps[stepIndex.coerceIn(0, traceSteps.size - 1)]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Interactive C++ Dry-Run & State Tracing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Code Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = """
int total = 0;
for (int k = 1; k <= 7; k += 2) {
    if (k == 5) continue;
    total += k;
}
cout << total << endl;
                    """.trimIndent(),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE2E8F0)
                )
            }

            // Step controller
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Execution Step ${stepIndex + 1} of ${traceSteps.size}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { if (stepIndex > 0) stepIndex-- },
                        enabled = stepIndex > 0,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Step Back", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { if (stepIndex < traceSteps.size - 1) stepIndex++ },
                        enabled = stepIndex < traceSteps.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Next Step", fontSize = 11.sp)
                    }
                }
            }

            // Variable State Table
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Memory State at Step ${stepIndex + 1}:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Iteration Variable `k`:", style = MaterialTheme.typography.bodySmall)
                        Text("${currentTrace.k}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = ElectricBlue)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Accumulator `total`:", style = MaterialTheme.typography.bodySmall)
                        Text("${currentTrace.total}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = EmeraldGreen)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Guard Condition (k <= 7):", style = MaterialTheme.typography.bodySmall)
                        Text(currentTrace.condition, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = AmberAccent)
                    }
                    if (currentTrace.output.isNotEmpty()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Standard Console Output:", style = MaterialTheme.typography.bodySmall)
                            Text(currentTrace.output, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = "Trace Explanation: ${currentTrace.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class TraceState(
    val step: Int,
    val k: Int,
    val total: Int,
    val condition: String,
    val output: String,
    val note: String
)

@Composable
private fun IpoChartLab() {
    var selectedProblemIndex by remember { mutableIntStateOf(0) }

    val problems = listOf(
        IpoProblem(
            title = "Overtime Payroll System",
            inputs = listOf("hoursWorked (double)", "hourlyPayRate (double)"),
            processes = listOf(
                "Verify hoursWorked >= 0 and hourlyPayRate > 0",
                "If hoursWorked <= 40: regularPay = hoursWorked * hourlyPayRate",
                "If hoursWorked > 40: regularPay = 40 * hourlyPayRate, overtimePay = (hoursWorked - 40) * (hourlyPayRate * 1.5)",
                "grossPay = regularPay + overtimePay"
            ),
            outputs = listOf("regularPay (double)", "overtimePay (double)", "grossPay (double)")
        ),
        IpoProblem(
            title = "Parallel Equivalent Resistance",
            inputs = listOf("R1 (double, ohms)", "R2 (double, ohms)", "R3 (double, ohms)", "Voltage V = 12V"),
            processes = listOf(
                "Ensure R1 > 0, R2 > 0, R3 > 0 to prevent division by zero",
                "1/Req = (1/R1) + (1/R2) + (1/R3)",
                "Req = 1.0 / ((1.0/R1) + (1.0/R2) + (1.0/R3))",
                "I_total = V / Req"
            ),
            outputs = listOf("Req (Equivalent Resistance, ohms)", "I_total (Total Current, Amperes)")
        )
    )

    val currentProblem = problems[selectedProblemIndex]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Input-Process-Output (IPO) Chart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedProblemIndex == 0,
                        onClick = { selectedProblemIndex = 0 },
                        label = { Text("Payroll") }
                    )
                    FilterChip(
                        selected = selectedProblemIndex == 1,
                        onClick = { selectedProblemIndex = 1 },
                        label = { Text("Resistance") }
                    )
                }
            }

            Text(
                text = "Problem Case Study: ${currentProblem.title}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 3-Column IPO Visual Table
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input Column
                IpoColumn(
                    title = "INPUT",
                    color = ElectricBlue,
                    items = currentProblem.inputs,
                    modifier = Modifier.weight(1f)
                )

                // Process Column
                IpoColumn(
                    title = "PROCESS",
                    color = AmberAccent,
                    items = currentProblem.processes,
                    modifier = Modifier.weight(1.5f)
                )

                // Output Column
                IpoColumn(
                    title = "OUTPUT",
                    color = EmeraldGreen,
                    items = currentProblem.outputs,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private data class IpoProblem(
    val title: String,
    val inputs: List<String>,
    val processes: List<String>,
    val outputs: List<String>
)

@Composable
private fun IpoColumn(
    title: String,
    color: Color,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                )
            }
            items.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun DebuggingArenaLab() {
    var selectedBugOption by remember { mutableStateOf<Int?>(null) }
    var hasChecked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Debugging Challenge #1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CrimsonRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Subtle Logic Trap",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "The following function is supposed to validate whether an exam score is between 50 and 100:",
                style = MaterialTheme.typography.bodySmall
            )

            // Buggy Code Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = """
bool isValidScore(int score) {
    if (50 <= score <= 100) {
        return true;
    }
    return false;
}
                    """.trimIndent(),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFCA5A5)
                )
            }

            Text(
                text = "Why does `isValidScore(10)` incorrectly return `true`?",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            // Diagnostic options
            val options = listOf(
                "Chained comparison: `50 <= score` evaluates to false (0), then `0 <= 100` evaluates to true!",
                "C++ does not have a boolean return type.",
                "The score variable is unsigned by default.",
                "The `<=` operator cannot be used inside an if block."
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEachIndexed { index, opt ->
                    val isSelected = selectedBugOption == index
                    val isCorrect = index == 0

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                selectedBugOption = index
                                hasChecked = true
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = when {
                            !hasChecked -> if (isSelected) Indigo600.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            isCorrect -> EmeraldGreen.copy(alpha = 0.2f)
                            isSelected -> CrimsonRed.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) Indigo600 else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            if (hasChecked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldGreen.copy(alpha = 0.1f))
                        .border(1.dp, EmeraldGreen, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Correct fix: `if (score >= 50 && score <= 100)`. Always combine multiple relational conditions using logical operators (`&&` or `||`).",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
