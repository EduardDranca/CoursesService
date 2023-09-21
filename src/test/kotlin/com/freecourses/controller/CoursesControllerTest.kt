package com.freecourses.controller

import com.freecourses.BaseIntegrationTest
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


class CoursesControllerTest: BaseIntegrationTest() {
    @Autowired
    lateinit var ddbTable: DynamoDbTable<CourseDO>

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
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound)
            .andReturn()
        println(result.response.contentAsString)
    }

    @Test
    fun Given_Course_When_GetCourse_Then_ReturnOk() {
        val courseId = UUID.randomUUID()
        createCourse(CourseDO(id = courseId))
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", courseId))
            .andExpect(status().isOk)
            .andReturn()
        println(result.response.contentAsString)
    }

    private fun createCourse(course: CourseDO) {
        ddbTable.putItem(course)
    }

    private fun clearItems() {
        ddbTable.scan().items().forEach {
            ddbTable.deleteItem(it)
        }
    }
}