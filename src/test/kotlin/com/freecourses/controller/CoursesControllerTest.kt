package com.freecourses.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.freecourses.BaseIntegrationTest
import com.freecourses.model.Course
import com.freecourses.model.ListCoursesResponse
import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import java.util.*
import java.util.stream.IntStream
import kotlin.streams.toList
import kotlin.test.assertEquals


class CoursesControllerTest: BaseIntegrationTest() {
    @Autowired
    lateinit var ddbTable: DynamoDbTable<CourseDO>
    val OBJECT_MAPPER = ObjectMapper()

    @BeforeEach
    fun setUp() {
        clearItems()
    }

    @Test
    fun Given_InvalidUUID_When_GetCourse_Then_ReturnBadRequest() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", "invalid"))
            .andExpect(status().isBadRequest)
            .andReturn()
            .response
            .contentAsString
        JSONAssert.assertEquals("{\"message\": \"Invalid UUID string: invalid\"}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_NoCourse_When_GetCourse_Then_ReturnNotFoundError() {
        val courseId = UUID.randomUUID()
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", courseId))
            .andExpect(status().isNotFound)
            .andReturn()
            .response
            .contentAsString
        JSONAssert.assertEquals("{\"message\": \"The course with id: <$courseId> could not be found.\", \"courseId\": \"$courseId\"}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_Course_When_GetCourse_Then_ReturnOk() {
        val courseId = UUID.randomUUID()
        createCourse(CourseDO(id = courseId))
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", courseId))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        JSONAssert.assertEquals("{\"id\":\"${courseId}\",\"description\":null,\"uri\":null,\"difficulty\":null,\"category\":null,\"subcategories\":[],\"source\":null}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_NoCourses_When_GetCourses_Then_ReturnEmptyListResponse() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses")
            .param("category", "Programming"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString
        JSONAssert.assertEquals("{\"nextPageToken\":null,\"courses\":[]}", result, JSONCompareMode.LENIENT)
    }

    @Test
    fun Given_SingePageOfCourses_When_GetCourses_Then_ReturnSinglePage() {
        val courses = IntStream.range(0, 10)
            .mapToObj { CourseDO(id = UUID.randomUUID(), category = "Programming") }
            .toList()
        courses.forEach { createCourse(it) }
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses")
            .param("category", "Programming")
            .param("maxCourses", "10"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val response = OBJECT_MAPPER.readValue(result, ListCoursesResponse::class.java)
        assertCoursesListsEqual(courses, response.courses)

        val secondResult = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses")
            .param("category", "Programming")
            .param("maxCourses", "10")
            .param("nextPageToken", response.nextPageToken))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        val secondResponse = OBJECT_MAPPER.readValue(secondResult, ListCoursesResponse::class.java)
        assertCoursesListsEqual(emptyList(), secondResponse.courses)
    }

    @Test
    fun Given_MultiplePagesOfCourses_When_GetCourses_Then_ReturnAllPages() {

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