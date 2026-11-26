package com.example.mohdeduapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizScoreDatabaseTest {

    @Test
    fun testQuizScoreRecordCreation() {
        val record = QuizScoreRecord(
            playerName = "Mohammed",
            topicTitle = "Jetpack Compose Quiz",
            score = 5,
            total = 5,
            date = "Today",
        )

        assertEquals("Mohammed", record.playerName)
        assertEquals("Jetpack Compose Quiz", record.topicTitle)
        assertEquals(5, record.score)
        assertEquals(5, record.total)
    }

    @Test
    fun testScoreHistoryListManagement() {
        val scoreList = mutableListOf<QuizScoreRecord>()

        // Simulate adding records for different players
        scoreList.add(QuizScoreRecord("Player 1", "Jetpack Compose Quiz", 4, 5, "Today"))
        scoreList.add(QuizScoreRecord("Alex", "Kotlin Coroutines & Flow", 5, 5, "Today"))

        assertEquals(2, scoreList.size)
        assertEquals("Player 1", scoreList[0].playerName)
        assertEquals("Alex", scoreList[1].playerName)

        // Test filtering by player
        val player1Scores = scoreList.filter { it.playerName == "Player 1" }
        assertEquals(1, player1Scores.size)
        assertEquals(4, player1Scores[0].score)

        // Test clearing history
        scoreList.clear()
        assertTrue(scoreList.isEmpty())
    }
}
