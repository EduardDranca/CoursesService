package com.freecourses.model.mappers

import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CourseSource
import com.freecourses.model.CreateCourseRequest
import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

class CourseMapperTest {
    val unit = CourseMapper.INSTANCE

    companion object {
        private val COURSE_DO = CourseDO(
            UUID.randomUUID(),
            "Programming",
            "Programming",
            "description",
            URI("https://www.google.com"),
            listOf("Python", "ML"),
            CourseSource.COURSERA,
            CourseDifficulty.BEGINNER)
    }

    @Test
    fun Given_NullCourseDO_When_MappingToCourse_Then_ExpectNull() {
        val course = unit.toCourse(null)
        assertEquals(null, course)
    }

    @Test
    fun Given_CourseDO_When_MappingToCourse_Then_ExpectCorrectMapping() {

        val course = unit.toCourse(COURSE_DO)
        assertEquals(COURSE_DO.source?.value, course.source)
        assertEquals(COURSE_DO.category, course.category)
        assertEquals(COURSE_DO.uri, course.uri)
        assertEquals(COURSE_DO.difficulty?.value, course.difficulty)
        assertEquals(COURSE_DO.description, course.description)
        assertEquals(COURSE_DO.subcategories, course.subcategories)
        assertEquals(COURSE_DO.id, course.id)
    }

    @Test
    fun Given_NullCreateCourseRequest_When_MappingToCourseDO_Then_ExpectNull() {
        val courseDO = unit.toCourseDO(null)
        assertEquals(null, courseDO)
    }

    @Test
    fun Given_CreateCourseRequest_When_MappingToCourseDO_Then_ExpectCorrectMapping() {
        val ccr = CreateCourseRequest(
            "description",
            URI.create("https://www.google.com"),
            CourseDifficulty.BEGINNER.value,
            "Programming",
            listOf("Python", "ML"),
            CourseSource.COURSERA.value)
        val courseDO = unit.toCourseDO(ccr)
        assertEquals(ccr.source, courseDO.source?.value)
        assertEquals(ccr.category, courseDO.category)
        assertEquals(ccr.uri, courseDO.uri)
        assertEquals(ccr.difficulty, courseDO.difficulty?.value)
        assertEquals(ccr.description, courseDO.description)
        assertEquals(ccr.subcategories, courseDO.subcategories)
        
        assertEquals("${courseDO.difficulty}#${courseDO.id}", courseDO.csGsiSk)
    }

    @Test
    fun Given_NullCourse_When_MappingToCourseDO_Then_ExpectNull() {
        val courseDO = unit.toCourseDO(null)
        assertEquals(null, courseDO)
    }

    @Test
    fun Given_CourseDoList_When_MappingToCourseList_Then_ExpectCorrectMapping() {
        val courseDOList = listOf(COURSE_DO)
        val courseList = unit.toCourseList(courseDOList)
        assertEquals(1, courseList.size)
        val course = courseList[0]
        assertEquals(COURSE_DO.id, course.id)
        assertEquals(COURSE_DO.source?.value, course.source)
        assertEquals(COURSE_DO.category, course.category)
        assertEquals(COURSE_DO.uri, course.uri)
        assertEquals(COURSE_DO.difficulty?.value, course.difficulty)
        assertEquals(COURSE_DO.description, course.description)
        assertEquals(COURSE_DO.subcategories, course.subcategories)
    }
}