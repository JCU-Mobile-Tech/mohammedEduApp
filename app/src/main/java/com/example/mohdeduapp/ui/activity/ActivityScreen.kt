package com.example.mohdeduapp.ui.activity

import android.media.ToneGenerator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mohdeduapp.Course
import com.example.mohdeduapp.QuizQuestion

@Composable
fun ActivityScreen(
    courses: List<Course>,
    onFinishQuiz: (String, Int, Int) -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()) }
    var startQuiz by remember { mutableStateOf(false) }

    if (!startQuiz || selectedCourse == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onBackHome) { Text("← Home") }
                Text("Quiz Activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            Text("Select a course to take a 3-question random quiz (from a pool of 10 questions):", style = MaterialTheme.typography.bodyMedium)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(courses.size) { index ->
                    val course = courses[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCourse = course
                                startQuiz = true
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(course.title, fontWeight = FontWeight.Bold)
                                Text(course.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                        }
                    }
                }
            }
        }
    } else {
        QuizRunningView(
            course = selectedCourse!!,
            onFinish = { score, total ->
                onFinishQuiz(selectedCourse!!.title, score, total)
            },
            onCancel = { startQuiz = false },
            modifier = modifier,
        )
    }
}

@Composable
fun QuizRunningView(
    course: Course,
    onFinish: (Int, Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allQuestions = remember {
        listOf(
            QuizQuestion(1, "What is the primary UI toolkit for modern Android apps?", listOf("XML Views", "Jetpack Compose", "Swing", "Flutter"), 1),
            QuizQuestion(2, "Which architecture pattern promotes unidirectional data flow?", listOf("MVC", "MVVM", "Singleton", "Procedural"), 1),
            QuizQuestion(3, "What Coroutine component handles hot reactive state streams?", listOf("LiveData", "StateFlow", "Intent", "Bundle"), 1),
            QuizQuestion(4, "What database library is recommended by Android for local storage?", listOf("Room", "MySQL", "Oracle", "MongoDB"), 0),
            QuizQuestion(5, "Which worker class executes background tasks in WorkManager?", listOf("Thread", "CoroutineWorker", "AsyncTask", "Service"), 1),
            QuizQuestion(6, "What annotation denotes a Composable function in Compose?", listOf("@View", "@Composable", "@Widget", "@Activity"), 1),
            QuizQuestion(7, "Which component manages background work persisting across reboots?", listOf("WorkManager", "Handler", "Timer", "Toast"), 0),
            QuizQuestion(8, "What layout replaces LinearLayout and RelativeLayout in Compose?", listOf("Column & Row", "StackView", "FlexLayout", "GridBag"), 0),
            QuizQuestion(9, "What API securely stores key-value preferences in Android?", listOf("DataStore", "Plaintext", "GlobalVariable", "Cookie"), 0),
            QuizQuestion(10, "What is the recommended design system for modern Android UI?", listOf("Material 3", "Bootstrap", "FlatUI", "Retro"), 0),
        )
    }

    val randomQuestions = remember { allQuestions.shuffled().take(3) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }

    val toneGen = remember { try { ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 100) } catch (_: Exception) { null } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = onCancel) { Text("Exit") }
            Text("Quiz: ${course.title}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (quizFinished) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Quiz Completed! 🎉", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your Score: $correctCount / ${randomQuestions.size}", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onFinish(correctCount, randomQuestions.size) }) {
                        Text("Save & View Statistics")
                    }
                }
            }
        } else {
            val q = randomQuestions[currentIndex]
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / randomQuestions.size },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "Question ${currentIndex + 1} of ${randomQuestions.size} (Randomly Selected from 10)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = q.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    q.options.forEachIndexed { idx, option ->
                        val isSelected = selectedOption == idx
                        OutlinedButton(
                            onClick = { selectedOption = idx },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            ),
                        ) {
                            Text(option, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (selectedOption != null) {
                        if (selectedOption == q.correctIndex) {
                            correctCount++
                            toneGen?.startTone(ToneGenerator.TONE_PROP_ACK)
                        } else {
                            toneGen?.startTone(ToneGenerator.TONE_PROP_NACK)
                        }
                        selectedOption = null
                        if (currentIndex < randomQuestions.size - 1) {
                            currentIndex++
                        } else {
                            quizFinished = true
                        }
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (currentIndex < randomQuestions.size - 1) "Next Question" else "Finish Quiz")
            }
        }
    }
}
