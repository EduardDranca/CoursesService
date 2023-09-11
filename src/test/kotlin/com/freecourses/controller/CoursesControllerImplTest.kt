package com.freecourses.controller

import com.freecourses.BaseIntegrationTest
import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import java.util.*


class CoursesControllerImplTest: BaseIntegrationTest() {
    @Autowired
    lateinit var ddbTable: DynamoDbTable<CourseDO>

    @BeforeEach
    fun setUp() {
        clearItems()
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
        createCourse(courseId)
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/courses/{id}", courseId))
            .andExpect(status().isOk)
            .andReturn()
        println(result.response.contentAsString)
    }

    private fun createCourse(id: UUID) {
        val course = CourseDO(id)
        ddbTable.putItem(course)
    }

    private fun clearItems() {
        ddbTable.scan().items().forEach {
            ddbTable.deleteItem(it)
        }
    }
}