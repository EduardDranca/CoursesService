package com.freecourses.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.freecourses.BaseIntegrationTest
import com.freecourses.model.Course
import com.freecourses.model.ListCoursesResponse
import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import java.util.stream.IntStream
import kotlin.streams.toList
import kotlin.test.assertEquals


class CoursesControllerTest: BaseIntegrationTest() {
    companion object {
        private val OBJECT_MAPPER = ObjectMapper()
    }

    @BeforeEach
    fun setUp() {
        clearItems()
    }

    @Test
    fun Given_InvalidUUID_When_GetCourse_Then_ReturnBadRequest() {
        val result = performGetCourseAndAssertStatus("invalid", status().isBadRequest)
        JSONAssert.assertEquals("{\"message\": \"courseId can not be initialized using value: invalid\"}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_NoCourse_When_GetCourse_Then_ReturnNotFoundError() {
        val courseId = UUID.randomUUID()
        val result = performGetCourseAndAssertStatus(courseId.toString(), status().isNotFound)
        JSONAssert.assertEquals("{\"message\": \"The course with id: <$courseId> could not be found.\", \"courseId\": \"$courseId\"}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_Course_When_GetCourse_Then_ReturnOk() {
        val courseId = UUID.randomUUID()
        createCourse(CourseDO(id = courseId))
        val result = performGetCourseAndAssertStatus(courseId.toString(), status().isOk)
        JSONAssert.assertEquals("{\"id\":\"${courseId}\",\"description\":null,\"uri\":null,\"difficulty\":null,\"category\":null,\"subcategories\":[],\"source\":null}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_NoCourses_When_GetCourses_Then_ReturnEmptyListResponse() {
        val result = performListCoursesRequestWithPage(null)
        assertCoursesListsEqual(emptyList(), result.courses)
        assertNull(result.nextPageToken, "The nextPageToken should be null if there are no more pages")
    }

    @Test
    fun Given_SingePageOfCourses_When_GetCourses_Then_ReturnSinglePage() {
        val courses = IntStream.range(0, 10)
            .mapToObj { CourseDO(id = UUID.randomUUID(), category = "Programming") }
            .toList()
        courses.forEach { createCourse(it) }
        val result = performListCoursesRequestWithPage(null)

        val secondResult = performListCoursesRequestWithPage(result.nextPageToken)

        assertCoursesListsEqual(courses, result.courses)
        assertCoursesListsEqual(emptyList(), secondResult.courses)
        assertNull(secondResult.nextPageToken, "The nextPageToken should be null if there are no more pages")
    }

    @Test
    fun Given_MultiplePagesOfCourses_When_GetCourses_Then_ReturnAllPages() {
        val courses = IntStream.range(0, 20)
            .mapToObj { CourseDO(id = UUID.randomUUID(), category = "Programming") }
            .toList()
        courses.forEach { createCourse(it) }
        val result = performListCoursesRequestWithPage(null)

        val secondResult = performListCoursesRequestWithPage(result.nextPageToken)

        val thirdResult = performListCoursesRequestWithPage(secondResult.nextPageToken)

        val allReturnedCourses = result.courses + secondResult.courses
        assertCoursesListsEqual(emptyList(), thirdResult.courses)
        assertCoursesListsEqual(courses, allReturnedCourses)
        assertNull(thirdResult.nextPageToken, "The nextPageToken should be null if there are no more pages")
    }

    @Test
    fun Given_DifferentCategoriesCourses_When_GetCoursesByCategory_Then_ReturnCoursesMatchingCategory() {
        val programmingCourses = IntStream.range(0, 10)
            .mapToObj { CourseDO(id = UUID.randomUUID(), category = "Programming") }
            .toList()
        val cookingCourses = IntStream.range(0, 10)
            .mapToObj { CourseDO(id = UUID.randomUUID(), category = "Cooking") }
            .toList()
        programmingCourses.forEach { createCourse(it) }
        cookingCourses.forEach { createCourse(it) }

        val programmingResult = performListCoursesRequest("Programming")
        val cookingResult = performListCoursesRequest("Cooking")
        val secondProgrammingResult = performListCoursesRequest("Programming", programmingResult.nextPageToken)
        val secondCookingResult = performListCoursesRequest("Cooking", cookingResult.nextPageToken)

        assertCoursesListsEqual(programmingCourses, programmingResult.courses)
        assertCoursesListsEqual(cookingCourses, cookingResult.courses)

        assertNull(secondProgrammingResult.nextPageToken, "The nextPageToken should be null if there are no more pages")
        assertNull(secondCookingResult.nextPageToken, "The nextPageToken should be null if there are no more pages")

        assertCoursesListsEqual(emptyList(), secondProgrammingResult.courses)
        assertCoursesListsEqual(emptyList(), secondCookingResult.courses)
    }

    fun assertCoursesListsEqual(expected: List<CourseDO>, actual: List<Course>) {
        assertEquals(expected.size, actual.size)
        val expectedSorted = expected.sortedWith(compareBy { it.id })
        val actualSorted = actual.sortedWith(compareBy { it.id })
        expectedSorted.forEachIndexed { index, course ->
            assertCoursesEqual(course, actualSorted[index])
        }
    }

    private fun createCourse(course: CourseDO) {
        ddbTable.putItem(course)
    }

    private fun clearItems() {
        ddbTable.scan().items().forEach {
            ddbTable.deleteItem(it)
        }
    }

    private fun performGetCourseAndAssertStatus(courseId: String, status: ResultMatcher): String {
        return mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", courseId))
            .andExpect(status)
            .andReturn()
            .response
            .contentAsString
    }

    private fun performListCoursesRequest(category: String): ListCoursesResponse {
        return performListCoursesRequest(category, null)
    }

    private fun performListCoursesRequestWithPage(nextPageToken: String?): ListCoursesResponse {
        return performListCoursesRequest("Programming", nextPageToken)
    }

    private fun performListCoursesRequest(category: String, nextPageToken: String?): ListCoursesResponse {
        return OBJECT_MAPPER.readValue(
            mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses")
                .param("category", category)
                .param("maxCourses", "10")
                .param("nextPageToken", nextPageToken))
                .andExpect(status().isOk)
                .andReturn()
                .response
                .contentAsString, ListCoursesResponse::class.java)
    }

    private fun assertCoursesEqual(expected: CourseDO, actual: Course) {
        org.junit.jupiter.api.Assertions.assertEquals(expected.id, actual.id)
        org.junit.jupiter.api.Assertions.assertEquals(expected.source?.value, actual.source)
        org.junit.jupiter.api.Assertions.assertEquals(expected.category, actual.category)
        org.junit.jupiter.api.Assertions.assertEquals(expected.uri, actual.uri)
        org.junit.jupiter.api.Assertions.assertEquals(expected.difficulty, actual.difficulty)
        org.junit.jupiter.api.Assertions.assertEquals(expected.description, actual.description)
        org.junit.jupiter.api.Assertions.assertEquals(expected.subcategories, actual.subcategories)
    }
}