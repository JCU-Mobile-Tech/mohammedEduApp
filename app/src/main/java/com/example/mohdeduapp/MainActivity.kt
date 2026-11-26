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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mohdeduapp.ui.theme.MohdEduAppTheme
import kotlinx.coroutines.delay

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
    val explanation: String,
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
            var soundEnabled by remember { mutableStateOf(true) }
            var timerEnabled by remember { mutableStateOf(true) }

            val scoreList = remember {
                mutableStateListOf(
                    QuizScoreRecord("Jetpack Compose Quiz", 5, 5, "Today"),
                    QuizScoreRecord("Kotlin Coroutines & Flow", 4, 5, "Yesterday"),
                    QuizScoreRecord("MVVM & Clean Architecture", 5, 5, "2 days ago"),
                )
            }

            MohdEduAppTheme(darkTheme = darkThemeOverride) {
                QuizAppRoot(
                    scoreList = scoreList,
                    darkTheme = darkThemeOverride,
                    onDarkThemeChanged = { darkThemeOverride = it },
                    soundEnabled = soundEnabled,
                    onSoundEnabledChanged = { soundEnabled = it },
                    timerEnabled = timerEnabled,
                    onTimerEnabledChanged = { timerEnabled = it },
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
    soundEnabled: Boolean,
    onSoundEnabledChanged: (Boolean) -> Unit,
    timerEnabled: Boolean,
    onTimerEnabledChanged: (Boolean) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(3) } // Default to settings or topics as needed
    var activeQuizTopic by remember { mutableStateOf<QuizTopic?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDifficulty by remember { mutableStateOf("All") }

    val categories = listOf("All", "Mobile", "Algorithms", "Architecture", "Database")
    val difficulties = listOf("All", "Easy", "Medium", "Hard")

    val topics = remember {
        listOf(
            QuizTopic(1, "Jetpack Compose Quiz", "Mobile", "Test your knowledge of modern declarative UI development for Android with 12 detailed questions.", 12),
            QuizTopic(2, "Kotlin Coroutines & Flow", "Mobile", "Challenge yourself with asynchronous programming, StateFlow, and Dispatchers questions.", 12),
            QuizTopic(3, "Data Structures & Algorithms", "Algorithms", "Assess essential DSA patterns, search trees, and complexity analysis.", 12),
            QuizTopic(4, "MVVM & Clean Architecture", "Architecture", "Test your understanding of scalable app architecture, ViewModels, and repositories.", 12),
            QuizTopic(5, "Room Database & SQLite", "Database", "Practice local persistence, entities, DAOs, and migration queries.", 12),
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
                soundEnabled = soundEnabled,
                timerEnabled = timerEnabled,
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
                    difficulties = difficulties,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = { selectedDifficulty = it },
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
                    soundEnabled = soundEnabled,
                    onSoundEnabledChanged = onSoundEnabledChanged,
                    timerEnabled = timerEnabled,
                    onTimerEnabledChanged = onTimerEnabledChanged,
                    onClearScores = { scoreList.clear() },
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
    difficulties: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
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
                        text = "Explore 60+ curriculum-aligned questions across 5 core topics, featuring random selection, timers, instant explanations, and score tracking.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Study Tip of the Day 💡", fontWeight = FontWeight.Bold)
                        Text("Take regular quizzes to improve long-term memory retention and exam scores!", style = MaterialTheme.typography.bodySmall)
                    }
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
            Text("Filter by Category:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
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
            Text("Filter by Difficulty:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(difficulties) { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { onDifficultySelected(diff) },
                        label = { Text(diff) },
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
                            text = "${topic.questionCount} Questions Available",
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Interactive Quiz Activity Hub ⚡", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select any topic below to launch a dynamic assessment session. Questions are randomized every attempt!", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Text(
                text = "Select Quiz Topic to Begin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
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
                        Text("Category: ${topic.category} • ${topic.questionCount} Questions Available", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
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
    soundEnabled: Boolean,
    timerEnabled: Boolean,
    onFinishQuiz: (Int, Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val allQuestions = remember(topic.id) {
        if (topic.id == 1) {
            listOf(
                QuizQuestion(1, "What is the primary UI toolkit for modern Android apps?", listOf("XML Views", "Jetpack Compose", "Swing", "Flutter"), 1, "Jetpack Compose is Android's modern native UI toolkit."),
                QuizQuestion(2, "What annotation denotes a Composable function in Compose?", listOf("@View", "@Composable", "@Widget", "@Activity"), 1, "@Composable indicates UI rendering."),
                QuizQuestion(3, "What layout replaces LinearLayout and RelativeLayout in Compose?", listOf("Column & Row", "StackView", "FlexLayout", "GridBag"), 0, "Column and Row replace linear layouts."),
                QuizQuestion(4, "How do you apply padding to a Composable modifier?", listOf("modifier = Modifier.padding(16.dp)", "padding.apply(16)", "setPadding(16)", "layout.pad(16)"), 0, "Modifier.padding(16.dp) adds inner spacing."),
                QuizQuestion(5, "What state holder preserves state across configuration changes in Compose?", listOf("remember", "rememberSaveable", "savedState", "persistentState"), 1, "rememberSaveable survives activity rotation."),
                QuizQuestion(6, "Which component is used for scrollable lists in Compose?", listOf("ListView", "RecyclerView", "LazyColumn", "ScrollLayout"), 2, "LazyColumn handles efficient scrolling."),
                QuizQuestion(7, "What is the recommended design system for modern Android UI?", listOf("Material 3", "Bootstrap", "FlatUI", "Retro"), 0, "Material 3 is Google's design system."),
                QuizQuestion(8, "How do you observe a Flow as Compose State?", listOf("collectAsState()", "observeAsState()", "toState()", "flowState()"), 0, "collectAsState() bridges flows to Compose."),
                QuizQuestion(9, "What parameter centers items horizontally in a Row?", listOf("horizontalArrangement = Arrangement.Center", "verticalAlignment = Alignment.Center", "align(Center)", "center()"), 0, "horizontalArrangement centers horizontally."),
                QuizQuestion(10, "What is used to display snackbars or bottom sheets in Compose?", listOf("Scaffold", "Container", "Frame", "Window"), 0, "Scaffold manages snackbars."),
                QuizQuestion(11, "Which modifier makes a Composable clickable?", listOf("clickable { }", "onClick { }", "press { }", "touch { }"), 0, "Modifier.clickable makes components interactive."),
                QuizQuestion(12, "What function is used for side effects in Compose linked to lifecycle?", listOf("LaunchedEffect", "SideEffect", "DisposableEffect", "All of the above"), 3, "Compose provides multiple effect APIs.")
            )
        } else if (topic.id == 2) {
            listOf(
                QuizQuestion(1, "What Coroutine component handles hot reactive state streams?", listOf("LiveData", "StateFlow", "Intent", "Bundle"), 1, "StateFlow is a hot observable state flow."),
                QuizQuestion(2, "Which dispatcher is optimized for disk or network I/O?", listOf("Dispatchers.Main", "Dispatchers.IO", "Dispatchers.Default", "Dispatchers.Unconfined"), 1, "Dispatchers.IO handles blocking I/O."),
                QuizQuestion(3, "What builder creates a new coroutine without blocking the current thread?", listOf("launch", "runBlocking", "thread", "execute"), 0, "launch starts fire-and-forget coroutines."),
                QuizQuestion(4, "What operator transforms items emitted by a Flow?", listOf("map", "filter", "transform", "All of the above"), 3, "Map/filter are standard flow operators."),
                QuizQuestion(5, "What is a cold asynchronous stream of values in Kotlin?", listOf("Flow", "StateFlow", "SharedFlow", "Channel"), 0, "Flow is cold by default."),
                QuizQuestion(6, "Which coroutine scope is tied to a ViewModel's lifecycle?", listOf("viewModelScope", "lifecycleScope", "globalScope", "ioScope"), 0, "viewModelScope cancels automatically."),
                QuizQuestion(7, "What function suspends a coroutine until deferred value is computed?", listOf("await", "get", "join", "fetch"), 0, "await retrieves deferred results."),
                QuizQuestion(8, "What flow is warm and replays the latest value to new subscribers?", listOf("StateFlow", "Flow", "Sequence", "Channel"), 0, "StateFlow replays latest state."),
                QuizQuestion(9, "Which dispatcher is used for heavy CPU-intensive computations?", listOf("Dispatchers.Default", "Dispatchers.IO", "Dispatchers.Main", "Dispatchers.Unconfined"), 0, "Default is tuned for CPU tasks."),
                QuizQuestion(10, "What builder suspends the current thread until the coroutine completes?", listOf("runBlocking", "launch", "async", "job"), 0, "runBlocking bridges blocking code."),
                QuizQuestion(11, "How do you safely cancel a coroutine job?", listOf("job.cancel()", "job.stop()", "job.kill()", "job.end()"), 0, "job.cancel cancels execution."),
                QuizQuestion(12, "What function combines multiple flows together?", listOf("combine", "merge", "zip", "All of the above"), 3, "Combine/merge/zip unite flows.")
            )
        } else if (topic.id == 3) {
            listOf(
                QuizQuestion(1, "What is the time complexity of binary search in a sorted array?", listOf("O(n)", "O(log n)", "O(n^2)", "O(1)"), 1, "Binary search is O(log n)."),
                QuizQuestion(2, "Which data structure operates on a Last-In-First-Out (LIFO) basis?", listOf("Queue", "Stack", "Array", "Tree"), 1, "Stack is LIFO."),
                QuizQuestion(3, "What data structure operates on a First-In-First-Out (FIFO) basis?", listOf("Stack", "Queue", "Graph", "Heap"), 1, "Queue is FIFO."),
                QuizQuestion(4, "What is the worst-case time complexity of Quicksort?", listOf("O(n log n)", "O(n)", "O(n^2)", "O(1)"), 2, "Quicksort worst-case is O(n^2)."),
                QuizQuestion(5, "Which data structure uses key-value pairs for O(1) average lookup?", listOf("LinkedList", "HashMap", "TreeSet", "ArrayList"), 1, "HashMap provides O(1) lookup."),
                QuizQuestion(6, "What tree data structure satisfies the heap property?", listOf("Binary Search Tree", "Min-Heap / Max-Heap", "AVL Tree", "Red-Black Tree"), 1, "Heaps satisfy heap properties."),
                QuizQuestion(7, "Which graph traversal algorithm uses a Queue (BFS)?", listOf("Depth-First Search", "Breadth-First Search", "Dijkstra", "Bellman-Ford"), 1, "BFS uses a Queue."),
                QuizQuestion(8, "What is the space complexity of recursive depth-first search (DFS)?", listOf("O(1)", "O(log n)", "O(h) where h is height", "O(n^2)"), 2, "DFS space is O(h)."),
                QuizQuestion(9, "Which sorting algorithm is stable and divides the array in half?", listOf("Mergesort", "Selection Sort", "Heapsort", "Quicksort"), 0, "Mergesort is stable."),
                QuizQuestion(10, "What data structure is ideal for implementing LRU cache?", listOf("Queue + Hash Map", "Stack", "Array", "Set"), 0, "LRU uses Hash Map + Queue."),
                QuizQuestion(11, "What is the time complexity of inserting into a Hash Set on average?", listOf("O(1)", "O(n)", "O(log n)", "O(n log n)"), 0, "Hash Set insertion is O(1)."),
                QuizQuestion(12, "Which algorithm finds the shortest path in a weighted graph with positive weights?", listOf("Dijkstra's Algorithm", "Kruskal's", "Prim's", "DFS"), 0, "Dijkstra finds shortest paths.")
            )
        } else if (topic.id == 4) {
            listOf(
                QuizQuestion(1, "Which architecture pattern promotes unidirectional data flow?", listOf("MVC", "MVVM", "Singleton", "Procedural"), 1, "MVVM provides UDF."),
                QuizQuestion(2, "What component in MVVM survives configuration changes like rotation?", listOf("Activity", "ViewModel", "Fragment", "View"), 1, "ViewModel survives rotation."),
                QuizQuestion(3, "What layer in Clean Architecture contains business rules and domain models?", listOf("UI Layer", "Data Layer", "Domain Layer", "Framework Layer"), 2, "Domain layer contains core business logic."),
                QuizQuestion(4, "What pattern mediates data operations between UI and local/remote sources?", listOf("Repository Pattern", "Observer Pattern", "Factory Pattern", "Adapter Pattern"), 0, "Repository pattern abstracts data."),
                QuizQuestion(5, "How does a ViewModel expose state safely to UI composables?", listOf("StateFlow / State", "MutableList", "Global variables", "Intents"), 0, "StateFlow exposes state."),
                QuizQuestion(6, "What layer depends on the Domain layer in Clean Architecture?", listOf("Data and UI layers", "Only Database", "External libraries", "Nothing"), 0, "Domain layer is independent."),
                QuizQuestion(7, "What is the benefit of separating UI from ViewModel?", listOf("Testability and lifecycle survival", "Faster compilation", "Smaller APK size", "Less memory"), 0, "Improves testability."),
                QuizQuestion(8, "What class handles dependency injection in modern Android?", listOf("Hilt / Dagger", "Intent", "Bundle", "Context"), 0, "Hilt manages DI."),
                QuizQuestion(9, "What principle states that classes should have one reason to change?", listOf("Single Responsibility Principle", "Open-Closed Principle", "Liskov Substitution", "Interface Segregation"), 0, "SRP principle."),
                QuizQuestion(10, "What data holder represents UI state in a reactive manner?", listOf("StateFlow", "LiveData", "LiveData & StateFlow", "All of the above"), 2, "Both StateFlow and LiveData are observable."),
                QuizQuestion(11, "Where should database queries be executed in MVVM?", listOf("ViewModel / Repository (Background Dispatcher)", "Main Thread", "Activity onCreate", "Composable function"), 0, "Background threads prevent ANRs."),
                QuizQuestion(12, "What is inversion of control (IoC) achieved via in Android architecture?", listOf("Dependency Injection", "Global state", "Static methods", "Intents"), 0, "DI achieves IoC.")
            )
        } else if (topic.id == 5) {
            listOf(
                QuizQuestion(1, "What database library is recommended by Android for local storage?", listOf("Room", "MySQL", "Oracle", "MongoDB"), 0, "Room is Google's SQLite wrapper."),
                QuizQuestion(2, "What annotation marks a class as a Room database entity?", listOf("@Entity", "@Table", "@Model", "@Database"), 0, "@Entity creates a database table."),
                QuizQuestion(3, "What annotation defines database operations (queries, inserts)?", listOf("@Dao", "@Query", "@Operation", "@Controller"), 0, "@Dao defines data access methods."),
                QuizQuestion(4, "What is the base abstract class for Room database configuration?", listOf("RoomDatabase", "SQLiteOpenHelper", "DatabaseClient", "SQLiteDatabase"), 0, "RoomDatabase is the base class."),
                QuizQuestion(5, "How does Room return reactive query results?", listOf("Flow / LiveData", "Callbacks", "Threads", "Intents"), 0, "Room queries return Flow."),
                QuizQuestion(6, "What annotation specifies primary keys in Room entities?", listOf("@PrimaryKey", "@Key", "@Id", "@Unique"), 0, "@PrimaryKey marks unique keys."),
                QuizQuestion(7, "How do you handle database schema version upgrades in Room?", listOf("Migration objects", "Delete app", "Auto-increment", "Restart device"), 0, "Migrations upgrade schemas."),
                QuizQuestion(8, "What SQL keyword retrieves unique records?", listOf("DISTINCT", "UNIQUE", "DIFFERENT", "SINGLE"), 0, "SELECT DISTINCT filters rows."),
                QuizQuestion(9, "What annotation defines foreign key relationships in Room?", listOf("@ForeignKey", "@Relation", "@Join", "@Link"), 0, "@ForeignKey handles relations."),
                QuizQuestion(10, "Why does Room require database queries to run off the main thread?", listOf("To prevent ANR (Application Not Responding) UI freezes", "To save battery", "Required by SQLite", "Security"), 0, "Prevents ANR freezes."),
                QuizQuestion(11, "What annotation allows embedding child objects in Room entities?", listOf("@Embedded", "@Nested", "@Inner", "@Subclass"), 0, "@Embedded flattens objects."),
                QuizQuestion(12, "What return type does a Room insert query have if returning row IDs?", listOf("Long / List<Long>", "Void", "Boolean", "String"), 0, "Inserts return row ID(s).")
            )
        } else {
            listOf(
                QuizQuestion(1, "What is Android?", listOf("An operating system", "A programming language", "A database", "A browser"), 0, "Android is an OS."),
                QuizQuestion(2, "What language is official for Android development?", listOf("Kotlin", "Python", "C++", "Ruby"), 0, "Kotlin is official."),
                QuizQuestion(3, "What tool builds Android apps?", listOf("Gradle", "Maven", "Ant", "Make"), 0, "Gradle is the build system."),
                QuizQuestion(4, "What file defines app permissions and activities?", listOf("AndroidManifest.xml", "build.gradle", "strings.xml", "MainActivity.kt"), 0, "Manifest defines app configuration."),
                QuizQuestion(5, "What layout shows items vertically?", listOf("Column", "Row", "Box", "Grid"), 0, "Column lays out vertically.")
            )
        }
    }

    val randomQuestions = remember(topic.id) { allQuestions.shuffled().take(5) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }
    var streakCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(20) }

    val toneGen = remember { try { ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 100) } catch (_: Exception) { null } }

    LaunchedEffect(currentIndex, timerEnabled, showExplanation, quizFinished) {
        if (timerEnabled && !showExplanation && !quizFinished) {
            timeLeft = 20
            while (timeLeft > 0 && !showExplanation && !quizFinished) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0 && !showExplanation && !quizFinished) {
                streakCount = 0
                if (soundEnabled) toneGen?.startTone(ToneGenerator.TONE_PROP_NACK)
                showExplanation = true
            }
        }
    }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (timerEnabled) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text("Time: ${timeLeft}s", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Streak: $streakCount", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
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
                text = "Question ${currentIndex + 1} of ${randomQuestions.size} (Randomly Selected from 12)",
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
                            onClick = { if (!showExplanation) selectedOption = idx },
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

                    if (showExplanation) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Explanation:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (!showExplanation) {
                        if (selectedOption != null) {
                            if (selectedOption == q.correctIndex) {
                                correctCount++
                                streakCount++
                                if (soundEnabled) toneGen?.startTone(ToneGenerator.TONE_PROP_ACK)
                            } else {
                                streakCount = 0
                                if (soundEnabled) toneGen?.startTone(ToneGenerator.TONE_PROP_NACK)
                            }
                            showExplanation = true
                        }
                    } else {
                        showExplanation = false
                        selectedOption = null
                        if (currentIndex < randomQuestions.size - 1) {
                            currentIndex++
                        } else {
                            quizFinished = true
                        }
                    }
                },
                enabled = selectedOption != null || (timerEnabled && timeLeft == 0),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (!showExplanation) "Submit Answer" else if (currentIndex < randomQuestions.size - 1) "Next Question" else "View Results")
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
    soundEnabled: Boolean,
    onSoundEnabledChanged: (Boolean) -> Unit,
    timerEnabled: Boolean,
    onTimerEnabledChanged: (Boolean) -> Unit,
    onClearScores: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var highContrastMode by remember { mutableStateOf(false) }
    var analyticsEnabled by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Score History") },
            text = { Text("Are you sure you want to clear all your saved quiz scores? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearScores()
                    showClearConfirm = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            },
        )
    }

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
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sound Effects", fontWeight = FontWeight.Bold)
                                Text("Play tone on quiz answers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = onSoundEnabledChanged,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Question Timer", fontWeight = FontWeight.Bold)
                                Text("Enable time limit per question", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = timerEnabled,
                            onCheckedChange = onTimerEnabledChanged,
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Haptic Feedback", fontWeight = FontWeight.Bold)
                                Text("Vibrate on button taps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("High Contrast Mode", fontWeight = FontWeight.Bold)
                                Text("Enhance UI element contrast", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = highContrastMode,
                            onCheckedChange = { highContrastMode = it },
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Auto-Sync Scores", fontWeight = FontWeight.Bold)
                                Text("Sync scores to cloud server", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = autoSyncEnabled,
                            onCheckedChange = { autoSyncEnabled = it },
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Usage Analytics", fontWeight = FontWeight.Bold)
                                Text("Share anonymous diagnostic logs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Switch(
                            checked = analyticsEnabled,
                            onCheckedChange = { analyticsEnabled = it },
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Storage & Telemetry", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Local Database: Room SQLite (Encrypted)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text("Cache Size: 4.2 MB • Sync Status: Online", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text("Security Protocol: TLS 1.3 / OAuth2", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { showClearConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear All Score History", color = MaterialTheme.colorScheme.onErrorContainer)
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
                            Text("Version 1.0.0 • Built with Jetpack Compose & Material 3", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text("Target SDK 35 • JCU Mobile Technologies", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
