package com.example.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ScientificCalculatorView(
    initialExpression: String = "",
    isExamMode: Boolean = false,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expression by remember { mutableStateOf(initialExpression) }
    var resultText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var angleMode by remember { mutableStateOf(AngleMode.DEG) }
    var isShiftActive by remember { mutableStateOf(false) }

    var memoryValue by remember { mutableDoubleStateOf(0.0) }
    var hasMemory by remember { mutableStateOf(false) }
    var lastAnswer by remember { mutableDoubleStateOf(0.0) }

    // History state
    val history = remember { mutableStateListOf<CalculationRecord>() }
    var showHistoryDialog by remember { mutableStateOf(false) }

    // Listen to initialExpression updates if prefilled
    LaunchedEffect(initialExpression) {
        if (initialExpression.isNotBlank()) {
            expression = initialExpression
            val res = ScientificCalculatorEngine.evaluate(initialExpression, angleMode, lastAnswer)
            res.onSuccess {
                resultText = ScientificCalculatorEngine.formatResult(it)
                errorText = null
            }.onFailure {
                resultText = ""
                errorText = it.message
            }
        }
    }

    // Live preview evaluation on expression change
    LaunchedEffect(expression, angleMode) {
        if (expression.isBlank()) {
            resultText = ""
            errorText = null
        } else {
            val res = ScientificCalculatorEngine.evaluate(expression, angleMode, lastAnswer)
            res.onSuccess {
                resultText = ScientificCalculatorEngine.formatResult(it)
                errorText = null
            }.onFailure {
                // If incomplete syntax while typing, keep current preview or wait for =
                errorText = null
            }
        }
    }

    fun calculateFinal() {
        if (expression.isBlank()) return
        val res = ScientificCalculatorEngine.evaluate(expression, angleMode, lastAnswer)
        res.onSuccess {
            val formatted = ScientificCalculatorEngine.formatResult(it)
            resultText = formatted
            errorText = null
            lastAnswer = it
            history.add(
                0,
                CalculationRecord(
                    expression = expression,
                    result = formatted,
                    angleMode = angleMode
                )
            )
        }.onFailure {
            errorText = it.message ?: "Evaluation Error"
            resultText = ""
        }
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("StudyForge Calculation", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $text to clipboard", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Toolbar: Status, DEG/RAD, Memory, History, Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // DEG / RAD Toggle
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            angleMode = if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
                        }
                        .testTag("calc_angle_mode_toggle"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (angleMode == AngleMode.DEG) Indigo600.copy(alpha = 0.25f) else EmeraldGreen.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (angleMode == AngleMode.DEG) Indigo400 else EmeraldGreen
                    )
                ) {
                    Text(
                        text = angleMode.name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (angleMode == AngleMode.DEG) Indigo400 else EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // 2nd Shift Indicator
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isShiftActive = !isShiftActive },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isShiftActive) AmberAccent.copy(alpha = 0.3f) else Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isShiftActive) AmberAccent else Color(0xFF334155))
                ) {
                    Text(
                        text = "2nd",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isShiftActive) AmberAccent else Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // Memory Indicator (M)
                if (hasMemory) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElectricBlue.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue)
                    ) {
                        Text(
                            text = "M",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // History Button
                IconButton(
                    onClick = { showHistoryDialog = true },
                    modifier = Modifier.size(32.dp).testTag("calc_history_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Copy Result
                if (resultText.isNotBlank()) {
                    IconButton(
                        onClick = { copyToClipboard(resultText) },
                        modifier = Modifier.size(32.dp).testTag("calc_copy_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Close / Minimize button if embedded in floating sheet
                if (onClose != null) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp).testTag("calc_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Mathematical Display Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF030712))
                .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Input Expression row with horizontal scroll
                val scrollState = rememberScrollState()
                LaunchedEffect(expression) {
                    scrollState.scrollTo(scrollState.maxValue)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (expression.isEmpty()) "0" else expression,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.Monospace,
                        color = if (expression.isEmpty()) Color.DarkGray else Color(0xFFE2E8F0),
                        fontSize = 18.sp,
                        textAlign = TextAlign.End
                    )
                }

                // Result or Error
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = if (resultText.isNotBlank()) "= $resultText" else "",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen,
                        fontSize = 24.sp
                    )
                }
            }
        }

        // Row 1: Memory Controls (MC, MR, M+, M-, ANS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(
                text = "MC",
                color = Color(0xFF1E293B),
                textColor = Color.Gray,
                modifier = Modifier.weight(1f),
                onClick = {
                    memoryValue = 0.0
                    hasMemory = false
                }
            )
            CalcButton(
                text = "MR",
                color = Color(0xFF1E293B),
                textColor = if (hasMemory) ElectricBlue else Color.Gray,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (hasMemory) expression += ScientificCalculatorEngine.formatResult(memoryValue)
                }
            )
            CalcButton(
                text = "M+",
                color = Color(0xFF1E293B),
                textColor = Color(0xFF94A3B8),
                modifier = Modifier.weight(1f),
                onClick = {
                    val currentVal = resultText.toDoubleOrNull() ?: expression.toDoubleOrNull() ?: 0.0
                    memoryValue += currentVal
                    hasMemory = true
                }
            )
            CalcButton(
                text = "M-",
                color = Color(0xFF1E293B),
                textColor = Color(0xFF94A3B8),
                modifier = Modifier.weight(1f),
                onClick = {
                    val currentVal = resultText.toDoubleOrNull() ?: expression.toDoubleOrNull() ?: 0.0
                    memoryValue -= currentVal
                    hasMemory = true
                }
            )
            CalcButton(
                text = "ANS",
                color = Indigo600.copy(alpha = 0.25f),
                textColor = Indigo400,
                modifier = Modifier.weight(1f),
                onClick = {
                    expression += "ANS"
                }
            )
        }

        // Scientific Keys Row A: Trigonometry & Powers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val sinLabel = if (isShiftActive) "sin⁻¹" else "sin"
            val sinFunc = if (isShiftActive) "asin(" else "sin("
            CalcButton(text = sinLabel, color = Color(0xFF1E293B), textColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f), onClick = { expression += sinFunc })

            val cosLabel = if (isShiftActive) "cos⁻¹" else "cos"
            val cosFunc = if (isShiftActive) "acos(" else "cos("
            CalcButton(text = cosLabel, color = Color(0xFF1E293B), textColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f), onClick = { expression += cosFunc })

            val tanLabel = if (isShiftActive) "tan⁻¹" else "tan"
            val tanFunc = if (isShiftActive) "atan(" else "tan("
            CalcButton(text = tanLabel, color = Color(0xFF1E293B), textColor = Color(0xFF60A5FA), modifier = Modifier.weight(1f), onClick = { expression += tanFunc })

            val pwrLabel = if (isShiftActive) "x³" else "x²"
            val pwrFunc = if (isShiftActive) "^3" else "^2"
            CalcButton(text = pwrLabel, color = Color(0xFF1E293B), textColor = Color(0xFFE2E8F0), modifier = Modifier.weight(1f), onClick = { expression += pwrFunc })

            CalcButton(text = "xʸ", color = Color(0xFF1E293B), textColor = Color(0xFFE2E8F0), modifier = Modifier.weight(1f), onClick = { expression += "^" })
        }

        // Scientific Keys Row B: Roots, Logs, Constants
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val rootLabel = if (isShiftActive) "∛" else "√"
            val rootFunc = if (isShiftActive) "cbrt(" else "sqrt("
            CalcButton(text = rootLabel, color = Color(0xFF1E293B), textColor = Color(0xFFE2E8F0), modifier = Modifier.weight(1f), onClick = { expression += rootFunc })

            val logLabel = if (isShiftActive) "10ˣ" else "log"
            val logFunc = if (isShiftActive) "10^(" else "log("
            CalcButton(text = logLabel, color = Color(0xFF1E293B), textColor = Color(0xFFE2E8F0), modifier = Modifier.weight(1f), onClick = { expression += logFunc })

            val lnLabel = if (isShiftActive) "eˣ" else "ln"
            val lnFunc = if (isShiftActive) "exp(" else "ln("
            CalcButton(text = lnLabel, color = Color(0xFF1E293B), textColor = Color(0xFFE2E8F0), modifier = Modifier.weight(1f), onClick = { expression += lnFunc })

            CalcButton(text = "π", color = Color(0xFF1E293B), textColor = AmberAccent, modifier = Modifier.weight(1f), onClick = { expression += "π" })
            CalcButton(text = "e", color = Color(0xFF1E293B), textColor = AmberAccent, modifier = Modifier.weight(1f), onClick = { expression += "e" })
        }

        // Scientific Keys Row C: nPr, nCr, Factorial, Abs, Reciprocal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "nPr", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "P" })
            CalcButton(text = "nCr", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "C" })
            CalcButton(text = "n!", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "!" })
            CalcButton(text = "1/x", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "^(-1)" })
            CalcButton(text = "|x|", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "abs(" })
        }

        // Keypad Grid: 7 8 9 ( ) DEL
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "7", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "7" })
            CalcButton(text = "8", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "8" })
            CalcButton(text = "9", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "9" })
            CalcButton(text = "(", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "(" })
            CalcButton(text = ")", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += ")" })
            CalcButton(
                text = "DEL",
                color = CrimsonRed.copy(alpha = 0.2f),
                textColor = CrimsonLight,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (expression.isNotEmpty()) expression = expression.dropLast(1)
                }
            )
        }

        // Keypad Grid: 4 5 6 × ÷ AC
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "4", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "4" })
            CalcButton(text = "5", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "5" })
            CalcButton(text = "6", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "6" })
            CalcButton(text = "×", color = Indigo600.copy(alpha = 0.25f), textColor = Indigo400, modifier = Modifier.weight(1f), onClick = { expression += "×" })
            CalcButton(text = "÷", color = Indigo600.copy(alpha = 0.25f), textColor = Indigo400, modifier = Modifier.weight(1f), onClick = { expression += "÷" })
            CalcButton(
                text = "AC",
                color = CrimsonRed,
                textColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = {
                    expression = ""
                    resultText = ""
                    errorText = null
                }
            )
        }

        // Keypad Grid: 1 2 3 + −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "1", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "1" })
            CalcButton(text = "2", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "2" })
            CalcButton(text = "3", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "3" })
            CalcButton(text = "+", color = Indigo600.copy(alpha = 0.25f), textColor = Indigo400, modifier = Modifier.weight(1f), onClick = { expression += "+" })
            CalcButton(text = "−", color = Indigo600.copy(alpha = 0.25f), textColor = Indigo400, modifier = Modifier.weight(1f), onClick = { expression += "−" })
            CalcButton(text = "%", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "%" })
        }

        // Keypad Grid: 0 . ± EXP =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalcButton(text = "0", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "0" })
            CalcButton(text = ".", color = Color(0xFF1E293B), textColor = Color.White, modifier = Modifier.weight(1f), onClick = { expression += "." })
            CalcButton(
                text = "±",
                color = Color(0xFF1E293B),
                textColor = Color(0xFF94A3B8),
                modifier = Modifier.weight(1f),
                onClick = {
                    expression = if (expression.startsWith("-")) expression.removePrefix("-") else "-$expression"
                }
            )
            CalcButton(text = "EXP", color = Color(0xFF1E293B), textColor = Color(0xFF94A3B8), modifier = Modifier.weight(1f), onClick = { expression += "E" })
            CalcButton(
                text = "=",
                color = EmeraldGreen,
                textColor = Color.White,
                modifier = Modifier.weight(2f),
                onClick = { calculateFinal() }
            )
        }
    }

    // History Modal Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isExamMode) "Exam Calculation History" else "Calculation History", style = MaterialTheme.typography.titleMedium)
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { history.clear() }) {
                            Text("Clear", color = CrimsonRed, fontSize = 12.sp)
                        }
                    }
                }
            },
            text = {
                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No calculations recorded yet.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history, key = { it.id }) { record ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expression = record.result
                                        showHistoryDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = record.expression, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color.Gray)
                                        Text(text = "= ${record.result}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldGreen)
                                    }
                                    IconButton(onClick = { copyToClipboard(record.result) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showHistoryDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun CalcButton(
    text: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .testTag("calc_btn_${text.replace(" ", "_")}"),
        shape = RoundedCornerShape(8.dp),
        color = color,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = if (text.length > 3) 11.sp else 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
