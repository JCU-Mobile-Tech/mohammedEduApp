package com.example.mohdeduapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.mohdeduapp.ui.navigation.AppNavGraph
import com.example.mohdeduapp.ui.theme.MohdEduAppTheme

data class Course(
    val id: Int,
    val title: String,
    val category: String,
    val progress: Float,
    val duration: String,
    val lessonsCount: Int,
    val description: String,
    val isEnrolled: Boolean = false,
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
)

data class QuizScoreRecord(
    val courseTitle: String,
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
            val navController = rememberNavController()

            val courses = remember {
                mutableStateListOf(
                    Course(1, "Jetpack Compose Masterclass", "Mobile", 0.75f, "6 hours", 24, "Learn modern declarative UI development for Android from scratch to advanced.", true),
                    Course(2, "Kotlin Coroutines & Flow", "Mobile", 0.40f, "4 hours", 16, "Master asynchronous programming, Dispatchers, StateFlow, and SharedFlow.", true),
                    Course(3, "Data Structures & Algorithms", "Algorithms", 0.20f, "12 hours", 45, "Essential DSA patterns for technical interviews and efficient software engineering.", false),
                    Course(4, "MVVM & Clean Architecture", "Architecture", 0.90f, "5 hours", 18, "Build scalable, testable Android apps with unidirectional data flow and repositories.", true),
                    Course(5, "Room Database & SQLite", "Database", 0.10f, "3 hours", 10, "Persistent data storage, type converters, migrations, and reactive queries.", false),
                )
            }

            val scoreList = remember {
                mutableStateListOf(
                    QuizScoreRecord("Jetpack Compose Masterclass", 3, 3, "Today"),
                    QuizScoreRecord("Kotlin Coroutines & Flow", 2, 3, "Yesterday"),
                    QuizScoreRecord("MVVM & Clean Architecture", 3, 3, "2 days ago"),
                )
            }

            MohdEduAppTheme(darkTheme = darkThemeOverride) {
                AppNavGraph(
                    navController = navController,
                    courses = courses,
                    scoreList = scoreList,
                    darkTheme = darkThemeOverride,
                    onDarkThemeChanged = { darkThemeOverride = it },
                    onUnenrollCourse = { courseId ->
                        val index = courses.indexOfFirst { it.id == courseId }
                        if (index != -1) {
                            courses[index] = courses[index].copy(isEnrolled = false)
                        }
                    },
                )
            }
        }
    }
}
