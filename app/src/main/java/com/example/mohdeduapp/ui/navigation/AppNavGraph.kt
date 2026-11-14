package com.example.mohdeduapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mohdeduapp.Course
import com.example.mohdeduapp.QuizScoreRecord
import com.example.mohdeduapp.ui.activity.ActivityScreen
import com.example.mohdeduapp.ui.landing.LandingScreen
import com.example.mohdeduapp.ui.practice.PracticeScreen
import com.example.mohdeduapp.ui.settings.SettingsScreen
import com.example.mohdeduapp.ui.statistics.StatisticsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    courses: MutableList<Course>,
    scoreList: MutableList<QuizScoreRecord>,
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onUnenrollCourse: (Int) -> Unit,
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.LANDING,
                    onClick = { navController.navigate(Routes.LANDING) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.ACTIVITY,
                    onClick = { navController.navigate(Routes.ACTIVITY) },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Quiz") },
                    label = { Text("Activity") },
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.PRACTICE,
                    onClick = { navController.navigate(Routes.PRACTICE) },
                    icon = { Icon(Icons.Default.School, contentDescription = "Practice") },
                    label = { Text("Practice") },
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.STATISTICS,
                    onClick = { navController.navigate(Routes.STATISTICS) },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Rank & Stats") },
                    label = { Text("Statistics") },
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.SETTINGS,
                    onClick = { navController.navigate(Routes.SETTINGS) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LANDING,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.LANDING) {
                LandingScreen(
                    courses = courses,
                    onStartQuiz = { navController.navigate(Routes.ACTIVITY) },
                    onOpenPractice = { navController.navigate(Routes.PRACTICE) },
                    onOpenStatistics = { navController.navigate(Routes.STATISTICS) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.ACTIVITY) {
                ActivityScreen(
                    courses = courses,
                    onFinishQuiz = { title, score, total ->
                        scoreList.add(0, QuizScoreRecord(title, score, total, "Just now"))
                        navController.navigate(Routes.STATISTICS)
                    },
                    onBackHome = { navController.navigate(Routes.LANDING) },
                )
            }
            composable(Routes.PRACTICE) {
                PracticeScreen(
                    courses = courses,
                    onBackHome = { navController.navigate(Routes.LANDING) },
                )
            }
            composable(Routes.STATISTICS) {
                StatisticsScreen(
                    courses = courses.filter { it.isEnrolled },
                    scoreList = scoreList,
                    onUnenroll = onUnenrollCourse,
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    darkTheme = darkTheme,
                    onDarkThemeChanged = onDarkThemeChanged,
                )
            }
        }
    }
}
