package com.example.mohdeduapp

import android.media.ToneGenerator
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mohdeduapp.ui.theme.MohdEduAppTheme

data class QuizTopic(
    val id: Int,
    val title: String,
    val category: String,
    val description: String,
    val questionCount: Int,
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
)

data class QuizScoreRecord(
    val topicTitle: String,
    val score: Int,
    val total: Int,
    val date: String,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkThemeOverride by remember { mutableStateOf(false) }
            val scoreList = remember {
                mutableStateListOf(
                    QuizScoreRecord("Jetpack Compose Quiz", 3, 3, "Today"),
                    QuizScoreRecord("Kotlin Coroutines Quiz", 2, 3, "Yesterday"),
                    QuizScoreRecord("MVVM Architecture Quiz", 3, 3, "2 days ago"),
                )
            }
            MohdEduAppTheme(darkTheme = darkThemeOverride) {
                QuizAppRoot(
                    scoreList = scoreList,
                    darkTheme = darkThemeOverride,
                    onDarkThemeChanged = { darkThemeOverride = it },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizAppRoot(
    scoreList: MutableList<QuizScoreRecord>,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var activeQuizTopic by remember { mutableStateOf<QuizTopic?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Mobile", "Algorithms", "Architecture", "Database")

    val topics = remember {
        listOf(
            QuizTopic(1, "Jetpack Compose Quiz", "Mobile", "Test your knowledge of modern declarative UI development for Android.", 10),
            QuizTopic(2, "Kotlin Coroutines & Flow", "Mobile", "Challenge yourself with asynchronous programming and StateFlow questions.", 10),
            QuizTopic(3, "Data Structures & Algorithms", "Algorithms", "Assess essential DSA patterns and problem-solving skills.", 10),
            QuizTopic(4, "MVVM & Clean Architecture", "Architecture", "Test your understanding of scalable app architecture and repositories.", 10),
            QuizTopic(5, "Room Database & SQLite", "Database", "Practice local persistence, queries, and migration questions.", 10),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            0 -> "EduQuiz - Topics"
                            1 -> "Quiz Activity"
                            2 -> "Score List"
                            3 -> "Settings"
                            else -> "EduQuiz"
                        },
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            if (activeQuizTopic == null) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Topics") },
                        label = { Text("Topics") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Quiz, contentDescription = "Quiz") },
                        label = { Text("Quiz") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Scores") },
                        label = { Text("Scores") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                    )
                }
            }
        },
    ) { innerPadding ->
        if (activeQuizTopic != null) {
            QuizScreen(
                topic = activeQuizTopic!!,
                onFinishQuiz = { score, total ->
                    scoreList.add(0, QuizScoreRecord(activeQuizTopic!!.title, score, total, "Just now"))
                    activeQuizTopic = null
                    selectedTab = 2 // Jump to scores
                },
                onCancel = { activeQuizTopic = null },
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            when (selectedTab) {
                0 -> TopicsScreen(
                    topics = topics,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    searchQuery = searchQuery,
                    onSearchQueryChanged = { searchQuery = it },
                    onStartQuiz = { activeQuizTopic = it },
                    modifier = Modifier.padding(innerPadding),
                )
                1 -> ActivityStartScreen(
                    topics = topics,
                    onStartQuiz = { activeQuizTopic = it },
                    modifier = Modifier.padding(innerPadding),
                )
                2 -> ScoresScreen(
                    scoreList = scoreList,
                    modifier = Modifier.padding(innerPadding),
                )
                3 -> SettingsScreen(
                    darkTheme = darkTheme,
                    onDarkThemeChanged = onDarkThemeChanged,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
fun TopicsScreen(
    topics: List<QuizTopic>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onStartQuiz: (QuizTopic) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTopics = topics.filter { topic ->
        val matchesCategory = (selectedCategory == "All") || (topic.category == selectedCategory)
        val matchesSearch = searchQuery.isBlank() || topic.title.contains(searchQuery, ignoreCase = true) || topic.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Welcome to EduQuiz 🧠",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Test your knowledge with random quizzes and track your score history.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search quiz topics...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) },
                    )
                }
            }
        }

        item {
            Text(
                text = "Available Quiz Topics (${filteredTopics.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        items(filteredTopics) { topic ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartQuiz(topic) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                            Text(
                                text = topic.category,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                        Text(
                            text = "${topic.questionCount} Questions Pool",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityStartScreen(
    topics: List<QuizTopic>,
    onStartQuiz: (QuizTopic) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Text(
                text = "Select Quiz Topic to Begin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Each quiz dynamically selects 3 random questions from a pool of 10.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }

        items(topics) { topic ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartQuiz(topic) },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(topic.title, fontWeight = FontWeight.Bold)
                        Text(topic.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Button(onClick = { onStartQuiz(topic) }) {
                        Text("Start Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(
    topic: QuizTopic,
    onFinishQuiz: (Int, Int) -> Unit,
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
            Button(onClick = onCancel) { Text("Exit Quiz") }
            Text(topic.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                    Button(onClick = { onFinishQuiz(correctCount, randomQuestions.size) }) {
                        Text("Save Score & View History")
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
                text = "Question ${currentIndex + 1} of ${randomQuestions.size} (Randomly Selected)",
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

@Composable
fun ScoresScreen(
    scoreList: List<QuizScoreRecord>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Text(
                text = "Quiz Score History List",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Track your quiz performance and score metrics.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }

        if (scoreList.isEmpty()) {
            item {
                Text("No quiz scores recorded yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            items(scoreList) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(record.topicTitle, fontWeight = FontWeight.Bold)
                            Text("Date: ${record.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                        Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                            Text(
                                text = "Score: ${record.score}/${record.total}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        item {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Dark Theme", fontWeight = FontWeight.Bold)
                                Text("Use dark color scheme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = onDarkThemeChanged,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Push Notifications", fontWeight = FontWeight.Bold)
                                Text("Receive study reminders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("About EduQuiz", fontWeight = FontWeight.Bold)
                            Text("Version 1.0.0 • Built with Jetpack Compose", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
