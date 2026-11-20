package com.example.mohdeduapp.ui.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mohdeduapp.Course
import com.example.mohdeduapp.QuizScoreRecord

@Composable
fun StatisticsScreen(
    courses: List<Course>,
    scoreList: List<QuizScoreRecord>,
    onUnenroll: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirmDialog by remember { mutableStateOf<Course?>(null) }

    if (showDeleteConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Confirm Course Removal") },
            text = { Text("Are you sure you want to remove '${showDeleteConfirmDialog!!.title}' from your enrolled courses?") },
            confirmButton = {
                TextButton(onClick = {
                    onUnenroll(showDeleteConfirmDialog!!.id)
                    showDeleteConfirmDialog = null
                }) {
                    Text("Remove", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Student Rank: Gold Scholar 🏆",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enrolled Courses: ${courses.size} • Total Quizzes Completed: ${scoreList.size}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        item {
            Text(
                text = "Enrolled Courses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        if (courses.isEmpty()) {
            item {
                Text("No enrolled courses yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            items(courses) { course ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, fontWeight = FontWeight.Bold)
                            Text("${(course.progress * 100).toInt()}% completed", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { showDeleteConfirmDialog = course }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Quiz Score Record List",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

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
                        Text(record.courseTitle, fontWeight = FontWeight.Bold)
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
