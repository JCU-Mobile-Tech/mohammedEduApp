package com.example.mohdeduapp

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "eduquiz_database_preferences")

class DataStoreManager(private val context: Context) {
    companion object {
        val PLAYERS_KEY = stringPreferencesKey("registered_players_db")
        val SCORES_KEY = stringPreferencesKey("quiz_scores_db")
    }

    val playersFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val playersString = preferences[PLAYERS_KEY] ?: "Player 1,Alex,Mohammed,Sarah,David"
        playersString.split(",").filter { it.isNotBlank() }
    }

    suspend fun savePlayers(players: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PLAYERS_KEY] = players.joinToString(",")
        }
    }

    val scoresFlow: Flow<List<QuizScoreRecord>> = context.dataStore.data.map { preferences ->
        val scoresString = preferences[SCORES_KEY] ?: ""
        if (scoresString.isBlank()) {
            listOf(
                QuizScoreRecord("Player 1", "Jetpack Compose Quiz", 5, 5, "Today"),
                QuizScoreRecord("Alex", "Kotlin Coroutines & Flow", 4, 5, "Yesterday"),
                QuizScoreRecord("Mohammed", "MVVM & Clean Architecture", 5, 5, "2 days ago")
            )
        } else {
            scoresString.split(";").mapNotNull { recordStr ->
                val parts = recordStr.split("|")
                if (parts.size == 5) {
                    QuizScoreRecord(parts[0], parts[1], parts[2].toIntOrNull() ?: 0, parts[3].toIntOrNull() ?: 5, parts[4])
                } else null
            }
        }
    }

    suspend fun saveScores(scores: List<QuizScoreRecord>) {
        context.dataStore.edit { preferences ->
            preferences[SCORES_KEY] = scores.joinToString(";") { "${it.playerName}|${it.topicTitle}|${it.score}|${it.total}|${it.date}" }
        }
    }
}
