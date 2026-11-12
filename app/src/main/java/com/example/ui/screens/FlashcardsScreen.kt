package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StudyForgeRepository
import com.example.model.Flashcard
import com.example.model.FlashcardCategory
import com.example.model.ReviewRating
import com.example.ui.theme.*

@Composable
fun FlashcardsScreen(
    repository: StudyForgeRepository
) {
    val allCards by repository.flashcards.collectAsState()
    var selectedCategory by remember { mutableStateOf<FlashcardCategory?>(null) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val filteredCards = remember(allCards, selectedCategory) {
        if (selectedCategory == null) allCards else allCards.filter { it.category == selectedCategory }
    }

    val currentCard = filteredCards.getOrNull(currentIndex.coerceIn(0, (filteredCards.size - 1).coerceAtLeast(0)))

    // 3D rotation animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_flip_animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Spaced Repetition & Active Recall",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Scientifically scheduled revision for physics formulas and algorithmic concepts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null; currentIndex = 0; isFlipped = false },
                        label = { Text("All (${allCards.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == FlashcardCategory.FORMULA,
                        onClick = { selectedCategory = FlashcardCategory.FORMULA; currentIndex = 0; isFlipped = false },
                        label = { Text("Formulas") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == FlashcardCategory.DEFINITION,
                        onClick = { selectedCategory = FlashcardCategory.DEFINITION; currentIndex = 0; isFlipped = false },
                        label = { Text("Definitions") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == FlashcardCategory.CONCEPT,
                        onClick = { selectedCategory = FlashcardCategory.CONCEPT; currentIndex = 0; isFlipped = false },
                        label = { Text("Concepts") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == FlashcardCategory.CODE_SYNTAX,
                        onClick = { selectedCategory = FlashcardCategory.CODE_SYNTAX; currentIndex = 0; isFlipped = false },
                        label = { Text("C++ Syntax") }
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentCard != null) {
                // Card Counter
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Card ${currentIndex + 1} of ${filteredCards.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${currentCard.courseCode} • ${currentCard.topic}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Interactive 3D Flip Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (rotation > 90f) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.5.dp,
                                if (isFlipped) EmeraldGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { isFlipped = !isFlipped }
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation <= 90f) {
                            // FRONT OF CARD
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = currentCard.category.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = currentCard.front,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 24.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "Tap to reveal answer",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            // BACK OF CARD (Rotated back)
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { rotationY = 180f },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "ANSWER & EXPLANATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = currentCard.back,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )

                                if (currentCard.formula != null) {
                                    Text(
                                        text = currentCard.formula,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = AmberAccent
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(1.dp))
                                }
                            }
                        }
                    }
                }

                // Spaced Repetition Rating Buttons
                item {
                    Text(
                        text = "How well did you recall this concept?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                repository.reviewFlashcard(currentCard.id, ReviewRating.AGAIN)
                                isFlipped = false
                                if (currentIndex < filteredCards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Again (1d)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                repository.reviewFlashcard(currentCard.id, ReviewRating.GOOD)
                                isFlipped = false
                                if (currentIndex < filteredCards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Good (3d)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                repository.reviewFlashcard(currentCard.id, ReviewRating.EASY)
                                isFlipped = false
                                if (currentIndex < filteredCards.size - 1) currentIndex++ else currentIndex = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Easy (7d)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                item {
                    Text("No flashcards found for selected filter.")
                }
            }
        }
    }
}
