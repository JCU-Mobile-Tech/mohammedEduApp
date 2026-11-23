package com.example.mohdeduapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseModelTest {

    @Test
    fun testCourseCreationAndEnrollment() {
        val course = Course(
            id = 1,
            title = "Test Course",
            category = "Mobile",
            progress = 0.5f,
            duration = "2 hours",
            lessonsCount = 10,
            description = "Test Description",
            isEnrolled = false,
        )

        assertEquals(1, course.id)
        assertEquals("Test Course", course.title)
        assertEquals(0.5f, course.progress, 0.01f)
        assertEquals(false, course.isEnrolled)

        val enrolledCourse = course.copy(isEnrolled = true)
        assertTrue(enrolledCourse.isEnrolled)
    }

    @Test
    fun testCourseFilteringLogic() {
        val courses = listOf(
            Course(1, "Compose", "Mobile", 0.5f, "2h", 10, "UI", true),
            Course(2, "DSA", "Algorithms", 0.2f, "5h", 20, "Logic", false),
        )

        val mobileCourses = courses.filter { it.category == "Mobile" }
        assertEquals(1, mobileCourses.size)
        assertEquals("Compose", mobileCourses[0].title)
    }
}
