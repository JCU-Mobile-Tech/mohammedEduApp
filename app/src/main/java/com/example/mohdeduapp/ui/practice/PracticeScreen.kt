package com.example.mohdeduapp.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mohdeduapp.Course
import com.example.mohdeduapp.di.AppContainer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    courses: List<Course>,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()) }
    var mathExpression by remember { mutableStateOf("2 * (3 + 4)") }
    var apiResult by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Track completed lectures per course (set of lesson indices)
    val completedLessons = remember { mutableStateMapOf<Int, MutableSet<Int>>() }

    val context = LocalContext.current
    val appContainer = remember { AppContainer(context) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onBackHome) { Text("← Home") }
                Text("Practice & Lectures", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        item {
            Text("Select Course for Practice:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(courses) { course ->
                    FilterChip(
                        selected = selectedCourse?.id == course.id,
                        onClick = { selectedCourse = course },
                        label = { Text(course.title) },
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Live Math Web API Solver (Retrofit)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Evaluates math expressions remotely via api.mathjs.org",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mathExpression,
                        onValueChange = { mathExpression = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Math Expression (e.g., sqrt(16) + 5)") },
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isLoading = true
                            scope.launch {
                                apiResult = appContainer.mathCheckRepository.evaluateExpression(mathExpression)
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Evaluate via Web API")
                        }
                    }
                    if (apiResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "API Result: $apiResult",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        if (selectedCourse != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = selectedCourse!!.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedCourse!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Interactive Lectures & Practice Labs (${selectedCourse!!.lessonsCount} Modules)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Tap any lecture to mark it as complete!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            items(selectedCourse!!.lessonsCount) { index ->
                val courseId = selectedCourse!!.id
                val completedSet = completedLessons.getOrPut(courseId) { mutableSetOf() }
                val isDone = completedSet.contains(index)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isDone) completedSet.remove(index) else completedSet.add(index)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Lecture ${index + 1}: Study Module & Interactive Lab",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = if (isDone) "Status: Completed ✓" else "Status: In Progress (Tap to complete)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                )
                            }
                        }
                        Text("${index + 10} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
