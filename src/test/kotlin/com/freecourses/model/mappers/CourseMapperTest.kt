package com.freecourses.model.mappers

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CourseSource
import com.freecourses.model.CreateCourseRequest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.URI

class CourseMapperTest {
    @Test
    fun Given_CreateCourseRequest_When_MappingToCourseDO_Then_ExpectCorrectMapping() {
        val ccr = CreateCourseRequest()
            .source(CourseSource.COURSERA)
            .category("Programming")
            .uri(URI.create("https://www.google.com"))
            .difficulty(CourseDifficulty.BEGINNER)
            .description("description")
            .subcategories(listOf("Python", "ML"))
        val courseDO = CourseMapper.INSTANCE.toCourseDO(ccr)
        Assertions.assertEquals(ccr.source, courseDO.source)
        Assertions.assertEquals(ccr.category, courseDO.category)
        Assertions.assertEquals(ccr.uri, courseDO.uri)
        Assertions.assertEquals(ccr.difficulty, courseDO.difficulty)
        Assertions.assertEquals(ccr.description, courseDO.description)
        Assertions.assertEquals(ccr.subcategories, courseDO.subcategories)
    }

//    @Test
//    fun Given_LastEvaluatedKey_When_MappingToNextPageToken_Then_ExpectCorrectMapping() {
//        val attrValMap: Map<String, AttributeValue>  = mapOf(
//            Pair("abc", AttributeValue.fromS("xyz")),
//            Pair("xyz", AttributeValue.fromN("123")))
//        val byteArray = CourseMapper.INSTANCE.toNextPageToken(attrValMap)
//    }
}