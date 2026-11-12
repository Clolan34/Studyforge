package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiTutorService
import com.example.ai.TutorMode
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun AiTutorScreen(
    aiTutorService: AiTutorService,
    initialPrompt: String = ""
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(TutorMode.SOCRATIC_HINT) }
    var inputText by remember { mutableStateOf(initialPrompt) }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = "msg_0",
                sender = "AI",
                text = "Welcome to the StudyForge AI Tutor. I use the Socratic method to guide you through CSPC 132 (Physics for Computing) and CSSD 101 (Programming and Problem Solving).\n\nSelect a mode above or ask about any formula, derivation, or C++ algorithm."
            )
        )
    }

    val listState = rememberLazyListState()

    // Handle initial prompt passed from lesson
    LaunchedEffect(initialPrompt) {
        if (initialPrompt.isNotBlank()) {
            inputText = initialPrompt
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_tutor_screen")
    ) {
        // Top Header & Mode Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Indigo600.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Indigo400, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text(
                            text = "Gemini Socratic AI Tutor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Powered by Firebase AI Logic (gemini-3.8-flash)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mode Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TutorMode.entries) { mode ->
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { selectedMode = mode },
                        label = { Text(mode.title, fontSize = 11.sp) }
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // Quick Starter Prompts
        if (messages.size <= 2) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AssistChip(
                        onClick = { inputText = "Explain Dot Product vs Cross Product with practical computing examples." },
                        label = { Text("Dot vs Cross Product", fontSize = 11.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { inputText = "How do I choose between the 4 kinematic acceleration equations?" },
                        label = { Text("Choosing Kinematic Formulas", fontSize = 11.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { inputText = "What is the difference between an IPO Chart and a PAC?" },
                        label = { Text("IPO vs PAC Charts", fontSize = 11.sp) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { inputText = "Why does pass-by-value not change the original variable in C++?" },
                        label = { Text("Pass by Value vs Reference", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Chat Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Indigo400)
                                Text("AI Tutor is formulating Socratic response...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask a physics formula or programming question...", fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_tutor_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 3
                )

                IconButton(
                    onClick = {
                        val query = inputText.trim()
                        if (query.isNotBlank() && !isLoading) {
                            inputText = ""
                            messages.add(ChatMessage(id = "user_${System.currentTimeMillis()}", sender = "USER", text = query))
                            isLoading = true

                            coroutineScope.launch {
                                listState.animateScrollToItem(messages.size - 1)
                                val history = messages.takeLast(4).map { it.sender to it.text }
                                val (isRealAi, responseText) = aiTutorService.askTutor(
                                    studentQuery = query,
                                    mode = selectedMode,
                                    conversationHistory = history
                                )
                                messages.add(ChatMessage(id = "ai_${System.currentTimeMillis()}", sender = "AI", text = responseText))
                                isLoading = false
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) Indigo600 else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("send_ai_query_btn"),
                    enabled = inputText.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "USER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 320.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Indigo600 else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) Indigo500 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Indigo400, modifier = Modifier.size(16.dp))
                        Text(
                            text = "StudyForge Socratic Tutor",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Indigo400
                        )
                    }
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
