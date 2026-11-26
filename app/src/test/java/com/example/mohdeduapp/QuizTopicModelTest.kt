package com.example.mohdeduapp

import org.junit.Assert.assertEquals
import org.junit.Test

class QuizTopicModelTest {

    @Test
    fun testQuizTopicCreation() {
        val topic = QuizTopic(
            id = 1,
            title = "Jetpack Compose Quiz",
            category = "Mobile",
            description = "Test Description",
            questionCount = 10,
        )

        assertEquals(1, topic.id)
        assertEquals("Jetpack Compose Quiz", topic.title)
        assertEquals("Mobile", topic.category)
        assertEquals(10, topic.questionCount)
    }

    @Test
    fun testQuizFilteringLogic() {
        val topics = listOf(
            QuizTopic(1, "Compose", "Mobile", "UI", 10),
            QuizTopic(2, "DSA", "Algorithms", "Logic", 10),
        )

        val mobileTopics = topics.filter { it.category == "Mobile" }
        assertEquals(1, mobileTopics.size)
        assertEquals("Compose", mobileTopics[0].title)
    }
}
