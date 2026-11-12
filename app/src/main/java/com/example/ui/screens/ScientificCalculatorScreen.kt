package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ScientificCalculatorView
import com.example.ui.theme.*

@Composable
fun ScientificCalculatorScreen(
    initialExpression: String = "",
    onNavigateToFormulaLibrary: () -> Unit
) {
    var activeExpression by remember { mutableStateOf(initialExpression) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scientific_calculator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Scientific Calculator",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Undergraduate Physics, Mathematics & Computer Science Precision Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = onNavigateToFormulaLibrary,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Functions, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Formulas", fontSize = 12.sp)
                    }
                }
            }
        }

        // Quick Preset Chips for Physics & Math
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    AssistChip(
                        onClick = { activeExpression = "20*cos(60)" },
                        label = { Text("Vector: 20×cos(60°)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.ArrowOutward, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { activeExpression = "sqrt(120^2 + 90^2)" },
                        label = { Text("Pythagoras: √(120²+90²)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { activeExpression = "sqrt(0 + 2*9.8*78.4)" },
                        label = { Text("Kinematics: √(2*9.8*78.4)", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { activeExpression = "5C2" },
                        label = { Text("Combination: 5C2", fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }
            }
        }

        // Main Calculator Component
        item {
            ScientificCalculatorView(
                initialExpression = activeExpression,
                isExamMode = false
            )
        }

        // Operational Reference Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Engineering & Physics Standards:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("• Trigonometry: Toggle DEG / RAD mode for degrees vs radians (e.g. sin(30°) = 0.5; cos(π) = -1).", style = MaterialTheme.typography.bodySmall)
                    Text("• Permutations & Combinations: Use nPr and nCr (e.g. 5P2 = 20, 5C2 = 10).", style = MaterialTheme.typography.bodySmall)
                    Text("• Order of Operations: Strict algebraic hierarchy: Parentheses () > Functions & Exponents ^ > Multiplication/Division * / > Addition/Subtraction + -.", style = MaterialTheme.typography.bodySmall)
                    Text("• Memory Keys: M+ (accumulate), M- (deduct), MR (recall), MC (clear memory bank), ANS (previous answer).", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
