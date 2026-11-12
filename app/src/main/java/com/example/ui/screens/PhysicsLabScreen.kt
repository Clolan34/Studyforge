package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicsLabScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("physics_lab_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Interactive Physics Problem-Solving Lab",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Manipulate physical variables, visualize vector geometry, and test kinematics",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Vector Visualizer") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Kinematics & Graphs") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Work & Energy") }
                )
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
                0 -> {
                    // Vector Visualizer & Decomposition
                    item { VectorVisualizerLab() }
                }
                1 -> {
                    // Kinematics & Velocity-Time Graphs
                    item { KinematicsAndGraphLab() }
                }
                2 -> {
                    // Work, Energy & Power Calculator
                    item { WorkEnergyLab() }
                }
            }
        }
    }
}

@Composable
private fun VectorVisualizerLab() {
    var magnitude by remember { mutableFloatStateOf(60f) }
    var angleDegrees by remember { mutableFloatStateOf(45f) }
    var isAdditionMode by remember { mutableStateOf(false) }

    // Second vector for addition mode
    var mag2 by remember { mutableFloatStateOf(40f) }
    var angle2 by remember { mutableFloatStateOf(120f) }

    val angleRad = Math.toRadians(angleDegrees.toDouble())
    val vx = magnitude * cos(angleRad)
    val vy = magnitude * sin(angleRad)

    val angle2Rad = Math.toRadians(angle2.toDouble())
    val v2x = mag2 * cos(angle2Rad)
    val v2y = mag2 * sin(angle2Rad)

    val rx = vx + v2x
    val ry = vy + v2y
    val rMag = sqrt(rx * rx + ry * ry)
    val rAngleDeg = Math.toDegrees(atan2(ry, rx)).let { if (it < 0) it + 360 else it }

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
                    text = "Vector Decomposition & Addition",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                FilterChip(
                    selected = isAdditionMode,
                    onClick = { isAdditionMode = !isAdditionMode },
                    label = { Text(if (isAdditionMode) "Addition Mode" else "Single Vector") }
                )
            }

            // Interactive Vector Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val originX = size.width / 2f
                    val originY = size.height / 2f
                    val scale = (size.width / 2f - 30.dp.toPx()) / 100f

                    // Draw Cartesian axes
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(20f, originY),
                        end = Offset(size.width - 20f, originY),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(originX, 20f),
                        end = Offset(originX, size.height - 20f),
                        strokeWidth = 2f
                    )

                    // Draw Vector A (Electric Blue)
                    val targetX = originX + (vx * scale).toFloat()
                    val targetY = originY - (vy * scale).toFloat()

                    // Projections
                    drawLine(
                        color = EmeraldGreen.copy(alpha = 0.7f),
                        start = Offset(originX, originY),
                        end = Offset(targetX, originY),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = AmberAccent.copy(alpha = 0.7f),
                        start = Offset(targetX, originY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 2f
                    )

                    // Main Vector A
                    drawLine(
                        color = ElectricBlue,
                        start = Offset(originX, originY),
                        end = Offset(targetX, targetY),
                        strokeWidth = 5f
                    )
                    drawCircle(color = ElectricBlue, radius = 6f, center = Offset(targetX, targetY))

                    if (isAdditionMode) {
                        // Vector B (Purple)
                        val target2X = originX + (v2x * scale).toFloat()
                        val target2Y = originY - (v2y * scale).toFloat()

                        drawLine(
                            color = PurpleAccent,
                            start = Offset(originX, originY),
                            end = Offset(target2X, target2Y),
                            strokeWidth = 4f
                        )

                        // Resultant Vector R (Emerald Green)
                        val rTargetX = originX + (rx * scale).toFloat()
                        val rTargetY = originY - (ry * scale).toFloat()

                        drawLine(
                            color = EmeraldGreen,
                            start = Offset(originX, originY),
                            end = Offset(rTargetX, rTargetY),
                            strokeWidth = 6f
                        )
                        drawCircle(color = EmeraldGreen, radius = 7f, center = Offset(rTargetX, rTargetY))
                    }
                }
            }

            // Controls for Vector A
            Text(
                text = "Vector A: Magnitude |A| = ${magnitude.toInt()} N, Angle θ = ${angleDegrees.toInt()}°",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Magnitude: ${magnitude.toInt()}", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = magnitude,
                        onValueChange = { magnitude = it },
                        valueRange = 10f..100f
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Angle: ${angleDegrees.toInt()}°", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = angleDegrees,
                        onValueChange = { angleDegrees = it },
                        valueRange = 0f..360f
                    )
                }
            }

            // Controls for Vector B if in addition mode
            if (isAdditionMode) {
                Text(
                    text = "Vector B: Magnitude |B| = ${mag2.toInt()} N, Angle θ2 = ${angle2.toInt()}°",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = PurpleAccent
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Magnitude B: ${mag2.toInt()}", style = MaterialTheme.typography.labelSmall)
                        Slider(value = mag2, onValueChange = { mag2 = it }, valueRange = 10f..100f)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Angle B: ${angle2.toInt()}°", style = MaterialTheme.typography.labelSmall)
                        Slider(value = angle2, onValueChange = { angle2 = it }, valueRange = 0f..360f)
                    }
                }
            }

            // Mathematical Readout Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Real-Time Analytical Readout:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Vector A Horizontal Component: Ax = |A| cos(${angleDegrees.toInt()}°) = ${String.format("%.2f", vx)} N",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "• Vector A Vertical Component: Ay = |A| sin(${angleDegrees.toInt()}°) = ${String.format("%.2f", vy)} N",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = AmberAccent
                    )

                    if (isAdditionMode) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = "• Resultant Rx = Ax + Bx = ${String.format("%.2f", rx)} N",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "• Resultant Ry = Ay + By = ${String.format("%.2f", ry)} N",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "• Resultant Magnitude |R| = √(Rx² + Ry²) = ${String.format("%.2f", rMag)} N",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "• Resultant Direction Angle θ_R = ${String.format("%.1f", rAngleDeg)}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KinematicsAndGraphLab() {
    var initialVelocity by remember { mutableFloatStateOf(10f) }
    var acceleration by remember { mutableFloatStateOf(2.5f) }
    var timeSeconds by remember { mutableFloatStateOf(6f) }

    val finalVelocity = initialVelocity + (acceleration * timeSeconds)
    val displacement = (initialVelocity * timeSeconds) + (0.5f * acceleration * timeSeconds * timeSeconds)

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
                text = "Uniform Acceleration & Velocity-Time Curve",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Dynamic V-T Graph Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val padding = 40.dp.toPx()
                    val graphWidth = size.width - (padding * 2)
                    val graphHeight = size.height - (padding * 2)

                    // Draw graph axes
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(padding, size.height - padding),
                        end = Offset(size.width - padding + 20f, size.height - padding),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(padding, size.height - padding),
                        end = Offset(padding, padding - 20f),
                        strokeWidth = 2f
                    )

                    // Scale factors
                    val maxV = max(finalVelocity, initialVelocity).coerceAtLeast(30f)
                    val maxT = timeSeconds.coerceAtLeast(10f)

                    val startY = (size.height - padding) - (initialVelocity / maxV) * graphHeight
                    val endY = (size.height - padding) - (finalVelocity / maxV) * graphHeight
                    val endX = padding + (timeSeconds / maxT) * graphWidth

                    // Shaded area under curve (displacement)
                    val path = Path().apply {
                        moveTo(padding, size.height - padding)
                        lineTo(padding, startY)
                        lineTo(endX, endY)
                        lineTo(endX, size.height - padding)
                        close()
                    }
                    drawPath(path, color = ElectricBlue.copy(alpha = 0.25f))

                    // Velocity line
                    drawLine(
                        color = ElectricBlue,
                        start = Offset(padding, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 5f
                    )
                    drawCircle(color = ElectricBlue, radius = 6f, center = Offset(endX, endY))
                }
            }

            // Parameter Sliders
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Initial Velocity u = ${initialVelocity.toInt()} m/s", style = MaterialTheme.typography.labelSmall)
                Slider(value = initialVelocity, onValueChange = { initialVelocity = it }, valueRange = 0f..50f)

                Text("Acceleration a = ${String.format("%.1f", acceleration)} m/s²", style = MaterialTheme.typography.labelSmall)
                Slider(value = acceleration, onValueChange = { acceleration = it }, valueRange = -5f..10f)

                Text("Elapsed Time t = ${timeSeconds.toInt()} s", style = MaterialTheme.typography.labelSmall)
                Slider(value = timeSeconds, onValueChange = { timeSeconds = it }, valueRange = 1f..15f)
            }

            // Results summary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Kinematic Solution Results:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Final Velocity v = u + at = ${initialVelocity.toInt()} + (${String.format("%.1f", acceleration)} * ${timeSeconds.toInt()}) = ${String.format("%.2f", finalVelocity)} m/s",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricBlue
                    )
                    Text(
                        text = "• Net Displacement s = ut + (1/2)at² (Area Under v-t) = ${String.format("%.2f", displacement)} m",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = EmeraldGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkEnergyLab() {
    var mass by remember { mutableFloatStateOf(10f) }
    var velocity by remember { mutableFloatStateOf(8f) }
    var height by remember { mutableFloatStateOf(5f) }

    val ke = 0.5f * mass * velocity * velocity
    val pe = mass * 9.8f * height
    val totalMechanicalEnergy = ke + pe

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
                text = "Work, Energy & Conservation of Energy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Sliders
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mass m = ${mass.toInt()} kg", style = MaterialTheme.typography.labelSmall)
                Slider(value = mass, onValueChange = { mass = it }, valueRange = 1f..50f)

                Text("Velocity v = ${velocity.toInt()} m/s", style = MaterialTheme.typography.labelSmall)
                Slider(value = velocity, onValueChange = { velocity = it }, valueRange = 0f..30f)

                Text("Height h = ${height.toInt()} m", style = MaterialTheme.typography.labelSmall)
                Slider(value = height, onValueChange = { height = it }, valueRange = 0f..20f)
            }

            // Energy Bar Graph Preview
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Energy Breakdown:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("• Kinetic Energy KE = (1/2)mv² = ${String.format("%.1f", ke)} Joules", style = MaterialTheme.typography.bodySmall, color = ElectricBlue)
                    Text("• Potential Energy PE = mgh = ${String.format("%.1f", pe)} Joules", style = MaterialTheme.typography.bodySmall, color = AmberAccent)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = "Total Mechanical Energy E = KE + PE = ${String.format("%.1f", totalMechanicalEnergy)} Joules",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldGreen
                    )
                }
            }
        }
    }
}
