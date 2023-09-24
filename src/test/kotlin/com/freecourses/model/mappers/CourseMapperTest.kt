package com.freecourses.model.mappers

import com.freecourses.model.Course
import com.freecourses.model.CourseDifficulty
import com.freecourses.model.CourseSource
import com.freecourses.model.CreateCourseRequest
import com.freecourses.persistence.model.CourseDO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.model.Page
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
        assertNull(course, "Mapping a null CourseDO to a Course should return null")
    }

    @Test
    fun Given_CourseDO_When_MappingToCourse_Then_ExpectCorrectMapping() {

        val course = unit.toCourse(COURSE_DO)
        assertEquals(COURSE_DO.source?.value, course.source)
        assertEquals(COURSE_DO.category, course.category)
        assertEquals(COURSE_DO.uri, course.uri)
        assertEquals(COURSE_DO.difficulty, course.difficulty)
        assertEquals(COURSE_DO.description, course.description)
        assertEquals(COURSE_DO.subcategories, course.subcategories)
        assertEquals(COURSE_DO.id, course.id)
    }

    @Test
    fun Given_NullCreateCourseRequest_When_MappingToCourseDO_Then_ExpectNull() {
        val courseDO = unit.toCourseDO(null)
        assertNull(courseDO, "Mapping a null CreateCourseRequest to a CourseDO should return null")
    }

    @Test
    fun Given_CreateCourseRequest_When_MappingToCourseDO_Then_ExpectCorrectMapping() {
        val ccr = CreateCourseRequest(
            "description",
            URI.create("https://www.google.com"),
            CourseDifficulty.BEGINNER,
            "Programming",
            listOf("Python", "ML"),
            CourseSource.COURSERA.value)
        val courseDO = unit.toCourseDO(ccr)
        assertEquals(ccr.source, courseDO.source?.value)
        assertEquals(ccr.category, courseDO.category)
        assertEquals(ccr.uri, courseDO.uri)
        assertEquals(ccr.difficulty, courseDO.difficulty)
        assertEquals(ccr.description, courseDO.description)
        assertEquals(ccr.subcategories, courseDO.subcategories)
        
        assertEquals("${courseDO.difficulty}#${courseDO.id}", courseDO.csGsiSk)
    }

    @Test
    fun Given_CourseDoList_When_MappingToCourseList_Then_ExpectCorrectMapping() {
        val courseDOList = listOf(COURSE_DO)
        val courseList = unit.toCourseList(courseDOList)
        assertEquals(1, courseList.size)
        val course = courseList[0]
        assertCoursesEqual(COURSE_DO, course)
    }

    @Test
    fun Given_NullCourseDoPage_When_MappingToListCoursesResponse_Then_ExpectNull() {
        val courseList = unit.toListCoursesResponse(null)
        assertNull(courseList, "Mapping a null Page<CourseDO> to a ListCoursesResponse should return null")
    }

    @Test
    fun Given_NullLastEvaluatedKeyInPage_When_MappingToListCoursesResponse_Then_ExpectNullNextPageToken() {
        val courseDOList = listOf(COURSE_DO)
        val courseListResponse = unit.toListCoursesResponse(Page.create(courseDOList))
        assertEquals(null, courseListResponse.nextPageToken)
        assertEquals(1, courseListResponse.courses.size)
        val course = courseListResponse.courses[0]
        assertCoursesEqual(COURSE_DO, course)
    }

    fun assertCoursesEqual(expected: CourseDO, actual: Course) {
        assertEquals(expected.id, actual.id)
        assertEquals(expected.source?.value, actual.source)
        assertEquals(expected.category, actual.category)
        assertEquals(expected.uri, actual.uri)
        assertEquals(expected.difficulty, actual.difficulty)
        assertEquals(expected.description, actual.description)
        assertEquals(expected.subcategories, actual.subcategories)
    }
}