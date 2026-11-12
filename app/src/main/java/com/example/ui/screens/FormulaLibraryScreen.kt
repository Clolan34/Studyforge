package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class FormulaReference(
    val id: String,
    val name: String,
    val category: String, // "Kinematics", "Vectors", "Energy", "Electricity", "Waves"
    val equation: String,
    val calculatorSnippet: String,
    val explanation: String,
    val variables: Map<String, String>,
    val siUnits: String,
    val isSourceExtracted: Boolean = true
)

@Composable
fun FormulaLibraryScreen(
    onSendToCalculator: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val allFormulas = remember {
        listOf(
            FormulaReference(
                id = "f_1",
                name = "Velocity-Time Kinematic Relation",
                category = "Kinematics",
                equation = "v = u + at",
                calculatorSnippet = "10 + 2.5 * 4",
                explanation = "Calculates final velocity after uniform acceleration over time.",
                variables = mapOf("v" to "Final velocity", "u" to "Initial velocity", "a" to "Acceleration", "t" to "Time"),
                siUnits = "v: m/s, u: m/s, a: m/s², t: s",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_2",
                name = "Displacement Under Uniform Acceleration",
                category = "Kinematics",
                equation = "s = ut + (1/2)at²",
                calculatorSnippet = "10*4 + 0.5*2.5*(4^2)",
                explanation = "Determines total spatial displacement when initial velocity and acceleration are known.",
                variables = mapOf("s" to "Displacement", "u" to "Initial velocity", "a" to "Acceleration", "t" to "Time"),
                siUnits = "s: meters (m)",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_3",
                name = "Torricelli's Velocity-Displacement Equation",
                category = "Kinematics",
                equation = "v² = u² + 2as => v = √(u² + 2as)",
                calculatorSnippet = "sqrt(0^2 + 2*9.8*78.4)",
                explanation = "Calculates velocity without time variable; crucial in free-fall and stopping distance problems.",
                variables = mapOf("v" to "Final velocity", "u" to "Initial velocity", "a" to "Acceleration", "s" to "Displacement"),
                siUnits = "m/s",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_4",
                name = "Vector Magnitude (2D Euclidean Norm)",
                category = "Vectors",
                equation = "|V| = √(Vx² + Vy²)",
                calculatorSnippet = "sqrt(120^2 + 90^2)",
                explanation = "Reconstructs vector magnitude from perpendicular orthogonal components.",
                variables = mapOf("|V|" to "Magnitude", "Vx" to "X component", "Vy" to "Y component"),
                siUnits = "Newtons (N) or m/s",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_5",
                name = "Orthogonal Vector Components",
                category = "Vectors",
                equation = "Vx = |V| cos(θ), Vy = |V| sin(θ)",
                calculatorSnippet = "20 * cos(60)",
                explanation = "Decomposes any 2D vector along Cartesian coordinates.",
                variables = mapOf("Vx" to "Horizontal component", "Vy" to "Vertical component", "|V|" to "Magnitude", "θ" to "Angle"),
                siUnits = "N or m/s",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_6",
                name = "Newton's Second Law of Motion",
                category = "Dynamics",
                equation = "F_net = m * a",
                calculatorSnippet = "0.04 * 20",
                explanation = "Relates net external force to mass and resultant acceleration.",
                variables = mapOf("F" to "Force (N)", "m" to "Mass (kg)", "a" to "Acceleration (m/s²)"),
                siUnits = "Newtons (kg·m/s²)",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_7",
                name = "Work Done by Constant Force",
                category = "Energy",
                equation = "W = F * d * cos(θ)",
                calculatorSnippet = "50 * 10 * cos(0)",
                explanation = "Calculates mechanical energy transferred by a force acting over a displacement.",
                variables = mapOf("W" to "Work", "F" to "Force", "d" to "Displacement", "θ" to "Angle between F and d"),
                siUnits = "Joules (J = N·m)",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_8",
                name = "Kinetic Energy",
                category = "Energy",
                equation = "KE = (1/2) * m * v²",
                calculatorSnippet = "0.5 * 250 * (12^2)",
                explanation = "Energy possessed by an object due to its motion.",
                variables = mapOf("KE" to "Kinetic energy", "m" to "Mass", "v" to "Velocity"),
                siUnits = "Joules (J)",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_9",
                name = "Coulomb's Law of Electrostatic Force",
                category = "Electricity",
                equation = "F = k * |q1 * q2| / r²",
                calculatorSnippet = "(8.99E9) * (1.6E-19) * (1.6E-19) / (0.05^2)",
                explanation = "Calculates electrostatic attractive or repulsive force between point charges.",
                variables = mapOf("k" to "Coulomb constant (8.99×10⁹ N·m²/C²)", "q1, q2" to "Charges (C)", "r" to "Distance (m)"),
                siUnits = "Newtons (N)",
                isSourceExtracted = true
            ),
            FormulaReference(
                id = "f_10",
                name = "Wave Speed Equation",
                category = "Waves",
                equation = "v = f * λ",
                calculatorSnippet = "440 * 0.77",
                explanation = "Relates propagation speed of a wave to its frequency and wavelength.",
                variables = mapOf("v" to "Wave speed (m/s)", "f" to "Frequency (Hz)", "λ" to "Wavelength (m)"),
                siUnits = "m/s",
                isSourceExtracted = false
            )
        )
    }

    val categories = listOf("All", "Kinematics", "Vectors", "Dynamics", "Energy", "Electricity", "Waves")

    val filteredFormulas = remember(searchQuery, selectedCategory) {
        allFormulas.filter { formula ->
            (selectedCategory == "All" || formula.category == selectedCategory) &&
            (searchQuery.isBlank() || formula.name.contains(searchQuery, ignoreCase = true) || formula.equation.contains(searchQuery, ignoreCase = true) || formula.category.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("formula_library_screen")
    ) {
        // Header & Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Physics & Mathematics Formula Library",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Search equations, inspect SI units, and calculate with one tap",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search formulas (e.g. kinematics, vector, Coulomb, energy)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredFormulas, key = { it.id }) { formula ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ElectricBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = formula.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (formula.isSourceExtracted) EmeraldGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (formula.isSourceExtracted) "Source-Extracted" else "Supplementary",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (formula.isSourceExtracted) EmeraldGreen else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = formula.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Formula Equation Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = formula.equation,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = AmberAccent
                            )
                        }

                        Text(
                            text = formula.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "SI Units: ${formula.siUnits}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { onSendToCalculator(formula.calculatorSnippet) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculate with this Formula", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
